package persianswitch.com.mqtt.mqttClient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observables.ConnectableObservable;

public class mqttClient {
    public static final String CONTROL_TOPIC = "Ͼ";
    public static final int QOS_CONTROL = -1;
    public static final int QOS_0 = 0;
    public static final int QOS_1 = 1;
    public static final int QOS_2 = 2;

    private static final String TAG = "mqttClient";
    private static mqttClient instance;
    private ConnectableObservable<mqttMessage> mqttConnectable;
    private Observable<mqttMessage> mqttObservable;
    private MqttAndroidClient mqttAndroidClient;
    private mqttClientOptions mMqttClientOptions;
    private ObservableEmitter<mqttMessage> emitter;
    private ArrayList<mqttTopic> topics;

    public Observable<mqttMessage> getMqttObservable() {
        return mqttObservable;
    }

    public ConnectableObservable<mqttMessage> getMqttConnectable() {
        return mqttConnectable;
    }

    /**
     * Listener used to dispatch connection events.
     */
    public OnConnectionChangeListener mOnConnectListener;


    public static mqttClient getInstance() {
        if (instance == null) {
            synchronized (mqttClient.class) {
                if (instance == null) {
                    throw new NullPointerException(
                            "Class must be initialized once by calling init function!");
                }
            }
        }
        return instance;
    }

    public static mqttClient init(final mqttClientOptions mqttClientOptions) {
        instance = new mqttClient(mqttClientOptions);
        return instance;
    }


    private mqttClient(final mqttClientOptions mqttClientOptions) {
        /**
         * Initializing: mqttObservable
         */
        mqttObservable = Observable.create(new ObservableOnSubscribe<mqttMessage>() {
            @Override
            public void subscribe(final ObservableEmitter<mqttMessage> observableEmitter) {
                emitter = observableEmitter;
                mqttAndroidClientSetup(mqttClientOptions, emitter);

            }
        });
        mqttConnectable = mqttObservable.publish();
    }

    private void mqttAndroidClientSetup(mqttClientOptions options, final ObservableEmitter<mqttMessage> em) {
        /**
         * Set mMqttClientOptions variable
         */
        this.mMqttClientOptions = options;

        /**
         * Initializing: mqttAndroidClient
         */
        mqttAndroidClient = new MqttAndroidClient(options.getContext(),
                options.getServerUri(), options.getClientId());

        /**
         * Set DefaultTopics variable if mqttClientOptions has some!
         */
        if (options.getDefaultTopics() != null) {
            this.topics = options.getDefaultTopics();
        }

        connect(options, em);

    }


    private void connect(mqttClientOptions options, ObservableEmitter<mqttMessage> em) {
        mqttAndroidClient.setCallback(getConnectMqttCallbackExtended(options, em));
        Log.i(TAG, "Connecting to " + options.getServerUri());
        em.onNext(new mqttMessage(QOS_CONTROL, "Connecting to " +
                options.getServerUri(), CONTROL_TOPIC, QOS_CONTROL));

        try {
            mqttAndroidClient.connect(options.getMqttConnectOptions(),
                    null, getConnectIMqttActionListenerCallback(options, em));

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mqttAndroidClient.disconnect(0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.close();
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onConnectionChane(false);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onConnectionChane(true);
                    }
                    Log.e(TAG, "onFailure: " + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect(final mqttClientOptions options) {
        if (mqttAndroidClient.isConnected()) {
            disconnect();
        } else {
            mqttAndroidClientSetup(options, emitter);
        }
    }


    /**
     * Register a callback to be invoked when this Disconnected.
     */
    public void setmOnConnectListener(@Nullable OnConnectionChangeListener l) {
        mOnConnectListener = l;
    }

    @NonNull
    private IMqttActionListener getConnectIMqttActionListenerCallback(final mqttClientOptions options, final ObservableEmitter<mqttMessage> em) {
        return new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                if (mOnConnectListener != null) {
                    mOnConnectListener.onConnectionChane(true);
                }

                /**
                 * Subscribe Topics
                 */
                Log.e(TAG, "topics.size(): " + topics.size());
                if (topics != null) {
                    for (mqttTopic t : topics) {
                        subToTopic(t, em);
                    }
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                if (mOnConnectListener != null) {
                    mOnConnectListener.onConnectionChane(false);
                }
                Log.i(TAG, "Failed to connect to: " + options.getServerUri());
                em.onNext(new mqttMessage(QOS_CONTROL,
                        "Failed to connect to: " + options.getServerUri(), CONTROL_TOPIC, QOS_CONTROL));

                if (exception != null) {
                    Log.i(TAG, exception.toString());
                    em.onNext(new mqttMessage(QOS_CONTROL,
                            exception.toString(), CONTROL_TOPIC, QOS_CONTROL));
                }
            }
        };
    }

    @NonNull
    private MqttCallbackExtended getConnectMqttCallbackExtended(final mqttClientOptions options, final ObservableEmitter<mqttMessage> em) {
        return new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.i(TAG, "Reconnected to : " + serverURI);
                    em.onNext(new mqttMessage(QOS_CONTROL, "Reconnected to : " + serverURI, CONTROL_TOPIC, QOS_CONTROL));
                    if (options.getMqttConnectOptions().isCleanSession() && (topics != null)) {
                        for (mqttTopic t : topics) {
                            subToTopic(t, em);
                        }
                    }
                } else {
                    Log.i(TAG, "Connected to " + serverURI);
                    em.onNext(new mqttMessage(QOS_CONTROL, "Connected to " + serverURI, CONTROL_TOPIC, QOS_CONTROL));
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                String causeStr = "";
                if (cause != null) {
                    causeStr = cause.getMessage();
                }
                Log.i(TAG, "The Connection was lost. " + causeStr);
                em.onNext(new mqttMessage(QOS_CONTROL, "The Connection was lost.", CONTROL_TOPIC, QOS_CONTROL));
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, "Incoming message: " + new String(message.getPayload()));
                em.onNext(new mqttMessage(QOS_CONTROL, "Incoming message: " +
                        new String(message.getPayload()), CONTROL_TOPIC, QOS_CONTROL));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i(TAG, "Delivery Completed: " + token.toString());
                em.onNext(new mqttMessage(QOS_CONTROL, "Delivery Completed.", CONTROL_TOPIC, QOS_CONTROL));
            }
        };
    }


    private void subToTopic(final mqttTopic topic, final ObservableEmitter<mqttMessage> em) {
        try {
            mqttAndroidClient.subscribe(topic.getName(), topic.getQos(), null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "Subscribed successfully; Topic: " + topic.getName() + " Qos: " + topic.getQos());
                    em.onNext(new mqttMessage(QOS_CONTROL,
                            "Subscribed: Topic=" + topic.getName() + " Qos=" + topic.getQos(), CONTROL_TOPIC, QOS_CONTROL));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "Failed to subscribe: Topic=" + topic.getName() + " Qos=" + topic.getQos());
                    em.onNext(new mqttMessage(QOS_CONTROL,
                            "Failed to subscribe: Topic=" + topic.getName() + " Qos=" + topic.getQos(), CONTROL_TOPIC, QOS_CONTROL));
                }
            });

            mqttAndroidClient.subscribe(topic.getName(), topic.getQos(), new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    Log.i(TAG, "Message: " + topic + " : " + new String(message.getPayload()));
                    em.onNext(new mqttMessage(message.getId(), new String(message.getPayload()), topic, message.getQos()));
                }
            });

        } catch (MqttException ex) {
            Log.e(TAG, "Exception whilst subscribing: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public void publishMessage(String topicName, String message, int Qos) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload((mqttAndroidClient.getClientId() + ": " + message).getBytes());
            mqttMessage.setQos(Qos);
            mqttAndroidClient.publish(topicName, mqttMessage);
            Log.i(TAG, "Message Published");
            if (!mqttAndroidClient.isConnected()) {
                Log.i(TAG, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            Log.i(TAG, "Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(final mqttTopic topic, final ObservableEmitter<mqttMessage> em) {
        topics.add(topic);
        subToTopic(topic, em);
    }

    public ArrayList<mqttTopic> getTopics() {
        return topics;
    }

    public Boolean isConnected() {
        return mqttAndroidClient.isConnected();
    }

    public void unbind() {
        mqttAndroidClient.unregisterResources();
        mqttAndroidClient.close();
    }
}


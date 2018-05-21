package persianswitch.com.mqtt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import persianswitch.com.mqtt.mqttClient.mqttClient;
import persianswitch.com.mqtt.mqttClient.mqttMessage;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatFragment extends Fragment {
    private static final String ARG_LOG_LEVEL = "log_level";
    public static final int LOG_LEVEL_CONTROL_MESSAGES = 0;
    public static final int LOG_LEVEL_MESSAGES = 1;
    public static final String CHAT_TOPIC = "ChatTopic";
    private static final String TAG = "mqttClient";
    private View rootView;
    private RecyclerView mRecyclerView;
    private LogAdapter mLogAdapter;

    public ChatFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        final EditText etChat = rootView.findViewById(R.id.etChat);

        Button btnSend = rootView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttClient.getInstance().publishMessage(CHAT_TOPIC, etChat.getText().toString(), mqttClient.QOS_2);
                etChat.setText("");
            }
        });

        mLogAdapter = new LogAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mLogAdapter);

        ConnectableObservable<mqttMessage> connectable = mqttClient.getInstance().getMqttConnectable();
        connectable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<mqttMessage>() {
                    @Override
                    public boolean test(mqttMessage mqttMessage) throws Exception {
                        return mqttMessage.getQos() == mqttClient.QOS_2;
                    }
                })
                .subscribe(new Observer<mqttMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(mqttMessage mqttMessage) {
                        addToLog(mqttMessage);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        connectable.connect();

        return rootView;
    }


    private void addToLog(mqttMessage mqttMessage) {
        String log = "";

        if (mqttMessage.getMessageId() > -1) {
            log += " MsgID: " + mqttMessage.getMessageId();
        }

        log = mqttMessage.getTopic() + ": " + mqttMessage.getMessage();
        if (mqttMessage.getQos() > -1) {
            log += " Qos: " + mqttMessage.getQos();
        }
        mLogAdapter.add(log);
        mRecyclerView.smoothScrollToPosition(-10);

    }


}
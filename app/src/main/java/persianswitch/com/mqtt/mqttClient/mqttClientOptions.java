package persianswitch.com.mqtt.mqttClient;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.ArrayList;

import io.reactivex.annotations.Nullable;
import persianswitch.com.mqtt.SettingFragment;

public class mqttClientOptions {

    private Context context;
    private String serverUri;
    private String userName;
    private String password;
    private String clientId;
    private ArrayList<mqttTopic> defaultTopics;

    private MqttConnectOptions mqttConnectOptions;

    public mqttClientOptions(Context context, String serverUri, String userName, String password, String clientId) {
        this(context, serverUri, userName, password, clientId, null);
    }

    public mqttClientOptions(Context context, String serverUri, String userName, String password, String clientId, ArrayList<mqttTopic> defaultTopics) {
        this.context = context;
        this.serverUri = serverUri;
        this.userName = userName;
        this.password = password;
        this.clientId = clientId + "_" + System.currentTimeMillis() % 1000;
        this.defaultTopics = defaultTopics;
        this.mqttConnectOptions = new MqttConnectOptions();
        this.mqttConnectOptions.setUserName(userName);
        this.mqttConnectOptions.setPassword(password.toCharArray());
        this.mqttConnectOptions.setAutomaticReconnect(true);
        this.mqttConnectOptions.setCleanSession(false);
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        return mqttConnectOptions;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<mqttTopic> getDefaultTopics() {
        return defaultTopics;
    }

    public void setDefaultTopics(ArrayList<mqttTopic> defaultTopics) {
        this.defaultTopics = defaultTopics;
    }
}

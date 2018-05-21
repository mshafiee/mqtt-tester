package persianswitch.com.mqtt.mqttClient;

public interface OnConnectionChangeListener {
    /**
     * Called when a Client Disconnects.
     */
    void onConnectionChane(boolean status);
}
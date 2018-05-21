package persianswitch.com.mqtt.mqttClient;

public class mqttMessage {
    private int messageId;
    private String message;
    private String topic;
    private int qos;

    public mqttMessage(int messageId, String message, String topic, int qos) {
        this.messageId = messageId;
        this.message = message;
        this.topic = topic;
        this.qos = qos;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }


}

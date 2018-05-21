package persianswitch.com.mqtt.mqttClient;

public class mqttTopic {
    private String Name;
    private int qos;

    public mqttTopic(String name, int qos) {
        Name = name;
        this.qos = qos;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
}

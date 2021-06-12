package mqtt.publisher;

import org.eclipse.paho.client.mqttv3.*;

import mqtt.utils.Utils;
import solutions.RFIDdata;


public class PhidgetPublisher {

	static RFIDdata oneTag = new RFIDdata();
    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static final String userid = "hotelserenity"; 
    public static final String TOPIC_HOTEL_SERENITY  = userid + "/" + oneTag.getDoorid();
    public static final String TOPIC_ANDROID  = userid + "/android/" + oneTag.getDoorid();
    private MqttClient client;

    public PhidgetPublisher() {

        try {
            client = new MqttClient(BROKER_URL, userid + "-" + Utils.createRandomNumberBetween(1, 1000));
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setWill(client.getTopic(userid + "/LWT"), "I'm gone :(".getBytes(), 0, false);
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void publishHotelSerenity(String string) throws MqttException {
        final MqttTopic rfidTopic = client.getTopic("hotelserenity/"+string);
        final String rfid = string + "";
        rfidTopic.publish(new MqttMessage(rfid.getBytes()));
        System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfid);  
    }
    
    public void publishAndroid(String string) throws MqttException {
        final MqttTopic rfidTopic = client.getTopic("hotelserenity/android");
        final String rfid = string + "";
        rfidTopic.publish(new MqttMessage(rfid.getBytes()));
        System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfid);  
    }
    
}

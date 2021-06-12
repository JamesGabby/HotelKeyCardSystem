package mqtt.subscriber;

import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.Gson;

import mqtt.publisher.PhidgetPublisher;
import solutions.RFIDdata;

public class AndroidSubscriberCallBack implements MqttCallback {

    public static final String userid = "hotelserenity"; 
    PhidgetPublisher publisher = new PhidgetPublisher();
    RFIDdata oneTag = new RFIDdata();
    Gson gson = new Gson();
    String oneTagJson = new String();
    String clientId = userid + "-sub";
    
    @Override
    public void connectionLost(Throwable cause) {}

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message arrived. Topic: " + topic + "  Message: " + message.toString());
        
        oneTag.setReaderid(Integer.parseInt(message.toString()));
        System.out.println("DEBUG READER ANDROID: " + oneTag.getReaderid());
        oneTagJson = gson.toJson(oneTag);
        String resultString = AndroidSubscriber.androidDoorLookup(oneTagJson);
        oneTag = gson.fromJson(resultString, RFIDdata.class);
        publisher.publishAndroid(oneTag.getDoorid());
        publisher.publishHotelSerenity(oneTag.getDoorid());
       
        if ((userid+"/LWT").equals(topic)) {
            System.err.println("Sensor gone!");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //no-op
    }
    
}

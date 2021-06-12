package mqtt.subscriber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.*;

import com.google.gson.Gson;

import mqtt.utils.Utils;
import solutions.RFIDdata;


public class DoorSubscribeCallback implements MqttCallback {

    public static final String userid = "hotelserenity"; 
 
    String clientId = userid + "-sub";
    
    @Override
    public void connectionLost(Throwable cause) {}

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message arrived. Topic: " + topic + "  Message: " + message.toString());
        
        PhidgetMotorMover.moveServoTo(180.0);
        System.out.println("Lock opened.");
        Utils.waitFor(5);
        PhidgetMotorMover.moveServoTo(0.0);
        Utils.waitFor(2);
        
        if ((userid+"/LWT").equals(topic)) {
            System.err.println("Sensor gone!");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
    
}

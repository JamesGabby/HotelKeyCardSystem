package mqtt.subscriber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.phidget22.PhidgetException;
import com.phidget22.RCServo;

import mqtt.utils.Utils;
import solutions.RFIDdata;

public class DoorSubscriber {

    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static String getMotorIDServerURL = "http://localhost:8080/PhidgetServer2019Original/GetRoomNumber";
    public static final String userid = "hotelserenity"; 
    String clientId = userid + "-sub";
 
    private MqttClient mqttClient;
    
    RCServo ch;
    String oneTagJson = new String();
    Gson gson = new Gson();
    RFIDdata oneTag = new RFIDdata();

    public DoorSubscriber() throws PhidgetException {
    	
    	ch = new RCServo();
    	ch.open(5000);
    	Integer motorid = ch.getDeviceSerialNumber();
    	oneTag.setDoorid(motorid.toString());
    	ch.close();
    	System.out.println("DEBUG MOTORID: "+motorid);
        oneTagJson = gson.toJson(oneTag);
        
        String resultString = getRoomNumber(oneTagJson);
        oneTag = gson.fromJson(resultString, RFIDdata.class);
        System.out.println("DEBUG DOORNAME="+oneTag.getDoorid());

        try {
        	// Create a client to connect to the broker, with a random client id
        	// this ensures publisher and client have different client ids
            mqttClient = new MqttClient(BROKER_URL, userid + "-" +Utils.createRandomNumberBetween(1, 1000));
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() throws PhidgetException {
        try {
        	
            mqttClient.setCallback(new DoorSubscribeCallback());
            mqttClient.connect();

            //Subscribe to correct topic
            final String topic = userid+"/"+oneTag.getDoorid();  
            mqttClient.subscribe(topic);

            System.out.println("Subscriber is now listening to "+topic);
         
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String... args) throws PhidgetException {
        final DoorSubscriber subscriber = new DoorSubscriber();
        subscriber.start();
    }
    
    public static String getRoomNumber(String oneSensorJson){
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        
        try {
			oneSensorJson = URLEncoder.encode(oneSensorJson, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        String fullURL = getMotorIDServerURL + "?sensordata="+oneSensorJson;
        System.out.println("Sending data to: "+fullURL);  
        String line;
        String result = "";
        try {
           url = new URL(fullURL);
           conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("GET");
           rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          
           while ((line = rd.readLine()) != null) {
              result += line;
           }
           rd.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
        System.out.println("RESULT: "+result);
        
        return result;    	    
    }
    
}

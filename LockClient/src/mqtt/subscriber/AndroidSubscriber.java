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


public class AndroidSubscriber {

    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static String getAndroidIDServerURL = "http://localhost:8080/PhidgetServer2019Original/AndroidDoorLookup";
    public static final String userid = "hotelserenity/android"; 
    public static final String androidID = "79832782";
    String clientId = userid + "-sub";
    private MqttClient mqttClient;
    
    String oneTagJson = new String();
    RFIDdata oneTag = new RFIDdata();
    Gson gson = new Gson();
    RCServo ch;
    
    public AndroidSubscriber() throws PhidgetException {
    	
        try {
            mqttClient = new MqttClient(BROKER_URL, userid + "-" +Utils.createRandomNumberBetween(1, 1000));
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() throws PhidgetException {
    	  	
        try {
            mqttClient.setCallback(new AndroidSubscriberCallBack());
            mqttClient.connect();

            final String topic = userid+"/"+androidID;  
            mqttClient.subscribe(topic);

            System.out.println("Subscriber is now listening to "+topic);

        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String... args) throws PhidgetException {
    	
        final AndroidSubscriber subscriber = new AndroidSubscriber();
        subscriber.start();
    }
    
    public static String androidDoorLookup(String oneSensorJson){
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        
        try {
			oneSensorJson = URLEncoder.encode(oneSensorJson, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        String fullURL = getAndroidIDServerURL + "?sensordata="+oneSensorJson;
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

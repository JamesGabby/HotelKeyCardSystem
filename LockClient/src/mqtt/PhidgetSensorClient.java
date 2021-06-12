package mqtt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.phidget22.PhidgetException;
import com.phidget22.RCServo;
import com.phidget22.RFID;
import com.phidget22.RFIDTagEvent;
import com.phidget22.RFIDTagListener;
import com.phidget22.RFIDTagLostEvent;
import com.phidget22.RFIDTagLostListener;

import mqtt.publisher.PhidgetPublisher;
import solutions.RFIDdata;

public class PhidgetSensorClient {

	public static String validateServerURL = "http://localhost:8080/PhidgetServer2019Original/ValidateTagUpdateTable";
	PhidgetPublisher publisher = new PhidgetPublisher();
	RFID rfid = new RFID();
    RFIDdata oneTag = new RFIDdata();
    Gson gson = new Gson();
    String oneSensorJson = new String();
    String oneTagJson = new String();
    static RCServo ch;
    
	public static void main(String[] args) throws PhidgetException {
		new PhidgetSensorClient();
	}

	public PhidgetSensorClient() throws PhidgetException {

		rfid.addTagListener(new RFIDTagListener() {
			public void onTag(RFIDTagEvent e) {
				try {
					int reader = rfid.getDeviceSerialNumber();
					String tagRead = e.getTag();
					oneTag.setTagid(tagRead);
	                oneTag.setReaderid(reader);
					System.out.println("DEBUG: Tag read: " + tagRead + " on reader: " + reader);
	                
	                oneTagJson = gson.toJson(oneTag);
	                String resultString = validateTag(oneTagJson);
	                oneTag = gson.fromJson(resultString, RFIDdata.class);
	               
	                try {
	                	if (oneTag.getValid().equals("VALID")) {
	                		System.out.println("DEBUG: Get Room: " + oneTag.getDoorid());
	                		publisher.publishHotelSerenity(oneTag.getDoorid());
	                	} else {
	                		System.out.println("This tag is invalid.");
	                	}
	                } catch (MqttException e1) {
		                // TODO Auto-generated catch block
		                e1.printStackTrace();
	                }
				} catch (PhidgetException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		});
		
		rfid.addTagLostListener(new RFIDTagLostListener() {
			public void onTagLost(RFIDTagLostEvent e) {
				System.out.println("DEBUG: Tag lost: " + e.getTag());
			}
		});

		rfid.open(5000);  
		try {      	                                
			System.out.println("\n\nGathering data...\n\n");
			Thread.sleep(Long.MAX_VALUE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			rfid.close();
			System.out.println("Publisher closed and exiting...");
		}
	}
	
	public String validateTag(String oneSensorJson){
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        
        try {
			oneSensorJson = URLEncoder.encode(oneSensorJson, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        String fullURL = validateServerURL + "?sensordata="+oneSensorJson;
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

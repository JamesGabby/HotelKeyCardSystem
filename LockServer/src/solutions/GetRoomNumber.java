package solutions;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.io.*;
import java.sql.*;

/**
 * Servlet implementation class sensorToDB
 */
@WebServlet("/GetRoomNumber")
public class GetRoomNumber extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	RFIDdata lastSensor = new RFIDdata();

	Connection conn = null;
	Statement stmt;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("GetRoomNumber server is up and running\n");			  
	} // init()

	private void getConnection() {
		String user = "gabbituj";
		String password = "queonitH8";

		String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/"+user;

		try {  Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("DEBUG: Connection to database successful.");
			stmt = conn.createStatement();
		} catch (SQLException se) {
			System.out.println(se);
		}
	}

	private void closeConnection() {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void destroy() {
		try { 
			// conn.close();  
		} catch (Exception e) {
			System.out.println(e);
		}
	} 




	public GetRoomNumber() {
		super();
		// TODO Auto-generated constructor stub
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setStatus(HttpServletResponse.SC_OK);
	    
	    RFIDdata oneSensor = new RFIDdata("", 0, null, null);
	    
		String sensorJsonString = request.getParameter("sensordata");
		
		if (sensorJsonString != null) {
			oneSensor = gson.fromJson(sensorJsonString, RFIDdata.class);
			lastSensor = oneSensor;

			PrintWriter out = response.getWriter();
			out.println(updateSensorValues(oneSensor));
			out.close();
		} else {  
    	   sendJSONString(response);
        }
	}

	private String updateSensorValues(RFIDdata oneSensor){
		lastSensor = oneSensor;
		System.out.println("Door ID: "+oneSensor.getDoorid());
		return retrieveSensorData(oneSensor.getDoorid());
	}
	
	private void sendJSONString(HttpServletResponse response) throws IOException{
		  response.setContentType("application/json");  
	      String json = gson.toJson(lastSensor);
	      
	      PrintWriter out = response.getWriter();
	      System.out.println("DEBUG: sensorServer JSON: "+lastSensor.toString());

	      out.println(json);
	      out.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String retrieveSensorData(String doorid) {
		String selectSQL = "SELECT roomid FROM doorlookup WHERE doorid = '" + doorid + "';";
		ResultSet rs;

		RFIDdata allData = new RFIDdata();

		try {	        
			getConnection();
			rs = stmt.executeQuery(selectSQL);

			while (rs.next()) {
				RFIDdata oneRfid = new RFIDdata(); // fill in statement
				allData.setDoorid(rs.getString("roomid"));	
				System.out.println("DEBUG: "+oneRfid.getDoorid());
			}
		} catch (SQLException ex) {
			System.out.println("Error in SQL " + ex.getMessage());
		}

		closeConnection();

		String allSensorsJson = gson.toJson(allData);
		
		return allSensorsJson;
	}

}

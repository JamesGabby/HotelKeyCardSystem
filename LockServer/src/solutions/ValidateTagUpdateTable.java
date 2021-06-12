package solutions;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/ValidateTagUpdateTable")

public class ValidateTagUpdateTable extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	RFIDdata lastSensor = new RFIDdata("unknown", 0);
	String stringToReturn;
	String returnJson;
	Gson gson = new Gson();
	Connection conn = null;
	Statement stmt;

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
			System.out.println("\nDid you alter the lines to set user/password in the sensor server code?");
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

	private void insertIntoAttempts(RFIDdata oneSensor){
		try {
			String updateSQL = 
					"INSERT INTO attempts(tagid, readerid, date, valid) " +
							"values('"+oneSensor.getTagid()  + "','" +
							oneSensor.getReaderid()  + "'," +
							"now(),'" +
							oneSensor.getValid()  + "');";

			System.out.println("DEBUG: Update: " + updateSQL);

			getConnection();
			stmt.executeUpdate(updateSQL);
			closeConnection();

			System.out.println("DEBUG: Update successful ");
		} catch (SQLException se) {
			System.out.println(se);
			System.out.println("\nDEBUG: Update error - see error trace above for help. ");
			return;
		}
		return;
	}	

	private String validateTag(String sensorname) {
		String selectSQL = "SELECT * FROM validtags WHERE tagid='" + 
				sensorname + "';";
		ResultSet rs;
		RFIDdata allSensors = new RFIDdata("unknown", 0);

		try {	        
			getConnection();
			rs = stmt.executeQuery(selectSQL);

			while (rs.next()) {
				RFIDdata oneSensor = new RFIDdata("unknown", 0); // fill in statement
				oneSensor.setDoorid(rs.getString("roomid"));
				oneSensor.setValid("VALID");
				oneSensor.setTagid(lastSensor.getTagid());
				oneSensor.setReaderid(lastSensor.getReaderid());
				allSensors = oneSensor;

				System.out.println("DEBUG: "+allSensors.toString());
			}
		} catch (SQLException ex) {
			System.out.println("Error in SQL " + ex.getMessage());
		}

		closeConnection();
		String allSensorsJson = gson.toJson(allSensors);

		return allSensorsJson;

	}

	public ValidateTagUpdateTable() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		System.out.println("Tag Validation server is up and running\n");		  
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		RFIDdata oneSensor = new RFIDdata("unknown", 0);
		String sensorJsonString = request.getParameter("sensordata");

		if (sensorJsonString != null) {
			oneSensor = gson.fromJson(sensorJsonString, RFIDdata.class);
			lastSensor = oneSensor;
			System.out.println("lastSensor: "+lastSensor);

			System.out.println("oneSensor.getTagid(): "+oneSensor.getTagid());
			String validatedTag = validateTag(oneSensor.getTagid());
			System.out.println("validatedTag: "+validatedTag);

			if (validatedTag != null) {
				stringToReturn = validatedTag;
			}
			System.out.println("stringToReturn: "+stringToReturn);
			RFIDdata toReturn = gson.fromJson(stringToReturn, RFIDdata.class);
			toReturn.setTagid(lastSensor.getTagid());
			toReturn.setReaderid(lastSensor.getReaderid());

			System.out.println("toReturn: "+toReturn);
			returnJson = gson.toJson(toReturn);
			insertIntoAttempts(toReturn);

			PrintWriter out = response.getWriter();
			out.println(updateSensorValues(toReturn));
			out.close();
		} else {  
			sendJSONString(response);
		}
	}

	private String updateSensorValues(RFIDdata oneSensor){
		lastSensor = oneSensor;
		System.out.println("DEBUG : Last sensor was " + oneSensor.getTagid() + ", with reader "+oneSensor.getReaderid());
		System.out.println("oneSensor: "+oneSensor);
		return returnJson;
	}	


	private void sendJSONString(HttpServletResponse response) throws IOException{
		response.setContentType("application/json");  
		String json = gson.toJson(lastSensor);

		PrintWriter out = response.getWriter();
		System.out.println("DEBUG: sensorServer JSON: "+lastSensor.toString());

		out.println(json);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

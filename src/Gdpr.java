import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


/**
 * Sample class to perform an analysis with Julia and Gdpr Checker
 * 
 * @author Luca Olivieri <luca.olivieri@juliasoft.com>
 */
public class Gdpr {
	
	public static void main(String[] args) {

		User u = new User("Foo", "surnameFoo");
		u.setCreditcard("XXXX-XXXX-XXXX-XXXX");

		sendToEmail(u.getInfo()); // Triggered warning

		sendToDatabase(u.getInfo()); // OK for the policy, without policy trigger a warning

		sendToDatabase(u.toString()); // with the policy Triggered a warning only for ccn
									  // without policy Triggered a warning for name, surname and ccn

		sendToEmail(u.getDate().toString()); // Triggered a unknown source warning 

	}


   /*
	* Example of method that sends a sentence by email
	* It could lead to sensitive data leaks
	* @param textToSend sentence to send
	*/
	private static void sendToEmail(String textToSend) {

		String from = "sender@juliasoft.com";

		String to = "receiver@juliasoft.com";

		String host = "localhost";

		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);

		Session session = Session.getDefaultInstance(properties);

		try {

			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			message.setSubject("my subject");

			message.setText(textToSend);

			Transport.send(message);

		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

   /*
	* Example of method that saves a value in a database
	* It could lead to sensitive data leaks
	* @param toSave value to save
	*/
	static void sendToDatabase(String toSave) {
		
		Connection conn = null;
		String url = "jdbc:mysql://192.168.2.128:3306/";
		String dbName = "anvayaV2";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "";

		try {
			
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + dbName, userName, password);

			Statement st = conn.createStatement();
			String query = "INSERT INTO Customers(CustomerInfo) " + "VALUES ('" + toSave + "')";
			System.out.printf(query);
			st.executeQuery(query);

			conn.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
   /*
	* Example of typically class that contains sensitive data
	*/
	public static class User {

		private String name;

		private String surname;

		private String creditcard;
		
		private String printAll; //variable that shows its contents on output video
		
		private String company;
		
		private Date bdate;

		public User(String name, String surname) {
			this.name = name;
			this.surname=surname;
			printAll+="User data: "+ this.name + " " + this.surname;
			
			System.out.println(printAll);
			
		}

		public Date getDate() {
			return bdate;
		}

		public void setCreditcard(String creditcard) {
			this.creditcard = creditcard;
		}

		public String getCreditcard() {
			return creditcard;
		}
		
		public String getInfo() {

			return "Company:"+ company+ "BirthDate: " + bdate;
		}

		@Override
		public String toString() {

			return "Name: " + name + " Surname: " + surname + "Ccn: " + creditcard;
		}

	}
}
package ebay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.jws.WebService;

@WebService
public class User {
	public boolean register(String firstname, String lastname, String email, String password) {
		System.out.println("Request: " + firstname);
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;
		Calendar calendar = Calendar.getInstance();
		java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

		String query = "insert into customer set first_nm =? , last_nm =? , email_id = ?, pass = ?, last_login_ts = CURRENT_TIMESTAMP";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, firstname);
			preparedStmt.setString(2, lastname);
			preparedStmt.setString(3, email);
			preparedStmt.setString(4, password);

			preparedStmt.execute();

			connect.close();
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}

	public String isUser(String email, String password) {
		System.out.println("Request: " + email);
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "SELECT cust_id, first_nm, DATE_FORMAT(last_login_ts,\'%b %d %Y %h:%i %p\') as date  FROM datahub.customer WHERE email_id = ? and pass = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, email);
			preparedStmt.setString(2, password);

			rs = preparedStmt.executeQuery();
			while (rs.next()) { // convert each object to an human readable JSON
								// object
				int total_rows = rs.getMetaData().getColumnCount();
				xmlArray.append("<result ");
				for (int i = 0; i < total_rows; i++) {
					xmlArray.append(" " + rs.getMetaData().getColumnLabel(i + 1).toLowerCase() + "='"
							+ rs.getObject(i + 1) + "'");
				}
				xmlArray.append(" />");
				String query2 = "Update datahub.customer set last_login_ts = CURRENT_TIMESTAMP WHERE email_id = ?";
				PreparedStatement preparedStmt2 = null;
				preparedStmt2 = connect.prepareStatement(query2);
				preparedStmt2.setString(1, email);
				preparedStmt2.execute();

			}
			connect.close();
		} catch (Exception e) {
			xmlArray.append("</results>");
			return xmlArray.toString();
		} finally {
		}
		xmlArray.append("</results>");
		return xmlArray.toString();
	}

}

package ebay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.jws.WebService;

@WebService
public class Profile {
	public String getItemsForSale(String cust_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "select p.prod_id , p.label, p.description, p.brand as brand_id,  "
				+ "(select b.label from brand b where b.brand_id = p.brand)  brand, p.quantity "
				+ " from product p where p.seller_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, cust_id);

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

	public String getItemsBought(String cust_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "select s.product_id , p.label, p.brand as brand_id,  "
				+ " (select b.label from datahub.brand b where b.brand_id = p.brand)  brand, s.quantity "
				+ " from datahub.product p, datahub.sales s where s.product_id = p.prod_id and s.customer_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, cust_id);

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

	public String getUserInfo(String cust_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "SELECT c.first_nm, c.last_nm, c.email_id, c.month, c.day, c.year, ca.address, ca.city, ca.country "
				+ " FROM customer c left join customer_add ca on ca.customer_id = c.cust_id where c.cust_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, cust_id);

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

	public boolean saveProfile(String cust_id, String day, String year, String address, String city, String country,
			String month) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		String query = "update customer set month = ? , year = ? , day = ? where cust_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, month);
			preparedStmt.setString(2, year);
			preparedStmt.setString(3, day);
			preparedStmt.setString(4, cust_id);

			preparedStmt.execute();

			connect.close();
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}

}

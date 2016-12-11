package ebay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.jws.WebService;

@WebService
public class Trans {

	public boolean addBid(String bid_amount, String product_id, String customer_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		String query = "insert into bid set ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, bid_amount);
			preparedStmt.setString(2, product_id);
			preparedStmt.setString(3, customer_id);

			preparedStmt.execute();

			connect.close();
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}

	public boolean addToCart(String product_id, String user_id, String quantity) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		String query = "insert into cart set ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, product_id);
			preparedStmt.setString(2, user_id);
			preparedStmt.setString(3, quantity);

			preparedStmt.execute();

			connect.close();
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}

	public String getCart(String cust_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "select p.prod_id , p.label,p.brand as brand_id, p.description,(select c.desc from conditions c where c.conditionId = p.condition)  conditions, "
				+ "(select b.label from brand b where b.brand_id = p.brand)  brand, c.quantity, "
				+ " (case when p.ship_price is null then 0 else  p.ship_price end ) ship_price , (case when p.price is null then 0 else  p.price end ) price  "
				+ " from product p, cart c where p.prod_id = c.product_id and c.user_id = ?";
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

	public String getCartAmount(String cust_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "select c.quantity, "
				+ " (case when p.ship_price is null then 0 else  p.ship_price end ) ship_price , (case when p.price is null then 0 else  p.price end ) price  "
				+ " from product p, cart c where p.prod_id = c.product_id and c.user_id = ?";
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

	public boolean removeFromCart(String cust_id, String prod_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		PreparedStatement preparedStmt2 = null;
		String query1 = "delete from cart where user_id = ? and product_id = ?";
		String query2 = "update cart set cart_delete = 1 where user_id = ? and product_id= ?";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query1);
			preparedStmt2 = connect.prepareStatement(query2);
			preparedStmt.setString(1, cust_id);
			preparedStmt.setString(1, prod_id);
			preparedStmt2.setString(1, cust_id);
			preparedStmt2.setString(1, prod_id);

			preparedStmt.execute();
			preparedStmt2.execute();
			connect.close();
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}

	public boolean emptyCart(String cust_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		String query1 = "delete from cart where user_id = ?";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query1);
			preparedStmt.setString(1, cust_id);

			preparedStmt.execute();
			connect.close();
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}

	public String getProductQuanity(String prod_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "select quantity from product where prod_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, prod_id);

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

	public String getAmount(String prod_id) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		StringBuffer xmlArray = new StringBuffer("<results>");
		String query = "select max(bid_amount) as max from bid where product_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, prod_id);

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

	public boolean sold(String customer_id, String product_id, String quantity) {
		Connection connect = null;
		PreparedStatement preparedStmt = null;
		String query = "insert into sales set ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/datahub?" + "user=root&password=blitz");

			preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, customer_id);
			preparedStmt.setString(2, product_id);
			preparedStmt.setString(3, quantity);

			preparedStmt.execute();

			connect.close();
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}
}

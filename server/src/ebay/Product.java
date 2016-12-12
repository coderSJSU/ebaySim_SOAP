package ebay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.jws.WebService;

@WebService
public class Product 
{
	public String productDetails(String prod_id, String customer_id){
		Connection connect = null;
	    PreparedStatement preparedStmt = null;
	    ResultSet rs = null;
	    StringBuffer xmlArray = new StringBuffer("<results>");
	    String query = "select p.prod_id , p.label, p.description,(select c.desc from conditions c where c.conditionId = p.condition)  conditions, "+
	"(select b.label from brand b where b.brand_id = p.brand)  brand, p.is_fixed, p.is_auction," +
 " (select max(b2.bid_amount) from datahub.bid b2 where b2.product_id = p.prod_id )  max, " +
 " (case when p.ship_price is null then 0 else  p.ship_price end ) ship_price , (case when p.price is null then 0 else  p.price end ) price, "+
 " (select count(b2.bid_id) from datahub.bid b2 where b2.product_id = p.prod_id group by b2.product_id) as count, "+ 
 " case when exists (select count(*) from datahub.bid b3  where b3.product_id = p.prod_id and b3.customer_id = "  + customer_id + " group by b3.product_id) "+
 " then 1 "+
 " else 0    "+
 " end as alreadyBid, DATEDIFF(CURDATE(), p.add_ts) as days, "+
 " (case when (select customer_id FROM datahub.bid where bid_amount = (select max(b.bid_amount) from bid b where b.product_id = ?)) = ? "+
 " then 1 else 0 end) as max_bidder " +
 " from datahub.product p where p.prod_id = ? ";
		try {
			Class.forName("com.mysql.jdbc.Driver");
           connect = DriverManager
                           .getConnection("jdbc:mysql://localhost/datahub?"
                                           + "user=root&password=blitz");
           
          preparedStmt = connect.prepareStatement(query);
          preparedStmt.setString(1, prod_id);
 	      preparedStmt.setInt(2, Integer.parseInt(customer_id));
 	      preparedStmt.setString(3, prod_id);

 	     rs  = preparedStmt.executeQuery();
 	    while(rs.next()) { // convert each object to an human readable JSON object
 	    	 int total_rows = rs.getMetaData().getColumnCount();
             xmlArray.append("<result ");
             for (int i = 0; i < total_rows; i++) {
                 xmlArray.append(" " + rs.getMetaData().getColumnLabel(i + 1)
                     .toLowerCase() + "='" + rs.getObject(i + 1) + "'");
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
	
	
	public String showProducts(String cust_id, String category_id){
		Connection connect = null;
	    PreparedStatement preparedStmt = null;
	    ResultSet rs = null;
	    StringBuffer xmlArray = new StringBuffer("<results>");
	    String query = "select prod_id, is_auction, is_fixed, quantity,brand as brand_id, DATEDIFF(CURDATE(), p.add_ts) as days, (select b.label from brand b where b.brand_id = p.brand)  brand, label, description, price, (select c.desc from conditions c where c.conditionId = p.condition)  conditions from product p where p.quantity > 0 and p.seller_id <> ? and p.category_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
           connect = DriverManager
                           .getConnection("jdbc:mysql://localhost/datahub?"
                                           + "user=root&password=blitz");
           
          preparedStmt = connect.prepareStatement(query);
 	      preparedStmt.setString   (1, cust_id);
 	      preparedStmt.setString(2, category_id);

 	     rs  = preparedStmt.executeQuery();
 	    while(rs.next()) { // convert each object to an human readable JSON object
 	    	 int total_rows = rs.getMetaData().getColumnCount();
             xmlArray.append("<result ");
             for (int i = 0; i < total_rows; i++) {
                 xmlArray.append(" " + rs.getMetaData().getColumnLabel(i + 1)
                     .toLowerCase() + "='" + rs.getObject(i + 1) + "'");
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
	
	public String prodDescription(String type){
		Connection connect = null;
	    PreparedStatement preparedStmt = null;
	    ResultSet rs = null;
	    StringBuffer xmlArray = new StringBuffer("<results>");
	    String query = "select * from brand where category_id = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
           connect = DriverManager
                           .getConnection("jdbc:mysql://localhost/datahub?"
                                           + "user=root&password=blitz");
           
          preparedStmt = connect.prepareStatement(query);
 	      preparedStmt.setString   (1, type);

 	     rs  = preparedStmt.executeQuery();
 	    while(rs.next()) { // convert each object to an human readable JSON object
 	    	 int total_rows = rs.getMetaData().getColumnCount();
             xmlArray.append("<result ");
             for (int i = 0; i < total_rows; i++) {
                 xmlArray.append(" " + rs.getMetaData().getColumnLabel(i + 1)
                     .toLowerCase() + "='" + rs.getObject(i + 1) + "'");
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

	
	public boolean addProduct(String label, String desc, String quantity, String brand){
		Connection connect = null;
	    PreparedStatement preparedStmt = null;
	    
	    String query = "insert into datahub.product set ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
           connect = DriverManager
                           .getConnection("jdbc:mysql://localhost/datahub?"
                                           + "user=root&password=blitz");
           
          preparedStmt = connect.prepareStatement(query);
 	     
 	    connect.close();
		} catch (Exception e) {
			return false;
   } finally {
   }
			return true;
	}
}

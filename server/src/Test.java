
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Test {
	

	public static void main(String[] args) {
		Test test = new Test();
		Connection connect = null;
	     Statement statement = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                            .getConnection("jdbc:mysql://localhost/datahub?"
                                            + "user=root&password=blitz");
            statement = connect.createStatement();
            
            resultSet = statement
                    .executeQuery("select * from datahub.bid");
    //writeResultSet(resultSet);
		} catch (Exception e) {
    } finally {
    }
	}

}

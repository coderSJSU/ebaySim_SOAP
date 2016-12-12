package pooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {

    private static BlockingQueue<Connection> pool;
    /**
     * Maximum number of connections that the pool can have
     */
    private static int maxPoolSize;
    /**
     * Number of connections that should be created initially
     */
    private int initialPoolSize;
    /**
     * Number of connections generated so far
     */
    private static int currentPoolSize;

  

    public ConnectionPool(int maxPoolSize, int initialPoolSize, String url, String username,
                          String password, String driverClassName) throws ClassNotFoundException, SQLException {

    	if(currentPoolSize == 0){
        if ((initialPoolSize > maxPoolSize) || initialPoolSize < 1 || maxPoolSize < 1) {
            throw new IllegalArgumentException("Invalid pool size parameters");
        }

        // default max pool size to 10
        maxPoolSize = maxPoolSize > 0 ? maxPoolSize : 10;
        this.initialPoolSize = initialPoolSize;
        pool = new LinkedBlockingQueue<Connection>(maxPoolSize);

        initPooledConnections(driverClassName);

        if (pool.size() != initialPoolSize) {
            
        }
    	}

    }

    private void initPooledConnections(String driverClassName)
            throws ClassNotFoundException, SQLException {

        // 1. Attempt to load the driver class
        Class.forName("com.mysql.jdbc.Driver");

        // 2. Create and pool connections
        for (int i = 0; i < initialPoolSize; i++) {
            openAndPoolConnection();
        }
    }

    private synchronized static void openAndPoolConnection() throws SQLException {
        if (currentPoolSize == maxPoolSize) {
            return;
        }

        Connection conn = DriverManager
                .getConnection("jdbc:mysql://localhost/datahub?"
                        + "user=root&password=blitz");
        pool.offer(conn);
        currentPoolSize++;

    }

    public static Connection borrowConnection() throws InterruptedException, SQLException {

        if (pool.peek()==null && currentPoolSize < maxPoolSize) {
            openAndPoolConnection();
        }
        return pool.take();
    }

    public static void surrenderConnection(Connection conn) {
        pool.offer(conn); // offer() as we do not want to go beyond capacity
    }
}
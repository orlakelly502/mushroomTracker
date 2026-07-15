import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.cj.jdbc.MysqlDataSource;

public class DBConnect implements AutoCloseable {
    public static final String DB_NAME = "mushrooms";
    public static final String SERVER = "localhost";
    private static final int PORT = 3306;

    private Connection connection;

    //throws us used here so exception is passed up the call stack instead of the constructor
    // creating a broken object that would likely cause a NullPointerException if interacted with
    public DBConnect(String username, String password) throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setServerName(SERVER);
        dataSource.setPort(PORT);
        dataSource.setDatabaseName(DB_NAME);

        this.connection = dataSource.getConnection(username, password);
    }

    public Connection getConnection(){
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
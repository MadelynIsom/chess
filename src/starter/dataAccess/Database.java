package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsible for creating connections to the database. Connections should be closed after use, either by calling
 * {@link #closeConnection(Connection)} on the Database instance or directly on the connection.
 */
public class Database {

    // FIXME: Change these fields, if necessary, to match your database configuration
    public static final String DB_NAME = "chess";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "monstermash";

    public static final Database DB = new Database();

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;

    /**
     * Gets a connection to the database.
     *
     * @return Connection the connection.
     * @throws DataAccessException if a data access error occurs.
     */
    public Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(CONNECTION_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Closes the specified connection.
     *
     * @param connection the connection to be closed.
     * @throws DataAccessException if a data access error occurs while closing the connection.
     */
    public void closeConnection(Connection connection) throws DataAccessException {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    static public void main(String[] args) throws DataAccessException {
        DB.configureDatabase();
    }

    public void configureDatabase() throws DataAccessException {
        try (var conn = getConnection()) {
            var createDbStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS chess");
            createDbStatement.executeUpdate();

            conn.setCatalog("chess");

            var createAuthTable = """
            CREATE TABLE IF NOT EXISTS authTable (
                token VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (token)
            )""";

            var createUserTable = """
            CREATE TABLE IF NOT EXISTS userTable (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

            var createGameTable = """
            CREATE TABLE IF NOT EXISTS gameTable (
                gameID INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                gameImpl TEXT NOT NULL,
                complete TINYINT NOT NULL,
                PRIMARY KEY (gameID)
            )""";


            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
            }
            try(var createTableStatement = conn.prepareStatement(createUserTable)){
                createTableStatement.executeUpdate();
            }
            try(var createTableStatement = conn.prepareStatement(createGameTable)){
                createTableStatement.executeUpdate();
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }


    static void truncateTable(String table) throws DataAccessException {
        Connection conn = Database.DB.getConnection();
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE " + table)) {
            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        finally{
            Database.DB.closeConnection(conn);
        }
    }
}
















/*
package dataAccess;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Database is responsible for creating connections to the database. Connections are
 * managed with a simple pool in order to increase performance. To obtain and
 * use connections represented by this class use the following pattern.
 *
 * <pre>
 *  public boolean example(String selectStatement, Database db) throws DataAccessException{
 *    var conn = db.getConnection();
 *    try (var preparedStatement = conn.prepareStatement(selectStatement)) {
 *        return preparedStatement.execute();
 *    } catch (SQLException ex) {
 *        throw new DataAccessException(ex.toString());
 *    } finally {
 *        db.returnConnection(conn);
 *    }
 *  }
 * </pre>
 */

/*
public class Database {

    // FIXME: Change these fields, if necessary, to match your database configuration
    public static final String DB_NAME = "chess";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "monstermash";

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306";

    public static final Database DB = new Database();

    private final LinkedList<Connection> connections = new LinkedList<>();



    /**
     * Get a connection to the database. This pulls a connection out of a simple
     * pool implementation. The connection must be returned to the pool after
     * you are done with it by calling {@link #returnConnection(Connection) returnConnection}.
     *
     * @return Connection
     */
    /*
    synchronized public Connection getConnection() throws DataAccessException {
        try {
            Connection connection;
            if (connections.isEmpty()) {
                connection = DriverManager.getConnection(CONNECTION_URL, DB_USERNAME, DB_PASSWORD);
                connection.setCatalog(DB_NAME);
            } else {
                connection = connections.removeFirst();
            }
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Return a previously acquired connection to the pool.
     *
     * @param connection previous obtained by calling {@link #getConnection() getConnection}.
     */
    /*
    synchronized public void returnConnection(Connection connection) {
        connections.add(connection);
    }
}

*/
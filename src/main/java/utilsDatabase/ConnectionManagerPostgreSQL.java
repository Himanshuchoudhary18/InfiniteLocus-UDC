package utilsDatabase;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import utilities.Base;

import java.sql.*;
import java.util.*;

import static org.testng.AssertJUnit.assertNotNull;
import static utilsDatabase.SSHConnectionManager.*;

@Slf4j
public class ConnectionManagerPostgreSQL {
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int MAX_CONNECTION_RETRIES = 3;
    private static final int CONNECTION_RETRY_DELAY_MS = 1000;
    private static final ThreadLocal<Connection> dbConnection = new ThreadLocal<>();
    private static final ThreadLocal<String> dbHost = new ThreadLocal<>();
    private static final ThreadLocal<String> dbUser = new ThreadLocal<>();
    private static final ThreadLocal<String> dbPassword = new ThreadLocal<>();
    private static final ThreadLocal<String> dbName = new ThreadLocal<>();
    private static final ThreadLocal<Integer> dbPort = ThreadLocal.withInitial(() -> 5432);
    private static final ThreadLocal<Boolean> isLocalRun = new ThreadLocal<>();

    public static void setDbHost(String value) { dbHost.set(value); }
    public static String getDbHost() { return dbHost.get(); }
    public static void setDbUser(String value) { dbUser.set(value); }
    public static String getDbUser() { return dbUser.get(); }
    public static void setDbPassword(String value) { dbPassword.set(value); }
    public static String getDbPassword() { return dbPassword.get(); }
    public static void setDbName(String value) { dbName.set(value); }
    public static String getDbName() { return dbName.get(); }
    public static void setDbPort(Integer value) { dbPort.set(value); }
    public static Integer getDbPort() { return dbPort.get(); }
    public static void setIsLocalRun(Boolean value) { isLocalRun.set(value); }
    public static Boolean getIsLocalRun() { return isLocalRun.get(); }

    private static void connectToPostgreSQLDatabase() throws JSchException {
        setDbHost(Base.getProperty().getProperty("postgreSQL_Host"));
        setDbName(Base.getProperty().getProperty("postgreSQL_DatabaseName"));
        setDbUser(Base.getProperty().getProperty("postgreSQL_User"));
        setDbPassword(Base.getProperty().getProperty("postgreSQL_Password"));
        connectToPostgreSQLDatabase(dbUser.get(), dbHost.get(), dbPassword.get(), dbName.get());
    }

    private static void connectToPostgreSQLDatabase(String dbUser, String dbHost, String dbPassword, String dbName) throws JSchException {
        boolean connected = false;
        int assignedPort;
        setIsLocalRun(Boolean.valueOf(Base.getProperty().getProperty("local")));
        if (isLocalRun.get()) {
            assignedPort = setPortForwarding(0, dbHost, dbPort.get());
        } else {
            assignedPort = getDbPort();
        }

        String jdbcHost = "127.0.0.1";
        String jdbcUrl = "jdbc:postgresql://" + jdbcHost + ":" + assignedPort + "/" + dbName;
        int retryCount = 0;

        while (!connected && retryCount < MAX_CONNECTION_RETRIES) {
            try {
                DriverManager.setLoginTimeout(CONNECTION_TIMEOUT / 1000);
                Properties props = new Properties();
                props.setProperty("user", dbUser);
                props.setProperty("password", dbPassword);
                props.setProperty("ssl", "false");

                dbConnection.set(DriverManager.getConnection(jdbcUrl, props));
                connected = true;
                Base.logger.info("{} DB Connection successful", dbName);
            } catch (SQLException e) {
                retryCount++;
                Base.logger.info("{} Database connection failed on attempt {}, will retry...", dbName, retryCount);
            }
        }

        if (!connected) {
            Base.logger.info("Failed to establish database connection after multiple retries!");
        }
    }

    private static void verifyDatabaseConnection() {
        assertNotNull("Database connection should not be null.", dbConnection.get());
    }

    public static void connectToDatabasePostgreSQL() throws JSchException {
        connectToCommonSSHServer();
        connectToPostgreSQLDatabase();
        verifyDatabaseConnection();
    }

    public static void closeConnectionDatabasePostgreSQL() {
        try {
            if (!Objects.isNull(dbConnection.get())) {
                dbConnection.get().close();
                Base.logger.info("DB Connection closed successfully");
            } else {
                Base.logger.info("DB Connection is already closed");
            }
        } catch (SQLException e) {
            Base.logger.error("Error occurred while closing the DB connection: {}", e.getMessage(), e);
        } finally {
            disconnectSSHSessionDeletePortForwarding();
        }
    }

    public static List<Map<String, String>> executeSelectQuery(String query) {
        List<Map<String, String>> resultList = new ArrayList<>();
        try (Statement statement = dbConnection.get().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = resultSet.getString(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }

            Base.logger.info("Query executed successfully: {}", query);

        } catch (SQLException e) {
            Base.logger.error("Error executing SELECT query: {}", e.getMessage(), e);
        }

        return resultList;
    }
}

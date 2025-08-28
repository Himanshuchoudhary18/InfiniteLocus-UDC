package utilsDatabase;

import lombok.Setter;
import redis.clients.jedis.Jedis;
import utilities.Base;

public class ConnectionManagerRedis {
    private static final ThreadLocal<Jedis> jedis = new ThreadLocal<>();
    private static final ThreadLocal<String> dbUser = new ThreadLocal<>();
    private static final ThreadLocal<String> dbPassword = new ThreadLocal<>();
    private static final ThreadLocal<Integer> dbPort = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isLocalRun = new ThreadLocal<>();
    private static final ThreadLocal<String> dbHost = new ThreadLocal<>();

    public static void connectToDatabaseRedis() {
        String connectionString = "redis://" + dbHost.get() + ":" + dbPort.get();
        // Connect to Redis
        try {
            jedis.set(new Jedis(connectionString));
            jedis.get().auth(dbUser.get(), dbPassword.get());
            Base.logger.info("Connected to Redis successfully!");
        } catch (Exception e) {
            Base.logger.error("Error connecting to Redis: {}", e.getMessage());
        }
    }

    public static void closeConnectionRedis() {
        try {
            if (jedis.get() != null) {
                jedis.get().close();
                Base.logger.info("Closed the connection to Redis Database");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

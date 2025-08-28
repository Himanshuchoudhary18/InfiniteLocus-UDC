package utilsDatabase;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import utilities.Base;

public class ConnectionManagerMongo {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    public static void connectToDatabaseMongo()
    {
        try {
            String uri = "mongodb+srv://infinite-locus:i4vEqNkDOtWVaULn@erspl-logistics.vd82vdt.mongodb.net/?retryWrites=true&w=majority&appName=udc";
            String dbName = "udc";

            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(dbName);
            Base.logger.info(" Connected to MongoDB database: " + dbName);
        }
        catch (Exception e)
        {
            Base.logger.error(" Error connecting to MongoDB: ", e);
        }
    }

    public static MongoDatabase getDatabase()
    {
        return database;
    }

    public static void closeMongoConnection() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                Base.logger.info("MongoDB connection closed");
            }
        } catch (Exception e) {
            Base.logger.warn("Error while closing MongoDB connection: ", e);
        }
    }
}
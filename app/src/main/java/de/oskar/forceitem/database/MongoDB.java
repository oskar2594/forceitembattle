package de.oskar.forceitem.database;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
    private static MongoDB instance;

    private static final String connectionString = "mongodb+srv://forceitem_plugin:X@forceitembattleresults.wdhvzry.mongodb.net/?retryWrites=true&w=majority";

    private ServerApi serverApi;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoClientSettings settings;

    private boolean connected = false;

    private MongoDB() {
        this.serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        this.settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
    }

    public void connect() {
        if (this.connected) {
            return;
        }
         
        this.mongoClient = MongoClients.create(this.settings);
        try{
            this.database = this.mongoClient.getDatabase("forceitem");
            database.runCommand(new Document("ping", 1));
            this.connected = true;
            System.out.println("Connected to MongoDB");
        } catch (MongoException  e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (!this.connected) {
            return;
        }
        this.mongoClient.close();
        this.connected = false;
    }

    public MongoDatabase getDatabase() {
        if(!this.connected) {
            this.connect();
        }
        return this.database;
    }

    public MongoClient getMongoClient() {
        if(!this.connected) {
            this.connect();
        }
        return this.mongoClient;
    }

    public static MongoDB getInstance() {
        if (instance == null) {
            instance = new MongoDB();
        }
        return instance;
    }

}

package cc.minetale.commonlib.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

@Getter
public class Database {

    @Getter private static Database database;

    private final MongoCollection<Document> rankCollection;
    private final MongoCollection<Document> grantsCollection;
    private final MongoCollection<Document> punishmentsCollection;
    private final MongoCollection<Document> profilesCollection;

    public Database(MongoDatabase mongoDatabase) {
        Database.database = this;

        this.rankCollection =  mongoDatabase.getCollection("ranks");
        this.grantsCollection =  mongoDatabase.getCollection("grants");
        this.punishmentsCollection =  mongoDatabase.getCollection("punishments");
        this.profilesCollection =  mongoDatabase.getCollection("profiles");
    }

}

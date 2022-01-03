package cc.minetale.commonlib.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

public class Database {

    @Getter @Setter private static MongoCollection<Document> serversCollection;
    @Getter @Setter private static MongoCollection<Document> rankCollection;
    @Getter @Setter private static MongoCollection<Document> grantsCollection;
    @Getter @Setter private static MongoCollection<Document> punishmentsCollection;
    @Getter @Setter private static MongoCollection<Document> profilesCollection;

    public static void init(MongoDatabase database) {
        serversCollection = database.getCollection("servers");
        rankCollection = database.getCollection("ranks");
        grantsCollection = database.getCollection("grants");
        punishmentsCollection = database.getCollection("punishments");
        profilesCollection = database.getCollection("profiles");
    }

}

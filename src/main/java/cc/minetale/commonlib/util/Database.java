package cc.minetale.commonlib.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bson.Document;

@UtilityClass
public class Database {

    @Getter private static MongoCollection<Document> rankCollection;
    @Getter private static MongoCollection<Document> grantsCollection;
    @Getter private static MongoCollection<Document> punishmentsCollection;
    @Getter private static MongoCollection<Document> profilesCollection;

    public static void init(MongoDatabase database) {
        rankCollection = database.getCollection("ranks");
        grantsCollection = database.getCollection("grants");
        punishmentsCollection = database.getCollection("punishments");
        profilesCollection = database.getCollection("profiles");
    }

}

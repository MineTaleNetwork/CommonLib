package cc.minetale.commonlib;

import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.StringUtil;
import cc.minetale.pigeon.Pigeon;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

public class CommonLib {

    @Getter private static MongoClient mongoClient;
    @Getter private static MongoDatabase mongoDatabase;

    public static void init() {
        loadPigeon();
        loadMongo();

        var pigeon = Pigeon.getPigeon();

        pigeon.getConvertersRegistry()
                .registerConvertersInPackage("cc.minetale.commonlib.pigeon.converters");

        pigeon.getPayloadsRegistry()
                .registerPayloadsInPackage("cc.minetale.commonlib.pigeon.payloads");

        pigeon.setupDefaultUpdater();
        pigeon.acceptDelivery();

        Database.init(mongoDatabase);
    }

    private static void loadPigeon() {
        var pigeon = new Pigeon();

        pigeon.initialize(
                System.getProperty("pigeonHost", "127.0.0.1"),
                Integer.getInteger("pigeonPort", 5672),
                System.getProperty("pigeonNetwork", "minetale"),
                System.getProperty("pigeonUnit", StringUtil.generateId())
        );

        pigeon.setupDefaultUpdater();
    }

    private static void loadMongo() {
        mongoClient = new MongoClient(System.getProperty("mongoHost", "127.0.0.1"), Integer.getInteger("mongoPort", 27017));
        mongoDatabase = mongoClient.getDatabase(System.getProperty("mongoDatabase", "MineTale"));
    }

}
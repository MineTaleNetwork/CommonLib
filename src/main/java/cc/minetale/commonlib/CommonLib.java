package cc.minetale.commonlib;

import cc.minetale.commonlib.api.LibProvider;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.StringUtil;
import cc.minetale.pigeon.Pigeon;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class CommonLib {

    @Getter private static final List<LibProvider> providers = new ArrayList<>();
    @Getter private static ObjectMapper mapper;
    @Getter private static JedisPool jedisPool;
    @Getter private static MongoClient mongoClient;
    @Getter private static MongoDatabase mongoDatabase;

    public static void init() {
        mapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        loadPigeon();
        loadMongo();
        loadRedis();

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

    private static void loadRedis() {
        jedisPool = new JedisPool(System.getProperty("redisHost", "127.0.0.1"), Integer.getInteger("redisPort", 6379));
    }

}
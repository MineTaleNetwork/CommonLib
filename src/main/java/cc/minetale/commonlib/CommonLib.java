package cc.minetale.commonlib;

import cc.minetale.commonlib.api.LibProvider;
import cc.minetale.commonlib.pigeon.serializers.ColorSerializers;
import cc.minetale.commonlib.util.Config;
import cc.minetale.commonlib.util.Database;
import cc.minetale.pigeon.Pigeon;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.undercouch.bson4jackson.BsonFactory;
import lombok.Getter;
import redis.clients.jedis.JedisPool;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommonLib {

    @Getter private static final List<LibProvider> providers = new ArrayList<>();
    @Getter private static ObjectMapper jsonMapper;
    @Getter private static ObjectMapper bsonMapper;
    @Getter private static JedisPool jedisPool;
    @Getter private static MongoClient mongoClient;
    @Getter private static MongoDatabase mongoDatabase;

    public static void init() {
        setupMappers();

        loadPigeon();
        loadMongo();
        loadRedis();

        var pigeon = Pigeon.getPigeon();

        pigeon.getPayloadsRegistry()
                .registerPayloadsInPackage("cc.minetale.commonlib.pigeon.payloads");

        pigeon.acceptDelivery();

        Database.init(mongoDatabase);
    }

    private static void setupMappers() {
        final var jf = new JsonFactory();
        final var bf = new BsonFactory();

        final var serializationInclusion = JsonInclude.Include.NON_ABSENT;
        final var defaultVisibility = JsonAutoDetect.Value.construct(
                JsonAutoDetect.Visibility.ANY,
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.NONE,
                JsonAutoDetect.Visibility.ANY
        );

        final var module = new SimpleModule()
                .addSerializer(Color.class, new ColorSerializers.Serializer())
                .addDeserializer(Color.class, new ColorSerializers.Deserializer());

        final var ctorParamsModule = new ParameterNamesModule();

        jsonMapper = new ObjectMapper(jf)
                .setSerializationInclusion(serializationInclusion)
                .setDefaultVisibility(defaultVisibility)
                .registerModules(
                        module,
                        ctorParamsModule);

        bsonMapper = new ObjectMapper(bf)
                .setSerializationInclusion(serializationInclusion)
                .setDefaultVisibility(defaultVisibility)
                .registerModules(
                        module,
                        ctorParamsModule);
    }

    private static void loadPigeon() {
        var pigeon = new Pigeon();

        pigeon.initialize(
                Config.PIGEON_HOST,
                Config.PIGEON_PORT,
                Config.PIGEON_NETWORK,
                Config.PIGEON_UNIT,
                CommonLib.getJsonMapper()
        );

        pigeon.setupDefaultUpdater();
    }

    private static void loadMongo() {
        mongoClient = new MongoClient(Config.MONGO_HOST, Config.MONGO_PORT);
        mongoDatabase = mongoClient.getDatabase(Config.MONGO_DATABASE);
    }

    private static void loadRedis() {
        jedisPool = new JedisPool(Config.REDIS_HOST, Config.REDIS_PORT);
    }

}
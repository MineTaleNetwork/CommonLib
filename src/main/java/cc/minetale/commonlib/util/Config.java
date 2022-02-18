package cc.minetale.commonlib.util;

public class Config {

    // Pigeon
    public static final String PIGEON_HOST = System.getProperty("pigeonHost", "127.0.0.1");
    public static final int PIGEON_PORT = Integer.getInteger("pigeonPort", 5672);
    public static final String PIGEON_NETWORK = System.getProperty("pigeonNetwork", "minetale");
    public static final String PIGEON_UNIT = System.getProperty("pigeonUnit", StringUtil.generateId());

    // Mongo
    public static final String MONGO_HOST = System.getProperty("mongoHost", "127.0.0.1");
    public static final int MONGO_PORT = Integer.getInteger("mongoPort", 27017);
    public static String MONGO_DATABASE = System.getProperty("mongoDatabase", "MineTale");

    // Redis
    public static final String REDIS_HOST = System.getProperty("redisHost", "127.0.0.1");
    public static final int REDIS_PORT = Integer.getInteger("redisPort", 6379);


}

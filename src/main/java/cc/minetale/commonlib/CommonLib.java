package cc.minetale.commonlib;

import cc.minetale.commonlib.api.APIListener;
import cc.minetale.commonlib.pigeon.Listeners;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.timer.TimerManager;
import cc.minetale.pigeon.Pigeon;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CommonLib {

    @Getter private static CommonLib commonLib;

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final Pigeon pigeon;
    private final TimerManager timerManager;
    private final Set<APIListener> apiListeners;

    public CommonLib(MongoClient mongoClient, MongoDatabase mongoDatabase, Pigeon pigeon) {
        CommonLib.commonLib = this;

        this.timerManager = new TimerManager();
        this.apiListeners = new HashSet<>();

        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;
        this.pigeon = pigeon;

        this.pigeon.getConvertersRegistry()
                .registerConvertersInPackage("cc.minetale.commonlib.pigeon.converters");

        this.pigeon.getPayloadsRegistry()
                .registerPayloadsInPackage("cc.minetale.commonlib.pigeon.payloads");

        this.pigeon.getListenersRegistry()
                .registerListener(new Listeners());

        new Database(this.mongoDatabase);

        PigeonUtil.setPigeon(this.pigeon);
    }

}
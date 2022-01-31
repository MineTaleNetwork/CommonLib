package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.cache.type.RequestCache;

import java.util.concurrent.TimeUnit;

public class PartyRequestCache extends RequestCache {

    public PartyRequestCache() {
        super("party-requests", TimeUnit.MINUTES.toMillis(30L));
    }

}

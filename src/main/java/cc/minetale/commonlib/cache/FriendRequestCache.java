package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.cache.type.RequestCache;

import java.util.concurrent.TimeUnit;

public class FriendRequestCache extends RequestCache {

    public FriendRequestCache() {
        super("friend-requests", TimeUnit.DAYS.toMillis(7L));
    }

}

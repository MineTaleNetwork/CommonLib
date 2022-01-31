package cc.minetale.commonlib.util;

import cc.minetale.commonlib.cache.FriendRequestCache;
import cc.minetale.commonlib.cache.PartyCache;
import cc.minetale.commonlib.cache.PartyRequestCache;
import cc.minetale.commonlib.cache.ProfileCache;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Cache {

    @Getter private static FriendRequestCache friendRequestCache;
    @Getter private static PartyRequestCache partyRequestCache;
    @Getter private static PartyCache partyCache;
    @Getter private static ProfileCache profileCache;

    public static void init() {
        friendRequestCache = new FriendRequestCache();
        partyRequestCache = new PartyRequestCache();
        partyCache = new PartyCache();
        profileCache = new ProfileCache();
    }

}

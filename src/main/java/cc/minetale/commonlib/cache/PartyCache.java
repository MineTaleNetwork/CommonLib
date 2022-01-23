package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.party.Party;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.util.Redis;
import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.params.SetParams;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PartyCache {

//    public static CompletableFuture<Void> removeCache(UUID player, UUID target) {
//        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.del(
//                getKey(player.toString(), target.toString())
//        )));
//    }

//    public static CompletableFuture<Void> updateCache(UUID player, UUID target) {
//        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.set(
//                getKey(player.toString(), target.toString()),
//                "",
//                new SetParams().ex(TimeUnit.DAYS.toSeconds(7))
//        )));
//    }

    public static String getInviteKey(String partyUuid, String playerUuid) {
        return "minetale:party-invite-cache:" + partyUuid + ":" + playerUuid;
    }

    public static String getPartyKey(String party) {
        return "minetale:party-cache:" + party;
    }

}

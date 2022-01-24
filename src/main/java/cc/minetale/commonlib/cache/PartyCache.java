package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.party.Party;
import cc.minetale.commonlib.party.PartyRequest;
import cc.minetale.commonlib.util.Redis;
import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.params.SetParams;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PartyCache {

    public static CompletableFuture<Void> removePartyCache(UUID partyUuid) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.del(
                getPartyKey(partyUuid.toString())
        )));
    }

    public static CompletableFuture<Void> updatePartyCache(Party party) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> {
                    try {
                        return jedis.set(
                                getPartyKey(party.getPartyId().toString()),
                                CommonLib.getJsonMapper().writeValueAsString(party)
                        );
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
        ));
    }

    public static CompletableFuture<Set<PartyRequest>> getIncomingRequests(UUID player) {
        return new CompletableFuture<Set<PartyRequest>>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> {
                    var partyRequests = new HashSet<PartyRequest>();

                    for(var request : jedis.keys(getInviteKey("*", player.toString()))) {
                        var key = request.split(":");

                        partyRequests.add(new PartyRequest(
                                UUID.fromString(key[2]),
                                UUID.fromString(key[3]),
                                jedis.pttl(request)
                        ));
                    }

                    return partyRequests;
                }));
    }

    public static CompletableFuture<Boolean> hasRequest(UUID partyUuid, UUID playerUuid) {
        return new CompletableFuture<Boolean>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> jedis.exists(
                        getInviteKey(partyUuid.toString(), playerUuid.toString())
                )));
    }

    public static CompletableFuture<Void> removeRequest(UUID partyUuid, UUID playerUuid) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.del(
                getInviteKey(partyUuid.toString(), playerUuid.toString())
        )));
    }

    public static CompletableFuture<Void> addRequest(UUID partyUuid, UUID playerUuid) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.set(
                getInviteKey(partyUuid.toString(), playerUuid.toString()),
                "",
                new SetParams().ex(TimeUnit.MINUTES.toSeconds(30))
        )));
    }

    public static String getInviteKey(String partyUuid, String playerUuid) {
        return "minetale:party-invite-cache:" + partyUuid + ":" + playerUuid;
    }

    public static String getPartyKey(String party) {
        return "minetale:party-cache:" + party;
    }

}

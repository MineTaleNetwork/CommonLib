package cc.minetale.commonlib.party;

import cc.minetale.commonlib.util.Cache;
import cc.minetale.commonlib.util.JsonUtil;
import cc.minetale.commonlib.util.Redis;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class Party {

    private UUID partyId;
    private UUID leader;
    private List<UUID> partyMods;
    private List<UUID> partyMembers;

    /**
     * Default constructor used for Jackson.
     */
    public Party() {
    }

    public Party(UUID partyId) {
        this.partyId = partyId;
    }

    public static CompletableFuture<Party> getParty(UUID partyUuid) {
        return new CompletableFuture<Party>()
                .completeAsync(() -> {
                    try {
                        String party = Cache.getPartyCache().get(partyUuid).get();

                        if(party != null) {
                            return JsonUtil.readFromJson(party, Party.class);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public CompletableFuture<Void> disband() {
        // TODO -> Send disband messages

        var requestCache = Cache.getPartyRequestCache();

        try {
            var requests = requestCache.getOutgoing(partyId).get();

            Redis.runRedisCommand(jedis -> jedis.del(requests.stream()
                    .map(request -> requestCache.getKey(request.initiator(), request.target()))
                    .toList()
                    .toArray(new String[0])
            ));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Cache.getPartyCache().remove(partyId);
    }

    public List<UUID> getAllMembers() {
        return Stream.of(Collections.singletonList(leader), partyMods, partyMembers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}

package cc.minetale.commonlib.party;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.cache.PartyCache;
import cc.minetale.commonlib.util.Redis;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
                    String party;

                    try {
                        return ((party = Redis.runRedisCommand(jedis -> jedis.get(PartyCache.getPartyKey(partyUuid.toString())))) != null) ?
                                CommonLib.getJsonMapper().readValue(party, Party.class) : null;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public CompletableFuture<Void> disband() {
        // TODO -> Send disband messages

        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.del(
                PartyCache.getPartyKey(partyId.toString())
        )));
    }

    public List<UUID> getAllMembers() {
        return Stream.of(partyMods, partyMembers, List.of(leader))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}

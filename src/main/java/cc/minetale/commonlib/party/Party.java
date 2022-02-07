package cc.minetale.commonlib.party;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Cache;
import cc.minetale.commonlib.util.JsonUtil;
import cc.minetale.commonlib.util.Redis;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Getter
@Setter
public class Party {

    private UUID partyUuid;
    private Settings settings;
    private Set<PartyMember> members;

    /**
     * Default constructor used for Jackson.
     */
    public Party() {
    }

    public Party(UUID partyUuid) {
        this.partyUuid = partyUuid;
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
            // TODO -> THREAD BLOCKING
            var requests = requestCache.getOutgoing(partyUuid).get();

            Redis.runRedisCommand(jedis -> jedis.del(requests.stream()
                    .map(request -> requestCache.getKey(request.initiator(), request.target()))
                    .toList()
                    .toArray(new String[0])
            ));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Cache.getPartyCache().remove(partyUuid);
    }

    public CompletableFuture<InviteResponse> invitePlayer(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<InviteResponse>()
                .completeAsync(() -> {
                    try {
                        var cache = Cache.getPartyRequestCache();
                        var outgoing = cache.getOutgoing(player.getUuid()).get();

                        for(var member : members) {
                            if(member.player().equals(targetUuid)) {
                                return InviteResponse.ALREADY_IN_PARTY;
                            }
                        }

                        if (targetUuid.equals(playerUuid)) {
                            return InviteResponse.TARGET_IS_PLAYER;
                        }

                        if (outgoing.size() >= 100) {
                            return InviteResponse.MAXIMUM_REQUESTS;
                        }

                        if (cache.has(partyUuid, targetUuid).get()) {
                            return InviteResponse.REQUEST_EXIST;
                        }

                        if (!target.getOptionsProfile().isReceivingPartyRequests()) {
                            return InviteResponse.REQUESTS_TOGGLED;
                        }

                        if (player.isIgnoring(target)) {
                            return InviteResponse.TARGET_IGNORED;
                        }

                        if (target.isIgnoring(player)) {
                            return InviteResponse.PLAYER_IGNORED;
                        }

                        cache.update("", partyUuid, targetUuid);

                        return InviteResponse.SUCCESS;
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return InviteResponse.ERROR;
                });
    }

    public enum InviteResponse {
        SUCCESS,
        ERROR,
        REQUEST_EXIST,
        ALREADY_IN_PARTY,
        TARGET_IS_PLAYER,
        REQUESTS_TOGGLED,
        MAXIMUM_REQUESTS,
        PLAYER_IGNORED,
        TARGET_IGNORED
    }

    @Getter
    @Setter
    public static class Settings {

        private boolean partyMuted = false;
        private boolean inviteOnly = true;
        private boolean privateGames = false;
        private int maximumMembers = 24;

    }

}

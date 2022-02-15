package cc.minetale.commonlib.friend;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.cache.RequestCache;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Redis;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Friend {

    public static CompletableFuture<RemoveResponse> removeFriend(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<RemoveResponse>()
                .completeAsync(() -> {
                    var targetFriends = target.getFriends();
                    var playerFriends = player.getFriends();

                    if (!player.isFriends(target) || !target.isFriends(player)) {
                        return RemoveResponse.NOT_ADDED;
                    }

                    playerFriends.remove(targetUuid);
                    targetFriends.remove(playerUuid);

                    ProfileCache.updateProfile(player);
                    ProfileCache.updateProfile(target);

                    player.save();
                    target.save();

                    return RemoveResponse.SUCCESS;
                });
    }

    public static CompletableFuture<CancelResponse> removeRequest(UUID player, UUID target) {
        var cache = RequestCache.getFriendRequest();

        return new CompletableFuture<CancelResponse>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> {
                    var incomingRequests = jedis.smembers(cache.getIncomingKey(player));

                    if (!incomingRequests.contains(target.toString())) {
                        return CancelResponse.NO_REQUEST;
                    }

                    var pipeline = jedis.pipelined();

                    pipeline.srem(cache.getIncomingKey(player), target.toString());
                    pipeline.srem(cache.getOutgoingKey(target), player.toString());

                    pipeline.sync();

                    return CancelResponse.SUCCESS;
                }));
    }

    public static CompletableFuture<AddResponse> addRequest(UUID player, UUID target) {
        var cache = RequestCache.getFriendRequest();

        return new CompletableFuture<AddResponse>()
                .completeAsync(() -> {
                    try {
                        var cache = Cache.getFriendRequestCache();
                        var outgoing = cache.getOutgoing(player.getUuid()).get();

                        if (target.getFriends().contains(playerUuid) || player.getFriends().contains(targetUuid)) {
                            return AddResponse.ALREADY_FRIENDS;
                        }

                        if (targetUuid.equals(playerUuid)) {
                            return AddResponse.TARGET_IS_PLAYER;
                        }

                        if (outgoing.size() >= 100) {
                            return AddResponse.MAX_OUTGOING;
                        }


                        if (cache.has(playerUuid, targetUuid).get()) {
                            return AddResponse.REQUEST_EXIST;
                        }

                        if (cache.has(targetUuid, playerUuid).get()) {
                            return AddResponse.PENDING_REQUEST;
                        }

                        if (!target.getOptionsProfile().isReceivingFriendRequests()) {
                            return AddResponse.REQUESTS_TOGGLED;
                        }

                        if (player.isIgnoring(target)) {
                            return AddResponse.TARGET_IGNORED;
                        }

                        if (target.isIgnoring(player)) {
                            return AddResponse.PLAYER_IGNORED;
                        }

                        cache.update("", playerUuid, targetUuid);

                        return AddResponse.SUCCESS;
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return AddResponse.ERROR;
                });
    }

    public static CompletableFuture<AcceptResponse> acceptRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<AcceptResponse>()
                .completeAsync(() -> {
                    try {
                        var cache = Cache.getFriendRequestCache();

                        if (cache.has(targetUuid, playerUuid).get()) {
                            return AcceptResponse.NO_REQUEST;
                        }

                        cache.remove(targetUuid, playerUuid);

                        if (player.getFriends().size() >= 100) {
                            return AcceptResponse.PLAYER_MAX_FRIENDS;
                        }

                        if (target.getFriends().size() >= 100) {
                            return AcceptResponse.TARGET_MAX_FRIENDS;
                        }

                        if (player.isIgnoring(target)) {
                            return AcceptResponse.TARGET_IGNORED;
                        }

                        if (target.isIgnoring(player)) {
                            return AcceptResponse.PLAYER_IGNORED;
                        }

                        player.getFriends().add(targetUuid);
                        target.getFriends().add(playerUuid);

                        var profileCache = Cache.getProfileCache();

                        profileCache.updateProfile(player);
                        profileCache.updateProfile(target);

                        player.save();
                        target.save();

                        return AcceptResponse.SUCCESS;
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return AcceptResponse.ERROR;
                });
    }

    public enum RemoveResponse {
        SUCCESS,
        NOT_ADDED
    }

    public enum CancelResponse {
        SUCCESS,
        NO_REQUEST
    }

    public enum AddResponse {
        ALREADY_FRIENDS,
        TARGET_IS_PLAYER,
        MAX_OUTGOING,
        REQUEST_EXIST,
        PENDING_REQUEST,
        REQUESTS_TOGGLED,
        TARGET_IGNORED,
        PLAYER_IGNORED,
        SUCCESS,
        ERROR
    }

    public enum AcceptResponse {
        NO_REQUEST,
        PLAYER_MAX_FRIENDS,
        TARGET_MAX_FRIENDS,
        TARGET_IGNORED,
        PLAYER_IGNORED,
        SUCCESS,
        ERROR
    }

}

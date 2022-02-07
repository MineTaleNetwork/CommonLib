package cc.minetale.commonlib.friend;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Cache;

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

                    if(targetFriends.contains(playerUuid) || playerFriends.contains(targetUuid)) {
                        playerFriends.remove(targetUuid);
                        targetFriends.remove(playerUuid);

                        var cache = Cache.getProfileCache();

                        cache.updateProfile(player);
                        cache.updateProfile(target);

                        player.save();
                        target.save();

                        return RemoveResponse.SUCCESS;
                    } else {
                        return RemoveResponse.NOT_ADDED;
                    }
                });
    }

    public static CompletableFuture<CancelResponse> denyRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<CancelResponse>()
                .completeAsync(() -> {
                    try {
                        var cache = Cache.getFriendRequestCache();
                        var hasRequest = cache.has(targetUuid, playerUuid).get();

                        if(hasRequest) {
                            cache.remove(targetUuid, playerUuid).get();

                            return CancelResponse.SUCCESS;
                        } else {
                            return CancelResponse.NO_REQUEST;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return CancelResponse.ERROR;
                });
    }

    public enum RemoveResponse {
        SUCCESS,
        NOT_ADDED
    }

    public static CompletableFuture<CancelResponse> cancelRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<CancelResponse>()
                .completeAsync(() -> {
                    try {
                        var cache = Cache.getFriendRequestCache();
                        var hasRequest = cache.has(playerUuid, targetUuid).get();

                        if(hasRequest) {
                            cache.remove(playerUuid, targetUuid).get();

                            return CancelResponse.SUCCESS;
                        } else {
                            return CancelResponse.NO_REQUEST;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return CancelResponse.ERROR;
                });
    }

    public enum CancelResponse {
        SUCCESS,
        ERROR,
        NO_REQUEST
    }

    public static CompletableFuture<AddResponse> addRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

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

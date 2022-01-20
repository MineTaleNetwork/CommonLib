package cc.minetale.commonlib.friend;

import cc.minetale.commonlib.cache.FriendCache;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.profile.Profile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public record FriendRequest(UUID player, UUID target, long ttl) {

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

                        ProfileCache.updateProfile(player);
                        ProfileCache.updateProfile(target);

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
                        var hasRequest = FriendCache.hasRequest(targetUuid, playerUuid).get();

                        if(hasRequest) {
                            FriendCache.removeCache(targetUuid, playerUuid).get();

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

    public static CompletableFuture<CancelResponse> cancelRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<CancelResponse>()
                .completeAsync(() -> {
                    try {
                        var hasRequest = FriendCache.hasRequest(playerUuid, targetUuid).get();

                        if(hasRequest) {
                            FriendCache.removeCache(playerUuid, targetUuid).get();

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

    public static CompletableFuture<AddResponse> addRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<AddResponse>()
                .completeAsync(() -> {
                    try {
                        var outgoing = FriendCache.getOutgoingRequests(player.getUuid()).get();

                        if (target.getFriends().contains(playerUuid) || player.getFriends().contains(targetUuid)) {
                            return AddResponse.ALREADY_FRIENDS;
                        }

                        if (targetUuid.equals(playerUuid)) {
                            return AddResponse.TARGET_IS_PLAYER;
                        }

                        if (outgoing.size() >= 100) {
                            return AddResponse.MAXIMUM_REQUESTS;
                        }

                        if (!FriendCache.hasRequest(playerUuid, targetUuid).get()) {
                            if (FriendCache.hasRequest(targetUuid, playerUuid).get()) {
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

                            FriendCache.updateCache(playerUuid, targetUuid);

                            return AddResponse.SUCCESS;
                        } else {
                            return AddResponse.REQUEST_EXIST;
                        }
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
                        var request = FriendCache.hasRequest(targetUuid, playerUuid).get();

                        if (request) {
                            FriendCache.removeCache(targetUuid, playerUuid);

                            if (player.getFriends().size() >= 100) {
                                return AcceptResponse.PLAYER_MAXIMUM_FRIENDS;
                            }

                            if (target.getFriends().size() >= 100) {
                                return AcceptResponse.TARGET_MAXIMUM_FRIENDS;
                            }

                            if (player.isIgnoring(target)) {
                                return AcceptResponse.TARGET_IGNORED;
                            }

                            if (target.isIgnoring(player)) {
                                return AcceptResponse.PLAYER_IGNORED;
                            }

                            player.getFriends().add(targetUuid);
                            target.getFriends().add(playerUuid);

                            System.out.println(player.getFriends());
                            System.out.println(target.getFriends());

                            ProfileCache.updateProfile(player);
                            ProfileCache.updateProfile(target);

                            return AcceptResponse.SUCCESS;
                        } else {
                            return AcceptResponse.NO_REQUEST;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return AcceptResponse.ERROR;
                });
    }

}

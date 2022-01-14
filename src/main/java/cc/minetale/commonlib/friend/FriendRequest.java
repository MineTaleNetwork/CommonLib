package cc.minetale.commonlib.friend;

import cc.minetale.commonlib.cache.FriendCache;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.profile.Profile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FriendRequest {

    public static CompletableFuture<AddResponse> addRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<AddResponse>().completeAsync(() -> {
            try {
                if(!FriendCache.hasRequest(playerUuid, targetUuid).get()) {
                    if(FriendCache.hasRequest(targetUuid, playerUuid).get()) {
                        return AddResponse.PENDING_REQUEST;
                    }

                    if(!player.getOptionsProfile().isReceivingFriendRequests()) {
                        return AddResponse.REQUESTS_TOGGLED;
                    }

                    if(player.isIgnoring(target)) {
                        return AddResponse.TARGET_IS_IGNORED;
                    }

                    if(target.isIgnoring(player)) {
                        return AddResponse.PLAYER_IS_IGNORED;
                    }

                    FriendCache.updateCache(playerUuid, targetUuid);

                    return AddResponse.SUCCESS;
                } else {
                    return AddResponse.REQUEST_EXIST;
                }
            } catch (InterruptedException | ExecutionException ignored) {}

            return AddResponse.ERROR;
        });
    }

    public static CompletableFuture<AcceptResponse> acceptRequest(Profile player, Profile target) {
        var playerUuid = player.getUuid();
        var targetUuid = target.getUuid();

        return new CompletableFuture<AcceptResponse>().completeAsync(() -> {
            try {
                var request = FriendCache.hasRequest(targetUuid, playerUuid).get();

                if(request) {
                    FriendCache.removeCache(targetUuid, playerUuid);

                    if(player.isIgnoring(target)) {
                        return AcceptResponse.TARGET_IS_IGNORED;
                    }

                    if(target.isIgnoring(player)) {
                        return AcceptResponse.PLAYER_IS_IGNORED;
                    }

                    player.getFriends().add(targetUuid);
                    target.getFriends().add(playerUuid);

                    ProfileCache.updateProfile(player);
                    ProfileCache.updateProfile(target);

                    return AcceptResponse.SUCCESS;
                } else {
                    return AcceptResponse.NO_REQUEST;
                }
            } catch (InterruptedException | ExecutionException ignored) {}

            return AcceptResponse.ERROR;
        });
    }

}

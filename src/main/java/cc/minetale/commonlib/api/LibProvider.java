package cc.minetale.commonlib.api;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.punishment.Punishment;

public interface LibProvider {

    default void addGrant(Grant grant) {}
    default void removeGrant(Grant grant) {}
    default void expireGrant(Grant grant) {}

    default void addPunishment(Punishment punishment) {}
    default void removePunishment(Punishment punishment) {}
    default void expirePunishment(Punishment punishment) {}

}

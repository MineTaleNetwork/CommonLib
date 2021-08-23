package cc.minetale.commonlib.modules.api;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.punishment.Punishment;

public interface APIListener {

    default void grantAdd(Grant grant) {}

    default void grantExpire(Grant grant) {}

    default void grantRemove(Grant grant) {}

    default void punishmentAdd(Punishment punishment) {}

    default void punishmentExpire(Punishment punishment) {}

    default void punishmentRemove(Punishment punishment) {}

}

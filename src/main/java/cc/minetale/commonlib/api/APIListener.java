package cc.minetale.commonlib.api;

public interface APIListener {

    default void grantAdd(Grant grant) {}

    default void grantExpire(Grant grant) {}

    default void grantRemove(Grant grant) {}

    default void punishmentAdd(Punishment punishment) {}

    default void punishmentExpire(Punishment punishment) {}

    default void punishmentRemove(Punishment punishment) {}

}

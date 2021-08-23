package cc.minetale.commonlib.modules.pigeon;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.modules.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.modules.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.punishment.PunishmentExpirePayload;
import cc.minetale.commonlib.modules.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;

@PayloadListener
public class Listeners implements Listener {

    @PayloadHandler
    public void onGrantAdd(GrantAddPayload payload) {
        Grant grant = Grant.getGrant(payload.getGrant());

        if(grant != null)
            CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantAdd(grant));
    }

    @PayloadHandler
    public void onGrantRemove(GrantRemovePayload payload) {
        Grant grant = Grant.getGrant(payload.getGrant());

        if(grant != null)
            CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantRemove(grant));
    }

    @PayloadHandler
    public void onGrantExpire(GrantExpirePayload payload) {
        Grant grant = Grant.getGrant(payload.getGrant());

        if(grant != null)
            CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantExpire(grant));
    }

    @PayloadHandler
    public void onPunishmentAdd(PunishmentAddPayload payload) {
        Punishment punishment = Punishment.getPunishment(payload.getPunishment());

        if(punishment != null)
            CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentAdd(punishment));
    }

    @PayloadHandler
    public void onPunishmentRemove(PunishmentRemovePayload payload) {
        Punishment punishment = Punishment.getPunishment(payload.getPunishment());

        if(punishment != null)
            CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentRemove(punishment));
    }

    @PayloadHandler
    public void onPunishmentExpire(PunishmentExpirePayload payload) {
        Punishment punishment = Punishment.getPunishment(payload.getPunishment());

        if(punishment != null)
            CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentExpire(punishment));
    }

}

package cc.minetale.commonlib.modules.pigeon.payloads.punishment;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class PunishmentExpirePayload extends BasePayload {

    @Transmit Profile profile;

    @Transmit String punishment;

    public PunishmentExpirePayload() {
        payloadId = "punishmentExpirePayload";
    }

    public PunishmentExpirePayload(String punishment) {
        this();
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}

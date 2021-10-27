package cc.minetale.commonlib.pigeon.payloads.punishment;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class PunishmentAddPayload extends BasePayload {

    @Transmit Profile profile;

    @Transmit String punishment;

    public PunishmentAddPayload() {
        payloadId = "punishmentAddPayload";
    }

    public PunishmentAddPayload(Profile profile, String punishment) {
        this();
        this.profile = profile;
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.modules.pigeon.payloads.punishment;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class PunishmentAddPayload extends BasePayload {

    @Transmit Profile profile;  // Transmit the most up-to-date profile with the payload,
                                // so every single listener won't have to request the profile everytime it receives the payload.
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

package cc.minetale.commonlib.pigeon.payloads.profile;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ProfileUpdatePayload extends BasePayload {

    @Transmit UUID player;

    public ProfileUpdatePayload() {
        payloadId = "profileUpdatePayload";
    }

    public ProfileUpdatePayload(UUID player) {
        this();
        this.player = player;
    }

    @Override
    public void receive() {}

}
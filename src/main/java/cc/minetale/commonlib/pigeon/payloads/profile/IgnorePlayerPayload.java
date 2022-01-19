package cc.minetale.commonlib.pigeon.payloads.profile;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class IgnorePlayerPayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit UUID player;

    public IgnorePlayerPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public IgnorePlayerPayload(UUID initiator, UUID player) {
        this();
        this.initiator = initiator;
        this.player = player;
    }

    @Override
    public void receive() {}

}

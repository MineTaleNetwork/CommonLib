package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyChatPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String message;

    public PartyChatPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PartyChatPayload(UUID player, String message) {
        this();
        this.player = player;
        this.message = message;
    }

    @Override
    public void receive() {}
}

package cc.minetale.commonlib.pigeon.payloads.minecraft;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class MessagePlayerPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String prefix;
    @Transmit String message;

    public MessagePlayerPayload() {
        payloadId = "messagePlayerPayload";
    }

    public MessagePlayerPayload(UUID player, String prefix, String message) {
        this();
        this.player = player;
        this.prefix = prefix;
        this.message = message;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.modules.pigeon.payloads.minecraft;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@Getter @Payload
public class MessagePlayerPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit Component message;

    public MessagePlayerPayload() {
        payloadId = "messagePlayerPayload";
    }

    public MessagePlayerPayload(UUID player, Component message) {
        this();
        this.player = player;
        this.message = message;
    }

    @Override
    public void receive() {}

}

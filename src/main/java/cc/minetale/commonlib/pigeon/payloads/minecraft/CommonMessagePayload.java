package cc.minetale.commonlib.pigeon.payloads.minecraft;

import cc.minetale.commonlib.messages.CommonMessage;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class CommonMessagePayload extends BasePayload {

    @Transmit UUID player;
    @Transmit CommonMessage message;
    @Transmit String[] strings;

    public CommonMessagePayload() {
        payloadId = "messagePlayerPayload";
    }

    public CommonMessagePayload(UUID player, CommonMessage message, String... strings) {
        this();
        this.player = player;
        this.message = message;
        this.strings = strings;
    }

    @Override
    public void receive() {}

}

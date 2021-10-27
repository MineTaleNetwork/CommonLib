package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyChatPayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit String message;

    public PartyChatPayload() {
        payloadId = "partyChatPayload";
    }

    public PartyChatPayload(UUID initiator, String message) {
        this();
        this.initiator = initiator;
        this.message = message;
    }

    @Override
    public void receive() {
    }

}

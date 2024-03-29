package cc.minetale.commonlib.pigeon.payloads.conversation;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ConversationMessagePayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit UUID target;
    @Transmit String message;

    public ConversationMessagePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public ConversationMessagePayload(UUID initiator, UUID target, String message) {
        this();
        this.initiator = initiator;
        this.target = target;
        this.message = message;
    }

    @Override
    public void receive() {}

}

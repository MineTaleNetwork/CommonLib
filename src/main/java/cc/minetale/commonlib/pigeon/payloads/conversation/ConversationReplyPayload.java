package cc.minetale.commonlib.pigeon.payloads.conversation;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ConversationReplyPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String message;

    public ConversationReplyPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public ConversationReplyPayload(UUID player, String message) {
        this();
        this.player = player;
        this.message = message;
    }

    @Override
    public void receive() {}

}
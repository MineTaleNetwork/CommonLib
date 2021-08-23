package cc.minetale.commonlib.modules.pigeon.payloads.atom;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.RequestConstructor;
import cc.minetale.pigeon.annotations.ResponseConstructor;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.feedback.RequiredState;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;

@Getter @Payload
public class AtomPlayerRequestPayload extends FeedbackPayload {

    public AtomPlayerRequestPayload() {
        this.payloadId = "atomPlayerRequestPayload";
        this.payloadTimeout = 10000;
    }

    @Transmit UUID player;

    @RequestConstructor
    public AtomPlayerRequestPayload(UUID player, Consumer<AtomPlayerRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.feedback = feedback;
    }

    @Transmit(direction = RequiredState.RESPONSE) boolean isOnline;

    @ResponseConstructor
    public AtomPlayerRequestPayload(boolean isOnline) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.isOnline = isOnline;
    }

    @Override
    public void receive() {}

}

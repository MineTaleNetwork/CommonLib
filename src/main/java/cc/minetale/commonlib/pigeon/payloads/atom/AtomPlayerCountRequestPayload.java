package cc.minetale.commonlib.pigeon.payloads.atom;

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
public class AtomPlayerCountRequestPayload extends FeedbackPayload {

    public AtomPlayerCountRequestPayload() {
        this.payloadId = "atomPlayerCountRequestPayload";
        this.payloadTimeout = 10000;
    }

    @RequestConstructor
    public AtomPlayerCountRequestPayload(Consumer<AtomPlayerCountRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.feedback = feedback;
    }

    @Transmit(direction = RequiredState.RESPONSE) Integer players;

    @ResponseConstructor
    public AtomPlayerCountRequestPayload(Integer players) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.players = players;
    }

    @Override
    public void receive() {}

}

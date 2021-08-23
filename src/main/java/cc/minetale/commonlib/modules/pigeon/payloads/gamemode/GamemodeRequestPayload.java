package cc.minetale.commonlib.modules.pigeon.payloads.gamemode;

import cc.minetale.commonlib.modules.network.Gamemode;
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
public class GamemodeRequestPayload extends FeedbackPayload {

    public GamemodeRequestPayload() {
        this.payloadId = "gamemodeRequestPayload";
        this.payloadTimeout = 10000;
    }

    //Request

    @Transmit(direction = RequiredState.REQUEST) String name;

    @RequestConstructor
    public GamemodeRequestPayload(String name, Consumer<GamemodeRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.name = name;

        this.feedback = feedback;
    }

    //Response

    @Transmit(direction = RequiredState.RESPONSE) Gamemode gamemode;

    @ResponseConstructor
    public GamemodeRequestPayload(Gamemode gamemode) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.gamemode = gamemode;
    }

    @Override
    public void receive() {}

}

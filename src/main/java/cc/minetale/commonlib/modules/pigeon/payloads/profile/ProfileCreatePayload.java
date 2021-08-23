package cc.minetale.commonlib.modules.pigeon.payloads.profile;


import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.profile.ProfileQueryResult;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.RequestConstructor;
import cc.minetale.pigeon.annotations.ResponseConstructor;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.feedback.RequiredState;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@Getter @Payload
public class ProfileCreatePayload extends FeedbackPayload {

    public ProfileCreatePayload() {
        this.payloadId = "profileCreatePayload";
        this.payloadTimeout = 10000;
    }

    //Request

    @Transmit(direction = RequiredState.REQUEST) Profile profile;

    @RequestConstructor
    public ProfileCreatePayload(Profile profile, Consumer<ProfileCreatePayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.profile = profile;

        this.feedback = feedback;
    }

    //Response

    @Nullable @Transmit(direction = RequiredState.RESPONSE) ProfileQueryResult result;

    @ResponseConstructor
    public ProfileCreatePayload(ProfileQueryResult result) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.result = result;
    }

    @Override
    public void receive() {}

}
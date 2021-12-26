package cc.minetale.commonlib.pigeon.payloads.profile;


import cc.minetale.commonlib.api.Profile;
import cc.minetale.commonlib.api.ProfileQueryResult;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.RequestConstructor;
import cc.minetale.pigeon.annotations.ResponseConstructor;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.feedback.RequiredState;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Used for requesting a new profile or replacing the current one with a newer/more up-to-date one.
 */
@Getter @Payload
public class ProfileRequestPayload extends FeedbackPayload {

    public ProfileRequestPayload() {
        this.payloadId = "profileRequestPayload";
        this.payloadTimeout = 10000;
    }

    @Transmit Type type;

    @Nullable @Transmit(direction = RequiredState.REQUEST) String name;
    @Nullable @Transmit(direction = RequiredState.REQUEST) UUID id;

    @RequestConstructor
    public ProfileRequestPayload(String name, UUID id, Consumer<ProfileRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.type = Type.SINGLE;

        this.name = name;
        this.id = id;

        this.feedback = feedback;
    }

    @RequestConstructor
    public ProfileRequestPayload(String name, Consumer<ProfileRequestPayload> feedback) {
        this(name, null, feedback);
    }

    @RequestConstructor
    public ProfileRequestPayload(UUID id, Consumer<ProfileRequestPayload> feedback) {
        this(null, id, feedback);
    }

    @Nullable @Transmit(direction = RequiredState.REQUEST) List<String> names;
    @Nullable @Transmit(direction = RequiredState.REQUEST) List<UUID> ids;

    @Transmit(direction = RequiredState.REQUEST)
    @Accessors(fluent = true)
    boolean areConnected;

    @RequestConstructor
    public ProfileRequestPayload(Map<UUID, String> info, Consumer<ProfileRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.type = Type.BULK;
        this.areConnected = true;

        this.names = new ArrayList<>();
        this.ids = new ArrayList<>();

        for(Map.Entry<UUID, String> ent : info.entrySet()) {
            this.ids.add(ent.getKey());
            this.names.add(ent.getValue());
        }

        this.feedback = feedback;
    }

    @RequestConstructor
    public ProfileRequestPayload(@Nullable List<String> names, @Nullable List<UUID> ids, Consumer<ProfileRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.type = Type.BULK;
        this.areConnected = false;

        this.names = names;
        this.ids = ids;

        this.feedback = feedback;
    }

    public static ProfileRequestPayload bulkRequestByNames(List<String> names, Consumer<ProfileRequestPayload> feedback) {
        return new ProfileRequestPayload(names, null, feedback);
    }

    public static ProfileRequestPayload bulkRequestByIds(List<UUID> ids, Consumer<ProfileRequestPayload> feedback) {
        return new ProfileRequestPayload(null, ids, feedback);
    }

    @Nullable @Transmit(direction = RequiredState.RESPONSE) ProfileQueryResult result;

    @Nullable @Transmit(direction = RequiredState.RESPONSE) List<Profile> profiles;

    @ResponseConstructor
    public ProfileRequestPayload(ProfileQueryResult result, List<Profile> profiles) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.result = result;
        this.profiles = profiles;
    }

    @Override
    public void receive() {}

    public enum Type {
        SINGLE,
        BULK
    }

}
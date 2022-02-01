package cc.minetale.commonlib.pigeon.payloads.friend;

import cc.minetale.commonlib.profile.MiniProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class FriendRemovePayload extends BasePayload {

    @Transmit MiniProfile initiator;
    @Transmit UUID target;

    public FriendRemovePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public FriendRemovePayload(Profile initiator, UUID target) {
        this();
        this.initiator = MiniProfile.of(initiator);
        this.target = target;
    }

    @Override
    public void receive() {}

}
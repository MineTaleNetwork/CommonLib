package cc.minetale.commonlib.pigeon.payloads.friend;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class FriendJoinedPayload extends BasePayload {

    @Transmit UUID player;

    public FriendJoinedPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public FriendJoinedPayload(UUID player) {
        this();
        this.player = player;
    }

    @Override
    public void receive() {}

}
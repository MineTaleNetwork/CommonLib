package cc.minetale.commonlib.pigeon.payloads.friend;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class FriendLeftPayload extends BasePayload {

    @Transmit UUID player;

    public FriendLeftPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public FriendLeftPayload(UUID player) {
        this();
        this.player = player;
    }

    @Override
    public void receive() {}

}
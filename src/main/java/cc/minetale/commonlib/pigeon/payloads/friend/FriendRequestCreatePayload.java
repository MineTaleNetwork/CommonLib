package cc.minetale.commonlib.pigeon.payloads.friend;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class FriendRequestCreatePayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit UUID target;

    public FriendRequestCreatePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public FriendRequestCreatePayload(UUID initiator, UUID target) {
        this();
        this.initiator = initiator;
        this.target = target;
    }

    @Override
    public void receive() {}

}
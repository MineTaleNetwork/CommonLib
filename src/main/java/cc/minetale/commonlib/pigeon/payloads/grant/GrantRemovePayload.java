package cc.minetale.commonlib.pigeon.payloads.grant;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class GrantRemovePayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String grant;

    public GrantRemovePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public GrantRemovePayload(UUID player, String grant) {
        this();
        this.player = player;
        this.grant = grant;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.pigeon.payloads.grant;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class GrantExpirePayload extends BasePayload {

    @Transmit
    UUID playerUuid;

    @Transmit String grant;

    public GrantExpirePayload() {
        payloadId = "grantExpirePayload";
    }

    public GrantExpirePayload(UUID playerUuid, String grant) {
        this();
        this.playerUuid = playerUuid;
        this.grant = grant;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.pigeon.payloads.grant;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class GrantExpirePayload extends BasePayload {

    @Transmit Profile profile;

    @Transmit String grant;

    public GrantExpirePayload() {
        payloadId = "grantExpirePayload";
    }

    public GrantExpirePayload(Profile profile, String grant) {
        this();
        this.profile = profile;
        this.grant = grant;
    }

    @Override
    public void receive() {}

}
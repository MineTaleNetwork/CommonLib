package cc.minetale.commonlib.modules.pigeon.payloads.grant;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class GrantRemovePayload extends BasePayload {

    @Transmit Profile profile;

    @Transmit String grant;

    public GrantRemovePayload() {
        payloadId = "grantRemovePayload";
    }

    public GrantRemovePayload(Profile profile, String grant) {
        this();
        this.profile = profile;
        this.grant = grant;
    }

    @Override
    public void receive() {}

}

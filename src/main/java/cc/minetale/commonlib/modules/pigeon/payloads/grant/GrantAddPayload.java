package cc.minetale.commonlib.modules.pigeon.payloads.grant;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class GrantAddPayload extends BasePayload {

    @Transmit Profile profile;  // Transmit the most up-to-date profile with the payload,
                                // so every single listener won't have to request the profile everytime it receives the payload.
    @Transmit String grant;

    public GrantAddPayload() {
        payloadId = "grantAddPayload";
    }

    public GrantAddPayload(Profile profile, String grant) {
        this();
        this.profile = profile;
        this.grant = grant;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ProxyPlayerSwitchPayload extends BasePayload {

    @Transmit Profile profile;
    @Transmit String serverFrom;
    @Transmit String serverTo;

    public ProxyPlayerSwitchPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public ProxyPlayerSwitchPayload(Profile profile, String serverFrom, String serverTo) {
        this();
        this.profile = profile;
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ProxyPlayerDisconnectPayload extends BasePayload {

    @Transmit Profile profile;
    @Transmit String server;

    public ProxyPlayerDisconnectPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public ProxyPlayerDisconnectPayload(Profile profile, String server) {
        this();
        this.profile = profile;
        this.server = server;
    }

    @Override
    public void receive() {}

}

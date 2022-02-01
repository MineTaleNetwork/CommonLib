package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.commonlib.profile.MiniProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ProxyPlayerConnectPayload extends BasePayload {

    @Transmit MiniProfile player;
    @Transmit String server;

    public ProxyPlayerConnectPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public ProxyPlayerConnectPayload(Profile player, String server) {
        this();
        this.player = MiniProfile.of(player);
        this.server = server;
    }

    @Override
    public void receive() {}

}

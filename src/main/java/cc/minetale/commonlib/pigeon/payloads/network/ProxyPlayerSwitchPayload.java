package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.commonlib.profile.MiniProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ProxyPlayerSwitchPayload extends BasePayload {

    @Transmit MiniProfile player;
    @Transmit String serverFrom;
    @Transmit String serverTo;

    public ProxyPlayerSwitchPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public ProxyPlayerSwitchPayload(Profile player, String serverFrom, String serverTo) {
        this();
        this.player = MiniProfile.of(player);
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    @Override
    public void receive() {}

}

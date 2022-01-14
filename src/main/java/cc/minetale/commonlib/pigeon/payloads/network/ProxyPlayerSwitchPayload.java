package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ProxyPlayerSwitchPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String serverFrom;
    @Transmit String serverTo;

    public ProxyPlayerSwitchPayload() {
        payloadId = "proxyPlayerSwitchPayload";
    }

    public ProxyPlayerSwitchPayload(UUID player, String serverFrom, String serverTo) {
        this();
        this.player = player;
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    @Override
    public void receive() {}

}

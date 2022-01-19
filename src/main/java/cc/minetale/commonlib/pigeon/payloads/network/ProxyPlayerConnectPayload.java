package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ProxyPlayerConnectPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String server;

    public ProxyPlayerConnectPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public ProxyPlayerConnectPayload(UUID player, String server) {
        this();
        this.player = player;
        this.server = server;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ProxyPlayerDisconnectPayload extends BasePayload {

    @Transmit UUID uuid;
    @Transmit String player;
    @Transmit String server;

    public ProxyPlayerDisconnectPayload() {
        payloadId = "proxyPlayerDisconnectPayload";
    }

    public ProxyPlayerDisconnectPayload(UUID uuid, String player, String server) {
        this();
        this.uuid = uuid;
        this.player = player;
        this.server = server;
    }

    @Override
    public void receive() {}

}

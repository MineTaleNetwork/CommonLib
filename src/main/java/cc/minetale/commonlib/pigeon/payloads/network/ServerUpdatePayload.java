package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.commonlib.network.server.Server;
import cc.minetale.commonlib.network.server.ServerAction;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ServerUpdatePayload extends BasePayload {

    @Transmit Server server;
    @Transmit ServerAction action;

    public ServerUpdatePayload() {
        payloadId = "serverUpdatePayload";
    }

    public ServerUpdatePayload(Server server, ServerAction action) {
        this();
        this.server = server;
        this.action = action;
    }

    @Override
    public void receive() {}

}

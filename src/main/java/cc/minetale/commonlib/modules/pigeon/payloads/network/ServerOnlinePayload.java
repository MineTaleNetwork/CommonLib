package cc.minetale.commonlib.modules.pigeon.payloads.network;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ServerOnlinePayload extends BasePayload {

    @Transmit String name;

    public ServerOnlinePayload() {
        payloadId = "serverOnlinePayload";
    }

    public ServerOnlinePayload(String name) {
        this();
        this.name = name;
    }

    @Override
    public void receive() {}

}

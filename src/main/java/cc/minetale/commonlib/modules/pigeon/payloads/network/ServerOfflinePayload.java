package cc.minetale.commonlib.modules.pigeon.payloads.network;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ServerOfflinePayload extends BasePayload {

    @Transmit String name;

    public ServerOfflinePayload() {
        payloadId = "serverOfflinePayload";
    }

    public ServerOfflinePayload(String name) {
        this();
        this.name = name;
    }

    @Override
    public void receive() {}

}
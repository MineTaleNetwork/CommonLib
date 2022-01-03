package cc.minetale.commonlib.pigeon.payloads.deployment;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class RegisterProxyServerPayload extends BasePayload {

    @Transmit String name;
    @Transmit String host;
    @Transmit int port;

    public RegisterProxyServerPayload() {
        payloadId = "registerProxyServerPayload";
    }

    public RegisterProxyServerPayload(String name, String host, int port) {
        this();
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public void receive() {}

}
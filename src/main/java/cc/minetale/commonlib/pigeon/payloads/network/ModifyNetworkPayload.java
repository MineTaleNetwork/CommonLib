package cc.minetale.commonlib.pigeon.payloads.network;

import cc.minetale.commonlib.pigeon.enums.NetworkSetting;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ModifyNetworkPayload extends BasePayload {

    @Transmit NetworkSetting setting;

    public ModifyNetworkPayload() {
        payloadId = "modifyNetworkPayload";
    }

    public ModifyNetworkPayload(NetworkSetting setting) {
        this();
        this.setting = setting;
    }

    @Override
    public void receive() {}

}

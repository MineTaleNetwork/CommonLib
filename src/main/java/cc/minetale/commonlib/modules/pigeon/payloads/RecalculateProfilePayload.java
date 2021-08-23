package cc.minetale.commonlib.modules.pigeon.payloads;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class RecalculateProfilePayload extends BasePayload {

    @Transmit UUID profile;

    public RecalculateProfilePayload() {
        payloadId = "recalculateProfile";
    }

    public RecalculateProfilePayload(UUID profile) {
        this();
        this.profile = profile;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.modules.pigeon.payloads.party;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartySummonPayload extends BasePayload {

    @Transmit UUID initiator;

    public PartySummonPayload() {
        payloadId = "partySummonPayload";
    }

    public PartySummonPayload(UUID initiator) {
        this();
        this.initiator = initiator;
    }

    @Override
    public void receive() {
    }

}

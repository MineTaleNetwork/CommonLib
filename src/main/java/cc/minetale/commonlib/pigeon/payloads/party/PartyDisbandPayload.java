package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyDisbandPayload extends BasePayload {

    @Transmit UUID initiator;

    public PartyDisbandPayload() {
        payloadId = "partyDisbandPayload";
    }

    public PartyDisbandPayload(UUID initiator) {
        this();
        this.initiator = initiator;
    }

    @Override
    public void receive() {
    }

}

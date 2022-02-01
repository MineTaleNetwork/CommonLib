package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.commonlib.party.Party;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class PartyDisbandPayload extends BasePayload {

    @Transmit Party party;

    public PartyDisbandPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PartyDisbandPayload(Party party) {
        this();
        this.party = party;
    }

    @Override
    public void receive() {}
}

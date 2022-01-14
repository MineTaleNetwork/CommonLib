package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyDisbandPayload extends BasePayload {

    @Transmit UUID player;

    public PartyDisbandPayload() {
        payloadId = "partyDisbandPayload";
    }

    public PartyDisbandPayload(UUID player) {
        this();
        this.player = player;
    }

    @Override
    public void receive() {}
}

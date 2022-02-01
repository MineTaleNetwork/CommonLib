package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.commonlib.party.Party;
import cc.minetale.commonlib.profile.MiniProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyRequestCreatePayload extends BasePayload {

    @Transmit Party party;
    @Transmit MiniProfile initiator;
    @Transmit MiniProfile target;

    public PartyRequestCreatePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PartyRequestCreatePayload(Party party, Profile initiator, Profile target) {
        this();
        this.party = party;
        this.initiator = MiniProfile.of(initiator);
        this.target = MiniProfile.of(target);
    }

    @Override
    public void receive() {}
}

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
public class PartyKickPayload extends BasePayload {

    @Transmit Party party;
    @Transmit MiniProfile player;

    public PartyKickPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PartyKickPayload(Party party, Profile player) {
        this();
        this.party = party;
        this.player = MiniProfile.of(player);
    }

    @Override
    public void receive() {}
}

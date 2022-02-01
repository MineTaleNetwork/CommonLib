package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.commonlib.party.Party;
import cc.minetale.commonlib.party.PartyMember;
import cc.minetale.commonlib.profile.MiniProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter
@Payload
public class PartyRoleChangePayload extends BasePayload {

    @Transmit Party party;
    @Transmit MiniProfile player;
    @Transmit PartyMember.Role newRole;
    @Transmit PartyMember.Role oldRole;

    public PartyRoleChangePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PartyRoleChangePayload(Party party, Profile player, PartyMember.Role newRole, PartyMember.Role oldRole) {
        this();
        this.party = party;
        this.player = MiniProfile.of(player);
        this.newRole = newRole;
        this.oldRole = oldRole;
    }

    @Override
    public void receive() {}

}

package cc.minetale.commonlib.pigeon.payloads.party;

import cc.minetale.commonlib.party.Party;
import cc.minetale.commonlib.profile.MiniProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class PartyChatPayload extends BasePayload {

    @Transmit Party party;
    @Transmit MiniProfile profile;
    @Transmit String message;

    public PartyChatPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PartyChatPayload(Party party, Profile profile, String message) {
        this();
        this.party = party;
        this.profile = MiniProfile.of(profile);;
        this.message = message;
    }

    @Override
    public void receive() {}
}

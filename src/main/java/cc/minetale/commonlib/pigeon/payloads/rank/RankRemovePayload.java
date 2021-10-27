package cc.minetale.commonlib.pigeon.payloads.rank;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class RankRemovePayload extends BasePayload {

    @Transmit UUID rank;

    public RankRemovePayload() {
        payloadId = "rankRemovePayload";
    }

    public RankRemovePayload(UUID rank) {
        this();
        this.rank = rank;
    }

    @Override
    public void receive() {
    }

}

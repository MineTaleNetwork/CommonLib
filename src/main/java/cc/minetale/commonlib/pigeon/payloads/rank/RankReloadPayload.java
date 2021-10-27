package cc.minetale.commonlib.pigeon.payloads.rank;

import cc.minetale.commonlib.rank.Rank;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class RankReloadPayload extends BasePayload {

    @Transmit UUID rank;

    public RankReloadPayload() {
        payloadId = "rankReloadPayload";
    }

    public RankReloadPayload(Rank rank) {
        this();
        this.rank = rank.getUuid();
    }

    @Override
    public void receive() {
    }

}

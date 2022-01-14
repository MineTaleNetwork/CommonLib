package cc.minetale.commonlib.pigeon.payloads.punishment;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PunishmentRemovePayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String punishment;

    public PunishmentRemovePayload() {
        payloadId = "punishmentRemovePayload";
    }

    public PunishmentRemovePayload(UUID player, String punishment) {
        this();
        this.player = player;
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}

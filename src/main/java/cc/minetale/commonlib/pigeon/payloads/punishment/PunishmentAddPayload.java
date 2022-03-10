package cc.minetale.commonlib.pigeon.payloads.punishment;

import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PunishmentAddPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit Punishment punishment;

    public PunishmentAddPayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PunishmentAddPayload(UUID player, Punishment punishment) {
        this();
        this.player = player;
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}

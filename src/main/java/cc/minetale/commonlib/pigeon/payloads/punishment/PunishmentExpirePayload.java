package cc.minetale.commonlib.pigeon.payloads.punishment;

import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PunishmentExpirePayload extends BasePayload {

    @Transmit UUID player;
    @Transmit Punishment punishment;

    public PunishmentExpirePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PunishmentExpirePayload(UUID player, Punishment punishment) {
        this();
        this.player = player;
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}

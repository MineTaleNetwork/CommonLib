package cc.minetale.commonlib.pigeon.payloads.punishment;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PunishmentExpirePayload extends BasePayload {

    @Transmit UUID player;
    @Transmit String punishment;

    public PunishmentExpirePayload() {
        payloadId = this.getClass().getSimpleName();
    }

    public PunishmentExpirePayload(UUID player, String punishment) {
        this();
        this.player = player;
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}

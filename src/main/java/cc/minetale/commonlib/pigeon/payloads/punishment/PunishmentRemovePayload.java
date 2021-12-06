package cc.minetale.commonlib.pigeon.payloads.punishment;

import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PunishmentRemovePayload extends BasePayload {

    @Transmit UUID playerUuid;

    @Transmit String punishment;

    public PunishmentRemovePayload() {
        payloadId = "punishmentRemovePayload";
    }

    public PunishmentRemovePayload(UUID playerUuid, String punishment) {
        this();
        this.playerUuid = playerUuid;
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}

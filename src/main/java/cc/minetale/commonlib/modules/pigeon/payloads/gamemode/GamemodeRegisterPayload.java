package cc.minetale.commonlib.modules.pigeon.payloads.gamemode;

import cc.minetale.commonlib.modules.network.Gamemode;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class GamemodeRegisterPayload extends BasePayload {

    public GamemodeRegisterPayload() {
        this.payloadId = "gamemodeRegisterPayload";
    }

    @Transmit Gamemode gamemode;

    public GamemodeRegisterPayload(Gamemode gamemode) {
        this();
        this.gamemode = gamemode;
    }

    @Override
    public void receive() {}

}

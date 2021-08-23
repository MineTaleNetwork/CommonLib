package cc.minetale.commonlib.modules.pigeon.payloads.network;

import cc.minetale.commonlib.modules.network.Gamemode;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.List;

@Getter @Payload
public class ServerUpdatePayload extends BasePayload {

    @Transmit String name;
    @Transmit Gamemode gamemode;
    @Transmit Long uptime;
    @Transmit Double tps;
    @Transmit List<String> players;
    @Transmit Integer maxPlayers;

    public ServerUpdatePayload() {
        payloadId = "serverUpdatePayload";
    }

    public ServerUpdatePayload(String name, Gamemode gamemode, Long uptime, Double tps, List<String> players, Integer maxPlayers) {
        this();
        this.name = name;
        this.gamemode = gamemode;
        this.uptime = uptime;
        this.tps = tps;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void receive() {}

}

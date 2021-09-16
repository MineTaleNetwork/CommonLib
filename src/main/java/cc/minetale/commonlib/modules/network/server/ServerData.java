package cc.minetale.commonlib.modules.network.server;

import cc.minetale.commonlib.modules.network.Gamemode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ServerData {

    private int maxPlayers;
    private double tps;
    private Map<String, String> metadata;
    private List<UUID> onlinePlayers;

    public ServerData() {}

    public ServerData(int maxPlayers, double tps, Map<String, String> metadata, List<UUID> onlinePlayers) {
        this.maxPlayers = maxPlayers;
        this.tps = tps;
        this.metadata = metadata;
        this.onlinePlayers = onlinePlayers;
    }

    public int getAmountOnline() {
        return this.onlinePlayers.size();
    }

}
package cc.minetale.commonlib.modules.network.server;

import cc.minetale.commonlib.modules.network.Gamemode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class ServerData {

    private int maxPlayers;
    private double tps;
    private Map<String, String> metadata;
    private Gamemode gamemode;
    private Set<UUID> onlinePlayers;

    public ServerData() {}

    public ServerData(int maxPlayers, double tps, Map<String, String> metadata, Gamemode gamemode, Set<UUID> onlinePlayers) {
        this.maxPlayers = maxPlayers;
        this.tps = tps;
        this.metadata = metadata;
        this.gamemode = gamemode;
        this.onlinePlayers = onlinePlayers;
    }

    /**
     * Gets the amount of people online on a specific Server.
     *
     * @return amount online.
     */
    public int getAmountOnline() {
        return this.onlinePlayers.size();
    }

}
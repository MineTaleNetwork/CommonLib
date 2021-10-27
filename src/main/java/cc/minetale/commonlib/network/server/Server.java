package cc.minetale.commonlib.network.server;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Server {

    @Getter private static final List<Server> serverList = new ArrayList<>();

    private String name;
    private Long startTime;
    private ServerData data;

    private final long lastUpdated;

    public Server(String name, Long startTime, ServerData data) {
        this.name = name;
        this.startTime = startTime;
        this.data = data;

        this.lastUpdated = System.currentTimeMillis();
    }

    public Server() {
        this.lastUpdated = System.currentTimeMillis();
    }

    public long getUptime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public void updateServer() {
        serverList.removeIf(server -> server.getName().equals(this.name));
        serverList.add(this);
    }

    public static int getOnlinePlayerCount() {
        int players = 0;

        for (Server server : serverList) {
            players += server.getData().getOnlinePlayers().size();
        }

        return players;
    }

    public static Server getServerByName(String name) {
        return serverList.stream()
                .filter(server -> server.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}

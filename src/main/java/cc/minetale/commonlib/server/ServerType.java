package cc.minetale.commonlib.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ServerType {
    HUB("minestom", new String[] { "Flame", "mLib", "Hub" });

    private final String egg;
    private final String[] extensions;
}

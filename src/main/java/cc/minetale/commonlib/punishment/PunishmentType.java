package cc.minetale.commonlib.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum PunishmentType {
    BLACKLIST("Blacklist", "blacklisted", "unblacklisted", true, true),
    BAN("Ban", "banned", "unbanned", true, true),
    MUTE("Mute", "muted", "unmuted", false, true),
    WARN("Warning", "warned", null, false, false);

    private final String readable;
    private final String context;
    private final String undoContext;
    private final boolean ban;
    private final boolean removable;
}
package cc.minetale.commonlib.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PunishmentType {
    BAN(
            "Ban",
            "banned",
            "unbanned"
    ),

    MUTE(
            "Mute",
            "muted",
            "unmuted"
    );

    private final String readable;
    private final String context;
    private final String undoContext;

}
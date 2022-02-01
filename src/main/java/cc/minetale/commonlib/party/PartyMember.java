package cc.minetale.commonlib.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

public record PartyMember(UUID player, Role role) {

    @Getter
    @AllArgsConstructor
    public enum Role {
        LEADER("Leader"),
        MODERATOR("Moderator"),
        MEMBER("Member");

        private final String readable;
    }

}

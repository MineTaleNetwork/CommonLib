package cc.minetale.commonlib.messages;

import cc.minetale.commonlib.util.MC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter @AllArgsConstructor
public enum CommonMessage {
    ERROR_OCCURRED(MC.component("An error has occurred, please try rejoining the network.", MC.CC.RED)),
    COULD_NOT_RESOLVE_PLAYER(MC.component("Could not resolve player information.", MC.CC.RED)),
    FAILED_TO_LOAD_PROFILE(MC.component("Failed to load your profile. Try again later.", MC.CC.RED)),
    PLAYER_OFFLINE(MC.component("That player is currently not online.", MC.CC.RED)),
    CONVERSATION_NOT_STARTED(MC.component("You haven't started a conversation with anybody.", MC.CC.RED)),
    NOT_RECEIVING_MESSAGES(MC.component("That player is not receiving new conversations right now.", MC.CC.RED));

    private Component component;

}

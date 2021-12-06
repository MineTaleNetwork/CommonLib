package cc.minetale.commonlib.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter @AllArgsConstructor
public enum CommonMessage {
    ERROR_OCCURRED(Component.text("An error has occurred, please try rejoining the network.", NamedTextColor.RED)),
    COULD_NOT_RESOLVE_PLAYER(Component.text("Could not resolve player information.", NamedTextColor.RED)),
    FAILED_TO_LOAD_PROFILE(Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED)),
    PLAYER_OFFLINE(Component.text("That player is currently not online.", NamedTextColor.RED)),
    CONVERSATION_NOT_STARTED(Component.text("You haven't started a conversation with anybody.", NamedTextColor.RED)),
    NOT_RECEIVING_MESSAGES(Component.text("That player is not receiving new conversations right now.", NamedTextColor.RED));

    private Component component;

}

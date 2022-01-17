package cc.minetale.commonlib.lang;

import cc.minetale.commonlib.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Language {

    public static class General {
        public static Component NETWORK_ERROR = Message.message("Error", Component.text("An error has occurred, please try rejoining the network.", NamedTextColor.RED));
        public static Component UNKNOWN_PLAYER_ERROR = Message.message("Error", Component.text("That player does not exist or hasn't joined.", NamedTextColor.RED));
        public static Component PROFILE_LOAD_ERROR = Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED);
        public static Component PLAYER_OFFLINE_ERROR = Message.message("Error", Component.text("That player is currently not online.", NamedTextColor.RED));
        public static Component COMMAND_ERROR = Message.message("Error", Component.text("An error occurred when trying to execute that command.", NamedTextColor.RED));
    }

    public static class Error {
        public static Component PLAYER_ERROR = Message.message("Error", Component.text("An error has occurred, please try rejoining the network.", NamedTextColor.RED));
        public static Component UNKNOWN_PLAYER = Message.message("Error", Component.text("That player does not exist or hasn't joined.", NamedTextColor.RED));
        public static Component FAILED_TO_LOAD_PROFILE = Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED);
        public static Component PLAYER_OFFLINE = Message.message("Error", Component.text("That player is currently not online.", NamedTextColor.RED));
        public static Component COMMAND_ERROR = Message.message("Error", Component.text("An error occurred when trying to execute that command.", NamedTextColor.RED));
    }

    public static class Conversation {
        public static Component CONVERSATION_NOT_STARTED = Message.message("Conversation", Component.text("You haven't started a conversation with anybody.", NamedTextColor.RED));
        public static Component NOT_RECEIVING_MESSAGES = Message.message("Conversation", Component.text("That player is not receiving new conversations right now.", NamedTextColor.RED));
    }

    public static class Friend  {
        public static class General {
            public static Component MAXIMUM_REQUESTS = Message.message("Friend", Component.text("You've hit the maximum amount of friend requests.", NamedTextColor.RED));
            public static Component PLAYER_MAXIMUM_FRIENDS = Message.message("Friend", Component.text("You've hit the maximum amount of friends.", NamedTextColor.RED));
            public static Component TARGET_MAXIMUM_FRIENDS = Message.message("Friend", Component.text("That player already has the maximum amount of friends.", NamedTextColor.RED));
            public static Component TARGET_IGNORED = Message.message("Friend", Component.text("You are currently ignoring that player.", NamedTextColor.RED));
            public static Component TARGET_TOGGLED = Message.message("Friend", Component.text("That players is not receiving new friends at this moment.", NamedTextColor.RED));
        }

        public static class Add {
            public static Component SUCCESS_TARGET = Message.message("Friend", Component.text("{0} has sent you a friend request!", NamedTextColor.GREEN));
            public static Component SUCCESS_PLAYER = Message.message("Friend", Component.text("You sent a friend request to {0}!", NamedTextColor.GREEN));
            public static Component REQUEST_EXIST = Message.message("Friend", Component.text("You've already sent a friend request to that player.", NamedTextColor.RED));
            public static Component PENDING_REQUEST = Message.message("Friend", Component.text("You already have a request from that player.", NamedTextColor.RED));
        }

        public static class Accept {
            public static Component SUCCESS = Message.message("Friend", Component.text("You are now friends with {0}!", NamedTextColor.GREEN));
            public static Component NO_REQUEST = Message.message("Friend", Component.text("You do not have a friend request from that player.", NamedTextColor.RED));
        }
    }

}

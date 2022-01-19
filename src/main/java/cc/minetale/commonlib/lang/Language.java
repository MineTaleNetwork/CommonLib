package cc.minetale.commonlib.lang;

import cc.minetale.commonlib.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Language {

    public static class General {
        public static Component CHAT_CLEARED = Message.notification("Chat", Component.text("Chat has been cleared by {0}.", NamedTextColor.GRAY));
    }

    public static class Punishment {
        public static Component PUNISHMENT_ANNOUNCEMENT = Message.notification("Punishment", Component.text("{0} has been {1} by {2}.", NamedTextColor.GRAY));
        public static Component PUNISHMENT_SUCCESS = Message.notification("Punishment", Component.text("You have successfully punished {0}!", NamedTextColor.GREEN));
    }

    public static class Command {
        public static Component UNKNOWN_COMMAND_ERROR = Message.notification("Command", Component.text("You have entered an unknown command."));
        public static Component COMMAND_EXCEPTION_ERROR = Message.notification("Command", Component.text("An error occurred when trying to execute that command.", NamedTextColor.RED));
        public static Component COMMAND_PERMISSION_ERROR = Message.notification("Command", Component.text("You need {0} rank to use this command.", NamedTextColor.GRAY));
    }

    public static class Error {
        public static Component NETWORK_ERROR = Message.notification("Error", Component.text("An error has occurred, please try rejoining the network.", NamedTextColor.RED));
        public static Component UNKNOWN_PLAYER_ERROR = Message.notification("Error", Component.text("That player does not exist or hasn't joined.", NamedTextColor.RED));
        public static Component PROFILE_LOAD_ERROR = Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED);
        public static Component PLAYER_OFFLINE_ERROR = Message.notification("Error", Component.text("That player is currently not online.", NamedTextColor.RED));
    }

    public static class Conversation {
        public static Component CONVERSATION_NOT_STARTED = Message.notification("Conversation", Component.text("You haven't started a conversation with anybody.", NamedTextColor.RED));
        public static Component NOT_RECEIVING_MESSAGES = Message.notification("Conversation", Component.text("That player is not receiving new conversations right now.", NamedTextColor.RED));
        public static Component TO_MSG = Component.text("(To {0}) {1}", NamedTextColor.GRAY);
        public static Component FROM_MSG = Component.text("(From {0}) {1}", NamedTextColor.GRAY);
    }

    public static class Friend  {
        public static class General {
            public static Component NO_FRIENDS = Message.notification("Friend", Component.text("You don't seem to have any friends, try adding some!", NamedTextColor.GRAY));
            public static Component MAXIMUM_REQUESTS = Message.notification("Friend", Component.text("You've hit the maximum amount of friend requests.", NamedTextColor.RED));
            public static Component PLAYER_MAXIMUM_FRIENDS = Message.notification("Friend", Component.text("You've hit the maximum amount of friends.", NamedTextColor.RED));
            public static Component TARGET_MAXIMUM_FRIENDS = Message.notification("Friend", Component.text("That player already has the maximum amount of friends.", NamedTextColor.RED));
            public static Component TARGET_IGNORED = Message.notification("Friend", Component.text("You are currently ignoring that player.", NamedTextColor.RED));
            public static Component TARGET_TOGGLED = Message.notification("Friend", Component.text("That players is not receiving new friends at this moment.", NamedTextColor.RED));
        }

        public static class Add {
            public static Component SUCCESS_TARGET = Message.notification("Friend", Component.text("{0} has sent you a friend request!", NamedTextColor.GREEN));
            public static Component SUCCESS_PLAYER = Message.notification("Friend", Component.text("You sent a friend request to {0}!", NamedTextColor.GREEN));
            public static Component ALREADY_FRIENDS = Message.notification("Friend", Component.text("You are already friends with that player.", NamedTextColor.GRAY));
            public static Component TARGET_IS_PLAYER = Message.notification("Friend", Component.text("You cannot add yourself as a friend.", NamedTextColor.RED));
            public static Component REQUEST_EXIST = Message.notification("Friend", Component.text("You've already sent a friend request to that player.", NamedTextColor.RED));
            public static Component PENDING_REQUEST = Message.notification("Friend", Component.text("You already have a request from that player.", NamedTextColor.RED));
        }

        public static class Accept {
            public static Component SUCCESS = Message.notification("Friend", Component.text("You are now friends with {0}!", NamedTextColor.GREEN));
            public static Component NO_REQUEST = Message.notification("Friend", Component.text("You do not have a friend request from that player.", NamedTextColor.RED));
        }
    }

}

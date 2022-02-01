package cc.minetale.commonlib.lang;

public class Language {

    private static String notification(String prefix) {
        return "<gold><bold>" + prefix + " <dark_gray>» <reset>";
    }

    public static class General {
        public static String CHAT_CLEARED = notification("Chat") + "<gray>Chat has been cleared by <0>.";
        public static String CHAT_FORMAT = "<0><1><2> <dark_gray><b>»</b></dark_gray> <3>";
    }

    public static class Punishment {
        public static String PUNISHMENT_ANNOUNCEMENT = notification("Punishment") + "<gray><0> has been <1> by <2>";
        public static String PUNISHMENT_SUCCESS = notification("Punishment") + "<green>You have successfully punished <0>";
    }

    public static class Command {
        public static String UNKNOWN_COMMAND_ERROR = notification("Command") + "You have entered an unknown command.";
        public static String COMMAND_EXCEPTION_ERROR = notification("Command") + "<red>An error occurred when trying to execute that command.";
        public static String COMMAND_PERMISSION_ERROR = notification("Command") + "<gray>You need <0> rank to use this command.";
    }

    public static class Error {
        public static String NETWORK_ERROR = notification("Error") + "<red>An error has occurred, please try rejoining the network.";
        public static String UNKNOWN_PLAYER_ERROR = notification("Error") + "<red>That player does not exist or hasn't joined.";
        public static String PROFILE_LOAD_ERROR = "<red>Failed to load your profile. Try again later.";
        public static String PLAYER_OFFLINE_ERROR = notification("Error") + "<red>That player is currently not online.";
    }

    public static class Conversation {
        public static String CONVERSATION_NOT_STARTED = notification("Conversation") + "<red>You haven't started a conversation with anybody.";
        public static String NOT_RECEIVING_MESSAGES = notification("Conversation") + "<red>That player is not receiving new conversations right now.";
        public static String LAST_MESSAGED_NULL = notification("Conversation") + "<red>You don't have anyone to reply to.";
        public static String TARGET_IS_PLAYER = notification("Conversation") + "<red>You cannot message yourself.";
        public static String TARGET_IGNORED = notification("Conversation") + "<red>You are currently ignoring that player.";
        public static String TARGET_TOGGLED = notification("Conversation") + "<red>That players is not receiving new friends at this moment.";
        public static String TO_MSG = "<gray>(To <0>) <1>";
        public static String FROM_MSG = "<gray>(From <0>) <1>";
    }

    public static class Party {
        public static class Invite {
            public static String SUCCESS_PLAYER = notification("Party") + "<0> <green>has been invited to the party!";
            public static String SUCCESS_TARGET = notification("Party") + "<0> <green>has invited you to their party!";
        }

        public static String PARTY_DISBANDED = notification("Party") + "<red>The party has been disbanded.";
        public static String PARTY_CHAT_FORMAT = notification("Party") + General.CHAT_FORMAT;

        public static String PARTY_JOIN = notification("Party") + "<0> <green>has joined the party!";
        public static String PARTY_LEAVE = notification("Party") + "<0> <red>has left the party.";
        public static String PARTY_KICK = notification("Party") + "<0> <red>has been kicked from the party.";
        public static String PARTY_SUMMON = notification("Party") + "<gray>You have been summoned to <0>.";
        public static String PARTY_PROMOTE = notification("Party") + "<0> <green>has been promoted to <1>!";
        public static String PARTY_DEMOTE = notification("Party") + "<0> <red>has been demoted to <1>.";

    }

    public static class Friend {
        public static class General {
            public static String FRIEND_JOINED = notification("Friend") + "<gray><0> has joined the network.";
            public static String FRIEND_LEFT = notification("Friend") + "<gray><0> has left the network.";
            public static String NO_OUTGOING = notification("Friend") + "<red>You don't have any outgoing friend requests.";
            public static String NO_INCOMING = notification("Friend") + "<red>You don't have any incoming friend requests.";
            public static String NO_FRIENDS = notification("Friend") + "<gray>You don't seem to have any friends, try adding some!";
            public static String MAXIMUM_REQUESTS = notification("Friend") + "<red>You've hit the maximum amount of friend requests.";
            public static String PLAYER_MAXIMUM_FRIENDS = notification("Friend") + "<red>You've hit the maximum amount of friends.";
            public static String TARGET_MAXIMUM_FRIENDS = notification("Friend") + "<red>That player already has the maximum amount of friends.";
            public static String TARGET_IGNORED = notification("Friend") + "<red>You are currently ignoring that player.";
            public static String TARGET_TOGGLED = notification("Friend") + "<red>That players is not receiving new friends at this moment.";
        }

        public static class Deny {
            public static String SUCCESS_PLAYER = notification("Friend") + "<green>You denied the friend request from <0>";
            public static String SUCCESS_TARGET = notification("Friend") + "<red><0> denied your friend request.";
            public static String NO_REQUEST = notification("Friend") + "<red>You do not have a pending friend request from <0>";
        }

        public static class Cancel {
            public static String SUCCESS = notification("Friend") + "<green>You cancelled the friend request to <0>";
            public static String NO_REQUEST = notification("Friend") + "<red><0> does not have a pending request from you.";
        }

        public static class Remove {
            public static String SUCCESS_TARGET = notification("Friend") + "<red><0> has removed you from their friends list.";
            public static String SUCCESS_PLAYER = notification("Friend") + "<green>You removed <0> from your friends list.";
            public static String NOT_ADDED = notification("Friend") + "<red><0> isn't on your friends list.";
        }

        public static class Add {
            public static String SUCCESS_TARGET = notification("Friend") + "<green><0> has sent you a friend request!";
            public static String SUCCESS_PLAYER = notification("Friend") + "<green>You sent a friend request to <0>";
            public static String ALREADY_FRIENDS = notification("Friend") + "<red>You are already friends with <0>";
            public static String TARGET_IS_PLAYER = notification("Friend") + "<red>You cannot add yourself as a friend.";
            public static String REQUEST_EXIST = notification("Friend") + "<red>You've already sent a friend request to <0>";
            public static String PENDING_REQUEST = notification("Friend") + "<red>You already have a request from <0>";
        }

        public static class Accept {
            public static String SUCCESS = notification("Friend") + "<green>You are now friends with <0>";
            public static String NO_REQUEST = notification("Friend") + "<red>You do not have a friend request from <0>";
        }
    }

}

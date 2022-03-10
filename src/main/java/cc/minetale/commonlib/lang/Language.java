package cc.minetale.commonlib.lang;

public class Language {

    private static String notification(String prefix) {
        return "<gold><bold>" + prefix + " <dark_gray>» <reset>";
    }

    public static class Staff {
        private static final String PREFIX = "<dark_gray>[<blue>➤</blue>]</dark_gray> ";
        public static String STAFF_JOIN = PREFIX + "<0> <gray>has joined <blue><1>";
        public static String STAFF_SWITCH = PREFIX + "<0> <gray>has switched from <blue><1></blue> to <blue><2>";
        public static String STAFF_LEAVE = PREFIX + "<0> <gray>has left <blue><1>";
        public static String CHAT_FORMAT = PREFIX + General.CHAT_FORMAT;
    }

    public static class General {
        public static String CHAT_CLEARED = notification("Chat") + "<gray>Chat has been cleared by <0>.";
        public static String CHAT_FORMAT = "<0> <dark_gray><b>»</b></dark_gray> <1>";
    }

    public static class Staff {
        public static String STAFF_JOIN = "";
        public static String STAFF_SWITCH = "";
        public static String STAFF_LEAVE = "";
        public static String STAFF_CHAT = "";
    }

    public static class Punishment {
        public static String ANNOUNCEMENT = notification("Punishment") + "<gray><0> has been <1> by <2>";
        public static String SUCCESS = notification("Punishment") + "<green>You have successfully punished <0>";
    }

    public static class Command {
        public static String UNKNOWN_COMMAND = notification("Command") + "You have entered an unknown command.";
        public static String COMMAND_EXCEPTION = notification("Command") + "<red>An error occurred when trying to execute that command.";
        public static String COMMAND_PERMISSION = notification("Command") + "<gray>You need <0> rank to use this command.";
    }

    public static class Error {
        public static String PLAYER_NETWORK = notification("Error") + "<red>An error has occurred, please try rejoining the network.";
        public static String UNKNOWN_PLAYER = notification("Error") + "<red>That player does not exist or hasn't joined.";
        public static String PROFILE_LOAD = "<red>Failed to load your profile. Try again later.";
        public static String PLAYER_OFFLINE = notification("Error") + "<red>That player is currently not online.";
    }

    public static class Conversation {
        public static String UNKNOWN_CONVERSATION = notification("Conversation") + "<red>You haven't started a conversation with anybody.";
        public static String TARGET_TOGGLED = notification("Conversation") + "<red>That player is not receiving new conversations right now.";
        public static String SELF_TARGET = notification("Conversation") + "<red>You cannot message yourself.";
        public static String TARGET_IGNORED = notification("Conversation") + "<red>You are currently ignoring that player.";
        public static String TO_MSG = "<gray>(To <0>) <1>";
        public static String FROM_MSG = "<gray>(From <0>) <1>";
    }

    public static class Party {
        public static String NO_OUTGOING = notification("Party") + "<red>You don't have any outgoing party requests.";
        public static String NO_INCOMING = notification("Party") + "<red>You don't have any incoming party requests.";

        public static String INVITE_PARTY = notification("Party") + "<0> <green>has been invited to the party!";
        public static String INVITE_TARGET = notification("Party") + "<0> <green>has invited you to their party!";
        public static String INVITE_MAX_OUTGOING = notification("Party") + "<red>You've hit the maximum amount of party requests.";
        public static String INVITE_MAX_INCOMING = notification("Friend") + "<0> <red>already has the maximum amount of party requests.";
        public static String INVITE_SELF_TARGET = notification("Party") + "<red>You cannot invite yourself to the party.";
        public static String INVITE_EXISTS = notification("Party") + "<red>You've already sent a party request to <0>";

        public static String TARGET_IGNORED = notification("Party") + "<red>You are currently ignoring that player.";
        public static String TARGET_TOGGLED = notification("Party") + "<0> <red>is not receiving new party requests at this moment.";
        public static String IN_PARTY = notification("Party") + "<0> <red>is already in the party.";
        public static String PARTY_MUTED = notification("Party") + "<red>The party is currently muted.";
        public static String NO_PARTY = notification("Party") + "<red>You are currently not in a party.";
        public static String PARTY_DISBANDED = notification("Party") + "<red>The party has been disbanded.";
        public static String PARTY_JOIN = notification("Party") + "<0> <green>has joined the party!";
        public static String PARTY_LEAVE = notification("Party") + "<0> <red>has left the party.";
        public static String PARTY_KICK = notification("Party") + "<0> <red>has been kicked from the party.";
        public static String PARTY_SUMMON = notification("Party") + "<gray>You have been summoned to <0>.";
        public static String PARTY_PROMOTE = notification("Party") + "<0> <green>has been promoted to <1>!";
        public static String PARTY_DEMOTE = notification("Party") + "<0> <red>has been demoted to <1>.";
        public static String PARTY_CHAT_FORMAT = notification("Party") + General.CHAT_FORMAT;
    }

    public static class Friend {
        public static String NO_FRIENDS = notification("Friend") + "<gray>You don't seem to have any friends, try adding some!";
        public static String NO_REQUEST = notification("Friend") + "<red>You do not have a pending friend request from <0>";

        public static String INVITE_NO_OUTGOING = notification("Friend") + "<red>You don't have any outgoing friend requests.";
        public static String INVITE_NO_INCOMING = notification("Friend") + "<red>You don't have any incoming friend requests.";
        public static String INVITE_MAX_OUTGOING = notification("Friend") + "<red>You've hit the maximum amount of friend requests.";
        public static String INVITE_MAX_INCOMING = notification("Friend") + "<0> <red>already has the maximum amount of party requests.";
        public static String INVITE_INITIATOR = notification("Friend") + "<green>You sent a friend request to <0>";
        public static String INVITE_TARGET = notification("Friend") + "<green><0> has sent you a friend request!";
        public static String INVITE_ALREADY_FRIENDS = notification("Friend") + "<red>You are already friends with <0>";
        public static String INVITE_SELF_TARGET = notification("Friend") + "<red>You cannot add yourself as a friend.";
        public static String INVITE_EXIST = notification("Friend") + "<red>You've already sent a friend request to <0>";
        public static String INVITE_PENDING = notification("Friend") + "<red>You already have a pending request from <0>";

        public static String JOINED_NETWORK = notification("Friend") + "<gray><0> has joined the network.";
        public static String LEFT_NETWORK = notification("Friend") + "<gray><0> has left the network.";

        public static String MAX_FRIENDS_INITIATOR = notification("Friend") + "<red>You've hit the maximum amount of friends.";
        public static String MAX_FRIENDS_TARGET = notification("Friend") + "<red>That player already has the maximum amount of friends.";
        public static String TARGET_IGNORED = notification("Friend") + "<red>You are currently ignoring that player.";
        public static String TARGET_TOGGLED = notification("Friend") + "<red>That players is not receiving new friends at this moment.";
        public static String DENY_INITIATOR = notification("Friend") + "<green>You denied the friend request from <0>";
        public static String DENY_TARGET = notification("Friend") + "<red><0> denied your friend request.";
        public static String CANCEL_INITIATOR = notification("Friend") + "<green>You cancelled the friend request to <0>";
        public static String CANCEL_NO_REQUEST = notification("Friend") + "<0> <red>does not have a pending request from you.";
        public static String REMOVE_INITIATOR = notification("Friend") + "<green>You removed <0> from your friends list.";
        public static String REMOVE_TARGET = notification("Friend") + "<0> <red>has removed you from their friends list.";
        public static String REMOVE_NOT_ADDED = notification("Friend") + "<0> <red>isn't on your friends list.";

        public static String ACCEPT_REQUEST = notification("Friend") + "<green>You are now friends with <0>";
    }

}

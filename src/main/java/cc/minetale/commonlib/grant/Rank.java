package cc.minetale.commonlib.grant;

import cc.minetale.commonlib.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
@AllArgsConstructor
public enum Rank {
    OWNER("Owner", NamedTextColor.DARK_RED, true),
    ADMIN("Admin", NamedTextColor.RED, true),
    MOD("Mod", NamedTextColor.DARK_PURPLE, true),
    HELPER("Helper", NamedTextColor.BLUE, true),

    YOUTUBE("YouTube", NamedTextColor.RED, false),
    MEDIA("Media", NamedTextColor.LIGHT_PURPLE, false),

    HIGHROLLER("Highroller", NamedTextColor.DARK_PURPLE, false),
    LEGEND("Legend", NamedTextColor.GOLD, false),
    MVP("MVP", NamedTextColor.DARK_AQUA, false),
    VIP("VIP", NamedTextColor.DARK_GREEN, false),
    PREMIUM("Premium", NamedTextColor.GREEN, false),

    MEMBER("Member", NamedTextColor.GRAY, false);

    /**
     * Name of the rank publicly displayed.
     */
    private final String name;

    /**
     * Color of the rank publicly displayed.
     */
    private final NamedTextColor color;

    /**
     * If the rank is considered a staff rank.
     */
    private final boolean staff;

    /**
     * Returns the prefix of the rank that
     * will be publicly displayed.
     *
     * @return The prefix
     */
    public Component getPrefix() {
        return Component.text().append(
                Component.text("[", NamedTextColor.DARK_GRAY),
                Component.text(this.name, this.color),
                Component.text("]", NamedTextColor.DARK_GRAY)
        ).build();
    }

    /**
     * Check if a player has the minimum rank.
     */
    public static boolean hasMinimumRank(Profile profile, Rank rank) {
        return profile.getGrant().getRank().compare(rank);
    }

    /**
     * Check whether the provided rank
     * is of a higher position than this one.
     *
     * @param other The other rank
     * @return Whether the other rank is higher
     */
    public boolean compare(Rank other) {
        return this.ordinal() <= other.ordinal();
    }

}

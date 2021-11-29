package cc.minetale.commonlib.api;

import cc.minetale.commonlib.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Comparator;

@Getter @AllArgsConstructor
public enum Rank {
    OWNER(
            "Owner",
            NamedTextColor.DARK_RED
    ),
    ADMIN(
            "Admin",
            NamedTextColor.RED
    ),
    MOD(
            "Mod",
            NamedTextColor.DARK_PURPLE
    ),
    HELPER(
            "Helper",
            NamedTextColor.BLUE
    ),
    DEFAULT(
            "Member",
            NamedTextColor.GRAY
    );

    private String name;
    private NamedTextColor color;

    public static Comparator<Rank> COMPARATOR = Comparator.comparingInt(Rank::getWeight);

    public int getWeight() {
        return this.ordinal();
    }

    public Component getPrefix() {
        return Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(this.name, this.color))
                        .append(Component.text("]", NamedTextColor.DARK_GRAY)))
                .build();
    }

    /**
     * Check if a player has the minimum rank.
     */
    public static boolean hasMinimumRank(Profile profile, Rank rank) {
        return profile.getGrant().getRank().getWeight() >= rank.getWeight();
    }

}

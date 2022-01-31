package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.grant.Rank;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@Getter
public class MiniProfile extends AbstractProfile{

    private UUID uuid;
    private String username;
    private Rank rank;

     private MiniProfile(UUID uuid, String username, Rank rank) {
        this.uuid = uuid;
        this.username = username;
        this.rank = rank;
    }

    /**
     * Default constructor used for Jackson.
     */
    public MiniProfile() { }

    public static MiniProfile of(Profile profile) {
        return new MiniProfile(profile.getUuid(), profile.getUsername(), profile.getGrant().getRank());
    }

    /**
     * Returns a decorated component
     * of the profiles chat format.
     *
     * @return The decorated component
     */
    @Override
    public Component getChatFormat() {
        return Component.text().append(
                this.getColoredPrefix(),
                Component.space(),
                this.getColoredName()
        ).build();
    }

    /**
     * Returns a decorated component of the
     * profiles name colored their grant's rank color.
     *
     * @return The decorated component
     */
    @Override
    public Component getColoredName() {
        return Component.text(this.username, this.rank.getColor());
    }

    /**
     * Returns a decorated component of the
     * profile's grant's rank prefix.
     *
     * @return The decorated component
     */
    @Override
    public Component getColoredPrefix() {
        return this.rank.getPrefix();
    }

}

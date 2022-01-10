package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.punishment.Punishment;
import lombok.Getter;

import java.util.List;

@Getter
public class CachedProfile {

    private final Profile profile;
    private final String currentServer;
    private final List<Grant> grants;
    private final List<Punishment> punishments;

    public CachedProfile(Profile profile, String currentServer) {
        this.profile = profile;
        this.currentServer = currentServer;

        this.grants = profile.getGrants();
        this.punishments = profile.getPunishments();
    }

    public Profile getProfile() {
        this.profile.setGrants(this.grants);
        this.profile.setPunishments(this.punishments);

        return this.profile;
    }

}

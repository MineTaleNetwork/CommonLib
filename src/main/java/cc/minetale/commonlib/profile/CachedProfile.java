package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.punishment.Punishment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CachedProfile {

    private Profile profile;
    private List<Grant> grants;
    private String server;
    private List<Punishment> punishments;

    public CachedProfile(Profile profile) {
        this.profile = profile;

        this.grants = profile.getGrants();
        this.punishments = profile.getPunishments();
    }

    public Profile getProfile() {
        this.profile.setGrants(this.grants);
        this.profile.setPunishments(this.punishments);

        return this.profile;
    }

}

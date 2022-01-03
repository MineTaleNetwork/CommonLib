package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.punishment.Punishment;
import lombok.Getter;

import java.util.List;

@Getter
public class CachedProfile {

    private final Profile profile;
    private final List<Grant> grants;
    private final List<Punishment> punishments;

    public CachedProfile(Profile profile) {
        this.profile = profile;
        this.grants = profile.getGrants();
        this.punishments = profile.getPunishments();
    }

}

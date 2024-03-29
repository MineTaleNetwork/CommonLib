package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.punishment.Punishment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = { "profile" })
public class CachedProfile {

    private Profile profile;
    private List<Grant> grants;
    private List<Punishment> punishments;
    private String server;
    private UUID lastMessaged;
    private UUID party;

    public CachedProfile(Profile profile) {
        this.profile = profile;

        grants = profile.getGrants();
        punishments = profile.getPunishments();
    }

    public CachedProfile() {}

    public Profile getProfile() {
        profile.setGrants(grants);
        profile.setPunishments(punishments);

        return profile;
    }

}

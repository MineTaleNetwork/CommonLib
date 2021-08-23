package cc.minetale.commonlib.modules.rank;

import cc.minetale.commonlib.util.MC;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class RankAPI {

    private final Rank rank;

    public RankAPI(Rank rank) {
        this.rank = rank;
    }

    /**
     * Retrieves the 'Default' Rank.
     */
    public static Rank getDefaultRank() {
        for (Rank rank : Rank.getRanks().values()) {
            if (rank.getName().equals("Default")) {
                return rank;
            }
        }

        Document document = Rank.getCollection().find(Filters.eq("name", "Default")).first();

        Rank rank = Rank.fromDocument(document);

        if(rank != null) {
            Rank.getRanks().put(rank.getUuid(), rank);
            return rank;
        }

        rank = new Rank("Default", 0, "&8[&7Default&8] &7", "GRAY");
        rank.save();

        Rank.getRanks().put(rank.getUuid(), rank);

        return rank;
    }

    /**
     * Returns if the provided rank is the Default Rank.
     */
    public boolean isDefaultRank() {
        return getDefaultRank().equals(this.rank);
    }

    /**
     * Checks if a Rank has a certain permission.
     */
    public boolean hasPermission(String permission) {
        return hasPermission(permission, true);
    }

    /**
     * Checks if a Rank has a certain permission.
     */
    public boolean hasPermission(String permission, boolean includeInheritance) {
        if (rank.getPermissions().contains(permission)) {
            return true;
        }

        if (includeInheritance) {
            for (Rank inherited : rank.getInherited()) {
                if (inherited.api().hasPermission(permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a Rank can inherit another Rank.
     */
    public boolean canInherit(Rank rank) {
        if (this.rank.equals(rank) || this.rank.getInherited().contains(rank) || rank.getInherited().contains(this.rank)) {
            return false;
        }

        for (Rank inherited : rank.getInherited()) {
            if (!this.canInherit(inherited)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets a list of all of a Rank's permissions.
     */
    public List<String> getAllPermissions() {
        return getAllPermissions(true);
    }

    /**
     * Gets a list of all of a Rank's permissions.
     */
    public List<String> getAllPermissions(boolean includeInheritance) {
        List<String> permissions = new ArrayList<>(this.rank.getPermissions());

        if (includeInheritance) {
            for (Rank inherited : this.rank.getInherited()) {
                permissions.addAll(inherited.api().getAllPermissions());
            }
        }

        return permissions;
    }

    public MC.CC getRankColor() {
        return MC.CC.valueOf(this.rank.getColor());
    }

    /**
     * Initialize all the Ranks.
     */
    public static void initialize() {
        Map<Rank, List<UUID>> inheritanceReferences = new HashMap<>();

        Rank.getRanks().clear();

        try (MongoCursor<Document> cursor = Rank.getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                Rank rank = Rank.getRank(UUID.fromString(document.getString("_id")), true);

                List<UUID> ranksToInherit = document.getList("inherits", String.class).
                        stream()
                        .map(UUID::fromString).collect(Collectors.toList());

                inheritanceReferences.put(rank, ranksToInherit);
            }
        }

        inheritanceReferences.forEach((rank, list) -> list.forEach(uuid -> {
            Rank inherited = Rank.getRank(uuid, true);

            if (inherited != null) {
                rank.getInherited().add(inherited);
            }
        }));

        getDefaultRank();
    }

}

package cc.minetale.commonlib.rank;

import cc.minetale.commonlib.util.MC;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;

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
     * Returns the ranks color.
     */
    public MC.CC getRankColor() {
        return MC.CC.valueOf(this.rank.getColor());
    }

    /**
     * Initialize all the ranks.
     */
    public static void initialize() {
        Rank.getRanks().clear();

        try (MongoCursor<Document> cursor = Rank.getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                Rank rank = Rank.getRank(UUID.fromString(document.getString("_id")), true);
            }
        }

        getDefaultRank();
    }

}

package cc.minetale.commonlib.modules.rank;

import cc.minetale.commonlib.CommonLib;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.*;

@Getter @Setter
public class Rank {

    @Getter private static final Map<UUID, Rank> ranks = new HashMap<>();
    @Getter private static MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("ranks");

    public static Comparator<Rank> COMPARATOR = Comparator.comparingInt(Rank::getWeight);

    private final UUID uuid;
    private String name;
    private String prefix;
    private int weight;
    private String color;
    @Accessors(fluent = true)
    private final RankAPI api;

    public Rank(String name, int weight, String prefix, String color) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.weight = weight;
        this.prefix = prefix;
        this.color = color;
        this.api = new RankAPI(this);

        ranks.put(this.uuid, this);
    }

    private Rank(UUID uuid) {
        this.uuid = uuid;
        this.api = new RankAPI(this);

        ranks.put(uuid, this);
    }

    public static Rank getRank(UUID uuid, boolean checkCache) {
        Rank rank =  null;

        if(checkCache) {
            rank = Rank.getRanks().get(uuid);
        }

        if(rank != null) {
            return rank;
        }

        Document document = collection.find(Filters.eq("_id", uuid.toString())).first();

        if (document != null) {
            rank = new Rank(UUID.fromString(document.getString("_id")));

            rank.setName(document.getString("name"));
            rank.setWeight(document.getInteger("weight"));
            rank.setPrefix(document.getString("prefix"));
            rank.setColor(document.getString("color"));

            Rank.getRanks().put(rank.getUuid(), rank);

            return rank;
        }

        return null;
    }

    public static Rank getRank(String name) {
        for (Rank rank : ranks.values()) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    public static Rank fromDocument(Document document) {
        if (document != null) {
            Rank rank = new Rank(UUID.fromString(document.getString("_id")));

            rank.setName(document.getString("name"));
            rank.setWeight(document.getInteger("weight"));
            rank.setPrefix(document.getString("prefix"));

            rank.setColor(document.getString("color"));

            return rank;
        }

        return null;
    }

    public void delete() {
        ranks.remove(this.uuid);
        collection.deleteOne(Filters.eq("_id", this.uuid.toString()));
    }

    public void save() {
        Document document = new Document();
        document.put("_id", this.uuid.toString());
        document.put("name", this.name);
        document.put("color", this.color);
        document.put("prefix", this.prefix);
        document.put("weight", this.weight);

        collection.replaceOne(Filters.eq("_id", this.uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Rank && ((Rank) object).uuid.equals(this.uuid);
    }

}
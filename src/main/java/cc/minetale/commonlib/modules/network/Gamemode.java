package cc.minetale.commonlib.modules.network;

import cc.minetale.commonlib.CommonLib;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
public class Gamemode {

    public static MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("gamemodes");
//    @Getter private static final List<Gamemode> cachedGamemodes = Collections.synchronizedList(new ArrayList<>());

    @Getter private final String name;

    @Getter @Setter private int totalPlayers;
    @Getter @Setter private RecordValue totalPlayersRecord;

    public Gamemode(Document document) {
        this.name = document.getString("_id");
        this.totalPlayersRecord = new RecordValue((Document) document.get("totalPlayersRecord"));
    }

    public Gamemode(String name) {
        this.name = name;
        this.totalPlayersRecord = new RecordValue(new Date(), "0");
    }

    public static Gamemode getByName(String name) {
        var document = collection.find(Filters.eq(name)).first();

        if(document != null) {
            return new Gamemode(document);
        }

        return null;
    }

    public static void register(@NotNull Gamemode gamemode) {
//        getByName(gamemode.getName(), otherGamemode -> {
//            if(otherGamemode == null) {
////                cachedGamemodes.add(gamemode);
//
////                if(callback != null) { callback.accept(true); }
//
//                var pigeon = CommonLib.getCommonLib().getPigeon();
//                pigeon.broadcast(new GamemodeRegisterPayload(gamemode));
//            }
//        });

        collection.insertOne(new Document("_id", gamemode.getName())
                .append("totalPlayersRecord", gamemode.getTotalPlayersRecord().toDocument()));
    }

    public static void loadAll() {
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                var document = cursor.next();
                load(new Gamemode(document));
            }
        }
    }

    public static boolean load(Gamemode gamemode) {
        if(getByName(gamemode.getName()) != null) { return false; }
//        gamemodes.add(gamemode);
        return true;
    }

    public static boolean save(Gamemode gamemode) {
        if(!load(gamemode)) { return false; }
        collection.insertOne(new Document("_id", gamemode.getName())
                .append("totalPlayersRecord", gamemode.getTotalPlayersRecord().toDocument()));
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Gamemode)) {
            return false;
        }

        Gamemode gamemode = (Gamemode) o;

        return Objects.equals(name, gamemode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}

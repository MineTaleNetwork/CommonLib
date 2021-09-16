package cc.minetale.commonlib.modules.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.Date;

@Getter @Setter @AllArgsConstructor
public class RecordValue {

    private final Date date;
    private final String value;

    public RecordValue(Document document) {
        this.date = document.getDate("date");
        this.value = document.getString("value");
    }

    public Document toDocument() {
        var document = new Document();
        document.append("date", this.date);
        document.append("value", this.value);
        return document;
    }

}

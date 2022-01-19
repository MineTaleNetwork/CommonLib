package cc.minetale.commonlib.util;

import cc.minetale.commonlib.CommonLib;
import lombok.experimental.UtilityClass;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.Document;
import org.bson.RawBsonDocument;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class BsonUtil {

    private static final DocumentCodec codec = new DocumentCodec();

    public static Document writeToBson(Object object) {
        try {
            final var mapper = CommonLib.getBsonMapper();

            var out = new ByteArrayOutputStream();
            mapper.writeValue(out, object);
            var bytes = out.toByteArray();

            var raw = new RawBsonDocument(bytes);

            var decoderContext = DecoderContext.builder().build();
            return codec.decode(new BsonDocumentReader(raw), decoderContext);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T readFromBson(Document document, Class<T> clazz) {
        try {
            final var mapper = CommonLib.getBsonMapper();

            var raw = new RawBsonDocument(document, codec);
            var in = new ByteArrayInputStream(raw.getByteBuffer().array());

            return mapper.readValue(in, clazz);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

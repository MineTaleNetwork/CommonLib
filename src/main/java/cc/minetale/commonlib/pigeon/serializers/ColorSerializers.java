package cc.minetale.commonlib.pigeon.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.awt.*;
import java.io.IOException;

public class ColorSerializers {
    public static class Serializer extends StdSerializer<Color> {
        public Serializer() {
            this(null);
        }

        public Serializer(Class<Color> t) {
            super(t);
        }

        @Override
        public void serialize(Color value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeNumber(value.getRGB());
        }
    }

    public static class Deserializer extends StdDeserializer<Color> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Color deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            var codec = jp.getCodec();
            int rgba = codec.readValue(jp, Integer.class);
            return new Color(rgba, true);
        }
    }
}

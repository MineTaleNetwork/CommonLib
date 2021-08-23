package cc.minetale.commonlib.modules.pigeon;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import com.google.gson.*;
import lombok.Getter;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Makes making converters easier for classes that have exporting and importing them as documents. <br>
 * Due to being unable to tell what the parameter types are in these circumstances (along with nested documents), <br>
 * converting requires you to make and specify a {@linkplain DefinedDocument}, <br>
 * which still cuts down on the required work when modifying a class's field <br>
 * since you only need to update them for properties with a parameterized type or ones that nest a document. <br>
 *
 * @deprecated This should be avoided for now as it's not fully finished. <br>
 * Parameterized types (especially nested ones) are painful to work with because of type erasure. <br>
 * There are three possible fixes I can think of: <br>
 * <ul>
 *     <li>Recursively get all nested parameters using {@linkplain ParameterizedType#getActualTypeArguments()} and
 *     save their {@linkplain Class#getCanonicalName()} in a {@linkplain JsonArray} that represents the order of these parameters.</li>
 *     <li>Extract the functionality of converters from Pigeon into its own project and extend them with
 *     the ability of converting objects into {@linkplain Document}s. <br>
 *     Converters could also return a/o contain additional data that could be used by other converters during the process of converting an object.</li>
 *     <li>Use third-party tools to help with parameterized types.</li>
 * </ul>
 *
 */
@Deprecated
public class DocumentHelper {

    public static Document convertToValue(JsonElement element, @Nullable DefinedDocument definition) {
        var data = element.getAsJsonObject();

        definition = Objects.requireNonNullElse(definition, new DefinedDocument(data.getAsJsonObject("definition")));
        var document = new Document();

        for(Map.Entry<String, JsonElement> ent : data.entrySet()) {
            var key = ent.getKey();
            var value = ent.getValue();

            if(value.isJsonObject()) {
                var json = value.getAsJsonObject();

                try {
                    Class<?> clazz = Class.forName(json.get("type").getAsString());

                    Object convertedValue;
                    if(clazz.equals(Document.class)) {
                        var nestedDocument = (DefinedDocument) definition.getPropertyParameters(key);
                        if(nestedDocument == null) { continue; }

                        convertedValue = convertToValue(json, nestedDocument);
                    } else if(clazz.getGenericSuperclass() instanceof ParameterizedType) {
                        var definedProperty = (DefinedType) definition.getPropertyParameters(key);
                        if(definedProperty == null) { continue; }

                        Type trueType = definedProperty.getType();

                        Converter<Object> converter = Pigeon.getPigeon()
                                .getConvertersRegistry().getConverterForType(trueType);

                        convertedValue = converter.convertToValue(trueType, json);
                    } else {
                        Converter<Object> converter = Pigeon.getPigeon()
                                .getConvertersRegistry().getConverterForType(clazz);

                        convertedValue = converter.convertToValue(clazz, json);
                    }

                    document.put(key, convertedValue);
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if(value.isJsonNull()) {
                document.put(key, null);
            }
        }

        return document;
    }

    /**
     * @param definition Contains definitions for properties which type is parameterized or holds a nested {@linkplain Document}. <br>
     *                   You can pass {@code null} if the provided {@linkplain Document} doesn't contain any aforementioned properties.
     * @throws IllegalArgumentException If provided {@linkplain Document} contains any properties which type is parameterized or holds a nested {@linkplain Document}, but no {@linkplain DefinedDocument} has been provided.
     */
    public static JsonElement convertToSimple(Document value, @Nullable DefinedDocument definition, boolean isNested) {
        var data = new JsonObject();

        if(!isNested && definition != null)
            data.add("definition", definition.simplify());

        var properties = new JsonObject();

        for(Map.Entry<String, Object> ent : value.entrySet()) {
            var documentKey = ent.getKey();
            var documentValue = ent.getValue();

            if(documentValue != null) {
                Class<?> type = documentValue.getClass();

                JsonElement simpleValue;

                try {
                    if(documentValue instanceof Document) {
                        Document nestedDocument = (Document) documentValue;

                        if(definition == null)
                            throw new IllegalArgumentException("Document contains a nested document at \"" + documentKey + "\", but no DefinedDocument has been provided. Nested documents need to be defined!");

                        var nestedDefinition = (DefinedDocument) definition.getPropertyParameters(documentKey);
                        if(nestedDefinition == null)
                            throw new IllegalArgumentException("Document contains a nested document at \"" + documentKey + "\", but it hasn't been defined.");

                        simpleValue = convertToSimple(nestedDocument, nestedDefinition, true);
                    } else if(type.getGenericSuperclass() instanceof ParameterizedType) {
                        if(definition == null)
                            throw new IllegalArgumentException("Document contains a parameterized type at \"" + documentKey + "\", but no DefinedDocument has been provided. Parameterized types need to be defined!");

                        var parametersDefinition = (DefinedType) definition.getPropertyParameters(documentKey);
                        if(parametersDefinition == null)
                            throw new IllegalArgumentException("Document contains a parameterized type at \"" + documentKey + "\", but it hasn't been defined.");

                        try {
                            var propertyType = parametersDefinition.getType();

                            Converter<Object> converter = Pigeon.getPigeon()
                                    .getConvertersRegistry().getConverterForType(propertyType);

                            simpleValue = converter.convertToSimple(propertyType, documentValue);
                        } catch(ClassNotFoundException e) {
                            e.printStackTrace();
                            return JsonNull.INSTANCE;
                        }
                    } else {
                        Converter<Object> converter = Pigeon.getPigeon()
                                .getConvertersRegistry().getConverterForType(type);

                        simpleValue = converter.convertToSimple(type, documentValue);
                    }
                } catch(IllegalArgumentException e) {
                    e.printStackTrace();
                    return JsonNull.INSTANCE;
                }

                properties.add(documentKey, simpleValue);
            } else {
                properties.add(documentKey, JsonNull.INSTANCE);
            }
        }

        data.add("properties", properties);

        return data;
    }

    /**
     * @param definition Contains definitions for properties which type is parameterized or holds a nested {@linkplain Document}. <br>
     *                   You can pass {@code null} if the provided {@linkplain Document} doesn't contain any aforementioned properties.
     * @throws IllegalArgumentException If provided {@linkplain Document} contains any properties which type is parameterized or holds a nested {@linkplain Document}, but no {@linkplain DefinedDocument} has been provided.
     */
    public static JsonElement convertToSimple(Document value, @Nullable DefinedDocument definition) {
        return convertToSimple(value, definition, false);
    }

    public abstract static class ParameterType {
        protected Class<?> type;

        protected abstract void load(JsonElement element);
        public abstract JsonElement simplify();
    }

//    @Getter @Setter @AllArgsConstructor
//    private static class PropertyType {
//        @NotNull private final String type; //Either "nestedDocument" or "parameterizedType"
//        @Nullable private final List<ParameterType> parameters;
//        @Nullable private final DefinedDocument nestedDocument;
//    }

    @Getter
    public static class DefinedType extends ParameterType {

        /**
         * A type can be also parameterized in a nested way. <br>
         * <br>
         * For example if a property is of type {@code Map<String, Map<UUID, Integer>>}. <br>
         * We know the first type, which is Map. <br>
         * Then {@linkplain DefinedType} kicks in and defines String as it's first parameter, easy. <br>
         * But now we'd have a problem because Map is our second parameter, which is also parameterized. <br>
         * That's why {@linkplain DefinedType} needs to be able to handle nested parameters.
         */

        private boolean isParameterized;
        private List<ParameterType> parameters;

        /**
         * Defines a not parameterized type.
         */
        private DefinedType(Class<?> type) {
            this.type = type;
        }

        /**
         * Defines a parameterized type.
         */
        private DefinedType(Class<?> type, ParameterType... parameters) {
            this.type = type;

            this.isParameterized = true;
            this.parameters = Arrays.asList(parameters);
        }

        private DefinedType(JsonObject data) {
            load(data);
        }

        @Override
        protected void load(JsonElement element) {
            if(!element.isJsonObject()) { return; }
            var data = element.getAsJsonObject();

            try {
                this.type = Class.forName(data.get("type").getAsString());
                this.isParameterized = data.get("isParameterized").getAsBoolean();

                if(this.isParameterized) {
                    this.parameters = new ArrayList<>();

                    var definedParameters = data.get("parameters").getAsJsonArray();
                    for(JsonElement parameter : definedParameters) {
                        this.parameters.add(new DefinedType(parameter.getAsJsonObject()));
                    }
                }
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Defines a non parameterized type. <br>
         * Use {@linkplain DefinedType#ofParameterized(Class, ParameterType...)} for parameterized types instead.
         */
        public static DefinedType of(Class<?> clazz) {
            Type type = clazz.getGenericSuperclass();
            if(type instanceof ParameterizedType)
                throw new IllegalArgumentException("Tried to define \"" + clazz.getCanonicalName() + "\", but it's parameterized!");

            return new DefinedType(clazz);
        }

        /**
         * Defines a parameterized type. <br>
         * Use {@linkplain DefinedType#of(Class)} for not parameterized types instead.
         */
        public static DefinedType ofParameterized(Class<?> clazz, ParameterType... parameters) {
            Type type = clazz.getGenericSuperclass();
            if(!(type instanceof ParameterizedType))
                throw new IllegalArgumentException("Tried to define \"" + clazz.getCanonicalName() + "\", but it's not parameterized!");

            return new DefinedType(clazz, parameters);
        }

        public Type getType() throws ClassNotFoundException {
            if(isParameterized) {
                Type[] types = new Type[this.parameters.size()];
                for(int i = 0; i < this.parameters.size(); i++) {
                    var parameter = this.parameters.get(i);
                    if(parameter instanceof DefinedType) {
                        DefinedType definedType = (DefinedType) parameter;

                        types[i] = definedType.getType();
                    } else if(parameter instanceof DefinedDocument) {
                        types[i] = Document.class;
                    }
                }
                return TypeUtils.parameterize(this.type, types);
            } else {
                return this.type;
            }
        }

        @Override
        public JsonElement simplify() {
            var data = new JsonObject();

            data.add("type", new JsonPrimitive(this.type.getName()));
            data.add("isParameterized", new JsonPrimitive(this.isParameterized));
            if(this.isParameterized) {
                var definedParameters = new JsonArray();
                for(ParameterType parameter : this.parameters) {
                    definedParameters.add(parameter.simplify());
                }
                data.add("parameters", definedParameters);
            }

            return data;
        }

    }

    /**
     * Defined properties' names are representing the document's properties, not the class's fields. <br>
     * If a property name differs from the field's one, make sure you're defining the correct name.
     */
    public static class DefinedDocument extends ParameterType {

        private boolean isNested;

        private final Map<String, ParameterType> definedProperties = new HashMap<>();

        public DefinedDocument(boolean isNested) {
            this.isNested = isNested;
            if(this.isNested)
                this.type = Document.class;
        }

        public DefinedDocument(JsonObject data) {
            load(data);
        }

        @Override
        protected void load(JsonElement element) {
            if(!element.isJsonObject()) { return; }
            var data = element.getAsJsonObject();

            this.isNested = data.get("isNested").getAsBoolean();

            for(Map.Entry<String, JsonElement> ent : data.getAsJsonObject("properties").entrySet()) {
                var property = ent.getKey();

                var parameterObj = ent.getValue().getAsJsonObject();
                if(Document.class.getName().equals(parameterObj.get("type").getAsString())) {
                    this.definedProperties.put(property, new DefinedDocument(parameterObj));
                } else {
                    this.definedProperties.put(property, new DefinedType(ent.getValue().getAsJsonObject()));
                }
            }
        }

//        private DefinedDocument defineProperty(String property, ParameterType parameterType) {
//            this.definedProperties.put(property, parameterType);
//            return this;
//        }

        public DefinedDocument defineProperty(String property, ParameterType... parameterTypes) {
            for(ParameterType parameterType : parameterTypes) {
                this.definedProperties.put(property, parameterType);
            }
            return this;
        }

        public DefinedDocument removeDefinedProperty(String property) {
            this.definedProperties.remove(property);
            return this;
        }

        public ParameterType getPropertyParameters(String property) {
            return this.definedProperties.get(property);
        }

        public void testAgainst(Class<?> clazz, boolean verbose) {
            if(verbose)
                System.out.println("Testing a DefinedDocument against \"" + clazz.getCanonicalName() + "\". Turn off verbose after testing.");

            for(Field field : clazz.getDeclaredFields()) {
                var type = field.getGenericType();
                if(type instanceof ParameterizedType) {
                    if(getPropertyParameters(field.getName()) == null)
                        System.err.println("There's no property definition for field \"" + field.getName() + "\", but its type is parameterized.");
                } else if(!isNested && verbose) {
                    if(getPropertyParameters(field.getName()) != null)
                        System.out.println("There's a definition for field \"" + field.getName() + "\", but there's no need for it to exist unless it's a nested document.");
                }
            }
        }

        @Override
        public JsonObject simplify() {
            var data = new JsonObject();

            data.add("type", new JsonPrimitive(Document.class.getName()));
            data.add("isNested", new JsonPrimitive(this.isNested));

            var properties = new JsonObject();
            for(Map.Entry<String, ParameterType> ent : this.definedProperties.entrySet()) {
                var property = ent.getKey();
                var parameter = ent.getValue();

                properties.add(property, parameter.simplify());
            }

            data.add("properties", properties);

            return data;
        }

    }

}

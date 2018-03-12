package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3SchemaParser extends SchemaParser {

    Map<Class<? extends Schema>, List<String>> keywordsMap;

    public OAS3SchemaParser(JsonObject schemaRoot, String scope) {
        this(schemaRoot, scope, new HashMap<>());
    }

    public OAS3SchemaParser(JsonObject schemaRoot, String scope, Map<String, Schema> refsCache) {
        super(schemaRoot, scope, refsCache);
        keywordsMap = new HashMap<>();
        fillKeywordsMap();
    }

    // This map is used by MissingTypeSchema
    private void fillKeywordsMap() {
        keywordsMap.put(GenericNumberSchema.class, Arrays.asList(
                "multipleOf",
                "maximum",
                "exclusiveMaximum",
                "minimum",
                "exclusiveMinimum"
        ));
        keywordsMap.put(OAS3StringSchema.class, Arrays.asList(
                "maxLength",
                "minLength",
                "pattern"
        ));
        keywordsMap.put(ArraySchema.class, Arrays.asList(
                "items",
                "maxItems",
                "minItems",
                "uniqueItems"
        ));
        keywordsMap.put(OAS3ObjectSchema.class, Arrays.asList(
                "required",
                "properties",
                "additionalProperties"
        ));
        keywordsMap.put(EnumSchema.class, Arrays.asList("enum"));
        //TODO fill with other keywords?
    }

    @Override
    public Schema instantiateSchema(Class<? extends Schema> schemaClass, JsonObject obj) {
        try {
            Constructor<? extends Schema> constructor = schemaClass.getConstructor(JsonObject.class, SchemaParser.class);
            return constructor.newInstance(obj, this);
        } catch (Exception e) {
            // Something happened during schema instantiation (wrong schema)
            System.out.println("Wrong schema! " + e);
            e.printStackTrace();
            return null;
        }
    }

    private Class<? extends NumberSchema> solveIntegerType(JsonObject obj) {
        return ("int64".equals(obj.getString("format"))) ? LongSchema.class : IntegerSchema.class;
    }

    private Class<? extends NumberSchema> solveFloatingPointType(JsonObject obj) {
        return ("double".equals(obj.getString("format"))) ? DoubleSchema.class : FloatSchema.class;
    }

    @Override
    public Class<? extends Schema> solveType(JsonObject obj) {
        String type = obj.getString("type");
        if (type == null) {
            return MissingTypeSchema.class;
        } else {
            if (obj.containsKey("enum")) {
                return EnumSchema.class;
            } else {
                switch (type) {
                    case "integer":
                        return solveIntegerType(obj);
                    case "number":
                        return solveFloatingPointType(obj);
                    case "string":
                        return OAS3StringSchema.class;
                    case "object":
                        return OAS3ObjectSchema.class;
                    case "boolean":
                        return BooleanSchema.class;
                    default:
                        return OAS3StringSchema.class; // Should throw an error here!
                }
            }
        }
    }

    @Override
    public Class<? extends Schema> inferType(JsonObject obj) {
        return MissingTypeSchema.class;
    }

    @Override
    public Map<Class<? extends Schema>, List<String>> getKeywordsMap() {
        return keywordsMap;
    }

    @Override
    public Pattern parseFormat(String format) {
        switch (format) {
            case "binary":
                return null;
            case "byte":
                return RegularExpressions.BASE64;
            case "date":
                return RegularExpressions.DATE;
            case "date-time":
                return RegularExpressions.DATETIME;
            case "ipv4":
                return RegularExpressions.IPV4;
            case "ipv6":
                return RegularExpressions.IPV6;
            case "hostname":
                return RegularExpressions.HOSTNAME;
            case "email":
                return RegularExpressions.EMAIL;
            default:
                return null; // Should throw an exception
        }
    }

    @Override
    public <T> PreValidationSchema buildPreValidationLogic(JsonObject jsonSchema, BaseSchema<T> schema) {
        return new OAS3PreValidationSchema(jsonSchema, this, schema);
    }

    @Override
    public PostValidationSchema buildPostValidationLogic(JsonObject schema) {
        return new OAS3PostValidationSchema(schema, this);
    }
}

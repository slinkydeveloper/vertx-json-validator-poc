package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.WrongSchemaException;
import io.vertx.ext.json.validator.schema.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3SchemaParser extends SchemaParser {

    Map<Class<? extends Schema>, List<String>> keywordsMap;

    public OAS3SchemaParser(JsonObject schemaRoot, String scope) {
        this(schemaRoot, scope, new HashMap<>(), new SchemaParserProperties());
    }

    public OAS3SchemaParser(JsonObject schemaRoot, String scope, Map<String, Schema> refsCache, SchemaParserProperties properties) {
        super(schemaRoot, scope, refsCache, properties);
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
        keywordsMap.put(StringSchema.class, Arrays.asList(
                "maxLength",
                "minLength",
                "pattern"
        ));
        keywordsMap.put(OAS3ArraySchema.class, Arrays.asList(
                "items",
                "maxItems",
                "minItems",
                "uniqueItems"
        ));
        keywordsMap.put(OAS3ObjectSchema.class, Arrays.asList(
                "required",
                "properties",
                "additionalProperties",
                "minProperties",
                "maxProperties"
        ));
        keywordsMap.put(EnumSchema.class, Arrays.asList("enum"));
        keywordsMap.put(NotSchema.class, Arrays.asList("not"));
        keywordsMap.put(AllOfSchema.class, Arrays.asList("allOf"));
        //TODO fill with other keywords?
    }

    @Override
    public Schema instantiateSchema(Class<? extends Schema> schemaClass, JsonObject obj) {
        try {
            Constructor<? extends Schema> constructor = schemaClass.getConstructor(JsonObject.class, SchemaParser.class);
            return constructor.newInstance(obj, this);
        } catch (Exception e) {
            // Something happened during schema instantiation (wrong schema)
            System.out.println("Wrong schema! " + e.getCause());
            e.printStackTrace();
            if (e.getCause() instanceof WrongSchemaException)
                throw (WrongSchemaException)e.getCause();
            else
                throw new WrongSchemaException(e.getMessage(), e.getCause());
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
        if (obj.containsKey("enum")) {
            return EnumSchema.class;
        } else if (obj.containsKey("not")){
            return NotSchema.class;
        } else if (obj.containsKey("allOf")) {
            return AllOfSchema.class;
        } else if (type != null) {
            switch (type) {
                case "integer":
                    return solveIntegerType(obj);
                case "number":
                    return solveFloatingPointType(obj);
                case "string":
                    return StringSchema.class;
                case "object":
                    return OAS3ObjectSchema.class;
                case "array":
                    return OAS3ArraySchema.class;
                case "boolean":
                    return BooleanSchema.class;
                default:
                    return MissingTypeSchema.class; // Should throw an error here!
            }
        } else {
            if (properties.isForceTypeKeywordInference()) {
                return inferType(obj);
            } else
                return MissingTypeSchema.class;
        }
    }

    @Override
    public Class<? extends Schema> inferType(JsonObject obj) {
        List<Class<? extends Schema>> schemas = solveMissingTypeSchemas(obj);
        if (schemas.size() == 1)
            return schemas.get(0);
        else if (schemas.size() > 1)
            return MissingTypeSchema.class;
        else
            throw new IllegalArgumentException("Cannot infer type");
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

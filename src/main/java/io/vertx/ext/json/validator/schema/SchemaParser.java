package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class SchemaParser {

    protected final JsonObject schemaRoot;
    protected final String scope;
    protected final Map<String, Schema> refsCache;

    public SchemaParser(JsonObject schemaRoot, String scope, Map<String, Schema> refsCache) {
        this.schemaRoot = schemaRoot;
        this.scope = scope;
        this.refsCache = refsCache;
    }

    public SchemaParser(JsonObject schemaRoot, String scope) {
        this.schemaRoot = schemaRoot;
        this.scope = scope;
        this.refsCache = new HashMap<>();
    }

    public Schema parse() {
        return this.parse(schemaRoot);
    }

    public abstract Schema instantiateSchema(Class<? extends Schema> schemaClass, JsonObject obj);

    public Schema parse(JsonObject obj) {
        return this.instantiateSchema(this.solveType(obj), obj);
    }

    public Schema parse(JsonObject obj, String defaultType) {
        if (!obj.containsKey("type")) obj.put("type", defaultType);
        return this.instantiateSchema(this.solveType(obj), obj);
    }

    // The "type" keyword in oas can be a single type, when in draft-7 can be an array of types!
    public abstract Class<? extends Schema> solveType(JsonObject obj);

    // When type is missing you should infer type from schema, and again this is different between specs
    public abstract Class<? extends Schema> inferType(JsonObject obj);

    public abstract Map<Class<? extends Schema>, List<String>> getKeywordsMap();

    // format keyword is different between oas and json schema specs
    public abstract Pattern parseFormat(String format);

    public abstract <T> PreValidationSchema buildPreValidationLogic(JsonObject jsonSchema, BaseSchema<T> schema);

    public abstract PostValidationSchema buildPostValidationLogic(JsonObject schema);
}

package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class ReflectedSchema {

    JsonObject originalJson;
    protected SchemaParser parser;

    public ReflectedSchema(JsonObject originalJson, SchemaParser parser) {
        this.originalJson = originalJson;
        this.parser = parser;
    }

    public JsonObject getOriginalJson() {
        return originalJson;
    }

    public SchemaParser getParser() {
        return parser;
    }

    protected void assignDouble(String propertyName, String fieldName) {
        try {
            parser.assignProperty(this.originalJson, jsonObject -> jsonObject.getDouble(propertyName), fieldName, Double.class, null, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected void assignDouble(String propertyName) {
        this.assignDouble(propertyName, propertyName);
    }

    protected void assignInteger(String propertyName, String fieldName) {
        try {
            parser.assignProperty(this.originalJson, jsonObject -> jsonObject.getInteger(propertyName), fieldName, Integer.class, null, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected void assignInteger(String propertyName) {
        this.assignInteger(propertyName, propertyName);
    }

    protected void assignBoolean(String propertyName, String fieldName) {
        try {
            parser.assignProperty(this.originalJson, jsonObject -> jsonObject.getBoolean(propertyName), fieldName, Boolean.class, null, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected void assignBoolean(String propertyName) {
        this.assignBoolean(propertyName, propertyName);
    }

    protected void assign(String propertyName, String fieldName) {
        try {
            parser.assignProperty(originalJson, jsonObject -> jsonObject.getValue(propertyName), fieldName, Object.class, null, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected <R> void assignObject(String propertyName, String fieldName, Class fieldType, Function<JsonObject, R> map) {
        try {
            parser.assignProperty(originalJson, jsonObject -> jsonObject.getJsonObject(propertyName), fieldName, fieldType, map, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected <R> void assignArray(String propertyName, String fieldName, Function<JsonArray, List<R>> map) {
        try {
            parser.assignProperty(originalJson, jsonObject -> jsonObject.getJsonArray(propertyName), fieldName, List.class, map, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected <T, R> void manipulateAndAssign(Function<JsonObject, T> propertyGetter, String fieldName, Class fieldType, Function<T, R> map) {
        try {
            parser.assignProperty(originalJson, propertyGetter, fieldName, fieldType, map, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

}

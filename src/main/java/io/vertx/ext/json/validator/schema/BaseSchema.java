package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class BaseSchema<T> implements Schema<T> {

    JsonObject originalJson;
    protected SchemaParser parser;

    public BaseSchema(JsonObject obj, SchemaParser parser){
        this.originalJson = obj;
        this.parser = parser;
    }

    void assignDouble(String propertyName, String fieldName) {
        try {
            parser.assignProperty(this.originalJson, jsonObject -> jsonObject.getDouble(propertyName), fieldName, Double.class, null, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    void assignDouble(String propertyName) {
        this.assignDouble(propertyName, propertyName);
    }

    void assignBoolean(String propertyName, String fieldName) {
        try {
            parser.assignProperty(this.originalJson, jsonObject -> jsonObject.getBoolean(propertyName), fieldName, Boolean.class, null, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    void assignBoolean(String propertyName) {
        this.assignBoolean(propertyName, propertyName);
    }

    <R> void assignObject(String propertyName, String fieldName, Class fieldType, Function<JsonObject, R> map) {
        try {
            parser.assignProperty(originalJson, jsonObject -> jsonObject.getJsonObject(propertyName), fieldName, fieldType, map, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

}

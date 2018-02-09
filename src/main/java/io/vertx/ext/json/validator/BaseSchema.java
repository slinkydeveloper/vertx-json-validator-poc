package io.vertx.ext.json.validator;

import io.vertx.core.json.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class BaseSchema<T> implements Schema<T> {

    JsonObject originalJson;

    BaseSchema(JsonObject obj){
        this.originalJson = obj;
    }

    void assignProperty(String propertyName, String fieldName, Class c) throws NoSuchFieldException, IllegalAccessException {
        Field f = c.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(this, this.originalJson.getValue(propertyName));
    }

    void assignProperty(String propertyName, Class c) throws NoSuchFieldException, IllegalAccessException {
        this.assignProperty(propertyName, propertyName, c);
    }

    void parseObject(String propertyName, String fieldName, Function<JsonObject, Object> fn, Class c) throws NoSuchFieldException, IllegalAccessException {
        if (this.originalJson.containsKey(propertyName)) {
            Object result = fn.apply(this.originalJson.getJsonObject(propertyName));
            c.getDeclaredField(fieldName).setAccessible(true);
            c.getDeclaredField(fieldName).set(this, result);
        }
    }

}

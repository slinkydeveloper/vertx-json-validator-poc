package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class ReflectedSchema {

    private JsonObject originalJson;
    private SchemaParser parser;

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

    // TODO for all reflection methods check the right exception and blabla

    private void invokeSetter(String fieldName, Class fieldType, Class c, Object instance, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method setter = c.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), fieldType);
        setter.invoke(instance, value);
    }

    <T, R> void assignProperty(JsonObject jsonObject, Function<JsonObject, T> extractProperty, String fieldName, Class fieldType, Function<T, R> map, boolean emptyNewInstance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        T propertyValue = extractProperty.apply(jsonObject);
        if (propertyValue != null) {
            if (map != null) {
                R transformedValue = map.apply(propertyValue);
                if (transformedValue != null)
                    invokeSetter(fieldName, fieldType, this.getClass(), this, transformedValue);
            } else {
                invokeSetter(fieldName, fieldType, this.getClass(), this, propertyValue);
            }
        } else if (emptyNewInstance) {
            invokeSetter(fieldName, fieldType, this.getClass(), this, fieldType.newInstance());
        }
    }

    protected void assignDouble(String propertyName, String fieldName) {
        try {
            this.assignProperty(this.originalJson, jsonObject -> jsonObject.getDouble(propertyName), fieldName, Double.class, null, false);
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
            this.assignProperty(this.originalJson, jsonObject -> jsonObject.getInteger(propertyName), fieldName, Integer.class, null, false);
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
            this.assignProperty(this.originalJson, jsonObject -> jsonObject.getBoolean(propertyName), fieldName, Boolean.class, null, false);
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
            this.assignProperty(originalJson, jsonObject -> jsonObject.getValue(propertyName), fieldName, Object.class, null, false);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected <R> void assignObject(String propertyName, String fieldName, Class fieldType, Function<JsonObject, R> map, boolean instantiateEmpty) {
        try {
            this.assignProperty(originalJson, jsonObject -> jsonObject.getJsonObject(propertyName), fieldName, fieldType, map, instantiateEmpty);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected <R> void assignArray(String propertyName, String fieldName, Function<JsonArray, Collection<R>> map, Class c, boolean instantiateEmpty) {
        try {
            this.assignProperty(originalJson, jsonObject -> jsonObject.getJsonArray(propertyName), fieldName, c, map, instantiateEmpty);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

    protected <T> T get(String propertyName, Class c) {
        try {
            return (T)c.cast(this.getOriginalJson().getValue(propertyName));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Wrong type for property " + propertyName);
        }
    }

    protected <T> Optional<T> getOptional(String propertyName, Class c) {
        try {
            return Optional.of((T)c.cast(this.getOriginalJson().getValue(propertyName)));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Wrong type for property " + propertyName);
        }
    }

    protected <T> T getRequired(String propertyName, Class c) {
        try {
            Object v = this.getOriginalJson().getValue(propertyName);
            if (v == null)
                throw new IllegalArgumentException("Missing property " + propertyName);
            return (T) c.cast(v);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Wrong type for property " + propertyName);
        }
    }

}

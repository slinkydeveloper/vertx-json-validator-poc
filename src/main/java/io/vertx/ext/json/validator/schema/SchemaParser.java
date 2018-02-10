package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class SchemaParser {

    private void invokeSetter(String fieldName, Class fieldType, Class c, Object instance, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method setter = c.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), fieldType);
        setter.invoke(instance, value);
    }

    <T, R> void assignProperty(JsonObject jsonObject, Function<JsonObject, T> extractProperty, String fieldName, Class fieldType, Function<T, R> map, Class instanceType, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        T propertyValue = extractProperty.apply(jsonObject);
        if (map != null) {
            R transformedValue = map.apply(propertyValue);
            if (transformedValue != null)
                invokeSetter(fieldName, fieldType, instanceType, instance, transformedValue);
        }
        else if (propertyValue != null) {
            invokeSetter(fieldName, fieldType, instanceType, instance, propertyValue);
        }
    }

    // The "type" keyword in oas can be a single type, when in draft-7 can be an array of types!
    public abstract List<String> solveTypes(JsonObject obj);

    // When type is missing you should infer type from schema, and again this is different between specs
    public abstract String inferType(JsonObject obj);
}

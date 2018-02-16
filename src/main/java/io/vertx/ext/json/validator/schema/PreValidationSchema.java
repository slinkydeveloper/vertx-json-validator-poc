package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 *
 * For every Schema, there are common keywords that we can implement as "pre validation"
 * They have different behaviours between specs:
 * oas3 -> default, nullable
 * draft7 -> default, const
 */
public abstract class PreValidationSchema extends ReflectedSchema {

    Object defaultValue;

    public <T> PreValidationSchema(JsonObject obj, SchemaParser parser, BaseSchema<T> schema) {
        super(obj, parser);
        if (originalJson.containsKey("default"))
            this.setDefaultValue(schema.checkType(originalJson.getValue("default")));
    }

    public abstract <T> Function<Object, ValidationStep<Object, T>> getPreValidationLogic();

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    void assign(String propertyName, String fieldName, Class fieldType) {
        try {
            parser.assignProperty(originalJson, jsonObject -> jsonObject.getValue(propertyName), fieldName, fieldType, null, this.getClass(), this);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
            e.printStackTrace();
        }
    }

}

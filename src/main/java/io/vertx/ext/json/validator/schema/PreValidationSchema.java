package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.monads.ValidationStep;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 *
 * For every Schema, there are common keywords that we can implement as "pre validation"
 * They have different behaviours between specs:
 * oas3 -> default, nullable
 * draft7 -> default
 */
public abstract class PreValidationSchema extends ReflectedSchema {

    Object defaultValue;

    public <T> PreValidationSchema(JsonObject obj, SchemaParser parser, BaseSchema<T> schema) {
        super(obj, parser);
        if (getOriginalJson().containsKey("default"))
            this.setDefaultValue(schema.checkType(getOriginalJson().getValue("default")));
    }

    public abstract <T> Function<Object, ValidationStep<Object, T, ValidationException>> getPreValidationLogic();

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

}

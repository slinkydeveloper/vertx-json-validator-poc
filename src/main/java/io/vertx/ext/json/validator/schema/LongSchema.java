package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class LongSchema extends NumberSchema<Long> {
    public LongSchema(JsonObject jsonObject, SchemaParser parser, JsonPointer pointer) {
        super(jsonObject, parser, pointer);
    }

    @Override
    public Class getRequiredType() {
        return Long.class;
    }

    // By default JSON Parser deserialize fp number as Integer.
    // But we need to give back to user a Long
    @Override
    public Long checkType(Object obj) {
        if (obj instanceof Long)
            return (Long)obj;
        else if (obj instanceof Integer) {
            return ((Integer)obj).longValue();
        } else {
            throw ValidationExceptionFactory.generate("Wrong type, expected " + getRequiredType(), obj, pointer);
        }
    }
}

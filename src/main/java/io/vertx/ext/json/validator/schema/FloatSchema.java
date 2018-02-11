package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class FloatSchema extends NumberSchema<Float> {
    public FloatSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }

    @Override
    public Class getRequiredType() {
        return Float.class;
    }

    // By default JSON Parser deserialize fp number as Double.
    // But we need to give back to user a Float
    @Override
    public Float checkType(Object obj) {
        if (obj instanceof Float)
            return (Float)obj;
        else if (obj instanceof Double) {
            return ((Double)obj).floatValue();
        } else {
            throw ValidationExceptionFactory.generateNotMatchValidationException("Wrong type");
        }
    }
}

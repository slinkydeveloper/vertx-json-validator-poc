package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class DoubleSchema extends NumberSchema<Double> {
    public DoubleSchema(JsonObject jsonObject, SchemaParser parser, JsonPointer pointer) {
        super(jsonObject, parser, pointer);
    }

    @Override
    public Class getRequiredType() {
        return Double.class;
    }

    // Note that number includes integer:
    // https://tools.ietf.org/html/draft-wright-json-schema-validation-00#section-5.21
    @Override
    public Double checkType(Object obj) {
        if (obj instanceof Double)
            return (Double)obj;
        else if (obj instanceof Float) {
            return ((Float)obj).doubleValue();
        } else if (obj instanceof Integer) {
            return ((Integer)obj).doubleValue();
        } else if (obj instanceof Long) {
            return ((Long)obj).doubleValue();
        } else {
            throw ValidationExceptionFactory.generate("Wrong type, expected " + getRequiredType(), obj, pointer);
        }
    }
}

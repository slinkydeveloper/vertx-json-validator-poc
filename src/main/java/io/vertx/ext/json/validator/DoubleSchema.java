package io.vertx.ext.json.validator;

import io.vertx.core.json.JsonObject;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class DoubleSchema extends NumberSchema<Double> {
    public DoubleSchema(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public Class getRequiredType() {
        return Double.class;
    }
}

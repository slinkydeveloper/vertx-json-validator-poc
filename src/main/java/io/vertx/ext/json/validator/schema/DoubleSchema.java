package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class DoubleSchema extends NumberSchema<Double> {
    public DoubleSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }

    @Override
    public Class getRequiredType() {
        return Double.class;
    }
}

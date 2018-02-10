package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class IntegerSchema extends NumberSchema<Integer> {

    public IntegerSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }

    @Override
    public Class getRequiredType() {
        return Integer.class;
    }
}

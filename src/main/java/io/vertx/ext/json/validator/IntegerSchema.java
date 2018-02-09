package io.vertx.ext.json.validator;

import io.vertx.core.json.JsonObject;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class IntegerSchema extends NumberSchema<Integer> {
    public IntegerSchema(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public Class getRequiredType() {
        return Integer.class;
    }
}

package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class IntegerSchema extends NumberSchema<Integer> {

    public IntegerSchema(JsonObject jsonObject, SchemaParser parser, JsonPointer pointer) {
        super(jsonObject, parser, pointer);
    }

    @Override
    public Class getRequiredType() {
        return Integer.class;
    }
}

package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;

/**
 * This schema should be used only when you don't know the number type
 *
 */
public class GenericNumberSchema extends NumberSchema<Number> {
    public GenericNumberSchema(JsonObject jsonObject, SchemaParser parser, JsonPointer pointer) {
        super(jsonObject, parser, pointer);
    }

    @Override
    Class<Number> getRequiredType() {
        return Number.class;
    }
}

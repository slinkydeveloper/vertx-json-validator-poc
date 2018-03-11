package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonObject;

/**
 * This schema should be used only when you don't know the number type
 *
 */
public class GenericNumberSchema extends NumberSchema<Number> {
    public GenericNumberSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }

    @Override
    Class<Number> getRequiredType() {
        return Number.class;
    }
}

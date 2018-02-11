package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.LongSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3LongSchema extends LongSchema {
    public OAS3LongSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }
}

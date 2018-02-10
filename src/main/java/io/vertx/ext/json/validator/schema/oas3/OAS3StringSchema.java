package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.SchemaParser;
import io.vertx.ext.json.validator.schema.StringSchema;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3StringSchema extends StringSchema {
    public OAS3StringSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
    }
}

package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.DoubleSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3DoubleSchema extends DoubleSchema {
    public OAS3DoubleSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }
}

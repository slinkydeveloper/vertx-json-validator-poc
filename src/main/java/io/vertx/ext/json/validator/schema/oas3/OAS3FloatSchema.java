package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.FloatSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3FloatSchema extends FloatSchema {
    public OAS3FloatSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }
}

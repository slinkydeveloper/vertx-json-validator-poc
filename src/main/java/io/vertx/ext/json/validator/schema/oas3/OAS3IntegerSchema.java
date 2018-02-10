package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.IntegerSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3IntegerSchema extends IntegerSchema {
    public OAS3IntegerSchema(JsonObject jsonObject, SchemaParser parser) {
        super(jsonObject, parser);
    }
}

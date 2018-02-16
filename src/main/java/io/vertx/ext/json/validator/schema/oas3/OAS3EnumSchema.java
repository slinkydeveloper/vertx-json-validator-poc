package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.EnumSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3EnumSchema extends EnumSchema {

    public OAS3EnumSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
    }
}

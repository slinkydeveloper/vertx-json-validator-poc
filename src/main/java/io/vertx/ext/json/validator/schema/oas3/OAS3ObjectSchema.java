package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.BaseSchema;
import io.vertx.ext.json.validator.schema.ObjectSchema;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.json.validator.schema.SchemaParser;

import java.util.List;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3ObjectSchema extends ObjectSchema {

    public OAS3ObjectSchema(JsonObject schema, SchemaParser parser) {
        super(schema, parser);
    }
}

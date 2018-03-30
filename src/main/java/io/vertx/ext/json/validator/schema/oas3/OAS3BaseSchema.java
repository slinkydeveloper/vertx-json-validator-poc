package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.schema.BaseSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class OAS3BaseSchema<T> extends BaseSchema<T> {
    public OAS3BaseSchema(JsonObject obj, SchemaParser parser, JsonPointer pointer) {
        super(obj, parser, pointer);
    }
}

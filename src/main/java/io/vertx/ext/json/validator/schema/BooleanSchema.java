package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.schema.BaseSchema;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class BooleanSchema extends BaseSchema<Boolean> {
    public BooleanSchema(JsonObject obj, SchemaParser parser, JsonPointer pointer) {
        super(obj, parser, pointer);
    }

    @Override
    Class<Boolean> getRequiredType() {
        return Boolean.class;
    }

    @Override
    Future<Boolean> validationLogic(Boolean obj) {
        return Future.succeededFuture(obj);
    }
}

package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class StringSchema extends BaseSchema<String> {

    public StringSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
    }

    @Override
    public Future<String> validate(Object obj) {
        return Future.succeededFuture(checkType(obj));
    }

    @Override
    public Class getRequiredType() {
        return String.class;
    }
}

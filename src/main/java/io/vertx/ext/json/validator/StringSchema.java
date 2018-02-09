package io.vertx.ext.json.validator;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class StringSchema extends BaseSchema<String> {
    StringSchema(JsonObject obj) {
        super(obj);
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

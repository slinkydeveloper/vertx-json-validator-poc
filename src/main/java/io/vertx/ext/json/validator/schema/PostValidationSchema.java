package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class PostValidationSchema extends ReflectedSchema {

    public PostValidationSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
    }

    protected abstract <T, R> Optional<Function<T, Future<R>>> getPostValidationLogic();

}

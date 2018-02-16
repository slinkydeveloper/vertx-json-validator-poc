package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.PostValidationSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3PostValidationSchema extends PostValidationSchema {
    public OAS3PostValidationSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
    }

    @Override
    protected <T, R> Optional<Function<T, Future<R>>> getPostValidationLogic() {
        return Optional.empty();
    }
}

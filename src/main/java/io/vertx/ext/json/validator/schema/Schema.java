package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationExceptionFactory;
import io.vertx.ext.json.validator.schema.oas3.OAS3SchemaParser;

import java.lang.reflect.Constructor;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public interface Schema<T> {

    Future<T> validate(Object obj);

    static Schema parseOAS3Schema(JsonObject jsonObject, String scope) {
        return new OAS3SchemaParser(jsonObject, scope).parse();
    }

}

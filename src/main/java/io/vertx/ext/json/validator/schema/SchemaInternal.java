package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public interface SchemaInternal<T> extends Schema<T> {

    JsonObject getOriginalJson();
    SchemaParser getParser();

    Class getRequiredType();

    Future<T> validationLogic(T obj);

    default T checkType(Object obj) {
        if (obj.getClass().equals(getRequiredType())) {
            return (T)obj;
        } else {
            throw ValidationExceptionFactory
                            .generateNotMatchValidationException("Wrong type, expected " + getRequiredType());
        }
    }
}

package io.vertx.ext.json.validator;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public interface Schema<T> {

    Future<T> validate(Object obj);

    public Class getRequiredType();

    default T checkType(Object obj) {
        if (obj.getClass().equals(getRequiredType())) {
            return (T)obj;
        } else {
            throw ValidationException.ValidationExceptionFactory.generateNotMatchValidationException("Wrong type");
        }
    }

    static Schema parse(JsonObject jsonObject) {
        String type = jsonObject.getString("type");
        switch (type) {
            case "object": return new ObjectSchema(jsonObject);
            case "integer": return new IntegerSchema(jsonObject);
            case "string": return new StringSchema(jsonObject);
            default: return new StringSchema(jsonObject);
        }
    }

}

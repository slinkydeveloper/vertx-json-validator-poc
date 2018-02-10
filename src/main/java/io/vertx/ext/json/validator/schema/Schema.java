package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.oas3.OAS3IntegerSchema;
import io.vertx.ext.json.validator.schema.oas3.OAS3ObjectSchema;
import io.vertx.ext.json.validator.schema.oas3.OAS3StringSchema;
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

    static Schema parseOAS3Schema(JsonObject jsonObject, SchemaParser parser) {
        // Only one type allowed in OAS 3!
        String type = parser.solveTypes(jsonObject).get(0);
        switch (type) {
            case "object": return new OAS3ObjectSchema(jsonObject, parser);
            case "integer": return new OAS3IntegerSchema(jsonObject, parser);
            case "string": return new OAS3StringSchema(jsonObject, parser);
            default: return new OAS3StringSchema(jsonObject, parser);
        }
    }

}

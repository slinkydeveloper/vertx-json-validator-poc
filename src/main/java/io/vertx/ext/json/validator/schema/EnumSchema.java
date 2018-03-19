package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class EnumSchema extends BaseSchema<Object> {

    List<Object> allowedValues;

    public EnumSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
        assignArray("enum", "allowedValues", a -> a.stream().collect(Collectors.toList()), List.class, false);
    }

    public List<Object> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<Object> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @Override
    public Object checkType(Object obj) {
        return obj; // We don't check the type of the obj, enum can be of every type we want
    }

    @Override
    public Class getRequiredType() {
        return Object.class;
    }

    @Override
    public Future<Object> validationLogic(Object obj) {
        if (getAllowedValues().contains(obj))
            return Future.succeededFuture(obj);
        else
            return Future.failedFuture(ValidationExceptionFactory.generateNotMatchValidationException(obj.toString() + " is not an allowed value"));
    }
}

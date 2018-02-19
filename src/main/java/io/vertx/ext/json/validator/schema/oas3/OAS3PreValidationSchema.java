package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.ValidationExceptionFactory;
import io.vertx.ext.json.validator.schema.BaseSchema;
import io.vertx.ext.json.validator.schema.PreValidationSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;
import io.vertx.ext.json.validator.monads.ValidationStep;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3PreValidationSchema extends PreValidationSchema {

    Boolean nullable;

    public <T> OAS3PreValidationSchema(JsonObject obj, SchemaParser parser, BaseSchema<T> schema) {
        super(obj, parser, schema);
        assignBoolean("nullable");
    }

    /**
     * This method should manage default and nullable keywords
     *
     * @return
     */
    @Override
    public <T> Function<Object, ValidationStep<Object, T, ValidationException>> getPreValidationLogic() {
        // Remember that we are in OAS3 context, null type doesn't exist!
        if (getNullable() != null && getNullable()) { // If nullable exists, we don't care about default
            return (obj) -> {
                if (obj == null)
                    return ValidationStep.stopNow(null);
                else
                    return ValidationStep.goFurther(obj);
            };
        } else if (getDefaultValue() != null) {
            return (obj) -> {
                if (obj == null)
                    return ValidationStep.stopNow(getDefaultValue());
                else
                    return ValidationStep.goFurther(obj);
            };
        } else
            return (obj) -> {
                if (obj == null)
                    return ValidationStep.error(
                            ValidationExceptionFactory
                                    .generateNotMatchValidationException("This value should not be null")
                    );
                else
                    return ValidationStep.goFurther(obj);
            };
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
}

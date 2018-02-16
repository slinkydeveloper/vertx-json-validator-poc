package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class BaseSchema<T> extends ReflectedSchema implements SchemaInternal<T> {

    PreValidationSchema preValidationSchema;
    Function<Object, Future<T>> validationFunction;

    public BaseSchema(JsonObject obj, SchemaParser parser){
        super(obj, parser);
        this.preValidationSchema = parser.buildPreValidationLogic(obj, this);
        PostValidationSchema postValidationSchema = parser.buildPostValidationLogic(obj);
        // I build a post validation
        this.validationFunction = preValidationSchema
                .<T>getPreValidationLogic()
                .andThen(v -> v
                        .getOrGoFurtherFuture(x -> this.validationLogic(this.checkType(x)))
                );
        postValidationSchema.<T, T>getPostValidationLogic().ifPresent(l -> {
            this.validationFunction = this.validationFunction.andThen(f -> f.compose(l));
        });

    }

    Object getDefaultValue() {
        return this.preValidationSchema.getDefaultValue();
    }

    @Override
    public Future<T> validate(Object obj) {
        return this.validationFunction.apply(obj);
    }
}

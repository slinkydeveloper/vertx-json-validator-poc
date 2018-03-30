package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.impl.FailedFuture;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class BaseSchema<T> extends ReflectedSchema implements Schema<T> {

    PreValidationSchema preValidationSchema;
    Function<Object, Future<T>> validationFunction;
    public JsonPointer pointer;

    public BaseSchema(JsonObject obj, SchemaParser parser, JsonPointer pointer){
        super(obj, parser);
        this.preValidationSchema = parser.buildPreValidationLogic(obj, this);
        PostValidationSchema postValidationSchema = parser.buildPostValidationLogic(obj);
        // I build a post validation
        this.validationFunction = preValidationSchema
                .<T>getPreValidationLogic()
                .andThen(v -> {
                    try {
                        return v.getOrGoFurtherFuture(x -> this.validationLogic(this.checkType(x)));
                    } catch (ValidationException e) { // Unmanaged exceptions catcher
                        if (e.pointer() == null)
                            e.setPointer(pointer);
                        return Future.failedFuture(e);
                    }
                });
        postValidationSchema.<T, T>getPostValidationLogic().ifPresent(l -> {
            this.validationFunction = this.validationFunction.andThen(f -> f.compose(l));
        });
        this.pointer = pointer;
    }

    public Object getDefaultValue() {
        return this.preValidationSchema.getDefaultValue();
    }

    @Override
    public Future<T> validate(Object obj) {
        return this.validationFunction.apply(obj);
    }

    abstract Class<T> getRequiredType();

    abstract Future<T> validationLogic(T obj);

    protected T checkType(Object obj) {
        if (getRequiredType().isInstance(obj)) {
            return (T)obj;
        } else {
            throw ValidationExceptionFactory
                    .generate("Wrong type received " + obj.getClass() + ", expected " + getRequiredType(), obj, pointer);
        }
    }
}

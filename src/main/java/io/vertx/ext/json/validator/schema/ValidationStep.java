package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ValidationStep<S, R> {

    Function<Function<S, Future<R>>, Future<R>> getOrGoFurtherFuture;
    Function<Function<S, R>, R> getOrGoFurther;

    public ValidationStep(Function<Function<S, Future<R>>, Future<R>> getOrGoFurtherFuture, Function<Function<S, R>, R> getOrGoFurther) {
        this.getOrGoFurtherFuture = getOrGoFurtherFuture;
        this.getOrGoFurther = getOrGoFurther;
    }

    Future<R> getOrGoFurtherFuture(Function<S, Future<R>> fn) {
        return getOrGoFurtherFuture.apply(fn);
    }

    R getOrGoFurther(Function<S, R> fn) {
        return getOrGoFurther.apply(fn);
    }

    public static ValidationStep goFurther(){
        return new ValidationStep<>((f) -> f.apply(null), (f) -> f.apply(null));
    }

    public static <S> ValidationStep goFurther(S v){
        return new ValidationStep<>((f) -> f.apply(v), (f) -> f.apply(v));
    }

    public static <R> ValidationStep stopNow(R v) {
        return new ValidationStep<>(f -> Future.succeededFuture(v), f -> v);
    }

    public static <R extends Throwable> ValidationStep error(R v) {
        return new ValidationStep<>(f -> Future.failedFuture(v), f -> v); //TODO better idea?
    }

}

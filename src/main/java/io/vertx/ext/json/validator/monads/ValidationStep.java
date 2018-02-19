package io.vertx.ext.json.validator.monads;

import io.vertx.core.Future;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class ValidationStep<S, R, E extends Throwable> {

    public abstract Future<R> getOrGoFurtherFuture(Function<S, Future<R>> fn);

    public static ValidationStep goFurther(){
        return new GoFurther(null);
    }

    public static <S> ValidationStep goFurther(S v){
        return new GoFurther(v);
    }

    public static <R> ValidationStep stopNow(R v) {
        return new StopNow(v);
    }

    public static <R extends Throwable> ValidationStep error(R v) {
        return new Error(v);
    }

}

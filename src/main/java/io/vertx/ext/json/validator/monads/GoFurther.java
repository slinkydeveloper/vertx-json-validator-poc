package io.vertx.ext.json.validator.monads;

import io.vertx.core.Future;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
class GoFurther<S, R> extends ValidationStep<S, R, Throwable> {
    final S param;

    public GoFurther(S param) {
        this.param = param;
    }

    @Override
    public Future<R> getOrGoFurtherFuture(Function<S, Future<R>> fn) {
        return fn.apply(param);
    }

    @Override
    public boolean shouldGoFurther() {
        return true;
    }
}

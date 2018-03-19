package io.vertx.ext.json.validator.monads;

import io.vertx.core.Future;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
class StopNow<S, R> extends ValidationStep<S, R, Throwable> {
    final R param;

    public StopNow(R param) {
        this.param = param;
    }

    @Override
    public Future<R> getOrGoFurtherFuture(Function<S, Future<R>> fn) {
        return Future.succeededFuture(param);
    }

    @Override
    public boolean shouldGoFurther() {
        return false;
    }
}

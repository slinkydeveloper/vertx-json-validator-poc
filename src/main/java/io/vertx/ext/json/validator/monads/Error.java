package io.vertx.ext.json.validator.monads;

import io.vertx.core.Future;

import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
class Error<E extends Throwable> extends ValidationStep<Object, Object, E> {
    final E param;

    public Error(E param) {
        this.param = param;
    }

    @Override
    public Future<Object> getOrGoFurtherFuture(Function<Object, Future<Object>> fn) {
        return Future.failedFuture(param);
    }

    @Override
    public boolean shouldGoFurther() {
        return true;
    }
}

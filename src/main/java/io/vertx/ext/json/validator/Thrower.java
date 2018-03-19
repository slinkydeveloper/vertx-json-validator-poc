package io.vertx.ext.json.validator;

@FunctionalInterface
public interface Thrower<E extends Throwable> {
    void throwException() throws E;
}

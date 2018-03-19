package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.Thrower;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.monads.ValidationStep;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class Utils {

    static <T,R> Consumer<T> applyAndAccept(Function<? super T,? extends R> f, Consumer<R> c){
        return t -> c.accept(f.apply(t));
    }

    static <T> Optional<Consumer<T>> composeCheckers(List<Consumer<T>> t) {
        return t.stream().reduce(Consumer::andThen);
    }

    static <T, R, E extends Throwable> Function<T, Future<R>> composeSteps(List<Function<T, ValidationStep<T, R, E>>> t, Function<T, Future<R>> def) {
        return t
                .stream()
                .reduce(ValidationStep::compose)
                .map(fn -> fn.andThen(state -> state.getOrGoFurtherFuture(def)))
                .orElse(def);
    }

    static <T, R> Function<T, Future<R>> composeFutureFunctions(LinkedList<Function<T, Future<R>>> fns, Function<T, Future<R>> defaultFuture) {
        return fns.stream().reduce(defaultFuture, (f1, f2) -> (T in) -> {
            Future<R> o = f1.apply(in);
            if (o != null) return o;
            else return f2.apply(in);
        });
    }

    public static <T, K, U> Collector<T, ?, Map<K,U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper)
    {
        return Collectors.toMap(keyMapper, valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K,U>> entriesToLinkedMap()
    {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new);
    }

    static boolean containsAllKeys(JsonObject obj, List<String> keys) {
        return obj.fieldNames().containsAll(keys);
    }

    static boolean containsAtLeastOneKey(JsonObject obj, List<String> keys) {
        return keys
                .stream()
                .map(obj::containsKey)
                .reduce(false, Boolean::logicalOr);
    }

    static <T> Stream<T> collectionDifference(Collection<T> a, Collection<T> b) {
        return a.stream().filter(s -> !b.contains(s));
    }

    static <T> T nullSupplier() {
        return null;
    }

}

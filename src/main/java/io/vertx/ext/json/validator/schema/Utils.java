package io.vertx.ext.json.validator.schema;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class Utils {

    static <T,R> Consumer<T> applyAndAccept(Function<? super T,? extends R> f, Consumer<R> c){
        return t -> c.accept(f.apply(t));
    }

    static <S, T> Optional<Consumer<T>> composeCheckers(List<Consumer<T>> t) {
        return t.stream().reduce(Consumer::andThen);
    }

}

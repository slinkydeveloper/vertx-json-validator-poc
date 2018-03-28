package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum OAS3Properties {
    ADDITIONAL_PROPERTIES("additionalProperties", JsonArray.class, (JsonArray a1, JsonArray a2) -> a1.copy().addAll(a2),

    ;

    private final String keyword;
    private final Class c;
    private final BiFunction<Object, Object, Object> merger;

    <T> OAS3Properties(String keyword, Class<T> c, BiFunction<T, T, Object> merger) {
        this.keyword = keyword;
        this.c = c;
        this.merger = (o1, o2) -> merger.apply(c.cast(o1), c.cast(o2));
    }

    public String getKeyword() {
        return keyword;
    }

    public Object merge(Object a, Object b) {
        return merger.apply(a, b);
    }

    public static JsonObject mergeSchema(JsonObject main, JsonObject... others) {

    }
}

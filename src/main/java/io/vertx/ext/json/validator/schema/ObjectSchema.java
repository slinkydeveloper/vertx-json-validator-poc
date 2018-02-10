package io.vertx.ext.json.validator.schema;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class ObjectSchema extends BaseSchema<JsonObject> {

    private LinkedList<Function<JsonObject, Future<Map.Entry<String, Object>>>> validators;

    public ObjectSchema(JsonObject schema, SchemaParser parser) {
        super(schema, parser);
        assignObject("properties", "validators", LinkedList.class,
                obj -> obj.stream()
                    .map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), this.parseProperty((JsonObject)e.getValue())))
                    .map(this::buildValidator)
                    .collect(Collectors.toCollection(LinkedList::new)));

    }

    public LinkedList<Function<JsonObject, Future<Map.Entry<String, Object>>>> getValidators() {
        return validators;
    }

    public void setValidators(LinkedList<Function<JsonObject, Future<Map.Entry<String, Object>>>> validators) {
        this.validators = validators;
    }

    protected abstract Schema parseProperty(JsonObject object);

    private Function<JsonObject, Future<Map.Entry<String, Object>>> buildValidator(Map.Entry<String, Schema> e) {
        return (JsonObject in) -> {
            Object v = in.getValue(e.getKey());
            if (v != null) {
                return e.getValue().validate(v).map(res -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), res));
            } else {
                return Future.failedFuture(ValidationException
                        .ValidationExceptionFactory
                        .generateNotMatchValidationException("Param required but not found")
                );
            }
        };
    }

    private Future<JsonObject> composer(CompositeFuture fut) {
        return Future.succeededFuture(new JsonObject(
                fut.list().stream()
                        .map(o -> ((Map.Entry<String, Object>)o))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }

    @Override
    public Future<JsonObject> validate(Object obj) {
        JsonObject jsonObject = this.checkType(obj);
        List<Future> futs = validators.stream().map(f -> f.apply(jsonObject)).collect(Collectors.toList());
        return CompositeFuture.all(futs).compose(this::composer);
    }

    @Override
    public Class getRequiredType() {
        return JsonObject.class;
    }
}

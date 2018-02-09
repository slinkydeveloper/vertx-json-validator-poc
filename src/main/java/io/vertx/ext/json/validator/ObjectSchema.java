package io.vertx.ext.json.validator;

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
public class ObjectSchema extends BaseSchema<JsonObject> {

    private LinkedList<Function<JsonObject, Future<Map.Entry<String, Object>>>> validators;

    public ObjectSchema(JsonObject schema) {
        super(schema);
        JsonObject properties = this.originalJson.getJsonObject("properties");
        Map<String, Schema> schemas = properties.fieldNames().stream()
                .map(k -> new AbstractMap.SimpleImmutableEntry(k, Schema.parse(properties.getJsonObject(k))))
                .collect(Collectors.toMap(Map.Entry<String, Schema>::getKey, Map.Entry<String, Schema>::getValue));
        this.validators = initializeValidators(schemas);

    }

    // For testing (for now)
    protected ObjectSchema(Map<String, Schema> validators) {
        super(null);
        this.initializeValidators(validators);
    }

    private LinkedList<Function<JsonObject, Future<Map.Entry<String, Object>>>> initializeValidators(Map<String, Schema> schemas) {
        LinkedList<Function<JsonObject, Future<Map.Entry<String, Object>>>> validators = new LinkedList<>();
        for (Map.Entry<String, Schema> e : schemas.entrySet()) {
            validators.add(in -> {
                Object v = in.getValue(e.getKey());
                if (v != null) {
                    return e.getValue().validate(v).map(res -> new AbstractMap.SimpleImmutableEntry(e.getKey(), res));
                } else {
                    return Future.failedFuture(ValidationException
                            .ValidationExceptionFactory
                            .generateNotMatchValidationException("Param required but not found")
                    );
                }
            });
        }
        return validators;
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

package io.vertx.ext.json.validator.schema;

import com.fasterxml.jackson.databind.ser.Serializers;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class ObjectSchema extends BaseSchema<JsonObject> {

    private List<String> required;
    private LinkedList<Function<JsonObject, Future<Optional<Map.Entry<String, Object>>>>> validators;

    public ObjectSchema(JsonObject schema, SchemaParser parser) {
        super(schema, parser);
        assignArray("required", "required", (json) -> json.stream().map(String.class::cast).collect(Collectors.toList()));
        assignObject("properties", "validators", LinkedList.class, //TODO missing properties keyword?
                obj -> obj.stream()
                    .map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), this.parseProperty((JsonObject)e.getValue())))
                    .map(this::buildValidator)
                    .collect(Collectors.toCollection(LinkedList::new)));
        //TODO additionalProperties

    }

    public LinkedList<Function<JsonObject, Future<Optional<Map.Entry<String, Object>>>>> getValidators() {
        return validators;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public void setValidators(LinkedList<Function<JsonObject, Future<Optional<Map.Entry<String, Object>>>>> validators) {
        this.validators = validators;
    }

    protected BaseSchema parseProperty(JsonObject object) {
        return (BaseSchema) this.getParser().parse(object);
    };

    private Function<JsonObject, Future<Optional<Map.Entry<String, Object>>>> buildValidator(Map.Entry<String, BaseSchema> e) {
        String propertyName = e.getKey();
        BaseSchema schema = e.getValue();
        boolean r = Optional.ofNullable(required).map(l -> l.contains(propertyName)).orElse(false);

        return (JsonObject in) -> {
            if (in.containsKey(propertyName)) {
                return schema
                        .validate(in.getValue(propertyName))
                        .map(res ->
                                Optional.of(new AbstractMap.SimpleImmutableEntry<>(propertyName, res))
                        );
            } else if (schema.getDefaultValue() != null) {
                return Future.succeededFuture(Optional.of(
                        new AbstractMap.SimpleImmutableEntry<>(propertyName, schema.getDefaultValue())
                ));
            } else if (r) {
                return Future.failedFuture(ValidationExceptionFactory
                        .generateNotMatchValidationException("Param " + propertyName + " required but not found")
                );
            } else {
                return Future.succeededFuture(Optional.empty());
            }
        };
    }

    private Future<JsonObject> composer(CompositeFuture fut) {
        return Future.succeededFuture(
            fut.list().stream()
                    .map(Optional.class::cast)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Map.Entry.class::cast)
                    .map(e -> new JsonObject().put((String)e.getKey(), e.getValue()))
                    .reduce(new JsonObject(), JsonObject::mergeIn)
        );
    }

    @Override
    public Future<JsonObject> validationLogic(JsonObject jsonObject) {
        List<Future> futs = validators.stream().map(f -> f.apply(jsonObject)).collect(Collectors.toList());
        return CompositeFuture.all(futs).compose(this::composer);
    }

    @Override
    public Class getRequiredType() {
        return JsonObject.class;
    }
}

package io.vertx.ext.json.validator.schema;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.AbstractMap.SimpleImmutableEntry;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class ObjectSchema extends BaseSchema<JsonObject> {

    protected HashSet<String> required;
    protected LinkedHashMap<String, BaseSchema> validators;
    protected Function<Map.Entry<String, Object>, Future<Map.Entry<String, Object>>> additionalPropertiesValidator;
    protected boolean applyDefaultValues;

    public ObjectSchema(JsonObject schema, SchemaParser parser) {
        super(schema, parser);
        assignArray("required", "required", (json) -> json.stream().map(String.class::cast).collect(Collectors.toSet()), HashSet.class, true);
        additionalPropertiesValidator = buildAdditionalPropertiesValidator(schema.getValue("additionalProperties"));
    }

    public void setRequired(HashSet<String> required) {
        this.required = required;
    }

    private Function<Map.Entry<String, Object>, Future<Map.Entry<String, Object>>> buildAdditionalPropertiesValidator(Object additionalProperties) {
        if (additionalProperties == null)
            return in -> Future.succeededFuture(in);
        else if (additionalProperties instanceof Boolean) {
            if (((Boolean) additionalProperties))
                return in -> Future.succeededFuture(in);
            else
                return in -> Future.failedFuture(ValidationExceptionFactory.generateNotMatchValidationException(
                        in.getKey() + "is an illegal keyword. Additional properties are not allowed")
                );
        } else if (additionalProperties instanceof JsonObject) {
            final Schema schema = this.parseProperty((JsonObject) additionalProperties);
            return in -> schema.validate(in.getValue()).map(out -> new SimpleImmutableEntry<>(in.getKey(), out));
        } else
            throw new IllegalArgumentException("You specified an illegal additionalProperties keyword");
    }

    public void setValidators(LinkedHashMap<String, BaseSchema> validators) {
        if (validators != null)
            this.validators = validators;
        else
            this.validators = new LinkedHashMap<>();
    }

    protected BaseSchema parseProperty(JsonObject object) {
        return (BaseSchema) this.getParser().parse(object);
    }

    protected abstract Future<Map.Entry<String, Object>> validateInputProperty(Map.Entry<String, Object> jsonEntry);

    @Override
    public Future<JsonObject> validationLogic(JsonObject in) {
        List<Future> propertiesValidationResult = in.stream().map(this::validateInputProperty).collect(Collectors.toList());
        return CompositeFuture.all(propertiesValidationResult).compose(cf -> {
            if (!in.fieldNames().containsAll(required)) // Check required properties
                return Future.failedFuture("Missing properties " + Utils.collectionDifference(required, in.fieldNames()));
            else {
                JsonObject out = cf.list()
                        .stream()
                        .map(Map.Entry.class::cast)
                        .reduce(new JsonObject(), (json, entry) -> json.put((String)entry.getKey(), entry.getValue()), JsonObject::mergeIn);
                if (applyDefaultValues) {
                    Set<String> missingProperties = Utils.collectionDifference(validators.keySet(), in.fieldNames()).collect(Collectors.toSet());
                    for (String missingProperty : missingProperties) {
                        BaseSchema s = validators.get(missingProperty);
                        if (s.getDefaultValue() != null)
                            out.put(missingProperty, s.getDefaultValue());
                    }
                }
                return Future.succeededFuture(out);
            }
        });
    }

    @Override
    public Class getRequiredType() {
        return JsonObject.class;
    }
}

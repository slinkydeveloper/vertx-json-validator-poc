package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.*;

import java.util.*;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3ObjectSchema extends ObjectSchema {

    public OAS3ObjectSchema(JsonObject schema, SchemaParser parser) {
        super(schema, parser);
        assignObject("properties", "validators", LinkedHashMap.class,
                obj -> obj.stream()
                        .map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), this.parseProperty((JsonObject)e.getValue())))
                        .collect(Utils.entriesToLinkedMap()), true);
        applyDefaultValues = validators.values().stream().map(BaseSchema::getDefaultValue).filter(Objects::nonNull).count() > 0;
    }

    @Override
    protected Future<Map.Entry<String, Object>> validateInputProperty(Map.Entry<String, Object> jsonEntry) {
        String key = jsonEntry.getKey();
        if (validators.containsKey(key)) {
            return validators.get(key).validate(jsonEntry.getValue()).map(r -> new AbstractMap.SimpleImmutableEntry<>(key, r));
        } else { // in a far future check if pattern properties
            return additionalPropertiesValidator.apply(new AbstractMap.SimpleImmutableEntry<>(key, jsonEntry.getValue()));
        }
    }
}

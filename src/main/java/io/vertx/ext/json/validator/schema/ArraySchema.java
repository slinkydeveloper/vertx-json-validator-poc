package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Optional;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class ArraySchema extends BaseSchema<JsonArray> {

    protected Function<JsonArray, Future<JsonArray>> validator;

    public ArraySchema(JsonObject obj, SchemaParser parser, JsonPointer pointer) {
        super(obj, parser, pointer);
        setValidator(buildSchemaValidator(), buildCheckers());
    }

    private void setValidator(
            final Function<JsonArray, Future<JsonArray>> schemaValidator,
            final List<Consumer<JsonArray>> checkers) {
        Optional<Consumer<JsonArray>> composedCheckers = Utils.composeCheckers(checkers);
        if (composedCheckers.isPresent()) {
            validator = Utils
                    .consumerToFunction(composedCheckers.get())
                    .<Future<JsonArray>>andThen(schemaValidator);
        } else {
            validator = schemaValidator;
        }
    }

    protected abstract Function<JsonArray, Future<JsonArray>> buildSchemaValidator();

    private List<Consumer<JsonArray>> buildCheckers() {
        final Integer minItems = get("minItems", Integer.class);
        final Integer maxItems = get("maxItems", Integer.class);
        final Boolean uniqueItems = get("uniqueItems", Boolean.class);
        List<Consumer<JsonArray>> checkers = new ArrayList<>();
        if (minItems != null)
            checkers.add(buildMinItemsChecker(minItems));
        if (maxItems != null)
            checkers.add(buildMaxItemsChecker(maxItems));
        if (uniqueItems != null && uniqueItems)
            checkers.add(buildUniqueChecker(pointer));
        return checkers;
    }

    private Consumer<JsonArray> buildMinItemsChecker(final Integer minItems) {
        return in -> {
            if (in.size() < minItems) throw ValidationExceptionFactory.generate(
                    "JsonArray size should be at least "+ minItems + " while now is " + in, pointer
            );
        };
    }

    private Consumer<JsonArray> buildMaxItemsChecker(final Integer maxItems) {
        return in -> {
            if (in.size() > maxItems) throw ValidationExceptionFactory.generate(
                    "JsonArray size should be at most " + maxItems + " while now is " + in, pointer
            );
        };
    }

    @Override
    Class<JsonArray> getRequiredType() {
        return JsonArray.class;
    }

    @Override
    Future<JsonArray> validationLogic(JsonArray obj) {
        return validator.apply(obj);
    }

    private static Consumer<JsonArray> buildUniqueChecker(JsonPointer pointer) {
        return in -> {
            if (!Utils.distinctValues(in))
                throw ValidationExceptionFactory.generate("Values must be distincts", in, pointer);
        };
    }
}

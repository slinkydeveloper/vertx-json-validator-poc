package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class StringSchema extends BaseSchema<String> {

    private Optional<Consumer<String>> checkStringProperties;

    public StringSchema(JsonObject obj, SchemaParser parser, JsonPointer pointer) {
        super(obj, parser, pointer);

        final Pattern pattern = parsePattern();
        final Integer minLength = get("minLength", Integer.class);
        final Integer maxLength = get("maxLength", Integer.class);

        List<Consumer<String>> checkers = new ArrayList<>();
        if (pattern != null)
            checkers.add((value) -> {
                if (!pattern.matcher(value).matches())
                    throw ValidationExceptionFactory.generate("String should match pattern " + pattern, value, pointer);
            });
        if (maxLength != null)
            checkers.add((value) -> {
                if (value.codePointCount(0, value.length()) > maxLength)
                    throw ValidationExceptionFactory.generate("String should have max length of " + maxLength, value, pointer);
            });
        if (minLength != null)
            checkers.add((value) -> {
                if (value.codePointCount(0, value.length()) < minLength)
                    throw ValidationExceptionFactory.generate("String should have min length of " + minLength, value, pointer);
            });

        this.checkStringProperties = Utils.composeCheckers(checkers);
    }

    private Pattern parsePattern() {
        try {
            return getParser().parseFormat(getRequired("format", String.class));
        } catch (IllegalArgumentException e) {
            String pattern = get("pattern", String.class);
            if (pattern == null) return null;
            else return Pattern.compile(pattern);
        }
    }

    @Override
    public Future<String> validationLogic(String s) {
        try {
            checkStringProperties.ifPresent(f -> f.accept(s));
            return Future.succeededFuture(s);
        } catch (ValidationException e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Class getRequiredType() {
        return String.class;
    }
}

package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
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
public abstract class StringSchema extends BaseSchema<String> {

    private Optional<Consumer<String>> checkStringProperties;

    public StringSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);

        final Pattern pattern = parsePattern();
        final Integer minLength = get("minLength", Integer.class);
        final Integer maxLength = get("maxLength", Integer.class);

        List<Consumer<String>> checkers = new ArrayList<>();
        if (pattern != null)
            checkers.add((value) -> {
                if (!pattern.matcher(value).matches())
                    throw ValidationExceptionFactory.generateNotMatchValidationException("String should match pattern " + pattern);
            });
        if (maxLength != null)
            checkers.add((value) -> {
                if (value.codePointCount(0, value.length()) > maxLength)
                    throw ValidationExceptionFactory.generateNotMatchValidationException("String should have max length of " + maxLength);
            });
        if (minLength != null)
            checkers.add((value) -> {
                if (value.codePointCount(0, value.length()) < minLength)
                    throw ValidationExceptionFactory.generateNotMatchValidationException("String should have min length of " + minLength);
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

package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.regex.Pattern;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class StringSchema extends BaseSchema<String> {

    private Pattern pattern;
    private Integer minLength;
    private Integer maxLength;

    public StringSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
        if (obj.containsKey("format"))
            this.pattern = parser.parseFormat(obj.getString("format"));
        else
            manipulateAndAssign(s -> s.getString("pattern"), "pattern", Pattern.class, Pattern::compile);
        assignInteger("minLength");
        assignInteger("maxLength");
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    private void checkMinLength(String value) {
        if (minLength != null && !(value.length() >= minLength))
            throw ValidationExceptionFactory.generateNotMatchValidationException("String should have min length of " + minLength);
    }

    private void checkMaxLength(String value) {
        if (maxLength != null && !(value.length() <= maxLength))
            throw ValidationExceptionFactory.generateNotMatchValidationException("String should have max length of " + maxLength);
    }

    private void checkPattern(String value) {
        if (pattern != null && !pattern.matcher(value).matches())
            throw ValidationExceptionFactory.generateNotMatchValidationException("String should match pattern " + pattern);
    }

    @Override
    public Future<String> validate(Object obj) {
        try {
            String s = checkType(obj);
            checkMinLength(s);
            checkMaxLength(s);
            checkPattern(s);
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

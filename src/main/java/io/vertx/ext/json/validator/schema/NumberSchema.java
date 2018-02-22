package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class NumberSchema<T extends Number> extends BaseSchema<T> {
  private Optional<Consumer<Number>> checkNumberProperties;

  public NumberSchema(JsonObject jsonObject, SchemaParser parser) {
    super(jsonObject, parser);

    Double maximum = get("maximum", Double.class);
    Double minimum = get("minimum", Double.class);
    Double multipleOf = get("multipleOf", Double.class);

    List<Consumer<Double>> checkers = new ArrayList<>();
    if (minimum != null)
      checkers.add(this.buildCheckMinimum(minimum, get("exclusiveMinimum", Boolean.class)));
    if (maximum != null)
      checkers.add(this.buildCheckMaximum(maximum, get("exclusiveMaximum", Boolean.class)));
    if (multipleOf != null)
      checkers.add(this.buildCheckMultipleOf(multipleOf));

    checkNumberProperties = Utils.composeCheckers(checkers).map((c) -> Utils.applyAndAccept(Number::doubleValue, c));
  }

  private Consumer<Double> buildCheckMaximum(final Double maximum, final Boolean exclusiveMaximum) {
    if (exclusiveMaximum != null && exclusiveMaximum)
      return (val) -> {
        if (!(val < maximum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be < " + maximum);
      };
    else
      return (val) -> {
        if (!(val <= maximum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be <= " + maximum);
      };
  }

  private Consumer<Double> buildCheckMinimum(final Double minimum, final Boolean exclusiveMinimum) {
    if (exclusiveMinimum != null && exclusiveMinimum)
      return (val) -> {
        if (!(val > minimum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be > " + minimum);
      };
    else
      return (val) -> {
        if (!(val >= minimum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be >= " + minimum);
      };
  }

  private Consumer<Double> buildCheckMultipleOf(final Double multipleOf) {
    return (val) -> {
      if (!(val % multipleOf == 0))
        throw ValidationExceptionFactory.generateNotMatchValidationException(
                "Number should be multipleOf " + multipleOf);
    };
  }

  @Override
  public Future<T> validationLogic(T n) {
    try {
      checkNumberProperties.ifPresent(fn -> fn.accept(n));
      return Future.succeededFuture(n);
    } catch (ValidationException e) {
      return Future.failedFuture(e);
    }
  }
}
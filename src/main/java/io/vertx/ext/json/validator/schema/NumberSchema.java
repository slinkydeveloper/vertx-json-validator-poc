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

  private Boolean exclusiveMaximum = null;
  private Double maximum = null;
  private Boolean exclusiveMinimum = null;
  private Double minimum = null;
  private Double multipleOf = null;

  private Optional<Consumer<Number>> checkNumberProperties;

  public NumberSchema(JsonObject jsonObject, SchemaParser parser) {
    super(jsonObject, parser);
    assignBoolean("exclusiveMaximum");
    assignDouble("maximum");
    assignBoolean("exclusiveMinimum");
    assignDouble("minimum");
    assignDouble("multipleOf");

    this.checkNumberProperties = buildCheckNumberProperties();
  }

  public Boolean getExclusiveMaximum() {
    return exclusiveMaximum;
  }

  public void setExclusiveMaximum(Boolean exclusiveMaximum) {
    this.exclusiveMaximum = exclusiveMaximum;
  }

  public Double getMaximum() {
    return maximum;
  }

  public void setMaximum(Double maximum) {
    this.maximum = maximum;
  }

  public Boolean getExclusiveMinimum() {
    return exclusiveMinimum;
  }

  public void setExclusiveMinimum(Boolean exclusiveMinimum) {
    this.exclusiveMinimum = exclusiveMinimum;
  }

  public Double getMinimum() {
    return minimum;
  }

  public void setMinimum(Double minimum) {
    this.minimum = minimum;
  }

  public Double getMultipleOf() {
    return multipleOf;
  }

  public void setMultipleOf(Double multipleOf) {
    this.multipleOf = multipleOf;
  }

  private Consumer<Double> buildCheckMaximum() {
    if (this.exclusiveMaximum != null && this.exclusiveMaximum)
      return (val) -> {
        if (!(val < maximum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be < " + this.maximum);
      };
    else
      return (val) -> {
        if (!(val <= maximum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be <= " + this.maximum);
      };
  }

  private Consumer<Double> buildCheckMinimum() {
    if (this.exclusiveMinimum != null && this.exclusiveMinimum)
      return (val) -> {
        if (!(val > minimum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be > " + this.minimum);
      };
    else
      return (val) -> {
        if (!(val >= minimum))
          throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be >= " + this.minimum);
      };
  }

  private void checkMultipleOf(double val) {
    if (!(val % multipleOf == 0))
      throw ValidationExceptionFactory.generateNotMatchValidationException(
              "Number should be multipleOf " + this.multipleOf);
  }

  private Optional<Consumer<Number>> buildCheckNumberProperties() {
    List<Consumer<Double>> checkers = new ArrayList<>();
    if (this.getMinimum() != null)
      checkers.add(this.buildCheckMaximum());
    if (this.getMaximum() != null)
      checkers.add(this.buildCheckMaximum());
    if (this.getMultipleOf() != null)
      checkers.add(this::checkMultipleOf);

    return Utils.composeCheckers(checkers).map((c) -> Utils.applyAndAccept(Number::doubleValue, c));
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
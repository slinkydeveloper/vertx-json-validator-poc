package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class NumberSchema<T extends Number> extends BaseSchema<T> {

  private Boolean exclusiveMaximum = null;
  private Double maximum = null;
  private Boolean exclusiveMinimum = null;
  private Double minimum = null;
  private Double multipleOf = null;



  public NumberSchema(JsonObject jsonObject, SchemaParser parser) {
    super(jsonObject, parser);
    assignBoolean("exclusiveMaximum");
    assignDouble("maximum");
    assignBoolean("exclusiveMinimum");
    assignDouble("minimum");
    assignDouble("multipleOf");
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

  private void checkMaximum(double val) {
    if (this.maximum != null) {
      if (this.exclusiveMaximum != null && this.exclusiveMaximum && !(val < maximum))
        throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be < " + this.maximum);
      else if (!(val <= maximum))
        throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be <= " + this.maximum);
    }
  }

  private void checkMinimum(double val) {
    if (this.minimum != null) {
      if (this.exclusiveMinimum != null && exclusiveMinimum && !(val > minimum))
        throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be > " + this.minimum);
      else if (!(val >= minimum))
        throw ValidationExceptionFactory.generateNotMatchValidationException("Number should be >= " + this.minimum);
    }
  }

  private void checkMultipleOf(double val) {
    if (multipleOf != null && !(val % multipleOf == 0))
      throw ValidationExceptionFactory.generateNotMatchValidationException(
              "Number should be multipleOf " + this.multipleOf);
  }

  private void checkNumberProperties(Number number) {
    double val = number.doubleValue();
    checkMaximum(val);
    checkMinimum(val);
    checkMultipleOf(val);
  }

  public Future<T> validate(Object obj) {
    try {
      T n = checkType(obj);
      checkNumberProperties(n);
      return Future.succeededFuture(n);
    } catch (ValidationException e) {
      return Future.failedFuture(e);
    }
  }
}
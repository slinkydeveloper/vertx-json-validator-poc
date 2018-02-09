package io.vertx.ext.json.validator;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public abstract class NumberSchema<T extends Number> extends BaseSchema<T> {

  private Boolean exclusiveMaximum = null;
  private Double maximum = null;
  private Boolean exclusiveMinimum = null;
  private Double minimum = null;
  private Double multipleOf = null;

  public NumberSchema(JsonObject jsonObject) {
    super(jsonObject);
    try {
      assignProperty("exclusiveMaximum", NumberSchema.class);
      assignProperty("maximum", NumberSchema.class);
      assignProperty("exclusiveMinimum", NumberSchema.class);
      assignProperty("minimum", NumberSchema.class);
      assignProperty("multipleOf", NumberSchema.class);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private void checkMaximum(double val) {
    if (this.maximum != null) {
      if (this.exclusiveMaximum != null && this.exclusiveMaximum && !(val < maximum))
        throw ValidationException.ValidationExceptionFactory.generateNotMatchValidationException("Number should be < " + this.maximum);
      else if (!(val <= maximum))
        throw ValidationException.ValidationExceptionFactory.generateNotMatchValidationException("Number should be <= " + this.maximum);
    }
  }

  private void checkMinimum(double val) {
    if (this.minimum != null) {
      if (this.exclusiveMinimum != null && exclusiveMinimum && !(val > minimum))
        throw ValidationException.ValidationExceptionFactory.generateNotMatchValidationException("Number should be > " + this.minimum);
      else if (!(val >= minimum))
        throw ValidationException.ValidationExceptionFactory.generateNotMatchValidationException("Number should be >= " + this.minimum);
    }
  }

  private void checkMultipleOf(double val) {
    if (multipleOf != null && !(val % multipleOf == 0))
      throw ValidationException.ValidationExceptionFactory.generateNotMatchValidationException(
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
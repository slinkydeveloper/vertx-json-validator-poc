package io.vertx.ext.json.validator;

import io.vertx.core.json.JsonPointer;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ValidationExceptionFactory {

  public static ValidationException generate(String message) {
    return new ValidationException(message, null, null);
  }

  public static ValidationException generate(String message, JsonPointer pointer) {
    return new ValidationException(message + " at " + pointer, null, pointer);
  }

  public static ValidationException generate(String message, Object wrongValue, JsonPointer pointer) {
    return new ValidationException(message + " at " + pointer, wrongValue, pointer);
  }

}

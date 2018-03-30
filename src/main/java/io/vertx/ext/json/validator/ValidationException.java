package io.vertx.ext.json.validator;

import io.vertx.core.VertxException;
import io.vertx.core.json.JsonPointer;

/**
 * This is the main class for every Validation flow related errors
 *
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ValidationException extends VertxException {

  private Object value;
  private JsonPointer pointer;

  public ValidationException(String message, Object value, JsonPointer pointer) {
    super((message != null && message.length() != 0) ? message : "ValidationException{" +
            "value='" + value + '\'' + ", pointer=" + pointer + '}');
    this.value = value;
    this.pointer = pointer;
  }

  public ValidationException(String message, JsonPointer pointer) {
    this(message, null, pointer);
  }

  public Object value() {
    return value;
  }

  public JsonPointer pointer() {
    return pointer;
  }

  public void setPointer(JsonPointer pointer) {
    this.pointer = pointer;
  }

  @Override
  public String toString() {
    return "ValidationException{" +
            "value='" + value + '\'' +
            ", pointer=" + pointer +
            '}';
  }
}

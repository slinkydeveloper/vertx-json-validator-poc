package io.vertx.ext.json.validator;

public class WrongSchemaException extends RuntimeException { //TODO should be runtimeexception?

    public WrongSchemaException(String message) {
        super(message);
    }

    public WrongSchemaException(String message, Throwable cause) {
        super(message, cause);
    }
}

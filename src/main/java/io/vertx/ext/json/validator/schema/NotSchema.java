package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

public class NotSchema extends BaseSchema<Object> {

    private BaseSchema innerSchema;

    public NotSchema(JsonObject obj, SchemaParser parser, JsonPointer pointer) {
        super(obj, parser, pointer);
        innerSchema = this.<JsonObject, BaseSchema>getRequired("not", JsonObject.class, in -> (BaseSchema) this.getParser().parse(in, pointer));
    }

    @Override
    Class<Object> getRequiredType() {
        return null;
    }

    @Override
    protected Object checkType(Object i) {
        return i;
    }

    @Override
    Future<Object> validationLogic(Object obj) {
        return Utils.andThen(
                innerSchema.validate(obj),
                r -> Future.failedFuture(ValidationExceptionFactory.generate("The input should not match the schema", obj, pointer)),
                e -> Future.succeededFuture(obj));
    }
}

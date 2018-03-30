package io.vertx.ext.json.validator.schema;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnyOfSchema extends BaseSchema<Object> {

    private List<BaseSchema> anyOfSchemas;
    private BaseSchema mainSchema;

    public AnyOfSchema(JsonObject obj, SchemaParser parser, JsonPointer pointer) {
        super(obj, parser, pointer);
        anyOfSchemas = this.<JsonArray, List<BaseSchema>>getRequired(
                "anyOf",
                JsonArray.class,
                in -> in.stream().map(s -> this.getParser().parse((JsonObject) s, pointer)).map(BaseSchema.class::cast).collect(Collectors.toList())
        );
        JsonObject copy = obj.copy();
        copy.remove("anyOf");
        copy.remove("nullable");
        copy.remove("defaultValue");
        if (copy.size() != 0)
            mainSchema = (BaseSchema) parser.parse(copy, pointer);
    }

    @Override
    Class<Object> getRequiredType() {
        return Object.class;
    }

    @Override
    protected Object checkType(Object obj) {
        return obj;
    }

    @Override
    Future<Object> validationLogic(Object obj) {
        if (mainSchema != null) {
            return mainSchema
                    .validate(obj)
                    .compose(r ->
                            CompositeFuture
                                    .any(anyOfSchemas.stream().map(v -> v.validate(obj)).collect(Collectors.toList()))
                                    .map(cf -> cf.list().stream().filter(Objects::nonNull).findFirst().orElse(r))
                                    .recover(t -> Future
                                            .failedFuture(ValidationExceptionFactory.generate("No schema of anyOf matches", obj, pointer))
                                    )
                    );
        } else {
            return CompositeFuture
                    .any(anyOfSchemas.stream().map(v -> v.validate(obj)).collect(Collectors.toList()))
                    .map(cf -> cf.list().stream().filter(Objects::nonNull).findFirst().get())
                    .recover(t -> Future
                        .failedFuture(ValidationExceptionFactory.generate("No schema of anyOf matches", obj, pointer))
                    );
        }
    }
}

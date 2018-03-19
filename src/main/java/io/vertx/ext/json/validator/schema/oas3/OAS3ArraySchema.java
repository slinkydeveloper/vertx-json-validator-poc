package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.ArraySchema;
import io.vertx.ext.json.validator.schema.BaseSchema;
import io.vertx.ext.json.validator.schema.SchemaParser;

import java.util.function.Function;
import java.util.stream.Collectors;

public class OAS3ArraySchema extends ArraySchema {

    public OAS3ArraySchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
    }

    @Override
    protected Function<JsonArray, Future<JsonArray>> buildSchemaValidator() {
        final BaseSchema itemSchema = this
                .<JsonObject>getOptional("items", JsonObject.class)
                .map(in -> (BaseSchema)this.getParser().parse(in))
                .orElseGet(() -> null);
        return (itemSchema != null) ? in ->
                CompositeFuture
                        .all(in.stream().map(itemSchema::validate).collect(Collectors.toList()))
                        .map(cf -> new JsonArray(cf.list())) : Future::succeededFuture;
    }

}

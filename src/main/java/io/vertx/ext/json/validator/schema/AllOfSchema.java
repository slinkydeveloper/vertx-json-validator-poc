package io.vertx.ext.json.validator.schema;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class AllOfSchema extends BaseSchema<Object> {

    private List<BaseSchema> allOfSchemas;
    private BaseSchema mainSchema;

    public AllOfSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
        allOfSchemas = this.<JsonArray, List<BaseSchema>>getRequired(
                "allOf",
                JsonArray.class,
                in -> in.stream().map(s -> this.getParser().parse((JsonObject) s)).map(BaseSchema.class::cast).collect(Collectors.toList())
        );
        JsonObject copy = obj.copy();
        copy.remove("allOf");
        copy.remove("nullable");
        copy.remove("defaultValue");
        if (copy.size() != 0)
            mainSchema = (BaseSchema) parser.parse(copy);
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
        List<Future> validating = allOfSchemas.stream().map(v -> v.validate(obj)).collect(Collectors.toList());
        if (mainSchema != null)
            validating.add(0, mainSchema.validate(obj));
        return CompositeFuture
                .all(validating)
                .map(cf -> {
                    if (obj instanceof JsonObject)
                        return ((JsonObject)obj).mergeIn(
                                cf.list()
                                    .stream()
                                    .map(JsonObject.class::cast)
                                    .reduce(new JsonObject(), (j1, j2) -> j1.mergeIn(j2, true)),
                                true);
                    else return obj;
                });
    }
}

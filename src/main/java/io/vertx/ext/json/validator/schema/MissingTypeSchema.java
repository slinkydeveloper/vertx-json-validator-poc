package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * When the type is missing strange things can happen: user can put in the same schema object
 * keywords he wants, like a pattern and a multipleOf property -_-'
 *
 * @author Francesco Guardiani francescoguard@gmail.com
 */
public class MissingTypeSchema extends BaseSchema<Object>{
    private List<BaseSchema> internalSchemas;

    public MissingTypeSchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
        // Now crazy stuff happens
        internalSchemas = parser.solveMissingTypeSchemas(obj)
                .stream()
                .map(e -> parser.instantiateSchema(e, obj))
                .map(BaseSchema.class::cast)
                .collect(Collectors.toList());

    }

    @Override
    Class<Object> getRequiredType() {
        return Object.class;
    }

    @Override
    public Object checkType(Object v) {
        return v;
    }

    @Override
    Future<Object> validationLogic(Object obj) {
        for (BaseSchema schema : internalSchemas) {
            try {
                schema.checkType(obj);
            } catch (ValidationException e) {
                continue;
            }
            return schema.validate(obj);
        }
        return Future.succeededFuture(obj);
    }
}

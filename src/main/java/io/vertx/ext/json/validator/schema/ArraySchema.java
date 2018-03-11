package io.vertx.ext.json.validator.schema;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ArraySchema extends BaseSchema<JsonArray> {

    public ArraySchema(JsonObject obj, SchemaParser parser) {
        super(obj, parser);
    }

    @Override
    Class<JsonArray> getRequiredType() {
        return JsonArray.class;
    }

    @Override
    Future<JsonArray> validationLogic(JsonArray obj) {
        return null;
    }
}

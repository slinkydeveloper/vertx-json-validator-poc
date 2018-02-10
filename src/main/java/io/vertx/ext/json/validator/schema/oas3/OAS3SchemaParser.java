package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.json.validator.schema.SchemaParser;

import java.util.Arrays;
import java.util.List;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3SchemaParser extends SchemaParser {

    @Override
    public List<String> solveTypes(JsonObject obj) {
        String type = obj.getString("type");
        if (type == null)
            type = this.inferType(obj);
        return Arrays.asList(type);
    }

    @Override
    public String inferType(JsonObject obj) {
        return "string";
    }
}

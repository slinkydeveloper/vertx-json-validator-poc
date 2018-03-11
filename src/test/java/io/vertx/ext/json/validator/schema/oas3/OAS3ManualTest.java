package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.BaseManualTest;
import io.vertx.ext.json.validator.schema.Schema;

public class OAS3ManualTest extends BaseManualTest {
    @Override
    public Schema buildSchemaFunction(JsonObject schema) {
        return Schema.parseOAS3Schema(schema, getSchemasPath());
    }

    @Override
    public String getSchemasPath() {
        return "src/test/resources/openapi3/manual";
    }
}

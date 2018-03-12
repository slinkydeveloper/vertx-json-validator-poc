package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.BaseIntegrationTest;
import io.vertx.ext.json.validator.schema.Schema;
import org.assertj.core.util.Lists;
import org.junit.runners.Parameterized;

import java.util.List;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3IntegrationTest extends BaseIntegrationTest {
    public OAS3IntegrationTest(Object testName, Object testObject) {
        super(testName, testObject);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() throws Exception {
        List<String> tests = Lists.newArrayList(
                "additionalProperties",
                "allOf",
                "anyOf",
                "boolean_schema",
                "default",
                "definitions",
                "dependencies",
                "enum",
                "exclusiveMaximum",
                "exclusiveMinimum",
                "items",
                "maximum",
                "maxItems",
                "maxLength",
                "maxProperties",
                "minimum",
                "minItems",
                "minLength",
                "minProperties",
                "multipleOf",
                "not",
                "oneOf",
                "pattern",
                "properties",
                "ref",
                "refRemote",
                "required",
                "type",
                "uniqueItems"
        );
        return BaseIntegrationTest.buildParameters(tests, "src/test/resources/openapi3");
    }


    @Override
    public Schema buildSchemaFunction(JsonObject schema) {
        return Schema.parseOAS3Schema(schema, getSchemasPath());
    }

    @Override
    public String getSchemasPath() {
        return "src/test/resources/openapi3";
    }
}

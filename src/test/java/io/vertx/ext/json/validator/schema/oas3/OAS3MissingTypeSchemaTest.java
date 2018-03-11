package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public class OAS3MissingTypeSchemaTest extends OAS3ManualTest {

    @Test
    public void maximumAndMaxLength(TestContext context) throws IOException {
        Schema s = loadSchema("missingTypeSchema.json");
        s.validate("hel").setHandler(context.asyncAssertSuccess());
        s.validate("helloworldhowareyou").setHandler(context.asyncAssertFailure());
        s.validate(4).setHandler(context.asyncAssertSuccess());
        s.validate(45).setHandler(context.asyncAssertFailure());
        s.validate(new JsonObject()).setHandler(context.asyncAssertSuccess());
    }

}

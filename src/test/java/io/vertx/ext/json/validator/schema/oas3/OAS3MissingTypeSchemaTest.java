package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.*;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;

@RunWith(VertxUnitRunner.class)
public class OAS3MissingTypeSchemaTest extends OAS3ManualTest {

    @Test
    public void maximumAndMaxLength(TestContext context) throws IOException {
        Schema s = loadSchema("missingTypeSchema.json");
        assertThat(s).isInstanceOf(MissingTypeSchema.class);
        s.validate("hel").setHandler(context.asyncAssertSuccess());
        s.validate("helloworldhowareyou").setHandler(context.asyncAssertFailure());
        s.validate(4).setHandler(context.asyncAssertSuccess());
        s.validate(45).setHandler(context.asyncAssertFailure());
        s.validate(new JsonObject()).setHandler(context.asyncAssertSuccess());
    }

    @Test
    public void dontCreateMissingTypeSchema(TestContext context) {
        Schema s1 = Schema.parseOAS3Schema(new JsonObject().put("maximum", 100), "", SchemaParserProperties.OPTIMIZED);
        assertThat(s1).isInstanceOf(GenericNumberSchema.class);
        s1.validate(10).setHandler(context.asyncAssertSuccess());
        s1.validate(1000).setHandler(context.asyncAssertFailure());

        Schema s2 = Schema.parseOAS3Schema(new JsonObject().put("maxLength", 8), "", SchemaParserProperties.OPTIMIZED);
        assertThat(s2).isInstanceOf(StringSchema.class);
        s2.validate("bla").setHandler(context.asyncAssertSuccess());
        s2.validate("qwertyuiop").setHandler(context.asyncAssertFailure());
    }

    @Test
    public void forceCreateMissingTypeSchema(TestContext context) {
        Schema s1 = Schema.parseOAS3Schema(new JsonObject().put("maximum", 100), "", SchemaParserProperties.STRICT);
        assertThat(s1).isInstanceOf(MissingTypeSchema.class);
        s1.validate(10).setHandler(context.asyncAssertSuccess());
        s1.validate(1000).setHandler(context.asyncAssertFailure());

        Schema s2 = Schema.parseOAS3Schema(new JsonObject().put("maxLength", 8), "", SchemaParserProperties.STRICT);
        assertThat(s2).isInstanceOf(MissingTypeSchema.class);
        s2.validate("bla").setHandler(context.asyncAssertSuccess());
        s2.validate("qwertyuiop").setHandler(context.asyncAssertFailure());
    }

}

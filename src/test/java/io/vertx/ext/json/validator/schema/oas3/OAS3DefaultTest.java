package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonPointer;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.WrongSchemaException;
import io.vertx.ext.json.validator.schema.AllOfSchema;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.*;

@RunWith(VertxUnitRunner.class)
public class OAS3DefaultTest extends OAS3ManualTest {

    @Test
    public void applyDefaultValues(TestContext context) throws IOException {
        Schema s = loadSchema("defaultSchema_1.json");
        assertThat(s).isInstanceOf(OAS3ObjectSchema.class);
        s.validate(new JsonObject()).setHandler(context.asyncAssertSuccess(result -> {
            assertThat(result).isInstanceOf(JsonObject.class);
            assertThat(((JsonObject) result).containsKey("string")).isTrue();
            assertThat(((JsonObject) result).getString("string")).isEqualTo("francesco");
            assertThat(((JsonObject) result).containsKey("object")).isTrue();
            assertThat(((JsonObject) result).getJsonObject("object")).isEqualTo(new JsonObject().put("hello", "francesco"));
            assertThat(((JsonObject) result).containsKey("array")).isTrue();
            assertThat(((JsonObject) result).getJsonArray("array")).isEqualTo(new JsonArray().add("bla"));
        }));
    }

    @Test
    public void defaultShouldBeSameType(TestContext context) throws Throwable {
        assertThatThrownBy(() -> Schema.parseOAS3Schema(new JsonObject().put("type", "string").put("default", 0), ""))
                .isInstanceOf(WrongSchemaException.class)
                .hasCauseInstanceOf(ValidationException.class);
        //TODO Should create an exception for schema creation exception
    }

    @Test
    public void allOfDefaultValues(TestContext context) throws IOException {
        Schema s = loadSchema("allOfDeepDefault.json");
        assertThat(s).isInstanceOf(AllOfSchema.class);
        s.validate(new JsonObject().put("a", new JsonObject()).put("b", new JsonObject())).setHandler(context.asyncAssertSuccess(result -> {
            testJsonParameter(result, "/a/a", String.class, "deep_a");
            testJsonParameter(result, "/b/b", String.class, "deep_b");
            testJsonParameter(result, "/c", String.class, "c");
        }));
    }

    @Test
    public void additionalPropertiesDeep(TestContext context) throws IOException {
        Schema<JsonObject> s = loadSchema("additionalPropertiesDeepDefault.json");
        assertThat(s).isInstanceOf(OAS3ObjectSchema.class);
        s.validate(new JsonObject()).setHandler(context.asyncAssertSuccess());
        s.validate(
                new JsonObject().put("a",
                        new JsonObject().put("b",
                                new JsonObject().put("c2", "bla")
                        )
                )).setHandler(context.asyncAssertSuccess(res -> {
            testJsonParameter(res, "/a/b/c2", String.class);
            testJsonParameter(res, "/a/b/c1", String.class, "c1");
        }));
        s.validate(
                new JsonObject().put("a",
                        new JsonObject().put("b",
                                new JsonObject()
                        )
                )).setHandler(context.asyncAssertFailure(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(ValidationException.class)
                            .hasFieldOrPropertyWithValue("pointer", JsonPointer.from("/a/b"));
        }));
        s.validate(
                new JsonObject().put("a",
                        new JsonObject().put("b",
                                new JsonObject()
                                        .put("c2", "bla")
                                        .put("c3", new JsonObject())
                        )
                )).setHandler(context.asyncAssertFailure(throwable -> {
                    assertThat(throwable)
                        .isInstanceOf(ValidationException.class)
                        .hasFieldOrPropertyWithValue("pointer", JsonPointer.from("/a/b/c3"));
        }));
    }

}

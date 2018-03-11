package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationException;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Strings;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class OAS3IntegrationTest {

    @Rule
    public final JUnitSoftAssertions t = new JUnitSoftAssertions();

    @Rule
    public final RunTestOnContext classContext = new RunTestOnContext(Vertx::vertx);

    public static final int SCHEMA_SERVER_PORT = 1234;

    public static final String SCHEMA_SCOPE = "src/test/resources/openapi3";

    Vertx vertx;
    HttpServer schemaServer;
    String testName;
    JsonObject test;

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() throws Exception {
        List<String> tests = Lists.newArrayList(
                "additionalItems",
                "additionalProperties",
                "allOf",
                "anyOf",
                "boolean_schema",
                "const",
                "contains",
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
                "patternProperties",
                "properties",
                "propertyNames",
                "ref",
                "refRemote",
                "required",
                "type",
                "uniqueItems"
        );
        return tests.stream()
                .map(f -> new AbstractMap.SimpleImmutableEntry<>(f, Paths.get("src", "test", "resources", "openapi3", f + ".json")))
                .map(p -> {
                    try {
                        return new AbstractMap.SimpleImmutableEntry<>(p.getKey(), Files.readAllLines(p.getValue(), Charset.forName("UTF8")));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(strings -> new AbstractMap.SimpleImmutableEntry<>(strings.getKey(), String.join("", strings.getValue())))
                .map(string -> new AbstractMap.SimpleImmutableEntry<>(string.getKey(), new JsonArray(string.getValue())))
                .flatMap(t -> t.getValue()
                        .stream()
                        .map(JsonObject.class::cast)
                        .map(o -> new Object[]{t.getKey() + ": " + o.getString("description"), o})
                )
                .collect(Collectors.toList());
    }

    private void startSchemaServer(TestContext context) throws Exception {
        Router r = Router.router(vertx);
        r.route().handler(StaticHandler.create("./src/test/resources/openapi3/remotes"));
        schemaServer = vertx.createHttpServer(new HttpServerOptions().setPort(SCHEMA_SERVER_PORT))
                .requestHandler(r::accept).listen(context.asyncAssertSuccess());
    }

    private void stopSchemaServer(TestContext context) throws Exception {
        if (schemaServer != null) {
            Async async = context.async();
            try {
                schemaServer.close((asyncResult) -> {
                    async.complete();
                });
            } catch (IllegalStateException e) { // Server is already open
                async.complete();
            }
            async.await();
        }
    }

    public OAS3IntegrationTest(Object testName, Object testObject) {
        this.testName = (String) testName;
        this.test = (JsonObject) testObject;
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = classContext.vertx();
        //startSchemaServer(context);
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        //stopSchemaServer(context);
    }

    private Optional<Schema> buildSchema(String testName, JsonObject schema) {
        try {
            return Optional.of(Schema.parseOAS3Schema(schema, SCHEMA_SCOPE));
        } catch (Exception e) {
            t.fail("Something went wrong during schema initialization for test \"" + testName +"\"", e);
            return Optional.empty();
        }
    }

    private void validateSuccess(Schema schema, Object obj, String testCaseName, TestContext context) {
        Async async = context.async();
        schema.validate(obj).setHandler(new Handler<AsyncResult>() {
            @Override
            public void handle(AsyncResult event) {
                if (event.failed())
                    t.fail(String.format("\"%s\" -> \"%s\" should be valid", testName, testCaseName), event.cause());
                async.complete();
            }
        });
        async.await();
    }

    private void validateFailure(Schema schema, Object obj, String testCaseName, TestContext context) {
        Async async = context.async();
        schema.validate(obj).setHandler(new Handler<AsyncResult>() {
            @Override
            public void handle(AsyncResult event) {
                if (event.succeeded())
                    t.fail("\"%s\" -> \"%s\" should be invalid", testName, testCaseName);
                async.complete();
            }
        });
        async.await();
    }

    @Test
    public void test(TestContext context) {
        buildSchema(testName, test.getJsonObject("schema"))
                .ifPresent(schema -> {
                    for (Object tc : test.getJsonArray("tests").stream().collect(Collectors.toList())) {
                        JsonObject testCase = (JsonObject)tc;
                        if (testCase.getBoolean("valid"))
                            validateSuccess(schema, testCase.getValue("data"), testCase.getString("description"), context);
                        else
                            validateSuccess(schema, testCase.getValue("data"), testCase.getString("description"), context);
                    }
                });
    }

}

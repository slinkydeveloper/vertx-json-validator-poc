package io.vertx.ext.json.validator;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.json.validator.schema.oas3.OAS3SchemaParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class TestVerticle extends AbstractVerticle {

    private Future<JsonObject> loadJson(String path) {
        Future<JsonObject> f = Future.future();
        vertx.fileSystem().readFile(path, res -> {
            if (res.succeeded()) {
                f.complete(new JsonObject(res.result().toString(Charset.forName("UTF-8"))));
            } else {
                f.fail(res.cause());
            }
        });
        return f;
    }

    @Override
    public void start() {
        Handler<AsyncResult<Object>> validationHandler = ar -> {
            if (ar.succeeded())
                System.out.println(ar.result());
            else
                System.out.println(ar.cause());
        };
        loadJson("schema.json").setHandler(schemaAr -> {
            if (schemaAr.succeeded()) {
                Schema s = Schema.parseOAS3Schema(schemaAr.result(), new OAS3SchemaParser());
                CompositeFuture
                        .all(
                                loadJson("tests/test.json"),
                                loadJson("tests/test2.json"),
                                loadJson("tests/test3.json"),
                                loadJson("tests/test4.json")
                        ).setHandler(ar -> {
                            if (ar.succeeded()) {
                                ar.result().list().forEach(obj -> s.validate(obj).setHandler(validationHandler));
                            } else {
                                System.out.println(ar.cause());
                            }
                        });
            } else {
                System.out.println(schemaAr.cause());
            }
        });

    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestVerticle());
    }

}

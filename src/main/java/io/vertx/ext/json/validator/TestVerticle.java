package io.vertx.ext.json.validator;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.json.validator.schema.oas3.OAS3SchemaParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class TestVerticle extends AbstractVerticle {

    private Handler<AsyncResult<Object>> validationHandler(String file) {
        return ar -> {
            if (ar.failed() && !(ar.cause() instanceof ValidationException))
                ar.cause().printStackTrace();
            System.out.println("file: " +
                    file.substring(file.lastIndexOf("/") + 1) +
                    " result: " +
                    ((ar.succeeded()) ? ar.result() : ar.cause()));
        };
    }

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

    private Future loadJsonAndValidate(String path, Schema s) {
        return loadJson(path).compose((res) -> s.validate(res));
    }

    @Override
    public void start() {
        vertx.fileSystem().readDir("tests", event -> {
            if (event.succeeded()) {
                loadJson("schema.json").setHandler(schemaAr -> {
                    if (schemaAr.succeeded()) {
                        Schema s = Schema.parseOAS3Schema(schemaAr.result(), new OAS3SchemaParser());
                        event.result().stream().forEach((f) -> loadJsonAndValidate(f, s).setHandler(validationHandler(f)));
                    } else {
                        System.out.println(schemaAr.cause());
                    }
                });
            } else {
                System.err.println("You dumb " + event.cause());
            }
        });

    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestVerticle());
    }

}

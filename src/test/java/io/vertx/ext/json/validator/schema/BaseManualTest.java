package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BaseManualTest {

    public abstract Schema buildSchemaFunction(JsonObject schema);

    public abstract String getSchemasPath();

    public Schema loadSchema(String fileName) throws IOException {
        return buildSchemaFunction(new JsonObject(String.join("", Files.readAllLines(Paths.get(getSchemasPath(), fileName), Charset.forName("UTF8")))));
    }
}

package io.vertx.ext.json.validator.schema;

import io.vertx.core.json.JsonPointer;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

public abstract class BaseManualTest {

    public abstract Schema buildSchemaFunction(JsonObject schema);

    public abstract String getSchemasPath();

    public void testJsonParameter(Object obj, String path, Class c) {
        Object result = JsonPointer.from(path).query(obj);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(c);
    }

    public <T> void testJsonParameter(Object obj, String path, Class<? extends T> c, T val) {
        Object result = JsonPointer.from(path).query(obj);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(c);
        assertThat(result).isEqualTo(val);
    }

    public Schema loadSchema(String fileName) throws IOException {
        return buildSchemaFunction(new JsonObject(String.join("", Files.readAllLines(Paths.get(getSchemasPath(), fileName), Charset.forName("UTF8")))));
    }
}

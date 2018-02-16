package io.vertx.ext.json.validator.schema;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.ValidationExceptionFactory;

import java.lang.reflect.Constructor;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public interface Schema<T> {

    Future<T> validate(Object obj);

    static Schema parseOAS3Schema(JsonObject jsonObject, SchemaParser parser) {
        try {
            Class<? extends Schema> schemaClass = parser.solveType(jsonObject);
            Constructor<? extends Schema> constructor = schemaClass.getConstructor(JsonObject.class, SchemaParser.class);
            return constructor.newInstance(jsonObject, parser);
        } catch (Exception e) {
            // Something happened during schema instantiation (wrong schema)
            System.out.println("Wrong schema! " + e);
            e.printStackTrace();
            return null;
        }
    }

}

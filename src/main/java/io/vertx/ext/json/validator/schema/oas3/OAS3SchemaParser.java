package io.vertx.ext.json.validator.schema.oas3;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.schema.NumberSchema;
import io.vertx.ext.json.validator.schema.Schema;
import io.vertx.ext.json.validator.schema.SchemaParser;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class OAS3SchemaParser extends SchemaParser {

    private Class<? extends NumberSchema> solveIntegerType(JsonObject obj) {
        return ("int64".equals(obj.getString("format"))) ? OAS3LongSchema.class : OAS3IntegerSchema.class;
    }

    private Class<? extends NumberSchema> solveFloatingPointType(JsonObject obj) {
        return ("double".equals(obj.getString("format"))) ? OAS3DoubleSchema.class : OAS3FloatSchema.class;
    }

    @Override
    public Class<? extends Schema> solveType(JsonObject obj) {
        String type = obj.getString("type");
        if (type == null) {
            return this.inferType(obj);
        } else {
            switch (type) {
                case "integer": return solveIntegerType(obj);
                case "number": return solveFloatingPointType(obj);
                case "string": return OAS3StringSchema.class;
                case "object": return OAS3ObjectSchema.class;
                default: return OAS3StringSchema.class; // Should throw an error here!
            }
        }
    }

    @Override
    public Class<? extends Schema> inferType(JsonObject obj) {
        return OAS3StringSchema.class; // This should be replaced with type guessing logic
    }
}

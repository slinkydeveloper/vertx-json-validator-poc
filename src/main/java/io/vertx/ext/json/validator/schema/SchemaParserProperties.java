package io.vertx.ext.json.validator.schema;

//TODO dataobject
public class SchemaParserProperties {

    public static SchemaParserProperties STRICT = new SchemaParserProperties().setForceTypeKeywordInference(false);
    public static SchemaParserProperties OPTIMIZED = new SchemaParserProperties().setForceTypeKeywordInference(true);

    private final static boolean FORCE_TYPE_KEYWORD_INFERENCE = true;

    private boolean forceTypeKeywordInference;

    public SchemaParserProperties() {
        this.forceTypeKeywordInference = FORCE_TYPE_KEYWORD_INFERENCE;
    } //TODO to choose

    public boolean isForceTypeKeywordInference() {
        return forceTypeKeywordInference;
    }

    public SchemaParserProperties setForceTypeKeywordInference(boolean forceTypeKeywordInference) {
        this.forceTypeKeywordInference = forceTypeKeywordInference;
        return this;
    }
}

package com.azul.crs.com.fasterxml.jackson.core;

import com.azul.crs.com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.azul.crs.com.fasterxml.jackson.core.json.JsonReadFeature;
import com.azul.crs.com.fasterxml.jackson.core.io.OutputDecorator;
import com.azul.crs.com.fasterxml.jackson.core.io.InputDecorator;

public abstract class TSFBuilder<F extends JsonFactory, B extends TSFBuilder<F, B>>
{
    protected static final int DEFAULT_FACTORY_FEATURE_FLAGS;
    protected static final int DEFAULT_PARSER_FEATURE_FLAGS;
    protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS;
    protected int _factoryFeatures;
    protected int _streamReadFeatures;
    protected int _streamWriteFeatures;
    protected InputDecorator _inputDecorator;
    protected OutputDecorator _outputDecorator;
    
    protected TSFBuilder() {
        this._factoryFeatures = TSFBuilder.DEFAULT_FACTORY_FEATURE_FLAGS;
        this._streamReadFeatures = TSFBuilder.DEFAULT_PARSER_FEATURE_FLAGS;
        this._streamWriteFeatures = TSFBuilder.DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._inputDecorator = null;
        this._outputDecorator = null;
    }
    
    protected TSFBuilder(final JsonFactory base) {
        this(base._factoryFeatures, base._parserFeatures, base._generatorFeatures);
    }
    
    protected TSFBuilder(final int factoryFeatures, final int parserFeatures, final int generatorFeatures) {
        this._factoryFeatures = factoryFeatures;
        this._streamReadFeatures = parserFeatures;
        this._streamWriteFeatures = generatorFeatures;
    }
    
    public int factoryFeaturesMask() {
        return this._factoryFeatures;
    }
    
    public int streamReadFeatures() {
        return this._streamReadFeatures;
    }
    
    public int streamWriteFeatures() {
        return this._streamWriteFeatures;
    }
    
    public InputDecorator inputDecorator() {
        return this._inputDecorator;
    }
    
    public OutputDecorator outputDecorator() {
        return this._outputDecorator;
    }
    
    public B enable(final JsonFactory.Feature f) {
        this._factoryFeatures |= f.getMask();
        return this._this();
    }
    
    public B disable(final JsonFactory.Feature f) {
        this._factoryFeatures &= ~f.getMask();
        return this._this();
    }
    
    public B configure(final JsonFactory.Feature f, final boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }
    
    public B enable(final StreamReadFeature f) {
        this._streamReadFeatures |= f.mappedFeature().getMask();
        return this._this();
    }
    
    public B enable(final StreamReadFeature first, final StreamReadFeature... other) {
        this._streamReadFeatures |= first.mappedFeature().getMask();
        for (final StreamReadFeature f : other) {
            this._streamReadFeatures |= f.mappedFeature().getMask();
        }
        return this._this();
    }
    
    public B disable(final StreamReadFeature f) {
        this._streamReadFeatures &= ~f.mappedFeature().getMask();
        return this._this();
    }
    
    public B disable(final StreamReadFeature first, final StreamReadFeature... other) {
        this._streamReadFeatures &= ~first.mappedFeature().getMask();
        for (final StreamReadFeature f : other) {
            this._streamReadFeatures &= ~f.mappedFeature().getMask();
        }
        return this._this();
    }
    
    public B configure(final StreamReadFeature f, final boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }
    
    public B enable(final StreamWriteFeature f) {
        this._streamWriteFeatures |= f.mappedFeature().getMask();
        return this._this();
    }
    
    public B enable(final StreamWriteFeature first, final StreamWriteFeature... other) {
        this._streamWriteFeatures |= first.mappedFeature().getMask();
        for (final StreamWriteFeature f : other) {
            this._streamWriteFeatures |= f.mappedFeature().getMask();
        }
        return this._this();
    }
    
    public B disable(final StreamWriteFeature f) {
        this._streamWriteFeatures &= ~f.mappedFeature().getMask();
        return this._this();
    }
    
    public B disable(final StreamWriteFeature first, final StreamWriteFeature... other) {
        this._streamWriteFeatures &= ~first.mappedFeature().getMask();
        for (final StreamWriteFeature f : other) {
            this._streamWriteFeatures &= ~f.mappedFeature().getMask();
        }
        return this._this();
    }
    
    public B configure(final StreamWriteFeature f, final boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }
    
    public B enable(final JsonReadFeature f) {
        return this._failNonJSON(f);
    }
    
    public B enable(final JsonReadFeature first, final JsonReadFeature... other) {
        return this._failNonJSON(first);
    }
    
    public B disable(final JsonReadFeature f) {
        return this._failNonJSON(f);
    }
    
    public B disable(final JsonReadFeature first, final JsonReadFeature... other) {
        return this._failNonJSON(first);
    }
    
    public B configure(final JsonReadFeature f, final boolean state) {
        return this._failNonJSON(f);
    }
    
    private B _failNonJSON(final Object feature) {
        throw new IllegalArgumentException("Feature " + feature.getClass().getName() + "#" + feature.toString() + " not supported for non-JSON backend");
    }
    
    public B enable(final JsonWriteFeature f) {
        return this._failNonJSON(f);
    }
    
    public B enable(final JsonWriteFeature first, final JsonWriteFeature... other) {
        return this._failNonJSON(first);
    }
    
    public B disable(final JsonWriteFeature f) {
        return this._failNonJSON(f);
    }
    
    public B disable(final JsonWriteFeature first, final JsonWriteFeature... other) {
        return this._failNonJSON(first);
    }
    
    public B configure(final JsonWriteFeature f, final boolean state) {
        return this._failNonJSON(f);
    }
    
    public B inputDecorator(final InputDecorator dec) {
        this._inputDecorator = dec;
        return this._this();
    }
    
    public B outputDecorator(final OutputDecorator dec) {
        this._outputDecorator = dec;
        return this._this();
    }
    
    public abstract F build();
    
    protected final B _this() {
        return (B)this;
    }
    
    protected void _legacyEnable(final JsonParser.Feature f) {
        if (f != null) {
            this._streamReadFeatures |= f.getMask();
        }
    }
    
    protected void _legacyDisable(final JsonParser.Feature f) {
        if (f != null) {
            this._streamReadFeatures &= ~f.getMask();
        }
    }
    
    protected void _legacyEnable(final JsonGenerator.Feature f) {
        if (f != null) {
            this._streamWriteFeatures |= f.getMask();
        }
    }
    
    protected void _legacyDisable(final JsonGenerator.Feature f) {
        if (f != null) {
            this._streamWriteFeatures &= ~f.getMask();
        }
    }
    
    static {
        DEFAULT_FACTORY_FEATURE_FLAGS = JsonFactory.Feature.collectDefaults();
        DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
        DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
    }
}

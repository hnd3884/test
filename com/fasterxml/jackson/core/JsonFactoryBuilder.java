package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.io.CharacterEscapes;

public class JsonFactoryBuilder extends TSFBuilder<JsonFactory, JsonFactoryBuilder>
{
    protected CharacterEscapes _characterEscapes;
    protected SerializableString _rootValueSeparator;
    protected int _maximumNonEscapedChar;
    protected char _quoteChar;
    
    public JsonFactoryBuilder() {
        this._quoteChar = '\"';
        this._rootValueSeparator = JsonFactory.DEFAULT_ROOT_VALUE_SEPARATOR;
        this._maximumNonEscapedChar = 0;
    }
    
    public JsonFactoryBuilder(final JsonFactory base) {
        super(base);
        this._quoteChar = '\"';
        this._characterEscapes = base.getCharacterEscapes();
        this._rootValueSeparator = base._rootValueSeparator;
        this._maximumNonEscapedChar = base._maximumNonEscapedChar;
    }
    
    @Override
    public JsonFactoryBuilder enable(final JsonReadFeature f) {
        this._legacyEnable(f.mappedFeature());
        return this;
    }
    
    @Override
    public JsonFactoryBuilder enable(final JsonReadFeature first, final JsonReadFeature... other) {
        this._legacyEnable(first.mappedFeature());
        this.enable(first);
        for (final JsonReadFeature f : other) {
            this._legacyEnable(f.mappedFeature());
        }
        return this;
    }
    
    @Override
    public JsonFactoryBuilder disable(final JsonReadFeature f) {
        this._legacyDisable(f.mappedFeature());
        return this;
    }
    
    @Override
    public JsonFactoryBuilder disable(final JsonReadFeature first, final JsonReadFeature... other) {
        this._legacyDisable(first.mappedFeature());
        for (final JsonReadFeature f : other) {
            this._legacyEnable(f.mappedFeature());
        }
        return this;
    }
    
    @Override
    public JsonFactoryBuilder configure(final JsonReadFeature f, final boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }
    
    @Override
    public JsonFactoryBuilder enable(final JsonWriteFeature f) {
        final JsonGenerator.Feature old = f.mappedFeature();
        if (old != null) {
            this._legacyEnable(old);
        }
        return this;
    }
    
    @Override
    public JsonFactoryBuilder enable(final JsonWriteFeature first, final JsonWriteFeature... other) {
        this._legacyEnable(first.mappedFeature());
        for (final JsonWriteFeature f : other) {
            this._legacyEnable(f.mappedFeature());
        }
        return this;
    }
    
    @Override
    public JsonFactoryBuilder disable(final JsonWriteFeature f) {
        this._legacyDisable(f.mappedFeature());
        return this;
    }
    
    @Override
    public JsonFactoryBuilder disable(final JsonWriteFeature first, final JsonWriteFeature... other) {
        this._legacyDisable(first.mappedFeature());
        for (final JsonWriteFeature f : other) {
            this._legacyDisable(f.mappedFeature());
        }
        return this;
    }
    
    @Override
    public JsonFactoryBuilder configure(final JsonWriteFeature f, final boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }
    
    public JsonFactoryBuilder characterEscapes(final CharacterEscapes esc) {
        this._characterEscapes = esc;
        return this;
    }
    
    public JsonFactoryBuilder rootValueSeparator(final String sep) {
        this._rootValueSeparator = ((sep == null) ? null : new SerializedString(sep));
        return this;
    }
    
    public JsonFactoryBuilder rootValueSeparator(final SerializableString sep) {
        this._rootValueSeparator = sep;
        return this;
    }
    
    public JsonFactoryBuilder highestNonEscapedChar(final int maxNonEscaped) {
        this._maximumNonEscapedChar = ((maxNonEscaped <= 0) ? 0 : Math.max(127, maxNonEscaped));
        return this;
    }
    
    public JsonFactoryBuilder quoteChar(final char ch) {
        if (ch > '\u007f') {
            throw new IllegalArgumentException("Can only use Unicode characters up to 0x7F as quote characters");
        }
        this._quoteChar = ch;
        return this;
    }
    
    public CharacterEscapes characterEscapes() {
        return this._characterEscapes;
    }
    
    public SerializableString rootValueSeparator() {
        return this._rootValueSeparator;
    }
    
    public int highestNonEscapedChar() {
        return this._maximumNonEscapedChar;
    }
    
    public char quoteChar() {
        return this._quoteChar;
    }
    
    @Override
    public JsonFactory build() {
        return new JsonFactory(this);
    }
}

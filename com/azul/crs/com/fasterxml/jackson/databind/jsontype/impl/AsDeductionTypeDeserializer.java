package com.azul.crs.com.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.util.TokenBuffer;
import java.util.LinkedList;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import java.util.List;
import java.util.Iterator;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.lang.reflect.Type;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import java.util.HashMap;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import java.util.BitSet;
import java.util.Map;

public class AsDeductionTypeDeserializer extends AsPropertyTypeDeserializer
{
    private static final long serialVersionUID = 1L;
    private final Map<String, Integer> fieldBitIndex;
    private final Map<BitSet, String> subtypeFingerprints;
    
    public AsDeductionTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final JavaType defaultImpl, final DeserializationConfig config, final Collection<NamedType> subtypes) {
        super(bt, idRes, null, false, defaultImpl);
        this.fieldBitIndex = new HashMap<String, Integer>();
        this.subtypeFingerprints = this.buildFingerprints(config, subtypes);
    }
    
    public AsDeductionTypeDeserializer(final AsDeductionTypeDeserializer src, final BeanProperty property) {
        super(src, property);
        this.fieldBitIndex = src.fieldBitIndex;
        this.subtypeFingerprints = src.subtypeFingerprints;
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return null;
    }
    
    @Override
    public TypeDeserializer forProperty(final BeanProperty prop) {
        return (prop == this._property) ? this : new AsDeductionTypeDeserializer(this, prop);
    }
    
    protected Map<BitSet, String> buildFingerprints(final DeserializationConfig config, final Collection<NamedType> subtypes) {
        final boolean ignoreCase = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        int nextField = 0;
        final Map<BitSet, String> fingerprints = new HashMap<BitSet, String>();
        for (final NamedType subtype : subtypes) {
            final JavaType subtyped = config.getTypeFactory().constructType(subtype.getType());
            final List<BeanPropertyDefinition> properties = config.introspect(subtyped).findProperties();
            final BitSet fingerprint = new BitSet(nextField + properties.size());
            for (final BeanPropertyDefinition property : properties) {
                String name = property.getName();
                if (ignoreCase) {
                    name = name.toLowerCase();
                }
                Integer bitIndex = this.fieldBitIndex.get(name);
                if (bitIndex == null) {
                    bitIndex = nextField;
                    this.fieldBitIndex.put(name, nextField++);
                }
                fingerprint.set(bitIndex);
            }
            final String existingFingerprint = fingerprints.put(fingerprint, subtype.getType().getName());
            if (existingFingerprint != null) {
                throw new IllegalStateException(String.format("Subtypes %s and %s have the same signature and cannot be uniquely deduced.", existingFingerprint, subtype.getType().getName()));
            }
        }
        return fingerprints;
    }
    
    @Override
    public Object deserializeTypedFromObject(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
            final List<BitSet> candidates = new LinkedList<BitSet>(this.subtypeFingerprints.keySet());
            final TokenBuffer tb = new TokenBuffer(p, ctxt);
            final boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            while (t == JsonToken.FIELD_NAME) {
                String name = p.currentName();
                if (ignoreCase) {
                    name = name.toLowerCase();
                }
                tb.copyCurrentStructure(p);
                final Integer bit = this.fieldBitIndex.get(name);
                if (bit != null) {
                    prune(candidates, bit);
                    if (candidates.size() == 1) {
                        return this._deserializeTypedForId(p, ctxt, tb, this.subtypeFingerprints.get(candidates.get(0)));
                    }
                }
                t = p.nextToken();
            }
            throw new InvalidTypeIdException(p, String.format("Cannot deduce unique subtype of %s (%d candidates match)", ClassUtil.getTypeDescription(this._baseType), candidates.size()), this._baseType, "DEDUCED");
        }
        return this._deserializeTypedUsingDefaultImpl(p, ctxt, null);
    }
    
    private static void prune(final List<BitSet> candidates, final int bit) {
        final Iterator<BitSet> iter = candidates.iterator();
        while (iter.hasNext()) {
            if (!iter.next().get(bit)) {
                iter.remove();
            }
        }
    }
}

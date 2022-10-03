package com.fasterxml.jackson.module.jaxb;

import com.fasterxml.jackson.databind.type.TypeFactory;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.StdConverter;

public class AdapterConverter extends StdConverter<Object, Object>
{
    protected final JavaType _inputType;
    protected final JavaType _targetType;
    protected final XmlAdapter<Object, Object> _adapter;
    protected final boolean _forSerialization;
    
    public AdapterConverter(final XmlAdapter<?, ?> adapter, final JavaType inType, final JavaType outType, final boolean ser) {
        this._adapter = (XmlAdapter<Object, Object>)adapter;
        this._inputType = inType;
        this._targetType = outType;
        this._forSerialization = ser;
    }
    
    public Object convert(final Object value) {
        try {
            if (this._forSerialization) {
                return this._adapter.marshal(value);
            }
            return this._adapter.unmarshal(value);
        }
        catch (final RuntimeException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new IllegalArgumentException(e2);
        }
    }
    
    public JavaType getInputType(final TypeFactory typeFactory) {
        return this._inputType;
    }
    
    public JavaType getOutputType(final TypeFactory typeFactory) {
        return this._targetType;
    }
}

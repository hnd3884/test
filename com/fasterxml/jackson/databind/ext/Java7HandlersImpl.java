package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.nio.file.Path;

public class Java7HandlersImpl extends Java7Handlers
{
    private final Class<?> _pathClass;
    
    public Java7HandlersImpl() {
        this._pathClass = Path.class;
    }
    
    @Override
    public Class<?> getClassJavaNioFilePath() {
        return this._pathClass;
    }
    
    @Override
    public JsonDeserializer<?> getDeserializerForJavaNioFilePath(final Class<?> rawType) {
        if (rawType == this._pathClass) {
            return new NioPathDeserializer();
        }
        return null;
    }
    
    @Override
    public JsonSerializer<?> getSerializerForJavaNioFilePath(final Class<?> rawType) {
        if (this._pathClass.isAssignableFrom(rawType)) {
            return new NioPathSerializer();
        }
        return null;
    }
}

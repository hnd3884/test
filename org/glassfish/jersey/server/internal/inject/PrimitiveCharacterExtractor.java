package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.core.MultivaluedMap;

class PrimitiveCharacterExtractor implements MultivaluedParameterExtractor<Object>
{
    final String parameter;
    final String defaultStringValue;
    final Object defaultPrimitiveTypeValue;
    
    public PrimitiveCharacterExtractor(final String parameter, final String defaultStringValue, final Object defaultPrimitiveTypeValue) {
        this.parameter = parameter;
        this.defaultStringValue = defaultStringValue;
        this.defaultPrimitiveTypeValue = defaultPrimitiveTypeValue;
    }
    
    @Override
    public String getName() {
        return this.parameter;
    }
    
    @Override
    public String getDefaultValueString() {
        return this.defaultStringValue;
    }
    
    @Override
    public Object extract(final MultivaluedMap<String, String> parameters) {
        final String v = (String)parameters.getFirst((Object)this.parameter);
        if (v != null && !v.trim().isEmpty()) {
            if (v.length() == 1) {
                return v.charAt(0);
            }
            throw new ExtractorException(LocalizationMessages.ERROR_PARAMETER_INVALID_CHAR_VALUE(v));
        }
        else {
            if (this.defaultStringValue == null || this.defaultStringValue.trim().isEmpty()) {
                return this.defaultPrimitiveTypeValue;
            }
            if (this.defaultStringValue.length() == 1) {
                return this.defaultStringValue.charAt(0);
            }
            throw new ExtractorException(LocalizationMessages.ERROR_PARAMETER_INVALID_CHAR_VALUE(this.defaultStringValue));
        }
    }
}

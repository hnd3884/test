package org.glassfish.jersey.server.internal.inject;

import javax.ws.rs.core.MultivaluedMap;

final class SingleStringValueExtractor implements MultivaluedParameterExtractor<String>
{
    private final String paramName;
    private final String defaultValue;
    
    public SingleStringValueExtractor(final String parameterName, final String defaultValue) {
        this.paramName = parameterName;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public String getName() {
        return this.paramName;
    }
    
    @Override
    public String getDefaultValueString() {
        return this.defaultValue;
    }
    
    @Override
    public String extract(final MultivaluedMap<String, String> parameters) {
        final String value = (String)parameters.getFirst((Object)this.paramName);
        return (value != null) ? value : this.defaultValue;
    }
}

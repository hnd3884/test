package org.apache.catalina.core;

public class ApplicationMappingImpl
{
    private final String matchValue;
    private final String pattern;
    private final ApplicationMappingMatch mappingType;
    private final String servletName;
    
    public ApplicationMappingImpl(final String matchValue, final String pattern, final ApplicationMappingMatch mappingType, final String servletName) {
        this.matchValue = matchValue;
        this.pattern = pattern;
        this.mappingType = mappingType;
        this.servletName = servletName;
    }
    
    public String getMatchValue() {
        return this.matchValue;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public ApplicationMappingMatch getMappingMatch() {
        return this.mappingType;
    }
    
    public String getServletName() {
        return this.servletName;
    }
}

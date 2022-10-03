package org.owasp.esapi.waf.internal;

public class Parameter
{
    private String name;
    private String value;
    private boolean fromMultipart;
    
    public Parameter(final String name, final String value, final boolean fromMultipart) {
        this.name = name;
        this.value = value;
        this.fromMultipart = fromMultipart;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}

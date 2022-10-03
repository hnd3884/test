package javax.ws.rs.core;

import java.util.Map;
import java.util.LinkedHashMap;

public class Form
{
    private final MultivaluedMap<String, String> parameters;
    
    public Form() {
        this(new AbstractMultivaluedMap<String, String>(new LinkedHashMap()) {});
    }
    
    public Form(final String parameterName, final String parameterValue) {
        this();
        this.parameters.add(parameterName, parameterValue);
    }
    
    public Form(final MultivaluedMap<String, String> store) {
        this.parameters = store;
    }
    
    public Form param(final String name, final String value) {
        this.parameters.add(name, value);
        return this;
    }
    
    public MultivaluedMap<String, String> asMap() {
        return this.parameters;
    }
}

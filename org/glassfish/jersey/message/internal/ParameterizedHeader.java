package org.glassfish.jersey.message.internal;

import java.util.Collections;
import java.text.ParseException;
import java.util.Map;

public class ParameterizedHeader
{
    private String value;
    private Map<String, String> parameters;
    
    public ParameterizedHeader(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public ParameterizedHeader(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.value = "";
        while (reader.hasNext() && !reader.hasNextSeparator(';', false)) {
            reader.next();
            this.value += (Object)reader.getEventValue();
        }
        if (reader.hasNext()) {
            this.parameters = HttpHeaderReader.readParameters(reader);
        }
        if (this.parameters == null) {
            this.parameters = Collections.emptyMap();
        }
        else {
            this.parameters = Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.parameters);
        }
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
}

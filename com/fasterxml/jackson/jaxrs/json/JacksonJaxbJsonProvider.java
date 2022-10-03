package com.fasterxml.jackson.jaxrs.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({ "*/*" })
@Produces({ "*/*" })
public class JacksonJaxbJsonProvider extends JacksonJsonProvider
{
    public static final Annotations[] DEFAULT_ANNOTATIONS;
    
    public JacksonJaxbJsonProvider() {
        this(null, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
    }
    
    public JacksonJaxbJsonProvider(final Annotations... annotationsToUse) {
        this(null, annotationsToUse);
    }
    
    public JacksonJaxbJsonProvider(final ObjectMapper mapper, final Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
    }
    
    static {
        DEFAULT_ANNOTATIONS = new Annotations[] { Annotations.JACKSON, Annotations.JAXB };
    }
}

package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({ "*/*" })
@Produces({ "*/*" })
public class JacksonJaxbXMLProvider extends JacksonXMLProvider
{
    public static final Annotations[] DEFAULT_ANNOTATIONS;
    
    public JacksonJaxbXMLProvider() {
        this(null, JacksonJaxbXMLProvider.DEFAULT_ANNOTATIONS);
    }
    
    public JacksonJaxbXMLProvider(final Annotations... annotationsToUse) {
        this(null, annotationsToUse);
    }
    
    public JacksonJaxbXMLProvider(final XmlMapper mapper, final Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
    }
    
    static {
        DEFAULT_ANNOTATIONS = new Annotations[] { Annotations.JACKSON, Annotations.JAXB };
    }
}

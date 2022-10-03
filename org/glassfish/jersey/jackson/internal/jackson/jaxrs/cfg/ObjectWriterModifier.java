package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import javax.ws.rs.core.MultivaluedMap;

public abstract class ObjectWriterModifier
{
    public abstract ObjectWriter modify(final EndpointConfigBase<?> p0, final MultivaluedMap<String, Object> p1, final Object p2, final ObjectWriter p3, final JsonGenerator p4) throws IOException;
}

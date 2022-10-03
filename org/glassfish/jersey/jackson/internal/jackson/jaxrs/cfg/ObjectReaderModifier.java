package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.JavaType;
import javax.ws.rs.core.MultivaluedMap;

public abstract class ObjectReaderModifier
{
    public abstract ObjectReader modify(final EndpointConfigBase<?> p0, final MultivaluedMap<String, String> p1, final JavaType p2, final ObjectReader p3, final JsonParser p4) throws IOException;
}

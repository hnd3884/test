package org.glassfish.jersey.server.wadl;

import javax.xml.bind.JAXBContext;
import com.sun.research.ws.wadl.Application;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import javax.ws.rs.core.UriInfo;

public interface WadlApplicationContext
{
    ApplicationDescription getApplication(final UriInfo p0, final boolean p1);
    
    Application getApplication(final UriInfo p0, final Resource p1, final boolean p2);
    
    JAXBContext getJAXBContext();
    
    void setWadlGenerationEnabled(final boolean p0);
    
    boolean isWadlGenerationEnabled();
}

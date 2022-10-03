package javax.management.loading;

import java.io.IOException;
import java.util.Enumeration;
import java.io.InputStream;
import java.net.URL;
import javax.management.ServiceNotFoundException;
import java.util.Set;

public interface MLetMBean
{
    Set<Object> getMBeansFromURL(final String p0) throws ServiceNotFoundException;
    
    Set<Object> getMBeansFromURL(final URL p0) throws ServiceNotFoundException;
    
    void addURL(final URL p0);
    
    void addURL(final String p0) throws ServiceNotFoundException;
    
    URL[] getURLs();
    
    URL getResource(final String p0);
    
    InputStream getResourceAsStream(final String p0);
    
    Enumeration<URL> getResources(final String p0) throws IOException;
    
    String getLibraryDirectory();
    
    void setLibraryDirectory(final String p0);
}

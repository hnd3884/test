package javax.ws.rs.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import javax.ws.rs.core.Configurable;

public interface Client extends Configurable<Client>
{
    void close();
    
    WebTarget target(final String p0);
    
    WebTarget target(final URI p0);
    
    WebTarget target(final UriBuilder p0);
    
    WebTarget target(final Link p0);
    
    Invocation.Builder invocation(final Link p0);
    
    SSLContext getSslContext();
    
    HostnameVerifier getHostnameVerifier();
}

package java.security;

import java.net.URI;
import javax.security.auth.login.Configuration;

public class URIParameter implements Policy.Parameters, Configuration.Parameters
{
    private URI uri;
    
    public URIParameter(final URI uri) {
        if (uri == null) {
            throw new NullPointerException("invalid null URI");
        }
        this.uri = uri;
    }
    
    public URI getURI() {
        return this.uri;
    }
}

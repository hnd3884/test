package javax.print.attribute;

import java.net.URI;
import java.io.Serializable;

public abstract class URISyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = -7842661210486401678L;
    private URI uri;
    
    protected URISyntax(final URI uri) {
        this.uri = verify(uri);
    }
    
    private static URI verify(final URI uri) {
        if (uri == null) {
            throw new NullPointerException(" uri is null");
        }
        return uri;
    }
    
    public URI getURI() {
        return this.uri;
    }
    
    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof URISyntax && this.uri.equals(((URISyntax)o).uri);
    }
    
    @Override
    public String toString() {
        return this.uri.toString();
    }
}

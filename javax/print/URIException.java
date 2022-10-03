package javax.print;

import java.net.URI;

public interface URIException
{
    public static final int URIInaccessible = 1;
    public static final int URISchemeNotSupported = 2;
    public static final int URIOtherProblem = -1;
    
    URI getUnsupportedURI();
    
    int getReason();
}

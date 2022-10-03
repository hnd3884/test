package sun.security.provider.certpath;

import java.security.cert.CertPathBuilderException;

public class SunCertPathBuilderException extends CertPathBuilderException
{
    private static final long serialVersionUID = -7814288414129264709L;
    private transient AdjacencyList adjList;
    
    public SunCertPathBuilderException() {
    }
    
    public SunCertPathBuilderException(final String s) {
        super(s);
    }
    
    public SunCertPathBuilderException(final Throwable t) {
        super(t);
    }
    
    public SunCertPathBuilderException(final String s, final Throwable t) {
        super(s, t);
    }
    
    SunCertPathBuilderException(final String s, final AdjacencyList adjList) {
        this(s);
        this.adjList = adjList;
    }
    
    SunCertPathBuilderException(final String s, final Throwable t, final AdjacencyList adjList) {
        this(s, t);
        this.adjList = adjList;
    }
    
    public AdjacencyList getAdjacencyList() {
        return this.adjList;
    }
}

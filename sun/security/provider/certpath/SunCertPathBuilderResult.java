package sun.security.provider.certpath;

import java.security.PublicKey;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.CertPath;
import sun.security.util.Debug;
import java.security.cert.PKIXCertPathBuilderResult;

public class SunCertPathBuilderResult extends PKIXCertPathBuilderResult
{
    private static final Debug debug;
    private AdjacencyList adjList;
    
    SunCertPathBuilderResult(final CertPath certPath, final TrustAnchor trustAnchor, final PolicyNode policyNode, final PublicKey publicKey, final AdjacencyList adjList) {
        super(certPath, trustAnchor, policyNode, publicKey);
        this.adjList = adjList;
    }
    
    public AdjacencyList getAdjacencyList() {
        return this.adjList;
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}

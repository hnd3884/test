package org.jscep.client.inspect;

import java.security.cert.X509Certificate;

public interface CertStoreInspector
{
    X509Certificate getSigner();
    
    X509Certificate getRecipient();
    
    X509Certificate getIssuer();
}

package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface PKMACValuesCalculator
{
    void setup(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1) throws CRMFException;
    
    byte[] calculateDigest(final byte[] p0) throws CRMFException;
    
    byte[] calculateMac(final byte[] p0, final byte[] p1) throws CRMFException;
}

package org.bouncycastle.cert;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public interface X509ContentVerifierProviderBuilder
{
    ContentVerifierProvider build(final SubjectPublicKeyInfo p0) throws OperatorCreationException;
    
    ContentVerifierProvider build(final X509CertificateHolder p0) throws OperatorCreationException;
}

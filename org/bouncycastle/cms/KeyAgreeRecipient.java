package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface KeyAgreeRecipient extends Recipient
{
    RecipientOperator getRecipientOperator(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1, final SubjectPublicKeyInfo p2, final ASN1OctetString p3, final byte[] p4) throws CMSException;
    
    AlgorithmIdentifier getPrivateKeyAlgorithmIdentifier();
}

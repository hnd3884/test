package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface KeyTransRecipient extends Recipient
{
    RecipientOperator getRecipientOperator(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1, final byte[] p2) throws CMSException;
}

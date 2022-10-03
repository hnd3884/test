package org.bouncycastle.operator.jcajce;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Key;
import org.bouncycastle.operator.GenericKey;

public class JceGenericKey extends GenericKey
{
    private static Object getRepresentation(final Key key) {
        final byte[] encoded = key.getEncoded();
        if (encoded != null) {
            return encoded;
        }
        return key;
    }
    
    public JceGenericKey(final AlgorithmIdentifier algorithmIdentifier, final Key key) {
        super(algorithmIdentifier, getRepresentation(key));
    }
}

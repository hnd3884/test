package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface OutputEncryptor
{
    AlgorithmIdentifier getAlgorithmIdentifier();
    
    OutputStream getOutputStream(final OutputStream p0);
    
    GenericKey getKey();
}

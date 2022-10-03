package org.bouncycastle.cms;

import org.bouncycastle.util.io.TeeInputStream;
import java.io.InputStream;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class RecipientOperator
{
    private final AlgorithmIdentifier algorithmIdentifier;
    private final Object operator;
    
    public RecipientOperator(final InputDecryptor operator) {
        this.algorithmIdentifier = operator.getAlgorithmIdentifier();
        this.operator = operator;
    }
    
    public RecipientOperator(final MacCalculator operator) {
        this.algorithmIdentifier = operator.getAlgorithmIdentifier();
        this.operator = operator;
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        if (this.operator instanceof InputDecryptor) {
            return ((InputDecryptor)this.operator).getInputStream(inputStream);
        }
        return (InputStream)new TeeInputStream(inputStream, ((MacCalculator)this.operator).getOutputStream());
    }
    
    public boolean isMacBased() {
        return this.operator instanceof MacCalculator;
    }
    
    public byte[] getMac() {
        return ((MacCalculator)this.operator).getMac();
    }
}

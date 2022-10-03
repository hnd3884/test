package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.operator.OperatorCreationException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class BcDigestCalculatorProvider implements DigestCalculatorProvider
{
    private BcDigestProvider digestProvider;
    
    public BcDigestCalculatorProvider() {
        this.digestProvider = BcDefaultDigestProvider.INSTANCE;
    }
    
    public DigestCalculator get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return new DigestCalculator() {
            final /* synthetic */ DigestOutputStream val$stream = new DigestOutputStream((Digest)BcDigestCalculatorProvider.this.digestProvider.get(algorithmIdentifier));
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier;
            }
            
            public OutputStream getOutputStream() {
                return this.val$stream;
            }
            
            public byte[] getDigest() {
                return this.val$stream.getDigest();
            }
        };
    }
    
    private class DigestOutputStream extends OutputStream
    {
        private Digest dig;
        
        DigestOutputStream(final Digest dig) {
            this.dig = dig;
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.dig.update(array, n, n2);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.dig.update(array, 0, array.length);
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.dig.update((byte)n);
        }
        
        byte[] getDigest() {
            final byte[] array = new byte[this.dig.getDigestSize()];
            this.dig.doFinal(array, 0);
            return array;
        }
    }
}

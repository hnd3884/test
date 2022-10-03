package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.security.MessageDigest;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;

public class JcaDigestCalculatorProviderBuilder
{
    private OperatorHelper helper;
    
    public JcaDigestCalculatorProviderBuilder() {
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    }
    
    public JcaDigestCalculatorProviderBuilder setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JcaDigestCalculatorProviderBuilder setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public DigestCalculatorProvider build() throws OperatorCreationException {
        return new DigestCalculatorProvider() {
            public DigestCalculator get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                DigestOutputStream digestOutputStream;
                try {
                    digestOutputStream = new DigestOutputStream(JcaDigestCalculatorProviderBuilder.this.helper.createDigest(algorithmIdentifier));
                }
                catch (final GeneralSecurityException ex) {
                    throw new OperatorCreationException("exception on setup: " + ex, ex);
                }
                return new DigestCalculator() {
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return algorithmIdentifier;
                    }
                    
                    public OutputStream getOutputStream() {
                        return digestOutputStream;
                    }
                    
                    public byte[] getDigest() {
                        return digestOutputStream.getDigest();
                    }
                };
            }
        };
    }
    
    private class DigestOutputStream extends OutputStream
    {
        private MessageDigest dig;
        
        DigestOutputStream(final MessageDigest dig) {
            this.dig = dig;
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.dig.update(array, n, n2);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.dig.update(array);
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.dig.update((byte)n);
        }
        
        byte[] getDigest() {
            return this.dig.digest();
        }
    }
}

package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.jcajce.io.MacOutputStream;
import java.io.OutputStream;
import javax.crypto.KeyGenerator;
import java.security.Key;
import javax.crypto.Mac;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.SecretKey;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.MacCalculator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class JceCMSMacCalculatorBuilder
{
    private final ASN1ObjectIdentifier macOID;
    private final int keySize;
    private EnvelopedDataHelper helper;
    private AlgorithmParameters algorithmParameters;
    private SecureRandom random;
    
    public JceCMSMacCalculatorBuilder(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this(asn1ObjectIdentifier, -1);
    }
    
    public JceCMSMacCalculatorBuilder(final ASN1ObjectIdentifier macOID, final int keySize) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.macOID = macOID;
        this.keySize = keySize;
    }
    
    public JceCMSMacCalculatorBuilder setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }
    
    public JceCMSMacCalculatorBuilder setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        return this;
    }
    
    public JceCMSMacCalculatorBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public JceCMSMacCalculatorBuilder setAlgorithmParameters(final AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }
    
    public MacCalculator build() throws CMSException {
        return new CMSMacCalculator(this.macOID, this.keySize, this.algorithmParameters, this.random);
    }
    
    private class CMSMacCalculator implements MacCalculator
    {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Mac mac;
        
        CMSMacCalculator(final ASN1ObjectIdentifier asn1ObjectIdentifier, final int n, AlgorithmParameters generateParameters, SecureRandom secureRandom) throws CMSException {
            final KeyGenerator keyGenerator = JceCMSMacCalculatorBuilder.this.helper.createKeyGenerator(asn1ObjectIdentifier);
            if (secureRandom == null) {
                secureRandom = new SecureRandom();
            }
            if (n < 0) {
                keyGenerator.init(secureRandom);
            }
            else {
                keyGenerator.init(n, secureRandom);
            }
            this.encKey = keyGenerator.generateKey();
            if (generateParameters == null) {
                generateParameters = JceCMSMacCalculatorBuilder.this.helper.generateParameters(asn1ObjectIdentifier, this.encKey, secureRandom);
            }
            this.algorithmIdentifier = JceCMSMacCalculatorBuilder.this.helper.getAlgorithmIdentifier(asn1ObjectIdentifier, generateParameters);
            this.mac = JceCMSMacCalculatorBuilder.this.helper.createContentMac(this.encKey, this.algorithmIdentifier);
        }
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }
        
        public OutputStream getOutputStream() {
            return (OutputStream)new MacOutputStream(this.mac);
        }
        
        public byte[] getMac() {
            return this.mac.doFinal();
        }
        
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }
}

package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.jcajce.io.MacOutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import javax.crypto.SecretKey;
import javax.crypto.Mac;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.jcajce.PKCS12Key;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class JcePKCS12MacCalculatorBuilder implements PKCS12MacCalculatorBuilder
{
    private JcaJceHelper helper;
    private ASN1ObjectIdentifier algorithm;
    private SecureRandom random;
    private int saltLength;
    private int iterationCount;
    
    public JcePKCS12MacCalculatorBuilder() {
        this(OIWObjectIdentifiers.idSHA1);
    }
    
    public JcePKCS12MacCalculatorBuilder(final ASN1ObjectIdentifier algorithm) {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.iterationCount = 1024;
        this.algorithm = algorithm;
    }
    
    public JcePKCS12MacCalculatorBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JcePKCS12MacCalculatorBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public JcePKCS12MacCalculatorBuilder setIterationCount(final int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }
    
    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)DERNull.INSTANCE);
    }
    
    public MacCalculator build(final char[] array) throws OperatorCreationException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            final Mac mac = this.helper.createMac(this.algorithm.getId());
            this.saltLength = mac.getMacLength();
            final byte[] array2 = new byte[this.saltLength];
            this.random.nextBytes(array2);
            final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(array2, this.iterationCount);
            final PKCS12Key pkcs12Key = new PKCS12Key(array);
            mac.init((Key)pkcs12Key, pbeParameterSpec);
            return new MacCalculator() {
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return new AlgorithmIdentifier(JcePKCS12MacCalculatorBuilder.this.algorithm, (ASN1Encodable)new PKCS12PBEParams(array2, JcePKCS12MacCalculatorBuilder.this.iterationCount));
                }
                
                public OutputStream getOutputStream() {
                    return (OutputStream)new MacOutputStream(mac);
                }
                
                public byte[] getMac() {
                    return mac.doFinal();
                }
                
                public GenericKey getKey() {
                    return new GenericKey(this.getAlgorithmIdentifier(), ((Key)pkcs12Key).getEncoded());
                }
            };
        }
        catch (final Exception ex) {
            throw new OperatorCreationException("unable to create MAC calculator: " + ex.getMessage(), ex);
        }
    }
}

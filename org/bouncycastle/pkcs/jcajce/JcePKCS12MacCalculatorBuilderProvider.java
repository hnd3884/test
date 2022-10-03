package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.jcajce.io.MacOutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import javax.crypto.SecretKey;
import javax.crypto.Mac;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.jcajce.PKCS12Key;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;

public class JcePKCS12MacCalculatorBuilderProvider implements PKCS12MacCalculatorBuilderProvider
{
    private JcaJceHelper helper;
    
    public JcePKCS12MacCalculatorBuilderProvider() {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JcePKCS12MacCalculatorBuilderProvider setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JcePKCS12MacCalculatorBuilderProvider setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier algorithmIdentifier) {
        return new PKCS12MacCalculatorBuilder() {
            public MacCalculator build(final char[] array) throws OperatorCreationException {
                final PKCS12PBEParams instance = PKCS12PBEParams.getInstance((Object)algorithmIdentifier.getParameters());
                try {
                    final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
                    final Mac mac = JcePKCS12MacCalculatorBuilderProvider.this.helper.createMac(algorithm.getId());
                    final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(instance.getIV(), instance.getIterations().intValue());
                    final PKCS12Key pkcs12Key = new PKCS12Key(array);
                    mac.init((Key)pkcs12Key, pbeParameterSpec);
                    return new MacCalculator() {
                        public AlgorithmIdentifier getAlgorithmIdentifier() {
                            return new AlgorithmIdentifier(algorithm, (ASN1Encodable)instance);
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
            
            public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
                return new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE);
            }
        };
    }
}

package org.bouncycastle.cert.crmf.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import javax.crypto.Mac;
import java.security.MessageDigest;
import org.bouncycastle.cert.crmf.PKMACValuesCalculator;

public class JcePKMACValuesCalculator implements PKMACValuesCalculator
{
    private MessageDigest digest;
    private Mac mac;
    private CRMFHelper helper;
    
    public JcePKMACValuesCalculator() {
        this.helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
    }
    
    public JcePKMACValuesCalculator setProvider(final Provider provider) {
        this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JcePKMACValuesCalculator setProvider(final String s) {
        this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public void setup(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2) throws CRMFException {
        this.digest = this.helper.createDigest(algorithmIdentifier.getAlgorithm());
        this.mac = this.helper.createMac(algorithmIdentifier2.getAlgorithm());
    }
    
    public byte[] calculateDigest(final byte[] array) {
        return this.digest.digest(array);
    }
    
    public byte[] calculateMac(final byte[] array, final byte[] array2) throws CRMFException {
        try {
            this.mac.init(new SecretKeySpec(array, this.mac.getAlgorithm()));
            return this.mac.doFinal(array2);
        }
        catch (final GeneralSecurityException ex) {
            throw new CRMFException("failure in setup: " + ex.getMessage(), ex);
        }
    }
}

package org.bouncycastle.cms.jcajce;

import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.cms.CMSException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Provider;
import java.security.SecureRandom;

public class JceAlgorithmIdentifierConverter
{
    private EnvelopedDataHelper helper;
    private SecureRandom random;
    
    public JceAlgorithmIdentifierConverter() {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }
    
    public JceAlgorithmIdentifierConverter setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }
    
    public JceAlgorithmIdentifierConverter setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        return this;
    }
    
    public AlgorithmParameters getAlgorithmParameters(final AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        if (algorithmIdentifier.getParameters() == null) {
            return null;
        }
        try {
            final AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(algorithmIdentifier.getAlgorithm());
            CMSUtils.loadParameters(algorithmParameters, algorithmIdentifier.getParameters());
            return algorithmParameters;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new CMSException("can't find parameters for algorithm", ex);
        }
        catch (final NoSuchProviderException ex2) {
            throw new CMSException("can't find provider for algorithm", ex2);
        }
    }
}

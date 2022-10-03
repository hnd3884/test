package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class XMSS
{
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.xmss.";
    
    public static class Mappings extends AsymmetricAlgorithmProvider
    {
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyPairGeneratorSpi");
            this.addSignatureAlgorithm(configurableProvider, "SHA256", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha256", BCObjectIdentifiers.xmss_with_SHA256);
            this.addSignatureAlgorithm(configurableProvider, "SHAKE128", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake128", BCObjectIdentifiers.xmss_with_SHAKE128);
            this.addSignatureAlgorithm(configurableProvider, "SHA512", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha512", BCObjectIdentifiers.xmss_with_SHA512);
            this.addSignatureAlgorithm(configurableProvider, "SHAKE256", "XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake256", BCObjectIdentifiers.xmss_with_SHAKE256);
            configurableProvider.addAlgorithm("KeyFactory.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyPairGeneratorSpi");
            this.addSignatureAlgorithm(configurableProvider, "SHA256", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha256", BCObjectIdentifiers.xmss_mt_with_SHA256);
            this.addSignatureAlgorithm(configurableProvider, "SHAKE128", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake128", BCObjectIdentifiers.xmss_mt_with_SHAKE128);
            this.addSignatureAlgorithm(configurableProvider, "SHA512", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha512", BCObjectIdentifiers.xmss_mt_with_SHA512);
            this.addSignatureAlgorithm(configurableProvider, "SHAKE256", "XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake256", BCObjectIdentifiers.xmss_mt_with_SHAKE256);
            this.registerOid(configurableProvider, PQCObjectIdentifiers.xmss, "XMSS", new XMSSKeyFactorySpi());
            this.registerOid(configurableProvider, PQCObjectIdentifiers.xmss_mt, "XMSSMT", new XMSSMTKeyFactorySpi());
        }
    }
}

package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyFactorySpi;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class Rainbow
{
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.rainbow.";
    
    public static class Mappings extends AsymmetricAlgorithmProvider
    {
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.Rainbow", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.Rainbow", "org.bouncycastle.pqc.jcajce.provider.rainbow.RainbowKeyPairGeneratorSpi");
            this.addSignatureAlgorithm(configurableProvider, "SHA224", "Rainbow", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$withSha224", PQCObjectIdentifiers.rainbowWithSha224);
            this.addSignatureAlgorithm(configurableProvider, "SHA256", "Rainbow", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$withSha256", PQCObjectIdentifiers.rainbowWithSha256);
            this.addSignatureAlgorithm(configurableProvider, "SHA384", "Rainbow", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$withSha384", PQCObjectIdentifiers.rainbowWithSha384);
            this.addSignatureAlgorithm(configurableProvider, "SHA512", "Rainbow", "org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi$withSha512", PQCObjectIdentifiers.rainbowWithSha512);
            this.registerOid(configurableProvider, PQCObjectIdentifiers.rainbow, "Rainbow", new RainbowKeyFactorySpi());
        }
    }
}

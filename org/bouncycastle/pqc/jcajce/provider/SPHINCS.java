package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class SPHINCS
{
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.sphincs.";
    
    public static class Mappings extends AsymmetricAlgorithmProvider
    {
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyPairGeneratorSpi");
            this.addSignatureAlgorithm(configurableProvider, "SHA512", "SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha512", PQCObjectIdentifiers.sphincs256_with_SHA512);
            this.addSignatureAlgorithm(configurableProvider, "SHA3-512", "SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha3_512", PQCObjectIdentifiers.sphincs256_with_SHA3_512);
            this.registerOid(configurableProvider, PQCObjectIdentifiers.sphincs256, "SPHINCS256", new Sphincs256KeyFactorySpi());
            this.registerOidAlgorithmParameters(configurableProvider, PQCObjectIdentifiers.sphincs256, "SPHINCS256");
        }
    }
}

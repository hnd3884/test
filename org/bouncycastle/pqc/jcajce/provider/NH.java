package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class NH
{
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.newhope.";
    
    public static class Mappings extends AsymmetricAlgorithmProvider
    {
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.NH", "org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.NH", "org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyAgreement.NH", "org.bouncycastle.pqc.jcajce.provider.newhope.KeyAgreementSpi");
            this.registerOid(configurableProvider, PQCObjectIdentifiers.newHope, "NH", new NHKeyFactorySpi());
        }
    }
}

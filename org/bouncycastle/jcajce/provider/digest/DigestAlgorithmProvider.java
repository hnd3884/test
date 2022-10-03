package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class DigestAlgorithmProvider extends AlgorithmProvider
{
    protected void addHMACAlgorithm(final ConfigurableProvider configurableProvider, final String s, final String s2, final String s3) {
        final String string = "HMAC" + s;
        configurableProvider.addAlgorithm("Mac." + string, s2);
        configurableProvider.addAlgorithm("Alg.Alias.Mac.HMAC-" + s, string);
        configurableProvider.addAlgorithm("Alg.Alias.Mac.HMAC/" + s, string);
        configurableProvider.addAlgorithm("KeyGenerator." + string, s3);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.HMAC-" + s, string);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.HMAC/" + s, string);
    }
    
    protected void addHMACAlias(final ConfigurableProvider configurableProvider, final String s, final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String string = "HMAC" + s;
        configurableProvider.addAlgorithm("Alg.Alias.Mac." + asn1ObjectIdentifier, string);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + asn1ObjectIdentifier, string);
    }
}

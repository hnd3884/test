package org.bouncycastle.jcajce.provider.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public abstract class AsymmetricAlgorithmProvider extends AlgorithmProvider
{
    protected void addSignatureAlgorithm(final ConfigurableProvider configurableProvider, final String s, final String s2, final String s3, final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String string = s + "WITH" + s2;
        final String string2 = s + "with" + s2;
        final String string3 = s + "With" + s2;
        final String string4 = s + "/" + s2;
        configurableProvider.addAlgorithm("Signature." + string, s3);
        configurableProvider.addAlgorithm("Alg.Alias.Signature." + string2, string);
        configurableProvider.addAlgorithm("Alg.Alias.Signature." + string3, string);
        configurableProvider.addAlgorithm("Alg.Alias.Signature." + string4, string);
        configurableProvider.addAlgorithm("Alg.Alias.Signature." + asn1ObjectIdentifier, string);
        configurableProvider.addAlgorithm("Alg.Alias.Signature.OID." + asn1ObjectIdentifier, string);
    }
    
    protected void registerOid(final ConfigurableProvider configurableProvider, final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s, final AsymmetricKeyInfoConverter asymmetricKeyInfoConverter) {
        configurableProvider.addAlgorithm("Alg.Alias.KeyFactory." + asn1ObjectIdentifier, s);
        configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator." + asn1ObjectIdentifier, s);
        configurableProvider.addKeyInfoConverter(asn1ObjectIdentifier, asymmetricKeyInfoConverter);
    }
    
    protected void registerOidAlgorithmParameters(final ConfigurableProvider configurableProvider, final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + asn1ObjectIdentifier, s);
    }
    
    protected void registerOidAlgorithmParameterGenerator(final ConfigurableProvider configurableProvider, final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + asn1ObjectIdentifier, s);
        configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + asn1ObjectIdentifier, s);
    }
}

package org.bouncycastle.operator.jcajce;

import org.bouncycastle.asn1.DEROctetString;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import javax.crypto.spec.OAEPParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class JcaAlgorithmParametersConverter
{
    public AlgorithmIdentifier getAlgorithmIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmParameters algorithmParameters) throws InvalidAlgorithmParameterException {
        try {
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)ASN1Primitive.fromByteArray(algorithmParameters.getEncoded()));
        }
        catch (final IOException ex) {
            throw new InvalidAlgorithmParameterException("unable to encode parameters object: " + ex.getMessage());
        }
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof OAEPParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter spec passed.");
        }
        if (algorithmParameterSpec.equals(OAEPParameterSpec.DEFAULT)) {
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new RSAESOAEPparams(RSAESOAEPparams.DEFAULT_HASH_ALGORITHM, RSAESOAEPparams.DEFAULT_MASK_GEN_FUNCTION, RSAESOAEPparams.DEFAULT_P_SOURCE_ALGORITHM));
        }
        final OAEPParameterSpec oaepParameterSpec = (OAEPParameterSpec)algorithmParameterSpec;
        final PSource pSource = oaepParameterSpec.getPSource();
        if (!oaepParameterSpec.getMGFAlgorithm().equals(OAEPParameterSpec.DEFAULT.getMGFAlgorithm())) {
            throw new InvalidAlgorithmParameterException("only " + OAEPParameterSpec.DEFAULT.getMGFAlgorithm() + " mask generator supported.");
        }
        return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new RSAESOAEPparams(new DefaultDigestAlgorithmIdentifierFinder().find(oaepParameterSpec.getDigestAlgorithm()), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)new DefaultDigestAlgorithmIdentifierFinder().find(((MGF1ParameterSpec)oaepParameterSpec.getMGFParameters()).getDigestAlgorithm())), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, (ASN1Encodable)new DEROctetString(((PSource.PSpecified)pSource).getValue()))));
    }
}

package org.bouncycastle.cms.bc;

import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.PasswordRecipientInfoGenerator;

public class BcPasswordRecipientInfoGenerator extends PasswordRecipientInfoGenerator
{
    public BcPasswordRecipientInfoGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier, final char[] array) {
        super(asn1ObjectIdentifier, array);
    }
    
    @Override
    protected byte[] calculateDerivedKey(final int n, final AlgorithmIdentifier algorithmIdentifier, final int n2) throws CMSException {
        final PBKDF2Params instance = PBKDF2Params.getInstance((Object)algorithmIdentifier.getParameters());
        final byte[] array = (n == 0) ? PBEParametersGenerator.PKCS5PasswordToBytes(this.password) : PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password);
        try {
            final PKCS5S2ParametersGenerator pkcs5S2ParametersGenerator = new PKCS5S2ParametersGenerator((Digest)EnvelopedDataHelper.getPRF(instance.getPrf()));
            pkcs5S2ParametersGenerator.init(array, instance.getSalt(), instance.getIterationCount().intValue());
            return ((KeyParameter)pkcs5S2ParametersGenerator.generateDerivedParameters(n2)).getKey();
        }
        catch (final Exception ex) {
            throw new CMSException("exception creating derived key: " + ex.getMessage(), ex);
        }
    }
    
    public byte[] generateEncryptedBytes(final AlgorithmIdentifier algorithmIdentifier, final byte[] array, final GenericKey genericKey) throws CMSException {
        final byte[] key = ((KeyParameter)CMSUtils.getBcKey(genericKey)).getKey();
        final Wrapper rfc3211Wrapper = EnvelopedDataHelper.createRFC3211Wrapper(algorithmIdentifier.getAlgorithm());
        rfc3211Wrapper.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(array), ASN1OctetString.getInstance((Object)algorithmIdentifier.getParameters()).getOctets()));
        return rfc3211Wrapper.wrap(key, 0, key.length);
    }
}

package org.bouncycastle.cms.bc;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.PasswordRecipient;

public abstract class BcPasswordRecipient implements PasswordRecipient
{
    private final char[] password;
    private int schemeID;
    
    BcPasswordRecipient(final char[] password) {
        this.schemeID = 1;
        this.password = password;
    }
    
    public BcPasswordRecipient setPasswordConversionScheme(final int schemeID) {
        this.schemeID = schemeID;
        return this;
    }
    
    protected KeyParameter extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array, final byte[] array2) throws CMSException {
        final Wrapper rfc3211Wrapper = EnvelopedDataHelper.createRFC3211Wrapper(algorithmIdentifier.getAlgorithm());
        rfc3211Wrapper.init(false, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(array), ASN1OctetString.getInstance((Object)algorithmIdentifier.getParameters()).getOctets()));
        try {
            return new KeyParameter(rfc3211Wrapper.unwrap(array2, 0, array2.length));
        }
        catch (final InvalidCipherTextException ex) {
            throw new CMSException("unable to unwrap key: " + ex.getMessage(), (Exception)ex);
        }
    }
    
    public byte[] calculateDerivedKey(final int n, final AlgorithmIdentifier algorithmIdentifier, final int n2) throws CMSException {
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
    
    public int getPasswordConversionScheme() {
        return this.schemeID;
    }
    
    public char[] getPassword() {
        return this.password;
    }
}

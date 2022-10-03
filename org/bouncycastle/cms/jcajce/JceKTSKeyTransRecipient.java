package org.bouncycastle.cms.jcajce;

import org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.cms.CMSException;
import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.security.PrivateKey;
import org.bouncycastle.cms.KeyTransRecipient;

public abstract class JceKTSKeyTransRecipient implements KeyTransRecipient
{
    private static final byte[] ANONYMOUS_SENDER;
    private final byte[] partyVInfo;
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    protected Map extraMappings;
    protected boolean validateKeySize;
    protected boolean unwrappedKeyMustBeEncodable;
    
    public JceKTSKeyTransRecipient(final PrivateKey recipientKey, final byte[] partyVInfo) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.contentHelper = this.helper;
        this.extraMappings = new HashMap();
        this.validateKeySize = false;
        this.recipientKey = recipientKey;
        this.partyVInfo = partyVInfo;
    }
    
    public JceKTSKeyTransRecipient setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKTSKeyTransRecipient setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKTSKeyTransRecipient setAlgorithmMapping(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        this.extraMappings.put(asn1ObjectIdentifier, s);
        return this;
    }
    
    public JceKTSKeyTransRecipient setContentProvider(final Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }
    
    public JceKTSKeyTransRecipient setContentProvider(final String s) {
        this.contentHelper = CMSUtils.createContentHelper(s);
        return this;
    }
    
    public JceKTSKeyTransRecipient setKeySizeValidation(final boolean validateKeySize) {
        this.validateKeySize = validateKeySize;
        return this;
    }
    
    protected Key extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        final JceKTSKeyUnwrapper asymmetricUnwrapper = this.helper.createAsymmetricUnwrapper(algorithmIdentifier, this.recipientKey, JceKTSKeyTransRecipient.ANONYMOUS_SENDER, this.partyVInfo);
        try {
            final Key jceKey = this.helper.getJceKey(algorithmIdentifier2.getAlgorithm(), asymmetricUnwrapper.generateUnwrappedKey(algorithmIdentifier2, array));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(algorithmIdentifier2, jceKey);
            }
            return jceKey;
        }
        catch (final OperatorException ex) {
            throw new CMSException("exception unwrapping key: " + ex.getMessage(), ex);
        }
    }
    
    protected static byte[] getPartyVInfoFromRID(final KeyTransRecipientId keyTransRecipientId) throws IOException {
        if (keyTransRecipientId.getSerialNumber() != null) {
            return new IssuerAndSerialNumber(keyTransRecipientId.getIssuer(), keyTransRecipientId.getSerialNumber()).getEncoded("DER");
        }
        return new DEROctetString(keyTransRecipientId.getSubjectKeyIdentifier()).getEncoded();
    }
    
    static {
        ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");
    }
}

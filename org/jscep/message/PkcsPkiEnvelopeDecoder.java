package org.jscep.message;

import org.bouncycastle.asn1.ASN1Encodable;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.CipherInputStream;
import java.io.InputStream;
import org.bouncycastle.operator.InputDecryptor;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.slf4j.LoggerFactory;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.CMSEnvelopedData;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;

public final class PkcsPkiEnvelopeDecoder
{
    private static final Logger LOGGER;
    private final X509Certificate recipient;
    private final PrivateKey privKey;
    
    public PkcsPkiEnvelopeDecoder(final X509Certificate recipient, final PrivateKey privKey) {
        this.recipient = recipient;
        this.privKey = privKey;
    }
    
    public byte[] decode(final CMSEnvelopedData pkcsPkiEnvelope) throws MessageDecodingException {
        PkcsPkiEnvelopeDecoder.LOGGER.debug("Decoding pkcsPkiEnvelope");
        this.validate(pkcsPkiEnvelope);
        PkcsPkiEnvelopeDecoder.LOGGER.debug("Decrypting pkcsPkiEnvelope using key belonging to [dn={}; serial={}]", (Object)this.recipient.getSubjectDN(), (Object)this.recipient.getSerialNumber());
        final RecipientInformationStore recipientInfos = pkcsPkiEnvelope.getRecipientInfos();
        final RecipientInformation info = recipientInfos.get((RecipientId)new JceKeyTransRecipientId(this.recipient));
        if (info == null) {
            throw new MessageDecodingException("Missing expected key transfer recipient " + this.recipient.getSubjectDN());
        }
        PkcsPkiEnvelopeDecoder.LOGGER.debug("pkcsPkiEnvelope encryption algorithm: {}", (Object)info.getKeyEncryptionAlgorithm().getAlgorithm());
        try {
            final byte[] messageData = info.getContent((Recipient)this.getKeyTransRecipient());
            PkcsPkiEnvelopeDecoder.LOGGER.debug("Finished decoding pkcsPkiEnvelope");
            return messageData;
        }
        catch (final CMSException e) {
            throw new MessageDecodingException((Throwable)e);
        }
    }
    
    private JceKeyTransEnvelopedRecipient getKeyTransRecipient() {
        return new InternalKeyTransEnvelopedRecipient(this.privKey);
    }
    
    private void validate(final CMSEnvelopedData pkcsPkiEnvelope) {
        final EnvelopedData ed = EnvelopedData.getInstance((Object)pkcsPkiEnvelope.toASN1Structure().getContent());
        PkcsPkiEnvelopeDecoder.LOGGER.debug("pkcsPkiEnvelope version: {}", (Object)ed.getVersion());
        PkcsPkiEnvelopeDecoder.LOGGER.debug("pkcsPkiEnvelope encryptedContentInfo contentType: {}", (Object)ed.getEncryptedContentInfo().getContentType());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PkcsPkiEnvelopeDecoder.class);
    }
    
    private static class InternalKeyTransEnvelopedRecipient extends JceKeyTransEnvelopedRecipient
    {
        private static final String RSA = "RSA/ECB/PKCS1Padding";
        private static final String DES = "DES";
        private final PrivateKey wrappingKey;
        
        public InternalKeyTransEnvelopedRecipient(final PrivateKey wrappingKey) {
            super(wrappingKey);
            this.wrappingKey = wrappingKey;
        }
        
        public RecipientOperator getRecipientOperator(final AlgorithmIdentifier notUsed, final AlgorithmIdentifier contentAlg, final byte[] wrappedKey) throws CMSException {
            if ("1.3.14.3.2.7".equals(contentAlg.getAlgorithm().getId())) {
                Cipher dataCipher;
                try {
                    final Key contentKey = this.unwrapKey(this.wrappingKey, wrappedKey);
                    dataCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                    dataCipher.init(2, contentKey, this.getIV(contentAlg));
                }
                catch (final GeneralSecurityException e) {
                    throw new CMSException("Could not create DES cipher", (Exception)e);
                }
                return new RecipientOperator((InputDecryptor)new InputDecryptor() {
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return contentAlg;
                    }
                    
                    public InputStream getInputStream(final InputStream dataIn) {
                        return new CipherInputStream(dataIn, dataCipher);
                    }
                });
            }
            return super.getRecipientOperator(notUsed, contentAlg, wrappedKey);
        }
        
        private Key unwrapKey(final PrivateKey wrappingKey, final byte[] wrappedKey) throws GeneralSecurityException {
            final Cipher unwrapper = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            unwrapper.init(4, wrappingKey);
            try {
                return unwrapper.unwrap(wrappedKey, "DES", 3);
            }
            catch (final InvalidKeyException e) {
                PkcsPkiEnvelopeDecoder.LOGGER.error("Cannot unwrap symetric key.  Are you using a valid key pair?");
                throw e;
            }
        }
        
        private AlgorithmParameterSpec getIV(final AlgorithmIdentifier envelopingAlgorithm) throws GeneralSecurityException {
            final ASN1Encodable ivParams = envelopingAlgorithm.getParameters();
            return new IvParameterSpec(ASN1OctetString.getInstance((Object)ivParams).getOctets());
        }
    }
}

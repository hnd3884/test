package org.jscep.message;

import org.slf4j.LoggerFactory;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;

public final class PkcsPkiEnvelopeEncoder
{
    private static final Logger LOGGER;
    private final X509Certificate recipient;
    private final ASN1ObjectIdentifier encAlgId;
    
    @Deprecated
    public PkcsPkiEnvelopeEncoder(final X509Certificate recipient) {
        this(recipient, "DES");
    }
    
    public PkcsPkiEnvelopeEncoder(final X509Certificate recipient, final String encAlg) {
        this.recipient = recipient;
        this.encAlgId = this.getAlgorithmId(encAlg);
    }
    
    public CMSEnvelopedData encode(final byte[] messageData) throws MessageEncodingException {
        PkcsPkiEnvelopeEncoder.LOGGER.debug("Encoding pkcsPkiEnvelope");
        final CMSEnvelopedDataGenerator edGenerator = new CMSEnvelopedDataGenerator();
        final CMSTypedData envelopable = (CMSTypedData)new CMSProcessableByteArray(messageData);
        RecipientInfoGenerator recipientGenerator;
        try {
            recipientGenerator = (RecipientInfoGenerator)new JceKeyTransRecipientInfoGenerator(this.recipient);
        }
        catch (final CertificateEncodingException e) {
            throw new MessageEncodingException(e);
        }
        edGenerator.addRecipientInfoGenerator(recipientGenerator);
        PkcsPkiEnvelopeEncoder.LOGGER.debug("Encrypting pkcsPkiEnvelope using key belonging to [dn={}; serial={}]", (Object)this.recipient.getSubjectDN(), (Object)this.recipient.getSerialNumber());
        OutputEncryptor encryptor;
        try {
            encryptor = new JceCMSContentEncryptorBuilder(this.encAlgId).build();
        }
        catch (final CMSException e2) {
            throw new MessageEncodingException((Throwable)e2);
        }
        try {
            final CMSEnvelopedData pkcsPkiEnvelope = edGenerator.generate(envelopable, encryptor);
            PkcsPkiEnvelopeEncoder.LOGGER.debug("Finished encoding pkcsPkiEnvelope");
            return pkcsPkiEnvelope;
        }
        catch (final CMSException e2) {
            throw new MessageEncodingException((Throwable)e2);
        }
    }
    
    private ASN1ObjectIdentifier getAlgorithmId(final String encAlg) {
        if ("DES".equals(encAlg)) {
            return CMSAlgorithm.DES_CBC;
        }
        if ("AES".equals(encAlg) || "AES_128".equals(encAlg)) {
            return CMSAlgorithm.AES128_CBC;
        }
        if ("AES_192".equals(encAlg)) {
            return CMSAlgorithm.AES192_CBC;
        }
        if ("AES_256".equals(encAlg)) {
            return CMSAlgorithm.AES256_CBC;
        }
        if ("DESede".equals(encAlg)) {
            return CMSAlgorithm.DES_EDE3_CBC;
        }
        throw new IllegalArgumentException("Unknown algorithm: " + encAlg);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PkcsPkiEnvelopeEncoder.class);
    }
}

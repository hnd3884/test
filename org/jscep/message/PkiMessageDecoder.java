package org.jscep.message;

import org.slf4j.LoggerFactory;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.jscep.transaction.FailInfo;
import org.jscep.transaction.TransactionId;
import org.jscep.transaction.Nonce;
import java.util.Iterator;
import java.util.Hashtable;
import org.bouncycastle.cms.SignerInformationVerifier;
import java.util.Collection;
import org.bouncycastle.util.Store;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.CMSProcessable;
import java.io.IOException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.jscep.asn1.IssuerAndSubject;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.jscep.transaction.PkiStatus;
import org.jscep.transaction.MessageType;
import org.jscep.asn1.ScepObjectIdentifier;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;
import java.security.cert.CertificateException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.jcajce.JcaSignerId;
import org.bouncycastle.cms.CMSSignedData;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;

public final class PkiMessageDecoder
{
    private static final Logger LOGGER;
    private final PkcsPkiEnvelopeDecoder decoder;
    private final X509Certificate signer;
    
    public PkiMessageDecoder(final X509Certificate signer, final PkcsPkiEnvelopeDecoder decoder) {
        this.decoder = decoder;
        this.signer = signer;
    }
    
    public PkiMessage<?> decode(final CMSSignedData pkiMessage) throws MessageDecodingException {
        PkiMessageDecoder.LOGGER.debug("Decoding pkiMessage");
        this.validate(pkiMessage);
        final CMSProcessable signedContent = (CMSProcessable)pkiMessage.getSignedContent();
        final SignerInformationStore signerStore = pkiMessage.getSignerInfos();
        final SignerInformation signerInfo = signerStore.get((SignerId)new JcaSignerId(this.signer));
        if (signerInfo == null) {
            throw new MessageDecodingException("Could not for signerInfo for " + this.signer.getSubjectDN());
        }
        PkiMessageDecoder.LOGGER.debug("pkiMessage digest algorithm: {}", (Object)signerInfo.getDigestAlgorithmID().getAlgorithm());
        PkiMessageDecoder.LOGGER.debug("pkiMessage encryption algorithm: {}", (Object)signerInfo.getEncryptionAlgOID());
        final Store store = pkiMessage.getCertificates();
        Collection<?> certColl;
        try {
            certColl = store.getMatches((Selector)signerInfo.getSID());
        }
        catch (final StoreException e) {
            throw new MessageDecodingException((Throwable)e);
        }
        if (certColl.size() > 0) {
            final X509CertificateHolder cert = (X509CertificateHolder)certColl.iterator().next();
            PkiMessageDecoder.LOGGER.debug("Verifying pkiMessage using key belonging to [dn={}; serial={}]", (Object)cert.getSubject(), (Object)cert.getSerialNumber());
            try {
                final SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder().build(cert);
                if (!signerInfo.verify(verifier)) {
                    final String msg = "pkiMessage verification failed.";
                    PkiMessageDecoder.LOGGER.warn("pkiMessage verification failed.");
                    throw new MessageDecodingException("pkiMessage verification failed.");
                }
                PkiMessageDecoder.LOGGER.debug("pkiMessage verified.");
            }
            catch (final CMSException e2) {
                throw new MessageDecodingException((Throwable)e2);
            }
            catch (final OperatorCreationException e3) {
                throw new MessageDecodingException((Throwable)e3);
            }
            catch (final CertificateException e4) {
                throw new MessageDecodingException(e4);
            }
        }
        else {
            PkiMessageDecoder.LOGGER.warn("Unable to verify message because the signedData contained no certificates.");
        }
        final Hashtable<ASN1ObjectIdentifier, Attribute> attrTable = signerInfo.getSignedAttributes().toHashtable();
        if (PkiMessageDecoder.LOGGER.isDebugEnabled()) {
            PkiMessageDecoder.LOGGER.debug("pkiMessage has {} signed attributes:", (Object)signerInfo.getSignedAttributes().size());
            for (final Map.Entry<ASN1ObjectIdentifier, Attribute> entry : attrTable.entrySet()) {
                PkiMessageDecoder.LOGGER.debug("  {}: {}", (Object)entry.getKey().getId(), (Object)entry.getValue().getAttrValues());
            }
        }
        final MessageType messageType = this.toMessageType(attrTable.get(this.toOid(ScepObjectIdentifier.MESSAGE_TYPE)));
        final Nonce senderNonce = this.toNonce(attrTable.get(this.toOid(ScepObjectIdentifier.SENDER_NONCE)));
        final TransactionId transId = this.toTransactionId(attrTable.get(this.toOid(ScepObjectIdentifier.TRANS_ID)));
        if (messageType == MessageType.CERT_REP) {
            final PkiStatus pkiStatus = this.toPkiStatus(attrTable.get(this.toOid(ScepObjectIdentifier.PKI_STATUS)));
            final Nonce recipientNonce = this.toNonce(attrTable.get(this.toOid(ScepObjectIdentifier.RECIPIENT_NONCE)));
            if (pkiStatus == PkiStatus.FAILURE) {
                final FailInfo failInfo = this.toFailInfo(attrTable.get(this.toOid(ScepObjectIdentifier.FAIL_INFO)));
                PkiMessageDecoder.LOGGER.debug("Finished decoding pkiMessage");
                return new CertRep(transId, senderNonce, recipientNonce, failInfo);
            }
            if (pkiStatus == PkiStatus.PENDING) {
                PkiMessageDecoder.LOGGER.debug("Finished decoding pkiMessage");
                return new CertRep(transId, senderNonce, recipientNonce);
            }
            final CMSEnvelopedData ed = this.getEnvelopedData(signedContent.getContent());
            final byte[] envelopedContent = this.decoder.decode(ed);
            CMSSignedData messageData;
            try {
                messageData = new CMSSignedData(envelopedContent);
            }
            catch (final CMSException e5) {
                throw new MessageDecodingException((Throwable)e5);
            }
            PkiMessageDecoder.LOGGER.debug("Finished decoding pkiMessage");
            return new CertRep(transId, senderNonce, recipientNonce, messageData);
        }
        else {
            final CMSEnvelopedData ed2 = this.getEnvelopedData(signedContent.getContent());
            final byte[] decoded = this.decoder.decode(ed2);
            if (messageType == MessageType.GET_CERT) {
                final IssuerAndSerialNumber messageData2 = IssuerAndSerialNumber.getInstance((Object)decoded);
                PkiMessageDecoder.LOGGER.debug("Finished decoding pkiMessage");
                return new GetCert(transId, senderNonce, messageData2);
            }
            if (messageType == MessageType.GET_CERT_INITIAL) {
                final IssuerAndSubject messageData3 = new IssuerAndSubject(decoded);
                PkiMessageDecoder.LOGGER.debug("Finished decoding pkiMessage");
                return new GetCertInitial(transId, senderNonce, messageData3);
            }
            if (messageType == MessageType.GET_CRL) {
                final IssuerAndSerialNumber messageData2 = IssuerAndSerialNumber.getInstance((Object)decoded);
                PkiMessageDecoder.LOGGER.debug("Finished decoding pkiMessage");
                return new GetCrl(transId, senderNonce, messageData2);
            }
            PKCS10CertificationRequest messageData4;
            try {
                messageData4 = new PKCS10CertificationRequest(decoded);
            }
            catch (final IOException e6) {
                throw new MessageDecodingException(e6);
            }
            PkiMessageDecoder.LOGGER.debug("Finished decoding pkiMessage");
            return new PkcsReq(transId, senderNonce, messageData4);
        }
    }
    
    private void validate(final CMSSignedData pkiMessage) {
        final SignedData sd = SignedData.getInstance((Object)pkiMessage.toASN1Structure().getContent());
        PkiMessageDecoder.LOGGER.debug("pkiMessage version: {}", (Object)sd.getVersion());
        PkiMessageDecoder.LOGGER.debug("pkiMessage contentInfo contentType: {}", (Object)sd.getEncapContentInfo().getContentType());
    }
    
    private ASN1ObjectIdentifier toOid(final ScepObjectIdentifier oid) {
        return new ASN1ObjectIdentifier(oid.id());
    }
    
    private CMSEnvelopedData getEnvelopedData(final Object bytes) throws MessageDecodingException {
        try {
            return new CMSEnvelopedData((byte[])bytes);
        }
        catch (final CMSException e) {
            throw new MessageDecodingException((Throwable)e);
        }
    }
    
    private Nonce toNonce(final Attribute attr) {
        if (attr == null) {
            return null;
        }
        final DEROctetString octets = (DEROctetString)attr.getAttrValues().getObjectAt(0);
        return new Nonce(octets.getOctets());
    }
    
    private MessageType toMessageType(final Attribute attr) {
        final DERPrintableString string = (DERPrintableString)attr.getAttrValues().getObjectAt(0);
        return MessageType.valueOf(Integer.valueOf(string.getString()));
    }
    
    private TransactionId toTransactionId(final Attribute attr) {
        final DERPrintableString string = (DERPrintableString)attr.getAttrValues().getObjectAt(0);
        return new TransactionId(string.getOctets());
    }
    
    private PkiStatus toPkiStatus(final Attribute attr) {
        final DERPrintableString string = (DERPrintableString)attr.getAttrValues().getObjectAt(0);
        return PkiStatus.valueOf(Integer.valueOf(string.getString()));
    }
    
    private FailInfo toFailInfo(final Attribute attr) {
        final DERPrintableString string = (DERPrintableString)attr.getAttrValues().getObjectAt(0);
        return FailInfo.valueOf(Integer.valueOf(string.getString()));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PkiMessageDecoder.class);
    }
}

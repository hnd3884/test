package org.jscep.message;

import org.slf4j.LoggerFactory;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.SignerInfoGenerator;
import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSAbsentContent;
import java.io.IOException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.jscep.transaction.PkiStatus;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.util.Store;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedData;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import org.slf4j.Logger;

public final class PkiMessageEncoder
{
    private static final String DATA = "1.2.840.113549.1.7.1";
    private static final Logger LOGGER;
    private final PrivateKey signerKey;
    private final X509Certificate signerId;
    private X509Certificate[] chain;
    private final PkcsPkiEnvelopeEncoder enveloper;
    private final String signatureAlgorithm;
    
    public PkiMessageEncoder(final PrivateKey signerKey, final X509Certificate signerId, final PkcsPkiEnvelopeEncoder enveloper) {
        this.chain = null;
        this.signerKey = signerKey;
        this.signerId = signerId;
        this.enveloper = enveloper;
        this.signatureAlgorithm = "SHA1withRSA";
    }
    
    public PkiMessageEncoder(final PrivateKey signerKey, final X509Certificate signerId, final X509Certificate[] chain, final PkcsPkiEnvelopeEncoder enveloper) {
        this.chain = null;
        this.signerKey = signerKey;
        this.signerId = signerId;
        this.chain = chain;
        this.enveloper = enveloper;
        this.signatureAlgorithm = "SHA1withRSA";
    }
    
    public PkiMessageEncoder(final PrivateKey signerKey, final X509Certificate signerId, final PkcsPkiEnvelopeEncoder enveloper, final String signatureAlgorithm) {
        this.chain = null;
        this.signerKey = signerKey;
        this.signerId = signerId;
        this.enveloper = enveloper;
        this.signatureAlgorithm = signatureAlgorithm;
    }
    
    public CMSSignedData encode(final PkiMessage<?> message) throws MessageEncodingException {
        PkiMessageEncoder.LOGGER.debug("Encoding pkiMessage");
        PkiMessageEncoder.LOGGER.debug("Encoding message: {}", (Object)message);
        final CMSTypedData content = this.getContent(message);
        PkiMessageEncoder.LOGGER.debug("Signing pkiMessage using key belonging to [dn={}; serial={}]", (Object)this.signerId.getSubjectDN(), (Object)this.signerId.getSerialNumber());
        try {
            final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            generator.addSignerInfoGenerator(this.getSignerInfo(message));
            generator.addCertificates((Store)this.getCertificates());
            PkiMessageEncoder.LOGGER.debug("Signing {} content", (Object)content);
            final CMSSignedData pkiMessage = generator.generate(content, true);
            PkiMessageEncoder.LOGGER.debug("Finished encoding pkiMessage");
            return pkiMessage;
        }
        catch (final CMSException e) {
            throw new MessageEncodingException((Throwable)e);
        }
        catch (final Exception e2) {
            throw new MessageEncodingException(e2);
        }
    }
    
    private CMSTypedData getContent(final PkiMessage<?> message) throws MessageEncodingException {
        boolean hasMessageData = true;
        if (message instanceof CertRep) {
            final CertRep response = (CertRep)message;
            if (response.getPkiStatus() != PkiStatus.SUCCESS) {
                hasMessageData = false;
            }
        }
        if (hasMessageData) {
            try {
                final CMSEnvelopedData ed = this.encodeMessage(message);
                final CMSTypedData signable = (CMSTypedData)new CMSProcessableByteArray(ed.getEncoded());
                return signable;
            }
            catch (final IOException e) {
                throw new MessageEncodingException(e);
            }
        }
        final CMSTypedData signable = (CMSTypedData)new CMSAbsentContent();
        return signable;
    }
    
    private CMSEnvelopedData encodeMessage(final PkiMessage<?> message) throws MessageEncodingException {
        final Object messageData = message.getMessageData();
        byte[] bytes;
        if (messageData instanceof byte[]) {
            bytes = (byte[])messageData;
        }
        else {
            if (messageData instanceof PKCS10CertificationRequest) {
                try {
                    bytes = ((PKCS10CertificationRequest)messageData).getEncoded();
                    return this.enveloper.encode(bytes);
                }
                catch (final IOException e) {
                    throw new MessageEncodingException(e);
                }
            }
            if (messageData instanceof CMSSignedData) {
                try {
                    bytes = ((CMSSignedData)messageData).getEncoded();
                    return this.enveloper.encode(bytes);
                }
                catch (final IOException e) {
                    throw new MessageEncodingException(e);
                }
            }
            try {
                bytes = ((ASN1Object)messageData).getEncoded();
            }
            catch (final IOException e) {
                throw new MessageEncodingException(e);
            }
        }
        return this.enveloper.encode(bytes);
    }
    
    private JcaCertStore getCertificates() throws MessageEncodingException {
        final Collection<X509Certificate> certColl = new LinkedList<X509Certificate>();
        certColl.add(this.signerId);
        if (this.chain != null) {
            for (final X509Certificate c : this.chain) {
                certColl.add(c);
                PkiMessageEncoder.LOGGER.debug("Add ca certificate {} to signed data", (Object)c.getSubjectX500Principal().toString());
            }
        }
        JcaCertStore certStore;
        try {
            certStore = new JcaCertStore((Collection)certColl);
        }
        catch (final CertificateEncodingException e) {
            throw new MessageEncodingException(e);
        }
        return certStore;
    }
    
    private SignerInfoGenerator getSignerInfo(final PkiMessage<?> message) throws MessageEncodingException {
        final JcaSignerInfoGeneratorBuilder signerInfoBuilder = new JcaSignerInfoGeneratorBuilder(this.getDigestCalculator());
        signerInfoBuilder.setSignedAttributeGenerator(this.getTableGenerator(message));
        SignerInfoGenerator signerInfo;
        try {
            signerInfo = signerInfoBuilder.build(this.getContentSigner(), this.signerId);
        }
        catch (final Exception e) {
            throw new MessageEncodingException(e);
        }
        return signerInfo;
    }
    
    private CMSAttributeTableGenerator getTableGenerator(final PkiMessage<?> message) {
        final AttributeTableFactory attrFactory = new AttributeTableFactory();
        final AttributeTable signedAttrs = attrFactory.fromPkiMessage(message);
        final CMSAttributeTableGenerator atGen = (CMSAttributeTableGenerator)new DefaultSignedAttributeTableGenerator(signedAttrs);
        return atGen;
    }
    
    private DigestCalculatorProvider getDigestCalculator() throws MessageEncodingException {
        try {
            return new JcaDigestCalculatorProviderBuilder().build();
        }
        catch (final OperatorCreationException e) {
            throw new MessageEncodingException((Throwable)e);
        }
    }
    
    private ContentSigner getContentSigner() throws OperatorCreationException {
        return new JcaContentSignerBuilder(this.signatureAlgorithm).build(this.signerKey);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PkiMessageEncoder.class);
    }
}

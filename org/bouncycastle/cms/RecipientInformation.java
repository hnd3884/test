package org.bouncycastle.cms;

import org.bouncycastle.util.io.Streams;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class RecipientInformation
{
    protected RecipientId rid;
    protected AlgorithmIdentifier keyEncAlg;
    protected AlgorithmIdentifier messageAlgorithm;
    protected CMSSecureReadable secureReadable;
    private AuthAttributesProvider additionalData;
    private byte[] resultMac;
    private RecipientOperator operator;
    
    RecipientInformation(final AlgorithmIdentifier keyEncAlg, final AlgorithmIdentifier messageAlgorithm, final CMSSecureReadable secureReadable, final AuthAttributesProvider additionalData) {
        this.keyEncAlg = keyEncAlg;
        this.messageAlgorithm = messageAlgorithm;
        this.secureReadable = secureReadable;
        this.additionalData = additionalData;
    }
    
    public RecipientId getRID() {
        return this.rid;
    }
    
    private byte[] encodeObj(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            return asn1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }
    
    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncAlg;
    }
    
    public String getKeyEncryptionAlgOID() {
        return this.keyEncAlg.getAlgorithm().getId();
    }
    
    public byte[] getKeyEncryptionAlgParams() {
        try {
            return this.encodeObj(this.keyEncAlg.getParameters());
        }
        catch (final Exception ex) {
            throw new RuntimeException("exception getting encryption parameters " + ex);
        }
    }
    
    public byte[] getContentDigest() {
        if (this.secureReadable instanceof CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable) {
            return ((CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable)this.secureReadable).getDigest();
        }
        return null;
    }
    
    public byte[] getMac() {
        if (this.resultMac == null && this.operator.isMacBased()) {
            if (this.additionalData != null) {
                try {
                    Streams.drain(this.operator.getInputStream(new ByteArrayInputStream(this.additionalData.getAuthAttributes().getEncoded("DER"))));
                }
                catch (final IOException ex) {
                    throw new IllegalStateException("unable to drain input: " + ex.getMessage());
                }
            }
            this.resultMac = this.operator.getMac();
        }
        return this.resultMac;
    }
    
    public byte[] getContent(final Recipient recipient) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(this.getContentStream(recipient).getContentStream());
        }
        catch (final IOException ex) {
            throw new CMSException("unable to parse internal stream: " + ex.getMessage(), ex);
        }
    }
    
    public CMSTypedStream getContentStream(final Recipient recipient) throws CMSException, IOException {
        this.operator = this.getRecipientOperator(recipient);
        if (this.additionalData != null) {
            return new CMSTypedStream(this.secureReadable.getInputStream());
        }
        return new CMSTypedStream(this.operator.getInputStream(this.secureReadable.getInputStream()));
    }
    
    protected abstract RecipientOperator getRecipientOperator(final Recipient p0) throws CMSException, IOException;
}

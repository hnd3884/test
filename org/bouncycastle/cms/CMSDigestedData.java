package org.bouncycastle.cms;

import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.operator.DigestCalculatorProvider;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.DigestedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.util.Encodable;

public class CMSDigestedData implements Encodable
{
    private ContentInfo contentInfo;
    private DigestedData digestedData;
    
    public CMSDigestedData(final byte[] array) throws CMSException {
        this(CMSUtils.readContentInfo(array));
    }
    
    public CMSDigestedData(final InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }
    
    public CMSDigestedData(final ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            this.digestedData = DigestedData.getInstance((Object)contentInfo.getContent());
        }
        catch (final ClassCastException ex) {
            throw new CMSException("Malformed content.", ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CMSException("Malformed content.", ex2);
        }
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.contentInfo.getContentType();
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestedData.getDigestAlgorithm();
    }
    
    public CMSProcessable getDigestedContent() throws CMSException {
        final ContentInfo encapContentInfo = this.digestedData.getEncapContentInfo();
        try {
            return new CMSProcessableByteArray(encapContentInfo.getContentType(), ((ASN1OctetString)encapContentInfo.getContent()).getOctets());
        }
        catch (final Exception ex) {
            throw new CMSException("exception reading digested stream.", ex);
        }
    }
    
    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
    
    public boolean verify(final DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        try {
            final ContentInfo encapContentInfo = this.digestedData.getEncapContentInfo();
            final DigestCalculator value = digestCalculatorProvider.get(this.digestedData.getDigestAlgorithm());
            value.getOutputStream().write(((ASN1OctetString)encapContentInfo.getContent()).getOctets());
            return Arrays.areEqual(this.digestedData.getDigest(), value.getDigest());
        }
        catch (final OperatorCreationException ex) {
            throw new CMSException("unable to create digest calculator: " + ex.getMessage(), ex);
        }
        catch (final IOException ex2) {
            throw new CMSException("unable process content: " + ex2.getMessage(), ex2);
        }
    }
}

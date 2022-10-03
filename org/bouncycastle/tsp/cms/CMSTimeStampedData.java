package org.bouncycastle.tsp.cms;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.asn1.cms.AttributeTable;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.DERIA5String;
import java.net.URI;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.TimeStampedData;

public class CMSTimeStampedData
{
    private TimeStampedData timeStampedData;
    private ContentInfo contentInfo;
    private TimeStampDataUtil util;
    
    public CMSTimeStampedData(final ContentInfo contentInfo) {
        this.initialize(contentInfo);
    }
    
    public CMSTimeStampedData(final InputStream inputStream) throws IOException {
        try {
            this.initialize(ContentInfo.getInstance((Object)new ASN1InputStream(inputStream).readObject()));
        }
        catch (final ClassCastException ex) {
            throw new IOException("Malformed content: " + ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new IOException("Malformed content: " + ex2);
        }
    }
    
    public CMSTimeStampedData(final byte[] array) throws IOException {
        this(new ByteArrayInputStream(array));
    }
    
    private void initialize(final ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
        if (CMSObjectIdentifiers.timestampedData.equals((Object)contentInfo.getContentType())) {
            this.timeStampedData = TimeStampedData.getInstance((Object)contentInfo.getContent());
            this.util = new TimeStampDataUtil(this.timeStampedData);
            return;
        }
        throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
    }
    
    public byte[] calculateNextHash(final DigestCalculator digestCalculator) throws CMSException {
        return this.util.calculateNextHash(digestCalculator);
    }
    
    public CMSTimeStampedData addTimeStamp(final TimeStampToken timeStampToken) throws CMSException {
        final TimeStampAndCRL[] timeStamps = this.util.getTimeStamps();
        final TimeStampAndCRL[] array = new TimeStampAndCRL[timeStamps.length + 1];
        System.arraycopy(timeStamps, 0, array, 0, timeStamps.length);
        array[timeStamps.length] = new TimeStampAndCRL(timeStampToken.toCMSSignedData().toASN1Structure());
        return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, (ASN1Encodable)new TimeStampedData(this.timeStampedData.getDataUri(), this.timeStampedData.getMetaData(), this.timeStampedData.getContent(), new Evidence(new TimeStampTokenEvidence(array)))));
    }
    
    public byte[] getContent() {
        if (this.timeStampedData.getContent() != null) {
            return this.timeStampedData.getContent().getOctets();
        }
        return null;
    }
    
    public URI getDataUri() throws URISyntaxException {
        final DERIA5String dataUri = this.timeStampedData.getDataUri();
        if (dataUri != null) {
            return new URI(dataUri.getString());
        }
        return null;
    }
    
    public String getFileName() {
        return this.util.getFileName();
    }
    
    public String getMediaType() {
        return this.util.getMediaType();
    }
    
    public AttributeTable getOtherMetaData() {
        return this.util.getOtherMetaData();
    }
    
    public TimeStampToken[] getTimeStampTokens() throws CMSException {
        return this.util.getTimeStampTokens();
    }
    
    public void initialiseMessageImprintDigestCalculator(final DigestCalculator digestCalculator) throws CMSException {
        this.util.initialiseMessageImprintDigestCalculator(digestCalculator);
    }
    
    public DigestCalculator getMessageImprintDigestCalculator(final DigestCalculatorProvider digestCalculatorProvider) throws OperatorCreationException {
        return this.util.getMessageImprintDigestCalculator(digestCalculatorProvider);
    }
    
    public void validate(final DigestCalculatorProvider digestCalculatorProvider, final byte[] array) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(digestCalculatorProvider, array);
    }
    
    public void validate(final DigestCalculatorProvider digestCalculatorProvider, final byte[] array, final TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        this.util.validate(digestCalculatorProvider, array, timeStampToken);
    }
    
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}

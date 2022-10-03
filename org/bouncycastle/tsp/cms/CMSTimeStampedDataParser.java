package org.bouncycastle.tsp.cms;

import org.bouncycastle.util.io.Streams;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.asn1.cms.AttributeTable;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.DERIA5String;
import java.net.URI;
import org.bouncycastle.operator.DigestCalculator;
import java.io.IOException;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import java.io.ByteArrayInputStream;
import org.bouncycastle.cms.CMSException;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.cms.CMSContentInfoParser;

public class CMSTimeStampedDataParser extends CMSContentInfoParser
{
    private TimeStampedDataParser timeStampedData;
    private TimeStampDataUtil util;
    
    public CMSTimeStampedDataParser(final InputStream inputStream) throws CMSException {
        super(inputStream);
        this.initialize(this._contentInfo);
    }
    
    public CMSTimeStampedDataParser(final byte[] array) throws CMSException {
        this(new ByteArrayInputStream(array));
    }
    
    private void initialize(final ContentInfoParser contentInfoParser) throws CMSException {
        try {
            if (!CMSObjectIdentifiers.timestampedData.equals((Object)contentInfoParser.getContentType())) {
                throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
            }
            this.timeStampedData = TimeStampedDataParser.getInstance((Object)contentInfoParser.getContent(16));
        }
        catch (final IOException ex) {
            throw new CMSException("parsing exception: " + ex.getMessage(), ex);
        }
    }
    
    public byte[] calculateNextHash(final DigestCalculator digestCalculator) throws CMSException {
        return this.util.calculateNextHash(digestCalculator);
    }
    
    public InputStream getContent() {
        if (this.timeStampedData.getContent() != null) {
            return this.timeStampedData.getContent().getOctetStream();
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
    
    public void initialiseMessageImprintDigestCalculator(final DigestCalculator digestCalculator) throws CMSException {
        this.util.initialiseMessageImprintDigestCalculator(digestCalculator);
    }
    
    public DigestCalculator getMessageImprintDigestCalculator(final DigestCalculatorProvider digestCalculatorProvider) throws OperatorCreationException {
        try {
            this.parseTimeStamps();
        }
        catch (final CMSException ex) {
            throw new OperatorCreationException("unable to extract algorithm ID: " + ex.getMessage(), ex);
        }
        return this.util.getMessageImprintDigestCalculator(digestCalculatorProvider);
    }
    
    public TimeStampToken[] getTimeStampTokens() throws CMSException {
        this.parseTimeStamps();
        return this.util.getTimeStampTokens();
    }
    
    public void validate(final DigestCalculatorProvider digestCalculatorProvider, final byte[] array) throws ImprintDigestInvalidException, CMSException {
        this.parseTimeStamps();
        this.util.validate(digestCalculatorProvider, array);
    }
    
    public void validate(final DigestCalculatorProvider digestCalculatorProvider, final byte[] array, final TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        this.parseTimeStamps();
        this.util.validate(digestCalculatorProvider, array, timeStampToken);
    }
    
    private void parseTimeStamps() throws CMSException {
        try {
            if (this.util == null) {
                final InputStream content = this.getContent();
                if (content != null) {
                    Streams.drain(content);
                }
                this.util = new TimeStampDataUtil(this.timeStampedData);
            }
        }
        catch (final IOException ex) {
            throw new CMSException("unable to parse evidence block: " + ex.getMessage(), ex);
        }
    }
}

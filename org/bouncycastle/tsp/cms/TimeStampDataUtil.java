package org.bouncycastle.tsp.cms;

import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.util.Arrays;
import java.io.OutputStream;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TimeStampToken;
import java.io.IOException;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;

class TimeStampDataUtil
{
    private final TimeStampAndCRL[] timeStamps;
    private final MetaDataUtil metaDataUtil;
    
    TimeStampDataUtil(final TimeStampedData timeStampedData) {
        this.metaDataUtil = new MetaDataUtil(timeStampedData.getMetaData());
        this.timeStamps = timeStampedData.getTemporalEvidence().getTstEvidence().toTimeStampAndCRLArray();
    }
    
    TimeStampDataUtil(final TimeStampedDataParser timeStampedDataParser) throws IOException {
        this.metaDataUtil = new MetaDataUtil(timeStampedDataParser.getMetaData());
        this.timeStamps = timeStampedDataParser.getTemporalEvidence().getTstEvidence().toTimeStampAndCRLArray();
    }
    
    TimeStampToken getTimeStampToken(final TimeStampAndCRL timeStampAndCRL) throws CMSException {
        final ContentInfo timeStampToken = timeStampAndCRL.getTimeStampToken();
        try {
            return new TimeStampToken(timeStampToken);
        }
        catch (final IOException ex) {
            throw new CMSException("unable to parse token data: " + ex.getMessage(), ex);
        }
        catch (final TSPException ex2) {
            if (ex2.getCause() instanceof CMSException) {
                throw (CMSException)ex2.getCause();
            }
            throw new CMSException("token data invalid: " + ex2.getMessage(), ex2);
        }
        catch (final IllegalArgumentException ex3) {
            throw new CMSException("token data invalid: " + ex3.getMessage(), ex3);
        }
    }
    
    void initialiseMessageImprintDigestCalculator(final DigestCalculator digestCalculator) throws CMSException {
        this.metaDataUtil.initialiseMessageImprintDigestCalculator(digestCalculator);
    }
    
    DigestCalculator getMessageImprintDigestCalculator(final DigestCalculatorProvider digestCalculatorProvider) throws OperatorCreationException {
        try {
            final DigestCalculator value = digestCalculatorProvider.get(new AlgorithmIdentifier(this.getTimeStampToken(this.timeStamps[0]).getTimeStampInfo().getMessageImprintAlgOID()));
            this.initialiseMessageImprintDigestCalculator(value);
            return value;
        }
        catch (final CMSException ex) {
            throw new OperatorCreationException("unable to extract algorithm ID: " + ex.getMessage(), ex);
        }
    }
    
    TimeStampToken[] getTimeStampTokens() throws CMSException {
        final TimeStampToken[] array = new TimeStampToken[this.timeStamps.length];
        for (int i = 0; i < this.timeStamps.length; ++i) {
            array[i] = this.getTimeStampToken(this.timeStamps[i]);
        }
        return array;
    }
    
    TimeStampAndCRL[] getTimeStamps() {
        return this.timeStamps;
    }
    
    byte[] calculateNextHash(final DigestCalculator digestCalculator) throws CMSException {
        final TimeStampAndCRL timeStampAndCRL = this.timeStamps[this.timeStamps.length - 1];
        final OutputStream outputStream = digestCalculator.getOutputStream();
        try {
            outputStream.write(timeStampAndCRL.getEncoded("DER"));
            outputStream.close();
            return digestCalculator.getDigest();
        }
        catch (final IOException ex) {
            throw new CMSException("exception calculating hash: " + ex.getMessage(), ex);
        }
    }
    
    void validate(final DigestCalculatorProvider digestCalculatorProvider, final byte[] array) throws ImprintDigestInvalidException, CMSException {
        byte[] digest = array;
        for (int i = 0; i < this.timeStamps.length; ++i) {
            try {
                final TimeStampToken timeStampToken = this.getTimeStampToken(this.timeStamps[i]);
                if (i > 0) {
                    final DigestCalculator value = digestCalculatorProvider.get(timeStampToken.getTimeStampInfo().getHashAlgorithm());
                    value.getOutputStream().write(this.timeStamps[i - 1].getEncoded("DER"));
                    digest = value.getDigest();
                }
                this.compareDigest(timeStampToken, digest);
            }
            catch (final IOException ex) {
                throw new CMSException("exception calculating hash: " + ex.getMessage(), ex);
            }
            catch (final OperatorCreationException ex2) {
                throw new CMSException("cannot create digest: " + ex2.getMessage(), ex2);
            }
        }
    }
    
    void validate(final DigestCalculatorProvider digestCalculatorProvider, final byte[] array, final TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        byte[] digest = array;
        byte[] encoded;
        try {
            encoded = timeStampToken.getEncoded();
        }
        catch (final IOException ex) {
            throw new CMSException("exception encoding timeStampToken: " + ex.getMessage(), ex);
        }
        for (int i = 0; i < this.timeStamps.length; ++i) {
            try {
                final TimeStampToken timeStampToken2 = this.getTimeStampToken(this.timeStamps[i]);
                if (i > 0) {
                    final DigestCalculator value = digestCalculatorProvider.get(timeStampToken2.getTimeStampInfo().getHashAlgorithm());
                    value.getOutputStream().write(this.timeStamps[i - 1].getEncoded("DER"));
                    digest = value.getDigest();
                }
                this.compareDigest(timeStampToken2, digest);
                if (Arrays.areEqual(timeStampToken2.getEncoded(), encoded)) {
                    return;
                }
            }
            catch (final IOException ex2) {
                throw new CMSException("exception calculating hash: " + ex2.getMessage(), ex2);
            }
            catch (final OperatorCreationException ex3) {
                throw new CMSException("cannot create digest: " + ex3.getMessage(), ex3);
            }
        }
        throw new ImprintDigestInvalidException("passed in token not associated with timestamps present", timeStampToken);
    }
    
    private void compareDigest(final TimeStampToken timeStampToken, final byte[] array) throws ImprintDigestInvalidException {
        if (!Arrays.areEqual(array, timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
            throw new ImprintDigestInvalidException("hash calculated is different from MessageImprintDigest found in TimeStampToken", timeStampToken);
        }
    }
    
    String getFileName() {
        return this.metaDataUtil.getFileName();
    }
    
    String getMediaType() {
        return this.metaDataUtil.getMediaType();
    }
    
    AttributeTable getOtherMetaData() {
        return new AttributeTable(this.metaDataUtil.getOtherMetaData());
    }
}

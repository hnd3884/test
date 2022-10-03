package org.bouncycastle.tsp.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.BEROctetString;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.io.Streams;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.cms.CMSException;
import java.io.InputStream;
import org.bouncycastle.tsp.TimeStampToken;

public class CMSTimeStampedDataGenerator extends CMSTimeStampedGenerator
{
    public CMSTimeStampedData generate(final TimeStampToken timeStampToken) throws CMSException {
        return this.generate(timeStampToken, (InputStream)null);
    }
    
    public CMSTimeStampedData generate(final TimeStampToken timeStampToken, final byte[] array) throws CMSException {
        return this.generate(timeStampToken, new ByteArrayInputStream(array));
    }
    
    public CMSTimeStampedData generate(final TimeStampToken timeStampToken, final InputStream inputStream) throws CMSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (inputStream != null) {
            try {
                Streams.pipeAll(inputStream, (OutputStream)byteArrayOutputStream);
            }
            catch (final IOException ex) {
                throw new CMSException("exception encapsulating content: " + ex.getMessage(), ex);
            }
        }
        Object o = null;
        if (byteArrayOutputStream.size() != 0) {
            o = new BEROctetString(byteArrayOutputStream.toByteArray());
        }
        final TimeStampAndCRL timeStampAndCRL = new TimeStampAndCRL(timeStampToken.toCMSSignedData().toASN1Structure());
        DERIA5String deria5String = null;
        if (this.dataUri != null) {
            deria5String = new DERIA5String(this.dataUri.toString());
        }
        return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, (ASN1Encodable)new TimeStampedData(deria5String, this.metaData, (ASN1OctetString)o, new Evidence(new TimeStampTokenEvidence(timeStampAndCRL)))));
    }
}

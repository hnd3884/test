package org.bouncycastle.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.DLSequence;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.tsp.TimeStampResp;

public class TimeStampResponse
{
    TimeStampResp resp;
    TimeStampToken timeStampToken;
    
    public TimeStampResponse(final TimeStampResp resp) throws TSPException, IOException {
        this.resp = resp;
        if (resp.getTimeStampToken() != null) {
            this.timeStampToken = new TimeStampToken(resp.getTimeStampToken());
        }
    }
    
    public TimeStampResponse(final byte[] array) throws TSPException, IOException {
        this(new ByteArrayInputStream(array));
    }
    
    public TimeStampResponse(final InputStream inputStream) throws TSPException, IOException {
        this(readTimeStampResp(inputStream));
    }
    
    TimeStampResponse(final DLSequence dlSequence) throws TSPException, IOException {
        try {
            this.resp = TimeStampResp.getInstance((Object)dlSequence);
            this.timeStampToken = new TimeStampToken(ContentInfo.getInstance((Object)dlSequence.getObjectAt(1)));
        }
        catch (final IllegalArgumentException ex) {
            throw new TSPException("malformed timestamp response: " + ex, ex);
        }
        catch (final ClassCastException ex2) {
            throw new TSPException("malformed timestamp response: " + ex2, ex2);
        }
    }
    
    private static TimeStampResp readTimeStampResp(final InputStream inputStream) throws IOException, TSPException {
        try {
            return TimeStampResp.getInstance((Object)new ASN1InputStream(inputStream).readObject());
        }
        catch (final IllegalArgumentException ex) {
            throw new TSPException("malformed timestamp response: " + ex, ex);
        }
        catch (final ClassCastException ex2) {
            throw new TSPException("malformed timestamp response: " + ex2, ex2);
        }
    }
    
    public int getStatus() {
        return this.resp.getStatus().getStatus().intValue();
    }
    
    public String getStatusString() {
        if (this.resp.getStatus().getStatusString() != null) {
            final StringBuffer sb = new StringBuffer();
            final PKIFreeText statusString = this.resp.getStatus().getStatusString();
            for (int i = 0; i != statusString.size(); ++i) {
                sb.append(statusString.getStringAt(i).getString());
            }
            return sb.toString();
        }
        return null;
    }
    
    public PKIFailureInfo getFailInfo() {
        if (this.resp.getStatus().getFailInfo() != null) {
            return new PKIFailureInfo(this.resp.getStatus().getFailInfo());
        }
        return null;
    }
    
    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }
    
    public void validate(final TimeStampRequest timeStampRequest) throws TSPException {
        final TimeStampToken timeStampToken = this.getTimeStampToken();
        if (timeStampToken != null) {
            final TimeStampTokenInfo timeStampInfo = timeStampToken.getTimeStampInfo();
            if (timeStampRequest.getNonce() != null && !timeStampRequest.getNonce().equals(timeStampInfo.getNonce())) {
                throw new TSPValidationException("response contains wrong nonce value.");
            }
            if (this.getStatus() != 0 && this.getStatus() != 1) {
                throw new TSPValidationException("time stamp token found in failed request.");
            }
            if (!Arrays.constantTimeAreEqual(timeStampRequest.getMessageImprintDigest(), timeStampInfo.getMessageImprintDigest())) {
                throw new TSPValidationException("response for different message imprint digest.");
            }
            if (!timeStampInfo.getMessageImprintAlgOID().equals((Object)timeStampRequest.getMessageImprintAlgOID())) {
                throw new TSPValidationException("response for different message imprint algorithm.");
            }
            final Attribute value = timeStampToken.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
            final Attribute value2 = timeStampToken.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
            if (value == null && value2 == null) {
                throw new TSPValidationException("no signing certificate attribute present.");
            }
            if (value == null || value2 != null) {}
            if (timeStampRequest.getReqPolicy() != null && !timeStampRequest.getReqPolicy().equals((Object)timeStampInfo.getPolicy())) {
                throw new TSPValidationException("TSA policy wrong for request.");
            }
        }
        else if (this.getStatus() == 0 || this.getStatus() == 1) {
            throw new TSPValidationException("no time stamp token found and one expected.");
        }
    }
    
    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }
    
    public byte[] getEncoded(final String s) throws IOException {
        if ("DL".equals(s)) {
            return new DLSequence(new ASN1Encodable[] { (ASN1Encodable)this.resp.getStatus(), (ASN1Encodable)this.timeStampToken.toCMSSignedData().toASN1Structure() }).getEncoded(s);
        }
        return this.resp.getEncoded(s);
    }
}

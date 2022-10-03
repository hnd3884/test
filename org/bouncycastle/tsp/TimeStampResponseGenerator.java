package org.bouncycastle.tsp;

import org.bouncycastle.asn1.DERBitString;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.HashSet;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.asn1.cms.ContentInfo;
import java.io.IOException;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.x509.Extensions;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERUTF8String;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;

public class TimeStampResponseGenerator
{
    int status;
    ASN1EncodableVector statusStrings;
    int failInfo;
    private TimeStampTokenGenerator tokenGenerator;
    private Set acceptedAlgorithms;
    private Set acceptedPolicies;
    private Set acceptedExtensions;
    
    public TimeStampResponseGenerator(final TimeStampTokenGenerator timeStampTokenGenerator, final Set set) {
        this(timeStampTokenGenerator, set, null, null);
    }
    
    public TimeStampResponseGenerator(final TimeStampTokenGenerator timeStampTokenGenerator, final Set set, final Set set2) {
        this(timeStampTokenGenerator, set, set2, null);
    }
    
    public TimeStampResponseGenerator(final TimeStampTokenGenerator tokenGenerator, final Set set, final Set set2, final Set set3) {
        this.tokenGenerator = tokenGenerator;
        this.acceptedAlgorithms = this.convert(set);
        this.acceptedPolicies = this.convert(set2);
        this.acceptedExtensions = this.convert(set3);
        this.statusStrings = new ASN1EncodableVector();
    }
    
    private void addStatusString(final String s) {
        this.statusStrings.add((ASN1Encodable)new DERUTF8String(s));
    }
    
    private void setFailInfoField(final int n) {
        this.failInfo |= n;
    }
    
    private PKIStatusInfo getPKIStatusInfo() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)new ASN1Integer((long)this.status));
        if (this.statusStrings.size() > 0) {
            asn1EncodableVector.add((ASN1Encodable)PKIFreeText.getInstance((Object)new DERSequence(this.statusStrings)));
        }
        if (this.failInfo != 0) {
            asn1EncodableVector.add((ASN1Encodable)new FailInfo(this.failInfo));
        }
        return PKIStatusInfo.getInstance((Object)new DERSequence(asn1EncodableVector));
    }
    
    public TimeStampResponse generate(final TimeStampRequest timeStampRequest, final BigInteger bigInteger, final Date date) throws TSPException {
        try {
            return this.generateGrantedResponse(timeStampRequest, bigInteger, date, "Operation Okay");
        }
        catch (final Exception ex) {
            return this.generateRejectedResponse(ex);
        }
    }
    
    public TimeStampResponse generateGrantedResponse(final TimeStampRequest timeStampRequest, final BigInteger bigInteger, final Date date) throws TSPException {
        return this.generateGrantedResponse(timeStampRequest, bigInteger, date, null);
    }
    
    public TimeStampResponse generateGrantedResponse(final TimeStampRequest timeStampRequest, final BigInteger bigInteger, final Date date, final String s) throws TSPException {
        return this.generateGrantedResponse(timeStampRequest, bigInteger, date, s, null);
    }
    
    public TimeStampResponse generateGrantedResponse(final TimeStampRequest timeStampRequest, final BigInteger bigInteger, final Date date, final String s, final Extensions extensions) throws TSPException {
        if (date == null) {
            throw new TSPValidationException("The time source is not available.", 512);
        }
        timeStampRequest.validate(this.acceptedAlgorithms, this.acceptedPolicies, this.acceptedExtensions);
        this.status = 0;
        this.statusStrings = new ASN1EncodableVector();
        if (s != null) {
            this.addStatusString(s);
        }
        final PKIStatusInfo pkiStatusInfo = this.getPKIStatusInfo();
        ContentInfo asn1Structure;
        try {
            asn1Structure = this.tokenGenerator.generate(timeStampRequest, bigInteger, date, extensions).toCMSSignedData().toASN1Structure();
        }
        catch (final TSPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new TSPException("Timestamp token received cannot be converted to ContentInfo", ex2);
        }
        try {
            return new TimeStampResponse(new DLSequence(new ASN1Encodable[] { (ASN1Encodable)pkiStatusInfo.toASN1Primitive(), (ASN1Encodable)asn1Structure.toASN1Primitive() }));
        }
        catch (final IOException ex3) {
            throw new TSPException("created badly formatted response!");
        }
    }
    
    public TimeStampResponse generateRejectedResponse(final Exception ex) throws TSPException {
        if (ex instanceof TSPValidationException) {
            return this.generateFailResponse(2, ((TSPValidationException)ex).getFailureCode(), ex.getMessage());
        }
        return this.generateFailResponse(2, 1073741824, ex.getMessage());
    }
    
    public TimeStampResponse generateFailResponse(final int status, final int failInfoField, final String s) throws TSPException {
        this.status = status;
        this.statusStrings = new ASN1EncodableVector();
        this.setFailInfoField(failInfoField);
        if (s != null) {
            this.addStatusString(s);
        }
        final TimeStampResp timeStampResp = new TimeStampResp(this.getPKIStatusInfo(), (ContentInfo)null);
        try {
            return new TimeStampResponse(timeStampResp);
        }
        catch (final IOException ex) {
            throw new TSPException("created badly formatted response!");
        }
    }
    
    private Set convert(final Set set) {
        if (set == null) {
            return set;
        }
        final HashSet set2 = new HashSet(set.size());
        for (final Object next : set) {
            if (next instanceof String) {
                set2.add(new ASN1ObjectIdentifier((String)next));
            }
            else {
                set2.add(next);
            }
        }
        return set2;
    }
    
    class FailInfo extends DERBitString
    {
        FailInfo(final int n) {
            super(getBytes(n), getPadBits(n));
        }
    }
}

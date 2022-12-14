package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Integer;

public class V2TBSCertListGenerator
{
    private ASN1Integer version;
    private AlgorithmIdentifier signature;
    private X500Name issuer;
    private Time thisUpdate;
    private Time nextUpdate;
    private Extensions extensions;
    private ASN1EncodableVector crlentries;
    private static final ASN1Sequence[] reasons;
    
    public V2TBSCertListGenerator() {
        this.version = new ASN1Integer(1L);
        this.nextUpdate = null;
        this.extensions = null;
        this.crlentries = new ASN1EncodableVector();
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.signature = signature;
    }
    
    @Deprecated
    public void setIssuer(final X509Name x509Name) {
        this.issuer = X500Name.getInstance(x509Name.toASN1Primitive());
    }
    
    public void setIssuer(final X500Name issuer) {
        this.issuer = issuer;
    }
    
    public void setThisUpdate(final ASN1UTCTime asn1UTCTime) {
        this.thisUpdate = new Time(asn1UTCTime);
    }
    
    public void setNextUpdate(final ASN1UTCTime asn1UTCTime) {
        this.nextUpdate = new Time(asn1UTCTime);
    }
    
    public void setThisUpdate(final Time thisUpdate) {
        this.thisUpdate = thisUpdate;
    }
    
    public void setNextUpdate(final Time nextUpdate) {
        this.nextUpdate = nextUpdate;
    }
    
    public void addCRLEntry(final ASN1Sequence asn1Sequence) {
        this.crlentries.add(asn1Sequence);
    }
    
    public void addCRLEntry(final ASN1Integer asn1Integer, final ASN1UTCTime asn1UTCTime, final int n) {
        this.addCRLEntry(asn1Integer, new Time(asn1UTCTime), n);
    }
    
    public void addCRLEntry(final ASN1Integer asn1Integer, final Time time, final int n) {
        this.addCRLEntry(asn1Integer, time, n, null);
    }
    
    public void addCRLEntry(final ASN1Integer asn1Integer, final Time time, final int n, final ASN1GeneralizedTime asn1GeneralizedTime) {
        if (n != 0) {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            if (n < V2TBSCertListGenerator.reasons.length) {
                if (n < 0) {
                    throw new IllegalArgumentException("invalid reason value: " + n);
                }
                asn1EncodableVector.add(V2TBSCertListGenerator.reasons[n]);
            }
            else {
                asn1EncodableVector.add(createReasonExtension(n));
            }
            if (asn1GeneralizedTime != null) {
                asn1EncodableVector.add(createInvalidityDateExtension(asn1GeneralizedTime));
            }
            this.internalAddCRLEntry(asn1Integer, time, new DERSequence(asn1EncodableVector));
        }
        else if (asn1GeneralizedTime != null) {
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            asn1EncodableVector2.add(createInvalidityDateExtension(asn1GeneralizedTime));
            this.internalAddCRLEntry(asn1Integer, time, new DERSequence(asn1EncodableVector2));
        }
        else {
            this.addCRLEntry(asn1Integer, time, null);
        }
    }
    
    private void internalAddCRLEntry(final ASN1Integer asn1Integer, final Time time, final ASN1Sequence asn1Sequence) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(asn1Integer);
        asn1EncodableVector.add(time);
        if (asn1Sequence != null) {
            asn1EncodableVector.add(asn1Sequence);
        }
        this.addCRLEntry(new DERSequence(asn1EncodableVector));
    }
    
    public void addCRLEntry(final ASN1Integer asn1Integer, final Time time, final Extensions extensions) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(asn1Integer);
        asn1EncodableVector.add(time);
        if (extensions != null) {
            asn1EncodableVector.add(extensions);
        }
        this.addCRLEntry(new DERSequence(asn1EncodableVector));
    }
    
    public void setExtensions(final X509Extensions x509Extensions) {
        this.setExtensions(Extensions.getInstance(x509Extensions));
    }
    
    public void setExtensions(final Extensions extensions) {
        this.extensions = extensions;
    }
    
    public TBSCertList generateTBSCertList() {
        if (this.signature == null || this.issuer == null || this.thisUpdate == null) {
            throw new IllegalStateException("Not all mandatory fields set in V2 TBSCertList generator.");
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.signature);
        asn1EncodableVector.add(this.issuer);
        asn1EncodableVector.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            asn1EncodableVector.add(this.nextUpdate);
        }
        if (this.crlentries.size() != 0) {
            asn1EncodableVector.add(new DERSequence(this.crlentries));
        }
        if (this.extensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(0, this.extensions));
        }
        return new TBSCertList(new DERSequence(asn1EncodableVector));
    }
    
    private static ASN1Sequence createReasonExtension(final int n) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final CRLReason lookup = CRLReason.lookup(n);
        try {
            asn1EncodableVector.add(Extension.reasonCode);
            asn1EncodableVector.add(new DEROctetString(lookup.getEncoded()));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("error encoding reason: " + ex);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    private static ASN1Sequence createInvalidityDateExtension(final ASN1GeneralizedTime asn1GeneralizedTime) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        try {
            asn1EncodableVector.add(Extension.invalidityDate);
            asn1EncodableVector.add(new DEROctetString(asn1GeneralizedTime.getEncoded()));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("error encoding reason: " + ex);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        (reasons = new ASN1Sequence[11])[0] = createReasonExtension(0);
        V2TBSCertListGenerator.reasons[1] = createReasonExtension(1);
        V2TBSCertListGenerator.reasons[2] = createReasonExtension(2);
        V2TBSCertListGenerator.reasons[3] = createReasonExtension(3);
        V2TBSCertListGenerator.reasons[4] = createReasonExtension(4);
        V2TBSCertListGenerator.reasons[5] = createReasonExtension(5);
        V2TBSCertListGenerator.reasons[6] = createReasonExtension(6);
        V2TBSCertListGenerator.reasons[7] = createReasonExtension(7);
        V2TBSCertListGenerator.reasons[8] = createReasonExtension(8);
        V2TBSCertListGenerator.reasons[9] = createReasonExtension(9);
        V2TBSCertListGenerator.reasons[10] = createReasonExtension(10);
    }
}

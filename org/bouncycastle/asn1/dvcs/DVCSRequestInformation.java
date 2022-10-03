package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.GeneralNames;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class DVCSRequestInformation extends ASN1Object
{
    private int version;
    private ServiceType service;
    private BigInteger nonce;
    private DVCSTime requestTime;
    private GeneralNames requester;
    private PolicyInformation requestPolicy;
    private GeneralNames dvcs;
    private GeneralNames dataLocations;
    private Extensions extensions;
    private static final int DEFAULT_VERSION = 1;
    private static final int TAG_REQUESTER = 0;
    private static final int TAG_REQUEST_POLICY = 1;
    private static final int TAG_DVCS = 2;
    private static final int TAG_DATA_LOCATIONS = 3;
    private static final int TAG_EXTENSIONS = 4;
    
    private DVCSRequestInformation(final ASN1Sequence asn1Sequence) {
        this.version = 1;
        int i = 0;
        if (asn1Sequence.getObjectAt(0) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(i++)).getValue().intValue();
        }
        else {
            this.version = 1;
        }
        this.service = ServiceType.getInstance(asn1Sequence.getObjectAt(i++));
        while (i < asn1Sequence.size()) {
            final ASN1Encodable object = asn1Sequence.getObjectAt(i);
            if (object instanceof ASN1Integer) {
                this.nonce = ASN1Integer.getInstance(object).getValue();
            }
            else if (object instanceof ASN1GeneralizedTime) {
                this.requestTime = DVCSTime.getInstance(object);
            }
            else if (object instanceof ASN1TaggedObject) {
                final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(object);
                final int tagNo = instance.getTagNo();
                switch (tagNo) {
                    case 0: {
                        this.requester = GeneralNames.getInstance(instance, false);
                        break;
                    }
                    case 1: {
                        this.requestPolicy = PolicyInformation.getInstance(ASN1Sequence.getInstance(instance, false));
                        break;
                    }
                    case 2: {
                        this.dvcs = GeneralNames.getInstance(instance, false);
                        break;
                    }
                    case 3: {
                        this.dataLocations = GeneralNames.getInstance(instance, false);
                        break;
                    }
                    case 4: {
                        this.extensions = Extensions.getInstance(instance, false);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("unknown tag number encountered: " + tagNo);
                    }
                }
            }
            else {
                this.requestTime = DVCSTime.getInstance(object);
            }
            ++i;
        }
    }
    
    public static DVCSRequestInformation getInstance(final Object o) {
        if (o instanceof DVCSRequestInformation) {
            return (DVCSRequestInformation)o;
        }
        if (o != null) {
            return new DVCSRequestInformation(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static DVCSRequestInformation getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.version != 1) {
            asn1EncodableVector.add(new ASN1Integer(this.version));
        }
        asn1EncodableVector.add(this.service);
        if (this.nonce != null) {
            asn1EncodableVector.add(new ASN1Integer(this.nonce));
        }
        if (this.requestTime != null) {
            asn1EncodableVector.add(this.requestTime);
        }
        final int[] array = { 0, 1, 2, 3, 4 };
        final ASN1Encodable[] array2 = { this.requester, this.requestPolicy, this.dvcs, this.dataLocations, this.extensions };
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i];
            final ASN1Encodable asn1Encodable = array2[i];
            if (asn1Encodable != null) {
                asn1EncodableVector.add(new DERTaggedObject(false, n, asn1Encodable));
            }
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("DVCSRequestInformation {\n");
        if (this.version != 1) {
            sb.append("version: " + this.version + "\n");
        }
        sb.append("service: " + this.service + "\n");
        if (this.nonce != null) {
            sb.append("nonce: " + this.nonce + "\n");
        }
        if (this.requestTime != null) {
            sb.append("requestTime: " + this.requestTime + "\n");
        }
        if (this.requester != null) {
            sb.append("requester: " + this.requester + "\n");
        }
        if (this.requestPolicy != null) {
            sb.append("requestPolicy: " + this.requestPolicy + "\n");
        }
        if (this.dvcs != null) {
            sb.append("dvcs: " + this.dvcs + "\n");
        }
        if (this.dataLocations != null) {
            sb.append("dataLocations: " + this.dataLocations + "\n");
        }
        if (this.extensions != null) {
            sb.append("extensions: " + this.extensions + "\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public ServiceType getService() {
        return this.service;
    }
    
    public BigInteger getNonce() {
        return this.nonce;
    }
    
    public DVCSTime getRequestTime() {
        return this.requestTime;
    }
    
    public GeneralNames getRequester() {
        return this.requester;
    }
    
    public PolicyInformation getRequestPolicy() {
        return this.requestPolicy;
    }
    
    public GeneralNames getDVCS() {
        return this.dvcs;
    }
    
    public GeneralNames getDataLocations() {
        return this.dataLocations;
    }
    
    public Extensions getExtensions() {
        return this.extensions;
    }
}

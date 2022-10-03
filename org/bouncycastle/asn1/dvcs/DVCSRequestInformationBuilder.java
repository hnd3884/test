package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.GeneralNames;
import java.math.BigInteger;

public class DVCSRequestInformationBuilder
{
    private int version;
    private final ServiceType service;
    private DVCSRequestInformation initialInfo;
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
    
    public DVCSRequestInformationBuilder(final ServiceType service) {
        this.version = 1;
        this.service = service;
    }
    
    public DVCSRequestInformationBuilder(final DVCSRequestInformation initialInfo) {
        this.version = 1;
        this.initialInfo = initialInfo;
        this.service = initialInfo.getService();
        this.version = initialInfo.getVersion();
        this.nonce = initialInfo.getNonce();
        this.requestTime = initialInfo.getRequestTime();
        this.requestPolicy = initialInfo.getRequestPolicy();
        this.dvcs = initialInfo.getDVCS();
        this.dataLocations = initialInfo.getDataLocations();
    }
    
    public DVCSRequestInformation build() {
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
        return DVCSRequestInformation.getInstance(new DERSequence(asn1EncodableVector));
    }
    
    public void setVersion(final int version) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change version in existing DVCSRequestInformation");
        }
        this.version = version;
    }
    
    public void setNonce(final BigInteger bigInteger) {
        if (this.initialInfo != null) {
            if (this.initialInfo.getNonce() == null) {
                this.nonce = bigInteger;
            }
            else {
                final byte[] byteArray = this.initialInfo.getNonce().toByteArray();
                final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
                final byte[] array = new byte[byteArray.length + unsignedByteArray.length];
                System.arraycopy(byteArray, 0, array, 0, byteArray.length);
                System.arraycopy(unsignedByteArray, 0, array, byteArray.length, unsignedByteArray.length);
                this.nonce = new BigInteger(array);
            }
        }
        this.nonce = bigInteger;
    }
    
    public void setRequestTime(final DVCSTime requestTime) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change request time in existing DVCSRequestInformation");
        }
        this.requestTime = requestTime;
    }
    
    public void setRequester(final GeneralName generalName) {
        this.setRequester(new GeneralNames(generalName));
    }
    
    public void setRequester(final GeneralNames requester) {
        this.requester = requester;
    }
    
    public void setRequestPolicy(final PolicyInformation requestPolicy) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change request policy in existing DVCSRequestInformation");
        }
        this.requestPolicy = requestPolicy;
    }
    
    public void setDVCS(final GeneralName generalName) {
        this.setDVCS(new GeneralNames(generalName));
    }
    
    public void setDVCS(final GeneralNames dvcs) {
        this.dvcs = dvcs;
    }
    
    public void setDataLocations(final GeneralName generalName) {
        this.setDataLocations(new GeneralNames(generalName));
    }
    
    public void setDataLocations(final GeneralNames dataLocations) {
        this.dataLocations = dataLocations;
    }
    
    public void setExtensions(final Extensions extensions) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change extensions in existing DVCSRequestInformation");
        }
        this.extensions = extensions;
    }
}

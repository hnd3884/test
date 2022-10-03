package org.bouncycastle.dvcs;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.tsp.TimeStampToken;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;

public class DVCSRequestInfo
{
    private DVCSRequestInformation data;
    
    public DVCSRequestInfo(final byte[] array) {
        this(DVCSRequestInformation.getInstance((Object)array));
    }
    
    public DVCSRequestInfo(final DVCSRequestInformation data) {
        this.data = data;
    }
    
    public DVCSRequestInformation toASN1Structure() {
        return this.data;
    }
    
    public int getVersion() {
        return this.data.getVersion();
    }
    
    public int getServiceType() {
        return this.data.getService().getValue().intValue();
    }
    
    public BigInteger getNonce() {
        return this.data.getNonce();
    }
    
    public Date getRequestTime() throws DVCSParsingException {
        final DVCSTime requestTime = this.data.getRequestTime();
        if (requestTime == null) {
            return null;
        }
        try {
            if (requestTime.getGenTime() != null) {
                return requestTime.getGenTime().getDate();
            }
            return new TimeStampToken(requestTime.getTimeStampToken()).getTimeStampInfo().getGenTime();
        }
        catch (final Exception ex) {
            throw new DVCSParsingException("unable to extract time: " + ex.getMessage(), ex);
        }
    }
    
    public GeneralNames getRequester() {
        return this.data.getRequester();
    }
    
    public PolicyInformation getRequestPolicy() {
        if (this.data.getRequestPolicy() != null) {
            return this.data.getRequestPolicy();
        }
        return null;
    }
    
    public GeneralNames getDVCSNames() {
        return this.data.getDVCS();
    }
    
    public GeneralNames getDataLocations() {
        return this.data.getDataLocations();
    }
    
    public static boolean validate(final DVCSRequestInfo dvcsRequestInfo, final DVCSRequestInfo dvcsRequestInfo2) {
        final DVCSRequestInformation data = dvcsRequestInfo.data;
        final DVCSRequestInformation data2 = dvcsRequestInfo2.data;
        if (data.getVersion() != data2.getVersion()) {
            return false;
        }
        if (!clientEqualsServer(data.getService(), data2.getService())) {
            return false;
        }
        if (!clientEqualsServer(data.getRequestTime(), data2.getRequestTime())) {
            return false;
        }
        if (!clientEqualsServer(data.getRequestPolicy(), data2.getRequestPolicy())) {
            return false;
        }
        if (!clientEqualsServer(data.getExtensions(), data2.getExtensions())) {
            return false;
        }
        if (data.getNonce() != null) {
            if (data2.getNonce() == null) {
                return false;
            }
            final byte[] byteArray = data.getNonce().toByteArray();
            final byte[] byteArray2 = data2.getNonce().toByteArray();
            if (byteArray2.length < byteArray.length) {
                return false;
            }
            if (!Arrays.areEqual(byteArray, Arrays.copyOfRange(byteArray2, 0, byteArray.length))) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean clientEqualsServer(final Object o, final Object o2) {
        return (o == null && o2 == null) || (o != null && o.equals(o2));
    }
}

package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import java.util.Date;
import org.bouncycastle.asn1.ocsp.ResponseData;

public class RespData
{
    private ResponseData data;
    
    public RespData(final ResponseData data) {
        this.data = data;
    }
    
    public int getVersion() {
        return this.data.getVersion().getValue().intValue() + 1;
    }
    
    public RespID getResponderId() {
        return new RespID(this.data.getResponderID());
    }
    
    public Date getProducedAt() {
        return OCSPUtils.extractDate(this.data.getProducedAt());
    }
    
    public SingleResp[] getResponses() {
        final ASN1Sequence responses = this.data.getResponses();
        final SingleResp[] array = new SingleResp[responses.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = new SingleResp(SingleResponse.getInstance((Object)responses.getObjectAt(i)));
        }
        return array;
    }
    
    public Extensions getResponseExtensions() {
        return this.data.getResponseExtensions();
    }
}

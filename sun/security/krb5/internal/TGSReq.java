package sun.security.krb5.internal;

import sun.security.krb5.KrbException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;

public class TGSReq extends KDCReq
{
    public TGSReq(final PAData[] array, final KDCReqBody kdcReqBody) throws IOException {
        super(array, kdcReqBody, 12);
    }
    
    public TGSReq(final byte[] array) throws Asn1Exception, IOException, KrbException {
        this.init(new DerValue(array));
    }
    
    public TGSReq(final DerValue derValue) throws Asn1Exception, IOException, KrbException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException, KrbException {
        this.init(derValue, 12);
    }
}

package sun.security.krb5.internal;

import sun.security.krb5.KrbException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;

public class ASReq extends KDCReq
{
    public ASReq(final PAData[] array, final KDCReqBody kdcReqBody) throws IOException {
        super(array, kdcReqBody, 10);
    }
    
    public ASReq(final byte[] array) throws Asn1Exception, KrbException, IOException {
        this.init(new DerValue(array));
    }
    
    public ASReq(final DerValue derValue) throws Asn1Exception, KrbException, IOException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException, KrbException {
        super.init(derValue, 10);
    }
}

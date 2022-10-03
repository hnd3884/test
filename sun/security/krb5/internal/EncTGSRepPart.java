package sun.security.krb5.internal;

import sun.security.krb5.KrbException;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptionKey;

public class EncTGSRepPart extends EncKDCRepPart
{
    public EncTGSRepPart(final EncryptionKey encryptionKey, final LastReq lastReq, final int n, final KerberosTime kerberosTime, final TicketFlags ticketFlags, final KerberosTime kerberosTime2, final KerberosTime kerberosTime3, final KerberosTime kerberosTime4, final KerberosTime kerberosTime5, final PrincipalName principalName, final HostAddresses hostAddresses, final PAData[] array) {
        super(encryptionKey, lastReq, n, kerberosTime, ticketFlags, kerberosTime2, kerberosTime3, kerberosTime4, kerberosTime5, principalName, hostAddresses, array, 26);
    }
    
    public EncTGSRepPart(final byte[] array) throws Asn1Exception, IOException, KrbException {
        this.init(new DerValue(array));
    }
    
    public EncTGSRepPart(final DerValue derValue) throws Asn1Exception, IOException, KrbException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException, KrbException {
        this.init(derValue, 26);
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        return this.asn1Encode(26);
    }
}

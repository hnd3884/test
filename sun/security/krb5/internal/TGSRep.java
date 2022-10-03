package sun.security.krb5.internal;

import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;

public class TGSRep extends KDCRep
{
    public TGSRep(final PAData[] array, final PrincipalName principalName, final Ticket ticket, final EncryptedData encryptedData) throws IOException {
        super(array, principalName, ticket, encryptedData, 13);
    }
    
    public TGSRep(final byte[] array) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(new DerValue(array));
    }
    
    public TGSRep(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(derValue, 13);
    }
}

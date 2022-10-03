package sun.security.krb5.internal;

import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;

public class ASRep extends KDCRep
{
    public ASRep(final PAData[] array, final PrincipalName principalName, final Ticket ticket, final EncryptedData encryptedData) throws IOException {
        super(array, principalName, ticket, encryptedData, 11);
    }
    
    public ASRep(final byte[] array) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(new DerValue(array));
    }
    
    public ASRep(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(derValue, 11);
    }
}

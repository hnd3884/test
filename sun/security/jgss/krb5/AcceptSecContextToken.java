package sun.security.jgss.krb5;

import sun.security.jgss.GSSToken;
import sun.security.util.DerValue;
import java.io.InputStream;
import sun.security.krb5.Credentials;
import org.ietf.jgss.GSSException;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.EncryptionKey;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.KrbApRep;

class AcceptSecContextToken extends InitialToken
{
    private KrbApRep apRep;
    
    public AcceptSecContextToken(final Krb5Context krb5Context, final KrbApReq krbApReq) throws KrbException, IOException, GSSException {
        this.apRep = null;
        final boolean booleanValue = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.krb5.acceptor.subkey"));
        final boolean b = true;
        EncryptionKey encryptionKey = null;
        if (booleanValue) {
            encryptionKey = new EncryptionKey(krbApReq.getCreds().getSessionKey());
            krb5Context.setKey(2, encryptionKey);
        }
        this.apRep = new KrbApRep(krbApReq, b, encryptionKey);
        krb5Context.resetMySequenceNumber(this.apRep.getSeqNumber());
    }
    
    public AcceptSecContextToken(final Krb5Context krb5Context, final Credentials credentials, final KrbApReq krbApReq, final InputStream inputStream) throws IOException, GSSException, KrbException {
        this.apRep = null;
        if ((inputStream.read() << 8 | inputStream.read()) != 0x200) {
            throw new GSSException(10, -1, "AP_REP token id does not match!");
        }
        final KrbApRep krbApRep = new KrbApRep(new DerValue(inputStream).toByteArray(), credentials, krbApReq);
        final EncryptionKey subKey = krbApRep.getSubKey();
        if (subKey != null) {
            krb5Context.setKey(2, subKey);
        }
        final Integer seqNumber = krbApRep.getSeqNumber();
        krb5Context.resetPeerSequenceNumber((seqNumber != null) ? ((int)seqNumber) : 0);
    }
    
    @Override
    public final byte[] encode() throws IOException {
        final byte[] message = this.apRep.getMessage();
        final byte[] array = new byte[2 + message.length];
        GSSToken.writeInt(512, array, 0);
        System.arraycopy(message, 0, array, 2, message.length);
        return array;
    }
}

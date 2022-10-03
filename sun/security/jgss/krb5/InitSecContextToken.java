package sun.security.jgss.krb5;

import sun.security.action.GetPropertyAction;
import sun.security.jgss.GSSToken;
import sun.security.krb5.internal.AuthorizationData;
import java.net.InetAddress;
import com.sun.security.jgss.AuthorizationDataEntry;
import sun.security.util.DerValue;
import java.io.InputStream;
import org.ietf.jgss.GSSException;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.Checksum;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.Credentials;
import sun.security.krb5.KrbApReq;

class InitSecContextToken extends InitialToken
{
    private static final boolean ACCEPTOR_USE_INITIATOR_SEQNUM;
    private KrbApReq apReq;
    
    InitSecContextToken(final Krb5Context krb5Context, final Credentials credentials, final Credentials credentials2) throws KrbException, IOException, GSSException {
        this.apReq = null;
        final boolean mutualAuthState = krb5Context.getMutualAuthState();
        final boolean b = true;
        final boolean b2 = true;
        final Checksum checksum = new OverloadedChecksum(krb5Context, credentials, credentials2).getChecksum();
        krb5Context.setTktFlags(credentials2.getFlags());
        krb5Context.setAuthTime(new KerberosTime(credentials2.getAuthTime()).toString());
        this.apReq = new KrbApReq(credentials2, mutualAuthState, b, b2, checksum);
        krb5Context.resetMySequenceNumber(this.apReq.getSeqNumber());
        final EncryptionKey subKey = this.apReq.getSubKey();
        if (subKey != null) {
            krb5Context.setKey(1, subKey);
        }
        else {
            krb5Context.setKey(0, credentials2.getSessionKey());
        }
        if (!mutualAuthState) {
            krb5Context.resetPeerSequenceNumber(InitSecContextToken.ACCEPTOR_USE_INITIATOR_SEQNUM ? ((int)this.apReq.getSeqNumber()) : 0);
        }
    }
    
    InitSecContextToken(final Krb5Context contextFlags, final Krb5AcceptCredential krb5AcceptCredential, final InputStream inputStream) throws IOException, GSSException, KrbException {
        this.apReq = null;
        if ((inputStream.read() << 8 | inputStream.read()) != 0x100) {
            throw new GSSException(10, -1, "AP_REQ token id does not match!");
        }
        final byte[] byteArray = new DerValue(inputStream).toByteArray();
        InetAddress initiatorAddress = null;
        if (contextFlags.getChannelBinding() != null) {
            initiatorAddress = contextFlags.getChannelBinding().getInitiatorAddress();
        }
        this.apReq = new KrbApReq(byteArray, krb5AcceptCredential, initiatorAddress);
        final EncryptionKey sessionKey = this.apReq.getCreds().getSessionKey();
        final EncryptionKey subKey = this.apReq.getSubKey();
        if (subKey != null) {
            contextFlags.setKey(1, subKey);
        }
        else {
            contextFlags.setKey(0, sessionKey);
        }
        final OverloadedChecksum overloadedChecksum = new OverloadedChecksum(contextFlags, this.apReq.getChecksum(), sessionKey, subKey);
        overloadedChecksum.setContextFlags(contextFlags);
        final Credentials delegatedCreds = overloadedChecksum.getDelegatedCreds();
        if (delegatedCreds != null) {
            contextFlags.setDelegCred(Krb5InitCredential.getInstance((Krb5NameElement)contextFlags.getSrcName(), delegatedCreds));
        }
        final Integer seqNumber = this.apReq.getSeqNumber();
        final int n = (seqNumber != null) ? seqNumber : 0;
        contextFlags.resetPeerSequenceNumber(n);
        if (!contextFlags.getMutualAuthState()) {
            contextFlags.resetMySequenceNumber(InitSecContextToken.ACCEPTOR_USE_INITIATOR_SEQNUM ? n : false);
        }
        contextFlags.setAuthTime(new KerberosTime(this.apReq.getCreds().getAuthTime()).toString());
        contextFlags.setTktFlags(this.apReq.getCreds().getFlags());
        final AuthorizationData authzData = this.apReq.getCreds().getAuthzData();
        if (authzData == null) {
            contextFlags.setAuthzData(null);
        }
        else {
            final AuthorizationDataEntry[] authzData2 = new AuthorizationDataEntry[authzData.count()];
            for (int i = 0; i < authzData.count(); ++i) {
                authzData2[i] = new AuthorizationDataEntry(authzData.item(i).adType, authzData.item(i).adData);
            }
            contextFlags.setAuthzData(authzData2);
        }
    }
    
    public final KrbApReq getKrbApReq() {
        return this.apReq;
    }
    
    @Override
    public final byte[] encode() throws IOException {
        final byte[] message = this.apReq.getMessage();
        final byte[] array = new byte[2 + message.length];
        GSSToken.writeInt(256, array, 0);
        System.arraycopy(message, 0, array, 2, message.length);
        return array;
    }
    
    static {
        final String s = "sun.security.krb5.acceptor.sequence.number.nonmutual";
        final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty(s, "initiator");
        if (privilegedGetProperty.equals("initiator")) {
            ACCEPTOR_USE_INITIATOR_SEQNUM = true;
        }
        else {
            if (!privilegedGetProperty.equals("zero") && !privilegedGetProperty.equals("0")) {
                throw new AssertionError((Object)("Unrecognized value for " + s + ": " + privilegedGetProperty));
            }
            ACCEPTOR_USE_INITIATOR_SEQNUM = false;
        }
    }
}

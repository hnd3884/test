package sun.security.jgss.krb5;

import java.util.Arrays;
import sun.security.krb5.Checksum;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import java.security.Permission;
import javax.security.auth.kerberos.DelegationPermission;
import sun.security.krb5.KrbCred;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.Credentials;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import sun.security.jgss.GSSToken;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import java.net.Inet6Address;
import java.net.Inet4Address;
import java.net.InetAddress;

abstract class InitialToken extends Krb5Token
{
    private static final int CHECKSUM_TYPE = 32771;
    private static final int CHECKSUM_LENGTH_SIZE = 4;
    private static final int CHECKSUM_BINDINGS_SIZE = 16;
    private static final int CHECKSUM_FLAGS_SIZE = 4;
    private static final int CHECKSUM_DELEG_OPT_SIZE = 2;
    private static final int CHECKSUM_DELEG_LGTH_SIZE = 2;
    private static final int CHECKSUM_DELEG_FLAG = 1;
    private static final int CHECKSUM_MUTUAL_FLAG = 2;
    private static final int CHECKSUM_REPLAY_FLAG = 4;
    private static final int CHECKSUM_SEQUENCE_FLAG = 8;
    private static final int CHECKSUM_CONF_FLAG = 16;
    private static final int CHECKSUM_INTEG_FLAG = 32;
    private final byte[] CHECKSUM_FIRST_BYTES;
    private static final int CHANNEL_BINDING_AF_UNSPEC = 0;
    private static final int CHANNEL_BINDING_AF_INET = 2;
    private static final int CHANNEL_BINDING_AF_INET6 = 24;
    private static final int CHANNEL_BINDING_AF_NULL_ADDR = 255;
    private static final int Inet4_ADDRSZ = 4;
    private static final int Inet6_ADDRSZ = 16;
    
    InitialToken() {
        this.CHECKSUM_FIRST_BYTES = new byte[] { 16, 0, 0, 0 };
    }
    
    private int getAddrType(final InetAddress inetAddress, final int n) {
        int n2 = n;
        if (inetAddress instanceof Inet4Address) {
            n2 = 2;
        }
        else if (inetAddress instanceof Inet6Address) {
            n2 = 24;
        }
        return n2;
    }
    
    private byte[] getAddrBytes(final InetAddress inetAddress) throws GSSException {
        final int addrType = this.getAddrType(inetAddress, 255);
        final byte[] address = inetAddress.getAddress();
        if (address == null) {
            return null;
        }
        switch (addrType) {
            case 2: {
                if (address.length != 4) {
                    throw new GSSException(11, -1, "Incorrect AF-INET address length in ChannelBinding.");
                }
                return address;
            }
            case 24: {
                if (address.length != 16) {
                    throw new GSSException(11, -1, "Incorrect AF-INET6 address length in ChannelBinding.");
                }
                return address;
            }
            default: {
                throw new GSSException(11, -1, "Cannot handle non AF-INET addresses in ChannelBinding.");
            }
        }
    }
    
    private byte[] computeChannelBinding(final ChannelBinding channelBinding) throws GSSException {
        final InetAddress initiatorAddress = channelBinding.getInitiatorAddress();
        final InetAddress acceptorAddress = channelBinding.getAcceptorAddress();
        int n = 20;
        final int addrType = this.getAddrType(initiatorAddress, (channelBinding instanceof TlsChannelBindingImpl) ? 0 : 255);
        final int addrType2 = this.getAddrType(acceptorAddress, (channelBinding instanceof TlsChannelBindingImpl) ? 0 : 255);
        Object addrBytes = null;
        if (initiatorAddress != null) {
            addrBytes = this.getAddrBytes(initiatorAddress);
            n += addrBytes.length;
        }
        Object addrBytes2 = null;
        if (acceptorAddress != null) {
            addrBytes2 = this.getAddrBytes(acceptorAddress);
            n += addrBytes2.length;
        }
        final byte[] applicationData = channelBinding.getApplicationData();
        if (applicationData != null) {
            n += applicationData.length;
        }
        final byte[] array = new byte[n];
        int n2 = 0;
        GSSToken.writeLittleEndian(addrType, array, n2);
        n2 += 4;
        if (addrBytes != null) {
            GSSToken.writeLittleEndian(addrBytes.length, array, n2);
            n2 += 4;
            System.arraycopy(addrBytes, 0, array, n2, addrBytes.length);
            n2 += addrBytes.length;
        }
        else {
            n2 += 4;
        }
        GSSToken.writeLittleEndian(addrType2, array, n2);
        n2 += 4;
        if (addrBytes2 != null) {
            GSSToken.writeLittleEndian(addrBytes2.length, array, n2);
            n2 += 4;
            System.arraycopy(addrBytes2, 0, array, n2, addrBytes2.length);
            n2 += addrBytes2.length;
        }
        else {
            n2 += 4;
        }
        if (applicationData != null) {
            GSSToken.writeLittleEndian(applicationData.length, array, n2);
            n2 += 4;
            System.arraycopy(applicationData, 0, array, n2, applicationData.length);
            final int n3 = n2 + applicationData.length;
        }
        else {
            n2 += 4;
        }
        try {
            return MessageDigest.getInstance("MD5").digest(array);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new GSSException(11, -1, "Could not get MD5 Message Digest - " + ex.getMessage());
        }
    }
    
    public abstract byte[] encode() throws IOException;
    
    protected class OverloadedChecksum
    {
        private byte[] checksumBytes;
        private Credentials delegCreds;
        private int flags;
        
        public OverloadedChecksum(final Krb5Context krb5Context, final Credentials credentials, final Credentials credentials2) throws KrbException, IOException, GSSException {
            this.checksumBytes = null;
            this.delegCreds = null;
            this.flags = 0;
            Object message = null;
            int n = 0;
            int n2 = 24;
            if (!credentials.isForwardable()) {
                krb5Context.setCredDelegState(false);
                krb5Context.setDelegPolicyState(false);
            }
            else if (krb5Context.getCredDelegState()) {
                if (krb5Context.getDelegPolicyState() && !credentials2.checkDelegate()) {
                    krb5Context.setDelegPolicyState(false);
                }
            }
            else if (krb5Context.getDelegPolicyState()) {
                if (credentials2.checkDelegate()) {
                    krb5Context.setCredDelegState(true);
                }
                else {
                    krb5Context.setDelegPolicyState(false);
                }
            }
            if (krb5Context.getCredDelegState()) {
                KrbCred krbCred;
                if (this.useNullKey(krb5Context.getCipherHelper(credentials2.getSessionKey()))) {
                    krbCred = new KrbCred(credentials, credentials2, EncryptionKey.NULL_KEY);
                }
                else {
                    krbCred = new KrbCred(credentials, credentials2, credentials2.getSessionKey());
                }
                message = krbCred.getMessage();
                n2 += 4 + message.length;
            }
            (this.checksumBytes = new byte[n2])[n++] = InitialToken.this.CHECKSUM_FIRST_BYTES[0];
            this.checksumBytes[n++] = InitialToken.this.CHECKSUM_FIRST_BYTES[1];
            this.checksumBytes[n++] = InitialToken.this.CHECKSUM_FIRST_BYTES[2];
            this.checksumBytes[n++] = InitialToken.this.CHECKSUM_FIRST_BYTES[3];
            if (krb5Context.getChannelBinding() != null) {
                final byte[] access$100 = InitialToken.this.computeChannelBinding(krb5Context.getChannelBinding());
                System.arraycopy(access$100, 0, this.checksumBytes, n, access$100.length);
            }
            n += 16;
            if (krb5Context.getCredDelegState()) {
                this.flags |= 0x1;
            }
            if (krb5Context.getMutualAuthState()) {
                this.flags |= 0x2;
            }
            if (krb5Context.getReplayDetState()) {
                this.flags |= 0x4;
            }
            if (krb5Context.getSequenceDetState()) {
                this.flags |= 0x8;
            }
            if (krb5Context.getIntegState()) {
                this.flags |= 0x20;
            }
            if (krb5Context.getConfState()) {
                this.flags |= 0x10;
            }
            final byte[] array = new byte[4];
            GSSToken.writeLittleEndian(this.flags, array);
            this.checksumBytes[n++] = array[0];
            this.checksumBytes[n++] = array[1];
            this.checksumBytes[n++] = array[2];
            this.checksumBytes[n++] = array[3];
            if (krb5Context.getCredDelegState()) {
                final PrincipalName server = credentials2.getServer();
                final StringBuffer sb = new StringBuffer("\"");
                sb.append(server.getName()).append('\"');
                final String realmAsString = server.getRealmAsString();
                sb.append(" \"krbtgt/").append(realmAsString).append('@');
                sb.append(realmAsString).append('\"');
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    securityManager.checkPermission(new DelegationPermission(sb.toString()));
                }
                this.checksumBytes[n++] = 1;
                this.checksumBytes[n++] = 0;
                if (message.length > 65535) {
                    throw new GSSException(11, -1, "Incorrect message length");
                }
                GSSToken.writeLittleEndian(message.length, array);
                this.checksumBytes[n++] = array[0];
                this.checksumBytes[n++] = array[1];
                System.arraycopy(message, 0, this.checksumBytes, n, message.length);
            }
        }
        
        public OverloadedChecksum(final Krb5Context krb5Context, final Checksum checksum, final EncryptionKey encryptionKey, final EncryptionKey encryptionKey2) throws GSSException, KrbException, IOException {
            this.checksumBytes = null;
            this.delegCreds = null;
            this.flags = 0;
            if (checksum == null) {
                final GSSException ex = new GSSException(11, -1, "No cksum in AP_REQ's authenticator");
                ex.initCause(new KrbException(50));
                throw ex;
            }
            this.checksumBytes = checksum.getBytes();
            if (this.checksumBytes[0] != InitialToken.this.CHECKSUM_FIRST_BYTES[0] || this.checksumBytes[1] != InitialToken.this.CHECKSUM_FIRST_BYTES[1] || this.checksumBytes[2] != InitialToken.this.CHECKSUM_FIRST_BYTES[2] || this.checksumBytes[3] != InitialToken.this.CHECKSUM_FIRST_BYTES[3]) {
                throw new GSSException(11, -1, "Incorrect checksum");
            }
            final ChannelBinding channelBinding = krb5Context.getChannelBinding();
            if (channelBinding != null) {
                final byte[] array = new byte[16];
                System.arraycopy(this.checksumBytes, 4, array, 0, 16);
                if (Arrays.equals(new byte[16], array)) {
                    throw new GSSException(1, -1, "Token missing ChannelBinding!");
                }
                if (!Arrays.equals(InitialToken.this.computeChannelBinding(channelBinding), array)) {
                    throw new GSSException(1, -1, "Bytes mismatch!");
                }
            }
            this.flags = GSSToken.readLittleEndian(this.checksumBytes, 20, 4);
            if ((this.flags & 0x1) > 0) {
                final int littleEndian = GSSToken.readLittleEndian(this.checksumBytes, 26, 2);
                final byte[] array2 = new byte[littleEndian];
                System.arraycopy(this.checksumBytes, 28, array2, 0, littleEndian);
                KrbCred krbCred;
                try {
                    krbCred = new KrbCred(array2, encryptionKey);
                }
                catch (final KrbException ex2) {
                    if (encryptionKey2 == null) {
                        throw ex2;
                    }
                    krbCred = new KrbCred(array2, encryptionKey2);
                }
                this.delegCreds = krbCred.getDelegatedCreds()[0];
            }
        }
        
        private boolean useNullKey(final CipherHelper cipherHelper) {
            boolean b = true;
            if (cipherHelper.getProto() == 1 || cipherHelper.isArcFour()) {
                b = false;
            }
            return b;
        }
        
        public Checksum getChecksum() throws KrbException {
            return new Checksum(this.checksumBytes, 32771);
        }
        
        public Credentials getDelegatedCreds() {
            return this.delegCreds;
        }
        
        public void setContextFlags(final Krb5Context krb5Context) {
            if ((this.flags & 0x1) > 0) {
                krb5Context.setCredDelegState(true);
            }
            if ((this.flags & 0x2) == 0x0) {
                krb5Context.setMutualAuthState(false);
            }
            if ((this.flags & 0x4) == 0x0) {
                krb5Context.setReplayDetState(false);
            }
            if ((this.flags & 0x8) == 0x0) {
                krb5Context.setSequenceDetState(false);
            }
            if ((this.flags & 0x10) == 0x0) {
                krb5Context.setConfState(false);
            }
            if ((this.flags & 0x20) == 0x0) {
                krb5Context.setIntegState(false);
            }
        }
    }
}

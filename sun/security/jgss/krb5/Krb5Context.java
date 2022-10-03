package sun.security.jgss.krb5;

import java.security.Key;
import com.sun.security.jgss.InquireType;
import java.security.Permission;
import javax.security.auth.kerberos.ServicePermission;
import java.security.Provider;
import java.io.OutputStream;
import org.ietf.jgss.MessageProp;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.misc.HexDumpEncoder;
import java.security.PrivilegedAction;
import javax.security.auth.Subject;
import java.security.PrivilegedActionException;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.kerberos.KerberosTicket;
import sun.security.jgss.GSSUtil;
import java.security.AccessController;
import java.io.InputStream;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import com.sun.security.jgss.AuthorizationDataEntry;
import sun.security.jgss.GSSCaller;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.KrbApReq;
import sun.security.krb5.Credentials;
import org.ietf.jgss.ChannelBinding;
import sun.security.krb5.EncryptionKey;
import sun.security.jgss.TokenTracker;
import sun.security.jgss.spi.GSSContextSpi;

class Krb5Context implements GSSContextSpi
{
    private static final int STATE_NEW = 1;
    private static final int STATE_IN_PROCESS = 2;
    private static final int STATE_DONE = 3;
    private static final int STATE_DELETED = 4;
    private int state;
    public static final int SESSION_KEY = 0;
    public static final int INITIATOR_SUBKEY = 1;
    public static final int ACCEPTOR_SUBKEY = 2;
    private boolean credDelegState;
    private boolean mutualAuthState;
    private boolean replayDetState;
    private boolean sequenceDetState;
    private boolean confState;
    private boolean integState;
    private boolean delegPolicyState;
    private boolean isConstrainedDelegationTried;
    private int mySeqNumber;
    private int peerSeqNumber;
    private int keySrc;
    private TokenTracker peerTokenTracker;
    private CipherHelper cipherHelper;
    private Object mySeqNumberLock;
    private Object peerSeqNumberLock;
    private EncryptionKey key;
    private Krb5NameElement myName;
    private Krb5NameElement peerName;
    private int lifetime;
    private boolean initiator;
    private ChannelBinding channelBinding;
    private Krb5CredElement myCred;
    private Krb5CredElement delegatedCred;
    private Credentials serviceCreds;
    private KrbApReq apReq;
    Ticket serviceTicket;
    private final GSSCaller caller;
    private static final boolean DEBUG;
    private boolean[] tktFlags;
    private String authTime;
    private AuthorizationDataEntry[] authzData;
    
    Krb5Context(final GSSCaller caller, final Krb5NameElement peerName, final Krb5CredElement myCred, final int lifetime) throws GSSException {
        this.state = 1;
        this.credDelegState = false;
        this.mutualAuthState = true;
        this.replayDetState = true;
        this.sequenceDetState = true;
        this.confState = true;
        this.integState = true;
        this.delegPolicyState = false;
        this.isConstrainedDelegationTried = false;
        this.cipherHelper = null;
        this.mySeqNumberLock = new Object();
        this.peerSeqNumberLock = new Object();
        if (peerName == null) {
            throw new IllegalArgumentException("Cannot have null peer name");
        }
        this.caller = caller;
        this.peerName = peerName;
        this.myCred = myCred;
        this.lifetime = lifetime;
        this.initiator = true;
    }
    
    Krb5Context(final GSSCaller caller, final Krb5CredElement myCred) throws GSSException {
        this.state = 1;
        this.credDelegState = false;
        this.mutualAuthState = true;
        this.replayDetState = true;
        this.sequenceDetState = true;
        this.confState = true;
        this.integState = true;
        this.delegPolicyState = false;
        this.isConstrainedDelegationTried = false;
        this.cipherHelper = null;
        this.mySeqNumberLock = new Object();
        this.peerSeqNumberLock = new Object();
        this.caller = caller;
        this.myCred = myCred;
        this.initiator = false;
    }
    
    public Krb5Context(final GSSCaller gssCaller, final byte[] array) throws GSSException {
        this.state = 1;
        this.credDelegState = false;
        this.mutualAuthState = true;
        this.replayDetState = true;
        this.sequenceDetState = true;
        this.confState = true;
        this.integState = true;
        this.delegPolicyState = false;
        this.isConstrainedDelegationTried = false;
        this.cipherHelper = null;
        this.mySeqNumberLock = new Object();
        this.peerSeqNumberLock = new Object();
        throw new GSSException(16, -1, "GSS Import Context not available");
    }
    
    @Override
    public final boolean isTransferable() throws GSSException {
        return false;
    }
    
    @Override
    public final int getLifetime() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public void requestLifetime(final int lifetime) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.lifetime = lifetime;
        }
    }
    
    @Override
    public final void requestConf(final boolean confState) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.confState = confState;
        }
    }
    
    @Override
    public final boolean getConfState() {
        return this.confState;
    }
    
    @Override
    public final void requestInteg(final boolean integState) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.integState = integState;
        }
    }
    
    @Override
    public final boolean getIntegState() {
        return this.integState;
    }
    
    @Override
    public final void requestCredDeleg(final boolean credDelegState) throws GSSException {
        if (this.state == 1 && this.isInitiator() && (this.myCred == null || !(this.myCred instanceof Krb5ProxyCredential))) {
            this.credDelegState = credDelegState;
        }
    }
    
    @Override
    public final boolean getCredDelegState() {
        if (this.isInitiator()) {
            return this.credDelegState;
        }
        this.tryConstrainedDelegation();
        return this.delegatedCred != null;
    }
    
    @Override
    public final void requestMutualAuth(final boolean mutualAuthState) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.mutualAuthState = mutualAuthState;
        }
    }
    
    @Override
    public final boolean getMutualAuthState() {
        return this.mutualAuthState;
    }
    
    @Override
    public final void requestReplayDet(final boolean replayDetState) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.replayDetState = replayDetState;
        }
    }
    
    @Override
    public final boolean getReplayDetState() {
        return this.replayDetState || this.sequenceDetState;
    }
    
    @Override
    public final void requestSequenceDet(final boolean sequenceDetState) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.sequenceDetState = sequenceDetState;
        }
    }
    
    @Override
    public final boolean getSequenceDetState() {
        return this.sequenceDetState || this.replayDetState;
    }
    
    @Override
    public final void requestDelegPolicy(final boolean delegPolicyState) {
        if (this.state == 1 && this.isInitiator()) {
            this.delegPolicyState = delegPolicyState;
        }
    }
    
    @Override
    public final boolean getDelegPolicyState() {
        return this.delegPolicyState;
    }
    
    @Override
    public final void requestAnonymity(final boolean b) throws GSSException {
    }
    
    @Override
    public final boolean getAnonymityState() {
        return false;
    }
    
    final CipherHelper getCipherHelper(final EncryptionKey encryptionKey) throws GSSException {
        if (this.cipherHelper == null) {
            this.cipherHelper = new CipherHelper((this.getKey() == null) ? encryptionKey : this.getKey());
        }
        return this.cipherHelper;
    }
    
    final int incrementMySequenceNumber() {
        final int n;
        synchronized (this.mySeqNumberLock) {
            n = this.mySeqNumber++;
        }
        return n;
    }
    
    final void resetMySequenceNumber(final int mySeqNumber) {
        if (Krb5Context.DEBUG) {
            System.out.println("Krb5Context setting mySeqNumber to: " + mySeqNumber);
        }
        synchronized (this.mySeqNumberLock) {
            this.mySeqNumber = mySeqNumber;
        }
    }
    
    final void resetPeerSequenceNumber(final int peerSeqNumber) {
        if (Krb5Context.DEBUG) {
            System.out.println("Krb5Context setting peerSeqNumber to: " + peerSeqNumber);
        }
        synchronized (this.peerSeqNumberLock) {
            this.peerSeqNumber = peerSeqNumber;
            this.peerTokenTracker = new TokenTracker(this.peerSeqNumber);
        }
    }
    
    final void setKey(final int keySrc, final EncryptionKey key) throws GSSException {
        this.key = key;
        this.keySrc = keySrc;
        this.cipherHelper = new CipherHelper(key);
    }
    
    public final int getKeySrc() {
        return this.keySrc;
    }
    
    private final EncryptionKey getKey() {
        return this.key;
    }
    
    final void setDelegCred(final Krb5CredElement delegatedCred) {
        this.delegatedCred = delegatedCred;
    }
    
    final void setCredDelegState(final boolean credDelegState) {
        this.credDelegState = credDelegState;
    }
    
    final void setMutualAuthState(final boolean mutualAuthState) {
        this.mutualAuthState = mutualAuthState;
    }
    
    final void setReplayDetState(final boolean replayDetState) {
        this.replayDetState = replayDetState;
    }
    
    final void setSequenceDetState(final boolean sequenceDetState) {
        this.sequenceDetState = sequenceDetState;
    }
    
    final void setConfState(final boolean confState) {
        this.confState = confState;
    }
    
    final void setIntegState(final boolean integState) {
        this.integState = integState;
    }
    
    final void setDelegPolicyState(final boolean delegPolicyState) {
        this.delegPolicyState = delegPolicyState;
    }
    
    @Override
    public final void setChannelBinding(final ChannelBinding channelBinding) throws GSSException {
        this.channelBinding = channelBinding;
    }
    
    final ChannelBinding getChannelBinding() {
        return this.channelBinding;
    }
    
    @Override
    public final Oid getMech() {
        return Krb5MechFactory.GSS_KRB5_MECH_OID;
    }
    
    @Override
    public final GSSNameSpi getSrcName() throws GSSException {
        return this.isInitiator() ? this.myName : this.peerName;
    }
    
    @Override
    public final GSSNameSpi getTargName() throws GSSException {
        return this.isInitiator() ? this.peerName : this.myName;
    }
    
    @Override
    public final GSSCredentialSpi getDelegCred() throws GSSException {
        if (this.state != 2 && this.state != 3) {
            throw new GSSException(12);
        }
        if (this.isInitiator()) {
            throw new GSSException(13);
        }
        this.tryConstrainedDelegation();
        if (this.delegatedCred == null) {
            throw new GSSException(13);
        }
        return this.delegatedCred;
    }
    
    private void tryConstrainedDelegation() {
        if (this.state != 2 && this.state != 3) {
            return;
        }
        if (!this.isConstrainedDelegationTried) {
            if (this.delegatedCred == null) {
                if (Krb5Context.DEBUG) {
                    System.out.println(">>> Constrained deleg from " + this.caller);
                }
                try {
                    this.delegatedCred = new Krb5ProxyCredential(Krb5InitCredential.getInstance(GSSCaller.CALLER_ACCEPT, this.myName, this.lifetime), this.peerName, this.serviceTicket);
                }
                catch (final GSSException ex) {}
            }
            this.isConstrainedDelegationTried = true;
        }
    }
    
    @Override
    public final boolean isInitiator() {
        return this.initiator;
    }
    
    @Override
    public final boolean isProtReady() {
        return this.state == 3;
    }
    
    @Override
    public final byte[] initSecContext(final InputStream inputStream, final int n) throws GSSException {
        byte[] encode = null;
        int n2 = 11;
        if (Krb5Context.DEBUG) {
            System.out.println("Entered Krb5Context.initSecContext with state=" + printState(this.state));
        }
        if (!this.isInitiator()) {
            throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
        }
        try {
            if (this.state == 1) {
                this.state = 2;
                n2 = 13;
                if (this.myCred == null) {
                    this.myCred = Krb5InitCredential.getInstance(this.caller, this.myName, 0);
                    this.myCred = Krb5ProxyCredential.tryImpersonation(this.caller, (Krb5InitCredential)this.myCred);
                }
                else if (!this.myCred.isInitiatorCredential()) {
                    throw new GSSException(n2, -1, "No TGT available");
                }
                this.myName = (Krb5NameElement)this.myCred.getName();
                Krb5ProxyCredential krb5ProxyCredential;
                Credentials credentials;
                if (this.myCred instanceof Krb5InitCredential) {
                    krb5ProxyCredential = null;
                    credentials = ((Krb5InitCredential)this.myCred).getKrb5Credentials();
                }
                else {
                    krb5ProxyCredential = (Krb5ProxyCredential)this.myCred;
                    credentials = krb5ProxyCredential.self.getKrb5Credentials();
                }
                this.checkPermission(this.peerName.getKrb5PrincipalName().getName(), "initiate");
                final AccessControlContext context = AccessController.getContext();
                if (GSSUtil.useSubjectCredsOnly(this.caller)) {
                    KerberosTicket kerberosTicket = null;
                    try {
                        kerberosTicket = AccessController.doPrivileged((PrivilegedExceptionAction<KerberosTicket>)new PrivilegedExceptionAction<KerberosTicket>() {
                            @Override
                            public KerberosTicket run() throws Exception {
                                return Krb5Util.getServiceTicket(GSSCaller.CALLER_UNKNOWN, (krb5ProxyCredential == null) ? Krb5Context.this.myName.getKrb5PrincipalName().getName() : krb5ProxyCredential.getName().getKrb5PrincipalName().getName(), Krb5Context.this.peerName.getKrb5PrincipalName().getName(), context);
                            }
                        });
                    }
                    catch (final PrivilegedActionException ex) {
                        if (Krb5Context.DEBUG) {
                            System.out.println("Attempt to obtain service ticket from the subject failed!");
                        }
                    }
                    if (kerberosTicket != null) {
                        if (Krb5Context.DEBUG) {
                            System.out.println("Found service ticket in the subject" + kerberosTicket);
                        }
                        this.serviceCreds = Krb5Util.ticketToCreds(kerberosTicket);
                    }
                }
                if (this.serviceCreds == null) {
                    if (Krb5Context.DEBUG) {
                        System.out.println("Service ticket not found in the subject");
                    }
                    if (krb5ProxyCredential == null) {
                        this.serviceCreds = Credentials.acquireServiceCreds(this.peerName.getKrb5PrincipalName().getName(), credentials);
                    }
                    else {
                        this.serviceCreds = Credentials.acquireS4U2proxyCreds(this.peerName.getKrb5PrincipalName().getName(), krb5ProxyCredential.tkt, krb5ProxyCredential.getName().getKrb5PrincipalName(), credentials);
                    }
                    if (GSSUtil.useSubjectCredsOnly(this.caller)) {
                        final Subject subject = AccessController.doPrivileged((PrivilegedAction<Subject>)new PrivilegedAction<Subject>() {
                            @Override
                            public Subject run() {
                                return Subject.getSubject(context);
                            }
                        });
                        if (subject != null && !subject.isReadOnly()) {
                            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                                final /* synthetic */ KerberosTicket val$kt = Krb5Util.credsToTicket(Krb5Context.this.serviceCreds);
                                
                                @Override
                                public Void run() {
                                    subject.getPrivateCredentials().add(this.val$kt);
                                    return null;
                                }
                            });
                        }
                        else if (Krb5Context.DEBUG) {
                            System.out.println("Subject is readOnly;Kerberos Service ticket not stored");
                        }
                    }
                }
                n2 = 11;
                final InitSecContextToken initSecContextToken = new InitSecContextToken(this, credentials, this.serviceCreds);
                this.apReq = initSecContextToken.getKrbApReq();
                encode = initSecContextToken.encode();
                this.myCred = null;
                if (!this.getMutualAuthState()) {
                    this.state = 3;
                }
                if (Krb5Context.DEBUG) {
                    System.out.println("Created InitSecContextToken:\n" + new HexDumpEncoder().encodeBuffer(encode));
                }
            }
            else if (this.state == 2) {
                new AcceptSecContextToken(this, this.serviceCreds, this.apReq, inputStream);
                this.serviceCreds = null;
                this.apReq = null;
                this.state = 3;
            }
            else if (Krb5Context.DEBUG) {
                System.out.println(this.state);
            }
        }
        catch (final KrbException ex2) {
            if (Krb5Context.DEBUG) {
                ex2.printStackTrace();
            }
            final GSSException ex3 = new GSSException(n2, -1, ex2.getMessage());
            ex3.initCause(ex2);
            throw ex3;
        }
        catch (final IOException ex4) {
            final GSSException ex5 = new GSSException(n2, -1, ex4.getMessage());
            ex5.initCause(ex4);
            throw ex5;
        }
        return encode;
    }
    
    @Override
    public final boolean isEstablished() {
        return this.state == 3;
    }
    
    @Override
    public final byte[] acceptSecContext(final InputStream inputStream, final int n) throws GSSException {
        byte[] encode = null;
        if (Krb5Context.DEBUG) {
            System.out.println("Entered Krb5Context.acceptSecContext with state=" + printState(this.state));
        }
        if (this.isInitiator()) {
            throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
        }
        try {
            if (this.state == 1) {
                this.state = 2;
                if (this.myCred == null) {
                    this.myCred = Krb5AcceptCredential.getInstance(this.caller, this.myName);
                }
                else if (!this.myCred.isAcceptorCredential()) {
                    throw new GSSException(13, -1, "No Secret Key available");
                }
                this.myName = (Krb5NameElement)this.myCred.getName();
                if (this.myName != null) {
                    Krb5MechFactory.checkAcceptCredPermission(this.myName, this.myName);
                }
                final InitSecContextToken initSecContextToken = new InitSecContextToken(this, (Krb5AcceptCredential)this.myCred, inputStream);
                this.peerName = Krb5NameElement.getInstance(initSecContextToken.getKrbApReq().getClient());
                if (this.myName == null) {
                    Krb5MechFactory.checkAcceptCredPermission(this.myName = Krb5NameElement.getInstance(initSecContextToken.getKrbApReq().getCreds().getServer()), this.myName);
                }
                if (this.getMutualAuthState()) {
                    encode = new AcceptSecContextToken(this, initSecContextToken.getKrbApReq()).encode();
                }
                this.serviceTicket = initSecContextToken.getKrbApReq().getCreds().getTicket();
                this.myCred = null;
                this.state = 3;
            }
            else if (Krb5Context.DEBUG) {
                System.out.println(this.state);
            }
        }
        catch (final KrbException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        catch (final IOException ex3) {
            if (Krb5Context.DEBUG) {
                ex3.printStackTrace();
            }
            final GSSException ex4 = new GSSException(11, -1, ex3.getMessage());
            ex4.initCause(ex3);
            throw ex4;
        }
        return encode;
    }
    
    @Override
    public final int getWrapSizeLimit(final int n, final boolean b, final int n2) throws GSSException {
        int n3 = 0;
        if (this.cipherHelper.getProto() == 0) {
            n3 = WrapToken.getSizeLimit(n, b, n2, this.getCipherHelper(null));
        }
        else if (this.cipherHelper.getProto() == 1) {
            n3 = WrapToken_v2.getSizeLimit(n, b, n2, this.getCipherHelper(null));
        }
        return n3;
    }
    
    @Override
    public final byte[] wrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (Krb5Context.DEBUG) {
            System.out.println("Krb5Context.wrap: data=[" + getHexBytes(array, n, n2) + "]");
        }
        if (this.state != 3) {
            throw new GSSException(12, -1, "Wrap called in invalid state!");
        }
        byte[] array2 = null;
        try {
            if (this.cipherHelper.getProto() == 0) {
                array2 = new WrapToken(this, messageProp, array, n, n2).encode();
            }
            else if (this.cipherHelper.getProto() == 1) {
                array2 = new WrapToken_v2(this, messageProp, array, n, n2).encode();
            }
            if (Krb5Context.DEBUG) {
                System.out.println("Krb5Context.wrap: token=[" + getHexBytes(array2, 0, array2.length) + "]");
            }
            return array2;
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    public final int wrap(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final MessageProp messageProp) throws GSSException {
        if (this.state != 3) {
            throw new GSSException(12, -1, "Wrap called in invalid state!");
        }
        int n4 = 0;
        try {
            if (this.cipherHelper.getProto() == 0) {
                n4 = new WrapToken(this, messageProp, array, n, n2).encode(array2, n3);
            }
            else if (this.cipherHelper.getProto() == 1) {
                n4 = new WrapToken_v2(this, messageProp, array, n, n2).encode(array2, n3);
            }
            if (Krb5Context.DEBUG) {
                System.out.println("Krb5Context.wrap: token=[" + getHexBytes(array2, n3, n4) + "]");
            }
            return n4;
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    public final void wrap(final byte[] array, final int n, final int n2, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.state != 3) {
            throw new GSSException(12, -1, "Wrap called in invalid state!");
        }
        byte[] array2 = null;
        try {
            if (this.cipherHelper.getProto() == 0) {
                final WrapToken wrapToken = new WrapToken(this, messageProp, array, n, n2);
                wrapToken.encode(outputStream);
                if (Krb5Context.DEBUG) {
                    array2 = wrapToken.encode();
                }
            }
            else if (this.cipherHelper.getProto() == 1) {
                final WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, messageProp, array, n, n2);
                wrapToken_v2.encode(outputStream);
                if (Krb5Context.DEBUG) {
                    array2 = wrapToken_v2.encode();
                }
            }
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        if (Krb5Context.DEBUG) {
            System.out.println("Krb5Context.wrap: token=[" + getHexBytes(array2, 0, array2.length) + "]");
        }
    }
    
    @Override
    public final void wrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        byte[] array;
        try {
            array = new byte[inputStream.available()];
            inputStream.read(array);
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        this.wrap(array, 0, array.length, outputStream, messageProp);
    }
    
    @Override
    public final byte[] unwrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (Krb5Context.DEBUG) {
            System.out.println("Krb5Context.unwrap: token=[" + getHexBytes(array, n, n2) + "]");
        }
        if (this.state != 3) {
            throw new GSSException(12, -1, " Unwrap called in invalid state!");
        }
        byte[] array2 = null;
        if (this.cipherHelper.getProto() == 0) {
            final WrapToken wrapToken = new WrapToken(this, array, n, n2, messageProp);
            array2 = wrapToken.getData();
            this.setSequencingAndReplayProps(wrapToken, messageProp);
        }
        else if (this.cipherHelper.getProto() == 1) {
            final WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, array, n, n2, messageProp);
            array2 = wrapToken_v2.getData();
            this.setSequencingAndReplayProps(wrapToken_v2, messageProp);
        }
        if (Krb5Context.DEBUG) {
            System.out.println("Krb5Context.unwrap: data=[" + getHexBytes(array2, 0, array2.length) + "]");
        }
        return array2;
    }
    
    public final int unwrap(final byte[] array, final int n, int n2, final byte[] array2, final int n3, final MessageProp messageProp) throws GSSException {
        if (this.state != 3) {
            throw new GSSException(12, -1, "Unwrap called in invalid state!");
        }
        if (this.cipherHelper.getProto() == 0) {
            final WrapToken wrapToken = new WrapToken(this, array, n, n2, messageProp);
            n2 = wrapToken.getData(array2, n3);
            this.setSequencingAndReplayProps(wrapToken, messageProp);
        }
        else if (this.cipherHelper.getProto() == 1) {
            final WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, array, n, n2, messageProp);
            n2 = wrapToken_v2.getData(array2, n3);
            this.setSequencingAndReplayProps(wrapToken_v2, messageProp);
        }
        return n2;
    }
    
    public final int unwrap(final InputStream inputStream, final byte[] array, final int n, final MessageProp messageProp) throws GSSException {
        if (this.state != 3) {
            throw new GSSException(12, -1, "Unwrap called in invalid state!");
        }
        int n2 = 0;
        if (this.cipherHelper.getProto() == 0) {
            final WrapToken wrapToken = new WrapToken(this, inputStream, messageProp);
            n2 = wrapToken.getData(array, n);
            this.setSequencingAndReplayProps(wrapToken, messageProp);
        }
        else if (this.cipherHelper.getProto() == 1) {
            final WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, inputStream, messageProp);
            n2 = wrapToken_v2.getData(array, n);
            this.setSequencingAndReplayProps(wrapToken_v2, messageProp);
        }
        return n2;
    }
    
    @Override
    public final void unwrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.state != 3) {
            throw new GSSException(12, -1, "Unwrap called in invalid state!");
        }
        byte[] array = null;
        if (this.cipherHelper.getProto() == 0) {
            final WrapToken wrapToken = new WrapToken(this, inputStream, messageProp);
            array = wrapToken.getData();
            this.setSequencingAndReplayProps(wrapToken, messageProp);
        }
        else if (this.cipherHelper.getProto() == 1) {
            final WrapToken_v2 wrapToken_v2 = new WrapToken_v2(this, inputStream, messageProp);
            array = wrapToken_v2.getData();
            this.setSequencingAndReplayProps(wrapToken_v2, messageProp);
        }
        try {
            outputStream.write(array);
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public final byte[] getMIC(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        byte[] array2 = null;
        try {
            if (this.cipherHelper.getProto() == 0) {
                array2 = new MicToken(this, messageProp, array, n, n2).encode();
            }
            else if (this.cipherHelper.getProto() == 1) {
                array2 = new MicToken_v2(this, messageProp, array, n, n2).encode();
            }
            return array2;
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private int getMIC(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final MessageProp messageProp) throws GSSException {
        int n4 = 0;
        try {
            if (this.cipherHelper.getProto() == 0) {
                n4 = new MicToken(this, messageProp, array, n, n2).encode(array2, n3);
            }
            else if (this.cipherHelper.getProto() == 1) {
                n4 = new MicToken_v2(this, messageProp, array, n, n2).encode(array2, n3);
            }
            return n4;
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void getMIC(final byte[] array, final int n, final int n2, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        try {
            if (this.cipherHelper.getProto() == 0) {
                new MicToken(this, messageProp, array, n, n2).encode(outputStream);
            }
            else if (this.cipherHelper.getProto() == 1) {
                new MicToken_v2(this, messageProp, array, n, n2).encode(outputStream);
            }
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public final void getMIC(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        byte[] array;
        try {
            array = new byte[inputStream.available()];
            inputStream.read(array);
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        this.getMIC(array, 0, array.length, outputStream, messageProp);
    }
    
    @Override
    public final void verifyMIC(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4, final MessageProp messageProp) throws GSSException {
        if (this.cipherHelper.getProto() == 0) {
            final MicToken micToken = new MicToken(this, array, n, n2, messageProp);
            micToken.verify(array2, n3, n4);
            this.setSequencingAndReplayProps(micToken, messageProp);
        }
        else if (this.cipherHelper.getProto() == 1) {
            final MicToken_v2 micToken_v2 = new MicToken_v2(this, array, n, n2, messageProp);
            micToken_v2.verify(array2, n3, n4);
            this.setSequencingAndReplayProps(micToken_v2, messageProp);
        }
    }
    
    private void verifyMIC(final InputStream inputStream, final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (this.cipherHelper.getProto() == 0) {
            final MicToken micToken = new MicToken(this, inputStream, messageProp);
            micToken.verify(array, n, n2);
            this.setSequencingAndReplayProps(micToken, messageProp);
        }
        else if (this.cipherHelper.getProto() == 1) {
            final MicToken_v2 micToken_v2 = new MicToken_v2(this, inputStream, messageProp);
            micToken_v2.verify(array, n, n2);
            this.setSequencingAndReplayProps(micToken_v2, messageProp);
        }
    }
    
    @Override
    public final void verifyMIC(final InputStream inputStream, final InputStream inputStream2, final MessageProp messageProp) throws GSSException {
        byte[] array;
        try {
            array = new byte[inputStream2.available()];
            inputStream2.read(array);
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        this.verifyMIC(inputStream, array, 0, array.length, messageProp);
    }
    
    @Override
    public final byte[] export() throws GSSException {
        throw new GSSException(16, -1, "GSS Export Context not available");
    }
    
    @Override
    public final void dispose() throws GSSException {
        this.state = 4;
        this.delegatedCred = null;
    }
    
    @Override
    public final Provider getProvider() {
        return Krb5MechFactory.PROVIDER;
    }
    
    private void setSequencingAndReplayProps(final MessageToken messageToken, final MessageProp messageProp) {
        if (this.replayDetState || this.sequenceDetState) {
            this.peerTokenTracker.getProps(messageToken.getSequenceNumber(), messageProp);
        }
    }
    
    private void setSequencingAndReplayProps(final MessageToken_v2 messageToken_v2, final MessageProp messageProp) {
        if (this.replayDetState || this.sequenceDetState) {
            this.peerTokenTracker.getProps(messageToken_v2.getSequenceNumber(), messageProp);
        }
    }
    
    private void checkPermission(final String s, final String s2) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new ServicePermission(s, s2));
        }
    }
    
    private static String getHexBytes(final byte[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n2; ++i) {
            final int n3 = array[i] >> 4 & 0xF;
            final int n4 = array[i] & 0xF;
            sb.append(Integer.toHexString(n3));
            sb.append(Integer.toHexString(n4));
            sb.append(' ');
        }
        return sb.toString();
    }
    
    private static String printState(final int n) {
        switch (n) {
            case 1: {
                return "STATE_NEW";
            }
            case 2: {
                return "STATE_IN_PROCESS";
            }
            case 3: {
                return "STATE_DONE";
            }
            case 4: {
                return "STATE_DELETED";
            }
            default: {
                return "Unknown state " + n;
            }
        }
    }
    
    GSSCaller getCaller() {
        return this.caller;
    }
    
    @Override
    public Object inquireSecContext(final InquireType inquireType) throws GSSException {
        if (!this.isEstablished()) {
            throw new GSSException(12, -1, "Security context not established.");
        }
        switch (inquireType) {
            case KRB5_GET_SESSION_KEY: {
                return new KerberosSessionKey(this.key);
            }
            case KRB5_GET_TKT_FLAGS: {
                return this.tktFlags.clone();
            }
            case KRB5_GET_AUTHZ_DATA: {
                if (this.isInitiator()) {
                    throw new GSSException(16, -1, "AuthzData not available on initiator side.");
                }
                return (this.authzData == null) ? null : this.authzData.clone();
            }
            case KRB5_GET_AUTHTIME: {
                return this.authTime;
            }
            default: {
                throw new GSSException(16, -1, "Inquire type not supported.");
            }
        }
    }
    
    public void setTktFlags(final boolean[] tktFlags) {
        this.tktFlags = tktFlags;
    }
    
    public void setAuthTime(final String authTime) {
        this.authTime = authTime;
    }
    
    public void setAuthzData(final AuthorizationDataEntry[] authzData) {
        this.authzData = authzData;
    }
    
    static {
        DEBUG = Krb5Util.DEBUG;
    }
    
    static class KerberosSessionKey implements Key
    {
        private static final long serialVersionUID = 699307378954123869L;
        private final EncryptionKey key;
        
        KerberosSessionKey(final EncryptionKey key) {
            this.key = key;
        }
        
        @Override
        public String getAlgorithm() {
            return Integer.toString(this.key.getEType());
        }
        
        @Override
        public String getFormat() {
            return "RAW";
        }
        
        @Override
        public byte[] getEncoded() {
            return this.key.getBytes().clone();
        }
        
        @Override
        public String toString() {
            return "Kerberos session key: etype: " + this.key.getEType() + "\n" + new HexDumpEncoder().encodeBuffer(this.key.getBytes());
        }
    }
}

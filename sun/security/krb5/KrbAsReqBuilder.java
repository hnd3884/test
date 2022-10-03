package sun.security.krb5;

import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import java.util.Arrays;
import sun.security.jgss.krb5.Krb5Util;
import java.io.IOException;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.PAData;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KDCOptions;

public final class KrbAsReqBuilder
{
    private KDCOptions options;
    private PrincipalName cname;
    private PrincipalName refCname;
    private PrincipalName sname;
    private KerberosTime from;
    private KerberosTime till;
    private KerberosTime rtime;
    private HostAddresses addresses;
    private final char[] password;
    private final KeyTab ktab;
    private PAData[] paList;
    private KrbAsReq req;
    private KrbAsRep rep;
    private State state;
    
    private void init(final PrincipalName principalName) throws KrbException {
        this.cname = principalName;
        this.refCname = principalName;
        this.state = State.INIT;
    }
    
    public KrbAsReqBuilder(final PrincipalName principalName, final KeyTab ktab) throws KrbException {
        this.init(principalName);
        this.ktab = ktab;
        this.password = null;
    }
    
    public KrbAsReqBuilder(final PrincipalName principalName, final char[] array) throws KrbException {
        this.init(principalName);
        this.password = array.clone();
        this.ktab = null;
    }
    
    public EncryptionKey[] getKeys(final boolean b) throws KrbException {
        this.checkState(b ? State.REQ_OK : State.INIT, "Cannot get keys");
        if (this.password != null) {
            final int[] defaults = EType.getDefaults("default_tkt_enctypes");
            final EncryptionKey[] array = new EncryptionKey[defaults.length];
            String s = null;
            try {
                for (int i = 0; i < defaults.length; ++i) {
                    final PAData.SaltAndParams saltAndParams = PAData.getSaltAndParams(defaults[i], this.paList);
                    if (saltAndParams != null) {
                        if (defaults[i] != 23 && saltAndParams.salt != null) {
                            s = saltAndParams.salt;
                        }
                        array[i] = EncryptionKey.acquireSecretKey(this.cname, this.password, defaults[i], saltAndParams);
                    }
                }
                if (s == null) {
                    s = this.cname.getSalt();
                }
                for (int j = 0; j < defaults.length; ++j) {
                    if (array[j] == null) {
                        array[j] = EncryptionKey.acquireSecretKey(this.password, s, defaults[j], null);
                    }
                }
            }
            catch (final IOException ex) {
                final KrbException ex2 = new KrbException(909);
                ex2.initCause(ex);
                throw ex2;
            }
            return array;
        }
        throw new IllegalStateException("Required password not provided");
    }
    
    public void setOptions(final KDCOptions options) {
        this.checkState(State.INIT, "Cannot specify options");
        this.options = options;
    }
    
    public void setTill(final KerberosTime till) {
        this.checkState(State.INIT, "Cannot specify till");
        this.till = till;
    }
    
    public void setRTime(final KerberosTime rtime) {
        this.checkState(State.INIT, "Cannot specify rtime");
        this.rtime = rtime;
    }
    
    public void setTarget(final PrincipalName sname) {
        this.checkState(State.INIT, "Cannot specify target");
        this.sname = sname;
    }
    
    public void setAddresses(final HostAddresses addresses) {
        this.checkState(State.INIT, "Cannot specify addresses");
        this.addresses = addresses;
    }
    
    private KrbAsReq build(final EncryptionKey encryptionKey, final ReferralsState referralsState) throws KrbException, IOException {
        PAData[] array = null;
        int[] array2;
        if (this.password != null) {
            array2 = EType.getDefaults("default_tkt_enctypes");
        }
        else {
            final EncryptionKey[] keysFromJavaxKeyTab = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
            array2 = EType.getDefaults("default_tkt_enctypes", keysFromJavaxKeyTab);
            final EncryptionKey[] array3 = keysFromJavaxKeyTab;
            for (int length = array3.length, i = 0; i < length; ++i) {
                array3[i].destroy();
            }
        }
        this.options = ((this.options == null) ? new KDCOptions() : this.options);
        if (referralsState.isEnabled()) {
            if (referralsState.sendCanonicalize()) {
                this.options.set(15, true);
            }
            array = new PAData[] { new PAData(149, new byte[0]) };
        }
        else {
            this.options.set(15, false);
        }
        return new KrbAsReq(encryptionKey, this.options, this.refCname, this.sname, this.from, this.till, this.rtime, array2, this.addresses, array);
    }
    
    private KrbAsReqBuilder resolve() throws KrbException, Asn1Exception, IOException {
        if (this.ktab != null) {
            this.rep.decryptUsingKeyTab(this.ktab, this.req, this.cname);
        }
        else {
            this.rep.decryptUsingPassword(this.password, this.req, this.cname);
        }
        if (this.rep.getPA() != null) {
            if (this.paList == null || this.paList.length == 0) {
                this.paList = this.rep.getPA();
            }
            else {
                final int length = this.rep.getPA().length;
                if (length > 0) {
                    final int length2 = this.paList.length;
                    this.paList = Arrays.copyOf(this.paList, this.paList.length + length);
                    System.arraycopy(this.rep.getPA(), 0, this.paList, length2, length);
                }
            }
        }
        return this;
    }
    
    private KrbAsReqBuilder send() throws KrbException, IOException {
        int n = 0;
        KdcComm kdcComm = null;
        EncryptionKey encryptionKey = null;
        final ReferralsState referralsState = new ReferralsState(this);
        while (true) {
            if (referralsState.refreshComm()) {
                kdcComm = new KdcComm(this.refCname.getRealmAsString());
            }
            try {
                this.req = this.build(encryptionKey, referralsState);
                this.rep = new KrbAsRep(kdcComm.send(this.req.encoding()));
                return this;
            }
            catch (final KrbException ex) {
                if (n == 0 && (ex.returnCode() == 24 || ex.returnCode() == 25)) {
                    if (Krb5.DEBUG) {
                        System.out.println("KrbAsReqBuilder: PREAUTH FAILED/REQ, re-send AS-REQ");
                    }
                    n = 1;
                    final KRBError error = ex.getError();
                    final int preferredEType = PAData.getPreferredEType(error.getPA(), EType.getDefaults("default_tkt_enctypes")[0]);
                    if (this.password == null) {
                        final EncryptionKey[] keysFromJavaxKeyTab = Krb5Util.keysFromJavaxKeyTab(this.ktab, this.cname);
                        encryptionKey = EncryptionKey.findKey(preferredEType, keysFromJavaxKeyTab);
                        if (encryptionKey != null) {
                            encryptionKey = (EncryptionKey)encryptionKey.clone();
                        }
                        final EncryptionKey[] array = keysFromJavaxKeyTab;
                        for (int length = array.length, i = 0; i < length; ++i) {
                            array[i].destroy();
                        }
                    }
                    else {
                        encryptionKey = EncryptionKey.acquireSecretKey(this.cname, this.password, preferredEType, PAData.getSaltAndParams(preferredEType, error.getPA()));
                    }
                    this.paList = error.getPA();
                }
                else {
                    if (!referralsState.handleError(ex)) {
                        throw ex;
                    }
                    encryptionKey = null;
                    n = 0;
                }
            }
        }
    }
    
    public KrbAsReqBuilder action() throws KrbException, Asn1Exception, IOException {
        this.checkState(State.INIT, "Cannot call action");
        this.state = State.REQ_OK;
        return this.send().resolve();
    }
    
    public Credentials getCreds() {
        this.checkState(State.REQ_OK, "Cannot retrieve creds");
        return this.rep.getCreds();
    }
    
    public sun.security.krb5.internal.ccache.Credentials getCCreds() {
        this.checkState(State.REQ_OK, "Cannot retrieve CCreds");
        return this.rep.getCCreds();
    }
    
    public void destroy() {
        this.state = State.DESTROYED;
        if (this.password != null) {
            Arrays.fill(this.password, '\0');
        }
    }
    
    private void checkState(final State state, final String s) {
        if (this.state != state) {
            throw new IllegalStateException(s + " at " + state + " state");
        }
    }
    
    private enum State
    {
        INIT, 
        REQ_OK, 
        DESTROYED;
    }
    
    static final class ReferralsState
    {
        private static boolean canonicalizeConfig;
        private boolean enabled;
        private boolean sendCanonicalize;
        private boolean isEnterpriseCname;
        private int count;
        private boolean refreshComm;
        private KrbAsReqBuilder reqBuilder;
        
        static void initStatic() {
            ReferralsState.canonicalizeConfig = false;
            try {
                ReferralsState.canonicalizeConfig = (Config.getInstance().getBooleanObject("libdefaults", "canonicalize") == Boolean.TRUE);
            }
            catch (final KrbException ex) {
                if (Krb5.DEBUG) {
                    System.out.println("Exception in getting canonicalize, using default value " + (Object)ReferralsState.canonicalizeConfig + ": " + ex.getMessage());
                }
            }
        }
        
        ReferralsState(final KrbAsReqBuilder reqBuilder) throws KrbException {
            this.reqBuilder = reqBuilder;
            this.sendCanonicalize = ReferralsState.canonicalizeConfig;
            this.isEnterpriseCname = (reqBuilder.refCname.getNameType() == 10);
            this.updateStatus();
            if (!this.enabled && this.isEnterpriseCname) {
                throw new KrbException("NT-ENTERPRISE principals only allowed when referrals are enabled.");
            }
            this.refreshComm = true;
        }
        
        private void updateStatus() {
            this.enabled = (!Config.DISABLE_REFERRALS && (this.isEnterpriseCname || this.sendCanonicalize));
        }
        
        boolean handleError(final KrbException ex) throws RealmException {
            if (this.enabled) {
                if (ex.returnCode() == 68) {
                    final Realm clientRealm = ex.getError().getClientRealm();
                    if (clientRealm != null && !clientRealm.toString().isEmpty() && this.count < Config.MAX_REFERRALS) {
                        this.reqBuilder.refCname = new PrincipalName(this.reqBuilder.refCname.getNameType(), this.reqBuilder.refCname.getNameStrings(), clientRealm);
                        this.refreshComm = true;
                        ++this.count;
                        return true;
                    }
                }
                if (this.count < Config.MAX_REFERRALS && this.sendCanonicalize) {
                    if (Krb5.DEBUG) {
                        System.out.println("KrbAsReqBuilder: AS-REQ failed. Retrying with CANONICALIZE false.");
                    }
                    this.sendCanonicalize = false;
                    this.updateStatus();
                    return true;
                }
            }
            return false;
        }
        
        boolean refreshComm() {
            final boolean refreshComm = this.refreshComm;
            this.refreshComm = false;
            return refreshComm;
        }
        
        boolean isEnabled() {
            return this.enabled;
        }
        
        boolean sendCanonicalize() {
            return this.sendCanonicalize;
        }
        
        static {
            initStatic();
        }
    }
}

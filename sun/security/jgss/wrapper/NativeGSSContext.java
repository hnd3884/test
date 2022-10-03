package sun.security.jgss.wrapper;

import com.sun.security.jgss.InquireType;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import org.ietf.jgss.MessageProp;
import java.security.Provider;
import sun.security.util.DerValue;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import sun.security.util.ObjectIdentifier;
import java.security.Permission;
import javax.security.auth.kerberos.DelegationPermission;
import sun.security.jgss.GSSUtil;
import org.ietf.jgss.GSSException;
import sun.security.jgss.spnego.NegTokenTarg;
import sun.security.jgss.spnego.NegTokenInit;
import java.io.IOException;
import sun.security.jgss.GSSExceptionImpl;
import java.io.InputStream;
import sun.security.jgss.GSSHeader;
import java.io.ByteArrayInputStream;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSContextSpi;

class NativeGSSContext implements GSSContextSpi
{
    private static final int GSS_C_DELEG_FLAG = 1;
    private static final int GSS_C_MUTUAL_FLAG = 2;
    private static final int GSS_C_REPLAY_FLAG = 4;
    private static final int GSS_C_SEQUENCE_FLAG = 8;
    private static final int GSS_C_CONF_FLAG = 16;
    private static final int GSS_C_INTEG_FLAG = 32;
    private static final int GSS_C_ANON_FLAG = 64;
    private static final int GSS_C_PROT_READY_FLAG = 128;
    private static final int GSS_C_TRANS_FLAG = 256;
    private static final int NUM_OF_INQUIRE_VALUES = 6;
    private long pContext;
    private GSSNameElement srcName;
    private GSSNameElement targetName;
    private GSSCredElement cred;
    private boolean isInitiator;
    private boolean isEstablished;
    private Oid actualMech;
    private ChannelBinding cb;
    private GSSCredElement delegatedCred;
    private int flags;
    private int lifetime;
    private final GSSLibStub cStub;
    private boolean skipDelegPermCheck;
    private boolean skipServicePermCheck;
    
    private static Oid getMechFromSpNegoToken(final byte[] array, final boolean b) throws GSSException {
        Oid supportedMech = null;
        if (b) {
            GSSHeader gssHeader;
            try {
                gssHeader = new GSSHeader(new ByteArrayInputStream(array));
            }
            catch (final IOException ex) {
                throw new GSSExceptionImpl(11, ex);
            }
            final int mechTokenLength = gssHeader.getMechTokenLength();
            final byte[] array2 = new byte[mechTokenLength];
            System.arraycopy(array, array.length - mechTokenLength, array2, 0, array2.length);
            final NegTokenInit negTokenInit = new NegTokenInit(array2);
            if (negTokenInit.getMechToken() != null) {
                supportedMech = negTokenInit.getMechTypeList()[0];
            }
        }
        else {
            supportedMech = new NegTokenTarg(array).getSupportedMech();
        }
        return supportedMech;
    }
    
    private void doServicePermCheck() throws GSSException {
        if (System.getSecurityManager() != null) {
            final String s = this.isInitiator ? "initiate" : "accept";
            if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && this.isInitiator && !this.isEstablished) {
                if (this.srcName == null) {
                    new GSSCredElement(null, this.lifetime, 1, GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID)).dispose();
                }
                else {
                    Krb5Util.checkServicePermission(Krb5Util.getTGSName(this.srcName), s);
                }
            }
            Krb5Util.checkServicePermission(this.targetName.getKrbName(), s);
            this.skipServicePermCheck = true;
        }
    }
    
    private void doDelegPermCheck() throws GSSException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final String krbName = this.targetName.getKrbName();
            final String tgsName = Krb5Util.getTGSName(this.targetName);
            final StringBuffer sb = new StringBuffer("\"");
            sb.append(krbName).append("\" \"");
            sb.append(tgsName).append('\"');
            final String string = sb.toString();
            SunNativeProvider.debug("Checking DelegationPermission (" + string + ")");
            securityManager.checkPermission(new DelegationPermission(string));
            this.skipDelegPermCheck = true;
        }
    }
    
    private byte[] retrieveToken(final InputStream inputStream, final int n) throws GSSException {
        try {
            byte[] array2;
            if (n != -1) {
                SunNativeProvider.debug("Precomputed mechToken length: " + n);
                final GSSHeader gssHeader = new GSSHeader(new ObjectIdentifier(this.cStub.getMech().toString()), n);
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(600);
                final byte[] array = new byte[n];
                final int read = inputStream.read(array);
                assert n == read;
                gssHeader.encode(byteArrayOutputStream);
                byteArrayOutputStream.write(array);
                array2 = byteArrayOutputStream.toByteArray();
            }
            else {
                assert n == -1;
                array2 = new DerValue(inputStream).toByteArray();
            }
            SunNativeProvider.debug("Complete Token length: " + array2.length);
            return array2;
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
    }
    
    NativeGSSContext(final GSSNameElement targetName, final GSSCredElement cred, final int lifetime, final GSSLibStub cStub) throws GSSException {
        this.pContext = 0L;
        this.lifetime = 0;
        if (targetName == null) {
            throw new GSSException(11, 1, "null peer");
        }
        this.cStub = cStub;
        this.cred = cred;
        this.targetName = targetName;
        this.isInitiator = true;
        this.lifetime = lifetime;
        if (GSSUtil.isKerberosMech(this.cStub.getMech())) {
            this.doServicePermCheck();
            if (this.cred == null) {
                this.cred = new GSSCredElement(null, this.lifetime, 1, this.cStub);
            }
            this.srcName = this.cred.getName();
        }
    }
    
    NativeGSSContext(final GSSCredElement cred, final GSSLibStub cStub) throws GSSException {
        this.pContext = 0L;
        this.lifetime = 0;
        this.cStub = cStub;
        this.cred = cred;
        if (this.cred != null) {
            this.targetName = this.cred.getName();
        }
        this.isInitiator = false;
        if (GSSUtil.isKerberosMech(this.cStub.getMech()) && this.targetName != null) {
            this.doServicePermCheck();
        }
    }
    
    NativeGSSContext(final long pContext, final GSSLibStub cStub) throws GSSException {
        this.pContext = 0L;
        this.lifetime = 0;
        assert this.pContext != 0L;
        this.pContext = pContext;
        this.cStub = cStub;
        final long[] inquireContext = this.cStub.inquireContext(this.pContext);
        if (inquireContext.length != 6) {
            throw new RuntimeException("Bug w/ GSSLibStub.inquireContext()");
        }
        this.srcName = new GSSNameElement(inquireContext[0], this.cStub);
        this.targetName = new GSSNameElement(inquireContext[1], this.cStub);
        this.isInitiator = (inquireContext[2] != 0L);
        this.isEstablished = (inquireContext[3] != 0L);
        this.flags = (int)inquireContext[4];
        this.lifetime = (int)inquireContext[5];
        final Oid mech = this.cStub.getMech();
        if (GSSUtil.isSpNegoMech(mech) || GSSUtil.isKerberosMech(mech)) {
            this.doServicePermCheck();
        }
    }
    
    @Override
    public Provider getProvider() {
        return SunNativeProvider.INSTANCE;
    }
    
    @Override
    public byte[] initSecContext(final InputStream inputStream, final int n) throws GSSException {
        byte[] initContext = null;
        if (!this.isEstablished && this.isInitiator) {
            byte[] retrieveToken = null;
            if (this.pContext != 0L) {
                retrieveToken = this.retrieveToken(inputStream, n);
                SunNativeProvider.debug("initSecContext=> inToken len=" + retrieveToken.length);
            }
            if (!this.getCredDelegState()) {
                this.skipDelegPermCheck = true;
            }
            if (GSSUtil.isKerberosMech(this.cStub.getMech()) && !this.skipDelegPermCheck) {
                this.doDelegPermCheck();
            }
            initContext = this.cStub.initContext((this.cred == null) ? 0L : this.cred.pCred, this.targetName.pName, this.cb, retrieveToken, this);
            SunNativeProvider.debug("initSecContext=> outToken len=" + ((initContext == null) ? 0 : initContext.length));
            if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && initContext != null) {
                this.actualMech = getMechFromSpNegoToken(initContext, true);
                if (GSSUtil.isKerberosMech(this.actualMech)) {
                    if (!this.skipServicePermCheck) {
                        this.doServicePermCheck();
                    }
                    if (!this.skipDelegPermCheck) {
                        this.doDelegPermCheck();
                    }
                }
            }
            if (this.isEstablished) {
                if (this.srcName == null) {
                    this.srcName = new GSSNameElement(this.cStub.getContextName(this.pContext, true), this.cStub);
                }
                if (this.cred == null) {
                    this.cred = new GSSCredElement(this.srcName, this.lifetime, 1, this.cStub);
                }
            }
        }
        return initContext;
    }
    
    @Override
    public byte[] acceptSecContext(final InputStream inputStream, final int n) throws GSSException {
        byte[] acceptContext = null;
        if (!this.isEstablished && !this.isInitiator) {
            final byte[] retrieveToken = this.retrieveToken(inputStream, n);
            SunNativeProvider.debug("acceptSecContext=> inToken len=" + retrieveToken.length);
            acceptContext = this.cStub.acceptContext((this.cred == null) ? 0L : this.cred.pCred, this.cb, retrieveToken, this);
            SunNativeProvider.debug("acceptSecContext=> outToken len=" + ((acceptContext == null) ? 0 : acceptContext.length));
            if (this.targetName == null) {
                this.targetName = new GSSNameElement(this.cStub.getContextName(this.pContext, false), this.cStub);
                if (this.cred != null) {
                    this.cred.dispose();
                }
                this.cred = new GSSCredElement(this.targetName, this.lifetime, 2, this.cStub);
            }
            if (GSSUtil.isSpNegoMech(this.cStub.getMech()) && acceptContext != null && !this.skipServicePermCheck && GSSUtil.isKerberosMech(getMechFromSpNegoToken(acceptContext, false))) {
                this.doServicePermCheck();
            }
        }
        return acceptContext;
    }
    
    @Override
    public boolean isEstablished() {
        return this.isEstablished;
    }
    
    @Override
    public void dispose() throws GSSException {
        this.srcName = null;
        this.targetName = null;
        this.cred = null;
        this.delegatedCred = null;
        if (this.pContext != 0L) {
            this.pContext = this.cStub.deleteContext(this.pContext);
            this.pContext = 0L;
        }
    }
    
    @Override
    public int getWrapSizeLimit(final int n, final boolean b, final int n2) throws GSSException {
        return this.cStub.wrapSizeLimit(this.pContext, b ? 1 : 0, n, n2);
    }
    
    @Override
    public byte[] wrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        byte[] array2 = array;
        if (n != 0 || n2 != array.length) {
            array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
        }
        return this.cStub.wrap(this.pContext, array2, messageProp);
    }
    
    public void wrap(final byte[] array, final int n, final int n2, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        try {
            outputStream.write(this.wrap(array, n, n2, messageProp));
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
    }
    
    public int wrap(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final MessageProp messageProp) throws GSSException {
        final byte[] wrap = this.wrap(array, n, n2, messageProp);
        System.arraycopy(wrap, 0, array2, n3, wrap.length);
        return wrap.length;
    }
    
    @Override
    public void wrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        try {
            final byte[] array = new byte[inputStream.available()];
            outputStream.write(this.wrap(array, 0, inputStream.read(array), messageProp));
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
    }
    
    @Override
    public byte[] unwrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (n != 0 || n2 != array.length) {
            final byte[] array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
            return this.cStub.unwrap(this.pContext, array2, messageProp);
        }
        return this.cStub.unwrap(this.pContext, array, messageProp);
    }
    
    public int unwrap(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final MessageProp messageProp) throws GSSException {
        byte[] array4;
        if (n != 0 || n2 != array.length) {
            final byte[] array3 = new byte[n2];
            System.arraycopy(array, n, array3, 0, n2);
            array4 = this.cStub.unwrap(this.pContext, array3, messageProp);
        }
        else {
            array4 = this.cStub.unwrap(this.pContext, array, messageProp);
        }
        System.arraycopy(array4, 0, array2, n3, array4.length);
        return array4.length;
    }
    
    @Override
    public void unwrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        try {
            final byte[] array = new byte[inputStream.available()];
            outputStream.write(this.unwrap(array, 0, inputStream.read(array), messageProp));
            outputStream.flush();
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
    }
    
    public int unwrap(final InputStream inputStream, final byte[] array, final int n, final MessageProp messageProp) throws GSSException {
        byte[] array2;
        int read;
        try {
            array2 = new byte[inputStream.available()];
            read = inputStream.read(array2);
            this.unwrap(array2, 0, read, messageProp);
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
        final byte[] unwrap = this.unwrap(array2, 0, read, messageProp);
        System.arraycopy(unwrap, 0, array, n, unwrap.length);
        return unwrap.length;
    }
    
    @Override
    public byte[] getMIC(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        final int n3 = (messageProp == null) ? 0 : messageProp.getQOP();
        byte[] array2 = array;
        if (n != 0 || n2 != array.length) {
            array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
        }
        return this.cStub.getMic(this.pContext, n3, array2);
    }
    
    @Override
    public void getMIC(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        try {
            final byte[] array = new byte[inputStream.available()];
            final byte[] mic = this.getMIC(array, 0, inputStream.read(array), messageProp);
            if (mic != null && mic.length != 0) {
                outputStream.write(mic);
            }
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
    }
    
    @Override
    public void verifyMIC(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4, final MessageProp messageProp) throws GSSException {
        byte[] array3 = array;
        byte[] array4 = array2;
        if (n != 0 || n2 != array.length) {
            array3 = new byte[n2];
            System.arraycopy(array, n, array3, 0, n2);
        }
        if (n3 != 0 || n4 != array2.length) {
            array4 = new byte[n4];
            System.arraycopy(array2, n3, array4, 0, n4);
        }
        this.cStub.verifyMic(this.pContext, array3, array4, messageProp);
    }
    
    @Override
    public void verifyMIC(final InputStream inputStream, final InputStream inputStream2, final MessageProp messageProp) throws GSSException {
        try {
            final byte[] array = new byte[inputStream2.available()];
            final int read = inputStream2.read(array);
            final byte[] array2 = new byte[inputStream.available()];
            this.verifyMIC(array2, 0, inputStream.read(array2), array, 0, read, messageProp);
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
    }
    
    @Override
    public byte[] export() throws GSSException {
        final byte[] exportContext = this.cStub.exportContext(this.pContext);
        this.pContext = 0L;
        return exportContext;
    }
    
    private void changeFlags(final int n, final boolean b) {
        if (this.isInitiator && this.pContext == 0L) {
            if (b) {
                this.flags |= n;
            }
            else {
                this.flags &= ~n;
            }
        }
    }
    
    @Override
    public void requestMutualAuth(final boolean b) throws GSSException {
        this.changeFlags(2, b);
    }
    
    @Override
    public void requestReplayDet(final boolean b) throws GSSException {
        this.changeFlags(4, b);
    }
    
    @Override
    public void requestSequenceDet(final boolean b) throws GSSException {
        this.changeFlags(8, b);
    }
    
    @Override
    public void requestCredDeleg(final boolean b) throws GSSException {
        this.changeFlags(1, b);
    }
    
    @Override
    public void requestAnonymity(final boolean b) throws GSSException {
        this.changeFlags(64, b);
    }
    
    @Override
    public void requestConf(final boolean b) throws GSSException {
        this.changeFlags(16, b);
    }
    
    @Override
    public void requestInteg(final boolean b) throws GSSException {
        this.changeFlags(32, b);
    }
    
    @Override
    public void requestDelegPolicy(final boolean b) throws GSSException {
    }
    
    @Override
    public void requestLifetime(final int lifetime) throws GSSException {
        if (this.isInitiator && this.pContext == 0L) {
            this.lifetime = lifetime;
        }
    }
    
    @Override
    public void setChannelBinding(final ChannelBinding cb) throws GSSException {
        if (this.pContext == 0L) {
            this.cb = cb;
        }
    }
    
    private boolean checkFlags(final int n) {
        return (this.flags & n) != 0x0;
    }
    
    @Override
    public boolean getCredDelegState() {
        return this.checkFlags(1);
    }
    
    @Override
    public boolean getMutualAuthState() {
        return this.checkFlags(2);
    }
    
    @Override
    public boolean getReplayDetState() {
        return this.checkFlags(4);
    }
    
    @Override
    public boolean getSequenceDetState() {
        return this.checkFlags(8);
    }
    
    @Override
    public boolean getAnonymityState() {
        return this.checkFlags(64);
    }
    
    @Override
    public boolean isTransferable() throws GSSException {
        return this.checkFlags(256);
    }
    
    @Override
    public boolean isProtReady() {
        return this.checkFlags(128);
    }
    
    @Override
    public boolean getConfState() {
        return this.checkFlags(16);
    }
    
    @Override
    public boolean getIntegState() {
        return this.checkFlags(32);
    }
    
    @Override
    public boolean getDelegPolicyState() {
        return false;
    }
    
    @Override
    public int getLifetime() {
        return this.cStub.getContextTime(this.pContext);
    }
    
    @Override
    public GSSNameSpi getSrcName() throws GSSException {
        return this.srcName;
    }
    
    @Override
    public GSSNameSpi getTargName() throws GSSException {
        return this.targetName;
    }
    
    @Override
    public Oid getMech() throws GSSException {
        if (this.isEstablished && this.actualMech != null) {
            return this.actualMech;
        }
        return this.cStub.getMech();
    }
    
    @Override
    public GSSCredentialSpi getDelegCred() throws GSSException {
        return this.delegatedCred;
    }
    
    @Override
    public boolean isInitiator() {
        return this.isInitiator;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.dispose();
    }
    
    @Override
    public Object inquireSecContext(final InquireType inquireType) throws GSSException {
        throw new GSSException(16, -1, "Inquire type not supported.");
    }
}

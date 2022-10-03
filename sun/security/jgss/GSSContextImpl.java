package sun.security.jgss;

import java.security.Permission;
import com.sun.security.jgss.InquireSecContextPermission;
import com.sun.security.jgss.InquireType;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.spi.GSSCredentialSpi;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.ChannelBinding;
import sun.security.util.ObjectIdentifier;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSContextSpi;
import com.sun.security.jgss.ExtendedGSSContext;

class GSSContextImpl implements ExtendedGSSContext
{
    private final GSSManagerImpl gssManager;
    private final boolean initiator;
    private static final int PRE_INIT = 1;
    private static final int IN_PROGRESS = 2;
    private static final int READY = 3;
    private static final int DELETED = 4;
    private int currentState;
    private GSSContextSpi mechCtxt;
    private Oid mechOid;
    private ObjectIdentifier objId;
    private GSSCredentialImpl myCred;
    private GSSNameImpl srcName;
    private GSSNameImpl targName;
    private int reqLifetime;
    private ChannelBinding channelBindings;
    private boolean reqConfState;
    private boolean reqIntegState;
    private boolean reqMutualAuthState;
    private boolean reqReplayDetState;
    private boolean reqSequenceDetState;
    private boolean reqCredDelegState;
    private boolean reqAnonState;
    private boolean reqDelegPolicyState;
    
    public GSSContextImpl(final GSSManagerImpl gssManager, final GSSName gssName, Oid default_MECH_OID, final GSSCredential gssCredential, final int reqLifetime) throws GSSException {
        this.currentState = 1;
        this.mechCtxt = null;
        this.mechOid = null;
        this.objId = null;
        this.myCred = null;
        this.srcName = null;
        this.targName = null;
        this.reqLifetime = Integer.MAX_VALUE;
        this.channelBindings = null;
        this.reqConfState = true;
        this.reqIntegState = true;
        this.reqMutualAuthState = true;
        this.reqReplayDetState = true;
        this.reqSequenceDetState = true;
        this.reqCredDelegState = false;
        this.reqAnonState = false;
        this.reqDelegPolicyState = false;
        if (gssName == null || !(gssName instanceof GSSNameImpl)) {
            throw new GSSException(3);
        }
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        this.gssManager = gssManager;
        this.myCred = (GSSCredentialImpl)gssCredential;
        this.reqLifetime = reqLifetime;
        this.targName = (GSSNameImpl)gssName;
        this.mechOid = default_MECH_OID;
        this.initiator = true;
    }
    
    public GSSContextImpl(final GSSManagerImpl gssManager, final GSSCredential gssCredential) throws GSSException {
        this.currentState = 1;
        this.mechCtxt = null;
        this.mechOid = null;
        this.objId = null;
        this.myCred = null;
        this.srcName = null;
        this.targName = null;
        this.reqLifetime = Integer.MAX_VALUE;
        this.channelBindings = null;
        this.reqConfState = true;
        this.reqIntegState = true;
        this.reqMutualAuthState = true;
        this.reqReplayDetState = true;
        this.reqSequenceDetState = true;
        this.reqCredDelegState = false;
        this.reqAnonState = false;
        this.reqDelegPolicyState = false;
        this.gssManager = gssManager;
        this.myCred = (GSSCredentialImpl)gssCredential;
        this.initiator = false;
    }
    
    public GSSContextImpl(final GSSManagerImpl gssManager, final byte[] array) throws GSSException {
        this.currentState = 1;
        this.mechCtxt = null;
        this.mechOid = null;
        this.objId = null;
        this.myCred = null;
        this.srcName = null;
        this.targName = null;
        this.reqLifetime = Integer.MAX_VALUE;
        this.channelBindings = null;
        this.reqConfState = true;
        this.reqIntegState = true;
        this.reqMutualAuthState = true;
        this.reqReplayDetState = true;
        this.reqSequenceDetState = true;
        this.reqCredDelegState = false;
        this.reqAnonState = false;
        this.reqDelegPolicyState = false;
        this.gssManager = gssManager;
        this.mechCtxt = gssManager.getMechanismContext(array);
        this.initiator = this.mechCtxt.isInitiator();
        this.mechOid = this.mechCtxt.getMech();
    }
    
    @Override
    public byte[] initSecContext(final byte[] array, final int n, final int n2) throws GSSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(600);
        return (byte[])((this.initSecContext(new ByteArrayInputStream(array, n, n2), byteArrayOutputStream) == 0) ? null : byteArrayOutputStream.toByteArray());
    }
    
    @Override
    public int initSecContext(final InputStream inputStream, final OutputStream outputStream) throws GSSException {
        if (this.mechCtxt != null && this.currentState != 2) {
            throw new GSSExceptionImpl(11, "Illegal call to initSecContext");
        }
        int mechTokenLength = -1;
        GSSCredentialSpi gssCredentialSpi = null;
        boolean b = false;
        try {
            if (this.mechCtxt == null) {
                if (this.myCred != null) {
                    try {
                        gssCredentialSpi = this.myCred.getElement(this.mechOid, true);
                    }
                    catch (final GSSException ex) {
                        if (!GSSUtil.isSpNegoMech(this.mechOid) || ex.getMajor() != 13) {
                            throw ex;
                        }
                        gssCredentialSpi = this.myCred.getElement(this.myCred.getMechs()[0], true);
                    }
                }
                (this.mechCtxt = this.gssManager.getMechanismContext(this.targName.getElement(this.mechOid), gssCredentialSpi, this.reqLifetime, this.mechOid)).requestConf(this.reqConfState);
                this.mechCtxt.requestInteg(this.reqIntegState);
                this.mechCtxt.requestCredDeleg(this.reqCredDelegState);
                this.mechCtxt.requestMutualAuth(this.reqMutualAuthState);
                this.mechCtxt.requestReplayDet(this.reqReplayDetState);
                this.mechCtxt.requestSequenceDet(this.reqSequenceDetState);
                this.mechCtxt.requestAnonymity(this.reqAnonState);
                this.mechCtxt.setChannelBinding(this.channelBindings);
                this.mechCtxt.requestDelegPolicy(this.reqDelegPolicyState);
                this.objId = new ObjectIdentifier(this.mechOid.toString());
                this.currentState = 2;
                b = true;
            }
            else if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS")) {
                if (!GSSUtil.isSpNegoMech(this.mechOid)) {
                    final GSSHeader gssHeader = new GSSHeader(inputStream);
                    if (!gssHeader.getOid().equals((Object)this.objId)) {
                        throw new GSSExceptionImpl(10, "Mechanism not equal to " + this.mechOid.toString() + " in initSecContext token");
                    }
                    mechTokenLength = gssHeader.getMechTokenLength();
                }
            }
            final byte[] initSecContext = this.mechCtxt.initSecContext(inputStream, mechTokenLength);
            int length = 0;
            if (initSecContext != null) {
                length = initSecContext.length;
                if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS")) {
                    if (b || !GSSUtil.isSpNegoMech(this.mechOid)) {
                        length += new GSSHeader(this.objId, initSecContext.length).encode(outputStream);
                    }
                }
                outputStream.write(initSecContext);
            }
            if (this.mechCtxt.isEstablished()) {
                this.currentState = 3;
            }
            return length;
        }
        catch (final IOException ex2) {
            throw new GSSExceptionImpl(10, ex2.getMessage());
        }
    }
    
    @Override
    public byte[] acceptSecContext(final byte[] array, final int n, final int n2) throws GSSException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
        this.acceptSecContext(new ByteArrayInputStream(array, n, n2), byteArrayOutputStream);
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        return (byte[])((byteArray.length == 0) ? null : byteArray);
    }
    
    @Override
    public void acceptSecContext(final InputStream inputStream, final OutputStream outputStream) throws GSSException {
        if (this.mechCtxt != null && this.currentState != 2) {
            throw new GSSExceptionImpl(11, "Illegal call to acceptSecContext");
        }
        int n = -1;
        GSSCredentialSpi element = null;
        try {
            if (this.mechCtxt == null) {
                final GSSHeader gssHeader = new GSSHeader(inputStream);
                n = gssHeader.getMechTokenLength();
                this.objId = gssHeader.getOid();
                this.mechOid = new Oid(this.objId.toString());
                if (this.myCred != null) {
                    element = this.myCred.getElement(this.mechOid, false);
                }
                (this.mechCtxt = this.gssManager.getMechanismContext(element, this.mechOid)).setChannelBinding(this.channelBindings);
                this.currentState = 2;
            }
            else if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS")) {
                if (!GSSUtil.isSpNegoMech(this.mechOid)) {
                    final GSSHeader gssHeader2 = new GSSHeader(inputStream);
                    if (!gssHeader2.getOid().equals((Object)this.objId)) {
                        throw new GSSExceptionImpl(10, "Mechanism not equal to " + this.mechOid.toString() + " in acceptSecContext token");
                    }
                    n = gssHeader2.getMechTokenLength();
                }
            }
            final byte[] acceptSecContext = this.mechCtxt.acceptSecContext(inputStream, n);
            if (acceptSecContext != null) {
                final int length = acceptSecContext.length;
                if (!this.mechCtxt.getProvider().getName().equals("SunNativeGSS")) {
                    if (!GSSUtil.isSpNegoMech(this.mechOid)) {
                        final int n2 = length + new GSSHeader(this.objId, acceptSecContext.length).encode(outputStream);
                    }
                }
                outputStream.write(acceptSecContext);
            }
            if (this.mechCtxt.isEstablished()) {
                this.currentState = 3;
            }
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(10, ex.getMessage());
        }
    }
    
    @Override
    public boolean isEstablished() {
        return this.mechCtxt != null && this.currentState == 3;
    }
    
    @Override
    public int getWrapSizeLimit(final int n, final boolean b, final int n2) throws GSSException {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getWrapSizeLimit(n, b, n2);
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public byte[] wrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            return this.mechCtxt.wrap(array, n, n2, messageProp);
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public void wrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            this.mechCtxt.wrap(inputStream, outputStream, messageProp);
            return;
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public byte[] unwrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            return this.mechCtxt.unwrap(array, n, n2, messageProp);
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public void unwrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            this.mechCtxt.unwrap(inputStream, outputStream, messageProp);
            return;
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public byte[] getMIC(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getMIC(array, n, n2, messageProp);
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public void getMIC(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            this.mechCtxt.getMIC(inputStream, outputStream, messageProp);
            return;
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public void verifyMIC(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            this.mechCtxt.verifyMIC(array, n, n2, array2, n3, n4, messageProp);
            return;
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public void verifyMIC(final InputStream inputStream, final InputStream inputStream2, final MessageProp messageProp) throws GSSException {
        if (this.mechCtxt != null) {
            this.mechCtxt.verifyMIC(inputStream, inputStream2, messageProp);
            return;
        }
        throw new GSSExceptionImpl(12, "No mechanism context yet!");
    }
    
    @Override
    public byte[] export() throws GSSException {
        byte[] export = null;
        if (this.mechCtxt.isTransferable() && this.mechCtxt.getProvider().getName().equals("SunNativeGSS")) {
            export = this.mechCtxt.export();
        }
        return export;
    }
    
    @Override
    public void requestMutualAuth(final boolean reqMutualAuthState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqMutualAuthState = reqMutualAuthState;
        }
    }
    
    @Override
    public void requestReplayDet(final boolean reqReplayDetState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqReplayDetState = reqReplayDetState;
        }
    }
    
    @Override
    public void requestSequenceDet(final boolean reqSequenceDetState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqSequenceDetState = reqSequenceDetState;
        }
    }
    
    @Override
    public void requestCredDeleg(final boolean reqCredDelegState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqCredDelegState = reqCredDelegState;
        }
    }
    
    @Override
    public void requestAnonymity(final boolean reqAnonState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqAnonState = reqAnonState;
        }
    }
    
    @Override
    public void requestConf(final boolean reqConfState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqConfState = reqConfState;
        }
    }
    
    @Override
    public void requestInteg(final boolean reqIntegState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqIntegState = reqIntegState;
        }
    }
    
    @Override
    public void requestLifetime(final int reqLifetime) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqLifetime = reqLifetime;
        }
    }
    
    @Override
    public void setChannelBinding(final ChannelBinding channelBindings) throws GSSException {
        if (this.mechCtxt == null) {
            if (this.channelBindings != null) {
                throw new GSSException(1);
            }
            this.channelBindings = channelBindings;
        }
    }
    
    @Override
    public boolean getCredDelegState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getCredDelegState();
        }
        return this.reqCredDelegState;
    }
    
    @Override
    public boolean getMutualAuthState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getMutualAuthState();
        }
        return this.reqMutualAuthState;
    }
    
    @Override
    public boolean getReplayDetState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getReplayDetState();
        }
        return this.reqReplayDetState;
    }
    
    @Override
    public boolean getSequenceDetState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getSequenceDetState();
        }
        return this.reqSequenceDetState;
    }
    
    @Override
    public boolean getAnonymityState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getAnonymityState();
        }
        return this.reqAnonState;
    }
    
    @Override
    public boolean isTransferable() throws GSSException {
        return this.mechCtxt != null && this.mechCtxt.isTransferable();
    }
    
    @Override
    public boolean isProtReady() {
        return this.mechCtxt != null && this.mechCtxt.isProtReady();
    }
    
    @Override
    public boolean getConfState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getConfState();
        }
        return this.reqConfState;
    }
    
    @Override
    public boolean getIntegState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getIntegState();
        }
        return this.reqIntegState;
    }
    
    @Override
    public int getLifetime() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getLifetime();
        }
        return this.reqLifetime;
    }
    
    @Override
    public GSSName getSrcName() throws GSSException {
        if (this.srcName == null) {
            this.srcName = GSSNameImpl.wrapElement(this.gssManager, this.mechCtxt.getSrcName());
        }
        return this.srcName;
    }
    
    @Override
    public GSSName getTargName() throws GSSException {
        if (this.targName == null) {
            this.targName = GSSNameImpl.wrapElement(this.gssManager, this.mechCtxt.getTargName());
        }
        return this.targName;
    }
    
    @Override
    public Oid getMech() throws GSSException {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getMech();
        }
        return this.mechOid;
    }
    
    @Override
    public GSSCredential getDelegCred() throws GSSException {
        if (this.mechCtxt == null) {
            throw new GSSExceptionImpl(12, "No mechanism context yet!");
        }
        final GSSCredentialSpi delegCred = this.mechCtxt.getDelegCred();
        return (delegCred == null) ? null : new GSSCredentialImpl(this.gssManager, delegCred);
    }
    
    @Override
    public boolean isInitiator() throws GSSException {
        return this.initiator;
    }
    
    @Override
    public void dispose() throws GSSException {
        this.currentState = 4;
        if (this.mechCtxt != null) {
            this.mechCtxt.dispose();
            this.mechCtxt = null;
        }
        this.myCred = null;
        this.srcName = null;
        this.targName = null;
    }
    
    @Override
    public Object inquireSecContext(final InquireType inquireType) throws GSSException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new InquireSecContextPermission(inquireType.toString()));
        }
        if (this.mechCtxt == null) {
            throw new GSSException(12);
        }
        return this.mechCtxt.inquireSecContext(inquireType);
    }
    
    @Override
    public void requestDelegPolicy(final boolean reqDelegPolicyState) throws GSSException {
        if (this.mechCtxt == null && this.initiator) {
            this.reqDelegPolicyState = reqDelegPolicyState;
        }
    }
    
    @Override
    public boolean getDelegPolicyState() {
        if (this.mechCtxt != null) {
            return this.mechCtxt.getDelegPolicyState();
        }
        return this.reqDelegPolicyState;
    }
}

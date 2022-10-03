package sun.security.jgss.spnego;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import com.sun.security.jgss.InquireType;
import java.io.OutputStream;
import sun.security.jgss.GSSNameImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import sun.security.jgss.GSSCredentialImpl;
import org.ietf.jgss.MessageProp;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.BitArray;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.GSSToken;
import java.io.InputStream;
import java.security.Provider;
import com.sun.security.jgss.ExtendedGSSContext;
import org.ietf.jgss.GSSException;
import sun.security.jgss.spi.GSSCredentialSpi;
import org.ietf.jgss.Oid;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSContext;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.GSSContextSpi;

public class SpNegoContext implements GSSContextSpi
{
    private static final int STATE_NEW = 1;
    private static final int STATE_IN_PROCESS = 2;
    private static final int STATE_DONE = 3;
    private static final int STATE_DELETED = 4;
    private int state;
    private boolean credDelegState;
    private boolean mutualAuthState;
    private boolean replayDetState;
    private boolean sequenceDetState;
    private boolean confState;
    private boolean integState;
    private boolean delegPolicyState;
    private GSSNameSpi peerName;
    private GSSNameSpi myName;
    private SpNegoCredElement myCred;
    private GSSContext mechContext;
    private byte[] DER_mechTypes;
    private int lifetime;
    private ChannelBinding channelBinding;
    private boolean initiator;
    private Oid internal_mech;
    private final SpNegoMechFactory factory;
    static final boolean DEBUG;
    
    public SpNegoContext(final SpNegoMechFactory factory, final GSSNameSpi peerName, final GSSCredentialSpi gssCredentialSpi, final int lifetime) throws GSSException {
        this.state = 1;
        this.credDelegState = false;
        this.mutualAuthState = true;
        this.replayDetState = true;
        this.sequenceDetState = true;
        this.confState = true;
        this.integState = true;
        this.delegPolicyState = false;
        this.peerName = null;
        this.myName = null;
        this.myCred = null;
        this.mechContext = null;
        this.DER_mechTypes = null;
        this.internal_mech = null;
        if (peerName == null) {
            throw new IllegalArgumentException("Cannot have null peer name");
        }
        if (gssCredentialSpi != null && !(gssCredentialSpi instanceof SpNegoCredElement)) {
            throw new IllegalArgumentException("Wrong cred element type");
        }
        this.peerName = peerName;
        this.myCred = (SpNegoCredElement)gssCredentialSpi;
        this.lifetime = lifetime;
        this.initiator = true;
        this.factory = factory;
    }
    
    public SpNegoContext(final SpNegoMechFactory factory, final GSSCredentialSpi gssCredentialSpi) throws GSSException {
        this.state = 1;
        this.credDelegState = false;
        this.mutualAuthState = true;
        this.replayDetState = true;
        this.sequenceDetState = true;
        this.confState = true;
        this.integState = true;
        this.delegPolicyState = false;
        this.peerName = null;
        this.myName = null;
        this.myCred = null;
        this.mechContext = null;
        this.DER_mechTypes = null;
        this.internal_mech = null;
        if (gssCredentialSpi != null && !(gssCredentialSpi instanceof SpNegoCredElement)) {
            throw new IllegalArgumentException("Wrong cred element type");
        }
        this.myCred = (SpNegoCredElement)gssCredentialSpi;
        this.initiator = false;
        this.factory = factory;
    }
    
    public SpNegoContext(final SpNegoMechFactory spNegoMechFactory, final byte[] array) throws GSSException {
        this.state = 1;
        this.credDelegState = false;
        this.mutualAuthState = true;
        this.replayDetState = true;
        this.sequenceDetState = true;
        this.confState = true;
        this.integState = true;
        this.delegPolicyState = false;
        this.peerName = null;
        this.myName = null;
        this.myCred = null;
        this.mechContext = null;
        this.DER_mechTypes = null;
        this.internal_mech = null;
        throw new GSSException(16, -1, "GSS Import Context not available");
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
    public final void requestDelegPolicy(final boolean delegPolicyState) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.delegPolicyState = delegPolicyState;
        }
    }
    
    @Override
    public final boolean getIntegState() {
        return this.integState;
    }
    
    @Override
    public final boolean getDelegPolicyState() {
        if (this.isInitiator() && this.mechContext != null && this.mechContext instanceof ExtendedGSSContext && (this.state == 2 || this.state == 3)) {
            return ((ExtendedGSSContext)this.mechContext).getDelegPolicyState();
        }
        return this.delegPolicyState;
    }
    
    @Override
    public final void requestCredDeleg(final boolean credDelegState) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.credDelegState = credDelegState;
        }
    }
    
    @Override
    public final boolean getCredDelegState() {
        if (this.isInitiator() && this.mechContext != null && (this.state == 2 || this.state == 3)) {
            return this.mechContext.getCredDelegState();
        }
        return this.credDelegState;
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
    public final Oid getMech() {
        if (this.isEstablished()) {
            return this.getNegotiatedMech();
        }
        return SpNegoMechFactory.GSS_SPNEGO_MECH_OID;
    }
    
    public final Oid getNegotiatedMech() {
        return this.internal_mech;
    }
    
    @Override
    public final Provider getProvider() {
        return SpNegoMechFactory.PROVIDER;
    }
    
    @Override
    public final void dispose() throws GSSException {
        this.mechContext = null;
        this.state = 4;
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
        byte[] array = null;
        byte[] gss_initSecContext = null;
        int n2 = 11;
        if (SpNegoContext.DEBUG) {
            System.out.println("Entered SpNego.initSecContext with state=" + printState(this.state));
        }
        if (!this.isInitiator()) {
            throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
        }
        try {
            if (this.state == 1) {
                this.state = 2;
                n2 = 13;
                final Oid[] availableMechs = this.getAvailableMechs();
                this.DER_mechTypes = this.getEncodedMechs(availableMechs);
                this.internal_mech = availableMechs[0];
                final byte[] gss_initSecContext2 = this.GSS_initSecContext(null);
                n2 = 10;
                final NegTokenInit negTokenInit = new NegTokenInit(this.DER_mechTypes, this.getContextFlags(), gss_initSecContext2, null);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.initSecContext: sending token of type = " + SpNegoToken.getTokenName(negTokenInit.getType()));
                }
                array = negTokenInit.getEncoded();
            }
            else if (this.state == 2) {
                n2 = 11;
                if (inputStream == null) {
                    throw new GSSException(n2, -1, "No token received from peer!");
                }
                n2 = 10;
                final byte[] array2 = new byte[inputStream.available()];
                GSSToken.readFully(inputStream, array2);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.initSecContext: process received token = " + GSSToken.getHexBytes(array2));
                }
                final NegTokenTarg negTokenTarg = new NegTokenTarg(array2);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.initSecContext: received token of type = " + SpNegoToken.getTokenName(negTokenTarg.getType()));
                }
                this.internal_mech = negTokenTarg.getSupportedMech();
                if (this.internal_mech == null) {
                    throw new GSSException(n2, -1, "supported mechanism from server is null");
                }
                SpNegoToken.NegoResult negoResult = null;
                switch (negTokenTarg.getNegotiatedResult()) {
                    case 0: {
                        negoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
                        this.state = 3;
                        break;
                    }
                    case 1: {
                        negoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
                        this.state = 2;
                        break;
                    }
                    case 2: {
                        negoResult = SpNegoToken.NegoResult.REJECT;
                        this.state = 4;
                        break;
                    }
                    default: {
                        this.state = 3;
                        break;
                    }
                }
                n2 = 2;
                if (negoResult == SpNegoToken.NegoResult.REJECT) {
                    throw new GSSException(n2, -1, this.internal_mech.toString());
                }
                n2 = 10;
                if (negoResult == SpNegoToken.NegoResult.ACCEPT_COMPLETE || negoResult == SpNegoToken.NegoResult.ACCEPT_INCOMPLETE) {
                    final byte[] responseToken = negTokenTarg.getResponseToken();
                    if (responseToken == null) {
                        if (!this.isMechContextEstablished()) {
                            throw new GSSException(n2, -1, "mechanism token from server is null");
                        }
                    }
                    else {
                        gss_initSecContext = this.GSS_initSecContext(responseToken);
                    }
                    if (!GSSUtil.useMSInterop() && !this.verifyMechListMIC(this.DER_mechTypes, negTokenTarg.getMechListMIC())) {
                        throw new GSSException(n2, -1, "verification of MIC on MechList Failed!");
                    }
                    if (this.isMechContextEstablished()) {
                        this.state = 3;
                        array = gss_initSecContext;
                        if (SpNegoContext.DEBUG) {
                            System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech));
                        }
                    }
                    else {
                        final NegTokenInit negTokenInit2 = new NegTokenInit(null, null, gss_initSecContext, null);
                        if (SpNegoContext.DEBUG) {
                            System.out.println("SpNegoContext.initSecContext: continue sending token of type = " + SpNegoToken.getTokenName(negTokenInit2.getType()));
                        }
                        array = negTokenInit2.getEncoded();
                    }
                }
            }
            else if (SpNegoContext.DEBUG) {
                System.out.println(this.state);
            }
            if (SpNegoContext.DEBUG && array != null) {
                System.out.println("SNegoContext.initSecContext: sending token = " + GSSToken.getHexBytes(array));
            }
        }
        catch (final GSSException ex) {
            final GSSException ex2 = new GSSException(n2, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        catch (final IOException ex3) {
            final GSSException ex4 = new GSSException(11, -1, ex3.getMessage());
            ex4.initCause(ex3);
            throw ex4;
        }
        return array;
    }
    
    @Override
    public final byte[] acceptSecContext(final InputStream inputStream, final int n) throws GSSException {
        byte[] array = null;
        int verifyMechListMIC = 1;
        if (SpNegoContext.DEBUG) {
            System.out.println("Entered SpNegoContext.acceptSecContext with state=" + printState(this.state));
        }
        if (this.isInitiator()) {
            throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
        }
        try {
            if (this.state == 1) {
                this.state = 2;
                final byte[] array2 = new byte[inputStream.available()];
                GSSToken.readFully(inputStream, array2);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.acceptSecContext: receiving token = " + GSSToken.getHexBytes(array2));
                }
                final NegTokenInit negTokenInit = new NegTokenInit(array2);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(negTokenInit.getType()));
                }
                final Oid[] mechTypeList = negTokenInit.getMechTypeList();
                this.DER_mechTypes = negTokenInit.getMechTypes();
                if (this.DER_mechTypes == null) {
                    verifyMechListMIC = 0;
                }
                Oid negotiate_mech_type = negotiate_mech_type(this.getAvailableMechs(), mechTypeList);
                if (negotiate_mech_type == null) {
                    verifyMechListMIC = 0;
                }
                this.internal_mech = negotiate_mech_type;
                byte[] gss_acceptSecContext;
                if (mechTypeList[0].equals(negotiate_mech_type) || (GSSUtil.isKerberosMech(mechTypeList[0]) && GSSUtil.isKerberosMech(negotiate_mech_type))) {
                    if (SpNegoContext.DEBUG && !negotiate_mech_type.equals(mechTypeList[0])) {
                        System.out.println("SpNegoContext.acceptSecContext: negotiated mech adjusted to " + mechTypeList[0]);
                    }
                    final byte[] mechToken = negTokenInit.getMechToken();
                    if (mechToken == null) {
                        throw new GSSException(11, -1, "mechToken is missing");
                    }
                    gss_acceptSecContext = this.GSS_acceptSecContext(mechToken);
                    negotiate_mech_type = mechTypeList[0];
                }
                else {
                    gss_acceptSecContext = null;
                }
                if (!GSSUtil.useMSInterop() && verifyMechListMIC != 0) {
                    verifyMechListMIC = (this.verifyMechListMIC(this.DER_mechTypes, negTokenInit.getMechListMIC()) ? 1 : 0);
                }
                SpNegoToken.NegoResult negoResult;
                if (verifyMechListMIC != 0) {
                    if (this.isMechContextEstablished()) {
                        negoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
                        this.state = 3;
                        this.setContextFlags();
                        if (SpNegoContext.DEBUG) {
                            System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + GSSUtil.getMechStr(this.internal_mech));
                        }
                    }
                    else {
                        negoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
                        this.state = 2;
                    }
                }
                else {
                    negoResult = SpNegoToken.NegoResult.REJECT;
                    this.state = 3;
                }
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.acceptSecContext: mechanism wanted = " + negotiate_mech_type);
                    System.out.println("SpNegoContext.acceptSecContext: negotiated result = " + negoResult);
                }
                final NegTokenTarg negTokenTarg = new NegTokenTarg(negoResult.ordinal(), negotiate_mech_type, gss_acceptSecContext, null);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(negTokenTarg.getType()));
                }
                array = negTokenTarg.getEncoded();
            }
            else if (this.state == 2) {
                final byte[] array3 = new byte[inputStream.available()];
                GSSToken.readFully(inputStream, array3);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.acceptSecContext: receiving token = " + GSSToken.getHexBytes(array3));
                }
                final NegTokenTarg negTokenTarg2 = new NegTokenTarg(array3);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.acceptSecContext: received token of type = " + SpNegoToken.getTokenName(negTokenTarg2.getType()));
                }
                final byte[] gss_acceptSecContext2 = this.GSS_acceptSecContext(negTokenTarg2.getResponseToken());
                if (gss_acceptSecContext2 == null) {
                    verifyMechListMIC = 0;
                }
                SpNegoToken.NegoResult negoResult2;
                if (verifyMechListMIC != 0) {
                    if (this.isMechContextEstablished()) {
                        negoResult2 = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
                        this.state = 3;
                    }
                    else {
                        negoResult2 = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
                        this.state = 2;
                    }
                }
                else {
                    negoResult2 = SpNegoToken.NegoResult.REJECT;
                    this.state = 3;
                }
                final NegTokenTarg negTokenTarg3 = new NegTokenTarg(negoResult2.ordinal(), null, gss_acceptSecContext2, null);
                if (SpNegoContext.DEBUG) {
                    System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + SpNegoToken.getTokenName(negTokenTarg3.getType()));
                }
                array = negTokenTarg3.getEncoded();
            }
            else if (SpNegoContext.DEBUG) {
                System.out.println("AcceptSecContext: state = " + this.state);
            }
            if (SpNegoContext.DEBUG) {
                System.out.println("SpNegoContext.acceptSecContext: sending token = " + GSSToken.getHexBytes(array));
            }
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        if (this.state == 3) {
            this.setContextFlags();
        }
        return array;
    }
    
    private Oid[] getAvailableMechs() {
        if (this.myCred != null) {
            return new Oid[] { this.myCred.getInternalMech() };
        }
        return this.factory.availableMechs;
    }
    
    private byte[] getEncodedMechs(final Oid[] array) throws IOException, GSSException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        for (int i = 0; i < array.length; ++i) {
            derOutputStream.write(array[i].getDER());
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write((byte)48, derOutputStream);
        return derOutputStream2.toByteArray();
    }
    
    private BitArray getContextFlags() {
        final BitArray bitArray = new BitArray(7);
        if (this.getCredDelegState()) {
            bitArray.set(0, true);
        }
        if (this.getMutualAuthState()) {
            bitArray.set(1, true);
        }
        if (this.getReplayDetState()) {
            bitArray.set(2, true);
        }
        if (this.getSequenceDetState()) {
            bitArray.set(3, true);
        }
        if (this.getConfState()) {
            bitArray.set(5, true);
        }
        if (this.getIntegState()) {
            bitArray.set(6, true);
        }
        return bitArray;
    }
    
    private void setContextFlags() {
        if (this.mechContext != null) {
            if (this.mechContext.getCredDelegState()) {
                this.credDelegState = true;
            }
            if (!this.mechContext.getMutualAuthState()) {
                this.mutualAuthState = false;
            }
            if (!this.mechContext.getReplayDetState()) {
                this.replayDetState = false;
            }
            if (!this.mechContext.getSequenceDetState()) {
                this.sequenceDetState = false;
            }
            if (!this.mechContext.getIntegState()) {
                this.integState = false;
            }
            if (!this.mechContext.getConfState()) {
                this.confState = false;
            }
        }
    }
    
    private boolean verifyMechListMIC(final byte[] array, final byte[] array2) throws GSSException {
        if (array2 == null) {
            if (SpNegoContext.DEBUG) {
                System.out.println("SpNegoContext: no MIC token validation");
            }
            return true;
        }
        if (!this.mechContext.getIntegState()) {
            if (SpNegoContext.DEBUG) {
                System.out.println("SpNegoContext: no MIC token validation - mechanism does not support integrity");
            }
            return true;
        }
        boolean b;
        try {
            this.verifyMIC(array2, 0, array2.length, array, 0, array.length, new MessageProp(0, true));
            b = true;
        }
        catch (final GSSException ex) {
            b = false;
            if (SpNegoContext.DEBUG) {
                System.out.println("SpNegoContext: MIC validation failed! " + ex.getMessage());
            }
        }
        return b;
    }
    
    private byte[] GSS_initSecContext(final byte[] array) throws GSSException {
        if (this.mechContext == null) {
            final GSSName name = this.factory.manager.createName(this.peerName.toString(), this.peerName.getStringNameType(), this.internal_mech);
            GSSCredential gssCredential = null;
            if (this.myCred != null) {
                gssCredential = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
            }
            (this.mechContext = this.factory.manager.createContext(name, this.internal_mech, gssCredential, 0)).requestConf(this.confState);
            this.mechContext.requestInteg(this.integState);
            this.mechContext.requestCredDeleg(this.credDelegState);
            this.mechContext.requestMutualAuth(this.mutualAuthState);
            this.mechContext.requestReplayDet(this.replayDetState);
            this.mechContext.requestSequenceDet(this.sequenceDetState);
            if (this.mechContext instanceof ExtendedGSSContext) {
                ((ExtendedGSSContext)this.mechContext).requestDelegPolicy(this.delegPolicyState);
            }
        }
        byte[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new byte[0];
        }
        return this.mechContext.initSecContext(array2, 0, array2.length);
    }
    
    private byte[] GSS_acceptSecContext(final byte[] array) throws GSSException {
        if (this.mechContext == null) {
            GSSCredential gssCredential = null;
            if (this.myCred != null) {
                gssCredential = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
            }
            this.mechContext = this.factory.manager.createContext(gssCredential);
        }
        return this.mechContext.acceptSecContext(array, 0, array.length);
    }
    
    private static Oid negotiate_mech_type(final Oid[] array, final Oid[] array2) {
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array2.length; ++j) {
                if (array2[j].equals(array[i])) {
                    if (SpNegoContext.DEBUG) {
                        System.out.println("SpNegoContext: negotiated mechanism = " + array2[j]);
                    }
                    return array2[j];
                }
            }
        }
        return null;
    }
    
    @Override
    public final boolean isEstablished() {
        return this.state == 3;
    }
    
    public final boolean isMechContextEstablished() {
        if (this.mechContext != null) {
            return this.mechContext.isEstablished();
        }
        if (SpNegoContext.DEBUG) {
            System.out.println("The underlying mechanism context has not been initialized");
        }
        return false;
    }
    
    @Override
    public final byte[] export() throws GSSException {
        throw new GSSException(16, -1, "GSS Export Context not available");
    }
    
    @Override
    public final void setChannelBinding(final ChannelBinding channelBinding) throws GSSException {
        this.channelBinding = channelBinding;
    }
    
    final ChannelBinding getChannelBinding() {
        return this.channelBinding;
    }
    
    @Override
    public final void requestAnonymity(final boolean b) throws GSSException {
    }
    
    @Override
    public final boolean getAnonymityState() {
        return false;
    }
    
    @Override
    public void requestLifetime(final int lifetime) throws GSSException {
        if (this.state == 1 && this.isInitiator()) {
            this.lifetime = lifetime;
        }
    }
    
    @Override
    public final int getLifetime() {
        if (this.mechContext != null) {
            return this.mechContext.getLifetime();
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    public final boolean isTransferable() throws GSSException {
        return false;
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
    public final GSSNameSpi getTargName() throws GSSException {
        if (this.mechContext != null) {
            return this.peerName = ((GSSNameImpl)this.mechContext.getTargName()).getElement(this.internal_mech);
        }
        if (SpNegoContext.DEBUG) {
            System.out.println("The underlying mechanism context has not been initialized");
        }
        return null;
    }
    
    @Override
    public final GSSNameSpi getSrcName() throws GSSException {
        if (this.mechContext != null) {
            return this.myName = ((GSSNameImpl)this.mechContext.getSrcName()).getElement(this.internal_mech);
        }
        if (SpNegoContext.DEBUG) {
            System.out.println("The underlying mechanism context has not been initialized");
        }
        return null;
    }
    
    @Override
    public final GSSCredentialSpi getDelegCred() throws GSSException {
        if (this.state != 2 && this.state != 3) {
            throw new GSSException(12);
        }
        if (this.mechContext == null) {
            throw new GSSException(12, -1, "getDelegCred called in invalid state!");
        }
        final GSSCredentialImpl gssCredentialImpl = (GSSCredentialImpl)this.mechContext.getDelegCred();
        if (gssCredentialImpl == null) {
            return null;
        }
        boolean b = false;
        if (gssCredentialImpl.getUsage() == 1) {
            b = true;
        }
        return new SpNegoCredElement(gssCredentialImpl.getElement(this.internal_mech, b)).getInternalCred();
    }
    
    @Override
    public final int getWrapSizeLimit(final int n, final boolean b, final int n2) throws GSSException {
        if (this.mechContext != null) {
            return this.mechContext.getWrapSizeLimit(n, b, n2);
        }
        throw new GSSException(12, -1, "getWrapSizeLimit called in invalid state!");
    }
    
    @Override
    public final byte[] wrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            return this.mechContext.wrap(array, n, n2, messageProp);
        }
        throw new GSSException(12, -1, "Wrap called in invalid state!");
    }
    
    @Override
    public final void wrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            this.mechContext.wrap(inputStream, outputStream, messageProp);
            return;
        }
        throw new GSSException(12, -1, "Wrap called in invalid state!");
    }
    
    @Override
    public final byte[] unwrap(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            return this.mechContext.unwrap(array, n, n2, messageProp);
        }
        throw new GSSException(12, -1, "UnWrap called in invalid state!");
    }
    
    @Override
    public final void unwrap(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            this.mechContext.unwrap(inputStream, outputStream, messageProp);
            return;
        }
        throw new GSSException(12, -1, "UnWrap called in invalid state!");
    }
    
    @Override
    public final byte[] getMIC(final byte[] array, final int n, final int n2, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            return this.mechContext.getMIC(array, n, n2, messageProp);
        }
        throw new GSSException(12, -1, "getMIC called in invalid state!");
    }
    
    @Override
    public final void getMIC(final InputStream inputStream, final OutputStream outputStream, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            this.mechContext.getMIC(inputStream, outputStream, messageProp);
            return;
        }
        throw new GSSException(12, -1, "getMIC called in invalid state!");
    }
    
    @Override
    public final void verifyMIC(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            this.mechContext.verifyMIC(array, n, n2, array2, n3, n4, messageProp);
            return;
        }
        throw new GSSException(12, -1, "verifyMIC called in invalid state!");
    }
    
    @Override
    public final void verifyMIC(final InputStream inputStream, final InputStream inputStream2, final MessageProp messageProp) throws GSSException {
        if (this.mechContext != null) {
            this.mechContext.verifyMIC(inputStream, inputStream2, messageProp);
            return;
        }
        throw new GSSException(12, -1, "verifyMIC called in invalid state!");
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
    
    @Override
    public Object inquireSecContext(final InquireType inquireType) throws GSSException {
        if (this.mechContext == null) {
            throw new GSSException(12, -1, "Underlying mech not established.");
        }
        if (this.mechContext instanceof ExtendedGSSContext) {
            return ((ExtendedGSSContext)this.mechContext).inquireSecContext(inquireType);
        }
        throw new GSSException(2, -1, "inquireSecContext not supported by underlying mech.");
    }
    
    static {
        DEBUG = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.spnego.debug"));
    }
}

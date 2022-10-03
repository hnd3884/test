package com.sun.jndi.ldap;

import java.util.Collection;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InvalidAttributeValueException;
import java.util.Locale;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import com.sun.jndi.ldap.sasl.SaslInputStream;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import com.sun.jndi.ldap.sasl.LdapSasl;
import java.io.IOException;
import javax.naming.CommunicationException;
import javax.naming.ldap.Control;
import javax.naming.NamingException;
import java.io.OutputStream;
import java.util.Vector;
import com.sun.jndi.ldap.pool.PoolCallback;
import java.util.Hashtable;
import com.sun.jndi.ldap.pool.PooledConnection;

public final class LdapClient implements PooledConnection
{
    private static final int debug = 0;
    static final boolean caseIgnore = true;
    private static final Hashtable<String, Boolean> defaultBinaryAttrs;
    private static final String DISCONNECT_OID = "1.3.6.1.4.1.1466.20036";
    boolean isLdapv3;
    int referenceCount;
    final Connection conn;
    private final PoolCallback pcb;
    private final boolean pooled;
    private boolean authenticateCalled;
    static final int SCOPE_BASE_OBJECT = 0;
    static final int SCOPE_ONE_LEVEL = 1;
    static final int SCOPE_SUBTREE = 2;
    static final int ADD = 0;
    static final int DELETE = 1;
    static final int REPLACE = 2;
    static final int LDAP_VERSION3_VERSION2 = 32;
    static final int LDAP_VERSION2 = 2;
    static final int LDAP_VERSION3 = 3;
    static final int LDAP_VERSION = 3;
    static final int LDAP_REF_FOLLOW = 1;
    static final int LDAP_REF_THROW = 2;
    static final int LDAP_REF_IGNORE = 3;
    static final int LDAP_REF_FOLLOW_SCHEME = 4;
    static final String LDAP_URL = "ldap://";
    static final String LDAPS_URL = "ldaps://";
    static final int LBER_BOOLEAN = 1;
    static final int LBER_INTEGER = 2;
    static final int LBER_BITSTRING = 3;
    static final int LBER_OCTETSTRING = 4;
    static final int LBER_NULL = 5;
    static final int LBER_ENUMERATED = 10;
    static final int LBER_SEQUENCE = 48;
    static final int LBER_SET = 49;
    static final int LDAP_SUPERIOR_DN = 128;
    static final int LDAP_REQ_BIND = 96;
    static final int LDAP_REQ_UNBIND = 66;
    static final int LDAP_REQ_SEARCH = 99;
    static final int LDAP_REQ_MODIFY = 102;
    static final int LDAP_REQ_ADD = 104;
    static final int LDAP_REQ_DELETE = 74;
    static final int LDAP_REQ_MODRDN = 108;
    static final int LDAP_REQ_COMPARE = 110;
    static final int LDAP_REQ_ABANDON = 80;
    static final int LDAP_REQ_EXTENSION = 119;
    static final int LDAP_REP_BIND = 97;
    static final int LDAP_REP_SEARCH = 100;
    static final int LDAP_REP_SEARCH_REF = 115;
    static final int LDAP_REP_RESULT = 101;
    static final int LDAP_REP_MODIFY = 103;
    static final int LDAP_REP_ADD = 105;
    static final int LDAP_REP_DELETE = 107;
    static final int LDAP_REP_MODRDN = 109;
    static final int LDAP_REP_COMPARE = 111;
    static final int LDAP_REP_EXTENSION = 120;
    static final int LDAP_REP_REFERRAL = 163;
    static final int LDAP_REP_EXT_OID = 138;
    static final int LDAP_REP_EXT_VAL = 139;
    static final int LDAP_CONTROLS = 160;
    static final String LDAP_CONTROL_MANAGE_DSA_IT = "2.16.840.1.113730.3.4.2";
    static final String LDAP_CONTROL_PREFERRED_LANG = "1.3.6.1.4.1.1466.20035";
    static final String LDAP_CONTROL_PAGED_RESULTS = "1.2.840.113556.1.4.319";
    static final String LDAP_CONTROL_SERVER_SORT_REQ = "1.2.840.113556.1.4.473";
    static final String LDAP_CONTROL_SERVER_SORT_RES = "1.2.840.113556.1.4.474";
    static final int LDAP_SUCCESS = 0;
    static final int LDAP_OPERATIONS_ERROR = 1;
    static final int LDAP_PROTOCOL_ERROR = 2;
    static final int LDAP_TIME_LIMIT_EXCEEDED = 3;
    static final int LDAP_SIZE_LIMIT_EXCEEDED = 4;
    static final int LDAP_COMPARE_FALSE = 5;
    static final int LDAP_COMPARE_TRUE = 6;
    static final int LDAP_AUTH_METHOD_NOT_SUPPORTED = 7;
    static final int LDAP_STRONG_AUTH_REQUIRED = 8;
    static final int LDAP_PARTIAL_RESULTS = 9;
    static final int LDAP_REFERRAL = 10;
    static final int LDAP_ADMIN_LIMIT_EXCEEDED = 11;
    static final int LDAP_UNAVAILABLE_CRITICAL_EXTENSION = 12;
    static final int LDAP_CONFIDENTIALITY_REQUIRED = 13;
    static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
    static final int LDAP_NO_SUCH_ATTRIBUTE = 16;
    static final int LDAP_UNDEFINED_ATTRIBUTE_TYPE = 17;
    static final int LDAP_INAPPROPRIATE_MATCHING = 18;
    static final int LDAP_CONSTRAINT_VIOLATION = 19;
    static final int LDAP_ATTRIBUTE_OR_VALUE_EXISTS = 20;
    static final int LDAP_INVALID_ATTRIBUTE_SYNTAX = 21;
    static final int LDAP_NO_SUCH_OBJECT = 32;
    static final int LDAP_ALIAS_PROBLEM = 33;
    static final int LDAP_INVALID_DN_SYNTAX = 34;
    static final int LDAP_IS_LEAF = 35;
    static final int LDAP_ALIAS_DEREFERENCING_PROBLEM = 36;
    static final int LDAP_INAPPROPRIATE_AUTHENTICATION = 48;
    static final int LDAP_INVALID_CREDENTIALS = 49;
    static final int LDAP_INSUFFICIENT_ACCESS_RIGHTS = 50;
    static final int LDAP_BUSY = 51;
    static final int LDAP_UNAVAILABLE = 52;
    static final int LDAP_UNWILLING_TO_PERFORM = 53;
    static final int LDAP_LOOP_DETECT = 54;
    static final int LDAP_NAMING_VIOLATION = 64;
    static final int LDAP_OBJECT_CLASS_VIOLATION = 65;
    static final int LDAP_NOT_ALLOWED_ON_NON_LEAF = 66;
    static final int LDAP_NOT_ALLOWED_ON_RDN = 67;
    static final int LDAP_ENTRY_ALREADY_EXISTS = 68;
    static final int LDAP_OBJECT_CLASS_MODS_PROHIBITED = 69;
    static final int LDAP_AFFECTS_MULTIPLE_DSAS = 71;
    static final int LDAP_OTHER = 80;
    static final String[] ldap_error_message;
    private Vector<LdapCtx> unsolicited;
    
    LdapClient(final String s, final int n, final String s2, final int n2, final int n3, final OutputStream outputStream, final PoolCallback pcb) throws NamingException {
        this.referenceCount = 1;
        this.authenticateCalled = false;
        this.unsolicited = new Vector<LdapCtx>(3);
        this.conn = new Connection(this, s, n, s2, n2, n3, outputStream);
        this.pcb = pcb;
        this.pooled = (pcb != null);
    }
    
    synchronized boolean authenticateCalled() {
        return this.authenticateCalled;
    }
    
    synchronized LdapResult authenticate(final boolean b, String s, Object o, final int n, final String s2, final Control[] array, final Hashtable<?, ?> hashtable) throws NamingException {
        final int readTimeout = this.conn.readTimeout;
        this.conn.readTimeout = this.conn.connectTimeout;
        LdapResult ldapResult = null;
        try {
            this.authenticateCalled = true;
            try {
                this.ensureOpen();
            }
            catch (final IOException rootCause) {
                final CommunicationException ex = new CommunicationException();
                ex.setRootCause(rootCause);
                throw ex;
            }
            switch (n) {
                case 3:
                case 32: {
                    this.isLdapv3 = true;
                    break;
                }
                case 2: {
                    this.isLdapv3 = false;
                    break;
                }
                default: {
                    throw new CommunicationException("Protocol version " + n + " not supported");
                }
            }
            Label_0624: {
                if (s2.equalsIgnoreCase("none") || s2.equalsIgnoreCase("anonymous")) {
                    Label_0294: {
                        if (b && n != 2 && n != 32) {
                            if (array == null || array.length <= 0) {
                                break Label_0294;
                            }
                        }
                        try {
                            ldapResult = this.ldapBind(s = null, (byte[])(o = null), array, null, false);
                            if (ldapResult.status == 0) {
                                this.conn.setBound();
                            }
                            break Label_0624;
                        }
                        catch (final IOException rootCause2) {
                            final CommunicationException ex2 = new CommunicationException("anonymous bind failed: " + this.conn.host + ":" + this.conn.port);
                            ex2.setRootCause(rootCause2);
                            throw ex2;
                        }
                    }
                    ldapResult = new LdapResult();
                    ldapResult.status = 0;
                }
                else {
                    if (!s2.equalsIgnoreCase("simple")) {
                        if (this.isLdapv3) {
                            try {
                                ldapResult = LdapSasl.saslBind(this, this.conn, this.conn.host, s, o, s2, hashtable, array);
                                if (ldapResult.status == 0) {
                                    this.conn.setBound();
                                }
                                break Label_0624;
                            }
                            catch (final IOException rootCause3) {
                                final CommunicationException ex3 = new CommunicationException("SASL bind failed: " + this.conn.host + ":" + this.conn.port);
                                ex3.setRootCause(rootCause3);
                                throw ex3;
                            }
                        }
                        throw new AuthenticationNotSupportedException(s2);
                    }
                    byte[] encodePassword = null;
                    try {
                        encodePassword = encodePassword(o, this.isLdapv3);
                        ldapResult = this.ldapBind(s, encodePassword, array, null, false);
                        if (ldapResult.status == 0) {
                            this.conn.setBound();
                        }
                    }
                    catch (final IOException rootCause4) {
                        final CommunicationException ex4 = new CommunicationException("simple bind failed: " + this.conn.host + ":" + this.conn.port);
                        ex4.setRootCause(rootCause4);
                        throw ex4;
                    }
                    finally {
                        if (encodePassword != o && encodePassword != null) {
                            for (int i = 0; i < encodePassword.length; ++i) {
                                encodePassword[i] = 0;
                            }
                        }
                    }
                }
            }
            if (b && ldapResult.status == 2 && n == 32 && (s2.equalsIgnoreCase("none") || s2.equalsIgnoreCase("anonymous") || s2.equalsIgnoreCase("simple"))) {
                byte[] encodePassword2 = null;
                try {
                    this.isLdapv3 = false;
                    encodePassword2 = encodePassword(o, false);
                    ldapResult = this.ldapBind(s, encodePassword2, array, null, false);
                    if (ldapResult.status == 0) {
                        this.conn.setBound();
                    }
                }
                catch (final IOException rootCause5) {
                    final CommunicationException ex5 = new CommunicationException(s2 + ":" + this.conn.host + ":" + this.conn.port);
                    ex5.setRootCause(rootCause5);
                    throw ex5;
                }
                finally {
                    if (encodePassword2 != o && encodePassword2 != null) {
                        for (int j = 0; j < encodePassword2.length; ++j) {
                            encodePassword2[j] = 0;
                        }
                    }
                }
            }
            if (ldapResult.status == 32) {
                throw new AuthenticationException(getErrorMessage(ldapResult.status, ldapResult.errorMessage));
            }
            this.conn.setV3(this.isLdapv3);
            return ldapResult;
        }
        finally {
            this.conn.readTimeout = readTimeout;
        }
    }
    
    public synchronized LdapResult ldapBind(final String s, final byte[] array, final Control[] array2, final String s2, final boolean b) throws IOException, NamingException {
        this.ensureOpen();
        this.conn.abandonOutstandingReqs(null);
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        final LdapResult ldapResult = new LdapResult();
        ldapResult.status = 1;
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.beginSeq(96);
        berEncoder.encodeInt(this.isLdapv3 ? 3 : 2);
        berEncoder.encodeString(s, this.isLdapv3);
        if (s2 != null) {
            berEncoder.beginSeq(163);
            berEncoder.encodeString(s2, this.isLdapv3);
            if (array != null) {
                berEncoder.encodeOctetString(array, 4);
            }
            berEncoder.endSeq();
        }
        else if (array != null) {
            berEncoder.encodeOctetString(array, 128);
        }
        else {
            berEncoder.encodeOctetString(null, 128, 0, 0);
        }
        berEncoder.endSeq();
        if (this.isLdapv3) {
            encodeControls(berEncoder, array2);
        }
        berEncoder.endSeq();
        final LdapRequest writeRequest = this.conn.writeRequest(berEncoder, msgId, b);
        if (array != null) {
            berEncoder.reset();
        }
        final BerDecoder reply = this.conn.readReply(writeRequest);
        reply.parseSeq(null);
        reply.parseInt();
        if (reply.parseByte() != 97) {
            return ldapResult;
        }
        reply.parseLength();
        parseResult(reply, ldapResult, this.isLdapv3);
        if (this.isLdapv3 && reply.bytesLeft() > 0 && reply.peekByte() == 135) {
            ldapResult.serverCreds = reply.parseOctetString(135, null);
        }
        ldapResult.resControls = (this.isLdapv3 ? parseControls(reply) : null);
        this.conn.removeRequest(writeRequest);
        return ldapResult;
    }
    
    boolean usingSaslStreams() {
        return this.conn.inStream instanceof SaslInputStream;
    }
    
    boolean isUpgradedToStartTls() {
        return this.conn.isUpgradedToStartTls();
    }
    
    synchronized void incRefCount() {
        ++this.referenceCount;
    }
    
    private static byte[] encodePassword(Object o, final boolean b) throws IOException {
        if (o instanceof char[]) {
            o = new String((char[])o);
        }
        if (!(o instanceof String)) {
            return (byte[])o;
        }
        if (b) {
            return ((String)o).getBytes("UTF8");
        }
        return ((String)o).getBytes("8859_1");
    }
    
    synchronized void close(final Control[] array, final boolean b) {
        --this.referenceCount;
        if (this.referenceCount <= 0) {
            if (!this.pooled) {
                this.conn.cleanup(array, false);
            }
            else if (b) {
                this.conn.cleanup(array, false);
                this.pcb.removePooledConnection(this);
            }
            else {
                this.pcb.releasePooledConnection(this);
            }
        }
    }
    
    private void forceClose(final boolean b) {
        this.referenceCount = 0;
        this.conn.cleanup(null, false);
        if (b) {
            this.pcb.removePooledConnection(this);
        }
    }
    
    @Override
    protected void finalize() {
        this.forceClose(this.pooled);
    }
    
    @Override
    public synchronized void closeConnection() {
        this.forceClose(false);
    }
    
    void processConnectionClosure() {
        if (this.unsolicited.size() > 0) {
            String string;
            if (this.conn != null) {
                string = this.conn.host + ":" + this.conn.port + " connection closed";
            }
            else {
                string = "Connection closed";
            }
            this.notifyUnsolicited(new CommunicationException(string));
        }
        if (this.pooled) {
            this.pcb.removePooledConnection(this);
        }
    }
    
    LdapResult search(final String s, final int n, final int n2, final int n3, final int n4, final boolean b, final String[] array, final String s2, final int n5, final Control[] array2, final Hashtable<String, Boolean> hashtable, final boolean b2, final int n6) throws IOException, NamingException {
        this.ensureOpen();
        LdapResult searchReply = new LdapResult();
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.beginSeq(99);
        berEncoder.encodeString((s == null) ? "" : s, this.isLdapv3);
        berEncoder.encodeInt(n, 10);
        berEncoder.encodeInt(n2, 10);
        berEncoder.encodeInt(n3);
        berEncoder.encodeInt(n4);
        berEncoder.encodeBoolean(b);
        Filter.encodeFilterString(berEncoder, s2, this.isLdapv3);
        berEncoder.beginSeq(48);
        berEncoder.encodeStringArray(array, this.isLdapv3);
        berEncoder.endSeq();
        berEncoder.endSeq();
        if (this.isLdapv3) {
            encodeControls(berEncoder, array2);
        }
        berEncoder.endSeq();
        final LdapRequest writeRequest = this.conn.writeRequest(berEncoder, msgId, false, n6);
        searchReply.msgId = msgId;
        searchReply.status = 0;
        if (b2) {
            searchReply = this.getSearchReply(writeRequest, n5, searchReply, hashtable);
        }
        return searchReply;
    }
    
    void clearSearchReply(final LdapResult ldapResult, final Control[] array) {
        if (ldapResult != null) {
            final LdapRequest request = this.conn.findRequest(ldapResult.msgId);
            if (request == null) {
                return;
            }
            if (request.hasSearchCompleted()) {
                this.conn.removeRequest(request);
            }
            else {
                this.conn.abandonRequest(request, array);
            }
        }
    }
    
    LdapResult getSearchReply(final int n, final LdapResult ldapResult, final Hashtable<String, Boolean> hashtable) throws IOException, NamingException {
        this.ensureOpen();
        final LdapRequest request;
        if ((request = this.conn.findRequest(ldapResult.msgId)) == null) {
            return null;
        }
        return this.getSearchReply(request, n, ldapResult, hashtable);
    }
    
    private LdapResult getSearchReply(final LdapRequest ldapRequest, int n, final LdapResult ldapResult, final Hashtable<String, Boolean> hashtable) throws IOException, NamingException {
        if (n == 0) {
            n = Integer.MAX_VALUE;
        }
        if (ldapResult.entries != null) {
            ldapResult.entries.setSize(0);
        }
        else {
            ldapResult.entries = new Vector<LdapEntry>((n == Integer.MAX_VALUE) ? 32 : n);
        }
        if (ldapResult.referrals != null) {
            ldapResult.referrals.setSize(0);
        }
        int i = 0;
        while (i < n) {
            final BerDecoder reply = this.conn.readReply(ldapRequest);
            reply.parseSeq(null);
            reply.parseInt();
            final int seq = reply.parseSeq(null);
            if (seq == 100) {
                final BasicAttributes basicAttributes = new BasicAttributes(true);
                final LdapEntry ldapEntry = new LdapEntry(reply.parseString(this.isLdapv3), basicAttributes);
                final int[] array = { 0 };
                reply.parseSeq(array);
                while (reply.getParsePosition() < reply.getParsePosition() + array[0] && reply.bytesLeft() > 0) {
                    basicAttributes.put(this.parseAttribute(reply, hashtable));
                }
                ldapEntry.respCtls = (this.isLdapv3 ? parseControls(reply) : null);
                ldapResult.entries.addElement(ldapEntry);
                ++i;
            }
            else if (seq == 115 && this.isLdapv3) {
                final Vector<String> vector = new Vector<String>(4);
                if (reply.peekByte() == 48) {
                    reply.parseSeq(null);
                }
                while (reply.bytesLeft() > 0 && reply.peekByte() == 4) {
                    vector.addElement(reply.parseString(this.isLdapv3));
                }
                if (ldapResult.referrals == null) {
                    ldapResult.referrals = new Vector<Vector<String>>(4);
                }
                ldapResult.referrals.addElement(vector);
                ldapResult.resControls = (this.isLdapv3 ? parseControls(reply) : null);
            }
            else if (seq == 120) {
                this.parseExtResponse(reply, ldapResult);
            }
            else {
                if (seq == 101) {
                    parseResult(reply, ldapResult, this.isLdapv3);
                    ldapResult.resControls = (this.isLdapv3 ? parseControls(reply) : null);
                    this.conn.removeRequest(ldapRequest);
                    return ldapResult;
                }
                continue;
            }
        }
        return ldapResult;
    }
    
    private Attribute parseAttribute(final BerDecoder berDecoder, final Hashtable<String, Boolean> hashtable) throws IOException {
        final int[] array = { 0 };
        berDecoder.parseSeq(null);
        final String string = berDecoder.parseString(this.isLdapv3);
        final boolean binaryValued = this.isBinaryValued(string, hashtable);
        final LdapAttribute ldapAttribute = new LdapAttribute(string);
        if (berDecoder.parseSeq(array) == 49) {
            int n = array[0];
            while (berDecoder.bytesLeft() > 0 && n > 0) {
                try {
                    n -= this.parseAttributeValue(berDecoder, ldapAttribute, binaryValued);
                    continue;
                }
                catch (final IOException ex) {
                    berDecoder.seek(n);
                }
                break;
            }
        }
        else {
            berDecoder.seek(array[0]);
        }
        return ldapAttribute;
    }
    
    private int parseAttributeValue(final BerDecoder berDecoder, final Attribute attribute, final boolean b) throws IOException {
        final int[] array = { 0 };
        if (b) {
            attribute.add(berDecoder.parseOctetString(berDecoder.peekByte(), array));
        }
        else {
            attribute.add(berDecoder.parseStringWithTag(4, this.isLdapv3, array));
        }
        return array[0];
    }
    
    private boolean isBinaryValued(final String s, final Hashtable<String, Boolean> hashtable) {
        final String lowerCase = s.toLowerCase(Locale.ENGLISH);
        return lowerCase.indexOf(";binary") != -1 || LdapClient.defaultBinaryAttrs.containsKey(lowerCase) || (hashtable != null && hashtable.containsKey(lowerCase));
    }
    
    static void parseResult(final BerDecoder berDecoder, final LdapResult ldapResult, final boolean b) throws IOException {
        ldapResult.status = berDecoder.parseEnumeration();
        ldapResult.matchedDN = berDecoder.parseString(b);
        ldapResult.errorMessage = berDecoder.parseString(b);
        if (b && berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 163) {
            final Vector vector = new Vector(4);
            final int[] array = { 0 };
            berDecoder.parseSeq(array);
            while (berDecoder.getParsePosition() < berDecoder.getParsePosition() + array[0] && berDecoder.bytesLeft() > 0) {
                vector.addElement(berDecoder.parseString(b));
            }
            if (ldapResult.referrals == null) {
                ldapResult.referrals = new Vector<Vector<String>>(4);
            }
            ldapResult.referrals.addElement(vector);
        }
    }
    
    static Vector<Control> parseControls(final BerDecoder berDecoder) throws IOException {
        if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 160) {
            final Vector vector = new Vector(4);
            boolean boolean1 = false;
            byte[] octetString = null;
            final int[] array = { 0 };
            berDecoder.parseSeq(array);
            while (berDecoder.getParsePosition() < berDecoder.getParsePosition() + array[0] && berDecoder.bytesLeft() > 0) {
                berDecoder.parseSeq(null);
                final String string = berDecoder.parseString(true);
                if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 1) {
                    boolean1 = berDecoder.parseBoolean();
                }
                if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 4) {
                    octetString = berDecoder.parseOctetString(4, null);
                }
                if (string != null) {
                    vector.addElement(new BasicControl(string, boolean1, octetString));
                }
            }
            return vector;
        }
        return null;
    }
    
    private void parseExtResponse(final BerDecoder berDecoder, final LdapResult ldapResult) throws IOException {
        parseResult(berDecoder, ldapResult, this.isLdapv3);
        if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 138) {
            ldapResult.extensionId = berDecoder.parseStringWithTag(138, this.isLdapv3, null);
        }
        if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 139) {
            ldapResult.extensionValue = berDecoder.parseOctetString(139, null);
        }
        ldapResult.resControls = parseControls(berDecoder);
    }
    
    static void encodeControls(final BerEncoder berEncoder, final Control[] array) throws IOException {
        if (array == null || array.length == 0) {
            return;
        }
        berEncoder.beginSeq(160);
        for (int i = 0; i < array.length; ++i) {
            berEncoder.beginSeq(48);
            berEncoder.encodeString(array[i].getID(), true);
            if (array[i].isCritical()) {
                berEncoder.encodeBoolean(true);
            }
            final byte[] encodedValue;
            if ((encodedValue = array[i].getEncodedValue()) != null) {
                berEncoder.encodeOctetString(encodedValue, 4);
            }
            berEncoder.endSeq();
        }
        berEncoder.endSeq();
    }
    
    private LdapResult processReply(final LdapRequest ldapRequest, final LdapResult ldapResult, final int n) throws IOException, NamingException {
        final BerDecoder reply = this.conn.readReply(ldapRequest);
        reply.parseSeq(null);
        reply.parseInt();
        if (reply.parseByte() != n) {
            return ldapResult;
        }
        reply.parseLength();
        parseResult(reply, ldapResult, this.isLdapv3);
        ldapResult.resControls = (this.isLdapv3 ? parseControls(reply) : null);
        this.conn.removeRequest(ldapRequest);
        return ldapResult;
    }
    
    LdapResult modify(final String s, final int[] array, final Attribute[] array2, final Control[] array3) throws IOException, NamingException {
        this.ensureOpen();
        final LdapResult ldapResult = new LdapResult();
        ldapResult.status = 1;
        if (s == null || array.length != array2.length) {
            return ldapResult;
        }
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.beginSeq(102);
        berEncoder.encodeString(s, this.isLdapv3);
        berEncoder.beginSeq(48);
        for (int i = 0; i < array.length; ++i) {
            berEncoder.beginSeq(48);
            berEncoder.encodeInt(array[i], 10);
            if (array[i] == 0 && hasNoValue(array2[i])) {
                throw new InvalidAttributeValueException("'" + array2[i].getID() + "' has no values.");
            }
            this.encodeAttribute(berEncoder, array2[i]);
            berEncoder.endSeq();
        }
        berEncoder.endSeq();
        berEncoder.endSeq();
        if (this.isLdapv3) {
            encodeControls(berEncoder, array3);
        }
        berEncoder.endSeq();
        return this.processReply(this.conn.writeRequest(berEncoder, msgId), ldapResult, 103);
    }
    
    private void encodeAttribute(final BerEncoder berEncoder, final Attribute attribute) throws IOException, NamingException {
        berEncoder.beginSeq(48);
        berEncoder.encodeString(attribute.getID(), this.isLdapv3);
        berEncoder.beginSeq(49);
        final NamingEnumeration<?> all = attribute.getAll();
        while (all.hasMore()) {
            final Object next = all.next();
            if (next instanceof String) {
                berEncoder.encodeString((String)next, this.isLdapv3);
            }
            else if (next instanceof byte[]) {
                berEncoder.encodeOctetString((byte[])next, 4);
            }
            else {
                if (next == null) {
                    continue;
                }
                throw new InvalidAttributeValueException("Malformed '" + attribute.getID() + "' attribute value");
            }
        }
        berEncoder.endSeq();
        berEncoder.endSeq();
    }
    
    private static boolean hasNoValue(final Attribute attribute) throws NamingException {
        return attribute.size() == 0 || (attribute.size() == 1 && attribute.get() == null);
    }
    
    LdapResult add(final LdapEntry ldapEntry, final Control[] array) throws IOException, NamingException {
        this.ensureOpen();
        final LdapResult ldapResult = new LdapResult();
        ldapResult.status = 1;
        if (ldapEntry == null || ldapEntry.DN == null) {
            return ldapResult;
        }
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.beginSeq(104);
        berEncoder.encodeString(ldapEntry.DN, this.isLdapv3);
        berEncoder.beginSeq(48);
        final NamingEnumeration<? extends Attribute> all = ldapEntry.attributes.getAll();
        while (all.hasMore()) {
            final Attribute attribute = (Attribute)all.next();
            if (hasNoValue(attribute)) {
                throw new InvalidAttributeValueException("'" + attribute.getID() + "' has no values.");
            }
            this.encodeAttribute(berEncoder, attribute);
        }
        berEncoder.endSeq();
        berEncoder.endSeq();
        if (this.isLdapv3) {
            encodeControls(berEncoder, array);
        }
        berEncoder.endSeq();
        return this.processReply(this.conn.writeRequest(berEncoder, msgId), ldapResult, 105);
    }
    
    LdapResult delete(final String s, final Control[] array) throws IOException, NamingException {
        this.ensureOpen();
        final LdapResult ldapResult = new LdapResult();
        ldapResult.status = 1;
        if (s == null) {
            return ldapResult;
        }
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.encodeString(s, 74, this.isLdapv3);
        if (this.isLdapv3) {
            encodeControls(berEncoder, array);
        }
        berEncoder.endSeq();
        return this.processReply(this.conn.writeRequest(berEncoder, msgId), ldapResult, 107);
    }
    
    LdapResult moddn(final String s, final String s2, final boolean b, final String s3, final Control[] array) throws IOException, NamingException {
        this.ensureOpen();
        final boolean b2 = s3 != null && s3.length() > 0;
        final LdapResult ldapResult = new LdapResult();
        ldapResult.status = 1;
        if (s == null || s2 == null) {
            return ldapResult;
        }
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.beginSeq(108);
        berEncoder.encodeString(s, this.isLdapv3);
        berEncoder.encodeString(s2, this.isLdapv3);
        berEncoder.encodeBoolean(b);
        if (this.isLdapv3 && b2) {
            berEncoder.encodeString(s3, 128, this.isLdapv3);
        }
        berEncoder.endSeq();
        if (this.isLdapv3) {
            encodeControls(berEncoder, array);
        }
        berEncoder.endSeq();
        return this.processReply(this.conn.writeRequest(berEncoder, msgId), ldapResult, 109);
    }
    
    LdapResult compare(final String s, final String s2, final String s3, final Control[] array) throws IOException, NamingException {
        this.ensureOpen();
        final LdapResult ldapResult = new LdapResult();
        ldapResult.status = 1;
        if (s == null || s2 == null || s3 == null) {
            return ldapResult;
        }
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.beginSeq(110);
        berEncoder.encodeString(s, this.isLdapv3);
        berEncoder.beginSeq(48);
        berEncoder.encodeString(s2, this.isLdapv3);
        final byte[] array2 = this.isLdapv3 ? s3.getBytes("UTF8") : s3.getBytes("8859_1");
        berEncoder.encodeOctetString(Filter.unescapeFilterValue(array2, 0, array2.length), 4);
        berEncoder.endSeq();
        berEncoder.endSeq();
        if (this.isLdapv3) {
            encodeControls(berEncoder, array);
        }
        berEncoder.endSeq();
        return this.processReply(this.conn.writeRequest(berEncoder, msgId), ldapResult, 111);
    }
    
    LdapResult extendedOp(final String s, final byte[] array, final Control[] array2, final boolean b) throws IOException, NamingException {
        this.ensureOpen();
        final LdapResult ldapResult = new LdapResult();
        ldapResult.status = 1;
        if (s == null) {
            return ldapResult;
        }
        final BerEncoder berEncoder = new BerEncoder();
        final int msgId = this.conn.getMsgId();
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(msgId);
        berEncoder.beginSeq(119);
        berEncoder.encodeString(s, 128, this.isLdapv3);
        if (array != null) {
            berEncoder.encodeOctetString(array, 129);
        }
        berEncoder.endSeq();
        encodeControls(berEncoder, array2);
        berEncoder.endSeq();
        final LdapRequest writeRequest = this.conn.writeRequest(berEncoder, msgId, b);
        final BerDecoder reply = this.conn.readReply(writeRequest);
        reply.parseSeq(null);
        reply.parseInt();
        if (reply.parseByte() != 120) {
            return ldapResult;
        }
        reply.parseLength();
        this.parseExtResponse(reply, ldapResult);
        this.conn.removeRequest(writeRequest);
        return ldapResult;
    }
    
    static String getErrorMessage(final int n, final String s) {
        String s2 = "[LDAP: error code " + n;
        if (s != null && s.length() != 0) {
            s2 = s2 + " - " + s + "]";
        }
        else {
            try {
                if (LdapClient.ldap_error_message[n] != null) {
                    s2 = s2 + " - " + LdapClient.ldap_error_message[n] + "]";
                }
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                s2 += "]";
            }
        }
        return s2;
    }
    
    void addUnsolicited(final LdapCtx ldapCtx) {
        this.unsolicited.addElement(ldapCtx);
    }
    
    void removeUnsolicited(final LdapCtx ldapCtx) {
        this.unsolicited.removeElement(ldapCtx);
    }
    
    void processUnsolicited(final BerDecoder berDecoder) {
        try {
            final LdapResult ldapResult = new LdapResult();
            berDecoder.parseSeq(null);
            berDecoder.parseInt();
            if (berDecoder.parseByte() != 120) {
                throw new IOException("Unsolicited Notification must be an Extended Response");
            }
            berDecoder.parseLength();
            this.parseExtResponse(berDecoder, ldapResult);
            if ("1.3.6.1.4.1.1466.20036".equals(ldapResult.extensionId)) {
                this.forceClose(this.pooled);
            }
            Object o = null;
            synchronized (this.unsolicited) {
                if (this.unsolicited.size() > 0) {
                    final LdapCtx ldapCtx = this.unsolicited.elementAt(0);
                    o = new UnsolicitedResponseImpl(ldapResult.extensionId, ldapResult.extensionValue, ldapResult.referrals, ldapResult.status, ldapResult.errorMessage, ldapResult.matchedDN, (Control[])((ldapResult.resControls != null) ? ldapCtx.convertControls(ldapResult.resControls) : null));
                }
            }
            if (o != null) {
                this.notifyUnsolicited(o);
                if ("1.3.6.1.4.1.1466.20036".equals(ldapResult.extensionId)) {
                    this.notifyUnsolicited(new CommunicationException("Connection closed"));
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex = new CommunicationException("Problem parsing unsolicited notification");
            ex.setRootCause(rootCause);
            this.notifyUnsolicited(ex);
        }
        catch (final NamingException ex2) {
            this.notifyUnsolicited(ex2);
        }
    }
    
    private void notifyUnsolicited(final Object o) {
        final Vector vector;
        synchronized (this.unsolicited) {
            vector = new Vector((Collection<? extends E>)this.unsolicited);
            if (o instanceof NamingException) {
                this.unsolicited.setSize(0);
            }
        }
        for (int i = 0; i < vector.size(); ++i) {
            ((LdapCtx)vector.elementAt(i)).fireUnsolicited(o);
        }
    }
    
    private void ensureOpen() throws IOException {
        if (this.conn != null && this.conn.useable) {
            return;
        }
        if (this.conn != null && this.conn.closureReason != null) {
            throw this.conn.closureReason;
        }
        throw new IOException("connection closed");
    }
    
    static LdapClient getInstance(final boolean b, final String s, final int n, final String s2, final int n2, final int n3, final OutputStream outputStream, final int n4, final String s3, final Control[] array, final String s4, final String s5, final Object o, final Hashtable<?, ?> hashtable) throws NamingException {
        if (b && LdapPoolManager.isPoolingAllowed(s2, outputStream, s3, s4, hashtable)) {
            final LdapClient ldapClient = LdapPoolManager.getLdapClient(s, n, s2, n2, n3, outputStream, n4, s3, array, s4, s5, o, hashtable);
            ldapClient.referenceCount = 1;
            return ldapClient;
        }
        return new LdapClient(s, n, s2, n2, n3, outputStream, null);
    }
    
    static {
        (defaultBinaryAttrs = new Hashtable<String, Boolean>(23, 0.75f)).put("userpassword", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("javaserializeddata", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("javaserializedobject", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("jpegphoto", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("audio", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("thumbnailphoto", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("thumbnaillogo", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("usercertificate", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("cacertificate", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("certificaterevocationlist", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("authorityrevocationlist", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("crosscertificatepair", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("photo", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("personalsignature", Boolean.TRUE);
        LdapClient.defaultBinaryAttrs.put("x500uniqueidentifier", Boolean.TRUE);
        ldap_error_message = new String[] { "Success", "Operations Error", "Protocol Error", "Timelimit Exceeded", "Sizelimit Exceeded", "Compare False", "Compare True", "Authentication Method Not Supported", "Strong Authentication Required", null, "Referral", "Administrative Limit Exceeded", "Unavailable Critical Extension", "Confidentiality Required", "SASL Bind In Progress", null, "No Such Attribute", "Undefined Attribute Type", "Inappropriate Matching", "Constraint Violation", "Attribute Or Value Exists", "Invalid Attribute Syntax", null, null, null, null, null, null, null, null, null, null, "No Such Object", "Alias Problem", "Invalid DN Syntax", null, "Alias Dereferencing Problem", null, null, null, null, null, null, null, null, null, null, null, "Inappropriate Authentication", "Invalid Credentials", "Insufficient Access Rights", "Busy", "Unavailable", "Unwilling To Perform", "Loop Detect", null, null, null, null, null, null, null, null, null, "Naming Violation", "Object Class Violation", "Not Allowed On Non-leaf", "Not Allowed On RDN", "Entry Already Exists", "Object Class Modifications Prohibited", null, "Affects Multiple DSAs", null, null, null, null, null, null, null, null, "Other", null, null, null, null, null, null, null, null, null, null };
    }
}

package com.sun.jndi.ldap;

import javax.naming.ldap.UnsolicitedNotificationListener;
import javax.naming.event.NamingListener;
import com.sun.jndi.ldap.ext.StartTlsResponseImpl;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.ServiceUnavailableException;
import javax.naming.TimeLimitExceededException;
import javax.naming.SizeLimitExceededException;
import javax.naming.ContextNotEmptyException;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.NoPermissionException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.AuthenticationException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.directory.AttributeInUseException;
import java.util.Iterator;
import com.sun.jndi.ldap.sasl.LdapSasl;
import java.util.HashSet;
import java.util.Collections;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Locale;
import java.util.StringTokenizer;
import com.sun.jndi.toolkit.dir.SearchFilter;
import javax.naming.ConfigurationException;
import javax.naming.directory.SearchResult;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.ModificationItem;
import javax.naming.Binding;
import javax.naming.LimitExceededException;
import javax.naming.NameClassPair;
import javax.naming.spi.DirectoryManager;
import javax.naming.directory.SearchControls;
import javax.naming.NamingEnumeration;
import java.util.List;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.PartialResultException;
import javax.naming.InvalidNameException;
import javax.naming.CompositeName;
import javax.naming.directory.Attribute;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.directory.Attributes;
import com.sun.jndi.toolkit.ctx.Continuation;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.io.OutputStream;
import java.util.Vector;
import javax.naming.Name;
import java.util.Hashtable;
import com.sun.jndi.toolkit.dir.HierMemDirCtx;
import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;
import javax.naming.NameParser;
import java.util.Set;
import javax.naming.ldap.LdapContext;
import javax.naming.event.EventDirContext;
import com.sun.jndi.toolkit.ctx.ComponentDirContext;

public final class LdapCtx extends ComponentDirContext implements EventDirContext, LdapContext
{
    private static final boolean debug = false;
    private static final boolean HARD_CLOSE = true;
    private static final boolean SOFT_CLOSE = false;
    public static final int DEFAULT_PORT = 389;
    public static final int DEFAULT_SSL_PORT = 636;
    public static final String DEFAULT_HOST = "localhost";
    private static final boolean DEFAULT_DELETE_RDN = true;
    private static final boolean DEFAULT_TYPES_ONLY = false;
    private static final int DEFAULT_DEREF_ALIASES = 3;
    private static final int DEFAULT_LDAP_VERSION = 32;
    private static final int DEFAULT_BATCH_SIZE = 1;
    private static final int DEFAULT_REFERRAL_MODE = 3;
    private static final char DEFAULT_REF_SEPARATOR = '#';
    static final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final int DEFAULT_REFERRAL_LIMIT = 10;
    private static final String STARTTLS_REQ_OID = "1.3.6.1.4.1.1466.20037";
    private static final String[] SCHEMA_ATTRIBUTES;
    private static final String VERSION = "java.naming.ldap.version";
    private static final String BINARY_ATTRIBUTES = "java.naming.ldap.attributes.binary";
    private static final String DELETE_RDN = "java.naming.ldap.deleteRDN";
    private static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
    private static final String TYPES_ONLY = "java.naming.ldap.typesOnly";
    private static final String REF_SEPARATOR = "java.naming.ldap.ref.separator";
    private static final String SOCKET_FACTORY = "java.naming.ldap.factory.socket";
    static final String BIND_CONTROLS = "java.naming.ldap.control.connect";
    private static final String REFERRAL_LIMIT = "java.naming.ldap.referral.limit";
    private static final String TRACE_BER = "com.sun.jndi.ldap.trace.ber";
    private static final String NETSCAPE_SCHEMA_BUG = "com.sun.jndi.ldap.netscape.schemaBugs";
    private static final String OLD_NETSCAPE_SCHEMA_BUG = "com.sun.naming.netscape.schemaBugs";
    private static final String CONNECT_TIMEOUT = "com.sun.jndi.ldap.connect.timeout";
    private static final String READ_TIMEOUT = "com.sun.jndi.ldap.read.timeout";
    private static final String ENABLE_POOL = "com.sun.jndi.ldap.connect.pool";
    private static final String DOMAIN_NAME = "com.sun.jndi.ldap.domainname";
    private static final String WAIT_FOR_REPLY = "com.sun.jndi.ldap.search.waitForReply";
    private static final String REPLY_QUEUE_SIZE = "com.sun.jndi.ldap.search.replyQueueSize";
    private static final String ALLOWED_MECHS_SP = "jdk.jndi.ldap.mechsAllowedToSendCredentials";
    private static final String ALLOWED_MECHS_SP_VALUE;
    private static final Set<String> MECHS_ALLOWED_BY_SP;
    private static final String UNSECURED_CRED_TRANSMIT_MSG = "Transmission of credentials over unsecured connection is not allowed";
    private static final NameParser parser;
    private static final ControlFactory myResponseControlFactory;
    private static final Control manageReferralControl;
    private static final HierMemDirCtx EMPTY_SCHEMA;
    int port_number;
    String hostname;
    LdapClient clnt;
    Hashtable<String, Object> envprops;
    int handleReferrals;
    boolean hasLdapsScheme;
    String currentDN;
    Name currentParsedDN;
    Vector<Control> respCtls;
    Control[] reqCtls;
    volatile boolean contextSeenStartTlsEnabled;
    private OutputStream trace;
    private boolean netscapeSchemaBug;
    private Control[] bindCtls;
    private int referralHopLimit;
    private Hashtable<String, DirContext> schemaTrees;
    private int batchSize;
    private boolean deleteRDN;
    private boolean typesOnly;
    private int derefAliases;
    private char addrEncodingSeparator;
    private Hashtable<String, Boolean> binaryAttrs;
    private int connectTimeout;
    private int readTimeout;
    private boolean waitForReply;
    private int replyQueueSize;
    private boolean useSsl;
    private boolean useDefaultPortNumber;
    private boolean parentIsLdapCtx;
    private int hopCount;
    private String url;
    private EventSupport eventSupport;
    private boolean unsolicited;
    private boolean sharable;
    private int enumCount;
    private boolean closeRequested;
    
    public LdapCtx(final String s, final String s2, final int port_number, final Hashtable<?, ?> hashtable, final boolean b) throws NamingException {
        this.hostname = null;
        this.clnt = null;
        this.envprops = null;
        this.handleReferrals = 3;
        this.hasLdapsScheme = false;
        this.respCtls = null;
        this.reqCtls = null;
        this.trace = null;
        this.netscapeSchemaBug = false;
        this.bindCtls = null;
        this.referralHopLimit = 10;
        this.schemaTrees = null;
        this.batchSize = 1;
        this.deleteRDN = true;
        this.typesOnly = false;
        this.derefAliases = 3;
        this.addrEncodingSeparator = '#';
        this.binaryAttrs = null;
        this.connectTimeout = -1;
        this.readTimeout = -1;
        this.waitForReply = true;
        this.replyQueueSize = -1;
        this.useSsl = false;
        this.useDefaultPortNumber = false;
        this.parentIsLdapCtx = false;
        this.hopCount = 1;
        this.url = null;
        this.unsolicited = false;
        this.sharable = true;
        this.enumCount = 0;
        this.closeRequested = false;
        this.hasLdapsScheme = b;
        this.useSsl = b;
        if (hashtable != null) {
            this.envprops = (Hashtable)hashtable.clone();
            if ("ssl".equals(this.envprops.get("java.naming.security.protocol"))) {
                this.useSsl = true;
            }
            this.trace = this.envprops.get("com.sun.jndi.ldap.trace.ber");
            if (hashtable.get("com.sun.jndi.ldap.netscape.schemaBugs") != null || hashtable.get("com.sun.naming.netscape.schemaBugs") != null) {
                this.netscapeSchemaBug = true;
            }
        }
        this.currentDN = ((s != null) ? s : "");
        this.currentParsedDN = LdapCtx.parser.parse(this.currentDN);
        this.hostname = ((s2 != null && s2.length() > 0) ? s2 : "localhost");
        if (this.hostname.charAt(0) == '[') {
            this.hostname = this.hostname.substring(1, this.hostname.length() - 1);
        }
        if (port_number > 0) {
            this.port_number = port_number;
        }
        else {
            this.port_number = (this.useSsl ? 636 : 389);
            this.useDefaultPortNumber = true;
        }
        this.schemaTrees = new Hashtable<String, DirContext>(11, 0.75f);
        this.initEnv();
        try {
            this.connect(false);
        }
        catch (final NamingException ex) {
            try {
                this.close();
            }
            catch (final Exception ex2) {}
            throw ex;
        }
    }
    
    LdapCtx(final LdapCtx ldapCtx, final String currentDN) throws NamingException {
        this.hostname = null;
        this.clnt = null;
        this.envprops = null;
        this.handleReferrals = 3;
        this.hasLdapsScheme = false;
        this.respCtls = null;
        this.reqCtls = null;
        this.trace = null;
        this.netscapeSchemaBug = false;
        this.bindCtls = null;
        this.referralHopLimit = 10;
        this.schemaTrees = null;
        this.batchSize = 1;
        this.deleteRDN = true;
        this.typesOnly = false;
        this.derefAliases = 3;
        this.addrEncodingSeparator = '#';
        this.binaryAttrs = null;
        this.connectTimeout = -1;
        this.readTimeout = -1;
        this.waitForReply = true;
        this.replyQueueSize = -1;
        this.useSsl = false;
        this.useDefaultPortNumber = false;
        this.parentIsLdapCtx = false;
        this.hopCount = 1;
        this.url = null;
        this.unsolicited = false;
        this.sharable = true;
        this.enumCount = 0;
        this.closeRequested = false;
        this.useSsl = ldapCtx.useSsl;
        this.hasLdapsScheme = ldapCtx.hasLdapsScheme;
        this.useDefaultPortNumber = ldapCtx.useDefaultPortNumber;
        this.hostname = ldapCtx.hostname;
        this.port_number = ldapCtx.port_number;
        this.currentDN = currentDN;
        if (ldapCtx.currentDN == this.currentDN) {
            this.currentParsedDN = ldapCtx.currentParsedDN;
        }
        else {
            this.currentParsedDN = LdapCtx.parser.parse(this.currentDN);
        }
        this.envprops = ldapCtx.envprops;
        this.schemaTrees = ldapCtx.schemaTrees;
        (this.clnt = ldapCtx.clnt).incRefCount();
        this.parentIsLdapCtx = ((currentDN != null && !currentDN.equals(ldapCtx.currentDN)) || ldapCtx.parentIsLdapCtx);
        this.trace = ldapCtx.trace;
        this.netscapeSchemaBug = ldapCtx.netscapeSchemaBug;
        this.initEnv();
    }
    
    @Override
    public LdapContext newInstance(final Control[] requestControls) throws NamingException {
        final LdapCtx ldapCtx = new LdapCtx(this, this.currentDN);
        ldapCtx.setRequestControls(requestControls);
        return ldapCtx;
    }
    
    @Override
    protected void c_bind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        this.c_bind(name, o, null, continuation);
    }
    
    @Override
    protected void c_bind(final Name name, final Object o, Attributes attributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        final Attributes attributes2 = attributes;
        try {
            this.ensureOpen();
            if (o == null) {
                if (attributes == null) {
                    throw new IllegalArgumentException("cannot bind null object with no attributes");
                }
            }
            else {
                attributes = Obj.determineBindAttrs(this.addrEncodingSeparator, o, attributes, false, name, this, this.envprops);
            }
            final String fullyQualifiedName = this.fullyQualifiedName(name);
            attributes = addRdnAttributes(fullyQualifiedName, attributes, attributes2 != attributes);
            final LdapResult add = this.clnt.add(new LdapEntry(fullyQualifiedName, attributes), this.reqCtls);
            this.respCtls = add.resControls;
            if (add.status != 0) {
                this.processReturnCode(add, name);
            }
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    ldapReferralContext.bind(name, o, attributes2);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw continuation.fillInException(ex3);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
    }
    
    @Override
    protected void c_rebind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        this.c_rebind(name, o, null, continuation);
    }
    
    @Override
    protected void c_rebind(final Name name, final Object o, Attributes attributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        final Attributes attributes2 = attributes;
        try {
            Attributes c_getAttributes = null;
            try {
                c_getAttributes = this.c_getAttributes(name, null, continuation);
            }
            catch (final NameNotFoundException ex) {}
            if (c_getAttributes == null) {
                this.c_bind(name, o, attributes, continuation);
                return;
            }
            if (attributes == null && o instanceof DirContext) {
                attributes = ((DirContext)o).getAttributes("");
            }
            final Attributes attributes3 = (Attributes)c_getAttributes.clone();
            if (attributes == null) {
                final Attribute value = c_getAttributes.get(Obj.JAVA_ATTRIBUTES[0]);
                if (value != null) {
                    final Attribute attribute = (Attribute)value.clone();
                    for (int i = 0; i < Obj.JAVA_OBJECT_CLASSES.length; ++i) {
                        attribute.remove(Obj.JAVA_OBJECT_CLASSES_LOWER[i]);
                        attribute.remove(Obj.JAVA_OBJECT_CLASSES[i]);
                    }
                    c_getAttributes.put(attribute);
                }
                for (int j = 1; j < Obj.JAVA_ATTRIBUTES.length; ++j) {
                    c_getAttributes.remove(Obj.JAVA_ATTRIBUTES[j]);
                }
                attributes = c_getAttributes;
            }
            if (o != null) {
                attributes = Obj.determineBindAttrs(this.addrEncodingSeparator, o, attributes, attributes2 != attributes, name, this, this.envprops);
            }
            final String fullyQualifiedName = this.fullyQualifiedName(name);
            LdapResult ldapResult = this.clnt.delete(fullyQualifiedName, this.reqCtls);
            this.respCtls = ldapResult.resControls;
            if (ldapResult.status != 0) {
                this.processReturnCode(ldapResult, name);
                return;
            }
            NamingException | IOException ex2 = null;
            try {
                attributes = addRdnAttributes(fullyQualifiedName, attributes, attributes2 != attributes);
                ldapResult = this.clnt.add(new LdapEntry(fullyQualifiedName, attributes), this.reqCtls);
                if (ldapResult.resControls != null) {
                    this.respCtls = appendVector(this.respCtls, ldapResult.resControls);
                }
            }
            catch (final NamingException | IOException ex3) {
                ex2 = ex3;
            }
            if ((ex2 != null && !(ex2 instanceof LdapReferralException)) || ldapResult.status != 0) {
                final LdapResult add = this.clnt.add(new LdapEntry(fullyQualifiedName, attributes3), this.reqCtls);
                if (add.resControls != null) {
                    this.respCtls = appendVector(this.respCtls, add.resControls);
                }
                if (ex2 == null) {
                    this.processReturnCode(ldapResult, name);
                }
            }
            if (ex2 instanceof NamingException) {
                throw (NamingException)ex2;
            }
            if (ex2 instanceof IOException) {
                throw (IOException)ex2;
            }
        }
        catch (final LdapReferralException ex4) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex4);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex4.getReferralContext(this.envprops, this.bindCtls);
                try {
                    ldapReferralContext.rebind(name, o, attributes2);
                }
                catch (final LdapReferralException ex5) {
                    ex4 = ex5;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex6 = new CommunicationException(rootCause.getMessage());
            ex6.setRootCause(rootCause);
            throw continuation.fillInException(ex6);
        }
        catch (final NamingException ex7) {
            throw continuation.fillInException(ex7);
        }
    }
    
    @Override
    protected void c_unbind(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        try {
            this.ensureOpen();
            final String fullyQualifiedName = this.fullyQualifiedName(name);
            final LdapResult delete = this.clnt.delete(fullyQualifiedName, this.reqCtls);
            this.respCtls = delete.resControls;
            this.adjustDeleteStatus(fullyQualifiedName, delete);
            if (delete.status != 0) {
                this.processReturnCode(delete, name);
            }
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    ldapReferralContext.unbind(name);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw continuation.fillInException(ex3);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
    }
    
    @Override
    protected void c_rename(final Name name, final Name name2, final Continuation continuation) throws NamingException {
        String value = null;
        String fullyQualifiedName = null;
        continuation.setError(this, name);
        try {
            this.ensureOpen();
            Name name3;
            if (name.isEmpty()) {
                name3 = LdapCtx.parser.parse("");
            }
            else {
                final Name parse = LdapCtx.parser.parse(name.get(0));
                name3 = parse.getPrefix(parse.size() - 1);
            }
            Name parse2;
            if (name2 instanceof CompositeName) {
                parse2 = LdapCtx.parser.parse(name2.get(0));
            }
            else {
                parse2 = name2;
            }
            final Name prefix = parse2.getPrefix(parse2.size() - 1);
            if (!name3.equals(prefix)) {
                if (!this.clnt.isLdapv3) {
                    throw new InvalidNameException("LDAPv2 doesn't support changing the parent as a result of a rename");
                }
                fullyQualifiedName = this.fullyQualifiedName(prefix.toString());
            }
            value = parse2.get(parse2.size() - 1);
            final LdapResult moddn = this.clnt.moddn(this.fullyQualifiedName(name), value, this.deleteRDN, fullyQualifiedName, this.reqCtls);
            this.respCtls = moddn.resControls;
            if (moddn.status != 0) {
                this.processReturnCode(moddn, name);
            }
        }
        catch (final LdapReferralException ex) {
            ex.setNewRdn(value);
            if (fullyQualifiedName != null) {
                final PartialResultException ex2 = new PartialResultException("Cannot continue referral processing when newSuperior is nonempty: " + fullyQualifiedName);
                ex2.setRootCause(continuation.fillInException(ex));
                throw continuation.fillInException(ex2);
            }
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    ldapReferralContext.rename(name, name2);
                }
                catch (final LdapReferralException ex3) {
                    ex = ex3;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex4 = new CommunicationException(rootCause.getMessage());
            ex4.setRootCause(rootCause);
            throw continuation.fillInException(ex4);
        }
        catch (final NamingException ex5) {
            throw continuation.fillInException(ex5);
        }
    }
    
    @Override
    protected Context c_createSubcontext(final Name name, final Continuation continuation) throws NamingException {
        return this.c_createSubcontext(name, null, continuation);
    }
    
    @Override
    protected DirContext c_createSubcontext(final Name name, Attributes addRdnAttributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        final Attributes attributes = addRdnAttributes;
        try {
            this.ensureOpen();
            if (addRdnAttributes == null) {
                final BasicAttribute basicAttribute = new BasicAttribute(Obj.JAVA_ATTRIBUTES[0], Obj.JAVA_OBJECT_CLASSES[0]);
                basicAttribute.add("top");
                addRdnAttributes = new BasicAttributes(true);
                addRdnAttributes.put(basicAttribute);
            }
            final String fullyQualifiedName = this.fullyQualifiedName(name);
            addRdnAttributes = addRdnAttributes(fullyQualifiedName, addRdnAttributes, attributes != addRdnAttributes);
            final LdapResult add = this.clnt.add(new LdapEntry(fullyQualifiedName, addRdnAttributes), this.reqCtls);
            this.respCtls = add.resControls;
            if (add.status != 0) {
                this.processReturnCode(add, name);
                return null;
            }
            return new LdapCtx(this, fullyQualifiedName);
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    return ldapReferralContext.createSubcontext(name, attributes);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw continuation.fillInException(ex3);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
    }
    
    @Override
    protected void c_destroySubcontext(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        try {
            this.ensureOpen();
            final String fullyQualifiedName = this.fullyQualifiedName(name);
            final LdapResult delete = this.clnt.delete(fullyQualifiedName, this.reqCtls);
            this.respCtls = delete.resControls;
            this.adjustDeleteStatus(fullyQualifiedName, delete);
            if (delete.status != 0) {
                this.processReturnCode(delete, name);
            }
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    ldapReferralContext.destroySubcontext(name);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw continuation.fillInException(ex3);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
    }
    
    private static Attributes addRdnAttributes(final String s, Attributes attributes, boolean b) throws NamingException {
        if (s.equals("")) {
            return attributes;
        }
        final List<Rdn> rdns = new LdapName(s).getRdns();
        final NamingEnumeration<? extends Attribute> all = rdns.get(rdns.size() - 1).toAttributes().getAll();
        while (all.hasMore()) {
            final Attribute attribute = (Attribute)all.next();
            if (attributes.get(attribute.getID()) == null) {
                if (!attributes.isCaseIgnored() && containsIgnoreCase(attributes.getIDs(), attribute.getID())) {
                    continue;
                }
                if (!b) {
                    attributes = (Attributes)attributes.clone();
                    b = true;
                }
                attributes.put(attribute);
            }
        }
        return attributes;
    }
    
    private static boolean containsIgnoreCase(final NamingEnumeration<String> namingEnumeration, final String s) throws NamingException {
        while (namingEnumeration.hasMore()) {
            if (namingEnumeration.next().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
    
    private void adjustDeleteStatus(final String s, final LdapResult ldapResult) {
        if (ldapResult.status == 32 && ldapResult.matchedDN != null) {
            try {
                if (LdapCtx.parser.parse(s).size() - LdapCtx.parser.parse(ldapResult.matchedDN).size() == 1) {
                    ldapResult.status = 0;
                }
            }
            catch (final NamingException ex) {}
        }
    }
    
    private static <T> Vector<T> appendVector(Vector<T> vector, final Vector<T> vector2) {
        if (vector == null) {
            vector = vector2;
        }
        else {
            for (int i = 0; i < vector2.size(); ++i) {
                vector.addElement((T)vector2.elementAt(i));
            }
        }
        return vector;
    }
    
    @Override
    protected Object c_lookupLink(final Name name, final Continuation continuation) throws NamingException {
        return this.c_lookup(name, continuation);
    }
    
    @Override
    protected Object c_lookup(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        Object decodeObject = null;
        Attributes attributes;
        try {
            final SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(0);
            searchControls.setReturningAttributes(null);
            searchControls.setReturningObjFlag(true);
            final LdapResult doSearchOnce = this.doSearchOnce(name, "(objectClass=*)", searchControls, true);
            this.respCtls = doSearchOnce.resControls;
            if (doSearchOnce.status != 0) {
                this.processReturnCode(doSearchOnce, name);
            }
            if (doSearchOnce.entries == null || doSearchOnce.entries.size() != 1) {
                attributes = new BasicAttributes(true);
            }
            else {
                final LdapEntry ldapEntry = doSearchOnce.entries.elementAt(0);
                attributes = ldapEntry.attributes;
                final Vector<Control> respCtls = ldapEntry.respCtls;
                if (respCtls != null) {
                    appendVector(this.respCtls, respCtls);
                }
            }
            if (attributes.get(Obj.JAVA_ATTRIBUTES[2]) != null) {
                decodeObject = Obj.decodeObject(attributes);
            }
            if (decodeObject == null) {
                decodeObject = new LdapCtx(this, this.fullyQualifiedName(name));
            }
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    return ldapReferralContext.lookup(name);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final NamingException ex3) {
            throw continuation.fillInException(ex3);
        }
        try {
            return DirectoryManager.getObjectInstance(decodeObject, name, this, this.envprops, attributes);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
        catch (final Exception rootCause) {
            final NamingException ex5 = new NamingException("problem generating object using object factory");
            ex5.setRootCause(rootCause);
            throw continuation.fillInException(ex5);
        }
    }
    
    @Override
    protected NamingEnumeration<NameClassPair> c_list(final Name name, final Continuation continuation) throws NamingException {
        final SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(new String[] { Obj.JAVA_ATTRIBUTES[0], Obj.JAVA_ATTRIBUTES[2] });
        searchControls.setReturningObjFlag(true);
        continuation.setError(this, name);
        LdapResult doSearch = null;
        try {
            doSearch = this.doSearch(name, "(objectClass=*)", searchControls, true, true);
            if (doSearch.status != 0 || doSearch.referrals != null) {
                this.processReturnCode(doSearch, name);
            }
            return new LdapNamingEnumeration(this, doSearch, name, continuation);
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    return ldapReferralContext.list(name);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final LimitExceededException ex3) {
            final LdapNamingEnumeration ldapNamingEnumeration = new LdapNamingEnumeration(this, doSearch, name, continuation);
            ldapNamingEnumeration.setNamingException(continuation.fillInException(ex3));
            return ldapNamingEnumeration;
        }
        catch (final PartialResultException ex4) {
            final LdapNamingEnumeration ldapNamingEnumeration2 = new LdapNamingEnumeration(this, doSearch, name, continuation);
            ldapNamingEnumeration2.setNamingException(continuation.fillInException(ex4));
            return ldapNamingEnumeration2;
        }
        catch (final NamingException ex5) {
            throw continuation.fillInException(ex5);
        }
    }
    
    @Override
    protected NamingEnumeration<Binding> c_listBindings(final Name name, final Continuation continuation) throws NamingException {
        final SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(null);
        searchControls.setReturningObjFlag(true);
        continuation.setError(this, name);
        LdapResult doSearch = null;
        try {
            doSearch = this.doSearch(name, "(objectClass=*)", searchControls, true, true);
            if (doSearch.status != 0 || doSearch.referrals != null) {
                this.processReturnCode(doSearch, name);
            }
            return new LdapBindingEnumeration(this, doSearch, name, continuation);
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    return ldapReferralContext.listBindings(name);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final LimitExceededException ex3) {
            final LdapBindingEnumeration ldapBindingEnumeration = new LdapBindingEnumeration(this, doSearch, name, continuation);
            ldapBindingEnumeration.setNamingException(continuation.fillInException(ex3));
            return ldapBindingEnumeration;
        }
        catch (final PartialResultException ex4) {
            final LdapBindingEnumeration ldapBindingEnumeration2 = new LdapBindingEnumeration(this, doSearch, name, continuation);
            ldapBindingEnumeration2.setNamingException(continuation.fillInException(ex4));
            return ldapBindingEnumeration2;
        }
        catch (final NamingException ex5) {
            throw continuation.fillInException(ex5);
        }
    }
    
    @Override
    protected NameParser c_getNameParser(final Name name, final Continuation continuation) throws NamingException {
        continuation.setSuccess();
        return LdapCtx.parser;
    }
    
    @Override
    public String getNameInNamespace() {
        return this.currentDN;
    }
    
    @Override
    public Name composeName(Name add, Name add2) throws NamingException {
        if (add instanceof LdapName && add2 instanceof LdapName) {
            final Name name = (Name)add2.clone();
            name.addAll(add);
            return new CompositeName().add(name.toString());
        }
        if (!(add instanceof CompositeName)) {
            add = new CompositeName().add(add.toString());
        }
        if (!(add2 instanceof CompositeName)) {
            add2 = new CompositeName().add(add2.toString());
        }
        final int n = add2.size() - 1;
        if (add.isEmpty() || add2.isEmpty() || add.get(0).equals("") || add2.get(n).equals("")) {
            return super.composeName(add, add2);
        }
        final Name name2 = (Name)add2.clone();
        name2.addAll(add);
        if (this.parentIsLdapCtx) {
            final String concatNames = concatNames(name2.get(n + 1), name2.get(n));
            name2.remove(n + 1);
            name2.remove(n);
            name2.add(n, concatNames);
        }
        return name2;
    }
    
    private String fullyQualifiedName(final Name name) {
        return name.isEmpty() ? this.currentDN : this.fullyQualifiedName(name.get(0));
    }
    
    private String fullyQualifiedName(final String s) {
        return concatNames(s, this.currentDN);
    }
    
    private static String concatNames(final String s, final String s2) {
        if (s == null || s.equals("")) {
            return s2;
        }
        if (s2 == null || s2.equals("")) {
            return s;
        }
        return s + "," + s2;
    }
    
    @Override
    protected Attributes c_getAttributes(final Name name, final String[] returningAttributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(0);
        searchControls.setReturningAttributes(returningAttributes);
        try {
            final LdapResult doSearchOnce = this.doSearchOnce(name, "(objectClass=*)", searchControls, true);
            this.respCtls = doSearchOnce.resControls;
            if (doSearchOnce.status != 0) {
                this.processReturnCode(doSearchOnce, name);
            }
            if (doSearchOnce.entries == null || doSearchOnce.entries.size() != 1) {
                return new BasicAttributes(true);
            }
            final LdapEntry ldapEntry = doSearchOnce.entries.elementAt(0);
            final Vector<Control> respCtls = ldapEntry.respCtls;
            if (respCtls != null) {
                appendVector(this.respCtls, respCtls);
            }
            this.setParents(ldapEntry.attributes, (Name)name.clone());
            return ldapEntry.attributes;
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    return ldapReferralContext.getAttributes(name, returningAttributes);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final NamingException ex3) {
            throw continuation.fillInException(ex3);
        }
    }
    
    @Override
    protected void c_modifyAttributes(final Name name, final int n, final Attributes attributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        try {
            this.ensureOpen();
            if (attributes == null || attributes.size() == 0) {
                return;
            }
            final String fullyQualifiedName = this.fullyQualifiedName(name);
            final int convertToLdapModCode = convertToLdapModCode(n);
            final int[] array = new int[attributes.size()];
            final Attribute[] array2 = new Attribute[attributes.size()];
            final NamingEnumeration<? extends Attribute> all = attributes.getAll();
            for (int n2 = 0; n2 < array.length && all.hasMore(); ++n2) {
                array[n2] = convertToLdapModCode;
                array2[n2] = all.next();
            }
            final LdapResult modify = this.clnt.modify(fullyQualifiedName, array, array2, this.reqCtls);
            this.respCtls = modify.resControls;
            if (modify.status != 0) {
                this.processReturnCode(modify, name);
            }
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    ldapReferralContext.modifyAttributes(name, n, attributes);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw continuation.fillInException(ex3);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
    }
    
    @Override
    protected void c_modifyAttributes(final Name name, final ModificationItem[] array, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        try {
            this.ensureOpen();
            if (array == null || array.length == 0) {
                return;
            }
            final String fullyQualifiedName = this.fullyQualifiedName(name);
            final int[] array2 = new int[array.length];
            final Attribute[] array3 = new Attribute[array.length];
            for (int i = 0; i < array2.length; ++i) {
                final ModificationItem modificationItem = array[i];
                array2[i] = convertToLdapModCode(modificationItem.getModificationOp());
                array3[i] = modificationItem.getAttribute();
            }
            final LdapResult modify = this.clnt.modify(fullyQualifiedName, array2, array3, this.reqCtls);
            this.respCtls = modify.resControls;
            if (modify.status != 0) {
                this.processReturnCode(modify, name);
            }
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    ldapReferralContext.modifyAttributes(name, array);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw continuation.fillInException(ex3);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
    }
    
    private static int convertToLdapModCode(final int n) {
        switch (n) {
            case 1: {
                return 0;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 1;
            }
            default: {
                throw new IllegalArgumentException("Invalid modification code");
            }
        }
    }
    
    @Override
    protected DirContext c_getSchema(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        try {
            return this.getSchemaTree(name);
        }
        catch (final NamingException ex) {
            throw continuation.fillInException(ex);
        }
    }
    
    @Override
    protected DirContext c_getSchemaClassDefinition(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        try {
            final Attribute value = this.c_getAttributes(name, new String[] { "objectclass" }, continuation).get("objectclass");
            if (value == null || value.size() == 0) {
                return LdapCtx.EMPTY_SCHEMA;
            }
            final Context context = (Context)this.c_getSchema(name, continuation).lookup("ClassDefinition");
            final HierMemDirCtx hierMemDirCtx = new HierMemDirCtx();
            final NamingEnumeration<?> all = value.getAll();
            while (all.hasMoreElements()) {
                final String s = all.nextElement();
                hierMemDirCtx.bind(s, context.lookup(s));
            }
            hierMemDirCtx.setReadOnly(new SchemaViolationException("Cannot update schema object"));
            return hierMemDirCtx;
        }
        catch (final NamingException ex) {
            throw continuation.fillInException(ex);
        }
    }
    
    private DirContext getSchemaTree(final Name name) throws NamingException {
        final String schemaEntry = this.getSchemaEntry(name, true);
        DirContext buildSchemaTree = this.schemaTrees.get(schemaEntry);
        if (buildSchemaTree == null) {
            buildSchemaTree = this.buildSchemaTree(schemaEntry);
            this.schemaTrees.put(schemaEntry, buildSchemaTree);
        }
        return buildSchemaTree;
    }
    
    private DirContext buildSchemaTree(final String s) throws NamingException {
        final NamingEnumeration<SearchResult> searchAux = this.searchAux(new CompositeName().add(s), "(objectClass=subschema)", new SearchControls(0, 0L, 0, LdapCtx.SCHEMA_ATTRIBUTES, true, false), false, true, new Continuation());
        if (!searchAux.hasMore()) {
            throw new OperationNotSupportedException("Cannot get read subschemasubentry: " + s);
        }
        final SearchResult searchResult = searchAux.next();
        searchAux.close();
        final Object object = searchResult.getObject();
        if (!(object instanceof LdapCtx)) {
            throw new NamingException("Cannot get schema object as DirContext: " + s);
        }
        return LdapSchemaCtx.createSchemaTree(this.envprops, s, (LdapCtx)object, searchResult.getAttributes(), this.netscapeSchemaBug);
    }
    
    private String getSchemaEntry(final Name name, final boolean b) throws NamingException {
        final SearchControls searchControls = new SearchControls(0, 0L, 0, new String[] { "subschemasubentry" }, false, false);
        NamingEnumeration<SearchResult> searchAux;
        try {
            searchAux = this.searchAux(name, "objectclass=*", searchControls, b, true, new Continuation());
        }
        catch (final NamingException ex) {
            if (!this.clnt.isLdapv3 && this.currentDN.length() == 0 && name.isEmpty()) {
                throw new OperationNotSupportedException("Cannot get schema information from server");
            }
            throw ex;
        }
        if (!searchAux.hasMoreElements()) {
            throw new ConfigurationException("Requesting schema of nonexistent entry: " + name);
        }
        final SearchResult searchResult = searchAux.next();
        searchAux.close();
        final Attribute value = searchResult.getAttributes().get("subschemasubentry");
        if (value != null && value.size() >= 0) {
            return (String)value.get();
        }
        if (this.currentDN.length() == 0 && name.isEmpty()) {
            throw new OperationNotSupportedException("Cannot read subschemasubentry of root DSE");
        }
        return this.getSchemaEntry(new CompositeName(), false);
    }
    
    void setParents(final Attributes attributes, final Name name) throws NamingException {
        final NamingEnumeration<? extends Attribute> all = attributes.getAll();
        while (all.hasMore()) {
            ((LdapAttribute)all.next()).setParent(this, name);
        }
    }
    
    String getURL() {
        if (this.url == null) {
            this.url = LdapURL.toUrlString(this.hostname, this.port_number, this.currentDN, this.hasLdapsScheme);
        }
        return this.url;
    }
    
    protected NamingEnumeration<SearchResult> c_search(final Name name, final Attributes attributes, final Continuation continuation) throws NamingException {
        return this.c_search(name, attributes, null, continuation);
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search(final Name name, final Attributes attributes, final String[] returningAttributes, final Continuation continuation) throws NamingException {
        final SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(returningAttributes);
        String format;
        try {
            format = SearchFilter.format(attributes);
        }
        catch (final NamingException ex) {
            continuation.setError(this, name);
            throw continuation.fillInException(ex);
        }
        return this.c_search(name, format, searchControls, continuation);
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search(final Name name, final String s, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        return this.searchAux(name, s, cloneSearchControls(searchControls), true, this.waitForReply, continuation);
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search(final Name name, final String s, final Object[] array, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        String format;
        try {
            format = SearchFilter.format(s, array);
        }
        catch (final NamingException ex) {
            continuation.setError(this, name);
            throw continuation.fillInException(ex);
        }
        return this.c_search(name, format, searchControls, continuation);
    }
    
    NamingEnumeration<SearchResult> searchAux(final Name name, final String s, SearchControls searchControls, final boolean b, final boolean b2, final Continuation continuation) throws NamingException {
        LdapResult ldapResult = null;
        final String[] array = new String[2];
        if (searchControls == null) {
            searchControls = new SearchControls();
        }
        final String[] returningAttributes = searchControls.getReturningAttributes();
        if (searchControls.getReturningObjFlag() && returningAttributes != null) {
            boolean b3 = false;
            for (int i = returningAttributes.length - 1; i >= 0; --i) {
                if (returningAttributes[i].equals("*")) {
                    b3 = true;
                    break;
                }
            }
            if (!b3) {
                final String[] returningAttributes2 = new String[returningAttributes.length + Obj.JAVA_ATTRIBUTES.length];
                System.arraycopy(returningAttributes, 0, returningAttributes2, 0, returningAttributes.length);
                System.arraycopy(Obj.JAVA_ATTRIBUTES, 0, returningAttributes2, returningAttributes.length, Obj.JAVA_ATTRIBUTES.length);
                searchControls.setReturningAttributes(returningAttributes2);
            }
        }
        final SearchArgs searchArgs = new SearchArgs(name, s, searchControls, returningAttributes);
        continuation.setError(this, name);
        try {
            if (searchToCompare(s, searchControls, array)) {
                ldapResult = this.compare(name, array[0], array[1]);
                if (!ldapResult.compareToSearchResult(this.fullyQualifiedName(name))) {
                    this.processReturnCode(ldapResult, name);
                }
            }
            else {
                ldapResult = this.doSearch(name, s, searchControls, b, b2);
                this.processReturnCode(ldapResult, name);
            }
            return new LdapSearchEnumeration(this, ldapResult, this.fullyQualifiedName(name), searchArgs, continuation);
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw continuation.fillInException(ex);
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    return ldapReferralContext.search(name, s, searchControls);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final LimitExceededException namingException) {
            final LdapSearchEnumeration ldapSearchEnumeration = new LdapSearchEnumeration(this, ldapResult, this.fullyQualifiedName(name), searchArgs, continuation);
            ldapSearchEnumeration.setNamingException(namingException);
            return ldapSearchEnumeration;
        }
        catch (final PartialResultException namingException2) {
            final LdapSearchEnumeration ldapSearchEnumeration2 = new LdapSearchEnumeration(this, ldapResult, this.fullyQualifiedName(name), searchArgs, continuation);
            ldapSearchEnumeration2.setNamingException(namingException2);
            return ldapSearchEnumeration2;
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw continuation.fillInException(ex3);
        }
        catch (final NamingException ex4) {
            throw continuation.fillInException(ex4);
        }
    }
    
    LdapResult getSearchReply(final LdapClient ldapClient, final LdapResult ldapResult) throws NamingException {
        if (this.clnt != ldapClient) {
            throw new CommunicationException("Context's connection changed; unable to continue enumeration");
        }
        try {
            return ldapClient.getSearchReply(this.batchSize, ldapResult, this.binaryAttrs);
        }
        catch (final IOException rootCause) {
            final CommunicationException ex = new CommunicationException(rootCause.getMessage());
            ex.setRootCause(rootCause);
            throw ex;
        }
    }
    
    private LdapResult doSearchOnce(final Name name, final String s, final SearchControls searchControls, final boolean b) throws NamingException {
        final int batchSize = this.batchSize;
        this.batchSize = 2;
        final LdapResult doSearch = this.doSearch(name, s, searchControls, b, true);
        this.batchSize = batchSize;
        return doSearch;
    }
    
    private LdapResult doSearch(final Name name, final String s, final SearchControls searchControls, final boolean b, final boolean b2) throws NamingException {
        this.ensureOpen();
        try {
            int n = 0;
            switch (searchControls.getSearchScope()) {
                case 0: {
                    n = 0;
                    break;
                }
                default: {
                    n = 1;
                    break;
                }
                case 2: {
                    n = 2;
                    break;
                }
            }
            String[] returningAttributes = searchControls.getReturningAttributes();
            if (returningAttributes != null && returningAttributes.length == 0) {
                returningAttributes = new String[] { "1.1" };
            }
            final String s2 = b ? this.fullyQualifiedName(name) : (name.isEmpty() ? "" : name.get(0));
            final int timeLimit = searchControls.getTimeLimit();
            int n2 = 0;
            if (timeLimit > 0) {
                n2 = timeLimit / 1000 + 1;
            }
            final LdapResult search = this.clnt.search(s2, n, this.derefAliases, (int)searchControls.getCountLimit(), n2, !searchControls.getReturningObjFlag() && this.typesOnly, returningAttributes, s, this.batchSize, this.reqCtls, this.binaryAttrs, b2, this.replyQueueSize);
            this.respCtls = search.resControls;
            return search;
        }
        catch (final IOException rootCause) {
            final CommunicationException ex = new CommunicationException(rootCause.getMessage());
            ex.setRootCause(rootCause);
            throw ex;
        }
    }
    
    private static boolean searchToCompare(final String s, final SearchControls searchControls, final String[] array) {
        if (searchControls.getSearchScope() != 0) {
            return false;
        }
        final String[] returningAttributes = searchControls.getReturningAttributes();
        return returningAttributes != null && returningAttributes.length == 0 && filterToAssertion(s, array);
    }
    
    private static boolean filterToAssertion(final String s, final String[] array) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "=");
        if (stringTokenizer.countTokens() != 2) {
            return false;
        }
        array[0] = stringTokenizer.nextToken();
        array[1] = stringTokenizer.nextToken();
        if (array[1].indexOf(42) != -1) {
            return false;
        }
        boolean b = false;
        final int length = array[1].length();
        if (array[0].charAt(0) == '(' && array[1].charAt(length - 1) == ')') {
            b = true;
        }
        else if (array[0].charAt(0) == '(' || array[1].charAt(length - 1) == ')') {
            return false;
        }
        if (new StringTokenizer(array[0], "()&|!=~><*", true).countTokens() != (b ? 2 : 1)) {
            return false;
        }
        if (new StringTokenizer(array[1], "()&|!=~><*", true).countTokens() != (b ? 2 : 1)) {
            return false;
        }
        if (b) {
            array[0] = array[0].substring(1);
            array[1] = array[1].substring(0, length - 1);
        }
        return true;
    }
    
    private LdapResult compare(final Name name, final String s, final String s2) throws IOException, NamingException {
        this.ensureOpen();
        final LdapResult compare = this.clnt.compare(this.fullyQualifiedName(name), s, s2, this.reqCtls);
        this.respCtls = compare.resControls;
        return compare;
    }
    
    private static SearchControls cloneSearchControls(final SearchControls searchControls) {
        if (searchControls == null) {
            return null;
        }
        String[] returningAttributes = searchControls.getReturningAttributes();
        if (returningAttributes != null) {
            final String[] array = new String[returningAttributes.length];
            System.arraycopy(returningAttributes, 0, array, 0, returningAttributes.length);
            returningAttributes = array;
        }
        return new SearchControls(searchControls.getSearchScope(), searchControls.getCountLimit(), searchControls.getTimeLimit(), returningAttributes, searchControls.getReturningObjFlag(), searchControls.getDerefLinkFlag());
    }
    
    @Override
    protected Hashtable<String, Object> p_getEnvironment() {
        return this.envprops;
    }
    
    @Override
    public Hashtable<String, Object> getEnvironment() throws NamingException {
        return (Hashtable<String, Object>)((this.envprops == null) ? new Hashtable<String, Object>(5, 0.75f) : this.envprops.clone());
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        if (this.envprops == null || this.envprops.get(s) == null) {
            return null;
        }
        switch (s) {
            case "java.naming.ldap.ref.separator": {
                this.addrEncodingSeparator = '#';
                break;
            }
            case "java.naming.ldap.typesOnly": {
                this.typesOnly = false;
                break;
            }
            case "java.naming.ldap.deleteRDN": {
                this.deleteRDN = true;
                break;
            }
            case "java.naming.ldap.derefAliases": {
                this.derefAliases = 3;
                break;
            }
            case "java.naming.batchsize": {
                this.batchSize = 1;
                break;
            }
            case "java.naming.ldap.referral.limit": {
                this.referralHopLimit = 10;
                break;
            }
            case "java.naming.referral": {
                this.setReferralMode(null, true);
                break;
            }
            case "java.naming.ldap.attributes.binary": {
                this.setBinaryAttributes(null);
                break;
            }
            case "com.sun.jndi.ldap.connect.timeout": {
                this.connectTimeout = -1;
                break;
            }
            case "com.sun.jndi.ldap.read.timeout": {
                this.readTimeout = -1;
                break;
            }
            case "com.sun.jndi.ldap.search.waitForReply": {
                this.waitForReply = true;
                break;
            }
            case "com.sun.jndi.ldap.search.replyQueueSize": {
                this.replyQueueSize = -1;
                break;
            }
            case "java.naming.security.protocol": {
                this.closeConnection(false);
                if (!this.useSsl || this.hasLdapsScheme) {
                    break;
                }
                this.useSsl = false;
                this.url = null;
                if (this.useDefaultPortNumber) {
                    this.port_number = 389;
                    break;
                }
                break;
            }
            case "java.naming.ldap.version":
            case "java.naming.ldap.factory.socket": {
                this.closeConnection(false);
                break;
            }
            case "java.naming.security.authentication":
            case "java.naming.security.principal":
            case "java.naming.security.credentials": {
                this.sharable = false;
                break;
            }
        }
        this.envprops = (Hashtable)this.envprops.clone();
        return this.envprops.remove(s);
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        if (o == null) {
            return this.removeFromEnvironment(s);
        }
        switch (s) {
            case "java.naming.ldap.ref.separator": {
                this.setRefSeparator((String)o);
                break;
            }
            case "java.naming.ldap.typesOnly": {
                this.setTypesOnly((String)o);
                break;
            }
            case "java.naming.ldap.deleteRDN": {
                this.setDeleteRDN((String)o);
                break;
            }
            case "java.naming.ldap.derefAliases": {
                this.setDerefAliases((String)o);
                break;
            }
            case "java.naming.batchsize": {
                this.setBatchSize((String)o);
                break;
            }
            case "java.naming.ldap.referral.limit": {
                this.setReferralLimit((String)o);
                break;
            }
            case "java.naming.referral": {
                this.setReferralMode((String)o, true);
                break;
            }
            case "java.naming.ldap.attributes.binary": {
                this.setBinaryAttributes((String)o);
                break;
            }
            case "com.sun.jndi.ldap.connect.timeout": {
                this.setConnectTimeout((String)o);
                break;
            }
            case "com.sun.jndi.ldap.read.timeout": {
                this.setReadTimeout((String)o);
                break;
            }
            case "com.sun.jndi.ldap.search.waitForReply": {
                this.setWaitForReply((String)o);
                break;
            }
            case "com.sun.jndi.ldap.search.replyQueueSize": {
                this.setReplyQueueSize((String)o);
                break;
            }
            case "java.naming.security.protocol": {
                this.closeConnection(false);
                if (!"ssl".equals(o)) {
                    break;
                }
                this.useSsl = true;
                this.url = null;
                if (this.useDefaultPortNumber) {
                    this.port_number = 636;
                    break;
                }
                break;
            }
            case "java.naming.ldap.version":
            case "java.naming.ldap.factory.socket": {
                this.closeConnection(false);
                break;
            }
            case "java.naming.security.authentication":
            case "java.naming.security.principal":
            case "java.naming.security.credentials": {
                this.sharable = false;
                break;
            }
        }
        this.envprops = (Hashtable<String, Object>)((this.envprops == null) ? new Hashtable<String, Object>(5, 0.75f) : this.envprops.clone());
        return this.envprops.put(s, o);
    }
    
    void setProviderUrl(final String s) {
        if (this.envprops != null) {
            this.envprops.put("java.naming.provider.url", s);
        }
    }
    
    void setDomainName(final String s) {
        if (this.envprops != null) {
            this.envprops.put("com.sun.jndi.ldap.domainname", s);
        }
    }
    
    private void initEnv() throws NamingException {
        if (this.envprops == null) {
            this.setReferralMode(null, false);
            return;
        }
        this.setBatchSize(this.envprops.get("java.naming.batchsize"));
        this.setRefSeparator(this.envprops.get("java.naming.ldap.ref.separator"));
        this.setDeleteRDN(this.envprops.get("java.naming.ldap.deleteRDN"));
        this.setTypesOnly(this.envprops.get("java.naming.ldap.typesOnly"));
        this.setDerefAliases(this.envprops.get("java.naming.ldap.derefAliases"));
        this.setReferralLimit(this.envprops.get("java.naming.ldap.referral.limit"));
        this.setBinaryAttributes(this.envprops.get("java.naming.ldap.attributes.binary"));
        this.bindCtls = cloneControls(this.envprops.get("java.naming.ldap.control.connect"));
        this.setReferralMode(this.envprops.get("java.naming.referral"), false);
        this.setConnectTimeout(this.envprops.get("com.sun.jndi.ldap.connect.timeout"));
        this.setReadTimeout(this.envprops.get("com.sun.jndi.ldap.read.timeout"));
        this.setWaitForReply(this.envprops.get("com.sun.jndi.ldap.search.waitForReply"));
        this.setReplyQueueSize(this.envprops.get("com.sun.jndi.ldap.search.replyQueueSize"));
    }
    
    private void setDeleteRDN(final String s) {
        if (s != null && s.equalsIgnoreCase("false")) {
            this.deleteRDN = false;
        }
        else {
            this.deleteRDN = true;
        }
    }
    
    private void setTypesOnly(final String s) {
        if (s != null && s.equalsIgnoreCase("true")) {
            this.typesOnly = true;
        }
        else {
            this.typesOnly = false;
        }
    }
    
    private void setBatchSize(final String s) {
        if (s != null) {
            this.batchSize = Integer.parseInt(s);
        }
        else {
            this.batchSize = 1;
        }
    }
    
    private void setReferralMode(final String s, final boolean b) {
        if (s != null) {
            switch (s) {
                case "follow-scheme": {
                    this.handleReferrals = 4;
                    break;
                }
                case "follow": {
                    this.handleReferrals = 1;
                    break;
                }
                case "throw": {
                    this.handleReferrals = 2;
                    break;
                }
                case "ignore": {
                    this.handleReferrals = 3;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal value for java.naming.referral property.");
                }
            }
        }
        else {
            this.handleReferrals = 3;
        }
        if (this.handleReferrals == 3) {
            this.reqCtls = addControl(this.reqCtls, LdapCtx.manageReferralControl);
        }
        else if (b) {
            this.reqCtls = removeControl(this.reqCtls, LdapCtx.manageReferralControl);
        }
    }
    
    private void setDerefAliases(final String s) {
        if (s != null) {
            switch (s) {
                case "never": {
                    this.derefAliases = 0;
                    break;
                }
                case "searching": {
                    this.derefAliases = 1;
                    break;
                }
                case "finding": {
                    this.derefAliases = 2;
                    break;
                }
                case "always": {
                    this.derefAliases = 3;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal value for java.naming.ldap.derefAliases property.");
                }
            }
        }
        else {
            this.derefAliases = 3;
        }
    }
    
    private void setRefSeparator(final String s) throws NamingException {
        if (s != null && s.length() > 0) {
            this.addrEncodingSeparator = s.charAt(0);
        }
        else {
            this.addrEncodingSeparator = '#';
        }
    }
    
    private void setReferralLimit(final String s) {
        if (s != null) {
            this.referralHopLimit = Integer.parseInt(s);
            if (this.referralHopLimit == 0) {
                this.referralHopLimit = Integer.MAX_VALUE;
            }
        }
        else {
            this.referralHopLimit = 10;
        }
    }
    
    void setHopCount(final int hopCount) {
        this.hopCount = hopCount;
    }
    
    private void setConnectTimeout(final String s) {
        if (s != null) {
            this.connectTimeout = Integer.parseInt(s);
        }
        else {
            this.connectTimeout = -1;
        }
    }
    
    private void setReplyQueueSize(final String s) {
        if (s != null) {
            this.replyQueueSize = Integer.parseInt(s);
            if (this.replyQueueSize <= 0) {
                this.replyQueueSize = -1;
            }
        }
        else {
            this.replyQueueSize = -1;
        }
    }
    
    private void setWaitForReply(final String s) {
        if (s != null && s.equalsIgnoreCase("false")) {
            this.waitForReply = false;
        }
        else {
            this.waitForReply = true;
        }
    }
    
    private void setReadTimeout(final String s) {
        if (s != null) {
            this.readTimeout = Integer.parseInt(s);
        }
        else {
            this.readTimeout = -1;
        }
    }
    
    private static Vector<Vector<String>> extractURLs(final String s) {
        int index;
        int n;
        for (index = 0, n = 0; (index = s.indexOf(10, index)) >= 0; ++index, ++n) {}
        final Vector vector = new Vector<Vector<String>>(n);
        int n2;
        int index2;
        for (n2 = s.indexOf(10) + 1; (index2 = s.indexOf(10, n2)) >= 0; n2 = index2 + 1) {
            final Vector<String> vector2 = new Vector<String>(1);
            vector2.addElement(s.substring(n2, index2));
            vector.addElement(vector2);
        }
        final Vector<String> vector3 = new Vector<String>(1);
        vector3.addElement(s.substring(n2));
        vector.addElement(vector3);
        return (Vector<Vector<String>>)vector;
    }
    
    private void setBinaryAttributes(final String s) {
        if (s == null) {
            this.binaryAttrs = null;
        }
        else {
            this.binaryAttrs = new Hashtable<String, Boolean>(11, 0.75f);
            final StringTokenizer stringTokenizer = new StringTokenizer(s.toLowerCase(Locale.ENGLISH), " ");
            while (stringTokenizer.hasMoreTokens()) {
                this.binaryAttrs.put(stringTokenizer.nextToken(), Boolean.TRUE);
            }
        }
    }
    
    @Override
    protected void finalize() {
        try {
            this.close();
        }
        catch (final NamingException ex) {}
    }
    
    @Override
    public synchronized void close() throws NamingException {
        if (this.eventSupport != null) {
            this.eventSupport.cleanup();
            this.removeUnsolicited();
        }
        if (this.enumCount > 0) {
            this.closeRequested = true;
            return;
        }
        this.closeConnection(false);
    }
    
    @Override
    public void reconnect(final Control[] array) throws NamingException {
        this.envprops = (Hashtable<String, Object>)((this.envprops == null) ? new Hashtable<String, Object>(5, 0.75f) : this.envprops.clone());
        if (array == null) {
            this.envprops.remove("java.naming.ldap.control.connect");
            this.bindCtls = null;
        }
        else {
            this.envprops.put("java.naming.ldap.control.connect", this.bindCtls = cloneControls(array));
        }
        this.sharable = false;
        this.ensureOpen();
    }
    
    private static String getMechsAllowedToSendCredentials() {
        final PrivilegedAction<String> privilegedAction = () -> System.getProperty("jdk.jndi.ldap.mechsAllowedToSendCredentials");
        return (System.getSecurityManager() == null) ? privilegedAction.run() : AccessController.doPrivileged(privilegedAction);
    }
    
    private static Set<String> getMechsFromPropertyValue(final String s) {
        if (s == null || s.isEmpty()) {
            return Collections.emptySet();
        }
        final HashSet set = new HashSet();
        for (final String s2 : s.trim().split("\\s*,\\s*")) {
            if (!s2.isEmpty()) {
                set.add(s2);
            }
        }
        return (Set<String>)Collections.unmodifiableSet((Set<?>)set);
    }
    
    private boolean isConnectionEncrypted() {
        return this.hasLdapsScheme || this.clnt.isUpgradedToStartTls();
    }
    
    private void ensureCanTransmitCredentials(final String s) throws NamingException {
        if ("none".equalsIgnoreCase(s) || "anonymous".equalsIgnoreCase(s)) {
            return;
        }
        String allowed_MECHS_SP_VALUE = this.envprops.get("jdk.jndi.ldap.mechsAllowedToSendCredentials");
        boolean b = false;
        final boolean b2 = LdapCtx.ALLOWED_MECHS_SP_VALUE != null || allowed_MECHS_SP_VALUE != null;
        if (!this.isConnectionEncrypted() && (this.contextSeenStartTlsEnabled || b2)) {
            if ("simple".equalsIgnoreCase(s) && !this.envprops.containsKey("java.naming.security.principal")) {
                return;
            }
            if (allowed_MECHS_SP_VALUE == null) {
                b = true;
                allowed_MECHS_SP_VALUE = LdapCtx.ALLOWED_MECHS_SP_VALUE;
            }
            if ("all".equalsIgnoreCase(allowed_MECHS_SP_VALUE)) {
                return;
            }
            if (!(b ? LdapCtx.MECHS_ALLOWED_BY_SP : getMechsFromPropertyValue(allowed_MECHS_SP_VALUE)).contains(s)) {
                throw new NamingException("Transmission of credentials over unsecured connection is not allowed");
            }
        }
    }
    
    private void ensureOpen() throws NamingException {
        this.ensureOpen(false);
    }
    
    private void ensureOpen(final boolean b) throws NamingException {
        try {
            if (this.clnt == null) {
                this.schemaTrees = new Hashtable<String, DirContext>(11, 0.75f);
                this.connect(b);
            }
            else if (!this.sharable || b) {
                synchronized (this.clnt) {
                    if (!this.clnt.isLdapv3 || this.clnt.referenceCount > 1 || this.clnt.usingSaslStreams() || !this.clnt.conn.useable) {
                        this.closeConnection(false);
                    }
                }
                this.schemaTrees = new Hashtable<String, DirContext>(11, 0.75f);
                this.connect(b);
            }
        }
        finally {
            this.sharable = true;
        }
    }
    
    private void connect(final boolean b) throws NamingException {
        String s = null;
        Object value = null;
        String s2 = null;
        String s3 = null;
        String s4 = null;
        String s5 = null;
        boolean equalsIgnoreCase = false;
        if (this.envprops != null) {
            s = this.envprops.get("java.naming.security.principal");
            value = this.envprops.get("java.naming.security.credentials");
            s5 = this.envprops.get("java.naming.ldap.version");
            s2 = (this.useSsl ? "ssl" : this.envprops.get("java.naming.security.protocol"));
            s3 = this.envprops.get("java.naming.ldap.factory.socket");
            s4 = this.envprops.get("java.naming.security.authentication");
            equalsIgnoreCase = "true".equalsIgnoreCase(this.envprops.get("com.sun.jndi.ldap.connect.pool"));
            LdapSasl.checkSaslParameters(this.envprops);
        }
        if (s3 == null) {
            s3 = ("ssl".equals(s2) ? "javax.net.ssl.SSLSocketFactory" : null);
        }
        if (s4 == null) {
            s4 = ((s == null) ? "none" : "simple");
        }
        try {
            final boolean b2 = this.clnt == null;
            int n;
            if (b2) {
                n = ((s5 != null) ? Integer.parseInt(s5) : 32);
                this.clnt = LdapClient.getInstance(equalsIgnoreCase, this.hostname, this.port_number, s3, this.connectTimeout, this.readTimeout, this.trace, n, s4, this.bindCtls, s2, s, value, this.envprops);
                this.contextSeenStartTlsEnabled |= this.clnt.isUpgradedToStartTls();
                if (this.clnt.authenticateCalled()) {
                    return;
                }
            }
            else {
                if (this.sharable && b) {
                    return;
                }
                n = 3;
            }
            final LdapResult authenticate;
            synchronized (this.clnt.conn.startTlsLock) {
                this.ensureCanTransmitCredentials(s4);
                authenticate = this.clnt.authenticate(b2, s, value, n, s4, this.bindCtls, this.envprops);
            }
            this.respCtls = authenticate.resControls;
            if (authenticate.status != 0) {
                if (b2) {
                    this.closeConnection(true);
                }
                this.processReturnCode(authenticate);
            }
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw ex;
            }
            Throwable t = null;
            String nextReferral;
            while ((nextReferral = ex.getNextReferral()) != null) {
                final LdapURL ldapURL = new LdapURL(nextReferral);
                this.hostname = ldapURL.getHost();
                if (this.hostname != null && this.hostname.charAt(0) == '[') {
                    this.hostname = this.hostname.substring(1, this.hostname.length() - 1);
                }
                this.port_number = ldapURL.getPort();
                try {
                    this.connect(b);
                }
                catch (final NamingException ex2) {
                    t = ex2;
                }
            }
            if (t != null) {
                throw (NamingException)t.fillInStackTrace();
            }
            throw new NamingException("Internal error processing referral during connection");
        }
    }
    
    private void closeConnection(final boolean b) {
        this.removeUnsolicited();
        if (this.clnt != null) {
            this.clnt.close(this.reqCtls, b);
            this.clnt = null;
        }
    }
    
    synchronized void incEnumCount() {
        ++this.enumCount;
    }
    
    synchronized void decEnumCount() {
        --this.enumCount;
        if (this.enumCount == 0 && this.closeRequested) {
            try {
                this.close();
            }
            catch (final NamingException ex) {}
        }
    }
    
    protected void processReturnCode(final LdapResult ldapResult) throws NamingException {
        this.processReturnCode(ldapResult, null, this, null, this.envprops, null);
    }
    
    void processReturnCode(final LdapResult ldapResult, final Name name) throws NamingException {
        this.processReturnCode(ldapResult, new CompositeName().add(this.currentDN), this, name, this.envprops, this.fullyQualifiedName(name));
    }
    
    protected void processReturnCode(final LdapResult ldapResult, final Name resolvedName, final Object resolvedObj, final Name remainingName, final Hashtable<?, ?> hashtable, final String s) throws NamingException {
        final String errorMessage = LdapClient.getErrorMessage(ldapResult.status, ldapResult.errorMessage);
        LdapReferralException ex = null;
        NamingException mapErrorCode = null;
        switch (ldapResult.status) {
            case 0: {
                if (ldapResult.referrals != null) {
                    final String s2 = "Unprocessed Continuation Reference(s)";
                    if (this.handleReferrals == 3) {
                        mapErrorCode = new PartialResultException(s2);
                        break;
                    }
                    final int size = ldapResult.referrals.size();
                    LdapReferralException ex2 = null;
                    LdapReferralException ex3 = null;
                    final String s3 = "Continuation Reference";
                    for (int i = 0; i < size; ++i) {
                        ex = new LdapReferralException(resolvedName, resolvedObj, remainingName, s3, hashtable, s, this.handleReferrals, this.reqCtls);
                        ex.setReferralInfo(ldapResult.referrals.elementAt(i), true);
                        if (this.hopCount > 1) {
                            ex.setHopCount(this.hopCount);
                        }
                        if (ex2 == null) {
                            ex3 = (ex2 = ex);
                        }
                        else {
                            ex3.nextReferralEx = ex;
                            ex3 = ex;
                        }
                    }
                    ldapResult.referrals = null;
                    if (ldapResult.refEx == null) {
                        ldapResult.refEx = ex2;
                    }
                    else {
                        LdapReferralException ex4;
                        for (ex4 = ldapResult.refEx; ex4.nextReferralEx != null; ex4 = ex4.nextReferralEx) {}
                        ex4.nextReferralEx = ex2;
                    }
                    if (this.hopCount > this.referralHopLimit) {
                        final LimitExceededException ex5 = new LimitExceededException("Referral limit exceeded");
                        ex5.setRootCause(ex);
                        throw ex5;
                    }
                }
                return;
            }
            case 10: {
                if (this.handleReferrals == 3) {
                    mapErrorCode = new PartialResultException(errorMessage);
                    break;
                }
                final LdapReferralException rootCause = new LdapReferralException(resolvedName, resolvedObj, remainingName, errorMessage, hashtable, s, this.handleReferrals, this.reqCtls);
                Vector<String> vector;
                if (ldapResult.referrals == null) {
                    vector = null;
                }
                else if (this.handleReferrals == 4) {
                    vector = new Vector<String>();
                    for (final String s4 : ldapResult.referrals.elementAt(0)) {
                        if (s4.startsWith("ldap:")) {
                            vector.add(s4);
                        }
                    }
                    if (vector.isEmpty()) {
                        vector = null;
                    }
                }
                else {
                    vector = ldapResult.referrals.elementAt(0);
                }
                rootCause.setReferralInfo(vector, false);
                if (this.hopCount > 1) {
                    rootCause.setHopCount(this.hopCount);
                }
                if (this.hopCount > this.referralHopLimit) {
                    final LimitExceededException ex6 = new LimitExceededException("Referral limit exceeded");
                    ex6.setRootCause(rootCause);
                    mapErrorCode = ex6;
                    break;
                }
                mapErrorCode = rootCause;
                break;
            }
            case 9: {
                if (this.handleReferrals == 3) {
                    mapErrorCode = new PartialResultException(errorMessage);
                    break;
                }
                if (ldapResult.errorMessage == null || ldapResult.errorMessage.equals("")) {
                    mapErrorCode = new PartialResultException(errorMessage);
                    break;
                }
                ldapResult.referrals = extractURLs(ldapResult.errorMessage);
                final LdapReferralException ex7 = new LdapReferralException(resolvedName, resolvedObj, remainingName, errorMessage, hashtable, s, this.handleReferrals, this.reqCtls);
                if (this.hopCount > 1) {
                    ex7.setHopCount(this.hopCount);
                }
                if ((ldapResult.entries != null && !ldapResult.entries.isEmpty()) || ldapResult.referrals == null || ldapResult.referrals.size() != 1) {
                    ex7.setReferralInfo(ldapResult.referrals, true);
                    ldapResult.refEx = ex7;
                    return;
                }
                ex7.setReferralInfo(ldapResult.referrals, false);
                if (this.hopCount > this.referralHopLimit) {
                    final LimitExceededException ex8 = new LimitExceededException("Referral limit exceeded");
                    ex8.setRootCause(ex7);
                    mapErrorCode = ex8;
                    break;
                }
                mapErrorCode = ex7;
                break;
            }
            case 34:
            case 64: {
                if (remainingName != null) {
                    mapErrorCode = new InvalidNameException(remainingName.toString() + ": " + errorMessage);
                    break;
                }
                mapErrorCode = new InvalidNameException(errorMessage);
                break;
            }
            default: {
                mapErrorCode = mapErrorCode(ldapResult.status, ldapResult.errorMessage);
                break;
            }
        }
        mapErrorCode.setResolvedName(resolvedName);
        mapErrorCode.setResolvedObj(resolvedObj);
        mapErrorCode.setRemainingName(remainingName);
        throw mapErrorCode;
    }
    
    public static NamingException mapErrorCode(final int n, final String s) {
        if (n == 0) {
            return null;
        }
        final String errorMessage = LdapClient.getErrorMessage(n, s);
        NamingException ex = null;
        switch (n) {
            case 36: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 33: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 20: {
                ex = new AttributeInUseException(errorMessage);
                break;
            }
            case 7:
            case 8:
            case 13:
            case 48: {
                ex = new AuthenticationNotSupportedException(errorMessage);
                break;
            }
            case 68: {
                ex = new NameAlreadyBoundException(errorMessage);
                break;
            }
            case 14:
            case 49: {
                ex = new AuthenticationException(errorMessage);
                break;
            }
            case 18: {
                ex = new InvalidSearchFilterException(errorMessage);
                break;
            }
            case 50: {
                ex = new NoPermissionException(errorMessage);
                break;
            }
            case 19:
            case 21: {
                ex = new InvalidAttributeValueException(errorMessage);
                break;
            }
            case 54: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 16: {
                ex = new NoSuchAttributeException(errorMessage);
                break;
            }
            case 32: {
                ex = new NameNotFoundException(errorMessage);
                break;
            }
            case 65:
            case 67:
            case 69: {
                ex = new SchemaViolationException(errorMessage);
                break;
            }
            case 66: {
                ex = new ContextNotEmptyException(errorMessage);
                break;
            }
            case 1: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 80: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 2: {
                ex = new CommunicationException(errorMessage);
                break;
            }
            case 4: {
                ex = new SizeLimitExceededException(errorMessage);
                break;
            }
            case 3: {
                ex = new TimeLimitExceededException(errorMessage);
                break;
            }
            case 12: {
                ex = new OperationNotSupportedException(errorMessage);
                break;
            }
            case 51:
            case 52: {
                ex = new ServiceUnavailableException(errorMessage);
                break;
            }
            case 17: {
                ex = new InvalidAttributeIdentifierException(errorMessage);
                break;
            }
            case 53: {
                ex = new OperationNotSupportedException(errorMessage);
                break;
            }
            case 5:
            case 6:
            case 35: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 11: {
                ex = new LimitExceededException(errorMessage);
                break;
            }
            case 10: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 9: {
                ex = new NamingException(errorMessage);
                break;
            }
            case 34:
            case 64: {
                ex = new InvalidNameException(errorMessage);
                break;
            }
            default: {
                ex = new NamingException(errorMessage);
                break;
            }
        }
        return ex;
    }
    
    @Override
    public ExtendedResponse extendedOperation(final ExtendedRequest extendedRequest) throws NamingException {
        final boolean equals = extendedRequest.getID().equals("1.3.6.1.4.1.1466.20037");
        this.ensureOpen(equals);
        try {
            final LdapResult extendedOp = this.clnt.extendedOp(extendedRequest.getID(), extendedRequest.getEncodedValue(), this.reqCtls, equals);
            this.respCtls = extendedOp.resControls;
            if (extendedOp.status != 0) {
                this.processReturnCode(extendedOp, new CompositeName());
            }
            final ExtendedResponse extendedResponse = extendedRequest.createExtendedResponse(extendedOp.extensionId, extendedOp.extensionValue, 0, (extendedOp.extensionValue == null) ? 0 : extendedOp.extensionValue.length);
            if (extendedResponse instanceof StartTlsResponseImpl) {
                ((StartTlsResponseImpl)extendedResponse).setConnection(this.clnt.conn, (this.envprops != null) ? this.envprops.get("com.sun.jndi.ldap.domainname") : null);
                this.contextSeenStartTlsEnabled |= equals;
            }
            return extendedResponse;
        }
        catch (final LdapReferralException ex) {
            if (this.handleReferrals == 2) {
                throw ex;
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)ex.getReferralContext(this.envprops, this.bindCtls);
                try {
                    return ldapReferralContext.extendedOperation(extendedRequest);
                }
                catch (final LdapReferralException ex2) {
                    ex = ex2;
                }
                finally {
                    ldapReferralContext.close();
                }
            }
        }
        catch (final IOException rootCause) {
            final CommunicationException ex3 = new CommunicationException(rootCause.getMessage());
            ex3.setRootCause(rootCause);
            throw ex3;
        }
    }
    
    @Override
    public void setRequestControls(final Control[] array) throws NamingException {
        if (this.handleReferrals == 3) {
            this.reqCtls = addControl(array, LdapCtx.manageReferralControl);
        }
        else {
            this.reqCtls = cloneControls(array);
        }
    }
    
    @Override
    public Control[] getRequestControls() throws NamingException {
        return cloneControls(this.reqCtls);
    }
    
    @Override
    public Control[] getConnectControls() throws NamingException {
        return cloneControls(this.bindCtls);
    }
    
    @Override
    public Control[] getResponseControls() throws NamingException {
        return (Control[])((this.respCtls != null) ? this.convertControls(this.respCtls) : null);
    }
    
    Control[] convertControls(final Vector<Control> vector) throws NamingException {
        final int size = vector.size();
        if (size == 0) {
            return null;
        }
        final Control[] array = new Control[size];
        for (int i = 0; i < size; ++i) {
            array[i] = LdapCtx.myResponseControlFactory.getControlInstance((Control)vector.elementAt(i));
            if (array[i] == null) {
                array[i] = ControlFactory.getControlInstance((Control)vector.elementAt(i), this, this.envprops);
            }
        }
        return array;
    }
    
    private static Control[] addControl(final Control[] array, final Control control) {
        if (array == null) {
            return new Control[] { control };
        }
        if (findControl(array, control) != -1) {
            return array;
        }
        final Control[] array2 = new Control[array.length + 1];
        System.arraycopy(array, 0, array2, 0, array.length);
        array2[array.length] = control;
        return array2;
    }
    
    private static int findControl(final Control[] array, final Control control) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == control) {
                return i;
            }
        }
        return -1;
    }
    
    private static Control[] removeControl(final Control[] array, final Control control) {
        if (array == null) {
            return null;
        }
        final int control2 = findControl(array, control);
        if (control2 == -1) {
            return array;
        }
        final Control[] array2 = new Control[array.length - 1];
        System.arraycopy(array, 0, array2, 0, control2);
        System.arraycopy(array, control2 + 1, array2, control2, array.length - control2 - 1);
        return array2;
    }
    
    private static Control[] cloneControls(final Control[] array) {
        if (array == null) {
            return null;
        }
        final Control[] array2 = new Control[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    @Override
    public void addNamingListener(final Name name, final int n, final NamingListener namingListener) throws NamingException {
        this.addNamingListener(getTargetName(name), n, namingListener);
    }
    
    @Override
    public void addNamingListener(final String s, final int n, final NamingListener namingListener) throws NamingException {
        if (this.eventSupport == null) {
            this.eventSupport = new EventSupport(this);
        }
        this.eventSupport.addNamingListener(getTargetName(new CompositeName(s)), n, namingListener);
        if (namingListener instanceof UnsolicitedNotificationListener && !this.unsolicited) {
            this.addUnsolicited();
        }
    }
    
    @Override
    public void removeNamingListener(final NamingListener namingListener) throws NamingException {
        if (this.eventSupport == null) {
            return;
        }
        this.eventSupport.removeNamingListener(namingListener);
        if (namingListener instanceof UnsolicitedNotificationListener && !this.eventSupport.hasUnsolicited()) {
            this.removeUnsolicited();
        }
    }
    
    @Override
    public void addNamingListener(final String s, final String s2, final SearchControls searchControls, final NamingListener namingListener) throws NamingException {
        if (this.eventSupport == null) {
            this.eventSupport = new EventSupport(this);
        }
        this.eventSupport.addNamingListener(getTargetName(new CompositeName(s)), s2, cloneSearchControls(searchControls), namingListener);
        if (namingListener instanceof UnsolicitedNotificationListener && !this.unsolicited) {
            this.addUnsolicited();
        }
    }
    
    @Override
    public void addNamingListener(final Name name, final String s, final SearchControls searchControls, final NamingListener namingListener) throws NamingException {
        this.addNamingListener(getTargetName(name), s, searchControls, namingListener);
    }
    
    @Override
    public void addNamingListener(final Name name, final String s, final Object[] array, final SearchControls searchControls, final NamingListener namingListener) throws NamingException {
        this.addNamingListener(getTargetName(name), s, array, searchControls, namingListener);
    }
    
    @Override
    public void addNamingListener(final String s, final String s2, final Object[] array, final SearchControls searchControls, final NamingListener namingListener) throws NamingException {
        this.addNamingListener(getTargetName(new CompositeName(s)), SearchFilter.format(s2, array), searchControls, namingListener);
    }
    
    @Override
    public boolean targetMustExist() {
        return true;
    }
    
    private static String getTargetName(final Name name) throws NamingException {
        if (!(name instanceof CompositeName)) {
            return name.toString();
        }
        if (name.size() > 1) {
            throw new InvalidNameException("Target cannot span multiple namespaces: " + name);
        }
        if (name.isEmpty()) {
            return "";
        }
        return name.get(0);
    }
    
    private void addUnsolicited() throws NamingException {
        this.ensureOpen();
        synchronized (this.eventSupport) {
            this.clnt.addUnsolicited(this);
            this.unsolicited = true;
        }
    }
    
    private void removeUnsolicited() {
        if (this.eventSupport == null) {
            return;
        }
        synchronized (this.eventSupport) {
            if (this.unsolicited && this.clnt != null) {
                this.clnt.removeUnsolicited(this);
            }
            this.unsolicited = false;
        }
    }
    
    void fireUnsolicited(final Object o) {
        synchronized (this.eventSupport) {
            if (this.unsolicited) {
                this.eventSupport.fireUnsolicited(o);
                if (o instanceof NamingException) {
                    this.unsolicited = false;
                }
            }
        }
    }
    
    static {
        SCHEMA_ATTRIBUTES = new String[] { "objectClasses", "attributeTypes", "matchingRules", "ldapSyntaxes" };
        ALLOWED_MECHS_SP_VALUE = getMechsAllowedToSendCredentials();
        MECHS_ALLOWED_BY_SP = getMechsFromPropertyValue(LdapCtx.ALLOWED_MECHS_SP_VALUE);
        parser = new LdapNameParser();
        myResponseControlFactory = new DefaultResponseControlFactory();
        manageReferralControl = new ManageReferralControl(false);
        (EMPTY_SCHEMA = new HierMemDirCtx()).setReadOnly(new SchemaViolationException("Cannot update schema object"));
    }
    
    static final class SearchArgs
    {
        Name name;
        String filter;
        SearchControls cons;
        String[] reqAttrs;
        
        SearchArgs(final Name name, final String filter, final SearchControls cons, final String[] reqAttrs) {
            this.name = name;
            this.filter = filter;
            this.cons = cons;
            this.reqAttrs = reqAttrs;
        }
    }
}

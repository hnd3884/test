package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.UpdatableLDAPRequest;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.AsyncRequestID;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import javax.net.SocketFactory;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.Mutable;

@Mutable
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPConnection
{
    public static final int DEREF_NEVER;
    public static final int DEREF_SEARCHING;
    public static final int DEREF_FINDING;
    public static final int DEREF_ALWAYS;
    public static final int SCOPE_BASE = 0;
    public static final int SCOPE_ONE = 1;
    public static final int SCOPE_SUB = 2;
    private volatile com.unboundid.ldap.sdk.LDAPConnection conn;
    private LDAPConstraints constraints;
    private LDAPControl[] responseControls;
    private LDAPSearchConstraints searchConstraints;
    private LDAPSocketFactory socketFactory;
    private String authDN;
    private String authPW;
    
    public LDAPConnection() {
        this(null);
    }
    
    public LDAPConnection(final LDAPSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
        if (socketFactory == null) {
            this.conn = new com.unboundid.ldap.sdk.LDAPConnection();
        }
        else {
            this.conn = new com.unboundid.ldap.sdk.LDAPConnection(new LDAPToJavaSocketFactory(socketFactory));
        }
        this.authDN = null;
        this.authPW = null;
        this.constraints = new LDAPConstraints();
        this.searchConstraints = new LDAPSearchConstraints();
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.conn.close();
        super.finalize();
    }
    
    public com.unboundid.ldap.sdk.LDAPConnection getSDKConnection() {
        return this.conn;
    }
    
    public String getHost() {
        return this.conn.getConnectedAddress();
    }
    
    public int getPort() {
        return this.conn.getConnectedPort();
    }
    
    public String getAuthenticationDN() {
        return this.authDN;
    }
    
    public String getAuthenticationPassword() {
        return this.authPW;
    }
    
    public int getConnectTimeout() {
        final int connectTimeoutMillis = this.conn.getConnectionOptions().getConnectTimeoutMillis();
        if (connectTimeoutMillis > 0) {
            return Math.max(1, connectTimeoutMillis / 1000);
        }
        return 0;
    }
    
    public void setConnectTimeout(final int timeout) {
        final LDAPConnectionOptions options = this.conn.getConnectionOptions();
        if (timeout > 0) {
            options.setConnectTimeoutMillis(1000 * timeout);
        }
        else {
            options.setConnectTimeoutMillis(0);
        }
        this.conn.setConnectionOptions(options);
    }
    
    public LDAPSocketFactory getSocketFactory() {
        return this.socketFactory;
    }
    
    public void setSocketFactory(final LDAPSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
        if (socketFactory == null) {
            this.conn.setSocketFactory(null);
        }
        else {
            this.conn.setSocketFactory(new LDAPToJavaSocketFactory(socketFactory));
        }
    }
    
    public LDAPConstraints getConstraints() {
        return this.constraints;
    }
    
    public void setConstraints(final LDAPConstraints constraints) {
        if (constraints == null) {
            this.constraints = new LDAPConstraints();
        }
        else {
            this.constraints = constraints;
        }
    }
    
    public LDAPSearchConstraints getSearchConstraints() {
        return this.searchConstraints;
    }
    
    public void setSearchConstraints(final LDAPSearchConstraints searchConstraints) {
        if (searchConstraints == null) {
            this.searchConstraints = new LDAPSearchConstraints();
        }
        else {
            this.searchConstraints = searchConstraints;
        }
    }
    
    public LDAPControl[] getResponseControls() {
        return this.responseControls;
    }
    
    public boolean isConnected() {
        return this.conn.isConnected();
    }
    
    public void connect(final String host, final int port) throws LDAPException {
        this.authDN = null;
        this.authPW = null;
        this.responseControls = null;
        try {
            this.conn.close();
            if (this.socketFactory == null) {
                this.conn = new com.unboundid.ldap.sdk.LDAPConnection(host, port);
            }
            else {
                this.conn = new com.unboundid.ldap.sdk.LDAPConnection(new LDAPToJavaSocketFactory(this.socketFactory), host, port);
            }
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(le);
        }
    }
    
    public void connect(final String host, final int port, final String dn, final String password) throws LDAPException {
        this.connect(3, host, port, dn, password, null);
    }
    
    public void connect(final String host, final int port, final String dn, final String password, final LDAPConstraints constraints) throws LDAPException {
        this.connect(3, host, port, dn, password, constraints);
    }
    
    public void connect(final int version, final String host, final int port, final String dn, final String password) throws LDAPException {
        this.connect(version, host, port, dn, password, null);
    }
    
    public void connect(final int version, final String host, final int port, final String dn, final String password, final LDAPConstraints constraints) throws LDAPException {
        this.connect(host, port);
        try {
            if (dn != null && password != null) {
                this.bind(version, dn, password, constraints);
            }
        }
        catch (final LDAPException le) {
            this.conn.close();
            throw le;
        }
    }
    
    public void disconnect() throws LDAPException {
        this.authDN = null;
        this.authPW = null;
        this.conn.close();
        if (this.socketFactory == null) {
            this.conn = new com.unboundid.ldap.sdk.LDAPConnection();
        }
        else {
            this.conn = new com.unboundid.ldap.sdk.LDAPConnection(new LDAPToJavaSocketFactory(this.socketFactory));
        }
    }
    
    public void reconnect() throws LDAPException {
        final String host = this.getHost();
        final int port = this.getPort();
        final String dn = this.authDN;
        final String pw = this.authPW;
        if (dn == null || pw == null) {
            this.connect(host, port);
        }
        else {
            this.connect(host, port, dn, pw);
        }
    }
    
    public void abandon(final int id) throws LDAPException {
        try {
            this.conn.abandon(InternalSDKHelper.createAsyncRequestID(id, this.conn), this.getControls(null));
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(le);
        }
    }
    
    public void abandon(final LDAPSearchResults searchResults) throws LDAPException {
        try {
            final AsyncRequestID requestID = searchResults.getAsyncRequestID();
            if (requestID == null) {
                throw new LDAPException("The search request has not been sent to the server", 89);
            }
            searchResults.setAbandoned();
            this.conn.abandon(requestID);
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(le);
        }
    }
    
    public void add(final LDAPEntry entry) throws LDAPException {
        this.add(entry, null);
    }
    
    public void add(final LDAPEntry entry, final LDAPConstraints constraints) throws LDAPException {
        final AddRequest addRequest = new AddRequest(entry.toEntry());
        this.update(addRequest, constraints);
        try {
            final LDAPResult result = this.conn.add(addRequest);
            this.setResponseControls(result);
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public void authenticate(final String dn, final String password) throws LDAPException {
        this.bind(3, dn, password, null);
    }
    
    public void authenticate(final String dn, final String password, final LDAPConstraints constraints) throws LDAPException {
        this.bind(3, dn, password, constraints);
    }
    
    public void authenticate(final int version, final String dn, final String password) throws LDAPException {
        this.bind(version, dn, password, null);
    }
    
    public void authenticate(final int version, final String dn, final String password, final LDAPConstraints constraints) throws LDAPException {
        this.bind(version, dn, password, constraints);
    }
    
    public void bind(final String dn, final String password) throws LDAPException {
        this.bind(3, dn, password, null);
    }
    
    public void bind(final String dn, final String password, final LDAPConstraints constraints) throws LDAPException {
        this.bind(3, dn, password, constraints);
    }
    
    public void bind(final int version, final String dn, final String password) throws LDAPException {
        this.bind(version, dn, password, null);
    }
    
    public void bind(final int version, final String dn, final String password, final LDAPConstraints constraints) throws LDAPException {
        final SimpleBindRequest bindRequest = new SimpleBindRequest(dn, password, this.getControls(constraints));
        this.authDN = null;
        this.authPW = null;
        try {
            final BindResult bindResult = this.conn.bind(bindRequest);
            this.setResponseControls(bindResult);
            if (bindResult.getResultCode() == ResultCode.SUCCESS) {
                this.authDN = dn;
                this.authPW = password;
            }
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public boolean compare(final String dn, final LDAPAttribute attribute) throws LDAPException {
        return this.compare(dn, attribute, null);
    }
    
    public boolean compare(final String dn, final LDAPAttribute attribute, final LDAPConstraints constraints) throws LDAPException {
        final CompareRequest compareRequest = new CompareRequest(dn, attribute.getName(), attribute.getByteValueArray()[0]);
        this.update(compareRequest, constraints);
        try {
            final CompareResult result = this.conn.compare(compareRequest);
            this.setResponseControls(result);
            return result.compareMatched();
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public void delete(final String dn) throws LDAPException {
        this.delete(dn, null);
    }
    
    public void delete(final String dn, final LDAPConstraints constraints) throws LDAPException {
        final DeleteRequest deleteRequest = new DeleteRequest(dn);
        this.update(deleteRequest, constraints);
        try {
            final LDAPResult result = this.conn.delete(deleteRequest);
            this.setResponseControls(result);
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public LDAPExtendedOperation extendedOperation(final LDAPExtendedOperation extendedOperation) throws LDAPException {
        return this.extendedOperation(extendedOperation, null);
    }
    
    public LDAPExtendedOperation extendedOperation(final LDAPExtendedOperation extendedOperation, final LDAPConstraints constraints) throws LDAPException {
        final ExtendedRequest extendedRequest = new ExtendedRequest(extendedOperation.getID(), new ASN1OctetString(extendedOperation.getValue()), this.getControls(constraints));
        try {
            final ExtendedResult result = this.conn.processExtendedOperation(extendedRequest);
            this.setResponseControls(result);
            if (result.getResultCode() != ResultCode.SUCCESS) {
                throw new LDAPException(result.getDiagnosticMessage(), result.getResultCode().intValue(), result.getDiagnosticMessage(), result.getMatchedDN());
            }
            final ASN1OctetString value = result.getValue();
            byte[] valueBytes;
            if (value == null) {
                valueBytes = null;
            }
            else {
                valueBytes = value.getValue();
            }
            return new LDAPExtendedOperation(result.getOID(), valueBytes);
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public void modify(final String dn, final LDAPModification mod) throws LDAPException {
        this.modify(dn, new LDAPModification[] { mod }, null);
    }
    
    public void modify(final String dn, final LDAPModification[] mods) throws LDAPException {
        this.modify(dn, mods, null);
    }
    
    public void modify(final String dn, final LDAPModification mod, final LDAPConstraints constraints) throws LDAPException {
        this.modify(dn, new LDAPModification[] { mod }, constraints);
    }
    
    public void modify(final String dn, final LDAPModification[] mods, final LDAPConstraints constraints) throws LDAPException {
        final Modification[] m = new Modification[mods.length];
        for (int i = 0; i < mods.length; ++i) {
            m[i] = mods[i].toModification();
        }
        final ModifyRequest modifyRequest = new ModifyRequest(dn, m);
        this.update(modifyRequest, constraints);
        try {
            final LDAPResult result = this.conn.modify(modifyRequest);
            this.setResponseControls(result);
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public void modify(final String dn, final LDAPModificationSet mods) throws LDAPException {
        this.modify(dn, mods.toArray(), null);
    }
    
    public void modify(final String dn, final LDAPModificationSet mods, final LDAPConstraints constraints) throws LDAPException {
        this.modify(dn, mods.toArray(), constraints);
    }
    
    public LDAPEntry read(final String dn) throws LDAPException {
        return this.read(dn, null, null);
    }
    
    public LDAPEntry read(final String dn, final LDAPSearchConstraints constraints) throws LDAPException {
        return this.read(dn, null, constraints);
    }
    
    public LDAPEntry read(final String dn, final String[] attrs) throws LDAPException {
        return this.read(dn, attrs, null);
    }
    
    public LDAPEntry read(final String dn, final String[] attrs, final LDAPSearchConstraints constraints) throws LDAPException {
        final Filter filter = Filter.createORFilter(Filter.createPresenceFilter("objectClass"), Filter.createEqualityFilter("objectClass", "ldapSubentry"));
        final SearchRequest searchRequest = new SearchRequest(dn, SearchScope.BASE, filter, attrs);
        this.update(searchRequest, constraints);
        try {
            final SearchResult searchResult = this.conn.search(searchRequest);
            this.setResponseControls(searchResult);
            if (searchResult.getEntryCount() != 1) {
                throw new LDAPException(null, 94);
            }
            return new LDAPEntry(searchResult.getSearchEntries().get(0));
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public void rename(final String dn, final String newRDN, final boolean deleteOldRDN) throws LDAPException {
        this.rename(dn, newRDN, null, deleteOldRDN, null);
    }
    
    public void rename(final String dn, final String newRDN, final boolean deleteOldRDN, final LDAPConstraints constraints) throws LDAPException {
        this.rename(dn, newRDN, null, deleteOldRDN, constraints);
    }
    
    public void rename(final String dn, final String newRDN, final String newParentDN, final boolean deleteOldRDN) throws LDAPException {
        this.rename(dn, newRDN, newParentDN, deleteOldRDN, null);
    }
    
    public void rename(final String dn, final String newRDN, final String newParentDN, final boolean deleteOldRDN, final LDAPConstraints constraints) throws LDAPException {
        final ModifyDNRequest modifyDNRequest = new ModifyDNRequest(dn, newRDN, deleteOldRDN, newParentDN);
        this.update(modifyDNRequest, constraints);
        try {
            final LDAPResult result = this.conn.modifyDN(modifyDNRequest);
            this.setResponseControls(result);
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    public LDAPSearchResults search(final String baseDN, final int scope, final String filter, final String[] attributes, final boolean typesOnly) throws LDAPException {
        return this.search(baseDN, scope, filter, attributes, typesOnly, null);
    }
    
    public LDAPSearchResults search(final String baseDN, final int scope, final String filter, final String[] attributes, final boolean typesOnly, final LDAPSearchConstraints constraints) throws LDAPException {
        final LDAPSearchConstraints c = (constraints == null) ? this.searchConstraints : constraints;
        final LDAPSearchResults results = new LDAPSearchResults(c.getTimeLimit());
        try {
            final SearchRequest searchRequest = new SearchRequest(results, baseDN, SearchScope.valueOf(scope), filter, attributes);
            searchRequest.setDerefPolicy(DereferencePolicy.valueOf(c.getDereference()));
            searchRequest.setSizeLimit(c.getMaxResults());
            searchRequest.setTimeLimitSeconds(c.getServerTimeLimit());
            searchRequest.setTypesOnly(typesOnly);
            this.update(searchRequest, constraints);
            results.setAsyncRequestID(this.conn.asyncSearch(searchRequest));
            return results;
        }
        catch (final com.unboundid.ldap.sdk.LDAPException le) {
            Debug.debugException(le);
            this.setResponseControls(le);
            throw new LDAPException(le);
        }
    }
    
    private Control[] getControls(final LDAPConstraints c) {
        Control[] controls = null;
        if (c != null) {
            controls = LDAPControl.toControls(c.getServerControls());
        }
        else if (this.constraints != null) {
            controls = LDAPControl.toControls(this.constraints.getServerControls());
        }
        if (controls == null) {
            return new Control[0];
        }
        return controls;
    }
    
    private void update(final UpdatableLDAPRequest request, final LDAPConstraints constraints) {
        final LDAPConstraints c = (constraints == null) ? this.constraints : constraints;
        request.setControls(LDAPControl.toControls(c.getServerControls()));
        request.setResponseTimeoutMillis(c.getTimeLimit());
        request.setFollowReferrals(c.getReferrals());
    }
    
    private void setResponseControls(final LDAPResult ldapResult) {
        if (ldapResult.hasResponseControl()) {
            this.responseControls = LDAPControl.toLDAPControls(ldapResult.getResponseControls());
        }
        else {
            this.responseControls = null;
        }
    }
    
    private void setResponseControls(final com.unboundid.ldap.sdk.LDAPException ldapException) {
        if (ldapException.hasResponseControl()) {
            this.responseControls = LDAPControl.toLDAPControls(ldapException.getResponseControls());
        }
        else {
            this.responseControls = null;
        }
    }
    
    static {
        DEREF_NEVER = DereferencePolicy.NEVER.intValue();
        DEREF_SEARCHING = DereferencePolicy.SEARCHING.intValue();
        DEREF_FINDING = DereferencePolicy.FINDING.intValue();
        DEREF_ALWAYS = DereferencePolicy.ALWAYS.intValue();
    }
}

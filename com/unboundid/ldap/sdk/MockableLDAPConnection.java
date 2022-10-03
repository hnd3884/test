package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldif.LDIFException;
import java.util.Collection;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
public class MockableLDAPConnection implements FullLDAPInterface
{
    private final LDAPConnection connection;
    
    public MockableLDAPConnection(final LDAPConnection connection) {
        Validator.ensureNotNullWithMessage(connection, "MockableLDAPConnection.connection must not be null.");
        this.connection = connection;
    }
    
    public final LDAPConnection getWrappedConnection() {
        return this.connection;
    }
    
    @Override
    public void close() {
        this.connection.close();
    }
    
    @Override
    public RootDSE getRootDSE() throws LDAPException {
        return this.connection.getRootDSE();
    }
    
    @Override
    public Schema getSchema() throws LDAPException {
        return this.connection.getSchema();
    }
    
    @Override
    public Schema getSchema(final String entryDN) throws LDAPException {
        return this.connection.getSchema(entryDN);
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn) throws LDAPException {
        return this.connection.getEntry(dn);
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn, final String... attributes) throws LDAPException {
        return this.connection.getEntry(dn, attributes);
    }
    
    @Override
    public LDAPResult add(final String dn, final Attribute... attributes) throws LDAPException {
        return this.connection.add(dn, attributes);
    }
    
    @Override
    public LDAPResult add(final String dn, final Collection<Attribute> attributes) throws LDAPException {
        return this.connection.add(dn, attributes);
    }
    
    @Override
    public LDAPResult add(final Entry entry) throws LDAPException {
        return this.connection.add(entry);
    }
    
    @Override
    public LDAPResult add(final String... ldifLines) throws LDIFException, LDAPException {
        return this.connection.add(ldifLines);
    }
    
    @Override
    public LDAPResult add(final AddRequest addRequest) throws LDAPException {
        return this.connection.add(addRequest);
    }
    
    @Override
    public LDAPResult add(final ReadOnlyAddRequest addRequest) throws LDAPException {
        return this.connection.add(addRequest);
    }
    
    @Override
    public BindResult bind(final String bindDN, final String password) throws LDAPException {
        return this.connection.bind(bindDN, password);
    }
    
    @Override
    public BindResult bind(final BindRequest bindRequest) throws LDAPException {
        return this.connection.bind(bindRequest);
    }
    
    @Override
    public CompareResult compare(final String dn, final String attributeName, final String assertionValue) throws LDAPException {
        return this.connection.compare(dn, attributeName, assertionValue);
    }
    
    @Override
    public CompareResult compare(final CompareRequest compareRequest) throws LDAPException {
        return this.connection.compare(compareRequest);
    }
    
    @Override
    public CompareResult compare(final ReadOnlyCompareRequest compareRequest) throws LDAPException {
        return this.connection.compare(compareRequest);
    }
    
    @Override
    public LDAPResult delete(final String dn) throws LDAPException {
        return this.connection.delete(dn);
    }
    
    @Override
    public LDAPResult delete(final DeleteRequest deleteRequest) throws LDAPException {
        return this.connection.delete(deleteRequest);
    }
    
    @Override
    public LDAPResult delete(final ReadOnlyDeleteRequest deleteRequest) throws LDAPException {
        return this.connection.delete(deleteRequest);
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final String requestOID) throws LDAPException {
        return this.connection.processExtendedOperation(requestOID);
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final String requestOID, final ASN1OctetString requestValue) throws LDAPException {
        return this.connection.processExtendedOperation(requestOID, requestValue);
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final ExtendedRequest extendedRequest) throws LDAPException {
        return this.connection.processExtendedOperation(extendedRequest);
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification mod) throws LDAPException {
        return this.connection.modify(dn, mod);
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification... mods) throws LDAPException {
        return this.connection.modify(dn, mods);
    }
    
    @Override
    public LDAPResult modify(final String dn, final List<Modification> mods) throws LDAPException {
        return this.connection.modify(dn, mods);
    }
    
    @Override
    public LDAPResult modify(final String... ldifModificationLines) throws LDIFException, LDAPException {
        return this.connection.modify(ldifModificationLines);
    }
    
    @Override
    public LDAPResult modify(final ModifyRequest modifyRequest) throws LDAPException {
        return this.connection.modify(modifyRequest);
    }
    
    @Override
    public LDAPResult modify(final ReadOnlyModifyRequest modifyRequest) throws LDAPException {
        return this.connection.modify(modifyRequest);
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN) throws LDAPException {
        return this.connection.modifyDN(dn, newRDN, deleteOldRDN);
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) throws LDAPException {
        return this.connection.modifyDN(dn, newRDN, deleteOldRDN, newSuperiorDN);
    }
    
    @Override
    public LDAPResult modifyDN(final ModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.connection.modifyDN(modifyDNRequest);
    }
    
    @Override
    public LDAPResult modifyDN(final ReadOnlyModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.connection.modifyDN(modifyDNRequest);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(searchResultListener, baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(searchResultListener, baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.connection.search(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchRequest searchRequest) throws LDAPSearchException {
        return this.connection.search(searchRequest);
    }
    
    @Override
    public SearchResult search(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.connection.search(searchRequest);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.connection.searchForEntry(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.connection.searchForEntry(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.connection.searchForEntry(baseDN, scope, derefPolicy, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.connection.searchForEntry(baseDN, scope, derefPolicy, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final SearchRequest searchRequest) throws LDAPSearchException {
        return this.connection.searchForEntry(searchRequest);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.connection.searchForEntry(searchRequest);
    }
}

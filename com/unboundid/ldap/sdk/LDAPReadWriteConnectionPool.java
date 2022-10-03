package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.ldif.LDIFException;
import java.util.Collection;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPReadWriteConnectionPool implements LDAPInterface, Closeable
{
    private final LDAPConnectionPool readPool;
    private final LDAPConnectionPool writePool;
    
    public LDAPReadWriteConnectionPool(final LDAPConnection readConnection, final int initialReadConnections, final int maxReadConnections, final LDAPConnection writeConnection, final int initialWriteConnections, final int maxWriteConnections) throws LDAPException {
        Validator.ensureNotNull(readConnection, writeConnection);
        Validator.ensureTrue(initialReadConnections >= 1, "LDAPReadWriteConnectionPool.initialReadConnections must be at least 1.");
        Validator.ensureTrue(maxReadConnections >= initialReadConnections, "LDAPReadWriteConnectionPool.initialReadConnections must not be greater than maxReadConnections.");
        Validator.ensureTrue(initialWriteConnections >= 1, "LDAPReadWriteConnectionPool.initialWriteConnections must be at least 1.");
        Validator.ensureTrue(maxWriteConnections >= initialWriteConnections, "LDAPReadWriteConnectionPool.initialWriteConnections must not be greater than maxWriteConnections.");
        this.readPool = new LDAPConnectionPool(readConnection, initialReadConnections, maxReadConnections);
        try {
            this.writePool = new LDAPConnectionPool(writeConnection, initialWriteConnections, maxWriteConnections);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.readPool.close();
            throw le;
        }
    }
    
    public LDAPReadWriteConnectionPool(final LDAPConnectionPool readPool, final LDAPConnectionPool writePool) {
        Validator.ensureNotNull(readPool, writePool);
        this.readPool = readPool;
        this.writePool = writePool;
    }
    
    @Override
    public void close() {
        this.readPool.close();
        this.writePool.close();
    }
    
    public boolean isClosed() {
        return this.readPool.isClosed() || this.writePool.isClosed();
    }
    
    public LDAPConnection getReadConnection() throws LDAPException {
        return this.readPool.getConnection();
    }
    
    public void releaseReadConnection(final LDAPConnection connection) {
        this.readPool.releaseConnection(connection);
    }
    
    public void releaseDefunctReadConnection(final LDAPConnection connection) {
        this.readPool.releaseDefunctConnection(connection);
    }
    
    public LDAPConnection getWriteConnection() throws LDAPException {
        return this.writePool.getConnection();
    }
    
    public void releaseWriteConnection(final LDAPConnection connection) {
        this.writePool.releaseConnection(connection);
    }
    
    public void releaseDefunctWriteConnection(final LDAPConnection connection) {
        this.writePool.releaseDefunctConnection(connection);
    }
    
    public LDAPConnectionPoolStatistics getReadPoolStatistics() {
        return this.readPool.getConnectionPoolStatistics();
    }
    
    public LDAPConnectionPoolStatistics getWritePoolStatistics() {
        return this.writePool.getConnectionPoolStatistics();
    }
    
    public LDAPConnectionPool getReadPool() {
        return this.readPool;
    }
    
    public LDAPConnectionPool getWritePool() {
        return this.writePool;
    }
    
    @Override
    public RootDSE getRootDSE() throws LDAPException {
        return this.readPool.getRootDSE();
    }
    
    @Override
    public Schema getSchema() throws LDAPException {
        return this.readPool.getSchema();
    }
    
    @Override
    public Schema getSchema(final String entryDN) throws LDAPException {
        return this.readPool.getSchema(entryDN);
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn) throws LDAPException {
        return this.readPool.getEntry(dn);
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn, final String... attributes) throws LDAPException {
        return this.readPool.getEntry(dn, attributes);
    }
    
    @Override
    public LDAPResult add(final String dn, final Attribute... attributes) throws LDAPException {
        return this.writePool.add(dn, attributes);
    }
    
    @Override
    public LDAPResult add(final String dn, final Collection<Attribute> attributes) throws LDAPException {
        return this.writePool.add(dn, attributes);
    }
    
    @Override
    public LDAPResult add(final Entry entry) throws LDAPException {
        return this.writePool.add(entry);
    }
    
    @Override
    public LDAPResult add(final String... ldifLines) throws LDIFException, LDAPException {
        return this.writePool.add(ldifLines);
    }
    
    @Override
    public LDAPResult add(final AddRequest addRequest) throws LDAPException {
        return this.writePool.add(addRequest);
    }
    
    @Override
    public LDAPResult add(final ReadOnlyAddRequest addRequest) throws LDAPException {
        return this.writePool.add((AddRequest)addRequest);
    }
    
    public BindResult bind(final String bindDN, final String password) throws LDAPException {
        return this.readPool.bind(bindDN, password);
    }
    
    public BindResult bind(final BindRequest bindRequest) throws LDAPException {
        return this.readPool.bind(bindRequest);
    }
    
    @Override
    public CompareResult compare(final String dn, final String attributeName, final String assertionValue) throws LDAPException {
        return this.readPool.compare(dn, attributeName, assertionValue);
    }
    
    @Override
    public CompareResult compare(final CompareRequest compareRequest) throws LDAPException {
        return this.readPool.compare(compareRequest);
    }
    
    @Override
    public CompareResult compare(final ReadOnlyCompareRequest compareRequest) throws LDAPException {
        return this.readPool.compare(compareRequest);
    }
    
    @Override
    public LDAPResult delete(final String dn) throws LDAPException {
        return this.writePool.delete(dn);
    }
    
    @Override
    public LDAPResult delete(final DeleteRequest deleteRequest) throws LDAPException {
        return this.writePool.delete(deleteRequest);
    }
    
    @Override
    public LDAPResult delete(final ReadOnlyDeleteRequest deleteRequest) throws LDAPException {
        return this.writePool.delete(deleteRequest);
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification mod) throws LDAPException {
        return this.writePool.modify(dn, mod);
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification... mods) throws LDAPException {
        return this.writePool.modify(dn, mods);
    }
    
    @Override
    public LDAPResult modify(final String dn, final List<Modification> mods) throws LDAPException {
        return this.writePool.modify(dn, mods);
    }
    
    @Override
    public LDAPResult modify(final String... ldifModificationLines) throws LDIFException, LDAPException {
        return this.writePool.modify(ldifModificationLines);
    }
    
    @Override
    public LDAPResult modify(final ModifyRequest modifyRequest) throws LDAPException {
        return this.writePool.modify(modifyRequest);
    }
    
    @Override
    public LDAPResult modify(final ReadOnlyModifyRequest modifyRequest) throws LDAPException {
        return this.writePool.modify(modifyRequest);
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN) throws LDAPException {
        return this.writePool.modifyDN(dn, newRDN, deleteOldRDN);
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) throws LDAPException {
        return this.writePool.modifyDN(dn, newRDN, deleteOldRDN, newSuperiorDN);
    }
    
    @Override
    public LDAPResult modifyDN(final ModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.writePool.modifyDN(modifyDNRequest);
    }
    
    @Override
    public LDAPResult modifyDN(final ReadOnlyModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.writePool.modifyDN(modifyDNRequest);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(searchResultListener, baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(searchResultListener, baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.search(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResult search(final SearchRequest searchRequest) throws LDAPSearchException {
        return this.readPool.search(searchRequest);
    }
    
    @Override
    public SearchResult search(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.readPool.search(searchRequest);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.searchForEntry(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.searchForEntry(baseDN, scope, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.searchForEntry(baseDN, scope, derefPolicy, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.readPool.searchForEntry(baseDN, scope, derefPolicy, timeLimit, typesOnly, filter, attributes);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final SearchRequest searchRequest) throws LDAPSearchException {
        return this.readPool.searchForEntry(searchRequest);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.readPool.searchForEntry(searchRequest);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}

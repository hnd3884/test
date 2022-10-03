package com.unboundid.ldap.sdk;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldif.LDIFException;
import java.util.Collection;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.EnumSet;
import java.util.Set;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Closeable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class AbstractConnectionPool implements FullLDAPInterface, Closeable
{
    @Override
    public abstract void close();
    
    public abstract void close(final boolean p0, final int p1);
    
    public abstract boolean isClosed();
    
    public abstract LDAPConnection getConnection() throws LDAPException;
    
    public abstract void releaseConnection(final LDAPConnection p0);
    
    public abstract void releaseDefunctConnection(final LDAPConnection p0);
    
    public final void releaseConnectionAfterException(final LDAPConnection connection, final LDAPException exception) {
        final LDAPConnectionPoolHealthCheck healthCheck = this.getHealthCheck();
        try {
            healthCheck.ensureConnectionValidAfterException(connection, exception);
            this.releaseConnection(connection);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.releaseDefunctConnection(connection);
        }
    }
    
    public abstract LDAPConnection replaceDefunctConnection(final LDAPConnection p0) throws LDAPException;
    
    private LDAPConnection replaceDefunctConnection(final Throwable t, final LDAPConnection connection) throws LDAPException {
        try {
            return this.replaceDefunctConnection(connection);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (t instanceof LDAPException) {
                throw (LDAPException)t;
            }
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_POOL_OP_EXCEPTION.get(StaticUtils.getExceptionMessage(t)), t);
        }
    }
    
    public final boolean retryFailedOperationsDueToInvalidConnections() {
        return !this.getOperationTypesToRetryDueToInvalidConnections().isEmpty();
    }
    
    public abstract Set<OperationType> getOperationTypesToRetryDueToInvalidConnections();
    
    public final void setRetryFailedOperationsDueToInvalidConnections(final boolean retryFailedOperationsDueToInvalidConnections) {
        if (retryFailedOperationsDueToInvalidConnections) {
            this.setRetryFailedOperationsDueToInvalidConnections(EnumSet.allOf(OperationType.class));
        }
        else {
            this.setRetryFailedOperationsDueToInvalidConnections(EnumSet.noneOf(OperationType.class));
        }
    }
    
    public abstract void setRetryFailedOperationsDueToInvalidConnections(final Set<OperationType> p0);
    
    public abstract int getCurrentAvailableConnections();
    
    public abstract int getMaximumAvailableConnections();
    
    public abstract LDAPConnectionPoolStatistics getConnectionPoolStatistics();
    
    public abstract String getConnectionPoolName();
    
    public abstract void setConnectionPoolName(final String p0);
    
    public abstract LDAPConnectionPoolHealthCheck getHealthCheck();
    
    public abstract long getHealthCheckIntervalMillis();
    
    public abstract void setHealthCheckIntervalMillis(final long p0);
    
    protected abstract void doHealthCheck();
    
    @Override
    public final RootDSE getRootDSE() throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final RootDSE rootDSE = conn.getRootDSE();
            this.releaseConnection(conn);
            return rootDSE;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.SEARCH, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final RootDSE rootDSE2 = newConn.getRootDSE();
                this.releaseConnection(newConn);
                return rootDSE2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final Schema getSchema() throws LDAPException {
        return this.getSchema("");
    }
    
    @Override
    public final Schema getSchema(final String entryDN) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final Schema schema = conn.getSchema(entryDN);
            this.releaseConnection(conn);
            return schema;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.SEARCH, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final Schema schema2 = newConn.getSchema(entryDN);
                this.releaseConnection(newConn);
                return schema2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final SearchResultEntry getEntry(final String dn) throws LDAPException {
        return this.getEntry(dn, StaticUtils.NO_STRINGS);
    }
    
    @Override
    public final SearchResultEntry getEntry(final String dn, final String... attributes) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final SearchResultEntry entry = conn.getEntry(dn, attributes);
            this.releaseConnection(conn);
            return entry;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.SEARCH, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final SearchResultEntry entry2 = newConn.getEntry(dn, attributes);
                this.releaseConnection(newConn);
                return entry2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final LDAPResult add(final String dn, final Attribute... attributes) throws LDAPException {
        return this.add(new AddRequest(dn, attributes));
    }
    
    @Override
    public final LDAPResult add(final String dn, final Collection<Attribute> attributes) throws LDAPException {
        return this.add(new AddRequest(dn, attributes));
    }
    
    @Override
    public final LDAPResult add(final Entry entry) throws LDAPException {
        return this.add(new AddRequest(entry));
    }
    
    @Override
    public final LDAPResult add(final String... ldifLines) throws LDIFException, LDAPException {
        return this.add(new AddRequest(ldifLines));
    }
    
    @Override
    public final LDAPResult add(final AddRequest addRequest) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final LDAPResult result = conn.add(addRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.ADD, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final LDAPResult result2 = newConn.add(addRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final LDAPResult add(final ReadOnlyAddRequest addRequest) throws LDAPException {
        return this.add((AddRequest)addRequest);
    }
    
    @Override
    public final BindResult bind(final String bindDN, final String password) throws LDAPException {
        return this.bind(new SimpleBindRequest(bindDN, password));
    }
    
    @Override
    public final BindResult bind(final BindRequest bindRequest) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final BindResult result = conn.bind(bindRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.BIND, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final BindResult result2 = newConn.bind(bindRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final CompareResult compare(final String dn, final String attributeName, final String assertionValue) throws LDAPException {
        return this.compare(new CompareRequest(dn, attributeName, assertionValue));
    }
    
    @Override
    public final CompareResult compare(final CompareRequest compareRequest) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final CompareResult result = conn.compare(compareRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.COMPARE, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final CompareResult result2 = newConn.compare(compareRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final CompareResult compare(final ReadOnlyCompareRequest compareRequest) throws LDAPException {
        return this.compare((CompareRequest)compareRequest);
    }
    
    @Override
    public final LDAPResult delete(final String dn) throws LDAPException {
        return this.delete(new DeleteRequest(dn));
    }
    
    @Override
    public final LDAPResult delete(final DeleteRequest deleteRequest) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final LDAPResult result = conn.delete(deleteRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.DELETE, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final LDAPResult result2 = newConn.delete(deleteRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final LDAPResult delete(final ReadOnlyDeleteRequest deleteRequest) throws LDAPException {
        return this.delete((DeleteRequest)deleteRequest);
    }
    
    @Override
    public final ExtendedResult processExtendedOperation(final String requestOID) throws LDAPException {
        return this.processExtendedOperation(new ExtendedRequest(requestOID));
    }
    
    @Override
    public final ExtendedResult processExtendedOperation(final String requestOID, final ASN1OctetString requestValue) throws LDAPException {
        return this.processExtendedOperation(new ExtendedRequest(requestOID, requestValue));
    }
    
    @Override
    public final ExtendedResult processExtendedOperation(final ExtendedRequest extendedRequest) throws LDAPException {
        if (extendedRequest.getOID().equals("1.3.6.1.4.1.1466.20037")) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_POOL_STARTTLS_NOT_ALLOWED.get());
        }
        final LDAPConnection conn = this.getConnection();
        try {
            final ExtendedResult result = conn.processExtendedOperation(extendedRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.EXTENDED, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final ExtendedResult result2 = newConn.processExtendedOperation(extendedRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final LDAPResult modify(final String dn, final Modification mod) throws LDAPException {
        return this.modify(new ModifyRequest(dn, mod));
    }
    
    @Override
    public final LDAPResult modify(final String dn, final Modification... mods) throws LDAPException {
        return this.modify(new ModifyRequest(dn, mods));
    }
    
    @Override
    public final LDAPResult modify(final String dn, final List<Modification> mods) throws LDAPException {
        return this.modify(new ModifyRequest(dn, mods));
    }
    
    @Override
    public final LDAPResult modify(final String... ldifModificationLines) throws LDIFException, LDAPException {
        return this.modify(new ModifyRequest(ldifModificationLines));
    }
    
    @Override
    public final LDAPResult modify(final ModifyRequest modifyRequest) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final LDAPResult result = conn.modify(modifyRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.MODIFY, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final LDAPResult result2 = newConn.modify(modifyRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final LDAPResult modify(final ReadOnlyModifyRequest modifyRequest) throws LDAPException {
        return this.modify((ModifyRequest)modifyRequest);
    }
    
    @Override
    public final LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN) throws LDAPException {
        return this.modifyDN(new ModifyDNRequest(dn, newRDN, deleteOldRDN));
    }
    
    @Override
    public final LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) throws LDAPException {
        return this.modifyDN(new ModifyDNRequest(dn, newRDN, deleteOldRDN, newSuperiorDN));
    }
    
    @Override
    public final LDAPResult modifyDN(final ModifyDNRequest modifyDNRequest) throws LDAPException {
        final LDAPConnection conn = this.getConnection();
        try {
            final LDAPResult result = conn.modifyDN(modifyDNRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPExceptionIfShouldNotRetry(t, OperationType.MODIFY_DN, conn);
            final LDAPConnection newConn = this.replaceDefunctConnection(t, conn);
            try {
                final LDAPResult result2 = newConn.modifyDN(modifyDNRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final LDAPResult modifyDN(final ReadOnlyModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.modifyDN((ModifyDNRequest)modifyDNRequest);
    }
    
    @Override
    public final SearchResult search(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, parseFilter(filter), attributes));
    }
    
    @Override
    public final SearchResult search(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, filter, attributes));
    }
    
    @Override
    public final SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, parseFilter(filter), attributes));
    }
    
    @Override
    public final SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, filter, attributes));
    }
    
    @Override
    public final SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, parseFilter(filter), attributes));
    }
    
    @Override
    public final SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public final SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, parseFilter(filter), attributes));
    }
    
    @Override
    public final SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public final SearchResult search(final SearchRequest searchRequest) throws LDAPSearchException {
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
        try {
            final SearchResult result = conn.search(searchRequest);
            this.releaseConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            this.throwLDAPSearchExceptionIfShouldNotRetry(t, conn);
            LDAPConnection newConn;
            try {
                newConn = this.replaceDefunctConnection(t, conn);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                throw new LDAPSearchException(le2);
            }
            try {
                final SearchResult result2 = newConn.search(searchRequest);
                this.releaseConnection(newConn);
                return result2;
            }
            catch (final Throwable t2) {
                this.throwLDAPSearchException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final SearchResult search(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.search((SearchRequest)searchRequest);
    }
    
    @Override
    public final SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, DereferencePolicy.NEVER, 1, 0, false, parseFilter(filter), attributes));
    }
    
    @Override
    public final SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, DereferencePolicy.NEVER, 1, 0, false, filter, attributes));
    }
    
    @Override
    public final SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, derefPolicy, 1, timeLimit, typesOnly, parseFilter(filter), attributes));
    }
    
    @Override
    public final SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, derefPolicy, 1, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public final SearchResultEntry searchForEntry(final SearchRequest searchRequest) throws LDAPSearchException {
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
        try {
            final SearchResultEntry entry = conn.searchForEntry(searchRequest);
            this.releaseConnection(conn);
            return entry;
        }
        catch (final Throwable t) {
            this.throwLDAPSearchExceptionIfShouldNotRetry(t, conn);
            LDAPConnection newConn;
            try {
                newConn = this.replaceDefunctConnection(t, conn);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                throw new LDAPSearchException(le2);
            }
            try {
                final SearchResultEntry entry2 = newConn.searchForEntry(searchRequest);
                this.releaseConnection(newConn);
                return entry2;
            }
            catch (final Throwable t2) {
                this.throwLDAPSearchException(t2, newConn);
                return null;
            }
        }
    }
    
    @Override
    public final SearchResultEntry searchForEntry(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.searchForEntry((SearchRequest)searchRequest);
    }
    
    private static Filter parseFilter(final String filterString) throws LDAPSearchException {
        try {
            return Filter.create(filterString);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
    }
    
    public final List<LDAPResult> processRequests(final List<LDAPRequest> requests, final boolean continueOnError) throws LDAPException {
        Validator.ensureNotNull(requests);
        Validator.ensureFalse(requests.isEmpty(), "LDAPConnectionPool.processRequests.requests must not be empty.");
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
        final ArrayList<LDAPResult> results = new ArrayList<LDAPResult>(requests.size());
        boolean isDefunct = false;
        try {
        Label_0257:
            for (final LDAPRequest request : requests) {
                try {
                    final LDAPResult result = conn.processOperation(request);
                    results.add(result);
                    switch (result.getResultCode().intValue()) {
                        case 0:
                        case 5:
                        case 6:
                        case 16654: {
                            continue;
                        }
                        default: {
                            if (!ResultCode.isConnectionUsable(result.getResultCode())) {
                                isDefunct = true;
                            }
                            if (!continueOnError) {
                                break Label_0257;
                            }
                            continue;
                        }
                    }
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    results.add(new LDAPResult(request.getLastMessageID(), le2.getResultCode(), le2.getMessage(), le2.getMatchedDN(), le2.getReferralURLs(), le2.getResponseControls()));
                    if (!ResultCode.isConnectionUsable(le2.getResultCode())) {
                        isDefunct = true;
                    }
                    if (!continueOnError) {
                        break;
                    }
                    continue;
                }
            }
        }
        finally {
            if (isDefunct) {
                this.releaseDefunctConnection(conn);
            }
            else {
                this.releaseConnection(conn);
            }
        }
        return results;
    }
    
    public final List<AsyncRequestID> processRequestsAsync(final List<LDAPRequest> requests, final long maxWaitTimeMillis) throws LDAPException {
        Validator.ensureNotNull(requests);
        Validator.ensureFalse(requests.isEmpty(), "LDAPConnectionPool.processRequests.requests must not be empty.");
        for (final LDAPRequest r : requests) {
            switch (r.getOperationType()) {
                case ADD:
                case COMPARE:
                case DELETE:
                case MODIFY:
                case MODIFY_DN: {
                    continue;
                }
                case SEARCH: {
                    final SearchRequest searchRequest = (SearchRequest)r;
                    if (searchRequest.getSearchResultListener() == null || !(searchRequest.getSearchResultListener() instanceof AsyncSearchResultListener)) {
                        throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_POOL_PROCESS_REQUESTS_ASYNC_SEARCH_NOT_ASYNC.get(String.valueOf(r)));
                    }
                    continue;
                }
                default: {
                    throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_POOL_PROCESS_REQUESTS_ASYNC_OP_NOT_ASYNC.get(String.valueOf(r)));
                }
            }
        }
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
        final ArrayList<AsyncRequestID> requestIDs = new ArrayList<AsyncRequestID>(requests.size());
        boolean isDefunct = false;
        try {
            if (conn.synchronousMode()) {
                throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_POOL_PROCESS_REQUESTS_ASYNC_SYNCHRONOUS_MODE.get());
            }
            for (final LDAPRequest r2 : requests) {
                AsyncRequestID requestID = null;
                try {
                    switch (r2.getOperationType()) {
                        case ADD: {
                            requestID = conn.asyncAdd((AddRequest)r2, null);
                            break;
                        }
                        case COMPARE: {
                            requestID = conn.asyncCompare((CompareRequest)r2, null);
                            break;
                        }
                        case DELETE: {
                            requestID = conn.asyncDelete((DeleteRequest)r2, null);
                            break;
                        }
                        case MODIFY: {
                            requestID = conn.asyncModify((ModifyRequest)r2, null);
                            break;
                        }
                        case MODIFY_DN: {
                            requestID = conn.asyncModifyDN((ModifyDNRequest)r2, null);
                            break;
                        }
                        case SEARCH: {
                            requestID = conn.asyncSearch((SearchRequest)r2);
                            break;
                        }
                    }
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    requestID = new AsyncRequestID(r2.getLastMessageID(), conn);
                    requestID.setResult(le2.toLDAPResult());
                }
                requestIDs.add(requestID);
            }
            final long startWaitingTime = System.currentTimeMillis();
            long stopWaitingTime;
            if (maxWaitTimeMillis > 0L) {
                stopWaitingTime = startWaitingTime + maxWaitTimeMillis;
            }
            else {
                stopWaitingTime = Long.MAX_VALUE;
            }
            for (final AsyncRequestID requestID2 : requestIDs) {
                final long waitTime = stopWaitingTime - System.currentTimeMillis();
                LDAPResult result;
                if (waitTime > 0L) {
                    try {
                        result = requestID2.get(waitTime, TimeUnit.MILLISECONDS);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        requestID2.cancel(true);
                        if (e instanceof TimeoutException) {
                            result = new LDAPResult(requestID2.getMessageID(), ResultCode.TIMEOUT, LDAPMessages.ERR_POOL_PROCESS_REQUESTS_ASYNC_RESULT_TIMEOUT.get(System.currentTimeMillis() - startWaitingTime), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
                        }
                        else {
                            result = new LDAPResult(requestID2.getMessageID(), ResultCode.LOCAL_ERROR, LDAPMessages.ERR_POOL_PROCESS_REQUESTS_ASYNC_RESULT_EXCEPTION.get(StaticUtils.getExceptionMessage(e)), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
                        }
                        requestID2.setResult(result);
                    }
                }
                else {
                    requestID2.cancel(true);
                    result = new LDAPResult(requestID2.getMessageID(), ResultCode.TIMEOUT, LDAPMessages.ERR_POOL_PROCESS_REQUESTS_ASYNC_RESULT_TIMEOUT.get(System.currentTimeMillis() - startWaitingTime), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
                    requestID2.setResult(result);
                }
                if (!ResultCode.isConnectionUsable(result.getResultCode())) {
                    isDefunct = true;
                }
            }
            return requestIDs;
        }
        finally {
            if (isDefunct) {
                this.releaseDefunctConnection(conn);
            }
            else {
                this.releaseConnection(conn);
            }
        }
    }
    
    private void throwLDAPExceptionIfShouldNotRetry(final Throwable t, final OperationType o, final LDAPConnection conn) throws LDAPException {
        if (t instanceof LDAPException && this.getOperationTypesToRetryDueToInvalidConnections().contains(o)) {
            final LDAPException le = (LDAPException)t;
            final LDAPConnectionPoolHealthCheck healthCheck = this.getHealthCheck();
            try {
                healthCheck.ensureConnectionValidAfterException(conn, le);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return;
            }
        }
        this.throwLDAPException(t, conn);
    }
    
    private void throwLDAPSearchExceptionIfShouldNotRetry(final Throwable t, final LDAPConnection conn) throws LDAPSearchException {
        if (t instanceof LDAPException && this.getOperationTypesToRetryDueToInvalidConnections().contains(OperationType.SEARCH)) {
            final LDAPException le = (LDAPException)t;
            final LDAPConnectionPoolHealthCheck healthCheck = this.getHealthCheck();
            try {
                healthCheck.ensureConnectionValidAfterException(conn, le);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return;
            }
        }
        this.throwLDAPSearchException(t, conn);
    }
    
    void throwLDAPException(final Throwable t, final LDAPConnection conn) throws LDAPException {
        Debug.debugException(t);
        if (t instanceof LDAPException) {
            final LDAPException le = (LDAPException)t;
            this.releaseConnectionAfterException(conn, le);
            throw le;
        }
        this.releaseDefunctConnection(conn);
        StaticUtils.rethrowIfError(t);
        throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_POOL_OP_EXCEPTION.get(StaticUtils.getExceptionMessage(t)), t);
    }
    
    void throwLDAPSearchException(final Throwable t, final LDAPConnection conn) throws LDAPSearchException {
        Debug.debugException(t);
        if (t instanceof LDAPException) {
            LDAPSearchException lse;
            if (t instanceof LDAPSearchException) {
                lse = (LDAPSearchException)t;
            }
            else {
                lse = new LDAPSearchException((LDAPException)t);
            }
            this.releaseConnectionAfterException(conn, lse);
            throw lse;
        }
        this.releaseDefunctConnection(conn);
        StaticUtils.rethrowIfError(t);
        throw new LDAPSearchException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_POOL_OP_EXCEPTION.get(StaticUtils.getExceptionMessage(t)), t);
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
}

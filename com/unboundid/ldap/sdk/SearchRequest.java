package com.unboundid.ldap.sdk;

import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.StaticUtils;
import java.util.Timer;
import java.util.logging.Level;
import java.util.TimerTask;
import com.unboundid.ldap.protocol.LDAPMessage;
import java.util.Collection;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import com.unboundid.util.Validator;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.ldap.protocol.ProtocolOp;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SearchRequest extends UpdatableLDAPRequest implements ReadOnlySearchRequest, ResponseAcceptor, ProtocolOp
{
    public static final String ALL_USER_ATTRIBUTES = "*";
    public static final String ALL_OPERATIONAL_ATTRIBUTES = "+";
    public static final String NO_ATTRIBUTES = "1.1";
    public static final String[] REQUEST_ATTRS_DEFAULT;
    private static final long serialVersionUID = 1500219434086474893L;
    private String[] attributes;
    private boolean typesOnly;
    private DereferencePolicy derefPolicy;
    private int messageID;
    private int sizeLimit;
    private int timeLimit;
    private Filter filter;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    private final SearchResultListener searchResultListener;
    private SearchScope scope;
    private String baseDN;
    
    public SearchRequest(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPException {
        this(null, null, baseDN, scope, DereferencePolicy.NEVER, 0, 0, false, Filter.create(filter), attributes);
    }
    
    public SearchRequest(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) {
        this(null, null, baseDN, scope, DereferencePolicy.NEVER, 0, 0, false, filter, attributes);
    }
    
    public SearchRequest(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPException {
        this(searchResultListener, null, baseDN, scope, DereferencePolicy.NEVER, 0, 0, false, Filter.create(filter), attributes);
    }
    
    public SearchRequest(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) {
        this(searchResultListener, null, baseDN, scope, DereferencePolicy.NEVER, 0, 0, false, filter, attributes);
    }
    
    public SearchRequest(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPException {
        this(null, null, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, Filter.create(filter), attributes);
    }
    
    public SearchRequest(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) {
        this(null, null, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    public SearchRequest(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPException {
        this(searchResultListener, null, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, Filter.create(filter), attributes);
    }
    
    public SearchRequest(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) {
        this(searchResultListener, null, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes);
    }
    
    public SearchRequest(final SearchResultListener searchResultListener, final Control[] controls, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPException {
        this(searchResultListener, controls, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, Filter.create(filter), attributes);
    }
    
    public SearchRequest(final SearchResultListener searchResultListener, final Control[] controls, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>(50);
        Validator.ensureNotNull(baseDN, filter);
        this.baseDN = baseDN;
        this.scope = scope;
        this.derefPolicy = derefPolicy;
        this.typesOnly = typesOnly;
        this.filter = filter;
        this.searchResultListener = searchResultListener;
        if (sizeLimit < 0) {
            this.sizeLimit = 0;
        }
        else {
            this.sizeLimit = sizeLimit;
        }
        if (timeLimit < 0) {
            this.timeLimit = 0;
        }
        else {
            this.timeLimit = timeLimit;
        }
        if (attributes == null) {
            this.attributes = SearchRequest.REQUEST_ATTRS_DEFAULT;
        }
        else {
            this.attributes = attributes;
        }
    }
    
    @Override
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public void setBaseDN(final String baseDN) {
        Validator.ensureNotNull(baseDN);
        this.baseDN = baseDN;
    }
    
    public void setBaseDN(final DN baseDN) {
        Validator.ensureNotNull(baseDN);
        this.baseDN = baseDN.toString();
    }
    
    @Override
    public SearchScope getScope() {
        return this.scope;
    }
    
    public void setScope(final SearchScope scope) {
        this.scope = scope;
    }
    
    @Override
    public DereferencePolicy getDereferencePolicy() {
        return this.derefPolicy;
    }
    
    public void setDerefPolicy(final DereferencePolicy derefPolicy) {
        this.derefPolicy = derefPolicy;
    }
    
    @Override
    public int getSizeLimit() {
        return this.sizeLimit;
    }
    
    public void setSizeLimit(final int sizeLimit) {
        if (sizeLimit < 0) {
            this.sizeLimit = 0;
        }
        else {
            this.sizeLimit = sizeLimit;
        }
    }
    
    @Override
    public int getTimeLimitSeconds() {
        return this.timeLimit;
    }
    
    public void setTimeLimitSeconds(final int timeLimit) {
        if (timeLimit < 0) {
            this.timeLimit = 0;
        }
        else {
            this.timeLimit = timeLimit;
        }
    }
    
    @Override
    public boolean typesOnly() {
        return this.typesOnly;
    }
    
    public void setTypesOnly(final boolean typesOnly) {
        this.typesOnly = typesOnly;
    }
    
    @Override
    public Filter getFilter() {
        return this.filter;
    }
    
    public void setFilter(final String filter) throws LDAPException {
        Validator.ensureNotNull(filter);
        this.filter = Filter.create(filter);
    }
    
    public void setFilter(final Filter filter) {
        Validator.ensureNotNull(filter);
        this.filter = filter;
    }
    
    public String[] getAttributes() {
        return this.attributes;
    }
    
    @Override
    public List<String> getAttributeList() {
        return Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])this.attributes));
    }
    
    public void setAttributes(final String... attributes) {
        if (attributes == null) {
            this.attributes = SearchRequest.REQUEST_ATTRS_DEFAULT;
        }
        else {
            this.attributes = attributes;
        }
    }
    
    public void setAttributes(final List<String> attributes) {
        if (attributes == null) {
            this.attributes = SearchRequest.REQUEST_ATTRS_DEFAULT;
        }
        else {
            this.attributes = new String[attributes.size()];
            for (int i = 0; i < this.attributes.length; ++i) {
                this.attributes[i] = attributes.get(i);
            }
        }
    }
    
    public SearchResultListener getSearchResultListener() {
        return this.searchResultListener;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 99;
    }
    
    @Override
    public void writeTo(final ASN1Buffer writer) {
        final ASN1BufferSequence requestSequence = writer.beginSequence((byte)99);
        writer.addOctetString(this.baseDN);
        writer.addEnumerated(this.scope.intValue());
        writer.addEnumerated(this.derefPolicy.intValue());
        writer.addInteger(this.sizeLimit);
        writer.addInteger(this.timeLimit);
        writer.addBoolean(this.typesOnly);
        this.filter.writeTo(writer);
        final ASN1BufferSequence attrSequence = writer.beginSequence();
        for (final String s : this.attributes) {
            writer.addOctetString(s);
        }
        attrSequence.end();
        requestSequence.end();
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ASN1Element[] attrElements = new ASN1Element[this.attributes.length];
        for (int i = 0; i < attrElements.length; ++i) {
            attrElements[i] = new ASN1OctetString(this.attributes[i]);
        }
        final ASN1Element[] protocolOpElements = { new ASN1OctetString(this.baseDN), new ASN1Enumerated(this.scope.intValue()), new ASN1Enumerated(this.derefPolicy.intValue()), new ASN1Integer(this.sizeLimit), new ASN1Integer(this.timeLimit), new ASN1Boolean(this.typesOnly), this.filter.encode(), new ASN1Sequence(attrElements) };
        return new ASN1Sequence((byte)99, protocolOpElements);
    }
    
    @Override
    protected SearchResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        if (connection.synchronousMode()) {
            final boolean autoReconnect = connection.getConnectionOptions().autoReconnect();
            return this.processSync(connection, depth, autoReconnect);
        }
        final long requestTime = System.nanoTime();
        this.processAsync(connection, null);
        try {
            ArrayList<SearchResultEntry> entryList;
            ArrayList<SearchResultReference> referenceList;
            if (this.searchResultListener == null) {
                entryList = new ArrayList<SearchResultEntry>(5);
                referenceList = new ArrayList<SearchResultReference>(5);
            }
            else {
                entryList = null;
                referenceList = null;
            }
            int numEntries = 0;
            int numReferences = 0;
            ResultCode intermediateResultCode = ResultCode.SUCCESS;
            final long responseTimeout = this.getResponseTimeoutMillis(connection);
            while (true) {
                LDAPResponse response;
                try {
                    if (responseTimeout > 0L) {
                        response = this.responseQueue.poll(responseTimeout, TimeUnit.MILLISECONDS);
                    }
                    else {
                        response = this.responseQueue.take();
                    }
                }
                catch (final InterruptedException ie) {
                    Debug.debugException(ie);
                    Thread.currentThread().interrupt();
                    throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_SEARCH_INTERRUPTED.get(connection.getHostPort()), ie);
                }
                if (response == null) {
                    if (connection.getConnectionOptions().abandonOnTimeout()) {
                        connection.abandon(this.messageID, new Control[0]);
                    }
                    final SearchResult searchResult = new SearchResult(this.messageID, ResultCode.TIMEOUT, LDAPMessages.ERR_SEARCH_CLIENT_TIMEOUT.get(responseTimeout, this.messageID, this.baseDN, this.scope.getName(), this.filter.toString(), connection.getHostPort()), null, null, entryList, referenceList, numEntries, numReferences, null);
                    throw new LDAPSearchException(searchResult);
                }
                if (response instanceof ConnectionClosedResponse) {
                    final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
                    final String message = ccr.getMessage();
                    if (message == null) {
                        final SearchResult searchResult2 = new SearchResult(this.messageID, ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_SEARCH_RESPONSE.get(connection.getHostPort(), this.toString()), null, null, entryList, referenceList, numEntries, numReferences, null);
                        throw new LDAPSearchException(searchResult2);
                    }
                    final SearchResult searchResult2 = new SearchResult(this.messageID, ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_SEARCH_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), message), null, null, entryList, referenceList, numEntries, numReferences, null);
                    throw new LDAPSearchException(searchResult2);
                }
                else if (response instanceof SearchResultEntry) {
                    final SearchResultEntry searchEntry = (SearchResultEntry)response;
                    ++numEntries;
                    if (this.searchResultListener == null) {
                        entryList.add(searchEntry);
                    }
                    else {
                        this.searchResultListener.searchEntryReturned(searchEntry);
                    }
                }
                else if (response instanceof SearchResultReference) {
                    final SearchResultReference searchReference = (SearchResultReference)response;
                    if (this.followReferrals(connection)) {
                        final LDAPResult result = this.followSearchReference(this.messageID, searchReference, connection, depth);
                        if (!result.getResultCode().equals(ResultCode.SUCCESS)) {
                            ++numReferences;
                            if (this.searchResultListener == null) {
                                referenceList.add(searchReference);
                            }
                            else {
                                this.searchResultListener.searchReferenceReturned(searchReference);
                            }
                            if (!intermediateResultCode.equals(ResultCode.SUCCESS) || result.getResultCode() == ResultCode.REFERRAL) {
                                continue;
                            }
                            intermediateResultCode = result.getResultCode();
                        }
                        else {
                            if (!(result instanceof SearchResult)) {
                                continue;
                            }
                            final SearchResult searchResult2 = (SearchResult)result;
                            numEntries += searchResult2.getEntryCount();
                            if (this.searchResultListener != null) {
                                continue;
                            }
                            entryList.addAll(searchResult2.getSearchEntries());
                        }
                    }
                    else {
                        ++numReferences;
                        if (this.searchResultListener == null) {
                            referenceList.add(searchReference);
                        }
                        else {
                            this.searchResultListener.searchReferenceReturned(searchReference);
                        }
                    }
                }
                else {
                    connection.getConnectionStatistics().incrementNumSearchResponses(numEntries, numReferences, System.nanoTime() - requestTime);
                    SearchResult result2 = (SearchResult)response;
                    result2.setCounts(numEntries, entryList, numReferences, referenceList);
                    if (result2.getResultCode().equals(ResultCode.REFERRAL) && this.followReferrals(connection)) {
                        if (depth >= connection.getConnectionOptions().getReferralHopLimit()) {
                            return new SearchResult(this.messageID, ResultCode.REFERRAL_LIMIT_EXCEEDED, LDAPMessages.ERR_TOO_MANY_REFERRALS.get(), result2.getMatchedDN(), result2.getReferralURLs(), entryList, referenceList, numEntries, numReferences, result2.getResponseControls());
                        }
                        result2 = this.followReferral(result2, connection, depth);
                    }
                    if (result2.getResultCode().equals(ResultCode.SUCCESS) && !intermediateResultCode.equals(ResultCode.SUCCESS)) {
                        return new SearchResult(this.messageID, intermediateResultCode, result2.getDiagnosticMessage(), result2.getMatchedDN(), result2.getReferralURLs(), entryList, referenceList, numEntries, numReferences, result2.getResponseControls());
                    }
                    return result2;
                }
            }
        }
        finally {
            connection.deregisterResponseAcceptor(this.messageID);
        }
    }
    
    AsyncRequestID processAsync(final LDAPConnection connection, final AsyncSearchResultListener resultListener) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long timeout = this.getResponseTimeoutMillis(connection);
        AsyncRequestID asyncRequestID;
        if (resultListener == null) {
            asyncRequestID = null;
            connection.registerResponseAcceptor(this.messageID, this);
        }
        else {
            final AsyncSearchHelper helper = new AsyncSearchHelper(connection, this.messageID, resultListener, this.getIntermediateResponseListener());
            connection.registerResponseAcceptor(this.messageID, helper);
            asyncRequestID = helper.getAsyncRequestID();
            if (timeout > 0L) {
                final Timer timer = connection.getTimer();
                final AsyncTimeoutTimerTask timerTask = new AsyncTimeoutTimerTask(helper);
                timer.schedule(timerTask, timeout);
                asyncRequestID.setTimerTask(timerTask);
            }
        }
        try {
            Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
            connection.getConnectionStatistics().incrementNumSearchRequests();
            connection.sendMessage(message, timeout);
            return asyncRequestID;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            connection.deregisterResponseAcceptor(this.messageID);
            throw le;
        }
    }
    
    private SearchResult processSync(final LDAPConnection connection, final int depth, final boolean allowRetry) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long responseTimeout = this.getResponseTimeoutMillis(connection);
        final long requestTime = System.nanoTime();
        Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
        connection.getConnectionStatistics().incrementNumSearchRequests();
        try {
            connection.sendMessage(message, responseTimeout);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (allowRetry) {
                final SearchResult retryResult = this.reconnectAndRetry(connection, depth, le.getResultCode(), 0, 0);
                if (retryResult != null) {
                    return retryResult;
                }
            }
            throw le;
        }
        ArrayList<SearchResultEntry> entryList;
        ArrayList<SearchResultReference> referenceList;
        if (this.searchResultListener == null) {
            entryList = new ArrayList<SearchResultEntry>(5);
            referenceList = new ArrayList<SearchResultReference>(5);
        }
        else {
            entryList = null;
            referenceList = null;
        }
        int numEntries = 0;
        int numReferences = 0;
        ResultCode intermediateResultCode = ResultCode.SUCCESS;
        while (true) {
            LDAPResponse response;
            try {
                response = connection.readResponse(this.messageID);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                if (le2.getResultCode() == ResultCode.TIMEOUT && connection.getConnectionOptions().abandonOnTimeout()) {
                    connection.abandon(this.messageID, new Control[0]);
                }
                if (allowRetry) {
                    final SearchResult retryResult2 = this.reconnectAndRetry(connection, depth, le2.getResultCode(), numEntries, numReferences);
                    if (retryResult2 != null) {
                        return retryResult2;
                    }
                }
                throw le2;
            }
            if (response == null) {
                if (connection.getConnectionOptions().abandonOnTimeout()) {
                    connection.abandon(this.messageID, new Control[0]);
                }
                throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_SEARCH_CLIENT_TIMEOUT.get(responseTimeout, this.messageID, this.baseDN, this.scope.getName(), this.filter.toString(), connection.getHostPort()));
            }
            if (response instanceof ConnectionClosedResponse) {
                if (allowRetry) {
                    final SearchResult retryResult3 = this.reconnectAndRetry(connection, depth, ResultCode.SERVER_DOWN, numEntries, numReferences);
                    if (retryResult3 != null) {
                        return retryResult3;
                    }
                }
                final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
                final String msg = ccr.getMessage();
                if (msg == null) {
                    final SearchResult searchResult = new SearchResult(this.messageID, ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_SEARCH_RESPONSE.get(connection.getHostPort(), this.toString()), null, null, entryList, referenceList, numEntries, numReferences, null);
                    throw new LDAPSearchException(searchResult);
                }
                final SearchResult searchResult = new SearchResult(this.messageID, ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_SEARCH_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), msg), null, null, entryList, referenceList, numEntries, numReferences, null);
                throw new LDAPSearchException(searchResult);
            }
            else if (response instanceof IntermediateResponse) {
                final IntermediateResponseListener listener = this.getIntermediateResponseListener();
                if (listener == null) {
                    continue;
                }
                listener.intermediateResponseReturned((IntermediateResponse)response);
            }
            else if (response instanceof SearchResultEntry) {
                final SearchResultEntry searchEntry = (SearchResultEntry)response;
                ++numEntries;
                if (this.searchResultListener == null) {
                    entryList.add(searchEntry);
                }
                else {
                    this.searchResultListener.searchEntryReturned(searchEntry);
                }
            }
            else {
                if (!(response instanceof SearchResultReference)) {
                    final SearchResult result = (SearchResult)response;
                    if (allowRetry) {
                        final SearchResult retryResult2 = this.reconnectAndRetry(connection, depth, result.getResultCode(), numEntries, numReferences);
                        if (retryResult2 != null) {
                            return retryResult2;
                        }
                    }
                    return this.handleResponse(connection, response, requestTime, depth, numEntries, numReferences, entryList, referenceList, intermediateResultCode);
                }
                final SearchResultReference searchReference = (SearchResultReference)response;
                if (this.followReferrals(connection)) {
                    final LDAPResult result2 = this.followSearchReference(this.messageID, searchReference, connection, depth);
                    if (!result2.getResultCode().equals(ResultCode.SUCCESS)) {
                        ++numReferences;
                        if (this.searchResultListener == null) {
                            referenceList.add(searchReference);
                        }
                        else {
                            this.searchResultListener.searchReferenceReturned(searchReference);
                        }
                        if (!intermediateResultCode.equals(ResultCode.SUCCESS) || result2.getResultCode() == ResultCode.REFERRAL) {
                            continue;
                        }
                        intermediateResultCode = result2.getResultCode();
                    }
                    else {
                        if (!(result2 instanceof SearchResult)) {
                            continue;
                        }
                        final SearchResult searchResult = (SearchResult)result2;
                        numEntries += searchResult.getEntryCount();
                        if (this.searchResultListener != null) {
                            continue;
                        }
                        entryList.addAll(searchResult.getSearchEntries());
                    }
                }
                else {
                    ++numReferences;
                    if (this.searchResultListener == null) {
                        referenceList.add(searchReference);
                    }
                    else {
                        this.searchResultListener.searchReferenceReturned(searchReference);
                    }
                }
            }
        }
    }
    
    private SearchResult reconnectAndRetry(final LDAPConnection connection, final int depth, final ResultCode resultCode, final int numEntries, final int numReferences) {
        try {
            switch (resultCode.intValue()) {
                case 81:
                case 84:
                case 91: {
                    connection.reconnect();
                    if (numEntries == 0 && numReferences == 0) {
                        return this.processSync(connection, depth, false);
                    }
                    break;
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        return null;
    }
    
    private SearchResult handleResponse(final LDAPConnection connection, final LDAPResponse response, final long requestTime, final int depth, final int numEntries, final int numReferences, final List<SearchResultEntry> entryList, final List<SearchResultReference> referenceList, final ResultCode intermediateResultCode) throws LDAPException {
        connection.getConnectionStatistics().incrementNumSearchResponses(numEntries, numReferences, System.nanoTime() - requestTime);
        SearchResult result = (SearchResult)response;
        result.setCounts(numEntries, entryList, numReferences, referenceList);
        if (result.getResultCode().equals(ResultCode.REFERRAL) && this.followReferrals(connection)) {
            if (depth >= connection.getConnectionOptions().getReferralHopLimit()) {
                return new SearchResult(this.messageID, ResultCode.REFERRAL_LIMIT_EXCEEDED, LDAPMessages.ERR_TOO_MANY_REFERRALS.get(), result.getMatchedDN(), result.getReferralURLs(), entryList, referenceList, numEntries, numReferences, result.getResponseControls());
            }
            result = this.followReferral(result, connection, depth);
        }
        if (result.getResultCode().equals(ResultCode.SUCCESS) && !intermediateResultCode.equals(ResultCode.SUCCESS)) {
            return new SearchResult(this.messageID, intermediateResultCode, result.getDiagnosticMessage(), result.getMatchedDN(), result.getReferralURLs(), entryList, referenceList, numEntries, numReferences, result.getResponseControls());
        }
        return result;
    }
    
    private LDAPResult followSearchReference(final int messageID, final SearchResultReference searchReference, final LDAPConnection connection, final int depth) throws LDAPException {
        for (final String urlString : searchReference.getReferralURLs()) {
            try {
                final LDAPURL referralURL = new LDAPURL(urlString);
                final String host = referralURL.getHost();
                if (host != null) {
                    String requestBaseDN;
                    if (referralURL.baseDNProvided()) {
                        requestBaseDN = referralURL.getBaseDN().toString();
                    }
                    else {
                        requestBaseDN = this.baseDN;
                    }
                    SearchScope requestScope;
                    if (referralURL.scopeProvided()) {
                        requestScope = referralURL.getScope();
                    }
                    else {
                        requestScope = this.scope;
                    }
                    Filter requestFilter;
                    if (referralURL.filterProvided()) {
                        requestFilter = referralURL.getFilter();
                    }
                    else {
                        requestFilter = this.filter;
                    }
                    final SearchRequest searchRequest = new SearchRequest(this.searchResultListener, this.getControls(), requestBaseDN, requestScope, this.derefPolicy, this.sizeLimit, this.timeLimit, this.typesOnly, requestFilter, this.attributes);
                    final LDAPConnection referralConn = this.getReferralConnector(connection).getReferralConnection(referralURL, connection);
                    try {
                        return searchRequest.process(referralConn, depth + 1);
                    }
                    finally {
                        referralConn.setDisconnectInfo(DisconnectType.REFERRAL, null, null);
                        referralConn.close();
                    }
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                if (le.getResultCode().equals(ResultCode.REFERRAL_LIMIT_EXCEEDED)) {
                    throw le;
                }
            }
        }
        return new SearchResult(messageID, ResultCode.REFERRAL, null, null, searchReference.getReferralURLs(), 0, 0, null);
    }
    
    private SearchResult followReferral(final SearchResult referralResult, final LDAPConnection connection, final int depth) throws LDAPException {
        for (final String urlString : referralResult.getReferralURLs()) {
            try {
                final LDAPURL referralURL = new LDAPURL(urlString);
                final String host = referralURL.getHost();
                if (host != null) {
                    String requestBaseDN;
                    if (referralURL.baseDNProvided()) {
                        requestBaseDN = referralURL.getBaseDN().toString();
                    }
                    else {
                        requestBaseDN = this.baseDN;
                    }
                    SearchScope requestScope;
                    if (referralURL.scopeProvided()) {
                        requestScope = referralURL.getScope();
                    }
                    else {
                        requestScope = this.scope;
                    }
                    Filter requestFilter;
                    if (referralURL.filterProvided()) {
                        requestFilter = referralURL.getFilter();
                    }
                    else {
                        requestFilter = this.filter;
                    }
                    final SearchRequest searchRequest = new SearchRequest(this.searchResultListener, this.getControls(), requestBaseDN, requestScope, this.derefPolicy, this.sizeLimit, this.timeLimit, this.typesOnly, requestFilter, this.attributes);
                    final LDAPConnection referralConn = this.getReferralConnector(connection).getReferralConnection(referralURL, connection);
                    try {
                        return searchRequest.process(referralConn, depth + 1);
                    }
                    finally {
                        referralConn.setDisconnectInfo(DisconnectType.REFERRAL, null, null);
                        referralConn.close();
                    }
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                if (le.getResultCode().equals(ResultCode.REFERRAL_LIMIT_EXCEEDED)) {
                    throw le;
                }
            }
        }
        return referralResult;
    }
    
    @InternalUseOnly
    @Override
    public void responseReceived(final LDAPResponse response) throws LDAPException {
        try {
            this.responseQueue.put(response);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_EXCEPTION_HANDLING_RESPONSE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.SEARCH;
    }
    
    @Override
    public SearchRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public SearchRequest duplicate(final Control[] controls) {
        final SearchRequest r = new SearchRequest(this.searchResultListener, controls, this.baseDN, this.scope, this.derefPolicy, this.sizeLimit, this.timeLimit, this.typesOnly, this.filter, this.attributes);
        if (this.followReferralsInternal() != null) {
            r.setFollowReferrals(this.followReferralsInternal());
        }
        if (this.getReferralConnectorInternal() != null) {
            r.setReferralConnector(this.getReferralConnectorInternal());
        }
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SearchRequest(baseDN='");
        buffer.append(this.baseDN);
        buffer.append("', scope=");
        buffer.append(this.scope);
        buffer.append(", deref=");
        buffer.append(this.derefPolicy);
        buffer.append(", sizeLimit=");
        buffer.append(this.sizeLimit);
        buffer.append(", timeLimit=");
        buffer.append(this.timeLimit);
        buffer.append(", filter='");
        buffer.append(this.filter);
        buffer.append("', attrs={");
        for (int i = 0; i < this.attributes.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.attributes[i]);
        }
        buffer.append('}');
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int j = 0; j < controls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
    
    @Override
    public void toCode(final List<String> lineList, final String requestID, final int indentSpaces, final boolean includeProcessing) {
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(10);
        constructorArgs.add(ToCodeArgHelper.createString(this.baseDN, "Base DN"));
        constructorArgs.add(ToCodeArgHelper.createScope(this.scope, "Scope"));
        constructorArgs.add(ToCodeArgHelper.createDerefPolicy(this.derefPolicy, "Alias Dereference Policy"));
        constructorArgs.add(ToCodeArgHelper.createInteger(this.sizeLimit, "Size Limit"));
        constructorArgs.add(ToCodeArgHelper.createInteger(this.timeLimit, "Time Limit"));
        constructorArgs.add(ToCodeArgHelper.createBoolean(this.typesOnly, "Types Only"));
        constructorArgs.add(ToCodeArgHelper.createFilter(this.filter, "Filter"));
        String comment = "Requested Attributes";
        for (final String s : this.attributes) {
            constructorArgs.add(ToCodeArgHelper.createString(s, comment));
            comment = null;
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "SearchRequest", requestID + "Request", "new SearchRequest", constructorArgs);
        for (final Control c : this.getControls()) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "Request.addControl", ToCodeArgHelper.createControl(c, null));
        }
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + "SearchResult " + requestID + "Result;");
            lineList.add(indent + "try");
            lineList.add(indent + '{');
            lineList.add(indent + "  " + requestID + "Result = connection.search(" + requestID + "Request);");
            lineList.add(indent + "  // The search was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPSearchException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The search failed.  Maybe the following " + "will help explain why.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add("");
            lineList.add(indent + "  // Even though there was an error, we may " + "have gotten some results.");
            lineList.add(indent + "  " + requestID + "Result = e.getSearchResult();");
            lineList.add(indent + '}');
            lineList.add("");
            lineList.add(indent + "// If there were results, then process them.");
            lineList.add(indent + "for (SearchResultEntry e : " + requestID + "Result.getSearchEntries())");
            lineList.add(indent + '{');
            lineList.add(indent + "  // Do something with the entry.");
            lineList.add(indent + '}');
        }
    }
    
    static {
        REQUEST_ATTRS_DEFAULT = StaticUtils.NO_STRINGS;
    }
}

package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetEntryLDAPConnectionPoolHealthCheck extends LDAPConnectionPoolHealthCheck implements Serializable
{
    private static final long DEFAULT_MAX_RESPONSE_TIME = 30000L;
    private static final long serialVersionUID = -3400259782503254645L;
    private final boolean invokeAfterAuthentication;
    private final boolean invokeForBackgroundChecks;
    private final boolean invokeOnCheckout;
    private final boolean invokeOnCreate;
    private final boolean invokeOnException;
    private final boolean invokeOnRelease;
    private final long maxResponseTime;
    private final SearchRequest searchRequest;
    private final String entryDN;
    
    public GetEntryLDAPConnectionPoolHealthCheck(final String entryDN, final long maxResponseTime, final boolean invokeOnCreate, final boolean invokeOnCheckout, final boolean invokeOnRelease, final boolean invokeForBackgroundChecks, final boolean invokeOnException) {
        this(entryDN, maxResponseTime, invokeOnCreate, false, invokeOnCheckout, invokeOnRelease, invokeForBackgroundChecks, invokeOnException);
    }
    
    public GetEntryLDAPConnectionPoolHealthCheck(final String entryDN, final long maxResponseTime, final boolean invokeOnCreate, final boolean invokeAfterAuthentication, final boolean invokeOnCheckout, final boolean invokeOnRelease, final boolean invokeForBackgroundChecks, final boolean invokeOnException) {
        this.invokeOnCreate = invokeOnCreate;
        this.invokeAfterAuthentication = invokeAfterAuthentication;
        this.invokeOnCheckout = invokeOnCheckout;
        this.invokeOnRelease = invokeOnRelease;
        this.invokeForBackgroundChecks = invokeForBackgroundChecks;
        this.invokeOnException = invokeOnException;
        if (entryDN == null) {
            this.entryDN = "";
        }
        else {
            this.entryDN = entryDN;
        }
        if (maxResponseTime > 0L) {
            this.maxResponseTime = maxResponseTime;
        }
        else {
            this.maxResponseTime = 30000L;
        }
        (this.searchRequest = new SearchRequest(this.entryDN, SearchScope.BASE, Filter.createPresenceFilter("objectClass"), new String[] { "1.1" })).setResponseTimeoutMillis(this.maxResponseTime);
    }
    
    @Override
    public void ensureNewConnectionValid(final LDAPConnection connection) throws LDAPException {
        if (this.invokeOnCreate) {
            this.getEntry(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidAfterAuthentication(final LDAPConnection connection, final BindResult bindResult) throws LDAPException {
        if (this.invokeAfterAuthentication && bindResult.getResultCode() == ResultCode.SUCCESS) {
            this.getEntry(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidForCheckout(final LDAPConnection connection) throws LDAPException {
        if (this.invokeOnCheckout) {
            this.getEntry(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidForRelease(final LDAPConnection connection) throws LDAPException {
        if (this.invokeOnRelease) {
            this.getEntry(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidForContinuedUse(final LDAPConnection connection) throws LDAPException {
        if (this.invokeForBackgroundChecks) {
            this.getEntry(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidAfterException(final LDAPConnection connection, final LDAPException exception) throws LDAPException {
        if (this.invokeOnException && !ResultCode.isConnectionUsable(exception.getResultCode())) {
            this.getEntry(connection);
        }
    }
    
    public String getEntryDN() {
        return this.entryDN;
    }
    
    public long getMaxResponseTimeMillis() {
        return this.maxResponseTime;
    }
    
    public boolean invokeOnCreate() {
        return this.invokeOnCreate;
    }
    
    public boolean invokeAfterAuthentication() {
        return this.invokeAfterAuthentication;
    }
    
    public boolean invokeOnCheckout() {
        return this.invokeOnCheckout;
    }
    
    public boolean invokeOnRelease() {
        return this.invokeOnRelease;
    }
    
    public boolean invokeForBackgroundChecks() {
        return this.invokeForBackgroundChecks;
    }
    
    public boolean invokeOnException() {
        return this.invokeOnException;
    }
    
    private void getEntry(final LDAPConnection conn) throws LDAPException {
        try {
            final SearchResult result = conn.search(this.searchRequest.duplicate());
            if (result.getEntryCount() != 1) {
                throw new LDAPException(ResultCode.NO_RESULTS_RETURNED, LDAPMessages.ERR_GET_ENTRY_HEALTH_CHECK_NO_ENTRY_RETURNED.get());
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            final String msg = LDAPMessages.ERR_GET_ENTRY_HEALTH_CHECK_FAILURE.get(this.entryDN, StaticUtils.getExceptionMessage(e));
            conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, msg, e);
            throw new LDAPException(ResultCode.SERVER_DOWN, msg, e);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetEntryLDAPConnectionPoolHealthCheck(entryDN='");
        buffer.append(this.entryDN);
        buffer.append("', maxResponseTimeMillis=");
        buffer.append(this.maxResponseTime);
        buffer.append(", invokeOnCreate=");
        buffer.append(this.invokeOnCreate);
        buffer.append(", invokeAfterAuthentication=");
        buffer.append(this.invokeAfterAuthentication);
        buffer.append(", invokeOnCheckout=");
        buffer.append(this.invokeOnCheckout);
        buffer.append(", invokeOnRelease=");
        buffer.append(this.invokeOnRelease);
        buffer.append(", invokeForBackgroundChecks=");
        buffer.append(this.invokeForBackgroundChecks);
        buffer.append(", invokeOnException=");
        buffer.append(this.invokeOnException);
        buffer.append(')');
    }
}

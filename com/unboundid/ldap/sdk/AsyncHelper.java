package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
final class AsyncHelper implements CommonAsyncHelper, IntermediateResponseListener
{
    private static final long serialVersionUID = 7186731025240177443L;
    private final AsyncRequestID asyncRequestID;
    private final AsyncResultListener resultListener;
    private final AtomicBoolean responseReturned;
    private final OperationType operationType;
    private final IntermediateResponseListener intermediateResponseListener;
    private final LDAPConnection connection;
    private final long createTime;
    
    @InternalUseOnly
    AsyncHelper(final LDAPConnection connection, final OperationType operationType, final int messageID, final AsyncResultListener resultListener, final IntermediateResponseListener intermediateResponseListener) {
        this.connection = connection;
        this.operationType = operationType;
        this.resultListener = resultListener;
        this.intermediateResponseListener = intermediateResponseListener;
        this.asyncRequestID = new AsyncRequestID(messageID, connection);
        this.responseReturned = new AtomicBoolean(false);
        this.createTime = System.nanoTime();
    }
    
    @Override
    public AsyncRequestID getAsyncRequestID() {
        return this.asyncRequestID;
    }
    
    @Override
    public LDAPConnection getConnection() {
        return this.connection;
    }
    
    @Override
    public long getCreateTimeNanos() {
        return this.createTime;
    }
    
    @Override
    public OperationType getOperationType() {
        return this.operationType;
    }
    
    @InternalUseOnly
    @Override
    public void responseReceived(final LDAPResponse response) throws LDAPException {
        if (!this.responseReturned.compareAndSet(false, true)) {
            return;
        }
        final long responseTime = System.nanoTime() - this.createTime;
        LDAPResult result;
        if (response instanceof ConnectionClosedResponse) {
            final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
            final String msg = ccr.getMessage();
            if (msg == null) {
                result = new LDAPResult(this.asyncRequestID.getMessageID(), ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE.get(), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
            }
            else {
                result = new LDAPResult(this.asyncRequestID.getMessageID(), ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE_WITH_MESSAGE.get(msg), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
            }
        }
        else {
            result = (LDAPResult)response;
        }
        switch (this.operationType) {
            case ADD: {
                this.connection.getConnectionStatistics().incrementNumAddResponses(responseTime);
                break;
            }
            case DELETE: {
                this.connection.getConnectionStatistics().incrementNumDeleteResponses(responseTime);
                break;
            }
            case MODIFY: {
                this.connection.getConnectionStatistics().incrementNumModifyResponses(responseTime);
                break;
            }
            case MODIFY_DN: {
                this.connection.getConnectionStatistics().incrementNumModifyDNResponses(responseTime);
                break;
            }
        }
        this.resultListener.ldapResultReceived(this.asyncRequestID, result);
        this.asyncRequestID.setResult(result);
    }
    
    @InternalUseOnly
    @Override
    public void intermediateResponseReturned(final IntermediateResponse intermediateResponse) {
        if (this.intermediateResponseListener == null) {
            Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.WARN_INTERMEDIATE_RESPONSE_WITH_NO_LISTENER.get(String.valueOf(intermediateResponse)));
        }
        else {
            this.intermediateResponseListener.intermediateResponseReturned(intermediateResponse);
        }
    }
}

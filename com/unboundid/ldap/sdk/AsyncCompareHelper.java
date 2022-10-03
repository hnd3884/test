package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
final class AsyncCompareHelper implements CommonAsyncHelper, IntermediateResponseListener
{
    private static final long serialVersionUID = 8888333889563000881L;
    private final AsyncCompareResultListener resultListener;
    private final AsyncRequestID asyncRequestID;
    private final AtomicBoolean responseReturned;
    private final IntermediateResponseListener intermediateResponseListener;
    private final LDAPConnection connection;
    private final long createTime;
    
    @InternalUseOnly
    AsyncCompareHelper(final LDAPConnection connection, final int messageID, final AsyncCompareResultListener resultListener, final IntermediateResponseListener intermediateResponseListener) {
        this.connection = connection;
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
        return OperationType.COMPARE;
    }
    
    @InternalUseOnly
    @Override
    public void responseReceived(final LDAPResponse response) throws LDAPException {
        if (!this.responseReturned.compareAndSet(false, true)) {
            return;
        }
        final long responseTime = System.nanoTime() - this.createTime;
        CompareResult result;
        if (response instanceof ConnectionClosedResponse) {
            final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
            final String msg = ccr.getMessage();
            if (msg == null) {
                result = new CompareResult(this.asyncRequestID.getMessageID(), ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE.get(), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
            }
            else {
                result = new CompareResult(this.asyncRequestID.getMessageID(), ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE_WITH_MESSAGE.get(msg), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
            }
        }
        else {
            result = (CompareResult)response;
        }
        this.connection.getConnectionStatistics().incrementNumCompareResponses(responseTime);
        this.resultListener.compareResultReceived(this.asyncRequestID, result);
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

package com.unboundid.ldap.sdk;

import com.unboundid.ldap.protocol.LDAPResponse;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.TimerTask;

final class AsyncTimeoutTimerTask extends TimerTask
{
    private final CommonAsyncHelper helper;
    
    AsyncTimeoutTimerTask(final CommonAsyncHelper helper) {
        this.helper = helper;
    }
    
    @Override
    public void run() {
        final long waitTimeNanos = System.nanoTime() - this.helper.getCreateTimeNanos();
        final long waitTimeMillis = waitTimeNanos / 1000000L;
        final LDAPConnection conn = this.helper.getConnection();
        final boolean abandon = conn.getConnectionOptions().abandonOnTimeout();
        String message;
        if (abandon) {
            message = LDAPMessages.INFO_ASYNC_OPERATION_TIMEOUT_WITH_ABANDON.get(waitTimeMillis);
        }
        else {
            message = LDAPMessages.INFO_ASYNC_OPERATION_TIMEOUT_WITHOUT_ABANDON.get(waitTimeMillis);
        }
        final int messageID = this.helper.getAsyncRequestID().getMessageID();
        LDAPResponse response = null;
        switch (this.helper.getOperationType()) {
            case ADD:
            case DELETE:
            case MODIFY:
            case MODIFY_DN: {
                response = new LDAPResult(messageID, ResultCode.TIMEOUT, message, null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
                break;
            }
            case COMPARE: {
                response = new CompareResult(messageID, ResultCode.TIMEOUT, message, null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
                break;
            }
            case SEARCH: {
                final AsyncSearchHelper searchHelper = (AsyncSearchHelper)this.helper;
                response = new SearchResult(messageID, ResultCode.TIMEOUT, message, null, StaticUtils.NO_STRINGS, searchHelper.getNumEntries(), searchHelper.getNumReferences(), StaticUtils.NO_CONTROLS);
                break;
            }
            default: {
                return;
            }
        }
        try {
            try {
                final LDAPConnectionReader connectionReader = conn.getConnectionInternals(true).getConnectionReader();
                connectionReader.deregisterResponseAcceptor(messageID);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            this.helper.responseReceived(response);
            if (abandon) {
                conn.abandon(this.helper.getAsyncRequestID());
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
    }
}

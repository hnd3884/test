package com.unboundid.ldap.listener;

import com.unboundid.util.StaticUtils;
import java.util.concurrent.TimeUnit;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;
import java.util.concurrent.Semaphore;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ConcurrentRequestLimiterRequestHandler extends LDAPListenerRequestHandler
{
    private final LDAPListenerRequestHandler downstreamRequestHandler;
    private final long rejectTimeoutMillis;
    private final Semaphore abandonSemaphore;
    private final Semaphore addSemaphore;
    private final Semaphore bindSemaphore;
    private final Semaphore compareSemaphore;
    private final Semaphore deleteSemaphore;
    private final Semaphore extendedSemaphore;
    private final Semaphore modifySemaphore;
    private final Semaphore modifyDNSemaphore;
    private final Semaphore searchSemaphore;
    
    public ConcurrentRequestLimiterRequestHandler(final LDAPListenerRequestHandler downstreamRequestHandler, final int maxConcurrentRequests, final long rejectTimeoutMillis) {
        this(downstreamRequestHandler, new Semaphore(maxConcurrentRequests), rejectTimeoutMillis);
    }
    
    public ConcurrentRequestLimiterRequestHandler(final LDAPListenerRequestHandler downstreamRequestHandler, final Semaphore semaphore, final long rejectTimeoutMillis) {
        this(downstreamRequestHandler, null, semaphore, semaphore, semaphore, semaphore, semaphore, semaphore, semaphore, semaphore, rejectTimeoutMillis);
    }
    
    public ConcurrentRequestLimiterRequestHandler(final LDAPListenerRequestHandler downstreamRequestHandler, final Semaphore abandonSemaphore, final Semaphore addSemaphore, final Semaphore bindSemaphore, final Semaphore compareSemaphore, final Semaphore deleteSemaphore, final Semaphore extendedSemaphore, final Semaphore modifySemaphore, final Semaphore modifyDNSemaphore, final Semaphore searchSemaphore, final long rejectTimeoutMillis) {
        Validator.ensureNotNull(downstreamRequestHandler);
        this.downstreamRequestHandler = downstreamRequestHandler;
        this.abandonSemaphore = abandonSemaphore;
        this.addSemaphore = addSemaphore;
        this.bindSemaphore = bindSemaphore;
        this.compareSemaphore = compareSemaphore;
        this.deleteSemaphore = deleteSemaphore;
        this.extendedSemaphore = extendedSemaphore;
        this.modifySemaphore = modifySemaphore;
        this.modifyDNSemaphore = modifyDNSemaphore;
        this.searchSemaphore = searchSemaphore;
        if (rejectTimeoutMillis >= 0L) {
            this.rejectTimeoutMillis = rejectTimeoutMillis;
        }
        else {
            this.rejectTimeoutMillis = 2147483647L;
        }
    }
    
    @Override
    public ConcurrentRequestLimiterRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        return new ConcurrentRequestLimiterRequestHandler(this.downstreamRequestHandler.newInstance(connection), this.abandonSemaphore, this.addSemaphore, this.bindSemaphore, this.compareSemaphore, this.deleteSemaphore, this.extendedSemaphore, this.modifySemaphore, this.modifyDNSemaphore, this.searchSemaphore, this.rejectTimeoutMillis);
    }
    
    @Override
    public void processAbandonRequest(final int messageID, final AbandonRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.abandonSemaphore, OperationType.ABANDON);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return;
        }
        try {
            this.downstreamRequestHandler.processAbandonRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.abandonSemaphore);
        }
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.addSemaphore, OperationType.ADD);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new AddResponseProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processAddRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.addSemaphore);
        }
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.bindSemaphore, OperationType.BIND);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new BindResponseProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processBindRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.bindSemaphore);
        }
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.compareSemaphore, OperationType.COMPARE);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new CompareResponseProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processCompareRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.compareSemaphore);
        }
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.deleteSemaphore, OperationType.DELETE);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new DeleteResponseProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processDeleteRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.deleteSemaphore);
        }
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.extendedSemaphore, OperationType.EXTENDED);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processExtendedRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.extendedSemaphore);
        }
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.modifySemaphore, OperationType.MODIFY);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processModifyRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.modifySemaphore);
        }
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.modifyDNSemaphore, OperationType.MODIFY_DN);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processModifyDNRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.modifyDNSemaphore);
        }
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        try {
            this.acquirePermit(this.searchSemaphore, OperationType.SEARCH);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(le.toLDAPResult()), new Control[0]);
        }
        try {
            return this.downstreamRequestHandler.processSearchRequest(messageID, request, controls);
        }
        finally {
            releasePermit(this.searchSemaphore);
        }
    }
    
    private void acquirePermit(final Semaphore semaphore, final OperationType operationType) throws LDAPException {
        if (semaphore == null) {
            return;
        }
        try {
            if (this.rejectTimeoutMillis == 0L) {
                if (!semaphore.tryAcquire()) {
                    throw new LDAPException(ResultCode.BUSY, ListenerMessages.ERR_CONCURRENT_LIMITER_REQUEST_HANDLER_NO_TIMEOUT.get(operationType.name()));
                }
            }
            else if (!semaphore.tryAcquire(this.rejectTimeoutMillis, TimeUnit.MILLISECONDS)) {
                throw new LDAPException(ResultCode.BUSY, ListenerMessages.ERR_CONCURRENT_LIMITER_REQUEST_HANDLER_TIMEOUT.get(operationType.name(), this.rejectTimeoutMillis));
            }
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.OTHER, ListenerMessages.ERR_CONCURRENT_LIMITER_REQUEST_HANDLER_SEMAPHORE_EXCEPTION.get(operationType.name(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static void releasePermit(final Semaphore semaphore) {
        if (semaphore != null) {
            semaphore.release();
        }
    }
}

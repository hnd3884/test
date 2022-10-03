package com.unboundid.ldap.listener;

import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RateLimiterRequestHandler extends LDAPListenerRequestHandler
{
    private final FixedRateBarrier abandonRateLimiter;
    private final FixedRateBarrier addRateLimiter;
    private final FixedRateBarrier bindRateLimiter;
    private final FixedRateBarrier compareRateLimiter;
    private final FixedRateBarrier deleteRateLimiter;
    private final FixedRateBarrier extendedRateLimiter;
    private final FixedRateBarrier modifyRateLimiter;
    private final FixedRateBarrier modifyDNRateLimiter;
    private final FixedRateBarrier searchRateLimiter;
    private final LDAPListenerRequestHandler downstreamRequestHandler;
    
    public RateLimiterRequestHandler(final LDAPListenerRequestHandler downstreamRequestHandler, final int maxPerSecond) {
        Validator.ensureNotNull(downstreamRequestHandler);
        Validator.ensureTrue(maxPerSecond > 0);
        this.downstreamRequestHandler = downstreamRequestHandler;
        final FixedRateBarrier rateLimiter = new FixedRateBarrier(1000L, maxPerSecond);
        this.abandonRateLimiter = null;
        this.addRateLimiter = rateLimiter;
        this.bindRateLimiter = rateLimiter;
        this.compareRateLimiter = rateLimiter;
        this.deleteRateLimiter = rateLimiter;
        this.extendedRateLimiter = rateLimiter;
        this.modifyRateLimiter = rateLimiter;
        this.modifyDNRateLimiter = rateLimiter;
        this.searchRateLimiter = rateLimiter;
    }
    
    public RateLimiterRequestHandler(final LDAPListenerRequestHandler downstreamRequestHandler, final FixedRateBarrier rateLimiter) {
        this(downstreamRequestHandler, null, rateLimiter, rateLimiter, rateLimiter, rateLimiter, rateLimiter, rateLimiter, rateLimiter, rateLimiter);
    }
    
    public RateLimiterRequestHandler(final LDAPListenerRequestHandler downstreamRequestHandler, final FixedRateBarrier abandonRateLimiter, final FixedRateBarrier addRateLimiter, final FixedRateBarrier bindRateLimiter, final FixedRateBarrier compareRateLimiter, final FixedRateBarrier deleteRateLimiter, final FixedRateBarrier extendedRateLimiter, final FixedRateBarrier modifyRateLimiter, final FixedRateBarrier modifyDNRateLimiter, final FixedRateBarrier searchRateLimiter) {
        Validator.ensureNotNull(downstreamRequestHandler);
        this.downstreamRequestHandler = downstreamRequestHandler;
        this.abandonRateLimiter = abandonRateLimiter;
        this.addRateLimiter = addRateLimiter;
        this.bindRateLimiter = bindRateLimiter;
        this.compareRateLimiter = compareRateLimiter;
        this.deleteRateLimiter = deleteRateLimiter;
        this.extendedRateLimiter = extendedRateLimiter;
        this.modifyRateLimiter = modifyRateLimiter;
        this.modifyDNRateLimiter = modifyDNRateLimiter;
        this.searchRateLimiter = searchRateLimiter;
    }
    
    @Override
    public RateLimiterRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        return new RateLimiterRequestHandler(this.downstreamRequestHandler.newInstance(connection), this.abandonRateLimiter, this.addRateLimiter, this.bindRateLimiter, this.compareRateLimiter, this.deleteRateLimiter, this.extendedRateLimiter, this.modifyRateLimiter, this.modifyDNRateLimiter, this.searchRateLimiter);
    }
    
    @Override
    public void processAbandonRequest(final int messageID, final AbandonRequestProtocolOp request, final List<Control> controls) {
        if (this.abandonRateLimiter != null) {
            this.abandonRateLimiter.await();
        }
        this.downstreamRequestHandler.processAbandonRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        if (this.addRateLimiter != null) {
            this.addRateLimiter.await();
        }
        return this.downstreamRequestHandler.processAddRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        if (this.bindRateLimiter != null) {
            this.bindRateLimiter.await();
        }
        return this.downstreamRequestHandler.processBindRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        if (this.compareRateLimiter != null) {
            this.compareRateLimiter.await();
        }
        return this.downstreamRequestHandler.processCompareRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        if (this.deleteRateLimiter != null) {
            this.deleteRateLimiter.await();
        }
        return this.downstreamRequestHandler.processDeleteRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        if (this.extendedRateLimiter != null) {
            this.extendedRateLimiter.await();
        }
        return this.downstreamRequestHandler.processExtendedRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        if (this.modifyRateLimiter != null) {
            this.modifyRateLimiter.await();
        }
        return this.downstreamRequestHandler.processModifyRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        if (this.modifyDNRateLimiter != null) {
            this.modifyDNRateLimiter.await();
        }
        return this.downstreamRequestHandler.processModifyDNRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        if (this.searchRateLimiter != null) {
            this.searchRateLimiter.await();
        }
        return this.downstreamRequestHandler.processSearchRequest(messageID, request, controls);
    }
}

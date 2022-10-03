package io.netty.resolver.dns;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.channel.ChannelPromise;
import io.netty.channel.Channel;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.AbstractDnsOptPseudoRrRecord;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.Future;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetSocketAddress;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.concurrent.FutureListener;

abstract class DnsQueryContext implements FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>
{
    private static final InternalLogger logger;
    private final DnsNameResolver parent;
    private final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise;
    private final int id;
    private final DnsQuestion question;
    private final DnsRecord[] additionals;
    private final DnsRecord optResource;
    private final InetSocketAddress nameServerAddr;
    private final boolean recursionDesired;
    private volatile Future<?> timeoutFuture;
    
    DnsQueryContext(final DnsNameResolver parent, final InetSocketAddress nameServerAddr, final DnsQuestion question, final DnsRecord[] additionals, final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise) {
        this.parent = ObjectUtil.checkNotNull(parent, "parent");
        this.nameServerAddr = ObjectUtil.checkNotNull(nameServerAddr, "nameServerAddr");
        this.question = ObjectUtil.checkNotNull(question, "question");
        this.additionals = ObjectUtil.checkNotNull(additionals, "additionals");
        this.promise = ObjectUtil.checkNotNull(promise, "promise");
        this.recursionDesired = parent.isRecursionDesired();
        this.id = parent.queryContextManager.add(this);
        promise.addListener((GenericFutureListener<? extends Future<? super AddressedEnvelope<DnsResponse, InetSocketAddress>>>)this);
        if (parent.isOptResourceEnabled()) {
            this.optResource = new AbstractDnsOptPseudoRrRecord(parent.maxPayloadSize(), 0, 0) {};
        }
        else {
            this.optResource = null;
        }
    }
    
    InetSocketAddress nameServerAddr() {
        return this.nameServerAddr;
    }
    
    DnsQuestion question() {
        return this.question;
    }
    
    DnsNameResolver parent() {
        return this.parent;
    }
    
    protected abstract DnsQuery newQuery(final int p0);
    
    protected abstract Channel channel();
    
    protected abstract String protocol();
    
    void query(final boolean flush, final ChannelPromise writePromise) {
        final DnsQuestion question = this.question();
        final InetSocketAddress nameServerAddr = this.nameServerAddr();
        final DnsQuery query = this.newQuery(this.id);
        query.setRecursionDesired(this.recursionDesired);
        query.addRecord(DnsSection.QUESTION, (DnsRecord)question);
        for (final DnsRecord record : this.additionals) {
            query.addRecord(DnsSection.ADDITIONAL, record);
        }
        if (this.optResource != null) {
            query.addRecord(DnsSection.ADDITIONAL, this.optResource);
        }
        if (DnsQueryContext.logger.isDebugEnabled()) {
            DnsQueryContext.logger.debug("{} WRITE: {}, [{}: {}], {}", this.channel(), this.protocol(), this.id, nameServerAddr, question);
        }
        this.sendQuery(query, flush, writePromise);
    }
    
    private void sendQuery(final DnsQuery query, final boolean flush, final ChannelPromise writePromise) {
        if (this.parent.channelFuture.isDone()) {
            this.writeQuery(query, flush, writePromise);
        }
        else {
            this.parent.channelFuture.addListener(new GenericFutureListener<Future<? super Channel>>() {
                @Override
                public void operationComplete(final Future<? super Channel> future) {
                    if (future.isSuccess()) {
                        DnsQueryContext.this.writeQuery(query, true, writePromise);
                    }
                    else {
                        final Throwable cause = future.cause();
                        DnsQueryContext.this.promise.tryFailure(cause);
                        writePromise.setFailure(cause);
                    }
                }
            });
        }
    }
    
    private void writeQuery(final DnsQuery query, final boolean flush, final ChannelPromise writePromise) {
        final ChannelFuture writeFuture = flush ? this.channel().writeAndFlush(query, writePromise) : this.channel().write(query, writePromise);
        if (writeFuture.isDone()) {
            this.onQueryWriteCompletion(writeFuture);
        }
        else {
            writeFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) {
                    DnsQueryContext.this.onQueryWriteCompletion(writeFuture);
                }
            });
        }
    }
    
    private void onQueryWriteCompletion(final ChannelFuture writeFuture) {
        if (!writeFuture.isSuccess()) {
            this.tryFailure("failed to send a query via " + this.protocol(), writeFuture.cause(), false);
            return;
        }
        final long queryTimeoutMillis = this.parent.queryTimeoutMillis();
        if (queryTimeoutMillis > 0L) {
            this.timeoutFuture = this.parent.ch.eventLoop().schedule((Runnable)new Runnable() {
                @Override
                public void run() {
                    if (DnsQueryContext.this.promise.isDone()) {
                        return;
                    }
                    DnsQueryContext.this.tryFailure("query via " + DnsQueryContext.this.protocol() + " timed out after " + queryTimeoutMillis + " milliseconds", null, true);
                }
            }, queryTimeoutMillis, TimeUnit.MILLISECONDS);
        }
    }
    
    void finish(final AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope) {
        final DnsResponse res = (DnsResponse)envelope.content();
        if (res.count(DnsSection.QUESTION) != 1) {
            DnsQueryContext.logger.warn("Received a DNS response with invalid number of questions: {}", envelope);
        }
        else if (!this.question().equals(res.recordAt(DnsSection.QUESTION))) {
            DnsQueryContext.logger.warn("Received a mismatching DNS response: {}", envelope);
        }
        else if (this.trySuccess(envelope)) {
            return;
        }
        envelope.release();
    }
    
    private boolean trySuccess(final AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope) {
        return this.promise.trySuccess((AddressedEnvelope<DnsResponse, InetSocketAddress>)envelope);
    }
    
    boolean tryFailure(final String message, final Throwable cause, final boolean timeout) {
        if (this.promise.isDone()) {
            return false;
        }
        final InetSocketAddress nameServerAddr = this.nameServerAddr();
        final StringBuilder buf = new StringBuilder(message.length() + 64);
        buf.append('[').append(nameServerAddr).append("] ").append(message).append(" (no stack trace available)");
        DnsNameResolverException e;
        if (timeout) {
            e = new DnsNameResolverTimeoutException(nameServerAddr, this.question(), buf.toString());
        }
        else {
            e = new DnsNameResolverException(nameServerAddr, this.question(), buf.toString(), cause);
        }
        return this.promise.tryFailure(e);
    }
    
    @Override
    public void operationComplete(final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
        final Future<?> timeoutFuture = this.timeoutFuture;
        if (timeoutFuture != null) {
            this.timeoutFuture = null;
            timeoutFuture.cancel(false);
        }
        this.parent.queryContextManager.remove(this.nameServerAddr, this.id);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DnsQueryContext.class);
    }
}

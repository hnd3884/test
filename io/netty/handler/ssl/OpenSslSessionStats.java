package io.netty.handler.ssl;

import java.util.concurrent.locks.Lock;
import io.netty.internal.tcnative.SSLContext;

public final class OpenSslSessionStats
{
    private final ReferenceCountedOpenSslContext context;
    
    OpenSslSessionStats(final ReferenceCountedOpenSslContext context) {
        this.context = context;
    }
    
    public long number() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionNumber(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long connect() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionConnect(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long connectGood() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionConnectGood(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long connectRenegotiate() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionConnectRenegotiate(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long accept() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionAccept(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long acceptGood() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionAcceptGood(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long acceptRenegotiate() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionAcceptRenegotiate(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long hits() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionHits(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long cbHits() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionCbHits(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long misses() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionMisses(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long timeouts() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionTimeouts(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long cacheFull() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionCacheFull(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long ticketKeyFail() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionTicketKeyFail(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long ticketKeyNew() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionTicketKeyNew(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long ticketKeyRenew() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionTicketKeyRenew(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public long ticketKeyResume() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.sessionTicketKeyResume(this.context.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
}

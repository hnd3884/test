package com.sun.jndi.ldap;

import java.util.concurrent.TimeUnit;
import javax.naming.NamingException;
import javax.naming.CommunicationException;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

final class LdapRequest
{
    private static final BerDecoder EOF;
    private static final String CLOSE_MSG = "LDAP connection has been closed";
    private static final String TIMEOUT_MSG_FMT = "LDAP response read timed out, timeout used: %d ms.";
    LdapRequest next;
    final int msgId;
    private final BlockingQueue<BerDecoder> replies;
    private volatile boolean cancelled;
    private volatile boolean closed;
    private volatile boolean completed;
    private final boolean pauseAfterReceipt;
    
    LdapRequest(final int msgId, final boolean pauseAfterReceipt, final int n) {
        this.msgId = msgId;
        this.pauseAfterReceipt = pauseAfterReceipt;
        if (n == -1) {
            this.replies = new LinkedBlockingQueue<BerDecoder>();
        }
        else {
            this.replies = new LinkedBlockingQueue<BerDecoder>(8 * n / 10);
        }
    }
    
    void cancel() {
        this.cancelled = true;
        this.replies.offer(LdapRequest.EOF);
    }
    
    synchronized void close() {
        this.closed = true;
        this.replies.offer(LdapRequest.EOF);
    }
    
    private boolean isClosed() {
        return this.closed && (this.replies.size() == 0 || this.replies.peek() == LdapRequest.EOF);
    }
    
    synchronized boolean addReplyBer(final BerDecoder berDecoder) {
        if (this.cancelled || this.closed) {
            return false;
        }
        try {
            berDecoder.parseSeq(null);
            berDecoder.parseInt();
            this.completed = (berDecoder.peekByte() == 101);
        }
        catch (final IOException ex) {}
        berDecoder.reset();
        try {
            this.replies.put(berDecoder);
        }
        catch (final InterruptedException ex2) {}
        return this.pauseAfterReceipt;
    }
    
    BerDecoder getReplyBer(final long n) throws NamingException, InterruptedException {
        if (this.cancelled) {
            throw new CommunicationException("Request: " + this.msgId + " cancelled");
        }
        if (this.isClosed()) {
            throw new NamingException("LDAP connection has been closed");
        }
        final BerDecoder berDecoder = (n > 0L) ? this.replies.poll(n, TimeUnit.MILLISECONDS) : this.replies.take();
        if (this.cancelled) {
            throw new CommunicationException("Request: " + this.msgId + " cancelled");
        }
        if (berDecoder == null) {
            throw new NamingException(String.format("LDAP response read timed out, timeout used: %d ms.", n));
        }
        if (berDecoder == LdapRequest.EOF) {
            throw new NamingException("LDAP connection has been closed");
        }
        return berDecoder;
    }
    
    boolean hasSearchCompleted() {
        return this.completed;
    }
    
    static {
        EOF = new BerDecoder(new byte[0], -1, 0);
    }
}

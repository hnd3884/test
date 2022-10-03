package com.maverick.ssh.message;

import java.io.EOFException;
import com.maverick.ssh.SshException;

public class SshMessageStore implements MessageStore
{
    public static final int NO_MESSAGES = -1;
    SshAbstractChannel g;
    SshMessageRouter f;
    boolean c;
    SshMessage h;
    int e;
    MessageObserver d;
    boolean b;
    
    public SshMessageStore(final SshMessageRouter f, final SshAbstractChannel g, final MessageObserver d) {
        this.c = false;
        this.h = new SshMessage();
        this.e = 0;
        this.b = Boolean.valueOf(System.getProperty("maverick.verbose", "false"));
        this.f = f;
        this.g = g;
        this.d = d;
        final SshMessage h = this.h;
        final SshMessage h2 = this.h;
        final SshMessage h3 = this.h;
        h2.e = h3;
        h.d = h3;
    }
    
    public SshMessage nextMessage(final MessageObserver messageObserver, final long n) throws SshException, EOFException {
        try {
            final SshMessage nextMessage = this.f.nextMessage(this.g, messageObserver, n);
            if (nextMessage != null) {
                synchronized (this.h) {
                    if (this.d.wantsNotification(nextMessage)) {
                        return nextMessage;
                    }
                    this.b(nextMessage);
                    return nextMessage;
                }
            }
        }
        catch (final InterruptedException ex) {
            throw new SshException("The thread was interrupted", 5);
        }
        throw new EOFException("The required message could not be found in the message store");
    }
    
    public boolean isClosed() {
        synchronized (this.h) {
            return this.c;
        }
    }
    
    private void b(final SshMessage sshMessage) {
        if (sshMessage == this.h) {
            throw new IndexOutOfBoundsException();
        }
        sshMessage.e.d = sshMessage.d;
        sshMessage.d.e = sshMessage.e;
        --this.e;
    }
    
    public Message hasMessage(final MessageObserver messageObserver) {
        synchronized (this.h) {
            SshMessage sshMessage = this.h.d;
            if (sshMessage == null) {
                return null;
            }
            while (sshMessage != this.h) {
                if (messageObserver.wantsNotification(sshMessage)) {
                    return sshMessage;
                }
                sshMessage = sshMessage.d;
            }
            return null;
        }
    }
    
    public void close() {
        synchronized (this.h) {
            this.c = true;
        }
    }
    
    void c(final SshMessage sshMessage) {
        synchronized (this.h) {
            sshMessage.d = this.h;
            sshMessage.e = this.h.e;
            sshMessage.e.d = sshMessage;
            sshMessage.d.e = sshMessage;
            ++this.e;
        }
    }
}

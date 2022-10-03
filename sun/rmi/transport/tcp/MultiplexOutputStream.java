package sun.rmi.transport.tcp;

import java.io.IOException;
import java.io.OutputStream;

final class MultiplexOutputStream extends OutputStream
{
    private ConnectionMultiplexer manager;
    private MultiplexConnectionInfo info;
    private byte[] buffer;
    private int pos;
    private int requested;
    private boolean disconnected;
    private Object lock;
    
    MultiplexOutputStream(final ConnectionMultiplexer manager, final MultiplexConnectionInfo info, final int n) {
        this.pos = 0;
        this.requested = 0;
        this.disconnected = false;
        this.lock = new Object();
        this.manager = manager;
        this.info = info;
        this.buffer = new byte[n];
        this.pos = 0;
    }
    
    @Override
    public synchronized void write(final int n) throws IOException {
        while (this.pos >= this.buffer.length) {
            this.push();
        }
        this.buffer[this.pos++] = (byte)n;
    }
    
    @Override
    public synchronized void write(final byte[] array, int n, int n2) throws IOException {
        if (n2 <= 0) {
            return;
        }
        if (n2 <= this.buffer.length - this.pos) {
            System.arraycopy(array, n, this.buffer, this.pos, n2);
            this.pos += n2;
            return;
        }
        this.flush();
        while (true) {
            int requested;
            synchronized (this.lock) {
                while ((requested = this.requested) < 1 && !this.disconnected) {
                    try {
                        this.lock.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
                if (this.disconnected) {
                    throw new IOException("Connection closed");
                }
            }
            if (requested >= n2) {
                break;
            }
            this.manager.sendTransmit(this.info, array, n, requested);
            n += requested;
            n2 -= requested;
            synchronized (this.lock) {
                this.requested -= requested;
            }
        }
        this.manager.sendTransmit(this.info, array, n, n2);
        synchronized (this.lock) {
            this.requested -= n2;
        }
    }
    
    @Override
    public synchronized void flush() throws IOException {
        while (this.pos > 0) {
            this.push();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.manager.sendClose(this.info);
    }
    
    void request(final int n) {
        synchronized (this.lock) {
            this.requested += n;
            this.lock.notifyAll();
        }
    }
    
    void disconnect() {
        synchronized (this.lock) {
            this.disconnected = true;
            this.lock.notifyAll();
        }
    }
    
    private void push() throws IOException {
        int requested;
        synchronized (this.lock) {
            while ((requested = this.requested) < 1 && !this.disconnected) {
                try {
                    this.lock.wait();
                }
                catch (final InterruptedException ex) {}
            }
            if (this.disconnected) {
                throw new IOException("Connection closed");
            }
        }
        if (requested < this.pos) {
            this.manager.sendTransmit(this.info, this.buffer, 0, requested);
            System.arraycopy(this.buffer, requested, this.buffer, 0, this.pos - requested);
            this.pos -= requested;
            synchronized (this.lock) {
                this.requested -= requested;
            }
        }
        else {
            this.manager.sendTransmit(this.info, this.buffer, 0, this.pos);
            synchronized (this.lock) {
                this.requested -= this.pos;
            }
            this.pos = 0;
        }
    }
}

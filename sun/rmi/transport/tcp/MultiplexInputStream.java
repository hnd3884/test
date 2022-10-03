package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MultiplexInputStream extends InputStream
{
    private ConnectionMultiplexer manager;
    private MultiplexConnectionInfo info;
    private byte[] buffer;
    private int present;
    private int pos;
    private int requested;
    private boolean disconnected;
    private Object lock;
    private int waterMark;
    private byte[] temp;
    
    MultiplexInputStream(final ConnectionMultiplexer manager, final MultiplexConnectionInfo info, final int n) {
        this.present = 0;
        this.pos = 0;
        this.requested = 0;
        this.disconnected = false;
        this.lock = new Object();
        this.temp = new byte[1];
        this.manager = manager;
        this.info = info;
        this.buffer = new byte[n];
        this.waterMark = n / 2;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.read(this.temp, 0, 1) != 1) {
            return -1;
        }
        return this.temp[0] & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 <= 0) {
            return 0;
        }
        final int max;
        synchronized (this.lock) {
            if (this.pos >= this.present) {
                final int n3 = 0;
                this.present = n3;
                this.pos = n3;
            }
            else if (this.pos >= this.waterMark) {
                System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.present - this.pos);
                this.present -= this.pos;
                this.pos = 0;
            }
            max = Math.max(this.buffer.length - this.present - this.requested, 0);
        }
        if (max > 0) {
            this.manager.sendRequest(this.info, max);
        }
        synchronized (this.lock) {
            this.requested += max;
            while (this.pos >= this.present && !this.disconnected) {
                try {
                    this.lock.wait();
                }
                catch (final InterruptedException ex) {}
            }
            if (this.disconnected && this.pos >= this.present) {
                return -1;
            }
            final int n4 = this.present - this.pos;
            if (n2 < n4) {
                System.arraycopy(this.buffer, this.pos, array, n, n2);
                this.pos += n2;
                return n2;
            }
            System.arraycopy(this.buffer, this.pos, array, n, n4);
            final int n5 = 0;
            this.present = n5;
            this.pos = n5;
            return n4;
        }
    }
    
    @Override
    public int available() throws IOException {
        synchronized (this.lock) {
            return this.present - this.pos;
        }
    }
    
    @Override
    public void close() throws IOException {
        this.manager.sendClose(this.info);
    }
    
    void receive(final int n, final DataInputStream dataInputStream) throws IOException {
        synchronized (this.lock) {
            if (this.pos > 0 && this.buffer.length - this.present < n) {
                System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.present - this.pos);
                this.present -= this.pos;
                this.pos = 0;
            }
            if (this.buffer.length - this.present < n) {
                throw new IOException("Receive buffer overflow");
            }
            dataInputStream.readFully(this.buffer, this.present, n);
            this.present += n;
            this.requested -= n;
            this.lock.notifyAll();
        }
    }
    
    void disconnect() {
        synchronized (this.lock) {
            this.disconnected = true;
            this.lock.notifyAll();
        }
    }
}

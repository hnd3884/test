package com.maverick.ssh1;

import com.maverick.ssh.message.SshMessage;
import java.io.EOFException;
import com.maverick.ssh.SshIOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.message.SshChannelMessage;
import com.maverick.ssh.SshException;
import com.maverick.ssh.message.SshMessageStore;
import com.maverick.ssh.SshChannel;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh.message.SshMessageRouter;
import com.maverick.ssh.message.Message;
import com.maverick.ssh.message.MessageObserver;
import java.util.Vector;
import com.maverick.ssh.message.SshAbstractChannel;

class e extends SshAbstractChannel
{
    b vd;
    int zd;
    _c xd;
    _b sd;
    boolean ud;
    boolean td;
    boolean rd;
    Vector wd;
    MessageObserver yd;
    
    e() {
        this.ud = false;
        this.td = false;
        this.rd = false;
        this.wd = new Vector();
        this.yd = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 24:
                    case 25: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
    }
    
    void b(final b vd, final int n) {
        super.init(this.vd = vd, n);
    }
    
    void b(final int zd) {
        this.zd = zd;
        this.xd = new _c(23);
        this.sd = new _b();
        synchronized (this.wd) {
            for (int i = 0; i < this.wd.size(); ++i) {
                ((ChannelEventListener)this.wd.elementAt(i)).channelOpened(this);
            }
        }
    }
    
    public void setAutoConsumeInput(final boolean rd) {
        this.rd = rd;
    }
    
    protected SshMessageStore getMessageStore() throws SshException {
        return super.getMessageStore();
    }
    
    protected boolean processChannelMessage(final SshChannelMessage sshChannelMessage) throws SshException {
        switch (sshChannelMessage.getMessageId()) {
            case 23: {
                if (this.wd != null) {
                    for (int i = 0; i < this.wd.size(); ++i) {
                        ((ChannelEventListener)this.wd.elementAt(i)).dataReceived(this, sshChannelMessage.array(), sshChannelMessage.getPosition() + 4, sshChannelMessage.available() - 4);
                    }
                }
                return this.rd;
            }
            case 24: {
                if (!this.td) {
                    try {
                        final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                        byteArrayWriter.write(25);
                        byteArrayWriter.writeInt(this.zd);
                        this.vd.b(byteArrayWriter.toByteArray());
                        final boolean b = true;
                        this.ud = b;
                        this.td = b;
                        synchronized (this.wd) {
                            for (int j = 0; j < this.wd.size(); ++j) {
                                ((ChannelEventListener)this.wd.elementAt(j)).channelClosed(this);
                            }
                        }
                    }
                    catch (final IOException ex) {
                        throw new SshException(5, ex);
                    }
                }
                return false;
            }
            case 25: {
                this.ud = true;
                synchronized (this.wd) {
                    for (int k = 0; k < this.wd.size(); ++k) {
                        ((ChannelEventListener)this.wd.elementAt(k)).channelClosed(this);
                    }
                }
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    public void addChannelEventListener(final ChannelEventListener channelEventListener) {
        synchronized (this.wd) {
            if (channelEventListener != null) {
                this.wd.addElement(channelEventListener);
            }
        }
    }
    
    public InputStream getInputStream() {
        return this.xd;
    }
    
    public OutputStream getOutputStream() {
        return this.sd;
    }
    
    public void close() {
        try {
            this.sd.close();
        }
        catch (final IOException ex) {
            this.vd.close();
        }
    }
    
    protected MessageObserver getStickyMessageIds() {
        return this.yd;
    }
    
    class _b extends OutputStream
    {
        public void write(final int n) throws IOException {
            this.write(new byte[] { (byte)n }, 0, 1);
        }
        
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            if (e.this.vd.isClosed()) {
                throw new SshIOException(new SshException("The session is closed", 6));
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(n2 + 9);
            byteArrayWriter.write(23);
            byteArrayWriter.writeInt(e.this.zd);
            byteArrayWriter.writeBinaryString(array, n, n2);
            try {
                e.this.vd.b(byteArrayWriter.toByteArray());
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
            if (e.this.wd != null) {
                for (int i = 0; i < e.this.wd.size(); ++i) {
                    ((ChannelEventListener)e.this.wd.elementAt(i)).dataSent(e.this, array, n, n2);
                }
            }
        }
        
        public void close() throws IOException {
            if (!e.this.ud) {
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                byteArrayWriter.write(24);
                byteArrayWriter.writeInt(e.this.zd);
                try {
                    e.this.vd.b(byteArrayWriter.toByteArray());
                }
                catch (final SshException ex) {
                    throw new EOFException();
                }
                e.this.td = true;
            }
        }
    }
    
    class _c extends InputStream
    {
        int b;
        SshMessage d;
        MessageObserver c;
        
        _c(final int b) {
            this.b = b;
            this.c = new MessageObserver() {
                public boolean wantsNotification(final Message message) {
                    switch (message.getMessageId()) {
                        case 24:
                        case 25: {
                            return true;
                        }
                        default: {
                            return _c.this.b == message.getMessageId();
                        }
                    }
                }
            };
        }
        
        public int available() throws IOException {
            try {
                if (this.d == null || this.d.available() == 0) {
                    if (e.this.getMessageStore().hasMessage(this.c) == null) {
                        return 0;
                    }
                    this.b();
                }
                return (this.d.available() > 0) ? this.d.available() : (e.this.ud ? -1 : 0);
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
        }
        
        public int read() throws IOException {
            try {
                if (e.this.ud && this.available() <= 0) {
                    return -1;
                }
                this.b();
                return this.d.read();
            }
            catch (final EOFException ex) {
                return -1;
            }
        }
        
        void b() throws IOException {
            try {
                if (this.d == null || this.d.available() == 0) {
                    final SshMessage nextMessage = e.this.getMessageStore().nextMessage(this.c, 0L);
                    switch (nextMessage.getMessageId()) {
                        case 23: {
                            nextMessage.skip(4L);
                            this.d = nextMessage;
                            break;
                        }
                        case 24: {
                            throw new EOFException("The channel has been closed");
                        }
                        case 25: {
                            throw new EOFException("The channel has been closed");
                        }
                    }
                }
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
        }
        
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            try {
                if (e.this.ud && this.available() <= 0) {
                    return -1;
                }
                this.b();
                return this.d.read(array, n, n2);
            }
            catch (final EOFException ex) {
                return -1;
            }
        }
    }
}

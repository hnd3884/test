package com.maverick.ssh1;

import java.io.EOFException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.message.Message;
import com.maverick.ssh.SshContext;
import com.maverick.ssh.SshTunnel;
import com.maverick.ssh.SshTransport;
import com.maverick.ssh.ForwardingRequestListener;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.PseudoTerminalModes;
import java.io.IOException;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.message.SshMessageStore;
import com.maverick.ssh.SshException;
import com.maverick.ssh.message.SshChannelMessage;
import com.maverick.ssh.message.SshMessage;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.message.SshAbstractChannel;
import com.maverick.ssh.SshChannel;
import com.maverick.ssh.message.SshMessageReader;
import com.maverick.ssh.ChannelEventListener;
import java.util.Hashtable;
import java.util.Vector;
import java.io.OutputStream;
import java.io.InputStream;
import com.maverick.ssh.message.MessageObserver;
import com.maverick.ssh.SshSession;
import com.maverick.ssh.message.SshMessageRouter;

class b extends SshMessageRouter implements SshSession
{
    static final MessageObserver ad;
    f fd;
    Ssh1Client bd;
    InputStream id;
    InputStream dd;
    OutputStream ed;
    boolean kd;
    int cd;
    boolean gd;
    boolean yc;
    Vector hd;
    boolean zc;
    Hashtable ld;
    _b jd;
    
    b(final f fd, final Ssh1Client bd, final ChannelEventListener channelEventListener, final boolean b) {
        super(fd, fd.b.getChannelLimit(), b);
        this.id = new _d(17);
        this.dd = new _d(18);
        this.ed = new _c();
        this.kd = false;
        this.cd = Integer.MIN_VALUE;
        this.gd = false;
        this.yc = false;
        this.hd = new Vector();
        this.zc = false;
        this.ld = new Hashtable();
        this.jd = new _b();
        this.fd = fd;
        this.bd = bd;
        if (channelEventListener != null) {
            this.addChannelEventListener(channelEventListener);
            synchronized (this.hd) {
                for (int i = 0; i < this.hd.size(); ++i) {
                    ((ChannelEventListener)this.hd.elementAt(i)).channelOpened(this);
                }
            }
        }
    }
    
    public void setAutoConsumeInput(final boolean zc) {
        this.zc = zc;
    }
    
    protected int allocateChannel(final SshAbstractChannel sshAbstractChannel) {
        return super.allocateChannel(sshAbstractChannel);
    }
    
    public SshClient getClient() {
        return this.bd;
    }
    
    public SshMessageRouter getMessageRouter() {
        return this;
    }
    
    protected SshMessage createMessage(final byte[] array) throws SshException {
        if (array[0] >= 21 && array[0] <= 25) {
            return new SshChannelMessage(array);
        }
        return new SshMessage(array);
    }
    
    public int getChannelId() {
        return -1;
    }
    
    public void addChannelEventListener(final ChannelEventListener channelEventListener) {
        synchronized (this.hd) {
            if (channelEventListener != null) {
                this.hd.addElement(channelEventListener);
            }
        }
    }
    
    protected SshMessageStore getGlobalMessages() {
        return super.getGlobalMessages();
    }
    
    public boolean startShell() throws SshException {
        if (this.kd) {
            throw new SshException("The session is already in interactive mode!", 4);
        }
        this.fd.d(new byte[] { 12 });
        this.kd = true;
        this.start();
        return true;
    }
    
    public boolean executeCommand(final String s) throws SshException {
        if (this.kd) {
            throw new SshException("The session is already in interactive mode!", 4);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(13);
            byteArrayWriter.writeString(s);
            this.fd.d(byteArrayWriter.toByteArray());
            this.kd = true;
            this.start();
            return true;
        }
        catch (final IOException ex) {
            throw new SshException("Ssh1Session.executeCommand caught an IOException: " + ex.getMessage(), 5);
        }
    }
    
    public boolean executeCommand(final String s, final String s2) throws SshException {
        if (this.kd) {
            throw new SshException("The session is already in interactive mode!", 4);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(13);
            byteArrayWriter.writeString(s, s2);
            this.fd.d(byteArrayWriter.toByteArray());
            this.kd = true;
            this.start();
            return true;
        }
        catch (final IOException ex) {
            throw new SshException("Ssh1Session.executeCommand caught an IOException: " + ex.getMessage(), 5);
        }
    }
    
    public boolean isClosed() {
        return this.fd.d == 3;
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4) throws SshException {
        return this.requestPseudoTerminal(s, n, n2, n3, n4, new byte[] { 0 });
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4, final PseudoTerminalModes pseudoTerminalModes) throws SshException {
        return this.requestPseudoTerminal(s, n, n2, n3, n4, pseudoTerminalModes.toByteArray());
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4, final byte[] array) throws SshException {
        if (this.kd) {
            throw new SshException("The session is already in interactive mode!", 4);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(10);
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeInt(n2);
            byteArrayWriter.writeInt(n);
            byteArrayWriter.writeInt(n3);
            byteArrayWriter.writeInt(n4);
            byteArrayWriter.write(array);
            this.fd.d(byteArrayWriter.toByteArray());
            return this.fd.f();
        }
        catch (final IOException ex) {
            throw new SshException("Ssh1Client.requestPseudoTerminal() caught an IOException: " + ex.getMessage(), 5);
        }
    }
    
    public void changeTerminalDimensions(final int n, final int n2, final int n3, final int n4) throws SshException {
        if (!this.kd) {
            throw new SshException("Dimensions can only be changed whilst in interactive mode", 4);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(11);
            byteArrayWriter.writeInt(n2);
            byteArrayWriter.writeInt(n);
            byteArrayWriter.writeInt(n4);
            byteArrayWriter.writeInt(n3);
            this.fd.d(byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    void b(final byte[] array) throws SshException {
        this.fd.d(array);
    }
    
    protected boolean processGlobalMessage(final SshMessage sshMessage) throws SshException {
        switch (sshMessage.getMessageId()) {
            case 17: {
                if (this.hd != null) {
                    for (int i = 0; i < this.hd.size(); ++i) {
                        ((ChannelEventListener)this.hd.elementAt(i)).dataReceived(this, sshMessage.array(), sshMessage.getPosition() + 4, sshMessage.available() - 4);
                    }
                }
                return this.zc;
            }
            case 18: {
                if (this.hd != null) {
                    for (int j = 0; j < this.hd.size(); ++j) {
                        ((ChannelEventListener)this.hd.elementAt(j)).extendedDataReceived(this, sshMessage.array(), sshMessage.getPosition() + 4, sshMessage.available() - 4, 1);
                    }
                }
                return this.zc;
            }
            case 20: {
                synchronized (this.hd) {
                    for (int k = 0; k < this.hd.size(); ++k) {
                        ((ChannelEventListener)this.hd.elementAt(k)).channelClosing(this);
                    }
                }
                try {
                    this.cd = (int)sshMessage.readInt();
                }
                catch (final IOException ex) {
                    throw new SshException(5, ex);
                }
                this.gd = true;
                try {
                    this.fd.d(new byte[] { 33 });
                }
                catch (final Throwable t) {}
                finally {
                    this.getGlobalMessages().close();
                    this.fd.c();
                    synchronized (this.hd) {
                        for (int l = 0; l < this.hd.size(); ++l) {
                            ((ChannelEventListener)this.hd.elementAt(l)).channelClosed(this);
                        }
                    }
                    this.stop();
                }
                return true;
            }
            case 29: {
                int n = 0;
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                try {
                    n = (int)sshMessage.readInt();
                    final String string = sshMessage.readString();
                    final int n2 = (int)sshMessage.readInt();
                    String string2 = "";
                    if ((this.fd.k & 0x2) != 0x0) {
                        string2 = sshMessage.readString();
                    }
                    final e b = this.jd.b(string, n2, string2);
                    byteArrayWriter.write(21);
                    byteArrayWriter.writeInt(n);
                    byteArrayWriter.writeInt(b.getChannelId());
                    this.b(byteArrayWriter.toByteArray());
                    b.b(n);
                }
                catch (final Exception ex2) {
                    try {
                        byteArrayWriter.write(22);
                        byteArrayWriter.writeInt(n);
                        this.b(byteArrayWriter.toByteArray());
                    }
                    catch (final Exception ex3) {}
                }
                return true;
            }
            case 27: {
                final ByteArrayWriter byteArrayWriter2 = new ByteArrayWriter();
                int n3 = 0;
                try {
                    n3 = (int)sshMessage.readInt();
                    String string3 = "";
                    if ((this.fd.k & 0x2) != 0x0) {
                        string3 = sshMessage.readString();
                    }
                    final String x11Display = this.bd.getContext().getX11Display();
                    final int index = x11Display.indexOf(58);
                    String substring = "localhost";
                    int n4;
                    if (index != -1) {
                        substring = x11Display.substring(0, index);
                        n4 = Integer.parseInt(x11Display.substring(index + 1));
                    }
                    else {
                        n4 = Integer.parseInt(x11Display);
                    }
                    final e b2 = this.jd.b(x11Display, substring, (n4 <= 10) ? (6000 + n4) : n4, string3);
                    byteArrayWriter2.write(21);
                    byteArrayWriter2.writeInt(n3);
                    byteArrayWriter2.writeInt(b2.getChannelId());
                    this.b(byteArrayWriter2.toByteArray());
                    b2.b(n3);
                }
                catch (final Exception ex4) {
                    try {
                        byteArrayWriter2.write(22);
                        byteArrayWriter2.writeInt(n3);
                        this.b(byteArrayWriter2.toByteArray());
                    }
                    catch (final Exception ex5) {}
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public int exitCode() {
        return this.cd;
    }
    
    public InputStream getInputStream() {
        return this.id;
    }
    
    public InputStream getStderrInputStream() {
        return this.dd;
    }
    
    public OutputStream getOutputStream() {
        return this.ed;
    }
    
    public void close() {
        synchronized (this.hd) {
            for (int i = 0; i < this.hd.size(); ++i) {
                ((ChannelEventListener)this.hd.elementAt(i)).channelClosing(this);
            }
        }
        try {
            this.ed.close();
        }
        catch (final IOException ex) {}
        this.getGlobalMessages().close();
        this.signalClosingState();
        this.fd.c("The user disconnected the application");
        synchronized (this.hd) {
            for (int j = 0; j < this.hd.size(); ++j) {
                ((ChannelEventListener)this.hd.elementAt(j)).channelClosed(this);
            }
        }
    }
    
    void b(final int n, final byte[] array, final e e) throws SshException, ChannelOpenException {
        this.b(n, array, e, 0L);
    }
    
    void b(final int n, final byte[] array, final e e, final long n2) throws SshException, ChannelOpenException {
        try {
            if (!this.kd) {
                throw new SshException("The session must be in interactive mode! Start the user's shell before attempting this operation", 4);
            }
            final int allocateChannel = this.allocateChannel(e);
            if (allocateChannel == -1) {
                throw new ChannelOpenException("Maximum number of channels exceeded", 4);
            }
            e.b(this, allocateChannel);
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(n);
            byteArrayWriter.writeInt(allocateChannel);
            byteArrayWriter.write(array);
            this.fd.d(byteArrayWriter.toByteArray());
            final SshMessage nextMessage = e.getMessageStore().nextMessage(b.ad, n2);
            if (nextMessage.getMessageId() != 21) {
                throw new SshException("The remote computer failed to open a channel", 6);
            }
            e.b((int)nextMessage.readInt());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    boolean b(final String x11Display, final ForwardingRequestListener forwardingRequestListener) throws SshException {
        if (!this.bd.getContext().getX11Display().equals(x11Display)) {
            this.bd.getContext().setX11Display(x11Display);
        }
        final int index = x11Display.indexOf(58);
        int n;
        if (index != -1) {
            x11Display.substring(0, index);
            n = Integer.parseInt(x11Display.substring(index + 1));
        }
        else {
            n = Integer.parseInt(x11Display);
        }
        final byte[] x11AuthenticationCookie = this.bd.getContext().getX11AuthenticationCookie();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; ++i) {
            String s = Integer.toHexString(x11AuthenticationCookie[i] & 0xFF);
            if (s.length() == 1) {
                s = "0" + s;
            }
            sb.append(s);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(34);
            byteArrayWriter.writeString("MIT-MAGIC-COOKIE-1");
            byteArrayWriter.writeString(sb.toString());
            byteArrayWriter.writeInt(n);
            this.fd.d(byteArrayWriter.toByteArray());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
        final boolean f = this.fd.f();
        this.yc = f;
        if (f) {
            this.ld.put(x11Display, forwardingRequestListener);
            return true;
        }
        return false;
    }
    
    boolean b(final int n, final String s, final int n2, final ForwardingRequestListener forwardingRequestListener) throws SshException {
        final String string = s + ":" + String.valueOf(n2);
        if (this.ld.containsKey(string)) {
            throw new SshException(string + " has already been requested!", 4);
        }
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(28);
            byteArrayWriter.writeInt(n);
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeInt(n2);
            this.fd.d(byteArrayWriter.toByteArray());
            if (this.fd.f()) {
                this.ld.put(string, forwardingRequestListener);
                return true;
            }
            return false;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public SshTunnel b(final String s, final int n, final String s2, final int n2, final String s3, final int n3, final SshTransport sshTransport, final ChannelEventListener channelEventListener) throws SshException, ChannelOpenException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeInt(n);
            if ((this.fd.k & 0x2) != 0x0) {
                byteArrayWriter.writeString(s3 + ":" + String.valueOf(n3));
            }
            final d d = new d(this.fd.b, s, n, s2, n2, s3, n3, 1, sshTransport);
            d.addChannelEventListener(channelEventListener);
            this.b(29, byteArrayWriter.toByteArray(), d);
            return d;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 6);
        }
    }
    
    protected void onThreadExit() {
    }
    
    static {
        ad = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 21:
                    case 22: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
    }
    
    class _b
    {
        public e b(final String s, final String s2, final int n, final String s3) throws SshException {
            if (b.this.ld.containsKey(s)) {
                final ForwardingRequestListener forwardingRequestListener = b.this.ld.get(s);
                final int index = s.indexOf(":");
                String substring;
                int n2;
                if (index > -1) {
                    substring = s.substring(0, index);
                    n2 = Integer.parseInt(s.substring(index + 1));
                }
                else {
                    substring = "";
                    n2 = Integer.parseInt(s.substring(index + 1));
                }
                final d d = new d(b.this.fd.b, s2, n, substring, n2, s3, -1, 3, forwardingRequestListener.createConnection(s2, n));
                d.b(b.this, b.this.allocateChannel(d));
                forwardingRequestListener.initializeTunnel(d);
                return d;
            }
            throw new SshException("Forwarding had not previously been requested", 6);
        }
        
        public e b(final String s, final int n, final String s2) throws SshException {
            final String string = s + ":" + String.valueOf(n);
            if (b.this.ld.containsKey(string)) {
                final ForwardingRequestListener forwardingRequestListener = b.this.ld.get(string);
                final d d = new d(b.this.fd.b, s, n, "127.0.0.1", n, s2, -1, 2, forwardingRequestListener.createConnection(s, n));
                d.b(b.this, b.this.allocateChannel(d));
                forwardingRequestListener.initializeTunnel(d);
                return d;
            }
            throw new SshException("Forwarding had not previously been requested", 6);
        }
    }
    
    class _c extends OutputStream
    {
        public void write(final int n) throws IOException {
            this.write(new byte[] { (byte)n }, 0, 1);
        }
        
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            if (b.this.fd.e() == 3) {
                throw new SshIOException(new SshException("The session is closed!", 6));
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(n2 + 5);
            byteArrayWriter.write(16);
            byteArrayWriter.writeBinaryString(array, n, n2);
            try {
                b.this.fd.d(byteArrayWriter.toByteArray());
            }
            catch (final SshException ex) {
                throw new EOFException();
            }
            if (b.this.hd != null) {
                for (int i = 0; i < b.this.hd.size(); ++i) {
                    ((ChannelEventListener)b.this.hd.elementAt(i)).dataSent(b.this, array, n, n2);
                }
            }
        }
        
        public void close() throws IOException {
            try {
                b.this.fd.d(new byte[] { 19 });
            }
            catch (final SshException ex) {}
        }
    }
    
    class _d extends InputStream
    {
        int b;
        SshMessage d;
        MessageObserver c;
        
        _d(final int b) {
            this.b = b;
            this.c = new MessageObserver() {
                public boolean wantsNotification(final Message message) {
                    return message.getMessageId() == _d.this.b;
                }
            };
        }
        
        public int available() throws IOException {
            try {
                if ((this.d == null || this.d.available() == 0) && com.maverick.ssh1.b.this.getGlobalMessages().hasMessage(this.c) != null) {
                    this.b();
                }
                return (this.d == null) ? 0 : this.d.available();
            }
            catch (final EOFException ex) {
                return -1;
            }
            catch (final SshException ex2) {
                throw new SshIOException(ex2);
            }
        }
        
        void b() throws SshException, EOFException {
            (this.d = com.maverick.ssh1.b.this.getGlobalMessages().nextMessage(this.c, 0L)).skip(4L);
        }
        
        public int read() throws IOException {
            try {
                if (this.d == null || this.d.available() == 0) {
                    this.b();
                }
                return this.d.read();
            }
            catch (final EOFException ex) {
                return -1;
            }
            catch (final SshException ex2) {
                throw new SshIOException(ex2);
            }
        }
        
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            try {
                if (this.d == null || this.d.available() == 0) {
                    this.b();
                }
                return this.d.read(array, n, n2);
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
            catch (final EOFException ex2) {
                return -1;
            }
        }
    }
}

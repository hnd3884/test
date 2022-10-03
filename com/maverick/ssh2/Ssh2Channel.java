package com.maverick.ssh2;

import com.maverick.ssh.SshIOException;
import com.maverick.util.ByteArrayWriter;
import java.io.EOFException;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.message.SshChannelMessage;
import java.io.IOException;
import java.util.Enumeration;
import com.maverick.ssh.SshChannel;
import com.maverick.ssh.message.SshMessageRouter;
import com.maverick.ssh.ChannelEventListener;
import java.io.OutputStream;
import java.io.InputStream;
import com.maverick.ssh.SshException;
import com.maverick.ssh.message.SshMessageStore;
import com.maverick.ssh.message.Message;
import com.maverick.ssh.message.MessageObserver;
import java.util.Vector;
import com.maverick.ssh.message.SshAbstractChannel;

public class Ssh2Channel extends SshAbstractChannel
{
    public static final String SESSION_CHANNEL = "session";
    b mb;
    int hb;
    String bc;
    Vector qb;
    boolean rb;
    boolean wb;
    boolean xb;
    boolean sb;
    final MessageObserver kb;
    final MessageObserver ac;
    final MessageObserver ib;
    final MessageObserver yb;
    final MessageObserver pb;
    static final MessageObserver zb;
    _d lb;
    _b vb;
    _c ob;
    _c ub;
    boolean jb;
    boolean nb;
    boolean tb;
    
    public Ssh2Channel(final String bc, final int n, final int n2) {
        this.qb = new Vector();
        this.rb = false;
        this.wb = false;
        this.xb = false;
        this.sb = false;
        this.kb = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 93:
                    case 96:
                    case 97: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
        this.ac = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 94:
                    case 96:
                    case 97: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
        this.ib = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 95:
                    case 96:
                    case 97: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
        this.yb = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 97:
                    case 99:
                    case 100: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
        this.pb = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 97: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
        this.jb = false;
        this.nb = false;
        this.tb = false;
        this.bc = bc;
        this.ob = new _c(n, n2);
        this.lb = new _d(this.ac);
        this.vb = new _b();
    }
    
    protected MessageObserver getStickyMessageIds() {
        return Ssh2Channel.zb;
    }
    
    public void setAutoConsumeInput(final boolean rb) {
        this.rb = rb;
    }
    
    long c() {
        return this.ob.c();
    }
    
    int b() {
        return this.ob.b();
    }
    
    protected SshMessageStore getMessageStore() throws SshException {
        return super.getMessageStore();
    }
    
    public String getName() {
        return this.bc;
    }
    
    public InputStream getInputStream() {
        return this.lb;
    }
    
    public OutputStream getOutputStream() {
        return this.vb;
    }
    
    public void addChannelEventListener(final ChannelEventListener channelEventListener) {
        synchronized (this.qb) {
            if (channelEventListener != null) {
                this.qb.addElement(channelEventListener);
            }
        }
    }
    
    public boolean isSendKeepAliveOnIdle() {
        return this.wb;
    }
    
    public void setSendKeepAliveOnIdle(final boolean wb) {
        this.wb = wb;
    }
    
    public void idle() {
        if (this.wb) {
            try {
                this.sendRequest("keep-alive@sshtools.com", false, null, false);
            }
            catch (final SshException ex) {}
        }
    }
    
    void b(final b mb, final int n) {
        super.init(this.mb = mb, n);
    }
    
    protected byte[] create() {
        return null;
    }
    
    protected void open(final int hb, final long n, final int n2) throws IOException {
        this.hb = hb;
        this.ub = new _c(n, n2);
        super.state = 2;
        synchronized (this.qb) {
            final Enumeration elements = this.qb.elements();
            while (elements.hasMoreElements()) {
                ((ChannelEventListener)elements.nextElement()).channelOpened(this);
            }
        }
    }
    
    protected void open(final int n, final long n2, final int n3, final byte[] array) throws IOException {
        this.open(n, n2, n3);
    }
    
    protected boolean processChannelMessage(final SshChannelMessage sshChannelMessage) throws SshException {
        try {
            switch (sshChannelMessage.getMessageId()) {
                case 98: {
                    final String string = sshChannelMessage.readString();
                    final boolean b = sshChannelMessage.read() != 0;
                    final byte[] array = new byte[sshChannelMessage.available()];
                    sshChannelMessage.read(array);
                    this.channelRequest(string, b, array);
                    return true;
                }
                case 93: {
                    return false;
                }
                case 94: {
                    if (this.rb) {
                        this.ob.b(sshChannelMessage.available() - 4);
                        if (this.ob.c() <= this.ob.d() / 2L) {
                            this.b(this.ob.d() - this.ob.c());
                        }
                    }
                    final Enumeration elements = this.qb.elements();
                    while (elements.hasMoreElements()) {
                        ((ChannelEventListener)elements.nextElement()).dataReceived(this, sshChannelMessage.array(), sshChannelMessage.getPosition() + 4, sshChannelMessage.available() - 4);
                    }
                    return this.rb;
                }
                case 95: {
                    final int n = (int)ByteArrayReader.readInt(sshChannelMessage.array(), sshChannelMessage.getPosition());
                    if (this.rb) {
                        this.ob.b(sshChannelMessage.available() - 8);
                        if (this.ob.c() <= this.ob.d() / 2L) {
                            this.b(this.ob.d() - this.ob.c());
                        }
                    }
                    final Enumeration elements2 = this.qb.elements();
                    while (elements2.hasMoreElements()) {
                        ((ChannelEventListener)elements2.nextElement()).extendedDataReceived(this, sshChannelMessage.array(), sshChannelMessage.getPosition() + 8, sshChannelMessage.available() - 8, n);
                    }
                    return this.rb;
                }
                case 97: {
                    synchronized (this) {
                        if (!this.jb) {
                            synchronized (super.ms) {
                                if (!super.ms.isClosed()) {
                                    super.ms.close();
                                }
                            }
                        }
                    }
                    this.checkCloseStatus(true);
                    return false;
                }
                case 96: {
                    this.xb = true;
                    final Enumeration elements3 = this.qb.elements();
                    while (elements3.hasMoreElements()) {
                        ((ChannelEventListener)elements3.nextElement()).channelEOF(this);
                    }
                    this.channelEOF();
                    if (this.sb) {
                        this.close();
                    }
                    return false;
                }
                default: {
                    return false;
                }
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    SshChannelMessage b(final MessageObserver messageObserver) throws SshException, EOFException {
        final SshChannelMessage sshChannelMessage = (SshChannelMessage)super.ms.nextMessage(messageObserver, 0L);
        switch (sshChannelMessage.getMessageId()) {
            case 93: {
                try {
                    this.ub.b(sshChannelMessage.readInt());
                    break;
                }
                catch (final IOException ex) {
                    throw new SshException(5, ex);
                }
            }
            case 94: {
                try {
                    this.processStandardData((int)sshChannelMessage.readInt(), sshChannelMessage);
                    break;
                }
                catch (final IOException ex2) {
                    throw new SshException(5, ex2);
                }
            }
            case 95: {
                try {
                    this.processExtendedData((int)sshChannelMessage.readInt(), (int)sshChannelMessage.readInt(), sshChannelMessage);
                    break;
                }
                catch (final IOException ex3) {
                    throw new SshException(5, ex3);
                }
            }
            case 97: {
                this.checkCloseStatus(true);
                throw new EOFException("The channel is closed");
            }
            case 96: {
                throw new EOFException("The channel is EOF");
            }
        }
        return sshChannelMessage;
    }
    
    protected void processStandardData(final int n, final SshChannelMessage sshChannelMessage) throws SshException {
        this.lb.b(n, sshChannelMessage);
    }
    
    protected void processExtendedData(final int n, final int n2, final SshChannelMessage sshChannelMessage) throws SshException {
    }
    
    protected _d createExtendedDataStream() {
        return new _d(this.ib);
    }
    
    void b(final byte[] array, final int n, final int n2) throws SshException {
        try {
            if (super.state != 2) {
                throw new SshException("The channel is closed", 6);
            }
            if (n2 > 0) {
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(n2 + 9);
                byteArrayWriter.write(94);
                byteArrayWriter.writeInt(this.hb);
                byteArrayWriter.writeBinaryString(array, n, n2);
                this.mb.b(byteArrayWriter.toByteArray(), true);
            }
            final Enumeration elements = this.qb.elements();
            while (elements.hasMoreElements()) {
                ((ChannelEventListener)elements.nextElement()).dataSent(this, array, n, n2);
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    private void b(final long n) throws SshException {
        try {
            if (this.jb || this.isClosed()) {
                return;
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(9);
            byteArrayWriter.write(93);
            byteArrayWriter.writeInt(this.hb);
            byteArrayWriter.writeInt(n);
            this.ob.b(n);
            this.mb.b(byteArrayWriter.toByteArray(), true);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean sendRequest(final String s, final boolean b, final byte[] array) throws SshException {
        return this.sendRequest(s, b, array, true);
    }
    
    public boolean sendRequest(final String s, final boolean b, final byte[] array, final boolean b2) throws SshException {
        synchronized (this.mb) {
            try {
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                byteArrayWriter.write(98);
                byteArrayWriter.writeInt(this.hb);
                byteArrayWriter.writeString(s);
                byteArrayWriter.writeBoolean(b);
                if (array != null) {
                    byteArrayWriter.write(array);
                }
                this.mb.b(byteArrayWriter.toByteArray(), true);
                final boolean b3 = false;
                if (b) {
                    return this.b(this.yb).getMessageId() == 99;
                }
                return b3;
            }
            catch (final IOException ex) {
                throw new SshException(ex, 5);
            }
        }
    }
    
    public void close() {
        int n = 0;
        synchronized (this) {
            if (!this.jb && super.state == 2) {
                final boolean jb = true;
                this.jb = jb;
                n = (jb ? 1 : 0);
            }
        }
        if (n != 0) {
            synchronized (this.qb) {
                final Enumeration elements = this.qb.elements();
                while (elements.hasMoreElements()) {
                    ((ChannelEventListener)elements.nextElement()).channelClosing(this);
                }
            }
            try {
                this.vb.b(!this.sb);
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(5);
                byteArrayWriter.write(97);
                byteArrayWriter.writeInt(this.hb);
                try {
                    this.mb.b(byteArrayWriter.toByteArray(), true);
                }
                catch (final SshException ex) {}
                super.state = 3;
            }
            catch (final EOFException ex2) {}
            catch (final SshIOException ex3) {
                this.mb.xc.disconnect(10, "IOException during channel close: " + ex3.getMessage());
            }
            catch (final IOException ex4) {
                this.mb.xc.disconnect(10, "IOException during channel close: " + ex4.getMessage());
            }
            finally {
                this.checkCloseStatus(super.ms.isClosed());
            }
        }
    }
    
    protected void checkCloseStatus(boolean b) {
        if (super.state != 3) {
            this.close();
            if (!b) {
                b = (super.ms.hasMessage(this.pb) != null);
            }
        }
        if (b) {
            synchronized (this) {
                if (!this.nb) {
                    synchronized (this.qb) {
                        final Enumeration elements = this.qb.elements();
                        while (elements.hasMoreElements()) {
                            ((ChannelEventListener)elements.nextElement()).channelClosed(this);
                        }
                    }
                    this.mb.b(this);
                    this.nb = true;
                }
            }
        }
    }
    
    public boolean equals(final Object o) {
        return o instanceof Ssh2Channel && ((Ssh2Channel)o).getChannelId() == super.channelid;
    }
    
    protected void channelRequest(final String s, final boolean b, final byte[] array) throws SshException {
        if (b) {
            try {
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                byteArrayWriter.write(100);
                byteArrayWriter.writeInt(this.hb);
                this.mb.b(byteArrayWriter.toByteArray(), true);
            }
            catch (final IOException ex) {
                throw new SshException(ex, 5);
            }
        }
    }
    
    protected void channelEOF() {
    }
    
    static {
        zb = new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                switch (message.getMessageId()) {
                    case 96:
                    case 97: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        };
    }
    
    class _b extends OutputStream
    {
        public void write(final int n) throws IOException {
            this.write(new byte[] { (byte)n }, 0, 1);
        }
        
        public void write(final byte[] array, int n, int i) throws IOException {
            try {
                do {
                    if (Ssh2Channel.this.ub.c() <= 0L) {
                        Ssh2Channel.this.b(Ssh2Channel.this.kb);
                    }
                    synchronized (Ssh2Channel.this) {
                        if (Ssh2Channel.this.sb) {
                            throw new EOFException("The channel is EOF");
                        }
                        if (Ssh2Channel.this.isClosed() || Ssh2Channel.this.jb) {
                            throw new EOFException("The channel is closed");
                        }
                        final long n2 = (Ssh2Channel.this.ub.c() < Ssh2Channel.this.ub.b()) ? ((Ssh2Channel.this.ub.c() < i) ? Ssh2Channel.this.ub.c() : i) : ((Ssh2Channel.this.ub.b() < i) ? Ssh2Channel.this.ub.b() : i);
                        if (n2 <= 0L) {
                            continue;
                        }
                        Ssh2Channel.this.b(array, n, (int)n2);
                        Ssh2Channel.this.ub.b((int)n2);
                        i -= (int)n2;
                        n += (int)n2;
                    }
                } while (i > 0);
            }
            catch (final SshException ex) {
                throw new SshIOException(ex);
            }
        }
        
        public void close() throws IOException {
            this.b(!Ssh2Channel.this.isClosed() && !Ssh2Channel.this.sb && !Ssh2Channel.this.jb);
        }
        
        public void b(final boolean b) throws IOException {
            if (b) {
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(5);
                byteArrayWriter.write(96);
                byteArrayWriter.writeInt(Ssh2Channel.this.hb);
                try {
                    Ssh2Channel.this.mb.b(byteArrayWriter.toByteArray(), true);
                }
                catch (final SshException ex) {
                    throw new SshIOException(ex);
                }
            }
            Ssh2Channel.this.sb = true;
            if (Ssh2Channel.this.xb) {
                Ssh2Channel.this.close();
            }
        }
    }
    
    static class _c
    {
        long c;
        long d;
        int b;
        
        _c(final long n, final int b) {
            this.d = n;
            this.c = n;
            this.b = b;
        }
        
        int b() {
            return this.b;
        }
        
        long d() {
            return this.d;
        }
        
        void b(final long n) {
            this.c += n;
        }
        
        void b(final int n) {
            this.c -= n;
        }
        
        long c() {
            return this.c;
        }
    }
    
    class _d extends InputStream
    {
        int c;
        MessageObserver e;
        long b;
        SshChannelMessage d;
        
        _d(final MessageObserver e) {
            this.c = 0;
            this.b = 0L;
            this.d = null;
            this.e = e;
        }
        
        void b(final int c, final SshChannelMessage d) {
            this.c = c;
            this.d = d;
        }
        
        public synchronized int available() throws IOException {
            try {
                if (this.c == 0 && Ssh2Channel.this.getMessageStore().hasMessage(this.e) != null) {
                    Ssh2Channel.this.b(this.e);
                }
                return this.c;
            }
            catch (final EOFException ex) {
                return -1;
            }
            catch (final SshException ex2) {
                throw new SshIOException(ex2);
            }
        }
        
        public int read() throws IOException {
            final byte[] array = { 0 };
            if (this.read(array, 0, 1) > 0) {
                return array[0] & 0xFF;
            }
            return -1;
        }
        
        public long skip(final long n) throws IOException {
            final int n2 = (this.c < n) ? this.c : ((int)n);
            try {
                if (n2 == 0 && Ssh2Channel.this.isClosed()) {
                    throw new EOFException("The inputstream is closed");
                }
                this.d.skip(n2);
                this.c -= n2;
                if (this.c + Ssh2Channel.this.ob.c() < Ssh2Channel.this.ob.d() / 2L) {
                    try {
                        Ssh2Channel.this.b(Ssh2Channel.this.ob.d() - Ssh2Channel.this.ob.c() - this.c);
                    }
                    catch (final SshException ex) {
                        throw new SshIOException(ex);
                    }
                }
            }
            finally {
                this.b += n2;
            }
            return n2;
        }
        
        public synchronized int read(final byte[] array, final int n, final int n2) throws IOException {
            try {
                if (this.available() == -1) {
                    return -1;
                }
                while (this.c <= 0 && !Ssh2Channel.this.isClosed()) {
                    Ssh2Channel.this.b(this.e);
                }
                final int n3 = (this.c < n2) ? this.c : n2;
                if (n3 == 0 && Ssh2Channel.this.isClosed()) {
                    return -1;
                }
                this.d.read(array, n, n3);
                Ssh2Channel.this.ob.b(n3);
                this.c -= n3;
                if (System.getProperty("maverick.windowAdjustTest", "false").equals("true") || (this.c + Ssh2Channel.this.ob.c() < Ssh2Channel.this.ob.d() / 2L && !Ssh2Channel.this.isClosed() && !Ssh2Channel.this.jb)) {
                    Ssh2Channel.this.b(Ssh2Channel.this.ob.d() - Ssh2Channel.this.ob.c() - this.c);
                }
                this.b += n3;
                return n3;
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

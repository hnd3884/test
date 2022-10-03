package com.maverick.ssh2;

import java.util.StringTokenizer;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.Digest;
import com.maverick.events.Event;
import com.maverick.events.EventServiceImplementation;
import com.maverick.util.ByteArrayReader;
import java.util.Enumeration;
import java.io.InterruptedIOException;
import com.maverick.ssh.components.ComponentManager;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.events.EventLog;
import com.maverick.ssh.SocketTimeoutSupport;
import java.util.Vector;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.compression.SshCompression;
import com.maverick.ssh.components.SshHmac;
import com.maverick.ssh.components.SshCipher;
import com.maverick.ssh.components.SshKeyExchangeClient;
import com.maverick.ssh.SshTransport;
import java.io.OutputStream;
import java.io.DataInputStream;
import com.maverick.ssh.message.SshMessageReader;

public class TransportProtocol implements SshMessageReader
{
    public static String CHARSET_ENCODING;
    DataInputStream ib;
    OutputStream bc;
    SshTransport lb;
    Ssh2Context qb;
    Ssh2Client fc;
    String zb;
    String rb;
    byte[] lc;
    byte[] bb;
    byte[] nc;
    public static final int NEGOTIATING_PROTOCOL = 1;
    public static final int PERFORMING_KEYEXCHANGE = 2;
    public static final int CONNECTED = 3;
    public static final int DISCONNECTED = 4;
    int pb;
    Throwable x;
    String cb;
    SshKeyExchangeClient mc;
    SshKeyExchangeClient gb;
    SshCipher cc;
    SshCipher xb;
    SshHmac fb;
    SshHmac tb;
    SshCompression kc;
    SshCompression nb;
    SshPublicKey z;
    boolean kb;
    boolean hc;
    int w;
    int hb;
    boolean mb;
    byte[] ab;
    ByteArrayWriter jb;
    int vb;
    int jc;
    long ec;
    long ic;
    int eb;
    int yb;
    int wb;
    int ub;
    long gc;
    long db;
    Object sb;
    Vector ac;
    Vector y;
    Vector dc;
    long ob;
    public static final int HOST_NOT_ALLOWED = 1;
    public static final int PROTOCOL_ERROR = 2;
    public static final int KEY_EXCHANGE_FAILED = 3;
    public static final int RESERVED = 4;
    public static final int MAC_ERROR = 5;
    public static final int COMPRESSION_ERROR = 6;
    public static final int SERVICE_NOT_AVAILABLE = 7;
    public static final int PROTOCOL_VERSION_NOT_SUPPORTED = 8;
    public static final int HOST_KEY_NOT_VERIFIABLE = 9;
    public static final int CONNECTION_LOST = 10;
    public static final int BY_APPLICATION = 11;
    public static final int TOO_MANY_CONNECTIONS = 12;
    public static final int AUTH_CANCELLED_BY_USER = 13;
    public static final int NO_MORE_AUTH_METHODS_AVAILABLE = 14;
    public static final int ILLEGAL_USER_NAME = 15;
    boolean v;
    
    public TransportProtocol() {
        this.kb = false;
        this.hc = false;
        this.w = 8;
        this.hb = 0;
        this.mb = false;
        this.vb = 8;
        this.jc = 0;
        this.ec = 0L;
        this.ic = 0L;
        this.gc = 0L;
        this.db = 0L;
        this.sb = new Object();
        this.ac = new Vector();
        this.y = new Vector();
        this.dc = new Vector();
        this.ob = System.currentTimeMillis();
        this.v = Boolean.valueOf(System.getProperty("maverick.verbose", "false"));
    }
    
    public SshTransport getProvider() {
        return this.lb;
    }
    
    public void addListener(final TransportProtocolListener transportProtocolListener) {
        this.dc.addElement(transportProtocolListener);
    }
    
    public Ssh2Client getClient() {
        return this.fc;
    }
    
    public boolean isConnected() {
        return this.pb == 3 || this.pb == 2;
    }
    
    public Throwable getLastError() {
        return this.x;
    }
    
    public Ssh2Context getContext() {
        return this.qb;
    }
    
    public boolean getIgnoreHostKeyifEmpty() {
        return this.mb;
    }
    
    public void setIgnoreHostKeyifEmpty(final boolean mb) {
        this.mb = mb;
    }
    
    public void startTransportProtocol(final SshTransport lb, final Ssh2Context qb, final String zb, final String rb, final Ssh2Client fc) throws SshException {
        try {
            this.ib = new DataInputStream(lb.getInputStream());
            this.bc = lb.getOutputStream();
            this.lb = lb;
            this.zb = zb;
            this.rb = rb;
            this.qb = qb;
            this.ab = new byte[this.qb.getMaximumPacketLength()];
            this.jb = new ByteArrayWriter(this.qb.getMaximumPacketLength());
            this.fc = fc;
            if (qb.getSocketTimeout() > 0 && lb instanceof SocketTimeoutSupport) {
                ((SocketTimeoutSupport)lb).setSoTimeout(qb.getSocketTimeout());
            }
            else if (qb.getSocketTimeout() > 0) {
                EventLog.LogEvent(this, "Socket timeout is set on SshContext but SshTransport does not support socket timeouts");
            }
            this.pb = 1;
            this.b(false);
            while (this.processMessage(this.j()) && this.pb != 3) {}
        }
        catch (final IOException ex) {
            throw new SshException(ex, 10);
        }
    }
    
    public String getRemoteIdentification() {
        return this.rb;
    }
    
    public byte[] getSessionIdentifier() {
        return this.nc;
    }
    
    public void disconnect(final int n, final String cb) {
        try {
            this.cb = cb;
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(1);
            byteArrayWriter.writeInt(n);
            byteArrayWriter.writeString(cb);
            byteArrayWriter.writeString("");
            EventLog.LogEvent(this, "Sending SSH_MSG_DISCONNECT [" + cb + "]");
            this.sendMessage(byteArrayWriter.toByteArray(), true);
        }
        catch (final Throwable t) {}
        finally {
            this.k();
        }
    }
    
    public void sendMessage(byte[] compress, final boolean b) throws SshException {
        synchronized (this.ac) {
            if (this.pb == 2 && !this.d(compress[0])) {
                this.ac.addElement(compress);
                return;
            }
        }
        synchronized (this.bc) {
            try {
                this.jb.reset();
                final int n = 4;
                if (this.kc != null && this.hc) {
                    compress = this.kc.compress(compress, 0, compress.length);
                }
                final int n2 = n + (this.w - (compress.length + 5 + n) % this.w) % this.w;
                this.jb.writeInt(compress.length + 1 + n2);
                this.jb.write(n2);
                this.jb.write(compress, 0, compress.length);
                ComponentManager.getInstance().getRND().nextBytes(this.jb.array(), this.jb.size(), n2);
                this.jb.move(n2);
                if (this.fb != null) {
                    this.fb.generate(this.ec, this.jb.array(), 0, this.jb.size(), this.jb.array(), this.jb.size());
                }
                if (this.cc != null) {
                    this.cc.transform(this.jb.array(), 0, this.jb.array(), 0, this.jb.size());
                }
                this.jb.move(this.hb);
                this.gc += this.jb.size();
                this.bc.write(this.jb.array(), 0, this.jb.size());
                this.bc.flush();
                if (b) {
                    this.ob = System.currentTimeMillis();
                }
                ++this.ec;
                this.wb += compress.length;
                ++this.ub;
                if (this.ec >= 4294967296L) {
                    this.ec = 0L;
                }
                if (!this.qb.isKeyReExchangeDisabled() && (this.wb >= 1073741824 || this.ub >= Integer.MAX_VALUE)) {
                    this.b(false);
                }
            }
            catch (final IOException ex) {
                this.k();
                throw new SshException("Unexpected termination: " + ex.getMessage(), 1);
            }
        }
    }
    
    public byte[] nextMessage() throws SshException {
        synchronized (this.sb) {
            byte[] j;
            do {
                j = this.j();
            } while (this.processMessage(j));
            return j;
        }
    }
    
    void b(final byte[] array, final int n, final int n2, final int n3, final boolean b) throws SshException {
        int n4 = 0;
        int c = 0;
        while (true) {
            if (b) {
                c = this.c(this.qb.getPartialMessageTimeout());
                try {
                    do {
                        try {
                            final int read = this.ib.read(array, n + n4, n2 - n4);
                            if (read == -1) {
                                throw new SshException("EOF received from remote side", 1);
                            }
                            n4 += read;
                        }
                        catch (final InterruptedIOException ex) {
                            if (b && ex.bytesTransferred > 0) {
                                n4 += ex.bytesTransferred;
                            }
                            else {
                                if (b) {
                                    this.k();
                                    throw new SshException("Remote host failed to respond during message receive!", 19);
                                }
                                if (this.getContext().getIdleConnectionTimeoutSeconds() > 0 && System.currentTimeMillis() - this.ob > this.getContext().getIdleConnectionTimeoutSeconds() * 1000) {
                                    this.disconnect(11, "Idle connection");
                                    throw new SshException("Connection has been dropped as it reached max idle time of " + this.getContext().getIdleConnectionTimeoutSeconds() + " seconds.", 12);
                                }
                                if (this.getContext().isSendIgnorePacketOnIdle()) {
                                    try {
                                        final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                                        byteArrayWriter.write(2);
                                        final byte[] array2 = new byte[(int)(Math.random() * this.getContext().getKeepAliveMaxDataLength() + 1.0)];
                                        ComponentManager.getInstance().getRND().nextBytes(array2);
                                        byteArrayWriter.writeBinaryString(array2);
                                        this.sendMessage(byteArrayWriter.toByteArray(), false);
                                    }
                                    catch (final IOException ex2) {
                                        this.b("Connection failed during SSH_MSG_IGNORE packet", 10);
                                    }
                                }
                                if (this.getContext().getSocketTimeout() <= 0) {
                                    throw new SshException("Socket connection timed out.", 19);
                                }
                                final Enumeration elements = this.dc.elements();
                                while (elements.hasMoreElements()) {
                                    final TransportProtocolListener transportProtocolListener = (TransportProtocolListener)elements.nextElement();
                                    try {
                                        transportProtocolListener.onIdle(this.ob);
                                    }
                                    catch (final Throwable t) {}
                                }
                            }
                        }
                        catch (final IOException ex3) {
                            throw new SshException("IO error received from remote" + ex3.getMessage(), 1, ex3);
                        }
                    } while (n4 < n2);
                }
                finally {
                    if (b) {
                        this.c(c);
                    }
                }
                return;
            }
            continue;
        }
    }
    
    private int c(final int soTimeout) {
        if (this.lb instanceof SocketTimeoutSupport) {
            try {
                final SocketTimeoutSupport socketTimeoutSupport = (SocketTimeoutSupport)this.lb;
                final int soTimeout2 = socketTimeoutSupport.getSoTimeout();
                socketTimeoutSupport.setSoTimeout(soTimeout);
                return soTimeout2;
            }
            catch (final IOException ex) {}
        }
        return 0;
    }
    
    byte[] j() throws SshException {
        synchronized (this.sb) {
            try {
                this.b(this.ab, 0, this.vb, this.qb.getPartialMessageTimeout(), false);
                if (this.xb != null) {
                    this.xb.transform(this.ab, 0, this.ab, 0, this.vb);
                }
                final int n = (int)ByteArrayReader.readInt(this.ab, 0);
                if (n <= 0) {
                    throw new SshException("Server sent invalid message length of " + n + "!", 3);
                }
                final int n2 = this.ab[4] & 0xFF;
                final int n3 = n - (this.vb - 4);
                if (n3 < 0) {
                    this.k();
                    throw new SshException("EOF whilst reading message data block", 1);
                }
                if (n3 > this.ab.length - this.vb) {
                    if (n3 + this.vb + this.jc > this.qb.getMaximumPacketLength()) {
                        this.k();
                        throw new SshException("Incoming packet length violates SSH protocol [" + n3 + this.vb + " bytes]", 1);
                    }
                    final byte[] ab = new byte[n3 + this.vb + this.jc];
                    System.arraycopy(this.ab, 0, ab, 0, this.vb);
                    this.ab = ab;
                }
                if (n3 > 0) {
                    this.b(this.ab, this.vb, n3, this.qb.getPartialMessageTimeout(), true);
                    if (this.xb != null) {
                        this.xb.transform(this.ab, this.vb, this.ab, this.vb, n3);
                    }
                }
                if (this.tb != null) {
                    this.b(this.ab, this.vb + n3, this.jc, this.qb.getPartialMessageTimeout(), true);
                    if (!this.tb.verify(this.ic, this.ab, 0, this.vb + n3, this.ab, this.vb + n3)) {
                        this.disconnect(5, "Corrupt Mac on input");
                        throw new SshException("Corrupt Mac on input", 3);
                    }
                }
                final long ic = this.ic + 1L;
                this.ic = ic;
                if (ic >= 4294967296L) {
                    this.ic = 0L;
                }
                this.db += this.vb + n3 + this.jc;
                final byte[] array = new byte[n + 4 - n2 - 5];
                System.arraycopy(this.ab, 5, array, 0, array.length);
                if (this.nb != null && this.kb) {
                    return this.nb.uncompress(array, 0, array.length);
                }
                this.eb += array.length;
                ++this.yb;
                if (!this.qb.isKeyReExchangeDisabled() && (this.eb >= 1073741824 || this.yb >= Integer.MAX_VALUE)) {
                    this.b(false);
                }
                return array;
            }
            catch (final InterruptedIOException ex) {
                throw new SshException("Interrupted IO; possible socket timeout detected?", 19);
            }
            catch (final IOException ex2) {
                this.k();
                throw new SshException("Unexpected terminaton: " + ((ex2.getMessage() != null) ? ex2.getMessage() : ex2.getClass().getName()) + " sequenceNo = " + this.ic + " bytesIn = " + this.db + " bytesOut = " + this.gc, 1, ex2);
            }
        }
    }
    
    public SshKeyExchangeClient getKeyExchange() {
        return this.mc;
    }
    
    public static boolean Arrayequals(final byte[] array, final byte[] array2) {
        if (array == array2) {
            return true;
        }
        if (array == null || array2 == null) {
            return false;
        }
        final int length = array.length;
        if (array2.length != length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    void e(byte[] j) throws SshException {
        try {
            synchronized (this.sb) {
                if (this.lc == null) {
                    this.b(false);
                }
                this.pb = 2;
                this.bb = j;
                final ByteArrayReader byteArrayReader = new ByteArrayReader(this.bb, 0, this.bb.length);
                byteArrayReader.skip(17L);
                final String b = this.b("key exchange", byteArrayReader.readString());
                final String b2 = this.b("public key", byteArrayReader.readString());
                final String b3 = this.b("client->server cipher", byteArrayReader.readString());
                final String b4 = this.b("server->client cipher", byteArrayReader.readString());
                final String b5 = this.b("client->server mac", byteArrayReader.readString());
                final String b6 = this.b("server->client mac", byteArrayReader.readString());
                final String b7 = this.b("client->server comp", byteArrayReader.readString());
                final String b8 = this.b("server->client comp", byteArrayReader.readString());
                byteArrayReader.readString();
                byteArrayReader.readString();
                final boolean boolean1 = byteArrayReader.readBoolean();
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 3, true).addAttribute("REMOTE_KEY_EXCHANGES", b).addAttribute("LOCAL_KEY_EXCHANGES", this.qb.supportedKeyExchanges().list(this.qb.getPreferredKeyExchange())).addAttribute("REMOTE_PUBLICKEYS", b2).addAttribute("LOCAL_PUBLICKEYS", this.qb.supportedPublicKeys().list(this.qb.getPreferredPublicKey())).addAttribute("REMOTE_CIPHERS_CS", b3).addAttribute("LOCAL_CIPHERS_CS", this.qb.supportedCiphersCS().list(this.qb.getPreferredCipherCS())).addAttribute("REMOTE_CIPHERS_SC", b4).addAttribute("LOCAL_CIPHERS_SC", this.qb.supportedCiphersSC().list(this.qb.getPreferredCipherSC())).addAttribute("REMOTE_CS_MACS", b5).addAttribute("LOCAL_CS_MACS", this.qb.supportedMacsCS().list(this.qb.getPreferredMacCS())).addAttribute("REMOTE_SC_MACS", b6).addAttribute("LOCAL_SC_MACS", this.qb.supportedMacsSC().list(this.qb.getPreferredMacSC())).addAttribute("REMOTE_CS_COMPRESSIONS", b7).addAttribute("LOCAL_CS_COMPRESSIONS", this.qb.supportedCompressionsCS().list(this.qb.getPreferredCompressionCS())).addAttribute("REMOTE_SC_COMPRESSIONS", b8).addAttribute("LOCAL_SC_COMPRESSIONS", this.qb.supportedCompressionsSC().list(this.qb.getPreferredCompressionSC())));
                final String c = this.c(this.qb.supportedCiphersCS().list(this.qb.getPreferredCipherCS()), b3);
                final String c2 = this.c(this.qb.supportedCiphersSC().list(this.qb.getPreferredCipherSC()), b4);
                final SshCipher cc = (SshCipher)this.qb.supportedCiphersCS().getInstance(c);
                final SshCipher xb = (SshCipher)this.qb.supportedCiphersSC().getInstance(c2);
                final String c3 = this.c(this.qb.supportedMacsCS().list(this.qb.getPreferredMacCS()), this.b("client->server hmac", b5));
                final String c4 = this.c(this.qb.supportedMacsSC().list(this.qb.getPreferredMacSC()), this.b("server->client hmac", b6));
                final SshHmac fb = (SshHmac)this.qb.supportedMacsCS().getInstance(c3);
                final SshHmac tb = (SshHmac)this.qb.supportedMacsSC().getInstance(c4);
                final String c5 = this.c(this.qb.supportedCompressionsCS().list(this.qb.getPreferredCompressionCS()), this.b("client->server compression", b7));
                final String c6 = this.c(this.qb.supportedCompressionsSC().list(this.qb.getPreferredCompressionSC()), this.b("server->client compression", b8));
                SshCompression kc = null;
                if (!c5.equals("none")) {
                    kc = (SshCompression)this.qb.supportedCompressionsCS().getInstance(c5);
                    kc.init(1, 6);
                }
                SshCompression nb = null;
                if (!c6.equals("none")) {
                    nb = (SshCompression)this.qb.supportedCompressionsSC().getInstance(c6);
                    nb.init(0, 6);
                }
                boolean b9 = false;
                final String c7 = this.c(this.qb.supportedKeyExchanges().list(this.qb.getPreferredKeyExchange()), b);
                if (this.gb == null || !c7.equals(this.gb.getAlgorithm())) {
                    this.mc = (SshKeyExchangeClient)this.qb.supportedKeyExchanges().getInstance(c7);
                }
                if (boolean1) {
                    if (!c7.equals(this.qb.getPreferredKeyExchange())) {
                        b9 = true;
                    }
                    final String c8 = this.c(this.qb.supportedPublicKeys().list(this.qb.getPreferredPublicKey()), b2);
                    if (!b9 && !c8.equals(this.qb.getPreferredPublicKey())) {
                        b9 = true;
                    }
                }
                this.mc.init(this, b9);
                this.mc.performClientExchange(this.zb, this.rb, this.lc, this.bb);
                final String c9 = this.c(this.qb.supportedPublicKeys().list(this.qb.getPreferredPublicKey()), b2);
                this.z = (SshPublicKey)this.qb.supportedPublicKeys().getInstance(c9);
                if (!this.mb || !Arrayequals(this.mc.getHostKey(), "".getBytes())) {
                    EventServiceImplementation.getInstance().fireEvent(new Event(this, 0, true).addAttribute("HOST_KEY", new String(this.mc.getHostKey())));
                    this.z.init(this.mc.getHostKey(), 0, this.mc.getHostKey().length);
                    if (this.qb.getHostKeyVerification() != null) {
                        if (!this.qb.getHostKeyVerification().verifyHost(this.lb.getHost(), this.z)) {
                            EventServiceImplementation.getInstance().fireEvent(new Event(this, 1, false));
                            this.disconnect(9, "Host key not accepted");
                            throw new SshException("The host key was not accepted", 8);
                        }
                        if (!this.z.verifySignature(this.mc.getSignature(), this.mc.getExchangeHash())) {
                            EventServiceImplementation.getInstance().fireEvent(new Event(this, 1, false));
                            this.disconnect(9, "Invalid host key signature");
                            throw new SshException("The host key signature is invalid", 3);
                        }
                        EventServiceImplementation.getInstance().fireEvent(new Event(this, 2, true));
                    }
                }
                if (this.nc == null) {
                    this.nc = this.mc.getExchangeHash();
                }
                this.sendMessage(new byte[] { 21 }, true);
                cc.init(0, this.b('A'), this.b('C'));
                this.w = cc.getBlockSize();
                fb.init(this.b('E'));
                this.hb = fb.getMacLength();
                this.cc = cc;
                this.fb = fb;
                this.kc = kc;
                do {
                    j = this.j();
                    if (!this.processMessage(j)) {
                        EventServiceImplementation.getInstance().fireEvent(new Event(this, 4, true));
                        this.disconnect(2, "Invalid message received");
                        throw new SshException("Invalid message received during key exchange", 3);
                    }
                } while (j[0] != 21);
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 5, true).addAttribute("USING_PUBLICKEY", c9).addAttribute("USING_KEY_EXCHANGE", c7).addAttribute("USING_CS_CIPHER", c).addAttribute("USING_SC_CIPHERC", c2).addAttribute("USING_CS_MAC", c3).addAttribute("USING_SC_MAC", c4).addAttribute("USING_CS_COMPRESSION", c5).addAttribute("USING_SC_COMPRESSION", c6));
                xb.init(1, this.b('B'), this.b('D'));
                this.vb = xb.getBlockSize();
                tb.init(this.b('F'));
                this.jc = tb.getMacLength();
                this.xb = xb;
                this.tb = tb;
                this.nb = nb;
                if (nb != null && !nb.getAlgorithm().equals("zlib@openssh.com")) {
                    this.kb = true;
                }
                if (kc != null && !kc.getAlgorithm().equals("zlib@openssh.com")) {
                    this.hc = true;
                }
                synchronized (this.ac) {
                    this.pb = 3;
                    final Enumeration elements = this.ac.elements();
                    while (elements.hasMoreElements()) {
                        this.sendMessage((byte[])elements.nextElement(), true);
                    }
                    this.ac.removeAllElements();
                }
                this.lc = null;
                this.bb = null;
            }
        }
        catch (final IOException ex) {
            EventServiceImplementation.getInstance().fireEvent(new Event(this, 4, true));
            throw new SshException(ex, 5);
        }
        catch (final SshException ex2) {
            EventServiceImplementation.getInstance().fireEvent(new Event(this, 4, true));
            throw ex2;
        }
    }
    
    void i() {
        if (this.nb != null && this.nb.getAlgorithm().equals("zlib@openssh.com")) {
            this.kb = true;
        }
        if (this.kc != null && this.kc.getAlgorithm().equals("zlib@openssh.com")) {
            this.hc = true;
        }
    }
    
    public void startService(final String s) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.write(5);
            byteArrayWriter.writeString(s);
            this.sendMessage(byteArrayWriter.toByteArray(), true);
            byte[] j;
            do {
                j = this.j();
            } while (this.processMessage(j) || j[0] != 6);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    void b(final String s, final int n) {
        this.pb = 4;
        try {
            this.lb.close();
        }
        catch (final IOException ex) {}
        final Enumeration elements = this.dc.elements();
        while (elements.hasMoreElements()) {
            final TransportProtocolListener transportProtocolListener = (TransportProtocolListener)elements.nextElement();
            try {
                transportProtocolListener.onDisconnect(s, n);
            }
            catch (final Throwable t) {}
        }
        for (int i = 0; i < this.y.size(); ++i) {
            try {
                ((Runnable)this.y.elementAt(i)).run();
            }
            catch (final Throwable t2) {}
        }
    }
    
    void k() {
        this.pb = 4;
        try {
            this.lb.close();
        }
        catch (final IOException ex) {}
        for (int i = 0; i < this.y.size(); ++i) {
            try {
                ((Runnable)this.y.elementAt(i)).run();
            }
            catch (final Throwable t) {}
        }
    }
    
    void b(final Runnable runnable) {
        if (runnable != null) {
            this.y.addElement(runnable);
        }
    }
    
    public boolean processMessage(final byte[] array) throws SshException {
        try {
            if (array.length < 1) {
                this.disconnect(2, "Invalid message received");
                throw new SshException("Invalid transport protocol message", 5);
            }
            switch (array[0]) {
                case 1: {
                    this.k();
                    final ByteArrayReader byteArrayReader = new ByteArrayReader(array, 5, array.length - 5);
                    EventServiceImplementation.getInstance().fireEvent(new Event(this, 21, true));
                    throw new SshException(byteArrayReader.readString(), 2);
                }
                case 2: {
                    return true;
                }
                case 4: {
                    this.ob = System.currentTimeMillis();
                    return true;
                }
                case 21: {
                    this.ob = System.currentTimeMillis();
                    return true;
                }
                case 20: {
                    this.ob = System.currentTimeMillis();
                    if (this.bb != null) {
                        this.disconnect(2, "Key exchange already in progress!");
                        throw new SshException("Key exchange already in progress!", 3);
                    }
                    this.e(array);
                    return true;
                }
                default: {
                    this.ob = System.currentTimeMillis();
                    return false;
                }
            }
        }
        catch (final IOException ex) {
            throw new SshException(ex.getMessage(), 5);
        }
    }
    
    boolean d(final int n) {
        switch (n) {
            case 1:
            case 2:
            case 4:
            case 20:
            case 21: {
                return true;
            }
            default: {
                return this.mc != null && this.mc.isKeyExchangeMessage(n);
            }
        }
    }
    
    String c(final String s, final String s2) throws SshException {
        String trim = s2;
        String trim2 = s;
        final Vector vector = new Vector();
        int index;
        while ((index = trim.indexOf(",")) > -1) {
            vector.addElement(trim.substring(0, index).trim());
            trim = trim.substring(index + 1).trim();
        }
        vector.addElement(trim.trim());
        int index2;
        while ((index2 = trim2.indexOf(",")) > -1) {
            final String trim3 = trim2.substring(0, index2).trim();
            if (vector.contains(trim3)) {
                return trim3;
            }
            trim2 = trim2.substring(index2 + 1).trim();
        }
        if (vector.contains(trim2)) {
            return trim2;
        }
        EventServiceImplementation.getInstance().fireEvent(new Event(this, 32, true).addAttribute("LOCAL_COMPONENT_LIST", s).addAttribute("REMOTE_COMPONENT_LIST", s2));
        throw new SshException("Failed to negotiate a transport component [" + s + "] [" + s2 + "]", 9);
    }
    
    void b(final boolean b) throws SshException {
        try {
            this.eb = 0;
            this.yb = 0;
            this.wb = 0;
            this.ub = 0;
            this.pb = 2;
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            final byte[] array = new byte[16];
            ComponentManager.getInstance().getRND().nextBytes(array);
            byteArrayWriter.write(20);
            byteArrayWriter.write(array);
            byteArrayWriter.writeString(this.qb.supportedKeyExchanges().list(this.qb.getPreferredKeyExchange()));
            byteArrayWriter.writeString(this.qb.supportedPublicKeys().list(this.qb.getPreferredPublicKey()));
            byteArrayWriter.writeString(this.qb.supportedCiphersCS().list(this.qb.getPreferredCipherCS()));
            byteArrayWriter.writeString(this.qb.supportedCiphersSC().list(this.qb.getPreferredCipherSC()));
            byteArrayWriter.writeString(this.qb.supportedMacsCS().list(this.qb.getPreferredMacCS()));
            byteArrayWriter.writeString(this.qb.supportedMacsSC().list(this.qb.getPreferredMacSC()));
            byteArrayWriter.writeString(this.qb.supportedCompressionsCS().list(this.qb.getPreferredCompressionCS()));
            byteArrayWriter.writeString(this.qb.supportedCompressionsSC().list(this.qb.getPreferredCompressionSC()));
            byteArrayWriter.writeString("");
            byteArrayWriter.writeString("");
            byteArrayWriter.writeBoolean(b);
            byteArrayWriter.writeInt(0);
            this.sendMessage(this.lc = byteArrayWriter.toByteArray(), true);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    byte[] b(final char c) throws IOException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            final byte[] array = new byte[20];
            final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance(this.mc.getHashAlgorithm());
            digest.putBigInteger(this.mc.getSecret());
            digest.putBytes(this.mc.getExchangeHash());
            digest.putByte((byte)c);
            digest.putBytes(this.nc);
            final byte[] doFinal = digest.doFinal();
            byteArrayWriter.write(doFinal);
            digest.reset();
            digest.putBigInteger(this.mc.getSecret());
            digest.putBytes(this.mc.getExchangeHash());
            digest.putBytes(doFinal);
            byteArrayWriter.write(digest.doFinal());
            return byteArrayWriter.toByteArray();
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    private String b(final String s, final String s2) throws IOException {
        if (s2.trim().equals("")) {
            throw new IOException("Server sent invalid " + s + " value '" + s2 + "'");
        }
        if (!new StringTokenizer(s2, ",").hasMoreElements()) {
            throw new IOException("Server sent invalid " + s + " value '" + s2 + "'");
        }
        return s2;
    }
    
    static {
        TransportProtocol.CHARSET_ENCODING = "UTF8";
    }
}

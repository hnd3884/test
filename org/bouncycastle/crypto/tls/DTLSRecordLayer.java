package org.bouncycastle.crypto.tls;

import java.io.IOException;

class DTLSRecordLayer implements DatagramTransport
{
    private static final int RECORD_HEADER_LENGTH = 13;
    private static final int MAX_FRAGMENT_LENGTH = 16384;
    private static final long TCP_MSL = 120000L;
    private static final long RETRANSMIT_TIMEOUT = 240000L;
    private final DatagramTransport transport;
    private final TlsContext context;
    private final TlsPeer peer;
    private final ByteQueue recordQueue;
    private volatile boolean closed;
    private volatile boolean failed;
    private volatile ProtocolVersion readVersion;
    private volatile ProtocolVersion writeVersion;
    private volatile boolean inHandshake;
    private volatile int plaintextLimit;
    private DTLSEpoch currentEpoch;
    private DTLSEpoch pendingEpoch;
    private DTLSEpoch readEpoch;
    private DTLSEpoch writeEpoch;
    private DTLSHandshakeRetransmit retransmit;
    private DTLSEpoch retransmitEpoch;
    private long retransmitExpiry;
    
    DTLSRecordLayer(final DatagramTransport transport, final TlsContext context, final TlsPeer peer, final short n) {
        this.recordQueue = new ByteQueue();
        this.closed = false;
        this.failed = false;
        this.readVersion = null;
        this.writeVersion = null;
        this.retransmit = null;
        this.retransmitEpoch = null;
        this.retransmitExpiry = 0L;
        this.transport = transport;
        this.context = context;
        this.peer = peer;
        this.inHandshake = true;
        this.currentEpoch = new DTLSEpoch(0, new TlsNullCipher(context));
        this.pendingEpoch = null;
        this.readEpoch = this.currentEpoch;
        this.writeEpoch = this.currentEpoch;
        this.setPlaintextLimit(16384);
    }
    
    void setPlaintextLimit(final int plaintextLimit) {
        this.plaintextLimit = plaintextLimit;
    }
    
    int getReadEpoch() {
        return this.readEpoch.getEpoch();
    }
    
    ProtocolVersion getReadVersion() {
        return this.readVersion;
    }
    
    void setReadVersion(final ProtocolVersion readVersion) {
        this.readVersion = readVersion;
    }
    
    void setWriteVersion(final ProtocolVersion writeVersion) {
        this.writeVersion = writeVersion;
    }
    
    void initPendingEpoch(final TlsCipher tlsCipher) {
        if (this.pendingEpoch != null) {
            throw new IllegalStateException();
        }
        this.pendingEpoch = new DTLSEpoch(this.writeEpoch.getEpoch() + 1, tlsCipher);
    }
    
    void handshakeSuccessful(final DTLSHandshakeRetransmit retransmit) {
        if (this.readEpoch == this.currentEpoch || this.writeEpoch == this.currentEpoch) {
            throw new IllegalStateException();
        }
        if (retransmit != null) {
            this.retransmit = retransmit;
            this.retransmitEpoch = this.currentEpoch;
            this.retransmitExpiry = System.currentTimeMillis() + 240000L;
        }
        this.inHandshake = false;
        this.currentEpoch = this.pendingEpoch;
        this.pendingEpoch = null;
    }
    
    void resetWriteEpoch() {
        if (this.retransmitEpoch != null) {
            this.writeEpoch = this.retransmitEpoch;
        }
        else {
            this.writeEpoch = this.currentEpoch;
        }
    }
    
    public int getReceiveLimit() throws IOException {
        return Math.min(this.plaintextLimit, this.readEpoch.getCipher().getPlaintextLimit(this.transport.getReceiveLimit() - 13));
    }
    
    public int getSendLimit() throws IOException {
        return Math.min(this.plaintextLimit, this.writeEpoch.getCipher().getPlaintextLimit(this.transport.getSendLimit() - 13));
    }
    
    public int receive(final byte[] array, final int n, final int n2, final int n3) throws IOException {
        byte[] array2 = null;
        while (true) {
            final int n4 = Math.min(n2, this.getReceiveLimit()) + 13;
            if (array2 == null || array2.length < n4) {
                array2 = new byte[n4];
            }
            try {
                if (this.retransmit != null && System.currentTimeMillis() > this.retransmitExpiry) {
                    this.retransmit = null;
                    this.retransmitEpoch = null;
                }
                final int receiveRecord = this.receiveRecord(array2, 0, n4, n3);
                if (receiveRecord < 0) {
                    return receiveRecord;
                }
                if (receiveRecord < 13) {
                    continue;
                }
                if (receiveRecord != TlsUtils.readUint16(array2, 11) + 13) {
                    continue;
                }
                final short uint8 = TlsUtils.readUint8(array2, 0);
                switch (uint8) {
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24: {
                        final int uint9 = TlsUtils.readUint16(array2, 3);
                        DTLSEpoch dtlsEpoch = null;
                        if (uint9 == this.readEpoch.getEpoch()) {
                            dtlsEpoch = this.readEpoch;
                        }
                        else if (uint8 == 22 && this.retransmitEpoch != null && uint9 == this.retransmitEpoch.getEpoch()) {
                            dtlsEpoch = this.retransmitEpoch;
                        }
                        if (dtlsEpoch == null) {
                            continue;
                        }
                        final long uint10 = TlsUtils.readUint48(array2, 5);
                        if (dtlsEpoch.getReplayWindow().shouldDiscard(uint10)) {
                            continue;
                        }
                        final ProtocolVersion version = TlsUtils.readVersion(array2, 1);
                        if (!version.isDTLS()) {
                            continue;
                        }
                        if (this.readVersion != null && !this.readVersion.equals(version)) {
                            continue;
                        }
                        final byte[] decodeCiphertext = dtlsEpoch.getCipher().decodeCiphertext(getMacSequenceNumber(dtlsEpoch.getEpoch(), uint10), uint8, array2, 13, receiveRecord - 13);
                        dtlsEpoch.getReplayWindow().reportAuthenticated(uint10);
                        if (decodeCiphertext.length > this.plaintextLimit) {
                            continue;
                        }
                        if (this.readVersion == null) {
                            this.readVersion = version;
                        }
                        switch (uint8) {
                            case 21: {
                                if (decodeCiphertext.length != 2) {
                                    continue;
                                }
                                final short n5 = decodeCiphertext[0];
                                final short n6 = decodeCiphertext[1];
                                this.peer.notifyAlertReceived(n5, n6);
                                if (n5 == 2) {
                                    this.failed();
                                    throw new TlsFatalAlert(n6);
                                }
                                if (n6 != 0) {
                                    continue;
                                }
                                this.closeTransport();
                                continue;
                            }
                            case 23: {
                                if (this.inHandshake) {
                                    continue;
                                }
                                break;
                            }
                            case 20: {
                                for (int i = 0; i < decodeCiphertext.length; ++i) {
                                    if (TlsUtils.readUint8(decodeCiphertext, i) == 1) {
                                        if (this.pendingEpoch != null) {
                                            this.readEpoch = this.pendingEpoch;
                                        }
                                    }
                                }
                                continue;
                            }
                            case 22: {
                                if (!this.inHandshake) {
                                    if (this.retransmit == null) {
                                        continue;
                                    }
                                    this.retransmit.receivedHandshakeRecord(uint9, decodeCiphertext, 0, decodeCiphertext.length);
                                    continue;
                                }
                                break;
                            }
                            case 24: {
                                continue;
                            }
                        }
                        if (!this.inHandshake && this.retransmit != null) {
                            this.retransmit = null;
                            this.retransmitEpoch = null;
                        }
                        System.arraycopy(decodeCiphertext, 0, array, n, decodeCiphertext.length);
                        return decodeCiphertext.length;
                    }
                    default: {
                        continue;
                    }
                }
            }
            catch (final IOException ex) {
                throw ex;
            }
        }
    }
    
    public void send(final byte[] array, final int n, final int n2) throws IOException {
        short n3 = 23;
        if (this.inHandshake || this.writeEpoch == this.retransmitEpoch) {
            n3 = 22;
            if (TlsUtils.readUint8(array, n) == 20) {
                DTLSEpoch writeEpoch = null;
                if (this.inHandshake) {
                    writeEpoch = this.pendingEpoch;
                }
                else if (this.writeEpoch == this.retransmitEpoch) {
                    writeEpoch = this.currentEpoch;
                }
                if (writeEpoch == null) {
                    throw new IllegalStateException();
                }
                final byte[] array2 = { 1 };
                this.sendRecord((short)20, array2, 0, array2.length);
                this.writeEpoch = writeEpoch;
            }
        }
        this.sendRecord(n3, array, n, n2);
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            if (this.inHandshake) {
                this.warn((short)90, "User canceled handshake");
            }
            this.closeTransport();
        }
    }
    
    void fail(final short n) {
        if (!this.closed) {
            try {
                this.raiseAlert((short)2, n, null, null);
            }
            catch (final Exception ex) {}
            this.failed = true;
            this.closeTransport();
        }
    }
    
    void failed() {
        if (!this.closed) {
            this.failed = true;
            this.closeTransport();
        }
    }
    
    void warn(final short n, final String s) throws IOException {
        this.raiseAlert((short)1, n, s, null);
    }
    
    private void closeTransport() {
        if (!this.closed) {
            try {
                if (!this.failed) {
                    this.warn((short)0, null);
                }
                this.transport.close();
            }
            catch (final Exception ex) {}
            this.closed = true;
        }
    }
    
    private void raiseAlert(final short n, final short n2, final String s, final Throwable t) throws IOException {
        this.peer.notifyAlertRaised(n, n2, s, t);
        this.sendRecord((short)21, new byte[] { (byte)n, (byte)n2 }, 0, 2);
    }
    
    private int receiveRecord(final byte[] array, final int n, final int n2, final int n3) throws IOException {
        if (this.recordQueue.available() > 0) {
            int uint16 = 0;
            if (this.recordQueue.available() >= 13) {
                final byte[] array2 = new byte[2];
                this.recordQueue.read(array2, 0, 2, 11);
                uint16 = TlsUtils.readUint16(array2, 0);
            }
            final int min = Math.min(this.recordQueue.available(), 13 + uint16);
            this.recordQueue.removeData(array, n, min, 0);
            return min;
        }
        int receive = this.transport.receive(array, n, n2, n3);
        if (receive >= 13) {
            final int n4 = 13 + TlsUtils.readUint16(array, n + 11);
            if (receive > n4) {
                this.recordQueue.addData(array, n + n4, receive - n4);
                receive = n4;
            }
        }
        return receive;
    }
    
    private void sendRecord(final short n, final byte[] array, final int n2, final int n3) throws IOException {
        if (this.writeVersion == null) {
            return;
        }
        if (n3 > this.plaintextLimit) {
            throw new TlsFatalAlert((short)80);
        }
        if (n3 < 1 && n != 23) {
            throw new TlsFatalAlert((short)80);
        }
        final int epoch = this.writeEpoch.getEpoch();
        final long allocateSequenceNumber = this.writeEpoch.allocateSequenceNumber();
        final byte[] encodePlaintext = this.writeEpoch.getCipher().encodePlaintext(getMacSequenceNumber(epoch, allocateSequenceNumber), n, array, n2, n3);
        final byte[] array2 = new byte[encodePlaintext.length + 13];
        TlsUtils.writeUint8(n, array2, 0);
        TlsUtils.writeVersion(this.writeVersion, array2, 1);
        TlsUtils.writeUint16(epoch, array2, 3);
        TlsUtils.writeUint48(allocateSequenceNumber, array2, 5);
        TlsUtils.writeUint16(encodePlaintext.length, array2, 11);
        System.arraycopy(encodePlaintext, 0, array2, 13, encodePlaintext.length);
        this.transport.send(array2, 0, array2.length);
    }
    
    private static long getMacSequenceNumber(final int n, final long n2) {
        return ((long)n & 0xFFFFFFFFL) << 48 | n2;
    }
}

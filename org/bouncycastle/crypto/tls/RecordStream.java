package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.util.io.SimpleOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

class RecordStream
{
    private static int DEFAULT_PLAINTEXT_LIMIT;
    static final int TLS_HEADER_SIZE = 5;
    static final int TLS_HEADER_TYPE_OFFSET = 0;
    static final int TLS_HEADER_VERSION_OFFSET = 1;
    static final int TLS_HEADER_LENGTH_OFFSET = 3;
    private TlsProtocol handler;
    private InputStream input;
    private OutputStream output;
    private TlsCompression pendingCompression;
    private TlsCompression readCompression;
    private TlsCompression writeCompression;
    private TlsCipher pendingCipher;
    private TlsCipher readCipher;
    private TlsCipher writeCipher;
    private SequenceNumber readSeqNo;
    private SequenceNumber writeSeqNo;
    private ByteArrayOutputStream buffer;
    private TlsHandshakeHash handshakeHash;
    private SimpleOutputStream handshakeHashUpdater;
    private ProtocolVersion readVersion;
    private ProtocolVersion writeVersion;
    private boolean restrictReadVersion;
    private int plaintextLimit;
    private int compressedLimit;
    private int ciphertextLimit;
    
    RecordStream(final TlsProtocol handler, final InputStream input, final OutputStream output) {
        this.pendingCompression = null;
        this.readCompression = null;
        this.writeCompression = null;
        this.pendingCipher = null;
        this.readCipher = null;
        this.writeCipher = null;
        this.readSeqNo = new SequenceNumber();
        this.writeSeqNo = new SequenceNumber();
        this.buffer = new ByteArrayOutputStream();
        this.handshakeHash = null;
        this.handshakeHashUpdater = new SimpleOutputStream() {
            @Override
            public void write(final byte[] array, final int n, final int n2) throws IOException {
                RecordStream.this.handshakeHash.update(array, n, n2);
            }
        };
        this.readVersion = null;
        this.writeVersion = null;
        this.restrictReadVersion = true;
        this.handler = handler;
        this.input = input;
        this.output = output;
        this.readCompression = new TlsNullCompression();
        this.writeCompression = this.readCompression;
    }
    
    void init(final TlsContext tlsContext) {
        this.readCipher = new TlsNullCipher(tlsContext);
        this.writeCipher = this.readCipher;
        (this.handshakeHash = new DeferredHash()).init(tlsContext);
        this.setPlaintextLimit(RecordStream.DEFAULT_PLAINTEXT_LIMIT);
    }
    
    int getPlaintextLimit() {
        return this.plaintextLimit;
    }
    
    void setPlaintextLimit(final int plaintextLimit) {
        this.plaintextLimit = plaintextLimit;
        this.compressedLimit = this.plaintextLimit + 1024;
        this.ciphertextLimit = this.compressedLimit + 1024;
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
    
    void setRestrictReadVersion(final boolean restrictReadVersion) {
        this.restrictReadVersion = restrictReadVersion;
    }
    
    void setPendingConnectionState(final TlsCompression pendingCompression, final TlsCipher pendingCipher) {
        this.pendingCompression = pendingCompression;
        this.pendingCipher = pendingCipher;
    }
    
    void sentWriteCipherSpec() throws IOException {
        if (this.pendingCompression == null || this.pendingCipher == null) {
            throw new TlsFatalAlert((short)40);
        }
        this.writeCompression = this.pendingCompression;
        this.writeCipher = this.pendingCipher;
        this.writeSeqNo = new SequenceNumber();
    }
    
    void receivedReadCipherSpec() throws IOException {
        if (this.pendingCompression == null || this.pendingCipher == null) {
            throw new TlsFatalAlert((short)40);
        }
        this.readCompression = this.pendingCompression;
        this.readCipher = this.pendingCipher;
        this.readSeqNo = new SequenceNumber();
    }
    
    void finaliseHandshake() throws IOException {
        if (this.readCompression != this.pendingCompression || this.writeCompression != this.pendingCompression || this.readCipher != this.pendingCipher || this.writeCipher != this.pendingCipher) {
            throw new TlsFatalAlert((short)40);
        }
        this.pendingCompression = null;
        this.pendingCipher = null;
    }
    
    void checkRecordHeader(final byte[] array) throws IOException {
        checkType(TlsUtils.readUint8(array, 0), (short)10);
        if (!this.restrictReadVersion) {
            if ((TlsUtils.readVersionRaw(array, 1) & 0xFFFFFF00) != 0x300) {
                throw new TlsFatalAlert((short)47);
            }
        }
        else {
            final ProtocolVersion version = TlsUtils.readVersion(array, 1);
            if (this.readVersion != null) {
                if (!version.equals(this.readVersion)) {
                    throw new TlsFatalAlert((short)47);
                }
            }
        }
        checkLength(TlsUtils.readUint16(array, 3), this.ciphertextLimit, (short)22);
    }
    
    boolean readRecord() throws IOException {
        final byte[] allOrNothing = TlsUtils.readAllOrNothing(5, this.input);
        if (allOrNothing == null) {
            return false;
        }
        final short uint8 = TlsUtils.readUint8(allOrNothing, 0);
        checkType(uint8, (short)10);
        if (!this.restrictReadVersion) {
            if ((TlsUtils.readVersionRaw(allOrNothing, 1) & 0xFFFFFF00) != 0x300) {
                throw new TlsFatalAlert((short)47);
            }
        }
        else {
            final ProtocolVersion version = TlsUtils.readVersion(allOrNothing, 1);
            if (this.readVersion == null) {
                this.readVersion = version;
            }
            else if (!version.equals(this.readVersion)) {
                throw new TlsFatalAlert((short)47);
            }
        }
        final int uint9 = TlsUtils.readUint16(allOrNothing, 3);
        checkLength(uint9, this.ciphertextLimit, (short)22);
        final byte[] decodeAndVerify = this.decodeAndVerify(uint8, this.input, uint9);
        this.handler.processRecord(uint8, decodeAndVerify, 0, decodeAndVerify.length);
        return true;
    }
    
    byte[] decodeAndVerify(final short n, final InputStream inputStream, final int n2) throws IOException {
        final byte[] fully = TlsUtils.readFully(n2, inputStream);
        byte[] array = this.readCipher.decodeCiphertext(this.readSeqNo.nextValue((short)10), n, fully, 0, fully.length);
        checkLength(array.length, this.compressedLimit, (short)22);
        final OutputStream decompress = this.readCompression.decompress(this.buffer);
        if (decompress != this.buffer) {
            decompress.write(array, 0, array.length);
            decompress.flush();
            array = this.getBufferContents();
        }
        checkLength(array.length, this.plaintextLimit, (short)30);
        if (array.length < 1 && n != 23) {
            throw new TlsFatalAlert((short)47);
        }
        return array;
    }
    
    void writeRecord(final short n, final byte[] array, final int n2, final int n3) throws IOException {
        if (this.writeVersion == null) {
            return;
        }
        checkType(n, (short)80);
        checkLength(n3, this.plaintextLimit, (short)80);
        if (n3 < 1 && n != 23) {
            throw new TlsFatalAlert((short)80);
        }
        final OutputStream compress = this.writeCompression.compress(this.buffer);
        final long nextValue = this.writeSeqNo.nextValue((short)80);
        byte[] array2;
        if (compress == this.buffer) {
            array2 = this.writeCipher.encodePlaintext(nextValue, n, array, n2, n3);
        }
        else {
            compress.write(array, n2, n3);
            compress.flush();
            final byte[] bufferContents = this.getBufferContents();
            checkLength(bufferContents.length, n3 + 1024, (short)80);
            array2 = this.writeCipher.encodePlaintext(nextValue, n, bufferContents, 0, bufferContents.length);
        }
        checkLength(array2.length, this.ciphertextLimit, (short)80);
        final byte[] array3 = new byte[array2.length + 5];
        TlsUtils.writeUint8(n, array3, 0);
        TlsUtils.writeVersion(this.writeVersion, array3, 1);
        TlsUtils.writeUint16(array2.length, array3, 3);
        System.arraycopy(array2, 0, array3, 5, array2.length);
        this.output.write(array3);
        this.output.flush();
    }
    
    void notifyHelloComplete() {
        this.handshakeHash = this.handshakeHash.notifyPRFDetermined();
    }
    
    TlsHandshakeHash getHandshakeHash() {
        return this.handshakeHash;
    }
    
    OutputStream getHandshakeHashUpdater() {
        return this.handshakeHashUpdater;
    }
    
    TlsHandshakeHash prepareToFinish() {
        final TlsHandshakeHash handshakeHash = this.handshakeHash;
        this.handshakeHash = this.handshakeHash.stopTracking();
        return handshakeHash;
    }
    
    void safeClose() {
        try {
            this.input.close();
        }
        catch (final IOException ex) {}
        try {
            this.output.close();
        }
        catch (final IOException ex2) {}
    }
    
    void flush() throws IOException {
        this.output.flush();
    }
    
    private byte[] getBufferContents() {
        final byte[] byteArray = this.buffer.toByteArray();
        this.buffer.reset();
        return byteArray;
    }
    
    private static void checkType(final short n, final short n2) throws IOException {
        switch (n) {
            case 20:
            case 21:
            case 22:
            case 23: {
                return;
            }
            default: {
                throw new TlsFatalAlert(n2);
            }
        }
    }
    
    private static void checkLength(final int n, final int n2, final short n3) throws IOException {
        if (n > n2) {
            throw new TlsFatalAlert(n3);
        }
    }
    
    static {
        RecordStream.DEFAULT_PLAINTEXT_LIMIT = 16384;
    }
    
    private static class SequenceNumber
    {
        private long value;
        private boolean exhausted;
        
        private SequenceNumber() {
            this.value = 0L;
            this.exhausted = false;
        }
        
        synchronized long nextValue(final short n) throws TlsFatalAlert {
            if (this.exhausted) {
                throw new TlsFatalAlert(n);
            }
            final long value = this.value;
            final long value2 = this.value + 1L;
            this.value = value2;
            if (value2 == 0L) {
                this.exhausted = true;
            }
            return value;
        }
    }
}

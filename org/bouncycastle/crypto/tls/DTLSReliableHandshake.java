package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.bouncycastle.util.Integers;
import java.util.Enumeration;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;

class DTLSReliableHandshake
{
    private static final int MAX_RECEIVE_AHEAD = 16;
    private static final int MESSAGE_HEADER_LENGTH = 12;
    private DTLSRecordLayer recordLayer;
    private TlsHandshakeHash handshakeHash;
    private Hashtable currentInboundFlight;
    private Hashtable previousInboundFlight;
    private Vector outboundFlight;
    private boolean sending;
    private int message_seq;
    private int next_receive_seq;
    
    DTLSReliableHandshake(final TlsContext tlsContext, final DTLSRecordLayer recordLayer) {
        this.currentInboundFlight = new Hashtable();
        this.previousInboundFlight = null;
        this.outboundFlight = new Vector();
        this.sending = true;
        this.message_seq = 0;
        this.next_receive_seq = 0;
        this.recordLayer = recordLayer;
        (this.handshakeHash = new DeferredHash()).init(tlsContext);
    }
    
    void notifyHelloComplete() {
        this.handshakeHash = this.handshakeHash.notifyPRFDetermined();
    }
    
    TlsHandshakeHash getHandshakeHash() {
        return this.handshakeHash;
    }
    
    TlsHandshakeHash prepareToFinish() {
        final TlsHandshakeHash handshakeHash = this.handshakeHash;
        this.handshakeHash = this.handshakeHash.stopTracking();
        return handshakeHash;
    }
    
    void sendMessage(final short n, final byte[] array) throws IOException {
        TlsUtils.checkUint24(array.length);
        if (!this.sending) {
            this.checkInboundFlight();
            this.sending = true;
            this.outboundFlight.removeAllElements();
        }
        final Message message = new Message(this.message_seq++, n, array);
        this.outboundFlight.addElement(message);
        this.writeMessage(message);
        this.updateHandshakeMessagesDigest(message);
    }
    
    byte[] receiveMessageBody(final short n) throws IOException {
        final Message receiveMessage = this.receiveMessage();
        if (receiveMessage.getType() != n) {
            throw new TlsFatalAlert((short)10);
        }
        return receiveMessage.getBody();
    }
    
    Message receiveMessage() throws IOException {
        if (this.sending) {
            this.sending = false;
            this.prepareInboundFlight(new Hashtable());
        }
        byte[] array = null;
        int n = 1000;
    Label_0029_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        final Message pendingMessage = this.getPendingMessage();
                        if (pendingMessage != null) {
                            return pendingMessage;
                        }
                        final int receiveLimit = this.recordLayer.getReceiveLimit();
                        if (array == null || array.length < receiveLimit) {
                            array = new byte[receiveLimit];
                        }
                        final int receive = this.recordLayer.receive(array, 0, receiveLimit, n);
                        if (receive < 0) {
                            break;
                        }
                        if (!this.processRecord(16, this.recordLayer.getReadEpoch(), array, 0, receive)) {
                            continue Label_0029_Outer;
                        }
                        n = this.backOff(n);
                    }
                }
                catch (final IOException ex) {}
                this.resendOutboundFlight();
                n = this.backOff(n);
                continue;
            }
        }
    }
    
    void finish() {
        DTLSHandshakeRetransmit dtlsHandshakeRetransmit = null;
        if (!this.sending) {
            this.checkInboundFlight();
        }
        else {
            this.prepareInboundFlight(null);
            if (this.previousInboundFlight != null) {
                dtlsHandshakeRetransmit = new DTLSHandshakeRetransmit() {
                    public void receivedHandshakeRecord(final int n, final byte[] array, final int n2, final int n3) throws IOException {
                        DTLSReliableHandshake.this.processRecord(0, n, array, n2, n3);
                    }
                };
            }
        }
        this.recordLayer.handshakeSuccessful(dtlsHandshakeRetransmit);
    }
    
    void resetHandshakeMessagesDigest() {
        this.handshakeHash.reset();
    }
    
    private int backOff(final int n) {
        return Math.min(n * 2, 60000);
    }
    
    private void checkInboundFlight() {
        final Enumeration keys = this.currentInboundFlight.keys();
        while (keys.hasMoreElements()) {
            if ((int)keys.nextElement() >= this.next_receive_seq) {}
        }
    }
    
    private Message getPendingMessage() throws IOException {
        final DTLSReassembler dtlsReassembler = this.currentInboundFlight.get(Integers.valueOf(this.next_receive_seq));
        if (dtlsReassembler != null) {
            final byte[] bodyIfComplete = dtlsReassembler.getBodyIfComplete();
            if (bodyIfComplete != null) {
                this.previousInboundFlight = null;
                return this.updateHandshakeMessagesDigest(new Message(this.next_receive_seq++, dtlsReassembler.getMsgType(), bodyIfComplete));
            }
        }
        return null;
    }
    
    private void prepareInboundFlight(final Hashtable currentInboundFlight) {
        resetAll(this.currentInboundFlight);
        this.previousInboundFlight = this.currentInboundFlight;
        this.currentInboundFlight = currentInboundFlight;
    }
    
    private boolean processRecord(final int n, final int n2, final byte[] array, int n3, int i) throws IOException {
        boolean b = false;
        while (i >= 12) {
            final int uint24 = TlsUtils.readUint24(array, n3 + 9);
            final int n4 = uint24 + 12;
            if (i < n4) {
                break;
            }
            final int uint25 = TlsUtils.readUint24(array, n3 + 1);
            final int uint26 = TlsUtils.readUint24(array, n3 + 6);
            if (uint26 + uint24 > uint25) {
                break;
            }
            final short uint27 = TlsUtils.readUint8(array, n3 + 0);
            if (n2 != ((uint27 == 20) ? 1 : 0)) {
                break;
            }
            final int uint28 = TlsUtils.readUint16(array, n3 + 4);
            if (uint28 < this.next_receive_seq + n) {
                if (uint28 >= this.next_receive_seq) {
                    DTLSReassembler dtlsReassembler = this.currentInboundFlight.get(Integers.valueOf(uint28));
                    if (dtlsReassembler == null) {
                        dtlsReassembler = new DTLSReassembler(uint27, uint25);
                        this.currentInboundFlight.put(Integers.valueOf(uint28), dtlsReassembler);
                    }
                    dtlsReassembler.contributeFragment(uint27, uint25, array, n3 + 12, uint26, uint24);
                }
                else if (this.previousInboundFlight != null) {
                    final DTLSReassembler dtlsReassembler2 = this.previousInboundFlight.get(Integers.valueOf(uint28));
                    if (dtlsReassembler2 != null) {
                        dtlsReassembler2.contributeFragment(uint27, uint25, array, n3 + 12, uint26, uint24);
                        b = true;
                    }
                }
            }
            n3 += n4;
            i -= n4;
        }
        final boolean b2 = b && checkAll(this.previousInboundFlight);
        if (b2) {
            this.resendOutboundFlight();
            resetAll(this.previousInboundFlight);
        }
        return b2;
    }
    
    private void resendOutboundFlight() throws IOException {
        this.recordLayer.resetWriteEpoch();
        for (int i = 0; i < this.outboundFlight.size(); ++i) {
            this.writeMessage((Message)this.outboundFlight.elementAt(i));
        }
    }
    
    private Message updateHandshakeMessagesDigest(final Message message) throws IOException {
        if (message.getType() != 0) {
            final byte[] body = message.getBody();
            final byte[] array = new byte[12];
            TlsUtils.writeUint8(message.getType(), array, 0);
            TlsUtils.writeUint24(body.length, array, 1);
            TlsUtils.writeUint16(message.getSeq(), array, 4);
            TlsUtils.writeUint24(0, array, 6);
            TlsUtils.writeUint24(body.length, array, 9);
            this.handshakeHash.update(array, 0, array.length);
            this.handshakeHash.update(body, 0, body.length);
        }
        return message;
    }
    
    private void writeMessage(final Message message) throws IOException {
        final int n = this.recordLayer.getSendLimit() - 12;
        if (n < 1) {
            throw new TlsFatalAlert((short)80);
        }
        final int length = message.getBody().length;
        int i = 0;
        do {
            final int min = Math.min(length - i, n);
            this.writeHandshakeFragment(message, i, min);
            i += min;
        } while (i < length);
    }
    
    private void writeHandshakeFragment(final Message message, final int n, final int n2) throws IOException {
        final RecordLayerBuffer recordLayerBuffer = new RecordLayerBuffer(12 + n2);
        TlsUtils.writeUint8(message.getType(), recordLayerBuffer);
        TlsUtils.writeUint24(message.getBody().length, recordLayerBuffer);
        TlsUtils.writeUint16(message.getSeq(), recordLayerBuffer);
        TlsUtils.writeUint24(n, recordLayerBuffer);
        TlsUtils.writeUint24(n2, recordLayerBuffer);
        recordLayerBuffer.write(message.getBody(), n, n2);
        recordLayerBuffer.sendToRecordLayer(this.recordLayer);
    }
    
    private static boolean checkAll(final Hashtable hashtable) {
        final Enumeration elements = hashtable.elements();
        while (elements.hasMoreElements()) {
            if (((DTLSReassembler)elements.nextElement()).getBodyIfComplete() == null) {
                return false;
            }
        }
        return true;
    }
    
    private static void resetAll(final Hashtable hashtable) {
        final Enumeration elements = hashtable.elements();
        while (elements.hasMoreElements()) {
            ((DTLSReassembler)elements.nextElement()).reset();
        }
    }
    
    static class Message
    {
        private final int message_seq;
        private final short msg_type;
        private final byte[] body;
        
        private Message(final int message_seq, final short msg_type, final byte[] body) {
            this.message_seq = message_seq;
            this.msg_type = msg_type;
            this.body = body;
        }
        
        public int getSeq() {
            return this.message_seq;
        }
        
        public short getType() {
            return this.msg_type;
        }
        
        public byte[] getBody() {
            return this.body;
        }
    }
    
    static class RecordLayerBuffer extends ByteArrayOutputStream
    {
        RecordLayerBuffer(final int n) {
            super(n);
        }
        
        void sendToRecordLayer(final DTLSRecordLayer dtlsRecordLayer) throws IOException {
            dtlsRecordLayer.send(this.buf, 0, this.count);
            this.buf = null;
        }
    }
}

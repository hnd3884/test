package org.openjsse.sun.security.ssl;

import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import javax.net.ssl.SSLException;
import java.security.GeneralSecurityException;
import javax.crypto.BadPaddingException;
import java.nio.ByteBuffer;
import java.io.IOException;

final class DTLSInputRecord extends InputRecord implements DTLSRecord
{
    private DTLSReassembler reassembler;
    private int readEpoch;
    
    DTLSInputRecord(final HandshakeHash handshakeHash) {
        super(handshakeHash, SSLCipher.SSLReadCipher.nullDTlsReadCipher());
        this.reassembler = null;
        this.readEpoch = 0;
    }
    
    @Override
    void changeReadCiphers(final SSLCipher.SSLReadCipher readCipher) {
        this.readCipher = readCipher;
        ++this.readEpoch;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.isClosed) {
            super.close();
        }
    }
    
    @Override
    boolean isEmpty() {
        return this.reassembler == null || this.reassembler.isEmpty();
    }
    
    @Override
    int estimateFragmentSize(final int packetSize) {
        if (packetSize > 0) {
            return this.readCipher.estimateFragmentSize(packetSize, 13);
        }
        return 16384;
    }
    
    @Override
    void expectingFinishFlight() {
        if (this.reassembler != null) {
            this.reassembler.expectingFinishFlight();
        }
    }
    
    @Override
    void finishHandshake() {
        this.reassembler = null;
    }
    
    @Override
    Plaintext acquirePlaintext() {
        if (this.reassembler != null) {
            return this.reassembler.acquirePlaintext();
        }
        return null;
    }
    
    @Override
    Plaintext[] decode(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength) throws IOException, BadPaddingException {
        if (srcs == null || srcs.length == 0 || srcsLength == 0) {
            final Plaintext pt = this.acquirePlaintext();
            return (pt == null) ? new Plaintext[0] : new Plaintext[] { pt };
        }
        if (srcsLength == 1) {
            return this.decode(srcs[srcsOffset]);
        }
        final ByteBuffer packet = InputRecord.extract(srcs, srcsOffset, srcsLength, 13);
        return this.decode(packet);
    }
    
    Plaintext[] decode(final ByteBuffer packet) {
        if (this.isClosed) {
            return null;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            SSLLogger.fine("Raw read", packet);
        }
        final int srcPos = packet.position();
        final int srcLim = packet.limit();
        byte contentType = packet.get();
        final byte majorVersion = packet.get();
        final byte minorVersion = packet.get();
        final byte[] recordEnS = new byte[8];
        packet.get(recordEnS);
        final int recordEpoch = (recordEnS[0] & 0xFF) << 8 | (recordEnS[1] & 0xFF);
        final long recordSeq = ((long)recordEnS[2] & 0xFFL) << 40 | ((long)recordEnS[3] & 0xFFL) << 32 | ((long)recordEnS[4] & 0xFFL) << 24 | ((long)recordEnS[5] & 0xFFL) << 16 | ((long)recordEnS[6] & 0xFFL) << 8 | ((long)recordEnS[7] & 0xFFL);
        final int contentLen = (packet.get() & 0xFF) << 8 | (packet.get() & 0xFF);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("READ: " + ProtocolVersion.nameOf(majorVersion, minorVersion) + " " + ContentType.nameOf(contentType) + ", length = " + contentLen, new Object[0]);
        }
        final int recLim = Math.addExact(srcPos, 13 + contentLen);
        if (this.readEpoch > recordEpoch) {
            packet.position(recLim);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("READ: discard this old record", recordEnS);
            }
            return null;
        }
        if (this.readEpoch < recordEpoch) {
            if ((contentType != ContentType.HANDSHAKE.id && contentType != ContentType.CHANGE_CIPHER_SPEC.id) || (this.reassembler == null && contentType != ContentType.HANDSHAKE.id) || this.readEpoch < recordEpoch - 1) {
                packet.position(recLim);
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("Premature record (epoch), discard it.", new Object[0]);
                }
                return null;
            }
            final byte[] fragment = new byte[contentLen];
            packet.get(fragment);
            final RecordFragment buffered = new RecordFragment(fragment, contentType, majorVersion, minorVersion, recordEnS, recordEpoch, recordSeq, true);
            if (this.reassembler == null) {
                this.reassembler = new DTLSReassembler(recordEpoch);
            }
            this.reassembler.queueUpFragment(buffered);
            packet.position(recLim);
            final Plaintext pt = this.reassembler.acquirePlaintext();
            return (Plaintext[])((pt == null) ? null : new Plaintext[] { pt });
        }
        else {
            packet.limit(recLim);
            packet.position(srcPos + 13);
            ByteBuffer plaintextFragment;
            try {
                final Plaintext plaintext = this.readCipher.decrypt(contentType, packet, recordEnS);
                plaintextFragment = plaintext.fragment;
                contentType = plaintext.contentType;
            }
            catch (final GeneralSecurityException gse) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("Discard invalid record: " + gse, new Object[0]);
                }
                return null;
            }
            finally {
                packet.limit(srcLim);
                packet.position(recLim);
            }
            if (contentType != ContentType.CHANGE_CIPHER_SPEC.id && contentType != ContentType.HANDSHAKE.id) {
                if (this.reassembler != null && this.reassembler.handshakeEpoch < recordEpoch) {
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Cleanup the handshake reassembler", new Object[0]);
                    }
                    this.reassembler = null;
                }
                return new Plaintext[] { new Plaintext(contentType, majorVersion, minorVersion, recordEpoch, Authenticator.toLong(recordEnS), plaintextFragment) };
            }
            if (contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                if (this.reassembler == null) {
                    this.reassembler = new DTLSReassembler(recordEpoch);
                }
                this.reassembler.queueUpChangeCipherSpec(new RecordFragment(plaintextFragment, contentType, majorVersion, minorVersion, recordEnS, recordEpoch, recordSeq, false));
            }
            else {
                while (plaintextFragment.remaining() > 0) {
                    final HandshakeFragment hsFrag = parseHandshakeMessage(contentType, majorVersion, minorVersion, recordEnS, recordEpoch, recordSeq, plaintextFragment);
                    if (hsFrag == null) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("Invalid handshake message, discard it.", new Object[0]);
                        }
                        return null;
                    }
                    if (this.reassembler == null) {
                        this.reassembler = new DTLSReassembler(recordEpoch);
                    }
                    this.reassembler.queueUpHandshake(hsFrag);
                }
            }
            if (this.reassembler != null) {
                final Plaintext pt2 = this.reassembler.acquirePlaintext();
                return (Plaintext[])((pt2 == null) ? null : new Plaintext[] { pt2 });
            }
            if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                SSLLogger.fine("The reassembler is not initialized yet.", new Object[0]);
            }
            return null;
        }
    }
    
    @Override
    int bytesInCompletePacket(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength) throws IOException {
        return this.bytesInCompletePacket(srcs[srcsOffset]);
    }
    
    private int bytesInCompletePacket(final ByteBuffer packet) throws SSLException {
        if (packet.remaining() < 13) {
            return -1;
        }
        final int pos = packet.position();
        final byte contentType = packet.get(pos);
        if (ContentType.valueOf(contentType) == null) {
            throw new SSLException("Unrecognized SSL message, plaintext connection?");
        }
        final byte majorVersion = packet.get(pos + 1);
        final byte minorVersion = packet.get(pos + 2);
        if (!ProtocolVersion.isNegotiable(majorVersion, minorVersion, true, false)) {
            throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(majorVersion, minorVersion) + " , plaintext connection?");
        }
        final int fragLen = ((packet.get(pos + 11) & 0xFF) << 8) + (packet.get(pos + 12) & 0xFF) + 13;
        if (fragLen > 18432) {
            throw new SSLException("Record overflow, fragment length (" + fragLen + ") MUST not exceed " + 18432);
        }
        return fragLen;
    }
    
    private static HandshakeFragment parseHandshakeMessage(final byte contentType, final byte majorVersion, final byte minorVersion, final byte[] recordEnS, final int recordEpoch, final long recordSeq, final ByteBuffer plaintextFragment) {
        final int remaining = plaintextFragment.remaining();
        if (remaining < 12) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("Discard invalid record: too small record to hold a handshake fragment", new Object[0]);
            }
            return null;
        }
        final byte handshakeType = plaintextFragment.get();
        final int messageLength = (plaintextFragment.get() & 0xFF) << 16 | (plaintextFragment.get() & 0xFF) << 8 | (plaintextFragment.get() & 0xFF);
        final int messageSeq = (plaintextFragment.get() & 0xFF) << 8 | (plaintextFragment.get() & 0xFF);
        final int fragmentOffset = (plaintextFragment.get() & 0xFF) << 16 | (plaintextFragment.get() & 0xFF) << 8 | (plaintextFragment.get() & 0xFF);
        final int fragmentLength = (plaintextFragment.get() & 0xFF) << 16 | (plaintextFragment.get() & 0xFF) << 8 | (plaintextFragment.get() & 0xFF);
        if (remaining - 12 < fragmentLength) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("Discard invalid record: not a complete handshake fragment in the record", new Object[0]);
            }
            return null;
        }
        final byte[] fragment = new byte[fragmentLength];
        plaintextFragment.get(fragment);
        return new HandshakeFragment(fragment, contentType, majorVersion, minorVersion, recordEnS, recordEpoch, recordSeq, handshakeType, messageLength, messageSeq, fragmentOffset, fragmentLength);
    }
    
    private static class RecordFragment implements Comparable<RecordFragment>
    {
        boolean isCiphertext;
        byte contentType;
        byte majorVersion;
        byte minorVersion;
        int recordEpoch;
        long recordSeq;
        byte[] recordEnS;
        byte[] fragment;
        
        RecordFragment(final ByteBuffer fragBuf, final byte contentType, final byte majorVersion, final byte minorVersion, final byte[] recordEnS, final int recordEpoch, final long recordSeq, final boolean isCiphertext) {
            this((byte[])null, contentType, majorVersion, minorVersion, recordEnS, recordEpoch, recordSeq, isCiphertext);
            fragBuf.get(this.fragment = new byte[fragBuf.remaining()]);
        }
        
        RecordFragment(final byte[] fragment, final byte contentType, final byte majorVersion, final byte minorVersion, final byte[] recordEnS, final int recordEpoch, final long recordSeq, final boolean isCiphertext) {
            this.isCiphertext = isCiphertext;
            this.contentType = contentType;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
            this.recordEpoch = recordEpoch;
            this.recordSeq = recordSeq;
            this.recordEnS = recordEnS;
            this.fragment = fragment;
        }
        
        @Override
        public int compareTo(final RecordFragment o) {
            if (this.contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                if (o.contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                    return Integer.compare(this.recordEpoch, o.recordEpoch);
                }
                if (this.recordEpoch == o.recordEpoch && o.contentType == ContentType.HANDSHAKE.id) {
                    return 1;
                }
            }
            else if (o.contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                if (this.recordEpoch == o.recordEpoch && this.contentType == ContentType.HANDSHAKE.id) {
                    return -1;
                }
                return this.compareToSequence(o.recordEpoch, o.recordSeq);
            }
            return this.compareToSequence(o.recordEpoch, o.recordSeq);
        }
        
        int compareToSequence(final int epoch, final long seq) {
            if (this.recordEpoch > epoch) {
                return 1;
            }
            if (this.recordEpoch == epoch) {
                return Long.compare(this.recordSeq, seq);
            }
            return -1;
        }
    }
    
    private static final class HandshakeFragment extends RecordFragment
    {
        byte handshakeType;
        int messageSeq;
        int messageLength;
        int fragmentOffset;
        int fragmentLength;
        
        HandshakeFragment(final byte[] fragment, final byte contentType, final byte majorVersion, final byte minorVersion, final byte[] recordEnS, final int recordEpoch, final long recordSeq, final byte handshakeType, final int messageLength, final int messageSeq, final int fragmentOffset, final int fragmentLength) {
            super(fragment, contentType, majorVersion, minorVersion, recordEnS, recordEpoch, recordSeq, false);
            this.handshakeType = handshakeType;
            this.messageSeq = messageSeq;
            this.messageLength = messageLength;
            this.fragmentOffset = fragmentOffset;
            this.fragmentLength = fragmentLength;
        }
        
        @Override
        public int compareTo(final RecordFragment o) {
            if (!(o instanceof HandshakeFragment)) {
                return super.compareTo(o);
            }
            final HandshakeFragment other = (HandshakeFragment)o;
            if (this.messageSeq != other.messageSeq) {
                return this.messageSeq - other.messageSeq;
            }
            if (this.fragmentOffset != other.fragmentOffset) {
                return this.fragmentOffset - other.fragmentOffset;
            }
            if (this.fragmentLength == other.fragmentLength) {
                return 0;
            }
            return this.compareToSequence(o.recordEpoch, o.recordSeq);
        }
    }
    
    private static final class HoleDescriptor
    {
        int offset;
        int limit;
        
        HoleDescriptor(final int offset, final int limit) {
            this.offset = offset;
            this.limit = limit;
        }
    }
    
    private static final class HandshakeFlight implements Cloneable
    {
        static final byte HF_UNKNOWN;
        byte handshakeType;
        int flightEpoch;
        int minMessageSeq;
        int maxMessageSeq;
        int maxRecordEpoch;
        long maxRecordSeq;
        HashMap<Byte, List<HoleDescriptor>> holesMap;
        
        HandshakeFlight() {
            this.handshakeType = HandshakeFlight.HF_UNKNOWN;
            this.flightEpoch = 0;
            this.minMessageSeq = 0;
            this.maxMessageSeq = 0;
            this.maxRecordEpoch = 0;
            this.maxRecordSeq = -1L;
            this.holesMap = new HashMap<Byte, List<HoleDescriptor>>(5);
        }
        
        boolean isRetransmitOf(final HandshakeFlight hs) {
            return hs != null && this.handshakeType == hs.handshakeType && this.minMessageSeq == hs.minMessageSeq;
        }
        
        public Object clone() {
            final HandshakeFlight hf = new HandshakeFlight();
            hf.handshakeType = this.handshakeType;
            hf.flightEpoch = this.flightEpoch;
            hf.minMessageSeq = this.minMessageSeq;
            hf.maxMessageSeq = this.maxMessageSeq;
            hf.maxRecordEpoch = this.maxRecordEpoch;
            hf.maxRecordSeq = this.maxRecordSeq;
            hf.holesMap = new HashMap<Byte, List<HoleDescriptor>>(this.holesMap);
            return hf;
        }
        
        static {
            HF_UNKNOWN = SSLHandshake.NOT_APPLICABLE.id;
        }
    }
    
    final class DTLSReassembler
    {
        final int handshakeEpoch;
        TreeSet<RecordFragment> bufferedFragments;
        HandshakeFlight handshakeFlight;
        HandshakeFlight precedingFlight;
        int nextRecordEpoch;
        long nextRecordSeq;
        boolean expectCCSFlight;
        boolean flightIsReady;
        boolean needToCheckFlight;
        
        DTLSReassembler(final int handshakeEpoch) {
            this.bufferedFragments = new TreeSet<RecordFragment>();
            this.handshakeFlight = new HandshakeFlight();
            this.precedingFlight = null;
            this.nextRecordSeq = 0L;
            this.expectCCSFlight = false;
            this.flightIsReady = false;
            this.needToCheckFlight = false;
            this.handshakeEpoch = handshakeEpoch;
            this.nextRecordEpoch = handshakeEpoch;
            this.handshakeFlight.flightEpoch = handshakeEpoch;
        }
        
        void expectingFinishFlight() {
            this.expectCCSFlight = true;
        }
        
        void queueUpHandshake(final HandshakeFragment hsf) {
            if (!this.isDesirable(hsf)) {
                return;
            }
            this.cleanUpRetransmit(hsf);
            boolean isMinimalFlightMessage = false;
            if (this.handshakeFlight.minMessageSeq == hsf.messageSeq) {
                isMinimalFlightMessage = true;
            }
            else if (this.precedingFlight != null && this.precedingFlight.minMessageSeq == hsf.messageSeq) {
                isMinimalFlightMessage = true;
            }
            if (isMinimalFlightMessage && hsf.fragmentOffset == 0 && hsf.handshakeType != SSLHandshake.FINISHED.id) {
                this.handshakeFlight.handshakeType = hsf.handshakeType;
                this.handshakeFlight.flightEpoch = hsf.recordEpoch;
                this.handshakeFlight.minMessageSeq = hsf.messageSeq;
            }
            if (hsf.handshakeType == SSLHandshake.FINISHED.id) {
                this.handshakeFlight.maxMessageSeq = hsf.messageSeq;
                this.handshakeFlight.maxRecordEpoch = hsf.recordEpoch;
                this.handshakeFlight.maxRecordSeq = hsf.recordSeq;
            }
            else {
                if (this.handshakeFlight.maxMessageSeq < hsf.messageSeq) {
                    this.handshakeFlight.maxMessageSeq = hsf.messageSeq;
                }
                final int n = hsf.recordEpoch - this.handshakeFlight.maxRecordEpoch;
                if (n > 0) {
                    this.handshakeFlight.maxRecordEpoch = hsf.recordEpoch;
                    this.handshakeFlight.maxRecordSeq = hsf.recordSeq;
                }
                else if (n == 0 && this.handshakeFlight.maxRecordSeq < hsf.recordSeq) {
                    this.handshakeFlight.maxRecordSeq = hsf.recordSeq;
                }
            }
            boolean fragmented = false;
            if (hsf.fragmentOffset != 0 || hsf.fragmentLength != hsf.messageLength) {
                fragmented = true;
            }
            List<HoleDescriptor> holes = this.handshakeFlight.holesMap.get(hsf.handshakeType);
            if (holes == null) {
                if (!fragmented) {
                    holes = Collections.emptyList();
                }
                else {
                    holes = new LinkedList<HoleDescriptor>();
                    holes.add(new HoleDescriptor(0, hsf.messageLength));
                }
                this.handshakeFlight.holesMap.put(hsf.handshakeType, holes);
            }
            else if (holes.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("Have got the full message, discard it.", new Object[0]);
                }
                return;
            }
            if (fragmented) {
                final int fragmentLimit = hsf.fragmentOffset + hsf.fragmentLength;
                int i = 0;
                while (i < holes.size()) {
                    final HoleDescriptor hole = holes.get(i);
                    if (hole.limit > hsf.fragmentOffset && hole.offset < fragmentLimit) {
                        if ((hole.offset > hsf.fragmentOffset && hole.offset < fragmentLimit) || (hole.limit > hsf.fragmentOffset && hole.limit < fragmentLimit)) {
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                                SSLLogger.fine("Discard invalid record: handshake fragment ranges are overlapping", new Object[0]);
                            }
                            return;
                        }
                        holes.remove(i);
                        if (hsf.fragmentOffset > hole.offset) {
                            holes.add(new HoleDescriptor(hole.offset, hsf.fragmentOffset));
                        }
                        if (fragmentLimit < hole.limit) {
                            holes.add(new HoleDescriptor(fragmentLimit, hole.limit));
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            if (hsf.handshakeType == SSLHandshake.FINISHED.id) {
                this.bufferedFragments.add(hsf);
            }
            else {
                this.bufferFragment(hsf);
            }
        }
        
        void queueUpChangeCipherSpec(final RecordFragment rf) {
            if (!this.isDesirable(rf)) {
                return;
            }
            this.cleanUpRetransmit(rf);
            if (this.expectCCSFlight) {
                this.handshakeFlight.handshakeType = HandshakeFlight.HF_UNKNOWN;
                this.handshakeFlight.flightEpoch = rf.recordEpoch;
            }
            if (this.handshakeFlight.maxRecordSeq < rf.recordSeq) {
                this.handshakeFlight.maxRecordSeq = rf.recordSeq;
            }
            this.bufferFragment(rf);
        }
        
        void queueUpFragment(final RecordFragment rf) {
            if (!this.isDesirable(rf)) {
                return;
            }
            this.cleanUpRetransmit(rf);
            this.bufferFragment(rf);
        }
        
        private void bufferFragment(final RecordFragment rf) {
            this.bufferedFragments.add(rf);
            if (this.flightIsReady) {
                this.flightIsReady = false;
            }
            if (!this.needToCheckFlight) {
                this.needToCheckFlight = true;
            }
        }
        
        private void cleanUpRetransmit(final RecordFragment rf) {
            boolean isNewFlight = false;
            if (this.precedingFlight != null) {
                if (this.precedingFlight.flightEpoch < rf.recordEpoch) {
                    isNewFlight = true;
                }
                else if (rf instanceof HandshakeFragment) {
                    final HandshakeFragment hsf = (HandshakeFragment)rf;
                    if (this.precedingFlight.maxMessageSeq < hsf.messageSeq) {
                        isNewFlight = true;
                    }
                }
                else if (rf.contentType != ContentType.CHANGE_CIPHER_SPEC.id && this.precedingFlight.maxRecordEpoch < rf.recordEpoch) {
                    isNewFlight = true;
                }
            }
            if (!isNewFlight) {
                return;
            }
            final Iterator<RecordFragment> it = this.bufferedFragments.iterator();
            while (it.hasNext()) {
                final RecordFragment frag = it.next();
                boolean isOld = false;
                if (frag.recordEpoch < this.precedingFlight.maxRecordEpoch) {
                    isOld = true;
                }
                else if (frag.recordEpoch == this.precedingFlight.maxRecordEpoch && frag.recordSeq <= this.precedingFlight.maxRecordSeq) {
                    isOld = true;
                }
                if (!isOld && frag instanceof HandshakeFragment) {
                    final HandshakeFragment hsf2 = (HandshakeFragment)frag;
                    isOld = (hsf2.messageSeq <= this.precedingFlight.maxMessageSeq);
                }
                if (!isOld) {
                    break;
                }
                it.remove();
            }
            this.precedingFlight = null;
        }
        
        private boolean isDesirable(final RecordFragment rf) {
            final int previousEpoch = this.nextRecordEpoch - 1;
            if (rf.recordEpoch < previousEpoch) {
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("Too old epoch to use this record, discard it.", new Object[0]);
                }
                return false;
            }
            if (rf.recordEpoch == previousEpoch) {
                boolean isDesired = true;
                if (this.precedingFlight == null) {
                    isDesired = false;
                }
                else if (rf instanceof HandshakeFragment) {
                    final HandshakeFragment hsf = (HandshakeFragment)rf;
                    if (this.precedingFlight.minMessageSeq > hsf.messageSeq) {
                        isDesired = false;
                    }
                }
                else if (rf.contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                    if (this.precedingFlight.flightEpoch != rf.recordEpoch) {
                        isDesired = false;
                    }
                }
                else if (rf.recordEpoch < this.precedingFlight.maxRecordEpoch || (rf.recordEpoch == this.precedingFlight.maxRecordEpoch && rf.recordSeq <= this.precedingFlight.maxRecordSeq)) {
                    isDesired = false;
                }
                if (!isDesired) {
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Too old retransmission to use, discard it.", new Object[0]);
                    }
                    return false;
                }
            }
            else if (rf.recordEpoch == this.nextRecordEpoch && this.nextRecordSeq > rf.recordSeq) {
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("Lagging behind record (sequence), discard it.", new Object[0]);
                }
                return false;
            }
            return true;
        }
        
        private boolean isEmpty() {
            return this.bufferedFragments.isEmpty() || (!this.flightIsReady && !this.needToCheckFlight) || (this.needToCheckFlight && !this.flightIsReady());
        }
        
        Plaintext acquirePlaintext() {
            if (this.bufferedFragments.isEmpty()) {
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("No received handshake messages", new Object[0]);
                }
                return null;
            }
            if (!this.flightIsReady && this.needToCheckFlight) {
                this.flightIsReady = this.flightIsReady();
                if (this.flightIsReady && this.handshakeFlight.isRetransmitOf(this.precedingFlight)) {
                    this.bufferedFragments.clear();
                    this.resetHandshakeFlight(this.precedingFlight);
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Received a retransmission flight.", new Object[0]);
                    }
                    return Plaintext.PLAINTEXT_NULL;
                }
                this.needToCheckFlight = false;
            }
            if (!this.flightIsReady) {
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("The handshake flight is not ready to use: " + this.handshakeFlight.handshakeType, new Object[0]);
                }
                return null;
            }
            final RecordFragment rFrag = this.bufferedFragments.first();
            Plaintext plaintext;
            if (!rFrag.isCiphertext) {
                plaintext = this.acquireHandshakeMessage();
                if (this.bufferedFragments.isEmpty()) {
                    this.handshakeFlight.holesMap.clear();
                    this.resetHandshakeFlight(this.precedingFlight = (HandshakeFlight)this.handshakeFlight.clone());
                    if (this.expectCCSFlight && this.precedingFlight.handshakeType == HandshakeFlight.HF_UNKNOWN) {
                        this.expectCCSFlight = false;
                    }
                }
            }
            else {
                plaintext = this.acquireCachedMessage();
            }
            return plaintext;
        }
        
        private void resetHandshakeFlight(final HandshakeFlight prev) {
            this.handshakeFlight.handshakeType = HandshakeFlight.HF_UNKNOWN;
            this.handshakeFlight.flightEpoch = prev.maxRecordEpoch;
            if (prev.flightEpoch != prev.maxRecordEpoch) {
                this.handshakeFlight.minMessageSeq = 0;
            }
            else {
                this.handshakeFlight.minMessageSeq = prev.maxMessageSeq + 1;
            }
            this.handshakeFlight.maxMessageSeq = 0;
            this.handshakeFlight.maxRecordEpoch = this.handshakeFlight.flightEpoch;
            this.handshakeFlight.maxRecordSeq = prev.maxRecordSeq + 1L;
            this.handshakeFlight.holesMap.clear();
            this.flightIsReady = false;
            this.needToCheckFlight = false;
        }
        
        private Plaintext acquireCachedMessage() {
            final RecordFragment rFrag = this.bufferedFragments.first();
            if (DTLSInputRecord.this.readEpoch != rFrag.recordEpoch) {
                if (DTLSInputRecord.this.readEpoch > rFrag.recordEpoch) {
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Discard old buffered ciphertext fragments.", new Object[0]);
                    }
                    this.bufferedFragments.remove(rFrag);
                }
                if (this.flightIsReady) {
                    this.flightIsReady = false;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("Not yet ready to decrypt the cached fragments.", new Object[0]);
                }
                return null;
            }
            this.bufferedFragments.remove(rFrag);
            final ByteBuffer fragment = ByteBuffer.wrap(rFrag.fragment);
            ByteBuffer plaintextFragment = null;
            try {
                final Plaintext plaintext = DTLSInputRecord.this.readCipher.decrypt(rFrag.contentType, fragment, rFrag.recordEnS);
                plaintextFragment = plaintext.fragment;
                rFrag.contentType = plaintext.contentType;
            }
            catch (final GeneralSecurityException gse) {
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("Discard invalid record: ", gse);
                }
                return null;
            }
            if (rFrag.contentType == ContentType.HANDSHAKE.id) {
                while (plaintextFragment.remaining() > 0) {
                    final HandshakeFragment hsFrag = parseHandshakeMessage(rFrag.contentType, rFrag.majorVersion, rFrag.minorVersion, rFrag.recordEnS, rFrag.recordEpoch, rFrag.recordSeq, plaintextFragment);
                    if (hsFrag == null) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("Invalid handshake fragment, discard it", plaintextFragment);
                        }
                        return null;
                    }
                    this.queueUpHandshake(hsFrag);
                    if (hsFrag.handshakeType == SSLHandshake.FINISHED.id) {
                        continue;
                    }
                    this.flightIsReady = false;
                    this.needToCheckFlight = true;
                }
                return this.acquirePlaintext();
            }
            return new Plaintext(rFrag.contentType, rFrag.majorVersion, rFrag.minorVersion, rFrag.recordEpoch, Authenticator.toLong(rFrag.recordEnS), plaintextFragment);
        }
        
        private Plaintext acquireHandshakeMessage() {
            RecordFragment rFrag = this.bufferedFragments.first();
            if (rFrag.contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                this.nextRecordEpoch = rFrag.recordEpoch + 1;
                this.nextRecordSeq = 0L;
                this.bufferedFragments.remove(rFrag);
                return new Plaintext(rFrag.contentType, rFrag.majorVersion, rFrag.minorVersion, rFrag.recordEpoch, Authenticator.toLong(rFrag.recordEnS), ByteBuffer.wrap(rFrag.fragment));
            }
            final HandshakeFragment hsFrag = (HandshakeFragment)rFrag;
            if (hsFrag.messageLength == hsFrag.fragmentLength && hsFrag.fragmentOffset == 0) {
                this.bufferedFragments.remove(rFrag);
                this.nextRecordSeq = hsFrag.recordSeq + 1L;
                final byte[] recordFrag = new byte[hsFrag.messageLength + 4];
                final Plaintext plaintext = new Plaintext(hsFrag.contentType, hsFrag.majorVersion, hsFrag.minorVersion, hsFrag.recordEpoch, Authenticator.toLong(hsFrag.recordEnS), ByteBuffer.wrap(recordFrag));
                recordFrag[0] = hsFrag.handshakeType;
                recordFrag[1] = (byte)(hsFrag.messageLength >>> 16 & 0xFF);
                recordFrag[2] = (byte)(hsFrag.messageLength >>> 8 & 0xFF);
                recordFrag[3] = (byte)(hsFrag.messageLength & 0xFF);
                System.arraycopy(hsFrag.fragment, 0, recordFrag, 4, hsFrag.fragmentLength);
                this.handshakeHashing(hsFrag, plaintext);
                return plaintext;
            }
            final byte[] recordFrag = new byte[hsFrag.messageLength + 4];
            final Plaintext plaintext = new Plaintext(hsFrag.contentType, hsFrag.majorVersion, hsFrag.minorVersion, hsFrag.recordEpoch, Authenticator.toLong(hsFrag.recordEnS), ByteBuffer.wrap(recordFrag));
            recordFrag[0] = hsFrag.handshakeType;
            recordFrag[1] = (byte)(hsFrag.messageLength >>> 16 & 0xFF);
            recordFrag[2] = (byte)(hsFrag.messageLength >>> 8 & 0xFF);
            recordFrag[3] = (byte)(hsFrag.messageLength & 0xFF);
            final int msgSeq = hsFrag.messageSeq;
            long maxRecodeSN = hsFrag.recordSeq;
            HandshakeFragment hmFrag = hsFrag;
            do {
                System.arraycopy(hmFrag.fragment, 0, recordFrag, hmFrag.fragmentOffset + 4, hmFrag.fragmentLength);
                this.bufferedFragments.remove(rFrag);
                if (maxRecodeSN < hmFrag.recordSeq) {
                    maxRecodeSN = hmFrag.recordSeq;
                }
                if (!this.bufferedFragments.isEmpty()) {
                    rFrag = this.bufferedFragments.first();
                    if (rFrag.contentType != ContentType.HANDSHAKE.id) {
                        break;
                    }
                    hmFrag = (HandshakeFragment)rFrag;
                }
            } while (!this.bufferedFragments.isEmpty() && msgSeq == hmFrag.messageSeq);
            this.handshakeHashing(hsFrag, plaintext);
            this.nextRecordSeq = maxRecodeSN + 1L;
            return plaintext;
        }
        
        boolean flightIsReady() {
            final byte flightType = this.handshakeFlight.handshakeType;
            if (flightType == HandshakeFlight.HF_UNKNOWN) {
                if (this.expectCCSFlight) {
                    final boolean isReady = this.hasFinishedMessage(this.bufferedFragments);
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Has the final flight been received? " + isReady, new Object[0]);
                    }
                    return isReady;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("No flight is received yet.", new Object[0]);
                }
                return false;
            }
            else {
                if (flightType == SSLHandshake.CLIENT_HELLO.id || flightType == SSLHandshake.HELLO_REQUEST.id || flightType == SSLHandshake.HELLO_VERIFY_REQUEST.id) {
                    final boolean isReady = this.hasCompleted(flightType);
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Is the handshake message completed? " + isReady, new Object[0]);
                    }
                    return isReady;
                }
                if (flightType == SSLHandshake.SERVER_HELLO.id) {
                    if (!this.hasCompleted(flightType)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("The ServerHello message is not completed yet.", new Object[0]);
                        }
                        return false;
                    }
                    if (this.hasFinishedMessage(this.bufferedFragments)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("It's an abbreviated handshake.", new Object[0]);
                        }
                        return true;
                    }
                    final List<HoleDescriptor> holes = this.handshakeFlight.holesMap.get(SSLHandshake.SERVER_HELLO_DONE.id);
                    if (holes == null || !holes.isEmpty()) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("Not yet got the ServerHelloDone message", new Object[0]);
                        }
                        return false;
                    }
                    final boolean isReady2 = this.hasCompleted(this.bufferedFragments, this.handshakeFlight.minMessageSeq, this.handshakeFlight.maxMessageSeq);
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Is the ServerHello flight (message " + this.handshakeFlight.minMessageSeq + "-" + this.handshakeFlight.maxMessageSeq + ") completed? " + isReady2, new Object[0]);
                    }
                    return isReady2;
                }
                else {
                    if (flightType != SSLHandshake.CERTIFICATE.id && flightType != SSLHandshake.CLIENT_KEY_EXCHANGE.id) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("Need to receive more handshake messages", new Object[0]);
                        }
                        return false;
                    }
                    if (!this.hasCompleted(flightType)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("The ClientKeyExchange or client Certificate message is not completed yet.", new Object[0]);
                        }
                        return false;
                    }
                    if (flightType == SSLHandshake.CERTIFICATE.id && this.needClientVerify(this.bufferedFragments) && !this.hasCompleted(SSLHandshake.CERTIFICATE_VERIFY.id)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("Not yet have the CertificateVerify message", new Object[0]);
                        }
                        return false;
                    }
                    if (!this.hasFinishedMessage(this.bufferedFragments)) {
                        if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                            SSLLogger.fine("Not yet have the ChangeCipherSpec and Finished messages", new Object[0]);
                        }
                        return false;
                    }
                    final boolean isReady = this.hasCompleted(this.bufferedFragments, this.handshakeFlight.minMessageSeq, this.handshakeFlight.maxMessageSeq);
                    if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                        SSLLogger.fine("Is the ClientKeyExchange flight (message " + this.handshakeFlight.minMessageSeq + "-" + this.handshakeFlight.maxMessageSeq + ") completed? " + isReady, new Object[0]);
                    }
                    return isReady;
                }
            }
        }
        
        private boolean hasFinishedMessage(final Set<RecordFragment> fragments) {
            boolean hasCCS = false;
            boolean hasFin = false;
            for (final RecordFragment fragment : fragments) {
                if (fragment.contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                    if (hasFin) {
                        return true;
                    }
                    hasCCS = true;
                }
                else {
                    if (fragment.contentType != ContentType.HANDSHAKE.id || !fragment.isCiphertext) {
                        continue;
                    }
                    if (hasCCS) {
                        return true;
                    }
                    hasFin = true;
                }
            }
            return hasFin && hasCCS;
        }
        
        private boolean needClientVerify(final Set<RecordFragment> fragments) {
            for (final RecordFragment rFrag : fragments) {
                if (rFrag.contentType != ContentType.HANDSHAKE.id) {
                    break;
                }
                if (rFrag.isCiphertext) {
                    break;
                }
                final HandshakeFragment hsFrag = (HandshakeFragment)rFrag;
                if (hsFrag.handshakeType != SSLHandshake.CERTIFICATE.id) {
                    continue;
                }
                return rFrag.fragment != null && rFrag.fragment.length > 28;
            }
            return false;
        }
        
        private boolean hasCompleted(final byte handshakeType) {
            final List<HoleDescriptor> holes = this.handshakeFlight.holesMap.get(handshakeType);
            return holes != null && holes.isEmpty();
        }
        
        private boolean hasCompleted(final Set<RecordFragment> fragments, int presentMsgSeq, final int endMsgSeq) {
            for (final RecordFragment rFrag : fragments) {
                if (rFrag.contentType != ContentType.HANDSHAKE.id) {
                    break;
                }
                if (rFrag.isCiphertext) {
                    break;
                }
                final HandshakeFragment hsFrag = (HandshakeFragment)rFrag;
                if (hsFrag.messageSeq == presentMsgSeq) {
                    continue;
                }
                if (hsFrag.messageSeq != presentMsgSeq + 1) {
                    break;
                }
                if (!this.hasCompleted(hsFrag.handshakeType)) {
                    return false;
                }
                presentMsgSeq = hsFrag.messageSeq;
            }
            return presentMsgSeq >= endMsgSeq;
        }
        
        private void handshakeHashing(final HandshakeFragment hsFrag, final Plaintext plaintext) {
            final byte hsType = hsFrag.handshakeType;
            if (!DTLSInputRecord.this.handshakeHash.isHashable(hsType)) {
                return;
            }
            plaintext.fragment.position(4);
            final byte[] temporary = new byte[plaintext.fragment.remaining() + 12];
            temporary[0] = hsFrag.handshakeType;
            temporary[1] = (byte)(hsFrag.messageLength >> 16 & 0xFF);
            temporary[2] = (byte)(hsFrag.messageLength >> 8 & 0xFF);
            temporary[3] = (byte)(hsFrag.messageLength & 0xFF);
            temporary[4] = (byte)(hsFrag.messageSeq >> 8 & 0xFF);
            temporary[5] = (byte)(hsFrag.messageSeq & 0xFF);
            temporary[6] = 0;
            temporary[8] = (temporary[7] = 0);
            temporary[9] = temporary[1];
            temporary[10] = temporary[2];
            temporary[11] = temporary[3];
            plaintext.fragment.get(temporary, 12, plaintext.fragment.remaining());
            DTLSInputRecord.this.handshakeHash.receive(temporary);
            plaintext.fragment.position(0);
        }
    }
}

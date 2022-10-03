package org.bouncycastle.crypto.tls;

class DTLSEpoch
{
    private final DTLSReplayWindow replayWindow;
    private final int epoch;
    private final TlsCipher cipher;
    private long sequenceNumber;
    
    DTLSEpoch(final int epoch, final TlsCipher cipher) {
        this.replayWindow = new DTLSReplayWindow();
        this.sequenceNumber = 0L;
        if (epoch < 0) {
            throw new IllegalArgumentException("'epoch' must be >= 0");
        }
        if (cipher == null) {
            throw new IllegalArgumentException("'cipher' cannot be null");
        }
        this.epoch = epoch;
        this.cipher = cipher;
    }
    
    long allocateSequenceNumber() {
        return this.sequenceNumber++;
    }
    
    TlsCipher getCipher() {
        return this.cipher;
    }
    
    int getEpoch() {
        return this.epoch;
    }
    
    DTLSReplayWindow getReplayWindow() {
        return this.replayWindow;
    }
    
    long getSequenceNumber() {
        return this.sequenceNumber;
    }
}

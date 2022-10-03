package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Arrays;

class Authenticator
{
    private final byte[] block;
    private static final int BLOCK_SIZE_SSL = 11;
    private static final int BLOCK_SIZE_TLS = 13;
    
    Authenticator() {
        this.block = new byte[0];
    }
    
    Authenticator(final ProtocolVersion protocolVersion) {
        if (protocolVersion.v >= ProtocolVersion.TLS10.v) {
            (this.block = new byte[13])[9] = protocolVersion.major;
            this.block[10] = protocolVersion.minor;
        }
        else {
            this.block = new byte[11];
        }
    }
    
    final boolean seqNumOverflow() {
        return this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1 && this.block[2] == -1 && this.block[3] == -1 && this.block[4] == -1 && this.block[5] == -1 && this.block[6] == -1;
    }
    
    final boolean seqNumIsHuge() {
        return this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1;
    }
    
    final byte[] sequenceNumber() {
        return Arrays.copyOf(this.block, 8);
    }
    
    final byte[] acquireAuthenticationBytes(final byte type, final int length) {
        final byte[] copy = this.block.clone();
        if (this.block.length != 0) {
            copy[8] = type;
            copy[copy.length - 2] = (byte)(length >> 8);
            copy[copy.length - 1] = (byte)length;
            for (int k = 7; k >= 0; --k) {
                final byte[] block = this.block;
                final int n = k;
                if (++block[n] != 0) {
                    break;
                }
            }
        }
        return copy;
    }
}

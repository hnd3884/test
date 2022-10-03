package jcifs.smb;

import jcifs.util.LogStream;

abstract class SmbComNtTransactionResponse extends SmbComTransactionResponse
{
    int readParameterWordsWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        buffer[bufferIndex++] = 0;
        buffer[bufferIndex++] = 0;
        buffer[bufferIndex++] = 0;
        this.totalParameterCount = ServerMessageBlock.readInt4(buffer, bufferIndex);
        if (this.bufDataStart == 0) {
            this.bufDataStart = this.totalParameterCount;
        }
        bufferIndex += 4;
        this.totalDataCount = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.parameterCount = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.parameterOffset = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.parameterDisplacement = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.dataCount = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.dataOffset = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.dataDisplacement = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.setupCount = (buffer[bufferIndex] & 0xFF);
        bufferIndex += 2;
        if (this.setupCount != 0) {
            final LogStream log = SmbComNtTransactionResponse.log;
            if (LogStream.level >= 3) {
                SmbComNtTransactionResponse.log.println("setupCount is not zero: " + this.setupCount);
            }
        }
        return bufferIndex - start;
    }
}

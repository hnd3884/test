package jcifs.smb;

import jcifs.util.Hexdump;

abstract class AndXServerMessageBlock extends ServerMessageBlock
{
    private static final int ANDX_COMMAND_OFFSET = 1;
    private static final int ANDX_RESERVED_OFFSET = 2;
    private static final int ANDX_OFFSET_OFFSET = 3;
    private byte andxCommand;
    private int andxOffset;
    ServerMessageBlock andx;
    
    AndXServerMessageBlock() {
        this.andxCommand = -1;
        this.andxOffset = 0;
        this.andx = null;
    }
    
    AndXServerMessageBlock(final ServerMessageBlock andx) {
        this.andxCommand = -1;
        this.andxOffset = 0;
        this.andx = null;
        if (andx != null) {
            this.andx = andx;
            this.andxCommand = andx.command;
        }
    }
    
    int getBatchLimit(final byte command) {
        return 0;
    }
    
    int encode(final byte[] dst, int dstIndex) {
        final int headerStart = dstIndex;
        this.headerStart = headerStart;
        final int start = headerStart;
        dstIndex += this.writeHeaderWireFormat(dst, dstIndex);
        dstIndex += this.writeAndXWireFormat(dst, dstIndex);
        this.length = dstIndex - start;
        if (this.digest != null) {
            this.digest.sign(dst, this.headerStart, this.length, this, this.response);
        }
        return this.length;
    }
    
    int decode(final byte[] buffer, int bufferIndex) {
        final int headerStart = bufferIndex;
        this.headerStart = headerStart;
        final int start = headerStart;
        bufferIndex += this.readHeaderWireFormat(buffer, bufferIndex);
        bufferIndex += this.readAndXWireFormat(buffer, bufferIndex);
        return this.length = bufferIndex - start;
    }
    
    int writeAndXWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        this.wordCount = this.writeParameterWordsWireFormat(dst, start + 3 + 2);
        this.wordCount += 4;
        dstIndex += this.wordCount + 1;
        this.wordCount /= 2;
        dst[start] = (byte)(this.wordCount & 0xFF);
        this.byteCount = this.writeBytesWireFormat(dst, dstIndex + 2);
        dst[dstIndex++] = (byte)(this.byteCount & 0xFF);
        dst[dstIndex++] = (byte)(this.byteCount >> 8 & 0xFF);
        dstIndex += this.byteCount;
        if (this.andx == null || !SmbConstants.USE_BATCHING || this.batchLevel >= this.getBatchLimit(this.andx.command)) {
            this.andxCommand = -1;
            this.andx = null;
            dst[start + 1] = -1;
            dst[start + 2] = 0;
            dst[start + 3 + 1] = (dst[start + 3] = 0);
            return dstIndex - start;
        }
        this.andx.batchLevel = this.batchLevel + 1;
        dst[start + 1] = this.andxCommand;
        dst[start + 2] = 0;
        this.andxOffset = dstIndex - this.headerStart;
        ServerMessageBlock.writeInt2(this.andxOffset, dst, start + 3);
        this.andx.useUnicode = this.useUnicode;
        if (this.andx instanceof AndXServerMessageBlock) {
            this.andx.uid = this.uid;
            dstIndex += ((AndXServerMessageBlock)this.andx).writeAndXWireFormat(dst, dstIndex);
        }
        else {
            final int andxStart = dstIndex;
            this.andx.wordCount = this.andx.writeParameterWordsWireFormat(dst, dstIndex);
            dstIndex += this.andx.wordCount + 1;
            final ServerMessageBlock andx = this.andx;
            andx.wordCount /= 2;
            dst[andxStart] = (byte)(this.andx.wordCount & 0xFF);
            this.andx.byteCount = this.andx.writeBytesWireFormat(dst, dstIndex + 2);
            dst[dstIndex++] = (byte)(this.andx.byteCount & 0xFF);
            dst[dstIndex++] = (byte)(this.andx.byteCount >> 8 & 0xFF);
            dstIndex += this.andx.byteCount;
        }
        return dstIndex - start;
    }
    
    int readAndXWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        this.wordCount = buffer[bufferIndex++];
        if (this.wordCount != 0) {
            this.andxCommand = buffer[bufferIndex];
            bufferIndex += 2;
            this.andxOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
            bufferIndex += 2;
            if (this.andxOffset == 0) {
                this.andxCommand = -1;
            }
            if (this.wordCount > 2) {
                bufferIndex += this.readParameterWordsWireFormat(buffer, bufferIndex);
            }
        }
        this.byteCount = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        if (this.byteCount != 0) {
            final int n = this.readBytesWireFormat(buffer, bufferIndex);
            bufferIndex += this.byteCount;
        }
        if (this.errorCode != 0 || this.andxCommand == -1) {
            this.andxCommand = -1;
            this.andx = null;
        }
        else {
            if (this.andx == null) {
                this.andxCommand = -1;
                throw new RuntimeException("no andx command supplied with response");
            }
            bufferIndex = this.headerStart + this.andxOffset;
            this.andx.headerStart = this.headerStart;
            this.andx.command = this.andxCommand;
            this.andx.errorCode = this.errorCode;
            this.andx.flags = this.flags;
            this.andx.flags2 = this.flags2;
            this.andx.tid = this.tid;
            this.andx.pid = this.pid;
            this.andx.uid = this.uid;
            this.andx.mid = this.mid;
            this.andx.useUnicode = this.useUnicode;
            if (this.andx instanceof AndXServerMessageBlock) {
                bufferIndex += ((AndXServerMessageBlock)this.andx).readAndXWireFormat(buffer, bufferIndex);
            }
            else {
                buffer[bufferIndex++] = (byte)(this.andx.wordCount & 0xFF);
                if (this.andx.wordCount != 0 && this.andx.wordCount > 2) {
                    bufferIndex += this.andx.readParameterWordsWireFormat(buffer, bufferIndex);
                }
                this.andx.byteCount = ServerMessageBlock.readInt2(buffer, bufferIndex);
                bufferIndex += 2;
                if (this.andx.byteCount != 0) {
                    this.andx.readBytesWireFormat(buffer, bufferIndex);
                    bufferIndex += this.andx.byteCount;
                }
            }
            this.andx.received = true;
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String(super.toString() + ",andxCommand=0x" + Hexdump.toHexString(this.andxCommand, 2) + ",andxOffset=" + this.andxOffset);
    }
}

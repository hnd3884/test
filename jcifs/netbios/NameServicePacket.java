package jcifs.netbios;

import jcifs.util.Hexdump;
import java.net.InetAddress;

abstract class NameServicePacket
{
    static final int QUERY = 0;
    static final int WACK = 7;
    static final int FMT_ERR = 1;
    static final int SRV_ERR = 2;
    static final int IMP_ERR = 4;
    static final int RFS_ERR = 5;
    static final int ACT_ERR = 6;
    static final int CFT_ERR = 7;
    static final int NB_IN = 2097153;
    static final int NBSTAT_IN = 2162689;
    static final int NB = 32;
    static final int NBSTAT = 33;
    static final int IN = 1;
    static final int A = 1;
    static final int NS = 2;
    static final int NULL = 10;
    static final int HEADER_LENGTH = 12;
    static final int OPCODE_OFFSET = 2;
    static final int QUESTION_OFFSET = 4;
    static final int ANSWER_OFFSET = 6;
    static final int AUTHORITY_OFFSET = 8;
    static final int ADDITIONAL_OFFSET = 10;
    int addrIndex;
    NbtAddress[] addrEntry;
    int nameTrnId;
    int opCode;
    int resultCode;
    int questionCount;
    int answerCount;
    int authorityCount;
    int additionalCount;
    boolean received;
    boolean isResponse;
    boolean isAuthAnswer;
    boolean isTruncated;
    boolean isRecurDesired;
    boolean isRecurAvailable;
    boolean isBroadcast;
    Name questionName;
    Name recordName;
    int questionType;
    int questionClass;
    int recordType;
    int recordClass;
    int ttl;
    int rDataLength;
    InetAddress addr;
    
    static void writeInt2(final int val, final byte[] dst, int dstIndex) {
        dst[dstIndex++] = (byte)(val >> 8 & 0xFF);
        dst[dstIndex] = (byte)(val & 0xFF);
    }
    
    static void writeInt4(final int val, final byte[] dst, int dstIndex) {
        dst[dstIndex++] = (byte)(val >> 24 & 0xFF);
        dst[dstIndex++] = (byte)(val >> 16 & 0xFF);
        dst[dstIndex++] = (byte)(val >> 8 & 0xFF);
        dst[dstIndex] = (byte)(val & 0xFF);
    }
    
    static int readInt2(final byte[] src, final int srcIndex) {
        return ((src[srcIndex] & 0xFF) << 8) + (src[srcIndex + 1] & 0xFF);
    }
    
    static int readInt4(final byte[] src, final int srcIndex) {
        return ((src[srcIndex] & 0xFF) << 24) + ((src[srcIndex + 1] & 0xFF) << 16) + ((src[srcIndex + 2] & 0xFF) << 8) + (src[srcIndex + 3] & 0xFF);
    }
    
    static int readNameTrnId(final byte[] src, final int srcIndex) {
        return readInt2(src, srcIndex);
    }
    
    NameServicePacket() {
        this.isRecurDesired = true;
        this.isBroadcast = true;
        this.questionCount = 1;
        this.questionClass = 1;
    }
    
    int writeWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        dstIndex += this.writeHeaderWireFormat(dst, dstIndex);
        dstIndex += this.writeBodyWireFormat(dst, dstIndex);
        return dstIndex - start;
    }
    
    int readWireFormat(final byte[] src, int srcIndex) {
        final int start = srcIndex;
        srcIndex += this.readHeaderWireFormat(src, srcIndex);
        srcIndex += this.readBodyWireFormat(src, srcIndex);
        return srcIndex - start;
    }
    
    int writeHeaderWireFormat(final byte[] dst, final int dstIndex) {
        final int start = dstIndex;
        writeInt2(this.nameTrnId, dst, dstIndex);
        dst[dstIndex + 2] = (byte)((this.isResponse ? 128 : 0) + (this.opCode << 3 & 0x78) + (this.isAuthAnswer ? 4 : 0) + (this.isTruncated ? 2 : 0) + (this.isRecurDesired ? 1 : 0));
        dst[dstIndex + 2 + 1] = (byte)((this.isRecurAvailable ? 128 : 0) + (this.isBroadcast ? 16 : 0) + (this.resultCode & 0xF));
        writeInt2(this.questionCount, dst, start + 4);
        writeInt2(this.answerCount, dst, start + 6);
        writeInt2(this.authorityCount, dst, start + 8);
        writeInt2(this.additionalCount, dst, start + 10);
        return 12;
    }
    
    int readHeaderWireFormat(final byte[] src, final int srcIndex) {
        this.nameTrnId = readInt2(src, srcIndex);
        this.isResponse = ((src[srcIndex + 2] & 0x80) != 0x0);
        this.opCode = (src[srcIndex + 2] & 0x78) >> 3;
        this.isAuthAnswer = ((src[srcIndex + 2] & 0x4) != 0x0);
        this.isTruncated = ((src[srcIndex + 2] & 0x2) != 0x0);
        this.isRecurDesired = ((src[srcIndex + 2] & 0x1) != 0x0);
        this.isRecurAvailable = ((src[srcIndex + 2 + 1] & 0x80) != 0x0);
        this.isBroadcast = ((src[srcIndex + 2 + 1] & 0x10) != 0x0);
        this.resultCode = (src[srcIndex + 2 + 1] & 0xF);
        this.questionCount = readInt2(src, srcIndex + 4);
        this.answerCount = readInt2(src, srcIndex + 6);
        this.authorityCount = readInt2(src, srcIndex + 8);
        this.additionalCount = readInt2(src, srcIndex + 10);
        return 12;
    }
    
    int writeQuestionSectionWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        dstIndex += this.questionName.writeWireFormat(dst, dstIndex);
        writeInt2(this.questionType, dst, dstIndex);
        dstIndex += 2;
        writeInt2(this.questionClass, dst, dstIndex);
        dstIndex += 2;
        return dstIndex - start;
    }
    
    int readQuestionSectionWireFormat(final byte[] src, int srcIndex) {
        final int start = srcIndex;
        srcIndex += this.questionName.readWireFormat(src, srcIndex);
        this.questionType = readInt2(src, srcIndex);
        srcIndex += 2;
        this.questionClass = readInt2(src, srcIndex);
        srcIndex += 2;
        return srcIndex - start;
    }
    
    int writeResourceRecordWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        if (this.recordName == this.questionName) {
            dst[dstIndex++] = -64;
            dst[dstIndex++] = 12;
        }
        else {
            dstIndex += this.recordName.writeWireFormat(dst, dstIndex);
        }
        writeInt2(this.recordType, dst, dstIndex);
        dstIndex += 2;
        writeInt2(this.recordClass, dst, dstIndex);
        dstIndex += 2;
        writeInt4(this.ttl, dst, dstIndex);
        dstIndex += 4;
        writeInt2(this.rDataLength = this.writeRDataWireFormat(dst, dstIndex + 2), dst, dstIndex);
        dstIndex += 2 + this.rDataLength;
        return dstIndex - start;
    }
    
    int readResourceRecordWireFormat(final byte[] src, int srcIndex) {
        final int start = srcIndex;
        if ((src[srcIndex] & 0xC0) == 0xC0) {
            this.recordName = this.questionName;
            srcIndex += 2;
        }
        else {
            srcIndex += this.recordName.readWireFormat(src, srcIndex);
        }
        this.recordType = readInt2(src, srcIndex);
        srcIndex += 2;
        this.recordClass = readInt2(src, srcIndex);
        srcIndex += 2;
        this.ttl = readInt4(src, srcIndex);
        srcIndex += 4;
        this.rDataLength = readInt2(src, srcIndex);
        srcIndex += 2;
        this.addrEntry = new NbtAddress[this.rDataLength / 6];
        final int end = srcIndex + this.rDataLength;
        this.addrIndex = 0;
        while (srcIndex < end) {
            srcIndex += this.readRDataWireFormat(src, srcIndex);
            ++this.addrIndex;
        }
        return srcIndex - start;
    }
    
    abstract int writeBodyWireFormat(final byte[] p0, final int p1);
    
    abstract int readBodyWireFormat(final byte[] p0, final int p1);
    
    abstract int writeRDataWireFormat(final byte[] p0, final int p1);
    
    abstract int readRDataWireFormat(final byte[] p0, final int p1);
    
    public String toString() {
        String opCodeString = null;
        switch (this.opCode) {
            case 0: {
                opCodeString = "QUERY";
                break;
            }
            case 7: {
                opCodeString = "WACK";
                break;
            }
            default: {
                opCodeString = Integer.toString(this.opCode);
                break;
            }
        }
        switch (this.resultCode) {
            case 1: {
                final String resultCodeString = "FMT_ERR";
                break;
            }
            case 2: {
                final String resultCodeString = "SRV_ERR";
                break;
            }
            case 4: {
                final String resultCodeString = "IMP_ERR";
                break;
            }
            case 5: {
                final String resultCodeString = "RFS_ERR";
                break;
            }
            case 6: {
                final String resultCodeString = "ACT_ERR";
                break;
            }
            case 7: {
                final String resultCodeString = "CFT_ERR";
                break;
            }
            default: {
                final String resultCodeString = "0x" + Hexdump.toHexString(this.resultCode, 1);
                break;
            }
        }
        switch (this.questionType) {
            case 32: {
                final String questionTypeString = "NB";
            }
            case 33: {
                final String questionTypeString = "NBSTAT";
                break;
            }
        }
        final String questionTypeString = "0x" + Hexdump.toHexString(this.questionType, 4);
        switch (this.recordType) {
            case 1: {
                final String recordTypeString = "A";
                return new String("nameTrnId=" + this.nameTrnId + ",isResponse=" + this.isResponse + ",opCode=" + opCodeString + ",isAuthAnswer=" + this.isAuthAnswer + ",isTruncated=" + this.isTruncated + ",isRecurAvailable=" + this.isRecurAvailable + ",isRecurDesired=" + this.isRecurDesired + ",isBroadcast=" + this.isBroadcast + ",resultCode=" + this.resultCode + ",questionCount=" + this.questionCount + ",answerCount=" + this.answerCount + ",authorityCount=" + this.authorityCount + ",additionalCount=" + this.additionalCount + ",questionName=" + this.questionName + ",questionType=" + questionTypeString + ",questionClass=" + ((this.questionClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.questionClass, 4))) + ",recordName=" + this.recordName + ",recordType=" + recordTypeString + ",recordClass=" + ((this.recordClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.recordClass, 4))) + ",ttl=" + this.ttl + ",rDataLength=" + this.rDataLength);
            }
            case 2: {
                final String recordTypeString = "NS";
                return new String("nameTrnId=" + this.nameTrnId + ",isResponse=" + this.isResponse + ",opCode=" + opCodeString + ",isAuthAnswer=" + this.isAuthAnswer + ",isTruncated=" + this.isTruncated + ",isRecurAvailable=" + this.isRecurAvailable + ",isRecurDesired=" + this.isRecurDesired + ",isBroadcast=" + this.isBroadcast + ",resultCode=" + this.resultCode + ",questionCount=" + this.questionCount + ",answerCount=" + this.answerCount + ",authorityCount=" + this.authorityCount + ",additionalCount=" + this.additionalCount + ",questionName=" + this.questionName + ",questionType=" + questionTypeString + ",questionClass=" + ((this.questionClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.questionClass, 4))) + ",recordName=" + this.recordName + ",recordType=" + recordTypeString + ",recordClass=" + ((this.recordClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.recordClass, 4))) + ",ttl=" + this.ttl + ",rDataLength=" + this.rDataLength);
            }
            case 10: {
                final String recordTypeString = "NULL";
                return new String("nameTrnId=" + this.nameTrnId + ",isResponse=" + this.isResponse + ",opCode=" + opCodeString + ",isAuthAnswer=" + this.isAuthAnswer + ",isTruncated=" + this.isTruncated + ",isRecurAvailable=" + this.isRecurAvailable + ",isRecurDesired=" + this.isRecurDesired + ",isBroadcast=" + this.isBroadcast + ",resultCode=" + this.resultCode + ",questionCount=" + this.questionCount + ",answerCount=" + this.answerCount + ",authorityCount=" + this.authorityCount + ",additionalCount=" + this.additionalCount + ",questionName=" + this.questionName + ",questionType=" + questionTypeString + ",questionClass=" + ((this.questionClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.questionClass, 4))) + ",recordName=" + this.recordName + ",recordType=" + recordTypeString + ",recordClass=" + ((this.recordClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.recordClass, 4))) + ",ttl=" + this.ttl + ",rDataLength=" + this.rDataLength);
            }
            case 32: {
                final String recordTypeString = "NB";
            }
            case 33: {
                final String recordTypeString = "NBSTAT";
                break;
            }
        }
        final String recordTypeString = "0x" + Hexdump.toHexString(this.recordType, 4);
        return new String("nameTrnId=" + this.nameTrnId + ",isResponse=" + this.isResponse + ",opCode=" + opCodeString + ",isAuthAnswer=" + this.isAuthAnswer + ",isTruncated=" + this.isTruncated + ",isRecurAvailable=" + this.isRecurAvailable + ",isRecurDesired=" + this.isRecurDesired + ",isBroadcast=" + this.isBroadcast + ",resultCode=" + this.resultCode + ",questionCount=" + this.questionCount + ",answerCount=" + this.answerCount + ",authorityCount=" + this.authorityCount + ",additionalCount=" + this.additionalCount + ",questionName=" + this.questionName + ",questionType=" + questionTypeString + ",questionClass=" + ((this.questionClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.questionClass, 4))) + ",recordName=" + this.recordName + ",recordType=" + recordTypeString + ",recordClass=" + ((this.recordClass == 1) ? "IN" : ("0x" + Hexdump.toHexString(this.recordClass, 4))) + ",ttl=" + this.ttl + ",rDataLength=" + this.rDataLength);
    }
}

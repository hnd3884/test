package jcifs.smb;

import java.util.Date;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import jcifs.util.LogStream;

class Trans2FindFirst2Response extends SmbComTransactionResponse
{
    static final int SMB_INFO_STANDARD = 1;
    static final int SMB_INFO_QUERY_EA_SIZE = 2;
    static final int SMB_INFO_QUERY_EAS_FROM_LIST = 3;
    static final int SMB_FIND_FILE_DIRECTORY_INFO = 257;
    static final int SMB_FIND_FILE_FULL_DIRECTORY_INFO = 258;
    static final int SMB_FILE_NAMES_INFO = 259;
    static final int SMB_FILE_BOTH_DIRECTORY_INFO = 260;
    int sid;
    boolean isEndOfSearch;
    int eaErrorOffset;
    int lastNameOffset;
    int lastNameBufferIndex;
    String lastName;
    int resumeKey;
    
    Trans2FindFirst2Response() {
        this.command = 50;
        this.subCommand = 1;
    }
    
    String readString(final byte[] src, final int srcIndex, int len) {
        String str = null;
        try {
            if (this.useUnicode) {
                str = new String(src, srcIndex, len, "UnicodeLittleUnmarked");
            }
            else {
                if (len > 0 && src[srcIndex + len - 1] == 0) {
                    --len;
                }
                str = new String(src, srcIndex, len, SmbConstants.OEM_ENCODING);
            }
        }
        catch (final UnsupportedEncodingException uee) {
            final LogStream log = Trans2FindFirst2Response.log;
            if (LogStream.level > 1) {
                uee.printStackTrace(Trans2FindFirst2Response.log);
            }
        }
        return str;
    }
    
    int writeSetupWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeParametersWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int writeDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readSetupWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readParametersWireFormat(final byte[] buffer, int bufferIndex, final int len) {
        final int start = bufferIndex;
        if (this.subCommand == 1) {
            this.sid = ServerMessageBlock.readInt2(buffer, bufferIndex);
            bufferIndex += 2;
        }
        this.numEntries = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.isEndOfSearch = ((buffer[bufferIndex] & 0x1) == 0x1);
        bufferIndex += 2;
        this.eaErrorOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.lastNameOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        return bufferIndex - start;
    }
    
    int readDataWireFormat(final byte[] buffer, int bufferIndex, final int len) {
        final int start = bufferIndex;
        this.lastNameBufferIndex = bufferIndex + this.lastNameOffset;
        this.results = new SmbFindFileBothDirectoryInfo[this.numEntries];
        for (int i = 0; i < this.numEntries; ++i) {
            final SmbFindFileBothDirectoryInfo e = (SmbFindFileBothDirectoryInfo)(this.results[i] = new SmbFindFileBothDirectoryInfo());
            e.nextEntryOffset = ServerMessageBlock.readInt4(buffer, bufferIndex);
            e.fileIndex = ServerMessageBlock.readInt4(buffer, bufferIndex + 4);
            e.creationTime = ServerMessageBlock.readTime(buffer, bufferIndex + 8);
            e.lastWriteTime = ServerMessageBlock.readTime(buffer, bufferIndex + 24);
            e.endOfFile = ServerMessageBlock.readInt8(buffer, bufferIndex + 40);
            e.extFileAttributes = ServerMessageBlock.readInt4(buffer, bufferIndex + 56);
            e.fileNameLength = ServerMessageBlock.readInt4(buffer, bufferIndex + 60);
            e.filename = this.readString(buffer, bufferIndex + 94, e.fileNameLength);
            if (this.lastNameBufferIndex >= bufferIndex && (e.nextEntryOffset == 0 || this.lastNameBufferIndex < bufferIndex + e.nextEntryOffset)) {
                this.lastName = e.filename;
                this.resumeKey = e.fileIndex;
            }
            bufferIndex += e.nextEntryOffset;
        }
        return this.dataCount;
    }
    
    public String toString() {
        String c;
        if (this.subCommand == 1) {
            c = "Trans2FindFirst2Response[";
        }
        else {
            c = "Trans2FindNext2Response[";
        }
        return new String(c + super.toString() + ",sid=" + this.sid + ",searchCount=" + this.numEntries + ",isEndOfSearch=" + this.isEndOfSearch + ",eaErrorOffset=" + this.eaErrorOffset + ",lastNameOffset=" + this.lastNameOffset + ",lastName=" + this.lastName + "]");
    }
    
    class SmbFindFileBothDirectoryInfo implements FileEntry
    {
        int nextEntryOffset;
        int fileIndex;
        long creationTime;
        long lastAccessTime;
        long lastWriteTime;
        long changeTime;
        long endOfFile;
        long allocationSize;
        int extFileAttributes;
        int fileNameLength;
        int eaSize;
        int shortNameLength;
        String shortName;
        String filename;
        
        public String getName() {
            return this.filename;
        }
        
        public int getType() {
            return 1;
        }
        
        public int getAttributes() {
            return this.extFileAttributes;
        }
        
        public long createTime() {
            return this.creationTime;
        }
        
        public long lastModified() {
            return this.lastWriteTime;
        }
        
        public long length() {
            return this.endOfFile;
        }
        
        public String toString() {
            return new String("SmbFindFileBothDirectoryInfo[nextEntryOffset=" + this.nextEntryOffset + ",fileIndex=" + this.fileIndex + ",creationTime=" + new Date(this.creationTime) + ",lastAccessTime=" + new Date(this.lastAccessTime) + ",lastWriteTime=" + new Date(this.lastWriteTime) + ",changeTime=" + new Date(this.changeTime) + ",endOfFile=" + this.endOfFile + ",allocationSize=" + this.allocationSize + ",extFileAttributes=" + this.extFileAttributes + ",fileNameLength=" + this.fileNameLength + ",eaSize=" + this.eaSize + ",shortNameLength=" + this.shortNameLength + ",shortName=" + this.shortName + ",filename=" + this.filename + "]");
        }
    }
}

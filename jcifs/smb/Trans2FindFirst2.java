package jcifs.smb;

import jcifs.Config;
import jcifs.util.Hexdump;

class Trans2FindFirst2 extends SmbComTransaction
{
    private static final int FLAGS_CLOSE_AFTER_THIS_REQUEST = 1;
    private static final int FLAGS_CLOSE_IF_END_REACHED = 2;
    private static final int FLAGS_RETURN_RESUME_KEYS = 4;
    private static final int FLAGS_RESUME_FROM_PREVIOUS_END = 8;
    private static final int FLAGS_FIND_WITH_BACKUP_INTENT = 16;
    private static final int DEFAULT_LIST_SIZE = 65535;
    private static final int DEFAULT_LIST_COUNT = 200;
    private int searchAttributes;
    private int flags;
    private int informationLevel;
    private int searchStorageType;
    private String wildcard;
    static final int SMB_INFO_STANDARD = 1;
    static final int SMB_INFO_QUERY_EA_SIZE = 2;
    static final int SMB_INFO_QUERY_EAS_FROM_LIST = 3;
    static final int SMB_FIND_FILE_DIRECTORY_INFO = 257;
    static final int SMB_FIND_FILE_FULL_DIRECTORY_INFO = 258;
    static final int SMB_FILE_NAMES_INFO = 259;
    static final int SMB_FILE_BOTH_DIRECTORY_INFO = 260;
    static final int LIST_SIZE;
    static final int LIST_COUNT;
    
    Trans2FindFirst2(final String filename, final String wildcard, final int searchAttributes) {
        this.searchStorageType = 0;
        if (filename.equals("\\")) {
            this.path = filename;
        }
        else {
            this.path = filename + "\\";
        }
        this.wildcard = wildcard;
        this.searchAttributes = (searchAttributes & 0x37);
        this.command = 50;
        this.subCommand = 1;
        this.flags = 0;
        this.informationLevel = 260;
        this.totalDataCount = 0;
        this.maxParameterCount = 10;
        this.maxDataCount = Trans2FindFirst2.LIST_SIZE;
        this.maxSetupCount = 0;
    }
    
    int writeSetupWireFormat(final byte[] dst, int dstIndex) {
        dst[dstIndex++] = this.subCommand;
        dst[dstIndex++] = 0;
        return 2;
    }
    
    int writeParametersWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        ServerMessageBlock.writeInt2(this.searchAttributes, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(Trans2FindFirst2.LIST_COUNT, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.flags, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.informationLevel, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.searchStorageType, dst, dstIndex);
        dstIndex += 4;
        dstIndex += this.writeString(this.path + this.wildcard, dst, dstIndex);
        return dstIndex - start;
    }
    
    int writeDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readSetupWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    public String toString() {
        return new String("Trans2FindFirst2[" + super.toString() + ",searchAttributes=0x" + Hexdump.toHexString(this.searchAttributes, 2) + ",searchCount=" + Trans2FindFirst2.LIST_COUNT + ",flags=0x" + Hexdump.toHexString(this.flags, 2) + ",informationLevel=0x" + Hexdump.toHexString(this.informationLevel, 3) + ",searchStorageType=" + this.searchStorageType + ",filename=" + this.path + "]");
    }
    
    static {
        LIST_SIZE = Config.getInt("jcifs.smb.client.listSize", 65535);
        LIST_COUNT = Config.getInt("jcifs.smb.client.listCount", 200);
    }
}

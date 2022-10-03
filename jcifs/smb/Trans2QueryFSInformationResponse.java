package jcifs.smb;

class Trans2QueryFSInformationResponse extends SmbComTransactionResponse
{
    static final int SMB_INFO_ALLOCATION = 1;
    static final int SMB_QUERY_FS_SIZE_INFO = 259;
    static final int SMB_FS_FULL_SIZE_INFORMATION = 1007;
    private int informationLevel;
    AllocInfo info;
    
    Trans2QueryFSInformationResponse(final int informationLevel) {
        this.informationLevel = informationLevel;
        this.command = 50;
        this.subCommand = 3;
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
    
    int readParametersWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        return 0;
    }
    
    int readDataWireFormat(final byte[] buffer, final int bufferIndex, final int len) {
        switch (this.informationLevel) {
            case 1: {
                return this.readSmbInfoAllocationWireFormat(buffer, bufferIndex);
            }
            case 259: {
                return this.readSmbQueryFSSizeInfoWireFormat(buffer, bufferIndex);
            }
            case 1007: {
                return this.readFsFullSizeInformationWireFormat(buffer, bufferIndex);
            }
            default: {
                return 0;
            }
        }
    }
    
    int readSmbInfoAllocationWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        final SmbInfoAllocation info = new SmbInfoAllocation();
        bufferIndex += 4;
        info.sectPerAlloc = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        info.alloc = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        info.free = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        info.bytesPerSect = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 4;
        this.info = info;
        return bufferIndex - start;
    }
    
    int readSmbQueryFSSizeInfoWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        final SmbInfoAllocation info = new SmbInfoAllocation();
        info.alloc = ServerMessageBlock.readInt8(buffer, bufferIndex);
        bufferIndex += 8;
        info.free = ServerMessageBlock.readInt8(buffer, bufferIndex);
        bufferIndex += 8;
        info.sectPerAlloc = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        info.bytesPerSect = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.info = info;
        return bufferIndex - start;
    }
    
    int readFsFullSizeInformationWireFormat(final byte[] buffer, int bufferIndex) {
        final int start = bufferIndex;
        final SmbInfoAllocation info = new SmbInfoAllocation();
        info.alloc = ServerMessageBlock.readInt8(buffer, bufferIndex);
        bufferIndex += 8;
        info.free = ServerMessageBlock.readInt8(buffer, bufferIndex);
        bufferIndex += 8;
        bufferIndex += 8;
        info.sectPerAlloc = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        info.bytesPerSect = ServerMessageBlock.readInt4(buffer, bufferIndex);
        bufferIndex += 4;
        this.info = info;
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String("Trans2QueryFSInformationResponse[" + super.toString() + "]");
    }
    
    class SmbInfoAllocation implements AllocInfo
    {
        long alloc;
        long free;
        int sectPerAlloc;
        int bytesPerSect;
        
        public long getCapacity() {
            return this.alloc * this.sectPerAlloc * this.bytesPerSect;
        }
        
        public long getFree() {
            return this.free * this.sectPerAlloc * this.bytesPerSect;
        }
        
        public String toString() {
            return new String("SmbInfoAllocation[alloc=" + this.alloc + ",free=" + this.free + ",sectPerAlloc=" + this.sectPerAlloc + ",bytesPerSect=" + this.bytesPerSect + "]");
        }
    }
}

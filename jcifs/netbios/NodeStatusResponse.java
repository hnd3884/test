package jcifs.netbios;

import java.io.UnsupportedEncodingException;

class NodeStatusResponse extends NameServicePacket
{
    private NbtAddress queryAddress;
    private int numberOfNames;
    private byte[] macAddress;
    private byte[] stats;
    NbtAddress[] addressArray;
    
    NodeStatusResponse(final NbtAddress queryAddress) {
        this.queryAddress = queryAddress;
        this.recordName = new Name();
        this.macAddress = new byte[6];
    }
    
    int writeBodyWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readBodyWireFormat(final byte[] src, final int srcIndex) {
        return this.readResourceRecordWireFormat(src, srcIndex);
    }
    
    int writeRDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readRDataWireFormat(final byte[] src, int srcIndex) {
        final int start = srcIndex;
        this.numberOfNames = (src[srcIndex] & 0xFF);
        final int namesLength = this.numberOfNames * 18;
        final int statsLength = this.rDataLength - namesLength - 1;
        this.numberOfNames = (src[srcIndex++] & 0xFF);
        System.arraycopy(src, srcIndex + namesLength, this.macAddress, 0, 6);
        srcIndex += this.readNodeNameArray(src, srcIndex);
        System.arraycopy(src, srcIndex, this.stats = new byte[statsLength], 0, statsLength);
        srcIndex += statsLength;
        return srcIndex - start;
    }
    
    private int readNodeNameArray(final byte[] src, int srcIndex) {
        final int start = srcIndex;
        this.addressArray = new NbtAddress[this.numberOfNames];
        final String scope = this.queryAddress.hostName.scope;
        boolean addrFound = false;
        try {
            for (int i = 0; i < this.numberOfNames; ++i) {
                int j;
                for (j = srcIndex + 14; src[j] == 32; --j) {}
                final String n = new String(src, srcIndex, j - srcIndex + 1, Name.OEM_ENCODING);
                final int hexCode = src[srcIndex + 15] & 0xFF;
                final boolean groupName = (src[srcIndex + 16] & 0x80) == 0x80;
                final int ownerNodeType = (src[srcIndex + 16] & 0x60) >> 5;
                final boolean isBeingDeleted = (src[srcIndex + 16] & 0x10) == 0x10;
                final boolean isInConflict = (src[srcIndex + 16] & 0x8) == 0x8;
                final boolean isActive = (src[srcIndex + 16] & 0x4) == 0x4;
                final boolean isPermanent = (src[srcIndex + 16] & 0x2) == 0x2;
                if (!addrFound && this.queryAddress.hostName.hexCode == hexCode && (this.queryAddress.hostName == NbtAddress.UNKNOWN_NAME || this.queryAddress.hostName.name.equals(n))) {
                    if (this.queryAddress.hostName == NbtAddress.UNKNOWN_NAME) {
                        this.queryAddress.hostName = new Name(n, hexCode, scope);
                    }
                    this.queryAddress.groupName = groupName;
                    this.queryAddress.nodeType = ownerNodeType;
                    this.queryAddress.isBeingDeleted = isBeingDeleted;
                    this.queryAddress.isInConflict = isInConflict;
                    this.queryAddress.isActive = isActive;
                    this.queryAddress.isPermanent = isPermanent;
                    this.queryAddress.macAddress = this.macAddress;
                    this.queryAddress.isDataFromNodeStatus = true;
                    addrFound = true;
                    this.addressArray[i] = this.queryAddress;
                }
                else {
                    this.addressArray[i] = new NbtAddress(new Name(n, hexCode, scope), this.queryAddress.address, groupName, ownerNodeType, isBeingDeleted, isInConflict, isActive, isPermanent, this.macAddress);
                }
                srcIndex += 18;
            }
        }
        catch (final UnsupportedEncodingException ex) {}
        return srcIndex - start;
    }
    
    public String toString() {
        return new String("NodeStatusResponse[" + super.toString() + "]");
    }
}

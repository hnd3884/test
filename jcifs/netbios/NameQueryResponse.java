package jcifs.netbios;

class NameQueryResponse extends NameServicePacket
{
    NameQueryResponse() {
        this.recordName = new Name();
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
        if (this.resultCode != 0 || this.opCode != 0) {
            return 0;
        }
        final boolean groupName = (src[srcIndex] & 0x80) == 0x80;
        final int nodeType = (src[srcIndex] & 0x60) >> 5;
        srcIndex += 2;
        final int address = NameServicePacket.readInt4(src, srcIndex);
        if (address != 0) {
            this.addrEntry[this.addrIndex] = new NbtAddress(this.recordName, address, groupName, nodeType);
        }
        else {
            this.addrEntry[this.addrIndex] = null;
        }
        return 6;
    }
    
    public String toString() {
        return new String("NameQueryResponse[" + super.toString() + ",addrEntry=" + this.addrEntry + "]");
    }
}

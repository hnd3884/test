package jcifs.netbios;

class NameQueryRequest extends NameServicePacket
{
    NameQueryRequest(final Name name) {
        this.questionName = name;
        this.questionType = 32;
    }
    
    int writeBodyWireFormat(final byte[] dst, final int dstIndex) {
        return this.writeQuestionSectionWireFormat(dst, dstIndex);
    }
    
    int readBodyWireFormat(final byte[] src, final int srcIndex) {
        return this.readQuestionSectionWireFormat(src, srcIndex);
    }
    
    int writeRDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readRDataWireFormat(final byte[] src, final int srcIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("NameQueryRequest[" + super.toString() + "]");
    }
}

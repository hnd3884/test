package jcifs.netbios;

class NodeStatusRequest extends NameServicePacket
{
    NodeStatusRequest(final Name name) {
        this.questionName = name;
        this.questionType = 33;
        this.isRecurDesired = false;
        this.isBroadcast = false;
    }
    
    int writeBodyWireFormat(final byte[] dst, final int dstIndex) {
        final int tmp = this.questionName.hexCode;
        this.questionName.hexCode = 0;
        final int result = this.writeQuestionSectionWireFormat(dst, dstIndex);
        this.questionName.hexCode = tmp;
        return result;
    }
    
    int readBodyWireFormat(final byte[] src, final int srcIndex) {
        return 0;
    }
    
    int writeRDataWireFormat(final byte[] dst, final int dstIndex) {
        return 0;
    }
    
    int readRDataWireFormat(final byte[] src, final int srcIndex) {
        return 0;
    }
    
    public String toString() {
        return new String("NodeStatusRequest[" + super.toString() + "]");
    }
}

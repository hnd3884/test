package jcifs.smb;

class Trans2GetDfsReferralResponse extends SmbComTransactionResponse
{
    int pathConsumed;
    int numReferrals;
    int flags;
    Referral referral;
    
    Trans2GetDfsReferralResponse() {
        this.subCommand = 16;
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
    
    int readDataWireFormat(final byte[] buffer, int bufferIndex, final int len) {
        final int start = bufferIndex;
        this.pathConsumed = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        if ((this.flags2 & 0x8000) != 0x0) {
            this.pathConsumed /= 2;
        }
        this.numReferrals = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 2;
        this.flags = ServerMessageBlock.readInt2(buffer, bufferIndex);
        bufferIndex += 4;
        this.referral = new Referral();
        while (this.numReferrals-- > 0) {
            bufferIndex += this.referral.readWireFormat(buffer, bufferIndex, len);
        }
        if (this.referral.path != null && this.referral.path.charAt(this.pathConsumed - 1) == '\\') {
            --this.pathConsumed;
        }
        return bufferIndex - start;
    }
    
    public String toString() {
        return new String("Trans2GetDfsReferralResponse[" + super.toString() + ",pathConsumed=" + this.pathConsumed + ",numReferrals=" + this.numReferrals + ",flags=" + this.flags + "," + this.referral + "]");
    }
    
    class Referral
    {
        private int version;
        private int size;
        private int serverType;
        private int flags;
        private int proximity;
        private int ttl;
        private int pathOffset;
        private int altPathOffset;
        private int nodeOffset;
        private String path;
        private String altPath;
        String node;
        
        Referral() {
            this.path = null;
        }
        
        int readWireFormat(final byte[] buffer, int bufferIndex, final int len) {
            final int start = bufferIndex;
            this.version = ServerMessageBlock.readInt2(buffer, bufferIndex);
            if (this.version != 3 && this.version != 1) {
                throw new RuntimeException("Version " + this.version + " referral not supported. Please report this to jcifs at samba dot org.");
            }
            bufferIndex += 2;
            this.size = ServerMessageBlock.readInt2(buffer, bufferIndex);
            bufferIndex += 2;
            this.serverType = ServerMessageBlock.readInt2(buffer, bufferIndex);
            bufferIndex += 2;
            this.flags = ServerMessageBlock.readInt2(buffer, bufferIndex);
            bufferIndex += 2;
            if (this.version == 3) {
                this.proximity = ServerMessageBlock.readInt2(buffer, bufferIndex);
                bufferIndex += 2;
                this.ttl = ServerMessageBlock.readInt2(buffer, bufferIndex);
                bufferIndex += 2;
                this.pathOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
                bufferIndex += 2;
                this.altPathOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
                bufferIndex += 2;
                this.nodeOffset = ServerMessageBlock.readInt2(buffer, bufferIndex);
                bufferIndex += 2;
                this.path = Trans2GetDfsReferralResponse.this.readString(buffer, start + this.pathOffset, len, (Trans2GetDfsReferralResponse.this.flags2 & 0x8000) != 0x0);
                this.node = Trans2GetDfsReferralResponse.this.readString(buffer, start + this.nodeOffset, len, (Trans2GetDfsReferralResponse.this.flags2 & 0x8000) != 0x0);
            }
            else if (this.version == 1) {
                this.node = Trans2GetDfsReferralResponse.this.readString(buffer, bufferIndex, len, (Trans2GetDfsReferralResponse.this.flags2 & 0x8000) != 0x0);
            }
            return this.size;
        }
        
        public String toString() {
            return new String("Referral[version=" + this.version + ",size=" + this.size + ",serverType=" + this.serverType + ",flags=" + this.flags + ",proximity=" + this.proximity + ",ttl=" + this.ttl + ",pathOffset=" + this.pathOffset + ",altPathOffset=" + this.altPathOffset + ",nodeOffset=" + this.nodeOffset + ",path=" + this.path + ",altPath=" + this.altPath + ",node=" + this.node + "]");
        }
    }
}

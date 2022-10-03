package jcifs.dcerpc.msrpc;

import jcifs.util.Hexdump;
import jcifs.smb.FileEntry;
import jcifs.dcerpc.ndr.NdrObject;

public class MsrpcShareEnum extends srvsvc.ShareEnumAll
{
    public MsrpcShareEnum(final String server) {
        super("\\\\" + server, 1, new srvsvc.ShareInfoCtr1(), -1, 0, 0);
        this.ptype = 0;
        this.flags = 3;
    }
    
    public FileEntry[] getEntries() {
        final srvsvc.ShareInfoCtr1 ctr = (srvsvc.ShareInfoCtr1)this.info;
        final MsrpcShareInfo1[] entries = new MsrpcShareInfo1[ctr.count];
        for (int i = 0; i < ctr.count; ++i) {
            entries[i] = new MsrpcShareInfo1(ctr.array[i]);
        }
        return entries;
    }
    
    class MsrpcShareInfo1 implements FileEntry
    {
        String netname;
        int type;
        String remark;
        
        MsrpcShareInfo1(final srvsvc.ShareInfo1 info1) {
            this.netname = info1.netname;
            this.type = info1.type;
            this.remark = info1.remark;
        }
        
        public String getName() {
            return this.netname;
        }
        
        public int getType() {
            switch (this.type & 0xFFFF) {
                case 1: {
                    return 32;
                }
                case 3: {
                    return 16;
                }
                default: {
                    return 8;
                }
            }
        }
        
        public int getAttributes() {
            return 17;
        }
        
        public long createTime() {
            return 0L;
        }
        
        public long lastModified() {
            return 0L;
        }
        
        public long length() {
            return 0L;
        }
        
        public String toString() {
            return new String("MsrpcShareInfo1[netName=" + this.netname + ",type=0x" + Hexdump.toHexString(this.type, 8) + ",remark=" + this.remark + "]");
        }
    }
}

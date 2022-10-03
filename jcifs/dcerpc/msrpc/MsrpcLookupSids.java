package jcifs.dcerpc.msrpc;

import jcifs.dcerpc.rpc;
import jcifs.smb.SID;

public class MsrpcLookupSids extends lsarpc.LsarLookupSids
{
    SID[] sids;
    
    public MsrpcLookupSids(final LsaPolicyHandle policyHandle, final SID[] sids) {
        super(policyHandle, new LsarSidArrayX(sids), new lsarpc.LsarRefDomainList(), new lsarpc.LsarTransNameArray(), (short)1, sids.length);
        this.sids = sids;
        this.ptype = 0;
        this.flags = 3;
    }
}

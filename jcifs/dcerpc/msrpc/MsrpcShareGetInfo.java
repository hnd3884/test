package jcifs.dcerpc.msrpc;

import jcifs.smb.SecurityDescriptor;
import jcifs.smb.ACE;
import jcifs.dcerpc.ndr.NdrObject;

public class MsrpcShareGetInfo extends srvsvc.ShareGetInfo
{
    public MsrpcShareGetInfo(final String server, final String sharename) {
        super(server, sharename, 502, new srvsvc.ShareInfo502());
        this.ptype = 0;
        this.flags = 3;
    }
    
    public ACE[] getSecurity() {
        final srvsvc.ShareInfo502 info502 = (srvsvc.ShareInfo502)this.info;
        if (info502.security_descriptor != null) {
            final SecurityDescriptor sd = new SecurityDescriptor(info502.security_descriptor, 0, info502.sd_size);
            return sd.aces;
        }
        return new ACE[0];
    }
}

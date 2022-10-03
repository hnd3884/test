package jcifs.dcerpc.msrpc;

import jcifs.dcerpc.rpc;

public class MsrpcLsarOpenPolicy2 extends lsarpc.LsarOpenPolicy2
{
    public MsrpcLsarOpenPolicy2(final String server, final int access, final LsaPolicyHandle policyHandle) {
        super(server, new lsarpc.LsarObjectAttributes(), access, policyHandle);
        this.object_attributes.length = 24;
        this.ptype = 0;
        this.flags = 3;
    }
}

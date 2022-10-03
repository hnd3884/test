package jcifs.dcerpc.msrpc;

import java.io.IOException;
import jcifs.dcerpc.DcerpcMessage;
import jcifs.dcerpc.DcerpcHandle;
import jcifs.dcerpc.rpc;

public class LsaPolicyHandle extends rpc.policy_handle
{
    public LsaPolicyHandle(final DcerpcHandle handle, String server, final int access) throws IOException {
        if (server == null) {
            server = "\\\\";
        }
        final MsrpcLsarOpenPolicy2 rpc = new MsrpcLsarOpenPolicy2(server, access, this);
        handle.sendrecv(rpc);
    }
    
    public void close() throws IOException {
    }
}

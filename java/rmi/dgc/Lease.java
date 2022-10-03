package java.rmi.dgc;

import java.io.Serializable;

public final class Lease implements Serializable
{
    private VMID vmid;
    private long value;
    private static final long serialVersionUID = -5713411624328831948L;
    
    public Lease(final VMID vmid, final long value) {
        this.vmid = vmid;
        this.value = value;
    }
    
    public VMID getVMID() {
        return this.vmid;
    }
    
    public long getValue() {
        return this.value;
    }
}

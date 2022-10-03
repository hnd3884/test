package java.rmi.dgc;

import java.security.SecureRandom;
import java.rmi.server.UID;
import java.io.Serializable;

public final class VMID implements Serializable
{
    private static final byte[] randomBytes;
    private byte[] addr;
    private UID uid;
    private static final long serialVersionUID = -538642295484486218L;
    
    public VMID() {
        this.addr = VMID.randomBytes;
        this.uid = new UID();
    }
    
    @Deprecated
    public static boolean isUnique() {
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.uid.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof VMID)) {
            return false;
        }
        final VMID vmid = (VMID)o;
        if (!this.uid.equals(vmid.uid)) {
            return false;
        }
        if (this.addr == null ^ vmid.addr == null) {
            return false;
        }
        if (this.addr != null) {
            if (this.addr.length != vmid.addr.length) {
                return false;
            }
            for (int i = 0; i < this.addr.length; ++i) {
                if (this.addr[i] != vmid.addr[i]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.addr != null) {
            for (int i = 0; i < this.addr.length; ++i) {
                final int n = this.addr[i] & 0xFF;
                sb.append(((n < 16) ? "0" : "") + Integer.toString(n, 16));
            }
        }
        sb.append(':');
        sb.append(this.uid.toString());
        return sb.toString();
    }
    
    static {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] randomBytes2 = new byte[8];
        secureRandom.nextBytes(randomBytes2);
        randomBytes = randomBytes2;
    }
}

package javax.naming;

public class BinaryRefAddr extends RefAddr
{
    private byte[] buf;
    private static final long serialVersionUID = -3415254970957330361L;
    
    public BinaryRefAddr(final String s, final byte[] array) {
        this(s, array, 0, array.length);
    }
    
    public BinaryRefAddr(final String s, final byte[] array, final int n, final int n2) {
        super(s);
        this.buf = null;
        System.arraycopy(array, n, this.buf = new byte[n2], 0, n2);
    }
    
    @Override
    public Object getContent() {
        return this.buf;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof BinaryRefAddr) {
            final BinaryRefAddr binaryRefAddr = (BinaryRefAddr)o;
            if (this.addrType.compareTo(binaryRefAddr.addrType) == 0) {
                if (this.buf == null && binaryRefAddr.buf == null) {
                    return true;
                }
                if (this.buf == null || binaryRefAddr.buf == null || this.buf.length != binaryRefAddr.buf.length) {
                    return false;
                }
                for (int i = 0; i < this.buf.length; ++i) {
                    if (this.buf[i] != binaryRefAddr.buf[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.addrType.hashCode();
        for (int i = 0; i < this.buf.length; ++i) {
            hashCode += this.buf[i];
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Address Type: " + this.addrType + "\n");
        sb.append("AddressContents: ");
        for (int n = 0; n < this.buf.length && n < 32; ++n) {
            sb.append(Integer.toHexString(this.buf[n]) + " ");
        }
        if (this.buf.length >= 32) {
            sb.append(" ...\n");
        }
        return sb.toString();
    }
}

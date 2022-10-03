package jcifs.smb;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

class MIEName
{
    private static byte[] TOK_ID;
    private static int TOK_ID_SIZE;
    private static int MECH_OID_LEN_SIZE;
    private static int NAME_LEN_SIZE;
    private Oid oid;
    private String name;
    
    MIEName(final byte[] buf) {
        if (buf.length < MIEName.TOK_ID_SIZE + MIEName.MECH_OID_LEN_SIZE) {
            throw new IllegalArgumentException();
        }
        int i;
        for (i = 0; i < MIEName.TOK_ID.length; ++i) {
            if (MIEName.TOK_ID[i] != buf[i]) {
                throw new IllegalArgumentException();
            }
        }
        int len = 0xFF00 & buf[i++] << 8;
        len |= (0xFF & buf[i++]);
        if (buf.length < i + len) {
            throw new IllegalArgumentException();
        }
        final byte[] bo = new byte[len];
        System.arraycopy(buf, i, bo, 0, len);
        i += len;
        try {
            this.oid = new Oid(bo);
        }
        catch (final GSSException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (buf.length < i + MIEName.NAME_LEN_SIZE) {
            throw new IllegalArgumentException();
        }
        len = (0xFF000000 & buf[i++] << 24);
        len |= (0xFF0000 & buf[i++] << 16);
        len |= (0xFF00 & buf[i++] << 8);
        len |= (0xFF & buf[i++]);
        if (buf.length < i + len) {
            throw new IllegalArgumentException();
        }
        this.name = new String(buf, i, len);
    }
    
    MIEName(final Oid oid, final String name) {
        this.oid = oid;
        this.name = name;
    }
    
    public boolean equals(final Object arg0) {
        try {
            final MIEName terg = (MIEName)arg0;
            if (this.oid.equals(terg.oid) && this.name.equalsIgnoreCase(terg.name)) {
                return true;
            }
        }
        catch (final Throwable t) {}
        return false;
    }
    
    public int hashCode() {
        return this.oid.hashCode();
    }
    
    public String toString() {
        return this.name;
    }
    
    static {
        MIEName.TOK_ID = new byte[] { 4, 1 };
        MIEName.TOK_ID_SIZE = 2;
        MIEName.MECH_OID_LEN_SIZE = 2;
        MIEName.NAME_LEN_SIZE = 4;
    }
}

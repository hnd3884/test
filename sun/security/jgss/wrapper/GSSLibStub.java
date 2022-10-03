package sun.security.jgss.wrapper;

import org.ietf.jgss.MessageProp;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import java.util.Hashtable;
import org.ietf.jgss.Oid;

class GSSLibStub
{
    private Oid mech;
    private long pMech;
    private static Hashtable<Oid, GSSLibStub> table;
    
    static native boolean init(final String p0, final boolean p1);
    
    private static native long getMechPtr(final byte[] p0);
    
    static native Oid[] indicateMechs();
    
    native Oid[] inquireNamesForMech() throws GSSException;
    
    native void releaseName(final long p0);
    
    native long importName(final byte[] p0, final Oid p1);
    
    native boolean compareName(final long p0, final long p1);
    
    native long canonicalizeName(final long p0);
    
    native byte[] exportName(final long p0) throws GSSException;
    
    native Object[] displayName(final long p0) throws GSSException;
    
    native long acquireCred(final long p0, final int p1, final int p2) throws GSSException;
    
    native long releaseCred(final long p0);
    
    native long getCredName(final long p0);
    
    native int getCredTime(final long p0);
    
    native int getCredUsage(final long p0);
    
    native NativeGSSContext importContext(final byte[] p0);
    
    native byte[] initContext(final long p0, final long p1, final ChannelBinding p2, final byte[] p3, final NativeGSSContext p4);
    
    native byte[] acceptContext(final long p0, final ChannelBinding p1, final byte[] p2, final NativeGSSContext p3);
    
    native long[] inquireContext(final long p0);
    
    native Oid getContextMech(final long p0);
    
    native long getContextName(final long p0, final boolean p1);
    
    native int getContextTime(final long p0);
    
    native long deleteContext(final long p0);
    
    native int wrapSizeLimit(final long p0, final int p1, final int p2, final int p3);
    
    native byte[] exportContext(final long p0);
    
    native byte[] getMic(final long p0, final int p1, final byte[] p2);
    
    native void verifyMic(final long p0, final byte[] p1, final byte[] p2, final MessageProp p3);
    
    native byte[] wrap(final long p0, final byte[] p1, final MessageProp p2);
    
    native byte[] unwrap(final long p0, final byte[] p1, final MessageProp p2);
    
    static GSSLibStub getInstance(final Oid oid) throws GSSException {
        GSSLibStub gssLibStub = GSSLibStub.table.get(oid);
        if (gssLibStub == null) {
            gssLibStub = new GSSLibStub(oid);
            GSSLibStub.table.put(oid, gssLibStub);
        }
        return gssLibStub;
    }
    
    private GSSLibStub(final Oid mech) throws GSSException {
        SunNativeProvider.debug("Created GSSLibStub for mech " + mech);
        this.mech = mech;
        this.pMech = getMechPtr(mech.getDER());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof GSSLibStub && this.mech.equals(((GSSLibStub)o).getMech()));
    }
    
    @Override
    public int hashCode() {
        return this.mech.hashCode();
    }
    
    Oid getMech() {
        return this.mech;
    }
    
    static {
        GSSLibStub.table = new Hashtable<Oid, GSSLibStub>(5);
    }
}

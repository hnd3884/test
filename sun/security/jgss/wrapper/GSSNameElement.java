package sun.security.jgss.wrapper;

import sun.security.util.DerInputStream;
import java.security.Provider;
import java.security.Permission;
import javax.security.auth.kerberos.ServicePermission;
import sun.security.krb5.Realm;
import java.io.IOException;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerOutputStream;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;
import sun.security.jgss.GSSUtil;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;

public class GSSNameElement implements GSSNameSpi
{
    long pName;
    private String printableName;
    private Oid printableType;
    private GSSLibStub cStub;
    static final GSSNameElement DEF_ACCEPTOR;
    
    private static Oid getNativeNameType(final Oid oid, GSSLibStub instance) {
        if (GSSUtil.NT_GSS_KRB5_PRINCIPAL.equals(oid)) {
            Oid[] array = null;
            try {
                array = instance.inquireNamesForMech();
            }
            catch (final GSSException ex) {
                if (ex.getMajor() == 2 && GSSUtil.isSpNegoMech(instance.getMech())) {
                    try {
                        instance = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
                        array = instance.inquireNamesForMech();
                    }
                    catch (final GSSException ex2) {
                        SunNativeProvider.debug("Name type list unavailable: " + ex2.getMajorString());
                    }
                }
                else {
                    SunNativeProvider.debug("Name type list unavailable: " + ex.getMajorString());
                }
            }
            if (array != null) {
                for (int i = 0; i < array.length; ++i) {
                    if (array[i].equals(oid)) {
                        return oid;
                    }
                }
                SunNativeProvider.debug("Override " + oid + " with mechanism default(null)");
                return null;
            }
        }
        return oid;
    }
    
    private GSSNameElement() {
        this.pName = 0L;
        this.printableName = "<DEFAULT ACCEPTOR>";
    }
    
    GSSNameElement(final long pName, final GSSLibStub cStub) throws GSSException {
        this.pName = 0L;
        assert cStub != null;
        if (pName == 0L) {
            throw new GSSException(3);
        }
        this.pName = pName;
        this.cStub = cStub;
        this.setPrintables();
    }
    
    GSSNameElement(final byte[] array, Oid nativeNameType, final GSSLibStub cStub) throws GSSException {
        this.pName = 0L;
        assert cStub != null;
        if (array == null) {
            throw new GSSException(3);
        }
        this.cStub = cStub;
        byte[] array2 = array;
        if (nativeNameType != null) {
            nativeNameType = getNativeNameType(nativeNameType, cStub);
            if (GSSName.NT_EXPORT_NAME.equals(nativeNameType)) {
                final DerOutputStream derOutputStream = new DerOutputStream();
                final Oid mech = this.cStub.getMech();
                try {
                    derOutputStream.putOID(new ObjectIdentifier(mech.toString()));
                }
                catch (final IOException ex) {
                    throw new GSSExceptionImpl(11, ex);
                }
                final byte[] byteArray = derOutputStream.toByteArray();
                array2 = new byte[4 + byteArray.length + 4 + array.length];
                int n = 0;
                array2[n++] = 4;
                array2[n++] = 1;
                array2[n++] = (byte)(byteArray.length >>> 8);
                array2[n++] = (byte)byteArray.length;
                System.arraycopy(byteArray, 0, array2, n, byteArray.length);
                int n2 = n + byteArray.length;
                array2[n2++] = (byte)(array.length >>> 24);
                array2[n2++] = (byte)(array.length >>> 16);
                array2[n2++] = (byte)(array.length >>> 8);
                array2[n2++] = (byte)array.length;
                System.arraycopy(array, 0, array2, n2, array.length);
            }
        }
        this.pName = this.cStub.importName(array2, nativeNameType);
        this.setPrintables();
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null && !Realm.AUTODEDUCEREALM) {
            final String krbName = this.getKrbName();
            final int lastIndex = krbName.lastIndexOf(64);
            if (lastIndex != -1) {
                final String substring = krbName.substring(lastIndex);
                if ((nativeNameType != null && !nativeNameType.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL)) || !new String(array).endsWith(substring)) {
                    try {
                        securityManager.checkPermission(new ServicePermission(substring, "-"));
                    }
                    catch (final SecurityException ex2) {
                        throw new GSSException(11);
                    }
                }
            }
        }
        SunNativeProvider.debug("Imported " + this.printableName + " w/ type " + this.printableType);
    }
    
    private void setPrintables() throws GSSException {
        final Object[] displayName = this.cStub.displayName(this.pName);
        assert displayName != null && displayName.length == 2;
        this.printableName = (String)displayName[0];
        assert this.printableName != null;
        this.printableType = (Oid)displayName[1];
        if (this.printableType == null) {
            this.printableType = GSSName.NT_USER_NAME;
        }
    }
    
    public String getKrbName() throws GSSException {
        GSSLibStub gssLibStub = this.cStub;
        if (!GSSUtil.isKerberosMech(this.cStub.getMech())) {
            gssLibStub = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
        }
        final long canonicalizeName = gssLibStub.canonicalizeName(this.pName);
        final Object[] displayName = gssLibStub.displayName(canonicalizeName);
        gssLibStub.releaseName(canonicalizeName);
        SunNativeProvider.debug("Got kerberized name: " + displayName[0]);
        return (String)displayName[0];
    }
    
    @Override
    public Provider getProvider() {
        return SunNativeProvider.INSTANCE;
    }
    
    @Override
    public boolean equals(final GSSNameSpi gssNameSpi) throws GSSException {
        return gssNameSpi instanceof GSSNameElement && this.cStub.compareName(this.pName, ((GSSNameElement)gssNameSpi).pName);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GSSNameElement)) {
            return false;
        }
        try {
            return this.equals((GSSNameSpi)o);
        }
        catch (final GSSException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return new Long(this.pName).hashCode();
    }
    
    @Override
    public byte[] export() throws GSSException {
        final byte[] exportName = this.cStub.exportName(this.pName);
        int n = 0;
        if (exportName[n++] != 4 || exportName[n++] != 1) {
            throw new GSSException(3);
        }
        final int n2 = (0xFF & exportName[n++]) << 8 | (0xFF & exportName[n++]);
        ObjectIdentifier objectIdentifier;
        try {
            objectIdentifier = new ObjectIdentifier(new DerInputStream(exportName, n, n2));
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(3, ex);
        }
        final Oid oid = new Oid(objectIdentifier.toString());
        assert oid.equals(this.getMechanism());
        int n3 = n + n2;
        final int n4 = (0xFF & exportName[n3++]) << 24 | (0xFF & exportName[n3++]) << 16 | (0xFF & exportName[n3++]) << 8 | (0xFF & exportName[n3++]);
        if (n4 < 0) {
            throw new GSSException(3);
        }
        final byte[] array = new byte[n4];
        System.arraycopy(exportName, n3, array, 0, n4);
        return array;
    }
    
    @Override
    public Oid getMechanism() {
        return this.cStub.getMech();
    }
    
    @Override
    public String toString() {
        return this.printableName;
    }
    
    @Override
    public Oid getStringNameType() {
        return this.printableType;
    }
    
    @Override
    public boolean isAnonymousName() {
        return GSSName.NT_ANONYMOUS.equals(this.printableType);
    }
    
    public void dispose() {
        if (this.pName != 0L) {
            this.cStub.releaseName(this.pName);
            this.pName = 0L;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.dispose();
    }
    
    static {
        DEF_ACCEPTOR = new GSSNameElement();
    }
}

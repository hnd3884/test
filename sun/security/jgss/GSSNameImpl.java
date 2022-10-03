package sun.security.jgss;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import sun.security.util.DerOutputStream;
import java.util.Arrays;
import java.io.IOException;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerInputStream;
import java.io.UnsupportedEncodingException;
import org.ietf.jgss.GSSException;
import sun.security.jgss.spi.GSSNameSpi;
import java.util.HashMap;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSName;

public class GSSNameImpl implements GSSName
{
    static final Oid oldHostbasedServiceName;
    private GSSManagerImpl gssManager;
    private String appNameStr;
    private byte[] appNameBytes;
    private Oid appNameType;
    private String printableName;
    private Oid printableNameType;
    private HashMap<Oid, GSSNameSpi> elements;
    private GSSNameSpi mechElement;
    
    static GSSNameImpl wrapElement(final GSSManagerImpl gssManagerImpl, final GSSNameSpi gssNameSpi) throws GSSException {
        return (gssNameSpi == null) ? null : new GSSNameImpl(gssManagerImpl, gssNameSpi);
    }
    
    GSSNameImpl(final GSSManagerImpl gssManager, final GSSNameSpi mechElement) {
        this.gssManager = null;
        this.appNameStr = null;
        this.appNameBytes = null;
        this.appNameType = null;
        this.printableName = null;
        this.printableNameType = null;
        this.elements = null;
        this.mechElement = null;
        this.gssManager = gssManager;
        final String string = mechElement.toString();
        this.printableName = string;
        this.appNameStr = string;
        final Oid stringNameType = mechElement.getStringNameType();
        this.printableNameType = stringNameType;
        this.appNameType = stringNameType;
        this.mechElement = mechElement;
        (this.elements = new HashMap<Oid, GSSNameSpi>(1)).put(mechElement.getMechanism(), this.mechElement);
    }
    
    GSSNameImpl(final GSSManagerImpl gssManagerImpl, final Object o, final Oid oid) throws GSSException {
        this(gssManagerImpl, o, oid, null);
    }
    
    GSSNameImpl(final GSSManagerImpl gssManagerImpl, final Object o, Oid nt_HOSTBASED_SERVICE, Oid default_MECH_OID) throws GSSException {
        this.gssManager = null;
        this.appNameStr = null;
        this.appNameBytes = null;
        this.appNameType = null;
        this.printableName = null;
        this.printableNameType = null;
        this.elements = null;
        this.mechElement = null;
        if (GSSNameImpl.oldHostbasedServiceName.equals(nt_HOSTBASED_SERVICE)) {
            nt_HOSTBASED_SERVICE = GSSName.NT_HOSTBASED_SERVICE;
        }
        if (o == null) {
            throw new GSSExceptionImpl(3, "Cannot import null name");
        }
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        if (GSSNameImpl.NT_EXPORT_NAME.equals(nt_HOSTBASED_SERVICE)) {
            this.importName(gssManagerImpl, o);
        }
        else {
            this.init(gssManagerImpl, o, nt_HOSTBASED_SERVICE, default_MECH_OID);
        }
    }
    
    private void init(final GSSManagerImpl gssManager, final Object o, final Oid oid, final Oid oid2) throws GSSException {
        this.gssManager = gssManager;
        this.elements = new HashMap<Oid, GSSNameSpi>(gssManager.getMechs().length);
        if (o instanceof String) {
            this.appNameStr = (String)o;
            if (oid != null) {
                this.printableName = this.appNameStr;
                this.printableNameType = oid;
            }
        }
        else {
            this.appNameBytes = (byte[])o;
        }
        this.appNameType = oid;
        this.mechElement = this.getElement(oid2);
        if (this.printableName == null) {
            this.printableName = this.mechElement.toString();
            this.printableNameType = this.mechElement.getStringNameType();
        }
    }
    
    private void importName(final GSSManagerImpl gssManagerImpl, final Object o) throws GSSException {
        int n = 0;
        byte[] bytes = null;
        if (o instanceof String) {
            try {
                bytes = ((String)o).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        else {
            bytes = (byte[])o;
        }
        if (bytes[n++] != 4 || bytes[n++] != 1) {
            throw new GSSExceptionImpl(3, "Exported name token id is corrupted!");
        }
        final int n2 = (0xFF & bytes[n++]) << 8 | (0xFF & bytes[n++]);
        ObjectIdentifier objectIdentifier;
        try {
            objectIdentifier = new ObjectIdentifier(new DerInputStream(bytes, n, n2));
        }
        catch (final IOException ex2) {
            throw new GSSExceptionImpl(3, "Exported name Object identifier is corrupted!");
        }
        final Oid oid = new Oid(objectIdentifier.toString());
        int n3 = n + n2;
        final int n4 = (0xFF & bytes[n3++]) << 24 | (0xFF & bytes[n3++]) << 16 | (0xFF & bytes[n3++]) << 8 | (0xFF & bytes[n3++]);
        if (n4 < 0 || n3 > bytes.length - n4) {
            throw new GSSExceptionImpl(3, "Exported name mech name is corrupted!");
        }
        final byte[] array = new byte[n4];
        System.arraycopy(bytes, n3, array, 0, n4);
        this.init(gssManagerImpl, array, GSSNameImpl.NT_EXPORT_NAME, oid);
    }
    
    @Override
    public GSSName canonicalize(Oid default_MECH_OID) throws GSSException {
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        return wrapElement(this.gssManager, this.getElement(default_MECH_OID));
    }
    
    @Override
    public boolean equals(final GSSName gssName) throws GSSException {
        if (this.isAnonymous() || gssName.isAnonymous()) {
            return false;
        }
        if (gssName == this) {
            return true;
        }
        if (!(gssName instanceof GSSNameImpl)) {
            return this.equals(this.gssManager.createName(gssName.toString(), gssName.getStringNameType()));
        }
        final GSSNameImpl gssNameImpl = (GSSNameImpl)gssName;
        GSSNameSpi gssNameSpi = this.mechElement;
        GSSNameSpi gssNameSpi2 = gssNameImpl.mechElement;
        if (gssNameSpi == null && gssNameSpi2 != null) {
            gssNameSpi = this.getElement(gssNameSpi2.getMechanism());
        }
        else if (gssNameSpi != null && gssNameSpi2 == null) {
            gssNameSpi2 = gssNameImpl.getElement(gssNameSpi.getMechanism());
        }
        if (gssNameSpi != null && gssNameSpi2 != null) {
            return gssNameSpi.equals(gssNameSpi2);
        }
        if (this.appNameType == null || gssNameImpl.appNameType == null) {
            return false;
        }
        if (!this.appNameType.equals(gssNameImpl.appNameType)) {
            return false;
        }
        byte[] array = null;
        byte[] array2 = null;
        try {
            array = ((this.appNameStr != null) ? this.appNameStr.getBytes("UTF-8") : this.appNameBytes);
            array2 = ((gssNameImpl.appNameStr != null) ? gssNameImpl.appNameStr.getBytes("UTF-8") : gssNameImpl.appNameBytes);
        }
        catch (final UnsupportedEncodingException ex) {}
        return Arrays.equals(array, array2);
    }
    
    @Override
    public int hashCode() {
        return 1;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            if (o instanceof GSSName) {
                return this.equals((GSSName)o);
            }
        }
        catch (final GSSException ex) {}
        return false;
    }
    
    @Override
    public byte[] export() throws GSSException {
        if (this.mechElement == null) {
            this.mechElement = this.getElement(ProviderList.DEFAULT_MECH_OID);
        }
        final byte[] export = this.mechElement.export();
        ObjectIdentifier objectIdentifier;
        try {
            objectIdentifier = new ObjectIdentifier(this.mechElement.getMechanism().toString());
        }
        catch (final IOException ex) {
            throw new GSSExceptionImpl(11, "Invalid OID String ");
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        try {
            derOutputStream.putOID(objectIdentifier);
        }
        catch (final IOException ex2) {
            throw new GSSExceptionImpl(11, "Could not ASN.1 Encode " + objectIdentifier.toString());
        }
        final byte[] byteArray = derOutputStream.toByteArray();
        final byte[] array = new byte[4 + byteArray.length + 4 + export.length];
        int n = 0;
        array[n++] = 4;
        array[n++] = 1;
        array[n++] = (byte)(byteArray.length >>> 8);
        array[n++] = (byte)byteArray.length;
        System.arraycopy(byteArray, 0, array, n, byteArray.length);
        int n2 = n + byteArray.length;
        array[n2++] = (byte)(export.length >>> 24);
        array[n2++] = (byte)(export.length >>> 16);
        array[n2++] = (byte)(export.length >>> 8);
        array[n2++] = (byte)export.length;
        System.arraycopy(export, 0, array, n2, export.length);
        return array;
    }
    
    @Override
    public String toString() {
        return this.printableName;
    }
    
    @Override
    public Oid getStringNameType() throws GSSException {
        return this.printableNameType;
    }
    
    @Override
    public boolean isAnonymous() {
        return this.printableNameType != null && GSSName.NT_ANONYMOUS.equals(this.printableNameType);
    }
    
    @Override
    public boolean isMN() {
        return true;
    }
    
    public synchronized GSSNameSpi getElement(final Oid oid) throws GSSException {
        GSSNameSpi gssNameSpi = this.elements.get(oid);
        if (gssNameSpi == null) {
            if (this.appNameStr != null) {
                gssNameSpi = this.gssManager.getNameElement(this.appNameStr, this.appNameType, oid);
            }
            else {
                gssNameSpi = this.gssManager.getNameElement(this.appNameBytes, this.appNameType, oid);
            }
            this.elements.put(oid, gssNameSpi);
        }
        return gssNameSpi;
    }
    
    Set<GSSNameSpi> getElements() {
        return new HashSet<GSSNameSpi>(this.elements.values());
    }
    
    private static String getNameTypeStr(final Oid oid) {
        if (oid == null) {
            return "(NT is null)";
        }
        if (oid.equals(GSSNameImpl.NT_USER_NAME)) {
            return "NT_USER_NAME";
        }
        if (oid.equals(GSSNameImpl.NT_HOSTBASED_SERVICE)) {
            return "NT_HOSTBASED_SERVICE";
        }
        if (oid.equals(GSSNameImpl.NT_EXPORT_NAME)) {
            return "NT_EXPORT_NAME";
        }
        if (oid.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL)) {
            return "NT_GSS_KRB5_PRINCIPAL";
        }
        return "Unknown";
    }
    
    static {
        Oid oldHostbasedServiceName2 = null;
        try {
            oldHostbasedServiceName2 = new Oid("1.3.6.1.5.6.2");
        }
        catch (final Exception ex) {}
        oldHostbasedServiceName = oldHostbasedServiceName2;
    }
}

package sun.security.x509;

import java.util.Arrays;
import sun.security.util.DerOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.ObjectIdentifier;

public class OtherName implements GeneralNameInterface
{
    private String name;
    private ObjectIdentifier oid;
    private byte[] nameValue;
    private GeneralNameInterface gni;
    private static final byte TAG_VALUE = 0;
    private int myhash;
    
    public OtherName(final ObjectIdentifier oid, final byte[] nameValue) throws IOException {
        this.nameValue = null;
        this.gni = null;
        this.myhash = -1;
        if (oid == null || nameValue == null) {
            throw new NullPointerException("parameters may not be null");
        }
        this.oid = oid;
        this.nameValue = nameValue;
        this.gni = this.getGNI(oid, nameValue);
        if (this.gni != null) {
            this.name = this.gni.toString();
        }
        else {
            this.name = "Unrecognized ObjectIdentifier: " + oid.toString();
        }
    }
    
    public OtherName(final DerValue derValue) throws IOException {
        this.nameValue = null;
        this.gni = null;
        this.myhash = -1;
        final DerInputStream derInputStream = derValue.toDerInputStream();
        this.oid = derInputStream.getOID();
        this.nameValue = derInputStream.getDerValue().toByteArray();
        this.gni = this.getGNI(this.oid, this.nameValue);
        if (this.gni != null) {
            this.name = this.gni.toString();
        }
        else {
            this.name = "Unrecognized ObjectIdentifier: " + this.oid.toString();
        }
    }
    
    public ObjectIdentifier getOID() {
        return this.oid;
    }
    
    public byte[] getNameValue() {
        return this.nameValue.clone();
    }
    
    private GeneralNameInterface getGNI(final ObjectIdentifier objectIdentifier, final byte[] array) throws IOException {
        try {
            final Class<?> class1 = OIDMap.getClass(objectIdentifier);
            if (class1 == null) {
                return null;
            }
            return (GeneralNameInterface)class1.getConstructor(Object.class).newInstance(array);
        }
        catch (final Exception ex) {
            throw new IOException("Instantiation error: " + ex, ex);
        }
    }
    
    @Override
    public int getType() {
        return 0;
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        if (this.gni != null) {
            this.gni.encode(derOutputStream);
            return;
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOID(this.oid);
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), this.nameValue);
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OtherName)) {
            return false;
        }
        final OtherName otherName = (OtherName)o;
        if (!otherName.oid.equals((Object)this.oid)) {
            return false;
        }
        GeneralNameInterface gni;
        try {
            gni = this.getGNI(otherName.oid, otherName.nameValue);
        }
        catch (final IOException ex) {
            return false;
        }
        boolean equals;
        if (gni != null) {
            try {
                equals = (gni.constrains(this) == 0);
            }
            catch (final UnsupportedOperationException ex2) {
                equals = false;
            }
        }
        else {
            equals = Arrays.equals(this.nameValue, otherName.nameValue);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        if (this.myhash == -1) {
            this.myhash = 37 + this.oid.hashCode();
            for (int i = 0; i < this.nameValue.length; ++i) {
                this.myhash = 37 * this.myhash + this.nameValue[i];
            }
        }
        return this.myhash;
    }
    
    @Override
    public String toString() {
        return "Other-Name: " + this.name;
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) {
        int n;
        if (generalNameInterface == null) {
            n = -1;
        }
        else {
            if (generalNameInterface.getType() == 0) {
                throw new UnsupportedOperationException("Narrowing, widening, and matching are not supported for OtherName.");
            }
            n = -1;
        }
        return n;
    }
    
    @Override
    public int subtreeDepth() {
        throw new UnsupportedOperationException("subtreeDepth() not supported for generic OtherName");
    }
}

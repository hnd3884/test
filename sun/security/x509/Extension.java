package sun.security.x509;

import java.util.Arrays;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class Extension implements java.security.cert.Extension
{
    protected ObjectIdentifier extensionId;
    protected boolean critical;
    protected byte[] extensionValue;
    private static final int hashMagic = 31;
    
    public Extension() {
        this.extensionId = null;
        this.critical = false;
        this.extensionValue = null;
    }
    
    public Extension(final DerValue derValue) throws IOException {
        this.extensionId = null;
        this.critical = false;
        this.extensionValue = null;
        final DerInputStream derInputStream = derValue.toDerInputStream();
        this.extensionId = derInputStream.getOID();
        final DerValue derValue2 = derInputStream.getDerValue();
        if (derValue2.tag == 1) {
            this.critical = derValue2.getBoolean();
            this.extensionValue = derInputStream.getDerValue().getOctetString();
        }
        else {
            this.critical = false;
            this.extensionValue = derValue2.getOctetString();
        }
    }
    
    public Extension(final ObjectIdentifier extensionId, final boolean critical, final byte[] array) throws IOException {
        this.extensionId = null;
        this.critical = false;
        this.extensionValue = null;
        this.extensionId = extensionId;
        this.critical = critical;
        this.extensionValue = new DerValue(array).getOctetString();
    }
    
    public Extension(final Extension extension) {
        this.extensionId = null;
        this.critical = false;
        this.extensionValue = null;
        this.extensionId = extension.extensionId;
        this.critical = extension.critical;
        this.extensionValue = extension.extensionValue;
    }
    
    public static Extension newExtension(final ObjectIdentifier extensionId, final boolean critical, final byte[] extensionValue) throws IOException {
        final Extension extension = new Extension();
        extension.extensionId = extensionId;
        extension.critical = critical;
        extension.extensionValue = extensionValue;
        return extension;
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new NullPointerException();
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.putOID(this.extensionId);
        if (this.critical) {
            derOutputStream.putBoolean(this.critical);
        }
        derOutputStream.putOctetString(this.extensionValue);
        derOutputStream2.write((byte)48, derOutputStream);
        outputStream.write(derOutputStream2.toByteArray());
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        if (this.extensionId == null) {
            throw new IOException("Null OID to encode for the extension!");
        }
        if (this.extensionValue == null) {
            throw new IOException("No value to encode for the extension!");
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOID(this.extensionId);
        if (this.critical) {
            derOutputStream2.putBoolean(this.critical);
        }
        derOutputStream2.putOctetString(this.extensionValue);
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public boolean isCritical() {
        return this.critical;
    }
    
    public ObjectIdentifier getExtensionId() {
        return this.extensionId;
    }
    
    @Override
    public byte[] getValue() {
        return this.extensionValue.clone();
    }
    
    public byte[] getExtensionValue() {
        return this.extensionValue;
    }
    
    @Override
    public String getId() {
        return this.extensionId.toString();
    }
    
    @Override
    public String toString() {
        final String string = "ObjectId: " + this.extensionId.toString();
        String s;
        if (this.critical) {
            s = string + " Criticality=true\n";
        }
        else {
            s = string + " Criticality=false\n";
        }
        return s;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.extensionValue != null) {
            final byte[] extensionValue = this.extensionValue;
            for (int i = extensionValue.length; i > 0; n += i * extensionValue[--i]) {}
        }
        return (n * 31 + this.extensionId.hashCode()) * 31 + (this.critical ? 1231 : 1237);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Extension)) {
            return false;
        }
        final Extension extension = (Extension)o;
        return this.critical == extension.critical && this.extensionId.equals((Object)extension.extensionId) && Arrays.equals(this.extensionValue, extension.extensionValue);
    }
}

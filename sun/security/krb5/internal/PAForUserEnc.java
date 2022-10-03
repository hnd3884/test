package sun.security.krb5.internal;

import java.io.ByteArrayOutputStream;
import sun.security.krb5.Checksum;
import sun.security.util.DerOutputStream;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.util.KerberosString;
import java.io.IOException;
import sun.security.krb5.RealmException;
import sun.security.krb5.Realm;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;

public class PAForUserEnc
{
    public final PrincipalName name;
    private final EncryptionKey key;
    public static final String AUTH_PACKAGE = "Kerberos";
    
    public PAForUserEnc(final PrincipalName name, final EncryptionKey key) {
        this.name = name;
        this.key = key;
    }
    
    public PAForUserEnc(final DerValue derValue, final EncryptionKey key) throws Asn1Exception, KrbException, IOException {
        this.key = key;
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        PrincipalName principalName = null;
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) == 0x0) {
            try {
                principalName = new PrincipalName(derValue2.getData().getDerValue(), new Realm("PLACEHOLDER"));
            }
            catch (final RealmException ex) {}
            final DerValue derValue3 = derValue.getData().getDerValue();
            if ((derValue3.getTag() & 0x1F) == 0x1) {
                Label_0180: {
                    try {
                        this.name = new PrincipalName(principalName.getNameType(), principalName.getNameStrings(), new Realm(derValue3.getData().getDerValue()));
                        break Label_0180;
                    }
                    catch (final RealmException ex2) {
                        throw new IOException(ex2);
                    }
                    throw new Asn1Exception(906);
                }
                if ((derValue.getData().getDerValue().getTag() & 0x1F) != 0x2) {
                    throw new Asn1Exception(906);
                }
                final DerValue derValue4 = derValue.getData().getDerValue();
                if ((derValue4.getTag() & 0x1F) != 0x3) {
                    throw new Asn1Exception(906);
                }
                if (!new KerberosString(derValue4.getData().getDerValue()).toString().equalsIgnoreCase("Kerberos")) {
                    throw new IOException("Incorrect auth-package");
                }
                if (derValue.getData().available() > 0) {
                    throw new Asn1Exception(906);
                }
                return;
            }
            throw new Asn1Exception(906);
        }
        throw new Asn1Exception(906);
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), this.name.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), this.name.getRealm().asn1Encode());
        try {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), new Checksum(-138, this.getS4UByteArray(), this.key, 17).asn1Encode());
        }
        catch (final KrbException ex) {
            throw new IOException(ex);
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putDerValue(new KerberosString("Kerberos").toDerValue());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write((byte)48, derOutputStream);
        return derOutputStream3.toByteArray();
    }
    
    public byte[] getS4UByteArray() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(new byte[4]);
            final String[] nameStrings = this.name.getNameStrings();
            for (int length = nameStrings.length, i = 0; i < length; ++i) {
                byteArrayOutputStream.write(nameStrings[i].getBytes("UTF-8"));
            }
            byteArrayOutputStream.write(this.name.getRealm().toString().getBytes("UTF-8"));
            byteArrayOutputStream.write("Kerberos".getBytes("UTF-8"));
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            final int nameType = this.name.getNameType();
            byteArray[0] = (byte)(nameType & 0xFF);
            byteArray[1] = (byte)(nameType >> 8 & 0xFF);
            byteArray[2] = (byte)(nameType >> 16 & 0xFF);
            byteArray[3] = (byte)(nameType >> 24 & 0xFF);
            return byteArray;
        }
        catch (final IOException ex) {
            throw new AssertionError("Cannot write ByteArrayOutputStream", ex);
        }
    }
    
    public PrincipalName getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "PA-FOR-USER: " + this.name;
    }
}

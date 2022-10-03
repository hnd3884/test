package sun.security.krb5.internal;

import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosFlags;

public class APOptions extends KerberosFlags
{
    public APOptions() {
        super(32);
    }
    
    public APOptions(final int n) throws Asn1Exception {
        super(32);
        this.set(n, true);
    }
    
    public APOptions(final int n, final byte[] array) throws Asn1Exception {
        super(n, array);
        if (n > array.length * 8 || n > 32) {
            throw new Asn1Exception(502);
        }
    }
    
    public APOptions(final boolean[] array) throws Asn1Exception {
        super(array);
        if (array.length > 32) {
            throw new Asn1Exception(502);
        }
    }
    
    public APOptions(final DerValue derValue) throws IOException, Asn1Exception {
        this(derValue.getUnalignedBitString(true).toBooleanArray());
    }
    
    public static APOptions parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new APOptions(derValue.getData().getDerValue());
    }
}

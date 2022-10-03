package sun.security.x509;

import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.BitArray;

public class UniqueIdentity
{
    private BitArray id;
    
    public UniqueIdentity(final BitArray id) {
        this.id = id;
    }
    
    public UniqueIdentity(final byte[] array) {
        this.id = new BitArray(array.length * 8, array);
    }
    
    public UniqueIdentity(final DerInputStream derInputStream) throws IOException {
        this.id = derInputStream.getDerValue().getUnalignedBitString(true);
    }
    
    public UniqueIdentity(final DerValue derValue) throws IOException {
        this.id = derValue.getUnalignedBitString(true);
    }
    
    @Override
    public String toString() {
        return "UniqueIdentity:" + this.id.toString() + "\n";
    }
    
    public void encode(final DerOutputStream derOutputStream, final byte b) throws IOException {
        final byte[] byteArray = this.id.toByteArray();
        final int n = byteArray.length * 8 - this.id.length();
        derOutputStream.write(b);
        derOutputStream.putLength(byteArray.length + 1);
        derOutputStream.write(n);
        derOutputStream.write(byteArray);
    }
    
    public boolean[] getId() {
        if (this.id == null) {
            return null;
        }
        return this.id.toBooleanArray();
    }
}

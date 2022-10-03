package sun.security.rsa;

import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public final class MGF1
{
    private final MessageDigest md;
    
    MGF1(final String s) throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance(s);
    }
    
    void generateAndXor(final byte[] array, final int n, final int n2, int i, final byte[] array2, int n3) throws RuntimeException {
        final byte[] array3 = new byte[4];
        final byte[] array4 = new byte[this.md.getDigestLength()];
        while (i > 0) {
            this.md.update(array, n, n2);
            this.md.update(array3);
            try {
                this.md.digest(array4, 0, array4.length);
            }
            catch (final DigestException ex) {
                throw new RuntimeException(ex.toString());
            }
            int n5;
            for (int n4 = 0; n4 < array4.length && i > 0; array2[n5] ^= array4[n4++], --i) {
                n5 = n3++;
            }
            if (i > 0) {
                int n6 = array3.length - 1;
                while (true) {
                    final byte[] array5 = array3;
                    final int n7 = n6;
                    final byte b = (byte)(array5[n7] + 1);
                    array5[n7] = b;
                    if (b != 0 || n6 <= 0) {
                        break;
                    }
                    --n6;
                }
            }
        }
    }
    
    String getName() {
        return "MGF1" + this.md.getAlgorithm();
    }
}

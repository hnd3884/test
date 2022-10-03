package sun.security.krb5.internal.crypto;

public class Crc32CksumType extends CksumType
{
    @Override
    public int confounderSize() {
        return 0;
    }
    
    @Override
    public int cksumType() {
        return 1;
    }
    
    @Override
    public boolean isKeyed() {
        return false;
    }
    
    @Override
    public int cksumSize() {
        return 4;
    }
    
    @Override
    public int keyType() {
        return 0;
    }
    
    @Override
    public int keySize() {
        return 0;
    }
    
    @Override
    public byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2) {
        return crc32.byte2crc32sum_bytes(array, n);
    }
    
    @Override
    public boolean verifyChecksum(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2) {
        return CksumType.isChecksumEqual(array3, crc32.byte2crc32sum_bytes(array));
    }
    
    public static byte[] int2quad(final long n) {
        final byte[] array = new byte[4];
        for (int i = 0; i < 4; ++i) {
            array[i] = (byte)(n >>> i * 8 & 0xFFL);
        }
        return array;
    }
    
    public static long bytes2long(final byte[] array) {
        return 0x0L | ((long)array[0] & 0xFFL) << 24 | ((long)array[1] & 0xFFL) << 16 | ((long)array[2] & 0xFFL) << 8 | ((long)array[3] & 0xFFL);
    }
}

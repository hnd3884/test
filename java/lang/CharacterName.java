package java.lang;

import java.io.DataInputStream;
import java.util.zip.InflaterInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.lang.ref.SoftReference;

class CharacterName
{
    private static SoftReference<byte[]> refStrPool;
    private static int[][] lookup;
    
    private static synchronized byte[] initNamePool() {
        byte[] array = null;
        if (CharacterName.refStrPool != null && (array = CharacterName.refStrPool.get()) != null) {
            return array;
        }
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new InflaterInputStream(AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    return this.getClass().getResourceAsStream("uniName.dat");
                }
            })));
            CharacterName.lookup = new int[4352][];
            final int int1 = dataInputStream.readInt();
            final int int2 = dataInputStream.readInt();
            final byte[] array2 = new byte[int2];
            dataInputStream.readFully(array2);
            int n = 0;
            int i = 0;
            int n2 = 0;
            do {
                int n3 = array2[i++] & 0xFF;
                if (n3 == 0) {
                    n3 = (array2[i++] & 0xFF);
                    n2 = ((array2[i++] & 0xFF) << 16 | (array2[i++] & 0xFF) << 8 | (array2[i++] & 0xFF));
                }
                else {
                    ++n2;
                }
                final int n4 = n2 >> 8;
                if (CharacterName.lookup[n4] == null) {
                    CharacterName.lookup[n4] = new int[256];
                }
                CharacterName.lookup[n4][n2 & 0xFF] = (n << 8 | n3);
                n += n3;
            } while (i < int2);
            array = new byte[int1 - int2];
            dataInputStream.readFully(array);
            CharacterName.refStrPool = new SoftReference<byte[]>(array);
        }
        catch (final Exception ex) {
            throw new InternalError(ex.getMessage(), ex);
        }
        finally {
            try {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return array;
    }
    
    public static String get(final int n) {
        byte[] initNamePool;
        if (CharacterName.refStrPool == null || (initNamePool = CharacterName.refStrPool.get()) == null) {
            initNamePool = initNamePool();
        }
        final int n2;
        if (CharacterName.lookup[n >> 8] == null || (n2 = CharacterName.lookup[n >> 8][n & 0xFF]) == 0) {
            return null;
        }
        return new String(initNamePool, 0, n2 >>> 8, n2 & 0xFF);
    }
}

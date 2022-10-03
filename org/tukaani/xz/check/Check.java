package org.tukaani.xz.check;

import org.tukaani.xz.UnsupportedOptionsException;
import java.security.NoSuchAlgorithmException;

public abstract class Check
{
    int size;
    String name;
    
    public abstract void update(final byte[] p0, final int p1, final int p2);
    
    public abstract byte[] finish();
    
    public void update(final byte[] array) {
        this.update(array, 0, array.length);
    }
    
    public int getSize() {
        return this.size;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static Check getInstance(final int n) throws UnsupportedOptionsException {
        switch (n) {
            case 0: {
                return new None();
            }
            case 1: {
                return new CRC32();
            }
            case 4: {
                return new CRC64();
            }
            case 10: {
                try {
                    return new SHA256();
                }
                catch (final NoSuchAlgorithmException ex) {}
                break;
            }
        }
        throw new UnsupportedOptionsException("Unsupported Check ID " + n);
    }
}

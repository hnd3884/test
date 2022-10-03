package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.io.Serializable;

public final class CommandAPDU implements Serializable
{
    private static final long serialVersionUID = 398698301286670877L;
    private static final int MAX_APDU_SIZE = 65544;
    private byte[] apdu;
    private transient int nc;
    private transient int ne;
    private transient int dataOffset;
    
    public CommandAPDU(final byte[] array) {
        this.apdu = array.clone();
        this.parse();
    }
    
    public CommandAPDU(final byte[] array, final int n, final int n2) {
        this.checkArrayBounds(array, n, n2);
        System.arraycopy(array, n, this.apdu = new byte[n2], 0, n2);
        this.parse();
    }
    
    private void checkArrayBounds(final byte[] array, final int n, final int n2) {
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Offset and length must not be negative");
        }
        if (array == null) {
            if (n != 0 && n2 != 0) {
                throw new IllegalArgumentException("offset and length must be 0 if array is null");
            }
        }
        else if (n > array.length - n2) {
            throw new IllegalArgumentException("Offset plus length exceed array size");
        }
    }
    
    public CommandAPDU(final ByteBuffer byteBuffer) {
        byteBuffer.get(this.apdu = new byte[byteBuffer.remaining()]);
        this.parse();
    }
    
    public CommandAPDU(final int n, final int n2, final int n3, final int n4) {
        this(n, n2, n3, n4, null, 0, 0, 0);
    }
    
    public CommandAPDU(final int n, final int n2, final int n3, final int n4, final int n5) {
        this(n, n2, n3, n4, null, 0, 0, n5);
    }
    
    public CommandAPDU(final int n, final int n2, final int n3, final int n4, final byte[] array) {
        this(n, n2, n3, n4, array, 0, arrayLength(array), 0);
    }
    
    public CommandAPDU(final int n, final int n2, final int n3, final int n4, final byte[] array, final int n5, final int n6) {
        this(n, n2, n3, n4, array, n5, n6, 0);
    }
    
    public CommandAPDU(final int n, final int n2, final int n3, final int n4, final byte[] array, final int n5) {
        this(n, n2, n3, n4, array, 0, arrayLength(array), n5);
    }
    
    private static int arrayLength(final byte[] array) {
        return (array != null) ? array.length : 0;
    }
    
    private void parse() {
        if (this.apdu.length < 4) {
            throw new IllegalArgumentException("apdu must be at least 4 bytes long");
        }
        if (this.apdu.length == 4) {
            return;
        }
        final int n = this.apdu[4] & 0xFF;
        if (this.apdu.length == 5) {
            this.ne = ((n == 0) ? 256 : n);
            return;
        }
        if (n != 0) {
            if (this.apdu.length == 5 + n) {
                this.nc = n;
                this.dataOffset = 5;
                return;
            }
            if (this.apdu.length == 6 + n) {
                this.nc = n;
                this.dataOffset = 5;
                final int n2 = this.apdu[this.apdu.length - 1] & 0xFF;
                this.ne = ((n2 == 0) ? 256 : n2);
                return;
            }
            throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + n);
        }
        else {
            if (this.apdu.length < 7) {
                throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + n);
            }
            final int n3 = (this.apdu[5] & 0xFF) << 8 | (this.apdu[6] & 0xFF);
            if (this.apdu.length == 7) {
                this.ne = ((n3 == 0) ? 65536 : n3);
                return;
            }
            if (n3 == 0) {
                throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + n + ", b2||b3=" + n3);
            }
            if (this.apdu.length == 7 + n3) {
                this.nc = n3;
                this.dataOffset = 7;
                return;
            }
            if (this.apdu.length == 9 + n3) {
                this.nc = n3;
                this.dataOffset = 7;
                final int n4 = this.apdu.length - 2;
                final int n5 = (this.apdu[n4] & 0xFF) << 8 | (this.apdu[n4 + 1] & 0xFF);
                this.ne = ((n5 == 0) ? 65536 : n5);
                return;
            }
            throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + n + ", b2||b3=" + n3);
        }
    }
    
    public CommandAPDU(final int n, final int n2, final int n3, final int n4, final byte[] array, final int n5, final int nc, final int ne) {
        this.checkArrayBounds(array, n5, nc);
        if (nc > 65535) {
            throw new IllegalArgumentException("dataLength is too large");
        }
        if (ne < 0) {
            throw new IllegalArgumentException("ne must not be negative");
        }
        if (ne > 65536) {
            throw new IllegalArgumentException("ne is too large");
        }
        this.ne = ne;
        if ((this.nc = nc) == 0) {
            if (ne == 0) {
                this.apdu = new byte[4];
                this.setHeader(n, n2, n3, n4);
            }
            else if (ne <= 256) {
                final byte b = (byte)((ne != 256) ? ((byte)ne) : 0);
                this.apdu = new byte[5];
                this.setHeader(n, n2, n3, n4);
                this.apdu[4] = b;
            }
            else {
                byte b2;
                byte b3;
                if (ne == 65536) {
                    b2 = 0;
                    b3 = 0;
                }
                else {
                    b2 = (byte)(ne >> 8);
                    b3 = (byte)ne;
                }
                this.apdu = new byte[7];
                this.setHeader(n, n2, n3, n4);
                this.apdu[5] = b2;
                this.apdu[6] = b3;
            }
        }
        else if (ne == 0) {
            if (nc <= 255) {
                this.apdu = new byte[5 + nc];
                this.setHeader(n, n2, n3, n4);
                this.apdu[4] = (byte)nc;
                this.dataOffset = 5;
                System.arraycopy(array, n5, this.apdu, 5, nc);
            }
            else {
                this.apdu = new byte[7 + nc];
                this.setHeader(n, n2, n3, n4);
                this.apdu[4] = 0;
                this.apdu[5] = (byte)(nc >> 8);
                this.apdu[6] = (byte)nc;
                this.dataOffset = 7;
                System.arraycopy(array, n5, this.apdu, 7, nc);
            }
        }
        else if (nc <= 255 && ne <= 256) {
            this.apdu = new byte[6 + nc];
            this.setHeader(n, n2, n3, n4);
            this.apdu[4] = (byte)nc;
            this.dataOffset = 5;
            System.arraycopy(array, n5, this.apdu, 5, nc);
            this.apdu[this.apdu.length - 1] = (byte)((ne != 256) ? ((byte)ne) : 0);
        }
        else {
            this.apdu = new byte[9 + nc];
            this.setHeader(n, n2, n3, n4);
            this.apdu[4] = 0;
            this.apdu[5] = (byte)(nc >> 8);
            this.apdu[6] = (byte)nc;
            this.dataOffset = 7;
            System.arraycopy(array, n5, this.apdu, 7, nc);
            if (ne != 65536) {
                final int n6 = this.apdu.length - 2;
                this.apdu[n6] = (byte)(ne >> 8);
                this.apdu[n6 + 1] = (byte)ne;
            }
        }
    }
    
    private void setHeader(final int n, final int n2, final int n3, final int n4) {
        this.apdu[0] = (byte)n;
        this.apdu[1] = (byte)n2;
        this.apdu[2] = (byte)n3;
        this.apdu[3] = (byte)n4;
    }
    
    public int getCLA() {
        return this.apdu[0] & 0xFF;
    }
    
    public int getINS() {
        return this.apdu[1] & 0xFF;
    }
    
    public int getP1() {
        return this.apdu[2] & 0xFF;
    }
    
    public int getP2() {
        return this.apdu[3] & 0xFF;
    }
    
    public int getNc() {
        return this.nc;
    }
    
    public byte[] getData() {
        final byte[] array = new byte[this.nc];
        System.arraycopy(this.apdu, this.dataOffset, array, 0, this.nc);
        return array;
    }
    
    public int getNe() {
        return this.ne;
    }
    
    public byte[] getBytes() {
        return this.apdu.clone();
    }
    
    @Override
    public String toString() {
        return "CommmandAPDU: " + this.apdu.length + " bytes, nc=" + this.nc + ", ne=" + this.ne;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof CommandAPDU && Arrays.equals(this.apdu, ((CommandAPDU)o).apdu));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.apdu);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.apdu = (byte[])objectInputStream.readUnshared();
        this.parse();
    }
}

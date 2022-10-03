package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.Serializable;

public final class ResponseAPDU implements Serializable
{
    private static final long serialVersionUID = 6962744978375594225L;
    private byte[] apdu;
    
    public ResponseAPDU(byte[] apdu) {
        apdu = apdu.clone();
        check(apdu);
        this.apdu = apdu;
    }
    
    private static void check(final byte[] array) {
        if (array.length < 2) {
            throw new IllegalArgumentException("apdu must be at least 2 bytes long");
        }
    }
    
    public int getNr() {
        return this.apdu.length - 2;
    }
    
    public byte[] getData() {
        final byte[] array = new byte[this.apdu.length - 2];
        System.arraycopy(this.apdu, 0, array, 0, array.length);
        return array;
    }
    
    public int getSW1() {
        return this.apdu[this.apdu.length - 2] & 0xFF;
    }
    
    public int getSW2() {
        return this.apdu[this.apdu.length - 1] & 0xFF;
    }
    
    public int getSW() {
        return this.getSW1() << 8 | this.getSW2();
    }
    
    public byte[] getBytes() {
        return this.apdu.clone();
    }
    
    @Override
    public String toString() {
        return "ResponseAPDU: " + this.apdu.length + " bytes, SW=" + Integer.toHexString(this.getSW());
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ResponseAPDU && Arrays.equals(this.apdu, ((ResponseAPDU)o).apdu));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.apdu);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        check(this.apdu = (byte[])objectInputStream.readUnshared());
    }
}

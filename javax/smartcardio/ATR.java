package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.Serializable;

public final class ATR implements Serializable
{
    private static final long serialVersionUID = 6695383790847736493L;
    private byte[] atr;
    private transient int startHistorical;
    private transient int nHistorical;
    
    public ATR(final byte[] array) {
        this.atr = array.clone();
        this.parse();
    }
    
    private void parse() {
        if (this.atr.length < 2) {
            return;
        }
        if (this.atr[0] != 59 && this.atr[0] != 63) {
            return;
        }
        int n = (this.atr[1] & 0xF0) >> 4;
        final int nHistorical = this.atr[1] & 0xF;
        int startHistorical = 2;
        while (n != 0 && startHistorical < this.atr.length) {
            if ((n & 0x1) != 0x0) {
                ++startHistorical;
            }
            if ((n & 0x2) != 0x0) {
                ++startHistorical;
            }
            if ((n & 0x4) != 0x0) {
                ++startHistorical;
            }
            if ((n & 0x8) != 0x0) {
                if (startHistorical >= this.atr.length) {
                    return;
                }
                n = (this.atr[startHistorical++] & 0xF0) >> 4;
            }
            else {
                n = 0;
            }
        }
        final int n2 = startHistorical + nHistorical;
        if (n2 == this.atr.length || n2 == this.atr.length - 1) {
            this.startHistorical = startHistorical;
            this.nHistorical = nHistorical;
        }
    }
    
    public byte[] getBytes() {
        return this.atr.clone();
    }
    
    public byte[] getHistoricalBytes() {
        final byte[] array = new byte[this.nHistorical];
        System.arraycopy(this.atr, this.startHistorical, array, 0, this.nHistorical);
        return array;
    }
    
    @Override
    public String toString() {
        return "ATR: " + this.atr.length + " bytes";
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ATR && Arrays.equals(this.atr, ((ATR)o).atr));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.atr);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.atr = (byte[])objectInputStream.readUnshared();
        this.parse();
    }
}

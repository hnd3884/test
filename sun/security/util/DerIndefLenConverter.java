package sun.security.util;

import java.io.IOException;
import java.util.ArrayList;

class DerIndefLenConverter
{
    private static final int TAG_MASK = 31;
    private static final int FORM_MASK = 32;
    private static final int CLASS_MASK = 192;
    private static final int LEN_LONG = 128;
    private static final int LEN_MASK = 127;
    private static final int SKIP_EOC_BYTES = 2;
    private byte[] data;
    private byte[] newData;
    private int newDataPos;
    private int dataPos;
    private int dataSize;
    private int index;
    private int unresolved;
    private ArrayList<Object> ndefsList;
    private int numOfTotalLenBytes;
    
    private boolean isEOC(final int n) {
        return (n & 0x1F) == 0x0 && (n & 0x20) == 0x0 && (n & 0xC0) == 0x0;
    }
    
    static boolean isLongForm(final int n) {
        return (n & 0x80) == 0x80;
    }
    
    DerIndefLenConverter() {
        this.unresolved = 0;
        this.ndefsList = new ArrayList<Object>();
        this.numOfTotalLenBytes = 0;
    }
    
    static boolean isIndefinite(final int n) {
        return isLongForm(n) && (n & 0x7F) == 0x0;
    }
    
    private void parseTag() throws IOException {
        if (this.dataPos == this.dataSize) {
            return;
        }
        try {
            if (this.isEOC(this.data[this.dataPos]) && this.data[this.dataPos + 1] == 0) {
                int n = 0;
                Integer value = null;
                int i;
                for (i = this.ndefsList.size() - 1; i >= 0; --i) {
                    value = (Integer)this.ndefsList.get(i);
                    if (value instanceof Integer) {
                        break;
                    }
                    n += ((byte[])(Object)value).length - 3;
                }
                if (i < 0) {
                    throw new IOException("EOC does not have matching indefinite-length tag");
                }
                final byte[] lengthBytes = this.getLengthBytes(this.dataPos - value + n);
                this.ndefsList.set(i, lengthBytes);
                --this.unresolved;
                this.numOfTotalLenBytes += lengthBytes.length - 3;
            }
            ++this.dataPos;
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new IOException(ex);
        }
    }
    
    private void writeTag() {
        if (this.dataPos == this.dataSize) {
            return;
        }
        final byte b = this.data[this.dataPos++];
        if (this.isEOC(b) && this.data[this.dataPos] == 0) {
            ++this.dataPos;
            this.writeTag();
        }
        else {
            this.newData[this.newDataPos++] = b;
        }
    }
    
    private int parseLength() throws IOException {
        int n = 0;
        if (this.dataPos == this.dataSize) {
            return n;
        }
        final int n2 = this.data[this.dataPos++] & 0xFF;
        if (isIndefinite(n2)) {
            this.ndefsList.add(new Integer(this.dataPos));
            ++this.unresolved;
            return n;
        }
        if (isLongForm(n2)) {
            final int n3 = n2 & 0x7F;
            if (n3 > 4) {
                throw new IOException("Too much data");
            }
            if (this.dataSize - this.dataPos < n3 + 1) {
                throw new IOException("Too little data");
            }
            for (int i = 0; i < n3; ++i) {
                n = (n << 8) + (this.data[this.dataPos++] & 0xFF);
            }
            if (n < 0) {
                throw new IOException("Invalid length bytes");
            }
        }
        else {
            n = (n2 & 0x7F);
        }
        return n;
    }
    
    private void writeLengthAndValue() throws IOException {
        if (this.dataPos == this.dataSize) {
            return;
        }
        int n = 0;
        final int n2 = this.data[this.dataPos++] & 0xFF;
        if (isIndefinite(n2)) {
            final byte[] array = this.ndefsList.get(this.index++);
            System.arraycopy(array, 0, this.newData, this.newDataPos, array.length);
            this.newDataPos += array.length;
            return;
        }
        if (isLongForm(n2)) {
            for (int n3 = n2 & 0x7F, i = 0; i < n3; ++i) {
                n = (n << 8) + (this.data[this.dataPos++] & 0xFF);
            }
            if (n < 0) {
                throw new IOException("Invalid length bytes");
            }
        }
        else {
            n = (n2 & 0x7F);
        }
        this.writeLength(n);
        this.writeValue(n);
    }
    
    private void writeLength(final int n) {
        if (n < 128) {
            this.newData[this.newDataPos++] = (byte)n;
        }
        else if (n < 256) {
            this.newData[this.newDataPos++] = -127;
            this.newData[this.newDataPos++] = (byte)n;
        }
        else if (n < 65536) {
            this.newData[this.newDataPos++] = -126;
            this.newData[this.newDataPos++] = (byte)(n >> 8);
            this.newData[this.newDataPos++] = (byte)n;
        }
        else if (n < 16777216) {
            this.newData[this.newDataPos++] = -125;
            this.newData[this.newDataPos++] = (byte)(n >> 16);
            this.newData[this.newDataPos++] = (byte)(n >> 8);
            this.newData[this.newDataPos++] = (byte)n;
        }
        else {
            this.newData[this.newDataPos++] = -124;
            this.newData[this.newDataPos++] = (byte)(n >> 24);
            this.newData[this.newDataPos++] = (byte)(n >> 16);
            this.newData[this.newDataPos++] = (byte)(n >> 8);
            this.newData[this.newDataPos++] = (byte)n;
        }
    }
    
    private byte[] getLengthBytes(final int n) {
        int n2 = 0;
        byte[] array;
        if (n < 128) {
            array = new byte[] { 0 };
            array[n2++] = (byte)n;
        }
        else if (n < 256) {
            array = new byte[2];
            array[n2++] = -127;
            array[n2++] = (byte)n;
        }
        else if (n < 65536) {
            array = new byte[3];
            array[n2++] = -126;
            array[n2++] = (byte)(n >> 8);
            array[n2++] = (byte)n;
        }
        else if (n < 16777216) {
            array = new byte[4];
            array[n2++] = -125;
            array[n2++] = (byte)(n >> 16);
            array[n2++] = (byte)(n >> 8);
            array[n2++] = (byte)n;
        }
        else {
            array = new byte[5];
            array[n2++] = -124;
            array[n2++] = (byte)(n >> 24);
            array[n2++] = (byte)(n >> 16);
            array[n2++] = (byte)(n >> 8);
            array[n2++] = (byte)n;
        }
        return array;
    }
    
    private int getNumOfLenBytes(final int n) {
        int n2;
        if (n < 128) {
            n2 = 1;
        }
        else if (n < 256) {
            n2 = 2;
        }
        else if (n < 65536) {
            n2 = 3;
        }
        else if (n < 16777216) {
            n2 = 4;
        }
        else {
            n2 = 5;
        }
        return n2;
    }
    
    private void parseValue(final int n) {
        this.dataPos += n;
    }
    
    private void writeValue(final int n) {
        for (int i = 0; i < n; ++i) {
            this.newData[this.newDataPos++] = this.data[this.dataPos++];
        }
    }
    
    byte[] convert(final byte[] data) throws IOException {
        this.data = data;
        this.dataPos = 0;
        this.index = 0;
        this.dataSize = this.data.length;
        int n = 0;
        while (this.dataPos < this.dataSize) {
            this.parseTag();
            this.parseValue(this.parseLength());
            if (this.unresolved == 0) {
                n = this.dataSize - this.dataPos;
                this.dataSize = this.dataPos;
                break;
            }
        }
        if (this.unresolved != 0) {
            throw new IOException("not all indef len BER resolved");
        }
        this.newData = new byte[this.dataSize + this.numOfTotalLenBytes + n];
        this.dataPos = 0;
        this.newDataPos = 0;
        this.index = 0;
        while (this.dataPos < this.dataSize) {
            this.writeTag();
            this.writeLengthAndValue();
        }
        System.arraycopy(data, this.dataSize, this.newData, this.dataSize + this.numOfTotalLenBytes, n);
        return this.newData;
    }
}

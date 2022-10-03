package java.awt.image;

public class ByteLookupTable extends LookupTable
{
    byte[][] data;
    
    public ByteLookupTable(final int n, final byte[][] array) {
        super(n, array.length);
        this.numComponents = array.length;
        this.numEntries = array[0].length;
        this.data = new byte[this.numComponents][];
        for (int i = 0; i < this.numComponents; ++i) {
            this.data[i] = array[i];
        }
    }
    
    public ByteLookupTable(final int n, final byte[] array) {
        super(n, array.length);
        this.numComponents = 1;
        this.numEntries = array.length;
        (this.data = new byte[1][])[0] = array;
    }
    
    public final byte[][] getTable() {
        return this.data;
    }
    
    @Override
    public int[] lookupPixel(final int[] array, int[] array2) {
        if (array2 == null) {
            array2 = new int[array.length];
        }
        if (this.numComponents == 1) {
            for (int i = 0; i < array.length; ++i) {
                final int n = array[i] - this.offset;
                if (n < 0) {
                    throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
                }
                array2[i] = this.data[0][n];
            }
        }
        else {
            for (int j = 0; j < array.length; ++j) {
                final int n2 = array[j] - this.offset;
                if (n2 < 0) {
                    throw new ArrayIndexOutOfBoundsException("src[" + j + "]-offset is less than zero");
                }
                array2[j] = this.data[j][n2];
            }
        }
        return array2;
    }
    
    public byte[] lookupPixel(final byte[] array, byte[] array2) {
        if (array2 == null) {
            array2 = new byte[array.length];
        }
        if (this.numComponents == 1) {
            for (int i = 0; i < array.length; ++i) {
                final int n = (array[i] & 0xFF) - this.offset;
                if (n < 0) {
                    throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
                }
                array2[i] = this.data[0][n];
            }
        }
        else {
            for (int j = 0; j < array.length; ++j) {
                final int n2 = (array[j] & 0xFF) - this.offset;
                if (n2 < 0) {
                    throw new ArrayIndexOutOfBoundsException("src[" + j + "]-offset is less than zero");
                }
                array2[j] = this.data[j][n2];
            }
        }
        return array2;
    }
}

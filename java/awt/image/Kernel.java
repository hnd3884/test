package java.awt.image;

public class Kernel implements Cloneable
{
    private int width;
    private int height;
    private int xOrigin;
    private int yOrigin;
    private float[] data;
    
    private static native void initIDs();
    
    public Kernel(final int width, final int height, final float[] array) {
        this.width = width;
        this.height = height;
        this.xOrigin = width - 1 >> 1;
        this.yOrigin = height - 1 >> 1;
        final int n = width * height;
        if (array.length < n) {
            throw new IllegalArgumentException("Data array too small (is " + array.length + " and should be " + n);
        }
        System.arraycopy(array, 0, this.data = new float[n], 0, n);
    }
    
    public final int getXOrigin() {
        return this.xOrigin;
    }
    
    public final int getYOrigin() {
        return this.yOrigin;
    }
    
    public final int getWidth() {
        return this.width;
    }
    
    public final int getHeight() {
        return this.height;
    }
    
    public final float[] getKernelData(float[] array) {
        if (array == null) {
            array = new float[this.data.length];
        }
        else if (array.length < this.data.length) {
            throw new IllegalArgumentException("Data array too small (should be " + this.data.length + " but is " + array.length + " )");
        }
        System.arraycopy(this.data, 0, array, 0, this.data.length);
        return array;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
}

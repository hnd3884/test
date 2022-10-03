package sun.java2d.loops;

class SolidPixelWriter extends PixelWriter
{
    protected Object srcData;
    
    SolidPixelWriter(final Object srcData) {
        this.srcData = srcData;
    }
    
    @Override
    public void writePixel(final int n, final int n2) {
        this.dstRast.setDataElements(n, n2, this.srcData);
    }
}

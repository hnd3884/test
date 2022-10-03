package java.awt.image;

public class PixelInterleavedSampleModel extends ComponentSampleModel
{
    public PixelInterleavedSampleModel(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        super(n, n2, n3, n4, n5, array);
        int min = this.bandOffsets[0];
        int max = this.bandOffsets[0];
        for (int i = 1; i < this.bandOffsets.length; ++i) {
            min = Math.min(min, this.bandOffsets[i]);
            max = Math.max(max, this.bandOffsets[i]);
        }
        final int n6 = max - min;
        if (n6 > n5) {
            throw new IllegalArgumentException("Offsets between bands must be less than the scanline  stride");
        }
        if (n4 * n2 > n5) {
            throw new IllegalArgumentException("Pixel stride times width must be less than or equal to the scanline stride");
        }
        if (n4 < n6) {
            throw new IllegalArgumentException("Pixel stride must be greater than or equal to the offsets between bands");
        }
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        int n3 = this.bandOffsets[0];
        final int length = this.bandOffsets.length;
        for (int i = 1; i < length; ++i) {
            if (this.bandOffsets[i] < n3) {
                n3 = this.bandOffsets[i];
            }
        }
        int[] bandOffsets;
        if (n3 > 0) {
            bandOffsets = new int[length];
            for (int j = 0; j < length; ++j) {
                bandOffsets[j] = this.bandOffsets[j] - n3;
            }
        }
        else {
            bandOffsets = this.bandOffsets;
        }
        return new PixelInterleavedSampleModel(this.dataType, n, n2, this.pixelStride, this.pixelStride * n, bandOffsets);
    }
    
    @Override
    public SampleModel createSubsetSampleModel(final int[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.bandOffsets[array[i]];
        }
        return new PixelInterleavedSampleModel(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, array2);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ 0x1;
    }
}

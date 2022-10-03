package java.awt.image;

import java.awt.Rectangle;
import java.util.Set;
import java.util.Vector;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;
import sun.awt.image.ByteComponentRaster;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.util.Hashtable;
import sun.awt.image.OffScreenImageSource;
import java.awt.Transparency;
import java.awt.Image;

public class BufferedImage extends Image implements WritableRenderedImage, Transparency
{
    private int imageType;
    private ColorModel colorModel;
    private final WritableRaster raster;
    private OffScreenImageSource osis;
    private Hashtable<String, Object> properties;
    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_INT_RGB = 1;
    public static final int TYPE_INT_ARGB = 2;
    public static final int TYPE_INT_ARGB_PRE = 3;
    public static final int TYPE_INT_BGR = 4;
    public static final int TYPE_3BYTE_BGR = 5;
    public static final int TYPE_4BYTE_ABGR = 6;
    public static final int TYPE_4BYTE_ABGR_PRE = 7;
    public static final int TYPE_USHORT_565_RGB = 8;
    public static final int TYPE_USHORT_555_RGB = 9;
    public static final int TYPE_BYTE_GRAY = 10;
    public static final int TYPE_USHORT_GRAY = 11;
    public static final int TYPE_BYTE_BINARY = 12;
    public static final int TYPE_BYTE_INDEXED = 13;
    private static final int DCM_RED_MASK = 16711680;
    private static final int DCM_GREEN_MASK = 65280;
    private static final int DCM_BLUE_MASK = 255;
    private static final int DCM_ALPHA_MASK = -16777216;
    private static final int DCM_565_RED_MASK = 63488;
    private static final int DCM_565_GRN_MASK = 2016;
    private static final int DCM_565_BLU_MASK = 31;
    private static final int DCM_555_RED_MASK = 31744;
    private static final int DCM_555_GRN_MASK = 992;
    private static final int DCM_555_BLU_MASK = 31;
    private static final int DCM_BGR_RED_MASK = 255;
    private static final int DCM_BGR_GRN_MASK = 65280;
    private static final int DCM_BGR_BLU_MASK = 16711680;
    
    private static native void initIDs();
    
    public BufferedImage(final int n, final int n2, final int imageType) {
        this.imageType = 0;
        switch (imageType) {
            case 1: {
                this.colorModel = new DirectColorModel(24, 16711680, 65280, 255, 0);
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            case 2: {
                this.colorModel = ColorModel.getRGBdefault();
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            case 3: {
                this.colorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            case 4: {
                this.colorModel = new DirectColorModel(24, 255, 65280, 16711680);
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            case 5: {
                final ColorSpace instance = ColorSpace.getInstance(1000);
                final int[] array = { 8, 8, 8 };
                final int[] array2 = { 2, 1, 0 };
                this.colorModel = new ComponentColorModel(instance, array, false, false, 1, 0);
                this.raster = Raster.createInterleavedRaster(0, n, n2, n * 3, 3, array2, null);
                break;
            }
            case 6: {
                final ColorSpace instance2 = ColorSpace.getInstance(1000);
                final int[] array3 = { 8, 8, 8, 8 };
                final int[] array4 = { 3, 2, 1, 0 };
                this.colorModel = new ComponentColorModel(instance2, array3, true, false, 3, 0);
                this.raster = Raster.createInterleavedRaster(0, n, n2, n * 4, 4, array4, null);
                break;
            }
            case 7: {
                final ColorSpace instance3 = ColorSpace.getInstance(1000);
                final int[] array5 = { 8, 8, 8, 8 };
                final int[] array6 = { 3, 2, 1, 0 };
                this.colorModel = new ComponentColorModel(instance3, array5, true, true, 3, 0);
                this.raster = Raster.createInterleavedRaster(0, n, n2, n * 4, 4, array6, null);
                break;
            }
            case 10: {
                this.colorModel = new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8 }, false, true, 1, 0);
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            case 11: {
                this.colorModel = new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 16 }, false, true, 1, 1);
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            case 12: {
                final byte[] array7 = { 0, -1 };
                this.colorModel = new IndexColorModel(1, 2, array7, array7, array7);
                this.raster = Raster.createPackedRaster(0, n, n2, 1, 1, null);
                break;
            }
            case 13: {
                final int[] array8 = new int[256];
                int i = 0;
                for (int j = 0; j < 256; j += 51) {
                    for (int k = 0; k < 256; k += 51) {
                        for (int l = 0; l < 256; l += 51) {
                            array8[i++] = (j << 16 | k << 8 | l);
                        }
                    }
                }
                final int n3 = 256 / (256 - i);
                int n4 = n3 * 3;
                while (i < 256) {
                    array8[i] = (n4 << 16 | n4 << 8 | n4);
                    n4 += n3;
                    ++i;
                }
                this.colorModel = new IndexColorModel(8, 256, array8, 0, false, -1, 0);
                this.raster = Raster.createInterleavedRaster(0, n, n2, 1, null);
                break;
            }
            case 8: {
                this.colorModel = new DirectColorModel(16, 63488, 2016, 31);
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            case 9: {
                this.colorModel = new DirectColorModel(15, 31744, 992, 31);
                this.raster = this.colorModel.createCompatibleWritableRaster(n, n2);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown image type " + imageType);
            }
        }
        this.imageType = imageType;
    }
    
    public BufferedImage(final int n, final int n2, final int imageType, final IndexColorModel colorModel) {
        this.imageType = 0;
        if (colorModel.hasAlpha() && colorModel.isAlphaPremultiplied()) {
            throw new IllegalArgumentException("This image types do not have premultiplied alpha.");
        }
        switch (imageType) {
            case 12: {
                final int mapSize = colorModel.getMapSize();
                int n3;
                if (mapSize <= 2) {
                    n3 = 1;
                }
                else if (mapSize <= 4) {
                    n3 = 2;
                }
                else {
                    if (mapSize > 16) {
                        throw new IllegalArgumentException("Color map for TYPE_BYTE_BINARY must have no more than 16 entries");
                    }
                    n3 = 4;
                }
                this.raster = Raster.createPackedRaster(0, n, n2, 1, n3, null);
                break;
            }
            case 13: {
                this.raster = Raster.createInterleavedRaster(0, n, n2, 1, null);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid image type (" + imageType + ").  Image type must be either TYPE_BYTE_BINARY or  TYPE_BYTE_INDEXED");
            }
        }
        if (!colorModel.isCompatibleRaster(this.raster)) {
            throw new IllegalArgumentException("Incompatible image type and IndexColorModel");
        }
        this.colorModel = colorModel;
        this.imageType = imageType;
    }
    
    public BufferedImage(final ColorModel colorModel, final WritableRaster raster, final boolean b, final Hashtable<?, ?> hashtable) {
        this.imageType = 0;
        if (!colorModel.isCompatibleRaster(raster)) {
            throw new IllegalArgumentException("Raster " + raster + " is incompatible with ColorModel " + colorModel);
        }
        if (raster.minX != 0 || raster.minY != 0) {
            throw new IllegalArgumentException("Raster " + raster + " has minX or minY not equal to zero: " + raster.minX + " " + raster.minY);
        }
        this.colorModel = colorModel;
        this.raster = raster;
        if (hashtable != null && !hashtable.isEmpty()) {
            this.properties = new Hashtable<String, Object>();
            for (final Object next : hashtable.keySet()) {
                if (next instanceof String) {
                    this.properties.put((String)next, hashtable.get(next));
                }
            }
        }
        final int numBands = raster.getNumBands();
        final boolean alphaPremultiplied = colorModel.isAlphaPremultiplied();
        final boolean standard = isStandard(colorModel, raster);
        this.coerceData(b);
        final SampleModel sampleModel = raster.getSampleModel();
        final int type = colorModel.getColorSpace().getType();
        if (type != 5) {
            if (type == 6 && standard && colorModel instanceof ComponentColorModel) {
                if (sampleModel instanceof ComponentSampleModel && ((ComponentSampleModel)sampleModel).getPixelStride() != numBands) {
                    this.imageType = 0;
                }
                else if (raster instanceof ByteComponentRaster && raster.getNumBands() == 1 && colorModel.getComponentSize(0) == 8 && ((ByteComponentRaster)raster).getPixelStride() == 1) {
                    this.imageType = 10;
                }
                else if (raster instanceof ShortComponentRaster && raster.getNumBands() == 1 && colorModel.getComponentSize(0) == 16 && ((ShortComponentRaster)raster).getPixelStride() == 1) {
                    this.imageType = 11;
                }
            }
            else {
                this.imageType = 0;
            }
            return;
        }
        if (raster instanceof IntegerComponentRaster && (numBands == 3 || numBands == 4)) {
            final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)raster;
            final int pixelSize = colorModel.getPixelSize();
            if (integerComponentRaster.getPixelStride() == 1 && standard && colorModel instanceof DirectColorModel && (pixelSize == 32 || pixelSize == 24)) {
                final DirectColorModel directColorModel = (DirectColorModel)colorModel;
                final int redMask = directColorModel.getRedMask();
                final int greenMask = directColorModel.getGreenMask();
                final int blueMask = directColorModel.getBlueMask();
                if (redMask == 16711680 && greenMask == 65280 && blueMask == 255) {
                    if (directColorModel.getAlphaMask() == -16777216) {
                        this.imageType = (alphaPremultiplied ? 3 : 2);
                    }
                    else if (!directColorModel.hasAlpha()) {
                        this.imageType = 1;
                    }
                }
                else if (redMask == 255 && greenMask == 65280 && blueMask == 16711680 && !directColorModel.hasAlpha()) {
                    this.imageType = 4;
                }
            }
        }
        else if (colorModel instanceof IndexColorModel && numBands == 1 && standard && (!colorModel.hasAlpha() || !alphaPremultiplied)) {
            final int pixelSize2 = colorModel.getPixelSize();
            if (raster instanceof BytePackedRaster) {
                this.imageType = 12;
            }
            else if (raster instanceof ByteComponentRaster && ((ByteComponentRaster)raster).getPixelStride() == 1 && pixelSize2 <= 8) {
                this.imageType = 13;
            }
        }
        else if (raster instanceof ShortComponentRaster && colorModel instanceof DirectColorModel && standard && numBands == 3 && !colorModel.hasAlpha()) {
            final DirectColorModel directColorModel2 = (DirectColorModel)colorModel;
            if (directColorModel2.getRedMask() == 63488) {
                if (directColorModel2.getGreenMask() == 2016 && directColorModel2.getBlueMask() == 31) {
                    this.imageType = 8;
                }
            }
            else if (directColorModel2.getRedMask() == 31744 && directColorModel2.getGreenMask() == 992 && directColorModel2.getBlueMask() == 31) {
                this.imageType = 9;
            }
        }
        else if (raster instanceof ByteComponentRaster && colorModel instanceof ComponentColorModel && standard && raster.getSampleModel() instanceof PixelInterleavedSampleModel && (numBands == 3 || numBands == 4)) {
            final ComponentColorModel componentColorModel = (ComponentColorModel)colorModel;
            final PixelInterleavedSampleModel pixelInterleavedSampleModel = (PixelInterleavedSampleModel)raster.getSampleModel();
            final ByteComponentRaster byteComponentRaster = (ByteComponentRaster)raster;
            final int[] bandOffsets = pixelInterleavedSampleModel.getBandOffsets();
            if (componentColorModel.getNumComponents() != numBands) {
                throw new RasterFormatException("Number of components in ColorModel (" + componentColorModel.getNumComponents() + ") does not match # in  Raster (" + numBands + ")");
            }
            final int[] componentSize = componentColorModel.getComponentSize();
            boolean b2 = true;
            for (int i = 0; i < numBands; ++i) {
                if (componentSize[i] != 8) {
                    b2 = false;
                    break;
                }
            }
            if (b2 && byteComponentRaster.getPixelStride() == numBands && bandOffsets[0] == numBands - 1 && bandOffsets[1] == numBands - 2 && bandOffsets[2] == numBands - 3) {
                if (numBands == 3 && !componentColorModel.hasAlpha()) {
                    this.imageType = 5;
                }
                else if (bandOffsets[3] == 0 && componentColorModel.hasAlpha()) {
                    this.imageType = (alphaPremultiplied ? 7 : 6);
                }
            }
        }
    }
    
    private static boolean isStandard(final ColorModel colorModel, final WritableRaster writableRaster) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            final /* synthetic */ Class val$cmClass = colorModel.getClass();
            final /* synthetic */ Class val$smClass = writableRaster.getSampleModel().getClass();
            final /* synthetic */ Class val$wrClass = writableRaster.getClass();
            
            @Override
            public Boolean run() {
                final ClassLoader classLoader = System.class.getClassLoader();
                return this.val$cmClass.getClassLoader() == classLoader && this.val$smClass.getClassLoader() == classLoader && this.val$wrClass.getClassLoader() == classLoader;
            }
        });
    }
    
    public int getType() {
        return this.imageType;
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.colorModel;
    }
    
    public WritableRaster getRaster() {
        return this.raster;
    }
    
    public WritableRaster getAlphaRaster() {
        return this.colorModel.getAlphaRaster(this.raster);
    }
    
    public int getRGB(final int n, final int n2) {
        return this.colorModel.getRGB(this.raster.getDataElements(n, n2, null));
    }
    
    public int[] getRGB(final int n, final int n2, final int n3, final int n4, int[] array, final int n5, final int n6) {
        int n7 = n5;
        final int numBands = this.raster.getNumBands();
        final int dataType = this.raster.getDataBuffer().getDataType();
        double[] array2 = null;
        switch (dataType) {
            case 0: {
                array2 = (double[])new byte[numBands];
                break;
            }
            case 1: {
                array2 = (double[])new short[numBands];
                break;
            }
            case 3: {
                array2 = (double[])new int[numBands];
                break;
            }
            case 4: {
                array2 = (double[])new float[numBands];
                break;
            }
            case 5: {
                array2 = new double[numBands];
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown data buffer type: " + dataType);
            }
        }
        if (array == null) {
            array = new int[n5 + n4 * n6];
        }
        for (int i = n2; i < n2 + n4; ++i, n7 += n6) {
            int n8 = n7;
            for (int j = n; j < n + n3; ++j) {
                array[n8++] = this.colorModel.getRGB(this.raster.getDataElements(j, i, array2));
            }
        }
        return array;
    }
    
    public synchronized void setRGB(final int n, final int n2, final int n3) {
        this.raster.setDataElements(n, n2, this.colorModel.getDataElements(n3, null));
    }
    
    public void setRGB(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5, final int n6) {
        int n7 = n5;
        Object dataElements = null;
        for (int i = n2; i < n2 + n4; ++i, n7 += n6) {
            int n8 = n7;
            for (int j = n; j < n + n3; ++j) {
                dataElements = this.colorModel.getDataElements(array[n8++], dataElements);
                this.raster.setDataElements(j, i, dataElements);
            }
        }
    }
    
    @Override
    public int getWidth() {
        return this.raster.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.raster.getHeight();
    }
    
    @Override
    public int getWidth(final ImageObserver imageObserver) {
        return this.raster.getWidth();
    }
    
    @Override
    public int getHeight(final ImageObserver imageObserver) {
        return this.raster.getHeight();
    }
    
    @Override
    public ImageProducer getSource() {
        if (this.osis == null) {
            if (this.properties == null) {
                this.properties = new Hashtable<String, Object>();
            }
            this.osis = new OffScreenImageSource(this, this.properties);
        }
        return this.osis;
    }
    
    @Override
    public Object getProperty(final String s, final ImageObserver imageObserver) {
        return this.getProperty(s);
    }
    
    @Override
    public Object getProperty(final String s) {
        if (s == null) {
            throw new NullPointerException("null property name is not allowed");
        }
        if (this.properties == null) {
            return Image.UndefinedProperty;
        }
        Object o = this.properties.get(s);
        if (o == null) {
            o = Image.UndefinedProperty;
        }
        return o;
    }
    
    @Override
    public Graphics getGraphics() {
        return this.createGraphics();
    }
    
    public Graphics2D createGraphics() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(this);
    }
    
    public BufferedImage getSubimage(final int n, final int n2, final int n3, final int n4) {
        return new BufferedImage(this.colorModel, this.raster.createWritableChild(n, n2, n3, n4, 0, 0, null), this.colorModel.isAlphaPremultiplied(), this.properties);
    }
    
    public boolean isAlphaPremultiplied() {
        return this.colorModel.isAlphaPremultiplied();
    }
    
    public void coerceData(final boolean b) {
        if (this.colorModel.hasAlpha() && this.colorModel.isAlphaPremultiplied() != b) {
            this.colorModel = this.colorModel.coerceData(this.raster, b);
        }
    }
    
    @Override
    public String toString() {
        return "BufferedImage@" + Integer.toHexString(this.hashCode()) + ": type = " + this.imageType + " " + this.colorModel + " " + this.raster;
    }
    
    @Override
    public Vector<RenderedImage> getSources() {
        return null;
    }
    
    @Override
    public String[] getPropertyNames() {
        if (this.properties == null || this.properties.isEmpty()) {
            return null;
        }
        final Set<String> keySet = this.properties.keySet();
        return keySet.toArray(new String[keySet.size()]);
    }
    
    @Override
    public int getMinX() {
        return this.raster.getMinX();
    }
    
    @Override
    public int getMinY() {
        return this.raster.getMinY();
    }
    
    @Override
    public SampleModel getSampleModel() {
        return this.raster.getSampleModel();
    }
    
    @Override
    public int getNumXTiles() {
        return 1;
    }
    
    @Override
    public int getNumYTiles() {
        return 1;
    }
    
    @Override
    public int getMinTileX() {
        return 0;
    }
    
    @Override
    public int getMinTileY() {
        return 0;
    }
    
    @Override
    public int getTileWidth() {
        return this.raster.getWidth();
    }
    
    @Override
    public int getTileHeight() {
        return this.raster.getHeight();
    }
    
    @Override
    public int getTileGridXOffset() {
        return this.raster.getSampleModelTranslateX();
    }
    
    @Override
    public int getTileGridYOffset() {
        return this.raster.getSampleModelTranslateY();
    }
    
    @Override
    public Raster getTile(final int n, final int n2) {
        if (n == 0 && n2 == 0) {
            return this.raster;
        }
        throw new ArrayIndexOutOfBoundsException("BufferedImages only have one tile with index 0,0");
    }
    
    @Override
    public Raster getData() {
        final int width = this.raster.getWidth();
        final int height = this.raster.getHeight();
        final int minX = this.raster.getMinX();
        final int minY = this.raster.getMinY();
        final WritableRaster writableRaster = Raster.createWritableRaster(this.raster.getSampleModel(), new Point(this.raster.getSampleModelTranslateX(), this.raster.getSampleModelTranslateY()));
        Object dataElements = null;
        for (int i = minY; i < minY + height; ++i) {
            dataElements = this.raster.getDataElements(minX, i, width, 1, dataElements);
            writableRaster.setDataElements(minX, i, width, 1, dataElements);
        }
        return writableRaster;
    }
    
    @Override
    public Raster getData(final Rectangle rectangle) {
        final WritableRaster writableRaster = Raster.createWritableRaster(this.raster.getSampleModel().createCompatibleSampleModel(rectangle.width, rectangle.height), rectangle.getLocation());
        final int width = rectangle.width;
        final int height = rectangle.height;
        final int x = rectangle.x;
        final int y = rectangle.y;
        Object dataElements = null;
        for (int i = y; i < y + height; ++i) {
            dataElements = this.raster.getDataElements(x, i, width, 1, dataElements);
            writableRaster.setDataElements(x, i, width, 1, dataElements);
        }
        return writableRaster;
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster writableRaster) {
        if (writableRaster == null) {
            return (WritableRaster)this.getData();
        }
        final int width = writableRaster.getWidth();
        final int height = writableRaster.getHeight();
        final int minX = writableRaster.getMinX();
        final int minY = writableRaster.getMinY();
        Object dataElements = null;
        for (int i = minY; i < minY + height; ++i) {
            dataElements = this.raster.getDataElements(minX, i, width, 1, dataElements);
            writableRaster.setDataElements(minX, i, width, 1, dataElements);
        }
        return writableRaster;
    }
    
    @Override
    public void setData(final Raster raster) {
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        int[] pixels = null;
        final Rectangle intersection = new Rectangle(minX, minY, width, height).intersection(new Rectangle(0, 0, this.raster.width, this.raster.height));
        if (intersection.isEmpty()) {
            return;
        }
        final int width2 = intersection.width;
        final int height2 = intersection.height;
        final int x = intersection.x;
        int i = 0;
        while (i < (i = intersection.y) + height2) {
            pixels = raster.getPixels(x, i, width2, 1, pixels);
            this.raster.setPixels(x, i, width2, 1, pixels);
            ++i;
        }
    }
    
    @Override
    public void addTileObserver(final TileObserver tileObserver) {
    }
    
    @Override
    public void removeTileObserver(final TileObserver tileObserver) {
    }
    
    @Override
    public boolean isTileWritable(final int n, final int n2) {
        if (n == 0 && n2 == 0) {
            return true;
        }
        throw new IllegalArgumentException("Only 1 tile in image");
    }
    
    @Override
    public Point[] getWritableTileIndices() {
        return new Point[] { new Point(0, 0) };
    }
    
    @Override
    public boolean hasTileWriters() {
        return true;
    }
    
    @Override
    public WritableRaster getWritableTile(final int n, final int n2) {
        return this.raster;
    }
    
    @Override
    public void releaseWritableTile(final int n, final int n2) {
    }
    
    @Override
    public int getTransparency() {
        return this.colorModel.getTransparency();
    }
    
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
}

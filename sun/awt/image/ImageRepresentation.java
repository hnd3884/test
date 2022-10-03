package sun.awt.image;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.util.Hashtable;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.image.ImageConsumer;

public class ImageRepresentation extends ImageWatched implements ImageConsumer
{
    InputStreamImageSource src;
    ToolkitImage image;
    int tag;
    long pData;
    int width;
    int height;
    int hints;
    int availinfo;
    Rectangle newbits;
    BufferedImage bimage;
    WritableRaster biRaster;
    protected ColorModel cmodel;
    ColorModel srcModel;
    int[] srcLUT;
    int srcLUTtransIndex;
    int numSrcLUT;
    boolean forceCMhint;
    int sstride;
    boolean isDefaultBI;
    boolean isSameCM;
    static boolean s_useNative;
    private boolean consuming;
    private int numWaiters;
    
    private static native void initIDs();
    
    public ImageRepresentation(final ToolkitImage image, final ColorModel colorModel, final boolean forceCMhint) {
        this.width = -1;
        this.height = -1;
        this.srcModel = null;
        this.srcLUT = null;
        this.srcLUTtransIndex = -1;
        this.numSrcLUT = 0;
        this.isDefaultBI = false;
        this.isSameCM = false;
        this.consuming = false;
        this.image = image;
        if (this.image.getSource() instanceof InputStreamImageSource) {
            this.src = (InputStreamImageSource)this.image.getSource();
        }
        this.setColorModel(colorModel);
        this.forceCMhint = forceCMhint;
    }
    
    public synchronized void reconstruct(final int n) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        final int n2 = n & ~this.availinfo;
        if ((this.availinfo & 0x40) == 0x0 && n2 != 0) {
            ++this.numWaiters;
            try {
                this.startProduction();
                for (int n3 = n & ~this.availinfo; (this.availinfo & 0x40) == 0x0 && n3 != 0; n3 = (n & ~this.availinfo)) {
                    try {
                        this.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            finally {
                this.decrementWaiters();
            }
        }
    }
    
    @Override
    public void setDimensions(final int width, final int height) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        this.image.setDimensions(width, height);
        this.newInfo(this.image, 3, 0, 0, width, height);
        if (width <= 0 || height <= 0) {
            this.imageComplete(1);
            return;
        }
        if (this.width != width || this.height != height) {
            this.bimage = null;
        }
        this.width = width;
        this.height = height;
        this.availinfo |= 0x3;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    ColorModel getColorModel() {
        return this.cmodel;
    }
    
    BufferedImage getBufferedImage() {
        return this.bimage;
    }
    
    protected BufferedImage createImage(final ColorModel colorModel, final WritableRaster writableRaster, final boolean b, final Hashtable hashtable) {
        final BufferedImage bufferedImage = new BufferedImage(colorModel, writableRaster, b, null);
        bufferedImage.setAccelerationPriority(this.image.getAccelerationPriority());
        return bufferedImage;
    }
    
    @Override
    public void setProperties(final Hashtable<?, ?> properties) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        this.image.setProperties(properties);
        this.newInfo(this.image, 4, 0, 0, 0, 0);
    }
    
    @Override
    public void setColorModel(final ColorModel colorModel) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        this.srcModel = colorModel;
        if (colorModel instanceof IndexColorModel) {
            if (colorModel.getTransparency() == 3) {
                this.cmodel = ColorModel.getRGBdefault();
                this.srcLUT = null;
            }
            else {
                final IndexColorModel indexColorModel = (IndexColorModel)colorModel;
                this.numSrcLUT = indexColorModel.getMapSize();
                indexColorModel.getRGBs(this.srcLUT = new int[Math.max(this.numSrcLUT, 256)]);
                this.srcLUTtransIndex = indexColorModel.getTransparentPixel();
                this.cmodel = colorModel;
            }
        }
        else if (this.cmodel == null) {
            this.cmodel = colorModel;
            this.srcLUT = null;
        }
        else if (colorModel instanceof DirectColorModel) {
            final DirectColorModel directColorModel = (DirectColorModel)colorModel;
            if (directColorModel.getRedMask() == 16711680 && directColorModel.getGreenMask() == 65280 && directColorModel.getBlueMask() == 255) {
                this.cmodel = colorModel;
                this.srcLUT = null;
            }
        }
        this.isSameCM = (this.cmodel == colorModel);
    }
    
    void createBufferedImage() {
        this.isDefaultBI = false;
        try {
            this.biRaster = this.cmodel.createCompatibleWritableRaster(this.width, this.height);
            this.bimage = this.createImage(this.cmodel, this.biRaster, this.cmodel.isAlphaPremultiplied(), null);
        }
        catch (final Exception ex) {
            this.cmodel = ColorModel.getRGBdefault();
            this.biRaster = this.cmodel.createCompatibleWritableRaster(this.width, this.height);
            this.bimage = this.createImage(this.cmodel, this.biRaster, false, null);
        }
        final int type = this.bimage.getType();
        if (this.cmodel == ColorModel.getRGBdefault() || type == 1 || type == 3) {
            this.isDefaultBI = true;
        }
        else if (this.cmodel instanceof DirectColorModel) {
            final DirectColorModel directColorModel = (DirectColorModel)this.cmodel;
            if (directColorModel.getRedMask() == 16711680 && directColorModel.getGreenMask() == 65280 && directColorModel.getBlueMask() == 255) {
                this.isDefaultBI = true;
            }
        }
    }
    
    private void convertToRGB() {
        final int width = this.bimage.getWidth();
        final int height = this.bimage.getHeight();
        final int n = width * height;
        final DataBufferInt dataBufferInt = new DataBufferInt(n);
        final int[] stealData = SunWritableRaster.stealData(dataBufferInt, 0);
        if (this.cmodel instanceof IndexColorModel && this.biRaster instanceof ByteComponentRaster && this.biRaster.getNumDataElements() == 1) {
            final ByteComponentRaster byteComponentRaster = (ByteComponentRaster)this.biRaster;
            final byte[] dataStorage = byteComponentRaster.getDataStorage();
            final int dataOffset = byteComponentRaster.getDataOffset(0);
            for (int i = 0; i < n; ++i) {
                stealData[i] = this.srcLUT[dataStorage[dataOffset + i] & 0xFF];
            }
        }
        else {
            Object dataElements = null;
            int n2 = 0;
            for (int j = 0; j < height; ++j) {
                for (int k = 0; k < width; ++k) {
                    dataElements = this.biRaster.getDataElements(k, j, dataElements);
                    stealData[n2++] = this.cmodel.getRGB(dataElements);
                }
            }
        }
        SunWritableRaster.markDirty(dataBufferInt);
        this.isSameCM = false;
        this.cmodel = ColorModel.getRGBdefault();
        this.biRaster = Raster.createPackedRaster(dataBufferInt, width, height, width, new int[] { 16711680, 65280, 255, -16777216 }, null);
        this.bimage = this.createImage(this.cmodel, this.biRaster, this.cmodel.isAlphaPremultiplied(), null);
        this.srcLUT = null;
        this.isDefaultBI = true;
    }
    
    @Override
    public void setHints(final int hints) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        this.hints = hints;
    }
    
    private native boolean setICMpixels(final int p0, final int p1, final int p2, final int p3, final int[] p4, final byte[] p5, final int p6, final int p7, final IntegerComponentRaster p8);
    
    private native boolean setDiffICM(final int p0, final int p1, final int p2, final int p3, final int[] p4, final int p5, final int p6, final IndexColorModel p7, final byte[] p8, final int p9, final int p10, final ByteComponentRaster p11, final int p12);
    
    @Override
    public void setPixels(int n, int n2, int n3, int n4, final ColorModel colorModel, final byte[] array, int n5, final int n6) {
        int n7 = n5;
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        synchronized (this) {
            if (this.bimage == null) {
                if (this.cmodel == null) {
                    this.cmodel = colorModel;
                }
                this.createBufferedImage();
            }
            if (n3 <= 0 || n4 <= 0) {
                return;
            }
            final int width = this.biRaster.getWidth();
            final int height = this.biRaster.getHeight();
            int n8 = n + n3;
            int n9 = n2 + n4;
            if (n < 0) {
                n5 -= n;
                n = 0;
            }
            else if (n8 < 0) {
                n8 = width;
            }
            if (n2 < 0) {
                n5 -= n2 * n6;
                n2 = 0;
            }
            else if (n9 < 0) {
                n9 = height;
            }
            if (n8 > width) {
                n8 = width;
            }
            if (n9 > height) {
                n9 = height;
            }
            if (n >= n8 || n2 >= n9) {
                return;
            }
            n3 = n8 - n;
            n4 = n9 - n2;
            if (n5 < 0 || n5 >= array.length) {
                throw new ArrayIndexOutOfBoundsException("Data offset out of bounds.");
            }
            final int n10 = array.length - n5;
            if (n10 < n3) {
                throw new ArrayIndexOutOfBoundsException("Data array is too short.");
            }
            int n11;
            if (n6 < 0) {
                n11 = n5 / -n6 + 1;
            }
            else if (n6 > 0) {
                n11 = (n10 - n3) / n6 + 1;
            }
            else {
                n11 = n4;
            }
            if (n4 > n11) {
                throw new ArrayIndexOutOfBoundsException("Data array is too short.");
            }
            if (this.isSameCM && this.cmodel != colorModel && this.srcLUT != null && colorModel instanceof IndexColorModel && this.biRaster instanceof ByteComponentRaster) {
                final IndexColorModel indexColorModel = (IndexColorModel)colorModel;
                final ByteComponentRaster byteComponentRaster = (ByteComponentRaster)this.biRaster;
                final int numSrcLUT = this.numSrcLUT;
                if (this.setDiffICM(n, n2, n3, n4, this.srcLUT, this.srcLUTtransIndex, this.numSrcLUT, indexColorModel, array, n5, n6, byteComponentRaster, byteComponentRaster.getDataOffset(0))) {
                    byteComponentRaster.markDirty();
                    if (numSrcLUT != this.numSrcLUT) {
                        boolean hasAlpha = indexColorModel.hasAlpha();
                        if (this.srcLUTtransIndex != -1) {
                            hasAlpha = true;
                        }
                        final int pixelSize = indexColorModel.getPixelSize();
                        final IndexColorModel cmodel = new IndexColorModel(pixelSize, this.numSrcLUT, this.srcLUT, 0, hasAlpha, this.srcLUTtransIndex, (pixelSize > 8) ? 1 : 0);
                        this.cmodel = cmodel;
                        this.bimage = this.createImage(cmodel, byteComponentRaster, false, null);
                    }
                    return;
                }
                this.convertToRGB();
            }
            if (this.isDefaultBI) {
                final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)this.biRaster;
                if (this.srcLUT != null && colorModel instanceof IndexColorModel) {
                    if (colorModel != this.srcModel) {
                        ((IndexColorModel)colorModel).getRGBs(this.srcLUT);
                        this.srcModel = colorModel;
                    }
                    if (ImageRepresentation.s_useNative) {
                        if (!this.setICMpixels(n, n2, n3, n4, this.srcLUT, array, n5, n6, integerComponentRaster)) {
                            this.abort();
                            return;
                        }
                        integerComponentRaster.markDirty();
                    }
                    else {
                        final int[] array2 = new int[n3 * n4];
                        int n12 = 0;
                        for (int i = 0; i < n4; ++i, n7 += n6) {
                            int n13 = n7;
                            for (int j = 0; j < n3; ++j) {
                                array2[n12++] = this.srcLUT[array[n13++] & 0xFF];
                            }
                        }
                        integerComponentRaster.setDataElements(n, n2, n3, n4, array2);
                    }
                }
                else {
                    final int[] array3 = new int[n3];
                    for (int k = n2; k < n2 + n4; ++k, n7 += n6) {
                        int n14 = n7;
                        for (int l = 0; l < n3; ++l) {
                            array3[l] = colorModel.getRGB(array[n14++] & 0xFF);
                        }
                        integerComponentRaster.setDataElements(n, k, n3, 1, array3);
                    }
                    this.availinfo |= 0x8;
                }
            }
            else if (this.cmodel == colorModel && this.biRaster instanceof ByteComponentRaster && this.biRaster.getNumDataElements() == 1) {
                final ByteComponentRaster byteComponentRaster2 = (ByteComponentRaster)this.biRaster;
                if (n5 == 0 && n6 == n3) {
                    byteComponentRaster2.putByteData(n, n2, n3, n4, array);
                }
                else {
                    final byte[] array4 = new byte[n3];
                    int n15 = n5;
                    for (int n16 = n2; n16 < n2 + n4; ++n16) {
                        System.arraycopy(array, n15, array4, 0, n3);
                        byteComponentRaster2.putByteData(n, n16, n3, 1, array4);
                        n15 += n6;
                    }
                }
            }
            else {
                for (int n17 = n2; n17 < n2 + n4; ++n17, n7 += n6) {
                    int n18 = n7;
                    for (int n19 = n; n19 < n + n3; ++n19) {
                        this.bimage.setRGB(n19, n17, colorModel.getRGB(array[n18++] & 0xFF));
                    }
                }
                this.availinfo |= 0x8;
            }
        }
        if ((this.availinfo & 0x10) == 0x0) {
            this.newInfo(this.image, 8, n, n2, n3, n4);
        }
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel cmodel, final int[] array, final int n5, final int n6) {
        int n7 = n5;
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        synchronized (this) {
            if (this.bimage == null) {
                if (this.cmodel == null) {
                    this.cmodel = cmodel;
                }
                this.createBufferedImage();
            }
            final int[] array2 = new int[n3];
            if (this.cmodel instanceof IndexColorModel) {
                this.convertToRGB();
            }
            if (cmodel == this.cmodel && this.biRaster instanceof IntegerComponentRaster) {
                final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)this.biRaster;
                if (n5 == 0 && n6 == n3) {
                    integerComponentRaster.setDataElements(n, n2, n3, n4, array);
                }
                else {
                    for (int i = n2; i < n2 + n4; ++i, n7 += n6) {
                        System.arraycopy(array, n7, array2, 0, n3);
                        integerComponentRaster.setDataElements(n, i, n3, 1, array2);
                    }
                }
            }
            else {
                if (cmodel.getTransparency() != 1) {
                    final int transparency = this.cmodel.getTransparency();
                    final ColorModel cmodel2 = this.cmodel;
                    if (transparency == 1) {
                        this.convertToRGB();
                    }
                }
                if (this.isDefaultBI) {
                    final IntegerComponentRaster integerComponentRaster2 = (IntegerComponentRaster)this.biRaster;
                    final int[] dataStorage = integerComponentRaster2.getDataStorage();
                    if (this.cmodel.equals(cmodel)) {
                        final int scanlineStride = integerComponentRaster2.getScanlineStride();
                        int n8 = n2 * scanlineStride + n;
                        for (int j = 0; j < n4; ++j, n7 += n6) {
                            System.arraycopy(array, n7, dataStorage, n8, n3);
                            n8 += scanlineStride;
                        }
                        integerComponentRaster2.markDirty();
                    }
                    else {
                        for (int k = n2; k < n2 + n4; ++k, n7 += n6) {
                            int n9 = n7;
                            for (int l = 0; l < n3; ++l) {
                                array2[l] = cmodel.getRGB(array[n9++]);
                            }
                            integerComponentRaster2.setDataElements(n, k, n3, 1, array2);
                        }
                    }
                    this.availinfo |= 0x8;
                }
                else {
                    Object dataElements = null;
                    for (int n10 = n2; n10 < n2 + n4; ++n10, n7 += n6) {
                        int n11 = n7;
                        for (int n12 = n; n12 < n + n3; ++n12) {
                            dataElements = this.cmodel.getDataElements(cmodel.getRGB(array[n11++]), dataElements);
                            this.biRaster.setDataElements(n12, n10, dataElements);
                        }
                    }
                    this.availinfo |= 0x8;
                }
            }
        }
        if ((this.availinfo & 0x10) == 0x0) {
            this.newInfo(this.image, 8, n, n2, n3, n4);
        }
    }
    
    public BufferedImage getOpaqueRGBImage() {
        if (this.bimage.getType() == 2) {
            final int width = this.bimage.getWidth();
            final int height = this.bimage.getHeight();
            final int n = width * height;
            final DataBufferInt dataBufferInt = (DataBufferInt)this.biRaster.getDataBuffer();
            final int[] stealData = SunWritableRaster.stealData(dataBufferInt, 0);
            for (int i = 0; i < n; ++i) {
                if (stealData[i] >>> 24 != 255) {
                    return this.bimage;
                }
            }
            final DirectColorModel directColorModel = new DirectColorModel(24, 16711680, 65280, 255);
            final WritableRaster packedRaster = Raster.createPackedRaster(dataBufferInt, width, height, width, new int[] { 16711680, 65280, 255 }, null);
            try {
                return this.createImage(directColorModel, packedRaster, false, null);
            }
            catch (final Exception ex) {
                return this.bimage;
            }
        }
        return this.bimage;
    }
    
    @Override
    public void imageComplete(final int n) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        boolean b = false;
        int n2 = 0;
        switch (n) {
            default: {
                b = true;
                n2 = 128;
                break;
            }
            case 1: {
                this.image.addInfo(64);
                b = true;
                n2 = 64;
                this.dispose();
                break;
            }
            case 3: {
                b = true;
                n2 = 32;
                break;
            }
            case 2: {
                b = false;
                n2 = 16;
                break;
            }
        }
        synchronized (this) {
            if (b) {
                this.image.getSource().removeConsumer(this);
                this.consuming = false;
                this.newbits = null;
                if (this.bimage != null) {
                    this.bimage = this.getOpaqueRGBImage();
                }
            }
            this.availinfo |= n2;
            this.notifyAll();
        }
        this.newInfo(this.image, n2, 0, 0, this.width, this.height);
        this.image.infoDone(n);
    }
    
    void startProduction() {
        if (!this.consuming) {
            this.consuming = true;
            this.image.getSource().startProduction(this);
        }
    }
    
    private synchronized void checkConsumption() {
        if (this.isWatcherListEmpty() && this.numWaiters == 0 && (this.availinfo & 0x20) == 0x0) {
            this.dispose();
        }
    }
    
    public synchronized void notifyWatcherListEmpty() {
        this.checkConsumption();
    }
    
    private synchronized void decrementWaiters() {
        --this.numWaiters;
        this.checkConsumption();
    }
    
    public boolean prepare(final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x40) != 0x0) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1);
            }
            return false;
        }
        boolean b = (this.availinfo & 0x20) != 0x0;
        if (!b) {
            this.addWatcher(imageObserver);
            this.startProduction();
            b = ((this.availinfo & 0x20) != 0x0);
        }
        return b;
    }
    
    public int check(final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x60) == 0x0) {
            this.addWatcher(imageObserver);
        }
        return this.availinfo;
    }
    
    public boolean drawToBufImage(final Graphics graphics, final ToolkitImage toolkitImage, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x40) != 0x0) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1);
            }
            return false;
        }
        boolean b = (this.availinfo & 0x20) != 0x0;
        final boolean b2 = (this.availinfo & 0x80) != 0x0;
        if (!b && !b2) {
            this.addWatcher(imageObserver);
            this.startProduction();
            b = ((this.availinfo & 0x20) != 0x0);
        }
        if (b || 0x0 != (this.availinfo & 0x10)) {
            graphics.drawImage(this.bimage, n, n2, color, null);
        }
        return b;
    }
    
    public boolean drawToBufImage(final Graphics graphics, final ToolkitImage toolkitImage, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x40) != 0x0) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1);
            }
            return false;
        }
        boolean b = (this.availinfo & 0x20) != 0x0;
        final boolean b2 = (this.availinfo & 0x80) != 0x0;
        if (!b && !b2) {
            this.addWatcher(imageObserver);
            this.startProduction();
            b = ((this.availinfo & 0x20) != 0x0);
        }
        if (b || 0x0 != (this.availinfo & 0x10)) {
            graphics.drawImage(this.bimage, n, n2, n3, n4, color, null);
        }
        return b;
    }
    
    public boolean drawToBufImage(final Graphics graphics, final ToolkitImage toolkitImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x40) != 0x0) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1);
            }
            return false;
        }
        boolean b = (this.availinfo & 0x20) != 0x0;
        final boolean b2 = (this.availinfo & 0x80) != 0x0;
        if (!b && !b2) {
            this.addWatcher(imageObserver);
            this.startProduction();
            b = ((this.availinfo & 0x20) != 0x0);
        }
        if (b || 0x0 != (this.availinfo & 0x10)) {
            graphics.drawImage(this.bimage, n, n2, n3, n4, n5, n6, n7, n8, color, null);
        }
        return b;
    }
    
    public boolean drawToBufImage(final Graphics graphics, final ToolkitImage toolkitImage, final AffineTransform affineTransform, final ImageObserver imageObserver) {
        final Graphics2D graphics2D = (Graphics2D)graphics;
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x40) != 0x0) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1);
            }
            return false;
        }
        boolean b = (this.availinfo & 0x20) != 0x0;
        final boolean b2 = (this.availinfo & 0x80) != 0x0;
        if (!b && !b2) {
            this.addWatcher(imageObserver);
            this.startProduction();
            b = ((this.availinfo & 0x20) != 0x0);
        }
        if (b || 0x0 != (this.availinfo & 0x10)) {
            graphics2D.drawImage(this.bimage, affineTransform, null);
        }
        return b;
    }
    
    synchronized void abort() {
        this.image.getSource().removeConsumer(this);
        this.consuming = false;
        this.newbits = null;
        this.bimage = null;
        this.biRaster = null;
        this.cmodel = null;
        this.srcLUT = null;
        this.isDefaultBI = false;
        this.isSameCM = false;
        this.newInfo(this.image, 128, -1, -1, -1, -1);
        this.availinfo &= 0xFFFFFF87;
    }
    
    synchronized void dispose() {
        this.image.getSource().removeConsumer(this);
        this.consuming = false;
        this.newbits = null;
        this.availinfo &= 0xFFFFFFC7;
    }
    
    public void setAccelerationPriority(final float accelerationPriority) {
        if (this.bimage != null) {
            this.bimage.setAccelerationPriority(accelerationPriority);
        }
    }
    
    static {
        NativeLibLoader.loadLibraries();
        initIDs();
        ImageRepresentation.s_useNative = true;
    }
}

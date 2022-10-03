package sun.awt.image;

import java.awt.Rectangle;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import sun.java2d.loops.CompositeType;
import java.awt.image.DataBuffer;
import sun.java2d.SunGraphics2D;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.RenderLoops;
import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;

public class BufImgSurfaceData extends SurfaceData
{
    BufferedImage bufImg;
    private BufferedImageGraphicsConfig graphicsConfig;
    RenderLoops solidloops;
    private static final int DCM_RGBX_RED_MASK = -16777216;
    private static final int DCM_RGBX_GREEN_MASK = 16711680;
    private static final int DCM_RGBX_BLUE_MASK = 65280;
    private static final int DCM_555X_RED_MASK = 63488;
    private static final int DCM_555X_GREEN_MASK = 1984;
    private static final int DCM_555X_BLUE_MASK = 62;
    private static final int DCM_4444_RED_MASK = 3840;
    private static final int DCM_4444_GREEN_MASK = 240;
    private static final int DCM_4444_BLUE_MASK = 15;
    private static final int DCM_4444_ALPHA_MASK = 61440;
    private static final int DCM_ARGBBM_ALPHA_MASK = 16777216;
    private static final int DCM_ARGBBM_RED_MASK = 16711680;
    private static final int DCM_ARGBBM_GREEN_MASK = 65280;
    private static final int DCM_ARGBBM_BLUE_MASK = 255;
    private static final int CACHE_SIZE = 5;
    private static RenderLoops[] loopcache;
    private static SurfaceType[] typecache;
    
    private static native void initIDs(final Class p0, final Class p1);
    
    public static SurfaceData createData(final BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            throw new NullPointerException("BufferedImage cannot be null");
        }
        final ColorModel colorModel = bufferedImage.getColorModel();
        SurfaceData surfaceData = null;
        switch (bufferedImage.getType()) {
            case 4: {
                surfaceData = createDataIC(bufferedImage, SurfaceType.IntBgr);
                break;
            }
            case 1: {
                surfaceData = createDataIC(bufferedImage, SurfaceType.IntRgb);
                break;
            }
            case 2: {
                surfaceData = createDataIC(bufferedImage, SurfaceType.IntArgb);
                break;
            }
            case 3: {
                surfaceData = createDataIC(bufferedImage, SurfaceType.IntArgbPre);
                break;
            }
            case 5: {
                surfaceData = createDataBC(bufferedImage, SurfaceType.ThreeByteBgr, 2);
                break;
            }
            case 6: {
                surfaceData = createDataBC(bufferedImage, SurfaceType.FourByteAbgr, 3);
                break;
            }
            case 7: {
                surfaceData = createDataBC(bufferedImage, SurfaceType.FourByteAbgrPre, 3);
                break;
            }
            case 8: {
                surfaceData = createDataSC(bufferedImage, SurfaceType.Ushort565Rgb, null);
                break;
            }
            case 9: {
                surfaceData = createDataSC(bufferedImage, SurfaceType.Ushort555Rgb, null);
                break;
            }
            case 13: {
                SurfaceType surfaceType = null;
                switch (colorModel.getTransparency()) {
                    case 1: {
                        if (SurfaceData.isOpaqueGray((IndexColorModel)colorModel)) {
                            surfaceType = SurfaceType.Index8Gray;
                            break;
                        }
                        surfaceType = SurfaceType.ByteIndexedOpaque;
                        break;
                    }
                    case 2: {
                        surfaceType = SurfaceType.ByteIndexedBm;
                        break;
                    }
                    case 3: {
                        surfaceType = SurfaceType.ByteIndexed;
                        break;
                    }
                    default: {
                        throw new InternalError("Unrecognized transparency");
                    }
                }
                surfaceData = createDataBC(bufferedImage, surfaceType, 0);
                break;
            }
            case 10: {
                surfaceData = createDataBC(bufferedImage, SurfaceType.ByteGray, 0);
                break;
            }
            case 11: {
                surfaceData = createDataSC(bufferedImage, SurfaceType.UshortGray, null);
                break;
            }
            case 12: {
                SurfaceType surfaceType2 = null;
                switch (bufferedImage.getRaster().getSampleModel().getSampleSize(0)) {
                    case 1: {
                        surfaceType2 = SurfaceType.ByteBinary1Bit;
                        break;
                    }
                    case 2: {
                        surfaceType2 = SurfaceType.ByteBinary2Bit;
                        break;
                    }
                    case 4: {
                        surfaceType2 = SurfaceType.ByteBinary4Bit;
                        break;
                    }
                    default: {
                        throw new InternalError("Unrecognized pixel size");
                    }
                }
                surfaceData = createDataBP(bufferedImage, surfaceType2);
                break;
            }
            default: {
                final WritableRaster raster = bufferedImage.getRaster();
                final int numBands = raster.getNumBands();
                if (raster instanceof IntegerComponentRaster && raster.getNumDataElements() == 1 && ((IntegerComponentRaster)raster).getPixelStride() == 1) {
                    SurfaceType surfaceType3 = SurfaceType.AnyInt;
                    if (colorModel instanceof DirectColorModel) {
                        final DirectColorModel directColorModel = (DirectColorModel)colorModel;
                        final int alphaMask = directColorModel.getAlphaMask();
                        final int redMask = directColorModel.getRedMask();
                        final int greenMask = directColorModel.getGreenMask();
                        final int blueMask = directColorModel.getBlueMask();
                        if (numBands == 3 && alphaMask == 0 && redMask == -16777216 && greenMask == 16711680 && blueMask == 65280) {
                            surfaceType3 = SurfaceType.IntRgbx;
                        }
                        else if (numBands == 4 && alphaMask == 16777216 && redMask == 16711680 && greenMask == 65280 && blueMask == 255) {
                            surfaceType3 = SurfaceType.IntArgbBm;
                        }
                        else {
                            surfaceType3 = SurfaceType.AnyDcm;
                        }
                    }
                    surfaceData = createDataIC(bufferedImage, surfaceType3);
                    break;
                }
                if (raster instanceof ShortComponentRaster && raster.getNumDataElements() == 1 && ((ShortComponentRaster)raster).getPixelStride() == 1) {
                    SurfaceType surfaceType4 = SurfaceType.AnyShort;
                    IndexColorModel indexColorModel = null;
                    if (colorModel instanceof DirectColorModel) {
                        final DirectColorModel directColorModel2 = (DirectColorModel)colorModel;
                        final int alphaMask2 = directColorModel2.getAlphaMask();
                        final int redMask2 = directColorModel2.getRedMask();
                        final int greenMask2 = directColorModel2.getGreenMask();
                        final int blueMask2 = directColorModel2.getBlueMask();
                        if (numBands == 3 && alphaMask2 == 0 && redMask2 == 63488 && greenMask2 == 1984 && blueMask2 == 62) {
                            surfaceType4 = SurfaceType.Ushort555Rgbx;
                        }
                        else if (numBands == 4 && alphaMask2 == 61440 && redMask2 == 3840 && greenMask2 == 240 && blueMask2 == 15) {
                            surfaceType4 = SurfaceType.Ushort4444Argb;
                        }
                    }
                    else if (colorModel instanceof IndexColorModel) {
                        indexColorModel = (IndexColorModel)colorModel;
                        if (indexColorModel.getPixelSize() == 12) {
                            if (SurfaceData.isOpaqueGray(indexColorModel)) {
                                surfaceType4 = SurfaceType.Index12Gray;
                            }
                            else {
                                surfaceType4 = SurfaceType.UshortIndexed;
                            }
                        }
                        else {
                            indexColorModel = null;
                        }
                    }
                    surfaceData = createDataSC(bufferedImage, surfaceType4, indexColorModel);
                    break;
                }
                surfaceData = new BufImgSurfaceData(raster.getDataBuffer(), bufferedImage, SurfaceType.Custom);
                break;
            }
        }
        ((BufImgSurfaceData)surfaceData).initSolidLoops();
        return surfaceData;
    }
    
    public static SurfaceData createData(final Raster raster, final ColorModel colorModel) {
        throw new InternalError("SurfaceData not implemented for Raster/CM");
    }
    
    public static SurfaceData createDataIC(final BufferedImage bufferedImage, final SurfaceType surfaceType) {
        final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)bufferedImage.getRaster();
        final BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(integerComponentRaster.getDataBuffer(), bufferedImage, surfaceType);
        bufImgSurfaceData.initRaster(integerComponentRaster.getDataStorage(), integerComponentRaster.getDataOffset(0) * 4, 0, integerComponentRaster.getWidth(), integerComponentRaster.getHeight(), integerComponentRaster.getPixelStride() * 4, integerComponentRaster.getScanlineStride() * 4, null);
        return bufImgSurfaceData;
    }
    
    public static SurfaceData createDataSC(final BufferedImage bufferedImage, final SurfaceType surfaceType, final IndexColorModel indexColorModel) {
        final ShortComponentRaster shortComponentRaster = (ShortComponentRaster)bufferedImage.getRaster();
        final BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(shortComponentRaster.getDataBuffer(), bufferedImage, surfaceType);
        bufImgSurfaceData.initRaster(shortComponentRaster.getDataStorage(), shortComponentRaster.getDataOffset(0) * 2, 0, shortComponentRaster.getWidth(), shortComponentRaster.getHeight(), shortComponentRaster.getPixelStride() * 2, shortComponentRaster.getScanlineStride() * 2, indexColorModel);
        return bufImgSurfaceData;
    }
    
    public static SurfaceData createDataBC(final BufferedImage bufferedImage, final SurfaceType surfaceType, final int n) {
        final ByteComponentRaster byteComponentRaster = (ByteComponentRaster)bufferedImage.getRaster();
        final BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(byteComponentRaster.getDataBuffer(), bufferedImage, surfaceType);
        final ColorModel colorModel = bufferedImage.getColorModel();
        bufImgSurfaceData.initRaster(byteComponentRaster.getDataStorage(), byteComponentRaster.getDataOffset(n), 0, byteComponentRaster.getWidth(), byteComponentRaster.getHeight(), byteComponentRaster.getPixelStride(), byteComponentRaster.getScanlineStride(), (colorModel instanceof IndexColorModel) ? ((IndexColorModel)colorModel) : null);
        return bufImgSurfaceData;
    }
    
    public static SurfaceData createDataBP(final BufferedImage bufferedImage, final SurfaceType surfaceType) {
        final BytePackedRaster bytePackedRaster = (BytePackedRaster)bufferedImage.getRaster();
        final BufImgSurfaceData bufImgSurfaceData = new BufImgSurfaceData(bytePackedRaster.getDataBuffer(), bufferedImage, surfaceType);
        final ColorModel colorModel = bufferedImage.getColorModel();
        bufImgSurfaceData.initRaster(bytePackedRaster.getDataStorage(), bytePackedRaster.getDataBitOffset() / 8, bytePackedRaster.getDataBitOffset() & 0x7, bytePackedRaster.getWidth(), bytePackedRaster.getHeight(), 0, bytePackedRaster.getScanlineStride(), (colorModel instanceof IndexColorModel) ? ((IndexColorModel)colorModel) : null);
        return bufImgSurfaceData;
    }
    
    @Override
    public RenderLoops getRenderLoops(final SunGraphics2D sunGraphics2D) {
        if (sunGraphics2D.paintState <= 1 && sunGraphics2D.compositeState <= 0) {
            return this.solidloops;
        }
        return super.getRenderLoops(sunGraphics2D);
    }
    
    @Override
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        return this.bufImg.getRaster();
    }
    
    protected native void initRaster(final Object p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final IndexColorModel p7);
    
    public BufImgSurfaceData(final DataBuffer dataBuffer, final BufferedImage bufImg, final SurfaceType surfaceType) {
        super(SunWritableRaster.stealTrackable(dataBuffer), surfaceType, bufImg.getColorModel());
        this.bufImg = bufImg;
    }
    
    protected BufImgSurfaceData(final SurfaceType surfaceType, final ColorModel colorModel) {
        super(surfaceType, colorModel);
    }
    
    public void initSolidLoops() {
        this.solidloops = getSolidLoops(this.getSurfaceType());
    }
    
    public static synchronized RenderLoops getSolidLoops(final SurfaceType surfaceType) {
        for (int i = 4; i >= 0; --i) {
            final SurfaceType surfaceType2 = BufImgSurfaceData.typecache[i];
            if (surfaceType2 == surfaceType) {
                return BufImgSurfaceData.loopcache[i];
            }
            if (surfaceType2 == null) {
                break;
            }
        }
        final RenderLoops renderLoops = SurfaceData.makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, surfaceType);
        System.arraycopy(BufImgSurfaceData.loopcache, 1, BufImgSurfaceData.loopcache, 0, 4);
        System.arraycopy(BufImgSurfaceData.typecache, 1, BufImgSurfaceData.typecache, 0, 4);
        BufImgSurfaceData.loopcache[4] = renderLoops;
        BufImgSurfaceData.typecache[4] = surfaceType;
        return renderLoops;
    }
    
    @Override
    public SurfaceData getReplacement() {
        return SurfaceData.restoreContents(this.bufImg);
    }
    
    @Override
    public synchronized GraphicsConfiguration getDeviceConfiguration() {
        if (this.graphicsConfig == null) {
            this.graphicsConfig = BufferedImageGraphicsConfig.getConfig(this.bufImg);
        }
        return this.graphicsConfig;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.bufImg.getWidth(), this.bufImg.getHeight());
    }
    
    @Override
    protected void checkCustomComposite() {
    }
    
    @Override
    public Object getDestination() {
        return this.bufImg;
    }
    
    static {
        initIDs(IndexColorModel.class, ICMColorData.class);
        BufImgSurfaceData.loopcache = new RenderLoops[5];
        BufImgSurfaceData.typecache = new SurfaceType[5];
    }
    
    public static final class ICMColorData
    {
        private long pData;
        
        private ICMColorData(final long pData) {
            this.pData = 0L;
            this.pData = pData;
        }
    }
}

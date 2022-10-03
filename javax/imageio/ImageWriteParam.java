package javax.imageio;

import java.util.Locale;
import java.awt.Dimension;

public class ImageWriteParam extends IIOParam
{
    public static final int MODE_DISABLED = 0;
    public static final int MODE_DEFAULT = 1;
    public static final int MODE_EXPLICIT = 2;
    public static final int MODE_COPY_FROM_METADATA = 3;
    private static final int MAX_MODE = 3;
    protected boolean canWriteTiles;
    protected int tilingMode;
    protected Dimension[] preferredTileSizes;
    protected boolean tilingSet;
    protected int tileWidth;
    protected int tileHeight;
    protected boolean canOffsetTiles;
    protected int tileGridXOffset;
    protected int tileGridYOffset;
    protected boolean canWriteProgressive;
    protected int progressiveMode;
    protected boolean canWriteCompressed;
    protected int compressionMode;
    protected String[] compressionTypes;
    protected String compressionType;
    protected float compressionQuality;
    protected Locale locale;
    
    protected ImageWriteParam() {
        this.canWriteTiles = false;
        this.tilingMode = 3;
        this.preferredTileSizes = null;
        this.tilingSet = false;
        this.tileWidth = 0;
        this.tileHeight = 0;
        this.canOffsetTiles = false;
        this.tileGridXOffset = 0;
        this.tileGridYOffset = 0;
        this.canWriteProgressive = false;
        this.progressiveMode = 3;
        this.canWriteCompressed = false;
        this.compressionMode = 3;
        this.compressionTypes = null;
        this.compressionType = null;
        this.compressionQuality = 1.0f;
        this.locale = null;
    }
    
    public ImageWriteParam(final Locale locale) {
        this.canWriteTiles = false;
        this.tilingMode = 3;
        this.preferredTileSizes = null;
        this.tilingSet = false;
        this.tileWidth = 0;
        this.tileHeight = 0;
        this.canOffsetTiles = false;
        this.tileGridXOffset = 0;
        this.tileGridYOffset = 0;
        this.canWriteProgressive = false;
        this.progressiveMode = 3;
        this.canWriteCompressed = false;
        this.compressionMode = 3;
        this.compressionTypes = null;
        this.compressionType = null;
        this.compressionQuality = 1.0f;
        this.locale = null;
        this.locale = locale;
    }
    
    private static Dimension[] clonePreferredTileSizes(final Dimension[] array) {
        if (array == null) {
            return null;
        }
        final Dimension[] array2 = new Dimension[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new Dimension(array[i]);
        }
        return array2;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public boolean canWriteTiles() {
        return this.canWriteTiles;
    }
    
    public boolean canOffsetTiles() {
        return this.canOffsetTiles;
    }
    
    public void setTilingMode(final int tilingMode) {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported!");
        }
        if (tilingMode < 0 || tilingMode > 3) {
            throw new IllegalArgumentException("Illegal value for mode!");
        }
        if ((this.tilingMode = tilingMode) == 2) {
            this.unsetTiling();
        }
    }
    
    public int getTilingMode() {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported");
        }
        return this.tilingMode;
    }
    
    public Dimension[] getPreferredTileSizes() {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported");
        }
        return clonePreferredTileSizes(this.preferredTileSizes);
    }
    
    public void setTiling(final int tileWidth, final int tileHeight, final int tileGridXOffset, final int tileGridYOffset) {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported!");
        }
        if (this.getTilingMode() != 2) {
            throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
        }
        if (tileWidth <= 0 || tileHeight <= 0) {
            throw new IllegalArgumentException("tile dimensions are non-positive!");
        }
        final boolean b = tileGridXOffset != 0 || tileGridYOffset != 0;
        if (!this.canOffsetTiles() && b) {
            throw new UnsupportedOperationException("Can't offset tiles!");
        }
        if (this.preferredTileSizes != null) {
            boolean b2 = true;
            for (int i = 0; i < this.preferredTileSizes.length; i += 2) {
                final Dimension dimension = this.preferredTileSizes[i];
                final Dimension dimension2 = this.preferredTileSizes[i + 1];
                if (tileWidth < dimension.width || tileWidth > dimension2.width || tileHeight < dimension.height || tileHeight > dimension2.height) {
                    b2 = false;
                    break;
                }
            }
            if (!b2) {
                throw new IllegalArgumentException("Illegal tile size!");
            }
        }
        this.tilingSet = true;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileGridXOffset = tileGridXOffset;
        this.tileGridYOffset = tileGridYOffset;
    }
    
    public void unsetTiling() {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported!");
        }
        if (this.getTilingMode() != 2) {
            throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
        }
        this.tilingSet = false;
        this.tileWidth = 0;
        this.tileHeight = 0;
        this.tileGridXOffset = 0;
        this.tileGridYOffset = 0;
    }
    
    public int getTileWidth() {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported!");
        }
        if (this.getTilingMode() != 2) {
            throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
        }
        if (!this.tilingSet) {
            throw new IllegalStateException("Tiling parameters not set!");
        }
        return this.tileWidth;
    }
    
    public int getTileHeight() {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported!");
        }
        if (this.getTilingMode() != 2) {
            throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
        }
        if (!this.tilingSet) {
            throw new IllegalStateException("Tiling parameters not set!");
        }
        return this.tileHeight;
    }
    
    public int getTileGridXOffset() {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported!");
        }
        if (this.getTilingMode() != 2) {
            throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
        }
        if (!this.tilingSet) {
            throw new IllegalStateException("Tiling parameters not set!");
        }
        return this.tileGridXOffset;
    }
    
    public int getTileGridYOffset() {
        if (!this.canWriteTiles()) {
            throw new UnsupportedOperationException("Tiling not supported!");
        }
        if (this.getTilingMode() != 2) {
            throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
        }
        if (!this.tilingSet) {
            throw new IllegalStateException("Tiling parameters not set!");
        }
        return this.tileGridYOffset;
    }
    
    public boolean canWriteProgressive() {
        return this.canWriteProgressive;
    }
    
    public void setProgressiveMode(final int progressiveMode) {
        if (!this.canWriteProgressive()) {
            throw new UnsupportedOperationException("Progressive output not supported");
        }
        if (progressiveMode < 0 || progressiveMode > 3) {
            throw new IllegalArgumentException("Illegal value for mode!");
        }
        if (progressiveMode == 2) {
            throw new IllegalArgumentException("MODE_EXPLICIT not supported for progressive output");
        }
        this.progressiveMode = progressiveMode;
    }
    
    public int getProgressiveMode() {
        if (!this.canWriteProgressive()) {
            throw new UnsupportedOperationException("Progressive output not supported");
        }
        return this.progressiveMode;
    }
    
    public boolean canWriteCompressed() {
        return this.canWriteCompressed;
    }
    
    public void setCompressionMode(final int compressionMode) {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        if (compressionMode < 0 || compressionMode > 3) {
            throw new IllegalArgumentException("Illegal value for mode!");
        }
        if ((this.compressionMode = compressionMode) == 2) {
            this.unsetCompression();
        }
    }
    
    public int getCompressionMode() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        return this.compressionMode;
    }
    
    public String[] getCompressionTypes() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported");
        }
        if (this.compressionTypes == null) {
            return null;
        }
        return this.compressionTypes.clone();
    }
    
    public void setCompressionType(final String compressionType) {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        final String[] compressionTypes = this.getCompressionTypes();
        if (compressionTypes == null) {
            throw new UnsupportedOperationException("No settable compression types");
        }
        if (compressionType != null) {
            boolean b = false;
            if (compressionTypes != null) {
                for (int i = 0; i < compressionTypes.length; ++i) {
                    if (compressionType.equals(compressionTypes[i])) {
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                throw new IllegalArgumentException("Unknown compression type!");
            }
        }
        this.compressionType = compressionType;
    }
    
    public String getCompressionType() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        return this.compressionType;
    }
    
    public void unsetCompression() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        this.compressionType = null;
        this.compressionQuality = 1.0f;
    }
    
    public String getLocalizedCompressionTypeName() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        return this.getCompressionType();
    }
    
    public boolean isCompressionLossless() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        return true;
    }
    
    public void setCompressionQuality(final float compressionQuality) {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        if (compressionQuality < 0.0f || compressionQuality > 1.0f) {
            throw new IllegalArgumentException("Quality out-of-bounds!");
        }
        this.compressionQuality = compressionQuality;
    }
    
    public float getCompressionQuality() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        return this.compressionQuality;
    }
    
    public float getBitRate(final float n) {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        if (n < 0.0f || n > 1.0f) {
            throw new IllegalArgumentException("Quality out-of-bounds!");
        }
        return -1.0f;
    }
    
    public String[] getCompressionQualityDescriptions() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        return null;
    }
    
    public float[] getCompressionQualityValues() {
        if (!this.canWriteCompressed()) {
            throw new UnsupportedOperationException("Compression not supported.");
        }
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        return null;
    }
}

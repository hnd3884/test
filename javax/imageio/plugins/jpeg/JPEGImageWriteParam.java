package javax.imageio.plugins.jpeg;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class JPEGImageWriteParam extends ImageWriteParam
{
    private JPEGQTable[] qTables;
    private JPEGHuffmanTable[] DCHuffmanTables;
    private JPEGHuffmanTable[] ACHuffmanTables;
    private boolean optimizeHuffman;
    private String[] compressionNames;
    private float[] qualityVals;
    private String[] qualityDescs;
    
    public JPEGImageWriteParam(final Locale locale) {
        super(locale);
        this.qTables = null;
        this.DCHuffmanTables = null;
        this.ACHuffmanTables = null;
        this.optimizeHuffman = false;
        this.compressionNames = new String[] { "JPEG" };
        this.qualityVals = new float[] { 0.0f, 0.3f, 0.75f, 1.0f };
        this.qualityDescs = new String[] { "Low quality", "Medium quality", "Visually lossless" };
        this.canWriteProgressive = true;
        this.progressiveMode = 0;
        this.canWriteCompressed = true;
        this.compressionTypes = this.compressionNames;
        this.compressionType = this.compressionTypes[0];
        this.compressionQuality = 0.75f;
    }
    
    @Override
    public void unsetCompression() {
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        this.compressionQuality = 0.75f;
    }
    
    @Override
    public boolean isCompressionLossless() {
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        return false;
    }
    
    @Override
    public String[] getCompressionQualityDescriptions() {
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        return this.qualityDescs.clone();
    }
    
    @Override
    public float[] getCompressionQualityValues() {
        if (this.getCompressionMode() != 2) {
            throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
        }
        if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
            throw new IllegalStateException("No compression type set!");
        }
        return this.qualityVals.clone();
    }
    
    public boolean areTablesSet() {
        return this.qTables != null;
    }
    
    public void setEncodeTables(final JPEGQTable[] array, final JPEGHuffmanTable[] array2, final JPEGHuffmanTable[] array3) {
        if (array == null || array2 == null || array3 == null || array.length > 4 || array2.length > 4 || array3.length > 4 || array2.length != array3.length) {
            throw new IllegalArgumentException("Invalid JPEG table arrays");
        }
        this.qTables = array.clone();
        this.DCHuffmanTables = array2.clone();
        this.ACHuffmanTables = array3.clone();
    }
    
    public void unsetEncodeTables() {
        this.qTables = null;
        this.DCHuffmanTables = null;
        this.ACHuffmanTables = null;
    }
    
    public JPEGQTable[] getQTables() {
        return (JPEGQTable[])((this.qTables != null) ? ((JPEGQTable[])this.qTables.clone()) : null);
    }
    
    public JPEGHuffmanTable[] getDCHuffmanTables() {
        return (JPEGHuffmanTable[])((this.DCHuffmanTables != null) ? ((JPEGHuffmanTable[])this.DCHuffmanTables.clone()) : null);
    }
    
    public JPEGHuffmanTable[] getACHuffmanTables() {
        return (JPEGHuffmanTable[])((this.ACHuffmanTables != null) ? ((JPEGHuffmanTable[])this.ACHuffmanTables.clone()) : null);
    }
    
    public void setOptimizeHuffmanTables(final boolean optimizeHuffman) {
        this.optimizeHuffman = optimizeHuffman;
    }
    
    public boolean getOptimizeHuffmanTables() {
        return this.optimizeHuffman;
    }
}

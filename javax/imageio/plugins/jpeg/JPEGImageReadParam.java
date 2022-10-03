package javax.imageio.plugins.jpeg;

import javax.imageio.ImageReadParam;

public class JPEGImageReadParam extends ImageReadParam
{
    private JPEGQTable[] qTables;
    private JPEGHuffmanTable[] DCHuffmanTables;
    private JPEGHuffmanTable[] ACHuffmanTables;
    
    public JPEGImageReadParam() {
        this.qTables = null;
        this.DCHuffmanTables = null;
        this.ACHuffmanTables = null;
    }
    
    public boolean areTablesSet() {
        return this.qTables != null;
    }
    
    public void setDecodeTables(final JPEGQTable[] array, final JPEGHuffmanTable[] array2, final JPEGHuffmanTable[] array3) {
        if (array == null || array2 == null || array3 == null || array.length > 4 || array2.length > 4 || array3.length > 4 || array2.length != array3.length) {
            throw new IllegalArgumentException("Invalid JPEG table arrays");
        }
        this.qTables = array.clone();
        this.DCHuffmanTables = array2.clone();
        this.ACHuffmanTables = array3.clone();
    }
    
    public void unsetDecodeTables() {
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
}

package org.apache.poi.sl.usermodel;

import java.awt.Dimension;
import java.io.IOException;

public interface PictureData
{
    String getContentType();
    
    PictureType getType();
    
    byte[] getData();
    
    void setData(final byte[] p0) throws IOException;
    
    byte[] getChecksum();
    
    Dimension getImageDimension();
    
    Dimension getImageDimensionInPixels();
    
    public enum PictureType
    {
        EMF(2, 2, "image/x-emf", ".emf"), 
        WMF(3, 3, "image/x-wmf", ".wmf"), 
        PICT(4, 4, "image/pict", ".pict"), 
        JPEG(5, 5, "image/jpeg", ".jpg"), 
        PNG(6, 6, "image/png", ".png"), 
        DIB(7, 7, "image/dib", ".dib"), 
        GIF(-1, 8, "image/gif", ".gif"), 
        TIFF(17, 9, "image/tiff", ".tif"), 
        EPS(-1, 10, "image/x-eps", ".eps"), 
        BMP(-1, 11, "image/x-ms-bmp", ".bmp"), 
        WPG(-1, 12, "image/x-wpg", ".wpg"), 
        WDP(-1, 13, "image/vnd.ms-photo", ".wdp"), 
        SVG(-1, -1, "image/svg+xml", ".svg"), 
        UNKNOWN(1, -1, "", ".dat"), 
        ERROR(0, -1, "", ".dat"), 
        CMYKJPEG(18, -1, "image/jpeg", ".jpg"), 
        CLIENT(32, -1, "", ".dat");
        
        public final int nativeId;
        public final int ooxmlId;
        public final String contentType;
        public final String extension;
        
        private PictureType(final int nativeId, final int ooxmlId, final String contentType, final String extension) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
            this.contentType = contentType;
            this.extension = extension;
        }
        
        public static PictureType forNativeID(final int nativeId) {
            for (final PictureType ans : values()) {
                if (ans.nativeId == nativeId) {
                    return ans;
                }
            }
            return (nativeId >= PictureType.CLIENT.nativeId) ? PictureType.CLIENT : PictureType.UNKNOWN;
        }
        
        public static PictureType forOoxmlID(final int ooxmlId) {
            for (final PictureType ans : values()) {
                if (ans.ooxmlId == ooxmlId) {
                    return ans;
                }
            }
            return null;
        }
    }
}

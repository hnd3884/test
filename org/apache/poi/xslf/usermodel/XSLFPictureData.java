package org.apache.poi.xslf.usermodel;

import java.io.OutputStream;
import org.apache.poi.sl.image.ImageHeaderBitmap;
import org.apache.poi.sl.image.ImageHeaderPICT;
import org.apache.poi.sl.image.ImageHeaderWMF;
import org.apache.poi.sl.image.ImageHeaderEMF;
import org.apache.poi.util.Units;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.awt.Dimension;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public final class XSLFPictureData extends POIXMLDocumentPart implements PictureData
{
    private Long checksum;
    private Dimension origSize;
    private int index;
    
    protected XSLFPictureData() {
        this.index = -1;
    }
    
    public XSLFPictureData(final PackagePart part) {
        super(part);
        this.index = -1;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.getPackagePart().getInputStream();
    }
    
    public byte[] getData() {
        try {
            return IOUtils.toByteArray(this.getInputStream());
        }
        catch (final IOException e) {
            throw new POIXMLException(e);
        }
    }
    
    public String getFileName() {
        final String name = this.getPackagePart().getPartName().getName();
        return name.substring(name.lastIndexOf(47) + 1);
    }
    
    public String suggestFileExtension() {
        return this.getPackagePart().getPartName().getExtension();
    }
    
    public byte[] getChecksum() {
        this.cacheProperties();
        final byte[] cs = new byte[8];
        LittleEndian.putLong(cs, 0, (long)this.checksum);
        return cs;
    }
    
    public Dimension getImageDimension() {
        this.cacheProperties();
        return this.origSize;
    }
    
    public Dimension getImageDimensionInPixels() {
        final Dimension dim = this.getImageDimension();
        return new Dimension(Units.pointsToPixel(dim.getWidth()), Units.pointsToPixel(dim.getHeight()));
    }
    
    protected void cacheProperties() {
        if (this.origSize == null || this.checksum == null) {
            final byte[] data = this.getData();
            this.checksum = IOUtils.calculateChecksum(data);
            final PictureData.PictureType pt = this.getType();
            if (pt == null) {
                this.origSize = new Dimension(1, 1);
                return;
            }
            switch (pt) {
                case EMF: {
                    this.origSize = new ImageHeaderEMF(data, 0).getSize();
                    break;
                }
                case WMF: {
                    this.origSize = new ImageHeaderWMF(data, 0).getSize();
                    break;
                }
                case PICT: {
                    this.origSize = new ImageHeaderPICT(data, 0).getSize();
                    break;
                }
                default: {
                    this.origSize = new ImageHeaderBitmap(data, 0).getSize();
                    break;
                }
            }
        }
    }
    
    @Override
    protected void prepareForCommit() {
    }
    
    public String getContentType() {
        return this.getPackagePart().getContentType();
    }
    
    public void setData(final byte[] data) throws IOException {
        final OutputStream os = this.getPackagePart().getOutputStream();
        os.write(data);
        os.close();
        this.checksum = IOUtils.calculateChecksum(data);
        this.origSize = null;
    }
    
    public PictureData.PictureType getType() {
        final String ct = this.getContentType();
        if (XSLFRelation.IMAGE_EMF.getContentType().equals(ct)) {
            return PictureData.PictureType.EMF;
        }
        if (XSLFRelation.IMAGE_WMF.getContentType().equals(ct)) {
            return PictureData.PictureType.WMF;
        }
        if (XSLFRelation.IMAGE_PICT.getContentType().equals(ct)) {
            return PictureData.PictureType.PICT;
        }
        if (XSLFRelation.IMAGE_JPEG.getContentType().equals(ct)) {
            return PictureData.PictureType.JPEG;
        }
        if (XSLFRelation.IMAGE_PNG.getContentType().equals(ct)) {
            return PictureData.PictureType.PNG;
        }
        if (XSLFRelation.IMAGE_DIB.getContentType().equals(ct)) {
            return PictureData.PictureType.DIB;
        }
        if (XSLFRelation.IMAGE_GIF.getContentType().equals(ct)) {
            return PictureData.PictureType.GIF;
        }
        if (XSLFRelation.IMAGE_EPS.getContentType().equals(ct)) {
            return PictureData.PictureType.EPS;
        }
        if (XSLFRelation.IMAGE_BMP.getContentType().equals(ct)) {
            return PictureData.PictureType.BMP;
        }
        if (XSLFRelation.IMAGE_WPG.getContentType().equals(ct)) {
            return PictureData.PictureType.WPG;
        }
        if (XSLFRelation.IMAGE_WDP.getContentType().equals(ct)) {
            return PictureData.PictureType.WDP;
        }
        if (XSLFRelation.IMAGE_TIFF.getContentType().equals(ct)) {
            return PictureData.PictureType.TIFF;
        }
        if (XSLFRelation.IMAGE_SVG.getContentType().equals(ct)) {
            return PictureData.PictureType.SVG;
        }
        return null;
    }
    
    static XSLFRelation getRelationForType(final PictureData.PictureType pt) {
        switch (pt) {
            case EMF: {
                return XSLFRelation.IMAGE_EMF;
            }
            case WMF: {
                return XSLFRelation.IMAGE_WMF;
            }
            case PICT: {
                return XSLFRelation.IMAGE_PICT;
            }
            case JPEG: {
                return XSLFRelation.IMAGE_JPEG;
            }
            case PNG: {
                return XSLFRelation.IMAGE_PNG;
            }
            case DIB: {
                return XSLFRelation.IMAGE_DIB;
            }
            case GIF: {
                return XSLFRelation.IMAGE_GIF;
            }
            case EPS: {
                return XSLFRelation.IMAGE_EPS;
            }
            case BMP: {
                return XSLFRelation.IMAGE_BMP;
            }
            case WPG: {
                return XSLFRelation.IMAGE_WPG;
            }
            case WDP: {
                return XSLFRelation.IMAGE_WDP;
            }
            case TIFF: {
                return XSLFRelation.IMAGE_TIFF;
            }
            case SVG: {
                return XSLFRelation.IMAGE_SVG;
            }
            default: {
                return null;
            }
        }
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
}

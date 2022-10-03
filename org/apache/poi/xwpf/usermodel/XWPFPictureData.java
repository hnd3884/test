package org.apache.poi.xwpf.usermodel;

import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.Arrays;
import java.io.InputStream;
import java.io.Closeable;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XWPFPictureData extends POIXMLDocumentPart
{
    protected static final POIXMLRelation[] RELATIONS;
    private Long checksum;
    
    protected XWPFPictureData() {
    }
    
    public XWPFPictureData(final PackagePart part) {
        super(part);
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        super.onDocumentRead();
    }
    
    public byte[] getData() {
        try {
            return IOUtils.toByteArray(this.getPackagePart().getInputStream());
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
    
    public int getPictureType() {
        final String contentType = this.getPackagePart().getContentType();
        for (int i = 0; i < XWPFPictureData.RELATIONS.length; ++i) {
            if (XWPFPictureData.RELATIONS[i] != null) {
                if (XWPFPictureData.RELATIONS[i].getContentType().equals(contentType)) {
                    return i;
                }
            }
        }
        return 0;
    }
    
    public Long getChecksum() {
        if (this.checksum == null) {
            InputStream is = null;
            byte[] data;
            try {
                is = this.getPackagePart().getInputStream();
                data = IOUtils.toByteArray(is);
            }
            catch (final IOException e) {
                throw new POIXMLException(e);
            }
            finally {
                IOUtils.closeQuietly((Closeable)is);
            }
            this.checksum = IOUtils.calculateChecksum(data);
        }
        return this.checksum;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof XWPFPictureData)) {
            return false;
        }
        final XWPFPictureData picData = (XWPFPictureData)obj;
        final PackagePart foreignPackagePart = picData.getPackagePart();
        final PackagePart ownPackagePart = this.getPackagePart();
        if ((foreignPackagePart != null && ownPackagePart == null) || (foreignPackagePart == null && ownPackagePart != null)) {
            return false;
        }
        if (ownPackagePart != null) {
            final OPCPackage foreignPackage = foreignPackagePart.getPackage();
            final OPCPackage ownPackage = ownPackagePart.getPackage();
            if ((foreignPackage != null && ownPackage == null) || (foreignPackage == null && ownPackage != null)) {
                return false;
            }
            if (ownPackage != null && !ownPackage.equals(foreignPackage)) {
                return false;
            }
        }
        final Long foreignChecksum = picData.getChecksum();
        final Long localChecksum = this.getChecksum();
        return localChecksum.equals(foreignChecksum) && Arrays.equals(this.getData(), picData.getData());
    }
    
    @Override
    public int hashCode() {
        return this.getChecksum().hashCode();
    }
    
    @Override
    protected void prepareForCommit() {
    }
    
    static {
        (RELATIONS = new POIXMLRelation[13])[2] = XWPFRelation.IMAGE_EMF;
        XWPFPictureData.RELATIONS[3] = XWPFRelation.IMAGE_WMF;
        XWPFPictureData.RELATIONS[4] = XWPFRelation.IMAGE_PICT;
        XWPFPictureData.RELATIONS[5] = XWPFRelation.IMAGE_JPEG;
        XWPFPictureData.RELATIONS[6] = XWPFRelation.IMAGE_PNG;
        XWPFPictureData.RELATIONS[7] = XWPFRelation.IMAGE_DIB;
        XWPFPictureData.RELATIONS[8] = XWPFRelation.IMAGE_GIF;
        XWPFPictureData.RELATIONS[9] = XWPFRelation.IMAGE_TIFF;
        XWPFPictureData.RELATIONS[10] = XWPFRelation.IMAGE_EPS;
        XWPFPictureData.RELATIONS[11] = XWPFRelation.IMAGE_BMP;
        XWPFPictureData.RELATIONS[12] = XWPFRelation.IMAGE_WPG;
    }
}

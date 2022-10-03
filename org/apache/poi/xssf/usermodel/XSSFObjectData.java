package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import java.io.Closeable;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtension;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShapeNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.ObjectData;

public class XSSFObjectData extends XSSFSimpleShape implements ObjectData
{
    private static final POILogger LOG;
    private static CTShape prototype;
    private CTOleObject oleObject;
    
    protected XSSFObjectData(final XSSFDrawing drawing, final CTShape ctShape) {
        super(drawing, ctShape);
    }
    
    protected static CTShape prototype() {
        final String drawNS = "http://schemas.microsoft.com/office/drawing/2010/main";
        if (XSSFObjectData.prototype == null) {
            final CTShape shape = CTShape.Factory.newInstance();
            final CTShapeNonVisual nv = shape.addNewNvSpPr();
            final CTNonVisualDrawingProps nvp = nv.addNewCNvPr();
            nvp.setId(1L);
            nvp.setName("Shape 1");
            final CTOfficeArtExtensionList extLst = nvp.addNewExtLst();
            final CTOfficeArtExtension ext = extLst.addNewExt();
            ext.setUri("{63B3BB69-23CF-44E3-9099-C40C66FF867C}");
            final XmlCursor cur = ext.newCursor();
            cur.toEndToken();
            cur.beginElement(new QName("http://schemas.microsoft.com/office/drawing/2010/main", "compatExt", "a14"));
            cur.insertNamespace("a14", "http://schemas.microsoft.com/office/drawing/2010/main");
            cur.insertAttributeWithValue("spid", "_x0000_s1");
            cur.dispose();
            nv.addNewCNvSpPr();
            final CTShapeProperties sp = shape.addNewSpPr();
            final CTTransform2D t2d = sp.addNewXfrm();
            final CTPositiveSize2D p1 = t2d.addNewExt();
            p1.setCx(0L);
            p1.setCy(0L);
            final CTPoint2D p2 = t2d.addNewOff();
            p2.setX(0L);
            p2.setY(0L);
            final CTPresetGeometry2D geom = sp.addNewPrstGeom();
            geom.setPrst(STShapeType.RECT);
            geom.addNewAvLst();
            XSSFObjectData.prototype = shape;
        }
        return XSSFObjectData.prototype;
    }
    
    public String getOLE2ClassName() {
        return this.getOleObject().getProgId();
    }
    
    public CTOleObject getOleObject() {
        if (this.oleObject == null) {
            final long shapeId = this.getCTShape().getNvSpPr().getCNvPr().getId();
            this.oleObject = this.getSheet().readOleObject(shapeId);
            if (this.oleObject == null) {
                throw new POIXMLException("Ole object not found in sheet container - it's probably a control element");
            }
        }
        return this.oleObject;
    }
    
    public byte[] getObjectData() throws IOException {
        final InputStream is = this.getObjectPart().getInputStream();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, (OutputStream)bos);
        is.close();
        return bos.toByteArray();
    }
    
    public PackagePart getObjectPart() {
        if (!this.getOleObject().isSetId()) {
            throw new POIXMLException("Invalid ole object found in sheet container");
        }
        final POIXMLDocumentPart pdp = this.getSheet().getRelationById(this.getOleObject().getId());
        return (pdp == null) ? null : pdp.getPackagePart();
    }
    
    public boolean hasDirectoryEntry() {
        InputStream is = null;
        try {
            is = this.getObjectPart().getInputStream();
            is = FileMagic.prepareToCheckMagic(is);
            return FileMagic.valueOf(is) == FileMagic.OLE2;
        }
        catch (final IOException e) {
            XSSFObjectData.LOG.log(5, new Object[] { "can't determine if directory entry exists", e });
            return false;
        }
        finally {
            IOUtils.closeQuietly((Closeable)is);
        }
    }
    
    public DirectoryEntry getDirectory() throws IOException {
        try (final InputStream is = this.getObjectPart().getInputStream()) {
            return (DirectoryEntry)new POIFSFileSystem(is).getRoot();
        }
    }
    
    public String getFileName() {
        return this.getObjectPart().getPartName().getName();
    }
    
    protected XSSFSheet getSheet() {
        return (XSSFSheet)this.getDrawing().getParent();
    }
    
    public XSSFPictureData getPictureData() {
        final XmlCursor cur = this.getOleObject().newCursor();
        try {
            if (cur.toChild("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "objectPr")) {
                final String blipId = cur.getAttributeText(new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id"));
                return (XSSFPictureData)this.getSheet().getRelationById(blipId);
            }
            return null;
        }
        finally {
            cur.dispose();
        }
    }
    
    public String getContentType() {
        return this.getObjectPart().getContentType();
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSSFObjectData.class);
    }
}

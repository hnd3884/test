package org.apache.poi.xslf.usermodel;

import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.filesystem.Ole10Native;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.ObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPictureNonVisual;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrameNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import java.io.OutputStream;
import org.apache.poi.sl.usermodel.ObjectMetaData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import javax.xml.stream.XMLStreamReader;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObject;
import javax.xml.namespace.QName;
import org.apache.poi.sl.usermodel.ObjectShape;

public class XSLFObjectShape extends XSLFGraphicFrame implements ObjectShape<XSLFShape, XSLFTextParagraph>
{
    static final String OLE_URI = "http://schemas.openxmlformats.org/presentationml/2006/ole";
    private static final QName[] GRAPHIC;
    private static final QName[] GRAPHIC_DATA;
    private static final QName[] OLE_OBJ;
    private static final QName[] CT_PICTURE;
    private CTOleObject _oleObject;
    private XSLFPictureData _data;
    
    XSLFObjectShape(final CTGraphicalObjectFrame shape, final XSLFSheet sheet) {
        super(shape, sheet);
        try {
            this._oleObject = this.selectProperty(CTOleObject.class, null, new QName[][] { XSLFObjectShape.GRAPHIC, XSLFObjectShape.GRAPHIC_DATA, XSLFObjectShape.OLE_OBJ });
        }
        catch (final XmlException e) {
            throw new IllegalStateException((Throwable)e);
        }
    }
    
    @Internal
    public CTOleObject getCTOleObject() {
        return this._oleObject;
    }
    
    public XSLFObjectData getObjectData() {
        final String oleRel = this.getCTOleObject().getId();
        return this.getSheet().getRelationPartById(oleRel).getDocumentPart();
    }
    
    public String getProgId() {
        return (this._oleObject == null) ? null : this._oleObject.getProgId();
    }
    
    public String getFullName() {
        return (this._oleObject == null) ? null : this._oleObject.getName();
    }
    
    public XSLFPictureData getPictureData() {
        if (this._data == null) {
            final String blipId = this.getBlipId();
            if (blipId == null) {
                return null;
            }
            final PackagePart p = this.getSheet().getPackagePart();
            final PackageRelationship rel = p.getRelationship(blipId);
            if (rel != null) {
                try {
                    final PackagePart imgPart = p.getRelatedPart(rel);
                    this._data = new XSLFPictureData(imgPart);
                }
                catch (final Exception e) {
                    throw new POIXMLException(e);
                }
            }
        }
        return this._data;
    }
    
    protected CTBlip getBlip() {
        return this.getBlipFill().getBlip();
    }
    
    protected String getBlipId() {
        final String id = this.getBlip().getEmbed();
        if (id.isEmpty()) {
            return null;
        }
        return id;
    }
    
    protected CTBlipFillProperties getBlipFill() {
        try {
            final CTPicture pic = this.selectProperty(CTPicture.class, XSLFObjectShape::parse, new QName[][] { XSLFObjectShape.GRAPHIC, XSLFObjectShape.GRAPHIC_DATA, XSLFObjectShape.OLE_OBJ, XSLFObjectShape.CT_PICTURE });
            return (pic != null) ? pic.getBlipFill() : null;
        }
        catch (final XmlException e) {
            return null;
        }
    }
    
    private static CTPicture parse(final XMLStreamReader reader) throws XmlException {
        final CTGroupShape gs = CTGroupShape.Factory.parse(reader);
        return (gs.sizeOfPicArray() > 0) ? gs.getPicArray(0) : null;
    }
    
    public OutputStream updateObjectData(final ObjectMetaData.Application application, final ObjectMetaData metaData) throws IOException {
        final ObjectMetaData md = (application != null) ? application.getMetaData() : metaData;
        if (md == null || md.getClassID() == null) {
            throw new IllegalArgumentException("either application and/or metaData needs to be set.");
        }
        final XSLFSheet sheet = this.getSheet();
        POIXMLDocumentPart.RelationPart rp;
        if (this._oleObject.isSetId()) {
            rp = sheet.getRelationPartById(this._oleObject.getId());
        }
        else {
            try {
                final XSLFRelation descriptor = XSLFRelation.OLE_OBJECT;
                final OPCPackage pack = sheet.getPackagePart().getPackage();
                final int nextIdx = pack.getUnusedPartIndex(descriptor.getDefaultFileName());
                rp = sheet.createRelationship(descriptor, XSLFFactory.getInstance(), nextIdx, false);
                this._oleObject.setId(rp.getRelationship().getId());
            }
            catch (final InvalidFormatException e) {
                throw new IOException("Unable to add new ole embedding", e);
            }
        }
        this._oleObject.setProgId(md.getProgId());
        this._oleObject.setName(md.getObjectName());
        return new XSLFObjectOutputStream(rp.getDocumentPart().getPackagePart(), md);
    }
    
    static CTGraphicalObjectFrame prototype(final int shapeId, final String picRel) {
        final CTGraphicalObjectFrame frame = CTGraphicalObjectFrame.Factory.newInstance();
        final CTGraphicalObjectFrameNonVisual nvGr = frame.addNewNvGraphicFramePr();
        final CTNonVisualDrawingProps cnv = nvGr.addNewCNvPr();
        cnv.setName("Object " + shapeId);
        cnv.setId((long)shapeId);
        nvGr.addNewCNvGraphicFramePr();
        nvGr.addNewNvPr();
        frame.addNewXfrm();
        final CTGraphicalObjectData gr = frame.addNewGraphic().addNewGraphicData();
        gr.setUri("http://schemas.openxmlformats.org/presentationml/2006/ole");
        final XmlCursor grCur = gr.newCursor();
        grCur.toEndToken();
        grCur.beginElement(new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "oleObj"));
        grCur.insertElement(new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "embed"));
        final CTGroupShape grpShp = CTGroupShape.Factory.newInstance();
        final CTPicture pic = grpShp.addNewPic();
        final CTPictureNonVisual nvPicPr = pic.addNewNvPicPr();
        final CTNonVisualDrawingProps cNvPr = nvPicPr.addNewCNvPr();
        cNvPr.setName("");
        cNvPr.setId(0L);
        nvPicPr.addNewCNvPicPr();
        nvPicPr.addNewNvPr();
        final CTBlipFillProperties blip = pic.addNewBlipFill();
        blip.addNewBlip().setEmbed(picRel);
        blip.addNewStretch().addNewFillRect();
        final CTShapeProperties spPr = pic.addNewSpPr();
        final CTTransform2D xfrm = spPr.addNewXfrm();
        final CTPoint2D off = xfrm.addNewOff();
        off.setX(1270000L);
        off.setY(1270000L);
        final CTPositiveSize2D xext = xfrm.addNewExt();
        xext.setCx(1270000L);
        xext.setCy(1270000L);
        spPr.addNewPrstGeom().setPrst(STShapeType.RECT);
        final XmlCursor picCur = grpShp.newCursor();
        picCur.toStartDoc();
        picCur.moveXmlContents(grCur);
        picCur.dispose();
        grCur.dispose();
        return frame;
    }
    
    static {
        GRAPHIC = new QName[] { new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphic") };
        GRAPHIC_DATA = new QName[] { new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphicData") };
        OLE_OBJ = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "oleObj") };
        CT_PICTURE = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "pic") };
    }
    
    private static class XSLFObjectOutputStream extends ByteArrayOutputStream
    {
        final PackagePart objectPart;
        final ObjectMetaData metaData;
        
        private XSLFObjectOutputStream(final PackagePart objectPart, final ObjectMetaData metaData) {
            super(100000);
            this.objectPart = objectPart;
            this.metaData = metaData;
        }
        
        @Override
        public void close() throws IOException {
            this.objectPart.clear();
            try (final OutputStream os = this.objectPart.getOutputStream()) {
                final ByteArrayInputStream bis = new ByteArrayInputStream(this.buf, 0, this.size());
                final FileMagic fm = FileMagic.valueOf(this.buf);
                if (fm == FileMagic.OLE2) {
                    try (final POIFSFileSystem poifs = new POIFSFileSystem((InputStream)bis)) {
                        poifs.getRoot().setStorageClsid(this.metaData.getClassID());
                        poifs.writeFilesystem(os);
                    }
                }
                else if (this.metaData.getOleEntry() == null) {
                    os.write(this.buf, 0, this.size());
                }
                else {
                    try (final POIFSFileSystem poifs = new POIFSFileSystem()) {
                        final ClassID clsId = this.metaData.getClassID();
                        if (clsId != null) {
                            poifs.getRoot().setStorageClsid(clsId);
                        }
                        poifs.createDocument((InputStream)bis, this.metaData.getOleEntry());
                        Ole10Native.createOleMarkerEntry(poifs);
                        poifs.writeFilesystem(os);
                    }
                }
            }
        }
    }
}

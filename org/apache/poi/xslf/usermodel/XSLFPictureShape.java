package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.geom.Dimension2D;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.draw.SVGImageRenderer;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtension;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import java.awt.Insets;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.net.URI;
import org.apache.poi.sl.usermodel.Placeholder;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPictureNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import javax.xml.namespace.QName;
import org.apache.poi.util.POILogger;
import org.apache.poi.sl.usermodel.PictureShape;

public class XSLFPictureShape extends XSLFSimpleShape implements PictureShape<XSLFShape, XSLFTextParagraph>
{
    private static final POILogger LOG;
    private static final String MS_DML_NS = "http://schemas.microsoft.com/office/drawing/2010/main";
    private static final String MS_SVG_NS = "http://schemas.microsoft.com/office/drawing/2016/SVG/main";
    private static final String BITMAP_URI = "{28A0092B-C50C-407E-A947-70E740481C1C}";
    private static final String SVG_URI = "{96DAC541-7B7A-43D3-8B79-37D633B846F1}";
    private static final QName EMBED_TAG;
    private static final QName[] BLIP_FILL;
    private XSLFPictureData _data;
    
    XSLFPictureShape(final CTPicture shape, final XSLFSheet sheet) {
        super((XmlObject)shape, sheet);
    }
    
    static CTPicture prototype(final int shapeId, final String rel) {
        final CTPicture ct = CTPicture.Factory.newInstance();
        final CTPictureNonVisual nvSpPr = ct.addNewNvPicPr();
        final CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("Picture " + shapeId);
        cnv.setId((long)shapeId);
        nvSpPr.addNewCNvPicPr().addNewPicLocks().setNoChangeAspect(true);
        nvSpPr.addNewNvPr();
        final CTBlipFillProperties blipFill = ct.addNewBlipFill();
        final CTBlip blip = blipFill.addNewBlip();
        blip.setEmbed(rel);
        blipFill.addNewStretch().addNewFillRect();
        final CTShapeProperties spPr = ct.addNewSpPr();
        final CTPresetGeometry2D prst = spPr.addNewPrstGeom();
        prst.setPrst(STShapeType.RECT);
        prst.addNewAvLst();
        return ct;
    }
    
    public boolean isExternalLinkedPicture() {
        return this.getBlipId() == null && this.getBlipLink() != null;
    }
    
    public XSLFPictureData getPictureData() {
        if (this._data == null) {
            final String blipId = this.getBlipId();
            if (blipId == null) {
                return null;
            }
            this._data = (XSLFPictureData)this.getSheet().getRelationById(blipId);
        }
        return this._data;
    }
    
    public void setPlaceholder(final Placeholder placeholder) {
        super.setPlaceholder(placeholder);
    }
    
    public URI getPictureLink() {
        if (this.getBlipId() != null) {
            return null;
        }
        final String rId = this.getBlipLink();
        if (rId == null) {
            return null;
        }
        final PackagePart p = this.getSheet().getPackagePart();
        final PackageRelationship rel = p.getRelationship(rId);
        if (rel != null) {
            return rel.getTargetURI();
        }
        return null;
    }
    
    protected CTBlipFillProperties getBlipFill() {
        final CTPicture ct = (CTPicture)this.getXmlObject();
        final CTBlipFillProperties bfp = ct.getBlipFill();
        if (bfp != null) {
            return bfp;
        }
        try {
            return this.selectProperty(CTBlipFillProperties.class, XSLFPictureShape::parse, new QName[][] { XSLFPictureShape.BLIP_FILL });
        }
        catch (final XmlException xe) {
            return null;
        }
    }
    
    private static CTBlipFillProperties parse(final XMLStreamReader reader) throws XmlException {
        final CTPicture pic = CTPicture.Factory.parse(reader);
        return (pic != null) ? pic.getBlipFill() : null;
    }
    
    protected CTBlip getBlip() {
        return this.getBlipFill().getBlip();
    }
    
    protected String getBlipLink() {
        final CTBlip blip = this.getBlip();
        if (blip != null) {
            final String link = blip.getLink();
            return link.isEmpty() ? null : link;
        }
        return null;
    }
    
    protected String getBlipId() {
        final CTBlip blip = this.getBlip();
        if (blip != null) {
            final String id = blip.getEmbed();
            return id.isEmpty() ? null : id;
        }
        return null;
    }
    
    public Insets getClipping() {
        final CTRelativeRect r = this.getBlipFill().getSrcRect();
        return (r == null) ? null : new Insets(r.getT(), r.getL(), r.getB(), r.getR());
    }
    
    public void setSvgImage(final XSLFPictureData svgPic) {
        final CTBlip blip = this.getBlip();
        final CTOfficeArtExtensionList extLst = blip.isSetExtLst() ? blip.getExtLst() : blip.addNewExtLst();
        final int bitmapId = this.getExt(extLst, "{28A0092B-C50C-407E-A947-70E740481C1C}");
        if (bitmapId == -1) {
            final CTOfficeArtExtension extBitmap = extLst.addNewExt();
            extBitmap.setUri("{28A0092B-C50C-407E-A947-70E740481C1C}");
            final XmlCursor cur = extBitmap.newCursor();
            cur.toEndToken();
            cur.beginElement(new QName("http://schemas.microsoft.com/office/drawing/2010/main", "useLocalDpi", "a14"));
            cur.insertNamespace("a14", "http://schemas.microsoft.com/office/drawing/2010/main");
            cur.insertAttributeWithValue("val", "0");
            cur.dispose();
        }
        final int svgId = this.getExt(extLst, "{96DAC541-7B7A-43D3-8B79-37D633B846F1}");
        if (svgId != -1) {
            extLst.removeExt(svgId);
        }
        String svgRelId = this.getSheet().getRelationId(svgPic);
        if (svgRelId == null) {
            svgRelId = this.getSheet().addRelation(null, XSLFRelation.IMAGE_SVG, svgPic).getRelationship().getId();
        }
        final CTOfficeArtExtension svgBitmap = extLst.addNewExt();
        svgBitmap.setUri("{96DAC541-7B7A-43D3-8B79-37D633B846F1}");
        final XmlCursor cur2 = svgBitmap.newCursor();
        cur2.toEndToken();
        cur2.beginElement(new QName("http://schemas.microsoft.com/office/drawing/2016/SVG/main", "svgBlip", "asvg"));
        cur2.insertNamespace("asvg", "http://schemas.microsoft.com/office/drawing/2016/SVG/main");
        cur2.insertAttributeWithValue(XSLFPictureShape.EMBED_TAG, svgRelId);
        cur2.dispose();
    }
    
    public PictureData getAlternativePictureData() {
        return (PictureData)this.getSvgImage();
    }
    
    public XSLFPictureData getSvgImage() {
        final CTBlip blip = this.getBlip();
        if (blip == null) {
            return null;
        }
        final CTOfficeArtExtensionList extLst = blip.getExtLst();
        if (extLst == null) {
            return null;
        }
        for (int size = extLst.sizeOfExtArray(), i = 0; i < size; ++i) {
            final XmlCursor cur = extLst.getExtArray(i).newCursor();
            try {
                if (cur.toChild("http://schemas.microsoft.com/office/drawing/2016/SVG/main", "svgBlip")) {
                    final String svgRelId = cur.getAttributeText(XSLFPictureShape.EMBED_TAG);
                    return (svgRelId != null) ? ((XSLFPictureData)this.getSheet().getRelationById(svgRelId)) : null;
                }
            }
            finally {
                cur.dispose();
            }
        }
        return null;
    }
    
    public static XSLFPictureShape addSvgImage(final XSLFSheet sheet, final XSLFPictureData svgPic, final PictureData.PictureType previewType, final Rectangle2D anchor) throws IOException {
        final SVGImageRenderer renderer = new SVGImageRenderer();
        try (final InputStream is = svgPic.getInputStream()) {
            renderer.loadImage(is, svgPic.getType().contentType);
        }
        final Dimension2D dim = renderer.getDimension();
        final Rectangle2D anc = (anchor != null) ? anchor : new Rectangle2D.Double(0.0, 0.0, Units.pixelToPoints((double)(int)dim.getWidth()), Units.pixelToPoints((double)(int)dim.getHeight()));
        PictureData.PictureType pt = (previewType != null) ? previewType : PictureData.PictureType.PNG;
        if (pt != PictureData.PictureType.JPEG || pt != PictureData.PictureType.GIF || pt != PictureData.PictureType.PNG) {
            pt = PictureData.PictureType.PNG;
        }
        final BufferedImage thmBI = renderer.getImage(dim);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(100000);
        ImageIO.write(thmBI, pt.extension.substring(1), bos);
        final XSLFPictureData pngPic = sheet.getSlideShow().addPicture(new ByteArrayInputStream(bos.toByteArray()), pt);
        final XSLFPictureShape shape = sheet.createPicture((PictureData)pngPic);
        shape.setAnchor(anc);
        shape.setSvgImage(svgPic);
        return shape;
    }
    
    private int getExt(final CTOfficeArtExtensionList extLst, final String uri) {
        for (int size = extLst.sizeOfExtArray(), i = 0; i < size; ++i) {
            final CTOfficeArtExtension ext = extLst.getExtArray(i);
            if (uri.equals(ext.getUri())) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    void copy(final XSLFShape sh) {
        super.copy(sh);
        final XSLFPictureShape p = (XSLFPictureShape)sh;
        final String blipId = p.getBlipId();
        if (blipId == null) {
            XSLFPictureShape.LOG.log(5, new Object[] { "unable to copy invalid picture shape" });
            return;
        }
        final String relId = this.getSheet().importBlip(blipId, p.getSheet());
        final CTPicture ct = (CTPicture)this.getXmlObject();
        final CTBlip blip = this.getBlipFill().getBlip();
        blip.setEmbed(relId);
        final CTApplicationNonVisualDrawingProps nvPr = ct.getNvPicPr().getNvPr();
        if (nvPr.isSetCustDataLst()) {
            nvPr.unsetCustDataLst();
        }
        if (blip.isSetExtLst()) {
            final CTOfficeArtExtensionList extLst = blip.getExtLst();
            for (final CTOfficeArtExtension ext : extLst.getExtArray()) {
                final String xpath = "declare namespace a14='http://schemas.microsoft.com/office/drawing/2010/main' $this//a14:imgProps/a14:imgLayer";
                final XmlObject[] obj = ext.selectPath(xpath);
                if (obj != null && obj.length == 1) {
                    final XmlCursor c = obj[0].newCursor();
                    final String id = c.getAttributeText(XSLFPictureShape.EMBED_TAG);
                    final String newId = this.getSheet().importBlip(id, p.getSheet());
                    c.setAttributeText(XSLFPictureShape.EMBED_TAG, newId);
                    c.dispose();
                }
            }
        }
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSLFPictureShape.class);
        EMBED_TAG = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "embed", "rel");
        BLIP_FILL = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "blipFill") };
    }
}

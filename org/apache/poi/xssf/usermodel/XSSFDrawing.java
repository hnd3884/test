package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.ObjectData;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAbsoluteAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTOneCellAnchor;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.poi.util.Units;
import org.apache.poi.ss.util.ImageUtils;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STEditAs;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.ss.util.CellAddress;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;
import org.apache.xmlbeans.XmlObject;
import java.io.OutputStream;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public final class XSSFDrawing extends POIXMLDocumentPart implements Drawing<XSSFShape>
{
    private static final POILogger LOG;
    private CTDrawing drawing;
    private long numOfGraphicFrames;
    protected static final String NAMESPACE_A = "http://schemas.openxmlformats.org/drawingml/2006/main";
    protected static final String NAMESPACE_C = "http://schemas.openxmlformats.org/drawingml/2006/chart";
    
    protected XSSFDrawing() {
        this.drawing = newDrawing();
    }
    
    public XSSFDrawing(final PackagePart part) throws IOException, XmlException {
        super(part);
        final XmlOptions options = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        options.setLoadReplaceDocumentElement((QName)null);
        try (final InputStream is = part.getInputStream()) {
            this.drawing = CTDrawing.Factory.parse(is, options);
        }
    }
    
    private static CTDrawing newDrawing() {
        return CTDrawing.Factory.newInstance();
    }
    
    @Internal
    public CTDrawing getCTDrawing() {
        return this.drawing;
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTDrawing.type.getName().getNamespaceURI(), "wsDr", "xdr"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.drawing.save(out, xmlOptions);
        out.close();
    }
    
    public XSSFClientAnchor createAnchor(final int dx1, final int dy1, final int dx2, final int dy2, final int col1, final int row1, final int col2, final int row2) {
        return new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2);
    }
    
    public XSSFTextBox createTextbox(final XSSFClientAnchor anchor) {
        final long shapeId = this.newShapeId();
        final CTTwoCellAnchor ctAnchor = this.createTwoCellAnchor(anchor);
        final CTShape ctShape = ctAnchor.addNewSp();
        ctShape.set((XmlObject)XSSFSimpleShape.prototype());
        ctShape.getNvSpPr().getCNvPr().setId(shapeId);
        final XSSFTextBox shape = new XSSFTextBox(this, ctShape);
        shape.anchor = anchor;
        return shape;
    }
    
    public XSSFPicture createPicture(final XSSFClientAnchor anchor, final int pictureIndex) {
        final PackageRelationship rel = this.addPictureReference(pictureIndex);
        final long shapeId = this.newShapeId();
        final CTTwoCellAnchor ctAnchor = this.createTwoCellAnchor(anchor);
        final CTPicture ctShape = ctAnchor.addNewPic();
        ctShape.set((XmlObject)XSSFPicture.prototype());
        ctShape.getNvPicPr().getCNvPr().setId(shapeId);
        final XSSFPicture shape = new XSSFPicture(this, ctShape);
        shape.anchor = anchor;
        shape.setPictureReference(rel);
        ctShape.getSpPr().setXfrm(this.createXfrm(anchor));
        return shape;
    }
    
    public XSSFPicture createPicture(final ClientAnchor anchor, final int pictureIndex) {
        return this.createPicture((XSSFClientAnchor)anchor, pictureIndex);
    }
    
    public XSSFChart createChart(final XSSFClientAnchor anchor) {
        final int chartNumber = this.getPackagePart().getPackage().getPartsByContentType(XSSFRelation.CHART.getContentType()).size() + 1;
        final RelationPart rp = this.createRelationship(XSSFRelation.CHART, XSSFFactory.getInstance(), chartNumber, false);
        final XSSFChart chart = rp.getDocumentPart();
        final String chartRelId = rp.getRelationship().getId();
        final XSSFGraphicFrame frame = this.createGraphicFrame(anchor);
        frame.setChart(chart, chartRelId);
        frame.getCTGraphicalObjectFrame().setXfrm(this.createXfrm(anchor));
        return chart;
    }
    
    public XSSFChart createChart(final ClientAnchor anchor) {
        return this.createChart((XSSFClientAnchor)anchor);
    }
    
    public XSSFChart importChart(final XSSFChart srcChart) {
        final CTTwoCellAnchor anchor = ((XSSFDrawing)srcChart.getParent()).getCTDrawing().getTwoCellAnchorArray(0);
        final CTMarker from = (CTMarker)anchor.getFrom().copy();
        final CTMarker to = (CTMarker)anchor.getTo().copy();
        final XSSFClientAnchor destAnchor = new XSSFClientAnchor(from, to);
        destAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        final XSSFChart destChart = this.createChart(destAnchor);
        destChart.getCTChartSpace().set(srcChart.getCTChartSpace().copy());
        destChart.getCTChart().set((XmlObject)destChart.getCTChartSpace().getChart());
        return destChart;
    }
    
    protected PackageRelationship addPictureReference(final int pictureIndex) {
        final XSSFWorkbook wb = (XSSFWorkbook)this.getParent().getParent();
        final XSSFPictureData data = wb.getAllPictures().get(pictureIndex);
        final XSSFPictureData pic = new XSSFPictureData(data.getPackagePart());
        final RelationPart rp = this.addRelation(null, XSSFRelation.IMAGES, pic);
        return rp.getRelationship();
    }
    
    public XSSFSimpleShape createSimpleShape(final XSSFClientAnchor anchor) {
        final long shapeId = this.newShapeId();
        final CTTwoCellAnchor ctAnchor = this.createTwoCellAnchor(anchor);
        final CTShape ctShape = ctAnchor.addNewSp();
        ctShape.set((XmlObject)XSSFSimpleShape.prototype());
        ctShape.getNvSpPr().getCNvPr().setId(shapeId);
        ctShape.getSpPr().setXfrm(this.createXfrm(anchor));
        final XSSFSimpleShape shape = new XSSFSimpleShape(this, ctShape);
        shape.anchor = anchor;
        return shape;
    }
    
    public XSSFConnector createConnector(final XSSFClientAnchor anchor) {
        final CTTwoCellAnchor ctAnchor = this.createTwoCellAnchor(anchor);
        final CTConnector ctShape = ctAnchor.addNewCxnSp();
        ctShape.set((XmlObject)XSSFConnector.prototype());
        final XSSFConnector shape = new XSSFConnector(this, ctShape);
        shape.anchor = anchor;
        return shape;
    }
    
    public XSSFShapeGroup createGroup(final XSSFClientAnchor anchor) {
        final CTTwoCellAnchor ctAnchor = this.createTwoCellAnchor(anchor);
        final CTGroupShape ctGroup = ctAnchor.addNewGrpSp();
        ctGroup.set((XmlObject)XSSFShapeGroup.prototype());
        final CTTransform2D xfrm = this.createXfrm(anchor);
        final CTGroupTransform2D grpXfrm = ctGroup.getGrpSpPr().getXfrm();
        grpXfrm.setOff(xfrm.getOff());
        grpXfrm.setExt(xfrm.getExt());
        grpXfrm.setChExt(xfrm.getExt());
        final XSSFShapeGroup shape = new XSSFShapeGroup(this, ctGroup);
        shape.anchor = anchor;
        return shape;
    }
    
    public XSSFComment createCellComment(final ClientAnchor anchor) {
        final XSSFClientAnchor ca = (XSSFClientAnchor)anchor;
        final XSSFSheet sheet = this.getSheet();
        final CommentsTable comments = sheet.getCommentsTable(true);
        final XSSFVMLDrawing vml = sheet.getVMLDrawing(true);
        final com.microsoft.schemas.vml.CTShape vmlShape = vml.newCommentShape();
        if (ca.isSet()) {
            final int dx1Pixels = ca.getDx1() / 9525;
            final int dy1Pixels = ca.getDy1() / 9525;
            final int dx2Pixels = ca.getDx2() / 9525;
            final int dy2Pixels = ca.getDy2() / 9525;
            final String position = ca.getCol1() + ", " + dx1Pixels + ", " + ca.getRow1() + ", " + dy1Pixels + ", " + ca.getCol2() + ", " + dx2Pixels + ", " + ca.getRow2() + ", " + dy2Pixels;
            vmlShape.getClientDataArray(0).setAnchorArray(0, position);
        }
        final CellAddress ref = new CellAddress(ca.getRow1(), (int)ca.getCol1());
        if (comments.findCellComment(ref) != null) {
            throw new IllegalArgumentException("Multiple cell comments in one cell are not allowed, cell: " + ref);
        }
        return new XSSFComment(comments, comments.newComment(ref), vmlShape);
    }
    
    private XSSFGraphicFrame createGraphicFrame(final XSSFClientAnchor anchor) {
        final CTTwoCellAnchor ctAnchor = this.createTwoCellAnchor(anchor);
        final CTGraphicalObjectFrame ctGraphicFrame = ctAnchor.addNewGraphicFrame();
        ctGraphicFrame.set((XmlObject)XSSFGraphicFrame.prototype());
        ctGraphicFrame.setXfrm(this.createXfrm(anchor));
        final long frameId = this.numOfGraphicFrames++;
        final XSSFGraphicFrame graphicFrame = new XSSFGraphicFrame(this, ctGraphicFrame);
        graphicFrame.setAnchor(anchor);
        graphicFrame.setId(frameId);
        graphicFrame.setName("Diagramm" + frameId);
        return graphicFrame;
    }
    
    public XSSFObjectData createObjectData(final ClientAnchor anchor, final int storageId, final int pictureIndex) {
        final XSSFSheet sh = this.getSheet();
        final PackagePart sheetPart = sh.getPackagePart();
        final XSSFSheet sheet = this.getSheet();
        final XSSFWorkbook wb = sheet.getWorkbook();
        final int sheetIndex = wb.getSheetIndex((Sheet)sheet);
        final long shapeId = (sheetIndex + 1L) * 1024L + this.newShapeId();
        PackagePartName olePN;
        try {
            olePN = PackagingURIHelper.createPartName("/xl/embeddings/oleObject" + storageId + ".bin");
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException(e);
        }
        final PackageRelationship olePR = sheetPart.addRelationship(olePN, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject");
        final XSSFPictureData imgPD = sh.getWorkbook().getAllPictures().get(pictureIndex);
        final PackagePartName imgPN = imgPD.getPackagePart().getPartName();
        final PackageRelationship imgSheetPR = sheetPart.addRelationship(imgPN, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image");
        final PackageRelationship imgDrawPR = this.getPackagePart().addRelationship(imgPN, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image");
        final CTWorksheet cwb = sh.getCTWorksheet();
        final CTOleObjects oo = cwb.isSetOleObjects() ? cwb.getOleObjects() : cwb.addNewOleObjects();
        final CTOleObject ole1 = oo.addNewOleObject();
        ole1.setProgId("Package");
        ole1.setShapeId(shapeId);
        ole1.setId(olePR.getId());
        final XmlCursor cur1 = ole1.newCursor();
        cur1.toEndToken();
        cur1.beginElement("objectPr", "http://schemas.openxmlformats.org/spreadsheetml/2006/main");
        cur1.insertAttributeWithValue("id", "http://schemas.openxmlformats.org/officeDocument/2006/relationships", imgSheetPR.getId());
        cur1.insertAttributeWithValue("defaultSize", "0");
        cur1.beginElement("anchor", "http://schemas.openxmlformats.org/spreadsheetml/2006/main");
        cur1.insertAttributeWithValue("moveWithCells", "1");
        final CTTwoCellAnchor ctAnchor = this.createTwoCellAnchor((XSSFClientAnchor)anchor);
        final XmlCursor cur2 = ctAnchor.newCursor();
        cur2.copyXmlContents(cur1);
        cur2.dispose();
        cur1.toParent();
        cur1.toFirstChild();
        cur1.setName(new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "from"));
        cur1.toNextSibling();
        cur1.setName(new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "to"));
        cur1.dispose();
        final CTShape ctShape = ctAnchor.addNewSp();
        ctShape.set((XmlObject)XSSFObjectData.prototype());
        ctShape.getSpPr().setXfrm(this.createXfrm((XSSFClientAnchor)anchor));
        final CTBlipFillProperties blipFill = ctShape.getSpPr().addNewBlipFill();
        blipFill.addNewBlip().setEmbed(imgDrawPR.getId());
        blipFill.addNewStretch().addNewFillRect();
        final CTNonVisualDrawingProps cNvPr = ctShape.getNvSpPr().getCNvPr();
        cNvPr.setId(shapeId);
        cNvPr.setName("Object " + shapeId);
        final XmlCursor extCur = cNvPr.getExtLst().getExtArray(0).newCursor();
        extCur.toFirstChild();
        extCur.setAttributeText(new QName("spid"), "_x0000_s" + shapeId);
        extCur.dispose();
        final XSSFObjectData shape = new XSSFObjectData(this, ctShape);
        shape.anchor = (XSSFClientAnchor)anchor;
        return shape;
    }
    
    public List<XSSFChart> getCharts() {
        final List<XSSFChart> charts = new ArrayList<XSSFChart>();
        for (final POIXMLDocumentPart part : this.getRelations()) {
            if (part instanceof XSSFChart) {
                charts.add((XSSFChart)part);
            }
        }
        return charts;
    }
    
    private CTTwoCellAnchor createTwoCellAnchor(final XSSFClientAnchor anchor) {
        final CTTwoCellAnchor ctAnchor = this.drawing.addNewTwoCellAnchor();
        ctAnchor.setFrom(anchor.getFrom());
        ctAnchor.setTo(anchor.getTo());
        ctAnchor.addNewClientData();
        anchor.setTo(ctAnchor.getTo());
        anchor.setFrom(ctAnchor.getFrom());
        STEditAs.Enum editAs = null;
        switch (anchor.getAnchorType()) {
            case DONT_MOVE_AND_RESIZE: {
                editAs = STEditAs.ABSOLUTE;
                break;
            }
            case MOVE_AND_RESIZE: {
                editAs = STEditAs.TWO_CELL;
                break;
            }
            case MOVE_DONT_RESIZE: {
                editAs = STEditAs.ONE_CELL;
                break;
            }
            default: {
                editAs = STEditAs.ONE_CELL;
                break;
            }
        }
        ctAnchor.setEditAs(editAs);
        return ctAnchor;
    }
    
    private CTTransform2D createXfrm(final XSSFClientAnchor anchor) {
        final CTTransform2D xfrm = CTTransform2D.Factory.newInstance();
        final CTPoint2D off = xfrm.addNewOff();
        off.setX((long)anchor.getDx1());
        off.setY((long)anchor.getDy1());
        final XSSFSheet sheet = this.getSheet();
        double widthPx = 0.0;
        for (int col = anchor.getCol1(); col < anchor.getCol2(); ++col) {
            widthPx += sheet.getColumnWidthInPixels(col);
        }
        double heightPx = 0.0;
        for (int row = anchor.getRow1(); row < anchor.getRow2(); ++row) {
            heightPx += ImageUtils.getRowHeightInPixels((Sheet)sheet, row);
        }
        final long width = Units.pixelToEMU((int)widthPx);
        final long height = Units.pixelToEMU((int)heightPx);
        final CTPositiveSize2D ext = xfrm.addNewExt();
        ext.setCx(width - anchor.getDx1() + anchor.getDx2());
        ext.setCy(height - anchor.getDy1() + anchor.getDy2());
        return xfrm;
    }
    
    private long newShapeId() {
        return 1 + this.drawing.sizeOfAbsoluteAnchorArray() + this.drawing.sizeOfOneCellAnchorArray() + this.drawing.sizeOfTwoCellAnchorArray();
    }
    
    public List<XSSFShape> getShapes() {
        final List<XSSFShape> lst = new ArrayList<XSSFShape>();
        final XmlCursor cur = this.drawing.newCursor();
        try {
            if (cur.toFirstChild()) {
                this.addShapes(cur, lst);
            }
        }
        finally {
            cur.dispose();
        }
        return lst;
    }
    
    public List<XSSFShape> getShapes(final XSSFShapeGroup groupshape) {
        final List<XSSFShape> lst = new ArrayList<XSSFShape>();
        final XmlCursor cur = groupshape.getCTGroupShape().newCursor();
        try {
            this.addShapes(cur, lst);
        }
        finally {
            cur.dispose();
        }
        return lst;
    }
    
    private void addShapes(final XmlCursor cur, final List<XSSFShape> lst) {
        try {
            do {
                cur.push();
                if (cur.toFirstChild()) {
                    do {
                        final XmlObject obj = cur.getObject();
                        if (obj instanceof CTMarker) {
                            continue;
                        }
                        XSSFShape shape;
                        if (obj instanceof CTPicture) {
                            shape = new XSSFPicture(this, (CTPicture)obj);
                        }
                        else if (obj instanceof CTConnector) {
                            shape = new XSSFConnector(this, (CTConnector)obj);
                        }
                        else if (obj instanceof CTShape) {
                            shape = (this.hasOleLink(obj) ? new XSSFObjectData(this, (CTShape)obj) : new XSSFSimpleShape(this, (CTShape)obj));
                        }
                        else if (obj instanceof CTGraphicalObjectFrame) {
                            shape = new XSSFGraphicFrame(this, (CTGraphicalObjectFrame)obj);
                        }
                        else if (obj instanceof CTGroupShape) {
                            shape = new XSSFShapeGroup(this, (CTGroupShape)obj);
                        }
                        else {
                            if (obj instanceof XmlAnyTypeImpl) {
                                XSSFDrawing.LOG.log(5, new Object[] { "trying to parse AlternateContent, this unlinks the returned Shapes from the underlying xml content, so those shapes can't be used to modify the drawing, i.e. modifications will be ignored!" });
                                cur.push();
                                cur.toFirstChild();
                                XmlCursor cur2 = null;
                                try {
                                    final CTDrawing alterWS = CTDrawing.Factory.parse(cur.newXMLStreamReader());
                                    cur2 = alterWS.newCursor();
                                    if (!cur2.toFirstChild()) {
                                        continue;
                                    }
                                    this.addShapes(cur2, lst);
                                }
                                catch (final XmlException e) {
                                    XSSFDrawing.LOG.log(5, new Object[] { "unable to parse CTDrawing in alternate content.", e });
                                }
                                finally {
                                    if (cur2 != null) {
                                        cur2.dispose();
                                    }
                                    cur.pop();
                                }
                                continue;
                            }
                            continue;
                        }
                        assert shape != null;
                        shape.anchor = this.getAnchorFromParent(obj);
                        lst.add(shape);
                    } while (cur.toNextSibling());
                }
                cur.pop();
            } while (cur.toNextSibling());
        }
        finally {
            cur.dispose();
        }
    }
    
    private boolean hasOleLink(final XmlObject shape) {
        final QName uriName = new QName(null, "uri");
        final String xquery = "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' .//a:extLst/a:ext";
        final XmlCursor cur = shape.newCursor();
        cur.selectPath(xquery);
        try {
            while (cur.toNextSelection()) {
                final String uri = cur.getAttributeText(uriName);
                if ("{63B3BB69-23CF-44E3-9099-C40C66FF867C}".equals(uri)) {
                    return true;
                }
            }
        }
        finally {
            cur.dispose();
        }
        return false;
    }
    
    private XSSFAnchor getAnchorFromParent(final XmlObject obj) {
        XSSFAnchor anchor = null;
        XmlObject parentXbean = null;
        final XmlCursor cursor = obj.newCursor();
        if (cursor.toParent()) {
            parentXbean = cursor.getObject();
        }
        cursor.dispose();
        if (parentXbean != null) {
            if (parentXbean instanceof CTTwoCellAnchor) {
                final CTTwoCellAnchor ct = (CTTwoCellAnchor)parentXbean;
                anchor = new XSSFClientAnchor(ct.getFrom(), ct.getTo());
            }
            else if (parentXbean instanceof CTOneCellAnchor) {
                final CTOneCellAnchor ct2 = (CTOneCellAnchor)parentXbean;
                anchor = new XSSFClientAnchor(this.getSheet(), ct2.getFrom(), ct2.getExt());
            }
            else if (parentXbean instanceof CTAbsoluteAnchor) {
                final CTAbsoluteAnchor ct3 = (CTAbsoluteAnchor)parentXbean;
                anchor = new XSSFClientAnchor(this.getSheet(), ct3.getPos(), ct3.getExt());
            }
        }
        return anchor;
    }
    
    public Iterator<XSSFShape> iterator() {
        return this.getShapes().iterator();
    }
    
    public XSSFSheet getSheet() {
        return (XSSFSheet)this.getParent();
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSSFDrawing.class);
    }
}

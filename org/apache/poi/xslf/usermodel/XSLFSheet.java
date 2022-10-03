package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.usermodel.PlaceholderDetails;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.AutoShape;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.sl.usermodel.ObjectShape;
import org.openxmlformats.schemas.drawingml.x2006.main.STColorSchemeIndex;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.DrawFactory;
import java.awt.Graphics2D;
import java.util.HashMap;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.apache.poi.sl.usermodel.Placeholder;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.util.function.Consumer;
import java.awt.Dimension;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObject;
import org.apache.poi.util.Units;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.draw.DrawPictureShape;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.sl.usermodel.PictureData;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import java.util.Collection;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import com.zaxxer.sparsebits.SparseBitSet;
import java.util.Map;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import java.util.List;
import org.apache.poi.util.POILogger;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public abstract class XSLFSheet extends POIXMLDocumentPart implements XSLFShapeContainer, Sheet<XSLFShape, XSLFTextParagraph>
{
    private static POILogger LOG;
    private XSLFDrawing _drawing;
    private List<XSLFShape> _shapes;
    private CTGroupShape _spTree;
    private XSLFTheme _theme;
    private List<XSLFTextShape> _placeholders;
    private Map<Integer, XSLFSimpleShape> _placeholderByIdMap;
    private Map<Integer, XSLFSimpleShape> _placeholderByTypeMap;
    private final SparseBitSet shapeIds;
    
    protected XSLFSheet() {
        this.shapeIds = new SparseBitSet();
    }
    
    protected XSLFSheet(final PackagePart part) {
        super(part);
        this.shapeIds = new SparseBitSet();
    }
    
    public XMLSlideShow getSlideShow() {
        for (POIXMLDocumentPart p = this.getParent(); p != null; p = p.getParent()) {
            if (p instanceof XMLSlideShow) {
                return (XMLSlideShow)p;
            }
        }
        throw new IllegalStateException("SlideShow was not found");
    }
    
    protected int allocateShapeId() {
        final int nextId = this.shapeIds.nextClearBit(1);
        this.shapeIds.set(nextId);
        return nextId;
    }
    
    protected void registerShapeId(final int shapeId) {
        if (this.shapeIds.get(shapeId)) {
            XSLFSheet.LOG.log(5, new Object[] { "shape id " + shapeId + " has been already used." });
        }
        this.shapeIds.set(shapeId);
    }
    
    protected void deregisterShapeId(final int shapeId) {
        if (!this.shapeIds.get(shapeId)) {
            XSLFSheet.LOG.log(5, new Object[] { "shape id " + shapeId + " hasn't been registered." });
        }
        this.shapeIds.clear(shapeId);
    }
    
    protected static List<XSLFShape> buildShapes(final CTGroupShape spTree, final XSLFShapeContainer parent) {
        final XSLFSheet sheet = (XSLFSheet)((parent instanceof XSLFSheet) ? parent : ((XSLFShape)parent).getSheet());
        final List<XSLFShape> shapes = new ArrayList<XSLFShape>();
        final XmlCursor cur = spTree.newCursor();
        try {
            for (boolean b = cur.toFirstChild(); b; b = cur.toNextSibling()) {
                final XmlObject ch = cur.getObject();
                if (ch instanceof CTShape) {
                    final XSLFAutoShape shape = XSLFAutoShape.create((CTShape)ch, sheet);
                    shapes.add(shape);
                }
                else if (ch instanceof CTGroupShape) {
                    shapes.add(new XSLFGroupShape((CTGroupShape)ch, sheet));
                }
                else if (ch instanceof CTConnector) {
                    shapes.add(new XSLFConnectorShape((CTConnector)ch, sheet));
                }
                else if (ch instanceof CTPicture) {
                    shapes.add(new XSLFPictureShape((CTPicture)ch, sheet));
                }
                else if (ch instanceof CTGraphicalObjectFrame) {
                    final XSLFGraphicFrame shape2 = XSLFGraphicFrame.create((CTGraphicalObjectFrame)ch, sheet);
                    shapes.add(shape2);
                }
                else if (ch instanceof XmlAnyTypeImpl) {
                    cur.push();
                    if (cur.toChild("http://schemas.openxmlformats.org/markup-compatibility/2006", "Choice") && cur.toFirstChild()) {
                        try {
                            final CTGroupShape grp = CTGroupShape.Factory.parse(cur.newXMLStreamReader());
                            shapes.addAll(buildShapes(grp, parent));
                        }
                        catch (final XmlException e) {
                            XSLFSheet.LOG.log(1, new Object[] { "unparsable alternate content", e });
                        }
                    }
                    cur.pop();
                }
            }
        }
        finally {
            cur.dispose();
        }
        for (final XSLFShape s : shapes) {
            s.setParent(parent);
        }
        return shapes;
    }
    
    public abstract XmlObject getXmlObject();
    
    private XSLFDrawing getDrawing() {
        this.initDrawingAndShapes();
        return this._drawing;
    }
    
    public List<XSLFShape> getShapes() {
        this.initDrawingAndShapes();
        return this._shapes;
    }
    
    private void initDrawingAndShapes() {
        final CTGroupShape cgs = this.getSpTree();
        if (this._drawing == null) {
            this._drawing = new XSLFDrawing(this, cgs);
        }
        if (this._shapes == null) {
            this._shapes = buildShapes(cgs, this);
        }
    }
    
    @Override
    public XSLFAutoShape createAutoShape() {
        final XSLFAutoShape sh = this.getDrawing().createAutoShape();
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFFreeformShape createFreeform() {
        final XSLFFreeformShape sh = this.getDrawing().createFreeform();
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFTextBox createTextBox() {
        final XSLFTextBox sh = this.getDrawing().createTextBox();
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFConnectorShape createConnector() {
        final XSLFConnectorShape sh = this.getDrawing().createConnector();
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFGroupShape createGroup() {
        final XSLFGroupShape sh = this.getDrawing().createGroup();
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFPictureShape createPicture(final PictureData pictureData) {
        if (!(pictureData instanceof XSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type XSLFPictureData");
        }
        final RelationPart rp = this.addRelation(null, XSLFRelation.IMAGES, (POIXMLDocumentPart)pictureData);
        final XSLFPictureShape sh = this.getDrawing().createPicture(rp.getRelationship().getId());
        new DrawPictureShape((PictureShape)sh).resize();
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    public XSLFTable createTable() {
        final XSLFTable sh = this.getDrawing().createTable();
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    public XSLFTable createTable(final int numRows, final int numCols) {
        if (numRows < 1 || numCols < 1) {
            throw new IllegalArgumentException("numRows and numCols must be greater than 0");
        }
        final XSLFTable sh = this.getDrawing().createTable();
        this.getShapes().add(sh);
        sh.setParent(this);
        for (int r = 0; r < numRows; ++r) {
            final XSLFTableRow row = sh.addRow();
            for (int c = 0; c < numCols; ++c) {
                row.addCell();
            }
        }
        return sh;
    }
    
    public XSLFObjectShape createOleShape(final PictureData pictureData) {
        if (!(pictureData instanceof XSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type XSLFPictureData");
        }
        final RelationPart rp = this.addRelation(null, XSLFRelation.IMAGES, (POIXMLDocumentPart)pictureData);
        final XSLFObjectShape sh = this.getDrawing().createOleShape(rp.getRelationship().getId());
        final CTOleObject oleObj = sh.getCTOleObject();
        final Dimension dim = pictureData.getImageDimension();
        oleObj.setImgW(Units.toEMU(dim.getWidth()));
        oleObj.setImgH(Units.toEMU(dim.getHeight()));
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    public Iterator<XSLFShape> iterator() {
        return this.getShapes().iterator();
    }
    
    public void addShape(final XSLFShape shape) {
        throw new UnsupportedOperationException("Adding a shape from a different container is not supported - create it from scratch witht XSLFSheet.create* methods");
    }
    
    public boolean removeShape(final XSLFShape xShape) {
        final XmlObject obj = xShape.getXmlObject();
        final CTGroupShape spTree = this.getSpTree();
        this.deregisterShapeId(xShape.getShapeId());
        if (obj instanceof CTShape) {
            spTree.getSpList().remove(obj);
        }
        else if (obj instanceof CTGroupShape) {
            final XSLFGroupShape gs = (XSLFGroupShape)xShape;
            new ArrayList(gs.getShapes()).forEach((Consumer)gs::removeShape);
            spTree.getGrpSpList().remove(obj);
        }
        else if (obj instanceof CTConnector) {
            spTree.getCxnSpList().remove(obj);
        }
        else if (obj instanceof CTGraphicalObjectFrame) {
            spTree.getGraphicFrameList().remove(obj);
        }
        else {
            if (!(obj instanceof CTPicture)) {
                throw new IllegalArgumentException("Unsupported shape: " + xShape);
            }
            final XSLFPictureShape ps = (XSLFPictureShape)xShape;
            this.removePictureRelation(ps);
            spTree.getPicList().remove(obj);
        }
        return this.getShapes().remove(xShape);
    }
    
    @Override
    public void clear() {
        final List<XSLFShape> shapes = new ArrayList<XSLFShape>(this.getShapes());
        for (final XSLFShape shape : shapes) {
            this.removeShape(shape);
        }
    }
    
    protected abstract String getRootElementName();
    
    protected CTGroupShape getSpTree() {
        if (this._spTree == null) {
            final XmlObject root = this.getXmlObject();
            final XmlObject[] sp = root.selectPath("declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:spTree");
            if (sp.length == 0) {
                throw new IllegalStateException("CTGroupShape was not found");
            }
            this._spTree = (CTGroupShape)sp[0];
        }
        return this._spTree;
    }
    
    @Override
    protected final void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        final String docName = this.getRootElementName();
        if (docName != null) {
            xmlOptions.setSaveSyntheticDocumentElement(new QName("http://schemas.openxmlformats.org/presentationml/2006/main", docName));
        }
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.getXmlObject().save(out, xmlOptions);
        out.close();
    }
    
    public XSLFSheet importContent(final XSLFSheet src) {
        this._spTree = null;
        this.getSpTree().set(src.getSpTree().copy());
        this.wipeAndReinitialize(src, 0);
        return this;
    }
    
    private void wipeAndReinitialize(final XSLFSheet src, final int offset) {
        this._shapes = null;
        this._drawing = null;
        this.initDrawingAndShapes();
        this._placeholders = null;
        final List<XSLFShape> tgtShapes = this.getShapes();
        final List<XSLFShape> srcShapes = src.getShapes();
        for (int i = 0; i < srcShapes.size(); ++i) {
            final XSLFShape s1 = srcShapes.get(i);
            final XSLFShape s2 = tgtShapes.get(offset + i);
            s2.copy(s1);
        }
    }
    
    public XSLFSheet appendContent(final XSLFSheet src) {
        final int numShapes = this.getShapes().size();
        final CTGroupShape spTree = this.getSpTree();
        final CTGroupShape srcTree = src.getSpTree();
        for (final XmlObject ch : srcTree.selectPath("*")) {
            if (ch instanceof CTShape) {
                spTree.addNewSp().set(ch.copy());
            }
            else if (ch instanceof CTGroupShape) {
                spTree.addNewGrpSp().set(ch.copy());
            }
            else if (ch instanceof CTConnector) {
                spTree.addNewCxnSp().set(ch.copy());
            }
            else if (ch instanceof CTPicture) {
                spTree.addNewPic().set(ch.copy());
            }
            else if (ch instanceof CTGraphicalObjectFrame) {
                spTree.addNewGraphicFrame().set(ch.copy());
            }
        }
        this.wipeAndReinitialize(src, numShapes);
        return this;
    }
    
    public XSLFTheme getTheme() {
        if (this._theme != null || !this.isSupportTheme()) {
            return this._theme;
        }
        this.getRelations().stream().filter(p -> p instanceof XSLFTheme).findAny().ifPresent(p -> this._theme = (XSLFTheme)p);
        return this._theme;
    }
    
    boolean isSupportTheme() {
        return false;
    }
    
    String mapSchemeColor(final String schemeColor) {
        return null;
    }
    
    protected XSLFTextShape getTextShapeByType(final Placeholder type) {
        for (final XSLFShape shape : this.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                final XSLFTextShape txt = (XSLFTextShape)shape;
                if (txt.getTextType() == type) {
                    return txt;
                }
                continue;
            }
        }
        return null;
    }
    
    public XSLFSimpleShape getPlaceholder(final Placeholder ph) {
        return this.getPlaceholderByType(ph.ooxmlId);
    }
    
    XSLFSimpleShape getPlaceholder(final CTPlaceholder ph) {
        XSLFSimpleShape shape = null;
        if (ph.isSetIdx()) {
            shape = this.getPlaceholderById((int)ph.getIdx());
        }
        if (shape == null && ph.isSetType()) {
            shape = this.getPlaceholderByType(ph.getType().intValue());
        }
        return shape;
    }
    
    private void initPlaceholders() {
        if (this._placeholders == null) {
            this._placeholders = new ArrayList<XSLFTextShape>();
            this._placeholderByIdMap = new HashMap<Integer, XSLFSimpleShape>();
            this._placeholderByTypeMap = new HashMap<Integer, XSLFSimpleShape>();
            for (final XSLFShape sh : this.getShapes()) {
                if (sh instanceof XSLFTextShape) {
                    final XSLFTextShape sShape = (XSLFTextShape)sh;
                    final CTPlaceholder ph = sShape.getPlaceholderDetails().getCTPlaceholder(false);
                    if (ph == null) {
                        continue;
                    }
                    this._placeholders.add(sShape);
                    if (ph.isSetIdx()) {
                        final int idx = (int)ph.getIdx();
                        this._placeholderByIdMap.put(idx, sShape);
                    }
                    if (!ph.isSetType()) {
                        continue;
                    }
                    this._placeholderByTypeMap.put(ph.getType().intValue(), sShape);
                }
            }
        }
    }
    
    private XSLFSimpleShape getPlaceholderById(final int id) {
        this.initPlaceholders();
        return this._placeholderByIdMap.get(id);
    }
    
    XSLFSimpleShape getPlaceholderByType(final int ordinal) {
        this.initPlaceholders();
        return this._placeholderByTypeMap.get(ordinal);
    }
    
    public XSLFTextShape getPlaceholder(final int idx) {
        this.initPlaceholders();
        return this._placeholders.get(idx);
    }
    
    public XSLFTextShape[] getPlaceholders() {
        this.initPlaceholders();
        return this._placeholders.toArray(new XSLFTextShape[0]);
    }
    
    public boolean getFollowMasterGraphics() {
        return false;
    }
    
    public XSLFBackground getBackground() {
        return null;
    }
    
    public void draw(final Graphics2D graphics) {
        final DrawFactory drawFact = DrawFactory.getInstance(graphics);
        final Drawable draw = (Drawable)drawFact.getDrawable((Sheet)this);
        draw.draw(graphics);
    }
    
    String importBlip(final String blipId, final POIXMLDocumentPart parent) {
        final XSLFPictureData parData = parent.getRelationPartById(blipId).getDocumentPart();
        XSLFPictureData pictureData;
        if (this.getPackagePart().getPackage() == parent.getPackagePart().getPackage()) {
            pictureData = parData;
        }
        else {
            final XMLSlideShow ppt = this.getSlideShow();
            pictureData = ppt.addPicture(parData.getData(), parData.getType());
        }
        final RelationPart rp = this.addRelation(blipId, XSLFRelation.IMAGES, pictureData);
        return rp.getRelationship().getId();
    }
    
    void importPart(final PackageRelationship srcRel, final PackagePart srcPafrt) {
        final PackagePart destPP = this.getPackagePart();
        final PackagePartName srcPPName = srcPafrt.getPartName();
        final OPCPackage pkg = destPP.getPackage();
        if (pkg.containPart(srcPPName)) {
            return;
        }
        destPP.addRelationship(srcPPName, TargetMode.INTERNAL, srcRel.getRelationshipType());
        final PackagePart part = pkg.createPart(srcPPName, srcPafrt.getContentType());
        try {
            final OutputStream out = part.getOutputStream();
            final InputStream is = srcPafrt.getInputStream();
            IOUtils.copy(is, out);
            is.close();
            out.close();
        }
        catch (final IOException e) {
            throw new POIXMLException(e);
        }
    }
    
    void removePictureRelation(final XSLFPictureShape pictureShape) {
        int numberOfRelations = 0;
        final String targetBlipId = pictureShape.getBlipId();
        for (final XSLFShape shape : pictureShape.getSheet().getShapes()) {
            if (shape instanceof XSLFPictureShape) {
                final XSLFPictureShape currentPictureShape = (XSLFPictureShape)shape;
                final String currentBlipId = currentPictureShape.getBlipId();
                if (currentBlipId == null || !currentBlipId.equals(targetBlipId)) {
                    continue;
                }
                ++numberOfRelations;
            }
        }
        if (numberOfRelations <= 1) {
            this.removeRelation(pictureShape.getBlipId());
        }
    }
    
    public XSLFPlaceholderDetails getPlaceholderDetails(final Placeholder placeholder) {
        final XSLFSimpleShape ph = this.getPlaceholder(placeholder);
        return (ph == null) ? null : new XSLFPlaceholderDetails(ph);
    }
    
    public void addChart(final XSLFChart chart) {
        final Rectangle2D rect2D = new Rectangle(10, 10, 500000, 500000);
        this.addChart(chart, rect2D);
    }
    
    public void addChart(final XSLFChart chart, final Rectangle2D rect2D) {
        final RelationPart rp = this.addRelation(null, XSLFRelation.CHART, chart);
        this.getDrawing().addChart(rp.getRelationship().getId(), rect2D);
    }
    
    protected String mapSchemeColor(final CTColorMappingOverride cmapOver, final String schemeColor) {
        final String slideColor = this.mapSchemeColor((cmapOver == null) ? null : cmapOver.getOverrideClrMapping(), schemeColor);
        if (slideColor != null) {
            return slideColor;
        }
        final XSLFSheet master = (XSLFSheet)this.getMasterSheet();
        final String masterColor = (master == null) ? null : master.mapSchemeColor(schemeColor);
        return (masterColor == null) ? schemeColor : masterColor;
    }
    
    protected String mapSchemeColor(final CTColorMapping cmap, final String schemeColor) {
        STColorSchemeIndex.Enum schemeMap = null;
        if (cmap != null && schemeColor != null) {
            switch (schemeColor) {
                case "accent1": {
                    schemeMap = cmap.getAccent1();
                    break;
                }
                case "accent2": {
                    schemeMap = cmap.getAccent2();
                    break;
                }
                case "accent3": {
                    schemeMap = cmap.getAccent3();
                    break;
                }
                case "accent4": {
                    schemeMap = cmap.getAccent4();
                    break;
                }
                case "accent5": {
                    schemeMap = cmap.getAccent5();
                    break;
                }
                case "accent6": {
                    schemeMap = cmap.getAccent6();
                    break;
                }
                case "bg1": {
                    schemeMap = cmap.getBg1();
                    break;
                }
                case "bg2": {
                    schemeMap = cmap.getBg2();
                    break;
                }
                case "folHlink": {
                    schemeMap = cmap.getFolHlink();
                    break;
                }
                case "hlink": {
                    schemeMap = cmap.getHlink();
                    break;
                }
                case "tx1": {
                    schemeMap = cmap.getTx1();
                    break;
                }
                case "tx2": {
                    schemeMap = cmap.getTx2();
                    break;
                }
            }
        }
        return (schemeMap == null) ? null : schemeMap.toString();
    }
    
    static {
        XSLFSheet.LOG = POILogFactory.getLogger((Class)XSLFSheet.class);
    }
}

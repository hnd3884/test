package org.apache.poi.xslf.usermodel;

import javax.xml.stream.XMLStreamReader;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.Sheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.poi.sl.draw.DrawFactory;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.poi.sl.draw.DrawPaint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import com.microsoft.schemas.compatibility.AlternateContentDocument;
import java.util.Locale;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.util.Internal;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.apache.xmlbeans.XmlObject;
import javax.xml.namespace.QName;
import org.apache.poi.sl.usermodel.Shape;

public abstract class XSLFShape implements Shape<XSLFShape, XSLFTextParagraph>
{
    static final String DML_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    static final String PML_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    private static final String MC_NS = "http://schemas.openxmlformats.org/markup-compatibility/2006";
    private static final String MAC_DML_NS = "http://schemas.microsoft.com/office/mac/drawingml/2008/main";
    private static final QName ALTERNATE_CONTENT_TAG;
    private static final QName[] NV_CONTAINER;
    private static final QName[] CNV_PROPS;
    private static final String OSGI_ERROR = "Schemas (*.xsb) for <CLASS> can't be loaded - usually this happens when OSGI loading is used and the thread context classloader has no reference to the xmlbeans classes - please either verify if the <XSB>.xsb is on the classpath or alternatively try to use the full ooxml-schemas-x.x.jar";
    private final XmlObject _shape;
    private final XSLFSheet _sheet;
    private XSLFShapeContainer _parent;
    private CTShapeStyle _spStyle;
    private CTNonVisualDrawingProps _nvPr;
    
    protected XSLFShape(final XmlObject shape, final XSLFSheet sheet) {
        this._shape = shape;
        this._sheet = sheet;
    }
    
    public final XmlObject getXmlObject() {
        return this._shape;
    }
    
    public XSLFSheet getSheet() {
        return this._sheet;
    }
    
    public String getShapeName() {
        final CTNonVisualDrawingProps nonVisualDrawingProps = this.getCNvPr();
        return (nonVisualDrawingProps == null) ? null : nonVisualDrawingProps.getName();
    }
    
    public int getShapeId() {
        final CTNonVisualDrawingProps nonVisualDrawingProps = this.getCNvPr();
        if (nonVisualDrawingProps == null) {
            throw new IllegalStateException("no underlying shape exists");
        }
        return Math.toIntExact(nonVisualDrawingProps.getId());
    }
    
    @Internal
    void copy(final XSLFShape sh) {
        if (!this.getClass().isInstance(sh)) {
            throw new IllegalArgumentException("Can't copy " + sh.getClass().getSimpleName() + " into " + this.getClass().getSimpleName());
        }
        if (this instanceof PlaceableShape) {
            final PlaceableShape<?, ?> ps = (PlaceableShape<?, ?>)this;
            ps.setAnchor(sh.getAnchor());
        }
    }
    
    public void setParent(final XSLFShapeContainer parent) {
        this._parent = parent;
    }
    
    public XSLFShapeContainer getParent() {
        return this._parent;
    }
    
    protected PaintStyle getFillPaint() {
        final XSLFTheme theme = this.getSheet().getTheme();
        final boolean hasPlaceholder = this.getPlaceholder() != null;
        final PropertyFetcher<PaintStyle> fetcher = new PropertyFetcher<PaintStyle>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final PackagePart pp = shape.getSheet().getPackagePart();
                if (shape instanceof XSLFPictureShape) {
                    final CTPicture pic = (CTPicture)shape.getXmlObject();
                    if (pic.getBlipFill() != null) {
                        this.setValue(XSLFShape.this.selectPaint(pic.getBlipFill(), pp, null, theme));
                        return true;
                    }
                }
                XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(shape.getShapeProperties());
                if (fp == null) {
                    return false;
                }
                if (fp.isSetNoFill()) {
                    this.setValue(null);
                    return true;
                }
                PaintStyle paint = XSLFShape.this.selectPaint(fp, null, pp, theme, hasPlaceholder);
                if (paint != null) {
                    this.setValue(paint);
                    return true;
                }
                final CTShapeStyle style = shape.getSpStyle();
                if (style != null) {
                    fp = XSLFPropertiesDelegate.getFillDelegate((XmlObject)style.getFillRef());
                    paint = XSLFShape.this.selectPaint(fp, null, pp, theme, hasPlaceholder);
                }
                if (paint != null) {
                    this.setValue(paint);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return fetcher.getValue();
    }
    
    protected CTBackgroundProperties getBgPr() {
        return this.getChild(CTBackgroundProperties.class, "http://schemas.openxmlformats.org/presentationml/2006/main", "bgPr");
    }
    
    protected CTStyleMatrixReference getBgRef() {
        return this.getChild(CTStyleMatrixReference.class, "http://schemas.openxmlformats.org/presentationml/2006/main", "bgRef");
    }
    
    protected CTGroupShapeProperties getGrpSpPr() {
        return this.getChild(CTGroupShapeProperties.class, "http://schemas.openxmlformats.org/presentationml/2006/main", "grpSpPr");
    }
    
    protected CTNonVisualDrawingProps getCNvPr() {
        try {
            if (this._nvPr == null) {
                this._nvPr = this.selectProperty(CTNonVisualDrawingProps.class, null, new QName[][] { XSLFShape.NV_CONTAINER, XSLFShape.CNV_PROPS });
            }
            return this._nvPr;
        }
        catch (final XmlException e) {
            return null;
        }
    }
    
    protected CTShapeStyle getSpStyle() {
        if (this._spStyle == null) {
            this._spStyle = this.getChild(CTShapeStyle.class, "http://schemas.openxmlformats.org/presentationml/2006/main", "style");
        }
        return this._spStyle;
    }
    
    protected <T extends XmlObject> T getChild(final Class<T> childClass, final String namespace, final String nodename) {
        final XmlCursor cur = this.getXmlObject().newCursor();
        T child = null;
        if (cur.toChild(namespace, nodename)) {
            child = (T)cur.getObject();
        }
        if (cur.toChild("http://schemas.openxmlformats.org/drawingml/2006/main", nodename)) {
            child = (T)cur.getObject();
        }
        cur.dispose();
        return child;
    }
    
    public boolean isPlaceholder() {
        return this.getPlaceholderDetails().getCTPlaceholder(false) != null;
    }
    
    public Placeholder getPlaceholder() {
        return this.getPlaceholderDetails().getPlaceholder();
    }
    
    public void setPlaceholder(final Placeholder placeholder) {
        this.getPlaceholderDetails().setPlaceholder(placeholder);
    }
    
    public XSLFPlaceholderDetails getPlaceholderDetails() {
        return new XSLFPlaceholderDetails(this);
    }
    
    protected <T extends XmlObject> T selectProperty(final Class<T> resultClass, final String xquery) {
        final XmlObject[] rs = this.getXmlObject().selectPath(xquery);
        if (rs.length == 0) {
            return null;
        }
        return (T)(resultClass.isInstance(rs[0]) ? rs[0] : null);
    }
    
    @Internal
    public <T extends XmlObject> T selectProperty(final Class<T> resultClass, final ReparseFactory<T> factory, final QName[]... path) throws XmlException {
        XmlObject xo = this.getXmlObject();
        final XmlCursor cur = xo.newCursor();
        XmlCursor innerCur = null;
        try {
            innerCur = this.selectProperty(cur, path, 0, factory != null, false);
            if (innerCur == null) {
                return null;
            }
            xo = innerCur.getObject();
            if (xo instanceof XmlAnyTypeImpl) {
                final String errorTxt = "Schemas (*.xsb) for <CLASS> can't be loaded - usually this happens when OSGI loading is used and the thread context classloader has no reference to the xmlbeans classes - please either verify if the <XSB>.xsb is on the classpath or alternatively try to use the full ooxml-schemas-x.x.jar".replace("<CLASS>", resultClass.getSimpleName()).replace("<XSB>", resultClass.getSimpleName().toLowerCase(Locale.ROOT) + "*");
                if (factory == null) {
                    throw new XmlException(errorTxt);
                }
                xo = factory.parse(innerCur.newXMLStreamReader());
            }
            return (T)xo;
        }
        finally {
            cur.dispose();
            if (innerCur != null) {
                innerCur.dispose();
            }
        }
    }
    
    private XmlCursor selectProperty(final XmlCursor cur, final QName[][] path, final int offset, final boolean reparseAlternate, final boolean isAlternate) throws XmlException {
        for (final QName qn : path[offset]) {
            if (cur.toChild(qn)) {
                if (offset == path.length - 1) {
                    return cur;
                }
                cur.push();
                final XmlCursor innerCur = this.selectProperty(cur, path, offset + 1, reparseAlternate, false);
                if (innerCur != null) {
                    return innerCur;
                }
                cur.pop();
            }
        }
        if (isAlternate || !cur.toChild(XSLFShape.ALTERNATE_CONTENT_TAG)) {
            return null;
        }
        final XmlObject xo = cur.getObject();
        AlternateContentDocument.AlternateContent alterCont;
        if (xo instanceof AlternateContentDocument.AlternateContent) {
            alterCont = (AlternateContentDocument.AlternateContent)xo;
        }
        else {
            if (!reparseAlternate) {
                throw new XmlException("Schemas (*.xsb) for <CLASS> can't be loaded - usually this happens when OSGI loading is used and the thread context classloader has no reference to the xmlbeans classes - please either verify if the <XSB>.xsb is on the classpath or alternatively try to use the full ooxml-schemas-x.x.jar".replace("<CLASS>", "AlternateContent").replace("<XSB>", "alternatecontentelement"));
            }
            try {
                final AlternateContentDocument acd = AlternateContentDocument.Factory.parse(cur.newXMLStreamReader());
                alterCont = acd.getAlternateContent();
            }
            catch (final XmlException e) {
                throw new XmlException("unable to parse AlternateContent element", (Throwable)e);
            }
        }
        for (int choices = alterCont.sizeOfChoiceArray(), i = 0; i < choices; ++i) {
            final AlternateContentDocument.AlternateContent.Choice choice = alterCont.getChoiceArray(i);
            final XmlCursor cCur = choice.newCursor();
            XmlCursor innerCur2 = null;
            try {
                final String requiresNS = cCur.namespaceForPrefix(choice.getRequires());
                if (!"http://schemas.microsoft.com/office/mac/drawingml/2008/main".equalsIgnoreCase(requiresNS)) {
                    innerCur2 = this.selectProperty(cCur, path, offset, reparseAlternate, true);
                    if (innerCur2 != null) {
                        return innerCur2;
                    }
                }
            }
            finally {
                if (innerCur2 != cCur) {
                    cCur.dispose();
                }
            }
        }
        if (!alterCont.isSetFallback()) {
            return null;
        }
        final XmlCursor fCur = alterCont.getFallback().newCursor();
        XmlCursor innerCur = null;
        try {
            innerCur = this.selectProperty(fCur, path, offset, reparseAlternate, true);
            return innerCur;
        }
        finally {
            if (innerCur != fCur) {
                fCur.dispose();
            }
        }
    }
    
    protected boolean fetchShapeProperty(final PropertyFetcher<?> visitor) {
        if (visitor.fetch(this)) {
            return true;
        }
        final CTPlaceholder ph = this.getPlaceholderDetails().getCTPlaceholder(false);
        if (ph == null) {
            return false;
        }
        MasterSheet<XSLFShape, XSLFTextParagraph> sm = (MasterSheet<XSLFShape, XSLFTextParagraph>)this.getSheet().getMasterSheet();
        if (sm instanceof XSLFSlideLayout) {
            final XSLFSlideLayout slideLayout = (XSLFSlideLayout)sm;
            final XSLFSimpleShape placeholderShape = slideLayout.getPlaceholder(ph);
            if (placeholderShape != null && visitor.fetch(placeholderShape)) {
                return true;
            }
            sm = (MasterSheet<XSLFShape, XSLFTextParagraph>)slideLayout.getMasterSheet();
        }
        if (sm instanceof XSLFSlideMaster) {
            final XSLFSlideMaster master = (XSLFSlideMaster)sm;
            final int textType = getPlaceholderType(ph);
            final XSLFSimpleShape masterShape = master.getPlaceholderByType(textType);
            return masterShape != null && visitor.fetch(masterShape);
        }
        return false;
    }
    
    private static int getPlaceholderType(final CTPlaceholder ph) {
        if (!ph.isSetType()) {
            return 2;
        }
        switch (ph.getType().intValue()) {
            case 1:
            case 3: {
                return 1;
            }
            case 5:
            case 6:
            case 7: {
                return ph.getType().intValue();
            }
            default: {
                return 2;
            }
        }
    }
    
    protected PaintStyle selectPaint(final XSLFPropertiesDelegate.XSLFFillProperties fp, final CTSchemeColor phClr, final PackagePart parentPart, final XSLFTheme theme, final boolean hasPlaceholder) {
        if (fp == null || fp.isSetNoFill()) {
            return null;
        }
        if (fp.isSetSolidFill()) {
            return this.selectPaint(fp.getSolidFill(), phClr, theme);
        }
        if (fp.isSetBlipFill()) {
            return this.selectPaint(fp.getBlipFill(), parentPart, phClr, theme);
        }
        if (fp.isSetGradFill()) {
            return this.selectPaint(fp.getGradFill(), phClr, theme);
        }
        if (fp.isSetMatrixStyle()) {
            return this.selectPaint(fp.getMatrixStyle(), theme, fp.isLineStyle(), hasPlaceholder);
        }
        return null;
    }
    
    protected PaintStyle selectPaint(final CTSolidColorFillProperties solidFill, CTSchemeColor phClr, final XSLFTheme theme) {
        if (solidFill.isSetSchemeClr() && phClr == null) {
            phClr = solidFill.getSchemeClr();
        }
        final XSLFColor c = new XSLFColor((XmlObject)solidFill, theme, phClr, this._sheet);
        return (PaintStyle)DrawPaint.createSolidPaint(c.getColorStyle());
    }
    
    protected PaintStyle selectPaint(final CTBlipFillProperties blipFill, final PackagePart parentPart, final CTSchemeColor phClr, final XSLFTheme theme) {
        return (PaintStyle)new XSLFTexturePaint(blipFill, parentPart, phClr, theme, this._sheet);
    }
    
    protected PaintStyle selectPaint(final CTGradientFillProperties gradFill, final CTSchemeColor phClr, final XSLFTheme theme) {
        return (PaintStyle)new XSLFGradientPaint(gradFill, phClr, theme, this._sheet);
    }
    
    protected PaintStyle selectPaint(final CTStyleMatrixReference fillRef, final XSLFTheme theme, final boolean isLineStyle, final boolean hasPlaceholder) {
        if (fillRef == null) {
            return null;
        }
        final long idx = fillRef.getIdx();
        final CTStyleMatrix matrix = theme.getXmlObject().getThemeElements().getFmtScheme();
        long childIdx;
        XmlObject styleLst;
        if (idx >= 1L && idx <= 999L) {
            childIdx = idx - 1L;
            styleLst = (XmlObject)(isLineStyle ? matrix.getLnStyleLst() : matrix.getFillStyleLst());
        }
        else {
            if (idx < 1001L) {
                return null;
            }
            childIdx = idx - 1001L;
            styleLst = (XmlObject)matrix.getBgFillStyleLst();
        }
        final XmlCursor cur = styleLst.newCursor();
        XSLFPropertiesDelegate.XSLFFillProperties fp = null;
        if (cur.toChild(Math.toIntExact(childIdx))) {
            fp = XSLFPropertiesDelegate.getFillDelegate(cur.getObject());
        }
        cur.dispose();
        final CTSchemeColor phClr = fillRef.getSchemeClr();
        final PaintStyle res = this.selectPaint(fp, phClr, theme.getPackagePart(), theme, hasPlaceholder);
        if (res != null || hasPlaceholder) {
            return res;
        }
        final XSLFColor col = new XSLFColor((XmlObject)fillRef, theme, phClr, this._sheet);
        return (PaintStyle)DrawPaint.createSolidPaint(col.getColorStyle());
    }
    
    public void draw(final Graphics2D graphics, final Rectangle2D bounds) {
        DrawFactory.getInstance(graphics).drawShape(graphics, (Shape)this, bounds);
    }
    
    protected XmlObject getShapeProperties() {
        return this.getChild((Class<XmlObject>)CTShapeProperties.class, "http://schemas.openxmlformats.org/presentationml/2006/main", "spPr");
    }
    
    static {
        ALTERNATE_CONTENT_TAG = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "AlternateContent");
        NV_CONTAINER = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvCxnSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGrpSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPicPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGraphicFramePr") };
        CNV_PROPS = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvPr") };
    }
    
    @Internal
    public interface ReparseFactory<T extends XmlObject>
    {
        T parse(final XMLStreamReader p0) throws XmlException;
    }
}

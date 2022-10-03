package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.DrawFactory;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Notes;
import org.apache.poi.util.NotImplemented;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.apache.poi.sl.usermodel.Placeholder;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.openxmlformats.schemas.presentationml.x2006.main.SldDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.apache.poi.sl.usermodel.Slide;

public final class XSLFSlide extends XSLFSheet implements Slide<XSLFShape, XSLFTextParagraph>
{
    private final CTSlide _slide;
    private XSLFSlideLayout _layout;
    private XSLFComments _comments;
    private XSLFCommentAuthors _commentAuthors;
    private XSLFNotes _notes;
    
    XSLFSlide() {
        this._slide = prototype();
    }
    
    XSLFSlide(final PackagePart part) throws IOException, XmlException {
        super(part);
        Document _doc;
        try {
            _doc = DocumentHelper.readDocument(this.getPackagePart().getInputStream());
        }
        catch (final SAXException e) {
            throw new IOException(e);
        }
        final SldDocument doc = SldDocument.Factory.parse((Node)_doc, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._slide = doc.getSld();
    }
    
    private static CTSlide prototype() {
        final CTSlide ctSlide = CTSlide.Factory.newInstance();
        final CTCommonSlideData cSld = ctSlide.addNewCSld();
        final CTGroupShape spTree = cSld.addNewSpTree();
        final CTGroupShapeNonVisual nvGrpSpPr = spTree.addNewNvGrpSpPr();
        final CTNonVisualDrawingProps cnvPr = nvGrpSpPr.addNewCNvPr();
        cnvPr.setId(1L);
        cnvPr.setName("");
        nvGrpSpPr.addNewCNvGrpSpPr();
        nvGrpSpPr.addNewNvPr();
        final CTGroupShapeProperties grpSpr = spTree.addNewGrpSpPr();
        final CTGroupTransform2D xfrm = grpSpr.addNewXfrm();
        final CTPoint2D off = xfrm.addNewOff();
        off.setX(0L);
        off.setY(0L);
        final CTPositiveSize2D ext = xfrm.addNewExt();
        ext.setCx(0L);
        ext.setCy(0L);
        final CTPoint2D choff = xfrm.addNewChOff();
        choff.setX(0L);
        choff.setY(0L);
        final CTPositiveSize2D chExt = xfrm.addNewChExt();
        chExt.setCx(0L);
        chExt.setCy(0L);
        ctSlide.addNewClrMapOvr().addNewMasterClrMapping();
        return ctSlide;
    }
    
    public CTSlide getXmlObject() {
        return this._slide;
    }
    
    @Override
    protected String getRootElementName() {
        return "sld";
    }
    
    protected void removeChartRelation(final XSLFChart chart) {
        this.removeRelation(chart);
    }
    
    protected void removeLayoutRelation(final XSLFSlideLayout layout) {
        this.removeRelation(layout, false);
    }
    
    public XSLFSlideLayout getMasterSheet() {
        return this.getSlideLayout();
    }
    
    public XSLFSlideLayout getSlideLayout() {
        if (this._layout == null) {
            for (final POIXMLDocumentPart p : this.getRelations()) {
                if (p instanceof XSLFSlideLayout) {
                    this._layout = (XSLFSlideLayout)p;
                }
            }
        }
        if (this._layout == null) {
            throw new IllegalArgumentException("SlideLayout was not found for " + this);
        }
        return this._layout;
    }
    
    public XSLFSlideMaster getSlideMaster() {
        return this.getSlideLayout().getSlideMaster();
    }
    
    public XSLFComments getCommentsPart() {
        if (this._comments == null) {
            for (final POIXMLDocumentPart p : this.getRelations()) {
                if (p instanceof XSLFComments) {
                    this._comments = (XSLFComments)p;
                    break;
                }
            }
        }
        return this._comments;
    }
    
    public XSLFCommentAuthors getCommentAuthorsPart() {
        if (this._commentAuthors == null) {
            for (final POIXMLDocumentPart p : this.getRelations()) {
                if (p instanceof XSLFCommentAuthors) {
                    return this._commentAuthors = (XSLFCommentAuthors)p;
                }
            }
            for (final POIXMLDocumentPart p : this.getSlideShow().getRelations()) {
                if (p instanceof XSLFCommentAuthors) {
                    return this._commentAuthors = (XSLFCommentAuthors)p;
                }
            }
        }
        return null;
    }
    
    public List<XSLFComment> getComments() {
        final List<XSLFComment> comments = new ArrayList<XSLFComment>();
        final XSLFComments xComments = this.getCommentsPart();
        final XSLFCommentAuthors xAuthors = this.getCommentAuthorsPart();
        if (xComments != null) {
            for (final CTComment xc : xComments.getCTCommentsList().getCmArray()) {
                comments.add(new XSLFComment(xc, xAuthors));
            }
        }
        return comments;
    }
    
    public XSLFNotes getNotes() {
        if (this._notes == null) {
            for (final POIXMLDocumentPart p : this.getRelations()) {
                if (p instanceof XSLFNotes) {
                    this._notes = (XSLFNotes)p;
                }
            }
        }
        if (this._notes == null) {
            return null;
        }
        return this._notes;
    }
    
    public String getTitle() {
        final XSLFTextShape txt = this.getTextShapeByType(Placeholder.TITLE);
        return (txt == null) ? null : txt.getText();
    }
    
    @Override
    public XSLFTheme getTheme() {
        return this.getSlideLayout().getSlideMaster().getTheme();
    }
    
    @Override
    public XSLFBackground getBackground() {
        final CTBackground bg = this._slide.getCSld().getBg();
        if (bg != null) {
            return new XSLFBackground(bg, this);
        }
        return this.getMasterSheet().getBackground();
    }
    
    @Override
    public boolean getFollowMasterGraphics() {
        return this._slide.getShowMasterSp();
    }
    
    public void setFollowMasterGraphics(final boolean value) {
        this._slide.setShowMasterSp(value);
    }
    
    public boolean getFollowMasterObjects() {
        return this.getFollowMasterGraphics();
    }
    
    public void setFollowMasterObjects(final boolean follow) {
        this.setFollowMasterGraphics(follow);
    }
    
    @Override
    public XSLFSlide importContent(final XSLFSheet src) {
        super.importContent(src);
        if (!(src instanceof XSLFSlide)) {
            return this;
        }
        final CTBackground bgOther = ((XSLFSlide)src)._slide.getCSld().getBg();
        if (bgOther == null) {
            return this;
        }
        CTBackground bgThis = this._slide.getCSld().getBg();
        if (bgThis != null) {
            if (bgThis.isSetBgPr() && bgThis.getBgPr().isSetBlipFill()) {
                final String oldId = bgThis.getBgPr().getBlipFill().getBlip().getEmbed();
                this.removeRelation(oldId);
            }
            this._slide.getCSld().unsetBg();
        }
        bgThis = (CTBackground)this._slide.getCSld().addNewBg().set((XmlObject)bgOther);
        if (bgOther.isSetBgPr() && bgOther.getBgPr().isSetBlipFill()) {
            final String idOther = bgOther.getBgPr().getBlipFill().getBlip().getEmbed();
            final String idThis = this.importBlip(idOther, src);
            bgThis.getBgPr().getBlipFill().getBlip().setEmbed(idThis);
        }
        return this;
    }
    
    public boolean getFollowMasterBackground() {
        return false;
    }
    
    @NotImplemented
    public void setFollowMasterBackground(final boolean follow) {
        throw new UnsupportedOperationException();
    }
    
    public boolean getFollowMasterColourScheme() {
        return false;
    }
    
    @NotImplemented
    public void setFollowMasterColourScheme(final boolean follow) {
        throw new UnsupportedOperationException();
    }
    
    @NotImplemented
    public void setNotes(final Notes<XSLFShape, XSLFTextParagraph> notes) {
        assert notes instanceof XSLFNotes;
    }
    
    public int getSlideNumber() {
        final int idx = this.getSlideShow().getSlides().indexOf(this);
        return (idx == -1) ? idx : (idx + 1);
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        final DrawFactory drawFact = DrawFactory.getInstance(graphics);
        final Drawable draw = (Drawable)drawFact.getDrawable((Slide)this);
        draw.draw(graphics);
    }
    
    public boolean getDisplayPlaceholder(final Placeholder placeholder) {
        return false;
    }
    
    public void setHidden(final boolean hidden) {
        final CTSlide sld = this.getXmlObject();
        if (hidden) {
            sld.setShow(false);
        }
        else if (sld.isSetShow()) {
            sld.unsetShow();
        }
    }
    
    public boolean isHidden() {
        final CTSlide sld = this.getXmlObject();
        return sld.isSetShow() && !sld.getShow();
    }
    
    public String getSlideName() {
        final CTCommonSlideData cSld = this.getXmlObject().getCSld();
        return cSld.isSetName() ? cSld.getName() : ("Slide" + this.getSlideNumber());
    }
    
    @Override
    String mapSchemeColor(final String schemeColor) {
        return this.mapSchemeColor(this._slide.getClrMapOvr(), schemeColor);
    }
}

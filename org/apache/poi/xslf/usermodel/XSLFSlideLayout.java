package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.Background;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.sl.usermodel.Placeholder;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import java.util.Iterator;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.openxmlformats.schemas.presentationml.x2006.main.SldLayoutDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayout;
import org.apache.poi.sl.usermodel.MasterSheet;

public class XSLFSlideLayout extends XSLFSheet implements MasterSheet<XSLFShape, XSLFTextParagraph>
{
    private final CTSlideLayout _layout;
    private XSLFSlideMaster _master;
    
    public XSLFSlideLayout(final PackagePart part) throws IOException, XmlException {
        super(part);
        final SldLayoutDocument doc = SldLayoutDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._layout = doc.getSldLayout();
    }
    
    public String getName() {
        return this._layout.getCSld().getName();
    }
    
    @Internal
    public CTSlideLayout getXmlObject() {
        return this._layout;
    }
    
    @Override
    protected String getRootElementName() {
        return "sldLayout";
    }
    
    public XSLFSlideMaster getSlideMaster() {
        if (this._master == null) {
            for (final POIXMLDocumentPart p : this.getRelations()) {
                if (p instanceof XSLFSlideMaster) {
                    this._master = (XSLFSlideMaster)p;
                }
            }
        }
        if (this._master == null) {
            throw new IllegalStateException("SlideMaster was not found for " + this);
        }
        return this._master;
    }
    
    public XSLFSlideMaster getMasterSheet() {
        return this.getSlideMaster();
    }
    
    @Override
    public XSLFTheme getTheme() {
        return this.getSlideMaster().getTheme();
    }
    
    @Override
    public boolean getFollowMasterGraphics() {
        return this._layout.getShowMasterSp();
    }
    
    @Override
    public XSLFBackground getBackground() {
        final CTBackground bg = this._layout.getCSld().getBg();
        if (bg != null) {
            return new XSLFBackground(bg, this);
        }
        return this.getMasterSheet().getBackground();
    }
    
    public void copyLayout(final XSLFSlide slide) {
        for (final XSLFShape sh : this.getShapes()) {
            if (sh instanceof XSLFTextShape) {
                final XSLFTextShape tsh = (XSLFTextShape)sh;
                final Placeholder ph = tsh.getTextType();
                if (ph == null) {
                    continue;
                }
                switch (ph) {
                    case DATETIME:
                    case SLIDE_NUMBER:
                    case FOOTER: {
                        continue;
                    }
                    default: {
                        slide.getSpTree().addNewSp().set(tsh.getXmlObject().copy());
                        continue;
                    }
                }
            }
        }
    }
    
    public SlideLayout getType() {
        final int ordinal = this._layout.getType().intValue() - 1;
        return SlideLayout.values()[ordinal];
    }
    
    @Override
    String mapSchemeColor(final String schemeColor) {
        return this.mapSchemeColor(this._layout.getClrMapOvr(), schemeColor);
    }
}

package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.Background;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterTextStyles;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.apache.poi.sl.usermodel.Placeholder;
import java.util.Iterator;
import java.util.Locale;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import java.util.HashMap;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.openxmlformats.schemas.presentationml.x2006.main.SldMasterDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Map;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMaster;
import org.apache.poi.sl.usermodel.MasterSheet;

public class XSLFSlideMaster extends XSLFSheet implements MasterSheet<XSLFShape, XSLFTextParagraph>
{
    private CTSlideMaster _slide;
    private Map<String, XSLFSlideLayout> _layouts;
    
    protected XSLFSlideMaster(final PackagePart part) throws IOException, XmlException {
        super(part);
        final SldMasterDocument doc = SldMasterDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._slide = doc.getSldMaster();
    }
    
    public CTSlideMaster getXmlObject() {
        return this._slide;
    }
    
    @Override
    protected String getRootElementName() {
        return "sldMaster";
    }
    
    public XSLFSlideMaster getMasterSheet() {
        return null;
    }
    
    private Map<String, XSLFSlideLayout> getLayouts() {
        if (this._layouts == null) {
            this._layouts = new HashMap<String, XSLFSlideLayout>();
            for (final POIXMLDocumentPart p : this.getRelations()) {
                if (p instanceof XSLFSlideLayout) {
                    final XSLFSlideLayout layout = (XSLFSlideLayout)p;
                    this._layouts.put(layout.getName().toLowerCase(Locale.ROOT), layout);
                }
            }
        }
        return this._layouts;
    }
    
    public XSLFSlideLayout[] getSlideLayouts() {
        return this.getLayouts().values().toArray(new XSLFSlideLayout[this._layouts.size()]);
    }
    
    public XSLFSlideLayout getLayout(final SlideLayout type) {
        for (final XSLFSlideLayout layout : this.getLayouts().values()) {
            if (layout.getType() == type) {
                return layout;
            }
        }
        return null;
    }
    
    public XSLFSlideLayout getLayout(final String name) {
        return this.getLayouts().get(name.toLowerCase(Locale.ROOT));
    }
    
    protected CTTextListStyle getTextProperties(final Placeholder textType) {
        final CTSlideMasterTextStyles txStyles = this.getXmlObject().getTxStyles();
        CTTextListStyle props = null;
        switch (textType) {
            case TITLE:
            case CENTERED_TITLE:
            case SUBTITLE: {
                props = txStyles.getTitleStyle();
                break;
            }
            case BODY: {
                props = txStyles.getBodyStyle();
                break;
            }
            default: {
                props = txStyles.getOtherStyle();
                break;
            }
        }
        return props;
    }
    
    @Override
    public XSLFBackground getBackground() {
        final CTBackground bg = this._slide.getCSld().getBg();
        if (bg != null) {
            return new XSLFBackground(bg, this);
        }
        return null;
    }
    
    @Override
    boolean isSupportTheme() {
        return true;
    }
    
    @Override
    String mapSchemeColor(final String schemeColor) {
        final String masterColor = this.mapSchemeColor(this._slide.getClrMap(), schemeColor);
        return (masterColor == null) ? schemeColor : masterColor;
    }
}

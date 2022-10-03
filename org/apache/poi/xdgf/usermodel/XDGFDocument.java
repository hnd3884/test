package org.apache.poi.xdgf.usermodel;

import org.apache.poi.util.Internal;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetType;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSettingsType;
import org.apache.poi.ooxml.POIXMLException;
import java.util.HashMap;
import java.util.Map;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentType;

public class XDGFDocument
{
    protected VisioDocumentType _document;
    Map<Long, XDGFStyleSheet> _styleSheets;
    long _defaultFillStyle;
    long _defaultGuideStyle;
    long _defaultLineStyle;
    long _defaultTextStyle;
    
    public XDGFDocument(final VisioDocumentType document) {
        this._styleSheets = new HashMap<Long, XDGFStyleSheet>();
        this._document = document;
        if (!this._document.isSetDocumentSettings()) {
            throw new POIXMLException("Document settings not found");
        }
        final DocumentSettingsType docSettings = this._document.getDocumentSettings();
        if (docSettings.isSetDefaultFillStyle()) {
            this._defaultFillStyle = docSettings.getDefaultFillStyle();
        }
        if (docSettings.isSetDefaultGuideStyle()) {
            this._defaultGuideStyle = docSettings.getDefaultGuideStyle();
        }
        if (docSettings.isSetDefaultLineStyle()) {
            this._defaultLineStyle = docSettings.getDefaultLineStyle();
        }
        if (docSettings.isSetDefaultTextStyle()) {
            this._defaultTextStyle = docSettings.getDefaultTextStyle();
        }
        if (this._document.isSetStyleSheets()) {
            for (final StyleSheetType styleSheet : this._document.getStyleSheets().getStyleSheetArray()) {
                this._styleSheets.put(styleSheet.getID(), new XDGFStyleSheet(styleSheet, this));
            }
        }
    }
    
    @Internal
    public VisioDocumentType getXmlObject() {
        return this._document;
    }
    
    public XDGFStyleSheet getStyleById(final long id) {
        return this._styleSheets.get(id);
    }
    
    public XDGFStyleSheet getDefaultFillStyle() {
        final XDGFStyleSheet style = this.getStyleById(this._defaultFillStyle);
        if (style == null) {
            throw new POIXMLException("No default fill style found!");
        }
        return style;
    }
    
    public XDGFStyleSheet getDefaultGuideStyle() {
        final XDGFStyleSheet style = this.getStyleById(this._defaultGuideStyle);
        if (style == null) {
            throw new POIXMLException("No default guide style found!");
        }
        return style;
    }
    
    public XDGFStyleSheet getDefaultLineStyle() {
        final XDGFStyleSheet style = this.getStyleById(this._defaultLineStyle);
        if (style == null) {
            throw new POIXMLException("No default line style found!");
        }
        return style;
    }
    
    public XDGFStyleSheet getDefaultTextStyle() {
        final XDGFStyleSheet style = this.getStyleById(this._defaultTextStyle);
        if (style == null) {
            throw new POIXMLException("No default text style found!");
        }
        return style;
    }
}

package org.apache.poi.xslf.usermodel;

import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBaseStyles;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSLFTheme extends POIXMLDocumentPart
{
    private CTOfficeStyleSheet _theme;
    
    XSLFTheme() {
        this._theme = CTOfficeStyleSheet.Factory.newInstance();
    }
    
    public XSLFTheme(final PackagePart part) throws IOException, XmlException {
        super(part);
        final ThemeDocument doc = ThemeDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._theme = doc.getTheme();
    }
    
    public void importTheme(final XSLFTheme theme) {
        this._theme = theme.getXmlObject();
    }
    
    public String getName() {
        return this._theme.getName();
    }
    
    public void setName(final String name) {
        this._theme.setName(name);
    }
    
    @Internal
    public CTColor getCTColor(final String name) {
        final CTBaseStyles elems = this._theme.getThemeElements();
        final CTColorScheme scheme = (elems == null) ? null : elems.getClrScheme();
        return getMapColor(name, scheme);
    }
    
    private static CTColor getMapColor(final String mapName, final CTColorScheme scheme) {
        if (mapName == null || scheme == null) {
            return null;
        }
        switch (mapName) {
            case "accent1": {
                return scheme.getAccent1();
            }
            case "accent2": {
                return scheme.getAccent2();
            }
            case "accent3": {
                return scheme.getAccent3();
            }
            case "accent4": {
                return scheme.getAccent4();
            }
            case "accent5": {
                return scheme.getAccent5();
            }
            case "accent6": {
                return scheme.getAccent6();
            }
            case "dk1": {
                return scheme.getDk1();
            }
            case "dk2": {
                return scheme.getDk2();
            }
            case "folHlink": {
                return scheme.getFolHlink();
            }
            case "hlink": {
                return scheme.getHlink();
            }
            case "lt1": {
                return scheme.getLt1();
            }
            case "lt2": {
                return scheme.getLt2();
            }
            default: {
                return null;
            }
        }
    }
    
    @Internal
    public CTOfficeStyleSheet getXmlObject() {
        return this._theme;
    }
    
    @Override
    protected final void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "theme"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.getXmlObject().save(out, xmlOptions);
        out.close();
    }
    
    public String getMajorFont() {
        return this._theme.getThemeElements().getFontScheme().getMajorFont().getLatin().getTypeface();
    }
    
    public String getMinorFont() {
        return this._theme.getThemeElements().getFontScheme().getMinorFont().getLatin().getTypeface();
    }
}

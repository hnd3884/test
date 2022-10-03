package org.apache.poi.xssf.model;

import java.io.OutputStream;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class ThemesTable extends POIXMLDocumentPart implements Themes
{
    private IndexedColorMap colorMap;
    private ThemeDocument theme;
    
    public ThemesTable() {
        this.theme = ThemeDocument.Factory.newInstance();
        this.theme.addNewTheme().addNewThemeElements();
    }
    
    public ThemesTable(final PackagePart part) throws IOException {
        super(part);
        try {
            this.theme = ThemeDocument.Factory.parse(part.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage(), (Throwable)e);
        }
    }
    
    public ThemesTable(final ThemeDocument theme) {
        this.theme = theme;
    }
    
    protected void setColorMap(final IndexedColorMap colorMap) {
        this.colorMap = colorMap;
    }
    
    @Override
    public XSSFColor getThemeColor(final int idx) {
        final CTColorScheme colorScheme = this.theme.getTheme().getThemeElements().getClrScheme();
        CTColor ctColor = null;
        switch (ThemeElement.byId(idx)) {
            case LT1: {
                ctColor = colorScheme.getLt1();
                break;
            }
            case DK1: {
                ctColor = colorScheme.getDk1();
                break;
            }
            case LT2: {
                ctColor = colorScheme.getLt2();
                break;
            }
            case DK2: {
                ctColor = colorScheme.getDk2();
                break;
            }
            case ACCENT1: {
                ctColor = colorScheme.getAccent1();
                break;
            }
            case ACCENT2: {
                ctColor = colorScheme.getAccent2();
                break;
            }
            case ACCENT3: {
                ctColor = colorScheme.getAccent3();
                break;
            }
            case ACCENT4: {
                ctColor = colorScheme.getAccent4();
                break;
            }
            case ACCENT5: {
                ctColor = colorScheme.getAccent5();
                break;
            }
            case ACCENT6: {
                ctColor = colorScheme.getAccent6();
                break;
            }
            case HLINK: {
                ctColor = colorScheme.getHlink();
                break;
            }
            case FOLHLINK: {
                ctColor = colorScheme.getFolHlink();
                break;
            }
            default: {
                return null;
            }
        }
        byte[] rgb = null;
        if (ctColor.isSetSrgbClr()) {
            rgb = ctColor.getSrgbClr().getVal();
        }
        else {
            if (!ctColor.isSetSysClr()) {
                return null;
            }
            rgb = ctColor.getSysClr().getLastClr();
        }
        return new XSSFColor(rgb, this.colorMap);
    }
    
    @Override
    public void inheritFromThemeAsRequired(final XSSFColor color) {
        if (color == null) {
            return;
        }
        if (!color.getCTColor().isSetTheme()) {
            return;
        }
        final XSSFColor themeColor = this.getThemeColor(color.getTheme());
        color.getCTColor().setRgb(themeColor.getCTColor().getRgb());
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        this.theme.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
    
    public enum ThemeElement
    {
        LT1(0, "Lt1"), 
        DK1(1, "Dk1"), 
        LT2(2, "Lt2"), 
        DK2(3, "Dk2"), 
        ACCENT1(4, "Accent1"), 
        ACCENT2(5, "Accent2"), 
        ACCENT3(6, "Accent3"), 
        ACCENT4(7, "Accent4"), 
        ACCENT5(8, "Accent5"), 
        ACCENT6(9, "Accent6"), 
        HLINK(10, "Hlink"), 
        FOLHLINK(11, "FolHlink"), 
        UNKNOWN(-1, (String)null);
        
        public final int idx;
        public final String name;
        
        public static ThemeElement byId(final int idx) {
            if (idx >= values().length || idx < 0) {
                return ThemeElement.UNKNOWN;
            }
            return values()[idx];
        }
        
        private ThemeElement(final int idx, final String name) {
            this.idx = idx;
            this.name = name;
        }
    }
}

package org.apache.poi.xssf.model;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.xssf.usermodel.XSSFBuiltinTableStyle;
import java.util.Set;
import org.apache.poi.ss.usermodel.FontScheme;
import org.apache.poi.ss.usermodel.FontFamily;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;
import java.io.OutputStream;
import org.apache.poi.util.Internal;
import java.util.Collections;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyleXfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellXfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorders;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFills;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFonts;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmts;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTStylesheet;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.xssf.usermodel.XSSFTableStyle;
import java.util.Collection;
import java.util.Arrays;
import org.apache.poi.xssf.usermodel.CustomIndexedColorMap;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.xssf.usermodel.XSSFFactory;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import java.util.Iterator;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeMap;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.StyleSheetDocument;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.ss.usermodel.TableStyle;
import java.util.Map;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.apache.poi.xssf.usermodel.XSSFFont;
import java.util.List;
import java.util.SortedMap;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class StylesTable extends POIXMLDocumentPart implements Styles
{
    private final SortedMap<Short, String> numberFormats;
    private final List<XSSFFont> fonts;
    private final List<XSSFCellFill> fills;
    private final List<XSSFCellBorder> borders;
    private final List<CTXf> styleXfs;
    private final List<CTXf> xfs;
    private final List<CTDxf> dxfs;
    private final Map<String, TableStyle> tableStyles;
    private IndexedColorMap indexedColors;
    public static final int FIRST_CUSTOM_STYLE_ID = 165;
    private static final int MAXIMUM_STYLE_ID;
    private static final short FIRST_USER_DEFINED_NUMBER_FORMAT_ID = 164;
    private int MAXIMUM_NUMBER_OF_DATA_FORMATS;
    private StyleSheetDocument doc;
    private XSSFWorkbook workbook;
    private ThemesTable theme;
    
    public void setMaxNumberOfDataFormats(final int num) {
        if (num >= this.getNumDataFormats()) {
            this.MAXIMUM_NUMBER_OF_DATA_FORMATS = num;
            return;
        }
        if (num < 0) {
            throw new IllegalArgumentException("Maximum Number of Data Formats must be greater than or equal to 0");
        }
        throw new IllegalStateException("Cannot set the maximum number of data formats less than the current quantity. Data formats must be explicitly removed (via StylesTable.removeNumberFormat) before the limit can be decreased.");
    }
    
    public int getMaxNumberOfDataFormats() {
        return this.MAXIMUM_NUMBER_OF_DATA_FORMATS;
    }
    
    public StylesTable() {
        this.numberFormats = new TreeMap<Short, String>();
        this.fonts = new ArrayList<XSSFFont>();
        this.fills = new ArrayList<XSSFCellFill>();
        this.borders = new ArrayList<XSSFCellBorder>();
        this.styleXfs = new ArrayList<CTXf>();
        this.xfs = new ArrayList<CTXf>();
        this.dxfs = new ArrayList<CTDxf>();
        this.tableStyles = new HashMap<String, TableStyle>();
        this.indexedColors = new DefaultIndexedColorMap();
        this.MAXIMUM_NUMBER_OF_DATA_FORMATS = 250;
        (this.doc = StyleSheetDocument.Factory.newInstance()).addNewStyleSheet();
        this.initialize();
    }
    
    public StylesTable(final PackagePart part) throws IOException {
        super(part);
        this.numberFormats = new TreeMap<Short, String>();
        this.fonts = new ArrayList<XSSFFont>();
        this.fills = new ArrayList<XSSFCellFill>();
        this.borders = new ArrayList<XSSFCellBorder>();
        this.styleXfs = new ArrayList<CTXf>();
        this.xfs = new ArrayList<CTXf>();
        this.dxfs = new ArrayList<CTDxf>();
        this.tableStyles = new HashMap<String, TableStyle>();
        this.indexedColors = new DefaultIndexedColorMap();
        this.MAXIMUM_NUMBER_OF_DATA_FORMATS = 250;
        this.readFrom(part.getInputStream());
    }
    
    public void setWorkbook(final XSSFWorkbook wb) {
        this.workbook = wb;
    }
    
    public ThemesTable getTheme() {
        return this.theme;
    }
    
    public void setTheme(final ThemesTable theme) {
        this.theme = theme;
        if (theme != null) {
            theme.setColorMap(this.getIndexedColors());
        }
        for (final XSSFFont font : this.fonts) {
            font.setThemesTable(theme);
        }
        for (final XSSFCellBorder border : this.borders) {
            border.setThemesTable(theme);
        }
    }
    
    public void ensureThemesTable() {
        if (this.theme != null) {
            return;
        }
        this.setTheme((ThemesTable)this.workbook.createRelationship(XSSFRelation.THEME, XSSFFactory.getInstance()));
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            this.doc = StyleSheetDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            final CTStylesheet styleSheet = this.doc.getStyleSheet();
            final IndexedColorMap customColors = CustomIndexedColorMap.fromColors(styleSheet.getColors());
            if (customColors != null) {
                this.indexedColors = customColors;
            }
            final CTNumFmts ctfmts = styleSheet.getNumFmts();
            if (ctfmts != null) {
                for (final CTNumFmt nfmt : ctfmts.getNumFmtArray()) {
                    final short formatId = (short)nfmt.getNumFmtId();
                    this.numberFormats.put(formatId, nfmt.getFormatCode());
                }
            }
            final CTFonts ctfonts = styleSheet.getFonts();
            if (ctfonts != null) {
                int idx = 0;
                for (final CTFont font : ctfonts.getFontArray()) {
                    final XSSFFont f = new XSSFFont(font, idx, this.indexedColors);
                    this.fonts.add(f);
                    ++idx;
                }
            }
            final CTFills ctfills = styleSheet.getFills();
            if (ctfills != null) {
                for (final CTFill fill : ctfills.getFillArray()) {
                    this.fills.add(new XSSFCellFill(fill, this.indexedColors));
                }
            }
            final CTBorders ctborders = styleSheet.getBorders();
            if (ctborders != null) {
                for (final CTBorder border : ctborders.getBorderArray()) {
                    this.borders.add(new XSSFCellBorder(border, this.indexedColors));
                }
            }
            final CTCellXfs cellXfs = styleSheet.getCellXfs();
            if (cellXfs != null) {
                this.xfs.addAll(Arrays.asList(cellXfs.getXfArray()));
            }
            final CTCellStyleXfs cellStyleXfs = styleSheet.getCellStyleXfs();
            if (cellStyleXfs != null) {
                this.styleXfs.addAll(Arrays.asList(cellStyleXfs.getXfArray()));
            }
            final CTDxfs styleDxfs = styleSheet.getDxfs();
            if (styleDxfs != null) {
                this.dxfs.addAll(Arrays.asList(styleDxfs.getDxfArray()));
            }
            final CTTableStyles ctTableStyles = styleSheet.getTableStyles();
            if (ctTableStyles != null) {
                int idx2 = 0;
                for (final CTTableStyle style : ctTableStyles.getTableStyleArray()) {
                    this.tableStyles.put(style.getName(), (TableStyle)new XSSFTableStyle(idx2, styleDxfs, style, this.indexedColors));
                    ++idx2;
                }
            }
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    @Override
    public String getNumberFormatAt(final short fmtId) {
        return this.numberFormats.get(fmtId);
    }
    
    private short getNumberFormatId(final String fmt) {
        for (final Map.Entry<Short, String> numFmt : this.numberFormats.entrySet()) {
            if (numFmt.getValue().equals(fmt)) {
                return numFmt.getKey();
            }
        }
        throw new IllegalStateException("Number format not in style table: " + fmt);
    }
    
    @Override
    public int putNumberFormat(final String fmt) {
        if (this.numberFormats.containsValue(fmt)) {
            try {
                return this.getNumberFormatId(fmt);
            }
            catch (final IllegalStateException e) {
                throw new IllegalStateException("Found the format, but couldn't figure out where - should never happen!");
            }
        }
        if (this.numberFormats.size() >= this.MAXIMUM_NUMBER_OF_DATA_FORMATS) {
            throw new IllegalStateException("The maximum number of Data Formats was exceeded. You can define up to " + this.MAXIMUM_NUMBER_OF_DATA_FORMATS + " formats in a .xlsx Workbook.");
        }
        short formatIndex;
        if (this.numberFormats.isEmpty()) {
            formatIndex = 164;
        }
        else {
            final short nextKey = (short)(this.numberFormats.lastKey() + 1);
            if (nextKey < 0) {
                throw new IllegalStateException("Cowardly avoiding creating a number format with a negative id. This is probably due to arithmetic overflow.");
            }
            formatIndex = (short)Math.max(nextKey, 164);
        }
        this.numberFormats.put(formatIndex, fmt);
        return formatIndex;
    }
    
    @Override
    public void putNumberFormat(final short index, final String fmt) {
        this.numberFormats.put(index, fmt);
    }
    
    @Override
    public boolean removeNumberFormat(final short index) {
        final String fmt = this.numberFormats.remove(index);
        final boolean removed = fmt != null;
        if (removed) {
            for (final CTXf style : this.xfs) {
                if (style.isSetNumFmtId() && style.getNumFmtId() == index) {
                    style.unsetApplyNumberFormat();
                    style.unsetNumFmtId();
                }
            }
        }
        return removed;
    }
    
    @Override
    public boolean removeNumberFormat(final String fmt) {
        final short id = this.getNumberFormatId(fmt);
        return this.removeNumberFormat(id);
    }
    
    @Override
    public XSSFFont getFontAt(final int idx) {
        return this.fonts.get(idx);
    }
    
    @Override
    public int putFont(final XSSFFont font, final boolean forceRegistration) {
        int idx = -1;
        if (!forceRegistration) {
            idx = this.fonts.indexOf(font);
        }
        if (idx != -1) {
            return idx;
        }
        idx = this.fonts.size();
        this.fonts.add(font);
        return idx;
    }
    
    @Override
    public int putFont(final XSSFFont font) {
        return this.putFont(font, false);
    }
    
    @Override
    public XSSFCellStyle getStyleAt(final int idx) {
        int styleXfId = 0;
        if (idx < 0 || idx >= this.xfs.size()) {
            return null;
        }
        if (this.xfs.get(idx).getXfId() > 0L) {
            styleXfId = (int)this.xfs.get(idx).getXfId();
        }
        return new XSSFCellStyle(idx, styleXfId, this, this.theme);
    }
    
    @Override
    public int putStyle(final XSSFCellStyle style) {
        final CTXf mainXF = style.getCoreXf();
        if (!this.xfs.contains(mainXF)) {
            this.xfs.add(mainXF);
        }
        return this.xfs.indexOf(mainXF);
    }
    
    @Override
    public XSSFCellBorder getBorderAt(final int idx) {
        return this.borders.get(idx);
    }
    
    @Override
    public int putBorder(final XSSFCellBorder border) {
        final int idx = this.borders.indexOf(border);
        if (idx != -1) {
            return idx;
        }
        this.borders.add(border);
        border.setThemesTable(this.theme);
        return this.borders.size() - 1;
    }
    
    @Override
    public XSSFCellFill getFillAt(final int idx) {
        return this.fills.get(idx);
    }
    
    public List<XSSFCellBorder> getBorders() {
        return Collections.unmodifiableList((List<? extends XSSFCellBorder>)this.borders);
    }
    
    public List<XSSFCellFill> getFills() {
        return Collections.unmodifiableList((List<? extends XSSFCellFill>)this.fills);
    }
    
    public List<XSSFFont> getFonts() {
        return Collections.unmodifiableList((List<? extends XSSFFont>)this.fonts);
    }
    
    public Map<Short, String> getNumberFormats() {
        return Collections.unmodifiableMap((Map<? extends Short, ? extends String>)this.numberFormats);
    }
    
    @Override
    public int putFill(final XSSFCellFill fill) {
        final int idx = this.fills.indexOf(fill);
        if (idx != -1) {
            return idx;
        }
        this.fills.add(fill);
        return this.fills.size() - 1;
    }
    
    @Internal
    public CTXf getCellXfAt(final int idx) {
        return this.xfs.get(idx);
    }
    
    @Internal
    public int putCellXf(final CTXf cellXf) {
        this.xfs.add(cellXf);
        return this.xfs.size();
    }
    
    @Internal
    public void replaceCellXfAt(final int idx, final CTXf cellXf) {
        this.xfs.set(idx, cellXf);
    }
    
    @Internal
    public CTXf getCellStyleXfAt(final int idx) {
        try {
            return this.styleXfs.get(idx);
        }
        catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    @Internal
    public int putCellStyleXf(final CTXf cellStyleXf) {
        this.styleXfs.add(cellStyleXf);
        return this.styleXfs.size();
    }
    
    @Internal
    protected void replaceCellStyleXfAt(final int idx, final CTXf cellStyleXf) {
        this.styleXfs.set(idx, cellStyleXf);
    }
    
    @Override
    public int getNumCellStyles() {
        return this.xfs.size();
    }
    
    @Override
    public int getNumDataFormats() {
        return this.numberFormats.size();
    }
    
    @Internal
    int _getXfsSize() {
        return this.xfs.size();
    }
    
    @Internal
    public int _getStyleXfsSize() {
        return this.styleXfs.size();
    }
    
    @Internal
    public CTStylesheet getCTStylesheet() {
        return this.doc.getStyleSheet();
    }
    
    @Internal
    public int _getDXfsSize() {
        return this.dxfs.size();
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        final CTStylesheet styleSheet = this.doc.getStyleSheet();
        final CTNumFmts formats = CTNumFmts.Factory.newInstance();
        formats.setCount((long)this.numberFormats.size());
        for (final Map.Entry<Short, String> entry : this.numberFormats.entrySet()) {
            final CTNumFmt ctFmt = formats.addNewNumFmt();
            ctFmt.setNumFmtId((long)entry.getKey());
            ctFmt.setFormatCode((String)entry.getValue());
        }
        styleSheet.setNumFmts(formats);
        CTFonts ctFonts = styleSheet.getFonts();
        if (ctFonts == null) {
            ctFonts = CTFonts.Factory.newInstance();
        }
        ctFonts.setCount((long)this.fonts.size());
        final CTFont[] ctfnt = new CTFont[this.fonts.size()];
        int idx = 0;
        for (final XSSFFont f : this.fonts) {
            ctfnt[idx++] = f.getCTFont();
        }
        ctFonts.setFontArray(ctfnt);
        styleSheet.setFonts(ctFonts);
        CTFills ctFills = styleSheet.getFills();
        if (ctFills == null) {
            ctFills = CTFills.Factory.newInstance();
        }
        ctFills.setCount((long)this.fills.size());
        final CTFill[] ctf = new CTFill[this.fills.size()];
        idx = 0;
        for (final XSSFCellFill f2 : this.fills) {
            ctf[idx++] = f2.getCTFill();
        }
        ctFills.setFillArray(ctf);
        styleSheet.setFills(ctFills);
        CTBorders ctBorders = styleSheet.getBorders();
        if (ctBorders == null) {
            ctBorders = CTBorders.Factory.newInstance();
        }
        ctBorders.setCount((long)this.borders.size());
        final CTBorder[] ctb = new CTBorder[this.borders.size()];
        idx = 0;
        for (final XSSFCellBorder b : this.borders) {
            ctb[idx++] = b.getCTBorder();
        }
        ctBorders.setBorderArray(ctb);
        styleSheet.setBorders(ctBorders);
        if (this.xfs.size() > 0) {
            CTCellXfs ctXfs = styleSheet.getCellXfs();
            if (ctXfs == null) {
                ctXfs = CTCellXfs.Factory.newInstance();
            }
            ctXfs.setCount((long)this.xfs.size());
            ctXfs.setXfArray((CTXf[])this.xfs.toArray(new CTXf[0]));
            styleSheet.setCellXfs(ctXfs);
        }
        if (this.styleXfs.size() > 0) {
            CTCellStyleXfs ctSXfs = styleSheet.getCellStyleXfs();
            if (ctSXfs == null) {
                ctSXfs = CTCellStyleXfs.Factory.newInstance();
            }
            ctSXfs.setCount((long)this.styleXfs.size());
            ctSXfs.setXfArray((CTXf[])this.styleXfs.toArray(new CTXf[0]));
            styleSheet.setCellStyleXfs(ctSXfs);
        }
        if (this.dxfs.size() > 0) {
            CTDxfs ctDxfs = styleSheet.getDxfs();
            if (ctDxfs == null) {
                ctDxfs = CTDxfs.Factory.newInstance();
            }
            ctDxfs.setCount((long)this.dxfs.size());
            ctDxfs.setDxfArray((CTDxf[])this.dxfs.toArray(new CTDxf[0]));
            styleSheet.setDxfs(ctDxfs);
        }
        this.doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
    
    private void initialize() {
        final XSSFFont xssfFont = createDefaultFont();
        this.fonts.add(xssfFont);
        final CTFill[] ctFill = createDefaultFills();
        this.fills.add(new XSSFCellFill(ctFill[0], this.indexedColors));
        this.fills.add(new XSSFCellFill(ctFill[1], this.indexedColors));
        final CTBorder ctBorder = createDefaultBorder();
        this.borders.add(new XSSFCellBorder(ctBorder));
        final CTXf styleXf = createDefaultXf();
        this.styleXfs.add(styleXf);
        final CTXf xf = createDefaultXf();
        xf.setXfId(0L);
        this.xfs.add(xf);
    }
    
    private static CTXf createDefaultXf() {
        final CTXf ctXf = CTXf.Factory.newInstance();
        ctXf.setNumFmtId(0L);
        ctXf.setFontId(0L);
        ctXf.setFillId(0L);
        ctXf.setBorderId(0L);
        return ctXf;
    }
    
    private static CTBorder createDefaultBorder() {
        final CTBorder ctBorder = CTBorder.Factory.newInstance();
        ctBorder.addNewBottom();
        ctBorder.addNewTop();
        ctBorder.addNewLeft();
        ctBorder.addNewRight();
        ctBorder.addNewDiagonal();
        return ctBorder;
    }
    
    private static CTFill[] createDefaultFills() {
        final CTFill[] ctFill = { CTFill.Factory.newInstance(), CTFill.Factory.newInstance() };
        ctFill[0].addNewPatternFill().setPatternType(STPatternType.NONE);
        ctFill[1].addNewPatternFill().setPatternType(STPatternType.DARK_GRAY);
        return ctFill;
    }
    
    private static XSSFFont createDefaultFont() {
        final CTFont ctFont = CTFont.Factory.newInstance();
        final XSSFFont xssfFont = new XSSFFont(ctFont, 0, null);
        xssfFont.setFontHeightInPoints((short)11);
        xssfFont.setColor(XSSFFont.DEFAULT_FONT_COLOR);
        xssfFont.setFontName("Calibri");
        xssfFont.setFamily(FontFamily.SWISS);
        xssfFont.setScheme(FontScheme.MINOR);
        return xssfFont;
    }
    
    @Internal
    public CTDxf getDxfAt(final int idx) {
        return this.dxfs.get(idx);
    }
    
    @Internal
    public int putDxf(final CTDxf dxf) {
        this.dxfs.add(dxf);
        return this.dxfs.size();
    }
    
    public TableStyle getExplicitTableStyle(final String name) {
        return this.tableStyles.get(name);
    }
    
    public Set<String> getExplicitTableStyleNames() {
        return this.tableStyles.keySet();
    }
    
    public TableStyle getTableStyle(final String name) {
        if (name == null) {
            return null;
        }
        try {
            return XSSFBuiltinTableStyle.valueOf(name).getStyle();
        }
        catch (final IllegalArgumentException e) {
            return this.getExplicitTableStyle(name);
        }
    }
    
    public XSSFCellStyle createCellStyle() {
        if (this.getNumCellStyles() > StylesTable.MAXIMUM_STYLE_ID) {
            throw new IllegalStateException("The maximum number of Cell Styles was exceeded. You can define up to " + StylesTable.MAXIMUM_STYLE_ID + " style in a .xlsx Workbook");
        }
        final int xfSize = this.styleXfs.size();
        final CTXf xf = CTXf.Factory.newInstance();
        xf.setNumFmtId(0L);
        xf.setFontId(0L);
        xf.setFillId(0L);
        xf.setBorderId(0L);
        xf.setXfId(0L);
        final int indexXf = this.putCellXf(xf);
        return new XSSFCellStyle(indexXf - 1, xfSize - 1, this, this.theme);
    }
    
    public XSSFFont findFont(final boolean bold, final short color, final short fontHeight, final String name, final boolean italic, final boolean strikeout, final short typeOffset, final byte underline) {
        for (final XSSFFont font : this.fonts) {
            if (font.getBold() == bold && font.getColor() == color && font.getFontHeight() == fontHeight && font.getFontName().equals(name) && font.getItalic() == italic && font.getStrikeout() == strikeout && font.getTypeOffset() == typeOffset && font.getUnderline() == underline) {
                return font;
            }
        }
        return null;
    }
    
    public XSSFFont findFont(final boolean bold, final Color color, final short fontHeight, final String name, final boolean italic, final boolean strikeout, final short typeOffset, final byte underline) {
        for (final XSSFFont font : this.fonts) {
            if (font.getBold() == bold && font.getXSSFColor().equals(color) && font.getFontHeight() == fontHeight && font.getFontName().equals(name) && font.getItalic() == italic && font.getStrikeout() == strikeout && font.getTypeOffset() == typeOffset && font.getUnderline() == underline) {
                return font;
            }
        }
        return null;
    }
    
    public IndexedColorMap getIndexedColors() {
        return this.indexedColors;
    }
    
    static {
        MAXIMUM_STYLE_ID = SpreadsheetVersion.EXCEL2007.getMaxCellStyles();
    }
}

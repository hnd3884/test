package org.apache.poi.xssf.usermodel;

import java.util.SortedMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.xmlbeans.XmlCursor;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.poi.xssf.model.ThemesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;
import java.util.TreeMap;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRPrElt;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.RichTextString;

public class XSSFRichTextString implements RichTextString
{
    private static final Pattern utfPtrn;
    private CTRst st;
    private StylesTable styles;
    
    public XSSFRichTextString(final String str) {
        (this.st = CTRst.Factory.newInstance()).setT(str);
        preserveSpaces(this.st.xgetT());
    }
    
    public XSSFRichTextString() {
        this.st = CTRst.Factory.newInstance();
    }
    
    @Internal
    public XSSFRichTextString(final CTRst st) {
        this.st = st;
    }
    
    public void applyFont(final int startIndex, final int endIndex, final short fontIndex) {
        XSSFFont font;
        if (this.styles == null) {
            font = new XSSFFont();
            font.setFontName("#" + fontIndex);
        }
        else {
            font = this.styles.getFontAt(fontIndex);
        }
        this.applyFont(startIndex, endIndex, (Font)font);
    }
    
    public void applyFont(final int startIndex, final int endIndex, final Font font) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index must be less than end index, but had " + startIndex + " and " + endIndex);
        }
        if (startIndex < 0 || endIndex > this.length()) {
            throw new IllegalArgumentException("Start and end index not in range, but had " + startIndex + " and " + endIndex);
        }
        if (startIndex == endIndex) {
            return;
        }
        if (this.st.sizeOfRArray() == 0 && this.st.isSetT()) {
            this.st.addNewR().setT(this.st.getT());
            this.st.unsetT();
        }
        final String text = this.getString();
        final XSSFFont xssfFont = (XSSFFont)font;
        final TreeMap<Integer, CTRPrElt> formats = this.getFormatMap(this.st);
        final CTRPrElt fmt = CTRPrElt.Factory.newInstance();
        this.setRunAttributes(xssfFont.getCTFont(), fmt);
        this.applyFont(formats, startIndex, endIndex, fmt);
        final CTRst newSt = this.buildCTRst(text, formats);
        this.st.set((XmlObject)newSt);
    }
    
    public void applyFont(final Font font) {
        final String text = this.getString();
        this.applyFont(0, text.length(), font);
    }
    
    public void applyFont(final short fontIndex) {
        XSSFFont font;
        if (this.styles == null) {
            font = new XSSFFont();
            font.setFontName("#" + fontIndex);
        }
        else {
            font = this.styles.getFontAt(fontIndex);
        }
        final String text = this.getString();
        this.applyFont(0, text.length(), (Font)font);
    }
    
    public void append(final String text, final XSSFFont font) {
        if (this.st.sizeOfRArray() == 0 && this.st.isSetT()) {
            final CTRElt lt = this.st.addNewR();
            lt.setT(this.st.getT());
            preserveSpaces(lt.xgetT());
            this.st.unsetT();
        }
        final CTRElt lt = this.st.addNewR();
        lt.setT(text);
        preserveSpaces(lt.xgetT());
        if (font != null) {
            final CTRPrElt pr = lt.addNewRPr();
            this.setRunAttributes(font.getCTFont(), pr);
        }
    }
    
    public void append(final String text) {
        this.append(text, null);
    }
    
    private void setRunAttributes(final CTFont ctFont, final CTRPrElt pr) {
        if (ctFont.sizeOfBArray() > 0) {
            pr.addNewB().setVal(ctFont.getBArray(0).getVal());
        }
        if (ctFont.sizeOfUArray() > 0) {
            pr.addNewU().setVal(ctFont.getUArray(0).getVal());
        }
        if (ctFont.sizeOfIArray() > 0) {
            pr.addNewI().setVal(ctFont.getIArray(0).getVal());
        }
        if (ctFont.sizeOfColorArray() > 0) {
            final CTColor c1 = ctFont.getColorArray(0);
            final CTColor c2 = pr.addNewColor();
            if (c1.isSetAuto()) {
                c2.setAuto(c1.getAuto());
            }
            if (c1.isSetIndexed()) {
                c2.setIndexed(c1.getIndexed());
            }
            if (c1.isSetRgb()) {
                c2.setRgb(c1.getRgb());
            }
            if (c1.isSetTheme()) {
                c2.setTheme(c1.getTheme());
            }
            if (c1.isSetTint()) {
                c2.setTint(c1.getTint());
            }
        }
        if (ctFont.sizeOfSzArray() > 0) {
            pr.addNewSz().setVal(ctFont.getSzArray(0).getVal());
        }
        if (ctFont.sizeOfNameArray() > 0) {
            pr.addNewRFont().setVal(ctFont.getNameArray(0).getVal());
        }
        if (ctFont.sizeOfFamilyArray() > 0) {
            pr.addNewFamily().setVal(ctFont.getFamilyArray(0).getVal());
        }
        if (ctFont.sizeOfSchemeArray() > 0) {
            pr.addNewScheme().setVal(ctFont.getSchemeArray(0).getVal());
        }
        if (ctFont.sizeOfCharsetArray() > 0) {
            pr.addNewCharset().setVal(ctFont.getCharsetArray(0).getVal());
        }
        if (ctFont.sizeOfCondenseArray() > 0) {
            pr.addNewCondense().setVal(ctFont.getCondenseArray(0).getVal());
        }
        if (ctFont.sizeOfExtendArray() > 0) {
            pr.addNewExtend().setVal(ctFont.getExtendArray(0).getVal());
        }
        if (ctFont.sizeOfVertAlignArray() > 0) {
            pr.addNewVertAlign().setVal(ctFont.getVertAlignArray(0).getVal());
        }
        if (ctFont.sizeOfOutlineArray() > 0) {
            pr.addNewOutline().setVal(ctFont.getOutlineArray(0).getVal());
        }
        if (ctFont.sizeOfShadowArray() > 0) {
            pr.addNewShadow().setVal(ctFont.getShadowArray(0).getVal());
        }
        if (ctFont.sizeOfStrikeArray() > 0) {
            pr.addNewStrike().setVal(ctFont.getStrikeArray(0).getVal());
        }
    }
    
    public boolean hasFormatting() {
        final CTRElt[] rs = this.st.getRArray();
        if (rs == null || rs.length == 0) {
            return false;
        }
        for (final CTRElt r : rs) {
            if (r.isSetRPr()) {
                return true;
            }
        }
        return false;
    }
    
    public void clearFormatting() {
        final String text = this.getString();
        this.st.setRArray((CTRElt[])null);
        this.st.setT(text);
    }
    
    public int getIndexOfFormattingRun(final int index) {
        if (this.st.sizeOfRArray() == 0) {
            return 0;
        }
        int pos = 0;
        for (int i = 0; i < this.st.sizeOfRArray(); ++i) {
            final CTRElt r = this.st.getRArray(i);
            if (i == index) {
                return pos;
            }
            pos += r.getT().length();
        }
        return -1;
    }
    
    public int getLengthOfFormattingRun(final int index) {
        if (this.st.sizeOfRArray() == 0 || index >= this.st.sizeOfRArray()) {
            return -1;
        }
        final CTRElt r = this.st.getRArray(index);
        return r.getT().length();
    }
    
    public String getString() {
        if (this.st.sizeOfRArray() == 0) {
            return utfDecode(this.st.getT());
        }
        final StringBuilder buf = new StringBuilder();
        for (final CTRElt r : this.st.getRArray()) {
            buf.append(r.getT());
        }
        return utfDecode(buf.toString());
    }
    
    public void setString(final String s) {
        this.clearFormatting();
        this.st.setT(s);
        preserveSpaces(this.st.xgetT());
    }
    
    @Override
    public String toString() {
        final String str = this.getString();
        if (str == null) {
            return "";
        }
        return str;
    }
    
    public int length() {
        return this.getString().length();
    }
    
    public int numFormattingRuns() {
        return this.st.sizeOfRArray();
    }
    
    public XSSFFont getFontOfFormattingRun(final int index) {
        if (this.st.sizeOfRArray() == 0 || index >= this.st.sizeOfRArray()) {
            return null;
        }
        final CTRElt r = this.st.getRArray(index);
        if (r.getRPr() != null) {
            final XSSFFont fnt = new XSSFFont(toCTFont(r.getRPr()));
            fnt.setThemesTable(this.getThemesTable());
            return fnt;
        }
        return null;
    }
    
    public XSSFFont getFontAtIndex(final int index) {
        final ThemesTable themes = this.getThemesTable();
        int pos = 0;
        for (final CTRElt r : this.st.getRArray()) {
            final int length = r.getT().length();
            if (index >= pos && index < pos + length) {
                final XSSFFont fnt = new XSSFFont(toCTFont(r.getRPr()));
                fnt.setThemesTable(themes);
                return fnt;
            }
            pos += length;
        }
        return null;
    }
    
    @Internal
    public CTRst getCTRst() {
        return this.st;
    }
    
    protected void setStylesTableReference(final StylesTable tbl) {
        this.styles = tbl;
        if (this.st.sizeOfRArray() > 0) {
            for (final CTRElt r : this.st.getRArray()) {
                final CTRPrElt pr = r.getRPr();
                if (pr != null && pr.sizeOfRFontArray() > 0) {
                    final String fontName = pr.getRFontArray(0).getVal();
                    if (fontName.startsWith("#")) {
                        final int idx = Integer.parseInt(fontName.substring(1));
                        final XSSFFont font = this.styles.getFontAt(idx);
                        pr.removeRFont(0);
                        this.setRunAttributes(font.getCTFont(), pr);
                    }
                }
            }
        }
    }
    
    protected static CTFont toCTFont(final CTRPrElt pr) {
        final CTFont ctFont = CTFont.Factory.newInstance();
        if (pr == null) {
            return ctFont;
        }
        if (pr.sizeOfBArray() > 0) {
            ctFont.addNewB().setVal(pr.getBArray(0).getVal());
        }
        if (pr.sizeOfUArray() > 0) {
            ctFont.addNewU().setVal(pr.getUArray(0).getVal());
        }
        if (pr.sizeOfIArray() > 0) {
            ctFont.addNewI().setVal(pr.getIArray(0).getVal());
        }
        if (pr.sizeOfColorArray() > 0) {
            final CTColor c1 = pr.getColorArray(0);
            final CTColor c2 = ctFont.addNewColor();
            if (c1.isSetAuto()) {
                c2.setAuto(c1.getAuto());
            }
            if (c1.isSetIndexed()) {
                c2.setIndexed(c1.getIndexed());
            }
            if (c1.isSetRgb()) {
                c2.setRgb(c1.getRgb());
            }
            if (c1.isSetTheme()) {
                c2.setTheme(c1.getTheme());
            }
            if (c1.isSetTint()) {
                c2.setTint(c1.getTint());
            }
        }
        if (pr.sizeOfSzArray() > 0) {
            ctFont.addNewSz().setVal(pr.getSzArray(0).getVal());
        }
        if (pr.sizeOfRFontArray() > 0) {
            ctFont.addNewName().setVal(pr.getRFontArray(0).getVal());
        }
        if (pr.sizeOfFamilyArray() > 0) {
            ctFont.addNewFamily().setVal(pr.getFamilyArray(0).getVal());
        }
        if (pr.sizeOfSchemeArray() > 0) {
            ctFont.addNewScheme().setVal(pr.getSchemeArray(0).getVal());
        }
        if (pr.sizeOfCharsetArray() > 0) {
            ctFont.addNewCharset().setVal(pr.getCharsetArray(0).getVal());
        }
        if (pr.sizeOfCondenseArray() > 0) {
            ctFont.addNewCondense().setVal(pr.getCondenseArray(0).getVal());
        }
        if (pr.sizeOfExtendArray() > 0) {
            ctFont.addNewExtend().setVal(pr.getExtendArray(0).getVal());
        }
        if (pr.sizeOfVertAlignArray() > 0) {
            ctFont.addNewVertAlign().setVal(pr.getVertAlignArray(0).getVal());
        }
        if (pr.sizeOfOutlineArray() > 0) {
            ctFont.addNewOutline().setVal(pr.getOutlineArray(0).getVal());
        }
        if (pr.sizeOfShadowArray() > 0) {
            ctFont.addNewShadow().setVal(pr.getShadowArray(0).getVal());
        }
        if (pr.sizeOfStrikeArray() > 0) {
            ctFont.addNewStrike().setVal(pr.getStrikeArray(0).getVal());
        }
        return ctFont;
    }
    
    protected static void preserveSpaces(final STXstring xs) {
        final String text = xs.getStringValue();
        if (text != null && text.length() > 0) {
            final char firstChar = text.charAt(0);
            final char lastChar = text.charAt(text.length() - 1);
            if (Character.isWhitespace(firstChar) || Character.isWhitespace(lastChar)) {
                final XmlCursor c = xs.newCursor();
                c.toNextToken();
                c.insertAttributeWithValue(new QName("http://www.w3.org/XML/1998/namespace", "space"), "preserve");
                c.dispose();
            }
        }
    }
    
    static String utfDecode(final String value) {
        if (value == null || !value.contains("_x")) {
            return value;
        }
        final StringBuilder buf = new StringBuilder();
        final Matcher m = XSSFRichTextString.utfPtrn.matcher(value);
        int idx = 0;
        while (m.find()) {
            final int pos = m.start();
            if (pos > idx) {
                buf.append(value, idx, pos);
            }
            final String code = m.group(1);
            final int icode = Integer.decode("0x" + code);
            buf.append((char)icode);
            idx = m.end();
        }
        if (idx == 0) {
            return value;
        }
        buf.append(value.substring(idx));
        return buf.toString();
    }
    
    void applyFont(final TreeMap<Integer, CTRPrElt> formats, final int startIndex, final int endIndex, final CTRPrElt fmt) {
        int runStartIdx = 0;
        final Iterator<Integer> it = formats.keySet().iterator();
        while (it.hasNext()) {
            final int runEndIdx = it.next();
            if (runStartIdx >= startIndex && runEndIdx < endIndex) {
                it.remove();
            }
            runStartIdx = runEndIdx;
        }
        if (startIndex > 0 && !formats.containsKey(startIndex)) {
            for (final Map.Entry<Integer, CTRPrElt> entry : formats.entrySet()) {
                if (entry.getKey() > startIndex) {
                    formats.put(startIndex, entry.getValue());
                    break;
                }
            }
        }
        formats.put(endIndex, fmt);
        final SortedMap<Integer, CTRPrElt> sub = formats.subMap(startIndex, endIndex);
        while (sub.size() > 1) {
            sub.remove(sub.lastKey());
        }
    }
    
    TreeMap<Integer, CTRPrElt> getFormatMap(final CTRst entry) {
        int length = 0;
        final TreeMap<Integer, CTRPrElt> formats = new TreeMap<Integer, CTRPrElt>();
        for (final CTRElt r : entry.getRArray()) {
            final String txt = r.getT();
            final CTRPrElt fmt = r.getRPr();
            length += txt.length();
            formats.put(length, fmt);
        }
        return formats;
    }
    
    CTRst buildCTRst(final String text, final TreeMap<Integer, CTRPrElt> formats) {
        if (text.length() != formats.lastKey()) {
            throw new IllegalArgumentException("Text length was " + text.length() + " but the last format index was " + formats.lastKey());
        }
        final CTRst stf = CTRst.Factory.newInstance();
        int runStartIdx = 0;
        for (final Map.Entry<Integer, CTRPrElt> me : formats.entrySet()) {
            final int runEndIdx = me.getKey();
            final CTRElt run = stf.addNewR();
            final String fragment = text.substring(runStartIdx, runEndIdx);
            run.setT(fragment);
            preserveSpaces(run.xgetT());
            final CTRPrElt fmt = me.getValue();
            if (fmt != null) {
                run.setRPr(fmt);
            }
            runStartIdx = runEndIdx;
        }
        return stf;
    }
    
    private ThemesTable getThemesTable() {
        if (this.styles == null) {
            return null;
        }
        return this.styles.getTheme();
    }
    
    static {
        utfPtrn = Pattern.compile("_x([0-9A-Fa-f]{4})_");
    }
}

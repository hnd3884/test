package com.lowagie.text.pdf;

import java.awt.Color;
import java.util.Iterator;
import com.lowagie.text.Font;
import com.lowagie.text.Utilities;
import java.util.Map;
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.lowagie.text.SplitCharacter;
import java.util.HashMap;

public class PdfChunk
{
    private static final char[] singleSpace;
    private static final PdfChunk[] thisChunk;
    private static final float ITALIC_ANGLE = 0.21256f;
    private static final HashMap keysAttributes;
    private static final HashMap keysNoStroke;
    protected String value;
    protected String encoding;
    protected PdfFont font;
    protected BaseFont baseFont;
    protected SplitCharacter splitCharacter;
    protected HashMap attributes;
    protected HashMap noStroke;
    protected boolean newlineSplit;
    protected Image image;
    protected float offsetX;
    protected float offsetY;
    protected boolean changeLeading;
    
    PdfChunk(final String string, final PdfChunk other) {
        this.value = "";
        this.encoding = "Cp1252";
        this.attributes = new HashMap();
        this.noStroke = new HashMap();
        this.changeLeading = false;
        PdfChunk.thisChunk[0] = this;
        this.value = string;
        this.font = other.font;
        this.attributes = other.attributes;
        this.noStroke = other.noStroke;
        this.baseFont = other.baseFont;
        final Object[] obj = this.attributes.get("IMAGE");
        if (obj == null) {
            this.image = null;
        }
        else {
            this.image = (Image)obj[0];
            this.offsetX = (float)obj[1];
            this.offsetY = (float)obj[2];
            this.changeLeading = (boolean)obj[3];
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = this.noStroke.get("SPLITCHARACTER");
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }
    
    PdfChunk(final Chunk chunk, final PdfAction action) {
        this.value = "";
        this.encoding = "Cp1252";
        this.attributes = new HashMap();
        this.noStroke = new HashMap();
        this.changeLeading = false;
        PdfChunk.thisChunk[0] = this;
        this.value = chunk.getContent();
        final Font f = chunk.getFont();
        float size = f.getSize();
        if (size == -1.0f) {
            size = 12.0f;
        }
        this.baseFont = f.getBaseFont();
        int style = f.getStyle();
        if (style == -1) {
            style = 0;
        }
        if (this.baseFont == null) {
            this.baseFont = f.getCalculatedBaseFont(false);
        }
        else {
            if ((style & 0x1) != 0x0) {
                this.attributes.put("TEXTRENDERMODE", new Object[] { new Integer(2), new Float(size / 30.0f), null });
            }
            if ((style & 0x2) != 0x0) {
                this.attributes.put("SKEW", new float[] { 0.0f, 0.21256f });
            }
        }
        this.font = new PdfFont(this.baseFont, size);
        final HashMap attr = chunk.getAttributes();
        if (attr != null) {
            for (final Map.Entry entry : attr.entrySet()) {
                final Object name = entry.getKey();
                if (PdfChunk.keysAttributes.containsKey(name)) {
                    this.attributes.put(name, entry.getValue());
                }
                else {
                    if (!PdfChunk.keysNoStroke.containsKey(name)) {
                        continue;
                    }
                    this.noStroke.put(name, entry.getValue());
                }
            }
            if ("".equals(attr.get("GENERICTAG"))) {
                this.attributes.put("GENERICTAG", chunk.getContent());
            }
        }
        if (f.isUnderlined()) {
            final Object[] obj = { null, { 0.0f, 0.06666667f, 0.0f, -0.33333334f, 0.0f } };
            final Object[][] unders = Utilities.addToArray(this.attributes.get("UNDERLINE"), obj);
            this.attributes.put("UNDERLINE", unders);
        }
        if (f.isStrikethru()) {
            final Object[] obj = { null, { 0.0f, 0.06666667f, 0.0f, 0.33333334f, 0.0f } };
            final Object[][] unders = Utilities.addToArray(this.attributes.get("UNDERLINE"), obj);
            this.attributes.put("UNDERLINE", unders);
        }
        if (action != null) {
            this.attributes.put("ACTION", action);
        }
        this.noStroke.put("COLOR", f.getColor());
        this.noStroke.put("ENCODING", this.font.getFont().getEncoding());
        final Object[] obj = this.attributes.get("IMAGE");
        if (obj == null) {
            this.image = null;
        }
        else {
            this.attributes.remove("HSCALE");
            this.image = (Image)obj[0];
            this.offsetX = (float)obj[1];
            this.offsetY = (float)obj[2];
            this.changeLeading = (boolean)obj[3];
        }
        this.font.setImage(this.image);
        final Float hs = this.attributes.get("HSCALE");
        if (hs != null) {
            this.font.setHorizontalScaling(hs);
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = this.noStroke.get("SPLITCHARACTER");
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }
    
    public int getUnicodeEquivalent(final int c) {
        return this.baseFont.getUnicodeEquivalent(c);
    }
    
    protected int getWord(final String text, int start) {
        for (int len = text.length(); start < len && Character.isLetter(text.charAt(start)); ++start) {}
        return start;
    }
    
    PdfChunk split(final float width) {
        this.newlineSplit = false;
        if (this.image != null) {
            if (this.image.getScaledWidth() > width) {
                final PdfChunk pc = new PdfChunk("\ufffc", this);
                this.value = "";
                this.attributes = new HashMap();
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pc;
            }
            return null;
        }
        else {
            final HyphenationEvent hyphenationEvent = this.noStroke.get("HYPHENATION");
            int currentPosition = 0;
            int splitPosition = -1;
            float currentWidth = 0.0f;
            int lastSpace = -1;
            float lastSpaceWidth = 0.0f;
            final int length = this.value.length();
            final char[] valueArray = this.value.toCharArray();
            char character = '\0';
            final BaseFont ft = this.font.getFont();
            boolean surrogate = false;
            if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
                while (currentPosition < length) {
                    final char cidChar = valueArray[currentPosition];
                    character = (char)ft.getUnicodeEquivalent(cidChar);
                    if (character == '\n') {
                        this.newlineSplit = true;
                        final String returnValue = this.value.substring(currentPosition + 1);
                        this.value = this.value.substring(0, currentPosition);
                        if (this.value.length() < 1) {
                            this.value = "\u0001";
                        }
                        final PdfChunk pc2 = new PdfChunk(returnValue, this);
                        return pc2;
                    }
                    currentWidth += this.getCharWidth(cidChar);
                    if (character == ' ') {
                        lastSpace = currentPosition + 1;
                        lastSpaceWidth = currentWidth;
                    }
                    if (currentWidth > width) {
                        break;
                    }
                    if (this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, PdfChunk.thisChunk)) {
                        splitPosition = currentPosition + 1;
                    }
                    ++currentPosition;
                }
            }
            else {
                while (currentPosition < length) {
                    character = valueArray[currentPosition];
                    if (character == '\r' || character == '\n') {
                        this.newlineSplit = true;
                        int inc = 1;
                        if (character == '\r' && currentPosition + 1 < length && valueArray[currentPosition + 1] == '\n') {
                            inc = 2;
                        }
                        final String returnValue = this.value.substring(currentPosition + inc);
                        this.value = this.value.substring(0, currentPosition);
                        if (this.value.length() < 1) {
                            this.value = " ";
                        }
                        final PdfChunk pc2 = new PdfChunk(returnValue, this);
                        return pc2;
                    }
                    surrogate = Utilities.isSurrogatePair(valueArray, currentPosition);
                    if (surrogate) {
                        currentWidth += this.getCharWidth(Utilities.convertToUtf32(valueArray[currentPosition], valueArray[currentPosition + 1]));
                    }
                    else {
                        currentWidth += this.getCharWidth(character);
                    }
                    if (character == ' ') {
                        lastSpace = currentPosition + 1;
                        lastSpaceWidth = currentWidth;
                    }
                    if (surrogate) {
                        ++currentPosition;
                    }
                    if (currentWidth > width) {
                        break;
                    }
                    if (this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, null)) {
                        splitPosition = currentPosition + 1;
                    }
                    ++currentPosition;
                }
            }
            if (currentPosition == length) {
                return null;
            }
            if (splitPosition < 0) {
                final String returnValue2 = this.value;
                this.value = "";
                final PdfChunk pc3 = new PdfChunk(returnValue2, this);
                return pc3;
            }
            if (lastSpace > splitPosition && this.splitCharacter.isSplitCharacter(0, 0, 1, PdfChunk.singleSpace, null)) {
                splitPosition = lastSpace;
            }
            if (hyphenationEvent != null && lastSpace >= 0 && lastSpace < currentPosition) {
                final int wordIdx = this.getWord(this.value, lastSpace);
                if (wordIdx > lastSpace) {
                    final String pre = hyphenationEvent.getHyphenatedWordPre(this.value.substring(lastSpace, wordIdx), this.font.getFont(), this.font.size(), width - lastSpaceWidth);
                    final String post = hyphenationEvent.getHyphenatedWordPost();
                    if (pre.length() > 0) {
                        final String returnValue3 = post + this.value.substring(wordIdx);
                        this.value = this.trim(this.value.substring(0, lastSpace) + pre);
                        final PdfChunk pc4 = new PdfChunk(returnValue3, this);
                        return pc4;
                    }
                }
            }
            final String returnValue2 = this.value.substring(splitPosition);
            this.value = this.trim(this.value.substring(0, splitPosition));
            final PdfChunk pc3 = new PdfChunk(returnValue2, this);
            return pc3;
        }
    }
    
    PdfChunk truncate(final float width) {
        if (this.image != null) {
            if (this.image.getScaledWidth() > width) {
                final PdfChunk pc = new PdfChunk("", this);
                this.value = "";
                this.attributes.remove("IMAGE");
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pc;
            }
            return null;
        }
        else {
            int currentPosition = 0;
            float currentWidth = 0.0f;
            if (width < this.font.width()) {
                final String returnValue = this.value.substring(1);
                this.value = this.value.substring(0, 1);
                final PdfChunk pc2 = new PdfChunk(returnValue, this);
                return pc2;
            }
            final int length = this.value.length();
            boolean surrogate = false;
            while (currentPosition < length) {
                surrogate = Utilities.isSurrogatePair(this.value, currentPosition);
                if (surrogate) {
                    currentWidth += this.getCharWidth(Utilities.convertToUtf32(this.value, currentPosition));
                }
                else {
                    currentWidth += this.getCharWidth(this.value.charAt(currentPosition));
                }
                if (currentWidth > width) {
                    break;
                }
                if (surrogate) {
                    ++currentPosition;
                }
                ++currentPosition;
            }
            if (currentPosition == length) {
                return null;
            }
            if (currentPosition == 0) {
                currentPosition = 1;
                if (surrogate) {
                    ++currentPosition;
                }
            }
            final String returnValue2 = this.value.substring(currentPosition);
            this.value = this.value.substring(0, currentPosition);
            final PdfChunk pc3 = new PdfChunk(returnValue2, this);
            return pc3;
        }
    }
    
    PdfFont font() {
        return this.font;
    }
    
    Color color() {
        return this.noStroke.get("COLOR");
    }
    
    float width() {
        if (this.isAttribute("CHAR_SPACING")) {
            final Float cs = (Float)this.getAttribute("CHAR_SPACING");
            return this.font.width(this.value) + this.value.length() * cs;
        }
        return this.font.width(this.value);
    }
    
    public boolean isNewlineSplit() {
        return this.newlineSplit;
    }
    
    public float getWidthCorrected(final float charSpacing, final float wordSpacing) {
        if (this.image != null) {
            return this.image.getScaledWidth() + charSpacing;
        }
        int numberOfSpaces = 0;
        int idx = -1;
        while ((idx = this.value.indexOf(32, idx + 1)) >= 0) {
            ++numberOfSpaces;
        }
        return this.width() + (this.value.length() * charSpacing + numberOfSpaces * wordSpacing);
    }
    
    public float getTextRise() {
        final Float f = (Float)this.getAttribute("SUBSUPSCRIPT");
        if (f != null) {
            return f;
        }
        return 0.0f;
    }
    
    public float trimLastSpace() {
        final BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.endsWith("\u0001")) {
                this.value = this.value.substring(0, this.value.length() - 1);
                return this.font.width(1);
            }
        }
        else if (this.value.length() > 1 && this.value.endsWith(" ")) {
            this.value = this.value.substring(0, this.value.length() - 1);
            return this.font.width(32);
        }
        return 0.0f;
    }
    
    public float trimFirstSpace() {
        final BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.startsWith("\u0001")) {
                this.value = this.value.substring(1);
                return this.font.width(1);
            }
        }
        else if (this.value.length() > 1 && this.value.startsWith(" ")) {
            this.value = this.value.substring(1);
            return this.font.width(32);
        }
        return 0.0f;
    }
    
    Object getAttribute(final String name) {
        if (this.attributes.containsKey(name)) {
            return this.attributes.get(name);
        }
        return this.noStroke.get(name);
    }
    
    boolean isAttribute(final String name) {
        return this.attributes.containsKey(name) || this.noStroke.containsKey(name);
    }
    
    boolean isStroked() {
        return !this.attributes.isEmpty();
    }
    
    boolean isSeparator() {
        return this.isAttribute("SEPARATOR");
    }
    
    boolean isHorizontalSeparator() {
        if (this.isAttribute("SEPARATOR")) {
            final Object[] o = (Object[])this.getAttribute("SEPARATOR");
            return !(boolean)o[1];
        }
        return false;
    }
    
    boolean isTab() {
        return this.isAttribute("TAB");
    }
    
    void adjustLeft(final float newValue) {
        final Object[] o = this.attributes.get("TAB");
        if (o != null) {
            this.attributes.put("TAB", new Object[] { o[0], o[1], o[2], new Float(newValue) });
        }
    }
    
    boolean isImage() {
        return this.image != null;
    }
    
    Image getImage() {
        return this.image;
    }
    
    void setImageOffsetX(final float offsetX) {
        this.offsetX = offsetX;
    }
    
    float getImageOffsetX() {
        return this.offsetX;
    }
    
    void setImageOffsetY(final float offsetY) {
        this.offsetY = offsetY;
    }
    
    float getImageOffsetY() {
        return this.offsetY;
    }
    
    void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    boolean isSpecialEncoding() {
        return this.encoding.equals("UnicodeBigUnmarked") || this.encoding.equals("Identity-H");
    }
    
    String getEncoding() {
        return this.encoding;
    }
    
    int length() {
        return this.value.length();
    }
    
    int lengthUtf32() {
        if (!"Identity-H".equals(this.encoding)) {
            return this.value.length();
        }
        int total = 0;
        for (int len = this.value.length(), k = 0; k < len; ++k) {
            if (Utilities.isSurrogateHigh(this.value.charAt(k))) {
                ++k;
            }
            ++total;
        }
        return total;
    }
    
    boolean isExtSplitCharacter(final int start, final int current, final int end, final char[] cc, final PdfChunk[] ck) {
        return this.splitCharacter.isSplitCharacter(start, current, end, cc, ck);
    }
    
    String trim(String string) {
        final BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            while (string.endsWith("\u0001")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        else {
            while (string.endsWith(" ") || string.endsWith("\t")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }
    
    public boolean changeLeading() {
        return this.changeLeading;
    }
    
    float getCharWidth(final int c) {
        if (noPrint(c)) {
            return 0.0f;
        }
        if (this.isAttribute("CHAR_SPACING")) {
            final Float cs = (Float)this.getAttribute("CHAR_SPACING");
            return this.font.width(c) + cs;
        }
        return this.font.width(c);
    }
    
    public static boolean noPrint(final int c) {
        return (c >= 8203 && c <= 8207) || (c >= 8234 && c <= 8238);
    }
    
    static {
        singleSpace = new char[] { ' ' };
        thisChunk = new PdfChunk[1];
        keysAttributes = new HashMap();
        keysNoStroke = new HashMap();
        PdfChunk.keysAttributes.put("ACTION", null);
        PdfChunk.keysAttributes.put("UNDERLINE", null);
        PdfChunk.keysAttributes.put("REMOTEGOTO", null);
        PdfChunk.keysAttributes.put("LOCALGOTO", null);
        PdfChunk.keysAttributes.put("LOCALDESTINATION", null);
        PdfChunk.keysAttributes.put("GENERICTAG", null);
        PdfChunk.keysAttributes.put("NEWPAGE", null);
        PdfChunk.keysAttributes.put("IMAGE", null);
        PdfChunk.keysAttributes.put("BACKGROUND", null);
        PdfChunk.keysAttributes.put("PDFANNOTATION", null);
        PdfChunk.keysAttributes.put("SKEW", null);
        PdfChunk.keysAttributes.put("HSCALE", null);
        PdfChunk.keysAttributes.put("SEPARATOR", null);
        PdfChunk.keysAttributes.put("TAB", null);
        PdfChunk.keysAttributes.put("CHAR_SPACING", null);
        PdfChunk.keysNoStroke.put("SUBSUPSCRIPT", null);
        PdfChunk.keysNoStroke.put("SPLITCHARACTER", null);
        PdfChunk.keysNoStroke.put("HYPHENATION", null);
        PdfChunk.keysNoStroke.put("TEXTRENDERMODE", null);
    }
}

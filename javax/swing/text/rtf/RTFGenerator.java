package javax.swing.text.rtf;

import java.util.Vector;
import javax.swing.text.BadLocationException;
import javax.swing.text.TabStop;
import java.util.Enumeration;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.util.Hashtable;
import java.io.IOException;
import javax.swing.text.Element;
import javax.swing.text.Document;
import java.awt.Color;
import javax.swing.text.Segment;
import javax.swing.text.MutableAttributeSet;
import java.io.OutputStream;
import javax.swing.text.AttributeSet;
import java.util.Dictionary;

class RTFGenerator
{
    Dictionary<Object, Integer> colorTable;
    int colorCount;
    Dictionary<String, Integer> fontTable;
    int fontCount;
    Dictionary<AttributeSet, Integer> styleTable;
    int styleCount;
    OutputStream outputStream;
    boolean afterKeyword;
    MutableAttributeSet outputAttributes;
    int unicodeCount;
    private Segment workingSegment;
    int[] outputConversion;
    public static final Color defaultRTFColor;
    public static final float defaultFontSize = 12.0f;
    public static final String defaultFontFamily = "Helvetica";
    private static final Object MagicToken;
    protected static CharacterKeywordPair[] textKeywords;
    static final char[] hexdigits;
    
    public static void writeDocument(final Document document, final OutputStream outputStream) throws IOException {
        final RTFGenerator rtfGenerator = new RTFGenerator(outputStream);
        final Element defaultRootElement = document.getDefaultRootElement();
        rtfGenerator.examineElement(defaultRootElement);
        rtfGenerator.writeRTFHeader();
        rtfGenerator.writeDocumentProperties(document);
        for (int elementCount = defaultRootElement.getElementCount(), i = 0; i < elementCount; ++i) {
            rtfGenerator.writeParagraphElement(defaultRootElement.getElement(i));
        }
        rtfGenerator.writeRTFTrailer();
    }
    
    public RTFGenerator(final OutputStream outputStream) {
        (this.colorTable = new Hashtable<Object, Integer>()).put(RTFGenerator.defaultRTFColor, 0);
        this.colorCount = 1;
        this.fontTable = new Hashtable<String, Integer>();
        this.fontCount = 0;
        this.styleTable = new Hashtable<AttributeSet, Integer>();
        this.styleCount = 0;
        this.workingSegment = new Segment();
        this.outputStream = outputStream;
        this.unicodeCount = 1;
    }
    
    public void examineElement(final Element element) {
        final AttributeSet attributes = element.getAttributes();
        this.tallyStyles(attributes);
        if (attributes != null) {
            final Color foreground = StyleConstants.getForeground(attributes);
            if (foreground != null && this.colorTable.get(foreground) == null) {
                this.colorTable.put(foreground, new Integer(this.colorCount));
                ++this.colorCount;
            }
            final Object attribute = attributes.getAttribute(StyleConstants.Background);
            if (attribute != null && this.colorTable.get(attribute) == null) {
                this.colorTable.put(attribute, new Integer(this.colorCount));
                ++this.colorCount;
            }
            String fontFamily = StyleConstants.getFontFamily(attributes);
            if (fontFamily == null) {
                fontFamily = "Helvetica";
            }
            if (fontFamily != null && this.fontTable.get(fontFamily) == null) {
                this.fontTable.put(fontFamily, new Integer(this.fontCount));
                ++this.fontCount;
            }
        }
        for (int elementCount = element.getElementCount(), i = 0; i < elementCount; ++i) {
            this.examineElement(element.getElement(i));
        }
    }
    
    private void tallyStyles(AttributeSet resolveParent) {
        while (resolveParent != null) {
            if (resolveParent instanceof Style && this.styleTable.get(resolveParent) == null) {
                ++this.styleCount;
                this.styleTable.put(resolveParent, new Integer(this.styleCount));
            }
            resolveParent = resolveParent.getResolveParent();
        }
    }
    
    private Style findStyle(AttributeSet resolveParent) {
        while (resolveParent != null) {
            if (resolveParent instanceof Style && this.styleTable.get(resolveParent) != null) {
                return (Style)resolveParent;
            }
            resolveParent = resolveParent.getResolveParent();
        }
        return null;
    }
    
    private Integer findStyleNumber(AttributeSet resolveParent, final String s) {
        while (resolveParent != null) {
            if (resolveParent instanceof Style) {
                final Integer n = this.styleTable.get(resolveParent);
                if (n != null && (s == null || s.equals(resolveParent.getAttribute("style:type")))) {
                    return n;
                }
            }
            resolveParent = resolveParent.getResolveParent();
        }
        return null;
    }
    
    private static Object attrDiff(final MutableAttributeSet set, final AttributeSet set2, final Object o, final Object o2) {
        final Object attribute = set.getAttribute(o);
        final Object attribute2 = set2.getAttribute(o);
        if (attribute2 == attribute) {
            return null;
        }
        if (attribute2 == null) {
            set.removeAttribute(o);
            if (o2 != null && !o2.equals(attribute)) {
                return o2;
            }
            return null;
        }
        else {
            if (attribute == null || !equalArraysOK(attribute, attribute2)) {
                set.addAttribute(o, attribute2);
                return attribute2;
            }
            return null;
        }
    }
    
    private static boolean equalArraysOK(final Object o, final Object o2) {
        if (o == o2) {
            return true;
        }
        if (o == null || o2 == null) {
            return false;
        }
        if (o.equals(o2)) {
            return true;
        }
        if (!o.getClass().isArray() || !o2.getClass().isArray()) {
            return false;
        }
        final Object[] array = (Object[])o;
        final Object[] array2 = (Object[])o2;
        if (array.length != array2.length) {
            return false;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            if (!equalArraysOK(array[i], array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    public void writeLineBreak() throws IOException {
        this.writeRawString("\n");
        this.afterKeyword = false;
    }
    
    public void writeRTFHeader() throws IOException {
        this.writeBegingroup();
        this.writeControlWord("rtf", 1);
        this.writeControlWord("ansi");
        this.outputConversion = outputConversionForName("ansi");
        this.writeLineBreak();
        final String[] array = new String[this.fontCount];
        final Enumeration<String> keys = this.fontTable.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            array[this.fontTable.get(s)] = s;
        }
        this.writeBegingroup();
        this.writeControlWord("fonttbl");
        for (int i = 0; i < this.fontCount; ++i) {
            this.writeControlWord("f", i);
            this.writeControlWord("fnil");
            this.writeText(array[i]);
            this.writeText(";");
        }
        this.writeEndgroup();
        this.writeLineBreak();
        if (this.colorCount > 1) {
            final Color[] array2 = new Color[this.colorCount];
            final Enumeration<Object> keys2 = this.colorTable.keys();
            while (keys2.hasMoreElements()) {
                final Color color = keys2.nextElement();
                array2[this.colorTable.get(color)] = color;
            }
            this.writeBegingroup();
            this.writeControlWord("colortbl");
            for (int j = 0; j < this.colorCount; ++j) {
                final Color color2 = array2[j];
                if (color2 != null) {
                    this.writeControlWord("red", color2.getRed());
                    this.writeControlWord("green", color2.getGreen());
                    this.writeControlWord("blue", color2.getBlue());
                }
                this.writeRawString(";");
            }
            this.writeEndgroup();
            this.writeLineBreak();
        }
        if (this.styleCount > 1) {
            this.writeBegingroup();
            this.writeControlWord("stylesheet");
            final Enumeration<AttributeSet> keys3 = this.styleTable.keys();
            while (keys3.hasMoreElements()) {
                final Style style = keys3.nextElement();
                final int intValue = this.styleTable.get(style);
                this.writeBegingroup();
                String s2 = (String)style.getAttribute("style:type");
                if (s2 == null) {
                    s2 = "paragraph";
                }
                if (s2.equals("character")) {
                    this.writeControlWord("*");
                    this.writeControlWord("cs", intValue);
                }
                else if (s2.equals("section")) {
                    this.writeControlWord("*");
                    this.writeControlWord("ds", intValue);
                }
                else {
                    this.writeControlWord("s", intValue);
                }
                final AttributeSet resolveParent = style.getResolveParent();
                SimpleAttributeSet set;
                if (resolveParent == null) {
                    set = new SimpleAttributeSet();
                }
                else {
                    set = new SimpleAttributeSet(resolveParent);
                }
                this.updateSectionAttributes(set, style, false);
                this.updateParagraphAttributes(set, style, false);
                this.updateCharacterAttributes(set, style, false);
                final AttributeSet resolveParent2 = style.getResolveParent();
                if (resolveParent2 != null && resolveParent2 instanceof Style) {
                    final Integer n = this.styleTable.get(resolveParent2);
                    if (n != null) {
                        this.writeControlWord("sbasedon", n);
                    }
                }
                final Style style2 = (Style)style.getAttribute("style:nextStyle");
                if (style2 != null) {
                    final Integer n2 = this.styleTable.get(style2);
                    if (n2 != null) {
                        this.writeControlWord("snext", n2);
                    }
                }
                final Boolean b = (Boolean)style.getAttribute("style:hidden");
                if (b != null && b) {
                    this.writeControlWord("shidden");
                }
                final Boolean b2 = (Boolean)style.getAttribute("style:additive");
                if (b2 != null && b2) {
                    this.writeControlWord("additive");
                }
                this.writeText(style.getName());
                this.writeText(";");
                this.writeEndgroup();
            }
            this.writeEndgroup();
            this.writeLineBreak();
        }
        this.outputAttributes = new SimpleAttributeSet();
    }
    
    void writeDocumentProperties(final Document document) throws IOException {
        boolean b = false;
        for (int i = 0; i < RTFAttributes.attributes.length; ++i) {
            final RTFAttribute rtfAttribute = RTFAttributes.attributes[i];
            if (rtfAttribute.domain() == 3) {
                if (rtfAttribute.writeValue(document.getProperty(rtfAttribute.swingName()), this, false)) {
                    b = true;
                }
            }
        }
        if (b) {
            this.writeLineBreak();
        }
    }
    
    public void writeRTFTrailer() throws IOException {
        this.writeEndgroup();
        this.writeLineBreak();
    }
    
    protected void checkNumericControlWord(final MutableAttributeSet set, final AttributeSet set2, final Object o, final String s, final float n, final float n2) throws IOException {
        final Object attrDiff;
        if ((attrDiff = attrDiff(set, set2, o, RTFGenerator.MagicToken)) != null) {
            float floatValue;
            if (attrDiff == RTFGenerator.MagicToken) {
                floatValue = n;
            }
            else {
                floatValue = ((Number)attrDiff).floatValue();
            }
            this.writeControlWord(s, Math.round(floatValue * n2));
        }
    }
    
    protected void checkControlWord(final MutableAttributeSet set, final AttributeSet set2, final RTFAttribute rtfAttribute) throws IOException {
        Object attrDiff;
        if ((attrDiff = attrDiff(set, set2, rtfAttribute.swingName(), RTFGenerator.MagicToken)) != null) {
            if (attrDiff == RTFGenerator.MagicToken) {
                attrDiff = null;
            }
            rtfAttribute.writeValue(attrDiff, this, true);
        }
    }
    
    protected void checkControlWords(final MutableAttributeSet set, final AttributeSet set2, final RTFAttribute[] array, final int n) throws IOException {
        for (final RTFAttribute rtfAttribute : array) {
            if (rtfAttribute.domain() == n) {
                this.checkControlWord(set, set2, rtfAttribute);
            }
        }
    }
    
    void updateSectionAttributes(final MutableAttributeSet set, final AttributeSet set2, final boolean b) throws IOException {
        if (b) {
            final Object attribute = set.getAttribute("sectionStyle");
            final Integer styleNumber = this.findStyleNumber(set2, "section");
            if (attribute != styleNumber) {
                if (attribute != null) {
                    this.resetSectionAttributes(set);
                }
                if (styleNumber != null) {
                    this.writeControlWord("ds", styleNumber);
                    set.addAttribute("sectionStyle", styleNumber);
                }
                else {
                    set.removeAttribute("sectionStyle");
                }
            }
        }
        this.checkControlWords(set, set2, RTFAttributes.attributes, 2);
    }
    
    protected void resetSectionAttributes(final MutableAttributeSet default1) throws IOException {
        this.writeControlWord("sectd");
        for (int length = RTFAttributes.attributes.length, i = 0; i < length; ++i) {
            final RTFAttribute rtfAttribute = RTFAttributes.attributes[i];
            if (rtfAttribute.domain() == 2) {
                rtfAttribute.setDefault(default1);
            }
        }
        default1.removeAttribute("sectionStyle");
    }
    
    void updateParagraphAttributes(final MutableAttributeSet set, final AttributeSet set2, final boolean b) throws IOException {
        Object attribute;
        Integer styleNumber;
        if (b) {
            attribute = set.getAttribute("paragraphStyle");
            styleNumber = this.findStyleNumber(set2, "paragraph");
            if (attribute != styleNumber && attribute != null) {
                this.resetParagraphAttributes(set);
                attribute = null;
            }
        }
        else {
            attribute = null;
            styleNumber = null;
        }
        Object attribute2 = set.getAttribute("tabs");
        final Object attribute3 = set2.getAttribute("tabs");
        if (attribute2 != attribute3 && attribute2 != null) {
            this.resetParagraphAttributes(set);
            attribute2 = null;
            attribute = null;
        }
        if (attribute != styleNumber && styleNumber != null) {
            this.writeControlWord("s", styleNumber);
            set.addAttribute("paragraphStyle", styleNumber);
        }
        this.checkControlWords(set, set2, RTFAttributes.attributes, 1);
        if (attribute2 != attribute3 && attribute3 != null) {
            final TabStop[] array = (TabStop[])attribute3;
            for (int i = 0; i < array.length; ++i) {
                final TabStop tabStop = array[i];
                switch (tabStop.getAlignment()) {
                    case 1: {
                        this.writeControlWord("tqr");
                        break;
                    }
                    case 2: {
                        this.writeControlWord("tqc");
                        break;
                    }
                    case 4: {
                        this.writeControlWord("tqdec");
                        break;
                    }
                }
                switch (tabStop.getLeader()) {
                    case 1: {
                        this.writeControlWord("tldot");
                        break;
                    }
                    case 2: {
                        this.writeControlWord("tlhyph");
                        break;
                    }
                    case 3: {
                        this.writeControlWord("tlul");
                        break;
                    }
                    case 4: {
                        this.writeControlWord("tlth");
                        break;
                    }
                    case 5: {
                        this.writeControlWord("tleq");
                        break;
                    }
                }
                final int round = Math.round(20.0f * tabStop.getPosition());
                if (tabStop.getAlignment() == 5) {
                    this.writeControlWord("tb", round);
                }
                else {
                    this.writeControlWord("tx", round);
                }
            }
            set.addAttribute("tabs", array);
        }
    }
    
    public void writeParagraphElement(final Element element) throws IOException {
        this.updateParagraphAttributes(this.outputAttributes, element.getAttributes(), true);
        for (int elementCount = element.getElementCount(), i = 0; i < elementCount; ++i) {
            this.writeTextElement(element.getElement(i));
        }
        this.writeControlWord("par");
        this.writeLineBreak();
    }
    
    protected void resetParagraphAttributes(final MutableAttributeSet default1) throws IOException {
        this.writeControlWord("pard");
        default1.addAttribute(StyleConstants.Alignment, 0);
        for (int length = RTFAttributes.attributes.length, i = 0; i < length; ++i) {
            final RTFAttribute rtfAttribute = RTFAttributes.attributes[i];
            if (rtfAttribute.domain() == 1) {
                rtfAttribute.setDefault(default1);
            }
        }
        default1.removeAttribute("paragraphStyle");
        default1.removeAttribute("tabs");
    }
    
    void updateCharacterAttributes(final MutableAttributeSet set, final AttributeSet set2, final boolean b) throws IOException {
        if (b) {
            final Object attribute = set.getAttribute("characterStyle");
            final Integer styleNumber = this.findStyleNumber(set2, "character");
            if (attribute != styleNumber) {
                if (attribute != null) {
                    this.resetCharacterAttributes(set);
                }
                if (styleNumber != null) {
                    this.writeControlWord("cs", styleNumber);
                    set.addAttribute("characterStyle", styleNumber);
                }
                else {
                    set.removeAttribute("characterStyle");
                }
            }
        }
        final Object attrDiff;
        if ((attrDiff = attrDiff(set, set2, StyleConstants.FontFamily, null)) != null) {
            this.writeControlWord("f", this.fontTable.get(attrDiff));
        }
        this.checkNumericControlWord(set, set2, StyleConstants.FontSize, "fs", 12.0f, 2.0f);
        this.checkControlWords(set, set2, RTFAttributes.attributes, 0);
        this.checkNumericControlWord(set, set2, StyleConstants.LineSpacing, "sl", 0.0f, 20.0f);
        final Object attrDiff2;
        if ((attrDiff2 = attrDiff(set, set2, StyleConstants.Background, RTFGenerator.MagicToken)) != null) {
            int intValue;
            if (attrDiff2 == RTFGenerator.MagicToken) {
                intValue = 0;
            }
            else {
                intValue = this.colorTable.get(attrDiff2);
            }
            this.writeControlWord("cb", intValue);
        }
        final Object attrDiff3;
        if ((attrDiff3 = attrDiff(set, set2, StyleConstants.Foreground, null)) != null) {
            int intValue2;
            if (attrDiff3 == RTFGenerator.MagicToken) {
                intValue2 = 0;
            }
            else {
                intValue2 = this.colorTable.get(attrDiff3);
            }
            this.writeControlWord("cf", intValue2);
        }
    }
    
    protected void resetCharacterAttributes(final MutableAttributeSet default1) throws IOException {
        this.writeControlWord("plain");
        for (int length = RTFAttributes.attributes.length, i = 0; i < length; ++i) {
            final RTFAttribute rtfAttribute = RTFAttributes.attributes[i];
            if (rtfAttribute.domain() == 0) {
                rtfAttribute.setDefault(default1);
            }
        }
        StyleConstants.setFontFamily(default1, "Helvetica");
        default1.removeAttribute(StyleConstants.FontSize);
        default1.removeAttribute(StyleConstants.Background);
        default1.removeAttribute(StyleConstants.Foreground);
        default1.removeAttribute(StyleConstants.LineSpacing);
        default1.removeAttribute("characterStyle");
    }
    
    public void writeTextElement(final Element element) throws IOException {
        this.updateCharacterAttributes(this.outputAttributes, element.getAttributes(), true);
        if (element.isLeaf()) {
            try {
                element.getDocument().getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset(), this.workingSegment);
            }
            catch (final BadLocationException ex) {
                ex.printStackTrace();
                throw new InternalError(ex.getMessage());
            }
            this.writeText(this.workingSegment);
        }
        else {
            for (int elementCount = element.getElementCount(), i = 0; i < elementCount; ++i) {
                this.writeTextElement(element.getElement(i));
            }
        }
    }
    
    public void writeText(final Segment segment) throws IOException {
        int i = segment.offset;
        final int n = i + segment.count;
        final char[] array = segment.array;
        while (i < n) {
            this.writeCharacter(array[i]);
            ++i;
        }
    }
    
    public void writeText(final String s) throws IOException {
        for (int i = 0; i < s.length(); ++i) {
            this.writeCharacter(s.charAt(i));
        }
    }
    
    public void writeRawString(final String s) throws IOException {
        for (int length = s.length(), i = 0; i < length; ++i) {
            this.outputStream.write(s.charAt(i));
        }
    }
    
    public void writeControlWord(final String s) throws IOException {
        this.outputStream.write(92);
        this.writeRawString(s);
        this.afterKeyword = true;
    }
    
    public void writeControlWord(final String s, final int n) throws IOException {
        this.outputStream.write(92);
        this.writeRawString(s);
        this.writeRawString(String.valueOf(n));
        this.afterKeyword = true;
    }
    
    public void writeBegingroup() throws IOException {
        this.outputStream.write(123);
        this.afterKeyword = false;
    }
    
    public void writeEndgroup() throws IOException {
        this.outputStream.write(125);
        this.afterKeyword = false;
    }
    
    public void writeCharacter(final char c) throws IOException {
        if (c == ' ') {
            this.outputStream.write(92);
            this.outputStream.write(126);
            this.afterKeyword = false;
            return;
        }
        if (c == '\t') {
            this.writeControlWord("tab");
            return;
        }
        if (c == '\n' || c == '\r') {
            return;
        }
        final int convertCharacter = convertCharacter(this.outputConversion, c);
        if (convertCharacter == 0) {
            for (int i = 0; i < RTFGenerator.textKeywords.length; ++i) {
                if (RTFGenerator.textKeywords[i].character == c) {
                    this.writeControlWord(RTFGenerator.textKeywords[i].keyword);
                    return;
                }
            }
            final String approximationForUnicode = this.approximationForUnicode(c);
            if (approximationForUnicode.length() != this.unicodeCount) {
                this.writeControlWord("uc", this.unicodeCount = approximationForUnicode.length());
            }
            this.writeControlWord("u", c);
            this.writeRawString(" ");
            this.writeRawString(approximationForUnicode);
            this.afterKeyword = false;
            return;
        }
        if (convertCharacter > 127) {
            this.outputStream.write(92);
            this.outputStream.write(39);
            this.outputStream.write(RTFGenerator.hexdigits[(convertCharacter & 0xF0) >>> 4]);
            this.outputStream.write(RTFGenerator.hexdigits[convertCharacter & 0xF]);
            this.afterKeyword = false;
            return;
        }
        switch (convertCharacter) {
            case 92:
            case 123:
            case 125: {
                this.outputStream.write(92);
                this.afterKeyword = false;
                break;
            }
        }
        if (this.afterKeyword) {
            this.outputStream.write(32);
            this.afterKeyword = false;
        }
        this.outputStream.write(convertCharacter);
    }
    
    String approximationForUnicode(final char c) {
        return "?";
    }
    
    static int[] outputConversionFromTranslationTable(final char[] array) {
        final int[] array2 = new int[2 * array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i * 2] = array[i];
            array2[i * 2 + 1] = i;
        }
        return array2;
    }
    
    static int[] outputConversionForName(final String s) throws IOException {
        return outputConversionFromTranslationTable((char[])RTFReader.getCharacterSet(s));
    }
    
    protected static int convertCharacter(final int[] array, final char c) {
        for (int i = 0; i < array.length; i += 2) {
            if (array[i] == c) {
                return array[i + 1];
            }
        }
        return 0;
    }
    
    static {
        defaultRTFColor = Color.black;
        MagicToken = new Object();
        final Dictionary<String, String> textKeywords = RTFReader.textKeywords;
        final Enumeration<String> keys = textKeywords.keys();
        final Vector vector = new Vector();
        while (keys.hasMoreElements()) {
            final CharacterKeywordPair characterKeywordPair = new CharacterKeywordPair();
            characterKeywordPair.keyword = keys.nextElement();
            characterKeywordPair.character = textKeywords.get(characterKeywordPair.keyword).charAt(0);
            vector.addElement(characterKeywordPair);
        }
        vector.copyInto(RTFGenerator.textKeywords = new CharacterKeywordPair[vector.size()]);
        hexdigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    static class CharacterKeywordPair
    {
        public char character;
        public String keyword;
    }
}

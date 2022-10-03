package javax.swing.text.rtf;

import javax.swing.text.BadLocationException;
import javax.swing.text.TabStop;
import javax.swing.text.StyleConstants;
import javax.swing.text.AttributeSet;
import java.util.Vector;
import java.net.URL;
import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.text.SimpleAttributeSet;
import java.util.Hashtable;
import javax.swing.text.Style;
import java.awt.Color;
import javax.swing.text.MutableAttributeSet;
import java.util.Dictionary;
import javax.swing.text.StyledDocument;

class RTFReader extends RTFParser
{
    StyledDocument target;
    Dictionary<Object, Object> parserState;
    Destination rtfDestination;
    MutableAttributeSet documentAttributes;
    Dictionary<Integer, String> fontTable;
    Color[] colorTable;
    Style[] characterStyles;
    Style[] paragraphStyles;
    Style[] sectionStyles;
    int rtfversion;
    boolean ignoreGroupIfUnknownKeyword;
    int skippingCharacters;
    private static Dictionary<String, RTFAttribute> straightforwardAttributes;
    private MockAttributeSet mockery;
    static Dictionary<String, String> textKeywords;
    static final String TabAlignmentKey = "tab_alignment";
    static final String TabLeaderKey = "tab_leader";
    static Dictionary<String, char[]> characterSets;
    static boolean useNeXTForAnsi;
    
    public RTFReader(final StyledDocument target) {
        this.target = target;
        this.parserState = new Hashtable<Object, Object>();
        this.fontTable = new Hashtable<Integer, String>();
        this.rtfversion = -1;
        this.mockery = new MockAttributeSet();
        this.documentAttributes = new SimpleAttributeSet();
    }
    
    @Override
    public void handleBinaryBlob(final byte[] array) {
        if (this.skippingCharacters > 0) {
            --this.skippingCharacters;
        }
    }
    
    @Override
    public void handleText(String substring) {
        if (this.skippingCharacters > 0) {
            if (this.skippingCharacters >= substring.length()) {
                this.skippingCharacters -= substring.length();
                return;
            }
            substring = substring.substring(this.skippingCharacters);
            this.skippingCharacters = 0;
        }
        if (this.rtfDestination != null) {
            this.rtfDestination.handleText(substring);
            return;
        }
        this.warning("Text with no destination. oops.");
    }
    
    Color defaultColor() {
        return Color.black;
    }
    
    @Override
    public void begingroup() {
        if (this.skippingCharacters > 0) {
            this.skippingCharacters = 0;
        }
        final Object value = this.parserState.get("_savedState");
        if (value != null) {
            this.parserState.remove("_savedState");
        }
        final Dictionary dictionary = (Dictionary)((Hashtable)this.parserState).clone();
        if (value != null) {
            dictionary.put("_savedState", value);
        }
        this.parserState.put("_savedState", dictionary);
        if (this.rtfDestination != null) {
            this.rtfDestination.begingroup();
        }
    }
    
    @Override
    public void endgroup() {
        if (this.skippingCharacters > 0) {
            this.skippingCharacters = 0;
        }
        final Dictionary parserState = this.parserState.get("_savedState");
        final Destination rtfDestination = (Destination)parserState.get("dst");
        if (rtfDestination != this.rtfDestination) {
            this.rtfDestination.close();
            this.rtfDestination = rtfDestination;
        }
        final Dictionary<Object, Object> parserState2 = this.parserState;
        this.parserState = parserState;
        if (this.rtfDestination != null) {
            this.rtfDestination.endgroup(parserState2);
        }
    }
    
    protected void setRTFDestination(final Destination rtfDestination) {
        final Dictionary dictionary = this.parserState.get("_savedState");
        if (dictionary != null && this.rtfDestination != dictionary.get("dst")) {
            this.warning("Warning, RTF destination overridden, invalid RTF.");
            this.rtfDestination.close();
        }
        this.rtfDestination = rtfDestination;
        this.parserState.put("dst", this.rtfDestination);
    }
    
    @Override
    public void close() throws IOException {
        final Enumeration<?> attributeNames = this.documentAttributes.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            this.target.putProperty(nextElement, this.documentAttributes.getAttribute(nextElement));
        }
        this.warning("RTF filter done.");
        super.close();
    }
    
    @Override
    public boolean handleKeyword(final String s) {
        boolean ignoreGroupIfUnknownKeyword = this.ignoreGroupIfUnknownKeyword;
        if (this.skippingCharacters > 0) {
            --this.skippingCharacters;
            return true;
        }
        this.ignoreGroupIfUnknownKeyword = false;
        final String s2;
        if ((s2 = RTFReader.textKeywords.get(s)) != null) {
            this.handleText(s2);
            return true;
        }
        if (s.equals("fonttbl")) {
            this.setRTFDestination(new FonttblDestination());
            return true;
        }
        if (s.equals("colortbl")) {
            this.setRTFDestination(new ColortblDestination());
            return true;
        }
        if (s.equals("stylesheet")) {
            this.setRTFDestination(new StylesheetDestination());
            return true;
        }
        if (s.equals("info")) {
            this.setRTFDestination(new InfoDestination());
            return false;
        }
        if (s.equals("mac")) {
            this.setCharacterSet("mac");
            return true;
        }
        if (s.equals("ansi")) {
            if (RTFReader.useNeXTForAnsi) {
                this.setCharacterSet("NeXT");
            }
            else {
                this.setCharacterSet("ansi");
            }
            return true;
        }
        if (s.equals("next")) {
            this.setCharacterSet("NeXT");
            return true;
        }
        if (s.equals("pc")) {
            this.setCharacterSet("cpg437");
            return true;
        }
        if (s.equals("pca")) {
            this.setCharacterSet("cpg850");
            return true;
        }
        if (s.equals("*")) {
            return this.ignoreGroupIfUnknownKeyword = true;
        }
        if (this.rtfDestination != null && this.rtfDestination.handleKeyword(s)) {
            return true;
        }
        if (s.equals("aftncn") || s.equals("aftnsep") || s.equals("aftnsepc") || s.equals("annotation") || s.equals("atnauthor") || s.equals("atnicn") || s.equals("atnid") || s.equals("atnref") || s.equals("atntime") || s.equals("atrfend") || s.equals("atrfstart") || s.equals("bkmkend") || s.equals("bkmkstart") || s.equals("datafield") || s.equals("do") || s.equals("dptxbxtext") || s.equals("falt") || s.equals("field") || s.equals("file") || s.equals("filetbl") || s.equals("fname") || s.equals("fontemb") || s.equals("fontfile") || s.equals("footer") || s.equals("footerf") || s.equals("footerl") || s.equals("footerr") || s.equals("footnote") || s.equals("ftncn") || s.equals("ftnsep") || s.equals("ftnsepc") || s.equals("header") || s.equals("headerf") || s.equals("headerl") || s.equals("headerr") || s.equals("keycode") || s.equals("nextfile") || s.equals("object") || s.equals("pict") || s.equals("pn") || s.equals("pnseclvl") || s.equals("pntxtb") || s.equals("pntxta") || s.equals("revtbl") || s.equals("rxe") || s.equals("tc") || s.equals("template") || s.equals("txe") || s.equals("xe")) {
            ignoreGroupIfUnknownKeyword = true;
        }
        if (ignoreGroupIfUnknownKeyword) {
            this.setRTFDestination(new DiscardingDestination());
        }
        return false;
    }
    
    @Override
    public boolean handleKeyword(final String s, int rtfversion) {
        boolean ignoreGroupIfUnknownKeyword = this.ignoreGroupIfUnknownKeyword;
        if (this.skippingCharacters > 0) {
            --this.skippingCharacters;
            return true;
        }
        this.ignoreGroupIfUnknownKeyword = false;
        if (s.equals("uc")) {
            this.parserState.put("UnicodeSkip", rtfversion);
            return true;
        }
        if (s.equals("u")) {
            if (rtfversion < 0) {
                rtfversion += 65536;
            }
            this.handleText((char)rtfversion);
            final Number n = this.parserState.get("UnicodeSkip");
            if (n != null) {
                this.skippingCharacters = n.intValue();
            }
            else {
                this.skippingCharacters = 1;
            }
            return true;
        }
        if (s.equals("rtf")) {
            this.rtfversion = rtfversion;
            this.setRTFDestination(new DocumentDestination());
            return true;
        }
        if (s.startsWith("NeXT") || s.equals("private")) {
            ignoreGroupIfUnknownKeyword = true;
        }
        if (this.rtfDestination != null && this.rtfDestination.handleKeyword(s, rtfversion)) {
            return true;
        }
        if (ignoreGroupIfUnknownKeyword) {
            this.setRTFDestination(new DiscardingDestination());
        }
        return false;
    }
    
    private void setTargetAttribute(final String s, final Object o) {
    }
    
    public void setCharacterSet(final String s) {
        Object characterSet;
        try {
            characterSet = getCharacterSet(s);
        }
        catch (final Exception ex) {
            this.warning("Exception loading RTF character set \"" + s + "\": " + ex);
            characterSet = null;
        }
        if (characterSet != null) {
            this.translationTable = (char[])characterSet;
        }
        else {
            this.warning("Unknown RTF character set \"" + s + "\"");
            if (!s.equals("ansi")) {
                try {
                    this.translationTable = (char[])getCharacterSet("ansi");
                }
                catch (final IOException ex2) {
                    throw new InternalError("RTFReader: Unable to find character set resources (" + ex2 + ")", ex2);
                }
            }
        }
        this.setTargetAttribute("rtfCharacterSet", s);
    }
    
    public static void defineCharacterSet(final String s, final char[] array) {
        if (array.length < 256) {
            throw new IllegalArgumentException("Translation table must have 256 entries.");
        }
        RTFReader.characterSets.put(s, array);
    }
    
    public static Object getCharacterSet(final String s) throws IOException {
        char[] charset = RTFReader.characterSets.get(s);
        if (charset == null) {
            charset = readCharset(AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    return RTFReader.class.getResourceAsStream("charsets/" + s + ".txt");
                }
            }));
            defineCharacterSet(s, charset);
        }
        return charset;
    }
    
    static char[] readCharset(final InputStream inputStream) throws IOException {
        final char[] array = new char[256];
        final StreamTokenizer streamTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1")));
        streamTokenizer.eolIsSignificant(false);
        streamTokenizer.commentChar(35);
        streamTokenizer.slashSlashComments(true);
        streamTokenizer.slashStarComments(true);
        for (int i = 0; i < 256; ++i) {
            int nextToken;
            try {
                nextToken = streamTokenizer.nextToken();
            }
            catch (final Exception ex) {
                throw new IOException("Unable to read from character set file (" + ex + ")");
            }
            if (nextToken != -2) {
                throw new IOException("Unexpected token in character set file");
            }
            array[i] = (char)streamTokenizer.nval;
        }
        return array;
    }
    
    static char[] readCharset(final URL url) throws IOException {
        return readCharset(url.openStream());
    }
    
    static {
        RTFReader.straightforwardAttributes = RTFAttributes.attributesByKeyword();
        RTFReader.textKeywords = null;
        (RTFReader.textKeywords = new Hashtable<String, String>()).put("\\", "\\");
        RTFReader.textKeywords.put("{", "{");
        RTFReader.textKeywords.put("}", "}");
        RTFReader.textKeywords.put(" ", " ");
        RTFReader.textKeywords.put("~", " ");
        RTFReader.textKeywords.put("_", "\u2011");
        RTFReader.textKeywords.put("bullet", "\u2022");
        RTFReader.textKeywords.put("emdash", "\u2014");
        RTFReader.textKeywords.put("emspace", "\u2003");
        RTFReader.textKeywords.put("endash", "\u2013");
        RTFReader.textKeywords.put("enspace", "\u2002");
        RTFReader.textKeywords.put("ldblquote", "\u201c");
        RTFReader.textKeywords.put("lquote", "\u2018");
        RTFReader.textKeywords.put("ltrmark", "\u200e");
        RTFReader.textKeywords.put("rdblquote", "\u201d");
        RTFReader.textKeywords.put("rquote", "\u2019");
        RTFReader.textKeywords.put("rtlmark", "\u200f");
        RTFReader.textKeywords.put("tab", "\t");
        RTFReader.textKeywords.put("zwj", "\u200d");
        RTFReader.textKeywords.put("zwnj", "\u200c");
        RTFReader.textKeywords.put("-", "\u2027");
        RTFReader.useNeXTForAnsi = false;
        RTFReader.characterSets = new Hashtable<String, char[]>();
    }
    
    class DiscardingDestination implements Destination
    {
        @Override
        public void handleBinaryBlob(final byte[] array) {
        }
        
        @Override
        public void handleText(final String s) {
        }
        
        @Override
        public boolean handleKeyword(final String s) {
            return true;
        }
        
        @Override
        public boolean handleKeyword(final String s, final int n) {
            return true;
        }
        
        @Override
        public void begingroup() {
        }
        
        @Override
        public void endgroup(final Dictionary dictionary) {
        }
        
        @Override
        public void close() {
        }
    }
    
    class FonttblDestination implements Destination
    {
        int nextFontNumber;
        Integer fontNumberKey;
        String nextFontFamily;
        
        FonttblDestination() {
            this.fontNumberKey = null;
        }
        
        @Override
        public void handleBinaryBlob(final byte[] array) {
        }
        
        @Override
        public void handleText(final String s) {
            final int index = s.indexOf(59);
            String s2;
            if (index > -1) {
                s2 = s.substring(0, index);
            }
            else {
                s2 = s;
            }
            if (this.nextFontNumber == -1 && this.fontNumberKey != null) {
                s2 = RTFReader.this.fontTable.get(this.fontNumberKey) + s2;
            }
            else {
                this.fontNumberKey = this.nextFontNumber;
            }
            RTFReader.this.fontTable.put(this.fontNumberKey, s2);
            this.nextFontNumber = -1;
            this.nextFontFamily = null;
        }
        
        @Override
        public boolean handleKeyword(final String s) {
            if (s.charAt(0) == 'f') {
                this.nextFontFamily = s.substring(1);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean handleKeyword(final String s, final int nextFontNumber) {
            if (s.equals("f")) {
                this.nextFontNumber = nextFontNumber;
                return true;
            }
            return false;
        }
        
        @Override
        public void begingroup() {
        }
        
        @Override
        public void endgroup(final Dictionary dictionary) {
        }
        
        @Override
        public void close() {
            final Enumeration<Integer> keys = RTFReader.this.fontTable.keys();
            RTFReader.this.warning("Done reading font table.");
            while (keys.hasMoreElements()) {
                final Integer n = keys.nextElement();
                RTFReader.this.warning("Number " + n + ": " + RTFReader.this.fontTable.get(n));
            }
        }
    }
    
    class ColortblDestination implements Destination
    {
        int red;
        int green;
        int blue;
        Vector<Color> proTemTable;
        
        public ColortblDestination() {
            this.red = 0;
            this.green = 0;
            this.blue = 0;
            this.proTemTable = new Vector<Color>();
        }
        
        @Override
        public void handleText(final String s) {
            for (int i = 0; i < s.length(); ++i) {
                if (s.charAt(i) == ';') {
                    this.proTemTable.addElement(new Color(this.red, this.green, this.blue));
                }
            }
        }
        
        @Override
        public void close() {
            final int size = this.proTemTable.size();
            RTFReader.this.warning("Done reading color table, " + size + " entries.");
            RTFReader.this.colorTable = new Color[size];
            this.proTemTable.copyInto(RTFReader.this.colorTable);
        }
        
        @Override
        public boolean handleKeyword(final String s, final int blue) {
            if (s.equals("red")) {
                this.red = blue;
            }
            else if (s.equals("green")) {
                this.green = blue;
            }
            else {
                if (!s.equals("blue")) {
                    return false;
                }
                this.blue = blue;
            }
            return true;
        }
        
        @Override
        public boolean handleKeyword(final String s) {
            return false;
        }
        
        @Override
        public void begingroup() {
        }
        
        @Override
        public void endgroup(final Dictionary dictionary) {
        }
        
        @Override
        public void handleBinaryBlob(final byte[] array) {
        }
    }
    
    class StylesheetDestination extends DiscardingDestination implements Destination
    {
        Dictionary<Integer, StyleDefiningDestination> definedStyles;
        final /* synthetic */ RTFReader this$0;
        
        public StylesheetDestination() {
            this.definedStyles = new Hashtable<Integer, StyleDefiningDestination>();
        }
        
        @Override
        public void begingroup() {
            RTFReader.this.setRTFDestination(new StyleDefiningDestination());
        }
        
        @Override
        public void close() {
            final Vector vector = new Vector();
            final Vector vector2 = new Vector();
            final Vector vector3 = new Vector();
            final Enumeration<StyleDefiningDestination> elements = this.definedStyles.elements();
            while (elements.hasMoreElements()) {
                final StyleDefiningDestination styleDefiningDestination = elements.nextElement();
                final Style realize = styleDefiningDestination.realize();
                RTFReader.this.warning("Style " + styleDefiningDestination.number + " (" + styleDefiningDestination.styleName + "): " + realize);
                final String s = (String)realize.getAttribute("style:type");
                Vector vector4;
                if (s.equals("section")) {
                    vector4 = vector3;
                }
                else if (s.equals("character")) {
                    vector4 = vector;
                }
                else {
                    vector4 = vector2;
                }
                if (vector4.size() <= styleDefiningDestination.number) {
                    vector4.setSize(styleDefiningDestination.number + 1);
                }
                vector4.setElementAt(realize, styleDefiningDestination.number);
            }
            if (!vector.isEmpty()) {
                final Style[] characterStyles = new Style[vector.size()];
                vector.copyInto(characterStyles);
                RTFReader.this.characterStyles = characterStyles;
            }
            if (!vector2.isEmpty()) {
                final Style[] paragraphStyles = new Style[vector2.size()];
                vector2.copyInto(paragraphStyles);
                RTFReader.this.paragraphStyles = paragraphStyles;
            }
            if (!vector3.isEmpty()) {
                final Style[] sectionStyles = new Style[vector3.size()];
                vector3.copyInto(sectionStyles);
                RTFReader.this.sectionStyles = sectionStyles;
            }
        }
        
        class StyleDefiningDestination extends AttributeTrackingDestination implements Destination
        {
            final int STYLENUMBER_NONE = 222;
            boolean additive;
            boolean characterStyle;
            boolean sectionStyle;
            public String styleName;
            public int number;
            int basedOn;
            int nextStyle;
            boolean hidden;
            Style realizedStyle;
            
            public StyleDefiningDestination() {
                StylesheetDestination.this.this$0.super();
                this.additive = false;
                this.characterStyle = false;
                this.sectionStyle = false;
                this.styleName = null;
                this.number = 0;
                this.basedOn = 222;
                this.nextStyle = 222;
                this.hidden = false;
            }
            
            @Override
            public void handleText(final String styleName) {
                if (this.styleName != null) {
                    this.styleName += styleName;
                }
                else {
                    this.styleName = styleName;
                }
            }
            
            @Override
            public void close() {
                final int n = (this.styleName == null) ? 0 : this.styleName.indexOf(59);
                if (n > 0) {
                    this.styleName = this.styleName.substring(0, n);
                }
                StylesheetDestination.this.definedStyles.put(this.number, this);
                super.close();
            }
            
            @Override
            public boolean handleKeyword(final String s) {
                if (s.equals("additive")) {
                    return this.additive = true;
                }
                if (s.equals("shidden")) {
                    return this.hidden = true;
                }
                return super.handleKeyword(s);
            }
            
            @Override
            public boolean handleKeyword(final String s, final int nextStyle) {
                if (s.equals("s")) {
                    this.characterStyle = false;
                    this.sectionStyle = false;
                    this.number = nextStyle;
                }
                else if (s.equals("cs")) {
                    this.characterStyle = true;
                    this.sectionStyle = false;
                    this.number = nextStyle;
                }
                else if (s.equals("ds")) {
                    this.characterStyle = false;
                    this.sectionStyle = true;
                    this.number = nextStyle;
                }
                else if (s.equals("sbasedon")) {
                    this.basedOn = nextStyle;
                }
                else {
                    if (!s.equals("snext")) {
                        return super.handleKeyword(s, nextStyle);
                    }
                    this.nextStyle = nextStyle;
                }
                return true;
            }
            
            public Style realize() {
                Style realize = null;
                Object realize2 = null;
                if (this.realizedStyle != null) {
                    return this.realizedStyle;
                }
                if (this.basedOn != 222) {
                    final StyleDefiningDestination styleDefiningDestination = StylesheetDestination.this.definedStyles.get(this.basedOn);
                    if (styleDefiningDestination != null && styleDefiningDestination != this) {
                        realize = styleDefiningDestination.realize();
                    }
                }
                this.realizedStyle = RTFReader.this.target.addStyle(this.styleName, realize);
                if (this.characterStyle) {
                    this.realizedStyle.addAttributes(this.currentTextAttributes());
                    this.realizedStyle.addAttribute("style:type", "character");
                }
                else if (this.sectionStyle) {
                    this.realizedStyle.addAttributes(this.currentSectionAttributes());
                    this.realizedStyle.addAttribute("style:type", "section");
                }
                else {
                    this.realizedStyle.addAttributes(this.currentParagraphAttributes());
                    this.realizedStyle.addAttribute("style:type", "paragraph");
                }
                if (this.nextStyle != 222) {
                    final StyleDefiningDestination styleDefiningDestination2 = StylesheetDestination.this.definedStyles.get(this.nextStyle);
                    if (styleDefiningDestination2 != null) {
                        realize2 = styleDefiningDestination2.realize();
                    }
                }
                if (realize2 != null) {
                    this.realizedStyle.addAttribute("style:nextStyle", realize2);
                }
                this.realizedStyle.addAttribute("style:additive", this.additive);
                this.realizedStyle.addAttribute("style:hidden", this.hidden);
                return this.realizedStyle;
            }
        }
    }
    
    class InfoDestination extends DiscardingDestination implements Destination
    {
    }
    
    abstract class AttributeTrackingDestination implements Destination
    {
        MutableAttributeSet characterAttributes;
        MutableAttributeSet paragraphAttributes;
        MutableAttributeSet sectionAttributes;
        
        public AttributeTrackingDestination() {
            this.characterAttributes = this.rootCharacterAttributes();
            RTFReader.this.parserState.put("chr", this.characterAttributes);
            this.paragraphAttributes = this.rootParagraphAttributes();
            RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
            this.sectionAttributes = this.rootSectionAttributes();
            RTFReader.this.parserState.put("sec", this.sectionAttributes);
        }
        
        @Override
        public abstract void handleText(final String p0);
        
        @Override
        public void handleBinaryBlob(final byte[] array) {
            RTFReader.this.warning("Unexpected binary data in RTF file.");
        }
        
        @Override
        public void begingroup() {
            final MutableAttributeSet currentTextAttributes = this.currentTextAttributes();
            final MutableAttributeSet currentParagraphAttributes = this.currentParagraphAttributes();
            final AttributeSet currentSectionAttributes = this.currentSectionAttributes();
            (this.characterAttributes = new SimpleAttributeSet()).addAttributes(currentTextAttributes);
            RTFReader.this.parserState.put("chr", this.characterAttributes);
            (this.paragraphAttributes = new SimpleAttributeSet()).addAttributes(currentParagraphAttributes);
            RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
            (this.sectionAttributes = new SimpleAttributeSet()).addAttributes(currentSectionAttributes);
            RTFReader.this.parserState.put("sec", this.sectionAttributes);
        }
        
        @Override
        public void endgroup(final Dictionary dictionary) {
            this.characterAttributes = RTFReader.this.parserState.get("chr");
            this.paragraphAttributes = RTFReader.this.parserState.get("pgf");
            this.sectionAttributes = RTFReader.this.parserState.get("sec");
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public boolean handleKeyword(final String s) {
            if (s.equals("ulnone")) {
                return this.handleKeyword("ul", 0);
            }
            final RTFAttribute rtfAttribute = RTFReader.straightforwardAttributes.get(s);
            if (rtfAttribute != null) {
                boolean b = false;
                switch (rtfAttribute.domain()) {
                    case 0: {
                        b = rtfAttribute.set(this.characterAttributes);
                        break;
                    }
                    case 1: {
                        b = rtfAttribute.set(this.paragraphAttributes);
                        break;
                    }
                    case 2: {
                        b = rtfAttribute.set(this.sectionAttributes);
                        break;
                    }
                    case 4: {
                        RTFReader.this.mockery.backing = RTFReader.this.parserState;
                        b = rtfAttribute.set(RTFReader.this.mockery);
                        RTFReader.this.mockery.backing = null;
                        break;
                    }
                    case 3: {
                        b = rtfAttribute.set(RTFReader.this.documentAttributes);
                        break;
                    }
                    default: {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    return true;
                }
            }
            if (s.equals("plain")) {
                this.resetCharacterAttributes();
                return true;
            }
            if (s.equals("pard")) {
                this.resetParagraphAttributes();
                return true;
            }
            if (s.equals("sectd")) {
                this.resetSectionAttributes();
                return true;
            }
            return false;
        }
        
        @Override
        public boolean handleKeyword(String s, final int n) {
            final boolean b = n != 0;
            if (s.equals("fc")) {
                s = "cf";
            }
            if (s.equals("f")) {
                RTFReader.this.parserState.put(s, n);
                return true;
            }
            if (s.equals("cf")) {
                RTFReader.this.parserState.put(s, n);
                return true;
            }
            final RTFAttribute rtfAttribute = RTFReader.straightforwardAttributes.get(s);
            if (rtfAttribute != null) {
                boolean b2 = false;
                switch (rtfAttribute.domain()) {
                    case 0: {
                        b2 = rtfAttribute.set(this.characterAttributes, n);
                        break;
                    }
                    case 1: {
                        b2 = rtfAttribute.set(this.paragraphAttributes, n);
                        break;
                    }
                    case 2: {
                        b2 = rtfAttribute.set(this.sectionAttributes, n);
                        break;
                    }
                    case 4: {
                        RTFReader.this.mockery.backing = RTFReader.this.parserState;
                        b2 = rtfAttribute.set(RTFReader.this.mockery, n);
                        RTFReader.this.mockery.backing = null;
                        break;
                    }
                    case 3: {
                        b2 = rtfAttribute.set(RTFReader.this.documentAttributes, n);
                        break;
                    }
                    default: {
                        b2 = false;
                        break;
                    }
                }
                if (b2) {
                    return true;
                }
            }
            if (s.equals("fs")) {
                StyleConstants.setFontSize(this.characterAttributes, n / 2);
                return true;
            }
            if (s.equals("sl")) {
                if (n == 1000) {
                    this.characterAttributes.removeAttribute(StyleConstants.LineSpacing);
                }
                else {
                    StyleConstants.setLineSpacing(this.characterAttributes, n / 20.0f);
                }
                return true;
            }
            if (s.equals("tx") || s.equals("tb")) {
                final float n2 = n / 20.0f;
                int intValue = 0;
                final Number n3 = RTFReader.this.parserState.get("tab_alignment");
                if (n3 != null) {
                    intValue = n3.intValue();
                }
                int intValue2 = 0;
                final Number n4 = RTFReader.this.parserState.get("tab_leader");
                if (n4 != null) {
                    intValue2 = n4.intValue();
                }
                if (s.equals("tb")) {
                    intValue = 5;
                }
                RTFReader.this.parserState.remove("tab_alignment");
                RTFReader.this.parserState.remove("tab_leader");
                final TabStop tabStop = new TabStop(n2, intValue, intValue2);
                Dictionary<?, ?> dictionary = RTFReader.this.parserState.get("_tabs");
                Integer n5;
                if (dictionary == null) {
                    dictionary = new Hashtable<Object, Object>();
                    RTFReader.this.parserState.put("_tabs", dictionary);
                    n5 = 1;
                }
                else {
                    n5 = 1 + (int)dictionary.get("stop count");
                }
                dictionary.put(n5, tabStop);
                dictionary.put("stop count", n5);
                RTFReader.this.parserState.remove("_tabs_immutable");
                return true;
            }
            if (s.equals("s") && RTFReader.this.paragraphStyles != null) {
                RTFReader.this.parserState.put("paragraphStyle", RTFReader.this.paragraphStyles[n]);
                return true;
            }
            if (s.equals("cs") && RTFReader.this.characterStyles != null) {
                RTFReader.this.parserState.put("characterStyle", RTFReader.this.characterStyles[n]);
                return true;
            }
            if (s.equals("ds") && RTFReader.this.sectionStyles != null) {
                RTFReader.this.parserState.put("sectionStyle", RTFReader.this.sectionStyles[n]);
                return true;
            }
            return false;
        }
        
        protected MutableAttributeSet rootCharacterAttributes() {
            final SimpleAttributeSet set = new SimpleAttributeSet();
            StyleConstants.setItalic(set, false);
            StyleConstants.setBold(set, false);
            StyleConstants.setUnderline(set, false);
            StyleConstants.setForeground(set, RTFReader.this.defaultColor());
            return set;
        }
        
        protected MutableAttributeSet rootParagraphAttributes() {
            final SimpleAttributeSet set = new SimpleAttributeSet();
            StyleConstants.setLeftIndent(set, 0.0f);
            StyleConstants.setRightIndent(set, 0.0f);
            StyleConstants.setFirstLineIndent(set, 0.0f);
            set.setResolveParent(RTFReader.this.target.getStyle("default"));
            return set;
        }
        
        protected MutableAttributeSet rootSectionAttributes() {
            return new SimpleAttributeSet();
        }
        
        MutableAttributeSet currentTextAttributes() {
            final SimpleAttributeSet set = new SimpleAttributeSet(this.characterAttributes);
            final Integer n = RTFReader.this.parserState.get("f");
            String s;
            if (n != null) {
                s = RTFReader.this.fontTable.get(n);
            }
            else {
                s = null;
            }
            if (s != null) {
                StyleConstants.setFontFamily(set, s);
            }
            else {
                set.removeAttribute(StyleConstants.FontFamily);
            }
            if (RTFReader.this.colorTable != null) {
                final Integer n2 = RTFReader.this.parserState.get("cf");
                if (n2 != null) {
                    StyleConstants.setForeground(set, RTFReader.this.colorTable[n2]);
                }
                else {
                    set.removeAttribute(StyleConstants.Foreground);
                }
            }
            if (RTFReader.this.colorTable != null) {
                final Integer n3 = RTFReader.this.parserState.get("cb");
                if (n3 != null) {
                    set.addAttribute(StyleConstants.Background, RTFReader.this.colorTable[n3]);
                }
                else {
                    set.removeAttribute(StyleConstants.Background);
                }
            }
            final Style resolveParent = RTFReader.this.parserState.get("characterStyle");
            if (resolveParent != null) {
                set.setResolveParent(resolveParent);
            }
            return set;
        }
        
        MutableAttributeSet currentParagraphAttributes() {
            final SimpleAttributeSet set = new SimpleAttributeSet(this.paragraphAttributes);
            TabStop[] array = RTFReader.this.parserState.get("_tabs_immutable");
            if (array == null) {
                final Dictionary dictionary = RTFReader.this.parserState.get("_tabs");
                if (dictionary != null) {
                    final int intValue = (int)dictionary.get("stop count");
                    array = new TabStop[intValue];
                    for (int i = 1; i <= intValue; ++i) {
                        array[i - 1] = (TabStop)dictionary.get(i);
                    }
                    RTFReader.this.parserState.put("_tabs_immutable", array);
                }
            }
            if (array != null) {
                set.addAttribute("tabs", array);
            }
            final Style resolveParent = RTFReader.this.parserState.get("paragraphStyle");
            if (resolveParent != null) {
                set.setResolveParent(resolveParent);
            }
            return set;
        }
        
        public AttributeSet currentSectionAttributes() {
            final SimpleAttributeSet set = new SimpleAttributeSet(this.sectionAttributes);
            final Style resolveParent = RTFReader.this.parserState.get("sectionStyle");
            if (resolveParent != null) {
                set.setResolveParent(resolveParent);
            }
            return set;
        }
        
        protected void resetCharacterAttributes() {
            this.handleKeyword("f", 0);
            this.handleKeyword("cf", 0);
            this.handleKeyword("fs", 24);
            final Enumeration elements = RTFReader.straightforwardAttributes.elements();
            while (elements.hasMoreElements()) {
                final RTFAttribute rtfAttribute = (RTFAttribute)elements.nextElement();
                if (rtfAttribute.domain() == 0) {
                    rtfAttribute.setDefault(this.characterAttributes);
                }
            }
            this.handleKeyword("sl", 1000);
            RTFReader.this.parserState.remove("characterStyle");
        }
        
        protected void resetParagraphAttributes() {
            RTFReader.this.parserState.remove("_tabs");
            RTFReader.this.parserState.remove("_tabs_immutable");
            RTFReader.this.parserState.remove("paragraphStyle");
            StyleConstants.setAlignment(this.paragraphAttributes, 0);
            final Enumeration elements = RTFReader.straightforwardAttributes.elements();
            while (elements.hasMoreElements()) {
                final RTFAttribute rtfAttribute = (RTFAttribute)elements.nextElement();
                if (rtfAttribute.domain() == 1) {
                    rtfAttribute.setDefault(this.characterAttributes);
                }
            }
        }
        
        protected void resetSectionAttributes() {
            final Enumeration elements = RTFReader.straightforwardAttributes.elements();
            while (elements.hasMoreElements()) {
                final RTFAttribute rtfAttribute = (RTFAttribute)elements.nextElement();
                if (rtfAttribute.domain() == 2) {
                    rtfAttribute.setDefault(this.characterAttributes);
                }
            }
            RTFReader.this.parserState.remove("sectionStyle");
        }
    }
    
    abstract class TextHandlingDestination extends AttributeTrackingDestination implements Destination
    {
        boolean inParagraph;
        
        public TextHandlingDestination() {
            this.inParagraph = false;
        }
        
        @Override
        public void handleText(final String s) {
            if (!this.inParagraph) {
                this.beginParagraph();
            }
            this.deliverText(s, this.currentTextAttributes());
        }
        
        abstract void deliverText(final String p0, final AttributeSet p1);
        
        @Override
        public void close() {
            if (this.inParagraph) {
                this.endParagraph();
            }
            super.close();
        }
        
        @Override
        public boolean handleKeyword(String s) {
            if (s.equals("\r") || s.equals("\n")) {
                s = "par";
            }
            if (s.equals("par")) {
                this.endParagraph();
                return true;
            }
            if (s.equals("sect")) {
                this.endSection();
                return true;
            }
            return super.handleKeyword(s);
        }
        
        protected void beginParagraph() {
            this.inParagraph = true;
        }
        
        protected void endParagraph() {
            this.finishParagraph(this.currentParagraphAttributes(), this.currentTextAttributes());
            this.inParagraph = false;
        }
        
        abstract void finishParagraph(final AttributeSet p0, final AttributeSet p1);
        
        abstract void endSection();
    }
    
    class DocumentDestination extends TextHandlingDestination implements Destination
    {
        public void deliverText(final String s, final AttributeSet set) {
            try {
                RTFReader.this.target.insertString(RTFReader.this.target.getLength(), s, this.currentTextAttributes());
            }
            catch (final BadLocationException ex) {
                throw new InternalError(ex.getMessage(), ex);
            }
        }
        
        public void finishParagraph(final AttributeSet set, final AttributeSet set2) {
            final int length = RTFReader.this.target.getLength();
            try {
                RTFReader.this.target.insertString(length, "\n", set2);
                RTFReader.this.target.setParagraphAttributes(length, 1, set, true);
            }
            catch (final BadLocationException ex) {
                throw new InternalError(ex.getMessage(), ex);
            }
        }
        
        public void endSection() {
        }
    }
    
    interface Destination
    {
        void handleBinaryBlob(final byte[] p0);
        
        void handleText(final String p0);
        
        boolean handleKeyword(final String p0);
        
        boolean handleKeyword(final String p0, final int p1);
        
        void begingroup();
        
        void endgroup(final Dictionary p0);
        
        void close();
    }
}

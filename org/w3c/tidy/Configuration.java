package org.w3c.tidy;

import java.util.Hashtable;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.Writer;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Map;
import java.io.Serializable;

public class Configuration implements Serializable
{
    public static final int RAW = 0;
    public static final int ASCII = 1;
    public static final int LATIN1 = 2;
    public static final int UTF8 = 3;
    public static final int ISO2022 = 4;
    public static final int MACROMAN = 5;
    public static final int UTF16LE = 6;
    public static final int UTF16BE = 7;
    public static final int UTF16 = 8;
    public static final int WIN1252 = 9;
    public static final int BIG5 = 10;
    public static final int SHIFTJIS = 11;
    private final String[] ENCODING_NAMES;
    public static final int DOCTYPE_OMIT = 0;
    public static final int DOCTYPE_AUTO = 1;
    public static final int DOCTYPE_STRICT = 2;
    public static final int DOCTYPE_LOOSE = 3;
    public static final int DOCTYPE_USER = 4;
    public static final int KEEP_LAST = 0;
    public static final int KEEP_FIRST = 1;
    private static final Map OPTIONS;
    private static final long serialVersionUID = -4955155037138560842L;
    protected int spaces;
    protected int wraplen;
    protected int tabsize;
    protected int docTypeMode;
    protected int duplicateAttrs;
    protected String altText;
    protected String slidestyle;
    protected String language;
    protected String docTypeStr;
    protected String errfile;
    protected boolean writeback;
    protected boolean onlyErrors;
    protected boolean showWarnings;
    protected boolean quiet;
    protected boolean indentContent;
    protected boolean smartIndent;
    protected boolean hideEndTags;
    protected boolean xmlTags;
    protected boolean xmlOut;
    protected boolean xHTML;
    protected boolean htmlOut;
    protected boolean xmlPi;
    protected boolean upperCaseTags;
    protected boolean upperCaseAttrs;
    protected boolean makeClean;
    protected boolean makeBare;
    protected boolean logicalEmphasis;
    protected boolean dropFontTags;
    protected boolean dropProprietaryAttributes;
    protected boolean dropEmptyParas;
    protected boolean fixComments;
    protected boolean trimEmpty;
    protected boolean breakBeforeBR;
    protected boolean burstSlides;
    protected boolean numEntities;
    protected boolean quoteMarks;
    protected boolean quoteNbsp;
    protected boolean quoteAmpersand;
    protected boolean wrapAttVals;
    protected boolean wrapScriptlets;
    protected boolean wrapSection;
    protected boolean wrapAsp;
    protected boolean wrapJste;
    protected boolean wrapPhp;
    protected boolean fixBackslash;
    protected boolean indentAttributes;
    protected boolean xmlPIs;
    protected boolean xmlSpace;
    protected boolean encloseBodyText;
    protected boolean encloseBlockText;
    protected boolean keepFileTimes;
    protected boolean word2000;
    protected boolean tidyMark;
    protected boolean emacs;
    protected boolean literalAttribs;
    protected boolean bodyOnly;
    protected boolean fixUri;
    protected boolean lowerLiterals;
    protected boolean replaceColor;
    protected boolean hideComments;
    protected boolean indentCdata;
    protected boolean forceOutput;
    protected int showErrors;
    protected boolean asciiChars;
    protected boolean joinClasses;
    protected boolean joinStyles;
    protected boolean escapeCdata;
    protected boolean ncr;
    protected String cssPrefix;
    protected String replacementCharEncoding;
    protected TagTable tt;
    protected Report report;
    protected int definedTags;
    protected char[] newline;
    private String inCharEncoding;
    private String outCharEncoding;
    protected boolean rawOut;
    private transient Properties properties;
    
    protected Configuration(final Report report) {
        this.ENCODING_NAMES = new String[] { "raw", "ASCII", "ISO8859_1", "UTF8", "JIS", "MacRoman", "UnicodeLittle", "UnicodeBig", "Unicode", "Cp1252", "Big5", "SJIS" };
        this.spaces = 2;
        this.wraplen = 68;
        this.tabsize = 8;
        this.docTypeMode = 1;
        this.duplicateAttrs = 0;
        this.showWarnings = true;
        this.dropEmptyParas = true;
        this.fixComments = true;
        this.trimEmpty = true;
        this.quoteNbsp = true;
        this.quoteAmpersand = true;
        this.wrapSection = true;
        this.wrapAsp = true;
        this.wrapJste = true;
        this.wrapPhp = true;
        this.fixBackslash = true;
        this.keepFileTimes = true;
        this.tidyMark = true;
        this.fixUri = true;
        this.lowerLiterals = true;
        this.showErrors = 6;
        this.asciiChars = true;
        this.joinStyles = true;
        this.escapeCdata = true;
        this.ncr = true;
        this.replacementCharEncoding = "WIN1252";
        this.newline = System.getProperty("line.separator").toCharArray();
        this.inCharEncoding = "ISO8859_1";
        this.outCharEncoding = "ASCII";
        this.properties = new Properties();
        this.report = report;
    }
    
    private static void addConfigOption(final Flag flag) {
        Configuration.OPTIONS.put(flag.getName(), flag);
    }
    
    public void addProps(final Properties properties) {
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            ((Hashtable<String, String>)this.properties).put(s, properties.getProperty(s));
        }
        this.parseProps();
    }
    
    public void parseFile(final String s) {
        try {
            this.properties.load(new FileInputStream(s));
        }
        catch (final IOException ex) {
            System.err.println(s + " " + ex.toString());
            return;
        }
        this.parseProps();
    }
    
    public static boolean isKnownOption(final String s) {
        return s != null && Configuration.OPTIONS.containsKey(s);
    }
    
    private void parseProps() {
        final Iterator<Object> iterator = (Iterator<Object>)((Hashtable<String, V>)this.properties).keySet().iterator();
        while (iterator.hasNext()) {
            final String s = iterator.next();
            final Flag flag = Configuration.OPTIONS.get(s);
            if (flag == null) {
                this.report.unknownOption(s);
            }
            else {
                final Object parse = flag.getParser().parse(this.properties.getProperty(s), s, this);
                if (flag.getLocation() == null) {
                    continue;
                }
                try {
                    flag.getLocation().set(this, parse);
                }
                catch (final IllegalArgumentException ex) {
                    throw new RuntimeException("IllegalArgumentException during config initialization for field " + s + "with value [" + parse + "]: " + ex.getMessage());
                }
                catch (final IllegalAccessException ex2) {
                    throw new RuntimeException("IllegalArgumentException during config initialization for field " + s + "with value [" + parse + "]: " + ex2.getMessage());
                }
            }
        }
    }
    
    public void adjust() {
        if (this.encloseBlockText) {
            this.encloseBodyText = true;
        }
        if (this.smartIndent) {
            this.indentContent = true;
        }
        if (this.wraplen == 0) {
            this.wraplen = Integer.MAX_VALUE;
        }
        if (this.word2000) {
            this.definedTags |= 0x2;
            this.tt.defineTag((short)2, "o:p");
        }
        if (this.xmlTags) {
            this.xHTML = false;
        }
        if (this.xHTML) {
            this.xmlOut = true;
            this.upperCaseTags = false;
            this.upperCaseAttrs = false;
        }
        if (this.xmlTags) {
            this.xmlOut = true;
            this.xmlPIs = true;
        }
        if (!"UTF8".equals(this.getOutCharEncodingName()) && !"ASCII".equals(this.getOutCharEncodingName()) && this.xmlOut) {
            this.xmlPi = true;
        }
        if (this.xmlOut) {
            this.quoteAmpersand = true;
            this.hideEndTags = false;
        }
    }
    
    public void printConfigOptions(final Writer writer, final boolean b) {
        final String s = "                                                                               ";
        try {
            writer.write("\nConfiguration File Settings:\n\n");
            if (b) {
                writer.write("Name                        Type       Current Value\n");
            }
            else {
                writer.write("Name                        Type       Allowable values\n");
            }
            writer.write("=========================== =========  ========================================\n");
            final ArrayList list = new ArrayList(Configuration.OPTIONS.values());
            Collections.sort((List<Comparable>)list);
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                final Flag flag = (Flag)iterator.next();
                writer.write(flag.getName());
                writer.write(s, 0, 28 - flag.getName().length());
                writer.write(flag.getParser().getType());
                writer.write(s, 0, 11 - flag.getParser().getType().length());
                if (b) {
                    final Field location = flag.getLocation();
                    Object value = null;
                    if (location != null) {
                        try {
                            value = location.get(this);
                        }
                        catch (final IllegalArgumentException ex) {
                            throw new RuntimeException("IllegalArgument when reading field " + location.getName());
                        }
                        catch (final IllegalAccessException ex2) {
                            throw new RuntimeException("IllegalAccess when reading field " + location.getName());
                        }
                    }
                    writer.write(flag.getParser().getFriendlyName(flag.getName(), value, this));
                }
                else {
                    writer.write(flag.getParser().getOptionValues());
                }
                writer.write("\n");
            }
            writer.flush();
        }
        catch (final IOException ex3) {
            throw new RuntimeException(ex3.getMessage());
        }
    }
    
    protected String getInCharEncodingName() {
        return this.inCharEncoding;
    }
    
    protected void setInCharEncodingName(final String s) {
        final String java = EncodingNameMapper.toJava(s);
        if (java != null) {
            this.inCharEncoding = java;
        }
    }
    
    protected String getOutCharEncodingName() {
        return this.outCharEncoding;
    }
    
    protected void setOutCharEncodingName(final String s) {
        final String java = EncodingNameMapper.toJava(s);
        if (java != null) {
            this.outCharEncoding = java;
        }
    }
    
    protected void setInOutEncodingName(final String s) {
        this.setInCharEncodingName(s);
        this.setOutCharEncodingName(s);
    }
    
    protected void setOutCharEncoding(final int n) {
        this.setOutCharEncodingName(this.convertCharEncoding(n));
    }
    
    protected void setInCharEncoding(final int n) {
        this.setInCharEncodingName(this.convertCharEncoding(n));
    }
    
    protected String convertCharEncoding(final int n) {
        if (n != 0 && n < this.ENCODING_NAMES.length) {
            return this.ENCODING_NAMES[n];
        }
        return null;
    }
    
    static {
        OPTIONS = new HashMap();
        addConfigOption(new Flag("indent-spaces", "spaces", ParsePropertyImpl.INT));
        addConfigOption(new Flag("wrap", "wraplen", ParsePropertyImpl.INT));
        addConfigOption(new Flag("show-errors", "showErrors", ParsePropertyImpl.INT));
        addConfigOption(new Flag("tab-size", "tabsize", ParsePropertyImpl.INT));
        addConfigOption(new Flag("wrap-attributes", "wrapAttVals", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("wrap-script-literals", "wrapScriptlets", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("wrap-sections", "wrapSection", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("wrap-asp", "wrapAsp", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("wrap-jste", "wrapJste", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("wrap-php", "wrapPhp", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("literal-attributes", "literalAttribs", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("show-body-only", "bodyOnly", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("fix-uri", "fixUri", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("lower-literals", "lowerLiterals", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("hide-comments", "hideComments", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("indent-cdata", "indentCdata", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("force-output", "forceOutput", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("ascii-chars", "asciiChars", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("join-classes", "joinClasses", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("join-styles", "joinStyles", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("escape-cdata", "escapeCdata", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("replace-color", "replaceColor", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("quiet", "quiet", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("tidy-mark", "tidyMark", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("indent-attributes", "indentAttributes", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("hide-endtags", "hideEndTags", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("input-xml", "xmlTags", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("output-xml", "xmlOut", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("output-html", "htmlOut", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("output-xhtml", "xHTML", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("add-xml-pi", "xmlPi", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("add-xml-decl", "xmlPi", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("assume-xml-procins", "xmlPIs", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("uppercase-tags", "upperCaseTags", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("uppercase-attributes", "upperCaseAttrs", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("bare", "makeBare", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("clean", "makeClean", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("logical-emphasis", "logicalEmphasis", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("word-2000", "word2000", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("drop-empty-paras", "dropEmptyParas", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("drop-font-tags", "dropFontTags", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("drop-proprietary-attributes", "dropProprietaryAttributes", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("enclose-text", "encloseBodyText", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("enclose-block-text", "encloseBlockText", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("add-xml-space", "xmlSpace", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("fix-bad-comments", "fixComments", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("split", "burstSlides", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("break-before-br", "breakBeforeBR", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("numeric-entities", "numEntities", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("quote-marks", "quoteMarks", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("quote-nbsp", "quoteNbsp", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("quote-ampersand", "quoteAmpersand", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("write-back", "writeback", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("keep-time", "keepFileTimes", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("show-warnings", "showWarnings", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("ncr", "ncr", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("fix-backslash", "fixBackslash", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("gnu-emacs", "emacs", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("only-errors", "onlyErrors", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("output-raw", "rawOut", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("trim-empty-elements", "trimEmpty", ParsePropertyImpl.BOOL));
        addConfigOption(new Flag("markup", "onlyErrors", ParsePropertyImpl.INVBOOL));
        addConfigOption(new Flag("char-encoding", null, ParsePropertyImpl.CHAR_ENCODING));
        addConfigOption(new Flag("input-encoding", null, ParsePropertyImpl.CHAR_ENCODING));
        addConfigOption(new Flag("output-encoding", null, ParsePropertyImpl.CHAR_ENCODING));
        addConfigOption(new Flag("error-file", "errfile", ParsePropertyImpl.NAME));
        addConfigOption(new Flag("slide-style", "slidestyle", ParsePropertyImpl.NAME));
        addConfigOption(new Flag("language", "language", ParsePropertyImpl.NAME));
        addConfigOption(new Flag("new-inline-tags", null, ParsePropertyImpl.TAGNAMES));
        addConfigOption(new Flag("new-blocklevel-tags", null, ParsePropertyImpl.TAGNAMES));
        addConfigOption(new Flag("new-empty-tags", null, ParsePropertyImpl.TAGNAMES));
        addConfigOption(new Flag("new-pre-tags", null, ParsePropertyImpl.TAGNAMES));
        addConfigOption(new Flag("doctype", "docTypeStr", ParsePropertyImpl.DOCTYPE));
        addConfigOption(new Flag("repeated-attributes", "duplicateAttrs", ParsePropertyImpl.REPEATED_ATTRIBUTES));
        addConfigOption(new Flag("alt-text", "altText", ParsePropertyImpl.STRING));
        addConfigOption(new Flag("indent", "indentContent", ParsePropertyImpl.INDENT));
        addConfigOption(new Flag("css-prefix", "cssPrefix", ParsePropertyImpl.CSS1SELECTOR));
        addConfigOption(new Flag("newline", null, ParsePropertyImpl.NEWLINE));
    }
    
    static class Flag implements Comparable
    {
        private String name;
        private String fieldName;
        private Field location;
        private ParseProperty parser;
        
        Flag(final String name, final String fieldName, final ParseProperty parser) {
            this.fieldName = fieldName;
            this.name = name;
            this.parser = parser;
        }
        
        public Field getLocation() {
            if (this.fieldName != null && this.location == null) {
                try {
                    this.location = Configuration.class.getDeclaredField(this.fieldName);
                }
                catch (final NoSuchFieldException ex) {
                    throw new RuntimeException("NoSuchField exception during config initialization for field " + this.fieldName);
                }
                catch (final SecurityException ex2) {
                    throw new RuntimeException("Security exception during config initialization for field " + this.fieldName + ": " + ex2.getMessage());
                }
            }
            return this.location;
        }
        
        public String getName() {
            return this.name;
        }
        
        public ParseProperty getParser() {
            return this.parser;
        }
        
        public boolean equals(final Object o) {
            return this.name.equals(((Flag)o).name);
        }
        
        public int hashCode() {
            return this.name.hashCode();
        }
        
        public int compareTo(final Object o) {
            return this.name.compareTo(((Flag)o).name);
        }
    }
}

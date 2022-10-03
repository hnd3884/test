package javax.swing.text.html;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.text.StyleContext;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import java.util.Hashtable;

public class HTML
{
    private static final Hashtable<String, Tag> tagHashtable;
    private static final Hashtable<Object, Tag> scMapping;
    public static final String NULL_ATTRIBUTE_VALUE = "#DEFAULT";
    private static final Hashtable<String, Attribute> attHashtable;
    
    public static Tag[] getAllTags() {
        final Tag[] array = new Tag[Tag.allTags.length];
        System.arraycopy(Tag.allTags, 0, array, 0, Tag.allTags.length);
        return array;
    }
    
    public static Tag getTag(final String s) {
        final Tag tag = HTML.tagHashtable.get(s);
        return (tag == null) ? null : tag;
    }
    
    static Tag getTagForStyleConstantsKey(final StyleConstants styleConstants) {
        return HTML.scMapping.get(styleConstants);
    }
    
    public static int getIntegerAttributeValue(final AttributeSet set, final Attribute attribute, final int n) {
        int intValue = n;
        final String s = (String)set.getAttribute(attribute);
        if (s != null) {
            try {
                intValue = Integer.valueOf(s);
            }
            catch (final NumberFormatException ex) {
                intValue = n;
            }
        }
        return intValue;
    }
    
    public static Attribute[] getAllAttributeKeys() {
        final Attribute[] array = new Attribute[Attribute.allAttributes.length];
        System.arraycopy(Attribute.allAttributes, 0, array, 0, Attribute.allAttributes.length);
        return array;
    }
    
    public static Attribute getAttributeKey(final String s) {
        final Attribute attribute = HTML.attHashtable.get(s);
        if (attribute == null) {
            return null;
        }
        return attribute;
    }
    
    static {
        tagHashtable = new Hashtable<String, Tag>(73);
        scMapping = new Hashtable<Object, Tag>(8);
        for (int i = 0; i < Tag.allTags.length; ++i) {
            HTML.tagHashtable.put(Tag.allTags[i].toString(), Tag.allTags[i]);
            StyleContext.registerStaticAttributeKey(Tag.allTags[i]);
        }
        StyleContext.registerStaticAttributeKey(Tag.IMPLIED);
        StyleContext.registerStaticAttributeKey(Tag.CONTENT);
        StyleContext.registerStaticAttributeKey(Tag.COMMENT);
        for (int j = 0; j < Attribute.allAttributes.length; ++j) {
            StyleContext.registerStaticAttributeKey(Attribute.allAttributes[j]);
        }
        StyleContext.registerStaticAttributeKey("#DEFAULT");
        HTML.scMapping.put(StyleConstants.Bold, Tag.B);
        HTML.scMapping.put(StyleConstants.Italic, Tag.I);
        HTML.scMapping.put(StyleConstants.Underline, Tag.U);
        HTML.scMapping.put(StyleConstants.StrikeThrough, Tag.STRIKE);
        HTML.scMapping.put(StyleConstants.Superscript, Tag.SUP);
        HTML.scMapping.put(StyleConstants.Subscript, Tag.SUB);
        HTML.scMapping.put(StyleConstants.FontFamily, Tag.FONT);
        HTML.scMapping.put(StyleConstants.FontSize, Tag.FONT);
        attHashtable = new Hashtable<String, Attribute>(77);
        for (int k = 0; k < Attribute.allAttributes.length; ++k) {
            HTML.attHashtable.put(Attribute.allAttributes[k].toString(), Attribute.allAttributes[k]);
        }
    }
    
    public static class Tag
    {
        boolean blockTag;
        boolean breakTag;
        String name;
        boolean unknown;
        public static final Tag A;
        public static final Tag ADDRESS;
        public static final Tag APPLET;
        public static final Tag AREA;
        public static final Tag B;
        public static final Tag BASE;
        public static final Tag BASEFONT;
        public static final Tag BIG;
        public static final Tag BLOCKQUOTE;
        public static final Tag BODY;
        public static final Tag BR;
        public static final Tag CAPTION;
        public static final Tag CENTER;
        public static final Tag CITE;
        public static final Tag CODE;
        public static final Tag DD;
        public static final Tag DFN;
        public static final Tag DIR;
        public static final Tag DIV;
        public static final Tag DL;
        public static final Tag DT;
        public static final Tag EM;
        public static final Tag FONT;
        public static final Tag FORM;
        public static final Tag FRAME;
        public static final Tag FRAMESET;
        public static final Tag H1;
        public static final Tag H2;
        public static final Tag H3;
        public static final Tag H4;
        public static final Tag H5;
        public static final Tag H6;
        public static final Tag HEAD;
        public static final Tag HR;
        public static final Tag HTML;
        public static final Tag I;
        public static final Tag IMG;
        public static final Tag INPUT;
        public static final Tag ISINDEX;
        public static final Tag KBD;
        public static final Tag LI;
        public static final Tag LINK;
        public static final Tag MAP;
        public static final Tag MENU;
        public static final Tag META;
        static final Tag NOBR;
        public static final Tag NOFRAMES;
        public static final Tag OBJECT;
        public static final Tag OL;
        public static final Tag OPTION;
        public static final Tag P;
        public static final Tag PARAM;
        public static final Tag PRE;
        public static final Tag SAMP;
        public static final Tag SCRIPT;
        public static final Tag SELECT;
        public static final Tag SMALL;
        public static final Tag SPAN;
        public static final Tag STRIKE;
        public static final Tag S;
        public static final Tag STRONG;
        public static final Tag STYLE;
        public static final Tag SUB;
        public static final Tag SUP;
        public static final Tag TABLE;
        public static final Tag TD;
        public static final Tag TEXTAREA;
        public static final Tag TH;
        public static final Tag TITLE;
        public static final Tag TR;
        public static final Tag TT;
        public static final Tag U;
        public static final Tag UL;
        public static final Tag VAR;
        public static final Tag IMPLIED;
        public static final Tag CONTENT;
        public static final Tag COMMENT;
        static final Tag[] allTags;
        
        public Tag() {
        }
        
        protected Tag(final String s) {
            this(s, false, false);
        }
        
        protected Tag(final String name, final boolean breakTag, final boolean blockTag) {
            this.name = name;
            this.breakTag = breakTag;
            this.blockTag = blockTag;
        }
        
        public boolean isBlock() {
            return this.blockTag;
        }
        
        public boolean breaksFlow() {
            return this.breakTag;
        }
        
        public boolean isPreformatted() {
            return this == Tag.PRE || this == Tag.TEXTAREA;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        boolean isParagraph() {
            return this == Tag.P || this == Tag.IMPLIED || this == Tag.DT || this == Tag.H1 || this == Tag.H2 || this == Tag.H3 || this == Tag.H4 || this == Tag.H5 || this == Tag.H6;
        }
        
        static {
            A = new Tag("a");
            ADDRESS = new Tag("address");
            APPLET = new Tag("applet");
            AREA = new Tag("area");
            B = new Tag("b");
            BASE = new Tag("base");
            BASEFONT = new Tag("basefont");
            BIG = new Tag("big");
            BLOCKQUOTE = new Tag("blockquote", true, true);
            BODY = new Tag("body", true, true);
            BR = new Tag("br", true, false);
            CAPTION = new Tag("caption");
            CENTER = new Tag("center", true, false);
            CITE = new Tag("cite");
            CODE = new Tag("code");
            DD = new Tag("dd", true, true);
            DFN = new Tag("dfn");
            DIR = new Tag("dir", true, true);
            DIV = new Tag("div", true, true);
            DL = new Tag("dl", true, true);
            DT = new Tag("dt", true, true);
            EM = new Tag("em");
            FONT = new Tag("font");
            FORM = new Tag("form", true, false);
            FRAME = new Tag("frame");
            FRAMESET = new Tag("frameset");
            H1 = new Tag("h1", true, true);
            H2 = new Tag("h2", true, true);
            H3 = new Tag("h3", true, true);
            H4 = new Tag("h4", true, true);
            H5 = new Tag("h5", true, true);
            H6 = new Tag("h6", true, true);
            HEAD = new Tag("head", true, true);
            HR = new Tag("hr", true, false);
            HTML = new Tag("html", true, false);
            I = new Tag("i");
            IMG = new Tag("img");
            INPUT = new Tag("input");
            ISINDEX = new Tag("isindex", true, false);
            KBD = new Tag("kbd");
            LI = new Tag("li", true, true);
            LINK = new Tag("link");
            MAP = new Tag("map");
            MENU = new Tag("menu", true, true);
            META = new Tag("meta");
            NOBR = new Tag("nobr");
            NOFRAMES = new Tag("noframes", true, true);
            OBJECT = new Tag("object");
            OL = new Tag("ol", true, true);
            OPTION = new Tag("option");
            P = new Tag("p", true, true);
            PARAM = new Tag("param");
            PRE = new Tag("pre", true, true);
            SAMP = new Tag("samp");
            SCRIPT = new Tag("script");
            SELECT = new Tag("select");
            SMALL = new Tag("small");
            SPAN = new Tag("span");
            STRIKE = new Tag("strike");
            S = new Tag("s");
            STRONG = new Tag("strong");
            STYLE = new Tag("style");
            SUB = new Tag("sub");
            SUP = new Tag("sup");
            TABLE = new Tag("table", false, true);
            TD = new Tag("td", true, true);
            TEXTAREA = new Tag("textarea");
            TH = new Tag("th", true, true);
            TITLE = new Tag("title", true, true);
            TR = new Tag("tr", false, true);
            TT = new Tag("tt");
            U = new Tag("u");
            UL = new Tag("ul", true, true);
            VAR = new Tag("var");
            IMPLIED = new Tag("p-implied");
            CONTENT = new Tag("content");
            COMMENT = new Tag("comment");
            allTags = new Tag[] { Tag.A, Tag.ADDRESS, Tag.APPLET, Tag.AREA, Tag.B, Tag.BASE, Tag.BASEFONT, Tag.BIG, Tag.BLOCKQUOTE, Tag.BODY, Tag.BR, Tag.CAPTION, Tag.CENTER, Tag.CITE, Tag.CODE, Tag.DD, Tag.DFN, Tag.DIR, Tag.DIV, Tag.DL, Tag.DT, Tag.EM, Tag.FONT, Tag.FORM, Tag.FRAME, Tag.FRAMESET, Tag.H1, Tag.H2, Tag.H3, Tag.H4, Tag.H5, Tag.H6, Tag.HEAD, Tag.HR, Tag.HTML, Tag.I, Tag.IMG, Tag.INPUT, Tag.ISINDEX, Tag.KBD, Tag.LI, Tag.LINK, Tag.MAP, Tag.MENU, Tag.META, Tag.NOBR, Tag.NOFRAMES, Tag.OBJECT, Tag.OL, Tag.OPTION, Tag.P, Tag.PARAM, Tag.PRE, Tag.SAMP, Tag.SCRIPT, Tag.SELECT, Tag.SMALL, Tag.SPAN, Tag.STRIKE, Tag.S, Tag.STRONG, Tag.STYLE, Tag.SUB, Tag.SUP, Tag.TABLE, Tag.TD, Tag.TEXTAREA, Tag.TH, Tag.TITLE, Tag.TR, Tag.TT, Tag.U, Tag.UL, Tag.VAR };
            javax.swing.text.html.HTML.getTag("html");
        }
    }
    
    public static class UnknownTag extends Tag implements Serializable
    {
        public UnknownTag(final String s) {
            super(s);
        }
        
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof UnknownTag && this.toString().equals(o.toString());
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeBoolean(this.blockTag);
            objectOutputStream.writeBoolean(this.breakTag);
            objectOutputStream.writeBoolean(this.unknown);
            objectOutputStream.writeObject(this.name);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
            this.blockTag = objectInputStream.readBoolean();
            this.breakTag = objectInputStream.readBoolean();
            this.unknown = objectInputStream.readBoolean();
            this.name = (String)objectInputStream.readObject();
        }
    }
    
    public static final class Attribute
    {
        private String name;
        public static final Attribute SIZE;
        public static final Attribute COLOR;
        public static final Attribute CLEAR;
        public static final Attribute BACKGROUND;
        public static final Attribute BGCOLOR;
        public static final Attribute TEXT;
        public static final Attribute LINK;
        public static final Attribute VLINK;
        public static final Attribute ALINK;
        public static final Attribute WIDTH;
        public static final Attribute HEIGHT;
        public static final Attribute ALIGN;
        public static final Attribute NAME;
        public static final Attribute HREF;
        public static final Attribute REL;
        public static final Attribute REV;
        public static final Attribute TITLE;
        public static final Attribute TARGET;
        public static final Attribute SHAPE;
        public static final Attribute COORDS;
        public static final Attribute ISMAP;
        public static final Attribute NOHREF;
        public static final Attribute ALT;
        public static final Attribute ID;
        public static final Attribute SRC;
        public static final Attribute HSPACE;
        public static final Attribute VSPACE;
        public static final Attribute USEMAP;
        public static final Attribute LOWSRC;
        public static final Attribute CODEBASE;
        public static final Attribute CODE;
        public static final Attribute ARCHIVE;
        public static final Attribute VALUE;
        public static final Attribute VALUETYPE;
        public static final Attribute TYPE;
        public static final Attribute CLASS;
        public static final Attribute STYLE;
        public static final Attribute LANG;
        public static final Attribute FACE;
        public static final Attribute DIR;
        public static final Attribute DECLARE;
        public static final Attribute CLASSID;
        public static final Attribute DATA;
        public static final Attribute CODETYPE;
        public static final Attribute STANDBY;
        public static final Attribute BORDER;
        public static final Attribute SHAPES;
        public static final Attribute NOSHADE;
        public static final Attribute COMPACT;
        public static final Attribute START;
        public static final Attribute ACTION;
        public static final Attribute METHOD;
        public static final Attribute ENCTYPE;
        public static final Attribute CHECKED;
        public static final Attribute MAXLENGTH;
        public static final Attribute MULTIPLE;
        public static final Attribute SELECTED;
        public static final Attribute ROWS;
        public static final Attribute COLS;
        public static final Attribute DUMMY;
        public static final Attribute CELLSPACING;
        public static final Attribute CELLPADDING;
        public static final Attribute VALIGN;
        public static final Attribute HALIGN;
        public static final Attribute NOWRAP;
        public static final Attribute ROWSPAN;
        public static final Attribute COLSPAN;
        public static final Attribute PROMPT;
        public static final Attribute HTTPEQUIV;
        public static final Attribute CONTENT;
        public static final Attribute LANGUAGE;
        public static final Attribute VERSION;
        public static final Attribute N;
        public static final Attribute FRAMEBORDER;
        public static final Attribute MARGINWIDTH;
        public static final Attribute MARGINHEIGHT;
        public static final Attribute SCROLLING;
        public static final Attribute NORESIZE;
        public static final Attribute ENDTAG;
        public static final Attribute COMMENT;
        static final Attribute MEDIA;
        static final Attribute[] allAttributes;
        
        Attribute(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        static {
            SIZE = new Attribute("size");
            COLOR = new Attribute("color");
            CLEAR = new Attribute("clear");
            BACKGROUND = new Attribute("background");
            BGCOLOR = new Attribute("bgcolor");
            TEXT = new Attribute("text");
            LINK = new Attribute("link");
            VLINK = new Attribute("vlink");
            ALINK = new Attribute("alink");
            WIDTH = new Attribute("width");
            HEIGHT = new Attribute("height");
            ALIGN = new Attribute("align");
            NAME = new Attribute("name");
            HREF = new Attribute("href");
            REL = new Attribute("rel");
            REV = new Attribute("rev");
            TITLE = new Attribute("title");
            TARGET = new Attribute("target");
            SHAPE = new Attribute("shape");
            COORDS = new Attribute("coords");
            ISMAP = new Attribute("ismap");
            NOHREF = new Attribute("nohref");
            ALT = new Attribute("alt");
            ID = new Attribute("id");
            SRC = new Attribute("src");
            HSPACE = new Attribute("hspace");
            VSPACE = new Attribute("vspace");
            USEMAP = new Attribute("usemap");
            LOWSRC = new Attribute("lowsrc");
            CODEBASE = new Attribute("codebase");
            CODE = new Attribute("code");
            ARCHIVE = new Attribute("archive");
            VALUE = new Attribute("value");
            VALUETYPE = new Attribute("valuetype");
            TYPE = new Attribute("type");
            CLASS = new Attribute("class");
            STYLE = new Attribute("style");
            LANG = new Attribute("lang");
            FACE = new Attribute("face");
            DIR = new Attribute("dir");
            DECLARE = new Attribute("declare");
            CLASSID = new Attribute("classid");
            DATA = new Attribute("data");
            CODETYPE = new Attribute("codetype");
            STANDBY = new Attribute("standby");
            BORDER = new Attribute("border");
            SHAPES = new Attribute("shapes");
            NOSHADE = new Attribute("noshade");
            COMPACT = new Attribute("compact");
            START = new Attribute("start");
            ACTION = new Attribute("action");
            METHOD = new Attribute("method");
            ENCTYPE = new Attribute("enctype");
            CHECKED = new Attribute("checked");
            MAXLENGTH = new Attribute("maxlength");
            MULTIPLE = new Attribute("multiple");
            SELECTED = new Attribute("selected");
            ROWS = new Attribute("rows");
            COLS = new Attribute("cols");
            DUMMY = new Attribute("dummy");
            CELLSPACING = new Attribute("cellspacing");
            CELLPADDING = new Attribute("cellpadding");
            VALIGN = new Attribute("valign");
            HALIGN = new Attribute("halign");
            NOWRAP = new Attribute("nowrap");
            ROWSPAN = new Attribute("rowspan");
            COLSPAN = new Attribute("colspan");
            PROMPT = new Attribute("prompt");
            HTTPEQUIV = new Attribute("http-equiv");
            CONTENT = new Attribute("content");
            LANGUAGE = new Attribute("language");
            VERSION = new Attribute("version");
            N = new Attribute("n");
            FRAMEBORDER = new Attribute("frameborder");
            MARGINWIDTH = new Attribute("marginwidth");
            MARGINHEIGHT = new Attribute("marginheight");
            SCROLLING = new Attribute("scrolling");
            NORESIZE = new Attribute("noresize");
            ENDTAG = new Attribute("endtag");
            COMMENT = new Attribute("comment");
            MEDIA = new Attribute("media");
            allAttributes = new Attribute[] { Attribute.FACE, Attribute.COMMENT, Attribute.SIZE, Attribute.COLOR, Attribute.CLEAR, Attribute.BACKGROUND, Attribute.BGCOLOR, Attribute.TEXT, Attribute.LINK, Attribute.VLINK, Attribute.ALINK, Attribute.WIDTH, Attribute.HEIGHT, Attribute.ALIGN, Attribute.NAME, Attribute.HREF, Attribute.REL, Attribute.REV, Attribute.TITLE, Attribute.TARGET, Attribute.SHAPE, Attribute.COORDS, Attribute.ISMAP, Attribute.NOHREF, Attribute.ALT, Attribute.ID, Attribute.SRC, Attribute.HSPACE, Attribute.VSPACE, Attribute.USEMAP, Attribute.LOWSRC, Attribute.CODEBASE, Attribute.CODE, Attribute.ARCHIVE, Attribute.VALUE, Attribute.VALUETYPE, Attribute.TYPE, Attribute.CLASS, Attribute.STYLE, Attribute.LANG, Attribute.DIR, Attribute.DECLARE, Attribute.CLASSID, Attribute.DATA, Attribute.CODETYPE, Attribute.STANDBY, Attribute.BORDER, Attribute.SHAPES, Attribute.NOSHADE, Attribute.COMPACT, Attribute.START, Attribute.ACTION, Attribute.METHOD, Attribute.ENCTYPE, Attribute.CHECKED, Attribute.MAXLENGTH, Attribute.MULTIPLE, Attribute.SELECTED, Attribute.ROWS, Attribute.COLS, Attribute.DUMMY, Attribute.CELLSPACING, Attribute.CELLPADDING, Attribute.VALIGN, Attribute.HALIGN, Attribute.NOWRAP, Attribute.ROWSPAN, Attribute.COLSPAN, Attribute.PROMPT, Attribute.HTTPEQUIV, Attribute.CONTENT, Attribute.LANGUAGE, Attribute.VERSION, Attribute.N, Attribute.FRAMEBORDER, Attribute.MARGINWIDTH, Attribute.MARGINHEIGHT, Attribute.SCROLLING, Attribute.NORESIZE, Attribute.MEDIA, Attribute.ENDTAG };
        }
    }
}

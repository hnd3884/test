package org.w3c.tidy;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Map;

public final class TagTable
{
    public static final Dict XML_TAGS;
    private static final Dict[] TAGS;
    protected Dict tagHtml;
    protected Dict tagHead;
    protected Dict tagBody;
    protected Dict tagFrameset;
    protected Dict tagFrame;
    protected Dict tagIframe;
    protected Dict tagNoframes;
    protected Dict tagMeta;
    protected Dict tagTitle;
    protected Dict tagBase;
    protected Dict tagHr;
    protected Dict tagPre;
    protected Dict tagListing;
    protected Dict tagH1;
    protected Dict tagH2;
    protected Dict tagP;
    protected Dict tagUl;
    protected Dict tagOl;
    protected Dict tagDir;
    protected Dict tagLi;
    protected Dict tagDt;
    protected Dict tagDd;
    protected Dict tagDl;
    protected Dict tagTd;
    protected Dict tagTh;
    protected Dict tagTr;
    protected Dict tagCol;
    protected Dict tagColgroup;
    protected Dict tagBr;
    protected Dict tagA;
    protected Dict tagLink;
    protected Dict tagB;
    protected Dict tagI;
    protected Dict tagStrong;
    protected Dict tagEm;
    protected Dict tagBig;
    protected Dict tagSmall;
    protected Dict tagParam;
    protected Dict tagOption;
    protected Dict tagOptgroup;
    protected Dict tagImg;
    protected Dict tagMap;
    protected Dict tagArea;
    protected Dict tagNobr;
    protected Dict tagWbr;
    protected Dict tagFont;
    protected Dict tagSpacer;
    protected Dict tagLayer;
    protected Dict tagCenter;
    protected Dict tagStyle;
    protected Dict tagScript;
    protected Dict tagNoscript;
    protected Dict tagTable;
    protected Dict tagCaption;
    protected Dict tagForm;
    protected Dict tagTextarea;
    protected Dict tagBlockquote;
    protected Dict tagApplet;
    protected Dict tagObject;
    protected Dict tagDiv;
    protected Dict tagSpan;
    protected Dict tagInput;
    protected Dict tagQ;
    protected Dict tagBlink;
    protected Anchor anchorList;
    private Configuration configuration;
    private Map tagHashtable;
    
    protected TagTable() {
        this.tagHashtable = new Hashtable();
        for (int i = 0; i < TagTable.TAGS.length; ++i) {
            this.install(TagTable.TAGS[i]);
        }
        this.tagHtml = this.lookup("html");
        this.tagHead = this.lookup("head");
        this.tagBody = this.lookup("body");
        this.tagFrameset = this.lookup("frameset");
        this.tagFrame = this.lookup("frame");
        this.tagIframe = this.lookup("iframe");
        this.tagNoframes = this.lookup("noframes");
        this.tagMeta = this.lookup("meta");
        this.tagTitle = this.lookup("title");
        this.tagBase = this.lookup("base");
        this.tagHr = this.lookup("hr");
        this.tagPre = this.lookup("pre");
        this.tagListing = this.lookup("listing");
        this.tagH1 = this.lookup("h1");
        this.tagH2 = this.lookup("h2");
        this.tagP = this.lookup("p");
        this.tagUl = this.lookup("ul");
        this.tagOl = this.lookup("ol");
        this.tagDir = this.lookup("dir");
        this.tagLi = this.lookup("li");
        this.tagDt = this.lookup("dt");
        this.tagDd = this.lookup("dd");
        this.tagDl = this.lookup("dl");
        this.tagTd = this.lookup("td");
        this.tagTh = this.lookup("th");
        this.tagTr = this.lookup("tr");
        this.tagCol = this.lookup("col");
        this.tagColgroup = this.lookup("colgroup");
        this.tagBr = this.lookup("br");
        this.tagA = this.lookup("a");
        this.tagLink = this.lookup("link");
        this.tagB = this.lookup("b");
        this.tagI = this.lookup("i");
        this.tagStrong = this.lookup("strong");
        this.tagEm = this.lookup("em");
        this.tagBig = this.lookup("big");
        this.tagSmall = this.lookup("small");
        this.tagParam = this.lookup("param");
        this.tagOption = this.lookup("option");
        this.tagOptgroup = this.lookup("optgroup");
        this.tagImg = this.lookup("img");
        this.tagMap = this.lookup("map");
        this.tagArea = this.lookup("area");
        this.tagNobr = this.lookup("nobr");
        this.tagWbr = this.lookup("wbr");
        this.tagFont = this.lookup("font");
        this.tagSpacer = this.lookup("spacer");
        this.tagLayer = this.lookup("layer");
        this.tagCenter = this.lookup("center");
        this.tagStyle = this.lookup("style");
        this.tagScript = this.lookup("script");
        this.tagNoscript = this.lookup("noscript");
        this.tagTable = this.lookup("table");
        this.tagCaption = this.lookup("caption");
        this.tagForm = this.lookup("form");
        this.tagTextarea = this.lookup("textarea");
        this.tagBlockquote = this.lookup("blockquote");
        this.tagApplet = this.lookup("applet");
        this.tagObject = this.lookup("object");
        this.tagDiv = this.lookup("div");
        this.tagSpan = this.lookup("span");
        this.tagInput = this.lookup("input");
        this.tagQ = this.lookup("q");
        this.tagBlink = this.lookup("blink");
    }
    
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    public Dict lookup(final String s) {
        return this.tagHashtable.get(s);
    }
    
    public Dict install(final Dict dict) {
        final Dict dict2 = this.tagHashtable.get(dict.name);
        if (dict2 != null) {
            dict2.versions = dict.versions;
            final Dict dict3 = dict2;
            dict3.model |= dict.model;
            dict2.setParser(dict.getParser());
            dict2.setChkattrs(dict.getChkattrs());
            return dict2;
        }
        this.tagHashtable.put(dict.name, dict);
        return dict;
    }
    
    public boolean findTag(final Node node) {
        if (this.configuration != null && this.configuration.xmlTags) {
            node.tag = TagTable.XML_TAGS;
            return true;
        }
        if (node.element != null) {
            final Dict lookup = this.lookup(node.element);
            if (lookup != null) {
                node.tag = lookup;
                return true;
            }
        }
        return false;
    }
    
    public Parser findParser(final Node node) {
        if (node.element != null) {
            final Dict lookup = this.lookup(node.element);
            if (lookup != null) {
                return lookup.getParser();
            }
        }
        return null;
    }
    
    boolean isAnchorElement(final Node node) {
        return node.tag == this.tagA || node.tag == this.tagApplet || node.tag == this.tagForm || node.tag == this.tagFrame || node.tag == this.tagIframe || node.tag == this.tagImg || node.tag == this.tagMap;
    }
    
    public void defineTag(final short n, final String s) {
        int n2 = 0;
        Parser parser = null;
        switch (n) {
            case 4: {
                n2 = 8;
                parser = ParserImpl.BLOCK;
                break;
            }
            case 1: {
                n2 = 1;
                parser = ParserImpl.BLOCK;
                break;
            }
            case 8: {
                n2 = 8;
                parser = ParserImpl.PRE;
                break;
            }
            default: {
                n2 = 16;
                parser = ParserImpl.INLINE;
                break;
            }
        }
        this.install(new Dict(s, (short)448, n2, parser, null));
    }
    
    List findAllDefinedTag(final short n) {
        final ArrayList list = new ArrayList();
        final Iterator iterator = this.tagHashtable.values().iterator();
        while (iterator.hasNext()) {
            final Dict dict = (Dict)iterator.next();
            if (dict != null) {
                switch (n) {
                    case 1: {
                        if (dict.versions == 448 && (dict.model & 0x1) == 0x1 && dict != this.tagWbr) {
                            list.add(dict.name);
                            continue;
                        }
                        continue;
                    }
                    case 2: {
                        if (dict.versions == 448 && (dict.model & 0x10) == 0x10 && dict != this.tagBlink && dict != this.tagNobr && dict != this.tagWbr) {
                            list.add(dict.name);
                            continue;
                        }
                        continue;
                    }
                    case 4: {
                        if (dict.versions == 448 && (dict.model & 0x8) == 0x8 && dict.getParser() == ParserImpl.BLOCK) {
                            list.add(dict.name);
                            continue;
                        }
                        continue;
                    }
                    case 8: {
                        if (dict.versions == 448 && (dict.model & 0x8) == 0x8 && dict.getParser() == ParserImpl.PRE) {
                            list.add(dict.name);
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        return list;
    }
    
    public void freeAttrs(final Node node) {
        while (node.attributes != null) {
            final AttVal attributes = node.attributes;
            if ("id".equalsIgnoreCase(attributes.attribute) || ("name".equalsIgnoreCase(attributes.attribute) && this.isAnchorElement(node))) {
                this.removeAnchorByNode(node);
            }
            node.attributes = attributes.next;
        }
    }
    
    void removeAnchorByNode(final Node node) {
        Anchor anchor = null;
        Anchor anchor2 = null;
        for (Anchor anchor3 = this.anchorList; anchor3 != null; anchor3 = anchor3.next) {
            final Anchor next = anchor3.next;
            if (anchor3.node == node) {
                if (anchor2 != null) {
                    anchor2.next = next;
                }
                else {
                    this.anchorList = next;
                }
                anchor = anchor3;
            }
            else {
                anchor2 = anchor3;
            }
        }
        if (anchor != null) {}
    }
    
    Anchor newAnchor() {
        return new Anchor();
    }
    
    Anchor addAnchor(final String name, final Node node) {
        final Anchor anchor = this.newAnchor();
        anchor.name = name;
        anchor.node = node;
        if (this.anchorList == null) {
            this.anchorList = anchor;
        }
        else {
            Anchor anchor2;
            for (anchor2 = this.anchorList; anchor2.next != null; anchor2 = anchor2.next) {}
            anchor2.next = anchor;
        }
        return this.anchorList;
    }
    
    Node getNodeByAnchor(final String s) {
        Anchor anchor;
        for (anchor = this.anchorList; anchor != null && !s.equalsIgnoreCase(anchor.name); anchor = anchor.next) {}
        if (anchor != null) {
            return anchor.node;
        }
        return null;
    }
    
    void freeAnchors() {
        this.anchorList = null;
    }
    
    static {
        XML_TAGS = new Dict(null, (short)3103, 8, null, null);
        TAGS = new Dict[] { new Dict("html", (short)3103, 2129922, ParserImpl.HTML, TagCheckImpl.HTML), new Dict("head", (short)3103, 2129922, ParserImpl.HEAD, null), new Dict("title", (short)3103, 4, ParserImpl.TITLE, null), new Dict("base", (short)3103, 5, ParserImpl.EMPTY, null), new Dict("link", (short)3103, 5, ParserImpl.EMPTY, TagCheckImpl.LINK), new Dict("meta", (short)3103, 5, ParserImpl.EMPTY, TagCheckImpl.META), new Dict("style", (short)28, 4, ParserImpl.SCRIPT, TagCheckImpl.STYLE), new Dict("script", (short)28, 131100, ParserImpl.SCRIPT, TagCheckImpl.SCRIPT), new Dict("server", (short)64, 131100, ParserImpl.SCRIPT, null), new Dict("body", (short)3103, 2129922, ParserImpl.BODY, null), new Dict("frameset", (short)16, 8194, ParserImpl.FRAMESET, null), new Dict("p", (short)3103, 32776, ParserImpl.INLINE, null), new Dict("h1", (short)3103, 16392, ParserImpl.INLINE, null), new Dict("h2", (short)3103, 16392, ParserImpl.INLINE, null), new Dict("h3", (short)3103, 16392, ParserImpl.INLINE, null), new Dict("h4", (short)3103, 16392, ParserImpl.INLINE, null), new Dict("h5", (short)3103, 16392, ParserImpl.INLINE, null), new Dict("h6", (short)3103, 16392, ParserImpl.INLINE, null), new Dict("ul", (short)3103, 8, ParserImpl.LIST, null), new Dict("ol", (short)3103, 8, ParserImpl.LIST, null), new Dict("dl", (short)3103, 8, ParserImpl.DEFLIST, null), new Dict("dir", (short)26, 524296, ParserImpl.LIST, null), new Dict("menu", (short)26, 524296, ParserImpl.LIST, null), new Dict("pre", (short)3103, 8, ParserImpl.PRE, null), new Dict("listing", (short)3103, 524296, ParserImpl.PRE, null), new Dict("xmp", (short)3103, 524296, ParserImpl.PRE, null), new Dict("plaintext", (short)3103, 524296, ParserImpl.PRE, null), new Dict("address", (short)3103, 8, ParserImpl.BLOCK, null), new Dict("blockquote", (short)3103, 8, ParserImpl.BLOCK, null), new Dict("form", (short)3103, 8, ParserImpl.BLOCK, TagCheckImpl.FORM), new Dict("isindex", (short)26, 9, ParserImpl.EMPTY, null), new Dict("fieldset", (short)28, 8, ParserImpl.BLOCK, null), new Dict("table", (short)30, 8, ParserImpl.TABLETAG, TagCheckImpl.TABLE), new Dict("hr", (short)1055, 9, ParserImpl.EMPTY, TagCheckImpl.HR), new Dict("div", (short)30, 8, ParserImpl.BLOCK, null), new Dict("multicol", (short)64, 8, ParserImpl.BLOCK, null), new Dict("nosave", (short)64, 8, ParserImpl.BLOCK, null), new Dict("layer", (short)64, 8, ParserImpl.BLOCK, null), new Dict("ilayer", (short)64, 16, ParserImpl.INLINE, null), new Dict("nolayer", (short)64, 131096, ParserImpl.BLOCK, null), new Dict("align", (short)64, 8, ParserImpl.BLOCK, null), new Dict("center", (short)26, 8, ParserImpl.BLOCK, null), new Dict("ins", (short)28, 131096, ParserImpl.INLINE, null), new Dict("del", (short)28, 131096, ParserImpl.INLINE, null), new Dict("li", (short)3103, 294944, ParserImpl.BLOCK, null), new Dict("dt", (short)3103, 294976, ParserImpl.INLINE, null), new Dict("dd", (short)3103, 294976, ParserImpl.BLOCK, null), new Dict("caption", (short)30, 128, ParserImpl.INLINE, TagCheckImpl.CAPTION), new Dict("colgroup", (short)28, 32896, ParserImpl.COLGROUP, null), new Dict("col", (short)28, 129, ParserImpl.EMPTY, null), new Dict("thead", (short)28, 33152, ParserImpl.ROWGROUP, null), new Dict("tfoot", (short)28, 33152, ParserImpl.ROWGROUP, null), new Dict("tbody", (short)28, 33152, ParserImpl.ROWGROUP, null), new Dict("tr", (short)30, 32896, ParserImpl.ROW, null), new Dict("td", (short)30, 295424, ParserImpl.BLOCK, TagCheckImpl.TABLECELL), new Dict("th", (short)30, 295424, ParserImpl.BLOCK, TagCheckImpl.TABLECELL), new Dict("q", (short)28, 16, ParserImpl.INLINE, null), new Dict("a", (short)3103, 16, ParserImpl.INLINE, TagCheckImpl.ANCHOR), new Dict("br", (short)3103, 17, ParserImpl.EMPTY, null), new Dict("img", (short)3103, 65553, ParserImpl.EMPTY, TagCheckImpl.IMG), new Dict("object", (short)28, 71700, ParserImpl.BLOCK, null), new Dict("applet", (short)26, 71696, ParserImpl.BLOCK, null), new Dict("servlet", (short)256, 71696, ParserImpl.BLOCK, null), new Dict("param", (short)30, 17, ParserImpl.EMPTY, null), new Dict("embed", (short)64, 65553, ParserImpl.EMPTY, null), new Dict("noembed", (short)64, 16, ParserImpl.INLINE, null), new Dict("iframe", (short)8, 16, ParserImpl.BLOCK, null), new Dict("frame", (short)16, 8193, ParserImpl.EMPTY, null), new Dict("noframes", (short)24, 8200, ParserImpl.NOFRAMES, null), new Dict("noscript", (short)28, 131096, ParserImpl.BLOCK, null), new Dict("b", (short)1055, 16, ParserImpl.INLINE, null), new Dict("i", (short)1055, 16, ParserImpl.INLINE, null), new Dict("u", (short)26, 16, ParserImpl.INLINE, null), new Dict("tt", (short)1055, 16, ParserImpl.INLINE, null), new Dict("s", (short)26, 16, ParserImpl.INLINE, null), new Dict("strike", (short)26, 16, ParserImpl.INLINE, null), new Dict("big", (short)28, 16, ParserImpl.INLINE, null), new Dict("small", (short)28, 16, ParserImpl.INLINE, null), new Dict("sub", (short)28, 16, ParserImpl.INLINE, null), new Dict("sup", (short)28, 16, ParserImpl.INLINE, null), new Dict("em", (short)3103, 16, ParserImpl.INLINE, null), new Dict("strong", (short)3103, 16, ParserImpl.INLINE, null), new Dict("dfn", (short)3103, 16, ParserImpl.INLINE, null), new Dict("code", (short)3103, 16, ParserImpl.INLINE, null), new Dict("samp", (short)3103, 16, ParserImpl.INLINE, null), new Dict("kbd", (short)3103, 16, ParserImpl.INLINE, null), new Dict("var", (short)3103, 16, ParserImpl.INLINE, null), new Dict("cite", (short)3103, 16, ParserImpl.INLINE, null), new Dict("abbr", (short)28, 16, ParserImpl.INLINE, null), new Dict("acronym", (short)28, 16, ParserImpl.INLINE, null), new Dict("span", (short)30, 16, ParserImpl.INLINE, null), new Dict("blink", (short)448, 16, ParserImpl.INLINE, null), new Dict("nobr", (short)448, 16, ParserImpl.INLINE, null), new Dict("wbr", (short)448, 17, ParserImpl.EMPTY, null), new Dict("marquee", (short)128, 32784, ParserImpl.INLINE, null), new Dict("bgsound", (short)128, 5, ParserImpl.EMPTY, null), new Dict("comment", (short)128, 16, ParserImpl.INLINE, null), new Dict("spacer", (short)64, 17, ParserImpl.EMPTY, null), new Dict("keygen", (short)64, 17, ParserImpl.EMPTY, null), new Dict("nolayer", (short)64, 131096, ParserImpl.BLOCK, null), new Dict("ilayer", (short)64, 16, ParserImpl.INLINE, null), new Dict("map", (short)28, 16, ParserImpl.BLOCK, TagCheckImpl.MAP), new Dict("area", (short)1055, 9, ParserImpl.EMPTY, TagCheckImpl.AREA), new Dict("input", (short)3103, 65553, ParserImpl.EMPTY, null), new Dict("select", (short)3103, 1040, ParserImpl.SELECT, null), new Dict("option", (short)3103, 33792, ParserImpl.TEXT, null), new Dict("optgroup", (short)28, 33792, ParserImpl.OPTGROUP, null), new Dict("textarea", (short)3103, 1040, ParserImpl.TEXT, null), new Dict("label", (short)28, 16, ParserImpl.INLINE, null), new Dict("legend", (short)28, 16, ParserImpl.INLINE, null), new Dict("button", (short)28, 16, ParserImpl.INLINE, null), new Dict("basefont", (short)26, 17, ParserImpl.EMPTY, null), new Dict("font", (short)26, 16, ParserImpl.INLINE, null), new Dict("bdo", (short)28, 16, ParserImpl.INLINE, null), new Dict("ruby", (short)1024, 16, ParserImpl.INLINE, null), new Dict("rbc", (short)1024, 16, ParserImpl.INLINE, null), new Dict("rtc", (short)1024, 16, ParserImpl.INLINE, null), new Dict("rb", (short)1024, 16, ParserImpl.INLINE, null), new Dict("rt", (short)1024, 16, ParserImpl.INLINE, null), new Dict("", (short)1024, 16, ParserImpl.INLINE, null), new Dict("rp", (short)1024, 16, ParserImpl.INLINE, null) };
    }
}

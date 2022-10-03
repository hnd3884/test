package org.w3c.tidy;

import java.util.Hashtable;
import java.util.Map;

public class AttributeTable
{
    protected static Attribute attrHref;
    protected static Attribute attrSrc;
    protected static Attribute attrId;
    protected static Attribute attrName;
    protected static Attribute attrSummary;
    protected static Attribute attrAlt;
    protected static Attribute attrLongdesc;
    protected static Attribute attrUsemap;
    protected static Attribute attrIsmap;
    protected static Attribute attrLanguage;
    protected static Attribute attrType;
    protected static Attribute attrTitle;
    protected static Attribute attrXmlns;
    protected static Attribute attrValue;
    protected static Attribute attrContent;
    protected static Attribute attrDatafld;
    protected static Attribute attrWidth;
    protected static Attribute attrHeight;
    private static AttributeTable defaultAttributeTable;
    private static final Attribute[] ATTRS;
    private Map attributeHashtable;
    
    public AttributeTable() {
        this.attributeHashtable = new Hashtable();
    }
    
    public Attribute lookup(final String s) {
        return this.attributeHashtable.get(s);
    }
    
    public Attribute install(final Attribute attribute) {
        return this.attributeHashtable.put(attribute.getName(), attribute);
    }
    
    public Attribute findAttribute(final AttVal attVal) {
        if (attVal.attribute != null) {
            return this.lookup(attVal.attribute);
        }
        return null;
    }
    
    public boolean isUrl(final String s) {
        final Attribute lookup = this.lookup(s);
        return lookup != null && lookup.getAttrchk() == AttrCheckImpl.URL;
    }
    
    public boolean isScript(final String s) {
        final Attribute lookup = this.lookup(s);
        return lookup != null && lookup.getAttrchk() == AttrCheckImpl.SCRIPT;
    }
    
    public boolean isLiteralAttribute(final String s) {
        final Attribute lookup = this.lookup(s);
        return lookup != null && lookup.isLiteral();
    }
    
    public void declareLiteralAttrib(final String s) {
        Attribute attribute = this.lookup(s);
        if (attribute == null) {
            attribute = this.install(new Attribute(s, (short)448, null));
        }
        attribute.setLiteral(true);
    }
    
    public static AttributeTable getDefaultAttributeTable() {
        if (AttributeTable.defaultAttributeTable == null) {
            AttributeTable.defaultAttributeTable = new AttributeTable();
            for (int i = 0; i < AttributeTable.ATTRS.length; ++i) {
                AttributeTable.defaultAttributeTable.install(AttributeTable.ATTRS[i]);
            }
            AttributeTable.attrHref = AttributeTable.defaultAttributeTable.lookup("href");
            AttributeTable.attrSrc = AttributeTable.defaultAttributeTable.lookup("src");
            AttributeTable.attrId = AttributeTable.defaultAttributeTable.lookup("id");
            AttributeTable.attrName = AttributeTable.defaultAttributeTable.lookup("name");
            AttributeTable.attrSummary = AttributeTable.defaultAttributeTable.lookup("summary");
            AttributeTable.attrAlt = AttributeTable.defaultAttributeTable.lookup("alt");
            AttributeTable.attrLongdesc = AttributeTable.defaultAttributeTable.lookup("longdesc");
            AttributeTable.attrUsemap = AttributeTable.defaultAttributeTable.lookup("usemap");
            AttributeTable.attrIsmap = AttributeTable.defaultAttributeTable.lookup("ismap");
            AttributeTable.attrLanguage = AttributeTable.defaultAttributeTable.lookup("language");
            AttributeTable.attrType = AttributeTable.defaultAttributeTable.lookup("type");
            AttributeTable.attrTitle = AttributeTable.defaultAttributeTable.lookup("title");
            AttributeTable.attrXmlns = AttributeTable.defaultAttributeTable.lookup("xmlns");
            AttributeTable.attrValue = AttributeTable.defaultAttributeTable.lookup("value");
            AttributeTable.attrContent = AttributeTable.defaultAttributeTable.lookup("content");
            AttributeTable.attrDatafld = AttributeTable.defaultAttributeTable.lookup("datafld");
            AttributeTable.attrWidth = AttributeTable.defaultAttributeTable.lookup("width");
            AttributeTable.attrHeight = AttributeTable.defaultAttributeTable.lookup("height");
            AttributeTable.attrAlt.setNowrap(true);
            AttributeTable.attrValue.setNowrap(true);
            AttributeTable.attrContent.setNowrap(true);
        }
        return AttributeTable.defaultAttributeTable;
    }
    
    static {
        ATTRS = new Attribute[] { new Attribute("abbr", (short)28, AttrCheckImpl.TEXT), new Attribute("accept-charset", (short)28, AttrCheckImpl.CHARSET), new Attribute("accept", (short)3103, AttrCheckImpl.TYPE), new Attribute("accesskey", (short)28, AttrCheckImpl.CHARACTER), new Attribute("action", (short)3103, AttrCheckImpl.URL), new Attribute("add_date", (short)64, AttrCheckImpl.TEXT), new Attribute("align", (short)3103, AttrCheckImpl.ALIGN), new Attribute("alink", (short)26, AttrCheckImpl.COLOR), new Attribute("alt", (short)3103, AttrCheckImpl.TEXT), new Attribute("archive", (short)28, AttrCheckImpl.URLS), new Attribute("axis", (short)28, AttrCheckImpl.TEXT), new Attribute("background", (short)26, AttrCheckImpl.URL), new Attribute("bgcolor", (short)26, AttrCheckImpl.COLOR), new Attribute("bgproperties", (short)448, AttrCheckImpl.TEXT), new Attribute("border", (short)3103, AttrCheckImpl.BOOL), new Attribute("bordercolor", (short)128, AttrCheckImpl.COLOR), new Attribute("bottommargin", (short)128, AttrCheckImpl.NUMBER), new Attribute("cellpadding", (short)30, AttrCheckImpl.LENGTH), new Attribute("cellspacing", (short)30, AttrCheckImpl.LENGTH), new Attribute("char", (short)28, AttrCheckImpl.CHARACTER), new Attribute("charoff", (short)28, AttrCheckImpl.LENGTH), new Attribute("charset", (short)28, AttrCheckImpl.CHARSET), new Attribute("checked", (short)3103, AttrCheckImpl.BOOL), new Attribute("cite", (short)28, AttrCheckImpl.URL), new Attribute("class", (short)28, AttrCheckImpl.TEXT), new Attribute("classid", (short)28, AttrCheckImpl.URL), new Attribute("clear", (short)26, AttrCheckImpl.CLEAR), new Attribute("code", (short)26, AttrCheckImpl.TEXT), new Attribute("codebase", (short)28, AttrCheckImpl.URL), new Attribute("codetype", (short)28, AttrCheckImpl.TYPE), new Attribute("color", (short)26, AttrCheckImpl.COLOR), new Attribute("cols", (short)24, AttrCheckImpl.COLS), new Attribute("colspan", (short)30, AttrCheckImpl.NUMBER), new Attribute("compact", (short)3103, AttrCheckImpl.BOOL), new Attribute("content", (short)3103, AttrCheckImpl.TEXT), new Attribute("coords", (short)30, AttrCheckImpl.COORDS), new Attribute("data", (short)28, AttrCheckImpl.URL), new Attribute("datafld", (short)128, AttrCheckImpl.TEXT), new Attribute("dataformatas", (short)128, AttrCheckImpl.TEXT), new Attribute("datapagesize", (short)128, AttrCheckImpl.NUMBER), new Attribute("datasrc", (short)128, AttrCheckImpl.URL), new Attribute("datetime", (short)28, AttrCheckImpl.DATE), new Attribute("declare", (short)28, AttrCheckImpl.BOOL), new Attribute("defer", (short)28, AttrCheckImpl.BOOL), new Attribute("dir", (short)28, AttrCheckImpl.TEXTDIR), new Attribute("disabled", (short)28, AttrCheckImpl.BOOL), new Attribute("enctype", (short)3103, AttrCheckImpl.TYPE), new Attribute("face", (short)26, AttrCheckImpl.TEXT), new Attribute("for", (short)28, AttrCheckImpl.IDREF), new Attribute("frame", (short)28, AttrCheckImpl.TFRAME), new Attribute("frameborder", (short)24, AttrCheckImpl.FBORDER), new Attribute("framespacing", (short)448, AttrCheckImpl.NUMBER), new Attribute("gridx", (short)448, AttrCheckImpl.NUMBER), new Attribute("gridy", (short)448, AttrCheckImpl.NUMBER), new Attribute("headers", (short)28, AttrCheckImpl.IDREF), new Attribute("height", (short)3103, AttrCheckImpl.LENGTH), new Attribute("href", (short)3103, AttrCheckImpl.URL), new Attribute("hreflang", (short)28, AttrCheckImpl.LANG), new Attribute("hspace", (short)3103, AttrCheckImpl.NUMBER), new Attribute("http-equiv", (short)3103, AttrCheckImpl.TEXT), new Attribute("id", (short)28, AttrCheckImpl.ID), new Attribute("ismap", (short)3103, AttrCheckImpl.BOOL), new Attribute("label", (short)28, AttrCheckImpl.TEXT), new Attribute("lang", (short)28, AttrCheckImpl.LANG), new Attribute("language", (short)26, AttrCheckImpl.TEXT), new Attribute("last_modified", (short)64, AttrCheckImpl.TEXT), new Attribute("last_visit", (short)64, AttrCheckImpl.TEXT), new Attribute("leftmargin", (short)128, AttrCheckImpl.NUMBER), new Attribute("link", (short)26, AttrCheckImpl.COLOR), new Attribute("longdesc", (short)28, AttrCheckImpl.URL), new Attribute("lowsrc", (short)448, AttrCheckImpl.URL), new Attribute("marginheight", (short)24, AttrCheckImpl.NUMBER), new Attribute("marginwidth", (short)24, AttrCheckImpl.NUMBER), new Attribute("maxlength", (short)3103, AttrCheckImpl.NUMBER), new Attribute("media", (short)28, AttrCheckImpl.MEDIA), new Attribute("method", (short)3103, AttrCheckImpl.FSUBMIT), new Attribute("multiple", (short)3103, AttrCheckImpl.BOOL), new Attribute("name", (short)3103, AttrCheckImpl.NAME), new Attribute("nohref", (short)30, AttrCheckImpl.BOOL), new Attribute("noresize", (short)16, AttrCheckImpl.BOOL), new Attribute("noshade", (short)26, AttrCheckImpl.BOOL), new Attribute("nowrap", (short)26, AttrCheckImpl.BOOL), new Attribute("object", (short)8, AttrCheckImpl.TEXT), new Attribute("onblur", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onchange", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onclick", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("ondblclick", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onkeydown", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onkeypress", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onkeyup", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onload", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onmousedown", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onmousemove", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onmouseout", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onmouseover", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onmouseup", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onsubmit", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onreset", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onselect", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onunload", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onfocus", (short)1052, AttrCheckImpl.SCRIPT), new Attribute("onafterupdate", (short)128, AttrCheckImpl.SCRIPT), new Attribute("onbeforeupdate", (short)128, AttrCheckImpl.SCRIPT), new Attribute("onerrorupdate", (short)128, AttrCheckImpl.SCRIPT), new Attribute("onrowenter", (short)128, AttrCheckImpl.SCRIPT), new Attribute("onrowexit", (short)128, AttrCheckImpl.SCRIPT), new Attribute("onbeforeunload", (short)128, AttrCheckImpl.SCRIPT), new Attribute("ondatasetchanged", (short)128, AttrCheckImpl.SCRIPT), new Attribute("ondataavailable", (short)128, AttrCheckImpl.SCRIPT), new Attribute("ondatasetcomplete", (short)128, AttrCheckImpl.SCRIPT), new Attribute("profile", (short)28, AttrCheckImpl.URL), new Attribute("prompt", (short)26, AttrCheckImpl.TEXT), new Attribute("readonly", (short)28, AttrCheckImpl.BOOL), new Attribute("rel", (short)3103, AttrCheckImpl.LINKTYPES), new Attribute("rev", (short)3103, AttrCheckImpl.LINKTYPES), new Attribute("rightmargin", (short)128, AttrCheckImpl.NUMBER), new Attribute("rows", (short)3103, AttrCheckImpl.NUMBER), new Attribute("rowspan", (short)3103, AttrCheckImpl.NUMBER), new Attribute("rules", (short)28, AttrCheckImpl.TRULES), new Attribute("scheme", (short)28, AttrCheckImpl.TEXT), new Attribute("scope", (short)28, AttrCheckImpl.SCOPE), new Attribute("scrolling", (short)24, AttrCheckImpl.SCROLL), new Attribute("selected", (short)3103, AttrCheckImpl.BOOL), new Attribute("shape", (short)30, AttrCheckImpl.SHAPE), new Attribute("showgrid", (short)448, AttrCheckImpl.BOOL), new Attribute("showgridx", (short)448, AttrCheckImpl.BOOL), new Attribute("showgridy", (short)448, AttrCheckImpl.BOOL), new Attribute("size", (short)26, AttrCheckImpl.NUMBER), new Attribute("span", (short)28, AttrCheckImpl.NUMBER), new Attribute("src", (short)3103, AttrCheckImpl.URL), new Attribute("standby", (short)28, AttrCheckImpl.TEXT), new Attribute("start", (short)3103, AttrCheckImpl.NUMBER), new Attribute("style", (short)28, AttrCheckImpl.TEXT), new Attribute("summary", (short)28, AttrCheckImpl.TEXT), new Attribute("tabindex", (short)28, AttrCheckImpl.NUMBER), new Attribute("target", (short)28, AttrCheckImpl.TARGET), new Attribute("text", (short)26, AttrCheckImpl.COLOR), new Attribute("title", (short)28, AttrCheckImpl.TEXT), new Attribute("topmargin", (short)128, AttrCheckImpl.NUMBER), new Attribute("type", (short)30, AttrCheckImpl.TYPE), new Attribute("usemap", (short)3103, AttrCheckImpl.BOOL), new Attribute("valign", (short)30, AttrCheckImpl.VALIGN), new Attribute("value", (short)3103, AttrCheckImpl.TEXT), new Attribute("valuetype", (short)28, AttrCheckImpl.VTYPE), new Attribute("version", (short)3103, AttrCheckImpl.TEXT), new Attribute("vlink", (short)26, AttrCheckImpl.COLOR), new Attribute("vspace", (short)26, AttrCheckImpl.NUMBER), new Attribute("width", (short)3103, AttrCheckImpl.LENGTH), new Attribute("wrap", (short)64, AttrCheckImpl.TEXT), new Attribute("xml:lang", (short)32, AttrCheckImpl.TEXT), new Attribute("xml:space", (short)32, AttrCheckImpl.TEXT), new Attribute("xmlns", (short)3103, AttrCheckImpl.TEXT), new Attribute("rbspan", (short)1024, AttrCheckImpl.NUMBER) };
    }
}

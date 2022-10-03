package org.w3c.tidy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class AttrCheckImpl
{
    public static final AttrCheck URL;
    public static final AttrCheck SCRIPT;
    public static final AttrCheck NAME;
    public static final AttrCheck ID;
    public static final AttrCheck ALIGN;
    public static final AttrCheck VALIGN;
    public static final AttrCheck BOOL;
    public static final AttrCheck LENGTH;
    public static final AttrCheck TARGET;
    public static final AttrCheck FSUBMIT;
    public static final AttrCheck CLEAR;
    public static final AttrCheck SHAPE;
    public static final AttrCheck NUMBER;
    public static final AttrCheck SCOPE;
    public static final AttrCheck COLOR;
    public static final AttrCheck VTYPE;
    public static final AttrCheck SCROLL;
    public static final AttrCheck TEXTDIR;
    public static final AttrCheck LANG;
    public static final AttrCheck TEXT;
    public static final AttrCheck CHARSET;
    public static final AttrCheck TYPE;
    public static final AttrCheck CHARACTER;
    public static final AttrCheck URLS;
    public static final AttrCheck COLS;
    public static final AttrCheck COORDS;
    public static final AttrCheck DATE;
    public static final AttrCheck IDREF;
    public static final AttrCheck TFRAME;
    public static final AttrCheck FBORDER;
    public static final AttrCheck MEDIA;
    public static final AttrCheck LINKTYPES;
    public static final AttrCheck TRULES;
    
    private AttrCheckImpl() {
    }
    
    static {
        URL = new CheckUrl();
        SCRIPT = new CheckScript();
        NAME = new CheckName();
        ID = new CheckId();
        ALIGN = new CheckAlign();
        VALIGN = new CheckValign();
        BOOL = new CheckBool();
        LENGTH = new CheckLength();
        TARGET = new CheckTarget();
        FSUBMIT = new CheckFsubmit();
        CLEAR = new CheckClear();
        SHAPE = new CheckShape();
        NUMBER = new CheckNumber();
        SCOPE = new CheckScope();
        COLOR = new CheckColor();
        VTYPE = new CheckVType();
        SCROLL = new CheckScroll();
        TEXTDIR = new CheckTextDir();
        LANG = new CheckLang();
        TEXT = null;
        CHARSET = null;
        TYPE = null;
        CHARACTER = null;
        URLS = null;
        COLS = null;
        COORDS = null;
        DATE = null;
        IDREF = null;
        TFRAME = null;
        FBORDER = null;
        MEDIA = null;
        LINKTYPES = null;
        TRULES = null;
    }
    
    public static class CheckAlign implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (node.tag != null && (node.tag.model & 0x10000) != 0x0) {
                AttrCheckImpl.VALIGN.check(lexer, node, attVal);
                return;
            }
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckAlign.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "left", "center", "right", "justify" };
        }
    }
    
    public static class CheckBool implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
        }
    }
    
    public static class CheckClear implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                attVal.value = CheckClear.VALID_VALUES[0];
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckClear.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "none", "left", "right", "all" };
        }
    }
    
    public static class CheckColor implements AttrCheck
    {
        private static final Map COLORS;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            final boolean b = true;
            int n = 0;
            boolean b2 = false;
            if (attVal.value == null || attVal.value.length() == 0) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            final String value = attVal.value;
            final Iterator iterator = CheckColor.COLORS.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                if (value.charAt(0) == '#') {
                    if (value.length() != 7) {
                        lexer.report.attrError(lexer, node, attVal, (short)51);
                        n = 1;
                        break;
                    }
                    if (value.equalsIgnoreCase((String)entry.getValue())) {
                        if (lexer.configuration.replaceColor) {
                            attVal.value = (String)entry.getKey();
                        }
                        b2 = true;
                        break;
                    }
                    continue;
                }
                else {
                    if (!TidyUtils.isLetter(value.charAt(0))) {
                        lexer.report.attrError(lexer, node, attVal, (short)51);
                        n = 1;
                        break;
                    }
                    if (value.equalsIgnoreCase((String)entry.getKey())) {
                        if (lexer.configuration.replaceColor) {
                            attVal.value = (String)entry.getKey();
                        }
                        b2 = true;
                        break;
                    }
                    continue;
                }
            }
            if (!b2 && n == 0) {
                if (value.charAt(0) == '#') {
                    for (int i = 1; i < 7; ++i) {
                        if (!TidyUtils.isDigit(value.charAt(i)) && "abcdef".indexOf(Character.toLowerCase(value.charAt(i))) == -1) {
                            lexer.report.attrError(lexer, node, attVal, (short)51);
                            n = 1;
                            break;
                        }
                    }
                    if (n == 0 && b) {
                        for (int j = 1; j < 7; ++j) {
                            attVal.value = value.toUpperCase();
                        }
                    }
                }
                else {
                    lexer.report.attrError(lexer, node, attVal, (short)51);
                }
            }
        }
        
        static {
            (COLORS = new HashMap()).put("black", "#000000");
            CheckColor.COLORS.put("green", "#008000");
            CheckColor.COLORS.put("silver", "#C0C0C0");
            CheckColor.COLORS.put("lime", "#00FF00");
            CheckColor.COLORS.put("gray", "#808080");
            CheckColor.COLORS.put("olive", "#808000");
            CheckColor.COLORS.put("white", "#FFFFFF");
            CheckColor.COLORS.put("yellow", "#FFFF00");
            CheckColor.COLORS.put("maroon", "#800000");
            CheckColor.COLORS.put("navy", "#000080");
            CheckColor.COLORS.put("red", "#FF0000");
            CheckColor.COLORS.put("blue", "#0000FF");
            CheckColor.COLORS.put("purple", "#800080");
            CheckColor.COLORS.put("teal", "#008080");
            CheckColor.COLORS.put("fuchsia", "#FF00FF");
            CheckColor.COLORS.put("aqua", "#00FFFF");
        }
    }
    
    public static class CheckFsubmit implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckFsubmit.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "get", "post" };
        }
    }
    
    public static class CheckId implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null || attVal.value.length() == 0) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            final String value = attVal.value;
            final char char1 = value.charAt(0);
            if (value.length() == 0 || !Character.isLetter(value.charAt(0))) {
                if (lexer.isvoyager && (TidyUtils.isXMLLetter(char1) || char1 == '_' || char1 == ':')) {
                    lexer.report.attrError(lexer, node, attVal, (short)71);
                }
                else {
                    lexer.report.attrError(lexer, node, attVal, (short)51);
                }
            }
            else {
                int i = 1;
                while (i < value.length()) {
                    final char char2 = value.charAt(i);
                    if (!TidyUtils.isNamechar(char2)) {
                        if (lexer.isvoyager && TidyUtils.isXMLNamechar(char2)) {
                            lexer.report.attrError(lexer, node, attVal, (short)71);
                            break;
                        }
                        lexer.report.attrError(lexer, node, attVal, (short)51);
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            final Node nodeByAnchor;
            if ((nodeByAnchor = lexer.configuration.tt.getNodeByAnchor(attVal.value)) != null && nodeByAnchor != node) {
                lexer.report.attrError(lexer, node, attVal, (short)66);
            }
            else {
                lexer.configuration.tt.anchorList = lexer.configuration.tt.addAnchor(attVal.value, node);
            }
        }
    }
    
    public static class CheckLang implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if ("lang".equals(attVal.attribute)) {
                lexer.constrainVersion(-1025);
            }
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
            }
        }
    }
    
    public static class CheckLength implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            if ("width".equalsIgnoreCase(attVal.attribute) && (node.tag == lexer.configuration.tt.tagCol || node.tag == lexer.configuration.tt.tagColgroup)) {
                return;
            }
            final String value = attVal.value;
            if (value.length() == 0 || (!Character.isDigit(value.charAt(0)) && '%' != value.charAt(0))) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
            else {
                final TagTable tt = lexer.configuration.tt;
                for (int i = 1; i < value.length(); ++i) {
                    if ((!Character.isDigit(value.charAt(i)) && (node.tag == tt.tagTd || node.tag == tt.tagTh)) || (!Character.isDigit(value.charAt(i)) && value.charAt(i) != '%')) {
                        lexer.report.attrError(lexer, node, attVal, (short)51);
                        break;
                    }
                }
            }
        }
    }
    
    public static class CheckName implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            if (lexer.configuration.tt.isAnchorElement(node)) {
                lexer.constrainVersion(-1025);
                final Node nodeByAnchor;
                if ((nodeByAnchor = lexer.configuration.tt.getNodeByAnchor(attVal.value)) != null && nodeByAnchor != node) {
                    lexer.report.attrError(lexer, node, attVal, (short)66);
                }
                else {
                    lexer.configuration.tt.anchorList = lexer.configuration.tt.addAnchor(attVal.value, node);
                }
            }
        }
    }
    
    public static class CheckNumber implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            if (("cols".equalsIgnoreCase(attVal.attribute) || "rows".equalsIgnoreCase(attVal.attribute)) && node.tag == lexer.configuration.tt.tagFrameset) {
                return;
            }
            final String value = attVal.value;
            int i = 0;
            if (node.tag == lexer.configuration.tt.tagFont && (value.startsWith("+") || value.startsWith("-"))) {
                ++i;
            }
            while (i < value.length()) {
                if (!Character.isDigit(value.charAt(i))) {
                    lexer.report.attrError(lexer, node, attVal, (short)51);
                    break;
                }
                ++i;
            }
        }
    }
    
    public static class CheckScope implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckScope.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "row", "rowgroup", "col", "colgroup" };
        }
    }
    
    public static class CheckScript implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
        }
    }
    
    public static class CheckScroll implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckScroll.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "no", "yes", "auto" };
        }
    }
    
    public static class CheckShape implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckShape.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "rect", "default", "circle", "poly" };
        }
    }
    
    public static class CheckTarget implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            lexer.constrainVersion(-5);
            if (attVal.value == null || attVal.value.length() == 0) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            final String value = attVal.value;
            if (Character.isLetter(value.charAt(0))) {
                return;
            }
            if (!TidyUtils.isInValuesIgnoreCase(CheckTarget.VALID_VALUES, value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "_blank", "_self", "_parent", "_top" };
        }
    }
    
    public static class CheckTextDir implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckTextDir.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "rtl", "ltr" };
        }
    }
    
    public static class CheckUrl implements AttrCheck
    {
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            boolean b = false;
            boolean b2 = false;
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            String s = attVal.value;
            for (int i = 0; i < s.length(); ++i) {
                final char char1 = s.charAt(i);
                if (char1 == '\\') {
                    b2 = true;
                }
                else if (char1 > '~' || char1 <= ' ' || char1 == '<' || char1 == '>') {
                    b = true;
                }
            }
            if (lexer.configuration.fixBackslash && b2) {
                attVal.value = attVal.value.replace('\\', '/');
                s = attVal.value;
            }
            if (lexer.configuration.fixUri && b) {
                final StringBuffer sb = new StringBuffer();
                for (int j = 0; j < s.length(); ++j) {
                    final char char2 = s.charAt(j);
                    if (char2 > '~' || char2 <= ' ' || char2 == '<' || char2 == '>') {
                        sb.append('%');
                        sb.append(Integer.toHexString(char2).toUpperCase());
                    }
                    else {
                        sb.append(char2);
                    }
                }
                attVal.value = sb.toString();
            }
            if (b2) {
                if (lexer.configuration.fixBackslash) {
                    lexer.report.attrError(lexer, node, attVal, (short)62);
                }
                else {
                    lexer.report.attrError(lexer, node, attVal, (short)61);
                }
            }
            if (b) {
                if (lexer.configuration.fixUri) {
                    lexer.report.attrError(lexer, node, attVal, (short)64);
                }
                else {
                    lexer.report.attrError(lexer, node, attVal, (short)63);
                }
                lexer.badChars |= 0x51;
            }
        }
    }
    
    public static class CheckVType implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            if (!TidyUtils.isInValuesIgnoreCase(CheckVType.VALID_VALUES, attVal.value)) {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "data", "object", "ref" };
        }
    }
    
    public static class CheckValign implements AttrCheck
    {
        private static final String[] VALID_VALUES;
        private static final String[] VALID_VALUES_IMG;
        private static final String[] VALID_VALUES_PROPRIETARY;
        
        public void check(final Lexer lexer, final Node node, final AttVal attVal) {
            if (attVal.value == null) {
                lexer.report.attrError(lexer, node, attVal, (short)50);
                return;
            }
            attVal.checkLowerCaseAttrValue(lexer, node);
            final String value = attVal.value;
            if (TidyUtils.isInValuesIgnoreCase(CheckValign.VALID_VALUES, value)) {
                return;
            }
            if (TidyUtils.isInValuesIgnoreCase(CheckValign.VALID_VALUES_IMG, value)) {
                if (node.tag == null || (node.tag.model & 0x10000) == 0x0) {
                    lexer.report.attrError(lexer, node, attVal, (short)51);
                }
            }
            else if (TidyUtils.isInValuesIgnoreCase(CheckValign.VALID_VALUES_PROPRIETARY, value)) {
                lexer.constrainVersion(448);
                lexer.report.attrError(lexer, node, attVal, (short)54);
            }
            else {
                lexer.report.attrError(lexer, node, attVal, (short)51);
            }
        }
        
        static {
            VALID_VALUES = new String[] { "top", "middle", "bottom", "baseline" };
            VALID_VALUES_IMG = new String[] { "left", "right" };
            VALID_VALUES_PROPRIETARY = new String[] { "texttop", "absmiddle", "absbottom", "textbottom" };
        }
    }
}

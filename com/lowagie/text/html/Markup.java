package com.lowagie.text.html;

import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Locale;
import java.awt.Color;

public class Markup
{
    public static final String ITEXT_TAG = "tag";
    public static final String HTML_TAG_BODY = "body";
    public static final String HTML_TAG_DIV = "div";
    public static final String HTML_TAG_LINK = "link";
    public static final String HTML_TAG_SPAN = "span";
    public static final String HTML_ATTR_HEIGHT = "height";
    public static final String HTML_ATTR_HREF = "href";
    public static final String HTML_ATTR_REL = "rel";
    public static final String HTML_ATTR_STYLE = "style";
    public static final String HTML_ATTR_TYPE = "type";
    public static final String HTML_ATTR_STYLESHEET = "stylesheet";
    public static final String HTML_ATTR_WIDTH = "width";
    public static final String HTML_ATTR_CSS_CLASS = "class";
    public static final String HTML_ATTR_CSS_ID = "id";
    public static final String HTML_VALUE_JAVASCRIPT = "text/javascript";
    public static final String HTML_VALUE_CSS = "text/css";
    public static final String CSS_KEY_BGCOLOR = "background-color";
    public static final String CSS_KEY_COLOR = "color";
    public static final String CSS_KEY_DISPLAY = "display";
    public static final String CSS_KEY_FONTFAMILY = "font-family";
    public static final String CSS_KEY_FONTSIZE = "font-size";
    public static final String CSS_KEY_FONTSTYLE = "font-style";
    public static final String CSS_KEY_FONTWEIGHT = "font-weight";
    public static final String CSS_KEY_LINEHEIGHT = "line-height";
    public static final String CSS_KEY_MARGIN = "margin";
    public static final String CSS_KEY_MARGINLEFT = "margin-left";
    public static final String CSS_KEY_MARGINRIGHT = "margin-right";
    public static final String CSS_KEY_MARGINTOP = "margin-top";
    public static final String CSS_KEY_MARGINBOTTOM = "margin-bottom";
    public static final String CSS_KEY_PADDING = "padding";
    public static final String CSS_KEY_PADDINGLEFT = "padding-left";
    public static final String CSS_KEY_PADDINGRIGHT = "padding-right";
    public static final String CSS_KEY_PADDINGTOP = "padding-top";
    public static final String CSS_KEY_PADDINGBOTTOM = "padding-bottom";
    public static final String CSS_KEY_BORDERCOLOR = "border-color";
    public static final String CSS_KEY_BORDERWIDTH = "border-width";
    public static final String CSS_KEY_BORDERWIDTHLEFT = "border-left-width";
    public static final String CSS_KEY_BORDERWIDTHRIGHT = "border-right-width";
    public static final String CSS_KEY_BORDERWIDTHTOP = "border-top-width";
    public static final String CSS_KEY_BORDERWIDTHBOTTOM = "border-bottom-width";
    public static final String CSS_KEY_PAGE_BREAK_AFTER = "page-break-after";
    public static final String CSS_KEY_PAGE_BREAK_BEFORE = "page-break-before";
    public static final String CSS_KEY_TEXTALIGN = "text-align";
    public static final String CSS_KEY_TEXTDECORATION = "text-decoration";
    public static final String CSS_KEY_VERTICALALIGN = "vertical-align";
    public static final String CSS_KEY_VISIBILITY = "visibility";
    public static final String CSS_VALUE_ALWAYS = "always";
    public static final String CSS_VALUE_BLOCK = "block";
    public static final String CSS_VALUE_BOLD = "bold";
    public static final String CSS_VALUE_HIDDEN = "hidden";
    public static final String CSS_VALUE_INLINE = "inline";
    public static final String CSS_VALUE_ITALIC = "italic";
    public static final String CSS_VALUE_LINETHROUGH = "line-through";
    public static final String CSS_VALUE_LISTITEM = "list-item";
    public static final String CSS_VALUE_NONE = "none";
    public static final String CSS_VALUE_NORMAL = "normal";
    public static final String CSS_VALUE_OBLIQUE = "oblique";
    public static final String CSS_VALUE_TABLE = "table";
    public static final String CSS_VALUE_TABLEROW = "table-row";
    public static final String CSS_VALUE_TABLECELL = "table-cell";
    public static final String CSS_VALUE_TEXTALIGNLEFT = "left";
    public static final String CSS_VALUE_TEXTALIGNRIGHT = "right";
    public static final String CSS_VALUE_TEXTALIGNCENTER = "center";
    public static final String CSS_VALUE_TEXTALIGNJUSTIFY = "justify";
    public static final String CSS_VALUE_UNDERLINE = "underline";
    public static final float DEFAULT_FONT_SIZE = 12.0f;
    
    public static float parseLength(String string) {
        int pos = 0;
        final int length = string.length();
        boolean ok = true;
        while (ok && pos < length) {
            switch (string.charAt(pos)) {
                case '+':
                case '-':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    ++pos;
                    continue;
                }
                default: {
                    ok = false;
                    continue;
                }
            }
        }
        if (pos == 0) {
            return 0.0f;
        }
        if (pos == length) {
            return Float.parseFloat(string + "f");
        }
        final float f = Float.parseFloat(string.substring(0, pos) + "f");
        string = string.substring(pos);
        if (string.startsWith("in")) {
            return f * 72.0f;
        }
        if (string.startsWith("cm")) {
            return f / 2.54f * 72.0f;
        }
        if (string.startsWith("mm")) {
            return f / 25.4f * 72.0f;
        }
        if (string.startsWith("pc")) {
            return f * 12.0f;
        }
        return f;
    }
    
    public static float parseLength(String string, final float actualFontSize) {
        if (string == null) {
            return 0.0f;
        }
        int pos = 0;
        final int length = string.length();
        boolean ok = true;
        while (ok && pos < length) {
            switch (string.charAt(pos)) {
                case '+':
                case '-':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    ++pos;
                    continue;
                }
                default: {
                    ok = false;
                    continue;
                }
            }
        }
        if (pos == 0) {
            return 0.0f;
        }
        if (pos == length) {
            return Float.parseFloat(string + "f");
        }
        final float f = Float.parseFloat(string.substring(0, pos) + "f");
        string = string.substring(pos);
        if (string.startsWith("in")) {
            return f * 72.0f;
        }
        if (string.startsWith("cm")) {
            return f / 2.54f * 72.0f;
        }
        if (string.startsWith("mm")) {
            return f / 25.4f * 72.0f;
        }
        if (string.startsWith("pc")) {
            return f * 12.0f;
        }
        if (string.startsWith("em")) {
            return f * actualFontSize;
        }
        if (string.startsWith("ex")) {
            return f * actualFontSize / 2.0f;
        }
        return f;
    }
    
    public static Color decodeColor(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase(Locale.ROOT).trim();
        try {
            return WebColors.getRGBColor(s);
        }
        catch (final IllegalArgumentException iae) {
            return null;
        }
    }
    
    public static Properties parseAttributes(final String string) {
        final Properties result = new Properties();
        if (string == null) {
            return result;
        }
        final StringTokenizer keyValuePairs = new StringTokenizer(string, ";");
        while (keyValuePairs.hasMoreTokens()) {
            final StringTokenizer keyValuePair = new StringTokenizer(keyValuePairs.nextToken(), ":");
            if (keyValuePair.hasMoreTokens()) {
                final String key = keyValuePair.nextToken().trim();
                if (!keyValuePair.hasMoreTokens()) {
                    continue;
                }
                String value = keyValuePair.nextToken().trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1);
                }
                if (value.endsWith("\"")) {
                    value = value.substring(0, value.length() - 1);
                }
                result.setProperty(key.toLowerCase(Locale.ROOT), value);
            }
        }
        return result;
    }
    
    public static String removeComment(final String string, final String startComment, final String endComment) {
        final StringBuffer result = new StringBuffer();
        int pos = 0;
        final int end = endComment.length();
        for (int start = string.indexOf(startComment, pos); start > -1; start = string.indexOf(startComment, pos)) {
            result.append(string, pos, start);
            pos = string.indexOf(endComment, start) + end;
        }
        result.append(string.substring(pos));
        return result.toString();
    }
}

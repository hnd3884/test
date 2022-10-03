package javax.swing.text.html;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.SizeRequirements;
import java.util.Enumeration;
import java.util.Vector;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import java.awt.Color;
import java.awt.Font;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleContext;
import javax.swing.text.View;
import javax.swing.text.StyleConstants;
import javax.swing.text.MutableAttributeSet;
import java.util.Hashtable;
import java.io.Serializable;

public class CSS implements Serializable
{
    private static final Hashtable<String, Attribute> attributeMap;
    private static final Hashtable<String, Value> valueMap;
    private static final Hashtable<HTML.Attribute, Attribute[]> htmlAttrToCssAttrMap;
    private static final Hashtable<Object, Attribute> styleConstantToCssMap;
    private static final Hashtable<String, Value> htmlValueToCssValueMap;
    private static final Hashtable<String, Value> cssValueToInternalValueMap;
    private transient Hashtable<Object, Object> valueConvertor;
    private int baseFontSize;
    private transient StyleSheet styleSheet;
    static int baseFontSizeIndex;
    
    public CSS() {
        this.styleSheet = null;
        this.baseFontSize = CSS.baseFontSizeIndex + 1;
        (this.valueConvertor = new Hashtable<Object, Object>()).put(Attribute.FONT_SIZE, new FontSize());
        this.valueConvertor.put(Attribute.FONT_FAMILY, new FontFamily());
        this.valueConvertor.put(Attribute.FONT_WEIGHT, new FontWeight());
        final BorderStyle borderStyle = new BorderStyle();
        this.valueConvertor.put(Attribute.BORDER_TOP_STYLE, borderStyle);
        this.valueConvertor.put(Attribute.BORDER_RIGHT_STYLE, borderStyle);
        this.valueConvertor.put(Attribute.BORDER_BOTTOM_STYLE, borderStyle);
        this.valueConvertor.put(Attribute.BORDER_LEFT_STYLE, borderStyle);
        final ColorValue colorValue = new ColorValue();
        this.valueConvertor.put(Attribute.COLOR, colorValue);
        this.valueConvertor.put(Attribute.BACKGROUND_COLOR, colorValue);
        this.valueConvertor.put(Attribute.BORDER_TOP_COLOR, colorValue);
        this.valueConvertor.put(Attribute.BORDER_RIGHT_COLOR, colorValue);
        this.valueConvertor.put(Attribute.BORDER_BOTTOM_COLOR, colorValue);
        this.valueConvertor.put(Attribute.BORDER_LEFT_COLOR, colorValue);
        final LengthValue lengthValue = new LengthValue();
        this.valueConvertor.put(Attribute.MARGIN_TOP, lengthValue);
        this.valueConvertor.put(Attribute.MARGIN_BOTTOM, lengthValue);
        this.valueConvertor.put(Attribute.MARGIN_LEFT, lengthValue);
        this.valueConvertor.put(Attribute.MARGIN_LEFT_LTR, lengthValue);
        this.valueConvertor.put(Attribute.MARGIN_LEFT_RTL, lengthValue);
        this.valueConvertor.put(Attribute.MARGIN_RIGHT, lengthValue);
        this.valueConvertor.put(Attribute.MARGIN_RIGHT_LTR, lengthValue);
        this.valueConvertor.put(Attribute.MARGIN_RIGHT_RTL, lengthValue);
        this.valueConvertor.put(Attribute.PADDING_TOP, lengthValue);
        this.valueConvertor.put(Attribute.PADDING_BOTTOM, lengthValue);
        this.valueConvertor.put(Attribute.PADDING_LEFT, lengthValue);
        this.valueConvertor.put(Attribute.PADDING_RIGHT, lengthValue);
        final BorderWidthValue borderWidthValue = new BorderWidthValue(null, 0);
        this.valueConvertor.put(Attribute.BORDER_TOP_WIDTH, borderWidthValue);
        this.valueConvertor.put(Attribute.BORDER_BOTTOM_WIDTH, borderWidthValue);
        this.valueConvertor.put(Attribute.BORDER_LEFT_WIDTH, borderWidthValue);
        this.valueConvertor.put(Attribute.BORDER_RIGHT_WIDTH, borderWidthValue);
        this.valueConvertor.put(Attribute.TEXT_INDENT, new LengthValue(true));
        this.valueConvertor.put(Attribute.WIDTH, lengthValue);
        this.valueConvertor.put(Attribute.HEIGHT, lengthValue);
        this.valueConvertor.put(Attribute.BORDER_SPACING, lengthValue);
        final StringValue stringValue = new StringValue();
        this.valueConvertor.put(Attribute.FONT_STYLE, stringValue);
        this.valueConvertor.put(Attribute.TEXT_DECORATION, stringValue);
        this.valueConvertor.put(Attribute.TEXT_ALIGN, stringValue);
        this.valueConvertor.put(Attribute.VERTICAL_ALIGN, stringValue);
        final CssValueMapper cssValueMapper = new CssValueMapper();
        this.valueConvertor.put(Attribute.LIST_STYLE_TYPE, cssValueMapper);
        this.valueConvertor.put(Attribute.BACKGROUND_IMAGE, new BackgroundImage());
        this.valueConvertor.put(Attribute.BACKGROUND_POSITION, new BackgroundPosition());
        this.valueConvertor.put(Attribute.BACKGROUND_REPEAT, cssValueMapper);
        this.valueConvertor.put(Attribute.BACKGROUND_ATTACHMENT, cssValueMapper);
        final CssValue cssValue = new CssValue();
        for (int length = Attribute.allAttributes.length, i = 0; i < length; ++i) {
            final Attribute attribute = Attribute.allAttributes[i];
            if (this.valueConvertor.get(attribute) == null) {
                this.valueConvertor.put(attribute, cssValue);
            }
        }
    }
    
    void setBaseFontSize(final int baseFontSize) {
        if (baseFontSize < 1) {
            this.baseFontSize = 0;
        }
        else if (baseFontSize > 7) {
            this.baseFontSize = 7;
        }
        else {
            this.baseFontSize = baseFontSize;
        }
    }
    
    void setBaseFontSize(final String s) {
        if (s != null) {
            if (s.startsWith("+")) {
                this.setBaseFontSize(this.baseFontSize + Integer.valueOf(s.substring(1)));
            }
            else if (s.startsWith("-")) {
                this.setBaseFontSize(this.baseFontSize + -Integer.valueOf(s.substring(1)));
            }
            else {
                this.setBaseFontSize(Integer.valueOf(s));
            }
        }
    }
    
    int getBaseFontSize() {
        return this.baseFontSize;
    }
    
    void addInternalCSSValue(final MutableAttributeSet set, final Attribute attribute, final String s) {
        if (attribute == Attribute.FONT) {
            ShorthandFontParser.parseShorthandFont(this, s, set);
        }
        else if (attribute == Attribute.BACKGROUND) {
            ShorthandBackgroundParser.parseShorthandBackground(this, s, set);
        }
        else if (attribute == Attribute.MARGIN) {
            ShorthandMarginParser.parseShorthandMargin(this, s, set, Attribute.ALL_MARGINS);
        }
        else if (attribute == Attribute.PADDING) {
            ShorthandMarginParser.parseShorthandMargin(this, s, set, Attribute.ALL_PADDING);
        }
        else if (attribute == Attribute.BORDER_WIDTH) {
            ShorthandMarginParser.parseShorthandMargin(this, s, set, Attribute.ALL_BORDER_WIDTHS);
        }
        else if (attribute == Attribute.BORDER_COLOR) {
            ShorthandMarginParser.parseShorthandMargin(this, s, set, Attribute.ALL_BORDER_COLORS);
        }
        else if (attribute == Attribute.BORDER_STYLE) {
            ShorthandMarginParser.parseShorthandMargin(this, s, set, Attribute.ALL_BORDER_STYLES);
        }
        else if (attribute == Attribute.BORDER || attribute == Attribute.BORDER_TOP || attribute == Attribute.BORDER_RIGHT || attribute == Attribute.BORDER_BOTTOM || attribute == Attribute.BORDER_LEFT) {
            ShorthandBorderParser.parseShorthandBorder(set, attribute, s);
        }
        else {
            final Object internalCSSValue = this.getInternalCSSValue(attribute, s);
            if (internalCSSValue != null) {
                set.addAttribute(attribute, internalCSSValue);
            }
        }
    }
    
    Object getInternalCSSValue(final Attribute attribute, final String s) {
        final CssValue cssValue = this.valueConvertor.get(attribute);
        final Object cssValue2 = cssValue.parseCssValue(s);
        return (cssValue2 != null) ? cssValue2 : cssValue.parseCssValue(attribute.getDefaultValue());
    }
    
    Attribute styleConstantsKeyToCSSKey(final StyleConstants styleConstants) {
        return CSS.styleConstantToCssMap.get(styleConstants);
    }
    
    Object styleConstantsValueToCSSValue(final StyleConstants styleConstants, final Object o) {
        final Attribute styleConstantsKeyToCSSKey = this.styleConstantsKeyToCSSKey(styleConstants);
        if (styleConstantsKeyToCSSKey != null) {
            return ((CssValue)this.valueConvertor.get(styleConstantsKeyToCSSKey)).fromStyleConstants(styleConstants, o);
        }
        return null;
    }
    
    Object cssValueToStyleConstantsValue(final StyleConstants styleConstants, final Object o) {
        if (o instanceof CssValue) {
            return ((CssValue)o).toStyleConstants(styleConstants, null);
        }
        return null;
    }
    
    Font getFont(final StyleContext styleContext, final AttributeSet set, final int n, StyleSheet styleSheet) {
        styleSheet = this.getStyleSheet(styleSheet);
        int fontSize = getFontSize(set, n, styleSheet);
        final StringValue stringValue = (StringValue)set.getAttribute(Attribute.VERTICAL_ALIGN);
        if (stringValue != null) {
            final String string = stringValue.toString();
            if (string.indexOf("sup") >= 0 || string.indexOf("sub") >= 0) {
                fontSize -= 2;
            }
        }
        final FontFamily fontFamily = (FontFamily)set.getAttribute(Attribute.FONT_FAMILY);
        String s = (fontFamily != null) ? fontFamily.getValue() : "SansSerif";
        int n2 = 0;
        final FontWeight fontWeight = (FontWeight)set.getAttribute(Attribute.FONT_WEIGHT);
        if (fontWeight != null && fontWeight.getValue() > 400) {
            n2 |= 0x1;
        }
        final Object attribute = set.getAttribute(Attribute.FONT_STYLE);
        if (attribute != null && attribute.toString().indexOf("italic") >= 0) {
            n2 |= 0x2;
        }
        if (s.equalsIgnoreCase("monospace")) {
            s = "Monospaced";
        }
        Font font = styleContext.getFont(s, n2, fontSize);
        if (font == null || (font.getFamily().equals("Dialog") && !s.equalsIgnoreCase("Dialog"))) {
            font = styleContext.getFont("SansSerif", n2, fontSize);
        }
        return font;
    }
    
    static int getFontSize(final AttributeSet set, final int n, final StyleSheet styleSheet) {
        final FontSize fontSize = (FontSize)set.getAttribute(Attribute.FONT_SIZE);
        return (fontSize != null) ? fontSize.getValue(set, styleSheet) : n;
    }
    
    Color getColor(final AttributeSet set, final Attribute attribute) {
        final ColorValue colorValue = (ColorValue)set.getAttribute(attribute);
        if (colorValue != null) {
            return colorValue.getValue();
        }
        return null;
    }
    
    float getPointSize(final String s, StyleSheet styleSheet) {
        styleSheet = this.getStyleSheet(styleSheet);
        if (s == null) {
            return 0.0f;
        }
        if (s.startsWith("+")) {
            return this.getPointSize(this.baseFontSize + Integer.valueOf(s.substring(1)), styleSheet);
        }
        if (s.startsWith("-")) {
            return this.getPointSize(this.baseFontSize + -Integer.valueOf(s.substring(1)), styleSheet);
        }
        return this.getPointSize(Integer.valueOf(s), styleSheet);
    }
    
    float getLength(final AttributeSet set, final Attribute attribute, StyleSheet styleSheet) {
        styleSheet = this.getStyleSheet(styleSheet);
        final LengthValue lengthValue = (LengthValue)set.getAttribute(attribute);
        final boolean b = styleSheet != null && styleSheet.isW3CLengthUnits();
        return (lengthValue != null) ? lengthValue.getValue(b) : 0.0f;
    }
    
    AttributeSet translateHTMLToCSS(final AttributeSet set) {
        final SimpleAttributeSet set2 = new SimpleAttributeSet();
        final Element element = (Element)set;
        final HTML.Tag htmlTag = this.getHTMLTag(set);
        if (htmlTag == HTML.Tag.TD || htmlTag == HTML.Tag.TH) {
            final AttributeSet attributes = element.getParentElement().getParentElement().getAttributes();
            if (getTableBorder(attributes) > 0) {
                this.translateAttribute(HTML.Attribute.BORDER, "1", set2);
            }
            final String s = (String)attributes.getAttribute(HTML.Attribute.CELLPADDING);
            if (s != null) {
                final LengthValue lengthValue = (LengthValue)this.getInternalCSSValue(Attribute.PADDING_TOP, s);
                lengthValue.span = ((lengthValue.span < 0.0f) ? 0.0f : lengthValue.span);
                set2.addAttribute(Attribute.PADDING_TOP, lengthValue);
                set2.addAttribute(Attribute.PADDING_BOTTOM, lengthValue);
                set2.addAttribute(Attribute.PADDING_LEFT, lengthValue);
                set2.addAttribute(Attribute.PADDING_RIGHT, lengthValue);
            }
        }
        if (element.isLeaf()) {
            this.translateEmbeddedAttributes(set, set2);
        }
        else {
            this.translateAttributes(htmlTag, set, set2);
        }
        if (htmlTag == HTML.Tag.CAPTION) {
            final Object attribute = set.getAttribute(HTML.Attribute.ALIGN);
            if (attribute != null && (attribute.equals("top") || attribute.equals("bottom"))) {
                set2.addAttribute(Attribute.CAPTION_SIDE, attribute);
                set2.removeAttribute(Attribute.TEXT_ALIGN);
            }
            else {
                final Object attribute2 = set.getAttribute(HTML.Attribute.VALIGN);
                if (attribute2 != null) {
                    set2.addAttribute(Attribute.CAPTION_SIDE, attribute2);
                }
            }
        }
        return set2;
    }
    
    private static int getTableBorder(final AttributeSet set) {
        final String s = (String)set.getAttribute(HTML.Attribute.BORDER);
        if (s == "#DEFAULT" || "".equals(s)) {
            return 1;
        }
        try {
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            return 0;
        }
    }
    
    public static Attribute[] getAllAttributeKeys() {
        final Attribute[] array = new Attribute[Attribute.allAttributes.length];
        System.arraycopy(Attribute.allAttributes, 0, array, 0, Attribute.allAttributes.length);
        return array;
    }
    
    public static final Attribute getAttribute(final String s) {
        return CSS.attributeMap.get(s);
    }
    
    static final Value getValue(final String s) {
        return CSS.valueMap.get(s);
    }
    
    static URL getURL(final URL url, String substring) {
        if (substring == null) {
            return null;
        }
        if (substring.startsWith("url(") && substring.endsWith(")")) {
            substring = substring.substring(4, substring.length() - 1);
        }
        try {
            final URL url2 = new URL(substring);
            if (url2 != null) {
                return url2;
            }
        }
        catch (final MalformedURLException ex) {}
        if (url != null) {
            try {
                return new URL(url, substring);
            }
            catch (final MalformedURLException ex2) {}
        }
        return null;
    }
    
    static String colorToHex(final Color color) {
        String s = "#";
        final String hexString = Integer.toHexString(color.getRed());
        if (hexString.length() > 2) {
            hexString.substring(0, 2);
        }
        else if (hexString.length() < 2) {
            s = s + "0" + hexString;
        }
        else {
            s += hexString;
        }
        final String hexString2 = Integer.toHexString(color.getGreen());
        if (hexString2.length() > 2) {
            hexString2.substring(0, 2);
        }
        else if (hexString2.length() < 2) {
            s = s + "0" + hexString2;
        }
        else {
            s += hexString2;
        }
        final String hexString3 = Integer.toHexString(color.getBlue());
        if (hexString3.length() > 2) {
            hexString3.substring(0, 2);
        }
        else if (hexString3.length() < 2) {
            s = s + "0" + hexString3;
        }
        else {
            s += hexString3;
        }
        return s;
    }
    
    static final Color hexToColor(final String s) {
        s.length();
        String substring;
        if (s.startsWith("#")) {
            substring = s.substring(1, Math.min(s.length(), 7));
        }
        else {
            substring = s;
        }
        final String string = "0x" + substring;
        Color decode;
        try {
            decode = Color.decode(string);
        }
        catch (final NumberFormatException ex) {
            decode = null;
        }
        return decode;
    }
    
    static Color stringToColor(final String s) {
        if (s == null) {
            return null;
        }
        Color color;
        if (s.length() == 0) {
            color = Color.black;
        }
        else if (s.startsWith("rgb(")) {
            color = parseRGB(s);
        }
        else if (s.charAt(0) == '#') {
            color = hexToColor(s);
        }
        else if (s.equalsIgnoreCase("Black")) {
            color = hexToColor("#000000");
        }
        else if (s.equalsIgnoreCase("Silver")) {
            color = hexToColor("#C0C0C0");
        }
        else if (s.equalsIgnoreCase("Gray")) {
            color = hexToColor("#808080");
        }
        else if (s.equalsIgnoreCase("White")) {
            color = hexToColor("#FFFFFF");
        }
        else if (s.equalsIgnoreCase("Maroon")) {
            color = hexToColor("#800000");
        }
        else if (s.equalsIgnoreCase("Red")) {
            color = hexToColor("#FF0000");
        }
        else if (s.equalsIgnoreCase("Purple")) {
            color = hexToColor("#800080");
        }
        else if (s.equalsIgnoreCase("Fuchsia")) {
            color = hexToColor("#FF00FF");
        }
        else if (s.equalsIgnoreCase("Green")) {
            color = hexToColor("#008000");
        }
        else if (s.equalsIgnoreCase("Lime")) {
            color = hexToColor("#00FF00");
        }
        else if (s.equalsIgnoreCase("Olive")) {
            color = hexToColor("#808000");
        }
        else if (s.equalsIgnoreCase("Yellow")) {
            color = hexToColor("#FFFF00");
        }
        else if (s.equalsIgnoreCase("Navy")) {
            color = hexToColor("#000080");
        }
        else if (s.equalsIgnoreCase("Blue")) {
            color = hexToColor("#0000FF");
        }
        else if (s.equalsIgnoreCase("Teal")) {
            color = hexToColor("#008080");
        }
        else if (s.equalsIgnoreCase("Aqua")) {
            color = hexToColor("#00FFFF");
        }
        else if (s.equalsIgnoreCase("Orange")) {
            color = hexToColor("#FF8000");
        }
        else {
            color = hexToColor(s);
        }
        return color;
    }
    
    private static Color parseRGB(final String s) {
        final int[] array = { 4 };
        return new Color(getColorComponent(s, array), getColorComponent(s, array), getColorComponent(s, array));
    }
    
    private static int getColorComponent(final String s, final int[] array) {
        final int length = s.length();
        char char1;
        while (array[0] < length && (char1 = s.charAt(array[0])) != '-' && !Character.isDigit(char1) && char1 != '.') {
            final int n = 0;
            ++array[n];
        }
        final int n2 = array[0];
        if (n2 < length && s.charAt(array[0]) == '-') {
            final int n3 = 0;
            ++array[n3];
        }
        while (array[0] < length && Character.isDigit(s.charAt(array[0]))) {
            final int n4 = 0;
            ++array[n4];
        }
        if (array[0] < length && s.charAt(array[0]) == '.') {
            final int n5 = 0;
            ++array[n5];
            while (array[0] < length && Character.isDigit(s.charAt(array[0]))) {
                final int n6 = 0;
                ++array[n6];
            }
        }
        if (n2 != array[0]) {
            try {
                float float1 = Float.parseFloat(s.substring(n2, array[0]));
                if (array[0] < length && s.charAt(array[0]) == '%') {
                    final int n7 = 0;
                    ++array[n7];
                    float1 = float1 * 255.0f / 100.0f;
                }
                return Math.min(255, Math.max(0, (int)float1));
            }
            catch (final NumberFormatException ex) {}
        }
        return 0;
    }
    
    static int getIndexOfSize(final float n, final int[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (n <= array[i]) {
                return i + 1;
            }
        }
        return array.length;
    }
    
    static int getIndexOfSize(final float n, final StyleSheet styleSheet) {
        return getIndexOfSize(n, (styleSheet != null) ? styleSheet.getSizeMap() : StyleSheet.sizeMapDefault);
    }
    
    static String[] parseStrings(final String s) {
        final int n = (s == null) ? 0 : s.length();
        final Vector vector = new Vector(4);
        for (int i = 0; i < n; ++i) {
            while (i < n && Character.isWhitespace(s.charAt(i))) {
                ++i;
            }
            final int n2 = i;
            while (i < n && !Character.isWhitespace(s.charAt(i))) {
                ++i;
            }
            if (n2 != i) {
                vector.addElement(s.substring(n2, i));
            }
        }
        final String[] array = new String[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    float getPointSize(int n, StyleSheet styleSheet) {
        styleSheet = this.getStyleSheet(styleSheet);
        final int[] array = (styleSheet != null) ? styleSheet.getSizeMap() : StyleSheet.sizeMapDefault;
        if (--n < 0) {
            return (float)array[0];
        }
        if (n > array.length - 1) {
            return (float)array[array.length - 1];
        }
        return (float)array[n];
    }
    
    private void translateEmbeddedAttributes(final AttributeSet set, final MutableAttributeSet set2) {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        if (set.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.HR) {
            this.translateAttributes(HTML.Tag.HR, set, set2);
        }
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (nextElement instanceof HTML.Tag) {
                final HTML.Tag tag = (HTML.Tag)nextElement;
                final Object attribute = set.getAttribute(tag);
                if (attribute == null || !(attribute instanceof AttributeSet)) {
                    continue;
                }
                this.translateAttributes(tag, (AttributeSet)attribute, set2);
            }
            else {
                if (!(nextElement instanceof Attribute)) {
                    continue;
                }
                set2.addAttribute(nextElement, set.getAttribute(nextElement));
            }
        }
    }
    
    private void translateAttributes(final HTML.Tag tag, final AttributeSet set, final MutableAttributeSet set2) {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (nextElement instanceof HTML.Attribute) {
                final HTML.Attribute attribute = (HTML.Attribute)nextElement;
                if (attribute == HTML.Attribute.ALIGN) {
                    final String s = (String)set.getAttribute(HTML.Attribute.ALIGN);
                    if (s == null) {
                        continue;
                    }
                    final Attribute cssAlignAttribute = this.getCssAlignAttribute(tag, set);
                    if (cssAlignAttribute == null) {
                        continue;
                    }
                    final Object cssValue = this.getCssValue(cssAlignAttribute, s);
                    if (cssValue == null) {
                        continue;
                    }
                    set2.addAttribute(cssAlignAttribute, cssValue);
                }
                else {
                    if (attribute == HTML.Attribute.SIZE && !this.isHTMLFontTag(tag)) {
                        continue;
                    }
                    if (tag == HTML.Tag.TABLE && attribute == HTML.Attribute.BORDER) {
                        final int tableBorder = getTableBorder(set);
                        if (tableBorder <= 0) {
                            continue;
                        }
                        this.translateAttribute(HTML.Attribute.BORDER, Integer.toString(tableBorder), set2);
                    }
                    else {
                        this.translateAttribute(attribute, (String)set.getAttribute(attribute), set2);
                    }
                }
            }
            else {
                if (!(nextElement instanceof Attribute)) {
                    continue;
                }
                set2.addAttribute(nextElement, set.getAttribute(nextElement));
            }
        }
    }
    
    private void translateAttribute(final HTML.Attribute attribute, final String s, final MutableAttributeSet set) {
        final Attribute[] cssAttribute = this.getCssAttribute(attribute);
        if (cssAttribute == null || s == null) {
            return;
        }
        for (final Attribute attribute2 : cssAttribute) {
            final Object cssValue = this.getCssValue(attribute2, s);
            if (cssValue != null) {
                set.addAttribute(attribute2, cssValue);
            }
        }
    }
    
    Object getCssValue(final Attribute attribute, final String s) {
        return this.valueConvertor.get(attribute).parseHtmlValue(s);
    }
    
    private Attribute[] getCssAttribute(final HTML.Attribute attribute) {
        return CSS.htmlAttrToCssAttrMap.get(attribute);
    }
    
    private Attribute getCssAlignAttribute(final HTML.Tag tag, final AttributeSet set) {
        return Attribute.TEXT_ALIGN;
    }
    
    private HTML.Tag getHTMLTag(final AttributeSet set) {
        final Object attribute = set.getAttribute(StyleConstants.NameAttribute);
        if (attribute instanceof HTML.Tag) {
            return (HTML.Tag)attribute;
        }
        return null;
    }
    
    private boolean isHTMLFontTag(final HTML.Tag tag) {
        return tag != null && (tag == HTML.Tag.FONT || tag == HTML.Tag.BASEFONT);
    }
    
    private boolean isFloater(final String s) {
        return s.equals("left") || s.equals("right");
    }
    
    private boolean validTextAlignValue(final String s) {
        return this.isFloater(s) || s.equals("center");
    }
    
    static SizeRequirements calculateTiledRequirements(final LayoutIterator layoutIterator, SizeRequirements sizeRequirements) {
        long n = 0L;
        long n2 = 0L;
        long n3 = 0L;
        int n4 = 0;
        int n5 = 0;
        for (int count = layoutIterator.getCount(), i = 0; i < count; ++i) {
            layoutIterator.setIndex(i);
            n5 += Math.max(n4, (int)layoutIterator.getLeadingCollapseSpan());
            n3 += (int)layoutIterator.getPreferredSpan(0.0f);
            n += (long)layoutIterator.getMinimumSpan(0.0f);
            n2 += (long)layoutIterator.getMaximumSpan(0.0f);
            n4 = (int)layoutIterator.getTrailingCollapseSpan();
        }
        final int n6 = (int)(n5 + n4 + 2.0f * layoutIterator.getBorderWidth());
        final long n7 = n + n6;
        final long n8 = n3 + n6;
        final long n9 = n2 + n6;
        if (sizeRequirements == null) {
            sizeRequirements = new SizeRequirements();
        }
        sizeRequirements.minimum = ((n7 > 2147483647L) ? Integer.MAX_VALUE : ((int)n7));
        sizeRequirements.preferred = ((n8 > 2147483647L) ? Integer.MAX_VALUE : ((int)n8));
        sizeRequirements.maximum = ((n9 > 2147483647L) ? Integer.MAX_VALUE : ((int)n9));
        return sizeRequirements;
    }
    
    static void calculateTiledLayout(final LayoutIterator layoutIterator, final int n) {
        long n2 = 0L;
        int n3 = 0;
        int n4 = 0;
        final int count = layoutIterator.getCount();
        final int n5 = 3;
        final long[] array = new long[n5];
        final long[] array2 = new long[n5];
        for (int i = 0; i < n5; ++i) {
            array[i] = (array2[i] = 0L);
        }
        for (int j = 0; j < count; ++j) {
            layoutIterator.setIndex(j);
            layoutIterator.setOffset(Math.max(n3, (int)layoutIterator.getLeadingCollapseSpan()));
            n4 += layoutIterator.getOffset();
            final long n6 = (long)layoutIterator.getPreferredSpan((float)n);
            layoutIterator.setSpan((int)n6);
            n2 += n6;
            final long[] array3 = array;
            final int adjustmentWeight = layoutIterator.getAdjustmentWeight();
            array3[adjustmentWeight] += (long)layoutIterator.getMaximumSpan((float)n) - n6;
            final long[] array4 = array2;
            final int adjustmentWeight2 = layoutIterator.getAdjustmentWeight();
            array4[adjustmentWeight2] += n6 - (long)layoutIterator.getMinimumSpan((float)n);
            n3 = (int)layoutIterator.getTrailingCollapseSpan();
        }
        final int n7 = (int)(n4 + n3 + 2.0f * layoutIterator.getBorderWidth());
        for (int k = 1; k < n5; ++k) {
            final long[] array5 = array;
            final int n8 = k;
            array5[n8] += array[k - 1];
            final long[] array6 = array2;
            final int n9 = k;
            array6[n9] += array2[k - 1];
        }
        final int n10 = n - n7;
        final long n11 = n10 - n2;
        long[] array7;
        long abs;
        int n12;
        for (array7 = ((n11 > 0L) ? array : array2), abs = Math.abs(n11), n12 = 0; n12 <= 2 && array7[n12] < abs; ++n12) {}
        float n13 = 0.0f;
        if (n12 <= 2) {
            final long n14 = abs - ((n12 > 0) ? array7[n12 - 1] : 0L);
            if (n14 != 0L) {
                n13 = n14 / (float)(array7[n12] - ((n12 > 0) ? array7[n12 - 1] : 0L));
            }
        }
        int n15 = (int)layoutIterator.getBorderWidth();
        for (int l = 0; l < count; ++l) {
            layoutIterator.setIndex(l);
            layoutIterator.setOffset(layoutIterator.getOffset() + n15);
            if (layoutIterator.getAdjustmentWeight() < n12) {
                layoutIterator.setSpan((int)((n10 > n2) ? Math.floor(layoutIterator.getMaximumSpan((float)n)) : Math.ceil(layoutIterator.getMinimumSpan((float)n))));
            }
            else if (layoutIterator.getAdjustmentWeight() == n12) {
                final int n16 = (int)Math.floor(n13 * ((n10 > n2) ? ((int)layoutIterator.getMaximumSpan((float)n) - layoutIterator.getSpan()) : (layoutIterator.getSpan() - (int)layoutIterator.getMinimumSpan((float)n))));
                layoutIterator.setSpan(layoutIterator.getSpan() + ((n10 > n2) ? n16 : (-n16)));
            }
            n15 = (int)Math.min(layoutIterator.getOffset() + (long)layoutIterator.getSpan(), 2147483647L);
        }
        final int n17 = n - n15 - (int)layoutIterator.getTrailingCollapseSpan() - (int)layoutIterator.getBorderWidth();
        final int n18 = (n17 > 0) ? 1 : -1;
        int n19 = n17 * n18;
        int n20 = 1;
        while (n19 > 0 && n20 != 0) {
            n20 = 0;
            int n21 = 0;
            for (int index = 0; index < count; ++index) {
                layoutIterator.setIndex(index);
                layoutIterator.setOffset(layoutIterator.getOffset() + n21);
                final int span = layoutIterator.getSpan();
                if (n19 > 0 && ((n18 > 0) ? ((int)Math.floor(layoutIterator.getMaximumSpan((float)n)) - span) : (span - (int)Math.ceil(layoutIterator.getMinimumSpan((float)n)))) >= 1) {
                    n20 = 1;
                    layoutIterator.setSpan(span + n18);
                    n21 += n18;
                    --n19;
                }
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        final Enumeration<Object> keys = this.valueConvertor.keys();
        objectOutputStream.writeInt(this.valueConvertor.size());
        if (keys != null) {
            while (keys.hasMoreElements()) {
                Object o = keys.nextElement();
                Object o2 = this.valueConvertor.get(o);
                if (!(o instanceof Serializable) && (o = StyleContext.getStaticAttributeKey(o)) == null) {
                    o = null;
                    o2 = null;
                }
                else if (!(o2 instanceof Serializable) && (o2 = StyleContext.getStaticAttributeKey(o2)) == null) {
                    o = null;
                    o2 = null;
                }
                objectOutputStream.writeObject(o);
                objectOutputStream.writeObject(o2);
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        int int1 = objectInputStream.readInt();
        this.valueConvertor = new Hashtable<Object, Object>();
        while (int1-- > 0) {
            Object object = objectInputStream.readObject();
            Object object2 = objectInputStream.readObject();
            final Object staticAttribute = StyleContext.getStaticAttribute(object);
            if (staticAttribute != null) {
                object = staticAttribute;
            }
            final Object staticAttribute2 = StyleContext.getStaticAttribute(object2);
            if (staticAttribute2 != null) {
                object2 = staticAttribute2;
            }
            if (object != null && object2 != null) {
                this.valueConvertor.put(object, object2);
            }
        }
    }
    
    private StyleSheet getStyleSheet(final StyleSheet styleSheet) {
        if (styleSheet != null) {
            this.styleSheet = styleSheet;
        }
        return this.styleSheet;
    }
    
    static {
        attributeMap = new Hashtable<String, Attribute>();
        valueMap = new Hashtable<String, Value>();
        htmlAttrToCssAttrMap = new Hashtable<HTML.Attribute, Attribute[]>(20);
        styleConstantToCssMap = new Hashtable<Object, Attribute>(17);
        htmlValueToCssValueMap = new Hashtable<String, Value>(8);
        cssValueToInternalValueMap = new Hashtable<String, Value>(13);
        for (int i = 0; i < Attribute.allAttributes.length; ++i) {
            CSS.attributeMap.put(Attribute.allAttributes[i].toString(), Attribute.allAttributes[i]);
        }
        for (int j = 0; j < Value.allValues.length; ++j) {
            CSS.valueMap.put(Value.allValues[j].toString(), Value.allValues[j]);
        }
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.COLOR, new Attribute[] { Attribute.COLOR });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.TEXT, new Attribute[] { Attribute.COLOR });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.CLEAR, new Attribute[] { Attribute.CLEAR });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.BACKGROUND, new Attribute[] { Attribute.BACKGROUND_IMAGE });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.BGCOLOR, new Attribute[] { Attribute.BACKGROUND_COLOR });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.WIDTH, new Attribute[] { Attribute.WIDTH });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.HEIGHT, new Attribute[] { Attribute.HEIGHT });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.BORDER, new Attribute[] { Attribute.BORDER_TOP_WIDTH, Attribute.BORDER_RIGHT_WIDTH, Attribute.BORDER_BOTTOM_WIDTH, Attribute.BORDER_LEFT_WIDTH });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.CELLPADDING, new Attribute[] { Attribute.PADDING });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.CELLSPACING, new Attribute[] { Attribute.BORDER_SPACING });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINWIDTH, new Attribute[] { Attribute.MARGIN_LEFT, Attribute.MARGIN_RIGHT });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINHEIGHT, new Attribute[] { Attribute.MARGIN_TOP, Attribute.MARGIN_BOTTOM });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.HSPACE, new Attribute[] { Attribute.PADDING_LEFT, Attribute.PADDING_RIGHT });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.VSPACE, new Attribute[] { Attribute.PADDING_BOTTOM, Attribute.PADDING_TOP });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.FACE, new Attribute[] { Attribute.FONT_FAMILY });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.SIZE, new Attribute[] { Attribute.FONT_SIZE });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.VALIGN, new Attribute[] { Attribute.VERTICAL_ALIGN });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.ALIGN, new Attribute[] { Attribute.VERTICAL_ALIGN, Attribute.TEXT_ALIGN, Attribute.FLOAT });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.TYPE, new Attribute[] { Attribute.LIST_STYLE_TYPE });
        CSS.htmlAttrToCssAttrMap.put(HTML.Attribute.NOWRAP, new Attribute[] { Attribute.WHITE_SPACE });
        CSS.styleConstantToCssMap.put(StyleConstants.FontFamily, Attribute.FONT_FAMILY);
        CSS.styleConstantToCssMap.put(StyleConstants.FontSize, Attribute.FONT_SIZE);
        CSS.styleConstantToCssMap.put(StyleConstants.Bold, Attribute.FONT_WEIGHT);
        CSS.styleConstantToCssMap.put(StyleConstants.Italic, Attribute.FONT_STYLE);
        CSS.styleConstantToCssMap.put(StyleConstants.Underline, Attribute.TEXT_DECORATION);
        CSS.styleConstantToCssMap.put(StyleConstants.StrikeThrough, Attribute.TEXT_DECORATION);
        CSS.styleConstantToCssMap.put(StyleConstants.Superscript, Attribute.VERTICAL_ALIGN);
        CSS.styleConstantToCssMap.put(StyleConstants.Subscript, Attribute.VERTICAL_ALIGN);
        CSS.styleConstantToCssMap.put(StyleConstants.Foreground, Attribute.COLOR);
        CSS.styleConstantToCssMap.put(StyleConstants.Background, Attribute.BACKGROUND_COLOR);
        CSS.styleConstantToCssMap.put(StyleConstants.FirstLineIndent, Attribute.TEXT_INDENT);
        CSS.styleConstantToCssMap.put(StyleConstants.LeftIndent, Attribute.MARGIN_LEFT);
        CSS.styleConstantToCssMap.put(StyleConstants.RightIndent, Attribute.MARGIN_RIGHT);
        CSS.styleConstantToCssMap.put(StyleConstants.SpaceAbove, Attribute.MARGIN_TOP);
        CSS.styleConstantToCssMap.put(StyleConstants.SpaceBelow, Attribute.MARGIN_BOTTOM);
        CSS.styleConstantToCssMap.put(StyleConstants.Alignment, Attribute.TEXT_ALIGN);
        CSS.htmlValueToCssValueMap.put("disc", Value.DISC);
        CSS.htmlValueToCssValueMap.put("square", Value.SQUARE);
        CSS.htmlValueToCssValueMap.put("circle", Value.CIRCLE);
        CSS.htmlValueToCssValueMap.put("1", Value.DECIMAL);
        CSS.htmlValueToCssValueMap.put("a", Value.LOWER_ALPHA);
        CSS.htmlValueToCssValueMap.put("A", Value.UPPER_ALPHA);
        CSS.htmlValueToCssValueMap.put("i", Value.LOWER_ROMAN);
        CSS.htmlValueToCssValueMap.put("I", Value.UPPER_ROMAN);
        CSS.cssValueToInternalValueMap.put("none", Value.NONE);
        CSS.cssValueToInternalValueMap.put("disc", Value.DISC);
        CSS.cssValueToInternalValueMap.put("square", Value.SQUARE);
        CSS.cssValueToInternalValueMap.put("circle", Value.CIRCLE);
        CSS.cssValueToInternalValueMap.put("decimal", Value.DECIMAL);
        CSS.cssValueToInternalValueMap.put("lower-roman", Value.LOWER_ROMAN);
        CSS.cssValueToInternalValueMap.put("upper-roman", Value.UPPER_ROMAN);
        CSS.cssValueToInternalValueMap.put("lower-alpha", Value.LOWER_ALPHA);
        CSS.cssValueToInternalValueMap.put("upper-alpha", Value.UPPER_ALPHA);
        CSS.cssValueToInternalValueMap.put("repeat", Value.BACKGROUND_REPEAT);
        CSS.cssValueToInternalValueMap.put("no-repeat", Value.BACKGROUND_NO_REPEAT);
        CSS.cssValueToInternalValueMap.put("repeat-x", Value.BACKGROUND_REPEAT_X);
        CSS.cssValueToInternalValueMap.put("repeat-y", Value.BACKGROUND_REPEAT_Y);
        CSS.cssValueToInternalValueMap.put("scroll", Value.BACKGROUND_SCROLL);
        CSS.cssValueToInternalValueMap.put("fixed", Value.BACKGROUND_FIXED);
        final Attribute[] allAttributes = Attribute.allAttributes;
        try {
            final Attribute[] array = allAttributes;
            for (int length = array.length, k = 0; k < length; ++k) {
                StyleContext.registerStaticAttributeKey(array[k]);
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
        }
        final Value[] allValues = Value.allValues;
        try {
            final Value[] array2 = allValues;
            for (int length2 = array2.length, l = 0; l < length2; ++l) {
                StyleContext.registerStaticAttributeKey(array2[l]);
            }
        }
        catch (final Throwable t2) {
            t2.printStackTrace();
        }
        CSS.baseFontSizeIndex = 3;
    }
    
    public static final class Attribute
    {
        private String name;
        private String defaultValue;
        private boolean inherited;
        public static final Attribute BACKGROUND;
        public static final Attribute BACKGROUND_ATTACHMENT;
        public static final Attribute BACKGROUND_COLOR;
        public static final Attribute BACKGROUND_IMAGE;
        public static final Attribute BACKGROUND_POSITION;
        public static final Attribute BACKGROUND_REPEAT;
        public static final Attribute BORDER;
        public static final Attribute BORDER_BOTTOM;
        public static final Attribute BORDER_BOTTOM_COLOR;
        public static final Attribute BORDER_BOTTOM_STYLE;
        public static final Attribute BORDER_BOTTOM_WIDTH;
        public static final Attribute BORDER_COLOR;
        public static final Attribute BORDER_LEFT;
        public static final Attribute BORDER_LEFT_COLOR;
        public static final Attribute BORDER_LEFT_STYLE;
        public static final Attribute BORDER_LEFT_WIDTH;
        public static final Attribute BORDER_RIGHT;
        public static final Attribute BORDER_RIGHT_COLOR;
        public static final Attribute BORDER_RIGHT_STYLE;
        public static final Attribute BORDER_RIGHT_WIDTH;
        public static final Attribute BORDER_STYLE;
        public static final Attribute BORDER_TOP;
        public static final Attribute BORDER_TOP_COLOR;
        public static final Attribute BORDER_TOP_STYLE;
        public static final Attribute BORDER_TOP_WIDTH;
        public static final Attribute BORDER_WIDTH;
        public static final Attribute CLEAR;
        public static final Attribute COLOR;
        public static final Attribute DISPLAY;
        public static final Attribute FLOAT;
        public static final Attribute FONT;
        public static final Attribute FONT_FAMILY;
        public static final Attribute FONT_SIZE;
        public static final Attribute FONT_STYLE;
        public static final Attribute FONT_VARIANT;
        public static final Attribute FONT_WEIGHT;
        public static final Attribute HEIGHT;
        public static final Attribute LETTER_SPACING;
        public static final Attribute LINE_HEIGHT;
        public static final Attribute LIST_STYLE;
        public static final Attribute LIST_STYLE_IMAGE;
        public static final Attribute LIST_STYLE_POSITION;
        public static final Attribute LIST_STYLE_TYPE;
        public static final Attribute MARGIN;
        public static final Attribute MARGIN_BOTTOM;
        public static final Attribute MARGIN_LEFT;
        public static final Attribute MARGIN_RIGHT;
        static final Attribute MARGIN_LEFT_LTR;
        static final Attribute MARGIN_LEFT_RTL;
        static final Attribute MARGIN_RIGHT_LTR;
        static final Attribute MARGIN_RIGHT_RTL;
        public static final Attribute MARGIN_TOP;
        public static final Attribute PADDING;
        public static final Attribute PADDING_BOTTOM;
        public static final Attribute PADDING_LEFT;
        public static final Attribute PADDING_RIGHT;
        public static final Attribute PADDING_TOP;
        public static final Attribute TEXT_ALIGN;
        public static final Attribute TEXT_DECORATION;
        public static final Attribute TEXT_INDENT;
        public static final Attribute TEXT_TRANSFORM;
        public static final Attribute VERTICAL_ALIGN;
        public static final Attribute WORD_SPACING;
        public static final Attribute WHITE_SPACE;
        public static final Attribute WIDTH;
        static final Attribute BORDER_SPACING;
        static final Attribute CAPTION_SIDE;
        static final Attribute[] allAttributes;
        private static final Attribute[] ALL_MARGINS;
        private static final Attribute[] ALL_PADDING;
        private static final Attribute[] ALL_BORDER_WIDTHS;
        private static final Attribute[] ALL_BORDER_STYLES;
        private static final Attribute[] ALL_BORDER_COLORS;
        
        private Attribute(final String name, final String defaultValue, final boolean inherited) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.inherited = inherited;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public String getDefaultValue() {
            return this.defaultValue;
        }
        
        public boolean isInherited() {
            return this.inherited;
        }
        
        static {
            BACKGROUND = new Attribute("background", null, false);
            BACKGROUND_ATTACHMENT = new Attribute("background-attachment", "scroll", false);
            BACKGROUND_COLOR = new Attribute("background-color", "transparent", false);
            BACKGROUND_IMAGE = new Attribute("background-image", "none", false);
            BACKGROUND_POSITION = new Attribute("background-position", null, false);
            BACKGROUND_REPEAT = new Attribute("background-repeat", "repeat", false);
            BORDER = new Attribute("border", null, false);
            BORDER_BOTTOM = new Attribute("border-bottom", null, false);
            BORDER_BOTTOM_COLOR = new Attribute("border-bottom-color", null, false);
            BORDER_BOTTOM_STYLE = new Attribute("border-bottom-style", "none", false);
            BORDER_BOTTOM_WIDTH = new Attribute("border-bottom-width", "medium", false);
            BORDER_COLOR = new Attribute("border-color", null, false);
            BORDER_LEFT = new Attribute("border-left", null, false);
            BORDER_LEFT_COLOR = new Attribute("border-left-color", null, false);
            BORDER_LEFT_STYLE = new Attribute("border-left-style", "none", false);
            BORDER_LEFT_WIDTH = new Attribute("border-left-width", "medium", false);
            BORDER_RIGHT = new Attribute("border-right", null, false);
            BORDER_RIGHT_COLOR = new Attribute("border-right-color", null, false);
            BORDER_RIGHT_STYLE = new Attribute("border-right-style", "none", false);
            BORDER_RIGHT_WIDTH = new Attribute("border-right-width", "medium", false);
            BORDER_STYLE = new Attribute("border-style", "none", false);
            BORDER_TOP = new Attribute("border-top", null, false);
            BORDER_TOP_COLOR = new Attribute("border-top-color", null, false);
            BORDER_TOP_STYLE = new Attribute("border-top-style", "none", false);
            BORDER_TOP_WIDTH = new Attribute("border-top-width", "medium", false);
            BORDER_WIDTH = new Attribute("border-width", "medium", false);
            CLEAR = new Attribute("clear", "none", false);
            COLOR = new Attribute("color", "black", true);
            DISPLAY = new Attribute("display", "block", false);
            FLOAT = new Attribute("float", "none", false);
            FONT = new Attribute("font", null, true);
            FONT_FAMILY = new Attribute("font-family", null, true);
            FONT_SIZE = new Attribute("font-size", "medium", true);
            FONT_STYLE = new Attribute("font-style", "normal", true);
            FONT_VARIANT = new Attribute("font-variant", "normal", true);
            FONT_WEIGHT = new Attribute("font-weight", "normal", true);
            HEIGHT = new Attribute("height", "auto", false);
            LETTER_SPACING = new Attribute("letter-spacing", "normal", true);
            LINE_HEIGHT = new Attribute("line-height", "normal", true);
            LIST_STYLE = new Attribute("list-style", null, true);
            LIST_STYLE_IMAGE = new Attribute("list-style-image", "none", true);
            LIST_STYLE_POSITION = new Attribute("list-style-position", "outside", true);
            LIST_STYLE_TYPE = new Attribute("list-style-type", "disc", true);
            MARGIN = new Attribute("margin", null, false);
            MARGIN_BOTTOM = new Attribute("margin-bottom", "0", false);
            MARGIN_LEFT = new Attribute("margin-left", "0", false);
            MARGIN_RIGHT = new Attribute("margin-right", "0", false);
            MARGIN_LEFT_LTR = new Attribute("margin-left-ltr", Integer.toString(Integer.MIN_VALUE), false);
            MARGIN_LEFT_RTL = new Attribute("margin-left-rtl", Integer.toString(Integer.MIN_VALUE), false);
            MARGIN_RIGHT_LTR = new Attribute("margin-right-ltr", Integer.toString(Integer.MIN_VALUE), false);
            MARGIN_RIGHT_RTL = new Attribute("margin-right-rtl", Integer.toString(Integer.MIN_VALUE), false);
            MARGIN_TOP = new Attribute("margin-top", "0", false);
            PADDING = new Attribute("padding", null, false);
            PADDING_BOTTOM = new Attribute("padding-bottom", "0", false);
            PADDING_LEFT = new Attribute("padding-left", "0", false);
            PADDING_RIGHT = new Attribute("padding-right", "0", false);
            PADDING_TOP = new Attribute("padding-top", "0", false);
            TEXT_ALIGN = new Attribute("text-align", null, true);
            TEXT_DECORATION = new Attribute("text-decoration", "none", true);
            TEXT_INDENT = new Attribute("text-indent", "0", true);
            TEXT_TRANSFORM = new Attribute("text-transform", "none", true);
            VERTICAL_ALIGN = new Attribute("vertical-align", "baseline", false);
            WORD_SPACING = new Attribute("word-spacing", "normal", true);
            WHITE_SPACE = new Attribute("white-space", "normal", true);
            WIDTH = new Attribute("width", "auto", false);
            BORDER_SPACING = new Attribute("border-spacing", "0", true);
            CAPTION_SIDE = new Attribute("caption-side", "left", true);
            allAttributes = new Attribute[] { Attribute.BACKGROUND, Attribute.BACKGROUND_ATTACHMENT, Attribute.BACKGROUND_COLOR, Attribute.BACKGROUND_IMAGE, Attribute.BACKGROUND_POSITION, Attribute.BACKGROUND_REPEAT, Attribute.BORDER, Attribute.BORDER_BOTTOM, Attribute.BORDER_BOTTOM_WIDTH, Attribute.BORDER_COLOR, Attribute.BORDER_LEFT, Attribute.BORDER_LEFT_WIDTH, Attribute.BORDER_RIGHT, Attribute.BORDER_RIGHT_WIDTH, Attribute.BORDER_STYLE, Attribute.BORDER_TOP, Attribute.BORDER_TOP_WIDTH, Attribute.BORDER_WIDTH, Attribute.BORDER_TOP_STYLE, Attribute.BORDER_RIGHT_STYLE, Attribute.BORDER_BOTTOM_STYLE, Attribute.BORDER_LEFT_STYLE, Attribute.BORDER_TOP_COLOR, Attribute.BORDER_RIGHT_COLOR, Attribute.BORDER_BOTTOM_COLOR, Attribute.BORDER_LEFT_COLOR, Attribute.CLEAR, Attribute.COLOR, Attribute.DISPLAY, Attribute.FLOAT, Attribute.FONT, Attribute.FONT_FAMILY, Attribute.FONT_SIZE, Attribute.FONT_STYLE, Attribute.FONT_VARIANT, Attribute.FONT_WEIGHT, Attribute.HEIGHT, Attribute.LETTER_SPACING, Attribute.LINE_HEIGHT, Attribute.LIST_STYLE, Attribute.LIST_STYLE_IMAGE, Attribute.LIST_STYLE_POSITION, Attribute.LIST_STYLE_TYPE, Attribute.MARGIN, Attribute.MARGIN_BOTTOM, Attribute.MARGIN_LEFT, Attribute.MARGIN_RIGHT, Attribute.MARGIN_TOP, Attribute.PADDING, Attribute.PADDING_BOTTOM, Attribute.PADDING_LEFT, Attribute.PADDING_RIGHT, Attribute.PADDING_TOP, Attribute.TEXT_ALIGN, Attribute.TEXT_DECORATION, Attribute.TEXT_INDENT, Attribute.TEXT_TRANSFORM, Attribute.VERTICAL_ALIGN, Attribute.WORD_SPACING, Attribute.WHITE_SPACE, Attribute.WIDTH, Attribute.BORDER_SPACING, Attribute.CAPTION_SIDE, Attribute.MARGIN_LEFT_LTR, Attribute.MARGIN_LEFT_RTL, Attribute.MARGIN_RIGHT_LTR, Attribute.MARGIN_RIGHT_RTL };
            ALL_MARGINS = new Attribute[] { Attribute.MARGIN_TOP, Attribute.MARGIN_RIGHT, Attribute.MARGIN_BOTTOM, Attribute.MARGIN_LEFT };
            ALL_PADDING = new Attribute[] { Attribute.PADDING_TOP, Attribute.PADDING_RIGHT, Attribute.PADDING_BOTTOM, Attribute.PADDING_LEFT };
            ALL_BORDER_WIDTHS = new Attribute[] { Attribute.BORDER_TOP_WIDTH, Attribute.BORDER_RIGHT_WIDTH, Attribute.BORDER_BOTTOM_WIDTH, Attribute.BORDER_LEFT_WIDTH };
            ALL_BORDER_STYLES = new Attribute[] { Attribute.BORDER_TOP_STYLE, Attribute.BORDER_RIGHT_STYLE, Attribute.BORDER_BOTTOM_STYLE, Attribute.BORDER_LEFT_STYLE };
            ALL_BORDER_COLORS = new Attribute[] { Attribute.BORDER_TOP_COLOR, Attribute.BORDER_RIGHT_COLOR, Attribute.BORDER_BOTTOM_COLOR, Attribute.BORDER_LEFT_COLOR };
        }
    }
    
    static final class Value
    {
        static final Value INHERITED;
        static final Value NONE;
        static final Value HIDDEN;
        static final Value DOTTED;
        static final Value DASHED;
        static final Value SOLID;
        static final Value DOUBLE;
        static final Value GROOVE;
        static final Value RIDGE;
        static final Value INSET;
        static final Value OUTSET;
        static final Value DISC;
        static final Value CIRCLE;
        static final Value SQUARE;
        static final Value DECIMAL;
        static final Value LOWER_ROMAN;
        static final Value UPPER_ROMAN;
        static final Value LOWER_ALPHA;
        static final Value UPPER_ALPHA;
        static final Value BACKGROUND_NO_REPEAT;
        static final Value BACKGROUND_REPEAT;
        static final Value BACKGROUND_REPEAT_X;
        static final Value BACKGROUND_REPEAT_Y;
        static final Value BACKGROUND_SCROLL;
        static final Value BACKGROUND_FIXED;
        private String name;
        static final Value[] allValues;
        
        private Value(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        static {
            INHERITED = new Value("inherited");
            NONE = new Value("none");
            HIDDEN = new Value("hidden");
            DOTTED = new Value("dotted");
            DASHED = new Value("dashed");
            SOLID = new Value("solid");
            DOUBLE = new Value("double");
            GROOVE = new Value("groove");
            RIDGE = new Value("ridge");
            INSET = new Value("inset");
            OUTSET = new Value("outset");
            DISC = new Value("disc");
            CIRCLE = new Value("circle");
            SQUARE = new Value("square");
            DECIMAL = new Value("decimal");
            LOWER_ROMAN = new Value("lower-roman");
            UPPER_ROMAN = new Value("upper-roman");
            LOWER_ALPHA = new Value("lower-alpha");
            UPPER_ALPHA = new Value("upper-alpha");
            BACKGROUND_NO_REPEAT = new Value("no-repeat");
            BACKGROUND_REPEAT = new Value("repeat");
            BACKGROUND_REPEAT_X = new Value("repeat-x");
            BACKGROUND_REPEAT_Y = new Value("repeat-y");
            BACKGROUND_SCROLL = new Value("scroll");
            BACKGROUND_FIXED = new Value("fixed");
            allValues = new Value[] { Value.INHERITED, Value.NONE, Value.DOTTED, Value.DASHED, Value.SOLID, Value.DOUBLE, Value.GROOVE, Value.RIDGE, Value.INSET, Value.OUTSET, Value.DISC, Value.CIRCLE, Value.SQUARE, Value.DECIMAL, Value.LOWER_ROMAN, Value.UPPER_ROMAN, Value.LOWER_ALPHA, Value.UPPER_ALPHA, Value.BACKGROUND_NO_REPEAT, Value.BACKGROUND_REPEAT, Value.BACKGROUND_REPEAT_X, Value.BACKGROUND_REPEAT_Y, Value.BACKGROUND_FIXED, Value.BACKGROUND_FIXED };
        }
    }
    
    static class CssValue implements Serializable
    {
        String svalue;
        
        Object parseCssValue(final String s) {
            return s;
        }
        
        Object parseHtmlValue(final String s) {
            return this.parseCssValue(s);
        }
        
        Object fromStyleConstants(final StyleConstants styleConstants, final Object o) {
            return null;
        }
        
        Object toStyleConstants(final StyleConstants styleConstants, final View view) {
            return null;
        }
        
        @Override
        public String toString() {
            return this.svalue;
        }
    }
    
    static class StringValue extends CssValue
    {
        @Override
        Object parseCssValue(final String svalue) {
            final StringValue stringValue = new StringValue();
            stringValue.svalue = svalue;
            return stringValue;
        }
        
        @Override
        Object fromStyleConstants(final StyleConstants styleConstants, final Object o) {
            if (styleConstants == StyleConstants.Italic) {
                if (o.equals(Boolean.TRUE)) {
                    return this.parseCssValue("italic");
                }
                return this.parseCssValue("");
            }
            else if (styleConstants == StyleConstants.Underline) {
                if (o.equals(Boolean.TRUE)) {
                    return this.parseCssValue("underline");
                }
                return this.parseCssValue("");
            }
            else {
                if (styleConstants == StyleConstants.Alignment) {
                    String s = null;
                    switch ((int)o) {
                        case 0: {
                            s = "left";
                            break;
                        }
                        case 2: {
                            s = "right";
                            break;
                        }
                        case 1: {
                            s = "center";
                            break;
                        }
                        case 3: {
                            s = "justify";
                            break;
                        }
                        default: {
                            s = "left";
                            break;
                        }
                    }
                    return this.parseCssValue(s);
                }
                if (styleConstants == StyleConstants.StrikeThrough) {
                    if (o.equals(Boolean.TRUE)) {
                        return this.parseCssValue("line-through");
                    }
                    return this.parseCssValue("");
                }
                else if (styleConstants == StyleConstants.Superscript) {
                    if (o.equals(Boolean.TRUE)) {
                        return this.parseCssValue("super");
                    }
                    return this.parseCssValue("");
                }
                else {
                    if (styleConstants != StyleConstants.Subscript) {
                        return null;
                    }
                    if (o.equals(Boolean.TRUE)) {
                        return this.parseCssValue("sub");
                    }
                    return this.parseCssValue("");
                }
            }
        }
        
        @Override
        Object toStyleConstants(final StyleConstants styleConstants, final View view) {
            if (styleConstants == StyleConstants.Italic) {
                if (this.svalue.indexOf("italic") >= 0) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            else if (styleConstants == StyleConstants.Underline) {
                if (this.svalue.indexOf("underline") >= 0) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            else if (styleConstants == StyleConstants.Alignment) {
                if (this.svalue.equals("right")) {
                    return new Integer(2);
                }
                if (this.svalue.equals("center")) {
                    return new Integer(1);
                }
                if (this.svalue.equals("justify")) {
                    return new Integer(3);
                }
                return new Integer(0);
            }
            else if (styleConstants == StyleConstants.StrikeThrough) {
                if (this.svalue.indexOf("line-through") >= 0) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            else if (styleConstants == StyleConstants.Superscript) {
                if (this.svalue.indexOf("super") >= 0) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            else {
                if (styleConstants != StyleConstants.Subscript) {
                    return null;
                }
                if (this.svalue.indexOf("sub") >= 0) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        }
        
        boolean isItalic() {
            return this.svalue.indexOf("italic") != -1;
        }
        
        boolean isStrike() {
            return this.svalue.indexOf("line-through") != -1;
        }
        
        boolean isUnderline() {
            return this.svalue.indexOf("underline") != -1;
        }
        
        boolean isSub() {
            return this.svalue.indexOf("sub") != -1;
        }
        
        boolean isSup() {
            return this.svalue.indexOf("sup") != -1;
        }
    }
    
    class FontSize extends CssValue
    {
        float value;
        boolean index;
        LengthUnit lu;
        
        int getValue(final AttributeSet set, StyleSheet access$500) {
            access$500 = CSS.this.getStyleSheet(access$500);
            if (this.index) {
                return Math.round(CSS.this.getPointSize((int)this.value, access$500));
            }
            if (this.lu == null) {
                return Math.round(this.value);
            }
            if (this.lu.type == 0) {
                return Math.round(this.lu.getValue(access$500 != null && access$500.isW3CLengthUnits()));
            }
            if (set != null) {
                final AttributeSet resolveParent = set.getResolveParent();
                if (resolveParent != null) {
                    final int fontSize = StyleConstants.getFontSize(resolveParent);
                    float n;
                    if (this.lu.type == 1 || this.lu.type == 3) {
                        n = this.lu.value * fontSize;
                    }
                    else {
                        n = this.lu.value + fontSize;
                    }
                    return Math.round(n);
                }
            }
            return 12;
        }
        
        @Override
        Object parseCssValue(final String svalue) {
            FontSize fontSize = new FontSize();
            fontSize.svalue = svalue;
            try {
                if (svalue.equals("xx-small")) {
                    fontSize.value = 1.0f;
                    fontSize.index = true;
                }
                else if (svalue.equals("x-small")) {
                    fontSize.value = 2.0f;
                    fontSize.index = true;
                }
                else if (svalue.equals("small")) {
                    fontSize.value = 3.0f;
                    fontSize.index = true;
                }
                else if (svalue.equals("medium")) {
                    fontSize.value = 4.0f;
                    fontSize.index = true;
                }
                else if (svalue.equals("large")) {
                    fontSize.value = 5.0f;
                    fontSize.index = true;
                }
                else if (svalue.equals("x-large")) {
                    fontSize.value = 6.0f;
                    fontSize.index = true;
                }
                else if (svalue.equals("xx-large")) {
                    fontSize.value = 7.0f;
                    fontSize.index = true;
                }
                else {
                    fontSize.lu = new LengthUnit(svalue, (short)1, 1.0f);
                }
            }
            catch (final NumberFormatException ex) {
                fontSize = null;
            }
            return fontSize;
        }
        
        @Override
        Object parseHtmlValue(final String svalue) {
            if (svalue == null || svalue.length() == 0) {
                return null;
            }
            FontSize fontSize = new FontSize();
            fontSize.svalue = svalue;
            try {
                final int baseFontSize = CSS.this.getBaseFontSize();
                if (svalue.charAt(0) == '+') {
                    fontSize.value = (float)(baseFontSize + Integer.valueOf(svalue.substring(1)));
                    fontSize.index = true;
                }
                else if (svalue.charAt(0) == '-') {
                    fontSize.value = (float)(baseFontSize + -Integer.valueOf(svalue.substring(1)));
                    fontSize.index = true;
                }
                else {
                    fontSize.value = (float)Integer.parseInt(svalue);
                    if (fontSize.value > 7.0f) {
                        fontSize.value = 7.0f;
                    }
                    else if (fontSize.value < 0.0f) {
                        fontSize.value = 0.0f;
                    }
                    fontSize.index = true;
                }
            }
            catch (final NumberFormatException ex) {
                fontSize = null;
            }
            return fontSize;
        }
        
        @Override
        Object fromStyleConstants(final StyleConstants styleConstants, final Object o) {
            if (o instanceof Number) {
                final FontSize fontSize = new FontSize();
                fontSize.value = (float)CSS.getIndexOfSize(((Number)o).floatValue(), StyleSheet.sizeMapDefault);
                fontSize.svalue = Integer.toString((int)fontSize.value);
                fontSize.index = true;
                return fontSize;
            }
            return this.parseCssValue(o.toString());
        }
        
        @Override
        Object toStyleConstants(final StyleConstants styleConstants, final View view) {
            if (view != null) {
                return this.getValue(view.getAttributes(), null);
            }
            return this.getValue(null, null);
        }
    }
    
    static class FontFamily extends CssValue
    {
        String family;
        
        String getValue() {
            return this.family;
        }
        
        @Override
        Object parseCssValue(final String svalue) {
            final int index = svalue.indexOf(44);
            final FontFamily fontFamily = new FontFamily();
            fontFamily.svalue = svalue;
            fontFamily.family = null;
            if (index == -1) {
                this.setFontName(fontFamily, svalue);
            }
            else {
                int i = 0;
                final int length = svalue.length();
                int index2 = 0;
                while (i == 0) {
                    while (index2 < length && Character.isWhitespace(svalue.charAt(index2))) {
                        ++index2;
                    }
                    final int n = index2;
                    index2 = svalue.indexOf(44, index2);
                    if (index2 == -1) {
                        index2 = length;
                    }
                    if (n < length) {
                        if (n != index2) {
                            int n2 = index2;
                            if (index2 > 0 && svalue.charAt(index2 - 1) == ' ') {
                                --n2;
                            }
                            this.setFontName(fontFamily, svalue.substring(n, n2));
                            i = ((fontFamily.family != null) ? 1 : 0);
                        }
                        ++index2;
                    }
                    else {
                        i = 1;
                    }
                }
            }
            if (fontFamily.family == null) {
                fontFamily.family = "SansSerif";
            }
            return fontFamily;
        }
        
        private void setFontName(final FontFamily fontFamily, final String family) {
            fontFamily.family = family;
        }
        
        @Override
        Object parseHtmlValue(final String s) {
            return this.parseCssValue(s);
        }
        
        @Override
        Object fromStyleConstants(final StyleConstants styleConstants, final Object o) {
            return this.parseCssValue(o.toString());
        }
        
        @Override
        Object toStyleConstants(final StyleConstants styleConstants, final View view) {
            return this.family;
        }
    }
    
    static class FontWeight extends CssValue
    {
        int weight;
        
        int getValue() {
            return this.weight;
        }
        
        @Override
        Object parseCssValue(final String svalue) {
            FontWeight fontWeight = new FontWeight();
            fontWeight.svalue = svalue;
            if (svalue.equals("bold")) {
                fontWeight.weight = 700;
            }
            else if (svalue.equals("normal")) {
                fontWeight.weight = 400;
            }
            else {
                try {
                    fontWeight.weight = Integer.parseInt(svalue);
                }
                catch (final NumberFormatException ex) {
                    fontWeight = null;
                }
            }
            return fontWeight;
        }
        
        @Override
        Object fromStyleConstants(final StyleConstants styleConstants, final Object o) {
            if (o.equals(Boolean.TRUE)) {
                return this.parseCssValue("bold");
            }
            return this.parseCssValue("normal");
        }
        
        @Override
        Object toStyleConstants(final StyleConstants styleConstants, final View view) {
            return (this.weight > 500) ? Boolean.TRUE : Boolean.FALSE;
        }
        
        boolean isBold() {
            return this.weight > 500;
        }
    }
    
    static class ColorValue extends CssValue
    {
        Color c;
        
        Color getValue() {
            return this.c;
        }
        
        @Override
        Object parseCssValue(final String svalue) {
            final Color stringToColor = CSS.stringToColor(svalue);
            if (stringToColor != null) {
                final ColorValue colorValue = new ColorValue();
                colorValue.svalue = svalue;
                colorValue.c = stringToColor;
                return colorValue;
            }
            return null;
        }
        
        @Override
        Object parseHtmlValue(final String s) {
            return this.parseCssValue(s);
        }
        
        @Override
        Object fromStyleConstants(final StyleConstants styleConstants, final Object o) {
            final ColorValue colorValue = new ColorValue();
            colorValue.c = (Color)o;
            colorValue.svalue = CSS.colorToHex(colorValue.c);
            return colorValue;
        }
        
        @Override
        Object toStyleConstants(final StyleConstants styleConstants, final View view) {
            return this.c;
        }
    }
    
    static class BorderStyle extends CssValue
    {
        private transient Value style;
        
        Value getValue() {
            return this.style;
        }
        
        @Override
        Object parseCssValue(final String svalue) {
            final Value value = CSS.getValue(svalue);
            if (value != null && (value == Value.INSET || value == Value.OUTSET || value == Value.NONE || value == Value.DOTTED || value == Value.DASHED || value == Value.SOLID || value == Value.DOUBLE || value == Value.GROOVE || value == Value.RIDGE)) {
                final BorderStyle borderStyle = new BorderStyle();
                borderStyle.svalue = svalue;
                borderStyle.style = value;
                return borderStyle;
            }
            return null;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            if (this.style == null) {
                objectOutputStream.writeObject(null);
            }
            else {
                objectOutputStream.writeObject(this.style.toString());
            }
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
            final Object object = objectInputStream.readObject();
            if (object != null) {
                this.style = CSS.getValue((String)object);
            }
        }
    }
    
    static class LengthValue extends CssValue
    {
        boolean mayBeNegative;
        boolean percentage;
        float span;
        String units;
        
        LengthValue() {
            this(false);
        }
        
        LengthValue(final boolean mayBeNegative) {
            this.units = null;
            this.mayBeNegative = mayBeNegative;
        }
        
        float getValue() {
            return this.getValue(false);
        }
        
        float getValue(final boolean b) {
            return this.getValue(0.0f, b);
        }
        
        float getValue(final float n) {
            return this.getValue(n, false);
        }
        
        float getValue(final float n, final boolean b) {
            if (this.percentage) {
                return this.span * n;
            }
            return LengthUnit.getValue(this.span, this.units, b);
        }
        
        boolean isPercentage() {
            return this.percentage;
        }
        
        @Override
        Object parseCssValue(final String svalue) {
            LengthValue lengthValue = null;
            try {
                final float floatValue = Float.valueOf(svalue);
                lengthValue = new LengthValue();
                lengthValue.span = floatValue;
            }
            catch (final NumberFormatException ex) {
                final LengthUnit lengthUnit = new LengthUnit(svalue, (short)10, 0.0f);
                switch (lengthUnit.type) {
                    case 0: {
                        lengthValue = new LengthValue();
                        lengthValue.span = (this.mayBeNegative ? lengthUnit.value : Math.max(0.0f, lengthUnit.value));
                        lengthValue.units = lengthUnit.units;
                        break;
                    }
                    case 1: {
                        lengthValue = new LengthValue();
                        lengthValue.span = Math.max(0.0f, Math.min(1.0f, lengthUnit.value));
                        lengthValue.percentage = true;
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            lengthValue.svalue = svalue;
            return lengthValue;
        }
        
        @Override
        Object parseHtmlValue(String s) {
            if (s.equals("#DEFAULT")) {
                s = "1";
            }
            return this.parseCssValue(s);
        }
        
        @Override
        Object fromStyleConstants(final StyleConstants styleConstants, final Object o) {
            final LengthValue lengthValue = new LengthValue();
            lengthValue.svalue = o.toString();
            lengthValue.span = (float)o;
            return lengthValue;
        }
        
        @Override
        Object toStyleConstants(final StyleConstants styleConstants, final View view) {
            return new Float(this.getValue(false));
        }
    }
    
    static class BorderWidthValue extends LengthValue
    {
        private static final float[] values;
        
        BorderWidthValue(final String svalue, final int n) {
            this.svalue = svalue;
            this.span = BorderWidthValue.values[n];
            this.percentage = false;
        }
        
        @Override
        Object parseCssValue(final String s) {
            if (s != null) {
                if (s.equals("thick")) {
                    return new BorderWidthValue(s, 2);
                }
                if (s.equals("medium")) {
                    return new BorderWidthValue(s, 1);
                }
                if (s.equals("thin")) {
                    return new BorderWidthValue(s, 0);
                }
            }
            return super.parseCssValue(s);
        }
        
        @Override
        Object parseHtmlValue(final String s) {
            if (s == "#DEFAULT") {
                return this.parseCssValue("medium");
            }
            return this.parseCssValue(s);
        }
        
        static {
            values = new float[] { 1.0f, 2.0f, 4.0f };
        }
    }
    
    static class CssValueMapper extends CssValue
    {
        @Override
        Object parseCssValue(final String s) {
            Object o = CSS.cssValueToInternalValueMap.get(s);
            if (o == null) {
                o = CSS.cssValueToInternalValueMap.get(s.toLowerCase());
            }
            return o;
        }
        
        @Override
        Object parseHtmlValue(final String s) {
            Object o = CSS.htmlValueToCssValueMap.get(s);
            if (o == null) {
                o = CSS.htmlValueToCssValueMap.get(s.toLowerCase());
            }
            return o;
        }
    }
    
    static class BackgroundPosition extends CssValue
    {
        float horizontalPosition;
        float verticalPosition;
        short relative;
        
        @Override
        Object parseCssValue(final String svalue) {
            final String[] strings = CSS.parseStrings(svalue);
            final int length = strings.length;
            final BackgroundPosition backgroundPosition = new BackgroundPosition();
            backgroundPosition.relative = 5;
            backgroundPosition.svalue = svalue;
            if (length > 0) {
                short n = 0;
                int i = 0;
                while (i < length) {
                    final String s = strings[i++];
                    if (s.equals("center")) {
                        n |= 0x4;
                    }
                    else {
                        if ((n & 0x1) == 0x0) {
                            if (s.equals("top")) {
                                n |= 0x1;
                            }
                            else if (s.equals("bottom")) {
                                n |= 0x1;
                                backgroundPosition.verticalPosition = 1.0f;
                                continue;
                            }
                        }
                        if ((n & 0x2) != 0x0) {
                            continue;
                        }
                        if (s.equals("left")) {
                            n |= 0x2;
                            backgroundPosition.horizontalPosition = 0.0f;
                        }
                        else {
                            if (!s.equals("right")) {
                                continue;
                            }
                            n |= 0x2;
                            backgroundPosition.horizontalPosition = 1.0f;
                        }
                    }
                }
                if (n != 0) {
                    if ((n & 0x1) == 0x1) {
                        if ((n & 0x2) == 0x0) {
                            backgroundPosition.horizontalPosition = 0.5f;
                        }
                    }
                    else if ((n & 0x2) == 0x2) {
                        backgroundPosition.verticalPosition = 0.5f;
                    }
                    else {
                        final BackgroundPosition backgroundPosition2 = backgroundPosition;
                        final BackgroundPosition backgroundPosition3 = backgroundPosition;
                        final float n2 = 0.5f;
                        backgroundPosition3.verticalPosition = n2;
                        backgroundPosition2.horizontalPosition = n2;
                    }
                }
                else {
                    final LengthUnit lengthUnit = new LengthUnit(strings[0], (short)0, 0.0f);
                    if (lengthUnit.type == 0) {
                        backgroundPosition.horizontalPosition = lengthUnit.value;
                        backgroundPosition.relative ^= 0x1;
                    }
                    else if (lengthUnit.type == 1) {
                        backgroundPosition.horizontalPosition = lengthUnit.value;
                    }
                    else if (lengthUnit.type == 3) {
                        backgroundPosition.horizontalPosition = lengthUnit.value;
                        backgroundPosition.relative = (short)((0x1 ^ backgroundPosition.relative) | 0x2);
                    }
                    if (length > 1) {
                        final LengthUnit lengthUnit2 = new LengthUnit(strings[1], (short)0, 0.0f);
                        if (lengthUnit2.type == 0) {
                            backgroundPosition.verticalPosition = lengthUnit2.value;
                            backgroundPosition.relative ^= 0x4;
                        }
                        else if (lengthUnit2.type == 1) {
                            backgroundPosition.verticalPosition = lengthUnit2.value;
                        }
                        else if (lengthUnit2.type == 3) {
                            backgroundPosition.verticalPosition = lengthUnit2.value;
                            backgroundPosition.relative = (short)((0x4 ^ backgroundPosition.relative) | 0x8);
                        }
                    }
                    else {
                        backgroundPosition.verticalPosition = 0.5f;
                    }
                }
            }
            return backgroundPosition;
        }
        
        boolean isHorizontalPositionRelativeToSize() {
            return (this.relative & 0x1) == 0x1;
        }
        
        boolean isHorizontalPositionRelativeToFontSize() {
            return (this.relative & 0x2) == 0x2;
        }
        
        float getHorizontalPosition() {
            return this.horizontalPosition;
        }
        
        boolean isVerticalPositionRelativeToSize() {
            return (this.relative & 0x4) == 0x4;
        }
        
        boolean isVerticalPositionRelativeToFontSize() {
            return (this.relative & 0x8) == 0x8;
        }
        
        float getVerticalPosition() {
            return this.verticalPosition;
        }
    }
    
    static class BackgroundImage extends CssValue
    {
        private boolean loadedImage;
        private ImageIcon image;
        
        @Override
        Object parseCssValue(final String svalue) {
            final BackgroundImage backgroundImage = new BackgroundImage();
            backgroundImage.svalue = svalue;
            return backgroundImage;
        }
        
        @Override
        Object parseHtmlValue(final String s) {
            return this.parseCssValue(s);
        }
        
        ImageIcon getImage(final URL url) {
            if (!this.loadedImage) {
                synchronized (this) {
                    if (!this.loadedImage) {
                        final URL url2 = CSS.getURL(url, this.svalue);
                        this.loadedImage = true;
                        if (url2 != null) {
                            this.image = new ImageIcon();
                            final Image image = Toolkit.getDefaultToolkit().createImage(url2);
                            if (image != null) {
                                this.image.setImage(image);
                            }
                        }
                    }
                }
            }
            return this.image;
        }
    }
    
    static class LengthUnit implements Serializable
    {
        static Hashtable<String, Float> lengthMapping;
        static Hashtable<String, Float> w3cLengthMapping;
        short type;
        float value;
        String units;
        static final short UNINITALIZED_LENGTH = 10;
        
        LengthUnit(final String s, final short n, final float n2) {
            this.units = null;
            this.parse(s, n, n2);
        }
        
        void parse(final String s, final short type, final float value) {
            this.type = type;
            this.value = value;
            final int length = s.length();
            if (length > 0 && s.charAt(length - 1) == '%') {
                try {
                    this.value = Float.valueOf(s.substring(0, length - 1)) / 100.0f;
                    this.type = 1;
                }
                catch (final NumberFormatException ex) {}
            }
            if (length >= 2) {
                this.units = s.substring(length - 2, length);
                if (LengthUnit.lengthMapping.get(this.units) != null) {
                    try {
                        this.value = Float.valueOf(s.substring(0, length - 2));
                        this.type = 0;
                    }
                    catch (final NumberFormatException ex2) {}
                }
                else {
                    if (!this.units.equals("em")) {
                        if (!this.units.equals("ex")) {
                            if (s.equals("larger")) {
                                this.value = 2.0f;
                                this.type = 2;
                                return;
                            }
                            if (s.equals("smaller")) {
                                this.value = -2.0f;
                                this.type = 2;
                                return;
                            }
                            try {
                                this.value = Float.valueOf(s);
                                this.type = 0;
                            }
                            catch (final NumberFormatException ex3) {}
                            return;
                        }
                    }
                    try {
                        this.value = Float.valueOf(s.substring(0, length - 2));
                        this.type = 3;
                    }
                    catch (final NumberFormatException ex4) {}
                }
            }
            else if (length > 0) {
                try {
                    this.value = Float.valueOf(s);
                    this.type = 0;
                }
                catch (final NumberFormatException ex5) {}
            }
        }
        
        float getValue(final boolean b) {
            final Hashtable<String, Float> hashtable = b ? LengthUnit.w3cLengthMapping : LengthUnit.lengthMapping;
            float floatValue = 1.0f;
            if (this.units != null) {
                final Float n = hashtable.get(this.units);
                if (n != null) {
                    floatValue = n;
                }
            }
            return this.value * floatValue;
        }
        
        static float getValue(final float n, final String s, final Boolean b) {
            final Hashtable<String, Float> hashtable = b ? LengthUnit.w3cLengthMapping : LengthUnit.lengthMapping;
            float floatValue = 1.0f;
            if (s != null) {
                final Float n2 = hashtable.get(s);
                if (n2 != null) {
                    floatValue = n2;
                }
            }
            return n * floatValue;
        }
        
        @Override
        public String toString() {
            return this.type + " " + this.value;
        }
        
        static {
            LengthUnit.lengthMapping = new Hashtable<String, Float>(6);
            LengthUnit.w3cLengthMapping = new Hashtable<String, Float>(6);
            LengthUnit.lengthMapping.put("pt", new Float(1.0f));
            LengthUnit.lengthMapping.put("px", new Float(1.3f));
            LengthUnit.lengthMapping.put("mm", new Float(2.83464f));
            LengthUnit.lengthMapping.put("cm", new Float(28.3464f));
            LengthUnit.lengthMapping.put("pc", new Float(12.0f));
            LengthUnit.lengthMapping.put("in", new Float(72.0f));
            int screenResolution = 72;
            try {
                screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
            }
            catch (final HeadlessException ex) {}
            LengthUnit.w3cLengthMapping.put("pt", new Float(screenResolution / 72.0f));
            LengthUnit.w3cLengthMapping.put("px", new Float(1.0f));
            LengthUnit.w3cLengthMapping.put("mm", new Float(screenResolution / 25.4f));
            LengthUnit.w3cLengthMapping.put("cm", new Float(screenResolution / 2.54f));
            LengthUnit.w3cLengthMapping.put("pc", new Float(screenResolution / 6.0f));
            LengthUnit.w3cLengthMapping.put("in", new Float((float)screenResolution));
        }
    }
    
    static class ShorthandFontParser
    {
        static void parseShorthandFont(final CSS css, final String s, final MutableAttributeSet set) {
            final String[] strings = CSS.parseStrings(s);
            final int length = strings.length;
            int i = 0;
            short n = 0;
            while (i < Math.min(3, length)) {
                if ((n & 0x1) == 0x0 && isFontStyle(strings[i])) {
                    css.addInternalCSSValue(set, Attribute.FONT_STYLE, strings[i++]);
                    n |= 0x1;
                }
                else if ((n & 0x2) == 0x0 && isFontVariant(strings[i])) {
                    css.addInternalCSSValue(set, Attribute.FONT_VARIANT, strings[i++]);
                    n |= 0x2;
                }
                else if ((n & 0x4) == 0x0 && isFontWeight(strings[i])) {
                    css.addInternalCSSValue(set, Attribute.FONT_WEIGHT, strings[i++]);
                    n |= 0x4;
                }
                else {
                    if (!strings[i].equals("normal")) {
                        break;
                    }
                    ++i;
                }
            }
            if ((n & 0x1) == 0x0) {
                css.addInternalCSSValue(set, Attribute.FONT_STYLE, "normal");
            }
            if ((n & 0x2) == 0x0) {
                css.addInternalCSSValue(set, Attribute.FONT_VARIANT, "normal");
            }
            if ((n & 0x4) == 0x0) {
                css.addInternalCSSValue(set, Attribute.FONT_WEIGHT, "normal");
            }
            if (i < length) {
                String substring = strings[i];
                final int index = substring.indexOf(47);
                if (index != -1) {
                    substring = substring.substring(0, index);
                    strings[i] = strings[i].substring(index);
                }
                else {
                    ++i;
                }
                css.addInternalCSSValue(set, Attribute.FONT_SIZE, substring);
            }
            else {
                css.addInternalCSSValue(set, Attribute.FONT_SIZE, "medium");
            }
            if (i < length && strings[i].startsWith("/")) {
                String substring2 = null;
                if (strings[i].equals("/")) {
                    if (++i < length) {
                        substring2 = strings[i++];
                    }
                }
                else {
                    substring2 = strings[i++].substring(1);
                }
                if (substring2 != null) {
                    css.addInternalCSSValue(set, Attribute.LINE_HEIGHT, substring2);
                }
                else {
                    css.addInternalCSSValue(set, Attribute.LINE_HEIGHT, "normal");
                }
            }
            else {
                css.addInternalCSSValue(set, Attribute.LINE_HEIGHT, "normal");
            }
            if (i < length) {
                String string;
                for (string = strings[i++]; i < length; string = string + " " + strings[i++]) {}
                css.addInternalCSSValue(set, Attribute.FONT_FAMILY, string);
            }
            else {
                css.addInternalCSSValue(set, Attribute.FONT_FAMILY, "SansSerif");
            }
        }
        
        private static boolean isFontStyle(final String s) {
            return s.equals("italic") || s.equals("oblique");
        }
        
        private static boolean isFontVariant(final String s) {
            return s.equals("small-caps");
        }
        
        private static boolean isFontWeight(final String s) {
            return s.equals("bold") || s.equals("bolder") || s.equals("italic") || s.equals("lighter") || (s.length() == 3 && s.charAt(0) >= '1' && s.charAt(0) <= '9' && s.charAt(1) == '0' && s.charAt(2) == '0');
        }
    }
    
    static class ShorthandBackgroundParser
    {
        static void parseShorthandBackground(final CSS css, final String s, final MutableAttributeSet set) {
            final String[] strings = CSS.parseStrings(s);
            final int length = strings.length;
            int i = 0;
            short n = 0;
            while (i < length) {
                final String s2 = strings[i++];
                if ((n & 0x1) == 0x0 && isImage(s2)) {
                    css.addInternalCSSValue(set, Attribute.BACKGROUND_IMAGE, s2);
                    n |= 0x1;
                }
                else if ((n & 0x2) == 0x0 && isRepeat(s2)) {
                    css.addInternalCSSValue(set, Attribute.BACKGROUND_REPEAT, s2);
                    n |= 0x2;
                }
                else if ((n & 0x4) == 0x0 && isAttachment(s2)) {
                    css.addInternalCSSValue(set, Attribute.BACKGROUND_ATTACHMENT, s2);
                    n |= 0x4;
                }
                else if ((n & 0x8) == 0x0 && isPosition(s2)) {
                    if (i < length && isPosition(strings[i])) {
                        css.addInternalCSSValue(set, Attribute.BACKGROUND_POSITION, s2 + " " + strings[i++]);
                    }
                    else {
                        css.addInternalCSSValue(set, Attribute.BACKGROUND_POSITION, s2);
                    }
                    n |= 0x8;
                }
                else {
                    if ((n & 0x10) != 0x0 || !isColor(s2)) {
                        continue;
                    }
                    css.addInternalCSSValue(set, Attribute.BACKGROUND_COLOR, s2);
                    n |= 0x10;
                }
            }
            if ((n & 0x1) == 0x0) {
                css.addInternalCSSValue(set, Attribute.BACKGROUND_IMAGE, null);
            }
            if ((n & 0x2) == 0x0) {
                css.addInternalCSSValue(set, Attribute.BACKGROUND_REPEAT, "repeat");
            }
            if ((n & 0x4) == 0x0) {
                css.addInternalCSSValue(set, Attribute.BACKGROUND_ATTACHMENT, "scroll");
            }
            if ((n & 0x8) == 0x0) {
                css.addInternalCSSValue(set, Attribute.BACKGROUND_POSITION, null);
            }
        }
        
        static boolean isImage(final String s) {
            return s.startsWith("url(") && s.endsWith(")");
        }
        
        static boolean isRepeat(final String s) {
            return s.equals("repeat-x") || s.equals("repeat-y") || s.equals("repeat") || s.equals("no-repeat");
        }
        
        static boolean isAttachment(final String s) {
            return s.equals("fixed") || s.equals("scroll");
        }
        
        static boolean isPosition(final String s) {
            return s.equals("top") || s.equals("bottom") || s.equals("left") || s.equals("right") || s.equals("center") || (s.length() > 0 && Character.isDigit(s.charAt(0)));
        }
        
        static boolean isColor(final String s) {
            return CSS.stringToColor(s) != null;
        }
    }
    
    static class ShorthandMarginParser
    {
        static void parseShorthandMargin(final CSS css, final String s, final MutableAttributeSet set, final Attribute[] array) {
            final String[] strings = CSS.parseStrings(s);
            switch (strings.length) {
                case 0: {
                    return;
                }
                case 1: {
                    for (int i = 0; i < 4; ++i) {
                        css.addInternalCSSValue(set, array[i], strings[0]);
                    }
                    break;
                }
                case 2: {
                    css.addInternalCSSValue(set, array[0], strings[0]);
                    css.addInternalCSSValue(set, array[2], strings[0]);
                    css.addInternalCSSValue(set, array[1], strings[1]);
                    css.addInternalCSSValue(set, array[3], strings[1]);
                    break;
                }
                case 3: {
                    css.addInternalCSSValue(set, array[0], strings[0]);
                    css.addInternalCSSValue(set, array[1], strings[1]);
                    css.addInternalCSSValue(set, array[2], strings[2]);
                    css.addInternalCSSValue(set, array[3], strings[1]);
                    break;
                }
                default: {
                    for (int j = 0; j < 4; ++j) {
                        css.addInternalCSSValue(set, array[j], strings[j]);
                    }
                    break;
                }
            }
        }
    }
    
    static class ShorthandBorderParser
    {
        static Attribute[] keys;
        
        static void parseShorthandBorder(final MutableAttributeSet set, final Attribute attribute, final String s) {
            final Object[] array = new Object[CSSBorder.PARSERS.length];
            for (final String s2 : CSS.parseStrings(s)) {
                boolean b = false;
                int j = 0;
                while (j < array.length) {
                    final Object cssValue = CSSBorder.PARSERS[j].parseCssValue(s2);
                    if (cssValue != null) {
                        if (array[j] == null) {
                            array[j] = cssValue;
                            b = true;
                            break;
                        }
                        break;
                    }
                    else {
                        ++j;
                    }
                }
                if (!b) {
                    return;
                }
            }
            for (int k = 0; k < array.length; ++k) {
                if (array[k] == null) {
                    array[k] = CSSBorder.DEFAULTS[k];
                }
            }
            for (int l = 0; l < ShorthandBorderParser.keys.length; ++l) {
                if (attribute == Attribute.BORDER || attribute == ShorthandBorderParser.keys[l]) {
                    for (int n = 0; n < array.length; ++n) {
                        set.addAttribute(CSSBorder.ATTRIBUTES[n][l], array[n]);
                    }
                }
            }
        }
        
        static {
            ShorthandBorderParser.keys = new Attribute[] { Attribute.BORDER_TOP, Attribute.BORDER_RIGHT, Attribute.BORDER_BOTTOM, Attribute.BORDER_LEFT };
        }
    }
    
    interface LayoutIterator
    {
        public static final int WorstAdjustmentWeight = 2;
        
        void setOffset(final int p0);
        
        int getOffset();
        
        void setSpan(final int p0);
        
        int getSpan();
        
        int getCount();
        
        void setIndex(final int p0);
        
        float getMinimumSpan(final float p0);
        
        float getPreferredSpan(final float p0);
        
        float getMaximumSpan(final float p0);
        
        int getAdjustmentWeight();
        
        float getBorderWidth();
        
        float getLeadingCollapseSpan();
        
        float getTrailingCollapseSpan();
    }
}

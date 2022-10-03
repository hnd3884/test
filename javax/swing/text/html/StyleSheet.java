package javax.swing.text.html;

import java.util.HashMap;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import java.awt.FontMetrics;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;
import javax.swing.text.StyledDocument;
import javax.swing.UIManager;
import java.awt.Shape;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import javax.swing.Icon;
import java.awt.Rectangle;
import java.awt.Container;
import javax.swing.border.BevelBorder;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Insets;
import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.Stack;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import javax.swing.text.MutableAttributeSet;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.plaf.UIResource;
import java.util.Enumeration;
import javax.swing.text.View;
import javax.swing.text.SimpleAttributeSet;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.text.Element;
import java.net.URL;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.border.Border;
import javax.swing.text.StyleContext;

public class StyleSheet extends StyleContext
{
    static final Border noBorder;
    static final int DEFAULT_FONT_SIZE = 3;
    private CSS css;
    private SelectorMapping selectorMapping;
    private Hashtable<String, ResolvedStyle> resolvedStyles;
    private Vector<StyleSheet> linkedStyleSheets;
    private URL base;
    static final int[] sizeMapDefault;
    private int[] sizeMap;
    private boolean w3cLengthUnits;
    
    public StyleSheet() {
        this.sizeMap = StyleSheet.sizeMapDefault;
        this.w3cLengthUnits = false;
        this.selectorMapping = new SelectorMapping(0);
        this.resolvedStyles = new Hashtable<String, ResolvedStyle>();
        if (this.css == null) {
            this.css = new CSS();
        }
    }
    
    public Style getRule(final HTML.Tag tag, Element element) {
        final SearchBuffer obtainSearchBuffer = SearchBuffer.obtainSearchBuffer();
        try {
            final Vector vector = obtainSearchBuffer.getVector();
            for (Element parentElement = element; parentElement != null; parentElement = parentElement.getParentElement()) {
                vector.addElement(parentElement);
            }
            final int size = vector.size();
            final StringBuffer stringBuffer = obtainSearchBuffer.getStringBuffer();
            for (int i = size - 1; i >= 1; --i) {
                element = (Element)vector.elementAt(i);
                final AttributeSet attributes = element.getAttributes();
                stringBuffer.append(attributes.getAttribute(StyleConstants.NameAttribute).toString());
                if (attributes != null) {
                    if (attributes.isDefined(HTML.Attribute.ID)) {
                        stringBuffer.append('#');
                        stringBuffer.append(attributes.getAttribute(HTML.Attribute.ID));
                    }
                    else if (attributes.isDefined(HTML.Attribute.CLASS)) {
                        stringBuffer.append('.');
                        stringBuffer.append(attributes.getAttribute(HTML.Attribute.CLASS));
                    }
                }
                stringBuffer.append(' ');
            }
            stringBuffer.append(tag.toString());
            element = (Element)vector.elementAt(0);
            AttributeSet attributes2 = element.getAttributes();
            if (element.isLeaf()) {
                final Object attribute = attributes2.getAttribute(tag);
                if (attribute instanceof AttributeSet) {
                    attributes2 = (AttributeSet)attribute;
                }
                else {
                    attributes2 = null;
                }
            }
            if (attributes2 != null) {
                if (attributes2.isDefined(HTML.Attribute.ID)) {
                    stringBuffer.append('#');
                    stringBuffer.append(attributes2.getAttribute(HTML.Attribute.ID));
                }
                else if (attributes2.isDefined(HTML.Attribute.CLASS)) {
                    stringBuffer.append('.');
                    stringBuffer.append(attributes2.getAttribute(HTML.Attribute.CLASS));
                }
            }
            return this.getResolvedStyle(stringBuffer.toString(), vector, tag);
        }
        finally {
            SearchBuffer.releaseSearchBuffer(obtainSearchBuffer);
        }
    }
    
    public Style getRule(String cleanSelectorString) {
        cleanSelectorString = this.cleanSelectorString(cleanSelectorString);
        if (cleanSelectorString != null) {
            return this.getResolvedStyle(cleanSelectorString);
        }
        return null;
    }
    
    public void addRule(final String s) {
        if (s != null) {
            if (s == "BASE_SIZE_DISABLE") {
                this.sizeMap = StyleSheet.sizeMapDefault;
            }
            else if (s.startsWith("BASE_SIZE ")) {
                this.rebaseSizeMap(Integer.parseInt(s.substring("BASE_SIZE ".length())));
            }
            else if (s == "W3C_LENGTH_UNITS_ENABLE") {
                this.w3cLengthUnits = true;
            }
            else if (s == "W3C_LENGTH_UNITS_DISABLE") {
                this.w3cLengthUnits = false;
            }
            else {
                final CssParser cssParser = new CssParser();
                try {
                    cssParser.parse(this.getBase(), new StringReader(s), false, false);
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    public AttributeSet getDeclaration(final String s) {
        if (s == null) {
            return SimpleAttributeSet.EMPTY;
        }
        return new CssParser().parseDeclaration(s);
    }
    
    public void loadRules(final Reader reader, final URL url) throws IOException {
        new CssParser().parse(url, reader, false, false);
    }
    
    public AttributeSet getViewAttributes(final View view) {
        return new ViewAttributeSet(view);
    }
    
    @Override
    public void removeStyle(final String s) {
        if (this.getStyle(s) != null) {
            final String[] simpleSelectors = this.getSimpleSelectors(this.cleanSelectorString(s));
            synchronized (this) {
                SelectorMapping selectorMapping = this.getRootSelectorMapping();
                for (int i = simpleSelectors.length - 1; i >= 0; --i) {
                    selectorMapping = selectorMapping.getChildSelectorMapping(simpleSelectors[i], true);
                }
                final Style style = selectorMapping.getStyle();
                if (style != null) {
                    selectorMapping.setStyle(null);
                    if (this.resolvedStyles.size() > 0) {
                        final Enumeration<ResolvedStyle> elements = this.resolvedStyles.elements();
                        while (elements.hasMoreElements()) {
                            elements.nextElement().removeStyle(style);
                        }
                    }
                }
            }
        }
        super.removeStyle(s);
    }
    
    public void addStyleSheet(final StyleSheet styleSheet) {
        synchronized (this) {
            if (this.linkedStyleSheets == null) {
                this.linkedStyleSheets = new Vector<StyleSheet>();
            }
            if (!this.linkedStyleSheets.contains(styleSheet)) {
                int n = 0;
                if (styleSheet instanceof UIResource && this.linkedStyleSheets.size() > 1) {
                    n = this.linkedStyleSheets.size() - 1;
                }
                this.linkedStyleSheets.insertElementAt(styleSheet, n);
                this.linkStyleSheetAt(styleSheet, n);
            }
        }
    }
    
    public void removeStyleSheet(final StyleSheet styleSheet) {
        synchronized (this) {
            if (this.linkedStyleSheets != null) {
                final int index = this.linkedStyleSheets.indexOf(styleSheet);
                if (index != -1) {
                    this.linkedStyleSheets.removeElementAt(index);
                    this.unlinkStyleSheet(styleSheet, index);
                    if (index == 0 && this.linkedStyleSheets.size() == 0) {
                        this.linkedStyleSheets = null;
                    }
                }
            }
        }
    }
    
    public StyleSheet[] getStyleSheets() {
        StyleSheet[] array;
        synchronized (this) {
            if (this.linkedStyleSheets != null) {
                array = new StyleSheet[this.linkedStyleSheets.size()];
                this.linkedStyleSheets.copyInto(array);
            }
            else {
                array = null;
            }
        }
        return array;
    }
    
    public void importStyleSheet(final URL url) {
        try {
            final InputStream openStream = url.openStream();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openStream));
            new CssParser().parse(url, bufferedReader, false, true);
            bufferedReader.close();
            openStream.close();
        }
        catch (final Throwable t) {}
    }
    
    public void setBase(final URL base) {
        this.base = base;
    }
    
    public URL getBase() {
        return this.base;
    }
    
    public void addCSSAttribute(final MutableAttributeSet set, final CSS.Attribute attribute, final String s) {
        this.css.addInternalCSSValue(set, attribute, s);
    }
    
    public boolean addCSSAttributeFromHTML(final MutableAttributeSet set, final CSS.Attribute attribute, final String s) {
        final Object cssValue = this.css.getCssValue(attribute, s);
        if (cssValue != null) {
            set.addAttribute(attribute, cssValue);
            return true;
        }
        return false;
    }
    
    public AttributeSet translateHTMLToCSS(final AttributeSet set) {
        final AttributeSet translateHTMLToCSS = this.css.translateHTMLToCSS(set);
        final Style addStyle = this.addStyle(null, null);
        addStyle.addAttributes(translateHTMLToCSS);
        return addStyle;
    }
    
    @Override
    public AttributeSet addAttribute(AttributeSet removeAttribute, final Object o, final Object o2) {
        if (this.css == null) {
            this.css = new CSS();
        }
        if (o instanceof StyleConstants) {
            final HTML.Tag tagForStyleConstantsKey = HTML.getTagForStyleConstantsKey((StyleConstants)o);
            if (tagForStyleConstantsKey != null && removeAttribute.isDefined(tagForStyleConstantsKey)) {
                removeAttribute = this.removeAttribute(removeAttribute, tagForStyleConstantsKey);
            }
            final Object styleConstantsValueToCSSValue = this.css.styleConstantsValueToCSSValue((StyleConstants)o, o2);
            if (styleConstantsValueToCSSValue != null) {
                final CSS.Attribute styleConstantsKeyToCSSKey = this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
                if (styleConstantsKeyToCSSKey != null) {
                    return super.addAttribute(removeAttribute, styleConstantsKeyToCSSKey, styleConstantsValueToCSSValue);
                }
            }
        }
        return super.addAttribute(removeAttribute, o, o2);
    }
    
    @Override
    public AttributeSet addAttributes(AttributeSet removeHTMLTags, final AttributeSet set) {
        if (!(set instanceof HTMLDocument.TaggedAttributeSet)) {
            removeHTMLTags = this.removeHTMLTags(removeHTMLTags, set);
        }
        return super.addAttributes(removeHTMLTags, this.convertAttributeSet(set));
    }
    
    @Override
    public AttributeSet removeAttribute(AttributeSet removeAttribute, final Object o) {
        if (o instanceof StyleConstants) {
            final HTML.Tag tagForStyleConstantsKey = HTML.getTagForStyleConstantsKey((StyleConstants)o);
            if (tagForStyleConstantsKey != null) {
                removeAttribute = super.removeAttribute(removeAttribute, tagForStyleConstantsKey);
            }
            final CSS.Attribute styleConstantsKeyToCSSKey = this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
            if (styleConstantsKeyToCSSKey != null) {
                return super.removeAttribute(removeAttribute, styleConstantsKeyToCSSKey);
            }
        }
        return super.removeAttribute(removeAttribute, o);
    }
    
    @Override
    public AttributeSet removeAttributes(final AttributeSet set, final Enumeration<?> enumeration) {
        return super.removeAttributes(set, enumeration);
    }
    
    @Override
    public AttributeSet removeAttributes(AttributeSet removeHTMLTags, final AttributeSet set) {
        if (removeHTMLTags != set) {
            removeHTMLTags = this.removeHTMLTags(removeHTMLTags, set);
        }
        return super.removeAttributes(removeHTMLTags, this.convertAttributeSet(set));
    }
    
    @Override
    protected SmallAttributeSet createSmallAttributeSet(final AttributeSet set) {
        return new SmallConversionSet(set);
    }
    
    @Override
    protected MutableAttributeSet createLargeAttributeSet(final AttributeSet set) {
        return new LargeConversionSet(set);
    }
    
    private AttributeSet removeHTMLTags(AttributeSet removeAttribute, final AttributeSet set) {
        if (!(set instanceof LargeConversionSet) && !(set instanceof SmallConversionSet)) {
            final Enumeration<?> attributeNames = set.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                final Object nextElement = attributeNames.nextElement();
                if (nextElement instanceof StyleConstants) {
                    final HTML.Tag tagForStyleConstantsKey = HTML.getTagForStyleConstantsKey((StyleConstants)nextElement);
                    if (tagForStyleConstantsKey == null || !removeAttribute.isDefined(tagForStyleConstantsKey)) {
                        continue;
                    }
                    removeAttribute = super.removeAttribute(removeAttribute, tagForStyleConstantsKey);
                }
            }
        }
        return removeAttribute;
    }
    
    AttributeSet convertAttributeSet(final AttributeSet set) {
        if (set instanceof LargeConversionSet || set instanceof SmallConversionSet) {
            return set;
        }
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            if (attributeNames.nextElement() instanceof StyleConstants) {
                final LargeConversionSet set2 = new LargeConversionSet();
                final Enumeration<?> attributeNames2 = set.getAttributeNames();
                while (attributeNames2.hasMoreElements()) {
                    final Object nextElement = attributeNames2.nextElement();
                    Object styleConstantsValueToCSSValue = null;
                    if (nextElement instanceof StyleConstants) {
                        final CSS.Attribute styleConstantsKeyToCSSKey = this.css.styleConstantsKeyToCSSKey((StyleConstants)nextElement);
                        if (styleConstantsKeyToCSSKey != null) {
                            styleConstantsValueToCSSValue = this.css.styleConstantsValueToCSSValue((StyleConstants)nextElement, set.getAttribute(nextElement));
                            if (styleConstantsValueToCSSValue != null) {
                                set2.addAttribute(styleConstantsKeyToCSSKey, styleConstantsValueToCSSValue);
                            }
                        }
                    }
                    if (styleConstantsValueToCSSValue == null) {
                        set2.addAttribute(nextElement, set.getAttribute(nextElement));
                    }
                }
                return set2;
            }
        }
        return set;
    }
    
    @Override
    public Font getFont(final AttributeSet set) {
        return this.css.getFont(this, set, 12, this);
    }
    
    @Override
    public Color getForeground(final AttributeSet set) {
        final Color color = this.css.getColor(set, CSS.Attribute.COLOR);
        if (color == null) {
            return Color.black;
        }
        return color;
    }
    
    @Override
    public Color getBackground(final AttributeSet set) {
        return this.css.getColor(set, CSS.Attribute.BACKGROUND_COLOR);
    }
    
    public BoxPainter getBoxPainter(final AttributeSet set) {
        return new BoxPainter(set, this.css, this);
    }
    
    public ListPainter getListPainter(final AttributeSet set) {
        return new ListPainter(set, this);
    }
    
    public void setBaseFontSize(final int baseFontSize) {
        this.css.setBaseFontSize(baseFontSize);
    }
    
    public void setBaseFontSize(final String baseFontSize) {
        this.css.setBaseFontSize(baseFontSize);
    }
    
    public static int getIndexOfSize(final float n) {
        return CSS.getIndexOfSize(n, StyleSheet.sizeMapDefault);
    }
    
    public float getPointSize(final int n) {
        return this.css.getPointSize(n, this);
    }
    
    public float getPointSize(final String s) {
        return this.css.getPointSize(s, this);
    }
    
    public Color stringToColor(final String s) {
        return CSS.stringToColor(s);
    }
    
    ImageIcon getBackgroundImage(final AttributeSet set) {
        final Object attribute = set.getAttribute(CSS.Attribute.BACKGROUND_IMAGE);
        if (attribute != null) {
            return ((CSS.BackgroundImage)attribute).getImage(this.getBase());
        }
        return null;
    }
    
    void addRule(final String[] array, final AttributeSet set, final boolean b) {
        final int length = array.length;
        final StringBuilder sb = new StringBuilder();
        sb.append(array[0]);
        for (int i = 1; i < length; ++i) {
            sb.append(' ');
            sb.append(array[i]);
        }
        final String string = sb.toString();
        Style style = this.getStyle(string);
        if (style == null) {
            final Style addStyle = this.addStyle(string, null);
            synchronized (this) {
                SelectorMapping selectorMapping = this.getRootSelectorMapping();
                for (int j = length - 1; j >= 0; --j) {
                    selectorMapping = selectorMapping.getChildSelectorMapping(array[j], true);
                }
                style = selectorMapping.getStyle();
                if (style == null) {
                    style = addStyle;
                    selectorMapping.setStyle(style);
                    this.refreshResolvedRules(string, array, style, selectorMapping.getSpecificity());
                }
            }
        }
        if (b) {
            style = this.getLinkedStyle(style);
        }
        style.addAttributes(set);
    }
    
    private synchronized void linkStyleSheetAt(final StyleSheet styleSheet, final int n) {
        if (this.resolvedStyles.size() > 0) {
            final Enumeration<ResolvedStyle> elements = this.resolvedStyles.elements();
            while (elements.hasMoreElements()) {
                final ResolvedStyle resolvedStyle = elements.nextElement();
                resolvedStyle.insertExtendedStyleAt(styleSheet.getRule(resolvedStyle.getName()), n);
            }
        }
    }
    
    private synchronized void unlinkStyleSheet(final StyleSheet styleSheet, final int n) {
        if (this.resolvedStyles.size() > 0) {
            final Enumeration<ResolvedStyle> elements = this.resolvedStyles.elements();
            while (elements.hasMoreElements()) {
                elements.nextElement().removeExtendedStyleAt(n);
            }
        }
    }
    
    String[] getSimpleSelectors(String cleanSelectorString) {
        cleanSelectorString = this.cleanSelectorString(cleanSelectorString);
        final SearchBuffer obtainSearchBuffer = SearchBuffer.obtainSearchBuffer();
        final Vector vector = obtainSearchBuffer.getVector();
        int i = 0;
        final int length = cleanSelectorString.length();
        while (i != -1) {
            int index = cleanSelectorString.indexOf(32, i);
            if (index != -1) {
                vector.addElement(cleanSelectorString.substring(i, index));
                if (++index == length) {
                    i = -1;
                }
                else {
                    i = index;
                }
            }
            else {
                vector.addElement(cleanSelectorString.substring(i));
                i = -1;
            }
        }
        final String[] array = new String[vector.size()];
        vector.copyInto(array);
        SearchBuffer.releaseSearchBuffer(obtainSearchBuffer);
        return array;
    }
    
    String cleanSelectorString(final String s) {
        int n = 1;
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case ' ': {
                    if (n != 0) {
                        return this._cleanSelectorString(s);
                    }
                    n = 1;
                    break;
                }
                case '\t':
                case '\n':
                case '\r': {
                    return this._cleanSelectorString(s);
                }
                default: {
                    n = 0;
                    break;
                }
            }
        }
        if (n != 0) {
            return this._cleanSelectorString(s);
        }
        return s;
    }
    
    private String _cleanSelectorString(final String s) {
        final SearchBuffer obtainSearchBuffer = SearchBuffer.obtainSearchBuffer();
        final StringBuffer stringBuffer = obtainSearchBuffer.getStringBuffer();
        int n = 1;
        int n2 = 0;
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        String string = null;
        try {
            for (int i = 0; i < length; ++i) {
                switch (charArray[i]) {
                    case ' ': {
                        if (n == 0) {
                            n = 1;
                            if (n2 < i) {
                                stringBuffer.append(charArray, n2, 1 + i - n2);
                            }
                        }
                        n2 = i + 1;
                        break;
                    }
                    case '\t':
                    case '\n':
                    case '\r': {
                        if (n == 0) {
                            n = 1;
                            if (n2 < i) {
                                stringBuffer.append(charArray, n2, i - n2);
                                stringBuffer.append(' ');
                            }
                        }
                        n2 = i + 1;
                        break;
                    }
                    default: {
                        n = 0;
                        break;
                    }
                }
            }
            if (n != 0 && stringBuffer.length() > 0) {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }
            else if (n2 < length) {
                stringBuffer.append(charArray, n2, length - n2);
            }
            string = stringBuffer.toString();
        }
        finally {
            SearchBuffer.releaseSearchBuffer(obtainSearchBuffer);
        }
        return string;
    }
    
    private SelectorMapping getRootSelectorMapping() {
        return this.selectorMapping;
    }
    
    static int getSpecificity(final String s) {
        int n = 0;
        int n2 = 1;
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case '.': {
                    n += 100;
                    break;
                }
                case '#': {
                    n += 10000;
                    break;
                }
                case ' ': {
                    n2 = 1;
                    break;
                }
                default: {
                    if (n2 != 0) {
                        n2 = 0;
                        ++n;
                        break;
                    }
                    break;
                }
            }
        }
        return n;
    }
    
    private Style getLinkedStyle(final Style style) {
        Style addStyle = (Style)style.getResolveParent();
        if (addStyle == null) {
            addStyle = this.addStyle(null, null);
            style.setResolveParent(addStyle);
        }
        return addStyle;
    }
    
    private synchronized Style getResolvedStyle(final String s, final Vector vector, final HTML.Tag tag) {
        Style resolvedStyle = this.resolvedStyles.get(s);
        if (resolvedStyle == null) {
            resolvedStyle = this.createResolvedStyle(s, vector, tag);
        }
        return resolvedStyle;
    }
    
    private synchronized Style getResolvedStyle(final String s) {
        Style resolvedStyle = this.resolvedStyles.get(s);
        if (resolvedStyle == null) {
            resolvedStyle = this.createResolvedStyle(s);
        }
        return resolvedStyle;
    }
    
    private void addSortedStyle(final SelectorMapping selectorMapping, final Vector<SelectorMapping> vector) {
        final int size = vector.size();
        if (size > 0) {
            final int specificity = selectorMapping.getSpecificity();
            for (int i = 0; i < size; ++i) {
                if (specificity >= ((SelectorMapping)vector.elementAt(i)).getSpecificity()) {
                    vector.insertElementAt(selectorMapping, i);
                    return;
                }
            }
        }
        vector.addElement(selectorMapping);
    }
    
    private synchronized void getStyles(final SelectorMapping selectorMapping, final Vector<SelectorMapping> vector, final String[] array, final String[] array2, final String[] array3, final int n, final int n2, final Hashtable<SelectorMapping, SelectorMapping> hashtable) {
        if (hashtable.contains(selectorMapping)) {
            return;
        }
        hashtable.put(selectorMapping, selectorMapping);
        if (selectorMapping.getStyle() != null) {
            this.addSortedStyle(selectorMapping, vector);
        }
        for (int i = n; i < n2; ++i) {
            final String s = array[i];
            if (s != null) {
                final SelectorMapping childSelectorMapping = selectorMapping.getChildSelectorMapping(s, false);
                if (childSelectorMapping != null) {
                    this.getStyles(childSelectorMapping, vector, array, array2, array3, i + 1, n2, hashtable);
                }
                if (array3[i] != null) {
                    final String s2 = array3[i];
                    final SelectorMapping childSelectorMapping2 = selectorMapping.getChildSelectorMapping(s + "." + s2, false);
                    if (childSelectorMapping2 != null) {
                        this.getStyles(childSelectorMapping2, vector, array, array2, array3, i + 1, n2, hashtable);
                    }
                    final SelectorMapping childSelectorMapping3 = selectorMapping.getChildSelectorMapping("." + s2, false);
                    if (childSelectorMapping3 != null) {
                        this.getStyles(childSelectorMapping3, vector, array, array2, array3, i + 1, n2, hashtable);
                    }
                }
                if (array2[i] != null) {
                    final String s3 = array2[i];
                    final SelectorMapping childSelectorMapping4 = selectorMapping.getChildSelectorMapping(s + "#" + s3, false);
                    if (childSelectorMapping4 != null) {
                        this.getStyles(childSelectorMapping4, vector, array, array2, array3, i + 1, n2, hashtable);
                    }
                    final SelectorMapping childSelectorMapping5 = selectorMapping.getChildSelectorMapping("#" + s3, false);
                    if (childSelectorMapping5 != null) {
                        this.getStyles(childSelectorMapping5, vector, array, array2, array3, i + 1, n2, hashtable);
                    }
                }
            }
        }
    }
    
    private synchronized Style createResolvedStyle(final String s, final String[] array, final String[] array2, final String[] array3) {
        final SearchBuffer obtainSearchBuffer = SearchBuffer.obtainSearchBuffer();
        final Vector vector = obtainSearchBuffer.getVector();
        final Hashtable hashtable = obtainSearchBuffer.getHashtable();
        try {
            final SelectorMapping rootSelectorMapping = this.getRootSelectorMapping();
            final int length = array.length;
            final String s2 = array[0];
            final SelectorMapping childSelectorMapping = rootSelectorMapping.getChildSelectorMapping(s2, false);
            if (childSelectorMapping != null) {
                this.getStyles(childSelectorMapping, vector, array, array2, array3, 1, length, hashtable);
            }
            if (array3[0] != null) {
                final String s3 = array3[0];
                final SelectorMapping childSelectorMapping2 = rootSelectorMapping.getChildSelectorMapping(s2 + "." + s3, false);
                if (childSelectorMapping2 != null) {
                    this.getStyles(childSelectorMapping2, vector, array, array2, array3, 1, length, hashtable);
                }
                final SelectorMapping childSelectorMapping3 = rootSelectorMapping.getChildSelectorMapping("." + s3, false);
                if (childSelectorMapping3 != null) {
                    this.getStyles(childSelectorMapping3, vector, array, array2, array3, 1, length, hashtable);
                }
            }
            if (array2[0] != null) {
                final String s4 = array2[0];
                final SelectorMapping childSelectorMapping4 = rootSelectorMapping.getChildSelectorMapping(s2 + "#" + s4, false);
                if (childSelectorMapping4 != null) {
                    this.getStyles(childSelectorMapping4, vector, array, array2, array3, 1, length, hashtable);
                }
                final SelectorMapping childSelectorMapping5 = rootSelectorMapping.getChildSelectorMapping("#" + s4, false);
                if (childSelectorMapping5 != null) {
                    this.getStyles(childSelectorMapping5, vector, array, array2, array3, 1, length, hashtable);
                }
            }
            final int n = (this.linkedStyleSheets != null) ? this.linkedStyleSheets.size() : 0;
            final int size = vector.size();
            final AttributeSet[] array4 = new AttributeSet[size + n];
            for (int i = 0; i < size; ++i) {
                array4[i] = ((SelectorMapping)vector.elementAt(i)).getStyle();
            }
            for (int j = 0; j < n; ++j) {
                final Style rule = this.linkedStyleSheets.elementAt(j).getRule(s);
                if (rule == null) {
                    array4[j + size] = SimpleAttributeSet.EMPTY;
                }
                else {
                    array4[j + size] = rule;
                }
            }
            final ResolvedStyle resolvedStyle = new ResolvedStyle(s, array4, size);
            this.resolvedStyles.put(s, resolvedStyle);
            return resolvedStyle;
        }
        finally {
            SearchBuffer.releaseSearchBuffer(obtainSearchBuffer);
        }
    }
    
    private Style createResolvedStyle(final String s, final Vector vector, final HTML.Tag tag) {
        final int size = vector.size();
        final String[] array = new String[size];
        final String[] array2 = new String[size];
        final String[] array3 = new String[size];
        for (int i = 0; i < size; ++i) {
            final Element element = vector.elementAt(i);
            AttributeSet attributes = element.getAttributes();
            if (i == 0 && element.isLeaf()) {
                final Object attribute = attributes.getAttribute(tag);
                if (attribute instanceof AttributeSet) {
                    attributes = (AttributeSet)attribute;
                }
                else {
                    attributes = null;
                }
            }
            if (attributes != null) {
                final HTML.Tag tag2 = (HTML.Tag)attributes.getAttribute(StyleConstants.NameAttribute);
                if (tag2 != null) {
                    array[i] = tag2.toString();
                }
                else {
                    array[i] = null;
                }
                if (attributes.isDefined(HTML.Attribute.CLASS)) {
                    array3[i] = attributes.getAttribute(HTML.Attribute.CLASS).toString();
                }
                else {
                    array3[i] = null;
                }
                if (attributes.isDefined(HTML.Attribute.ID)) {
                    array2[i] = attributes.getAttribute(HTML.Attribute.ID).toString();
                }
                else {
                    array2[i] = null;
                }
            }
            else {
                final String[] array4 = array;
                final int n = i;
                final String[] array5 = array2;
                final int n2 = i;
                final String[] array6 = array3;
                final int n3 = i;
                final String s2 = null;
                array6[n3] = s2;
                array4[n] = (array5[n2] = s2);
            }
        }
        array[0] = tag.toString();
        return this.createResolvedStyle(s, array, array2, array3);
    }
    
    private Style createResolvedStyle(final String s) {
        final SearchBuffer obtainSearchBuffer = SearchBuffer.obtainSearchBuffer();
        final Vector vector = obtainSearchBuffer.getVector();
        try {
            int index = 0;
            int index2 = 0;
            int index3;
            for (int i = 0, length = s.length(); i < length; i = index3 + 1) {
                if (index == i) {
                    index = s.indexOf(46, i);
                }
                if (index2 == i) {
                    index2 = s.indexOf(35, i);
                }
                index3 = s.indexOf(32, i);
                if (index3 == -1) {
                    index3 = length;
                }
                if (index != -1 && index2 != -1 && index < index3 && index2 < index3) {
                    if (index2 < index) {
                        if (i == index2) {
                            vector.addElement("");
                        }
                        else {
                            vector.addElement(s.substring(i, index2));
                        }
                        if (index + 1 < index3) {
                            vector.addElement(s.substring(index + 1, index3));
                        }
                        else {
                            vector.addElement(null);
                        }
                        if (index2 + 1 == index) {
                            vector.addElement(null);
                        }
                        else {
                            vector.addElement(s.substring(index2 + 1, index));
                        }
                    }
                    else if (index2 < index3) {
                        if (i == index) {
                            vector.addElement("");
                        }
                        else {
                            vector.addElement(s.substring(i, index));
                        }
                        if (index + 1 < index2) {
                            vector.addElement(s.substring(index + 1, index2));
                        }
                        else {
                            vector.addElement(null);
                        }
                        if (index2 + 1 == index3) {
                            vector.addElement(null);
                        }
                        else {
                            vector.addElement(s.substring(index2 + 1, index3));
                        }
                    }
                    index2 = (index = index3 + 1);
                }
                else if (index != -1 && index < index3) {
                    if (index == i) {
                        vector.addElement("");
                    }
                    else {
                        vector.addElement(s.substring(i, index));
                    }
                    if (index + 1 == index3) {
                        vector.addElement(null);
                    }
                    else {
                        vector.addElement(s.substring(index + 1, index3));
                    }
                    vector.addElement(null);
                    index = index3 + 1;
                }
                else if (index2 != -1 && index2 < index3) {
                    if (index2 == i) {
                        vector.addElement("");
                    }
                    else {
                        vector.addElement(s.substring(i, index2));
                    }
                    vector.addElement(null);
                    if (index2 + 1 == index3) {
                        vector.addElement(null);
                    }
                    else {
                        vector.addElement(s.substring(index2 + 1, index3));
                    }
                    index2 = index3 + 1;
                }
                else {
                    vector.addElement(s.substring(i, index3));
                    vector.addElement(null);
                    vector.addElement(null);
                }
            }
            final int size = vector.size();
            final int n = size / 3;
            final String[] array = new String[n];
            final String[] array2 = new String[n];
            final String[] array3 = new String[n];
            for (int j = 0, n2 = size - 3; j < n; ++j, n2 -= 3) {
                array[j] = (String)vector.elementAt(n2);
                array3[j] = (String)vector.elementAt(n2 + 1);
                array2[j] = (String)vector.elementAt(n2 + 2);
            }
            return this.createResolvedStyle(s, array, array2, array3);
        }
        finally {
            SearchBuffer.releaseSearchBuffer(obtainSearchBuffer);
        }
    }
    
    private synchronized void refreshResolvedRules(final String s, final String[] array, final Style style, final int n) {
        if (this.resolvedStyles.size() > 0) {
            final Enumeration<ResolvedStyle> elements = this.resolvedStyles.elements();
            while (elements.hasMoreElements()) {
                final ResolvedStyle resolvedStyle = elements.nextElement();
                if (resolvedStyle.matches(s)) {
                    resolvedStyle.insertStyle(style, n);
                }
            }
        }
    }
    
    void rebaseSizeMap(final int n) {
        this.sizeMap = new int[StyleSheet.sizeMapDefault.length];
        for (int i = 0; i < StyleSheet.sizeMapDefault.length; ++i) {
            this.sizeMap[i] = Math.max(n * StyleSheet.sizeMapDefault[i] / StyleSheet.sizeMapDefault[CSS.baseFontSizeIndex], 4);
        }
    }
    
    int[] getSizeMap() {
        return this.sizeMap;
    }
    
    boolean isW3CLengthUnits() {
        return this.w3cLengthUnits;
    }
    
    static {
        noBorder = new EmptyBorder(0, 0, 0, 0);
        sizeMapDefault = new int[] { 8, 10, 12, 14, 18, 24, 36 };
    }
    
    class LargeConversionSet extends SimpleAttributeSet
    {
        public LargeConversionSet(final AttributeSet set) {
            super(set);
        }
        
        public LargeConversionSet() {
        }
        
        @Override
        public boolean isDefined(final Object o) {
            if (o instanceof StyleConstants) {
                final CSS.Attribute styleConstantsKeyToCSSKey = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
                if (styleConstantsKeyToCSSKey != null) {
                    return super.isDefined(styleConstantsKeyToCSSKey);
                }
            }
            return super.isDefined(o);
        }
        
        @Override
        public Object getAttribute(final Object o) {
            if (o instanceof StyleConstants) {
                final CSS.Attribute styleConstantsKeyToCSSKey = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
                if (styleConstantsKeyToCSSKey != null) {
                    final Object attribute = super.getAttribute(styleConstantsKeyToCSSKey);
                    if (attribute != null) {
                        return StyleSheet.this.css.cssValueToStyleConstantsValue((StyleConstants)o, attribute);
                    }
                }
            }
            return super.getAttribute(o);
        }
    }
    
    class SmallConversionSet extends SmallAttributeSet
    {
        public SmallConversionSet(final AttributeSet set) {
            super(set);
        }
        
        @Override
        public boolean isDefined(final Object o) {
            if (o instanceof StyleConstants) {
                final CSS.Attribute styleConstantsKeyToCSSKey = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
                if (styleConstantsKeyToCSSKey != null) {
                    return super.isDefined(styleConstantsKeyToCSSKey);
                }
            }
            return super.isDefined(o);
        }
        
        @Override
        public Object getAttribute(final Object o) {
            if (o instanceof StyleConstants) {
                final CSS.Attribute styleConstantsKeyToCSSKey = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
                if (styleConstantsKeyToCSSKey != null) {
                    final Object attribute = super.getAttribute(styleConstantsKeyToCSSKey);
                    if (attribute != null) {
                        return StyleSheet.this.css.cssValueToStyleConstantsValue((StyleConstants)o, attribute);
                    }
                }
            }
            return super.getAttribute(o);
        }
    }
    
    private static class SearchBuffer
    {
        static Stack<SearchBuffer> searchBuffers;
        Vector vector;
        StringBuffer stringBuffer;
        Hashtable hashtable;
        
        private SearchBuffer() {
            this.vector = null;
            this.stringBuffer = null;
            this.hashtable = null;
        }
        
        static SearchBuffer obtainSearchBuffer() {
            SearchBuffer searchBuffer;
            try {
                if (!SearchBuffer.searchBuffers.empty()) {
                    searchBuffer = SearchBuffer.searchBuffers.pop();
                }
                else {
                    searchBuffer = new SearchBuffer();
                }
            }
            catch (final EmptyStackException ex) {
                searchBuffer = new SearchBuffer();
            }
            return searchBuffer;
        }
        
        static void releaseSearchBuffer(final SearchBuffer searchBuffer) {
            searchBuffer.empty();
            SearchBuffer.searchBuffers.push(searchBuffer);
        }
        
        StringBuffer getStringBuffer() {
            if (this.stringBuffer == null) {
                this.stringBuffer = new StringBuffer();
            }
            return this.stringBuffer;
        }
        
        Vector getVector() {
            if (this.vector == null) {
                this.vector = new Vector();
            }
            return this.vector;
        }
        
        Hashtable getHashtable() {
            if (this.hashtable == null) {
                this.hashtable = new Hashtable();
            }
            return this.hashtable;
        }
        
        void empty() {
            if (this.stringBuffer != null) {
                this.stringBuffer.setLength(0);
            }
            if (this.vector != null) {
                this.vector.removeAllElements();
            }
            if (this.hashtable != null) {
                this.hashtable.clear();
            }
        }
        
        static {
            SearchBuffer.searchBuffers = new Stack<SearchBuffer>();
        }
    }
    
    public static class BoxPainter implements Serializable
    {
        float topMargin;
        float bottomMargin;
        float leftMargin;
        float rightMargin;
        short marginFlags;
        Border border;
        Insets binsets;
        CSS css;
        StyleSheet ss;
        Color bg;
        BackgroundImagePainter bgPainter;
        
        BoxPainter(final AttributeSet set, final CSS css, final StyleSheet ss) {
            this.ss = ss;
            this.css = css;
            this.border = this.getBorder(set);
            this.binsets = this.border.getBorderInsets(null);
            this.topMargin = this.getLength(CSS.Attribute.MARGIN_TOP, set);
            this.bottomMargin = this.getLength(CSS.Attribute.MARGIN_BOTTOM, set);
            this.leftMargin = this.getLength(CSS.Attribute.MARGIN_LEFT, set);
            this.rightMargin = this.getLength(CSS.Attribute.MARGIN_RIGHT, set);
            this.bg = ss.getBackground(set);
            if (ss.getBackgroundImage(set) != null) {
                this.bgPainter = new BackgroundImagePainter(set, css, ss);
            }
        }
        
        Border getBorder(final AttributeSet set) {
            return new CSSBorder(set);
        }
        
        Color getBorderColor(final AttributeSet set) {
            Color color = this.css.getColor(set, CSS.Attribute.BORDER_COLOR);
            if (color == null) {
                color = this.css.getColor(set, CSS.Attribute.COLOR);
                if (color == null) {
                    return Color.black;
                }
            }
            return color;
        }
        
        public float getInset(final int n, final View view) {
            final AttributeSet attributes = view.getAttributes();
            final float n2 = 0.0f;
            float n3 = 0.0f;
            switch (n) {
                case 2: {
                    n3 = n2 + this.getOrientationMargin(HorizontalMargin.LEFT, this.leftMargin, attributes, isLeftToRight(view)) + this.binsets.left + this.getLength(CSS.Attribute.PADDING_LEFT, attributes);
                    break;
                }
                case 4: {
                    n3 = n2 + this.getOrientationMargin(HorizontalMargin.RIGHT, this.rightMargin, attributes, isLeftToRight(view)) + this.binsets.right + this.getLength(CSS.Attribute.PADDING_RIGHT, attributes);
                    break;
                }
                case 1: {
                    n3 = n2 + this.topMargin + this.binsets.top + this.getLength(CSS.Attribute.PADDING_TOP, attributes);
                    break;
                }
                case 3: {
                    n3 = n2 + this.bottomMargin + this.binsets.bottom + this.getLength(CSS.Attribute.PADDING_BOTTOM, attributes);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid side: " + n);
                }
            }
            return n3;
        }
        
        public void paint(final Graphics graphics, float n, float n2, float n3, float n4, final View view) {
            float n5 = 0.0f;
            float topMargin = 0.0f;
            float n6 = 0.0f;
            float n7 = 0.0f;
            final AttributeSet attributes = view.getAttributes();
            final boolean leftToRight = isLeftToRight(view);
            final float orientationMargin = this.getOrientationMargin(HorizontalMargin.LEFT, this.leftMargin, attributes, leftToRight);
            final float orientationMargin2 = this.getOrientationMargin(HorizontalMargin.RIGHT, this.rightMargin, attributes, leftToRight);
            if (!(view instanceof HTMLEditorKit.HTMLFactory.BodyBlockView)) {
                n5 = orientationMargin;
                topMargin = this.topMargin;
                n6 = -(orientationMargin + orientationMargin2);
                n7 = -(this.topMargin + this.bottomMargin);
            }
            if (this.bg != null) {
                graphics.setColor(this.bg);
                graphics.fillRect((int)(n + n5), (int)(n2 + topMargin), (int)(n3 + n6), (int)(n4 + n7));
            }
            if (this.bgPainter != null) {
                this.bgPainter.paint(graphics, n + n5, n2 + topMargin, n3 + n6, n4 + n7, view);
            }
            n += orientationMargin;
            n2 += this.topMargin;
            n3 -= orientationMargin + orientationMargin2;
            n4 -= this.topMargin + this.bottomMargin;
            if (this.border instanceof BevelBorder) {
                for (int i = (int)this.getLength(CSS.Attribute.BORDER_TOP_WIDTH, attributes) - 1; i >= 0; --i) {
                    this.border.paintBorder(null, graphics, (int)n + i, (int)n2 + i, (int)n3 - 2 * i, (int)n4 - 2 * i);
                }
            }
            else {
                this.border.paintBorder(null, graphics, (int)n, (int)n2, (int)n3, (int)n4);
            }
        }
        
        float getLength(final CSS.Attribute attribute, final AttributeSet set) {
            return this.css.getLength(set, attribute, this.ss);
        }
        
        static boolean isLeftToRight(final View view) {
            boolean leftToRight = true;
            final Container container;
            if (isOrientationAware(view) && view != null && (container = view.getContainer()) != null) {
                leftToRight = container.getComponentOrientation().isLeftToRight();
            }
            return leftToRight;
        }
        
        static boolean isOrientationAware(final View view) {
            boolean b = false;
            final AttributeSet attributes;
            final Object attribute;
            if (view != null && (attributes = view.getElement().getAttributes()) != null && (attribute = attributes.getAttribute(StyleConstants.NameAttribute)) instanceof HTML.Tag && (attribute == HTML.Tag.DIR || attribute == HTML.Tag.MENU || attribute == HTML.Tag.UL || attribute == HTML.Tag.OL)) {
                b = true;
            }
            return b;
        }
        
        float getOrientationMargin(final HorizontalMargin horizontalMargin, final float n, final AttributeSet set, final boolean b) {
            float n2 = n;
            float n3 = n;
            Object o = null;
            switch (horizontalMargin) {
                case RIGHT: {
                    n3 = (b ? this.getLength(CSS.Attribute.MARGIN_RIGHT_LTR, set) : this.getLength(CSS.Attribute.MARGIN_RIGHT_RTL, set));
                    o = set.getAttribute(CSS.Attribute.MARGIN_RIGHT);
                    break;
                }
                case LEFT: {
                    n3 = (b ? this.getLength(CSS.Attribute.MARGIN_LEFT_LTR, set) : this.getLength(CSS.Attribute.MARGIN_LEFT_RTL, set));
                    o = set.getAttribute(CSS.Attribute.MARGIN_LEFT);
                    break;
                }
            }
            if (o == null && n3 != -2.14748365E9f) {
                n2 = n3;
            }
            return n2;
        }
        
        enum HorizontalMargin
        {
            LEFT, 
            RIGHT;
        }
    }
    
    public static class ListPainter implements Serializable
    {
        static final char[][] romanChars;
        private Rectangle paintRect;
        private boolean checkedForStart;
        private int start;
        private CSS.Value type;
        URL imageurl;
        private StyleSheet ss;
        Icon img;
        private int bulletgap;
        private boolean isLeftToRight;
        
        ListPainter(final AttributeSet set, final StyleSheet ss) {
            this.ss = null;
            this.img = null;
            this.bulletgap = 5;
            this.ss = ss;
            final String s = (String)set.getAttribute(CSS.Attribute.LIST_STYLE_IMAGE);
            this.type = null;
            if (s != null && !s.equals("none")) {
                String s2 = null;
                try {
                    final StringTokenizer stringTokenizer = new StringTokenizer(s, "()");
                    if (stringTokenizer.hasMoreTokens()) {
                        s2 = stringTokenizer.nextToken();
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        s2 = stringTokenizer.nextToken();
                    }
                    this.img = new ImageIcon(new URL(s2));
                }
                catch (final MalformedURLException ex) {
                    if (s2 != null && ss != null && ss.getBase() != null) {
                        try {
                            this.img = new ImageIcon(new URL(ss.getBase(), s2));
                        }
                        catch (final MalformedURLException ex2) {
                            this.img = null;
                        }
                    }
                    else {
                        this.img = null;
                    }
                }
            }
            if (this.img == null) {
                this.type = (CSS.Value)set.getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
            }
            this.start = 1;
            this.paintRect = new Rectangle();
        }
        
        private CSS.Value getChildType(final View view) {
            CSS.Value value = (CSS.Value)view.getAttributes().getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
            if (value == null) {
                if (this.type == null) {
                    final View parent = view.getParent();
                    final HTMLDocument htmlDocument = (HTMLDocument)parent.getDocument();
                    if (HTMLDocument.matchNameAttribute(parent.getElement().getAttributes(), HTML.Tag.OL)) {
                        value = CSS.Value.DECIMAL;
                    }
                    else {
                        value = CSS.Value.DISC;
                    }
                }
                else {
                    value = this.type;
                }
            }
            return value;
        }
        
        private void getStart(final View view) {
            this.checkedForStart = true;
            final Element element = view.getElement();
            if (element != null) {
                final AttributeSet attributes = element.getAttributes();
                final Object attribute;
                if (attributes != null && attributes.isDefined(HTML.Attribute.START) && (attribute = attributes.getAttribute(HTML.Attribute.START)) != null && attribute instanceof String) {
                    try {
                        this.start = Integer.parseInt((String)attribute);
                    }
                    catch (final NumberFormatException ex) {}
                }
            }
        }
        
        private int getRenderIndex(final View view, final int n) {
            if (!this.checkedForStart) {
                this.getStart(view);
            }
            int n2 = n;
            for (int i = n; i >= 0; --i) {
                final AttributeSet attributes = view.getElement().getElement(i).getAttributes();
                if (attributes.getAttribute(StyleConstants.NameAttribute) != HTML.Tag.LI) {
                    --n2;
                }
                else if (attributes.isDefined(HTML.Attribute.VALUE)) {
                    final Object attribute = attributes.getAttribute(HTML.Attribute.VALUE);
                    if (attribute != null && attribute instanceof String) {
                        try {
                            return n2 - i + Integer.parseInt((String)attribute);
                        }
                        catch (final NumberFormatException ex) {}
                    }
                }
            }
            return n2 + this.start;
        }
        
        public void paint(final Graphics graphics, final float n, float n2, final float n3, float n4, final View view, final int n5) {
            final View view2 = view.getView(n5);
            final Container container = view.getContainer();
            final Object attribute = view2.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (!(attribute instanceof HTML.Tag) || attribute != HTML.Tag.LI) {
                return;
            }
            this.isLeftToRight = container.getComponentOrientation().isLeftToRight();
            float alignment = 0.0f;
            if (view2.getViewCount() > 0) {
                final View view3 = view2.getView(0);
                final Object attribute2 = view3.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
                if ((attribute2 == HTML.Tag.P || attribute2 == HTML.Tag.IMPLIED) && view3.getViewCount() > 0) {
                    this.paintRect.setBounds((int)n, (int)n2, (int)n3, (int)n4);
                    final Shape childAllocation = view2.getChildAllocation(0, this.paintRect);
                    final Shape childAllocation2;
                    if (childAllocation != null && (childAllocation2 = view3.getView(0).getChildAllocation(0, childAllocation)) != null) {
                        final Rectangle rectangle = (Rectangle)((childAllocation2 instanceof Rectangle) ? childAllocation2 : childAllocation2.getBounds());
                        alignment = view3.getView(0).getAlignment(1);
                        n2 = (float)rectangle.y;
                        n4 = (float)rectangle.height;
                    }
                }
            }
            graphics.setColor(container.isEnabled() ? ((this.ss != null) ? this.ss.getForeground(view2.getAttributes()) : container.getForeground()) : UIManager.getColor("textInactiveText"));
            if (this.img != null) {
                this.drawIcon(graphics, (int)n, (int)n2, (int)n3, (int)n4, alignment, container);
                return;
            }
            final CSS.Value childType = this.getChildType(view2);
            final Font font = ((StyledDocument)view2.getDocument()).getFont(view2.getAttributes());
            if (font != null) {
                graphics.setFont(font);
            }
            if (childType == CSS.Value.SQUARE || childType == CSS.Value.CIRCLE || childType == CSS.Value.DISC) {
                this.drawShape(graphics, childType, (int)n, (int)n2, (int)n3, (int)n4, alignment);
            }
            else if (childType == CSS.Value.DECIMAL) {
                this.drawLetter(graphics, '1', (int)n, (int)n2, (int)n3, (int)n4, alignment, this.getRenderIndex(view, n5));
            }
            else if (childType == CSS.Value.LOWER_ALPHA) {
                this.drawLetter(graphics, 'a', (int)n, (int)n2, (int)n3, (int)n4, alignment, this.getRenderIndex(view, n5));
            }
            else if (childType == CSS.Value.UPPER_ALPHA) {
                this.drawLetter(graphics, 'A', (int)n, (int)n2, (int)n3, (int)n4, alignment, this.getRenderIndex(view, n5));
            }
            else if (childType == CSS.Value.LOWER_ROMAN) {
                this.drawLetter(graphics, 'i', (int)n, (int)n2, (int)n3, (int)n4, alignment, this.getRenderIndex(view, n5));
            }
            else if (childType == CSS.Value.UPPER_ROMAN) {
                this.drawLetter(graphics, 'I', (int)n, (int)n2, (int)n3, (int)n4, alignment, this.getRenderIndex(view, n5));
            }
        }
        
        void drawIcon(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final float n5, final Component component) {
            this.img.paintIcon(component, graphics, n + (this.isLeftToRight ? (-(this.img.getIconWidth() + this.bulletgap)) : (n3 + this.bulletgap)), Math.max(n2, n2 + (int)(n5 * n4) - this.img.getIconHeight()));
        }
        
        void drawShape(final Graphics graphics, final CSS.Value value, final int n, final int n2, final int n3, final int n4, final float n5) {
            final int n6 = n + (this.isLeftToRight ? (-(this.bulletgap + 8)) : (n3 + this.bulletgap));
            final int max = Math.max(n2, n2 + (int)(n5 * n4) - 8);
            if (value == CSS.Value.SQUARE) {
                graphics.drawRect(n6, max, 8, 8);
            }
            else if (value == CSS.Value.CIRCLE) {
                graphics.drawOval(n6, max, 8, 8);
            }
            else {
                graphics.fillOval(n6, max, 8, 8);
            }
        }
        
        void drawLetter(final Graphics graphics, final char c, final int n, final int n2, final int n3, final int n4, final float n5, final int n6) {
            final String formatItemNum = this.formatItemNum(n6, c);
            final String s = this.isLeftToRight ? (formatItemNum + ".") : ("." + formatItemNum);
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(null, graphics);
            final int stringWidth = SwingUtilities2.stringWidth(null, fontMetrics, s);
            SwingUtilities2.drawString(null, graphics, s, n + (this.isLeftToRight ? (-(stringWidth + this.bulletgap)) : (n3 + this.bulletgap)), Math.max(n2 + fontMetrics.getAscent(), n2 + (int)(n4 * n5)));
        }
        
        String formatItemNum(final int n, final char c) {
            boolean b = false;
            String s = null;
            switch (c) {
                default: {
                    s = String.valueOf(n);
                    break;
                }
                case 'A': {
                    b = true;
                }
                case 'a': {
                    s = this.formatAlphaNumerals(n);
                    break;
                }
                case 'I': {
                    b = true;
                }
                case 'i': {
                    s = this.formatRomanNumerals(n);
                    break;
                }
            }
            if (b) {
                s = s.toUpperCase();
            }
            return s;
        }
        
        String formatAlphaNumerals(final int n) {
            String s;
            if (n > 26) {
                s = this.formatAlphaNumerals(n / 26) + this.formatAlphaNumerals(n % 26);
            }
            else {
                s = String.valueOf((char)(97 + n - 1));
            }
            return s;
        }
        
        String formatRomanNumerals(final int n) {
            return this.formatRomanNumerals(0, n);
        }
        
        String formatRomanNumerals(final int n, final int n2) {
            if (n2 < 10) {
                return this.formatRomanDigit(n, n2);
            }
            return this.formatRomanNumerals(n + 1, n2 / 10) + this.formatRomanDigit(n, n2 % 10);
        }
        
        String formatRomanDigit(final int n, int n2) {
            String s = "";
            if (n2 == 9) {
                return s + ListPainter.romanChars[n][0] + ListPainter.romanChars[n + 1][0];
            }
            if (n2 == 4) {
                return s + ListPainter.romanChars[n][0] + ListPainter.romanChars[n][1];
            }
            if (n2 >= 5) {
                s += ListPainter.romanChars[n][1];
                n2 -= 5;
            }
            for (int i = 0; i < n2; ++i) {
                s += ListPainter.romanChars[n][0];
            }
            return s;
        }
        
        static {
            romanChars = new char[][] { { 'i', 'v' }, { 'x', 'l' }, { 'c', 'd' }, { 'm', '?' } };
        }
    }
    
    static class BackgroundImagePainter implements Serializable
    {
        ImageIcon backgroundImage;
        float hPosition;
        float vPosition;
        short flags;
        private int paintX;
        private int paintY;
        private int paintMaxX;
        private int paintMaxY;
        
        BackgroundImagePainter(final AttributeSet set, final CSS css, final StyleSheet styleSheet) {
            this.backgroundImage = styleSheet.getBackgroundImage(set);
            final CSS.BackgroundPosition backgroundPosition = (CSS.BackgroundPosition)set.getAttribute(CSS.Attribute.BACKGROUND_POSITION);
            if (backgroundPosition != null) {
                this.hPosition = backgroundPosition.getHorizontalPosition();
                this.vPosition = backgroundPosition.getVerticalPosition();
                if (backgroundPosition.isHorizontalPositionRelativeToSize()) {
                    this.flags |= 0x4;
                }
                else if (backgroundPosition.isHorizontalPositionRelativeToSize()) {
                    this.hPosition *= CSS.getFontSize(set, 12, styleSheet);
                }
                if (backgroundPosition.isVerticalPositionRelativeToSize()) {
                    this.flags |= 0x8;
                }
                else if (backgroundPosition.isVerticalPositionRelativeToFontSize()) {
                    this.vPosition *= CSS.getFontSize(set, 12, styleSheet);
                }
            }
            final CSS.Value value = (CSS.Value)set.getAttribute(CSS.Attribute.BACKGROUND_REPEAT);
            if (value == null || value == CSS.Value.BACKGROUND_REPEAT) {
                this.flags |= 0x3;
            }
            else if (value == CSS.Value.BACKGROUND_REPEAT_X) {
                this.flags |= 0x1;
            }
            else if (value == CSS.Value.BACKGROUND_REPEAT_Y) {
                this.flags |= 0x2;
            }
        }
        
        void paint(final Graphics graphics, final float n, final float n2, final float n3, final float n4, final View view) {
            final Rectangle clipRect = graphics.getClipRect();
            if (clipRect != null) {
                graphics.clipRect((int)n, (int)n2, (int)n3, (int)n4);
            }
            if ((this.flags & 0x3) == 0x0) {
                final int iconWidth = this.backgroundImage.getIconWidth();
                final int iconWidth2 = this.backgroundImage.getIconWidth();
                if ((this.flags & 0x4) == 0x4) {
                    this.paintX = (int)(n + n3 * this.hPosition - iconWidth * this.hPosition);
                }
                else {
                    this.paintX = (int)n + (int)this.hPosition;
                }
                if ((this.flags & 0x8) == 0x8) {
                    this.paintY = (int)(n2 + n4 * this.vPosition - iconWidth2 * this.vPosition);
                }
                else {
                    this.paintY = (int)n2 + (int)this.vPosition;
                }
                if (clipRect == null || (this.paintX + iconWidth > clipRect.x && this.paintY + iconWidth2 > clipRect.y && this.paintX < clipRect.x + clipRect.width && this.paintY < clipRect.y + clipRect.height)) {
                    this.backgroundImage.paintIcon(null, graphics, this.paintX, this.paintY);
                }
            }
            else {
                final int iconWidth3 = this.backgroundImage.getIconWidth();
                final int iconHeight = this.backgroundImage.getIconHeight();
                if (iconWidth3 > 0 && iconHeight > 0) {
                    this.paintX = (int)n;
                    this.paintY = (int)n2;
                    this.paintMaxX = (int)(n + n3);
                    this.paintMaxY = (int)(n2 + n4);
                    if (this.updatePaintCoordinates(clipRect, iconWidth3, iconHeight)) {
                        while (this.paintX < this.paintMaxX) {
                            for (int i = this.paintY; i < this.paintMaxY; i += iconHeight) {
                                this.backgroundImage.paintIcon(null, graphics, this.paintX, i);
                            }
                            this.paintX += iconWidth3;
                        }
                    }
                }
            }
            if (clipRect != null) {
                graphics.setClip(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
            }
        }
        
        private boolean updatePaintCoordinates(final Rectangle rectangle, final int n, final int n2) {
            if ((this.flags & 0x3) == 0x1) {
                this.paintMaxY = this.paintY + 1;
            }
            else if ((this.flags & 0x3) == 0x2) {
                this.paintMaxX = this.paintX + 1;
            }
            if (rectangle != null) {
                if ((this.flags & 0x3) == 0x1 && (this.paintY + n2 <= rectangle.y || this.paintY > rectangle.y + rectangle.height)) {
                    return false;
                }
                if ((this.flags & 0x3) == 0x2 && (this.paintX + n <= rectangle.x || this.paintX > rectangle.x + rectangle.width)) {
                    return false;
                }
                if ((this.flags & 0x1) == 0x1) {
                    if (rectangle.x + rectangle.width < this.paintMaxX) {
                        if ((rectangle.x + rectangle.width - this.paintX) % n == 0) {
                            this.paintMaxX = rectangle.x + rectangle.width;
                        }
                        else {
                            this.paintMaxX = ((rectangle.x + rectangle.width - this.paintX) / n + 1) * n + this.paintX;
                        }
                    }
                    if (rectangle.x > this.paintX) {
                        this.paintX += (rectangle.x - this.paintX) / n * n;
                    }
                }
                if ((this.flags & 0x2) == 0x2) {
                    if (rectangle.y + rectangle.height < this.paintMaxY) {
                        if ((rectangle.y + rectangle.height - this.paintY) % n2 == 0) {
                            this.paintMaxY = rectangle.y + rectangle.height;
                        }
                        else {
                            this.paintMaxY = ((rectangle.y + rectangle.height - this.paintY) / n2 + 1) * n2 + this.paintY;
                        }
                    }
                    if (rectangle.y > this.paintY) {
                        this.paintY += (rectangle.y - this.paintY) / n2 * n2;
                    }
                }
            }
            return true;
        }
    }
    
    class ViewAttributeSet extends MuxingAttributeSet
    {
        View host;
        
        ViewAttributeSet(final View host) {
            this.host = host;
            final Document document = host.getDocument();
            final SearchBuffer obtainSearchBuffer = SearchBuffer.obtainSearchBuffer();
            final Vector vector = obtainSearchBuffer.getVector();
            try {
                if (document instanceof HTMLDocument) {
                    final Element element = host.getElement();
                    final AttributeSet attributes = element.getAttributes();
                    final AttributeSet translateHTMLToCSS = StyleSheet.this.translateHTMLToCSS(attributes);
                    if (translateHTMLToCSS.getAttributeCount() != 0) {
                        vector.addElement(translateHTMLToCSS);
                    }
                    if (element.isLeaf()) {
                        final Enumeration<?> attributeNames = attributes.getAttributeNames();
                        while (attributeNames.hasMoreElements()) {
                            final Object nextElement = attributeNames.nextElement();
                            if (nextElement instanceof HTML.Tag) {
                                if (nextElement == HTML.Tag.A) {
                                    final Object attribute = attributes.getAttribute(nextElement);
                                    if (attribute != null && attribute instanceof AttributeSet && ((AttributeSet)attribute).getAttribute(HTML.Attribute.HREF) == null) {
                                        continue;
                                    }
                                }
                                final Style rule = StyleSheet.this.getRule((HTML.Tag)nextElement, element);
                                if (rule == null) {
                                    continue;
                                }
                                vector.addElement(rule);
                            }
                        }
                    }
                    else {
                        final Style rule2 = StyleSheet.this.getRule((HTML.Tag)attributes.getAttribute(StyleConstants.NameAttribute), element);
                        if (rule2 != null) {
                            vector.addElement(rule2);
                        }
                    }
                }
                final AttributeSet[] attributes2 = new AttributeSet[vector.size()];
                vector.copyInto(attributes2);
                this.setAttributes(attributes2);
            }
            finally {
                SearchBuffer.releaseSearchBuffer(obtainSearchBuffer);
            }
        }
        
        @Override
        public boolean isDefined(Object o) {
            if (o instanceof StyleConstants) {
                final CSS.Attribute styleConstantsKeyToCSSKey = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
                if (styleConstantsKeyToCSSKey != null) {
                    o = styleConstantsKeyToCSSKey;
                }
            }
            return super.isDefined(o);
        }
        
        @Override
        public Object getAttribute(final Object o) {
            if (o instanceof StyleConstants) {
                final CSS.Attribute styleConstantsKeyToCSSKey = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)o);
                if (styleConstantsKeyToCSSKey != null) {
                    final Object doGetAttribute = this.doGetAttribute(styleConstantsKeyToCSSKey);
                    if (doGetAttribute instanceof CSS.CssValue) {
                        return ((CSS.CssValue)doGetAttribute).toStyleConstants((StyleConstants)o, this.host);
                    }
                }
            }
            return this.doGetAttribute(o);
        }
        
        Object doGetAttribute(final Object o) {
            final Object attribute = super.getAttribute(o);
            if (attribute != null) {
                return attribute;
            }
            if (o instanceof CSS.Attribute && ((CSS.Attribute)o).isInherited()) {
                final AttributeSet resolveParent = this.getResolveParent();
                if (resolveParent != null) {
                    return resolveParent.getAttribute(o);
                }
            }
            return null;
        }
        
        @Override
        public AttributeSet getResolveParent() {
            if (this.host == null) {
                return null;
            }
            final View parent = this.host.getParent();
            return (parent != null) ? parent.getAttributes() : null;
        }
    }
    
    static class ResolvedStyle extends MuxingAttributeSet implements Serializable, Style
    {
        String name;
        private int extendedIndex;
        
        ResolvedStyle(final String name, final AttributeSet[] array, final int extendedIndex) {
            super(array);
            this.name = name;
            this.extendedIndex = extendedIndex;
        }
        
        synchronized void insertStyle(final Style style, final int n) {
            final AttributeSet[] attributes = this.getAttributes();
            final int length = attributes.length;
            int n2;
            for (n2 = 0; n2 < this.extendedIndex && n <= StyleSheet.getSpecificity(((Style)attributes[n2]).getName()); ++n2) {}
            this.insertAttributeSetAt(style, n2);
            ++this.extendedIndex;
        }
        
        synchronized void removeStyle(final Style style) {
            final AttributeSet[] attributes = this.getAttributes();
            int i = attributes.length - 1;
            while (i >= 0) {
                if (attributes[i] == style) {
                    this.removeAttributeSetAt(i);
                    if (i < this.extendedIndex) {
                        --this.extendedIndex;
                        break;
                    }
                    break;
                }
                else {
                    --i;
                }
            }
        }
        
        synchronized void insertExtendedStyleAt(final Style style, final int n) {
            this.insertAttributeSetAt(style, this.extendedIndex + n);
        }
        
        synchronized void addExtendedStyle(final Style style) {
            this.insertAttributeSetAt(style, this.getAttributes().length);
        }
        
        synchronized void removeExtendedStyleAt(final int n) {
            this.removeAttributeSetAt(this.extendedIndex + n);
        }
        
        protected boolean matches(final String s) {
            final int length = s.length();
            if (length == 0) {
                return false;
            }
            final int length2 = this.name.length();
            int i = s.lastIndexOf(32);
            int n = this.name.lastIndexOf(32);
            if (i >= 0) {
                ++i;
            }
            if (n >= 0) {
                ++n;
            }
            if (!this.matches(s, i, length, n, length2)) {
                return false;
            }
            while (i != -1) {
                final int n2 = i - 1;
                i = s.lastIndexOf(32, n2 - 1);
                if (i >= 0) {
                    ++i;
                }
                boolean matches;
                int n3;
                for (matches = false; !matches && n != -1; matches = this.matches(s, i, n2, n, n3)) {
                    n3 = n - 1;
                    n = this.name.lastIndexOf(32, n3 - 1);
                    if (n >= 0) {
                        ++n;
                    }
                }
                if (!matches) {
                    return false;
                }
            }
            return true;
        }
        
        boolean matches(final String s, int max, final int n, int max2, final int n2) {
            max = Math.max(max, 0);
            max2 = Math.max(max2, 0);
            final int boundedIndex = this.boundedIndexOf(this.name, '.', max2, n2);
            final int boundedIndex2 = this.boundedIndexOf(this.name, '#', max2, n2);
            final int boundedIndex3 = this.boundedIndexOf(s, '.', max, n);
            final int boundedIndex4 = this.boundedIndexOf(s, '#', max, n);
            if (boundedIndex3 != -1) {
                if (boundedIndex == -1) {
                    return false;
                }
                if (max == boundedIndex3) {
                    if (n2 - boundedIndex != n - boundedIndex3 || !s.regionMatches(max, this.name, boundedIndex, n2 - boundedIndex)) {
                        return false;
                    }
                }
                else if (n - max != n2 - max2 || !s.regionMatches(max, this.name, max2, n2 - max2)) {
                    return false;
                }
                return true;
            }
            else if (boundedIndex4 != -1) {
                if (boundedIndex2 == -1) {
                    return false;
                }
                if (max == boundedIndex4) {
                    if (n2 - boundedIndex2 != n - boundedIndex4 || !s.regionMatches(max, this.name, boundedIndex2, n2 - boundedIndex2)) {
                        return false;
                    }
                }
                else if (n - max != n2 - max2 || !s.regionMatches(max, this.name, max2, n2 - max2)) {
                    return false;
                }
                return true;
            }
            else {
                if (boundedIndex != -1) {
                    return boundedIndex - max2 == n - max && s.regionMatches(max, this.name, max2, boundedIndex - max2);
                }
                if (boundedIndex2 != -1) {
                    return boundedIndex2 - max2 == n - max && s.regionMatches(max, this.name, max2, boundedIndex2 - max2);
                }
                return n2 - max2 == n - max && s.regionMatches(max, this.name, max2, n2 - max2);
            }
        }
        
        int boundedIndexOf(final String s, final char c, final int n, final int n2) {
            final int index = s.indexOf(c, n);
            if (index >= n2) {
                return -1;
            }
            return index;
        }
        
        @Override
        public void addAttribute(final Object o, final Object o2) {
        }
        
        @Override
        public void addAttributes(final AttributeSet set) {
        }
        
        @Override
        public void removeAttribute(final Object o) {
        }
        
        @Override
        public void removeAttributes(final Enumeration<?> enumeration) {
        }
        
        @Override
        public void removeAttributes(final AttributeSet set) {
        }
        
        @Override
        public void setResolveParent(final AttributeSet set) {
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public void addChangeListener(final ChangeListener changeListener) {
        }
        
        @Override
        public void removeChangeListener(final ChangeListener changeListener) {
        }
        
        public ChangeListener[] getChangeListeners() {
            return new ChangeListener[0];
        }
    }
    
    static class SelectorMapping implements Serializable
    {
        private int specificity;
        private Style style;
        private HashMap<String, SelectorMapping> children;
        
        public SelectorMapping(final int specificity) {
            this.specificity = specificity;
        }
        
        public int getSpecificity() {
            return this.specificity;
        }
        
        public void setStyle(final Style style) {
            this.style = style;
        }
        
        public Style getStyle() {
            return this.style;
        }
        
        public SelectorMapping getChildSelectorMapping(final String s, final boolean b) {
            SelectorMapping childSelectorMapping = null;
            if (this.children != null) {
                childSelectorMapping = this.children.get(s);
            }
            else if (b) {
                this.children = new HashMap<String, SelectorMapping>(7);
            }
            if (childSelectorMapping == null && b) {
                childSelectorMapping = this.createChildSelectorMapping(this.getChildSpecificity(s));
                this.children.put(s, childSelectorMapping);
            }
            return childSelectorMapping;
        }
        
        protected SelectorMapping createChildSelectorMapping(final int n) {
            return new SelectorMapping(n);
        }
        
        protected int getChildSpecificity(final String s) {
            final char char1 = s.charAt(0);
            int specificity = this.getSpecificity();
            if (char1 == '.') {
                specificity += 100;
            }
            else if (char1 == '#') {
                specificity += 10000;
            }
            else {
                ++specificity;
                if (s.indexOf(46) != -1) {
                    specificity += 100;
                }
                if (s.indexOf(35) != -1) {
                    specificity += 10000;
                }
            }
            return specificity;
        }
    }
    
    class CssParser implements CSSParser.CSSParserCallback
    {
        Vector<String[]> selectors;
        Vector<String> selectorTokens;
        String propertyName;
        MutableAttributeSet declaration;
        boolean parsingDeclaration;
        boolean isLink;
        URL base;
        CSSParser parser;
        
        CssParser() {
            this.selectors = new Vector<String[]>();
            this.selectorTokens = new Vector<String>();
            this.declaration = new SimpleAttributeSet();
            this.parser = new CSSParser();
        }
        
        public AttributeSet parseDeclaration(final String s) {
            try {
                return this.parseDeclaration(new StringReader(s));
            }
            catch (final IOException ex) {
                return null;
            }
        }
        
        public AttributeSet parseDeclaration(final Reader reader) throws IOException {
            this.parse(this.base, reader, true, false);
            return this.declaration.copyAttributes();
        }
        
        public void parse(final URL base, final Reader reader, final boolean parsingDeclaration, final boolean isLink) throws IOException {
            this.base = base;
            this.isLink = isLink;
            this.parsingDeclaration = parsingDeclaration;
            this.declaration.removeAttributes(this.declaration);
            this.selectorTokens.removeAllElements();
            this.selectors.removeAllElements();
            this.propertyName = null;
            this.parser.parse(reader, this, parsingDeclaration);
        }
        
        @Override
        public void handleImport(final String s) {
            final URL url = CSS.getURL(this.base, s);
            if (url != null) {
                StyleSheet.this.importStyleSheet(url);
            }
        }
        
        @Override
        public void handleSelector(String s) {
            if (!s.startsWith(".") && !s.startsWith("#")) {
                s = s.toLowerCase();
            }
            final int length = s.length();
            if (s.endsWith(",")) {
                if (length > 1) {
                    s = s.substring(0, length - 1);
                    this.selectorTokens.addElement(s);
                }
                this.addSelector();
            }
            else if (length > 0) {
                this.selectorTokens.addElement(s);
            }
        }
        
        @Override
        public void startRule() {
            if (this.selectorTokens.size() > 0) {
                this.addSelector();
            }
            this.propertyName = null;
        }
        
        @Override
        public void handleProperty(final String propertyName) {
            this.propertyName = propertyName;
        }
        
        @Override
        public void handleValue(String string) {
            if (this.propertyName != null && string != null && string.length() > 0) {
                final CSS.Attribute attribute = CSS.getAttribute(this.propertyName);
                if (attribute != null) {
                    if (attribute == CSS.Attribute.LIST_STYLE_IMAGE && string != null && !string.equals("none")) {
                        final URL url = CSS.getURL(this.base, string);
                        if (url != null) {
                            string = url.toString();
                        }
                    }
                    StyleSheet.this.addCSSAttribute(this.declaration, attribute, string);
                }
                this.propertyName = null;
            }
        }
        
        @Override
        public void endRule() {
            for (int size = this.selectors.size(), i = 0; i < size; ++i) {
                final String[] array = this.selectors.elementAt(i);
                if (array.length > 0) {
                    StyleSheet.this.addRule(array, this.declaration, this.isLink);
                }
            }
            this.declaration.removeAttributes(this.declaration);
            this.selectors.removeAllElements();
        }
        
        private void addSelector() {
            final String[] array = new String[this.selectorTokens.size()];
            this.selectorTokens.copyInto(array);
            this.selectors.addElement(array);
            this.selectorTokens.removeAllElements();
        }
    }
}

package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.DOMException;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import com.steadystate.css.format.CSSFormat;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.css.CSSRule;
import java.io.Serializable;
import org.w3c.dom.css.CSS2Properties;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.dom.css.CSSStyleDeclaration;

public class CSSStyleDeclarationImpl implements CSSStyleDeclaration, CSSFormatable, CSS2Properties, Serializable
{
    private static final long serialVersionUID = -2373755821317100189L;
    private static final String PRIORITY_IMPORTANT = "important";
    private CSSRule parentRule_;
    private List<Property> properties_;
    private static final String AZIMUTH = "azimuth";
    private static final String BACKGROUND = "background";
    private static final String BACKGROUND_ATTACHMENT = "background-attachment";
    private static final String BACKGROUND_COLOR = "background-color";
    private static final String BACKGROUND_IMAGE = "background-image";
    private static final String BACKGROUND_POSITION = "background-position";
    private static final String BACKGROUND_REPEAT = "background-repeat";
    private static final String BORDER = "border";
    private static final String BORDER_BOTTOM = "border-bottom";
    private static final String BORDER_BOTTOM_COLOR = "border-bottom-color";
    private static final String BORDER_BOTTOM_STYLE = "border-bottom-style";
    private static final String BORDER_BOTTOM_WIDTH = "border-bottom-width";
    private static final String BORDER_COLLAPSE = "border-collapse";
    private static final String BORDER_COLOR = "border-color";
    private static final String BORDER_LEFT = "border-left";
    private static final String BORDER_LEFT_COLOR = "border-left-color";
    private static final String BORDER_LEFT_STYLE = "border-left-style";
    private static final String BORDER_LEFT_WIDTH = "border-left-width";
    private static final String BORDER_RIGHT = "border-right";
    private static final String BORDER_RIGHT_COLOR = "border-right-color";
    private static final String BORDER_RIGHT_STYLE = "border-right-style";
    private static final String BORDER_RIGHT_WIDTH = "border-right-width";
    private static final String BORDER_SPACING = "border-spacing";
    private static final String BORDER_STYLE = "border-style";
    private static final String BORDER_TOP = "border-top";
    private static final String BORDER_TOP_COLOR = "border-top-color";
    private static final String BORDER_TOP_STYLE = "border-top-style";
    private static final String BORDER_TOP_WIDTH = "border-top-width";
    private static final String BORDER_WIDTH = "border-width";
    private static final String BOTTOM = "bottom";
    private static final String CAPTION_SIDE = "caption-side";
    private static final String CLEAR = "clear";
    private static final String CLIP = "clip";
    private static final String COLOR = "color";
    private static final String CONTENT = "content";
    private static final String COUNTER_INCREMENT = "counter-increment";
    private static final String COUNTER_RESET = "counter-reset";
    private static final String CSS_FLOAT = "css-float";
    private static final String CUE = "cue";
    private static final String CUE_AFTER = "cue-after";
    private static final String CUE_BEFORE = "cue-before";
    private static final String CURSOR = "cursor";
    private static final String DIRECTION = "direction";
    private static final String DISPLAY = "display";
    private static final String ELEVATION = "elevation";
    private static final String EMPTY_CELLS = "empty-cells";
    private static final String FONT = "font";
    private static final String FONT_FAMILY = "font-family";
    private static final String FONT_SIZE = "font-size";
    private static final String FONT_SIZE_ADJUST = "font-size-adjust";
    private static final String FONT_STRETCH = "font-stretch";
    private static final String FONT_STYLE = "font-style";
    private static final String FONT_VARIANT = "font-variant";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String HEIGHT = "height";
    private static final String LEFT = "left";
    private static final String LETTER_SPACING = "letter-spacing";
    private static final String LINE_HEIGHT = "line-height";
    private static final String LIST_STYLE = "list-style";
    private static final String LIST_STYLE_IMAGE = "list-style-image";
    private static final String LIST_STYLE_POSITION = "list-style-position";
    private static final String LIST_STYLE_TYPE = "list-style-type";
    private static final String MARGIN = "margin";
    private static final String MARGIN_BOTTOM = "margin-bottom";
    private static final String MARGIN_LEFT = "margin-left";
    private static final String MARGIN_RIGHT = "margin-right";
    private static final String MARGIN_TOP = "margin-top";
    private static final String MARKER_OFFSET = "marker-offset";
    private static final String MARKS = "marks";
    private static final String MAX_HEIGHT = "max-height";
    private static final String MAX_WIDTH = "max-width";
    private static final String MIN_HEIGHT = "min-height";
    private static final String MIN_WIDTH = "min-width";
    private static final String ORPHANS = "orphans";
    private static final String OUTLINE = "outline";
    private static final String OUTLINE_COLOR = "outline-color";
    private static final String OUTLINE_STYLE = "outline-style";
    private static final String OUTLINE_WIDTH = "outline-width";
    private static final String OVERFLOW = "overflow";
    private static final String PADDING = "padding";
    private static final String PADDING_BOTTOM = "padding-bottom";
    private static final String PADDING_LEFT = "padding-left";
    private static final String PADDING_RIGHT = "padding-right";
    private static final String PADDING_TOP = "padding-top";
    private static final String PAGE = "page";
    private static final String PAGE_BREAK_AFTER = "page-break-after";
    private static final String PAGE_BREAK_BEFORE = "page-break-before";
    private static final String PAGE_BREAK_INSIDE = "page-break-inside";
    private static final String PAUSE = "pause";
    private static final String PAUSE_AFTER = "pause-after";
    private static final String PAUSE_BEFORE = "pause-before";
    private static final String PITCH = "pitch";
    private static final String PITCH_RANGE = "pitch-range";
    private static final String PLAY_DURING = "play-during";
    private static final String POSITION = "position";
    private static final String QUOTES = "quotes";
    private static final String RICHNESS = "richness";
    private static final String RIGHT = "right";
    private static final String SIZE = "size";
    private static final String SPEAK = "speak";
    private static final String SPEAK_HEADER = "speak-header";
    private static final String SPEAK_NUMERAL = "speak-numeral";
    private static final String SPEAK_PUNCTUATION = "speak-puctuation";
    private static final String SPEECH_RATE = "speech-rate";
    private static final String STRESS = "stress";
    private static final String TABLE_LAYOUT = "table-layout";
    private static final String TEXT_ALIGN = "text-align";
    private static final String TEXT_DECORATION = "text-decoration";
    private static final String TEXT_INDENT = "text-indent";
    private static final String TEXT_SHADOW = "text-shadow";
    private static final String TEXT_TRANSFORM = "text-transform";
    private static final String TOP = "top";
    private static final String UNICODE_BIDI = "unicode-bidi";
    private static final String VERTICAL_ALIGN = "vertical-align";
    private static final String VISIBILITY = "visibility";
    private static final String VOICE_FAMILY = "voice-family";
    private static final String VOLUME = "volume";
    private static final String WHITE_SPACE = "white-space";
    private static final String WIDOWS = "widows";
    private static final String WIDTH = "width";
    private static final String WORD_SPACING = "word_spacing";
    private static final String Z_INDEX = "z-index";
    
    public void setParentRule(final CSSRule parentRule) {
        this.parentRule_ = parentRule;
    }
    
    public List<Property> getProperties() {
        return this.properties_;
    }
    
    public void setProperties(final List<Property> properties) {
        this.properties_ = properties;
    }
    
    public CSSStyleDeclarationImpl(final CSSRule parentRule) {
        this.properties_ = new ArrayList<Property>();
        this.parentRule_ = parentRule;
    }
    
    public CSSStyleDeclarationImpl() {
        this.properties_ = new ArrayList<Property>();
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        final boolean nl = format != null && format.getPropertiesInSeparateLines();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.properties_.size(); ++i) {
            final Property p = this.properties_.get(i);
            if (p != null) {
                if (nl) {
                    sb.append(format.getNewLine());
                    sb.append(format.getPropertiesIndent());
                }
                sb.append(p.getCssText(format));
            }
            if (i < this.properties_.size() - 1) {
                sb.append(";");
                if (!nl) {
                    sb.append(' ');
                }
            }
            else if (nl) {
                sb.append(format.getNewLine());
            }
        }
        return sb.toString();
    }
    
    public void setCssText(final String cssText) throws DOMException {
        try {
            final InputSource is = new InputSource((Reader)new StringReader(cssText));
            final CSSOMParser parser = new CSSOMParser();
            this.properties_.clear();
            parser.parseStyleDeclaration(this, is);
        }
        catch (final Exception e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
    }
    
    public String getPropertyValue(final String propertyName) {
        final Property p = this.getPropertyDeclaration(propertyName);
        if (p == null || p.getValue() == null) {
            return "";
        }
        return p.getValue().toString();
    }
    
    public CSSValue getPropertyCSSValue(final String propertyName) {
        final Property p = this.getPropertyDeclaration(propertyName);
        return (p == null) ? null : p.getValue();
    }
    
    public String removeProperty(final String propertyName) throws DOMException {
        if (null == propertyName) {
            return "";
        }
        int i = 0;
        while (i < this.properties_.size()) {
            final Property p = this.properties_.get(i);
            if (p != null && propertyName.equalsIgnoreCase(p.getName())) {
                this.properties_.remove(i);
                if (p.getValue() == null) {
                    return "";
                }
                return p.getValue().toString();
            }
            else {
                ++i;
            }
        }
        return "";
    }
    
    public String getPropertyPriority(final String propertyName) {
        final Property p = this.getPropertyDeclaration(propertyName);
        if (p == null) {
            return "";
        }
        return p.isImportant() ? "important" : "";
    }
    
    public void setProperty(final String propertyName, final String value, final String priority) throws DOMException {
        try {
            CSSValue expr = null;
            if (!value.isEmpty()) {
                final CSSOMParser parser = new CSSOMParser();
                final InputSource is = new InputSource((Reader)new StringReader(value));
                expr = parser.parsePropertyValue(is);
            }
            Property p = this.getPropertyDeclaration(propertyName);
            final boolean important = "important".equalsIgnoreCase(priority);
            if (p == null) {
                p = new Property(propertyName, expr, important);
                this.addProperty(p);
            }
            else {
                p.setValue(expr);
                p.setImportant(important);
            }
        }
        catch (final Exception e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
    }
    
    public int getLength() {
        return this.properties_.size();
    }
    
    public String item(final int index) {
        final Property p = this.properties_.get(index);
        return (p == null) ? "" : p.getName();
    }
    
    public CSSRule getParentRule() {
        return this.parentRule_;
    }
    
    public void addProperty(final Property p) {
        if (null == p) {
            return;
        }
        this.properties_.add(p);
    }
    
    public Property getPropertyDeclaration(final String propertyName) {
        if (null == propertyName) {
            return null;
        }
        for (int i = this.properties_.size() - 1; i > -1; --i) {
            final Property p = this.properties_.get(i);
            if (p != null && propertyName.equalsIgnoreCase(p.getName())) {
                return p;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSStyleDeclaration)) {
            return false;
        }
        final CSSStyleDeclaration csd = (CSSStyleDeclaration)obj;
        return this.equalsProperties(csd);
    }
    
    private boolean equalsProperties(final CSSStyleDeclaration csd) {
        if (csd == null || this.getLength() != csd.getLength()) {
            return false;
        }
        for (int i = 0; i < this.getLength(); ++i) {
            final String propertyName = this.item(i);
            final String propertyValue1 = this.getPropertyValue(propertyName);
            final String propertyValue2 = csd.getPropertyValue(propertyName);
            if (!LangUtils.equals(propertyValue1, propertyValue2)) {
                return false;
            }
            final String propertyPriority1 = this.getPropertyPriority(propertyName);
            final String propertyPriority2 = csd.getPropertyPriority(propertyName);
            if (!LangUtils.equals(propertyPriority1, propertyPriority2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.properties_);
        return hash;
    }
    
    public String getAzimuth() {
        return this.getPropertyValue("azimuth");
    }
    
    public void setAzimuth(final String azimuth) throws DOMException {
        this.setProperty("azimuth", azimuth, null);
    }
    
    public String getBackground() {
        return this.getPropertyValue("background");
    }
    
    public void setBackground(final String background) throws DOMException {
        this.setProperty("background", background, null);
    }
    
    public String getBackgroundAttachment() {
        return this.getPropertyValue("background-attachment");
    }
    
    public void setBackgroundAttachment(final String backgroundAttachment) throws DOMException {
        this.setProperty("background-attachment", backgroundAttachment, null);
    }
    
    public String getBackgroundColor() {
        return this.getPropertyValue("background-color");
    }
    
    public void setBackgroundColor(final String backgroundColor) throws DOMException {
        this.setProperty("background-color", backgroundColor, null);
    }
    
    public String getBackgroundImage() {
        return this.getPropertyValue("background-image");
    }
    
    public void setBackgroundImage(final String backgroundImage) throws DOMException {
        this.setProperty("background-image", backgroundImage, null);
    }
    
    public String getBackgroundPosition() {
        return this.getPropertyValue("background-position");
    }
    
    public void setBackgroundPosition(final String backgroundPosition) throws DOMException {
        this.setProperty("background-position", backgroundPosition, null);
    }
    
    public String getBackgroundRepeat() {
        return this.getPropertyValue("background-repeat");
    }
    
    public void setBackgroundRepeat(final String backgroundRepeat) throws DOMException {
        this.setProperty("background-repeat", backgroundRepeat, null);
    }
    
    public String getBorder() {
        return this.getPropertyValue("border");
    }
    
    public void setBorder(final String border) throws DOMException {
        this.setProperty("border", border, null);
    }
    
    public String getBorderCollapse() {
        return this.getPropertyValue("border-collapse");
    }
    
    public void setBorderCollapse(final String borderCollapse) throws DOMException {
        this.setProperty("border-collapse", borderCollapse, null);
    }
    
    public String getBorderColor() {
        return this.getPropertyValue("border-color");
    }
    
    public void setBorderColor(final String borderColor) throws DOMException {
        this.setProperty("border-color", borderColor, null);
    }
    
    public String getBorderSpacing() {
        return this.getPropertyValue("border-spacing");
    }
    
    public void setBorderSpacing(final String borderSpacing) throws DOMException {
        this.setProperty("border-spacing", borderSpacing, null);
    }
    
    public String getBorderStyle() {
        return this.getPropertyValue("border-style");
    }
    
    public void setBorderStyle(final String borderStyle) throws DOMException {
        this.setProperty("border-style", borderStyle, null);
    }
    
    public String getBorderTop() {
        return this.getPropertyValue("border-top");
    }
    
    public void setBorderTop(final String borderTop) throws DOMException {
        this.setProperty("border-top", borderTop, null);
    }
    
    public String getBorderRight() {
        return this.getPropertyValue("border-right");
    }
    
    public void setBorderRight(final String borderRight) throws DOMException {
        this.setProperty("border-right", borderRight, null);
    }
    
    public String getBorderBottom() {
        return this.getPropertyValue("border-bottom");
    }
    
    public void setBorderBottom(final String borderBottom) throws DOMException {
        this.setProperty("border-bottom", borderBottom, null);
    }
    
    public String getBorderLeft() {
        return this.getPropertyValue("border-left");
    }
    
    public void setBorderLeft(final String borderLeft) throws DOMException {
        this.setProperty("border-left", borderLeft, null);
    }
    
    public String getBorderTopColor() {
        return this.getPropertyValue("border-top-color");
    }
    
    public void setBorderTopColor(final String borderTopColor) throws DOMException {
        this.setProperty("border-top-color", borderTopColor, null);
    }
    
    public String getBorderRightColor() {
        return this.getPropertyValue("border-right-color");
    }
    
    public void setBorderRightColor(final String borderRightColor) throws DOMException {
        this.setProperty("border-right-color", borderRightColor, null);
    }
    
    public String getBorderBottomColor() {
        return this.getPropertyValue("border-bottom-color");
    }
    
    public void setBorderBottomColor(final String borderBottomColor) throws DOMException {
        this.setProperty("border-bottom-color", borderBottomColor, null);
    }
    
    public String getBorderLeftColor() {
        return this.getPropertyValue("border-left-color");
    }
    
    public void setBorderLeftColor(final String borderLeftColor) throws DOMException {
        this.setProperty("border-left-color", borderLeftColor, null);
    }
    
    public String getBorderTopStyle() {
        return this.getPropertyValue("border-top-style");
    }
    
    public void setBorderTopStyle(final String borderTopStyle) throws DOMException {
        this.setProperty("border-top-style", borderTopStyle, null);
    }
    
    public String getBorderRightStyle() {
        return this.getPropertyValue("border-right-style");
    }
    
    public void setBorderRightStyle(final String borderRightStyle) throws DOMException {
        this.setProperty("border-right-style", borderRightStyle, null);
    }
    
    public String getBorderBottomStyle() {
        return this.getPropertyValue("border-bottom-style");
    }
    
    public void setBorderBottomStyle(final String borderBottomStyle) throws DOMException {
        this.setProperty("border-bottom-style", borderBottomStyle, null);
    }
    
    public String getBorderLeftStyle() {
        return this.getPropertyValue("border-left-style");
    }
    
    public void setBorderLeftStyle(final String borderLeftStyle) throws DOMException {
        this.setProperty("border-left-style", borderLeftStyle, null);
    }
    
    public String getBorderTopWidth() {
        return this.getPropertyValue("border-top-width");
    }
    
    public void setBorderTopWidth(final String borderTopWidth) throws DOMException {
        this.setProperty("border-top-width", borderTopWidth, null);
    }
    
    public String getBorderRightWidth() {
        return this.getPropertyValue("border-right-width");
    }
    
    public void setBorderRightWidth(final String borderRightWidth) throws DOMException {
        this.setProperty("border-right-width", borderRightWidth, null);
    }
    
    public String getBorderBottomWidth() {
        return this.getPropertyValue("border-bottom-width");
    }
    
    public void setBorderBottomWidth(final String borderBottomWidth) throws DOMException {
        this.setProperty("border-bottom-width", borderBottomWidth, null);
    }
    
    public String getBorderLeftWidth() {
        return this.getPropertyValue("border-left-width");
    }
    
    public void setBorderLeftWidth(final String borderLeftWidth) throws DOMException {
        this.setProperty("border-left-width", borderLeftWidth, null);
    }
    
    public String getBorderWidth() {
        return this.getPropertyValue("border-width");
    }
    
    public void setBorderWidth(final String borderWidth) throws DOMException {
        this.setProperty("border-width", borderWidth, null);
    }
    
    public String getBottom() {
        return this.getPropertyValue("bottom");
    }
    
    public void setBottom(final String bottom) throws DOMException {
        this.setProperty("bottom", bottom, null);
    }
    
    public String getCaptionSide() {
        return this.getPropertyValue("caption-side");
    }
    
    public void setCaptionSide(final String captionSide) throws DOMException {
        this.setProperty("caption-side", captionSide, null);
    }
    
    public String getClear() {
        return this.getPropertyValue("clear");
    }
    
    public void setClear(final String clear) throws DOMException {
        this.setProperty("clear", clear, null);
    }
    
    public String getClip() {
        return this.getPropertyValue("clip");
    }
    
    public void setClip(final String clip) throws DOMException {
        this.setProperty("clip", clip, null);
    }
    
    public String getColor() {
        return this.getPropertyValue("color");
    }
    
    public void setColor(final String color) throws DOMException {
        this.setProperty("color", color, null);
    }
    
    public String getContent() {
        return this.getPropertyValue("content");
    }
    
    public void setContent(final String content) throws DOMException {
        this.setProperty("content", content, null);
    }
    
    public String getCounterIncrement() {
        return this.getPropertyValue("counter-increment");
    }
    
    public void setCounterIncrement(final String counterIncrement) throws DOMException {
        this.setProperty("counter-increment", counterIncrement, null);
    }
    
    public String getCounterReset() {
        return this.getPropertyValue("counter-reset");
    }
    
    public void setCounterReset(final String counterReset) throws DOMException {
        this.setProperty("counter-reset", counterReset, null);
    }
    
    public String getCue() {
        return this.getPropertyValue("cue");
    }
    
    public void setCue(final String cue) throws DOMException {
        this.setProperty("cue", cue, null);
    }
    
    public String getCueAfter() {
        return this.getPropertyValue("cue-after");
    }
    
    public void setCueAfter(final String cueAfter) throws DOMException {
        this.setProperty("cue-after", cueAfter, null);
    }
    
    public String getCueBefore() {
        return this.getPropertyValue("cue-before");
    }
    
    public void setCueBefore(final String cueBefore) throws DOMException {
        this.setProperty("cue-before", cueBefore, null);
    }
    
    public String getCursor() {
        return this.getPropertyValue("cursor");
    }
    
    public void setCursor(final String cursor) throws DOMException {
        this.setProperty("cursor", cursor, null);
    }
    
    public String getDirection() {
        return this.getPropertyValue("direction");
    }
    
    public void setDirection(final String direction) throws DOMException {
        this.setProperty("direction", direction, null);
    }
    
    public String getDisplay() {
        return this.getPropertyValue("display");
    }
    
    public void setDisplay(final String display) throws DOMException {
        this.setProperty("display", display, null);
    }
    
    public String getElevation() {
        return this.getPropertyValue("elevation");
    }
    
    public void setElevation(final String elevation) throws DOMException {
        this.setProperty("elevation", elevation, null);
    }
    
    public String getEmptyCells() {
        return this.getPropertyValue("empty-cells");
    }
    
    public void setEmptyCells(final String emptyCells) throws DOMException {
        this.setProperty("empty-cells", emptyCells, null);
    }
    
    public String getCssFloat() {
        return this.getPropertyValue("css-float");
    }
    
    public void setCssFloat(final String cssFloat) throws DOMException {
        this.setProperty("css-float", cssFloat, null);
    }
    
    public String getFont() {
        return this.getPropertyValue("font");
    }
    
    public void setFont(final String font) throws DOMException {
        this.setProperty("font", font, null);
    }
    
    public String getFontFamily() {
        return this.getPropertyValue("font-family");
    }
    
    public void setFontFamily(final String fontFamily) throws DOMException {
        this.setProperty("font-family", fontFamily, null);
    }
    
    public String getFontSize() {
        return this.getPropertyValue("font-size");
    }
    
    public void setFontSize(final String fontSize) throws DOMException {
        this.setProperty("font-size", fontSize, null);
    }
    
    public String getFontSizeAdjust() {
        return this.getPropertyValue("font-size-adjust");
    }
    
    public void setFontSizeAdjust(final String fontSizeAdjust) throws DOMException {
        this.setProperty("font-size-adjust", fontSizeAdjust, null);
    }
    
    public String getFontStretch() {
        return this.getPropertyValue("font-stretch");
    }
    
    public void setFontStretch(final String fontStretch) throws DOMException {
        this.setProperty("font-stretch", fontStretch, null);
    }
    
    public String getFontStyle() {
        return this.getPropertyValue("font-style");
    }
    
    public void setFontStyle(final String fontStyle) throws DOMException {
        this.setProperty("font-style", fontStyle, null);
    }
    
    public String getFontVariant() {
        return this.getPropertyValue("font-variant");
    }
    
    public void setFontVariant(final String fontVariant) throws DOMException {
        this.setProperty("font-variant", fontVariant, null);
    }
    
    public String getFontWeight() {
        return this.getPropertyValue("font-weight");
    }
    
    public void setFontWeight(final String fontWeight) throws DOMException {
        this.setProperty("font-weight", fontWeight, null);
    }
    
    public String getHeight() {
        return this.getPropertyValue("height");
    }
    
    public void setHeight(final String height) throws DOMException {
        this.setProperty("height", height, null);
    }
    
    public String getLeft() {
        return this.getPropertyValue("left");
    }
    
    public void setLeft(final String left) throws DOMException {
        this.setProperty("left", left, null);
    }
    
    public String getLetterSpacing() {
        return this.getPropertyValue("letter-spacing");
    }
    
    public void setLetterSpacing(final String letterSpacing) throws DOMException {
        this.setProperty("letter-spacing", letterSpacing, null);
    }
    
    public String getLineHeight() {
        return this.getPropertyValue("line-height");
    }
    
    public void setLineHeight(final String lineHeight) throws DOMException {
        this.setProperty("line-height", lineHeight, null);
    }
    
    public String getListStyle() {
        return this.getPropertyValue("list-style");
    }
    
    public void setListStyle(final String listStyle) throws DOMException {
        this.setProperty("list-style", listStyle, null);
    }
    
    public String getListStyleImage() {
        return this.getPropertyValue("list-style-image");
    }
    
    public void setListStyleImage(final String listStyleImage) throws DOMException {
        this.setProperty("list-style-image", listStyleImage, null);
    }
    
    public String getListStylePosition() {
        return this.getPropertyValue("list-style-position");
    }
    
    public void setListStylePosition(final String listStylePosition) throws DOMException {
        this.setProperty("list-style-position", listStylePosition, null);
    }
    
    public String getListStyleType() {
        return this.getPropertyValue("list-style-type");
    }
    
    public void setListStyleType(final String listStyleType) throws DOMException {
        this.setProperty("list-style-type", listStyleType, null);
    }
    
    public String getMargin() {
        return this.getPropertyValue("margin");
    }
    
    public void setMargin(final String margin) throws DOMException {
        this.setProperty("margin", margin, null);
    }
    
    public String getMarginTop() {
        return this.getPropertyValue("margin-top");
    }
    
    public void setMarginTop(final String marginTop) throws DOMException {
        this.setProperty("margin-top", marginTop, null);
    }
    
    public String getMarginRight() {
        return this.getPropertyValue("margin-right");
    }
    
    public void setMarginRight(final String marginRight) throws DOMException {
        this.setProperty("margin-right", marginRight, null);
    }
    
    public String getMarginBottom() {
        return this.getPropertyValue("margin-bottom");
    }
    
    public void setMarginBottom(final String marginBottom) throws DOMException {
        this.setProperty("margin-bottom", marginBottom, null);
    }
    
    public String getMarginLeft() {
        return this.getPropertyValue("margin-left");
    }
    
    public void setMarginLeft(final String marginLeft) throws DOMException {
        this.setProperty("margin-left", marginLeft, null);
    }
    
    public String getMarkerOffset() {
        return this.getPropertyValue("marker-offset");
    }
    
    public void setMarkerOffset(final String markerOffset) throws DOMException {
        this.setProperty("marker-offset", markerOffset, null);
    }
    
    public String getMarks() {
        return this.getPropertyValue("marks");
    }
    
    public void setMarks(final String marks) throws DOMException {
        this.setProperty("marks", marks, null);
    }
    
    public String getMaxHeight() {
        return this.getPropertyValue("max-height");
    }
    
    public void setMaxHeight(final String maxHeight) throws DOMException {
        this.setProperty("max-height", maxHeight, null);
    }
    
    public String getMaxWidth() {
        return this.getPropertyValue("max-width");
    }
    
    public void setMaxWidth(final String maxWidth) throws DOMException {
        this.setProperty("max-width", maxWidth, null);
    }
    
    public String getMinHeight() {
        return this.getPropertyValue("min-height");
    }
    
    public void setMinHeight(final String minHeight) throws DOMException {
        this.setProperty("min-height", minHeight, null);
    }
    
    public String getMinWidth() {
        return this.getPropertyValue("min-width");
    }
    
    public void setMinWidth(final String minWidth) throws DOMException {
        this.setProperty("min-width", minWidth, null);
    }
    
    public String getOrphans() {
        return this.getPropertyValue("orphans");
    }
    
    public void setOrphans(final String orphans) throws DOMException {
        this.setProperty("orphans", orphans, null);
    }
    
    public String getOutline() {
        return this.getPropertyValue("outline");
    }
    
    public void setOutline(final String outline) throws DOMException {
        this.setProperty("outline", outline, null);
    }
    
    public String getOutlineColor() {
        return this.getPropertyValue("outline-color");
    }
    
    public void setOutlineColor(final String outlineColor) throws DOMException {
        this.setProperty("outline-color", outlineColor, null);
    }
    
    public String getOutlineStyle() {
        return this.getPropertyValue("outline-style");
    }
    
    public void setOutlineStyle(final String outlineStyle) throws DOMException {
        this.setProperty("outline-style", outlineStyle, null);
    }
    
    public String getOutlineWidth() {
        return this.getPropertyValue("outline-width");
    }
    
    public void setOutlineWidth(final String outlineWidth) throws DOMException {
        this.setProperty("outline-width", outlineWidth, null);
    }
    
    public String getOverflow() {
        return this.getPropertyValue("overflow");
    }
    
    public void setOverflow(final String overflow) throws DOMException {
        this.setProperty("overflow", overflow, null);
    }
    
    public String getPadding() {
        return this.getPropertyValue("padding");
    }
    
    public void setPadding(final String padding) throws DOMException {
        this.setProperty("padding", padding, null);
    }
    
    public String getPaddingTop() {
        return this.getPropertyValue("padding-top");
    }
    
    public void setPaddingTop(final String paddingTop) throws DOMException {
        this.setProperty("padding-top", paddingTop, null);
    }
    
    public String getPaddingRight() {
        return this.getPropertyValue("padding-right");
    }
    
    public void setPaddingRight(final String paddingRight) throws DOMException {
        this.setProperty("padding-right", paddingRight, null);
    }
    
    public String getPaddingBottom() {
        return this.getPropertyValue("padding-bottom");
    }
    
    public void setPaddingBottom(final String paddingBottom) throws DOMException {
        this.setProperty("padding-bottom", paddingBottom, null);
    }
    
    public String getPaddingLeft() {
        return this.getPropertyValue("padding-left");
    }
    
    public void setPaddingLeft(final String paddingLeft) throws DOMException {
        this.setProperty("padding-left", paddingLeft, null);
    }
    
    public String getPage() {
        return this.getPropertyValue("page");
    }
    
    public void setPage(final String page) throws DOMException {
        this.setProperty("page", page, null);
    }
    
    public String getPageBreakAfter() {
        return this.getPropertyValue("page-break-after");
    }
    
    public void setPageBreakAfter(final String pageBreakAfter) throws DOMException {
        this.setProperty("page-break-after", pageBreakAfter, null);
    }
    
    public String getPageBreakBefore() {
        return this.getPropertyValue("page-break-before");
    }
    
    public void setPageBreakBefore(final String pageBreakBefore) throws DOMException {
        this.setProperty("page-break-before", "page-break-before", null);
    }
    
    public String getPageBreakInside() {
        return this.getPropertyValue("page-break-inside");
    }
    
    public void setPageBreakInside(final String pageBreakInside) throws DOMException {
        this.setProperty("page-break-inside", pageBreakInside, null);
    }
    
    public String getPause() {
        return this.getPropertyValue("pause");
    }
    
    public void setPause(final String pause) throws DOMException {
        this.setProperty("pause", pause, null);
    }
    
    public String getPauseAfter() {
        return this.getPropertyValue("pause-after");
    }
    
    public void setPauseAfter(final String pauseAfter) throws DOMException {
        this.setProperty("pause-after", pauseAfter, null);
    }
    
    public String getPauseBefore() {
        return this.getPropertyValue("pause-before");
    }
    
    public void setPauseBefore(final String pauseBefore) throws DOMException {
        this.setProperty("pause-before", "pause-before", null);
    }
    
    public String getPitch() {
        return this.getPropertyValue("pitch");
    }
    
    public void setPitch(final String pitch) throws DOMException {
        this.setProperty("pitch", pitch, null);
    }
    
    public String getPitchRange() {
        return this.getPropertyValue("pitch-range");
    }
    
    public void setPitchRange(final String pitchRange) throws DOMException {
        this.setProperty("pitch-range", pitchRange, null);
    }
    
    public String getPlayDuring() {
        return this.getPropertyValue("play-during");
    }
    
    public void setPlayDuring(final String playDuring) throws DOMException {
        this.setProperty("play-during", playDuring, null);
    }
    
    public String getPosition() {
        return this.getPropertyValue("position");
    }
    
    public void setPosition(final String position) throws DOMException {
        this.setProperty("position", position, null);
    }
    
    public String getQuotes() {
        return this.getPropertyValue("quotes");
    }
    
    public void setQuotes(final String quotes) throws DOMException {
        this.setProperty("quotes", quotes, null);
    }
    
    public String getRichness() {
        return this.getPropertyValue("richness");
    }
    
    public void setRichness(final String richness) throws DOMException {
        this.setProperty("richness", richness, null);
    }
    
    public String getRight() {
        return this.getPropertyValue("right");
    }
    
    public void setRight(final String right) throws DOMException {
        this.setProperty("right", right, null);
    }
    
    public String getSize() {
        return this.getPropertyValue("size");
    }
    
    public void setSize(final String size) throws DOMException {
        this.setProperty("size", size, null);
    }
    
    public String getSpeak() {
        return this.getPropertyValue("speak");
    }
    
    public void setSpeak(final String speak) throws DOMException {
        this.setProperty("speak", speak, null);
    }
    
    public String getSpeakHeader() {
        return this.getPropertyValue("speak-header");
    }
    
    public void setSpeakHeader(final String speakHeader) throws DOMException {
        this.setProperty("speak-header", speakHeader, null);
    }
    
    public String getSpeakNumeral() {
        return this.getPropertyValue("speak-numeral");
    }
    
    public void setSpeakNumeral(final String speakNumeral) throws DOMException {
        this.setProperty("speak-numeral", speakNumeral, null);
    }
    
    public String getSpeakPunctuation() {
        return this.getPropertyValue("speak-puctuation");
    }
    
    public void setSpeakPunctuation(final String speakPunctuation) throws DOMException {
        this.setProperty("speak-puctuation", speakPunctuation, null);
    }
    
    public String getSpeechRate() {
        return this.getPropertyValue("speech-rate");
    }
    
    public void setSpeechRate(final String speechRate) throws DOMException {
        this.setProperty("speech-rate", speechRate, null);
    }
    
    public String getStress() {
        return this.getPropertyValue("stress");
    }
    
    public void setStress(final String stress) throws DOMException {
        this.setProperty("stress", stress, null);
    }
    
    public String getTableLayout() {
        return this.getPropertyValue("table-layout");
    }
    
    public void setTableLayout(final String tableLayout) throws DOMException {
        this.setProperty("table-layout", tableLayout, null);
    }
    
    public String getTextAlign() {
        return this.getPropertyValue("text-align");
    }
    
    public void setTextAlign(final String textAlign) throws DOMException {
        this.setProperty("text-align", textAlign, null);
    }
    
    public String getTextDecoration() {
        return this.getPropertyValue("text-decoration");
    }
    
    public void setTextDecoration(final String textDecoration) throws DOMException {
        this.setProperty("text-decoration", textDecoration, null);
    }
    
    public String getTextIndent() {
        return this.getPropertyValue("text-indent");
    }
    
    public void setTextIndent(final String textIndent) throws DOMException {
        this.setProperty("text-indent", textIndent, null);
    }
    
    public String getTextShadow() {
        return this.getPropertyValue("text-shadow");
    }
    
    public void setTextShadow(final String textShadow) throws DOMException {
        this.setProperty("text-shadow", textShadow, null);
    }
    
    public String getTextTransform() {
        return this.getPropertyValue("text-transform");
    }
    
    public void setTextTransform(final String textTransform) throws DOMException {
        this.setProperty("text-transform", textTransform, null);
    }
    
    public String getTop() {
        return this.getPropertyValue("top");
    }
    
    public void setTop(final String top) throws DOMException {
        this.setProperty("top", top, null);
    }
    
    public String getUnicodeBidi() {
        return this.getPropertyValue("unicode-bidi");
    }
    
    public void setUnicodeBidi(final String unicodeBidi) throws DOMException {
        this.setProperty("unicode-bidi", unicodeBidi, null);
    }
    
    public String getVerticalAlign() {
        return this.getPropertyValue("vertical-align");
    }
    
    public void setVerticalAlign(final String verticalAlign) throws DOMException {
        this.setProperty("vertical-align", verticalAlign, null);
    }
    
    public String getVisibility() {
        return this.getPropertyValue("visibility");
    }
    
    public void setVisibility(final String visibility) throws DOMException {
        this.setProperty("visibility", visibility, null);
    }
    
    public String getVoiceFamily() {
        return this.getPropertyValue("voice-family");
    }
    
    public void setVoiceFamily(final String voiceFamily) throws DOMException {
        this.setProperty("voice-family", voiceFamily, null);
    }
    
    public String getVolume() {
        return this.getPropertyValue("volume");
    }
    
    public void setVolume(final String volume) throws DOMException {
        this.setProperty("volume", volume, null);
    }
    
    public String getWhiteSpace() {
        return this.getPropertyValue("white-space");
    }
    
    public void setWhiteSpace(final String whiteSpace) throws DOMException {
        this.setProperty("white-space", whiteSpace, null);
    }
    
    public String getWidows() {
        return this.getPropertyValue("widows");
    }
    
    public void setWidows(final String widows) throws DOMException {
        this.setProperty("widows", widows, null);
    }
    
    public String getWidth() {
        return this.getPropertyValue("width");
    }
    
    public void setWidth(final String width) throws DOMException {
        this.setProperty("width", width, null);
    }
    
    public String getWordSpacing() {
        return this.getPropertyValue("word_spacing");
    }
    
    public void setWordSpacing(final String wordSpacing) throws DOMException {
        this.setProperty("word_spacing", wordSpacing, null);
    }
    
    public String getZIndex() {
        return this.getPropertyValue("z-index");
    }
    
    public void setZIndex(final String zIndex) throws DOMException {
        this.setProperty("z-index", zIndex, null);
    }
}

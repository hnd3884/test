package com.steadystate.css.parser;

import org.w3c.css.sac.Selector;
import java.net.URL;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import java.io.IOException;
import org.w3c.dom.DOMException;
import java.text.MessageFormat;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.Locator;
import java.util.MissingResourceException;
import com.steadystate.css.sac.ConditionFactoryAdapter;
import org.w3c.css.sac.ConditionFactory;
import com.steadystate.css.parser.selectors.ConditionFactoryImpl;
import com.steadystate.css.sac.SelectorFactoryAdapter;
import org.w3c.css.sac.SelectorFactory;
import com.steadystate.css.parser.selectors.SelectorFactoryImpl;
import com.steadystate.css.sac.DocumentHandlerAdapter;
import org.w3c.css.sac.DocumentHandler;
import java.util.ResourceBundle;
import com.steadystate.css.sac.ConditionFactoryExt;
import com.steadystate.css.sac.SelectorFactoryExt;
import java.util.Locale;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.ErrorHandler;
import com.steadystate.css.sac.DocumentHandlerExt;

abstract class AbstractSACParser implements SACParser
{
    private DocumentHandlerExt documentHandler_;
    private ErrorHandler errorHandler_;
    private InputSource source_;
    private Locale locale_;
    private SelectorFactoryExt selectorFactory_;
    private ConditionFactoryExt conditionFactory_;
    private ResourceBundle sacParserMessages_;
    private boolean ieStarHackAccepted_;
    private static final String NUM_CHARS = "0123456789.";
    
    protected DocumentHandlerExt getDocumentHandler() {
        if (this.documentHandler_ == null) {
            this.setDocumentHandler((DocumentHandler)new HandlerBase());
        }
        return this.documentHandler_;
    }
    
    public void setDocumentHandler(final DocumentHandler handler) {
        if (handler instanceof DocumentHandlerExt) {
            this.documentHandler_ = (DocumentHandlerExt)handler;
        }
        else {
            this.documentHandler_ = new DocumentHandlerAdapter(handler);
        }
    }
    
    protected ErrorHandler getErrorHandler() {
        if (this.errorHandler_ == null) {
            this.setErrorHandler((ErrorHandler)new HandlerBase());
        }
        return this.errorHandler_;
    }
    
    public void setErrorHandler(final ErrorHandler eh) {
        this.errorHandler_ = eh;
    }
    
    protected InputSource getInputSource() {
        return this.source_;
    }
    
    public void setIeStarHackAccepted(final boolean accepted) {
        this.ieStarHackAccepted_ = accepted;
    }
    
    public boolean isIeStarHackAccepted() {
        return this.ieStarHackAccepted_;
    }
    
    public void setLocale(final Locale locale) {
        if (this.locale_ != locale) {
            this.sacParserMessages_ = null;
        }
        this.locale_ = locale;
    }
    
    protected Locale getLocale() {
        if (this.locale_ == null) {
            this.setLocale(Locale.getDefault());
        }
        return this.locale_;
    }
    
    protected SelectorFactoryExt getSelectorFactory() {
        if (this.selectorFactory_ == null) {
            this.selectorFactory_ = new SelectorFactoryImpl();
        }
        return this.selectorFactory_;
    }
    
    public void setSelectorFactory(final SelectorFactory selectorFactory) {
        if (selectorFactory instanceof SelectorFactoryExt) {
            this.selectorFactory_ = (SelectorFactoryExt)selectorFactory;
        }
        else {
            this.selectorFactory_ = new SelectorFactoryAdapter(selectorFactory);
        }
    }
    
    protected ConditionFactoryExt getConditionFactory() {
        if (this.conditionFactory_ == null) {
            this.conditionFactory_ = new ConditionFactoryImpl();
        }
        return this.conditionFactory_;
    }
    
    public void setConditionFactory(final ConditionFactory conditionFactory) {
        if (conditionFactory instanceof ConditionFactoryExt) {
            this.conditionFactory_ = (ConditionFactoryExt)conditionFactory;
        }
        else {
            this.conditionFactory_ = new ConditionFactoryAdapter(conditionFactory);
        }
    }
    
    protected ResourceBundle getSACParserMessages() {
        if (this.sacParserMessages_ == null) {
            try {
                this.sacParserMessages_ = ResourceBundle.getBundle("com.steadystate.css.parser.SACParserMessages", this.getLocale());
            }
            catch (final MissingResourceException e) {
                e.printStackTrace();
            }
        }
        return this.sacParserMessages_;
    }
    
    protected Locator createLocator(final Token t) {
        return (Locator)new LocatorImpl(this.getInputSource().getURI(), (t == null) ? 0 : t.beginLine, (t == null) ? 0 : t.beginColumn);
    }
    
    protected String add_escapes(final String str) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            final char ch = str.charAt(i);
            switch (ch) {
                case '\0': {
                    break;
                }
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                case '\"': {
                    sb.append("\\\"");
                    break;
                }
                case '\'': {
                    sb.append("\\'");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                default: {
                    if (ch < ' ' || ch > '~') {
                        final String s = "0000" + Integer.toString(ch, 16);
                        sb.append("\\u" + s.substring(s.length() - 4, s.length()));
                        break;
                    }
                    sb.append(ch);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    protected CSSParseException toCSSParseException(final String key, final ParseException e) {
        final String messagePattern1 = this.getSACParserMessages().getString("invalidExpectingOne");
        final String messagePattern2 = this.getSACParserMessages().getString("invalidExpectingMore");
        int maxSize = 0;
        final StringBuilder expected = new StringBuilder();
        for (int i = 0; i < e.expectedTokenSequences.length; ++i) {
            if (maxSize < e.expectedTokenSequences[i].length) {
                maxSize = e.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < e.expectedTokenSequences[i].length; ++j) {
                expected.append(e.tokenImage[e.expectedTokenSequences[i][j]]);
            }
            if (i < e.expectedTokenSequences.length - 1) {
                expected.append(", ");
            }
        }
        final StringBuilder invalid = new StringBuilder();
        Token tok = e.currentToken.next;
        for (int k = 0; k < maxSize; ++k) {
            if (k != 0) {
                invalid.append(" ");
            }
            if (tok.kind == 0) {
                invalid.append(e.tokenImage[0]);
                break;
            }
            invalid.append(this.add_escapes(tok.image));
            tok = tok.next;
        }
        String s = null;
        try {
            s = this.getSACParserMessages().getString(key);
        }
        catch (final MissingResourceException ex) {
            s = key;
        }
        final StringBuilder message = new StringBuilder(s);
        message.append(" (");
        if (e.expectedTokenSequences.length == 1) {
            message.append(MessageFormat.format(messagePattern1, invalid, expected));
        }
        else {
            message.append(MessageFormat.format(messagePattern2, invalid, expected));
        }
        message.append(")");
        return new CSSParseException(message.toString(), this.getInputSource().getURI(), e.currentToken.next.beginLine, e.currentToken.next.beginColumn);
    }
    
    protected CSSParseException toCSSParseException(final DOMException e) {
        final String messagePattern = this.getSACParserMessages().getString("domException");
        return new CSSParseException(MessageFormat.format(messagePattern, e.getMessage()), this.getInputSource().getURI(), 1, 1);
    }
    
    protected CSSParseException toCSSParseException(final TokenMgrError e) {
        final String messagePattern = this.getSACParserMessages().getString("tokenMgrError");
        return new CSSParseException(messagePattern, this.getInputSource().getURI(), 1, 1);
    }
    
    protected CSSParseException toCSSParseException(final String messageKey, final Object[] msgParams, final Locator locator) {
        final String messagePattern = this.getSACParserMessages().getString(messageKey);
        return new CSSParseException(MessageFormat.format(messagePattern, msgParams), locator);
    }
    
    protected CSSParseException createSkipWarning(final String key, final CSSParseException e) {
        String s = null;
        try {
            s = this.getSACParserMessages().getString(key);
        }
        catch (final MissingResourceException ex) {
            s = key;
        }
        return new CSSParseException(s, e.getURI(), e.getLineNumber(), e.getColumnNumber());
    }
    
    public void parseStyleSheet(final InputSource source) throws IOException {
        this.source_ = source;
        this.ReInit(this.getCharStream(source));
        try {
            this.styleSheet();
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidStyleSheet", e));
        }
        catch (final TokenMgrError e2) {
            this.getErrorHandler().error(this.toCSSParseException(e2));
        }
        catch (final CSSParseException e3) {
            this.getErrorHandler().error(e3);
        }
    }
    
    public void parseStyleSheet(final String uri) throws IOException {
        this.parseStyleSheet(new InputSource(uri));
    }
    
    public void parseStyleDeclaration(final InputSource source) throws IOException {
        this.source_ = source;
        this.ReInit(this.getCharStream(source));
        try {
            this.styleDeclaration();
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidStyleDeclaration", e));
        }
        catch (final TokenMgrError e2) {
            this.getErrorHandler().error(this.toCSSParseException(e2));
        }
        catch (final CSSParseException e3) {
            this.getErrorHandler().error(e3);
        }
    }
    
    public void parseRule(final InputSource source) throws IOException {
        this.source_ = source;
        this.ReInit(this.getCharStream(source));
        try {
            this.styleSheetRuleSingle();
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidRule", e));
        }
        catch (final TokenMgrError e2) {
            this.getErrorHandler().error(this.toCSSParseException(e2));
        }
        catch (final CSSParseException e3) {
            this.getErrorHandler().error(e3);
        }
    }
    
    public SelectorList parseSelectors(final InputSource source) throws IOException {
        this.source_ = source;
        this.ReInit(this.getCharStream(source));
        SelectorList sl = null;
        try {
            sl = this.parseSelectorsInternal();
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidSelectorList", e));
        }
        catch (final TokenMgrError e2) {
            this.getErrorHandler().error(this.toCSSParseException(e2));
        }
        catch (final CSSParseException e3) {
            this.getErrorHandler().error(e3);
        }
        return sl;
    }
    
    public LexicalUnit parsePropertyValue(final InputSource source) throws IOException {
        this.source_ = source;
        this.ReInit(this.getCharStream(source));
        LexicalUnit lu = null;
        try {
            lu = this.expr();
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidExpr", e));
        }
        catch (final TokenMgrError e2) {
            this.getErrorHandler().error(this.toCSSParseException(e2));
        }
        catch (final CSSParseException e3) {
            this.getErrorHandler().error(e3);
        }
        return lu;
    }
    
    public boolean parsePriority(final InputSource source) throws IOException {
        this.source_ = source;
        this.ReInit(this.getCharStream(source));
        boolean b = false;
        try {
            b = this.prio();
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidPrio", e));
        }
        catch (final TokenMgrError e2) {
            this.getErrorHandler().error(this.toCSSParseException(e2));
        }
        catch (final CSSParseException e3) {
            this.getErrorHandler().error(e3);
        }
        return b;
    }
    
    public SACMediaList parseMedia(final InputSource source) throws IOException {
        this.source_ = source;
        this.ReInit(this.getCharStream(source));
        final SACMediaListImpl ml = new SACMediaListImpl();
        try {
            this.mediaList(ml);
        }
        catch (final ParseException e) {
            this.getErrorHandler().error(this.toCSSParseException("invalidMediaList", e));
        }
        catch (final TokenMgrError e2) {
            this.getErrorHandler().error(this.toCSSParseException(e2));
        }
        catch (final CSSParseException e3) {
            this.getErrorHandler().error(e3);
        }
        return (SACMediaList)ml;
    }
    
    private CharStream getCharStream(final InputSource source) throws IOException {
        if (source.getCharacterStream() != null) {
            return new CssCharStream(source.getCharacterStream(), 1, 1);
        }
        if (source.getByteStream() != null) {
            final String encoding = source.getEncoding();
            InputStreamReader reader;
            if (encoding == null || encoding.length() < 1) {
                reader = new InputStreamReader(source.getByteStream(), Charset.defaultCharset());
            }
            else {
                reader = new InputStreamReader(source.getByteStream(), encoding);
            }
            return new CssCharStream(reader, 1, 1);
        }
        if (source.getURI() != null) {
            final InputStreamReader reader = new InputStreamReader(new URL(source.getURI()).openStream());
            return new CssCharStream(reader, 1, 1);
        }
        return null;
    }
    
    public abstract String getParserVersion();
    
    protected abstract String getGrammarUri();
    
    protected abstract void ReInit(final CharStream p0);
    
    protected abstract void styleSheet() throws CSSParseException, ParseException;
    
    protected abstract void styleDeclaration() throws ParseException;
    
    protected abstract void styleSheetRuleSingle() throws ParseException;
    
    protected abstract SelectorList parseSelectorsInternal() throws ParseException;
    
    protected abstract SelectorList selectorList() throws ParseException;
    
    protected abstract LexicalUnit expr() throws ParseException;
    
    protected abstract boolean prio() throws ParseException;
    
    protected abstract void mediaList(final SACMediaListImpl p0) throws ParseException;
    
    protected void handleStartDocument() {
        this.getDocumentHandler().startDocument(this.getInputSource());
    }
    
    protected void handleEndDocument() {
        this.getDocumentHandler().endDocument(this.getInputSource());
    }
    
    protected void handleIgnorableAtRule(final String s, final Locator locator) {
        this.getDocumentHandler().ignorableAtRule(s, locator);
    }
    
    protected void handleCharset(final String characterEncoding, final Locator locator) {
        this.getDocumentHandler().charset(characterEncoding, locator);
    }
    
    protected void handleImportStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI, final Locator locator) {
        this.getDocumentHandler().importStyle(uri, media, defaultNamespaceURI, locator);
    }
    
    protected void handleStartMedia(final SACMediaList media, final Locator locator) {
        this.getDocumentHandler().startMedia(media, locator);
    }
    
    protected void handleMedium(final String medium, final Locator locator) {
    }
    
    protected void handleEndMedia(final SACMediaList media) {
        this.getDocumentHandler().endMedia(media);
    }
    
    protected void handleStartPage(final String name, final String pseudoPage, final Locator locator) {
        this.getDocumentHandler().startPage(name, pseudoPage, locator);
    }
    
    protected void handleEndPage(final String name, final String pseudoPage) {
        this.getDocumentHandler().endPage(name, pseudoPage);
    }
    
    protected void handleStartFontFace(final Locator locator) {
        this.getDocumentHandler().startFontFace(locator);
    }
    
    protected void handleEndFontFace() {
        this.getDocumentHandler().endFontFace();
    }
    
    protected void handleSelector(final Selector selector) {
    }
    
    protected void handleStartSelector(final SelectorList selectors, final Locator locator) {
        this.getDocumentHandler().startSelector(selectors, locator);
    }
    
    protected void handleEndSelector(final SelectorList selectors) {
        this.getDocumentHandler().endSelector(selectors);
    }
    
    protected void handleProperty(final String name, final LexicalUnit value, final boolean important, final Locator locator) {
        this.getDocumentHandler().property(name, value, important, locator);
    }
    
    protected LexicalUnit functionInternal(final LexicalUnit prev, final String funct, final LexicalUnit params) {
        if ("counter(".equalsIgnoreCase(funct)) {
            return LexicalUnitImpl.createCounter(prev, params);
        }
        if ("counters(".equalsIgnoreCase(funct)) {
            return LexicalUnitImpl.createCounters(prev, params);
        }
        if ("attr(".equalsIgnoreCase(funct)) {
            return LexicalUnitImpl.createAttr(prev, params.getStringValue());
        }
        if ("rect(".equalsIgnoreCase(funct)) {
            return LexicalUnitImpl.createRect(prev, params);
        }
        if ("rgb(".equalsIgnoreCase(funct)) {
            return LexicalUnitImpl.createRgbColor(prev, params);
        }
        return LexicalUnitImpl.createFunction(prev, funct.substring(0, funct.length() - 1), params);
    }
    
    protected LexicalUnit hexcolorInternal(final LexicalUnit prev, final Token t) {
        final int i = 1;
        int r = 0;
        int g = 0;
        int b = 0;
        final int len = t.image.length() - 1;
        try {
            if (len == 3) {
                r = Integer.parseInt(t.image.substring(1, 2), 16);
                g = Integer.parseInt(t.image.substring(2, 3), 16);
                b = Integer.parseInt(t.image.substring(3, 4), 16);
                r |= r << 4;
                g |= g << 4;
                b |= b << 4;
            }
            else {
                if (len != 6) {
                    final String pattern = this.getSACParserMessages().getString("invalidColor");
                    throw new CSSParseException(MessageFormat.format(pattern, t), this.getInputSource().getURI(), t.beginLine, t.beginColumn);
                }
                r = Integer.parseInt(t.image.substring(1, 3), 16);
                g = Integer.parseInt(t.image.substring(3, 5), 16);
                b = Integer.parseInt(t.image.substring(5, 7), 16);
            }
            final LexicalUnit lr = LexicalUnitImpl.createNumber(null, r);
            final LexicalUnit lc1 = LexicalUnitImpl.createComma(lr);
            final LexicalUnit lg = LexicalUnitImpl.createNumber(lc1, g);
            final LexicalUnit lc2 = LexicalUnitImpl.createComma(lg);
            LexicalUnitImpl.createNumber(lc2, b);
            return LexicalUnitImpl.createRgbColor(prev, lr);
        }
        catch (final NumberFormatException ex) {
            final String pattern2 = this.getSACParserMessages().getString("invalidColor");
            throw new CSSParseException(MessageFormat.format(pattern2, t), this.getInputSource().getURI(), t.beginLine, t.beginColumn, (Exception)ex);
        }
    }
    
    int intValue(final char op, final String s) {
        final int result = Integer.parseInt(s);
        if (op == '-') {
            return -1 * result;
        }
        return result;
    }
    
    float floatValue(final char op, final String s) {
        final float result = Float.parseFloat(s);
        if (op == '-') {
            return -1.0f * result;
        }
        return result;
    }
    
    int getLastNumPos(final String s) {
        int i;
        for (i = 0; i < s.length() && "0123456789.".indexOf(s.charAt(i)) >= 0; ++i) {}
        return i - 1;
    }
    
    public String unescape(final String s, final boolean unescapeDoubleQuotes) {
        if (s == null) {
            return s;
        }
        StringBuilder buf = null;
        int index = -1;
        int len = s.length();
        --len;
        if (unescapeDoubleQuotes) {
            while (index < len) {
                final char c = s.charAt(++index);
                if (c == '\\' || c == '\"') {
                    buf = new StringBuilder(len);
                    buf.append(s.substring(0, index));
                    --index;
                    break;
                }
            }
        }
        else {
            while (index < len) {
                if ('\\' == s.charAt(++index)) {
                    buf = new StringBuilder(len);
                    buf.append(s.substring(0, index));
                    --index;
                    break;
                }
            }
        }
        if (null == buf) {
            return s;
        }
        int numValue = -1;
        int digitCount = 0;
        while (index < len) {
            final char c2 = s.charAt(++index);
            if (numValue > -1) {
                final int hexval = hexval(c2);
                if (hexval != -1) {
                    numValue = numValue * 16 + hexval;
                    if (++digitCount < 6) {
                        continue;
                    }
                    if (numValue > 65535 || numValue == 0) {
                        numValue = 65533;
                    }
                    buf.append((char)numValue);
                    numValue = -1;
                    continue;
                }
                else {
                    if (digitCount > 0) {
                        if (numValue > 65535 || numValue == 0) {
                            numValue = 65533;
                        }
                        buf.append((char)numValue);
                        if (c2 == ' ' || c2 == '\t') {
                            numValue = -1;
                            continue;
                        }
                    }
                    numValue = -1;
                    if (digitCount == 0 && c2 == '\\') {
                        buf.append('\\');
                        continue;
                    }
                    if (c2 == '\n') {
                        continue;
                    }
                    if (c2 == '\f') {
                        continue;
                    }
                    if (c2 == '\r') {
                        if (index < len && s.charAt(index + 1) == '\n') {
                            ++index;
                            continue;
                        }
                        continue;
                    }
                }
            }
            if (c2 == '\\') {
                numValue = 0;
                digitCount = 0;
            }
            else {
                if (c2 == '\"' && !unescapeDoubleQuotes) {
                    buf.append('\\');
                }
                buf.append(c2);
            }
        }
        if (numValue > -1) {
            if (digitCount == 0) {
                buf.append('\\');
            }
            else {
                if (numValue > 65535 || numValue == 0) {
                    numValue = 65533;
                }
                buf.append((char)numValue);
            }
        }
        return buf.toString();
    }
    
    private static int hexval(final char c) {
        switch (c) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            case '2': {
                return 2;
            }
            case '3': {
                return 3;
            }
            case '4': {
                return 4;
            }
            case '5': {
                return 5;
            }
            case '6': {
                return 6;
            }
            case '7': {
                return 7;
            }
            case '8': {
                return 8;
            }
            case '9': {
                return 9;
            }
            case 'A':
            case 'a': {
                return 10;
            }
            case 'B':
            case 'b': {
                return 11;
            }
            case 'C':
            case 'c': {
                return 12;
            }
            case 'D':
            case 'd': {
                return 13;
            }
            case 'E':
            case 'e': {
                return 14;
            }
            case 'F':
            case 'f': {
                return 15;
            }
            default: {
                return -1;
            }
        }
    }
}

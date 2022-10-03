package javax.swing.plaf.synth;

import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import javax.swing.ImageIcon;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import javax.swing.UIDefaults;
import java.util.Locale;
import java.util.regex.PatternSyntaxException;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.DimensionUIResource;
import sun.reflect.misc.ReflectUtil;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import sun.swing.plaf.synth.DefaultSynthStyle;
import org.xml.sax.Attributes;
import java.awt.Insets;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.text.ParseException;
import java.io.BufferedInputStream;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URL;
import java.util.Map;
import java.util.List;
import com.sun.beans.decoder.DocumentHandler;
import org.xml.sax.helpers.DefaultHandler;

class SynthParser extends DefaultHandler
{
    private static final String ELEMENT_SYNTH = "synth";
    private static final String ELEMENT_STYLE = "style";
    private static final String ELEMENT_STATE = "state";
    private static final String ELEMENT_FONT = "font";
    private static final String ELEMENT_COLOR = "color";
    private static final String ELEMENT_IMAGE_PAINTER = "imagePainter";
    private static final String ELEMENT_PAINTER = "painter";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ELEMENT_SYNTH_GRAPHICS = "graphicsUtils";
    private static final String ELEMENT_IMAGE_ICON = "imageIcon";
    private static final String ELEMENT_BIND = "bind";
    private static final String ELEMENT_BIND_KEY = "bindKey";
    private static final String ELEMENT_INSETS = "insets";
    private static final String ELEMENT_OPAQUE = "opaque";
    private static final String ELEMENT_DEFAULTS_PROPERTY = "defaultsProperty";
    private static final String ELEMENT_INPUT_MAP = "inputMap";
    private static final String ATTRIBUTE_ACTION = "action";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_IDREF = "idref";
    private static final String ATTRIBUTE_CLONE = "clone";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_STYLE = "style";
    private static final String ATTRIBUTE_SIZE = "size";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_TOP = "top";
    private static final String ATTRIBUTE_LEFT = "left";
    private static final String ATTRIBUTE_BOTTOM = "bottom";
    private static final String ATTRIBUTE_RIGHT = "right";
    private static final String ATTRIBUTE_KEY = "key";
    private static final String ATTRIBUTE_SOURCE_INSETS = "sourceInsets";
    private static final String ATTRIBUTE_DEST_INSETS = "destinationInsets";
    private static final String ATTRIBUTE_PATH = "path";
    private static final String ATTRIBUTE_STRETCH = "stretch";
    private static final String ATTRIBUTE_PAINT_CENTER = "paintCenter";
    private static final String ATTRIBUTE_METHOD = "method";
    private static final String ATTRIBUTE_DIRECTION = "direction";
    private static final String ATTRIBUTE_CENTER = "center";
    private DocumentHandler _handler;
    private int _depth;
    private DefaultSynthStyleFactory _factory;
    private List<ParsedSynthStyle.StateInfo> _stateInfos;
    private ParsedSynthStyle _style;
    private ParsedSynthStyle.StateInfo _stateInfo;
    private List<String> _inputMapBindings;
    private String _inputMapID;
    private Map<String, Object> _mapping;
    private URL _urlResourceBase;
    private Class<?> _classResourceBase;
    private List<ColorType> _colorTypes;
    private Map<String, Object> _defaultsMap;
    private List<ParsedSynthStyle.PainterInfo> _stylePainters;
    private List<ParsedSynthStyle.PainterInfo> _statePainters;
    
    SynthParser() {
        this._mapping = new HashMap<String, Object>();
        this._stateInfos = new ArrayList<ParsedSynthStyle.StateInfo>();
        this._colorTypes = new ArrayList<ColorType>();
        this._inputMapBindings = new ArrayList<String>();
        this._stylePainters = new ArrayList<ParsedSynthStyle.PainterInfo>();
        this._statePainters = new ArrayList<ParsedSynthStyle.PainterInfo>();
    }
    
    public void parse(final InputStream inputStream, final DefaultSynthStyleFactory factory, final URL urlResourceBase, final Class<?> classResourceBase, final Map<String, Object> defaultsMap) throws ParseException, IllegalArgumentException {
        if (inputStream == null || factory == null || (urlResourceBase == null && classResourceBase == null)) {
            throw new IllegalArgumentException("You must supply an InputStream, StyleFactory and Class or URL");
        }
        assert classResourceBase == null;
        this._factory = factory;
        this._classResourceBase = classResourceBase;
        this._urlResourceBase = urlResourceBase;
        this._defaultsMap = defaultsMap;
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(new BufferedInputStream(inputStream), this);
        }
        catch (final ParserConfigurationException ex) {
            throw new ParseException("Error parsing: " + ex, 0);
        }
        catch (final SAXException ex2) {
            throw new ParseException("Error parsing: " + ex2 + " " + ex2.getException(), 0);
        }
        catch (final IOException ex3) {
            throw new ParseException("Error parsing: " + ex3, 0);
        }
        finally {
            this.reset();
        }
    }
    
    private URL getResource(final String s) {
        if (this._classResourceBase != null) {
            return this._classResourceBase.getResource(s);
        }
        try {
            return new URL(this._urlResourceBase, s);
        }
        catch (final MalformedURLException ex) {
            return null;
        }
    }
    
    private void reset() {
        this._handler = null;
        this._depth = 0;
        this._mapping.clear();
        this._stateInfos.clear();
        this._colorTypes.clear();
        this._statePainters.clear();
        this._stylePainters.clear();
    }
    
    private boolean isForwarding() {
        return this._depth > 0;
    }
    
    private DocumentHandler getHandler() {
        if (this._handler == null) {
            this._handler = new DocumentHandler();
            if (this._urlResourceBase != null) {
                this._handler.setClassLoader(new URLClassLoader(new URL[] { this.getResource(".") }, Thread.currentThread().getContextClassLoader()));
            }
            else {
                this._handler.setClassLoader(this._classResourceBase.getClassLoader());
            }
            for (final String s : this._mapping.keySet()) {
                this._handler.setVariable(s, this._mapping.get(s));
            }
        }
        return this._handler;
    }
    
    private Object checkCast(final Object o, final Class clazz) throws SAXException {
        if (!clazz.isInstance(o)) {
            throw new SAXException("Expected type " + clazz + " got " + o.getClass());
        }
        return o;
    }
    
    private Object lookup(final String s, final Class clazz) throws SAXException {
        if (this._handler != null && this._handler.hasVariable(s)) {
            return this.checkCast(this._handler.getVariable(s), clazz);
        }
        final Object value = this._mapping.get(s);
        if (value == null) {
            throw new SAXException("ID " + s + " has not been defined");
        }
        return this.checkCast(value, clazz);
    }
    
    private void register(final String s, final Object o) throws SAXException {
        if (s != null) {
            if (this._mapping.get(s) != null || (this._handler != null && this._handler.hasVariable(s))) {
                throw new SAXException("ID " + s + " is already defined");
            }
            if (this._handler != null) {
                this._handler.setVariable(s, o);
            }
            else {
                this._mapping.put(s, o);
            }
        }
    }
    
    private int nextInt(final StringTokenizer stringTokenizer, final String s) throws SAXException {
        if (!stringTokenizer.hasMoreTokens()) {
            throw new SAXException(s);
        }
        try {
            return Integer.parseInt(stringTokenizer.nextToken());
        }
        catch (final NumberFormatException ex) {
            throw new SAXException(s);
        }
    }
    
    private Insets parseInsets(final String s, final String s2) throws SAXException {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        return new Insets(this.nextInt(stringTokenizer, s2), this.nextInt(stringTokenizer, s2), this.nextInt(stringTokenizer, s2), this.nextInt(stringTokenizer, s2));
    }
    
    private void startStyle(final Attributes attributes) throws SAXException {
        String value = null;
        this._style = null;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            if (qName.equals("clone")) {
                this._style = (ParsedSynthStyle)((ParsedSynthStyle)this.lookup(attributes.getValue(i), ParsedSynthStyle.class)).clone();
            }
            else if (qName.equals("id")) {
                value = attributes.getValue(i);
            }
        }
        if (this._style == null) {
            this._style = new ParsedSynthStyle();
        }
        this.register(value, this._style);
    }
    
    private void endStyle() {
        final int size = this._stylePainters.size();
        if (size > 0) {
            this._style.setPainters(this._stylePainters.toArray(new ParsedSynthStyle.PainterInfo[size]));
            this._stylePainters.clear();
        }
        final int size2 = this._stateInfos.size();
        if (size2 > 0) {
            this._style.setStateInfo(this._stateInfos.toArray(new ParsedSynthStyle.StateInfo[size2]));
            this._stateInfos.clear();
        }
        this._style = null;
    }
    
    private void startState(final Attributes attributes) throws SAXException {
        int componentState = 0;
        String value = null;
        this._stateInfo = null;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            if (qName.equals("id")) {
                value = attributes.getValue(i);
            }
            else if (qName.equals("idref")) {
                this._stateInfo = (ParsedSynthStyle.StateInfo)this.lookup(attributes.getValue(i), ParsedSynthStyle.StateInfo.class);
            }
            else if (qName.equals("clone")) {
                this._stateInfo = (ParsedSynthStyle.StateInfo)((ParsedSynthStyle.StateInfo)this.lookup(attributes.getValue(i), ParsedSynthStyle.StateInfo.class)).clone();
            }
            else if (qName.equals("value")) {
                final StringTokenizer stringTokenizer = new StringTokenizer(attributes.getValue(i));
                while (stringTokenizer.hasMoreTokens()) {
                    final String intern = stringTokenizer.nextToken().toUpperCase().intern();
                    if (intern == "ENABLED") {
                        componentState |= 0x1;
                    }
                    else if (intern == "MOUSE_OVER") {
                        componentState |= 0x2;
                    }
                    else if (intern == "PRESSED") {
                        componentState |= 0x4;
                    }
                    else if (intern == "DISABLED") {
                        componentState |= 0x8;
                    }
                    else if (intern == "FOCUSED") {
                        componentState |= 0x100;
                    }
                    else if (intern == "SELECTED") {
                        componentState |= 0x200;
                    }
                    else if (intern == "DEFAULT") {
                        componentState |= 0x400;
                    }
                    else {
                        if (intern != "AND") {
                            throw new SAXException("Unknown state: " + componentState);
                        }
                        continue;
                    }
                }
            }
        }
        if (this._stateInfo == null) {
            this._stateInfo = new ParsedSynthStyle.StateInfo();
        }
        this._stateInfo.setComponentState(componentState);
        this.register(value, this._stateInfo);
        this._stateInfos.add(this._stateInfo);
    }
    
    private void endState() {
        final int size = this._statePainters.size();
        if (size > 0) {
            this._stateInfo.setPainters(this._statePainters.toArray(new ParsedSynthStyle.PainterInfo[size]));
            this._statePainters.clear();
        }
        this._stateInfo = null;
    }
    
    private void startFont(final Attributes attributes) throws SAXException {
        Object o = null;
        int n = 0;
        int int1 = 0;
        String value = null;
        String value2 = null;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            if (qName.equals("id")) {
                value = attributes.getValue(i);
            }
            else if (qName.equals("idref")) {
                o = this.lookup(attributes.getValue(i), Font.class);
            }
            else if (qName.equals("name")) {
                value2 = attributes.getValue(i);
            }
            else {
                if (qName.equals("size")) {
                    try {
                        int1 = Integer.parseInt(attributes.getValue(i));
                        continue;
                    }
                    catch (final NumberFormatException ex) {
                        throw new SAXException("Invalid font size: " + attributes.getValue(i));
                    }
                }
                if (qName.equals("style")) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(attributes.getValue(i));
                    while (stringTokenizer.hasMoreTokens()) {
                        final String intern = stringTokenizer.nextToken().intern();
                        if (intern == "BOLD") {
                            n = (((n | 0x0) ^ 0x0) | 0x1);
                        }
                        else {
                            if (intern != "ITALIC") {
                                continue;
                            }
                            n |= 0x2;
                        }
                    }
                }
            }
        }
        if (o == null) {
            if (value2 == null) {
                throw new SAXException("You must define a name for the font");
            }
            if (int1 == 0) {
                throw new SAXException("You must define a size for the font");
            }
            o = new FontUIResource(value2, n, int1);
        }
        else if (value2 != null || int1 != 0 || n != 0) {
            throw new SAXException("Name, size and style are not for use with idref");
        }
        this.register(value, o);
        if (this._stateInfo != null) {
            this._stateInfo.setFont((Font)o);
        }
        else if (this._style != null) {
            this._style.setFont((Font)o);
        }
    }
    
    private void startColor(final Attributes attributes) throws SAXException {
        Object o = null;
        String value = null;
        this._colorTypes.clear();
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            if (qName.equals("id")) {
                value = attributes.getValue(i);
            }
            else if (qName.equals("idref")) {
                o = this.lookup(attributes.getValue(i), Color.class);
            }
            else if (!qName.equals("name")) {
                if (qName.equals("value")) {
                    final String value2 = attributes.getValue(i);
                    Label_0428: {
                        if (value2.startsWith("#")) {
                            try {
                                final int length = value2.length();
                                int n;
                                boolean b;
                                if (length < 8) {
                                    n = Integer.decode(value2);
                                    b = false;
                                }
                                else if (length == 8) {
                                    n = Integer.decode(value2);
                                    b = true;
                                }
                                else {
                                    if (length != 9) {
                                        throw new SAXException("Invalid Color value: " + value2);
                                    }
                                    n = (Integer.decode(value2.substring(0, 3)) << 24 | Integer.decode('#' + value2.substring(3, 9)));
                                    b = true;
                                }
                                o = new ColorUIResource(new Color(n, b));
                                break Label_0428;
                            }
                            catch (final NumberFormatException ex) {
                                throw new SAXException("Invalid Color value: " + value2);
                            }
                        }
                        try {
                            o = new ColorUIResource((Color)Color.class.getField(value2.toUpperCase()).get(Color.class));
                        }
                        catch (final NoSuchFieldException ex2) {
                            throw new SAXException("Invalid color name: " + value2);
                        }
                        catch (final IllegalAccessException ex3) {
                            throw new SAXException("Invalid color name: " + value2);
                        }
                    }
                }
                else if (qName.equals("type")) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(attributes.getValue(i));
                    while (stringTokenizer.hasMoreTokens()) {
                        final String nextToken = stringTokenizer.nextToken();
                        int lastIndex = nextToken.lastIndexOf(46);
                        Class<?> forName;
                        if (lastIndex == -1) {
                            forName = ColorType.class;
                            lastIndex = 0;
                        }
                        else {
                            try {
                                forName = ReflectUtil.forName(nextToken.substring(0, lastIndex));
                            }
                            catch (final ClassNotFoundException ex4) {
                                throw new SAXException("Unknown class: " + nextToken.substring(0, lastIndex));
                            }
                            ++lastIndex;
                        }
                        try {
                            this._colorTypes.add((ColorType)this.checkCast(forName.getField(nextToken.substring(lastIndex)).get(forName), ColorType.class));
                        }
                        catch (final NoSuchFieldException ex5) {
                            throw new SAXException("Unable to find color type: " + nextToken);
                        }
                        catch (final IllegalAccessException ex6) {
                            throw new SAXException("Unable to find color type: " + nextToken);
                        }
                    }
                }
            }
        }
        if (o == null) {
            throw new SAXException("color: you must specificy a value");
        }
        this.register(value, o);
        if (this._stateInfo != null && this._colorTypes.size() > 0) {
            Color[] colors = this._stateInfo.getColors();
            int max = 0;
            for (int j = this._colorTypes.size() - 1; j >= 0; --j) {
                max = Math.max(max, this._colorTypes.get(j).getID());
            }
            if (colors == null || colors.length <= max) {
                final Color[] array = new Color[max + 1];
                if (colors != null) {
                    System.arraycopy(colors, 0, array, 0, colors.length);
                }
                colors = array;
            }
            for (int k = this._colorTypes.size() - 1; k >= 0; --k) {
                colors[this._colorTypes.get(k).getID()] = (Color)o;
            }
            this._stateInfo.setColors(colors);
        }
    }
    
    private void startProperty(final Attributes attributes, final Object o) throws SAXException {
        Object o2 = null;
        String value = null;
        int n = 0;
        String value2 = null;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            if (qName.equals("type")) {
                final String upperCase = attributes.getValue(i).toUpperCase();
                if (upperCase.equals("IDREF")) {
                    n = 0;
                }
                else if (upperCase.equals("BOOLEAN")) {
                    n = 1;
                }
                else if (upperCase.equals("DIMENSION")) {
                    n = 2;
                }
                else if (upperCase.equals("INSETS")) {
                    n = 3;
                }
                else if (upperCase.equals("INTEGER")) {
                    n = 4;
                }
                else {
                    if (!upperCase.equals("STRING")) {
                        throw new SAXException(o + " unknown type, useidref, boolean, dimension, insets or integer");
                    }
                    n = 5;
                }
            }
            else if (qName.equals("value")) {
                value2 = attributes.getValue(i);
            }
            else if (qName.equals("key")) {
                value = attributes.getValue(i);
            }
        }
        if (value2 != null) {
            switch (n) {
                case 0: {
                    o2 = this.lookup(value2, Object.class);
                    break;
                }
                case 1: {
                    if (value2.toUpperCase().equals("TRUE")) {
                        o2 = Boolean.TRUE;
                        break;
                    }
                    o2 = Boolean.FALSE;
                    break;
                }
                case 2: {
                    final StringTokenizer stringTokenizer = new StringTokenizer(value2);
                    o2 = new DimensionUIResource(this.nextInt(stringTokenizer, "Invalid dimension"), this.nextInt(stringTokenizer, "Invalid dimension"));
                    break;
                }
                case 3: {
                    o2 = this.parseInsets(value2, o + " invalid insets");
                    break;
                }
                case 4: {
                    try {
                        o2 = new Integer(Integer.parseInt(value2));
                        break;
                    }
                    catch (final NumberFormatException ex) {
                        throw new SAXException(o + " invalid value");
                    }
                }
                case 5: {
                    o2 = value2;
                    break;
                }
            }
        }
        if (o2 == null || value == null) {
            throw new SAXException(o + ": you must supply a key and value");
        }
        if (o == "defaultsProperty") {
            this._defaultsMap.put(value, o2);
        }
        else if (this._stateInfo != null) {
            if (this._stateInfo.getData() == null) {
                this._stateInfo.setData(new HashMap());
            }
            this._stateInfo.getData().put(value, o2);
        }
        else if (this._style != null) {
            if (this._style.getData() == null) {
                this._style.setData(new HashMap());
            }
            this._style.getData().put(value, o2);
        }
    }
    
    private void startGraphics(final Attributes attributes) throws SAXException {
        SynthGraphicsUtils graphicsUtils = null;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            if (attributes.getQName(i).equals("idref")) {
                graphicsUtils = (SynthGraphicsUtils)this.lookup(attributes.getValue(i), SynthGraphicsUtils.class);
            }
        }
        if (graphicsUtils == null) {
            throw new SAXException("graphicsUtils: you must supply an idref");
        }
        if (this._style != null) {
            this._style.setGraphicsUtils(graphicsUtils);
        }
    }
    
    private void startInsets(final Attributes attributes) throws SAXException {
        int int1 = 0;
        int int2 = 0;
        int int3 = 0;
        int int4 = 0;
        Object insets = null;
        String value = null;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            try {
                if (qName.equals("idref")) {
                    insets = this.lookup(attributes.getValue(i), Insets.class);
                }
                else if (qName.equals("id")) {
                    value = attributes.getValue(i);
                }
                else if (qName.equals("top")) {
                    int1 = Integer.parseInt(attributes.getValue(i));
                }
                else if (qName.equals("left")) {
                    int3 = Integer.parseInt(attributes.getValue(i));
                }
                else if (qName.equals("bottom")) {
                    int2 = Integer.parseInt(attributes.getValue(i));
                }
                else if (qName.equals("right")) {
                    int4 = Integer.parseInt(attributes.getValue(i));
                }
            }
            catch (final NumberFormatException ex) {
                throw new SAXException("insets: bad integer value for " + attributes.getValue(i));
            }
        }
        if (insets == null) {
            insets = new InsetsUIResource(int1, int3, int2, int4);
        }
        this.register(value, insets);
        if (this._style != null) {
            this._style.setInsets((Insets)insets);
        }
    }
    
    private void startBind(final Attributes attributes) throws SAXException {
        DefaultSynthStyle defaultSynthStyle = null;
        String value = null;
        int n = -1;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            if (qName.equals("style")) {
                defaultSynthStyle = (ParsedSynthStyle)this.lookup(attributes.getValue(i), ParsedSynthStyle.class);
            }
            else if (qName.equals("type")) {
                final String upperCase = attributes.getValue(i).toUpperCase();
                if (upperCase.equals("NAME")) {
                    n = 0;
                }
                else {
                    if (!upperCase.equals("REGION")) {
                        throw new SAXException("bind: unknown type " + upperCase);
                    }
                    n = 1;
                }
            }
            else if (qName.equals("key")) {
                value = attributes.getValue(i);
            }
        }
        if (defaultSynthStyle == null || value == null || n == -1) {
            throw new SAXException("bind: you must specify a style, type and key");
        }
        try {
            this._factory.addStyle(defaultSynthStyle, value, n);
        }
        catch (final PatternSyntaxException ex) {
            throw new SAXException("bind: " + value + " is not a valid regular expression");
        }
    }
    
    private void startPainter(final Attributes attributes, final String s) throws SAXException {
        Insets insets = null;
        Insets insets2 = null;
        String s2 = null;
        boolean equals = true;
        boolean equals2 = true;
        Object o = null;
        String lowerCase = null;
        String s3 = null;
        int n = -1;
        boolean equals3 = false;
        boolean b = false;
        boolean b2 = false;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            final String value = attributes.getValue(i);
            if (qName.equals("id")) {
                s3 = value;
            }
            else if (qName.equals("method")) {
                lowerCase = value.toLowerCase(Locale.ENGLISH);
            }
            else if (qName.equals("idref")) {
                o = this.lookup(value, SynthPainter.class);
            }
            else if (qName.equals("path")) {
                s2 = value;
            }
            else if (qName.equals("sourceInsets")) {
                insets = this.parseInsets(value, s + ": sourceInsets must be top left bottom right");
            }
            else if (qName.equals("destinationInsets")) {
                insets2 = this.parseInsets(value, s + ": destinationInsets must be top left bottom right");
            }
            else if (qName.equals("paintCenter")) {
                equals = value.toLowerCase().equals("true");
                b2 = true;
            }
            else if (qName.equals("stretch")) {
                equals2 = value.toLowerCase().equals("true");
                b = true;
            }
            else if (qName.equals("direction")) {
                final String intern = value.toUpperCase().intern();
                if (intern == "EAST") {
                    n = 3;
                }
                else if (intern == "NORTH") {
                    n = 1;
                }
                else if (intern == "SOUTH") {
                    n = 5;
                }
                else if (intern == "WEST") {
                    n = 7;
                }
                else if (intern == "TOP") {
                    n = 1;
                }
                else if (intern == "LEFT") {
                    n = 2;
                }
                else if (intern == "BOTTOM") {
                    n = 3;
                }
                else if (intern == "RIGHT") {
                    n = 4;
                }
                else if (intern == "HORIZONTAL") {
                    n = 0;
                }
                else if (intern == "VERTICAL") {
                    n = 1;
                }
                else if (intern == "HORIZONTAL_SPLIT") {
                    n = 1;
                }
                else {
                    if (intern != "VERTICAL_SPLIT") {
                        throw new SAXException(s + ": unknown direction");
                    }
                    n = 0;
                }
            }
            else if (qName.equals("center")) {
                equals3 = value.toLowerCase().equals("true");
            }
        }
        if (o == null) {
            if (s == "painter") {
                throw new SAXException(s + ": you must specify an idref");
            }
            if (insets == null && !equals3) {
                throw new SAXException("property: you must specify sourceInsets");
            }
            if (s2 == null) {
                throw new SAXException("property: you must specify a path");
            }
            if (equals3 && (insets != null || insets2 != null || b2 || b)) {
                throw new SAXException("The attributes: sourceInsets, destinationInsets, paintCenter and stretch  are not legal when center is true");
            }
            o = new ImagePainter(!equals2, equals, insets, insets2, this.getResource(s2), equals3);
        }
        this.register(s3, o);
        if (this._stateInfo != null) {
            this.addPainterOrMerge(this._statePainters, lowerCase, (SynthPainter)o, n);
        }
        else if (this._style != null) {
            this.addPainterOrMerge(this._stylePainters, lowerCase, (SynthPainter)o, n);
        }
    }
    
    private void addPainterOrMerge(final List<ParsedSynthStyle.PainterInfo> list, final String s, final SynthPainter synthPainter, final int n) {
        final ParsedSynthStyle.PainterInfo painterInfo = new ParsedSynthStyle.PainterInfo(s, synthPainter, n);
        for (final ParsedSynthStyle.PainterInfo painterInfo2 : list) {
            if (painterInfo.equalsPainter(painterInfo2)) {
                painterInfo2.addPainter(synthPainter);
                return;
            }
        }
        list.add(painterInfo);
    }
    
    private void startImageIcon(final Attributes attributes) throws SAXException {
        String value = null;
        String value2 = null;
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            final String qName = attributes.getQName(i);
            if (qName.equals("id")) {
                value2 = attributes.getValue(i);
            }
            else if (qName.equals("path")) {
                value = attributes.getValue(i);
            }
        }
        if (value == null) {
            throw new SAXException("imageIcon: you must specify a path");
        }
        this.register(value2, new LazyImageIcon(this.getResource(value)));
    }
    
    private void startOpaque(final Attributes attributes) {
        if (this._style != null) {
            this._style.setOpaque(true);
            for (int i = attributes.getLength() - 1; i >= 0; --i) {
                if (attributes.getQName(i).equals("value")) {
                    this._style.setOpaque("true".equals(attributes.getValue(i).toLowerCase()));
                }
            }
        }
    }
    
    private void startInputMap(final Attributes attributes) throws SAXException {
        this._inputMapBindings.clear();
        this._inputMapID = null;
        if (this._style != null) {
            for (int i = attributes.getLength() - 1; i >= 0; --i) {
                if (attributes.getQName(i).equals("id")) {
                    this._inputMapID = attributes.getValue(i);
                }
            }
        }
    }
    
    private void endInputMap() throws SAXException {
        if (this._inputMapID != null) {
            this.register(this._inputMapID, new UIDefaults.LazyInputMap(this._inputMapBindings.toArray(new Object[this._inputMapBindings.size()])));
        }
        this._inputMapBindings.clear();
        this._inputMapID = null;
    }
    
    private void startBindKey(final Attributes attributes) throws SAXException {
        if (this._inputMapID == null) {
            return;
        }
        if (this._style != null) {
            String value = null;
            String value2 = null;
            for (int i = attributes.getLength() - 1; i >= 0; --i) {
                final String qName = attributes.getQName(i);
                if (qName.equals("key")) {
                    value = attributes.getValue(i);
                }
                else if (qName.equals("action")) {
                    value2 = attributes.getValue(i);
                }
            }
            if (value == null || value2 == null) {
                throw new SAXException("bindKey: you must supply a key and action");
            }
            this._inputMapBindings.add(value);
            this._inputMapBindings.add(value2);
        }
    }
    
    @Override
    public InputSource resolveEntity(final String s, final String s2) throws IOException, SAXException {
        if (this.isForwarding()) {
            return this.getHandler().resolveEntity(s, s2);
        }
        return null;
    }
    
    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().notationDecl(name, publicId, systemId);
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().unparsedEntityDecl(name, publicId, systemId, notationName);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator documentLocator) {
        if (this.isForwarding()) {
            this.getHandler().setDocumentLocator(documentLocator);
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().startDocument();
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().endDocument();
        }
    }
    
    @Override
    public void startElement(final String s, final String s2, String intern, final Attributes attributes) throws SAXException {
        intern = intern.intern();
        if (intern == "style") {
            this.startStyle(attributes);
        }
        else if (intern == "state") {
            this.startState(attributes);
        }
        else if (intern == "font") {
            this.startFont(attributes);
        }
        else if (intern == "color") {
            this.startColor(attributes);
        }
        else if (intern == "painter") {
            this.startPainter(attributes, intern);
        }
        else if (intern == "imagePainter") {
            this.startPainter(attributes, intern);
        }
        else if (intern == "property") {
            this.startProperty(attributes, "property");
        }
        else if (intern == "defaultsProperty") {
            this.startProperty(attributes, "defaultsProperty");
        }
        else if (intern == "graphicsUtils") {
            this.startGraphics(attributes);
        }
        else if (intern == "insets") {
            this.startInsets(attributes);
        }
        else if (intern == "bind") {
            this.startBind(attributes);
        }
        else if (intern == "bindKey") {
            this.startBindKey(attributes);
        }
        else if (intern == "imageIcon") {
            this.startImageIcon(attributes);
        }
        else if (intern == "opaque") {
            this.startOpaque(attributes);
        }
        else if (intern == "inputMap") {
            this.startInputMap(attributes);
        }
        else if (intern != "synth") {
            if (this._depth++ == 0) {
                this.getHandler().startDocument();
            }
            this.getHandler().startElement(s, s2, intern, attributes);
        }
    }
    
    @Override
    public void endElement(final String s, final String s2, String intern) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().endElement(s, s2, intern);
            --this._depth;
            if (!this.isForwarding()) {
                this.getHandler().startDocument();
            }
        }
        else {
            intern = intern.intern();
            if (intern == "style") {
                this.endStyle();
            }
            else if (intern == "state") {
                this.endState();
            }
            else if (intern == "inputMap") {
                this.endInputMap();
            }
        }
    }
    
    @Override
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().characters(array, n, n2);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().ignorableWhitespace(ch, start, length);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().processingInstruction(target, data);
        }
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().warning(e);
        }
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().error(e);
        }
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        if (this.isForwarding()) {
            this.getHandler().fatalError(e);
        }
        throw e;
    }
    
    private static class LazyImageIcon extends ImageIcon implements UIResource
    {
        private URL location;
        
        public LazyImageIcon(final URL location) {
            this.location = location;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (this.getImage() != null) {
                super.paintIcon(component, graphics, n, n2);
            }
        }
        
        @Override
        public int getIconWidth() {
            if (this.getImage() != null) {
                return super.getIconWidth();
            }
            return 0;
        }
        
        @Override
        public int getIconHeight() {
            if (this.getImage() != null) {
                return super.getIconHeight();
            }
            return 0;
        }
        
        @Override
        public Image getImage() {
            if (this.location != null) {
                this.setImage(Toolkit.getDefaultToolkit().getImage(this.location));
                this.location = null;
            }
            return super.getImage();
        }
    }
}

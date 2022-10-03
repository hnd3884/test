package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codehaus.stax2.io.Stax2ByteArraySource;
import javax.xml.transform.Source;
import org.codehaus.stax2.io.Stax2CharArraySource;
import javax.xml.stream.XMLStreamException;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import java.io.InputStream;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.Writer;
import com.fasterxml.jackson.core.JsonEncoding;
import java.io.OutputStream;
import com.fasterxml.jackson.core.io.IOContext;
import java.io.Reader;
import java.io.StringReader;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.TSFBuilder;
import com.fasterxml.jackson.core.ObjectCodec;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import com.fasterxml.jackson.core.JsonFactory;

public class XmlFactory extends JsonFactory
{
    private static final long serialVersionUID = 1L;
    public static final String FORMAT_NAME_XML = "XML";
    static final int DEFAULT_XML_PARSER_FEATURE_FLAGS;
    static final int DEFAULT_XML_GENERATOR_FEATURE_FLAGS;
    protected int _xmlParserFeatures;
    protected int _xmlGeneratorFeatures;
    protected transient XMLInputFactory _xmlInputFactory;
    protected transient XMLOutputFactory _xmlOutputFactory;
    protected String _cfgNameForTextElement;
    protected transient String _jdkXmlInFactory;
    protected transient String _jdkXmlOutFactory;
    private static final byte UTF8_BOM_1 = -17;
    private static final byte UTF8_BOM_2 = -69;
    private static final byte UTF8_BOM_3 = -65;
    private static final byte BYTE_x = 120;
    private static final byte BYTE_m = 109;
    private static final byte BYTE_l = 108;
    private static final byte BYTE_D = 68;
    private static final byte BYTE_LT = 60;
    private static final byte BYTE_QMARK = 63;
    private static final byte BYTE_EXCL = 33;
    private static final byte BYTE_HYPHEN = 45;
    
    public XmlFactory() {
        this(null, null, null);
    }
    
    public XmlFactory(final ObjectCodec oc) {
        this(oc, null, null);
    }
    
    public XmlFactory(final XMLInputFactory xmlIn) {
        this(null, xmlIn, null);
    }
    
    public XmlFactory(final XMLInputFactory xmlIn, final XMLOutputFactory xmlOut) {
        this(null, xmlIn, xmlOut);
    }
    
    public XmlFactory(final ObjectCodec oc, final XMLInputFactory xmlIn, final XMLOutputFactory xmlOut) {
        this(oc, XmlFactory.DEFAULT_XML_PARSER_FEATURE_FLAGS, XmlFactory.DEFAULT_XML_GENERATOR_FEATURE_FLAGS, xmlIn, xmlOut, null);
    }
    
    protected XmlFactory(final ObjectCodec oc, final int xpFeatures, final int xgFeatures, XMLInputFactory xmlIn, XMLOutputFactory xmlOut, final String nameForTextElem) {
        super(oc);
        this._xmlParserFeatures = xpFeatures;
        this._xmlGeneratorFeatures = xgFeatures;
        this._cfgNameForTextElement = nameForTextElem;
        if (xmlIn == null) {
            xmlIn = XMLInputFactory.newInstance();
            xmlIn.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
            xmlIn.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
        }
        if (xmlOut == null) {
            xmlOut = XMLOutputFactory.newInstance();
        }
        this._initFactories(xmlIn, xmlOut);
        this._xmlInputFactory = xmlIn;
        this._xmlOutputFactory = xmlOut;
    }
    
    protected XmlFactory(final XmlFactory src, final ObjectCodec oc) {
        super((JsonFactory)src, oc);
        this._xmlParserFeatures = src._xmlParserFeatures;
        this._xmlGeneratorFeatures = src._xmlGeneratorFeatures;
        this._cfgNameForTextElement = src._cfgNameForTextElement;
        this._xmlInputFactory = src._xmlInputFactory;
        this._xmlOutputFactory = src._xmlOutputFactory;
    }
    
    protected XmlFactory(final XmlFactoryBuilder b) {
        super((TSFBuilder)b, false);
        this._xmlParserFeatures = b.formatParserFeaturesMask();
        this._xmlGeneratorFeatures = b.formatGeneratorFeaturesMask();
        this._cfgNameForTextElement = b.nameForTextElement();
        this._xmlInputFactory = b.xmlInputFactory();
        this._xmlOutputFactory = b.xmlOutputFactory();
        this._initFactories(this._xmlInputFactory, this._xmlOutputFactory);
    }
    
    public static XmlFactoryBuilder builder() {
        return new XmlFactoryBuilder();
    }
    
    public XmlFactoryBuilder rebuild() {
        return new XmlFactoryBuilder(this);
    }
    
    protected void _initFactories(final XMLInputFactory xmlIn, final XMLOutputFactory xmlOut) {
        xmlOut.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
        xmlIn.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
    }
    
    public XmlFactory copy() {
        this._checkInvalidCopy((Class)XmlFactory.class);
        return new XmlFactory(this, null);
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    protected Object readResolve() {
        if (this._jdkXmlInFactory == null) {
            throw new IllegalStateException("No XMLInputFactory class name read during JDK deserialization");
        }
        if (this._jdkXmlOutFactory == null) {
            throw new IllegalStateException("No XMLOutputFactory class name read during JDK deserialization");
        }
        XMLInputFactory inf;
        XMLOutputFactory outf;
        try {
            inf = (XMLInputFactory)Class.forName(this._jdkXmlInFactory).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            outf = (XMLOutputFactory)Class.forName(this._jdkXmlOutFactory).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
        return new XmlFactory(this._objectCodec, this._xmlParserFeatures, this._xmlGeneratorFeatures, inf, outf, this._cfgNameForTextElement);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this._jdkXmlInFactory = in.readUTF();
        this._jdkXmlOutFactory = in.readUTF();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(this._xmlInputFactory.getClass().getName());
        out.writeUTF(this._xmlOutputFactory.getClass().getName());
    }
    
    public void setXMLTextElementName(final String name) {
        this._cfgNameForTextElement = name;
    }
    
    public String getXMLTextElementName() {
        return this._cfgNameForTextElement;
    }
    
    public final XmlFactory configure(final FromXmlParser.Feature f, final boolean state) {
        if (state) {
            this.enable(f);
        }
        else {
            this.disable(f);
        }
        return this;
    }
    
    public XmlFactory enable(final FromXmlParser.Feature f) {
        this._xmlParserFeatures |= f.getMask();
        return this;
    }
    
    public XmlFactory disable(final FromXmlParser.Feature f) {
        this._xmlParserFeatures &= ~f.getMask();
        return this;
    }
    
    public final boolean isEnabled(final FromXmlParser.Feature f) {
        return (this._xmlParserFeatures & f.getMask()) != 0x0;
    }
    
    public int getFormatParserFeatures() {
        return this._xmlParserFeatures;
    }
    
    public int getFormatGeneratorFeatures() {
        return this._xmlGeneratorFeatures;
    }
    
    public final XmlFactory configure(final ToXmlGenerator.Feature f, final boolean state) {
        if (state) {
            this.enable(f);
        }
        else {
            this.disable(f);
        }
        return this;
    }
    
    public XmlFactory enable(final ToXmlGenerator.Feature f) {
        this._xmlGeneratorFeatures |= f.getMask();
        return this;
    }
    
    public XmlFactory disable(final ToXmlGenerator.Feature f) {
        this._xmlGeneratorFeatures &= ~f.getMask();
        return this;
    }
    
    public final boolean isEnabled(final ToXmlGenerator.Feature f) {
        return (this._xmlGeneratorFeatures & f.getMask()) != 0x0;
    }
    
    public XMLInputFactory getXMLInputFactory() {
        return this._xmlInputFactory;
    }
    
    public void setXMLInputFactory(final XMLInputFactory f) {
        this._xmlInputFactory = f;
    }
    
    public XMLOutputFactory getXMLOutputFactory() {
        return this._xmlOutputFactory;
    }
    
    public void setXMLOutputFactory(final XMLOutputFactory f) {
        this._xmlOutputFactory = f;
    }
    
    public String getFormatName() {
        return "XML";
    }
    
    public MatchStrength hasFormat(final InputAccessor acc) throws IOException {
        return hasXMLFormat(acc);
    }
    
    public boolean requiresCustomCodec() {
        return true;
    }
    
    public boolean canUseCharArrays() {
        return false;
    }
    
    public Class<FromXmlParser.Feature> getFormatReadFeatureType() {
        return FromXmlParser.Feature.class;
    }
    
    public Class<ToXmlGenerator.Feature> getFormatWriteFeatureType() {
        return ToXmlGenerator.Feature.class;
    }
    
    public JsonParser createParser(final String content) throws IOException {
        Reader r = new StringReader(content);
        final IOContext ctxt = this._createContext((Object)r, true);
        if (this._inputDecorator != null) {
            r = this._inputDecorator.decorate(ctxt, r);
        }
        return (JsonParser)this._createParser(r, ctxt);
    }
    
    public ToXmlGenerator createGenerator(final OutputStream out) throws IOException {
        return this.createGenerator(out, JsonEncoding.UTF8);
    }
    
    public ToXmlGenerator createGenerator(final OutputStream out, final JsonEncoding enc) throws IOException {
        final IOContext ctxt = this._createContext((Object)out, false);
        ctxt.setEncoding(enc);
        return new ToXmlGenerator(ctxt, this._generatorFeatures, this._xmlGeneratorFeatures, this._objectCodec, this._createXmlWriter(ctxt, out));
    }
    
    public ToXmlGenerator createGenerator(final Writer out) throws IOException {
        final IOContext ctxt = this._createContext((Object)out, false);
        return new ToXmlGenerator(ctxt, this._generatorFeatures, this._xmlGeneratorFeatures, this._objectCodec, this._createXmlWriter(ctxt, out));
    }
    
    public ToXmlGenerator createGenerator(final File f, final JsonEncoding enc) throws IOException {
        final OutputStream out = new FileOutputStream(f);
        final IOContext ctxt = this._createContext((Object)out, true);
        ctxt.setEncoding(enc);
        return new ToXmlGenerator(ctxt, this._generatorFeatures, this._xmlGeneratorFeatures, this._objectCodec, this._createXmlWriter(ctxt, out));
    }
    
    public FromXmlParser createParser(XMLStreamReader sr) throws IOException {
        if (sr.getEventType() != 1) {
            sr = this._initializeXmlReader(sr);
        }
        final FromXmlParser xp = new FromXmlParser(this._createContext((Object)sr, false), this._parserFeatures, this._xmlParserFeatures, this._objectCodec, sr);
        if (this._cfgNameForTextElement != null) {
            xp.setXMLTextElementName(this._cfgNameForTextElement);
        }
        return xp;
    }
    
    public ToXmlGenerator createGenerator(XMLStreamWriter sw) throws IOException {
        sw = this._initializeXmlWriter(sw);
        final IOContext ctxt = this._createContext((Object)sw, false);
        return new ToXmlGenerator(ctxt, this._generatorFeatures, this._xmlGeneratorFeatures, this._objectCodec, sw);
    }
    
    protected FromXmlParser _createParser(final InputStream in, final IOContext ctxt) throws IOException {
        XMLStreamReader sr;
        try {
            sr = this._xmlInputFactory.createXMLStreamReader(in);
        }
        catch (final XMLStreamException e) {
            return StaxUtil.throwAsParseException(e, null);
        }
        sr = this._initializeXmlReader(sr);
        final FromXmlParser xp = new FromXmlParser(ctxt, this._parserFeatures, this._xmlParserFeatures, this._objectCodec, sr);
        if (this._cfgNameForTextElement != null) {
            xp.setXMLTextElementName(this._cfgNameForTextElement);
        }
        return xp;
    }
    
    protected FromXmlParser _createParser(final Reader r, final IOContext ctxt) throws IOException {
        XMLStreamReader sr;
        try {
            sr = this._xmlInputFactory.createXMLStreamReader(r);
        }
        catch (final XMLStreamException e) {
            return StaxUtil.throwAsParseException(e, null);
        }
        sr = this._initializeXmlReader(sr);
        final FromXmlParser xp = new FromXmlParser(ctxt, this._parserFeatures, this._xmlParserFeatures, this._objectCodec, sr);
        if (this._cfgNameForTextElement != null) {
            xp.setXMLTextElementName(this._cfgNameForTextElement);
        }
        return xp;
    }
    
    protected FromXmlParser _createParser(final char[] data, final int offset, final int len, final IOContext ctxt, final boolean recycleBuffer) throws IOException {
        XMLStreamReader sr;
        try {
            sr = this._xmlInputFactory.createXMLStreamReader((Source)new Stax2CharArraySource(data, offset, len));
        }
        catch (final XMLStreamException e) {
            return StaxUtil.throwAsParseException(e, null);
        }
        sr = this._initializeXmlReader(sr);
        final FromXmlParser xp = new FromXmlParser(ctxt, this._parserFeatures, this._xmlParserFeatures, this._objectCodec, sr);
        if (this._cfgNameForTextElement != null) {
            xp.setXMLTextElementName(this._cfgNameForTextElement);
        }
        return xp;
    }
    
    protected FromXmlParser _createParser(final byte[] data, final int offset, final int len, final IOContext ctxt) throws IOException {
        XMLStreamReader sr;
        try {
            sr = this._xmlInputFactory.createXMLStreamReader((Source)new Stax2ByteArraySource(data, offset, len));
        }
        catch (final XMLStreamException e) {
            return StaxUtil.throwAsParseException(e, null);
        }
        sr = this._initializeXmlReader(sr);
        final FromXmlParser xp = new FromXmlParser(ctxt, this._parserFeatures, this._xmlParserFeatures, this._objectCodec, sr);
        if (this._cfgNameForTextElement != null) {
            xp.setXMLTextElementName(this._cfgNameForTextElement);
        }
        return xp;
    }
    
    protected JsonGenerator _createGenerator(final Writer out, final IOContext ctxt) throws IOException {
        VersionUtil.throwInternal();
        return null;
    }
    
    protected XMLStreamWriter _createXmlWriter(final IOContext ctxt, final OutputStream out) throws IOException {
        XMLStreamWriter sw;
        try {
            sw = this._xmlOutputFactory.createXMLStreamWriter(this._decorate(ctxt, out), "UTF-8");
        }
        catch (final Exception e) {
            throw new JsonGenerationException(e.getMessage(), (Throwable)e, (JsonGenerator)null);
        }
        return this._initializeXmlWriter(sw);
    }
    
    protected XMLStreamWriter _createXmlWriter(final IOContext ctxt, final Writer w) throws IOException {
        XMLStreamWriter sw;
        try {
            sw = this._xmlOutputFactory.createXMLStreamWriter(this._decorate(ctxt, w));
        }
        catch (final Exception e) {
            throw new JsonGenerationException(e.getMessage(), (Throwable)e, (JsonGenerator)null);
        }
        return this._initializeXmlWriter(sw);
    }
    
    protected final XMLStreamWriter _initializeXmlWriter(final XMLStreamWriter sw) throws IOException {
        try {
            sw.setDefaultNamespace("");
        }
        catch (final Exception e) {
            throw new JsonGenerationException(e.getMessage(), (Throwable)e, (JsonGenerator)null);
        }
        return sw;
    }
    
    protected final XMLStreamReader _initializeXmlReader(final XMLStreamReader sr) throws IOException {
        try {
            while (sr.next() != 1) {}
        }
        catch (final Exception e) {
            throw new JsonParseException((JsonParser)null, e.getMessage(), (Throwable)e);
        }
        return sr;
    }
    
    public static MatchStrength hasXMLFormat(final InputAccessor acc) throws IOException {
        if (!acc.hasMoreBytes()) {
            return MatchStrength.INCONCLUSIVE;
        }
        byte b = acc.nextByte();
        if (b == -17) {
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != -69) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != -65) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            b = acc.nextByte();
        }
        final boolean maybeXmlDecl = b == 60;
        if (!maybeXmlDecl) {
            final int ch = skipSpace(acc, b);
            if (ch < 0) {
                return MatchStrength.INCONCLUSIVE;
            }
            b = (byte)ch;
            if (b != 60) {
                return MatchStrength.NO_MATCH;
            }
        }
        if (!acc.hasMoreBytes()) {
            return MatchStrength.INCONCLUSIVE;
        }
        b = acc.nextByte();
        if (b == 63) {
            b = acc.nextByte();
            if (b == 120) {
                if (maybeXmlDecl && acc.hasMoreBytes() && acc.nextByte() == 109 && acc.hasMoreBytes() && acc.nextByte() == 108) {
                    return MatchStrength.FULL_MATCH;
                }
                return MatchStrength.SOLID_MATCH;
            }
            else if (validXmlNameStartChar(acc, b)) {
                return MatchStrength.SOLID_MATCH;
            }
        }
        else if (b == 33) {
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            b = acc.nextByte();
            if (b == 45) {
                if (!acc.hasMoreBytes()) {
                    return MatchStrength.INCONCLUSIVE;
                }
                if (acc.nextByte() == 45) {
                    return MatchStrength.SOLID_MATCH;
                }
            }
            else if (b == 68) {
                return tryMatch(acc, "OCTYPE", MatchStrength.SOLID_MATCH);
            }
        }
        else if (validXmlNameStartChar(acc, b)) {
            return MatchStrength.SOLID_MATCH;
        }
        return MatchStrength.NO_MATCH;
    }
    
    private static final boolean validXmlNameStartChar(final InputAccessor acc, final byte b) throws IOException {
        final int ch = b & 0xFF;
        return ch >= 65;
    }
    
    private static final MatchStrength tryMatch(final InputAccessor acc, final String matchStr, final MatchStrength fullMatchStrength) throws IOException {
        for (int i = 0, len = matchStr.length(); i < len; ++i) {
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != matchStr.charAt(i)) {
                return MatchStrength.NO_MATCH;
            }
        }
        return fullMatchStrength;
    }
    
    private static final int skipSpace(final InputAccessor acc, byte b) throws IOException {
        while (true) {
            int ch = b & 0xFF;
            if (ch != 32 && ch != 13 && ch != 10 && ch != 9) {
                return ch;
            }
            if (!acc.hasMoreBytes()) {
                return -1;
            }
            b = acc.nextByte();
            ch = (b & 0xFF);
        }
    }
    
    protected OutputStream _decorate(final IOContext ioCtxt, final OutputStream out) throws IOException {
        if (this._outputDecorator != null) {
            final OutputStream out2 = this._outputDecorator.decorate(ioCtxt, out);
            if (out2 != null) {
                return out2;
            }
        }
        return out;
    }
    
    protected Writer _decorate(final IOContext ioCtxt, final Writer out) throws IOException {
        if (this._outputDecorator != null) {
            final Writer out2 = this._outputDecorator.decorate(ioCtxt, out);
            if (out2 != null) {
                return out2;
            }
        }
        return out;
    }
    
    static {
        DEFAULT_XML_PARSER_FEATURE_FLAGS = FromXmlParser.Feature.collectDefaults();
        DEFAULT_XML_GENERATOR_FEATURE_FLAGS = ToXmlGenerator.Feature.collectDefaults();
    }
}

package com.sun.xml.internal.bind.v2.model.impl;

import java.util.UUID;
import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import java.util.Collections;
import java.util.Collection;
import javax.xml.datatype.Duration;
import javax.xml.namespace.NamespaceContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.transform.Transformer;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import com.sun.xml.internal.bind.v2.util.DataSourceSource;
import javax.xml.transform.Source;
import javax.activation.DataSource;
import com.sun.istack.internal.ByteArrayDataSource;
import javax.activation.DataHandler;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import javax.imageio.stream.ImageOutputStream;
import java.util.Iterator;
import java.awt.image.RenderedImage;
import javax.imageio.ImageWriter;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventImpl;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.MediaTracker;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.Image;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.MalformedURLException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.TODO;
import java.net.URL;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConstants;
import java.util.Calendar;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.xml.bind.MarshalException;
import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import java.lang.reflect.Type;

public abstract class RuntimeBuiltinLeafInfoImpl<T> extends BuiltinLeafInfoImpl<Type, Class> implements RuntimeBuiltinLeafInfo, Transducer<T>
{
    public static final Map<Type, RuntimeBuiltinLeafInfoImpl<?>> LEAVES;
    public static final RuntimeBuiltinLeafInfoImpl<String> STRING;
    private static final String DATE = "date";
    public static final List<RuntimeBuiltinLeafInfoImpl<?>> builtinBeanInfos;
    public static final String MAP_ANYURI_TO_URI = "mapAnyUriToUri";
    private static final Map<QName, String> xmlGregorianCalendarFormatString;
    private static final Map<QName, Integer> xmlGregorianCalendarFieldRef;
    
    private RuntimeBuiltinLeafInfoImpl(final Class type, final QName... typeNames) {
        super(type, typeNames);
        RuntimeBuiltinLeafInfoImpl.LEAVES.put(type, this);
    }
    
    @Override
    public final Class getClazz() {
        return (Class)this.getType();
    }
    
    @Override
    public final Transducer getTransducer() {
        return this;
    }
    
    @Override
    public boolean useNamespace() {
        return false;
    }
    
    @Override
    public final boolean isDefault() {
        return true;
    }
    
    @Override
    public void declareNamespace(final T o, final XMLSerializer w) throws AccessorException {
    }
    
    @Override
    public QName getTypeName(final T instance) {
        return null;
    }
    
    private static QName createXS(final String typeName) {
        return new QName("http://www.w3.org/2001/XMLSchema", typeName);
    }
    
    private static byte[] decodeBase64(final CharSequence text) {
        if (text instanceof Base64Data) {
            final Base64Data base64Data = (Base64Data)text;
            return base64Data.getExact();
        }
        return DatatypeConverterImpl._parseBase64Binary(text.toString());
    }
    
    private static void checkXmlGregorianCalendarFieldRef(final QName type, final XMLGregorianCalendar cal) throws MarshalException {
        final StringBuilder buf = new StringBuilder();
        int bitField = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFieldRef.get(type);
        final int l = 1;
        int pos = 0;
        while (bitField != 0) {
            final int bit = bitField & 0x1;
            bitField >>>= 4;
            ++pos;
            if (bit == 1) {
                switch (pos) {
                    case 1: {
                        if (cal.getSecond() == Integer.MIN_VALUE) {
                            buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_SEC);
                            continue;
                        }
                        continue;
                    }
                    case 2: {
                        if (cal.getMinute() == Integer.MIN_VALUE) {
                            buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_MIN);
                            continue;
                        }
                        continue;
                    }
                    case 3: {
                        if (cal.getHour() == Integer.MIN_VALUE) {
                            buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_HR);
                            continue;
                        }
                        continue;
                    }
                    case 4: {
                        if (cal.getDay() == Integer.MIN_VALUE) {
                            buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_DAY);
                            continue;
                        }
                        continue;
                    }
                    case 5: {
                        if (cal.getMonth() == Integer.MIN_VALUE) {
                            buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_MONTH);
                            continue;
                        }
                        continue;
                    }
                    case 6: {
                        if (cal.getYear() == Integer.MIN_VALUE) {
                            buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_YEAR);
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        if (buf.length() > 0) {
            throw new MarshalException(Messages.XMLGREGORIANCALENDAR_INVALID.format(type.getLocalPart()) + buf.toString());
        }
    }
    
    static {
        LEAVES = new HashMap<Type, RuntimeBuiltinLeafInfoImpl<?>>();
        final String MAP_ANYURI_TO_URI_VALUE = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("mapAnyUriToUri");
            }
        });
        final QName[] qnames = (MAP_ANYURI_TO_URI_VALUE == null) ? new QName[] { createXS("string"), createXS("anySimpleType"), createXS("normalizedString"), createXS("anyURI"), createXS("token"), createXS("language"), createXS("Name"), createXS("NCName"), createXS("NMTOKEN"), createXS("ENTITY") } : new QName[] { createXS("string"), createXS("anySimpleType"), createXS("normalizedString"), createXS("token"), createXS("language"), createXS("Name"), createXS("NCName"), createXS("NMTOKEN"), createXS("ENTITY") };
        STRING = new StringImplImpl(String.class, qnames);
        final ArrayList<RuntimeBuiltinLeafInfoImpl<?>> secondaryList = new ArrayList<RuntimeBuiltinLeafInfoImpl<?>>();
        secondaryList.add(new StringImpl<Character>((Class)Character.class, new QName[] { createXS("unsignedShort") }) {
            @Override
            public Character parse(final CharSequence text) {
                return (char)DatatypeConverterImpl._parseInt(text);
            }
            
            @Override
            public String print(final Character v) {
                return Integer.toString(v);
            }
        });
        secondaryList.add(new StringImpl<Calendar>((Class)Calendar.class, new QName[] { DatatypeConstants.DATETIME }) {
            @Override
            public Calendar parse(final CharSequence text) {
                return DatatypeConverterImpl._parseDateTime(text.toString());
            }
            
            @Override
            public String print(final Calendar v) {
                return DatatypeConverterImpl._printDateTime(v);
            }
        });
        secondaryList.add(new StringImpl<GregorianCalendar>((Class)GregorianCalendar.class, new QName[] { DatatypeConstants.DATETIME }) {
            @Override
            public GregorianCalendar parse(final CharSequence text) {
                return DatatypeConverterImpl._parseDateTime(text.toString());
            }
            
            @Override
            public String print(final GregorianCalendar v) {
                return DatatypeConverterImpl._printDateTime(v);
            }
        });
        secondaryList.add(new StringImpl<Date>((Class)Date.class, new QName[] { DatatypeConstants.DATETIME }) {
            @Override
            public Date parse(final CharSequence text) {
                return DatatypeConverterImpl._parseDateTime(text.toString()).getTime();
            }
            
            @Override
            public String print(final Date v) {
                final XMLSerializer xs = XMLSerializer.getInstance();
                final QName type = xs.getSchemaType();
                final GregorianCalendar cal = new GregorianCalendar(0, 0, 0);
                cal.setTime(v);
                if (type != null && "http://www.w3.org/2001/XMLSchema".equals(type.getNamespaceURI()) && "date".equals(type.getLocalPart())) {
                    return DatatypeConverterImpl._printDate(cal);
                }
                return DatatypeConverterImpl._printDateTime(cal);
            }
        });
        secondaryList.add(new StringImpl<File>((Class)File.class, new QName[] { createXS("string") }) {
            @Override
            public File parse(final CharSequence text) {
                return new File(WhiteSpaceProcessor.trim(text).toString());
            }
            
            @Override
            public String print(final File v) {
                return v.getPath();
            }
        });
        secondaryList.add(new StringImpl<URL>((Class)URL.class, new QName[] { createXS("anyURI") }) {
            @Override
            public URL parse(final CharSequence text) throws SAXException {
                TODO.checkSpec("JSR222 Issue #42");
                try {
                    return new URL(WhiteSpaceProcessor.trim(text).toString());
                }
                catch (final MalformedURLException e) {
                    UnmarshallingContext.getInstance().handleError(e);
                    return null;
                }
            }
            
            @Override
            public String print(final URL v) {
                return v.toExternalForm();
            }
        });
        if (MAP_ANYURI_TO_URI_VALUE == null) {
            secondaryList.add(new StringImpl<URI>((Class)URI.class, new QName[] { createXS("string") }) {
                @Override
                public URI parse(final CharSequence text) throws SAXException {
                    try {
                        return new URI(text.toString());
                    }
                    catch (final URISyntaxException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                @Override
                public String print(final URI v) {
                    return v.toString();
                }
            });
        }
        secondaryList.add(new StringImpl<Class>((Class)Class.class, new QName[] { createXS("string") }) {
            @Override
            public Class parse(final CharSequence text) throws SAXException {
                TODO.checkSpec("JSR222 Issue #42");
                try {
                    final String name = WhiteSpaceProcessor.trim(text).toString();
                    ClassLoader cl = UnmarshallingContext.getInstance().classLoader;
                    if (cl == null) {
                        cl = Thread.currentThread().getContextClassLoader();
                    }
                    if (cl != null) {
                        return cl.loadClass(name);
                    }
                    return Class.forName(name);
                }
                catch (final ClassNotFoundException e) {
                    UnmarshallingContext.getInstance().handleError(e);
                    return null;
                }
            }
            
            @Override
            public String print(final Class v) {
                return v.getName();
            }
        });
        secondaryList.add(new PcdataImpl<Image>((Class)Image.class, new QName[] { createXS("base64Binary") }) {
            @Override
            public Image parse(final CharSequence text) throws SAXException {
                try {
                    InputStream is;
                    if (text instanceof Base64Data) {
                        is = ((Base64Data)text).getInputStream();
                    }
                    else {
                        is = new ByteArrayInputStream(decodeBase64(text));
                    }
                    try {
                        return ImageIO.read(is);
                    }
                    finally {
                        is.close();
                    }
                }
                catch (final IOException e) {
                    UnmarshallingContext.getInstance().handleError(e);
                    return null;
                }
            }
            
            private BufferedImage convertToBufferedImage(final Image image) throws IOException {
                if (image instanceof BufferedImage) {
                    return (BufferedImage)image;
                }
                final MediaTracker tracker = new MediaTracker(new Component() {});
                tracker.addImage(image, 0);
                try {
                    tracker.waitForAll();
                }
                catch (final InterruptedException e) {
                    throw new IOException(e.getMessage());
                }
                final BufferedImage bufImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
                final Graphics g = bufImage.createGraphics();
                g.drawImage(image, 0, 0, null);
                return bufImage;
            }
            
            @Override
            public Base64Data print(final Image v) {
                final ByteArrayOutputStreamEx imageData = new ByteArrayOutputStreamEx();
                final XMLSerializer xs = XMLSerializer.getInstance();
                String mimeType = xs.getXMIMEContentType();
                if (mimeType == null || mimeType.startsWith("image/*")) {
                    mimeType = "image/png";
                }
                try {
                    final Iterator<ImageWriter> itr = ImageIO.getImageWritersByMIMEType(mimeType);
                    if (!itr.hasNext()) {
                        xs.handleEvent(new ValidationEventImpl(1, Messages.NO_IMAGE_WRITER.format(mimeType), xs.getCurrentLocation(null)));
                        throw new RuntimeException("no encoder for MIME type " + mimeType);
                    }
                    final ImageWriter w = itr.next();
                    final ImageOutputStream os = ImageIO.createImageOutputStream(imageData);
                    w.setOutput(os);
                    w.write(this.convertToBufferedImage(v));
                    os.close();
                    w.dispose();
                }
                catch (final IOException e) {
                    xs.handleError(e);
                    throw new RuntimeException(e);
                }
                final Base64Data bd = new Base64Data();
                imageData.set(bd, mimeType);
                return bd;
            }
        });
        secondaryList.add(new PcdataImpl<DataHandler>((Class)DataHandler.class, new QName[] { createXS("base64Binary") }) {
            @Override
            public DataHandler parse(final CharSequence text) {
                if (text instanceof Base64Data) {
                    return ((Base64Data)text).getDataHandler();
                }
                return new DataHandler(new ByteArrayDataSource(decodeBase64(text), UnmarshallingContext.getInstance().getXMIMEContentType()));
            }
            
            @Override
            public Base64Data print(final DataHandler v) {
                final Base64Data bd = new Base64Data();
                bd.set(v);
                return bd;
            }
        });
        secondaryList.add(new PcdataImpl<Source>((Class)Source.class, new QName[] { createXS("base64Binary") }) {
            @Override
            public Source parse(final CharSequence text) throws SAXException {
                try {
                    if (text instanceof Base64Data) {
                        return new DataSourceSource(((Base64Data)text).getDataHandler());
                    }
                    return new DataSourceSource(new ByteArrayDataSource(decodeBase64(text), UnmarshallingContext.getInstance().getXMIMEContentType()));
                }
                catch (final MimeTypeParseException e) {
                    UnmarshallingContext.getInstance().handleError(e);
                    return null;
                }
            }
            
            @Override
            public Base64Data print(final Source v) {
                final XMLSerializer xs = XMLSerializer.getInstance();
                final Base64Data bd = new Base64Data();
                final String contentType = xs.getXMIMEContentType();
                MimeType mt = null;
                if (contentType != null) {
                    try {
                        mt = new MimeType(contentType);
                    }
                    catch (final MimeTypeParseException e) {
                        xs.handleError(e);
                    }
                }
                if (v instanceof DataSourceSource) {
                    final DataSource ds = ((DataSourceSource)v).getDataSource();
                    final String dsct = ds.getContentType();
                    if (dsct != null && (contentType == null || contentType.equals(dsct))) {
                        bd.set(new DataHandler(ds));
                        return bd;
                    }
                }
                String charset = null;
                if (mt != null) {
                    charset = mt.getParameter("charset");
                }
                if (charset == null) {
                    charset = "UTF-8";
                }
                try {
                    final ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx();
                    final Transformer tr = xs.getIdentityTransformer();
                    final String defaultEncoding = tr.getOutputProperty("encoding");
                    tr.setOutputProperty("encoding", charset);
                    tr.transform(v, new StreamResult(new OutputStreamWriter(baos, charset)));
                    tr.setOutputProperty("encoding", defaultEncoding);
                    baos.set(bd, "application/xml; charset=" + charset);
                    return bd;
                }
                catch (final TransformerException e2) {
                    xs.handleError(e2);
                }
                catch (final UnsupportedEncodingException e3) {
                    xs.handleError(e3);
                }
                bd.set(new byte[0], "application/xml");
                return bd;
            }
        });
        secondaryList.add(new StringImpl<XMLGregorianCalendar>((Class)XMLGregorianCalendar.class, new QName[] { createXS("anySimpleType"), DatatypeConstants.DATE, DatatypeConstants.DATETIME, DatatypeConstants.TIME, DatatypeConstants.GMONTH, DatatypeConstants.GDAY, DatatypeConstants.GYEAR, DatatypeConstants.GYEARMONTH, DatatypeConstants.GMONTHDAY }) {
            @Override
            public String print(final XMLGregorianCalendar cal) {
                final XMLSerializer xs = XMLSerializer.getInstance();
                final QName type = xs.getSchemaType();
                if (type != null) {
                    try {
                        checkXmlGregorianCalendarFieldRef(type, cal);
                        final String format = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFormatString.get(type);
                        if (format != null) {
                            return this.format(format, cal);
                        }
                    }
                    catch (final MarshalException e) {
                        xs.handleEvent(new ValidationEventImpl(0, e.getMessage(), xs.getCurrentLocation(null)));
                        return "";
                    }
                }
                return cal.toXMLFormat();
            }
            
            @Override
            public XMLGregorianCalendar parse(final CharSequence lexical) throws SAXException {
                try {
                    return DatatypeConverterImpl.getDatatypeFactory().newXMLGregorianCalendar(lexical.toString().trim());
                }
                catch (final Exception e) {
                    UnmarshallingContext.getInstance().handleError(e);
                    return null;
                }
            }
            
            private String format(final String format, final XMLGregorianCalendar value) {
                final StringBuilder buf = new StringBuilder();
                int fidx = 0;
                final int flen = format.length();
                while (fidx < flen) {
                    final char fch = format.charAt(fidx++);
                    if (fch != '%') {
                        buf.append(fch);
                    }
                    else {
                        switch (format.charAt(fidx++)) {
                            case 'Y': {
                                this.printNumber(buf, value.getEonAndYear(), 4);
                                continue;
                            }
                            case 'M': {
                                this.printNumber(buf, value.getMonth(), 2);
                                continue;
                            }
                            case 'D': {
                                this.printNumber(buf, value.getDay(), 2);
                                continue;
                            }
                            case 'h': {
                                this.printNumber(buf, value.getHour(), 2);
                                continue;
                            }
                            case 'm': {
                                this.printNumber(buf, value.getMinute(), 2);
                                continue;
                            }
                            case 's': {
                                this.printNumber(buf, value.getSecond(), 2);
                                if (value.getFractionalSecond() != null) {
                                    final String frac = value.getFractionalSecond().toPlainString();
                                    buf.append(frac.substring(1, frac.length()));
                                    continue;
                                }
                                continue;
                            }
                            case 'z': {
                                int offset = value.getTimezone();
                                if (offset == 0) {
                                    buf.append('Z');
                                    continue;
                                }
                                if (offset != Integer.MIN_VALUE) {
                                    if (offset < 0) {
                                        buf.append('-');
                                        offset *= -1;
                                    }
                                    else {
                                        buf.append('+');
                                    }
                                    this.printNumber(buf, offset / 60, 2);
                                    buf.append(':');
                                    this.printNumber(buf, offset % 60, 2);
                                    continue;
                                }
                                continue;
                            }
                            default: {
                                throw new InternalError();
                            }
                        }
                    }
                }
                return buf.toString();
            }
            
            private void printNumber(final StringBuilder out, final BigInteger number, final int nDigits) {
                final String s = number.toString();
                for (int i = s.length(); i < nDigits; ++i) {
                    out.append('0');
                }
                out.append(s);
            }
            
            private void printNumber(final StringBuilder out, final int number, final int nDigits) {
                final String s = String.valueOf(number);
                for (int i = s.length(); i < nDigits; ++i) {
                    out.append('0');
                }
                out.append(s);
            }
            
            @Override
            public QName getTypeName(final XMLGregorianCalendar cal) {
                return cal.getXMLSchemaType();
            }
        });
        final ArrayList<RuntimeBuiltinLeafInfoImpl<?>> primaryList = new ArrayList<RuntimeBuiltinLeafInfoImpl<?>>();
        primaryList.add(RuntimeBuiltinLeafInfoImpl.STRING);
        primaryList.add(new StringImpl<Boolean>((Class)Boolean.class, new QName[] { createXS("boolean") }) {
            @Override
            public Boolean parse(final CharSequence text) {
                return DatatypeConverterImpl._parseBoolean(text);
            }
            
            @Override
            public String print(final Boolean v) {
                return v.toString();
            }
        });
        primaryList.add(new PcdataImpl<byte[]>((Class)byte[].class, new QName[] { createXS("base64Binary"), createXS("hexBinary") }) {
            @Override
            public byte[] parse(final CharSequence text) {
                return decodeBase64(text);
            }
            
            @Override
            public Base64Data print(final byte[] v) {
                final XMLSerializer w = XMLSerializer.getInstance();
                final Base64Data bd = new Base64Data();
                final String mimeType = w.getXMIMEContentType();
                bd.set(v, mimeType);
                return bd;
            }
        });
        primaryList.add(new StringImpl<Byte>((Class)Byte.class, new QName[] { createXS("byte") }) {
            @Override
            public Byte parse(final CharSequence text) {
                return DatatypeConverterImpl._parseByte(text);
            }
            
            @Override
            public String print(final Byte v) {
                return DatatypeConverterImpl._printByte(v);
            }
        });
        primaryList.add(new StringImpl<Short>((Class)Short.class, new QName[] { createXS("short"), createXS("unsignedByte") }) {
            @Override
            public Short parse(final CharSequence text) {
                return DatatypeConverterImpl._parseShort(text);
            }
            
            @Override
            public String print(final Short v) {
                return DatatypeConverterImpl._printShort(v);
            }
        });
        primaryList.add(new StringImpl<Integer>((Class)Integer.class, new QName[] { createXS("int"), createXS("unsignedShort") }) {
            @Override
            public Integer parse(final CharSequence text) {
                return DatatypeConverterImpl._parseInt(text);
            }
            
            @Override
            public String print(final Integer v) {
                return DatatypeConverterImpl._printInt(v);
            }
        });
        primaryList.add(new StringImpl<Long>((Class)Long.class, new QName[] { createXS("long"), createXS("unsignedInt") }) {
            @Override
            public Long parse(final CharSequence text) {
                return DatatypeConverterImpl._parseLong(text);
            }
            
            @Override
            public String print(final Long v) {
                return DatatypeConverterImpl._printLong(v);
            }
        });
        primaryList.add(new StringImpl<Float>((Class)Float.class, new QName[] { createXS("float") }) {
            @Override
            public Float parse(final CharSequence text) {
                return DatatypeConverterImpl._parseFloat(text.toString());
            }
            
            @Override
            public String print(final Float v) {
                return DatatypeConverterImpl._printFloat(v);
            }
        });
        primaryList.add(new StringImpl<Double>((Class)Double.class, new QName[] { createXS("double") }) {
            @Override
            public Double parse(final CharSequence text) {
                return DatatypeConverterImpl._parseDouble(text);
            }
            
            @Override
            public String print(final Double v) {
                return DatatypeConverterImpl._printDouble(v);
            }
        });
        primaryList.add(new StringImpl<BigInteger>((Class)BigInteger.class, new QName[] { createXS("integer"), createXS("positiveInteger"), createXS("negativeInteger"), createXS("nonPositiveInteger"), createXS("nonNegativeInteger"), createXS("unsignedLong") }) {
            @Override
            public BigInteger parse(final CharSequence text) {
                return DatatypeConverterImpl._parseInteger(text);
            }
            
            @Override
            public String print(final BigInteger v) {
                return DatatypeConverterImpl._printInteger(v);
            }
        });
        primaryList.add(new StringImpl<BigDecimal>((Class)BigDecimal.class, new QName[] { createXS("decimal") }) {
            @Override
            public BigDecimal parse(final CharSequence text) {
                return DatatypeConverterImpl._parseDecimal(text.toString());
            }
            
            @Override
            public String print(final BigDecimal v) {
                return DatatypeConverterImpl._printDecimal(v);
            }
        });
        primaryList.add(new StringImpl<QName>((Class)QName.class, new QName[] { createXS("QName") }) {
            @Override
            public QName parse(final CharSequence text) throws SAXException {
                try {
                    return DatatypeConverterImpl._parseQName(text.toString(), UnmarshallingContext.getInstance());
                }
                catch (final IllegalArgumentException e) {
                    UnmarshallingContext.getInstance().handleError(e);
                    return null;
                }
            }
            
            @Override
            public String print(final QName v) {
                return DatatypeConverterImpl._printQName(v, XMLSerializer.getInstance().getNamespaceContext());
            }
            
            @Override
            public boolean useNamespace() {
                return true;
            }
            
            @Override
            public void declareNamespace(final QName v, final XMLSerializer w) {
                w.getNamespaceContext().declareNamespace(v.getNamespaceURI(), v.getPrefix(), false);
            }
        });
        if (MAP_ANYURI_TO_URI_VALUE != null) {
            primaryList.add(new StringImpl<URI>((Class)URI.class, new QName[] { createXS("anyURI") }) {
                @Override
                public URI parse(final CharSequence text) throws SAXException {
                    try {
                        return new URI(text.toString());
                    }
                    catch (final URISyntaxException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                @Override
                public String print(final URI v) {
                    return v.toString();
                }
            });
        }
        primaryList.add(new StringImpl<Duration>((Class)Duration.class, new QName[] { createXS("duration") }) {
            @Override
            public String print(final Duration duration) {
                return duration.toString();
            }
            
            @Override
            public Duration parse(final CharSequence lexical) {
                TODO.checkSpec("JSR222 Issue #42");
                return DatatypeConverterImpl.getDatatypeFactory().newDuration(lexical.toString());
            }
        });
        primaryList.add(new StringImpl<Void>((Class)Void.class, new QName[0]) {
            @Override
            public String print(final Void value) {
                return "";
            }
            
            @Override
            public Void parse(final CharSequence lexical) {
                return null;
            }
        });
        final List<RuntimeBuiltinLeafInfoImpl<?>> l = new ArrayList<RuntimeBuiltinLeafInfoImpl<?>>(secondaryList.size() + primaryList.size() + 1);
        l.addAll(secondaryList);
        try {
            l.add(new UUIDImpl());
        }
        catch (final LinkageError linkageError) {}
        l.addAll(primaryList);
        builtinBeanInfos = Collections.unmodifiableList((List<? extends RuntimeBuiltinLeafInfoImpl<?>>)l);
        xmlGregorianCalendarFormatString = new HashMap<QName, String>();
        final Map<QName, String> m = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFormatString;
        m.put(DatatypeConstants.DATETIME, "%Y-%M-%DT%h:%m:%s%z");
        m.put(DatatypeConstants.DATE, "%Y-%M-%D%z");
        m.put(DatatypeConstants.TIME, "%h:%m:%s%z");
        m.put(DatatypeConstants.GMONTH, "--%M--%z");
        m.put(DatatypeConstants.GDAY, "---%D%z");
        m.put(DatatypeConstants.GYEAR, "%Y%z");
        m.put(DatatypeConstants.GYEARMONTH, "%Y-%M%z");
        m.put(DatatypeConstants.GMONTHDAY, "--%M-%D%z");
        xmlGregorianCalendarFieldRef = new HashMap<QName, Integer>();
        final Map<QName, Integer> f = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFieldRef;
        f.put(DatatypeConstants.DATETIME, 17895697);
        f.put(DatatypeConstants.DATE, 17895424);
        f.put(DatatypeConstants.TIME, 16777489);
        f.put(DatatypeConstants.GDAY, 16781312);
        f.put(DatatypeConstants.GMONTH, 16842752);
        f.put(DatatypeConstants.GYEAR, 17825792);
        f.put(DatatypeConstants.GYEARMONTH, 17891328);
        f.put(DatatypeConstants.GMONTHDAY, 16846848);
    }
    
    private abstract static class StringImpl<T> extends RuntimeBuiltinLeafInfoImpl<T>
    {
        protected StringImpl(final Class type, final QName... typeNames) {
            super(type, typeNames, null);
        }
        
        @Override
        public abstract String print(final T p0) throws AccessorException;
        
        @Override
        public void writeText(final XMLSerializer w, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.text(this.print(o), fieldName);
        }
        
        @Override
        public void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.leafElement(tagName, this.print(o), fieldName);
        }
    }
    
    private abstract static class PcdataImpl<T> extends RuntimeBuiltinLeafInfoImpl<T>
    {
        protected PcdataImpl(final Class type, final QName... typeNames) {
            super(type, typeNames, null);
        }
        
        @Override
        public abstract Pcdata print(final T p0) throws AccessorException;
        
        @Override
        public final void writeText(final XMLSerializer w, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.text(this.print(o), fieldName);
        }
        
        @Override
        public final void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.leafElement(tagName, this.print(o), fieldName);
        }
    }
    
    private static class UUIDImpl extends StringImpl<UUID>
    {
        public UUIDImpl() {
            super(UUID.class, new QName[] { createXS("string") });
        }
        
        @Override
        public UUID parse(final CharSequence text) throws SAXException {
            TODO.checkSpec("JSR222 Issue #42");
            try {
                return UUID.fromString(WhiteSpaceProcessor.trim(text).toString());
            }
            catch (final IllegalArgumentException e) {
                UnmarshallingContext.getInstance().handleError(e);
                return null;
            }
        }
        
        @Override
        public String print(final UUID v) {
            return v.toString();
        }
    }
    
    private static class StringImplImpl extends StringImpl<String>
    {
        public StringImplImpl(final Class type, final QName[] typeNames) {
            super(type, typeNames);
        }
        
        @Override
        public String parse(final CharSequence text) {
            return text.toString();
        }
        
        @Override
        public String print(final String s) {
            return s;
        }
        
        @Override
        public final void writeText(final XMLSerializer w, final String o, final String fieldName) throws IOException, SAXException, XMLStreamException {
            w.text(o, fieldName);
        }
        
        @Override
        public final void writeLeafElement(final XMLSerializer w, final Name tagName, final String o, final String fieldName) throws IOException, SAXException, XMLStreamException {
            w.leafElement(tagName, o, fieldName);
        }
    }
}

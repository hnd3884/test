package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;

public interface STScatterStyle extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STScatterStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stscatterstyle9eb9type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum LINE = Enum.forString("line");
    public static final Enum LINE_MARKER = Enum.forString("lineMarker");
    public static final Enum MARKER = Enum.forString("marker");
    public static final Enum SMOOTH = Enum.forString("smooth");
    public static final Enum SMOOTH_MARKER = Enum.forString("smoothMarker");
    public static final int INT_NONE = 1;
    public static final int INT_LINE = 2;
    public static final int INT_LINE_MARKER = 3;
    public static final int INT_MARKER = 4;
    public static final int INT_SMOOTH = 5;
    public static final int INT_SMOOTH_MARKER = 6;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STScatterStyle newValue(final Object o) {
            return (STScatterStyle)STScatterStyle.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STScatterStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STScatterStyle newInstance() {
            return (STScatterStyle)getTypeLoader().newInstance(STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle newInstance(final XmlOptions xmlOptions) {
            return (STScatterStyle)getTypeLoader().newInstance(STScatterStyle.type, xmlOptions);
        }
        
        public static STScatterStyle parse(final String s) throws XmlException {
            return (STScatterStyle)getTypeLoader().parse(s, STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STScatterStyle)getTypeLoader().parse(s, STScatterStyle.type, xmlOptions);
        }
        
        public static STScatterStyle parse(final File file) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(file, STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(file, STScatterStyle.type, xmlOptions);
        }
        
        public static STScatterStyle parse(final URL url) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(url, STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(url, STScatterStyle.type, xmlOptions);
        }
        
        public static STScatterStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(inputStream, STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(inputStream, STScatterStyle.type, xmlOptions);
        }
        
        public static STScatterStyle parse(final Reader reader) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(reader, STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STScatterStyle)getTypeLoader().parse(reader, STScatterStyle.type, xmlOptions);
        }
        
        public static STScatterStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STScatterStyle)getTypeLoader().parse(xmlStreamReader, STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STScatterStyle)getTypeLoader().parse(xmlStreamReader, STScatterStyle.type, xmlOptions);
        }
        
        public static STScatterStyle parse(final Node node) throws XmlException {
            return (STScatterStyle)getTypeLoader().parse(node, STScatterStyle.type, (XmlOptions)null);
        }
        
        public static STScatterStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STScatterStyle)getTypeLoader().parse(node, STScatterStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static STScatterStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STScatterStyle)getTypeLoader().parse(xmlInputStream, STScatterStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STScatterStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STScatterStyle)getTypeLoader().parse(xmlInputStream, STScatterStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STScatterStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STScatterStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_LINE = 2;
        static final int INT_LINE_MARKER = 3;
        static final int INT_MARKER = 4;
        static final int INT_SMOOTH = 5;
        static final int INT_SMOOTH_MARKER = 6;
        public static final StringEnumAbstractBase.Table table;
        private static final long serialVersionUID = 1L;
        
        public static Enum forString(final String s) {
            return (Enum)Enum.table.forString(s);
        }
        
        public static Enum forInt(final int n) {
            return (Enum)Enum.table.forInt(n);
        }
        
        private Enum(final String s, final int n) {
            super(s, n);
        }
        
        private Object readResolve() {
            return forInt(this.intValue());
        }
        
        static {
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("line", 2), new Enum("lineMarker", 3), new Enum("marker", 4), new Enum("smooth", 5), new Enum("smoothMarker", 6) });
        }
    }
}

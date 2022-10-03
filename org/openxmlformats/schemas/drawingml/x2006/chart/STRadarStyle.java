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

public interface STRadarStyle extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STRadarStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stradarstyle3dc1type");
    public static final Enum STANDARD = Enum.forString("standard");
    public static final Enum MARKER = Enum.forString("marker");
    public static final Enum FILLED = Enum.forString("filled");
    public static final int INT_STANDARD = 1;
    public static final int INT_MARKER = 2;
    public static final int INT_FILLED = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STRadarStyle newValue(final Object o) {
            return (STRadarStyle)STRadarStyle.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STRadarStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STRadarStyle newInstance() {
            return (STRadarStyle)getTypeLoader().newInstance(STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle newInstance(final XmlOptions xmlOptions) {
            return (STRadarStyle)getTypeLoader().newInstance(STRadarStyle.type, xmlOptions);
        }
        
        public static STRadarStyle parse(final String s) throws XmlException {
            return (STRadarStyle)getTypeLoader().parse(s, STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STRadarStyle)getTypeLoader().parse(s, STRadarStyle.type, xmlOptions);
        }
        
        public static STRadarStyle parse(final File file) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(file, STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(file, STRadarStyle.type, xmlOptions);
        }
        
        public static STRadarStyle parse(final URL url) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(url, STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(url, STRadarStyle.type, xmlOptions);
        }
        
        public static STRadarStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(inputStream, STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(inputStream, STRadarStyle.type, xmlOptions);
        }
        
        public static STRadarStyle parse(final Reader reader) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(reader, STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRadarStyle)getTypeLoader().parse(reader, STRadarStyle.type, xmlOptions);
        }
        
        public static STRadarStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STRadarStyle)getTypeLoader().parse(xmlStreamReader, STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STRadarStyle)getTypeLoader().parse(xmlStreamReader, STRadarStyle.type, xmlOptions);
        }
        
        public static STRadarStyle parse(final Node node) throws XmlException {
            return (STRadarStyle)getTypeLoader().parse(node, STRadarStyle.type, (XmlOptions)null);
        }
        
        public static STRadarStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STRadarStyle)getTypeLoader().parse(node, STRadarStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static STRadarStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STRadarStyle)getTypeLoader().parse(xmlInputStream, STRadarStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STRadarStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STRadarStyle)getTypeLoader().parse(xmlInputStream, STRadarStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRadarStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRadarStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_STANDARD = 1;
        static final int INT_MARKER = 2;
        static final int INT_FILLED = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("standard", 1), new Enum("marker", 2), new Enum("filled", 3) });
        }
    }
}

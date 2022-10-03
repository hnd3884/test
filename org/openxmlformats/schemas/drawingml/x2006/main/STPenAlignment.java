package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.XmlToken;

public interface STPenAlignment extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPenAlignment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpenalignmentd775type");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum IN = Enum.forString("in");
    public static final int INT_CTR = 1;
    public static final int INT_IN = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPenAlignment newValue(final Object o) {
            return (STPenAlignment)STPenAlignment.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPenAlignment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPenAlignment newInstance() {
            return (STPenAlignment)getTypeLoader().newInstance(STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment newInstance(final XmlOptions xmlOptions) {
            return (STPenAlignment)getTypeLoader().newInstance(STPenAlignment.type, xmlOptions);
        }
        
        public static STPenAlignment parse(final String s) throws XmlException {
            return (STPenAlignment)getTypeLoader().parse(s, STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPenAlignment)getTypeLoader().parse(s, STPenAlignment.type, xmlOptions);
        }
        
        public static STPenAlignment parse(final File file) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(file, STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(file, STPenAlignment.type, xmlOptions);
        }
        
        public static STPenAlignment parse(final URL url) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(url, STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(url, STPenAlignment.type, xmlOptions);
        }
        
        public static STPenAlignment parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(inputStream, STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(inputStream, STPenAlignment.type, xmlOptions);
        }
        
        public static STPenAlignment parse(final Reader reader) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(reader, STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPenAlignment)getTypeLoader().parse(reader, STPenAlignment.type, xmlOptions);
        }
        
        public static STPenAlignment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPenAlignment)getTypeLoader().parse(xmlStreamReader, STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPenAlignment)getTypeLoader().parse(xmlStreamReader, STPenAlignment.type, xmlOptions);
        }
        
        public static STPenAlignment parse(final Node node) throws XmlException {
            return (STPenAlignment)getTypeLoader().parse(node, STPenAlignment.type, (XmlOptions)null);
        }
        
        public static STPenAlignment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPenAlignment)getTypeLoader().parse(node, STPenAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static STPenAlignment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPenAlignment)getTypeLoader().parse(xmlInputStream, STPenAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPenAlignment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPenAlignment)getTypeLoader().parse(xmlInputStream, STPenAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPenAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPenAlignment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CTR = 1;
        static final int INT_IN = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("ctr", 1), new Enum("in", 2) });
        }
    }
}

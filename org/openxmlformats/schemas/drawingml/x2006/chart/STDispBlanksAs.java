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

public interface STDispBlanksAs extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDispBlanksAs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdispblanksas3a59type");
    public static final Enum SPAN = Enum.forString("span");
    public static final Enum GAP = Enum.forString("gap");
    public static final Enum ZERO = Enum.forString("zero");
    public static final int INT_SPAN = 1;
    public static final int INT_GAP = 2;
    public static final int INT_ZERO = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDispBlanksAs newValue(final Object o) {
            return (STDispBlanksAs)STDispBlanksAs.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDispBlanksAs.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDispBlanksAs newInstance() {
            return (STDispBlanksAs)getTypeLoader().newInstance(STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs newInstance(final XmlOptions xmlOptions) {
            return (STDispBlanksAs)getTypeLoader().newInstance(STDispBlanksAs.type, xmlOptions);
        }
        
        public static STDispBlanksAs parse(final String s) throws XmlException {
            return (STDispBlanksAs)getTypeLoader().parse(s, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDispBlanksAs)getTypeLoader().parse(s, STDispBlanksAs.type, xmlOptions);
        }
        
        public static STDispBlanksAs parse(final File file) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(file, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(file, STDispBlanksAs.type, xmlOptions);
        }
        
        public static STDispBlanksAs parse(final URL url) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(url, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(url, STDispBlanksAs.type, xmlOptions);
        }
        
        public static STDispBlanksAs parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(inputStream, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(inputStream, STDispBlanksAs.type, xmlOptions);
        }
        
        public static STDispBlanksAs parse(final Reader reader) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(reader, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDispBlanksAs)getTypeLoader().parse(reader, STDispBlanksAs.type, xmlOptions);
        }
        
        public static STDispBlanksAs parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDispBlanksAs)getTypeLoader().parse(xmlStreamReader, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDispBlanksAs)getTypeLoader().parse(xmlStreamReader, STDispBlanksAs.type, xmlOptions);
        }
        
        public static STDispBlanksAs parse(final Node node) throws XmlException {
            return (STDispBlanksAs)getTypeLoader().parse(node, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static STDispBlanksAs parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDispBlanksAs)getTypeLoader().parse(node, STDispBlanksAs.type, xmlOptions);
        }
        
        @Deprecated
        public static STDispBlanksAs parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDispBlanksAs)getTypeLoader().parse(xmlInputStream, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDispBlanksAs parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDispBlanksAs)getTypeLoader().parse(xmlInputStream, STDispBlanksAs.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDispBlanksAs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDispBlanksAs.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SPAN = 1;
        static final int INT_GAP = 2;
        static final int INT_ZERO = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("span", 1), new Enum("gap", 2), new Enum("zero", 3) });
        }
    }
}

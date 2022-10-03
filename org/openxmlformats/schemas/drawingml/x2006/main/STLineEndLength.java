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

public interface STLineEndLength extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLineEndLength.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlineendlength3f2etype");
    public static final Enum SM = Enum.forString("sm");
    public static final Enum MED = Enum.forString("med");
    public static final Enum LG = Enum.forString("lg");
    public static final int INT_SM = 1;
    public static final int INT_MED = 2;
    public static final int INT_LG = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLineEndLength newValue(final Object o) {
            return (STLineEndLength)STLineEndLength.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLineEndLength.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLineEndLength newInstance() {
            return (STLineEndLength)getTypeLoader().newInstance(STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength newInstance(final XmlOptions xmlOptions) {
            return (STLineEndLength)getTypeLoader().newInstance(STLineEndLength.type, xmlOptions);
        }
        
        public static STLineEndLength parse(final String s) throws XmlException {
            return (STLineEndLength)getTypeLoader().parse(s, STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLineEndLength)getTypeLoader().parse(s, STLineEndLength.type, xmlOptions);
        }
        
        public static STLineEndLength parse(final File file) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(file, STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(file, STLineEndLength.type, xmlOptions);
        }
        
        public static STLineEndLength parse(final URL url) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(url, STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(url, STLineEndLength.type, xmlOptions);
        }
        
        public static STLineEndLength parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(inputStream, STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(inputStream, STLineEndLength.type, xmlOptions);
        }
        
        public static STLineEndLength parse(final Reader reader) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(reader, STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndLength)getTypeLoader().parse(reader, STLineEndLength.type, xmlOptions);
        }
        
        public static STLineEndLength parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLineEndLength)getTypeLoader().parse(xmlStreamReader, STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLineEndLength)getTypeLoader().parse(xmlStreamReader, STLineEndLength.type, xmlOptions);
        }
        
        public static STLineEndLength parse(final Node node) throws XmlException {
            return (STLineEndLength)getTypeLoader().parse(node, STLineEndLength.type, (XmlOptions)null);
        }
        
        public static STLineEndLength parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLineEndLength)getTypeLoader().parse(node, STLineEndLength.type, xmlOptions);
        }
        
        @Deprecated
        public static STLineEndLength parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLineEndLength)getTypeLoader().parse(xmlInputStream, STLineEndLength.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLineEndLength parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLineEndLength)getTypeLoader().parse(xmlInputStream, STLineEndLength.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineEndLength.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineEndLength.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SM = 1;
        static final int INT_MED = 2;
        static final int INT_LG = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("sm", 1), new Enum("med", 2), new Enum("lg", 3) });
        }
    }
}

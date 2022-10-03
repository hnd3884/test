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

public interface STTextAnchoringType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextAnchoringType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextanchoringtyped99btype");
    public static final Enum T = Enum.forString("t");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum B = Enum.forString("b");
    public static final Enum JUST = Enum.forString("just");
    public static final Enum DIST = Enum.forString("dist");
    public static final int INT_T = 1;
    public static final int INT_CTR = 2;
    public static final int INT_B = 3;
    public static final int INT_JUST = 4;
    public static final int INT_DIST = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextAnchoringType newValue(final Object o) {
            return (STTextAnchoringType)STTextAnchoringType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextAnchoringType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextAnchoringType newInstance() {
            return (STTextAnchoringType)getTypeLoader().newInstance(STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType newInstance(final XmlOptions xmlOptions) {
            return (STTextAnchoringType)getTypeLoader().newInstance(STTextAnchoringType.type, xmlOptions);
        }
        
        public static STTextAnchoringType parse(final String s) throws XmlException {
            return (STTextAnchoringType)getTypeLoader().parse(s, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAnchoringType)getTypeLoader().parse(s, STTextAnchoringType.type, xmlOptions);
        }
        
        public static STTextAnchoringType parse(final File file) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(file, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(file, STTextAnchoringType.type, xmlOptions);
        }
        
        public static STTextAnchoringType parse(final URL url) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(url, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(url, STTextAnchoringType.type, xmlOptions);
        }
        
        public static STTextAnchoringType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(inputStream, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(inputStream, STTextAnchoringType.type, xmlOptions);
        }
        
        public static STTextAnchoringType parse(final Reader reader) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(reader, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAnchoringType)getTypeLoader().parse(reader, STTextAnchoringType.type, xmlOptions);
        }
        
        public static STTextAnchoringType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextAnchoringType)getTypeLoader().parse(xmlStreamReader, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAnchoringType)getTypeLoader().parse(xmlStreamReader, STTextAnchoringType.type, xmlOptions);
        }
        
        public static STTextAnchoringType parse(final Node node) throws XmlException {
            return (STTextAnchoringType)getTypeLoader().parse(node, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        public static STTextAnchoringType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAnchoringType)getTypeLoader().parse(node, STTextAnchoringType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextAnchoringType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextAnchoringType)getTypeLoader().parse(xmlInputStream, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextAnchoringType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextAnchoringType)getTypeLoader().parse(xmlInputStream, STTextAnchoringType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAnchoringType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAnchoringType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_T = 1;
        static final int INT_CTR = 2;
        static final int INT_B = 3;
        static final int INT_JUST = 4;
        static final int INT_DIST = 5;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("t", 1), new Enum("ctr", 2), new Enum("b", 3), new Enum("just", 4), new Enum("dist", 5) });
        }
    }
}

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

public interface STLblAlgn extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLblAlgn.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlblalgn934etype");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum L = Enum.forString("l");
    public static final Enum R = Enum.forString("r");
    public static final int INT_CTR = 1;
    public static final int INT_L = 2;
    public static final int INT_R = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLblAlgn newValue(final Object o) {
            return (STLblAlgn)STLblAlgn.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLblAlgn.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLblAlgn newInstance() {
            return (STLblAlgn)getTypeLoader().newInstance(STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn newInstance(final XmlOptions xmlOptions) {
            return (STLblAlgn)getTypeLoader().newInstance(STLblAlgn.type, xmlOptions);
        }
        
        public static STLblAlgn parse(final String s) throws XmlException {
            return (STLblAlgn)getTypeLoader().parse(s, STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLblAlgn)getTypeLoader().parse(s, STLblAlgn.type, xmlOptions);
        }
        
        public static STLblAlgn parse(final File file) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(file, STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(file, STLblAlgn.type, xmlOptions);
        }
        
        public static STLblAlgn parse(final URL url) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(url, STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(url, STLblAlgn.type, xmlOptions);
        }
        
        public static STLblAlgn parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(inputStream, STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(inputStream, STLblAlgn.type, xmlOptions);
        }
        
        public static STLblAlgn parse(final Reader reader) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(reader, STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLblAlgn)getTypeLoader().parse(reader, STLblAlgn.type, xmlOptions);
        }
        
        public static STLblAlgn parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLblAlgn)getTypeLoader().parse(xmlStreamReader, STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLblAlgn)getTypeLoader().parse(xmlStreamReader, STLblAlgn.type, xmlOptions);
        }
        
        public static STLblAlgn parse(final Node node) throws XmlException {
            return (STLblAlgn)getTypeLoader().parse(node, STLblAlgn.type, (XmlOptions)null);
        }
        
        public static STLblAlgn parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLblAlgn)getTypeLoader().parse(node, STLblAlgn.type, xmlOptions);
        }
        
        @Deprecated
        public static STLblAlgn parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLblAlgn)getTypeLoader().parse(xmlInputStream, STLblAlgn.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLblAlgn parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLblAlgn)getTypeLoader().parse(xmlInputStream, STLblAlgn.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLblAlgn.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLblAlgn.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CTR = 1;
        static final int INT_L = 2;
        static final int INT_R = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("ctr", 1), new Enum("l", 2), new Enum("r", 3) });
        }
    }
}

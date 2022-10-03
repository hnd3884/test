package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface STMerge extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STMerge.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stmerge50aatype");
    public static final Enum CONTINUE = Enum.forString("continue");
    public static final Enum RESTART = Enum.forString("restart");
    public static final int INT_CONTINUE = 1;
    public static final int INT_RESTART = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STMerge newValue(final Object o) {
            return (STMerge)STMerge.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STMerge.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STMerge newInstance() {
            return (STMerge)getTypeLoader().newInstance(STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge newInstance(final XmlOptions xmlOptions) {
            return (STMerge)getTypeLoader().newInstance(STMerge.type, xmlOptions);
        }
        
        public static STMerge parse(final String s) throws XmlException {
            return (STMerge)getTypeLoader().parse(s, STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STMerge)getTypeLoader().parse(s, STMerge.type, xmlOptions);
        }
        
        public static STMerge parse(final File file) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(file, STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(file, STMerge.type, xmlOptions);
        }
        
        public static STMerge parse(final URL url) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(url, STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(url, STMerge.type, xmlOptions);
        }
        
        public static STMerge parse(final InputStream inputStream) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(inputStream, STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(inputStream, STMerge.type, xmlOptions);
        }
        
        public static STMerge parse(final Reader reader) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(reader, STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMerge)getTypeLoader().parse(reader, STMerge.type, xmlOptions);
        }
        
        public static STMerge parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STMerge)getTypeLoader().parse(xmlStreamReader, STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STMerge)getTypeLoader().parse(xmlStreamReader, STMerge.type, xmlOptions);
        }
        
        public static STMerge parse(final Node node) throws XmlException {
            return (STMerge)getTypeLoader().parse(node, STMerge.type, (XmlOptions)null);
        }
        
        public static STMerge parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STMerge)getTypeLoader().parse(node, STMerge.type, xmlOptions);
        }
        
        @Deprecated
        public static STMerge parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STMerge)getTypeLoader().parse(xmlInputStream, STMerge.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STMerge parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STMerge)getTypeLoader().parse(xmlInputStream, STMerge.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STMerge.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STMerge.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CONTINUE = 1;
        static final int INT_RESTART = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("continue", 1), new Enum("restart", 2) });
        }
    }
}

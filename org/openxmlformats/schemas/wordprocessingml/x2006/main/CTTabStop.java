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
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTabStop extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTabStop.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttabstop5ebbtype");
    
    STTabJc.Enum getVal();
    
    STTabJc xgetVal();
    
    void setVal(final STTabJc.Enum p0);
    
    void xsetVal(final STTabJc p0);
    
    STTabTlc.Enum getLeader();
    
    STTabTlc xgetLeader();
    
    boolean isSetLeader();
    
    void setLeader(final STTabTlc.Enum p0);
    
    void xsetLeader(final STTabTlc p0);
    
    void unsetLeader();
    
    BigInteger getPos();
    
    STSignedTwipsMeasure xgetPos();
    
    void setPos(final BigInteger p0);
    
    void xsetPos(final STSignedTwipsMeasure p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTabStop.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTabStop newInstance() {
            return (CTTabStop)getTypeLoader().newInstance(CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop newInstance(final XmlOptions xmlOptions) {
            return (CTTabStop)getTypeLoader().newInstance(CTTabStop.type, xmlOptions);
        }
        
        public static CTTabStop parse(final String s) throws XmlException {
            return (CTTabStop)getTypeLoader().parse(s, CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTabStop)getTypeLoader().parse(s, CTTabStop.type, xmlOptions);
        }
        
        public static CTTabStop parse(final File file) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(file, CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(file, CTTabStop.type, xmlOptions);
        }
        
        public static CTTabStop parse(final URL url) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(url, CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(url, CTTabStop.type, xmlOptions);
        }
        
        public static CTTabStop parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(inputStream, CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(inputStream, CTTabStop.type, xmlOptions);
        }
        
        public static CTTabStop parse(final Reader reader) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(reader, CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabStop)getTypeLoader().parse(reader, CTTabStop.type, xmlOptions);
        }
        
        public static CTTabStop parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTabStop)getTypeLoader().parse(xmlStreamReader, CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTabStop)getTypeLoader().parse(xmlStreamReader, CTTabStop.type, xmlOptions);
        }
        
        public static CTTabStop parse(final Node node) throws XmlException {
            return (CTTabStop)getTypeLoader().parse(node, CTTabStop.type, (XmlOptions)null);
        }
        
        public static CTTabStop parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTabStop)getTypeLoader().parse(node, CTTabStop.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTabStop parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTabStop)getTypeLoader().parse(xmlInputStream, CTTabStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTabStop parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTabStop)getTypeLoader().parse(xmlInputStream, CTTabStop.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTabStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTabStop.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

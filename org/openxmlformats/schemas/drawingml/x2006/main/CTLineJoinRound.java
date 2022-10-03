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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLineJoinRound extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLineJoinRound.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlinejoinround7be1type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLineJoinRound.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLineJoinRound newInstance() {
            return (CTLineJoinRound)getTypeLoader().newInstance(CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound newInstance(final XmlOptions xmlOptions) {
            return (CTLineJoinRound)getTypeLoader().newInstance(CTLineJoinRound.type, xmlOptions);
        }
        
        public static CTLineJoinRound parse(final String s) throws XmlException {
            return (CTLineJoinRound)getTypeLoader().parse(s, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineJoinRound)getTypeLoader().parse(s, CTLineJoinRound.type, xmlOptions);
        }
        
        public static CTLineJoinRound parse(final File file) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(file, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(file, CTLineJoinRound.type, xmlOptions);
        }
        
        public static CTLineJoinRound parse(final URL url) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(url, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(url, CTLineJoinRound.type, xmlOptions);
        }
        
        public static CTLineJoinRound parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(inputStream, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(inputStream, CTLineJoinRound.type, xmlOptions);
        }
        
        public static CTLineJoinRound parse(final Reader reader) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(reader, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinRound)getTypeLoader().parse(reader, CTLineJoinRound.type, xmlOptions);
        }
        
        public static CTLineJoinRound parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLineJoinRound)getTypeLoader().parse(xmlStreamReader, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineJoinRound)getTypeLoader().parse(xmlStreamReader, CTLineJoinRound.type, xmlOptions);
        }
        
        public static CTLineJoinRound parse(final Node node) throws XmlException {
            return (CTLineJoinRound)getTypeLoader().parse(node, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        public static CTLineJoinRound parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineJoinRound)getTypeLoader().parse(node, CTLineJoinRound.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLineJoinRound parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLineJoinRound)getTypeLoader().parse(xmlInputStream, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLineJoinRound parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLineJoinRound)getTypeLoader().parse(xmlInputStream, CTLineJoinRound.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineJoinRound.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineJoinRound.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNumPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumpr16aatype");
    
    CTDecimalNumber getIlvl();
    
    boolean isSetIlvl();
    
    void setIlvl(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewIlvl();
    
    void unsetIlvl();
    
    CTDecimalNumber getNumId();
    
    boolean isSetNumId();
    
    void setNumId(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewNumId();
    
    void unsetNumId();
    
    CTTrackChangeNumbering getNumberingChange();
    
    boolean isSetNumberingChange();
    
    void setNumberingChange(final CTTrackChangeNumbering p0);
    
    CTTrackChangeNumbering addNewNumberingChange();
    
    void unsetNumberingChange();
    
    CTTrackChange getIns();
    
    boolean isSetIns();
    
    void setIns(final CTTrackChange p0);
    
    CTTrackChange addNewIns();
    
    void unsetIns();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumPr newInstance() {
            return (CTNumPr)getTypeLoader().newInstance(CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr newInstance(final XmlOptions xmlOptions) {
            return (CTNumPr)getTypeLoader().newInstance(CTNumPr.type, xmlOptions);
        }
        
        public static CTNumPr parse(final String s) throws XmlException {
            return (CTNumPr)getTypeLoader().parse(s, CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumPr)getTypeLoader().parse(s, CTNumPr.type, xmlOptions);
        }
        
        public static CTNumPr parse(final File file) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(file, CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(file, CTNumPr.type, xmlOptions);
        }
        
        public static CTNumPr parse(final URL url) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(url, CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(url, CTNumPr.type, xmlOptions);
        }
        
        public static CTNumPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(inputStream, CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(inputStream, CTNumPr.type, xmlOptions);
        }
        
        public static CTNumPr parse(final Reader reader) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(reader, CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumPr)getTypeLoader().parse(reader, CTNumPr.type, xmlOptions);
        }
        
        public static CTNumPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumPr)getTypeLoader().parse(xmlStreamReader, CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumPr)getTypeLoader().parse(xmlStreamReader, CTNumPr.type, xmlOptions);
        }
        
        public static CTNumPr parse(final Node node) throws XmlException {
            return (CTNumPr)getTypeLoader().parse(node, CTNumPr.type, (XmlOptions)null);
        }
        
        public static CTNumPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumPr)getTypeLoader().parse(node, CTNumPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumPr)getTypeLoader().parse(xmlInputStream, CTNumPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumPr)getTypeLoader().parse(xmlInputStream, CTNumPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

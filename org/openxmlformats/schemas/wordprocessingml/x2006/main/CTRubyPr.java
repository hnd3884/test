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

public interface CTRubyPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRubyPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrubyprb2actype");
    
    CTRubyAlign getRubyAlign();
    
    void setRubyAlign(final CTRubyAlign p0);
    
    CTRubyAlign addNewRubyAlign();
    
    CTHpsMeasure getHps();
    
    void setHps(final CTHpsMeasure p0);
    
    CTHpsMeasure addNewHps();
    
    CTHpsMeasure getHpsRaise();
    
    void setHpsRaise(final CTHpsMeasure p0);
    
    CTHpsMeasure addNewHpsRaise();
    
    CTHpsMeasure getHpsBaseText();
    
    void setHpsBaseText(final CTHpsMeasure p0);
    
    CTHpsMeasure addNewHpsBaseText();
    
    CTLang getLid();
    
    void setLid(final CTLang p0);
    
    CTLang addNewLid();
    
    CTOnOff getDirty();
    
    boolean isSetDirty();
    
    void setDirty(final CTOnOff p0);
    
    CTOnOff addNewDirty();
    
    void unsetDirty();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRubyPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRubyPr newInstance() {
            return (CTRubyPr)getTypeLoader().newInstance(CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr newInstance(final XmlOptions xmlOptions) {
            return (CTRubyPr)getTypeLoader().newInstance(CTRubyPr.type, xmlOptions);
        }
        
        public static CTRubyPr parse(final String s) throws XmlException {
            return (CTRubyPr)getTypeLoader().parse(s, CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRubyPr)getTypeLoader().parse(s, CTRubyPr.type, xmlOptions);
        }
        
        public static CTRubyPr parse(final File file) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(file, CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(file, CTRubyPr.type, xmlOptions);
        }
        
        public static CTRubyPr parse(final URL url) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(url, CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(url, CTRubyPr.type, xmlOptions);
        }
        
        public static CTRubyPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(inputStream, CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(inputStream, CTRubyPr.type, xmlOptions);
        }
        
        public static CTRubyPr parse(final Reader reader) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(reader, CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyPr)getTypeLoader().parse(reader, CTRubyPr.type, xmlOptions);
        }
        
        public static CTRubyPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRubyPr)getTypeLoader().parse(xmlStreamReader, CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRubyPr)getTypeLoader().parse(xmlStreamReader, CTRubyPr.type, xmlOptions);
        }
        
        public static CTRubyPr parse(final Node node) throws XmlException {
            return (CTRubyPr)getTypeLoader().parse(node, CTRubyPr.type, (XmlOptions)null);
        }
        
        public static CTRubyPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRubyPr)getTypeLoader().parse(node, CTRubyPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRubyPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRubyPr)getTypeLoader().parse(xmlInputStream, CTRubyPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRubyPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRubyPr)getTypeLoader().parse(xmlInputStream, CTRubyPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRubyPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRubyPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

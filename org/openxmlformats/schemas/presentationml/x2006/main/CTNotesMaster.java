package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNotesMaster extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNotesMaster.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnotesmaster69ectype");
    
    CTCommonSlideData getCSld();
    
    void setCSld(final CTCommonSlideData p0);
    
    CTCommonSlideData addNewCSld();
    
    CTColorMapping getClrMap();
    
    void setClrMap(final CTColorMapping p0);
    
    CTColorMapping addNewClrMap();
    
    CTHeaderFooter getHf();
    
    boolean isSetHf();
    
    void setHf(final CTHeaderFooter p0);
    
    CTHeaderFooter addNewHf();
    
    void unsetHf();
    
    CTTextListStyle getNotesStyle();
    
    boolean isSetNotesStyle();
    
    void setNotesStyle(final CTTextListStyle p0);
    
    CTTextListStyle addNewNotesStyle();
    
    void unsetNotesStyle();
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNotesMaster.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNotesMaster newInstance() {
            return (CTNotesMaster)getTypeLoader().newInstance(CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster newInstance(final XmlOptions xmlOptions) {
            return (CTNotesMaster)getTypeLoader().newInstance(CTNotesMaster.type, xmlOptions);
        }
        
        public static CTNotesMaster parse(final String s) throws XmlException {
            return (CTNotesMaster)getTypeLoader().parse(s, CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMaster)getTypeLoader().parse(s, CTNotesMaster.type, xmlOptions);
        }
        
        public static CTNotesMaster parse(final File file) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(file, CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(file, CTNotesMaster.type, xmlOptions);
        }
        
        public static CTNotesMaster parse(final URL url) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(url, CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(url, CTNotesMaster.type, xmlOptions);
        }
        
        public static CTNotesMaster parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(inputStream, CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(inputStream, CTNotesMaster.type, xmlOptions);
        }
        
        public static CTNotesMaster parse(final Reader reader) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(reader, CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMaster)getTypeLoader().parse(reader, CTNotesMaster.type, xmlOptions);
        }
        
        public static CTNotesMaster parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNotesMaster)getTypeLoader().parse(xmlStreamReader, CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMaster)getTypeLoader().parse(xmlStreamReader, CTNotesMaster.type, xmlOptions);
        }
        
        public static CTNotesMaster parse(final Node node) throws XmlException {
            return (CTNotesMaster)getTypeLoader().parse(node, CTNotesMaster.type, (XmlOptions)null);
        }
        
        public static CTNotesMaster parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMaster)getTypeLoader().parse(node, CTNotesMaster.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNotesMaster parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNotesMaster)getTypeLoader().parse(xmlInputStream, CTNotesMaster.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNotesMaster parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNotesMaster)getTypeLoader().parse(xmlInputStream, CTNotesMaster.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesMaster.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesMaster.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

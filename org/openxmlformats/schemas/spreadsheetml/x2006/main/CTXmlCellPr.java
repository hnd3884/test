package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTXmlCellPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTXmlCellPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctxmlcellprf1datype");
    
    CTXmlPr getXmlPr();
    
    void setXmlPr(final CTXmlPr p0);
    
    CTXmlPr addNewXmlPr();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    XmlUnsignedInt xgetId();
    
    void setId(final long p0);
    
    void xsetId(final XmlUnsignedInt p0);
    
    String getUniqueName();
    
    STXstring xgetUniqueName();
    
    boolean isSetUniqueName();
    
    void setUniqueName(final String p0);
    
    void xsetUniqueName(final STXstring p0);
    
    void unsetUniqueName();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTXmlCellPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTXmlCellPr newInstance() {
            return (CTXmlCellPr)getTypeLoader().newInstance(CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr newInstance(final XmlOptions xmlOptions) {
            return (CTXmlCellPr)getTypeLoader().newInstance(CTXmlCellPr.type, xmlOptions);
        }
        
        public static CTXmlCellPr parse(final String s) throws XmlException {
            return (CTXmlCellPr)getTypeLoader().parse(s, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlCellPr)getTypeLoader().parse(s, CTXmlCellPr.type, xmlOptions);
        }
        
        public static CTXmlCellPr parse(final File file) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(file, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(file, CTXmlCellPr.type, xmlOptions);
        }
        
        public static CTXmlCellPr parse(final URL url) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(url, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(url, CTXmlCellPr.type, xmlOptions);
        }
        
        public static CTXmlCellPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(inputStream, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(inputStream, CTXmlCellPr.type, xmlOptions);
        }
        
        public static CTXmlCellPr parse(final Reader reader) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(reader, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlCellPr)getTypeLoader().parse(reader, CTXmlCellPr.type, xmlOptions);
        }
        
        public static CTXmlCellPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTXmlCellPr)getTypeLoader().parse(xmlStreamReader, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlCellPr)getTypeLoader().parse(xmlStreamReader, CTXmlCellPr.type, xmlOptions);
        }
        
        public static CTXmlCellPr parse(final Node node) throws XmlException {
            return (CTXmlCellPr)getTypeLoader().parse(node, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        public static CTXmlCellPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlCellPr)getTypeLoader().parse(node, CTXmlCellPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTXmlCellPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTXmlCellPr)getTypeLoader().parse(xmlInputStream, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTXmlCellPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTXmlCellPr)getTypeLoader().parse(xmlInputStream, CTXmlCellPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXmlCellPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXmlCellPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

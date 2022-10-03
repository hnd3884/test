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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBookViews extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBookViews.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbookviewsb864type");
    
    List<CTBookView> getWorkbookViewList();
    
    @Deprecated
    CTBookView[] getWorkbookViewArray();
    
    CTBookView getWorkbookViewArray(final int p0);
    
    int sizeOfWorkbookViewArray();
    
    void setWorkbookViewArray(final CTBookView[] p0);
    
    void setWorkbookViewArray(final int p0, final CTBookView p1);
    
    CTBookView insertNewWorkbookView(final int p0);
    
    CTBookView addNewWorkbookView();
    
    void removeWorkbookView(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBookViews.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBookViews newInstance() {
            return (CTBookViews)getTypeLoader().newInstance(CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews newInstance(final XmlOptions xmlOptions) {
            return (CTBookViews)getTypeLoader().newInstance(CTBookViews.type, xmlOptions);
        }
        
        public static CTBookViews parse(final String s) throws XmlException {
            return (CTBookViews)getTypeLoader().parse(s, CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookViews)getTypeLoader().parse(s, CTBookViews.type, xmlOptions);
        }
        
        public static CTBookViews parse(final File file) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(file, CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(file, CTBookViews.type, xmlOptions);
        }
        
        public static CTBookViews parse(final URL url) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(url, CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(url, CTBookViews.type, xmlOptions);
        }
        
        public static CTBookViews parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(inputStream, CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(inputStream, CTBookViews.type, xmlOptions);
        }
        
        public static CTBookViews parse(final Reader reader) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(reader, CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookViews)getTypeLoader().parse(reader, CTBookViews.type, xmlOptions);
        }
        
        public static CTBookViews parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBookViews)getTypeLoader().parse(xmlStreamReader, CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookViews)getTypeLoader().parse(xmlStreamReader, CTBookViews.type, xmlOptions);
        }
        
        public static CTBookViews parse(final Node node) throws XmlException {
            return (CTBookViews)getTypeLoader().parse(node, CTBookViews.type, (XmlOptions)null);
        }
        
        public static CTBookViews parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookViews)getTypeLoader().parse(node, CTBookViews.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBookViews parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBookViews)getTypeLoader().parse(xmlInputStream, CTBookViews.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBookViews parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBookViews)getTypeLoader().parse(xmlInputStream, CTBookViews.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookViews.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookViews.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

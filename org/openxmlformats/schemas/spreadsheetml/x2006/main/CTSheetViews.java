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

public interface CTSheetViews extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetViews.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetviewsb918type");
    
    List<CTSheetView> getSheetViewList();
    
    @Deprecated
    CTSheetView[] getSheetViewArray();
    
    CTSheetView getSheetViewArray(final int p0);
    
    int sizeOfSheetViewArray();
    
    void setSheetViewArray(final CTSheetView[] p0);
    
    void setSheetViewArray(final int p0, final CTSheetView p1);
    
    CTSheetView insertNewSheetView(final int p0);
    
    CTSheetView addNewSheetView();
    
    void removeSheetView(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetViews.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetViews newInstance() {
            return (CTSheetViews)getTypeLoader().newInstance(CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews newInstance(final XmlOptions xmlOptions) {
            return (CTSheetViews)getTypeLoader().newInstance(CTSheetViews.type, xmlOptions);
        }
        
        public static CTSheetViews parse(final String s) throws XmlException {
            return (CTSheetViews)getTypeLoader().parse(s, CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetViews)getTypeLoader().parse(s, CTSheetViews.type, xmlOptions);
        }
        
        public static CTSheetViews parse(final File file) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(file, CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(file, CTSheetViews.type, xmlOptions);
        }
        
        public static CTSheetViews parse(final URL url) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(url, CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(url, CTSheetViews.type, xmlOptions);
        }
        
        public static CTSheetViews parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(inputStream, CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(inputStream, CTSheetViews.type, xmlOptions);
        }
        
        public static CTSheetViews parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(reader, CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetViews)getTypeLoader().parse(reader, CTSheetViews.type, xmlOptions);
        }
        
        public static CTSheetViews parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetViews)getTypeLoader().parse(xmlStreamReader, CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetViews)getTypeLoader().parse(xmlStreamReader, CTSheetViews.type, xmlOptions);
        }
        
        public static CTSheetViews parse(final Node node) throws XmlException {
            return (CTSheetViews)getTypeLoader().parse(node, CTSheetViews.type, (XmlOptions)null);
        }
        
        public static CTSheetViews parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetViews)getTypeLoader().parse(node, CTSheetViews.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetViews parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetViews)getTypeLoader().parse(xmlInputStream, CTSheetViews.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetViews parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetViews)getTypeLoader().parse(xmlInputStream, CTSheetViews.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetViews.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetViews.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

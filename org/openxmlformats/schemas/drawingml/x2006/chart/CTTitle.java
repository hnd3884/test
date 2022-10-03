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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTitle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTitle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttitleb54etype");
    
    CTTx getTx();
    
    boolean isSetTx();
    
    void setTx(final CTTx p0);
    
    CTTx addNewTx();
    
    void unsetTx();
    
    CTLayout getLayout();
    
    boolean isSetLayout();
    
    void setLayout(final CTLayout p0);
    
    CTLayout addNewLayout();
    
    void unsetLayout();
    
    CTBoolean getOverlay();
    
    boolean isSetOverlay();
    
    void setOverlay(final CTBoolean p0);
    
    CTBoolean addNewOverlay();
    
    void unsetOverlay();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
    CTTextBody getTxPr();
    
    boolean isSetTxPr();
    
    void setTxPr(final CTTextBody p0);
    
    CTTextBody addNewTxPr();
    
    void unsetTxPr();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTitle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTitle newInstance() {
            return (CTTitle)getTypeLoader().newInstance(CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle newInstance(final XmlOptions xmlOptions) {
            return (CTTitle)getTypeLoader().newInstance(CTTitle.type, xmlOptions);
        }
        
        public static CTTitle parse(final String s) throws XmlException {
            return (CTTitle)getTypeLoader().parse(s, CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTitle)getTypeLoader().parse(s, CTTitle.type, xmlOptions);
        }
        
        public static CTTitle parse(final File file) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(file, CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(file, CTTitle.type, xmlOptions);
        }
        
        public static CTTitle parse(final URL url) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(url, CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(url, CTTitle.type, xmlOptions);
        }
        
        public static CTTitle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(inputStream, CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(inputStream, CTTitle.type, xmlOptions);
        }
        
        public static CTTitle parse(final Reader reader) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(reader, CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTitle)getTypeLoader().parse(reader, CTTitle.type, xmlOptions);
        }
        
        public static CTTitle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTitle)getTypeLoader().parse(xmlStreamReader, CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTitle)getTypeLoader().parse(xmlStreamReader, CTTitle.type, xmlOptions);
        }
        
        public static CTTitle parse(final Node node) throws XmlException {
            return (CTTitle)getTypeLoader().parse(node, CTTitle.type, (XmlOptions)null);
        }
        
        public static CTTitle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTitle)getTypeLoader().parse(node, CTTitle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTitle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTitle)getTypeLoader().parse(xmlInputStream, CTTitle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTitle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTitle)getTypeLoader().parse(xmlInputStream, CTTitle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTitle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTitle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

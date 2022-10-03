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

public interface CTTblPrExBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblPrExBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblprexbasee7eetype");
    
    CTTblWidth getTblW();
    
    boolean isSetTblW();
    
    void setTblW(final CTTblWidth p0);
    
    CTTblWidth addNewTblW();
    
    void unsetTblW();
    
    CTJc getJc();
    
    boolean isSetJc();
    
    void setJc(final CTJc p0);
    
    CTJc addNewJc();
    
    void unsetJc();
    
    CTTblWidth getTblCellSpacing();
    
    boolean isSetTblCellSpacing();
    
    void setTblCellSpacing(final CTTblWidth p0);
    
    CTTblWidth addNewTblCellSpacing();
    
    void unsetTblCellSpacing();
    
    CTTblWidth getTblInd();
    
    boolean isSetTblInd();
    
    void setTblInd(final CTTblWidth p0);
    
    CTTblWidth addNewTblInd();
    
    void unsetTblInd();
    
    CTTblBorders getTblBorders();
    
    boolean isSetTblBorders();
    
    void setTblBorders(final CTTblBorders p0);
    
    CTTblBorders addNewTblBorders();
    
    void unsetTblBorders();
    
    CTShd getShd();
    
    boolean isSetShd();
    
    void setShd(final CTShd p0);
    
    CTShd addNewShd();
    
    void unsetShd();
    
    CTTblLayoutType getTblLayout();
    
    boolean isSetTblLayout();
    
    void setTblLayout(final CTTblLayoutType p0);
    
    CTTblLayoutType addNewTblLayout();
    
    void unsetTblLayout();
    
    CTTblCellMar getTblCellMar();
    
    boolean isSetTblCellMar();
    
    void setTblCellMar(final CTTblCellMar p0);
    
    CTTblCellMar addNewTblCellMar();
    
    void unsetTblCellMar();
    
    CTShortHexNumber getTblLook();
    
    boolean isSetTblLook();
    
    void setTblLook(final CTShortHexNumber p0);
    
    CTShortHexNumber addNewTblLook();
    
    void unsetTblLook();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblPrExBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblPrExBase newInstance() {
            return (CTTblPrExBase)getTypeLoader().newInstance(CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase newInstance(final XmlOptions xmlOptions) {
            return (CTTblPrExBase)getTypeLoader().newInstance(CTTblPrExBase.type, xmlOptions);
        }
        
        public static CTTblPrExBase parse(final String s) throws XmlException {
            return (CTTblPrExBase)getTypeLoader().parse(s, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrExBase)getTypeLoader().parse(s, CTTblPrExBase.type, xmlOptions);
        }
        
        public static CTTblPrExBase parse(final File file) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(file, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(file, CTTblPrExBase.type, xmlOptions);
        }
        
        public static CTTblPrExBase parse(final URL url) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(url, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(url, CTTblPrExBase.type, xmlOptions);
        }
        
        public static CTTblPrExBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(inputStream, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(inputStream, CTTblPrExBase.type, xmlOptions);
        }
        
        public static CTTblPrExBase parse(final Reader reader) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(reader, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrExBase)getTypeLoader().parse(reader, CTTblPrExBase.type, xmlOptions);
        }
        
        public static CTTblPrExBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblPrExBase)getTypeLoader().parse(xmlStreamReader, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrExBase)getTypeLoader().parse(xmlStreamReader, CTTblPrExBase.type, xmlOptions);
        }
        
        public static CTTblPrExBase parse(final Node node) throws XmlException {
            return (CTTblPrExBase)getTypeLoader().parse(node, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrExBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrExBase)getTypeLoader().parse(node, CTTblPrExBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblPrExBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblPrExBase)getTypeLoader().parse(xmlInputStream, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblPrExBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblPrExBase)getTypeLoader().parse(xmlInputStream, CTTblPrExBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblPrExBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblPrExBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

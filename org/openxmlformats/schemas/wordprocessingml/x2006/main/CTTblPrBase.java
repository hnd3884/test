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

public interface CTTblPrBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblPrBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblprbaseeba1type");
    
    CTString getTblStyle();
    
    boolean isSetTblStyle();
    
    void setTblStyle(final CTString p0);
    
    CTString addNewTblStyle();
    
    void unsetTblStyle();
    
    CTTblPPr getTblpPr();
    
    boolean isSetTblpPr();
    
    void setTblpPr(final CTTblPPr p0);
    
    CTTblPPr addNewTblpPr();
    
    void unsetTblpPr();
    
    CTTblOverlap getTblOverlap();
    
    boolean isSetTblOverlap();
    
    void setTblOverlap(final CTTblOverlap p0);
    
    CTTblOverlap addNewTblOverlap();
    
    void unsetTblOverlap();
    
    CTOnOff getBidiVisual();
    
    boolean isSetBidiVisual();
    
    void setBidiVisual(final CTOnOff p0);
    
    CTOnOff addNewBidiVisual();
    
    void unsetBidiVisual();
    
    CTDecimalNumber getTblStyleRowBandSize();
    
    boolean isSetTblStyleRowBandSize();
    
    void setTblStyleRowBandSize(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewTblStyleRowBandSize();
    
    void unsetTblStyleRowBandSize();
    
    CTDecimalNumber getTblStyleColBandSize();
    
    boolean isSetTblStyleColBandSize();
    
    void setTblStyleColBandSize(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewTblStyleColBandSize();
    
    void unsetTblStyleColBandSize();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblPrBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblPrBase newInstance() {
            return (CTTblPrBase)getTypeLoader().newInstance(CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase newInstance(final XmlOptions xmlOptions) {
            return (CTTblPrBase)getTypeLoader().newInstance(CTTblPrBase.type, xmlOptions);
        }
        
        public static CTTblPrBase parse(final String s) throws XmlException {
            return (CTTblPrBase)getTypeLoader().parse(s, CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrBase)getTypeLoader().parse(s, CTTblPrBase.type, xmlOptions);
        }
        
        public static CTTblPrBase parse(final File file) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(file, CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(file, CTTblPrBase.type, xmlOptions);
        }
        
        public static CTTblPrBase parse(final URL url) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(url, CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(url, CTTblPrBase.type, xmlOptions);
        }
        
        public static CTTblPrBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(inputStream, CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(inputStream, CTTblPrBase.type, xmlOptions);
        }
        
        public static CTTblPrBase parse(final Reader reader) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(reader, CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrBase)getTypeLoader().parse(reader, CTTblPrBase.type, xmlOptions);
        }
        
        public static CTTblPrBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblPrBase)getTypeLoader().parse(xmlStreamReader, CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrBase)getTypeLoader().parse(xmlStreamReader, CTTblPrBase.type, xmlOptions);
        }
        
        public static CTTblPrBase parse(final Node node) throws XmlException {
            return (CTTblPrBase)getTypeLoader().parse(node, CTTblPrBase.type, (XmlOptions)null);
        }
        
        public static CTTblPrBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrBase)getTypeLoader().parse(node, CTTblPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblPrBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblPrBase)getTypeLoader().parse(xmlInputStream, CTTblPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblPrBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblPrBase)getTypeLoader().parse(xmlInputStream, CTTblPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblPrBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

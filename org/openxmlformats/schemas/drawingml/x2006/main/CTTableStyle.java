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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestyled59etype");
    
    CTTableBackgroundStyle getTblBg();
    
    boolean isSetTblBg();
    
    void setTblBg(final CTTableBackgroundStyle p0);
    
    CTTableBackgroundStyle addNewTblBg();
    
    void unsetTblBg();
    
    CTTablePartStyle getWholeTbl();
    
    boolean isSetWholeTbl();
    
    void setWholeTbl(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewWholeTbl();
    
    void unsetWholeTbl();
    
    CTTablePartStyle getBand1H();
    
    boolean isSetBand1H();
    
    void setBand1H(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewBand1H();
    
    void unsetBand1H();
    
    CTTablePartStyle getBand2H();
    
    boolean isSetBand2H();
    
    void setBand2H(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewBand2H();
    
    void unsetBand2H();
    
    CTTablePartStyle getBand1V();
    
    boolean isSetBand1V();
    
    void setBand1V(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewBand1V();
    
    void unsetBand1V();
    
    CTTablePartStyle getBand2V();
    
    boolean isSetBand2V();
    
    void setBand2V(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewBand2V();
    
    void unsetBand2V();
    
    CTTablePartStyle getLastCol();
    
    boolean isSetLastCol();
    
    void setLastCol(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewLastCol();
    
    void unsetLastCol();
    
    CTTablePartStyle getFirstCol();
    
    boolean isSetFirstCol();
    
    void setFirstCol(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewFirstCol();
    
    void unsetFirstCol();
    
    CTTablePartStyle getLastRow();
    
    boolean isSetLastRow();
    
    void setLastRow(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewLastRow();
    
    void unsetLastRow();
    
    CTTablePartStyle getSeCell();
    
    boolean isSetSeCell();
    
    void setSeCell(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewSeCell();
    
    void unsetSeCell();
    
    CTTablePartStyle getSwCell();
    
    boolean isSetSwCell();
    
    void setSwCell(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewSwCell();
    
    void unsetSwCell();
    
    CTTablePartStyle getFirstRow();
    
    boolean isSetFirstRow();
    
    void setFirstRow(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewFirstRow();
    
    void unsetFirstRow();
    
    CTTablePartStyle getNeCell();
    
    boolean isSetNeCell();
    
    void setNeCell(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewNeCell();
    
    void unsetNeCell();
    
    CTTablePartStyle getNwCell();
    
    boolean isSetNwCell();
    
    void setNwCell(final CTTablePartStyle p0);
    
    CTTablePartStyle addNewNwCell();
    
    void unsetNwCell();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getStyleId();
    
    STGuid xgetStyleId();
    
    void setStyleId(final String p0);
    
    void xsetStyleId(final STGuid p0);
    
    String getStyleName();
    
    XmlString xgetStyleName();
    
    void setStyleName(final String p0);
    
    void xsetStyleName(final XmlString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyle newInstance() {
            return (CTTableStyle)getTypeLoader().newInstance(CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyle)getTypeLoader().newInstance(CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final String s) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(s, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(s, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final File file) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(file, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(file, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final URL url) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(url, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(url, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(inputStream, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(inputStream, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(reader, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(reader, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final Node node) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(node, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(node, CTTableStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyle)getTypeLoader().parse(xmlInputStream, CTTableStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyle)getTypeLoader().parse(xmlInputStream, CTTableStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

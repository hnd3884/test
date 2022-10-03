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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttableproperties3512type");
    
    CTNoFillProperties getNoFill();
    
    boolean isSetNoFill();
    
    void setNoFill(final CTNoFillProperties p0);
    
    CTNoFillProperties addNewNoFill();
    
    void unsetNoFill();
    
    CTSolidColorFillProperties getSolidFill();
    
    boolean isSetSolidFill();
    
    void setSolidFill(final CTSolidColorFillProperties p0);
    
    CTSolidColorFillProperties addNewSolidFill();
    
    void unsetSolidFill();
    
    CTGradientFillProperties getGradFill();
    
    boolean isSetGradFill();
    
    void setGradFill(final CTGradientFillProperties p0);
    
    CTGradientFillProperties addNewGradFill();
    
    void unsetGradFill();
    
    CTBlipFillProperties getBlipFill();
    
    boolean isSetBlipFill();
    
    void setBlipFill(final CTBlipFillProperties p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    void unsetBlipFill();
    
    CTPatternFillProperties getPattFill();
    
    boolean isSetPattFill();
    
    void setPattFill(final CTPatternFillProperties p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void unsetPattFill();
    
    CTGroupFillProperties getGrpFill();
    
    boolean isSetGrpFill();
    
    void setGrpFill(final CTGroupFillProperties p0);
    
    CTGroupFillProperties addNewGrpFill();
    
    void unsetGrpFill();
    
    CTEffectList getEffectLst();
    
    boolean isSetEffectLst();
    
    void setEffectLst(final CTEffectList p0);
    
    CTEffectList addNewEffectLst();
    
    void unsetEffectLst();
    
    CTEffectContainer getEffectDag();
    
    boolean isSetEffectDag();
    
    void setEffectDag(final CTEffectContainer p0);
    
    CTEffectContainer addNewEffectDag();
    
    void unsetEffectDag();
    
    CTTableStyle getTableStyle();
    
    boolean isSetTableStyle();
    
    void setTableStyle(final CTTableStyle p0);
    
    CTTableStyle addNewTableStyle();
    
    void unsetTableStyle();
    
    String getTableStyleId();
    
    STGuid xgetTableStyleId();
    
    boolean isSetTableStyleId();
    
    void setTableStyleId(final String p0);
    
    void xsetTableStyleId(final STGuid p0);
    
    void unsetTableStyleId();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getRtl();
    
    XmlBoolean xgetRtl();
    
    boolean isSetRtl();
    
    void setRtl(final boolean p0);
    
    void xsetRtl(final XmlBoolean p0);
    
    void unsetRtl();
    
    boolean getFirstRow();
    
    XmlBoolean xgetFirstRow();
    
    boolean isSetFirstRow();
    
    void setFirstRow(final boolean p0);
    
    void xsetFirstRow(final XmlBoolean p0);
    
    void unsetFirstRow();
    
    boolean getFirstCol();
    
    XmlBoolean xgetFirstCol();
    
    boolean isSetFirstCol();
    
    void setFirstCol(final boolean p0);
    
    void xsetFirstCol(final XmlBoolean p0);
    
    void unsetFirstCol();
    
    boolean getLastRow();
    
    XmlBoolean xgetLastRow();
    
    boolean isSetLastRow();
    
    void setLastRow(final boolean p0);
    
    void xsetLastRow(final XmlBoolean p0);
    
    void unsetLastRow();
    
    boolean getLastCol();
    
    XmlBoolean xgetLastCol();
    
    boolean isSetLastCol();
    
    void setLastCol(final boolean p0);
    
    void xsetLastCol(final XmlBoolean p0);
    
    void unsetLastCol();
    
    boolean getBandRow();
    
    XmlBoolean xgetBandRow();
    
    boolean isSetBandRow();
    
    void setBandRow(final boolean p0);
    
    void xsetBandRow(final XmlBoolean p0);
    
    void unsetBandRow();
    
    boolean getBandCol();
    
    XmlBoolean xgetBandCol();
    
    boolean isSetBandCol();
    
    void setBandCol(final boolean p0);
    
    void xsetBandCol(final XmlBoolean p0);
    
    void unsetBandCol();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableProperties newInstance() {
            return (CTTableProperties)getTypeLoader().newInstance(CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties newInstance(final XmlOptions xmlOptions) {
            return (CTTableProperties)getTypeLoader().newInstance(CTTableProperties.type, xmlOptions);
        }
        
        public static CTTableProperties parse(final String s) throws XmlException {
            return (CTTableProperties)getTypeLoader().parse(s, CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableProperties)getTypeLoader().parse(s, CTTableProperties.type, xmlOptions);
        }
        
        public static CTTableProperties parse(final File file) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(file, CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(file, CTTableProperties.type, xmlOptions);
        }
        
        public static CTTableProperties parse(final URL url) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(url, CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(url, CTTableProperties.type, xmlOptions);
        }
        
        public static CTTableProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(inputStream, CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(inputStream, CTTableProperties.type, xmlOptions);
        }
        
        public static CTTableProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(reader, CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableProperties)getTypeLoader().parse(reader, CTTableProperties.type, xmlOptions);
        }
        
        public static CTTableProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableProperties)getTypeLoader().parse(xmlStreamReader, CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableProperties)getTypeLoader().parse(xmlStreamReader, CTTableProperties.type, xmlOptions);
        }
        
        public static CTTableProperties parse(final Node node) throws XmlException {
            return (CTTableProperties)getTypeLoader().parse(node, CTTableProperties.type, (XmlOptions)null);
        }
        
        public static CTTableProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableProperties)getTypeLoader().parse(node, CTTableProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableProperties)getTypeLoader().parse(xmlInputStream, CTTableProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableProperties)getTypeLoader().parse(xmlInputStream, CTTableProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

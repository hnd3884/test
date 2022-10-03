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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLineProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLineProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlinepropertiesd5e2type");
    
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
    
    CTPatternFillProperties getPattFill();
    
    boolean isSetPattFill();
    
    void setPattFill(final CTPatternFillProperties p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void unsetPattFill();
    
    CTPresetLineDashProperties getPrstDash();
    
    boolean isSetPrstDash();
    
    void setPrstDash(final CTPresetLineDashProperties p0);
    
    CTPresetLineDashProperties addNewPrstDash();
    
    void unsetPrstDash();
    
    CTDashStopList getCustDash();
    
    boolean isSetCustDash();
    
    void setCustDash(final CTDashStopList p0);
    
    CTDashStopList addNewCustDash();
    
    void unsetCustDash();
    
    CTLineJoinRound getRound();
    
    boolean isSetRound();
    
    void setRound(final CTLineJoinRound p0);
    
    CTLineJoinRound addNewRound();
    
    void unsetRound();
    
    CTLineJoinBevel getBevel();
    
    boolean isSetBevel();
    
    void setBevel(final CTLineJoinBevel p0);
    
    CTLineJoinBevel addNewBevel();
    
    void unsetBevel();
    
    CTLineJoinMiterProperties getMiter();
    
    boolean isSetMiter();
    
    void setMiter(final CTLineJoinMiterProperties p0);
    
    CTLineJoinMiterProperties addNewMiter();
    
    void unsetMiter();
    
    CTLineEndProperties getHeadEnd();
    
    boolean isSetHeadEnd();
    
    void setHeadEnd(final CTLineEndProperties p0);
    
    CTLineEndProperties addNewHeadEnd();
    
    void unsetHeadEnd();
    
    CTLineEndProperties getTailEnd();
    
    boolean isSetTailEnd();
    
    void setTailEnd(final CTLineEndProperties p0);
    
    CTLineEndProperties addNewTailEnd();
    
    void unsetTailEnd();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    int getW();
    
    STLineWidth xgetW();
    
    boolean isSetW();
    
    void setW(final int p0);
    
    void xsetW(final STLineWidth p0);
    
    void unsetW();
    
    STLineCap.Enum getCap();
    
    STLineCap xgetCap();
    
    boolean isSetCap();
    
    void setCap(final STLineCap.Enum p0);
    
    void xsetCap(final STLineCap p0);
    
    void unsetCap();
    
    STCompoundLine.Enum getCmpd();
    
    STCompoundLine xgetCmpd();
    
    boolean isSetCmpd();
    
    void setCmpd(final STCompoundLine.Enum p0);
    
    void xsetCmpd(final STCompoundLine p0);
    
    void unsetCmpd();
    
    STPenAlignment.Enum getAlgn();
    
    STPenAlignment xgetAlgn();
    
    boolean isSetAlgn();
    
    void setAlgn(final STPenAlignment.Enum p0);
    
    void xsetAlgn(final STPenAlignment p0);
    
    void unsetAlgn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLineProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLineProperties newInstance() {
            return (CTLineProperties)getTypeLoader().newInstance(CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties newInstance(final XmlOptions xmlOptions) {
            return (CTLineProperties)getTypeLoader().newInstance(CTLineProperties.type, xmlOptions);
        }
        
        public static CTLineProperties parse(final String s) throws XmlException {
            return (CTLineProperties)getTypeLoader().parse(s, CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineProperties)getTypeLoader().parse(s, CTLineProperties.type, xmlOptions);
        }
        
        public static CTLineProperties parse(final File file) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(file, CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(file, CTLineProperties.type, xmlOptions);
        }
        
        public static CTLineProperties parse(final URL url) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(url, CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(url, CTLineProperties.type, xmlOptions);
        }
        
        public static CTLineProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(inputStream, CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(inputStream, CTLineProperties.type, xmlOptions);
        }
        
        public static CTLineProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(reader, CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineProperties)getTypeLoader().parse(reader, CTLineProperties.type, xmlOptions);
        }
        
        public static CTLineProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLineProperties)getTypeLoader().parse(xmlStreamReader, CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineProperties)getTypeLoader().parse(xmlStreamReader, CTLineProperties.type, xmlOptions);
        }
        
        public static CTLineProperties parse(final Node node) throws XmlException {
            return (CTLineProperties)getTypeLoader().parse(node, CTLineProperties.type, (XmlOptions)null);
        }
        
        public static CTLineProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineProperties)getTypeLoader().parse(node, CTLineProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLineProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLineProperties)getTypeLoader().parse(xmlInputStream, CTLineProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLineProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLineProperties)getTypeLoader().parse(xmlInputStream, CTLineProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

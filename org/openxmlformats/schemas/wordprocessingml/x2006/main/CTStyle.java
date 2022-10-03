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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstyle41c1type");
    
    CTString getName();
    
    boolean isSetName();
    
    void setName(final CTString p0);
    
    CTString addNewName();
    
    void unsetName();
    
    CTString getAliases();
    
    boolean isSetAliases();
    
    void setAliases(final CTString p0);
    
    CTString addNewAliases();
    
    void unsetAliases();
    
    CTString getBasedOn();
    
    boolean isSetBasedOn();
    
    void setBasedOn(final CTString p0);
    
    CTString addNewBasedOn();
    
    void unsetBasedOn();
    
    CTString getNext();
    
    boolean isSetNext();
    
    void setNext(final CTString p0);
    
    CTString addNewNext();
    
    void unsetNext();
    
    CTString getLink();
    
    boolean isSetLink();
    
    void setLink(final CTString p0);
    
    CTString addNewLink();
    
    void unsetLink();
    
    CTOnOff getAutoRedefine();
    
    boolean isSetAutoRedefine();
    
    void setAutoRedefine(final CTOnOff p0);
    
    CTOnOff addNewAutoRedefine();
    
    void unsetAutoRedefine();
    
    CTOnOff getHidden();
    
    boolean isSetHidden();
    
    void setHidden(final CTOnOff p0);
    
    CTOnOff addNewHidden();
    
    void unsetHidden();
    
    CTDecimalNumber getUiPriority();
    
    boolean isSetUiPriority();
    
    void setUiPriority(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewUiPriority();
    
    void unsetUiPriority();
    
    CTOnOff getSemiHidden();
    
    boolean isSetSemiHidden();
    
    void setSemiHidden(final CTOnOff p0);
    
    CTOnOff addNewSemiHidden();
    
    void unsetSemiHidden();
    
    CTOnOff getUnhideWhenUsed();
    
    boolean isSetUnhideWhenUsed();
    
    void setUnhideWhenUsed(final CTOnOff p0);
    
    CTOnOff addNewUnhideWhenUsed();
    
    void unsetUnhideWhenUsed();
    
    CTOnOff getQFormat();
    
    boolean isSetQFormat();
    
    void setQFormat(final CTOnOff p0);
    
    CTOnOff addNewQFormat();
    
    void unsetQFormat();
    
    CTOnOff getLocked();
    
    boolean isSetLocked();
    
    void setLocked(final CTOnOff p0);
    
    CTOnOff addNewLocked();
    
    void unsetLocked();
    
    CTOnOff getPersonal();
    
    boolean isSetPersonal();
    
    void setPersonal(final CTOnOff p0);
    
    CTOnOff addNewPersonal();
    
    void unsetPersonal();
    
    CTOnOff getPersonalCompose();
    
    boolean isSetPersonalCompose();
    
    void setPersonalCompose(final CTOnOff p0);
    
    CTOnOff addNewPersonalCompose();
    
    void unsetPersonalCompose();
    
    CTOnOff getPersonalReply();
    
    boolean isSetPersonalReply();
    
    void setPersonalReply(final CTOnOff p0);
    
    CTOnOff addNewPersonalReply();
    
    void unsetPersonalReply();
    
    CTLongHexNumber getRsid();
    
    boolean isSetRsid();
    
    void setRsid(final CTLongHexNumber p0);
    
    CTLongHexNumber addNewRsid();
    
    void unsetRsid();
    
    CTPPr getPPr();
    
    boolean isSetPPr();
    
    void setPPr(final CTPPr p0);
    
    CTPPr addNewPPr();
    
    void unsetPPr();
    
    CTRPr getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTRPr p0);
    
    CTRPr addNewRPr();
    
    void unsetRPr();
    
    CTTblPrBase getTblPr();
    
    boolean isSetTblPr();
    
    void setTblPr(final CTTblPrBase p0);
    
    CTTblPrBase addNewTblPr();
    
    void unsetTblPr();
    
    CTTrPr getTrPr();
    
    boolean isSetTrPr();
    
    void setTrPr(final CTTrPr p0);
    
    CTTrPr addNewTrPr();
    
    void unsetTrPr();
    
    CTTcPr getTcPr();
    
    boolean isSetTcPr();
    
    void setTcPr(final CTTcPr p0);
    
    CTTcPr addNewTcPr();
    
    void unsetTcPr();
    
    List<CTTblStylePr> getTblStylePrList();
    
    @Deprecated
    CTTblStylePr[] getTblStylePrArray();
    
    CTTblStylePr getTblStylePrArray(final int p0);
    
    int sizeOfTblStylePrArray();
    
    void setTblStylePrArray(final CTTblStylePr[] p0);
    
    void setTblStylePrArray(final int p0, final CTTblStylePr p1);
    
    CTTblStylePr insertNewTblStylePr(final int p0);
    
    CTTblStylePr addNewTblStylePr();
    
    void removeTblStylePr(final int p0);
    
    STStyleType.Enum getType();
    
    STStyleType xgetType();
    
    boolean isSetType();
    
    void setType(final STStyleType.Enum p0);
    
    void xsetType(final STStyleType p0);
    
    void unsetType();
    
    String getStyleId();
    
    STString xgetStyleId();
    
    boolean isSetStyleId();
    
    void setStyleId(final String p0);
    
    void xsetStyleId(final STString p0);
    
    void unsetStyleId();
    
    STOnOff.Enum getDefault();
    
    STOnOff xgetDefault();
    
    boolean isSetDefault();
    
    void setDefault(final STOnOff.Enum p0);
    
    void xsetDefault(final STOnOff p0);
    
    void unsetDefault();
    
    STOnOff.Enum getCustomStyle();
    
    STOnOff xgetCustomStyle();
    
    boolean isSetCustomStyle();
    
    void setCustomStyle(final STOnOff.Enum p0);
    
    void xsetCustomStyle(final STOnOff p0);
    
    void unsetCustomStyle();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStyle newInstance() {
            return (CTStyle)getTypeLoader().newInstance(CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle newInstance(final XmlOptions xmlOptions) {
            return (CTStyle)getTypeLoader().newInstance(CTStyle.type, xmlOptions);
        }
        
        public static CTStyle parse(final String s) throws XmlException {
            return (CTStyle)getTypeLoader().parse(s, CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyle)getTypeLoader().parse(s, CTStyle.type, xmlOptions);
        }
        
        public static CTStyle parse(final File file) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(file, CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(file, CTStyle.type, xmlOptions);
        }
        
        public static CTStyle parse(final URL url) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(url, CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(url, CTStyle.type, xmlOptions);
        }
        
        public static CTStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(inputStream, CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(inputStream, CTStyle.type, xmlOptions);
        }
        
        public static CTStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(reader, CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyle)getTypeLoader().parse(reader, CTStyle.type, xmlOptions);
        }
        
        public static CTStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStyle)getTypeLoader().parse(xmlStreamReader, CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyle)getTypeLoader().parse(xmlStreamReader, CTStyle.type, xmlOptions);
        }
        
        public static CTStyle parse(final Node node) throws XmlException {
            return (CTStyle)getTypeLoader().parse(node, CTStyle.type, (XmlOptions)null);
        }
        
        public static CTStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyle)getTypeLoader().parse(node, CTStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStyle)getTypeLoader().parse(xmlInputStream, CTStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStyle)getTypeLoader().parse(xmlInputStream, CTStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

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

public interface CTSectPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSectPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsectpr1123type");
    
    List<CTHdrFtrRef> getHeaderReferenceList();
    
    @Deprecated
    CTHdrFtrRef[] getHeaderReferenceArray();
    
    CTHdrFtrRef getHeaderReferenceArray(final int p0);
    
    int sizeOfHeaderReferenceArray();
    
    void setHeaderReferenceArray(final CTHdrFtrRef[] p0);
    
    void setHeaderReferenceArray(final int p0, final CTHdrFtrRef p1);
    
    CTHdrFtrRef insertNewHeaderReference(final int p0);
    
    CTHdrFtrRef addNewHeaderReference();
    
    void removeHeaderReference(final int p0);
    
    List<CTHdrFtrRef> getFooterReferenceList();
    
    @Deprecated
    CTHdrFtrRef[] getFooterReferenceArray();
    
    CTHdrFtrRef getFooterReferenceArray(final int p0);
    
    int sizeOfFooterReferenceArray();
    
    void setFooterReferenceArray(final CTHdrFtrRef[] p0);
    
    void setFooterReferenceArray(final int p0, final CTHdrFtrRef p1);
    
    CTHdrFtrRef insertNewFooterReference(final int p0);
    
    CTHdrFtrRef addNewFooterReference();
    
    void removeFooterReference(final int p0);
    
    CTFtnProps getFootnotePr();
    
    boolean isSetFootnotePr();
    
    void setFootnotePr(final CTFtnProps p0);
    
    CTFtnProps addNewFootnotePr();
    
    void unsetFootnotePr();
    
    CTEdnProps getEndnotePr();
    
    boolean isSetEndnotePr();
    
    void setEndnotePr(final CTEdnProps p0);
    
    CTEdnProps addNewEndnotePr();
    
    void unsetEndnotePr();
    
    CTSectType getType();
    
    boolean isSetType();
    
    void setType(final CTSectType p0);
    
    CTSectType addNewType();
    
    void unsetType();
    
    CTPageSz getPgSz();
    
    boolean isSetPgSz();
    
    void setPgSz(final CTPageSz p0);
    
    CTPageSz addNewPgSz();
    
    void unsetPgSz();
    
    CTPageMar getPgMar();
    
    boolean isSetPgMar();
    
    void setPgMar(final CTPageMar p0);
    
    CTPageMar addNewPgMar();
    
    void unsetPgMar();
    
    CTPaperSource getPaperSrc();
    
    boolean isSetPaperSrc();
    
    void setPaperSrc(final CTPaperSource p0);
    
    CTPaperSource addNewPaperSrc();
    
    void unsetPaperSrc();
    
    CTPageBorders getPgBorders();
    
    boolean isSetPgBorders();
    
    void setPgBorders(final CTPageBorders p0);
    
    CTPageBorders addNewPgBorders();
    
    void unsetPgBorders();
    
    CTLineNumber getLnNumType();
    
    boolean isSetLnNumType();
    
    void setLnNumType(final CTLineNumber p0);
    
    CTLineNumber addNewLnNumType();
    
    void unsetLnNumType();
    
    CTPageNumber getPgNumType();
    
    boolean isSetPgNumType();
    
    void setPgNumType(final CTPageNumber p0);
    
    CTPageNumber addNewPgNumType();
    
    void unsetPgNumType();
    
    CTColumns getCols();
    
    boolean isSetCols();
    
    void setCols(final CTColumns p0);
    
    CTColumns addNewCols();
    
    void unsetCols();
    
    CTOnOff getFormProt();
    
    boolean isSetFormProt();
    
    void setFormProt(final CTOnOff p0);
    
    CTOnOff addNewFormProt();
    
    void unsetFormProt();
    
    CTVerticalJc getVAlign();
    
    boolean isSetVAlign();
    
    void setVAlign(final CTVerticalJc p0);
    
    CTVerticalJc addNewVAlign();
    
    void unsetVAlign();
    
    CTOnOff getNoEndnote();
    
    boolean isSetNoEndnote();
    
    void setNoEndnote(final CTOnOff p0);
    
    CTOnOff addNewNoEndnote();
    
    void unsetNoEndnote();
    
    CTOnOff getTitlePg();
    
    boolean isSetTitlePg();
    
    void setTitlePg(final CTOnOff p0);
    
    CTOnOff addNewTitlePg();
    
    void unsetTitlePg();
    
    CTTextDirection getTextDirection();
    
    boolean isSetTextDirection();
    
    void setTextDirection(final CTTextDirection p0);
    
    CTTextDirection addNewTextDirection();
    
    void unsetTextDirection();
    
    CTOnOff getBidi();
    
    boolean isSetBidi();
    
    void setBidi(final CTOnOff p0);
    
    CTOnOff addNewBidi();
    
    void unsetBidi();
    
    CTOnOff getRtlGutter();
    
    boolean isSetRtlGutter();
    
    void setRtlGutter(final CTOnOff p0);
    
    CTOnOff addNewRtlGutter();
    
    void unsetRtlGutter();
    
    CTDocGrid getDocGrid();
    
    boolean isSetDocGrid();
    
    void setDocGrid(final CTDocGrid p0);
    
    CTDocGrid addNewDocGrid();
    
    void unsetDocGrid();
    
    CTRel getPrinterSettings();
    
    boolean isSetPrinterSettings();
    
    void setPrinterSettings(final CTRel p0);
    
    CTRel addNewPrinterSettings();
    
    void unsetPrinterSettings();
    
    CTSectPrChange getSectPrChange();
    
    boolean isSetSectPrChange();
    
    void setSectPrChange(final CTSectPrChange p0);
    
    CTSectPrChange addNewSectPrChange();
    
    void unsetSectPrChange();
    
    byte[] getRsidRPr();
    
    STLongHexNumber xgetRsidRPr();
    
    boolean isSetRsidRPr();
    
    void setRsidRPr(final byte[] p0);
    
    void xsetRsidRPr(final STLongHexNumber p0);
    
    void unsetRsidRPr();
    
    byte[] getRsidDel();
    
    STLongHexNumber xgetRsidDel();
    
    boolean isSetRsidDel();
    
    void setRsidDel(final byte[] p0);
    
    void xsetRsidDel(final STLongHexNumber p0);
    
    void unsetRsidDel();
    
    byte[] getRsidR();
    
    STLongHexNumber xgetRsidR();
    
    boolean isSetRsidR();
    
    void setRsidR(final byte[] p0);
    
    void xsetRsidR(final STLongHexNumber p0);
    
    void unsetRsidR();
    
    byte[] getRsidSect();
    
    STLongHexNumber xgetRsidSect();
    
    boolean isSetRsidSect();
    
    void setRsidSect(final byte[] p0);
    
    void xsetRsidSect(final STLongHexNumber p0);
    
    void unsetRsidSect();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSectPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSectPr newInstance() {
            return (CTSectPr)getTypeLoader().newInstance(CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr newInstance(final XmlOptions xmlOptions) {
            return (CTSectPr)getTypeLoader().newInstance(CTSectPr.type, xmlOptions);
        }
        
        public static CTSectPr parse(final String s) throws XmlException {
            return (CTSectPr)getTypeLoader().parse(s, CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSectPr)getTypeLoader().parse(s, CTSectPr.type, xmlOptions);
        }
        
        public static CTSectPr parse(final File file) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(file, CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(file, CTSectPr.type, xmlOptions);
        }
        
        public static CTSectPr parse(final URL url) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(url, CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(url, CTSectPr.type, xmlOptions);
        }
        
        public static CTSectPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(inputStream, CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(inputStream, CTSectPr.type, xmlOptions);
        }
        
        public static CTSectPr parse(final Reader reader) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(reader, CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSectPr)getTypeLoader().parse(reader, CTSectPr.type, xmlOptions);
        }
        
        public static CTSectPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSectPr)getTypeLoader().parse(xmlStreamReader, CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSectPr)getTypeLoader().parse(xmlStreamReader, CTSectPr.type, xmlOptions);
        }
        
        public static CTSectPr parse(final Node node) throws XmlException {
            return (CTSectPr)getTypeLoader().parse(node, CTSectPr.type, (XmlOptions)null);
        }
        
        public static CTSectPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSectPr)getTypeLoader().parse(node, CTSectPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSectPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSectPr)getTypeLoader().parse(xmlInputStream, CTSectPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSectPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSectPr)getTypeLoader().parse(xmlInputStream, CTSectPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSectPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSectPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

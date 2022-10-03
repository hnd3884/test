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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPageSetup extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPageSetup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpagesetup534dtype");
    
    long getPaperSize();
    
    XmlUnsignedInt xgetPaperSize();
    
    boolean isSetPaperSize();
    
    void setPaperSize(final long p0);
    
    void xsetPaperSize(final XmlUnsignedInt p0);
    
    void unsetPaperSize();
    
    long getScale();
    
    XmlUnsignedInt xgetScale();
    
    boolean isSetScale();
    
    void setScale(final long p0);
    
    void xsetScale(final XmlUnsignedInt p0);
    
    void unsetScale();
    
    long getFirstPageNumber();
    
    XmlUnsignedInt xgetFirstPageNumber();
    
    boolean isSetFirstPageNumber();
    
    void setFirstPageNumber(final long p0);
    
    void xsetFirstPageNumber(final XmlUnsignedInt p0);
    
    void unsetFirstPageNumber();
    
    long getFitToWidth();
    
    XmlUnsignedInt xgetFitToWidth();
    
    boolean isSetFitToWidth();
    
    void setFitToWidth(final long p0);
    
    void xsetFitToWidth(final XmlUnsignedInt p0);
    
    void unsetFitToWidth();
    
    long getFitToHeight();
    
    XmlUnsignedInt xgetFitToHeight();
    
    boolean isSetFitToHeight();
    
    void setFitToHeight(final long p0);
    
    void xsetFitToHeight(final XmlUnsignedInt p0);
    
    void unsetFitToHeight();
    
    STPageOrder.Enum getPageOrder();
    
    STPageOrder xgetPageOrder();
    
    boolean isSetPageOrder();
    
    void setPageOrder(final STPageOrder.Enum p0);
    
    void xsetPageOrder(final STPageOrder p0);
    
    void unsetPageOrder();
    
    STOrientation.Enum getOrientation();
    
    STOrientation xgetOrientation();
    
    boolean isSetOrientation();
    
    void setOrientation(final STOrientation.Enum p0);
    
    void xsetOrientation(final STOrientation p0);
    
    void unsetOrientation();
    
    boolean getUsePrinterDefaults();
    
    XmlBoolean xgetUsePrinterDefaults();
    
    boolean isSetUsePrinterDefaults();
    
    void setUsePrinterDefaults(final boolean p0);
    
    void xsetUsePrinterDefaults(final XmlBoolean p0);
    
    void unsetUsePrinterDefaults();
    
    boolean getBlackAndWhite();
    
    XmlBoolean xgetBlackAndWhite();
    
    boolean isSetBlackAndWhite();
    
    void setBlackAndWhite(final boolean p0);
    
    void xsetBlackAndWhite(final XmlBoolean p0);
    
    void unsetBlackAndWhite();
    
    boolean getDraft();
    
    XmlBoolean xgetDraft();
    
    boolean isSetDraft();
    
    void setDraft(final boolean p0);
    
    void xsetDraft(final XmlBoolean p0);
    
    void unsetDraft();
    
    STCellComments.Enum getCellComments();
    
    STCellComments xgetCellComments();
    
    boolean isSetCellComments();
    
    void setCellComments(final STCellComments.Enum p0);
    
    void xsetCellComments(final STCellComments p0);
    
    void unsetCellComments();
    
    boolean getUseFirstPageNumber();
    
    XmlBoolean xgetUseFirstPageNumber();
    
    boolean isSetUseFirstPageNumber();
    
    void setUseFirstPageNumber(final boolean p0);
    
    void xsetUseFirstPageNumber(final XmlBoolean p0);
    
    void unsetUseFirstPageNumber();
    
    STPrintError.Enum getErrors();
    
    STPrintError xgetErrors();
    
    boolean isSetErrors();
    
    void setErrors(final STPrintError.Enum p0);
    
    void xsetErrors(final STPrintError p0);
    
    void unsetErrors();
    
    long getHorizontalDpi();
    
    XmlUnsignedInt xgetHorizontalDpi();
    
    boolean isSetHorizontalDpi();
    
    void setHorizontalDpi(final long p0);
    
    void xsetHorizontalDpi(final XmlUnsignedInt p0);
    
    void unsetHorizontalDpi();
    
    long getVerticalDpi();
    
    XmlUnsignedInt xgetVerticalDpi();
    
    boolean isSetVerticalDpi();
    
    void setVerticalDpi(final long p0);
    
    void xsetVerticalDpi(final XmlUnsignedInt p0);
    
    void unsetVerticalDpi();
    
    long getCopies();
    
    XmlUnsignedInt xgetCopies();
    
    boolean isSetCopies();
    
    void setCopies(final long p0);
    
    void xsetCopies(final XmlUnsignedInt p0);
    
    void unsetCopies();
    
    String getId();
    
    STRelationshipId xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    void unsetId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPageSetup.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPageSetup newInstance() {
            return (CTPageSetup)getTypeLoader().newInstance(CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup newInstance(final XmlOptions xmlOptions) {
            return (CTPageSetup)getTypeLoader().newInstance(CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final String s) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(s, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(s, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final File file) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(file, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(file, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final URL url) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(url, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(url, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(inputStream, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(inputStream, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final Reader reader) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(reader, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(reader, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(xmlStreamReader, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(xmlStreamReader, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final Node node) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(node, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(node, CTPageSetup.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPageSetup parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPageSetup)getTypeLoader().parse(xmlInputStream, CTPageSetup.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPageSetup parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPageSetup)getTypeLoader().parse(xmlInputStream, CTPageSetup.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageSetup.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageSetup.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

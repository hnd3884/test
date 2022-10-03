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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTXf extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTXf.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctxf97f7type");
    
    CTCellAlignment getAlignment();
    
    boolean isSetAlignment();
    
    void setAlignment(final CTCellAlignment p0);
    
    CTCellAlignment addNewAlignment();
    
    void unsetAlignment();
    
    CTCellProtection getProtection();
    
    boolean isSetProtection();
    
    void setProtection(final CTCellProtection p0);
    
    CTCellProtection addNewProtection();
    
    void unsetProtection();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getNumFmtId();
    
    STNumFmtId xgetNumFmtId();
    
    boolean isSetNumFmtId();
    
    void setNumFmtId(final long p0);
    
    void xsetNumFmtId(final STNumFmtId p0);
    
    void unsetNumFmtId();
    
    long getFontId();
    
    STFontId xgetFontId();
    
    boolean isSetFontId();
    
    void setFontId(final long p0);
    
    void xsetFontId(final STFontId p0);
    
    void unsetFontId();
    
    long getFillId();
    
    STFillId xgetFillId();
    
    boolean isSetFillId();
    
    void setFillId(final long p0);
    
    void xsetFillId(final STFillId p0);
    
    void unsetFillId();
    
    long getBorderId();
    
    STBorderId xgetBorderId();
    
    boolean isSetBorderId();
    
    void setBorderId(final long p0);
    
    void xsetBorderId(final STBorderId p0);
    
    void unsetBorderId();
    
    long getXfId();
    
    STCellStyleXfId xgetXfId();
    
    boolean isSetXfId();
    
    void setXfId(final long p0);
    
    void xsetXfId(final STCellStyleXfId p0);
    
    void unsetXfId();
    
    boolean getQuotePrefix();
    
    XmlBoolean xgetQuotePrefix();
    
    boolean isSetQuotePrefix();
    
    void setQuotePrefix(final boolean p0);
    
    void xsetQuotePrefix(final XmlBoolean p0);
    
    void unsetQuotePrefix();
    
    boolean getPivotButton();
    
    XmlBoolean xgetPivotButton();
    
    boolean isSetPivotButton();
    
    void setPivotButton(final boolean p0);
    
    void xsetPivotButton(final XmlBoolean p0);
    
    void unsetPivotButton();
    
    boolean getApplyNumberFormat();
    
    XmlBoolean xgetApplyNumberFormat();
    
    boolean isSetApplyNumberFormat();
    
    void setApplyNumberFormat(final boolean p0);
    
    void xsetApplyNumberFormat(final XmlBoolean p0);
    
    void unsetApplyNumberFormat();
    
    boolean getApplyFont();
    
    XmlBoolean xgetApplyFont();
    
    boolean isSetApplyFont();
    
    void setApplyFont(final boolean p0);
    
    void xsetApplyFont(final XmlBoolean p0);
    
    void unsetApplyFont();
    
    boolean getApplyFill();
    
    XmlBoolean xgetApplyFill();
    
    boolean isSetApplyFill();
    
    void setApplyFill(final boolean p0);
    
    void xsetApplyFill(final XmlBoolean p0);
    
    void unsetApplyFill();
    
    boolean getApplyBorder();
    
    XmlBoolean xgetApplyBorder();
    
    boolean isSetApplyBorder();
    
    void setApplyBorder(final boolean p0);
    
    void xsetApplyBorder(final XmlBoolean p0);
    
    void unsetApplyBorder();
    
    boolean getApplyAlignment();
    
    XmlBoolean xgetApplyAlignment();
    
    boolean isSetApplyAlignment();
    
    void setApplyAlignment(final boolean p0);
    
    void xsetApplyAlignment(final XmlBoolean p0);
    
    void unsetApplyAlignment();
    
    boolean getApplyProtection();
    
    XmlBoolean xgetApplyProtection();
    
    boolean isSetApplyProtection();
    
    void setApplyProtection(final boolean p0);
    
    void xsetApplyProtection(final XmlBoolean p0);
    
    void unsetApplyProtection();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTXf.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTXf newInstance() {
            return (CTXf)getTypeLoader().newInstance(CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf newInstance(final XmlOptions xmlOptions) {
            return (CTXf)getTypeLoader().newInstance(CTXf.type, xmlOptions);
        }
        
        public static CTXf parse(final String s) throws XmlException {
            return (CTXf)getTypeLoader().parse(s, CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTXf)getTypeLoader().parse(s, CTXf.type, xmlOptions);
        }
        
        public static CTXf parse(final File file) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(file, CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(file, CTXf.type, xmlOptions);
        }
        
        public static CTXf parse(final URL url) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(url, CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(url, CTXf.type, xmlOptions);
        }
        
        public static CTXf parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(inputStream, CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(inputStream, CTXf.type, xmlOptions);
        }
        
        public static CTXf parse(final Reader reader) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(reader, CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXf)getTypeLoader().parse(reader, CTXf.type, xmlOptions);
        }
        
        public static CTXf parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTXf)getTypeLoader().parse(xmlStreamReader, CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTXf)getTypeLoader().parse(xmlStreamReader, CTXf.type, xmlOptions);
        }
        
        public static CTXf parse(final Node node) throws XmlException {
            return (CTXf)getTypeLoader().parse(node, CTXf.type, (XmlOptions)null);
        }
        
        public static CTXf parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTXf)getTypeLoader().parse(node, CTXf.type, xmlOptions);
        }
        
        @Deprecated
        public static CTXf parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTXf)getTypeLoader().parse(xmlInputStream, CTXf.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTXf parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTXf)getTypeLoader().parse(xmlInputStream, CTXf.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXf.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXf.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

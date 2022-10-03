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
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSheetFormatPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetFormatPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetformatprdef7type");
    
    long getBaseColWidth();
    
    XmlUnsignedInt xgetBaseColWidth();
    
    boolean isSetBaseColWidth();
    
    void setBaseColWidth(final long p0);
    
    void xsetBaseColWidth(final XmlUnsignedInt p0);
    
    void unsetBaseColWidth();
    
    double getDefaultColWidth();
    
    XmlDouble xgetDefaultColWidth();
    
    boolean isSetDefaultColWidth();
    
    void setDefaultColWidth(final double p0);
    
    void xsetDefaultColWidth(final XmlDouble p0);
    
    void unsetDefaultColWidth();
    
    double getDefaultRowHeight();
    
    XmlDouble xgetDefaultRowHeight();
    
    void setDefaultRowHeight(final double p0);
    
    void xsetDefaultRowHeight(final XmlDouble p0);
    
    boolean getCustomHeight();
    
    XmlBoolean xgetCustomHeight();
    
    boolean isSetCustomHeight();
    
    void setCustomHeight(final boolean p0);
    
    void xsetCustomHeight(final XmlBoolean p0);
    
    void unsetCustomHeight();
    
    boolean getZeroHeight();
    
    XmlBoolean xgetZeroHeight();
    
    boolean isSetZeroHeight();
    
    void setZeroHeight(final boolean p0);
    
    void xsetZeroHeight(final XmlBoolean p0);
    
    void unsetZeroHeight();
    
    boolean getThickTop();
    
    XmlBoolean xgetThickTop();
    
    boolean isSetThickTop();
    
    void setThickTop(final boolean p0);
    
    void xsetThickTop(final XmlBoolean p0);
    
    void unsetThickTop();
    
    boolean getThickBottom();
    
    XmlBoolean xgetThickBottom();
    
    boolean isSetThickBottom();
    
    void setThickBottom(final boolean p0);
    
    void xsetThickBottom(final XmlBoolean p0);
    
    void unsetThickBottom();
    
    short getOutlineLevelRow();
    
    XmlUnsignedByte xgetOutlineLevelRow();
    
    boolean isSetOutlineLevelRow();
    
    void setOutlineLevelRow(final short p0);
    
    void xsetOutlineLevelRow(final XmlUnsignedByte p0);
    
    void unsetOutlineLevelRow();
    
    short getOutlineLevelCol();
    
    XmlUnsignedByte xgetOutlineLevelCol();
    
    boolean isSetOutlineLevelCol();
    
    void setOutlineLevelCol(final short p0);
    
    void xsetOutlineLevelCol(final XmlUnsignedByte p0);
    
    void unsetOutlineLevelCol();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetFormatPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetFormatPr newInstance() {
            return (CTSheetFormatPr)getTypeLoader().newInstance(CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr newInstance(final XmlOptions xmlOptions) {
            return (CTSheetFormatPr)getTypeLoader().newInstance(CTSheetFormatPr.type, xmlOptions);
        }
        
        public static CTSheetFormatPr parse(final String s) throws XmlException {
            return (CTSheetFormatPr)getTypeLoader().parse(s, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetFormatPr)getTypeLoader().parse(s, CTSheetFormatPr.type, xmlOptions);
        }
        
        public static CTSheetFormatPr parse(final File file) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(file, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(file, CTSheetFormatPr.type, xmlOptions);
        }
        
        public static CTSheetFormatPr parse(final URL url) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(url, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(url, CTSheetFormatPr.type, xmlOptions);
        }
        
        public static CTSheetFormatPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(inputStream, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(inputStream, CTSheetFormatPr.type, xmlOptions);
        }
        
        public static CTSheetFormatPr parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(reader, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetFormatPr)getTypeLoader().parse(reader, CTSheetFormatPr.type, xmlOptions);
        }
        
        public static CTSheetFormatPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetFormatPr)getTypeLoader().parse(xmlStreamReader, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetFormatPr)getTypeLoader().parse(xmlStreamReader, CTSheetFormatPr.type, xmlOptions);
        }
        
        public static CTSheetFormatPr parse(final Node node) throws XmlException {
            return (CTSheetFormatPr)getTypeLoader().parse(node, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        public static CTSheetFormatPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetFormatPr)getTypeLoader().parse(node, CTSheetFormatPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetFormatPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetFormatPr)getTypeLoader().parse(xmlInputStream, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetFormatPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetFormatPr)getTypeLoader().parse(xmlInputStream, CTSheetFormatPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetFormatPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetFormatPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

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
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCellAlignment extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCellAlignment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcellalignmentb580type");
    
    STHorizontalAlignment.Enum getHorizontal();
    
    STHorizontalAlignment xgetHorizontal();
    
    boolean isSetHorizontal();
    
    void setHorizontal(final STHorizontalAlignment.Enum p0);
    
    void xsetHorizontal(final STHorizontalAlignment p0);
    
    void unsetHorizontal();
    
    STVerticalAlignment.Enum getVertical();
    
    STVerticalAlignment xgetVertical();
    
    boolean isSetVertical();
    
    void setVertical(final STVerticalAlignment.Enum p0);
    
    void xsetVertical(final STVerticalAlignment p0);
    
    void unsetVertical();
    
    long getTextRotation();
    
    XmlUnsignedInt xgetTextRotation();
    
    boolean isSetTextRotation();
    
    void setTextRotation(final long p0);
    
    void xsetTextRotation(final XmlUnsignedInt p0);
    
    void unsetTextRotation();
    
    boolean getWrapText();
    
    XmlBoolean xgetWrapText();
    
    boolean isSetWrapText();
    
    void setWrapText(final boolean p0);
    
    void xsetWrapText(final XmlBoolean p0);
    
    void unsetWrapText();
    
    long getIndent();
    
    XmlUnsignedInt xgetIndent();
    
    boolean isSetIndent();
    
    void setIndent(final long p0);
    
    void xsetIndent(final XmlUnsignedInt p0);
    
    void unsetIndent();
    
    int getRelativeIndent();
    
    XmlInt xgetRelativeIndent();
    
    boolean isSetRelativeIndent();
    
    void setRelativeIndent(final int p0);
    
    void xsetRelativeIndent(final XmlInt p0);
    
    void unsetRelativeIndent();
    
    boolean getJustifyLastLine();
    
    XmlBoolean xgetJustifyLastLine();
    
    boolean isSetJustifyLastLine();
    
    void setJustifyLastLine(final boolean p0);
    
    void xsetJustifyLastLine(final XmlBoolean p0);
    
    void unsetJustifyLastLine();
    
    boolean getShrinkToFit();
    
    XmlBoolean xgetShrinkToFit();
    
    boolean isSetShrinkToFit();
    
    void setShrinkToFit(final boolean p0);
    
    void xsetShrinkToFit(final XmlBoolean p0);
    
    void unsetShrinkToFit();
    
    long getReadingOrder();
    
    XmlUnsignedInt xgetReadingOrder();
    
    boolean isSetReadingOrder();
    
    void setReadingOrder(final long p0);
    
    void xsetReadingOrder(final XmlUnsignedInt p0);
    
    void unsetReadingOrder();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCellAlignment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCellAlignment newInstance() {
            return (CTCellAlignment)getTypeLoader().newInstance(CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment newInstance(final XmlOptions xmlOptions) {
            return (CTCellAlignment)getTypeLoader().newInstance(CTCellAlignment.type, xmlOptions);
        }
        
        public static CTCellAlignment parse(final String s) throws XmlException {
            return (CTCellAlignment)getTypeLoader().parse(s, CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellAlignment)getTypeLoader().parse(s, CTCellAlignment.type, xmlOptions);
        }
        
        public static CTCellAlignment parse(final File file) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(file, CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(file, CTCellAlignment.type, xmlOptions);
        }
        
        public static CTCellAlignment parse(final URL url) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(url, CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(url, CTCellAlignment.type, xmlOptions);
        }
        
        public static CTCellAlignment parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(inputStream, CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(inputStream, CTCellAlignment.type, xmlOptions);
        }
        
        public static CTCellAlignment parse(final Reader reader) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(reader, CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellAlignment)getTypeLoader().parse(reader, CTCellAlignment.type, xmlOptions);
        }
        
        public static CTCellAlignment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCellAlignment)getTypeLoader().parse(xmlStreamReader, CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellAlignment)getTypeLoader().parse(xmlStreamReader, CTCellAlignment.type, xmlOptions);
        }
        
        public static CTCellAlignment parse(final Node node) throws XmlException {
            return (CTCellAlignment)getTypeLoader().parse(node, CTCellAlignment.type, (XmlOptions)null);
        }
        
        public static CTCellAlignment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellAlignment)getTypeLoader().parse(node, CTCellAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCellAlignment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCellAlignment)getTypeLoader().parse(xmlInputStream, CTCellAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCellAlignment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCellAlignment)getTypeLoader().parse(xmlInputStream, CTCellAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellAlignment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

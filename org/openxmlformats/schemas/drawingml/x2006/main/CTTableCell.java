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
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableCell extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableCell.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablecell3ac1type");
    
    CTTextBody getTxBody();
    
    boolean isSetTxBody();
    
    void setTxBody(final CTTextBody p0);
    
    CTTextBody addNewTxBody();
    
    void unsetTxBody();
    
    CTTableCellProperties getTcPr();
    
    boolean isSetTcPr();
    
    void setTcPr(final CTTableCellProperties p0);
    
    CTTableCellProperties addNewTcPr();
    
    void unsetTcPr();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    int getRowSpan();
    
    XmlInt xgetRowSpan();
    
    boolean isSetRowSpan();
    
    void setRowSpan(final int p0);
    
    void xsetRowSpan(final XmlInt p0);
    
    void unsetRowSpan();
    
    int getGridSpan();
    
    XmlInt xgetGridSpan();
    
    boolean isSetGridSpan();
    
    void setGridSpan(final int p0);
    
    void xsetGridSpan(final XmlInt p0);
    
    void unsetGridSpan();
    
    boolean getHMerge();
    
    XmlBoolean xgetHMerge();
    
    boolean isSetHMerge();
    
    void setHMerge(final boolean p0);
    
    void xsetHMerge(final XmlBoolean p0);
    
    void unsetHMerge();
    
    boolean getVMerge();
    
    XmlBoolean xgetVMerge();
    
    boolean isSetVMerge();
    
    void setVMerge(final boolean p0);
    
    void xsetVMerge(final XmlBoolean p0);
    
    void unsetVMerge();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableCell.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableCell newInstance() {
            return (CTTableCell)getTypeLoader().newInstance(CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell newInstance(final XmlOptions xmlOptions) {
            return (CTTableCell)getTypeLoader().newInstance(CTTableCell.type, xmlOptions);
        }
        
        public static CTTableCell parse(final String s) throws XmlException {
            return (CTTableCell)getTypeLoader().parse(s, CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCell)getTypeLoader().parse(s, CTTableCell.type, xmlOptions);
        }
        
        public static CTTableCell parse(final File file) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(file, CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(file, CTTableCell.type, xmlOptions);
        }
        
        public static CTTableCell parse(final URL url) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(url, CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(url, CTTableCell.type, xmlOptions);
        }
        
        public static CTTableCell parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(inputStream, CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(inputStream, CTTableCell.type, xmlOptions);
        }
        
        public static CTTableCell parse(final Reader reader) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(reader, CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCell)getTypeLoader().parse(reader, CTTableCell.type, xmlOptions);
        }
        
        public static CTTableCell parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableCell)getTypeLoader().parse(xmlStreamReader, CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCell)getTypeLoader().parse(xmlStreamReader, CTTableCell.type, xmlOptions);
        }
        
        public static CTTableCell parse(final Node node) throws XmlException {
            return (CTTableCell)getTypeLoader().parse(node, CTTableCell.type, (XmlOptions)null);
        }
        
        public static CTTableCell parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCell)getTypeLoader().parse(node, CTTableCell.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableCell parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableCell)getTypeLoader().parse(xmlInputStream, CTTableCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableCell parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableCell)getTypeLoader().parse(xmlInputStream, CTTableCell.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableCell.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

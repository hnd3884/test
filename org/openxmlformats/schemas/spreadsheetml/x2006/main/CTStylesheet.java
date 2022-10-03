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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStylesheet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStylesheet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstylesheet4257type");
    
    CTNumFmts getNumFmts();
    
    boolean isSetNumFmts();
    
    void setNumFmts(final CTNumFmts p0);
    
    CTNumFmts addNewNumFmts();
    
    void unsetNumFmts();
    
    CTFonts getFonts();
    
    boolean isSetFonts();
    
    void setFonts(final CTFonts p0);
    
    CTFonts addNewFonts();
    
    void unsetFonts();
    
    CTFills getFills();
    
    boolean isSetFills();
    
    void setFills(final CTFills p0);
    
    CTFills addNewFills();
    
    void unsetFills();
    
    CTBorders getBorders();
    
    boolean isSetBorders();
    
    void setBorders(final CTBorders p0);
    
    CTBorders addNewBorders();
    
    void unsetBorders();
    
    CTCellStyleXfs getCellStyleXfs();
    
    boolean isSetCellStyleXfs();
    
    void setCellStyleXfs(final CTCellStyleXfs p0);
    
    CTCellStyleXfs addNewCellStyleXfs();
    
    void unsetCellStyleXfs();
    
    CTCellXfs getCellXfs();
    
    boolean isSetCellXfs();
    
    void setCellXfs(final CTCellXfs p0);
    
    CTCellXfs addNewCellXfs();
    
    void unsetCellXfs();
    
    CTCellStyles getCellStyles();
    
    boolean isSetCellStyles();
    
    void setCellStyles(final CTCellStyles p0);
    
    CTCellStyles addNewCellStyles();
    
    void unsetCellStyles();
    
    CTDxfs getDxfs();
    
    boolean isSetDxfs();
    
    void setDxfs(final CTDxfs p0);
    
    CTDxfs addNewDxfs();
    
    void unsetDxfs();
    
    CTTableStyles getTableStyles();
    
    boolean isSetTableStyles();
    
    void setTableStyles(final CTTableStyles p0);
    
    CTTableStyles addNewTableStyles();
    
    void unsetTableStyles();
    
    CTColors getColors();
    
    boolean isSetColors();
    
    void setColors(final CTColors p0);
    
    CTColors addNewColors();
    
    void unsetColors();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStylesheet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStylesheet newInstance() {
            return (CTStylesheet)getTypeLoader().newInstance(CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet newInstance(final XmlOptions xmlOptions) {
            return (CTStylesheet)getTypeLoader().newInstance(CTStylesheet.type, xmlOptions);
        }
        
        public static CTStylesheet parse(final String s) throws XmlException {
            return (CTStylesheet)getTypeLoader().parse(s, CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStylesheet)getTypeLoader().parse(s, CTStylesheet.type, xmlOptions);
        }
        
        public static CTStylesheet parse(final File file) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(file, CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(file, CTStylesheet.type, xmlOptions);
        }
        
        public static CTStylesheet parse(final URL url) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(url, CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(url, CTStylesheet.type, xmlOptions);
        }
        
        public static CTStylesheet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(inputStream, CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(inputStream, CTStylesheet.type, xmlOptions);
        }
        
        public static CTStylesheet parse(final Reader reader) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(reader, CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStylesheet)getTypeLoader().parse(reader, CTStylesheet.type, xmlOptions);
        }
        
        public static CTStylesheet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStylesheet)getTypeLoader().parse(xmlStreamReader, CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStylesheet)getTypeLoader().parse(xmlStreamReader, CTStylesheet.type, xmlOptions);
        }
        
        public static CTStylesheet parse(final Node node) throws XmlException {
            return (CTStylesheet)getTypeLoader().parse(node, CTStylesheet.type, (XmlOptions)null);
        }
        
        public static CTStylesheet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStylesheet)getTypeLoader().parse(node, CTStylesheet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStylesheet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStylesheet)getTypeLoader().parse(xmlInputStream, CTStylesheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStylesheet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStylesheet)getTypeLoader().parse(xmlInputStream, CTStylesheet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStylesheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStylesheet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

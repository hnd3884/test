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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTIgnoredError extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTIgnoredError.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctignorederrorc51ftype");
    
    List getSqref();
    
    STSqref xgetSqref();
    
    void setSqref(final List p0);
    
    void xsetSqref(final STSqref p0);
    
    boolean getEvalError();
    
    XmlBoolean xgetEvalError();
    
    boolean isSetEvalError();
    
    void setEvalError(final boolean p0);
    
    void xsetEvalError(final XmlBoolean p0);
    
    void unsetEvalError();
    
    boolean getTwoDigitTextYear();
    
    XmlBoolean xgetTwoDigitTextYear();
    
    boolean isSetTwoDigitTextYear();
    
    void setTwoDigitTextYear(final boolean p0);
    
    void xsetTwoDigitTextYear(final XmlBoolean p0);
    
    void unsetTwoDigitTextYear();
    
    boolean getNumberStoredAsText();
    
    XmlBoolean xgetNumberStoredAsText();
    
    boolean isSetNumberStoredAsText();
    
    void setNumberStoredAsText(final boolean p0);
    
    void xsetNumberStoredAsText(final XmlBoolean p0);
    
    void unsetNumberStoredAsText();
    
    boolean getFormula();
    
    XmlBoolean xgetFormula();
    
    boolean isSetFormula();
    
    void setFormula(final boolean p0);
    
    void xsetFormula(final XmlBoolean p0);
    
    void unsetFormula();
    
    boolean getFormulaRange();
    
    XmlBoolean xgetFormulaRange();
    
    boolean isSetFormulaRange();
    
    void setFormulaRange(final boolean p0);
    
    void xsetFormulaRange(final XmlBoolean p0);
    
    void unsetFormulaRange();
    
    boolean getUnlockedFormula();
    
    XmlBoolean xgetUnlockedFormula();
    
    boolean isSetUnlockedFormula();
    
    void setUnlockedFormula(final boolean p0);
    
    void xsetUnlockedFormula(final XmlBoolean p0);
    
    void unsetUnlockedFormula();
    
    boolean getEmptyCellReference();
    
    XmlBoolean xgetEmptyCellReference();
    
    boolean isSetEmptyCellReference();
    
    void setEmptyCellReference(final boolean p0);
    
    void xsetEmptyCellReference(final XmlBoolean p0);
    
    void unsetEmptyCellReference();
    
    boolean getListDataValidation();
    
    XmlBoolean xgetListDataValidation();
    
    boolean isSetListDataValidation();
    
    void setListDataValidation(final boolean p0);
    
    void xsetListDataValidation(final XmlBoolean p0);
    
    void unsetListDataValidation();
    
    boolean getCalculatedColumn();
    
    XmlBoolean xgetCalculatedColumn();
    
    boolean isSetCalculatedColumn();
    
    void setCalculatedColumn(final boolean p0);
    
    void xsetCalculatedColumn(final XmlBoolean p0);
    
    void unsetCalculatedColumn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTIgnoredError.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTIgnoredError newInstance() {
            return (CTIgnoredError)getTypeLoader().newInstance(CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError newInstance(final XmlOptions xmlOptions) {
            return (CTIgnoredError)getTypeLoader().newInstance(CTIgnoredError.type, xmlOptions);
        }
        
        public static CTIgnoredError parse(final String s) throws XmlException {
            return (CTIgnoredError)getTypeLoader().parse(s, CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTIgnoredError)getTypeLoader().parse(s, CTIgnoredError.type, xmlOptions);
        }
        
        public static CTIgnoredError parse(final File file) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(file, CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(file, CTIgnoredError.type, xmlOptions);
        }
        
        public static CTIgnoredError parse(final URL url) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(url, CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(url, CTIgnoredError.type, xmlOptions);
        }
        
        public static CTIgnoredError parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(inputStream, CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(inputStream, CTIgnoredError.type, xmlOptions);
        }
        
        public static CTIgnoredError parse(final Reader reader) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(reader, CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredError)getTypeLoader().parse(reader, CTIgnoredError.type, xmlOptions);
        }
        
        public static CTIgnoredError parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTIgnoredError)getTypeLoader().parse(xmlStreamReader, CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTIgnoredError)getTypeLoader().parse(xmlStreamReader, CTIgnoredError.type, xmlOptions);
        }
        
        public static CTIgnoredError parse(final Node node) throws XmlException {
            return (CTIgnoredError)getTypeLoader().parse(node, CTIgnoredError.type, (XmlOptions)null);
        }
        
        public static CTIgnoredError parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTIgnoredError)getTypeLoader().parse(node, CTIgnoredError.type, xmlOptions);
        }
        
        @Deprecated
        public static CTIgnoredError parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTIgnoredError)getTypeLoader().parse(xmlInputStream, CTIgnoredError.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTIgnoredError parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTIgnoredError)getTypeLoader().parse(xmlInputStream, CTIgnoredError.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIgnoredError.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIgnoredError.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

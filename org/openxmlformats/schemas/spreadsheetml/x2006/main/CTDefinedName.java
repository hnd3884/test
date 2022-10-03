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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;

public interface CTDefinedName extends STFormula
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDefinedName.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdefinedname9413type");
    
    String getName();
    
    STXstring xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    String getComment();
    
    STXstring xgetComment();
    
    boolean isSetComment();
    
    void setComment(final String p0);
    
    void xsetComment(final STXstring p0);
    
    void unsetComment();
    
    String getCustomMenu();
    
    STXstring xgetCustomMenu();
    
    boolean isSetCustomMenu();
    
    void setCustomMenu(final String p0);
    
    void xsetCustomMenu(final STXstring p0);
    
    void unsetCustomMenu();
    
    String getDescription();
    
    STXstring xgetDescription();
    
    boolean isSetDescription();
    
    void setDescription(final String p0);
    
    void xsetDescription(final STXstring p0);
    
    void unsetDescription();
    
    String getHelp();
    
    STXstring xgetHelp();
    
    boolean isSetHelp();
    
    void setHelp(final String p0);
    
    void xsetHelp(final STXstring p0);
    
    void unsetHelp();
    
    String getStatusBar();
    
    STXstring xgetStatusBar();
    
    boolean isSetStatusBar();
    
    void setStatusBar(final String p0);
    
    void xsetStatusBar(final STXstring p0);
    
    void unsetStatusBar();
    
    long getLocalSheetId();
    
    XmlUnsignedInt xgetLocalSheetId();
    
    boolean isSetLocalSheetId();
    
    void setLocalSheetId(final long p0);
    
    void xsetLocalSheetId(final XmlUnsignedInt p0);
    
    void unsetLocalSheetId();
    
    boolean getHidden();
    
    XmlBoolean xgetHidden();
    
    boolean isSetHidden();
    
    void setHidden(final boolean p0);
    
    void xsetHidden(final XmlBoolean p0);
    
    void unsetHidden();
    
    boolean getFunction();
    
    XmlBoolean xgetFunction();
    
    boolean isSetFunction();
    
    void setFunction(final boolean p0);
    
    void xsetFunction(final XmlBoolean p0);
    
    void unsetFunction();
    
    boolean getVbProcedure();
    
    XmlBoolean xgetVbProcedure();
    
    boolean isSetVbProcedure();
    
    void setVbProcedure(final boolean p0);
    
    void xsetVbProcedure(final XmlBoolean p0);
    
    void unsetVbProcedure();
    
    boolean getXlm();
    
    XmlBoolean xgetXlm();
    
    boolean isSetXlm();
    
    void setXlm(final boolean p0);
    
    void xsetXlm(final XmlBoolean p0);
    
    void unsetXlm();
    
    long getFunctionGroupId();
    
    XmlUnsignedInt xgetFunctionGroupId();
    
    boolean isSetFunctionGroupId();
    
    void setFunctionGroupId(final long p0);
    
    void xsetFunctionGroupId(final XmlUnsignedInt p0);
    
    void unsetFunctionGroupId();
    
    String getShortcutKey();
    
    STXstring xgetShortcutKey();
    
    boolean isSetShortcutKey();
    
    void setShortcutKey(final String p0);
    
    void xsetShortcutKey(final STXstring p0);
    
    void unsetShortcutKey();
    
    boolean getPublishToServer();
    
    XmlBoolean xgetPublishToServer();
    
    boolean isSetPublishToServer();
    
    void setPublishToServer(final boolean p0);
    
    void xsetPublishToServer(final XmlBoolean p0);
    
    void unsetPublishToServer();
    
    boolean getWorkbookParameter();
    
    XmlBoolean xgetWorkbookParameter();
    
    boolean isSetWorkbookParameter();
    
    void setWorkbookParameter(final boolean p0);
    
    void xsetWorkbookParameter(final XmlBoolean p0);
    
    void unsetWorkbookParameter();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDefinedName.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDefinedName newInstance() {
            return (CTDefinedName)getTypeLoader().newInstance(CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName newInstance(final XmlOptions xmlOptions) {
            return (CTDefinedName)getTypeLoader().newInstance(CTDefinedName.type, xmlOptions);
        }
        
        public static CTDefinedName parse(final String s) throws XmlException {
            return (CTDefinedName)getTypeLoader().parse(s, CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDefinedName)getTypeLoader().parse(s, CTDefinedName.type, xmlOptions);
        }
        
        public static CTDefinedName parse(final File file) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(file, CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(file, CTDefinedName.type, xmlOptions);
        }
        
        public static CTDefinedName parse(final URL url) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(url, CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(url, CTDefinedName.type, xmlOptions);
        }
        
        public static CTDefinedName parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(inputStream, CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(inputStream, CTDefinedName.type, xmlOptions);
        }
        
        public static CTDefinedName parse(final Reader reader) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(reader, CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedName)getTypeLoader().parse(reader, CTDefinedName.type, xmlOptions);
        }
        
        public static CTDefinedName parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDefinedName)getTypeLoader().parse(xmlStreamReader, CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDefinedName)getTypeLoader().parse(xmlStreamReader, CTDefinedName.type, xmlOptions);
        }
        
        public static CTDefinedName parse(final Node node) throws XmlException {
            return (CTDefinedName)getTypeLoader().parse(node, CTDefinedName.type, (XmlOptions)null);
        }
        
        public static CTDefinedName parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDefinedName)getTypeLoader().parse(node, CTDefinedName.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDefinedName parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDefinedName)getTypeLoader().parse(xmlInputStream, CTDefinedName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDefinedName parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDefinedName)getTypeLoader().parse(xmlInputStream, CTDefinedName.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDefinedName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDefinedName.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}

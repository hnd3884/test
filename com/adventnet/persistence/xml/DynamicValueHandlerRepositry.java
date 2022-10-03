package com.adventnet.persistence.xml;

import java.io.File;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.adventnet.persistence.template.TemplateUtil;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import java.util.logging.Level;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilder;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.Properties;
import java.util.Hashtable;
import org.w3c.dom.Document;
import java.util.logging.Logger;

public class DynamicValueHandlerRepositry
{
    private static final Logger LOGGER;
    Document document;
    public static Hashtable dynamicHandlers;
    private static Hashtable<String, XmlRowTransformer> dynamicRowHanlders;
    private static Hashtable<String, Properties> columnvisibilityPropHanlders;
    private static Hashtable<String, Properties> columnNamesPropHanlders;
    private static Hashtable<String, String> tableDisplayNames;
    
    public DynamicValueHandlerRepositry() {
    }
    
    public void parse(final URL url) throws SAXException {
        DynamicValueHandlerRepositry.LOGGER.info("Going to load DVH :: " + url.toString());
        this.loadDocument(new InputSource(url.toExternalForm()));
        this.visitDocument();
    }
    
    public static XmlRowTransformer getRowTransformer(final String tableName) {
        return DynamicValueHandlerRepositry.dynamicRowHanlders.get(tableName);
    }
    
    public static DVHandlerTemplate getDVHandlerTemplate(final String tableName, final String columnName) {
        final String key = tableName + ":" + columnName;
        return DynamicValueHandlerRepositry.dynamicHandlers.get(key);
    }
    
    public static Properties getColumnVisibilityProperties(final String tableName) {
        if (DynamicValueHandlerRepositry.columnvisibilityPropHanlders.containsKey(tableName)) {
            return DynamicValueHandlerRepositry.columnvisibilityPropHanlders.get(tableName);
        }
        return null;
    }
    
    public static Properties getColumnNamesProperties(final String tableName) {
        if (DynamicValueHandlerRepositry.columnNamesPropHanlders.containsKey(tableName)) {
            return DynamicValueHandlerRepositry.columnNamesPropHanlders.get(tableName);
        }
        return null;
    }
    
    public static String getTableDisplayName(final String tableName) {
        if (DynamicValueHandlerRepositry.tableDisplayNames.containsKey(tableName)) {
            return DynamicValueHandlerRepositry.tableDisplayNames.get(tableName);
        }
        return null;
    }
    
    public static String getTableName(final String displayName) {
        for (final Map.Entry table : DynamicValueHandlerRepositry.tableDisplayNames.entrySet()) {
            final String tableName = table.getKey().toString();
            final String disName = table.getValue().toString();
            if (disName.toLowerCase().equals(displayName)) {
                return tableName;
            }
        }
        return null;
    }
    
    private void loadDocument(final InputSource inputsource) throws SAXException {
        try {
            final DocumentBuilder documentbuilder = SecurityUtil.createDocumentBuilder(true, true, (Properties)null);
            documentbuilder.setErrorHandler(this.getDefaultErrorHandler());
            documentbuilder.setEntityResolver(this.getDefaultEntityResolver());
            this.document = documentbuilder.parse(inputsource);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            final SAXException sax = new SAXException(exp);
            throw sax;
        }
    }
    
    protected EntityResolver getDefaultEntityResolver() {
        return new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
                DynamicValueHandlerRepositry.LOGGER.log(Level.FINE, "dvh parser,Entered resolveEntity :: publicId :: [{0}], systemId :: [{1}]", new Object[] { publicId, systemId });
                if (publicId != null) {
                    throw new SAXException("Trying to parse a PUBLIC entity");
                }
                if (systemId == null) {
                    throw new SAXException("SystemID cannot be [null]");
                }
                if (systemId.indexOf("http:") >= 0) {
                    throw new SAXException("Invalid Entity ::[" + systemId + "]");
                }
                if (!systemId.endsWith(".xml")) {
                    throw new SAXException(" URL :: [" + systemId + "] is not an xml file");
                }
                return null;
            }
        };
    }
    
    protected ErrorHandler getDefaultErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void error(final SAXParseException ex) throws SAXException {
                throw ex;
            }
            
            @Override
            public void fatalError(final SAXParseException ex) throws SAXException {
                throw ex;
            }
            
            @Override
            public void warning(final SAXParseException ex) throws SAXException {
            }
        };
    }
    
    DynamicValueHandlerRepositry(final Document document) {
        this.document = document;
    }
    
    public void visitDocument() throws SAXException {
        final Element element = this.document.getDocumentElement();
        if (null == element) {
            return;
        }
        final String tagName = element.getTagName();
        if (tagName.equals("DVPHParameter")) {
            this.visitElement_DVPHParameter(element, null);
        }
        else if (tagName.equals("DVPHParameterList")) {
            this.visitElement_DVPHParameterList(element);
        }
        else if (tagName.equals("DynamicValueHandler")) {
            this.visitElement_DynamicValueHandler(element);
        }
        else if (tagName.equals("dynamic-value-handlers")) {
            this.visitElement_dynamic_value_handlers(element);
        }
    }
    
    void visitElement_DVPHParameter(final Element element, final Properties prop) {
        final String key = element.getAttribute("name");
        final String value = element.getAttribute("value");
        if (value != null && key != null) {
            if ("referred-table".equals(key) && TemplateUtil.isTemplate(value)) {
                throw new UnsupportedOperationException("Not supported. [" + value + "] is a template-table");
            }
            ((Hashtable<String, String>)prop).put(key, value);
        }
    }
    
    void visitElement_ColumnVisibilityParameter(final Element element, final Properties prop) {
        final String key = element.getAttribute("property");
        final String value = element.getAttribute("value");
        if (value != null && key != null) {
            final StringTokenizer columns = new StringTokenizer(value, " *, *");
            final ArrayList columnNames = new ArrayList();
            while (columns.hasMoreElements()) {
                columnNames.add(columns.nextElement());
            }
            ((Hashtable<String, ArrayList>)prop).put(key, columnNames);
        }
    }
    
    void handleRowTransformer(final Element element) throws SAXException {
        final String tableName = element.getAttribute("tablename");
        final String className = element.getAttribute("classname");
        if (element.hasAttribute("display-name")) {
            final String displayname = element.getAttribute("display-name");
            DynamicValueHandlerRepositry.tableDisplayNames.put(tableName, displayname);
        }
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class userRowTransformer = null;
        XmlRowTransformer rowProcessInstance = null;
        final Properties visibilityProps = new Properties();
        final Properties columnNameProps = new Properties();
        try {
            userRowTransformer = cl.loadClass(className);
            rowProcessInstance = userRowTransformer.newInstance();
            final NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                final Node node = nodes.item(i);
                switch (node.getNodeType()) {
                    case 1: {
                        final Element nodeElement = (Element)node;
                        if (nodeElement.getTagName().equals("Columns_Visibility")) {
                            this.visitElement_ColumnVisibilityParameter(nodeElement, visibilityProps);
                        }
                        if (nodeElement.getTagName().equals("Columns_DisplayName")) {
                            this.visitElement_DVPHParameter(nodeElement, columnNameProps);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        catch (final Exception e1) {
            throw new SAXException(e1);
        }
        DynamicValueHandlerRepositry.dynamicRowHanlders.put(tableName, rowProcessInstance);
        DynamicValueHandlerRepositry.columnvisibilityPropHanlders.put(tableName, visibilityProps);
        DynamicValueHandlerRepositry.columnNamesPropHanlders.put(tableName, columnNameProps);
    }
    
    Properties visitElement_DVPHParameterList(final Element element) {
        final NodeList nodes = element.getChildNodes();
        final Properties configuredProps = new Properties();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("DVPHParameter")) {
                        this.visitElement_DVPHParameter(nodeElement, configuredProps);
                        break;
                    }
                    break;
                }
            }
        }
        return configuredProps;
    }
    
    void visitElement_DynamicValueHandler(final Element element) throws SAXException {
        final DVHandlerTemplate dVHandler = new DVHandlerTemplate();
        final NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("columnname")) {
                dVHandler.setColumnName(attr.getValue());
            }
            if (attr.getName().equals("class")) {
                final String className = attr.getValue();
                try {
                    final DynamicValueHandler handler = (DynamicValueHandler)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
                    dVHandler.setDynamicValueHandler(handler);
                }
                catch (final Exception ex) {
                    final SAXException sax = new SAXException(ex);
                    throw sax;
                }
            }
            if (attr.getName().equals("tablename")) {
                final String tableName = attr.getValue();
                if (TemplateUtil.isTemplate(tableName)) {
                    throw new UnsupportedOperationException("Not supported. [" + tableName + "] is a template-table");
                }
                dVHandler.setTableName(tableName);
            }
        }
        final NodeList nodes = element.getChildNodes();
        for (int j = 0; j < nodes.getLength(); ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("DVPHParameterList")) {
                        final Properties props = this.visitElement_DVPHParameterList(nodeElement);
                        dVHandler.setConfiguredAttributes(props);
                        break;
                    }
                    break;
                }
            }
        }
        final String key = dVHandler.getTableName() + ":" + dVHandler.getColumnName();
        DynamicValueHandlerRepositry.dynamicHandlers.put(key, dVHandler);
    }
    
    void visitElement_dynamic_value_handlers(final Element element) throws SAXException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("DynamicValueHandler")) {
                        this.visitElement_DynamicValueHandler(nodeElement);
                        break;
                    }
                    if (nodeElement.getTagName().equals("RowTransformer")) {
                        this.handleRowTransformer(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public static void main(final String[] args) {
        try {
            final URL url = new File("DynamicValueHandler.xml").toURL();
            final DynamicValueHandlerRepositry rep = new DynamicValueHandlerRepositry();
            rep.parse(url);
            DynamicValueHandlerRepositry.LOGGER.fine("DEBUG******" + DynamicValueHandlerRepositry.dynamicHandlers + "**********");
            final DVHandlerTemplate dv = DynamicValueHandlerRepositry.dynamicHandlers.get("TestTable:TestColumn");
            DynamicValueHandlerRepositry.LOGGER.fine("DEBUG******" + dv.getConfiguredAttributes() + "**********");
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DynamicValueHandlerRepositry.class.getName());
        DynamicValueHandlerRepositry.dynamicHandlers = new Hashtable();
        DynamicValueHandlerRepositry.dynamicRowHanlders = new Hashtable<String, XmlRowTransformer>();
        DynamicValueHandlerRepositry.columnvisibilityPropHanlders = new Hashtable<String, Properties>();
        DynamicValueHandlerRepositry.columnNamesPropHanlders = new Hashtable<String, Properties>();
        DynamicValueHandlerRepositry.tableDisplayNames = new Hashtable<String, String>();
    }
}

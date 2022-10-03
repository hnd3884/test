package com.adventnet.persistence;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import javax.xml.transform.dom.DOMSource;
import com.zoho.mickey.api.TransformerFactoryUtil;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import java.util.Collection;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import java.io.File;
import java.util.logging.Logger;
import java.util.List;
import java.util.Properties;
import java.util.HashMap;

public class ConfigurationParser
{
    HashMap<String, String> confNameVsValue;
    HashMap<String, Properties> confNameVsProps;
    HashMap<String, List<String>> confNameVsList;
    Properties dbProps;
    private static final Logger LOGGER;
    
    public ConfigurationParser() {
        this.confNameVsValue = null;
        this.confNameVsProps = null;
        this.confNameVsList = null;
        this.dbProps = null;
        this.confNameVsValue = new HashMap<String, String>();
        this.confNameVsProps = new HashMap<String, Properties>();
        this.confNameVsList = new HashMap<String, List<String>>();
        this.dbProps = new Properties();
    }
    
    public ConfigurationParser(final String fileName) throws Exception {
        this.confNameVsValue = null;
        this.confNameVsProps = null;
        this.confNameVsList = null;
        this.dbProps = null;
        try {
            final DocumentBuilder builder = getDocumentBuilder();
            final File file = new File(fileName);
            final URL url = file.toURL();
            final String extForm = url.toExternalForm();
            final Document document = builder.parse(new InputSource(extForm));
            this.confNameVsValue = new HashMap<String, String>();
            this.confNameVsProps = new HashMap<String, Properties>();
            this.confNameVsList = new HashMap<String, List<String>>();
            this.parseDocument(document);
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Exception occured while parsing the file :: " + fileName);
            throw e;
        }
    }
    
    public String getConfigurationValue(final String confName) {
        return this.confNameVsValue.get(confName);
    }
    
    public Properties getConfigurationProps(final String confName) {
        return this.confNameVsProps.get(confName);
    }
    
    public List<String> getConfigurationList(final String confName) {
        return this.confNameVsList.get(confName);
    }
    
    public HashMap<String, String> getConfigurationValues() {
        return this.confNameVsValue;
    }
    
    public HashMap<String, List<String>> getConfigurationList() {
        return this.confNameVsList;
    }
    
    public HashMap<String, Properties> getConfigurationProps() {
        return this.confNameVsProps;
    }
    
    public Properties getDBProperties() {
        return this.dbProps;
    }
    
    private void parseDocument(final Document document) throws Exception {
        final Element element = document.getDocumentElement();
        if (element != null) {
            this.updateConfigurationsInCache(element);
        }
    }
    
    private void updateConfigurationsInCache(final Element element) {
        final NodeList nodes = element.getChildNodes();
        final int length = nodes.getLength();
        if (length > 0) {
            for (int i = 0; i < length; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() != 3) {
                    if (node.getNodeType() != 8) {
                        final Element nodeElement = (Element)node;
                        if (nodeElement != null && nodeElement.getTagName().equalsIgnoreCase("extended-configurations")) {
                            this.updateConfigurationsInCache(nodeElement);
                        }
                        else {
                            processConfigurationNode(nodeElement, this.confNameVsValue, this.confNameVsProps, this.confNameVsList, new ArrayList<String>(), false);
                        }
                    }
                }
            }
        }
    }
    
    private static boolean isProperty(final Element element) {
        return element.getTagName().equalsIgnoreCase("property");
    }
    
    private static void processConfigurationNode(final Element element, final HashMap<String, String> nameVsValue, final HashMap<String, Properties> nameVsProps, final HashMap<String, List<String>> nameVsList, final List<String> configToBeReplaced, final boolean isInvokedByStaticAPI) {
        final NamedNodeMap attrs = element.getAttributes();
        final Attr name = (Attr)attrs.getNamedItem("name");
        final Attr value = (Attr)attrs.getNamedItem("value");
        final String confName = name.getNodeValue();
        final String confValue = value.getNodeValue();
        if (isInvokedByStaticAPI) {
            if (!nameVsValue.containsKey(confName)) {
                nameVsValue.put(confName, confValue);
            }
        }
        else {
            nameVsValue.put(confName, confValue);
        }
        final Properties props = new Properties();
        final List<String> list = new ArrayList<String>();
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() != 3) {
                if (node.getNodeType() != 8) {
                    final Element nodeElement = (Element)node;
                    if (!configToBeReplaced.contains(confName)) {
                        if (isProperty(nodeElement)) {
                            processConfigurationProps(nodeElement, props);
                        }
                        else {
                            processConfigurationList(nodeElement, list);
                        }
                    }
                }
            }
        }
        Label_0368: {
            if (props.size() > 0) {
                if (isInvokedByStaticAPI) {
                    if (configToBeReplaced.contains(confName)) {
                        break Label_0368;
                    }
                }
                if (nameVsProps.containsKey(confName)) {
                    final Properties p = nameVsProps.get(confName);
                    final Set s = p.entrySet();
                    for (final Map.Entry e : s) {
                        if (!isInvokedByStaticAPI) {
                            if (props.containsKey(e.getKey())) {
                                continue;
                            }
                        }
                        props.setProperty(e.getKey(), e.getValue());
                    }
                }
                nameVsProps.put(confName, props);
            }
        }
        if (list.size() > 0) {
            if (isInvokedByStaticAPI) {
                if (configToBeReplaced.contains(confName)) {
                    return;
                }
            }
            if (nameVsList.containsKey(confName)) {
                final List<String> l = nameVsList.get(confName);
                list.removeAll(l);
                list.addAll(l);
            }
            nameVsList.put(confName, list);
        }
    }
    
    private static void processConfigurationList(final Element element, final List<String> list) {
        final NamedNodeMap attrs = element.getAttributes();
        final Attr value = (Attr)attrs.item(0);
        final String confValue = value.getValue();
        list.add(confValue);
    }
    
    private static void processConfigurationProps(final Element element, final Properties props) {
        final NamedNodeMap attrs = element.getAttributes();
        final Attr name = (Attr)attrs.item(0);
        final Attr value = (Attr)attrs.item(1);
        final String confName = name.getValue();
        final String confValue = value.getValue();
        props.setProperty(confName, confValue);
    }
    
    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", Thread.currentThread().getContextClassLoader());
        factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "1000");
        final DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(getEntityResolver());
        return builder;
    }
    
    private static EntityResolver getEntityResolver() {
        return new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, String systemId) throws SAXException {
                try {
                    String defaultPath = null;
                    if (systemId.endsWith("${product.config}")) {
                        systemId = "product.config";
                        defaultPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "product-config.xml";
                    }
                    else {
                        if (!systemId.endsWith("${customer.config}")) {
                            throw new SAXException("Invalid Entity :: " + systemId);
                        }
                        systemId = "customer.config";
                        defaultPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
                    }
                    final File config = new File(defaultPath);
                    if ((systemId.equals("product.config") || systemId.equals("customer.config")) && !config.exists()) {
                        ConfigurationParser.LOGGER.log(Level.INFO, "{0} doesn't exists, hence it is skipped", config.getAbsolutePath());
                        return new InputSource(new ByteArrayInputStream(" ".getBytes()));
                    }
                    if (systemId.equals("product.config") && config.exists()) {
                        ConfigurationParser.LOGGER.log(Level.INFO, "Processing all product-config files");
                        final File[] productConfFiles = config.getParentFile().listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(final File dir, final String name) {
                                return name.startsWith("product-config") && !name.equalsIgnoreCase("product-config.xml");
                            }
                        });
                        Arrays.sort(productConfFiles);
                        ConfigurationParser.LOGGER.log(Level.INFO, "Processing {0}", config.getAbsolutePath());
                        byte[] confData = new byte[(int)config.length()];
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(config);
                            fis.read(confData);
                        }
                        finally {
                            fis.close();
                        }
                        for (final File confFile : productConfFiles) {
                            ConfigurationParser.LOGGER.log(Level.INFO, "Processing {0}", confFile.getAbsolutePath());
                            final byte[] data = new byte[(int)confFile.length()];
                            try {
                                fis = new FileInputStream(confFile);
                                fis.read(data);
                                byte[] tempData = new byte[0];
                                tempData = new byte[confData.length + data.length];
                                System.arraycopy(confData, 0, tempData, 0, confData.length);
                                System.arraycopy(data, 0, tempData, confData.length, data.length);
                                confData = tempData;
                            }
                            finally {
                                fis.close();
                            }
                        }
                        return new InputSource(new ByteArrayInputStream(confData));
                    }
                    ConfigurationParser.LOGGER.log(Level.INFO, "Processing {0}", config.getAbsolutePath());
                    return new InputSource(new FileReader(config));
                }
                catch (final SAXException e) {
                    throw e;
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                    return null;
                }
            }
        };
    }
    
    public static void writeExtendedPersistenceConfFile(HashMap<String, String> nameVsValue, HashMap<String, Properties> nameVsProps, HashMap<String, List<String>> nameVsList, List<String> configToBeReplaced) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        nameVsList = ((nameVsList == null) ? new HashMap<String, List<String>>() : nameVsList);
        nameVsProps = ((nameVsProps == null) ? new HashMap<String, Properties>() : nameVsProps);
        nameVsValue = ((nameVsValue == null) ? new HashMap<String, String>() : nameVsValue);
        if (configToBeReplaced == null) {
            configToBeReplaced = new ArrayList<String>();
        }
        final String defaultPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
        final File configXml = new File(defaultPath);
        if (configXml.exists()) {
            ConfigurationParser.LOGGER.log(Level.INFO, "Configuration file : {0}  exists , Updating the existing file.", new Object[] { configXml.getAbsolutePath() });
            final DocumentBuilder builder = getDocumentBuilder();
            final Document document = builder.parse(configXml);
            Element element = document.getDocumentElement();
            if (element != null && element.getTagName().equalsIgnoreCase("persistence-configurations")) {
                throw new IllegalArgumentException("Persistence-Configurations.xml file can't be modified . Can write or modify customer-config.xml / product-config.xml");
            }
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = factory.newDocumentBuilder();
            final Document doc = db.parse(configXml);
            doc.getDocumentElement().normalize();
            final NodeList nodeList = doc.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                element = (Element)nodeList.item(i);
                if (element.getNodeName().equals("configuration")) {
                    processConfigurationNode(element, nameVsValue, nameVsProps, nameVsList, configToBeReplaced, true);
                }
            }
        }
        else {
            ConfigurationParser.LOGGER.log(Level.INFO, "Configuration file : {0} doesn't exist . Creating a new file and configurations will be added to it.", new Object[] { configXml.getAbsolutePath() });
            configXml.createNewFile();
        }
        final DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder2 = factory2.newDocumentBuilder();
        final Document document2 = builder2.newDocument();
        document2.setXmlStandalone(true);
        final Element extConfig = document2.createElement("extended-configurations");
        Set s = nameVsValue.entrySet();
        for (final Map.Entry e : s) {
            final String key = e.getKey();
            if (!(nameVsProps.containsKey(key) ^ nameVsList.containsKey(key))) {
                final String value = e.getValue();
                final Element configurationTag = document2.createElement("configuration");
                configurationTag.setAttribute("name", key);
                configurationTag.setAttribute("value", value);
                extConfig.appendChild(configurationTag);
            }
        }
        s = nameVsProps.entrySet();
        for (final Map.Entry e : s) {
            final String key = e.getKey();
            final String value = nameVsValue.containsKey(key) ? nameVsValue.get(key) : "";
            Properties p = new Properties();
            p = e.getValue();
            final Set propSet = p.entrySet();
            final Iterator propIterator = propSet.iterator();
            final Element configurationTag2 = document2.createElement("configuration");
            configurationTag2.setAttribute("name", key);
            configurationTag2.setAttribute("value", value);
            while (propIterator.hasNext()) {
                final Map.Entry propsEntry = propIterator.next();
                final String propKey = propsEntry.getKey();
                final String propValue = propsEntry.getValue();
                final Element propertyTag = document2.createElement("property");
                propertyTag.setAttribute("name", propKey);
                propertyTag.setAttribute("value", propValue);
                configurationTag2.appendChild(propertyTag);
            }
            extConfig.appendChild(configurationTag2);
        }
        s = nameVsList.entrySet();
        for (final Map.Entry e : s) {
            final String key = e.getKey();
            final String value = nameVsValue.containsKey(key) ? nameVsValue.get(key) : "";
            final List l = e.getValue();
            final Iterator listIterator = l.iterator();
            final Element configurationTag3 = document2.createElement("configuration");
            configurationTag3.setAttribute("name", key);
            configurationTag3.setAttribute("value", value);
            while (listIterator.hasNext()) {
                final Element dataTag = document2.createElement("data");
                dataTag.setAttribute("value", listIterator.next());
                configurationTag3.appendChild(dataTag);
            }
            extConfig.appendChild(configurationTag3);
        }
        document2.appendChild(extConfig);
        document2.getDocumentElement().normalize();
        final TransformerFactory tfac = TransformerFactoryUtil.newInstance();
        tfac.setAttribute("indent-number", new Integer(4));
        final Transformer t = tfac.newTransformer();
        t.setOutputProperty("indent", "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        final DOMSource doms = new DOMSource(document2);
        final StreamResult stream = new StreamResult(new FileOutputStream(configXml, false));
        t.transform(doms, stream);
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append("<ConfigurationParser>\n\tconfNameVsValue :: ");
        s.append(this.confNameVsValue);
        s.append("\n\tconfNameVsProps :: ");
        s.append(this.confNameVsProps);
        s.append("\n\tconfNameVsList :: ");
        s.append(this.confNameVsList);
        s.append("\n</ConfigurationParser>");
        return s.toString();
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            ConfigurationParser.LOGGER.log(Level.INFO, "Please provide the path to persistence-configurations.xml file.");
            return;
        }
        final ConfigurationParser cp = new ConfigurationParser(args[0]);
        ConfigurationParser.LOGGER.log(Level.INFO, cp.toString());
    }
    
    static {
        LOGGER = Logger.getLogger(ConfigurationParser.class.getName());
    }
}

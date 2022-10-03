package com.me.tools.zcutil;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Map;
import org.w3c.dom.Element;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class ConfFileReader
{
    private Document xmlDoc;
    
    public ConfFileReader() {
        this.xmlDoc = null;
    }
    
    private void loadFile(final String fileName, final boolean fromJar) {
        InputStream ins = null;
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = dbFactory.newDocumentBuilder();
            if (fromJar) {
                ins = this.getClass().getResourceAsStream(fileName);
                this.xmlDoc = builder.parse(ins);
            }
            else {
                this.xmlDoc = builder.parse(new File(fileName));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (ins != null) {
                    ins.close();
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    public Properties getConfProps(final String productName, final String fileName, final ProductConf prdConf) {
        Properties genProps = null;
        try {
            this.loadFile(fileName, true);
            final Element rootElem = this.xmlDoc.getDocumentElement();
            if (rootElem == null) {
                return null;
            }
            final Element general = (Element)rootElem.getElementsByTagName("general").item(0);
            genProps = getPropertyValues(general, "property", "name", "value");
            final Properties productProp = this.getProductProp(prdConf.getRootElement(), productName);
            genProps.putAll(productProp);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
        return genProps;
    }
    
    public ProductConf getFormConfiguaration() {
        final String fileLoc = METrack.getConfDir() + File.separator + "metrack.xml";
        if (new File(fileLoc).exists()) {
            this.loadFile(fileLoc, false);
            return this.setProductConfiguaration();
        }
        return new ProductConf();
    }
    
    private Properties getProductProp(final Element element, final String productName) throws Exception {
        final NodeList list = element.getElementsByTagName("product");
        for (int i = 0; i < list.getLength(); ++i) {
            final Node node = list.item(i);
            if (node.getNodeType() == 1) {
                final Element productElement = (Element)node;
                if (productElement.getAttribute("name").equals(productName)) {
                    return getPropertyValues(productElement, "property", "name", "value");
                }
            }
        }
        return null;
    }
    
    private ProductConf setProductConfiguaration() {
        final ProductConf prodConf = new ProductConf();
        final Element rootElem = this.xmlDoc.getDocumentElement();
        prodConf.setRootElement(rootElem);
        prodConf.setFormsKeys();
        final NodeList list = rootElem.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            final Node node = list.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                if (node.getNodeName().equals("baseform") && node.getFirstChild().getNodeValue() != null) {
                    prodConf.setBaseFormExcludeFields(element.getElementsByTagName("exclude-fileds").item(0).getFirstChild().getNodeValue().split(","));
                }
                else if (node.getNodeName().equals("licenseform")) {
                    if (element.getAttribute("enabled").equals("false")) {
                        prodConf.setLicenseFormAccess(false);
                    }
                    else if (element.getAttribute("local").equals("true")) {
                        prodConf.setLicenseQueryFromProduct(true);
                        prodConf.setLicenseElement((Element)element.getElementsByTagName("response").item(0));
                    }
                }
                else if (node.getNodeName().equals("loadform")) {
                    if (element.getAttribute("enabled").equals("false")) {
                        prodConf.setLoadFormAccess(false);
                    }
                    else if (element.getAttribute("local").equals("true")) {
                        prodConf.setLoadQueryFromProduct(true);
                        prodConf.setLoadElement((Element)element.getElementsByTagName("response").item(0));
                    }
                }
                else if (node.getNodeName().equals("actionlogform") && element.getAttribute("enabled").equals("false")) {
                    prodConf.setActionLogFormAccess(false);
                }
            }
        }
        return prodConf;
    }
    
    public static Properties getPropertyValues(final Element element, final String tagName, final String key, final String value) {
        try {
            final NodeList list = element.getElementsByTagName(tagName);
            final int size = list.getLength();
            if (size == 0) {
                return null;
            }
            final Properties prop = new Properties();
            for (int i = 0; i < size; ++i) {
                final Node node = list.item(i);
                if (node.getNodeType() == 1) {
                    final Element propertyElement = (Element)node;
                    prop.setProperty(propertyElement.getAttribute(key), propertyElement.getAttribute(value));
                }
            }
            return prop;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

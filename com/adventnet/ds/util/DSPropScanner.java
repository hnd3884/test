package com.adventnet.ds.util;

import java.util.logging.Level;
import org.w3c.dom.Document;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.InputStream;
import java.util.Properties;
import com.adventnet.iam.security.SecurityUtil;
import java.io.ByteArrayInputStream;
import javax.xml.transform.Result;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.Source;
import com.zoho.mickey.api.TransformerFactoryUtil;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Attr;
import java.util.Map;
import org.w3c.dom.Element;
import java.util.logging.Logger;
import java.util.HashMap;

public class DSPropScanner
{
    private HashMap mapProperties;
    public static final Logger LOGGER;
    private Element properties;
    
    private Map getProperties() {
        return this.mapProperties;
    }
    
    public DSPropScanner() {
        this.mapProperties = new HashMap();
    }
    
    private void visitElement_config_property(final Element element) {
        String attName = null;
        String value = null;
        final NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                attName = attr.getValue();
                this.mapProperties.put(attName, null);
            }
        }
        final NodeList nodes = element.getChildNodes();
        for (int j = 0; j < nodes.getLength(); ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 4: {
                    DSPropScanner.LOGGER.fine("CDATA :" + ((CDATASection)node).getData());
                    break;
                }
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    value = ((Text)node).getData();
                    this.mapProperties.put(attName, value);
                    break;
                }
            }
        }
    }
    
    private void enhanceProperties() {
        if (this.mapProperties.get("ConnectionURL") == null) {
            this.mapProperties.put("ConnectionURL", this.mapProperties.get("Database"));
        }
    }
    
    private void visitProperties() {
        final NodeList nodes = this.properties.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("config-property")) {
                        this.visitElement_config_property(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public Map parseDS(final String dsFile) throws Exception {
        return this.parseDS(dsFile, "stylesheets/ConnectionFactoryTemplate.xsl");
    }
    
    public Map parseDSElement(final Element elem) {
        this.properties = elem;
        this.visitProperties();
        this.enhanceProperties();
        return this.getProperties();
    }
    
    public Map parseDS(final String dsFile, final String styleSheet) throws Exception {
        final File f = new File(dsFile);
        final InputStream ris = Thread.currentThread().getContextClassLoader().getResourceAsStream(styleSheet);
        final StreamSource streamSource = new StreamSource(ris);
        final TransformerFactory tFactory = TransformerFactoryUtil.newInstance();
        final Transformer transformer = tFactory.newTransformer(streamSource);
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        transformer.transform(new StreamSource(f), new StreamResult(bout));
        final ByteArrayInputStream bis = new ByteArrayInputStream(bout.toByteArray());
        final Document document = SecurityUtil.createDocumentBuilder(true, false, (Properties)null).parse(bis);
        bout.close();
        bis.close();
        final NodeList nlist = document.getElementsByTagName("properties");
        final Node pn = nlist.item(0);
        this.properties = (Element)pn;
        this.visitProperties();
        this.enhanceProperties();
        return this.getProperties();
    }
    
    public static void main(final String[] arg) throws Exception {
        final DSPropScanner scanner = new DSPropScanner();
        DSPropScanner.LOGGER.log(Level.FINE, String.valueOf(scanner.parseDS(arg[0])));
    }
    
    static {
        LOGGER = Logger.getLogger(DSPropScanner.class.getName());
    }
}

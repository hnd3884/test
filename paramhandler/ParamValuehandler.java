package paramhandler;

import java.util.Hashtable;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.util.Iterator;
import java.io.InputStream;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.logging.Level;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ParamValuehandler
{
    private static final Logger LOGGER;
    
    public static void main(final String[] args) throws Exception {
        final String filepath = args[0];
        final String propertypath = args[1];
        final Properties properties = new Properties();
        final InputStream is = new FileInputStream(propertypath);
        properties.load(is);
        final Iterator keyIterator = ((Hashtable<Object, V>)properties).keySet().iterator();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder parser = factory.newDocumentBuilder();
            final Document doc = parser.parse(filepath);
            final XPath xpath = XPathFactory.newInstance().newXPath();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String value = properties.getProperty(key);
                key = "{" + key + "}";
                final NodeList nodes = (NodeList)xpath.evaluate("//param-value[contains(text(),'" + key + "')]", doc, XPathConstants.NODESET);
                for (int idx = 0; idx < nodes.getLength(); ++idx) {
                    value = nodes.item(idx).getTextContent().replace(key, value);
                    nodes.item(idx).setTextContent(value);
                }
            }
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }
        catch (final TransformerException e) {
            ParamValuehandler.LOGGER.log(Level.INFO, "TransformerException", e);
            throw e;
        }
        catch (final ParserConfigurationException e2) {
            ParamValuehandler.LOGGER.log(Level.INFO, " ParserConfigurationException", e2);
            throw e2;
        }
        catch (final SAXException e3) {
            ParamValuehandler.LOGGER.log(Level.INFO, " SAXException ", e3);
            throw e3;
        }
        catch (final XPathExpressionException e4) {
            ParamValuehandler.LOGGER.log(Level.INFO, "XPathExpressionException", e4);
            throw e4;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ParamValuehandler.class.getName());
    }
}

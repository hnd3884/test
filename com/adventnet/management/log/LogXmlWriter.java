package com.adventnet.management.log;

import java.util.Hashtable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.w3c.dom.Node;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Properties;
import org.w3c.dom.Document;

public class LogXmlWriter
{
    private static Document xmldoc;
    
    public static void write(final Properties properties, final String s) throws Exception {
        final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Element documentElement = documentBuilder.parse(new InputSource(new FileInputStream(s))).getDocumentElement();
        LogXmlWriter.xmldoc = documentBuilder.newDocument();
        final Element element = LogXmlWriter.xmldoc.createElement("LOG_USER");
        element.setAttribute("FileName", properties.getProperty("FileName"));
        element.setAttribute("MaxLines", properties.getProperty("MaxLines", "1000"));
        element.setAttribute("FileCount", properties.getProperty("FileCount", "10"));
        element.setAttribute("MaxLinesCached", properties.getProperty("MaxLinesCached", "0"));
        element.setAttribute("LogsDirectory", properties.getProperty("LogsDirectory", "logs"));
        element.setAttribute("UseTimeStamp", properties.getProperty("UseTimeStamp", "true"));
        final String[] array = ((Hashtable<K, String[]>)properties).get("DisplayName");
        final String[] array2 = ((Hashtable<K, String[]>)properties).get("Name");
        final Integer[] array3 = ((Hashtable<K, Integer[]>)properties).get("LogLevel");
        final Boolean[] array4 = ((Hashtable<K, Boolean[]>)properties).get("Logging");
        for (int i = 0; i < array2.length; ++i) {
            final Element element2 = LogXmlWriter.xmldoc.createElement("KEY");
            element2.setAttribute("Name", array2[i]);
            element2.setAttribute("DisplayName", array[i]);
            element2.setAttribute("LogLevel", array3[i].toString());
            element2.setAttribute("Logging", array4[i].toString());
            element.appendChild(element2);
        }
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(s));
        final Element element3 = LogXmlWriter.xmldoc.createElement(documentElement.getNodeName());
        element3.appendChild(element);
        setAttributes(documentElement, element3);
        LogXmlWriter.xmldoc.appendChild(element3);
        process(documentElement.getChildNodes(), element3);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("encoding", "ISO-8859-1");
        transformer.transform(new DOMSource(LogXmlWriter.xmldoc.getDocumentElement()), new StreamResult(outputStreamWriter));
        try {
            if (outputStreamWriter != null) {
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }
        }
        catch (final IOException ex) {
            System.err.println("Exception while writing file " + ex);
            ex.printStackTrace();
        }
    }
    
    private static void process(final NodeList list, final Element element) {
        for (int i = 0; i < list.getLength(); ++i) {
            final Node item = list.item(i);
            if (item.getNodeType() == 1) {
                final Element element2 = LogXmlWriter.xmldoc.createElement(item.getNodeName());
                element.appendChild(element2);
                setAttributes(item, element2);
                if (item.hasChildNodes()) {
                    process(item.getChildNodes(), element2);
                }
            }
            else if (item.getNodeType() == 8) {
                element.appendChild(LogXmlWriter.xmldoc.createComment(item.getNodeValue()));
            }
            else if (item.getNodeType() == 4) {
                element.appendChild(LogXmlWriter.xmldoc.createCDATASection(item.getNodeValue()));
            }
            else if (item.getNodeType() == 5) {
                element.appendChild(LogXmlWriter.xmldoc.createEntityReference(item.getNodeValue()));
            }
            else if (item.getNodeType() != 9) {
                if (item.getNodeType() == 7) {
                    element.appendChild(LogXmlWriter.xmldoc.createProcessingInstruction(item.getNodeName(), item.getNodeValue()));
                }
            }
        }
    }
    
    private static void setAttributes(final Node node, final Element element) {
        final NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            element.setAttribute(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
    }
}

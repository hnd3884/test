package com.adventnet.util.parser;

import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.util.Properties;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Hashtable;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

class RulesXmlParser
{
    DocumentBuilder dBuild;
    Document doc;
    Node rootNode;
    private static final String parser = "PARSER";
    private static final String command = "COMMAND";
    private static final String metadata = "METADATA";
    private static final String classname = "CLASSNAME";
    private static final String name = "NAME";
    private static final String type = "TYPE";
    private static final String erule = "ERROR-RULE";
    private static final String vrule = "VALID-RULE";
    private static final String metadata_tag1 = "<METADATA>";
    private static final String metadata_tag2 = "</METADATA>";
    private Hashtable parsers;
    
    public Hashtable getParserList() {
        return this.parsers;
    }
    
    RulesXmlParser(final InputStream inputStream) throws ParseException {
        this.dBuild = null;
        this.doc = null;
        this.rootNode = null;
        this.parsers = null;
        try {
            this.dBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.loadFromXmlFile(inputStream);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage());
        }
    }
    
    void loadFromXmlFile(final InputStream inputStream) throws Exception {
        this.doc = this.dBuild.parse(inputStream);
        this.rootNode = this.doc.getDocumentElement();
        this.parsers = new Hashtable();
        this.getParsers();
    }
    
    void getParsers() {
        final NodeList elementsByTagName = ((Element)this.rootNode).getElementsByTagName("PARSER");
        if (elementsByTagName != null) {
            for (int i = 0; i < elementsByTagName.getLength(); ++i) {
                final Node item = elementsByTagName.item(i);
                final NamedNodeMap attributes = item.getAttributes();
                final Attr attr = (Attr)attributes.getNamedItem("CLASSNAME");
                final Attr attr2 = (Attr)attributes.getNamedItem("TYPE");
                final ArrayList rules = this.getRules(item);
                final Parser parser = new Parser();
                parser.setClassName(attr.getValue());
                parser.setType(attr2.getValue());
                parser.setRuleList(rules);
                parser.setErrorRules(this.getParserErrorRules(item));
                this.parsers.put(parser.getType(), parser);
            }
        }
    }
    
    private ArrayList getRules(final Node node) {
        final ArrayList list = new ArrayList();
        final NodeList elementsByTagName = ((Element)node).getElementsByTagName("COMMAND");
        if (elementsByTagName != null) {
            for (int i = 0; i < elementsByTagName.getLength(); ++i) {
                final RuleObject ruleObject = new RuleObject();
                final Node item = elementsByTagName.item(i);
                final NodeList elementsByTagName2 = ((Element)item).getElementsByTagName("ERROR-RULE");
                if (elementsByTagName2 != null) {
                    final Node item2 = elementsByTagName2.item(0);
                    if (item2 != null) {
                        final NodeList elementsByTagName3 = ((Element)item2).getElementsByTagName("METADATA");
                        if (elementsByTagName3 != null) {
                            ruleObject.setErrorRule(getString((Element)elementsByTagName3.item(0)));
                        }
                    }
                }
                final NodeList elementsByTagName4 = ((Element)item).getElementsByTagName("VALID-RULE");
                if (elementsByTagName4 != null) {
                    final Node item3 = elementsByTagName4.item(0);
                    if (item3 != null) {
                        final NodeList elementsByTagName5 = ((Element)item3).getElementsByTagName("METADATA");
                        if (elementsByTagName5 != null) {
                            ruleObject.setValidRule(getString((Element)elementsByTagName5.item(0)));
                        }
                    }
                }
                ruleObject.setCommand(((Attr)item.getAttributes().getNamedItem("NAME")).getValue());
                list.add(ruleObject);
            }
        }
        return list;
    }
    
    private ArrayList getParserErrorRules(final Node node) {
        final ArrayList list = new ArrayList();
        final NodeList elementsByTagName = ((Element)node).getElementsByTagName("ERROR-RULES");
        if (elementsByTagName != null) {
            final Node item = elementsByTagName.item(0);
            if (item != null) {
                final NodeList elementsByTagName2 = ((Element)item).getElementsByTagName("ERROR-RULE");
                if (elementsByTagName2 != null) {
                    for (int i = 0; i < elementsByTagName2.getLength(); ++i) {
                        final Node item2 = elementsByTagName2.item(i);
                        if (item2 != null) {
                            final NodeList elementsByTagName3 = ((Element)item2).getElementsByTagName("METADATA");
                            if (elementsByTagName3 != null) {
                                list.add(getString((Element)elementsByTagName3.item(0)));
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public static String getString(final Element element) {
        try {
            final StringWriter stringWriter = new StringWriter(32000);
            final BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
            final String s = "ISO-8859-1";
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final DOMSource domSource = new DOMSource(element);
            final StreamResult streamResult = new StreamResult(bufferedWriter);
            final Properties outputProperties = new Properties();
            ((Hashtable<String, String>)outputProperties).put("indent", "yes");
            ((Hashtable<String, String>)outputProperties).put("encoding", s);
            ((Hashtable<String, String>)outputProperties).put("method", "xml");
            transformer.setOutputProperties(outputProperties);
            transformer.transform(domSource, streamResult);
            return stringWriter.toString();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}

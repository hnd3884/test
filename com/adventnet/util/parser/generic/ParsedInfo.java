package com.adventnet.util.parser.generic;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class ParsedInfo
{
    static final String patternDefnList = "PATTERN-DEFN-LIST";
    static final String patternDefn = "PATTERN-DEFN";
    static final String token = "TOKEN";
    static final String trim = "TRIM";
    static final String match = "MATCH";
    static final String pattern = "PATTERN";
    static final String param = "PARAM";
    static final String value = "VALUE";
    static final String name_attr = "NAME";
    static final String action = "ACTION";
    static final String delimiter_tag = "DELIMITER";
    static final int MATCH_NAME = 0;
    static final int MATCH_VALUE = 1;
    static final int MATCH_OPTIONS = 2;
    String version;
    TokenInfo tokenInfo;
    Element rootNode;
    Document doc;
    NodeList childNodes;
    
    ParsedInfo(final Document doc) {
        this.version = null;
        this.doc = doc;
        this.rootNode = (Element)doc.getElementsByTagName("GENERIC-PARSER").item(0);
        this.childNodes = doc.getChildNodes();
        this.tokenInfo = new TokenInfo();
    }
    
    void parse() {
        this.getTokens();
    }
    
    void getTokens() {
        this.getParserVersion();
        this.getPatternDefinitions();
        this.getDelimiterList(this.getTokensByName(this.rootNode, "TOKEN"), null);
    }
    
    void getParserVersion() {
        this.version = ((Attr)this.doc.getElementsByTagName("GENERIC-PARSER").item(0).getAttributes().item(0)).getValue();
    }
    
    String getVersion() {
        return this.version;
    }
    
    void getPatternDefinitions() {
        final NodeList elementsByTagName = this.doc.getElementsByTagName("PATTERN-DEFN");
        if (elementsByTagName != null) {
            for (int length = elementsByTagName.getLength(), i = 0; i < length; ++i) {
                final Node item = elementsByTagName.item(i);
                if (item.getNodeType() == 1) {
                    final NamedNodeMap attributes = item.getAttributes();
                    if (attributes != null) {
                        final Attr attr = (Attr)attributes.item(0);
                        if (attr != null) {
                            final String value = attr.getValue();
                            final Attr attr2 = (Attr)attributes.item(1);
                            if (attr2 != null) {
                                this.tokenInfo.addMatchDefinition(value, attr2.getValue());
                            }
                        }
                    }
                }
            }
        }
    }
    
    Vector getTokensByName(final Node node, final String s) {
        final NodeList childNodes = node.getChildNodes();
        final Vector vector = new Vector();
        for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNodeName().equals(s)) {
                vector.addElement(childNodes.item(i));
            }
        }
        return vector;
    }
    
    Vector getDelimiterList(final Vector vector, final Delimiter delimiter) {
        final Vector vector2 = new Vector();
        for (int size = vector.size(), i = 0; i < size; ++i) {
            vector2.addElement(this.getDelimiter((Node)vector.elementAt(i), delimiter));
        }
        this.tokenInfo.addDelimiterList(vector2);
        return vector2;
    }
    
    Delimiter getDelimiter(final Node node, final Delimiter delimiter) {
        final NamedNodeMap attributes = node.getAttributes();
        final Delimiter delimiter2 = new Delimiter();
        if (attributes != null) {
            delimiter2.setDescription(((Attr)attributes.item(0)).getValue());
        }
        final String[] delimiterList = this.getDelimiterList(node);
        delimiter2.setDelimiterList(delimiterList);
        delimiter2.setDelimiterValue(delimiterList[0]);
        delimiter2.setTrimList(this.getTrimList(node));
        delimiter2.setMatch(this.getMatch(node));
        final Vector tokensByName = this.getTokensByName(node, "TOKEN");
        final int size = tokensByName.size();
        if (size == 0) {
            delimiter2.setChildList(null);
            delimiter2.setParent(delimiter);
            return delimiter2;
        }
        final Delimiter[] childList = new Delimiter[size];
        final Vector delimiterList2 = this.getDelimiterList(tokensByName, delimiter2);
        int n = 0;
        final Enumeration elements = delimiterList2.elements();
        while (elements.hasMoreElements()) {
            childList[n++] = (Delimiter)elements.nextElement();
        }
        delimiter2.setChildList(childList);
        delimiter2.setParent(delimiter);
        return delimiter2;
    }
    
    String[] getDelimiterList(final Node node) {
        final Vector tokensByName = this.getTokensByName(node, "DELIMITER");
        final int size = tokensByName.size();
        final String[] array = new String[size];
        for (int i = 0; i < size; ++i) {
            String value = tokensByName.elementAt(i).getAttributeNode("VALUE").getValue();
            if (value.equals("\\n")) {
                value = "\n";
            }
            else if (value.equals("\\t")) {
                value = "\t";
            }
            array[i] = value;
        }
        return array;
    }
    
    String[] getTrimList(final Node node) {
        final Vector tokensByName = this.getTokensByName(node, "TRIM");
        final int size = tokensByName.size();
        final String[] array = new String[size];
        for (int i = 0; i < size; ++i) {
            array[i] = ((Element)tokensByName.elementAt(i)).getAttributeNode("VALUE").getValue();
        }
        return array;
    }
    
    Match getMatch(final Node node) {
        final Hashtable hashtable = new Hashtable();
        final NodeList elementsByTagName = ((Element)node).getElementsByTagName("MATCH");
        if (elementsByTagName == null || elementsByTagName.getLength() == 0) {
            return null;
        }
        final NodeList elementsByTagName2 = ((Element)elementsByTagName.item(0)).getElementsByTagName("PATTERN");
        if (elementsByTagName2 == null || elementsByTagName2.getLength() == 0) {
            return null;
        }
        final int length = elementsByTagName2.getLength();
        final Match match = new Match();
        final Hashtable patternList = new Hashtable();
        for (int i = 0; i < length; ++i) {
            final Pattern pattern = new Pattern();
            if (i == 0) {
                pattern.setFirst(true);
            }
            if (i == length - 1) {
                pattern.setLast(true);
            }
            final Attr attributeNode = ((Element)elementsByTagName2.item(i)).getAttributeNode("NAME");
            final Attr attributeNode2 = ((Element)elementsByTagName2.item(i)).getAttributeNode("ACTION");
            final NodeList elementsByTagName3 = ((Element)elementsByTagName2.item(i)).getElementsByTagName("PARAM");
            final String value = attributeNode2.getValue();
            if (value.equals("IGNORE")) {
                pattern.setAction(1);
            }
            else if (value.equals("SUBS")) {
                pattern.setAction(2);
            }
            pattern.setPattern(this.tokenInfo.getMatchDefinition(attributeNode.getValue()));
            if (elementsByTagName3 == null) {
                patternList.put(attributeNode.getValue(), pattern);
            }
            else {
                final int length2 = elementsByTagName3.getLength();
                if (length2 == 0) {
                    patternList.put(attributeNode.getValue(), pattern);
                }
                else {
                    final Hashtable parameterList = new Hashtable();
                    for (int j = 0; j < length2; ++j) {
                        parameterList.put(((Element)elementsByTagName3.item(j)).getAttributeNode("NAME").getValue(), ((Element)elementsByTagName3.item(j)).getAttributeNode("VALUE").getValue());
                    }
                    pattern.setParameterList(parameterList);
                    patternList.put(attributeNode.getValue(), pattern);
                }
            }
        }
        match.setPatternList(patternList);
        return match;
    }
    
    Enumeration getMatchList() {
        return this.tokenInfo.getMatchDefinitions();
    }
    
    TokenInfo getTokenInfo() {
        return this.tokenInfo;
    }
}

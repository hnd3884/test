package com.adventnet.util.parser.line;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import java.io.InputStream;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

class XMLLineRuleParser
{
    DocumentBuilder dBuild;
    Document doc;
    ByteArrayInputStream bis;
    private static final String line = "LINE";
    private static final String param = "PARAM";
    private static final String table = "TABLE";
    private static final String column = "COLUMN";
    private static final String scalar = "SCALAR";
    private static final String name = "NAME";
    private static final String start = "START";
    private static final String end = "END";
    private static final String pattern = "PATTERN";
    private static final String delimiter = "DELIMITER";
    private static final String tokenno = "TOKENNO";
    private static final String trim = "TRIM";
    
    XMLLineRuleParser() throws Exception {
        this.dBuild = null;
        this.doc = null;
        this.bis = null;
        try {
            this.dBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
    }
    
    Vector parseRule(final String s) throws Exception {
        this.bis = new ByteArrayInputStream(s.getBytes());
        this.doc = this.dBuild.parse(this.bis);
        return this.getLineRules(this.doc.getDocumentElement());
    }
    
    Vector getLineRules(final Node node) throws Exception {
        return this.getLineInfo(node);
    }
    
    Vector getLineInfo(final Node node) throws Exception {
        final NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            final Vector vector = new Vector();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Node item = childNodes.item(i);
                if (item.getNodeType() == 1) {
                    if (item.getNodeName().equals("TABLE") || item.getNodeName().equals("SCALAR")) {
                        final LineRule lineRule = new LineRule();
                        final NamedNodeMap attributes = item.getAttributes();
                        final Attr attr = (Attr)attributes.getNamedItem("START");
                        final Attr attr2 = (Attr)attributes.getNamedItem("END");
                        final Attr attr3 = (Attr)attributes.getNamedItem("DELIMITER");
                        if (attr != null) {
                            lineRule.setStartLineNo(Integer.parseInt(attr.getValue()));
                        }
                        if (attr2 != null) {
                            lineRule.setEndLineNo(Integer.parseInt(attr2.getValue()));
                        }
                        if (item.getNodeName().equals("TABLE")) {
                            lineRule.setTable(true);
                        }
                        if (attr3 != null) {
                            lineRule.setDelimiter(attr3.getValue());
                        }
                        else {
                            lineRule.setDelimiter(" ");
                        }
                        final Vector paramList = this.getParamList(item, lineRule.isTable());
                        if (paramList.size() > 0) {
                            lineRule.setParamList(paramList);
                        }
                        vector.addElement(lineRule);
                    }
                }
            }
            return vector;
        }
        return null;
    }
    
    Vector getParamList(final Node node, final boolean b) throws Exception {
        NodeList list;
        if (b) {
            list = ((Element)node).getElementsByTagName("COLUMN");
        }
        else {
            list = ((Element)node).getElementsByTagName("PARAM");
        }
        final Vector<Param> vector = new Vector<Param>();
        if (list != null) {
            for (int i = 0; i < list.getLength(); ++i) {
                final NamedNodeMap attributes = list.item(i).getAttributes();
                final Attr attr = (Attr)attributes.getNamedItem("NAME");
                final Attr attr2 = (Attr)attributes.getNamedItem("TOKENNO");
                final Attr attr3 = (Attr)attributes.getNamedItem("PATTERN");
                final Attr attr4 = (Attr)attributes.getNamedItem("TRIM");
                if (b) {
                    if (attr3 == null && attr == null) {
                        throw new Exception("Cannot resolve parameter name");
                    }
                }
                else {
                    if (attr2 == null) {
                        throw new Exception("Cannot resolve parameter value");
                    }
                    if (attr == null && attr3 == null) {
                        throw new Exception("Cannot resolve parameter name");
                    }
                }
                final Param param = new Param();
                if (attr2 != null) {
                    param.setTokenNo(Integer.parseInt(attr2.getValue()));
                }
                if (attr != null) {
                    param.setParamName(attr.getValue());
                }
                if (attr3 != null) {
                    param.setPattern(attr3.getValue());
                }
                if (attr4 != null) {
                    param.setTrimString(attr4.getValue());
                }
                if (param.getParamName() == null) {
                    param.setParamName(param.getPattern());
                }
                vector.addElement(param);
            }
        }
        return vector;
    }
}

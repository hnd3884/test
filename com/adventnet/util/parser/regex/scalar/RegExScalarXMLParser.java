package com.adventnet.util.parser.regex.scalar;

import java.util.regex.Matcher;
import com.adventnet.util.parser.regex.ColumnObject;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import com.adventnet.util.parser.regex.ParserResponseObject;
import com.adventnet.util.parser.regex.ParameterObject;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import com.adventnet.util.parser.ParseException;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import com.adventnet.util.parser.regex.RegExParserException;

public class RegExScalarXMLParser
{
    public ScalarObject parseRule(final String s) throws ParseException {
        ScalarObject scalarObject = null;
        try {
            if (s == null) {
                throw new RegExParserException("RegExScalarXMLParser: no rule defined");
            }
            final Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes()));
            if (parse == null && parse.getDocumentElement() == null) {
                throw new RegExParserException("RegExParserException: document root element missing");
            }
            scalarObject = this.getScalarObject(parse.getDocumentElement());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return scalarObject;
    }
    
    public ScalarObject getScalarObject(final Element element) throws ParseException {
        final ScalarObject scalarObject = new ScalarObject();
        final NodeList elementsByTagName = element.getElementsByTagName("SCALAR");
        if (elementsByTagName != null) {
            final Node item = elementsByTagName.item(0);
            if (item != null && item.getNodeType() == 1) {
                final String attribute = ((Element)item).getAttribute("REFERENCEREQ");
                if (attribute != null && attribute.length() != 0) {
                    if (attribute.equals("true")) {
                        scalarObject.setReferenceRequired(true);
                    }
                    else {
                        if (!attribute.equals("false")) {
                            throw new ParseException("value should be true/false");
                        }
                        scalarObject.setReferenceRequired(false);
                    }
                }
                scalarObject.setRegExpressions(this.getRegExforNode(item));
            }
        }
        return scalarObject;
    }
    
    public ArrayList getRegExforNode(final Node node) throws ParseException {
        final NodeList childNodes = node.getChildNodes();
        final ArrayList list = new ArrayList();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Node item = childNodes.item(i);
                if (item.getNodeType() == 1 || item.getNodeName().equals("REGEX")) {
                    final ScalarRegEx scalarRegEx = new ScalarRegEx();
                    final Element element = (Element)item;
                    if (element != null) {
                        final String attribute = element.getAttribute("NAME");
                        if (attribute == null || attribute.length() == 0) {
                            throw new ParseException("name attribute not defined");
                        }
                        scalarRegEx.setName(attribute);
                        final String attribute2 = element.getAttribute("EXPRESSION");
                        if (attribute2 == null || attribute2.length() == 0) {
                            throw new ParseException("regular expression not defined");
                        }
                        scalarRegEx.setExpression(attribute2);
                        scalarRegEx.setParameterList(this.getParamNodeList(element));
                        list.add(scalarRegEx);
                    }
                }
            }
        }
        return list;
    }
    
    public ArrayList getParamNodeList(final Element element) throws ParseException {
        final NodeList elementsByTagName = element.getElementsByTagName("PARAM");
        final ArrayList list = new ArrayList();
        if (elementsByTagName != null) {
            for (int i = 0; i < elementsByTagName.getLength(); ++i) {
                final Node item = elementsByTagName.item(i);
                if (item.getNodeType() == 1) {
                    final Element element2 = (Element)item;
                    final String attribute = element2.getAttribute("NAME");
                    if (attribute == null || attribute.length() == 0) {
                        throw new ParseException("Name attribute should have value");
                    }
                    final String attribute2 = element2.getAttribute("TOKENNO");
                    int int1 = -1;
                    if (attribute2 != null) {
                        try {
                            int1 = Integer.parseInt(attribute2);
                        }
                        catch (final Exception ex) {
                            throw new ParseException("Valid Token number for parameter " + attribute + " should be given");
                        }
                    }
                    if (attribute != null && int1 != -1) {
                        final ParameterObject parameterObject = new ParameterObject();
                        parameterObject.setTokenNo(int1);
                        parameterObject.setParamName(attribute);
                        list.add(parameterObject);
                    }
                }
            }
        }
        return list;
    }
    
    public ParserResponseObject parseMessage(final ScalarObject scalarObject, final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "\n\r");
        final String[] array = new String[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            array[n++] = stringTokenizer.nextToken();
        }
        final ArrayList regExpressions = scalarObject.getRegExpressions();
        final boolean referenceRequired = scalarObject.getReferenceRequired();
        final ParserResponseObject parserResponseObject = new ParserResponseObject();
        int n2 = 0;
        if (referenceRequired) {
            this.initializeResult(parserResponseObject, regExpressions);
        }
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < regExpressions.size(); ++j) {
                final ScalarRegEx scalarRegEx = regExpressions.get(j);
                final Matcher matcher = Pattern.compile(scalarRegEx.getExpression()).matcher(array[i]);
                if (matcher.matches()) {
                    if (referenceRequired && j == 0) {
                        this.addNullValues(parserResponseObject);
                    }
                    final ArrayList parameterList = scalarRegEx.getParameterList();
                    for (int k = 0; k < parameterList.size(); ++k) {
                        final ParameterObject parameterObject = parameterList.get(k);
                        final int tokenNo = parameterObject.getTokenNo();
                        if (tokenNo > matcher.groupCount()) {
                            System.err.println(tokenNo + " token does not exist!!");
                        }
                        final String paramName = parameterObject.getParamName();
                        final String substring = array[i].substring(matcher.start(tokenNo), matcher.end(tokenNo));
                        if (!this.isColumnAlreadyPresent(paramName, parserResponseObject)) {
                            final ColumnObject columnObject = new ColumnObject();
                            columnObject.setName(paramName);
                            columnObject.addColumnEntry(substring);
                            parserResponseObject.put(new Integer(n2++), columnObject);
                        }
                        else {
                            parserResponseObject.getColumnObject(paramName).addColumnEntry(substring);
                        }
                    }
                    break;
                }
            }
        }
        if (referenceRequired) {
            this.addNullValues(parserResponseObject);
        }
        return parserResponseObject;
    }
    
    private boolean isColumnAlreadyPresent(final String s, final ParserResponseObject parserResponseObject) {
        return parserResponseObject.getColumnObject(s) != null;
    }
    
    private void initializeResult(final ParserResponseObject parserResponseObject, final ArrayList list) {
        int n = 0;
        for (int i = 0; i < list.size(); ++i) {
            final ArrayList parameterList = list.get(i).getParameterList();
            for (int j = 0; j < parameterList.size(); ++j) {
                final String paramName = parameterList.get(j).getParamName();
                final ColumnObject columnObject = new ColumnObject();
                columnObject.setName(paramName);
                parserResponseObject.put(new Integer(n++), columnObject);
            }
        }
    }
    
    public void addNullValues(final ParserResponseObject parserResponseObject) {
        final int size = parserResponseObject.get(new Integer(0)).getValues().size();
        for (int size2 = parserResponseObject.size(), i = 1; i < size2; ++i) {
            final ColumnObject columnObject = parserResponseObject.get(new Integer(i));
            if (columnObject.getValues().size() != size) {
                if (columnObject.getValues().size() == size - 1) {
                    columnObject.addColumnEntry(null);
                }
                else {
                    System.err.println("RegExScalarXMLParser: the number of column entries do not match. ");
                }
            }
        }
    }
}

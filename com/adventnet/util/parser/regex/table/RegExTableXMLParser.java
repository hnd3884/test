package com.adventnet.util.parser.regex.table;

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

public class RegExTableXMLParser
{
    public TableObject parseRule(final String s) throws ParseException {
        TableObject tableObject = null;
        try {
            if (s == null) {
                throw new RegExParserException("RegExTableXMLParser: no rule defined");
            }
            final Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes()));
            if (parse == null && parse.getDocumentElement() == null) {
                throw new RegExParserException("RegExParserException: document root element missing");
            }
            tableObject = this.getTableObject(parse.getDocumentElement());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return tableObject;
    }
    
    public TableObject getTableObject(final Element element) throws ParseException {
        final TableObject tableObject = new TableObject();
        final NodeList elementsByTagName = element.getElementsByTagName("TABLE");
        if (elementsByTagName != null) {
            final Node item = elementsByTagName.item(0);
            if (item != null && item.getNodeType() == 1) {
                tableObject.setRegExpression(this.getRegExforNode((Element)item));
            }
        }
        return tableObject;
    }
    
    public TableRegEx getRegExforNode(final Element element) throws ParseException {
        final NodeList elementsByTagName = element.getElementsByTagName("REGEX");
        if (elementsByTagName != null) {
            for (int i = 0; i < elementsByTagName.getLength(); ++i) {
                final Node item = elementsByTagName.item(i);
                if (item.getNodeType() == 1 || item.getNodeName().equals("REGEX")) {
                    final TableRegEx tableRegEx = new TableRegEx();
                    final Element element2 = (Element)item;
                    if (element2 != null) {
                        final String attribute = element2.getAttribute("NAME");
                        if (attribute == null || attribute.length() == 0) {
                            throw new ParseException("name attribute not defined");
                        }
                        tableRegEx.setName(attribute);
                        final String attribute2 = element2.getAttribute("ROWEXP");
                        if (attribute2 == null || attribute2.length() == 0) {
                            throw new ParseException("regular expression not defined");
                        }
                        tableRegEx.setExpression(attribute2);
                        final String attribute3 = element2.getAttribute("REFERENCE");
                        if (attribute3 != null && attribute3.length() != 0) {
                            tableRegEx.setReference(attribute3);
                        }
                        final String attribute4 = element2.getAttribute("STARTLINE");
                        if (attribute4 != null && attribute4.length() != 0) {
                            try {
                                tableRegEx.setStartLine(Integer.parseInt(attribute4));
                            }
                            catch (final NumberFormatException ex) {
                                throw new ParseException("Valid value, integer required for startline " + ex.getMessage());
                            }
                        }
                        final String attribute5 = element2.getAttribute("ENDLINE");
                        if (attribute5 != null && attribute5.length() != 0) {
                            try {
                                tableRegEx.setEndLine(Integer.parseInt(attribute5));
                            }
                            catch (final NumberFormatException ex2) {
                                throw new ParseException("Valid value (integer) required for endline " + ex2.getMessage());
                            }
                        }
                        tableRegEx.setParameterList(this.getParamNodeList(element2));
                        return tableRegEx;
                    }
                }
            }
        }
        return null;
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
                    final String attribute2 = element2.getAttribute("TOKENNO");
                    int int1 = -1;
                    if (attribute2 != null) {
                        try {
                            int1 = Integer.parseInt(attribute2);
                        }
                        catch (final Exception ex) {
                            throw new ParseException("Not a valid Token number for parameter " + attribute);
                        }
                    }
                    if (attribute != null && int1 >= 0) {
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
    
    public ParserResponseObject parseMessage(final TableObject tableObject, final String s) throws ParseException {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "\r\n");
        final String[] array = new String[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            array[n++] = stringTokenizer.nextToken();
        }
        final TableRegEx regExpression = tableObject.getRegExpression();
        final int startLine = regExpression.getStartLine();
        final int endLine = regExpression.getEndLine();
        final ParserResponseObject parserResponseObject = new ParserResponseObject();
        int n2 = 0;
        for (int initializeStartEnd = this.initializeStartEnd(startLine, endLine, array.length), i = startLine; i <= initializeStartEnd; ++i) {
            final Matcher matcher = Pattern.compile(regExpression.getExpression()).matcher(array[i]);
            try {
                if (matcher.matches()) {
                    final ArrayList parameterList = regExpression.getParameterList();
                    for (int j = 0; j < parameterList.size(); ++j) {
                        final ParameterObject parameterObject = parameterList.get(j);
                        final int tokenNo = parameterObject.getTokenNo();
                        if (tokenNo > matcher.groupCount()) {
                            throw new ParseException(tokenNo + " token does not exist!!");
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
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                throw new ParseException(ex.getMessage());
            }
        }
        return parserResponseObject;
    }
    
    private boolean isColumnAlreadyPresent(final String s, final ParserResponseObject parserResponseObject) {
        return parserResponseObject.getColumnObject(s) != null;
    }
    
    public void addNewColumn(final String name, final String s, final ParserResponseObject parserResponseObject, final int n) {
        final ColumnObject columnObject = new ColumnObject();
        columnObject.setName(name);
        columnObject.addColumnEntry(s);
        parserResponseObject.put(new Integer(n), columnObject);
    }
    
    public int initializeStartEnd(final int n, int n2, final int n3) throws ParseException {
        if (n2 <= 0) {
            n2 = n3 + n2 - 1;
        }
        if (n < 0) {
            throw new ParseException("Start line should not be less than zero.");
        }
        if (n > n2) {
            throw new ParseException("Start line greater than end line");
        }
        if (n2 > n3 - 1) {
            throw new ParseException("End line greater than number of lines");
        }
        return n2;
    }
}

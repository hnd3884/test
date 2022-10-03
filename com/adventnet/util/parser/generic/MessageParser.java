package com.adventnet.util.parser.generic;

import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Arrays;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.ParserConfigurationException;
import com.adventnet.util.parser.ParseException;
import java.util.Hashtable;
import java.util.Vector;

public class MessageParser
{
    String rulesFile;
    ParsedInfo parsedInfo;
    TokenInfo tokenInfo;
    XmlParser xmlParser;
    Vector resultTokens;
    Hashtable patternTable;
    Vector resultVector;
    Hashtable result;
    
    public MessageParser() throws ParseException {
        this.tokenInfo = null;
        this.xmlParser = null;
        this.resultTokens = null;
        this.result = null;
        this.rulesFile = this.rulesFile;
        try {
            this.xmlParser = new XmlParser();
        }
        catch (final ParserConfigurationException ex) {
            throw new ParseException(ex.getMessage());
        }
    }
    
    public void parseRule(final String s) throws ParseException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
        try {
            this.parseXml(byteArrayInputStream);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage());
        }
    }
    
    public MessageParser(final String rulesFile) throws ParseException {
        this.tokenInfo = null;
        this.xmlParser = null;
        this.resultTokens = null;
        this.result = null;
        this.rulesFile = rulesFile;
        try {
            this.xmlParser = new XmlParser();
        }
        catch (final ParserConfigurationException ex) {
            throw new ParseException(ex.getMessage());
        }
        try {
            this.parseXml();
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
            throw new ParseException(ex2.getMessage());
        }
    }
    
    public String getRulesFileName() {
        return this.rulesFile;
    }
    
    public void setRulesFileName(final String rulesFile) throws ParseException {
        this.rulesFile = rulesFile;
        try {
            this.parseXml();
        }
        catch (final Exception ex) {
            throw new ParseException(ex.getMessage());
        }
    }
    
    void parseXml(final InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        this.xmlParser.parseXml(inputStream);
        this.parsedInfo = this.xmlParser.getParsedInfo();
        this.tokenInfo = this.parsedInfo.getTokenInfo();
    }
    
    void parseXml() throws SAXException, IOException, ParserConfigurationException {
        this.xmlParser.setXmlFile(this.rulesFile);
        this.xmlParser.parseXml();
        this.parsedInfo = this.xmlParser.getParsedInfo();
        this.tokenInfo = this.parsedInfo.getTokenInfo();
    }
    
    public String getVersion() {
        if (this.parsedInfo == null) {
            return null;
        }
        return this.parsedInfo.getVersion();
    }
    
    boolean isVersionGreater(final String s) {
        return Float.parseFloat(this.getVersion()) > Float.parseFloat(s);
    }
    
    public Vector parseIntoTokens(final String s) {
        if (!this.isVersionGreater("1.0")) {
            return this.splitTokens(s, this.tokenInfo.getDelimiterList());
        }
        try {
            this.getParameters(s);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return this.resultTokens;
    }
    
    Vector splitTokens(String substring, final Vector vector) {
        final Vector vector2 = new Vector();
        final Vector resultTokens = new Vector();
        for (int size = vector.size(), i = 0; i < size; ++i) {
            final Vector vector3 = vector.elementAt(i);
            for (int size2 = vector3.size(), j = 0; j < size2; ++j) {
                if (!this.isVersionGreater("1.0")) {
                    vector2.addElement(((Delimiter)vector3.elementAt(j)).getDelimiterValue());
                }
                else {
                    vector2.addElement(vector3.elementAt(j));
                }
            }
        }
        while (true) {
            this.removeDelimitersWithoutMatch(vector2, substring);
            final int size3 = vector2.size();
            if (size3 == 0) {
                break;
            }
            final Hashtable<Integer, String> hashtable = new Hashtable<Integer, String>();
            final int[] array = new int[size3];
            for (int k = 0; k < size3; ++k) {
                final int index = substring.indexOf(vector2.elementAt(k));
                array[k] = index;
                hashtable.put(new Integer(index), vector2.elementAt(k));
            }
            Arrays.sort(array);
            final int length = hashtable.get(new Integer(array[0])).length();
            final String substring2 = substring.substring(0, array[0]);
            if (substring2.length() != 0) {
                resultTokens.addElement(substring2);
            }
            substring = substring.substring(array[0] + length);
        }
        if (substring.length() != 0) {}
        resultTokens.addElement(substring);
        return this.resultTokens = resultTokens;
    }
    
    void removeDelimitersWithoutMatch(final Vector vector, final String s) {
        final Vector vector2 = new Vector();
        for (int i = 0; i < vector.size(); ++i) {
            if (s.indexOf((String)vector.elementAt(i)) < 0) {
                vector2.addElement(new Integer(i));
            }
        }
        for (int j = 0; j < vector2.size(); ++j) {
            vector.removeElement(vector.elementAt((int)vector2.elementAt(j) - j));
        }
    }
    
    public Vector getMatchList() {
        return this.resultVector;
    }
    
    synchronized Vector getParameters(final String s) throws ParseException {
        this.resultVector = new Vector();
        this.patternTable = new Hashtable();
        try {
            final Delimiter topLevelDelimiter = this.getTopLevelDelimiter();
            if (topLevelDelimiter.getMatch() != null && topLevelDelimiter.getChildList() == null) {
                System.out.println(" into single level delimiter");
                this.getOneLevelParamValues(topLevelDelimiter, s);
            }
            else {
                this.getParams(topLevelDelimiter, s);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage());
        }
        return this.resultVector;
    }
    
    Delimiter getTopLevelDelimiter() throws Exception {
        return this.tokenInfo.getDelimiterList().lastElement().elementAt(0);
    }
    
    void getParams(final Delimiter delimiter, final String s) throws Exception {
        final Match match = delimiter.getMatch();
        Vector vector = this.splitIntoTokens(s, delimiter.getDelimiterList());
        if (match != null) {
            vector = this.checkForMatch(vector, match);
        }
        final Delimiter[] childList = delimiter.getChildList();
        if (childList == null) {
            return;
        }
        for (int i = 0; i < vector.size(); ++i) {
            this.getParams(childList[0], (String)vector.elementAt(i));
        }
        if (this.result != null && this.result.size() > 0) {
            this.resultVector.addElement(this.result);
        }
    }
    
    Vector checkForMatch(final Vector vector, final Match match) {
        final Enumeration elements = match.getPatternList().elements();
        while (elements.hasMoreElements()) {
            final Pattern pattern = (Pattern)elements.nextElement();
            for (int i = 0; i < vector.size(); ++i) {
                if (((String)vector.elementAt(i)).equals(pattern.getPattern())) {
                    if (pattern.isIgnore()) {
                        vector.removeElementAt(i);
                    }
                    else if (pattern.isSubs()) {
                        if (pattern.isFirst()) {
                            if (this.result == null) {
                                this.result = new Hashtable();
                            }
                            else {
                                if (this.result.size() > 0) {
                                    this.resultVector.addElement(this.result);
                                }
                                this.result = new Hashtable();
                            }
                        }
                        this.doParamSubstitution(pattern.getParameterList(), vector, i, this.result);
                    }
                }
            }
        }
        return vector;
    }
    
    void doParamSubstitution(final Hashtable hashtable, final Vector vector, final int n, final Hashtable hashtable2) {
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            hashtable2.put(s, this.getValueFromTokens(vector, n, (String)hashtable.get(s)));
        }
    }
    
    String getValueFromTokens(final Vector vector, final int n, final String s) {
        String s2 = "";
        if (s.startsWith("$")) {
            int n2 = 0;
            final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
            final String[] array = new String[stringTokenizer.countTokens()];
            while (stringTokenizer.hasMoreTokens()) {
                array[n2] = stringTokenizer.nextToken();
                ++n2;
            }
            for (int i = 0; i < array.length; ++i) {
                final int index = array[i].indexOf("+");
                if (index >= 0) {
                    int int1 = 0;
                    try {
                        if (array[i].indexOf("MIN") < 0) {
                            int1 = Integer.parseInt(array[i].substring(1, index));
                        }
                    }
                    catch (final NumberFormatException ex) {
                        ex.printStackTrace();
                        return "";
                    }
                    for (int j = n + int1; j < vector.size(); ++j) {
                        s2 = s2 + vector.elementAt(j) + " ";
                    }
                    break;
                }
                int n3;
                try {
                    if (array[i].indexOf("MIN") > 0) {
                        n3 = 0;
                    }
                    else if (array[i].indexOf("MAX") > 0) {
                        n3 = vector.size() - 1;
                    }
                    else {
                        n3 = n + Integer.parseInt(array[i].substring(1));
                    }
                }
                catch (final NumberFormatException ex2) {
                    ex2.printStackTrace();
                    return "";
                }
                if (n3 > vector.size() || n3 < 0) {
                    return "";
                }
                s2 = s2 + vector.elementAt(n3) + " ";
            }
            return s2;
        }
        return s;
    }
    
    Vector splitIntoTokens(String substring, final String[] array) {
        if (array.length == 1) {
            final String s = array[0];
            final Vector vector = new Vector();
            while (true) {
                final int index = substring.indexOf(s);
                if (index < 0) {
                    break;
                }
                vector.addElement(substring.substring(0, index));
                substring = substring.substring(index + 1);
            }
            vector.addElement(substring);
            return vector;
        }
        final Vector vector2 = new Vector();
        for (int i = 0; i < array.length; ++i) {
            vector2.addElement(array[i]);
        }
        final Vector vector3 = new Vector();
        vector3.addElement(vector2);
        return this.splitTokens(substring, vector3);
    }
    
    private void getOneLevelParamValues(final Delimiter delimiter, final String s) {
        final Match match = delimiter.getMatch();
        final Vector splitIntoTokens = this.splitIntoTokens(s, delimiter.getDelimiterList());
        if (match != null) {
            this.checkForOneLevelMatch(splitIntoTokens, match);
        }
        if (this.result != null && this.result.size() > 0) {
            this.resultVector.addElement(this.result);
        }
    }
    
    private Vector checkForOneLevelMatch(final Vector vector, final Match match) {
        final Hashtable patternList = match.getPatternList();
        for (int i = 0; i < vector.size(); ++i) {
            final String s = vector.elementAt(i);
            final Enumeration elements = patternList.elements();
            while (elements.hasMoreElements()) {
                final Pattern pattern = (Pattern)elements.nextElement();
                if (s.equals(pattern.getPattern())) {
                    if (pattern.isIgnore()) {
                        vector.removeElementAt(i);
                    }
                    else {
                        if (!pattern.isSubs()) {
                            continue;
                        }
                        if (pattern.isFirst()) {
                            if (this.result == null) {
                                this.result = new Hashtable();
                            }
                            else {
                                if (this.result.size() > 0) {
                                    this.resultVector.addElement(this.result);
                                }
                                this.result = new Hashtable();
                            }
                        }
                        this.doParamSubstitution(pattern.getParameterList(), vector, i, this.result);
                    }
                }
            }
        }
        return vector;
    }
}

package com.adventnet.util.parser.line;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

class LineRuleResponseParser
{
    LineRuleResponseParser() throws Exception {
    }
    
    Object parseMessage(final Vector vector, final String s) throws Exception {
        final ParsedResult parsedResult = new ParsedResult();
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "\n");
        final int countTokens = stringTokenizer.countTokens();
        final String[] array = new String[countTokens];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            array[n++] = stringTokenizer.nextToken();
        }
        int matchingLine = 0;
        for (int i = 0; i < vector.size(); ++i) {
            final LineRule lineRule = vector.elementAt(i);
            final int startLineNo = lineRule.getStartLineNo();
            if (startLineNo > 0) {
                matchingLine = startLineNo;
            }
            final int endLineNo = lineRule.getEndLineNo();
            if (endLineNo > countTokens) {
                throw new Exception("Invalid line no");
            }
            if (lineRule.isTable()) {
                final Vector paramList = lineRule.getParamList();
                if (((Param)paramList.elementAt(0)).getPattern() != null && lineRule.getStartLineNo() < 0) {
                    matchingLine = this.getMatchingLine(array, matchingLine, paramList);
                }
                final StringTokenizer stringTokenizer2 = new StringTokenizer(array[matchingLine], lineRule.getDelimiter());
                boolean b = false;
                Vector<Param> vector2;
                if (((Param)paramList.elementAt(0)).getPattern() != null) {
                    b = true;
                    ++matchingLine;
                    vector2 = new Vector<Param>();
                    int tokenNo = 0;
                    while (stringTokenizer2.hasMoreTokens()) {
                        final String nextToken = stringTokenizer2.nextToken();
                        for (int j = 0; j < paramList.size(); ++j) {
                            final Param param = paramList.elementAt(j);
                            if (nextToken.indexOf(param.getPattern()) >= 0) {
                                param.setTokenNo(tokenNo);
                                vector2.addElement(param);
                                break;
                            }
                        }
                        ++tokenNo;
                    }
                }
                else {
                    vector2 = paramList;
                }
                final Hashtable tableParameters = this.getTableParameters(array, vector2, matchingLine, endLineNo, lineRule.getDelimiter(), b);
                if (endLineNo > 0) {
                    matchingLine = endLineNo + 1;
                }
                parsedResult.setTableResult(tableParameters);
            }
            else {
                Properties result = parsedResult.getResult();
                if (result == null) {
                    result = new Properties();
                    parsedResult.setResult(result);
                }
                final Vector paramList2 = lineRule.getParamList();
                boolean b2 = false;
                for (int k = 0; k < paramList2.size(); ++k) {
                    final Param param2 = paramList2.elementAt(k);
                    if (param2.getPattern() != null) {
                        final String pattern = param2.getPattern();
                        int index = 0;
                        int n2;
                        for (n2 = matchingLine; n2 < array.length && (index = array[n2].indexOf(pattern)) < 0; ++n2) {}
                        if (n2 != array.length) {
                            final StringTokenizer stringTokenizer3 = new StringTokenizer(array[n2].substring(index + pattern.length()), lineRule.getDelimiter());
                            final String[] array2 = new String[stringTokenizer3.countTokens()];
                            int n3 = 0;
                            while (stringTokenizer3.hasMoreTokens()) {
                                array2[n3++] = stringTokenizer3.nextToken();
                            }
                            String trim;
                            if (param2.getTokenNo() >= array2.length) {
                                trim = "";
                            }
                            else {
                                trim = array2[param2.getTokenNo()];
                            }
                            final String trimString = param2.getTrimString();
                            if (trimString != null && trimString.length() > 0) {
                                trim = this.trim(trim, trimString);
                            }
                            result.setProperty(param2.getParamName(), trim);
                        }
                    }
                    else {
                        b2 = true;
                        final StringTokenizer stringTokenizer4 = new StringTokenizer(array[matchingLine], lineRule.getDelimiter());
                        final String[] array3 = new String[stringTokenizer4.countTokens()];
                        int n4 = 0;
                        while (stringTokenizer4.hasMoreTokens()) {
                            array3[n4++] = stringTokenizer4.nextToken();
                        }
                        String trim2;
                        if (param2.getTokenNo() >= array3.length) {
                            trim2 = "";
                        }
                        else {
                            trim2 = array3[param2.getTokenNo()];
                        }
                        final String trimString2 = param2.getTrimString();
                        if (trimString2 != null && trimString2.length() > 0) {
                            trim2 = this.trim(trim2, trimString2);
                        }
                        result.setProperty(param2.getParamName(), trim2);
                    }
                }
                if (b2) {
                    ++matchingLine;
                }
            }
            if (matchingLine > array.length) {
                break;
            }
        }
        return parsedResult;
    }
    
    private int getMatchingLine(final String[] array, final int n, final Vector vector) {
        int n2 = 0;
        int n3 = 0;
        for (int i = n; i < array.length; ++i) {
            int n4 = 0;
            for (int j = 0; j < vector.size(); ++j) {
                if (array[i].indexOf(((Param)vector.elementAt(j)).getPattern()) >= 0) {
                    ++n4;
                }
            }
            if (n4 >= n2) {
                n2 = n4;
                n3 = i;
            }
        }
        return n3;
    }
    
    private Hashtable getTableParameters(final String[] array, final Vector vector, final int n, final int n2, final String s, final boolean b) {
        final Hashtable hashtable = new Hashtable();
        for (int i = n; i < array.length + n2; ++i) {
            final StringTokenizer stringTokenizer = new StringTokenizer(array[i], s);
            final int countTokens = stringTokenizer.countTokens();
            final String[] array2 = new String[countTokens];
            for (int j = 0; j < countTokens; ++j) {
                array2[j] = stringTokenizer.nextToken();
            }
            final String[] array3 = new String[vector.size()];
            for (int k = 0; k < vector.size(); ++k) {
                final Param param = vector.elementAt(k);
                ArrayList<?> list = hashtable.get(param.getParamName());
                if (list == null) {
                    list = new ArrayList<Object>();
                    hashtable.put(param.getParamName(), list);
                }
                if (b) {
                    final int tokenNo = param.getTokenNo();
                    if (tokenNo >= array2.length) {
                        list.add("");
                    }
                    else {
                        list.add(array2[tokenNo]);
                    }
                }
                else if (k >= array2.length) {
                    list.add("");
                }
                else {
                    list.add(array2[k]);
                }
            }
        }
        return hashtable;
    }
    
    private String trim(String s, final String s2) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s2, ",");
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            final int length = nextToken.length();
            for (int i = s.indexOf(nextToken); i >= 0; i = s.indexOf(nextToken)) {
                if (i == 0) {
                    s = s.substring(length);
                }
                else {
                    s = s.substring(0, i) + s.substring(i + length);
                }
            }
        }
        return s;
    }
}

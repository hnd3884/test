package com.me.devicemanagement.framework.server.sql;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class DefaultVariableHandler implements VariablesHandler
{
    @Override
    public String handleVariables(String sqlString, final Properties variableValues) throws Exception {
        if (variableValues == null || variableValues.isEmpty()) {
            return sqlString;
        }
        final Enumeration keys = variableValues.keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            if (sqlString.contains(key)) {
                final Object valueObj = ((Hashtable<K, Object>)variableValues).get(key);
                String replaceStr = null;
                if (valueObj instanceof String) {
                    boolean isReplaceQuote = Boolean.TRUE;
                    if (variableValues != null && variableValues.containsKey("isReplaceQuote")) {
                        isReplaceQuote = Boolean.parseBoolean(String.valueOf(((Hashtable<K, Object>)variableValues).get("isReplaceQuote")));
                    }
                    if (isReplaceQuote) {
                        replaceStr = this.replaceQuotes((String)valueObj);
                    }
                    if (replaceStr == null) {
                        replaceStr = (String)valueObj;
                    }
                }
                else if (valueObj instanceof List) {
                    final List valueList = (List)valueObj;
                    final int vsize = valueList.size();
                    final StringBuffer sb = new StringBuffer();
                    for (int s = 0; s < vsize; ++s) {
                        final Object valObj = valueList.get(s);
                        if (s > 0) {
                            sb.append(",");
                        }
                        if (valObj instanceof String) {
                            String valStr = this.replaceQuotes((String)valObj);
                            valStr = "'" + valStr + "'";
                            sb.append(valStr);
                        }
                        else {
                            sb.append(valObj);
                        }
                    }
                    replaceStr = sb.toString();
                }
                sqlString = sqlString.replace(key, replaceStr);
            }
        }
        return sqlString;
    }
    
    private String replaceQuotes(String inputStr) {
        if (inputStr.indexOf("'") != -1) {
            inputStr = inputStr.replaceAll("'", "''");
        }
        return inputStr;
    }
}

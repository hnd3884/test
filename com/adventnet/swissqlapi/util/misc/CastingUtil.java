package com.adventnet.swissqlapi.util.misc;

import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import com.adventnet.swissqlapi.SwisSQLAPI;

public class CastingUtil
{
    public static boolean functionParameter;
    
    public static String getDB2DataTypeCastedString(String sourceDataType, String targetDataType, String expr) {
        if (targetDataType == null) {
            return expr;
        }
        if (expr.equalsIgnoreCase("sysdate")) {
            return expr;
        }
        if (expr.trim().toLowerCase().startsWith("coalesce(")) {
            return expr;
        }
        if ((targetDataType.trim().toLowerCase().startsWith("varchar") || targetDataType.trim().toLowerCase().startsWith("char")) && (expr.trim().toLowerCase().startsWith("char") || expr.trim().toLowerCase().startsWith("varchar") || (expr.trim().startsWith("'") && expr.trim().endsWith("'")) || expr.indexOf("||") != -1)) {
            return expr;
        }
        if (targetDataType.trim().toLowerCase().startsWith("double") && expr.trim().toLowerCase().startsWith("double")) {
            return expr;
        }
        if (targetDataType.trim().toLowerCase().startsWith("integer") && expr.trim().toLowerCase().startsWith("integer")) {
            return expr;
        }
        if (targetDataType.trim().toLowerCase().startsWith("timestamp") && expr.trim().toLowerCase().startsWith("timestamp")) {
            return expr;
        }
        if (targetDataType.trim().toLowerCase().startsWith("decimal") && expr.trim().toLowerCase().startsWith("decimal")) {
            return expr;
        }
        if (sourceDataType == null) {
            expr = expr.trim();
            if (expr.startsWith("'") && expr.endsWith("'")) {
                sourceDataType = "varchar";
            }
            else {
                try {
                    Integer.parseInt(expr);
                    sourceDataType = "integer";
                }
                catch (final NumberFormatException nfe) {
                    try {
                        Float.parseFloat(expr);
                        sourceDataType = "decimal";
                    }
                    catch (final NumberFormatException ex) {}
                }
            }
            if (sourceDataType == null) {
                targetDataType = targetDataType.trim();
                if (targetDataType.equalsIgnoreCase("float") || targetDataType.trim().equalsIgnoreCase("double")) {
                    expr = "DOUBLE(" + expr + ")";
                }
                else if (targetDataType.equalsIgnoreCase("decimal")) {
                    expr = "DECIMAL(" + expr + ")";
                }
                else if (targetDataType.equalsIgnoreCase("integer")) {
                    expr = "INTEGER(" + expr + ")";
                }
                else if (targetDataType.equalsIgnoreCase("timestamp")) {
                    if (expr.trim().equalsIgnoreCase("current timestamp")) {
                        return expr;
                    }
                    expr = "TIMESTAMP(" + expr + ")";
                }
                else if (targetDataType.toLowerCase().startsWith("varchar")) {
                    expr = "VARCHAR(RTRIM(CHAR(" + expr + ")))";
                }
                else if (targetDataType.equalsIgnoreCase("char")) {
                    expr = "RTRIM(CHAR(" + expr + "))";
                }
                else {
                    expr = targetDataType.toUpperCase() + "(CHAR(" + expr + "))";
                }
                return expr;
            }
        }
        sourceDataType = sourceDataType.trim();
        targetDataType = targetDataType.trim();
        if (!sourceDataType.toLowerCase().equals(targetDataType.toLowerCase())) {
            Label_0727: {
                if (!targetDataType.toLowerCase().startsWith("float")) {
                    if (!targetDataType.toLowerCase().startsWith("double")) {
                        break Label_0727;
                    }
                }
                try {
                    Integer.parseInt(expr);
                    return expr;
                }
                catch (final NumberFormatException nfe) {
                    try {
                        Float.parseFloat(expr);
                        return expr;
                    }
                    catch (final NumberFormatException ex2) {
                        if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                            expr = "DOUBLE(" + expr + ")";
                            return expr;
                        }
                        return expr;
                    }
                }
            }
            if (targetDataType.toLowerCase().startsWith("decimal")) {
                if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                    expr = "DECIMAL(" + expr + " ,31,18)";
                }
            }
            else if (targetDataType.toLowerCase().equals("integer")) {
                if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                    expr = "INTEGER(" + expr + ")";
                }
                else if (CastingUtil.functionParameter && (sourceDataType.toLowerCase().equals("float") || sourceDataType.toLowerCase().equals("double"))) {
                    expr = "INTEGER(" + expr + ")";
                }
            }
            else if (targetDataType.toLowerCase().equals("bigint") || targetDataType.toLowerCase().equals("smallint")) {
                if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                    expr = targetDataType.toUpperCase() + "(" + expr + ")";
                }
            }
            else if (targetDataType.toLowerCase().startsWith("varchar")) {
                if (sourceDataType.toLowerCase().equals("float") || sourceDataType.toLowerCase().equals("double") || sourceDataType.toLowerCase().equals("decimal") || sourceDataType.toLowerCase().equals("smallint") || sourceDataType.toLowerCase().equals("integer") || sourceDataType.toLowerCase().equals("bigint") || sourceDataType.toLowerCase().equals("int") || sourceDataType.toLowerCase().equals("timestamp") || sourceDataType.toLowerCase().equals("date")) {
                    expr = "VARCHAR(RTRIM(CHAR(" + expr + ")))";
                }
                else if (!sourceDataType.toLowerCase().equals("char")) {
                    if (!sourceDataType.toLowerCase().startsWith("varchar")) {
                        expr = "VARCHAR(" + expr + ")";
                    }
                }
            }
            else if (targetDataType.toLowerCase().startsWith("char")) {
                if (!sourceDataType.toLowerCase().equals("varchar")) {
                    expr = "RTRIM(CHAR(" + expr + "))";
                }
            }
            else if (targetDataType.toLowerCase().startsWith("timestamp")) {
                if (expr.trim().equalsIgnoreCase("current timestamp")) {
                    return expr;
                }
                if (sourceDataType.toLowerCase().equals("varchar") || sourceDataType.toLowerCase().equals("char")) {
                    expr = "TIMESTAMP(" + expr + ")";
                }
            }
            else {
                expr = targetDataType.toUpperCase() + "(" + expr + ")";
            }
        }
        return expr;
    }
    
    public static String getDB2DataTypeCastedParameter(final String sourceDataType, final String targetDataType, String expr) {
        CastingUtil.functionParameter = true;
        expr = getDB2DataTypeCastedString(sourceDataType, targetDataType, expr);
        CastingUtil.functionParameter = false;
        return expr;
    }
    
    public static String getDataType(final String dataType) {
        if (dataType == null) {
            return null;
        }
        if (dataType.indexOf("(") != -1) {
            return dataType.substring(0, dataType.indexOf("("));
        }
        return dataType;
    }
    
    public static String getReturnDataType(final String builtInFunctionName) {
        if (SwisSQLAPI.builtInFunctionDetails != null) {
            return SwisSQLAPI.builtInFunctionDetails.getReturnDataType(builtInFunctionName);
        }
        return null;
    }
    
    public static String getParameterDataType(final String builtInFunctionName, final int paramNum) {
        if (SwisSQLAPI.builtInFunctionDetails != null) {
            return SwisSQLAPI.builtInFunctionDetails.getParameterDataType(builtInFunctionName, paramNum);
        }
        return null;
    }
    
    public static Object getValueIgnoreCase(final Map ht, final String variableName) {
        Object obj = ht.get(variableName);
        if (obj == null) {
            obj = ht.get(variableName.toLowerCase());
        }
        if (obj == null) {
            obj = ht.get(variableName.toUpperCase());
        }
        if (obj == null) {
            final Set keys = ht.keySet();
            for (final Object keyObj : keys) {
                if (keyObj.toString().equalsIgnoreCase(variableName)) {
                    return ht.get(keyObj);
                }
            }
        }
        return obj;
    }
    
    public static boolean ContainsIgnoreCase(final List list, final String variableName) {
        if (list.contains(variableName) || list.contains(variableName.toLowerCase()) || list.contains(variableName.toUpperCase())) {
            return true;
        }
        for (int i = 0; i < list.size(); ++i) {
            final Object obj = list.get(i);
            if (obj instanceof String) {
                final String listValue = (String)obj;
                if (listValue.equalsIgnoreCase(variableName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        CastingUtil.functionParameter = false;
    }
}

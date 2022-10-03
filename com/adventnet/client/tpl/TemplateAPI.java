package com.adventnet.client.tpl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Arrays;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.cache.StaticCache;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TemplateAPI
{
    public static Pattern multiParenthisisPattern;
    public static Pattern simplePattern;
    public static HashMap htmlmap;
    public static HashMap lastReadTime;
    
    public static DataObject getAsDataObject(final String templateXMLFile, final boolean isRelativeToServerHome, final VariableHandler varHandler, final Object handlerContext) throws Exception {
        final String xmlContent = getFilledString(templateXMLFile, isRelativeToServerHome, varHandler, handlerContext);
        return Xml2DoConverter.transform(new InputSource(new StringReader(xmlContent)));
    }
    
    public static String getFilledString(String templateFile, final boolean isRelativeToServerHome, final VariableHandler varHandler, final Object handlerContext) throws Exception {
        if (isRelativeToServerHome) {
            templateFile = System.getProperty("server.dir") + "/" + templateFile;
        }
        final String fileData = getFileAsString(templateFile);
        StringBuffer strBuf = new StringBuffer();
        final Matcher mat = TemplateAPI.multiParenthisisPattern.matcher(fileData);
        int pos = -1;
        while (mat.find()) {
            final String variable = mat.group(1);
            mat.appendReplacement(strBuf, getVariableValue(getVariableDef(variable), ++pos, varHandler, handlerContext));
        }
        strBuf = mat.appendTail(strBuf);
        return strBuf.toString();
    }
    
    public static String getVariableValue(final String[] varDef, final int pos, final VariableHandler varHandler, final Object handlerContext) throws Exception {
        Object value = null;
        if (varDef[0] != null) {
            value = getVariableHandler(varDef[0]).getVariableValue(varDef[1], pos, handlerContext);
        }
        else if (varHandler != null) {
            value = varHandler.getVariableValue(varDef[1], pos, handlerContext);
        }
        else if (handlerContext instanceof Object[][]) {
            final Object[][] mapArr = (Object[][])handlerContext;
            for (int i = 0; i < mapArr.length; ++i) {
                if (mapArr[i][0].equals(varDef[1])) {
                    value = mapArr[i][1];
                    break;
                }
            }
        }
        else if (handlerContext instanceof Object[]) {
            value = ((Object[])handlerContext)[pos];
        }
        else {
            if (!(handlerContext instanceof Map)) {
                throw new IllegalArgumentException("Neither VariableHandler is passed, nor is the handlerContext of type String[] or Map for variable " + varDef[1]);
            }
            value = ((Map)handlerContext).get(varDef[1]);
        }
        if (value == null) {
            value = varDef[2];
        }
        if (value != null && !(value instanceof String)) {
            value = String.valueOf(value);
        }
        String retvalue = (String)value;
        final String validator = varDef[3];
        if (validator != null) {
            retvalue = checkAndEscapeSQLValueString(retvalue, validator);
        }
        return retvalue;
    }
    
    private static boolean hasSpecialCharacters(final String valueStr) {
        return valueStr.contains("'");
    }
    
    private static String escapeSpecialCharacters(final String valueStr) {
        return valueStr.replaceAll("'", "''");
    }
    
    public static String getEscapedSearchString(String val) {
        if (val.indexOf("\\") != -1) {
            val = val.replaceAll("\\\\", "\\\\\\\\");
        }
        if (val.indexOf("\"") != -1) {
            val = val.replaceAll("\"", "\\\\\"");
        }
        return val;
    }
    
    public static String checkAndEscapeSQLValueString(String retvalue, final String validator) throws Exception {
        if (validator.equals("L")) {
            Long.parseLong(retvalue);
        }
        else if (validator.equals("D")) {
            Double.parseDouble(retvalue);
        }
        else if (validator.equals("S")) {
            retvalue = escapeSpecialCharacters(retvalue);
            retvalue = getEscapedSearchString(retvalue);
        }
        else if (validator.equals("C") && hasSpecialCharacters(retvalue)) {
            throw new RuntimeException("Invalid column name");
        }
        return retvalue;
    }
    
    public static void fillCompiledInfo(final String templateString, final Object[] compiledInfo, final Pattern varPat) throws Exception {
        final String[] staticList = varPat.split(templateString);
        final List variables = new ArrayList(staticList.length);
        final Matcher mat = varPat.matcher(templateString);
        while (mat.find()) {
            final String combinedVar = mat.group(1);
            variables.add(getVariableDef(combinedVar));
        }
        compiledInfo[0] = staticList;
        compiledInfo[1] = variables.toArray(new String[variables.size()][]);
    }
    
    public static Object[] getCompiledInfo(final String templateString, final Pattern varPat) throws Exception {
        final String[] staticList = varPat.split(templateString);
        final List variables = new ArrayList(staticList.length);
        final Matcher mat = varPat.matcher(templateString);
        while (mat.find()) {
            final String combinedVar = mat.group(1);
            variables.add(getVariableDef(combinedVar));
        }
        final Object[] compiledInfo = { staticList, variables.toArray(new String[variables.size()][]) };
        return compiledInfo;
    }
    
    public static String[] getVariableDef(final String combinedVar) {
        final String[] varDetails = new String[4];
        final int index = combinedVar.indexOf(58);
        if (index > 0) {
            varDetails[0] = combinedVar.substring(0, index);
            final int index2 = combinedVar.indexOf(58, index + 1);
            final int index3 = combinedVar.indexOf(58, index2 + 1);
            if (index2 > 0) {
                varDetails[1] = combinedVar.substring(index + 1, index2);
                varDetails[2] = combinedVar.substring(index2 + 1);
                if (index3 > 0) {
                    varDetails[3] = combinedVar.substring(index3 + 1);
                }
            }
            else {
                varDetails[1] = combinedVar.substring(index + 1);
            }
        }
        else {
            varDetails[1] = combinedVar;
        }
        return varDetails;
    }
    
    public static String getFilledString(final Object[] compiledInfo, final VariableHandler varHandler, final Object handlerContext) throws Exception {
        final String[] staticList = (String[])compiledInfo[0];
        final String[][] variablesList = (String[][])compiledInfo[1];
        final StringBuilder completeString = new StringBuilder();
        for (int i = 0; i < staticList.length; ++i) {
            completeString.append(staticList[i]);
            if (i < variablesList.length) {
                final String value = getVariableValue(variablesList[i], i, varHandler, handlerContext);
                if (value != null) {
                    completeString.append(value);
                }
            }
        }
        return completeString.toString();
    }
    
    public static VariableHandler getVariableHandler(final String handlerName) {
        HashMap handlerMap = (HashMap)StaticCache.getFromCache("ACTEMPLATEHANDLERS");
        if (handlerMap == null) {
            try {
                handlerMap = new HashMap();
                final DataObject handlerDO = LookUpUtil.getPersistence().get("ACTemplateHandler", (Criteria)null);
                final Iterator ite = handlerDO.getRows("ACTemplateHandler");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    handlerMap.put(r.get("HANDLERNAME"), Class.forName((String)r.get("CLASSNAME")).newInstance());
                }
                StaticCache.addToCache("ACTEMPLATEHANDLERS", handlerMap, Arrays.asList("ACTemplateHandler"));
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        final VariableHandler handler = handlerMap.get(handlerName);
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined in ACTemplateHandler table: " + handlerName);
        }
        return handler;
    }
    
    public static String getFileAsString(final String fileName) throws IOException {
        final RandomAccessFile rd = new RandomAccessFile(fileName, "r");
        try {
            final byte[] arr = new byte[(int)rd.length()];
            rd.readFully(arr);
            return new String(arr);
        }
        finally {
            rd.close();
        }
    }
    
    public static DynamicStringGenerator getStringGenerator(final String stringGenerator) throws Exception {
        final DynamicStringGenerator dg = (DynamicStringGenerator)Class.forName(stringGenerator).newInstance();
        return dg;
    }
    
    public static String givehtml(final String key, final VariableHandler handler, final Object handlerContext) throws Exception {
        final Object[] compiledInfo = TemplateAPI.htmlmap.get(key);
        try {
            final String filledhtml = getFilledString(compiledInfo, handler, handlerContext);
            return filledhtml;
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Exception while trying to get template with key=" + key);
            return "";
        }
    }
    
    public static void setHtmlMap(final String key, final String value) throws Exception {
        final Object[] compiledInfo = getCompiledInfo(value, TemplateAPI.simplePattern);
        TemplateAPI.htmlmap.put(key, compiledInfo);
    }
    
    static {
        TemplateAPI.multiParenthisisPattern = Pattern.compile("\\$\\{\\{([^\\}]*)\\}\\}");
        TemplateAPI.simplePattern = Pattern.compile("\\$\\{([^\\}]*)\\}");
        TemplateAPI.htmlmap = new HashMap(149, 0.75f);
        TemplateAPI.lastReadTime = new HashMap(149, 0.75f);
    }
    
    public interface DynamicStringGenerator
    {
        String getString(final String p0) throws Exception;
    }
    
    public interface VariableHandler
    {
        String getVariableValue(final String p0, final int p1, final Object p2) throws Exception;
    }
}

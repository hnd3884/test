package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xpath.internal.functions.FuncUnparsedEntityURI;
import com.sun.org.apache.xpath.internal.functions.FuncDoclocation;
import com.sun.org.apache.xpath.internal.functions.FuncStringLength;
import com.sun.org.apache.xpath.internal.functions.FuncSubstring;
import com.sun.org.apache.xpath.internal.functions.FuncExtElementAvailable;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunctionAvailable;
import com.sun.org.apache.xpath.internal.functions.FuncSystemProperty;
import com.sun.org.apache.xpath.internal.functions.FuncConcat;
import com.sun.org.apache.xpath.internal.functions.FuncTranslate;
import com.sun.org.apache.xpath.internal.functions.FuncNormalizeSpace;
import com.sun.org.apache.xpath.internal.functions.FuncSubstringAfter;
import com.sun.org.apache.xpath.internal.functions.FuncSubstringBefore;
import com.sun.org.apache.xpath.internal.functions.FuncContains;
import com.sun.org.apache.xpath.internal.functions.FuncStartsWith;
import com.sun.org.apache.xpath.internal.functions.FuncString;
import com.sun.org.apache.xpath.internal.functions.FuncSum;
import com.sun.org.apache.xpath.internal.functions.FuncRound;
import com.sun.org.apache.xpath.internal.functions.FuncCeiling;
import com.sun.org.apache.xpath.internal.functions.FuncFloor;
import com.sun.org.apache.xpath.internal.functions.FuncNumber;
import com.sun.org.apache.xpath.internal.functions.FuncLang;
import com.sun.org.apache.xpath.internal.functions.FuncBoolean;
import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import com.sun.org.apache.xpath.internal.functions.FuncTrue;
import com.sun.org.apache.xpath.internal.functions.FuncNot;
import com.sun.org.apache.xpath.internal.functions.FuncGenerateId;
import com.sun.org.apache.xpath.internal.functions.FuncQname;
import com.sun.org.apache.xpath.internal.functions.FuncNamespace;
import com.sun.org.apache.xpath.internal.functions.FuncLocalPart;
import com.sun.org.apache.xpath.internal.functions.FuncId;
import com.sun.org.apache.xpath.internal.functions.FuncCount;
import com.sun.org.apache.xpath.internal.functions.FuncPosition;
import com.sun.org.apache.xpath.internal.functions.FuncLast;
import com.sun.org.apache.xpath.internal.functions.FuncCurrent;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.functions.Function;
import java.util.HashMap;

public class FunctionTable
{
    public static final int FUNC_CURRENT = 0;
    public static final int FUNC_LAST = 1;
    public static final int FUNC_POSITION = 2;
    public static final int FUNC_COUNT = 3;
    public static final int FUNC_ID = 4;
    public static final int FUNC_KEY = 5;
    public static final int FUNC_LOCAL_PART = 7;
    public static final int FUNC_NAMESPACE = 8;
    public static final int FUNC_QNAME = 9;
    public static final int FUNC_GENERATE_ID = 10;
    public static final int FUNC_NOT = 11;
    public static final int FUNC_TRUE = 12;
    public static final int FUNC_FALSE = 13;
    public static final int FUNC_BOOLEAN = 14;
    public static final int FUNC_NUMBER = 15;
    public static final int FUNC_FLOOR = 16;
    public static final int FUNC_CEILING = 17;
    public static final int FUNC_ROUND = 18;
    public static final int FUNC_SUM = 19;
    public static final int FUNC_STRING = 20;
    public static final int FUNC_STARTS_WITH = 21;
    public static final int FUNC_CONTAINS = 22;
    public static final int FUNC_SUBSTRING_BEFORE = 23;
    public static final int FUNC_SUBSTRING_AFTER = 24;
    public static final int FUNC_NORMALIZE_SPACE = 25;
    public static final int FUNC_TRANSLATE = 26;
    public static final int FUNC_CONCAT = 27;
    public static final int FUNC_SUBSTRING = 29;
    public static final int FUNC_STRING_LENGTH = 30;
    public static final int FUNC_SYSTEM_PROPERTY = 31;
    public static final int FUNC_LANG = 32;
    public static final int FUNC_EXT_FUNCTION_AVAILABLE = 33;
    public static final int FUNC_EXT_ELEM_AVAILABLE = 34;
    public static final int FUNC_UNPARSED_ENTITY_URI = 36;
    public static final int FUNC_DOCLOCATION = 35;
    private static Class[] m_functions;
    private static HashMap m_functionID;
    private Class[] m_functions_customer;
    private HashMap m_functionID_customer;
    private static final int NUM_BUILT_IN_FUNCS = 37;
    private static final int NUM_ALLOWABLE_ADDINS = 30;
    private int m_funcNextFreeIndex;
    
    public FunctionTable() {
        this.m_functions_customer = new Class[30];
        this.m_functionID_customer = new HashMap();
        this.m_funcNextFreeIndex = 37;
    }
    
    String getFunctionName(final int funcID) {
        if (funcID < 37) {
            return FunctionTable.m_functions[funcID].getName();
        }
        return this.m_functions_customer[funcID - 37].getName();
    }
    
    Function getFunction(final int which) throws TransformerException {
        try {
            if (which < 37) {
                return FunctionTable.m_functions[which].newInstance();
            }
            return this.m_functions_customer[which - 37].newInstance();
        }
        catch (final IllegalAccessException ex) {
            throw new TransformerException(ex.getMessage());
        }
        catch (final InstantiationException ex2) {
            throw new TransformerException(ex2.getMessage());
        }
    }
    
    Object getFunctionID(final String key) {
        Object id = this.m_functionID_customer.get(key);
        if (null == id) {
            id = FunctionTable.m_functionID.get(key);
        }
        return id;
    }
    
    public int installFunction(final String name, final Class func) {
        final Object funcIndexObj = this.getFunctionID(name);
        int funcIndex;
        if (null != funcIndexObj) {
            funcIndex = (int)funcIndexObj;
            if (funcIndex < 37) {
                funcIndex = this.m_funcNextFreeIndex++;
                this.m_functionID_customer.put(name, new Integer(funcIndex));
            }
            this.m_functions_customer[funcIndex - 37] = func;
        }
        else {
            funcIndex = this.m_funcNextFreeIndex++;
            this.m_functions_customer[funcIndex - 37] = func;
            this.m_functionID_customer.put(name, new Integer(funcIndex));
        }
        return funcIndex;
    }
    
    public boolean functionAvailable(final String methName) {
        Object tblEntry = FunctionTable.m_functionID.get(methName);
        if (null != tblEntry) {
            return true;
        }
        tblEntry = this.m_functionID_customer.get(methName);
        return null != tblEntry;
    }
    
    static {
        FunctionTable.m_functionID = new HashMap();
        (FunctionTable.m_functions = new Class[37])[0] = FuncCurrent.class;
        FunctionTable.m_functions[1] = FuncLast.class;
        FunctionTable.m_functions[2] = FuncPosition.class;
        FunctionTable.m_functions[3] = FuncCount.class;
        FunctionTable.m_functions[4] = FuncId.class;
        FunctionTable.m_functions[7] = FuncLocalPart.class;
        FunctionTable.m_functions[8] = FuncNamespace.class;
        FunctionTable.m_functions[9] = FuncQname.class;
        FunctionTable.m_functions[10] = FuncGenerateId.class;
        FunctionTable.m_functions[11] = FuncNot.class;
        FunctionTable.m_functions[12] = FuncTrue.class;
        FunctionTable.m_functions[13] = FuncFalse.class;
        FunctionTable.m_functions[14] = FuncBoolean.class;
        FunctionTable.m_functions[32] = FuncLang.class;
        FunctionTable.m_functions[15] = FuncNumber.class;
        FunctionTable.m_functions[16] = FuncFloor.class;
        FunctionTable.m_functions[17] = FuncCeiling.class;
        FunctionTable.m_functions[18] = FuncRound.class;
        FunctionTable.m_functions[19] = FuncSum.class;
        FunctionTable.m_functions[20] = FuncString.class;
        FunctionTable.m_functions[21] = FuncStartsWith.class;
        FunctionTable.m_functions[22] = FuncContains.class;
        FunctionTable.m_functions[23] = FuncSubstringBefore.class;
        FunctionTable.m_functions[24] = FuncSubstringAfter.class;
        FunctionTable.m_functions[25] = FuncNormalizeSpace.class;
        FunctionTable.m_functions[26] = FuncTranslate.class;
        FunctionTable.m_functions[27] = FuncConcat.class;
        FunctionTable.m_functions[31] = FuncSystemProperty.class;
        FunctionTable.m_functions[33] = FuncExtFunctionAvailable.class;
        FunctionTable.m_functions[34] = FuncExtElementAvailable.class;
        FunctionTable.m_functions[29] = FuncSubstring.class;
        FunctionTable.m_functions[30] = FuncStringLength.class;
        FunctionTable.m_functions[35] = FuncDoclocation.class;
        FunctionTable.m_functions[36] = FuncUnparsedEntityURI.class;
        FunctionTable.m_functionID.put("current", new Integer(0));
        FunctionTable.m_functionID.put("last", new Integer(1));
        FunctionTable.m_functionID.put("position", new Integer(2));
        FunctionTable.m_functionID.put("count", new Integer(3));
        FunctionTable.m_functionID.put("id", new Integer(4));
        FunctionTable.m_functionID.put("key", new Integer(5));
        FunctionTable.m_functionID.put("local-name", new Integer(7));
        FunctionTable.m_functionID.put("namespace-uri", new Integer(8));
        FunctionTable.m_functionID.put("name", new Integer(9));
        FunctionTable.m_functionID.put("generate-id", new Integer(10));
        FunctionTable.m_functionID.put("not", new Integer(11));
        FunctionTable.m_functionID.put("true", new Integer(12));
        FunctionTable.m_functionID.put("false", new Integer(13));
        FunctionTable.m_functionID.put("boolean", new Integer(14));
        FunctionTable.m_functionID.put("lang", new Integer(32));
        FunctionTable.m_functionID.put("number", new Integer(15));
        FunctionTable.m_functionID.put("floor", new Integer(16));
        FunctionTable.m_functionID.put("ceiling", new Integer(17));
        FunctionTable.m_functionID.put("round", new Integer(18));
        FunctionTable.m_functionID.put("sum", new Integer(19));
        FunctionTable.m_functionID.put("string", new Integer(20));
        FunctionTable.m_functionID.put("starts-with", new Integer(21));
        FunctionTable.m_functionID.put("contains", new Integer(22));
        FunctionTable.m_functionID.put("substring-before", new Integer(23));
        FunctionTable.m_functionID.put("substring-after", new Integer(24));
        FunctionTable.m_functionID.put("normalize-space", new Integer(25));
        FunctionTable.m_functionID.put("translate", new Integer(26));
        FunctionTable.m_functionID.put("concat", new Integer(27));
        FunctionTable.m_functionID.put("system-property", new Integer(31));
        FunctionTable.m_functionID.put("function-available", new Integer(33));
        FunctionTable.m_functionID.put("element-available", new Integer(34));
        FunctionTable.m_functionID.put("substring", new Integer(29));
        FunctionTable.m_functionID.put("string-length", new Integer(30));
        FunctionTable.m_functionID.put("unparsed-entity-uri", new Integer(36));
        FunctionTable.m_functionID.put("document-location", new Integer(35));
    }
}

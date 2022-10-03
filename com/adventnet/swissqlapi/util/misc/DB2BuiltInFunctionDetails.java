package com.adventnet.swissqlapi.util.misc;

import java.util.ArrayList;
import java.util.HashMap;

public class DB2BuiltInFunctionDetails implements BuiltInFunctionDetails
{
    private HashMap returnTypeHash;
    private HashMap parameterTypeHash;
    
    public DB2BuiltInFunctionDetails() {
        this.returnTypeHash = new HashMap();
        this.parameterTypeHash = new HashMap();
        this.populateDetails();
    }
    
    @Override
    public String getReturnDataType(final String builtInFunctionName) {
        return this.returnTypeHash.get(builtInFunctionName.trim().toLowerCase());
    }
    
    @Override
    public String getParameterDataType(final String functionName, final int paramNum) {
        final ArrayList aList = this.parameterTypeHash.get(functionName.trim().toLowerCase());
        if (aList != null && paramNum < aList.size()) {
            return aList.get(paramNum);
        }
        return null;
    }
    
    private void populateDetails() {
        this.populateReturnType();
        this.populateParameterTypes();
    }
    
    private void populateReturnType() {
        this.returnTypeHash.put("oracle_substr", "varchar");
        this.returnTypeHash.put("oracle_to_char", "varchar");
        this.returnTypeHash.put("double", "double");
        this.returnTypeHash.put("oracle_add_months", "timestamp");
        this.returnTypeHash.put("oracle_lastday", "timestamp");
        this.returnTypeHash.put("oracle_to_date", "timestamp");
        this.returnTypeHash.put("ltrim", "varchar");
        this.returnTypeHash.put("rtrim", "varchar");
        this.returnTypeHash.put("oracle_new_time", "timestamp");
    }
    
    private void populateParameterTypes() {
        final String[] param1 = { "varchar", "integer", "integer" };
        this.parameterTypeHash.put("oracle_substr", this.addAndReturnList(param1));
        final String[] param2 = { "timestamp", "varchar" };
        this.parameterTypeHash.put("oracle_to_char", this.addAndReturnList(param2));
        final String[] param3 = { "timestamp", "integer" };
        this.parameterTypeHash.put("oracle_add_months", this.addAndReturnList(param3));
        final String[] param4 = { "timestamp" };
        this.parameterTypeHash.put("oracle_lastday", this.addAndReturnList(param4));
        final String[] param5 = { "varchar", "varchar" };
        this.parameterTypeHash.put("oracle_to_date", this.addAndReturnList(param5));
        final String[] param6 = { "varchar" };
        this.parameterTypeHash.put("ltrim", this.addAndReturnList(param6));
        final String[] param7 = { "varchar" };
        this.parameterTypeHash.put("rtrim", this.addAndReturnList(param7));
        final String[] param8 = { "double" };
        this.parameterTypeHash.put("abs", this.addAndReturnList(param8));
        final String[] param9 = { "timestamp", "varchar", "varchar" };
        this.parameterTypeHash.put("oracle_new_time", this.addAndReturnList(param9));
    }
    
    private ArrayList addAndReturnList(final String[] arr) {
        if (arr == null) {
            return null;
        }
        final ArrayList aList = new ArrayList();
        for (int i = 0; i < arr.length; ++i) {
            aList.add(arr[i]);
        }
        return aList;
    }
}

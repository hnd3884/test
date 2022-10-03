package com.adventnet.webclient.components.table;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class TableTEI extends TagExtraInfo
{
    protected String[] attrlist;
    protected String[] types;
    
    public TableTEI() {
        this.attrlist = new String[] { "RECORDS_COUNT", "START_INDEX", "END_INDEX", "COLUMN_COUNT", "ROW_COUNT", "USER_VAR", "ORIGINAL_VAR" };
        this.types = new String[] { "Long", "Long", "Long", "Integer", "Integer", "String", "String" };
    }
    
    public VariableInfo[] getVariableInfo(final TagData data) {
        final VariableInfo[] varList = new VariableInfo[this.attrlist.length];
        for (int i = 0; i < this.attrlist.length; ++i) {
            varList[i] = new VariableInfo(this.attrlist[i], this.types[i], true, 0);
        }
        return varList;
    }
}

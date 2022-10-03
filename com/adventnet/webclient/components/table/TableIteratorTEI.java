package com.adventnet.webclient.components.table;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class TableIteratorTEI extends TagExtraInfo
{
    protected String[] attrlist;
    
    public TableIteratorTEI() {
        this.attrlist = new String[] { "COLUMN_INDEX" };
    }
    
    public VariableInfo[] getVariableInfo(final TagData data) {
        final VariableInfo[] varList = new VariableInfo[this.attrlist.length];
        for (int i = 0; i < this.attrlist.length; ++i) {
            varList[i] = new VariableInfo(this.attrlist[i], "Integer", true, 0);
        }
        return varList;
    }
}

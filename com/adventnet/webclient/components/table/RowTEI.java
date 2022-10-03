package com.adventnet.webclient.components.table;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class RowTEI extends TagExtraInfo
{
    protected String[] attrlist;
    
    public RowTEI() {
        this.attrlist = new String[] { "ROW_INDEX" };
    }
    
    public VariableInfo[] getVariableInfo(final TagData data) {
        final VariableInfo[] varList = new VariableInfo[this.attrlist.length];
        for (int i = 0; i < this.attrlist.length; ++i) {
            varList[i] = new VariableInfo(this.attrlist[i], "Integer", true, 0);
        }
        return varList;
    }
}

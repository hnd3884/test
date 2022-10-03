package com.adventnet.webclient.components.table;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class HeaderTEI extends TagExtraInfo
{
    protected String[] attrlist;
    
    public HeaderTEI() {
        this.attrlist = new String[] { "HEADER" };
    }
    
    public VariableInfo[] getVariableInfo(final TagData data) {
        final VariableInfo[] varList = new VariableInfo[this.attrlist.length];
        for (int i = 0; i < this.attrlist.length; ++i) {
            varList[i] = new VariableInfo(this.attrlist[i], "Object", true, 0);
        }
        return varList;
    }
}

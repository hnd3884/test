package com.adventnet.webclient.components.increments;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;

public class PageNavigationTEI extends TagExtraInfo
{
    public VariableInfo[] getVariableInfo(final TagData data) {
        final VariableInfo[] info = { new VariableInfo("TOTAL_PAGES", "String", true, 0), new VariableInfo("PAGE_NUMBER", "String", true, 0), new VariableInfo("TOTAL_RECORDS", "String", true, 0), new VariableInfo("IS_FIRST", "Boolean", true, 0), new VariableInfo("IS_LAST", "Boolean", true, 0), new VariableInfo("FROM_INDEX", "String", true, 0), new VariableInfo("TO_INDEX", "String", true, 0), new VariableInfo("RECORDS_PER_PAGE", "String", true, 0), new VariableInfo("FORWARD_TO", "String", true, 0) };
        return info;
    }
}

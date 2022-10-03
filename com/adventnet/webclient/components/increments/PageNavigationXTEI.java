package com.adventnet.webclient.components.increments;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

public class PageNavigationXTEI extends PageNavigationTEI
{
    public VariableInfo[] getVariableInfo(final TagData data) {
        final VariableInfo[] info = { new VariableInfo("FROM_LINK", "String", true, 0), new VariableInfo("TO_LINK", "String", true, 0), new VariableInfo("FIRST_LINK", "String", true, 0), new VariableInfo("PREVIOUS_LINK", "Boolean", true, 0), new VariableInfo("NEXT_LINK", "Boolean", true, 0), new VariableInfo("LAST_LINK", "String", true, 0), new VariableInfo("LINKS", "java.util.Vector", true, 0), new VariableInfo("FROM_INDEX", "String", true, 0), new VariableInfo("TO_INDEX", "String", true, 0), new VariableInfo("PAGE_NUMBER", "Long", true, 0), new VariableInfo("TOTAL_PAGES", "String", true, 0), new VariableInfo("RECORDS_PER_PAGE", "Long", true, 0) };
        return info;
    }
}

package com.adventnet.webclient.components.increments;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

public class RecordsNavigationTag extends TagSupport
{
    private long totalRecords;
    private String optionsVar;
    private long fromIndex;
    private long toIndex;
    private String forwardTo;
    protected String uniqueId;
    
    public RecordsNavigationTag() {
        this.fromIndex = 1L;
        this.forwardTo = null;
        this.uniqueId = null;
    }
    
    public void setTotalRecords(final Long totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public void setOptionsList(final String optionsVar) {
        this.optionsVar = optionsVar;
    }
    
    public void setFromIndex(final Long fromIndex) {
        this.fromIndex = fromIndex;
    }
    
    public void setToIndex(final Long toIndex) {
        this.toIndex = toIndex;
    }
    
    public void setForwardTo(final String forwardTo) {
        this.forwardTo = forwardTo;
    }
    
    public int doStartTag() {
        final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        final HttpSession session = this.pageContext.getSession();
        if (this.forwardTo == null) {
            this.forwardTo = request.getServletPath();
        }
        List optionsList = null;
        if (this.optionsVar != null) {
            optionsList = (List)this.pageContext.findAttribute(this.optionsVar);
        }
        else {
            final ArrayList vect = new ArrayList();
            for (long i = 25L; i <= 125L; i += 25L) {
                vect.add(i + "");
            }
            optionsList = vect;
        }
        String currentOption = (String)request.getAttribute("PREVIOUS_SELECTED_RANGE");
        if (currentOption == null) {
            currentOption = this.toIndex - this.fromIndex + 1L + "";
        }
        if (!optionsList.contains(currentOption + "")) {
            optionsList.add(currentOption + "");
        }
        boolean isFirst = false;
        boolean isLast = false;
        if (this.fromIndex == 1L) {
            isFirst = true;
        }
        if (this.toIndex == this.totalRecords) {
            isLast = true;
        }
        request.setAttribute("TOTAL_RECORDS", (Object)(this.totalRecords + ""));
        request.setAttribute("FROM_INDEX", (Object)(this.fromIndex + ""));
        request.setAttribute("TO_INDEX", (Object)(this.toIndex + ""));
        request.setAttribute("IS_FIRST", (Object)new Boolean(isFirst));
        request.setAttribute("IS_LAST", (Object)new Boolean(isLast));
        request.setAttribute("OPTIONS_LIST", (Object)optionsList);
        request.setAttribute("RANGE_VALUE", (Object)currentOption);
        if (this.getUniqueId() != null) {
            request.setAttribute("FORWARD_TO", (Object)(this.forwardTo + "?uniqueId=" + this.getUniqueId()));
        }
        else {
            request.setAttribute("FORWARD_TO", (Object)this.forwardTo);
        }
        return 1;
    }
    
    public int doEndTag() {
        return 6;
    }
    
    public String getKey() {
        if (this.uniqueId != null) {
            return this.uniqueId;
        }
        return "";
    }
    
    public String getUniqueId() {
        return this.uniqueId;
    }
    
    public void setUniqueId(final String uniqueIdArg) {
        this.uniqueId = uniqueIdArg;
    }
}

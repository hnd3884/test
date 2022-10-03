package com.adventnet.webclient.components.increments;

import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.jsp.tagext.TagSupport;

public class PageNavigationTag extends TagSupport
{
    protected long totalRecords;
    protected int recordsPerPage;
    protected int totalPages;
    private long fromIndex;
    private long toIndex;
    protected Logger logger;
    protected String forwardTo;
    protected String pageNumberStr;
    protected int pageNumber;
    protected String uniqueId;
    
    public PageNavigationTag() {
        this.totalRecords = 0L;
        this.recordsPerPage = 0;
        this.fromIndex = 0L;
        this.toIndex = 0L;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.forwardTo = null;
        this.pageNumberStr = null;
        this.pageNumber = 1;
        this.uniqueId = null;
    }
    
    public void setTotalRecords(final Long totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public void setRecordsPerPage(final Integer recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }
    
    public void setForwardTo(final String forwardTo) {
        this.forwardTo = forwardTo;
    }
    
    public void setPageNumber(final String pageNumberStr) {
        this.pageNumberStr = pageNumberStr;
    }
    
    public int doStartTag() {
        boolean isFirst = false;
        boolean isLast = false;
        final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        this.logger.log(Level.FINEST, "Total Records => " + this.totalRecords);
        this.pageNumber = 1;
        if (this.pageNumberStr != null) {
            this.pageNumber = Integer.parseInt(this.pageNumberStr);
        }
        if (this.forwardTo == null) {
            this.forwardTo = request.getServletPath();
        }
        if (this.totalRecords == 0L) {
            this.pageNumber = 0;
            this.fromIndex = 0L;
            this.toIndex = 0L;
            isFirst = true;
            this.totalPages = 0;
        }
        else {
            this.totalPages = (int)this.totalRecords / this.recordsPerPage;
            if (this.totalRecords % this.recordsPerPage != 0L) {
                ++this.totalPages;
            }
            if (this.pageNumber >= this.totalPages) {
                this.pageNumber = this.totalPages;
                isLast = true;
            }
            if (this.pageNumber <= 1) {
                this.pageNumber = 1;
                isFirst = true;
            }
            if (this.pageNumber >= 1) {
                this.fromIndex = (this.pageNumber - 1) * this.recordsPerPage + 1;
                this.toIndex = this.pageNumber * this.recordsPerPage;
                if (this.toIndex > this.totalRecords) {
                    this.toIndex = this.totalRecords;
                }
            }
        }
        request.setAttribute("TOTAL_PAGES", (Object)(this.totalPages + ""));
        request.setAttribute("PAGE_NUMBER", (Object)(this.pageNumber + ""));
        request.setAttribute("IS_FIRST", (Object)new Boolean(isFirst));
        request.setAttribute("IS_LAST", (Object)new Boolean(isLast));
        request.setAttribute("FROM_INDEX", (Object)(this.fromIndex + ""));
        request.setAttribute("TO_INDEX", (Object)(this.toIndex + ""));
        request.setAttribute("RECORDS_PER_PAGE", (Object)(this.recordsPerPage + ""));
        request.setAttribute("TOTAL_RECORDS", (Object)(this.totalRecords + ""));
        if (this.getUniqueId() != null) {
            final String forwardStr = this.forwardTo + ((this.forwardTo.indexOf(63) > 0) ? "&" : "?") + "uniqueId=" + this.getUniqueId();
            request.setAttribute("FORWARD_TO", (Object)forwardStr);
        }
        else {
            request.setAttribute("FORWARD_TO", (Object)this.forwardTo);
        }
        return 1;
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

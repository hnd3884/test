package com.adventnet.client.components.rangenavigator.web;

import java.util.List;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class PageNavigationTag extends BodyTagSupport
{
    private int pageNumber;
    private int linksPerPage;
    private long totalRecords;
    private int recordsPerPage;
    private List rangeList;
    private String forwardTo;
    
    public PageNavigationTag() {
        this.pageNumber = 0;
        this.linksPerPage = 0;
        this.totalRecords = 0L;
        this.recordsPerPage = 0;
        this.rangeList = null;
        this.forwardTo = null;
    }
    
    public void setTotalRecords(final long totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public void setRecordsPerPage(final int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }
    
    public void setForwardTo(final String forwardTo) {
        this.forwardTo = forwardTo;
    }
    
    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public void setLinksPerPage(final int linksPerPage) {
        this.linksPerPage = linksPerPage;
    }
    
    public void setRangeList(final List rangeList) {
        this.rangeList = rangeList;
    }
    
    public int doStartTag() {
        int totalPages = (int)this.totalRecords / this.recordsPerPage;
        long fromIndex = 0L;
        long toIndex = 0L;
        if (this.totalRecords % this.recordsPerPage != 0L) {
            ++totalPages;
        }
        if (this.pageNumber >= totalPages) {
            this.pageNumber = totalPages;
        }
        if (this.pageNumber <= 1) {
            this.pageNumber = 1;
        }
        if (this.pageNumber >= 1) {
            fromIndex = (this.pageNumber - 1) * this.recordsPerPage + 1;
            toIndex = this.pageNumber * this.recordsPerPage;
            if (toIndex > this.totalRecords) {
                toIndex = this.totalRecords;
            }
        }
        if (this.linksPerPage == 0) {
            this.linksPerPage = 10;
        }
        long fromLink = 0L;
        long toLink = 0L;
        if (this.linksPerPage > totalPages) {
            this.linksPerPage = totalPages;
        }
        if (this.pageNumber <= this.linksPerPage / 2) {
            fromLink = 1L;
            toLink = this.linksPerPage;
        }
        else if (this.pageNumber > this.linksPerPage / 2 && totalPages - this.pageNumber > this.linksPerPage / 2) {
            fromLink = this.pageNumber - (this.linksPerPage / 2 - 1);
            toLink = this.pageNumber + this.linksPerPage / 2;
        }
        else if (this.pageNumber > this.linksPerPage / 2 || totalPages - this.pageNumber <= this.linksPerPage / 2) {
            fromLink = totalPages - this.linksPerPage + 1;
            toLink = totalPages;
        }
        return 1;
    }
}

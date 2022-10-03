package com.adventnet.webclient.components.increments;

import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

public class PageNavigationXTag extends PageNavigationTag
{
    private int linksPerPage;
    
    public PageNavigationXTag() {
        this.linksPerPage = 0;
    }
    
    public void setLinksPerPage(final String linksPerPage) {
        this.linksPerPage = Integer.parseInt(linksPerPage);
    }
    
    public int doStartTag() {
        super.doStartTag();
        final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        if (this.linksPerPage == 0) {
            this.linksPerPage = 10;
        }
        long fromLink = 0L;
        long toLink = 0L;
        if (this.linksPerPage > this.totalPages) {
            this.linksPerPage = this.totalPages;
        }
        if (this.pageNumber <= this.linksPerPage / 2) {
            fromLink = 1L;
            toLink = this.linksPerPage;
        }
        else if (this.pageNumber > this.linksPerPage / 2 && this.totalPages - this.pageNumber > this.linksPerPage / 2) {
            fromLink = this.pageNumber - (this.linksPerPage / 2 - 1);
            toLink = this.pageNumber + this.linksPerPage / 2;
        }
        else if (this.pageNumber > this.linksPerPage / 2 || this.totalPages - this.pageNumber <= this.linksPerPage / 2) {
            fromLink = this.totalPages - this.linksPerPage + 1;
            toLink = this.totalPages;
        }
        if (this.forwardTo.indexOf("?") >= 1) {
            this.forwardTo = this.forwardTo.concat("&");
        }
        else {
            this.forwardTo = this.forwardTo.concat("?");
        }
        final String firstLink = this.createLink(1, 1, this.recordsPerPage);
        final String previousLink = this.createLink(this.pageNumber - 1, (this.pageNumber - 2) * this.recordsPerPage + 1, this.recordsPerPage);
        final String nextLink = this.createLink(this.pageNumber + 1, this.pageNumber * this.recordsPerPage + 1, this.recordsPerPage);
        final String lastLink = this.createLink(this.totalPages, (this.totalPages - 1) * this.recordsPerPage + 1, this.recordsPerPage);
        final Vector links = new Vector(this.linksPerPage);
        int iterator = (int)fromLink;
        for (int i = 0; i < this.linksPerPage; ++i) {
            final String link = this.createLink(iterator, (iterator - 1) * this.recordsPerPage + 1, this.recordsPerPage);
            links.add(i, link);
            ++iterator;
        }
        request.setAttribute("LINKS_PER_PAGE", (Object)(this.linksPerPage + ""));
        request.setAttribute("FROM_LINK", (Object)(fromLink + ""));
        request.setAttribute("TO_LINK", (Object)(toLink + ""));
        request.setAttribute("FIRST_LINK", (Object)firstLink);
        request.setAttribute("PREVIOUS_LINK", (Object)previousLink);
        request.setAttribute("NEXT_LINK", (Object)nextLink);
        request.setAttribute("LAST_LINK", (Object)lastLink);
        request.setAttribute("LINKS", (Object)links);
        return 1;
    }
    
    private String createLink(final int pageNumber, final int fromIndex, final int records) {
        final StringBuffer link = new StringBuffer(this.forwardTo);
        link.append("PAGE_NUMBER=");
        link.append(pageNumber);
        link.append("&FROM_INDEX=");
        link.append(fromIndex);
        link.append("&TO_INDEX=");
        if (pageNumber * records > this.totalRecords) {
            link.append(this.totalRecords);
        }
        else {
            link.append(pageNumber * records);
        }
        link.append("&RANGE=");
        link.append(this.recordsPerPage);
        return link.toString();
    }
}

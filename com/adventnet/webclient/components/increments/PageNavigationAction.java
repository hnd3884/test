package com.adventnet.webclient.components.increments;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class PageNavigationAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        final Logger logger = Logger.getLogger(this.getClass().getName());
        logger.log(Level.FINEST, "Execute method in PageAction invoked");
        final String totalPages = request.getParameter("totalPages");
        final String pageNumberStr = request.getParameter("pageNumber");
        final String recordsPerPage = request.getParameter("recordsPerPage");
        final String totalRecordsStr = request.getParameter("totalRecords");
        final int total = Integer.parseInt(totalPages);
        int pageNumber = Integer.parseInt(pageNumberStr);
        final int records = Integer.parseInt(recordsPerPage);
        final long totalRecords = Long.parseLong(totalRecordsStr);
        if (request.getParameter("next.x") != null) {
            if (pageNumber < total) {
                ++pageNumber;
            }
        }
        else if (request.getParameter("previous.x") != null) {
            if (pageNumber > 1) {
                --pageNumber;
            }
        }
        else if (request.getParameter("first.x") != null) {
            pageNumber = 1;
        }
        else if (request.getParameter("last.x") != null) {
            pageNumber = total;
        }
        long fromIndex = 0L;
        long toIndex = 0L;
        if (pageNumber >= 1) {
            fromIndex = (pageNumber - 1) * records + 1;
            toIndex = pageNumber * records;
            if (pageNumber >= total) {
                toIndex = totalRecords;
            }
        }
        request.setAttribute("PAGE_NUMBER", (Object)(pageNumber + ""));
        request.setAttribute("FROM_INDEX", (Object)(fromIndex + ""));
        request.setAttribute("TO_INDEX", (Object)(toIndex + ""));
        final String currentFileName = request.getParameter("fileName");
        final ActionForward actionForward = new ActionForward(currentFileName);
        return actionForward;
    }
}

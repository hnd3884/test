package com.adventnet.webclient.components.increments;

import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class RecordsNavigationAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        long fromIndex = 1L;
        long toIndex = 1L;
        long totalRecords = 1L;
        long currentOption = 25L;
        boolean imageClicked = false;
        final String currentFileName = request.getParameter("fileName");
        final String onSelect = request.getParameter("OnSelect");
        final String fromIndexStr = request.getParameter("fromIndex");
        if (fromIndexStr != null) {
            fromIndex = Long.parseLong(fromIndexStr);
        }
        final String toIndexStr = request.getParameter("toIndex");
        if (toIndexStr != null) {
            toIndex = Long.parseLong(toIndexStr);
        }
        final String totalRecordsStr = request.getParameter("totalRecords");
        if (totalRecordsStr != null) {
            totalRecords = Long.parseLong(totalRecordsStr);
        }
        final String currentOptionStr = request.getParameter("currentOption");
        if (currentOptionStr != null) {
            currentOption = Long.parseLong(currentOptionStr);
            if (request.getParameter("next.x") != null) {
                fromIndex = toIndex + 1L;
                toIndex = fromIndex + currentOption - 1L;
                imageClicked = true;
            }
            else if (request.getParameter("previous.x") != null) {
                toIndex = fromIndex - 1L;
                fromIndex = toIndex - currentOption + 1L;
                imageClicked = true;
            }
            else if (request.getParameter("first.x") != null) {
                fromIndex = 1L;
                toIndex = fromIndex + currentOption - 1L;
                imageClicked = true;
            }
            else if (request.getParameter("last.x") != null) {
                toIndex = totalRecords;
                fromIndex = toIndex - currentOption + 1L;
                imageClicked = true;
            }
            else if (onSelect.equals("true")) {
                toIndex = fromIndex + currentOption - 1L;
            }
        }
        if (fromIndex < 1L) {
            fromIndex = 1L;
        }
        if (toIndex > totalRecords) {
            toIndex = totalRecords;
        }
        if ((fromIndex == 1L || toIndex == totalRecords) && toIndex - fromIndex + 1L < currentOption && imageClicked) {
            request.setAttribute("PREVIOUS_SELECTED_RANGE", (Object)(currentOption + ""));
        }
        request.setAttribute("FROM_INDEX", (Object)(fromIndex + ""));
        request.setAttribute("TO_INDEX", (Object)(toIndex + ""));
        final ActionForward actionForward = new ActionForward(currentFileName);
        return actionForward;
    }
}

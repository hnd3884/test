package com.adventnet.client.util.web;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletRequest;

public class LazyLoadUtil
{
    public static final String QUEUEDACTION = "QUEUEDACTION";
    
    public static void queueAction(final ServletRequest request, final String action) {
        final List queuedAction = (List)request.getAttribute("QUEUEDACTION");
        if (queuedAction == null) {
            request.setAttribute("QUEUEDACTION", (Object)new LinkedList());
        }
        final LinkedList queue = (LinkedList)request.getAttribute("QUEUEDACTION");
        queue.addLast(action);
    }
    
    public static List getQueuedActions(final ServletRequest request) {
        return (List)request.getAttribute("QUEUEDACTION");
    }
    
    public static void executeQueuedAction(final PageContext pageContext) throws IOException, ServletException {
        pageContext.getOut().flush();
        final ServletRequest request = pageContext.getRequest();
        final ServletResponse response = pageContext.getResponse();
        final List queuedAction = getQueuedActions(request);
        if (queuedAction != null) {
            for (final String action : queuedAction) {
                pageContext.getOut().flush();
                request.getRequestDispatcher(action).include(request, response);
            }
        }
        request.removeAttribute("QUEUEDACTION");
    }
}

package com.adventnet.webclient.struts.action;

import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public abstract class SynchroAction extends Action
{
    private static final String PACKAGE = "com.adventnet.webclient.struts.action";
    protected static final String COMPLETE_KEY = "com.adventnet.webclient.struts.action.SynchroAction.COMPLETE";
    protected static final String FORM_KEY = "com.adventnet.webclient.struts.action.SynchroAction.FORM";
    protected static final String EXCEPTION_KEY = "com.adventnet.webclient.struts.action.SynchroAction.EXCEPTION";
    protected static final String FORWARD_KEY = "com.adventnet.webclient.struts.action.SynchroAction.FORWARD";
    private static final String ERRORS_KEY = "com.adventnet.webclient.struts.action.SynchroAction.ERRORS";
    private static final String MAPPING_KEY = "com.adventnet.webclient.struts.action.SynchroAction.MAPPING";
    
    public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final HttpSession session = request.getSession();
        ActionForward forward = null;
        if (this.isTokenValid(request)) {
            this.resetToken(request);
            session.removeAttribute("com.adventnet.webclient.struts.action.SynchroAction.COMPLETE");
            session.removeAttribute("com.adventnet.webclient.struts.action.SynchroAction.FORM");
            session.removeAttribute("com.adventnet.webclient.struts.action.SynchroAction.EXCEPTION");
            session.removeAttribute("com.adventnet.webclient.struts.action.SynchroAction.FORWARD");
            session.removeAttribute("com.adventnet.webclient.struts.action.SynchroAction.ERRORS");
            session.setAttribute("com.adventnet.webclient.struts.action.SynchroAction.MAPPING", (Object)mapping.getPath());
            try {
                forward = this.performSynchro(mapping, form, request, response);
                session.setAttribute("com.adventnet.webclient.struts.action.SynchroAction.FORM", (Object)form);
                session.setAttribute("com.adventnet.webclient.struts.action.SynchroAction.FORWARD", (Object)forward);
                final ActionErrors errors = (ActionErrors)request.getAttribute("org.apache.struts.action.ERROR");
                if (errors != null && !errors.isEmpty()) {
                    this.saveToken(request);
                }
                session.setAttribute("com.adventnet.webclient.struts.action.SynchroAction.ERRORS", (Object)errors);
            }
            catch (final IOException e) {
                session.setAttribute("com.adventnet.webclient.struts.action.SynchroAction.EXCEPTION", (Object)e);
                throw e;
            }
            catch (final ServletException e2) {
                session.setAttribute("com.adventnet.webclient.struts.action.SynchroAction.EXCEPTION", (Object)e2);
                throw e2;
            }
            finally {
                session.setAttribute("com.adventnet.webclient.struts.action.SynchroAction.COMPLETE", (Object)"true");
            }
        }
        final boolean isComplete = "true".equals(session.getAttribute("com.adventnet.webclient.struts.action.SynchroAction.COMPLETE"));
        final String m = (String)session.getAttribute("com.adventnet.webclient.struts.action.SynchroAction.MAPPING");
        if (isComplete && mapping.getPath().equals(m)) {
            final Exception e3 = (Exception)session.getAttribute("com.adventnet.webclient.struts.action.SynchroAction.EXCEPTION");
            if (e3 != null) {
                if (e3 instanceof IOException) {
                    throw (IOException)e3;
                }
                if (e3 instanceof ServletException) {
                    throw (ServletException)e3;
                }
            }
            final ActionForm f = (ActionForm)session.getAttribute("com.adventnet.webclient.struts.action.SynchroAction.FORM");
            if ("request".equals(mapping.getScope())) {
                request.setAttribute(mapping.getAttribute(), (Object)f);
            }
            else {
                session.setAttribute(mapping.getAttribute(), (Object)f);
            }
            this.saveErrors(request, (ActionErrors)session.getAttribute("com.adventnet.webclient.struts.action.SynchroAction.ERRORS"));
            forward = (ActionForward)session.getAttribute("com.adventnet.webclient.struts.action.SynchroAction.FORWARD");
        }
        else {
            forward = this.performInvalidToken(mapping, form, request, response);
        }
        return forward;
    }
    
    protected abstract ActionForward performSynchro(final ActionMapping p0, final ActionForm p1, final HttpServletRequest p2, final HttpServletResponse p3) throws IOException, ServletException;
    
    protected ActionForward performInvalidToken(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        return mapping.findForward("synchroError");
    }
}

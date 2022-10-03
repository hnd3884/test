package com.adventnet.client.view.pdf;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.adventnet.client.ClientErrorCodes;
import com.adventnet.client.ClientException;
import javax.servlet.ServletException;
import java.io.OutputStream;
import com.adventnet.client.util.pdf.PDFUtil;
import com.adventnet.client.view.web.ViewContext;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class ViewPDFProcessorServlet extends HttpServlet implements WebConstants, JavaScriptConstants
{
    private static Logger LOGGER;
    
    public void init() {
        final String ctxName = this.getServletContext().getInitParameter("ContextPath");
        if (ctxName == null) {
            throw new RuntimeException("Please set the 'ContextPath' parameter in web.xml");
        }
        WebClientUtil.setServletContext(ctxName, this.getServletContext());
    }
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        try {
            final String origView = WebClientUtil.getRequestedPathName(request);
            String fileName = request.getParameter("fileName");
            fileName = (String)StringUtils.defaultIfBlank((CharSequence)fileName, (CharSequence)origView);
            final ViewContext vc = ViewContext.getViewContext(origView, request);
            if (response.getContentType() == null) {
                response.setContentType("application/pdf");
            }
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "max-age=0");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".pdf\"");
            vc.getModel().getController().processPreRendering(vc, request, response, null);
            PDFUtil.generatePDF(origView, this.getServletContext(), request, (OutputStream)response.getOutputStream());
            vc.getModel().getController().processPostRendering(vc, request, response);
        }
        catch (final Exception ex) {
            response.setContentType("text/html");
            this.handleException(ex, request, response);
        }
    }
    
    public void handleException(final Exception ex, final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        try {
            if (ex instanceof ClientException && ((ClientException)ex).getErrorCode().equals(ClientErrorCodes.RECURSIVE_LAYOUT.getCode())) {
                final RequestDispatcher rd = request.getRequestDispatcher("/components/jsp/error/RecursiveLayout.jsp");
                rd.include((ServletRequest)request, (ServletResponse)response);
                return;
            }
        }
        catch (final Exception newEx) {
            newEx.printStackTrace();
        }
        throw new ServletException((Throwable)ex);
    }
    
    static {
        ViewPDFProcessorServlet.LOGGER = Logger.getLogger(ViewPDFProcessorServlet.class.getName());
    }
}

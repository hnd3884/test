package com.me.mdm.files.serve;

import javax.servlet.AsyncContext;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.adventnet.iam.security.SecurityUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(asyncSupported = true)
public class FileServlet extends HttpServlet
{
    public void doGet(final HttpServletRequest req, final HttpServletResponse response) {
        final Long requestedAt = System.currentTimeMillis();
        AsyncContext asyncContext = null;
        try {
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final HttpServletRequest request = SecurityUtil.getCurrentRequest();
            request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", (Object)true);
            asyncContext = request.startAsync((ServletRequest)request, (ServletResponse)response);
            FileDispatchController.getInstance().dispatch(new AsyncContextAuthorizer(loginId, asyncContext, requestedAt));
        }
        catch (final Exception ex) {
            SyMLogger.log("FileServletLog", Level.SEVERE, "FileServlet,doGet " + ex.getMessage());
            if (asyncContext != null) {
                try {
                    response.setStatus(400);
                    asyncContext.complete();
                }
                catch (final Exception ex2) {
                    SyMLogger.log("FileServletLog", Level.SEVERE, (String)null, (Throwable)ex2);
                }
            }
        }
    }
}

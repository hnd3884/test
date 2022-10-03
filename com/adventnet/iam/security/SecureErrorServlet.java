package com.adventnet.iam.security;

import java.io.IOException;
import javax.servlet.ServletException;
import org.json.JSONObject;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public abstract class SecureErrorServlet extends HttpServlet
{
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final SecurityRequestWrapper safeReq = (SecurityRequestWrapper)request.getAttribute(SecurityRequestWrapper.class.getName());
        final IAMSecurityException ex = (IAMSecurityException)request.getAttribute(IAMSecurityException.class.getName());
        if (ex != null) {
            if (ex.getErrorCode() == "HIP_REQUIRED") {
                if (SecurityFilterProperties.getInstance(request).handleErrorPageHip()) {
                    response.getWriter().println("<p> DIGEST  : " + ex.getHipDigest());
                    final String dosUri = request.getAttribute("javax.servlet.error.request_uri").toString();
                    response.getWriter().println("<p> URL : " + dosUri);
                    request.getRequestDispatcher("/handledoscaptcha.jsp?dosdigest=" + ex.getHipDigest()).include((ServletRequest)request, (ServletResponse)response);
                }
            }
            else if (SecurityFilterProperties.getInstance(request).handleErrorPageJsonResponse()) {
                response.addHeader("Content-Type", "application/json;charset=UTF-8");
                response.getWriter().println(new JSONObject().put("errorCode", (Object)ex.getErrorCode()).put("status", request.getAttribute("javax.servlet.error.status_code")).toString());
            }
        }
        if (safeReq != null) {
            super.service((HttpServletRequest)safeReq, response);
        }
        else {
            super.service(request, response);
        }
    }
    
    public static AccessInfo getAccessInfo(final String throttleKey) {
        return null;
    }
}

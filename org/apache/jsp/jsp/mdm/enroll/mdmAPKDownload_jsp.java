package org.apache.jsp.jsp.mdm.enroll;

import java.util.HashSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import com.adventnet.i18n.I18N;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Servlet;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.jasper.runtime.InstanceManagerFactory;
import org.apache.tomcat.InstanceManager;
import javax.el.ExpressionFactory;
import java.util.Set;
import java.util.Map;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class mdmAPKDownload_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmAPKDownload_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmAPKDownload_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmAPKDownload_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmAPKDownload_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
                }
            }
        }
        return this._el_expressionfactory;
    }
    
    public InstanceManager _jsp_getInstanceManager() {
        if (this._jsp_instancemanager == null) {
            synchronized (this) {
                if (this._jsp_instancemanager == null) {
                    this._jsp_instancemanager = InstanceManagerFactory.getInstanceManager(this.getServletConfig());
                }
            }
        }
        return this._jsp_instancemanager;
    }
    
    public void _jspInit() {
    }
    
    public void _jspDestroy() {
    }
    
    public void _jspService(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final String _jspx_method = request.getMethod();
        if (!"GET".equals(_jspx_method) && !"POST".equals(_jspx_method) && !"HEAD".equals(_jspx_method) && !DispatcherType.ERROR.equals((Object)request.getDispatcherType())) {
            response.sendError(405, "JSPs only permit GET, POST or HEAD. Jasper also permits OPTIONS");
            return;
        }
        HttpSession session = null;
        JspWriter out = null;
        final Object page = this;
        JspWriter _jspx_out = null;
        PageContext _jspx_page_context = null;
        try {
            response.setContentType("text/html");
            final PageContext pageContext = _jspx_page_context = mdmAPKDownload_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    \n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css\" />\n    </head>\n    <script  language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/AjaxAPI.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n    <script>\n\n        function downloadCertificate(){\n\n            callGetDownloadStatusRecursively();\n            location.href=\"/installCertificate.mob\";\n        }\n\n        // We are loosing JS Fn calling control, once we moved to other page. As a workaround\n        // same function will be called for every 1 second for 20 seconds using setTimeout method.\n        function callGetDownloadStatusRecursively(){\n\n            var callTimeInSeconds = 1;\n            while(callTimeInSeconds <= 20){\n                setTimeout(\"getMobileConfigDownloadStatus()\", 1000 * callTimeInSeconds);\n                callTimeInSeconds ++;\n            }\n            setTimeout(\"downloadAgent()\", 1000 * callTimeInSeconds);\n        }\n\n\n        function getMobileConfigDownloadStatus(){\n\n            var url ='/getCertificateDownloadStatus.mob';  // No I18N\n            var req = AjaxAPI.getXMLHttpRequest();\n            req.open(\"POST\", url, true);\n            req.setRequestHeader(\"Connection\", \"close\");\n            req.onreadystatechange = function(){\n");
            out.write("                checkDownloadStatusAndContinue(req);\n            };\n            req.send(null);\n        }\n\n        function checkDownloadStatusAndContinue(req){\n            mobileConfigDownloadStatusCalled = true;\n            var downloadStatus = req.responseText;\n            if(downloadStatus != null && downloadStatus.trim() == \"true\"){\n                mobileConfigDownloadStatusCalled = false;\n                downloadAgent();\n            }\n        }\n        function downloadAgent(){\n            getAgentStatusRecursively();\n            location.href=\"/DownloadAPK.mob\";\n        }\n\n        function getAgentStatusRecursively(){\n            var callTimeInSeconds = 1;\n            while(callTimeInSeconds <= 20){\n                setTimeout(\"getAgentDownloadStatus()\", 100 * callTimeInSeconds);\n                callTimeInSeconds ++;\n            }\n            setTimeout(\"redirectSuccess()\", 100 * callTimeInSeconds);\n        }\n\n        function getAgentDownloadStatus(){\n            var url ='/getCertificateDownloadStatus.mob';  // No I18N\n");
            out.write("            var req = AjaxAPI.getXMLHttpRequest();\n            req.open(\"POST\", url, true);\n            req.setRequestHeader(\"Connection\", \"close\");\n            req.onreadystatechange = function(){\n                checkAgentStatusAndContinue(req);\n            };\n            req.send(null);\n        }\n\n        function checkAgentStatusAndContinue(req){\n            var downloadStatus = req.responseText;\n            if(downloadStatus != null && downloadStatus.trim() == \"true\"){\n                redirectSuccess();\n            }\n        }\n\n        function redirectSuccess(){\n            location.href=\"/mdm/enroll?actionToCall=agentDownloadSuccess\";\n        }\n\n        // When ready...\n        window.addEventListener(\"load\",function() {\n            // Set a timeout...\n            setTimeout(function(){\n                // Hide the address bar!\n                window.scrollTo(0, 1);\n            }, 0);\n        });\n    </script>\n    <body class=\"androidContent\" style=\"height: 100%\">\n        <table width=\"100%\">\n            <tr class=\"androidHeader\">\n");
            out.write("                <td>&nbsp;&nbsp;&nbsp;<img src=\"/images/ic_launcher.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]));
            out.write("</td>\n            </tr>\n        </table>\n        <div class=\"androidDivcontent\">\n            <b>");
            out.print(I18N.getMsg("dc.mdm.email.android.enrollment_download", new Object[0]));
            out.write("</b> <br />\n            <p>\n                ");
            out.print(I18N.getMsg("dc.mdm.email.android.download_apk", new Object[0]));
            out.write("\n            </p>\n        </div>\n        <table width=\"100%\" style=\"position: fixed; height: 30px; bottom:0; \">\n            <tr  class=\"androidbuttonBg\">\n                <td style=\"padding: 10px\" align=\"right\">\n                    <input type=\"button\" onclick=\"javascript:downloadAgent();\" value=\"");
            out.print(I18N.getMsg("dc.common.button.CONTINUE", new Object[0]));
            out.write("\" class=\"androidButton\"/>\n                </td>\n            </tr>\n        </table>\n    </body>\n</html>\n");
        }
        catch (final Throwable t) {
            if (!(t instanceof SkipPageException)) {
                out = _jspx_out;
                if (out != null && out.getBufferSize() != 0) {
                    try {
                        if (response.isCommitted()) {
                            out.flush();
                        }
                        else {
                            out.clearBuffer();
                        }
                    }
                    catch (final IOException ex) {}
                }
                if (_jspx_page_context == null) {
                    throw new ServletException(t);
                }
                _jspx_page_context.handlePageException(t);
            }
        }
        finally {
            mdmAPKDownload_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmAPKDownload_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmAPKDownload_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        mdmAPKDownload_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
    }
}

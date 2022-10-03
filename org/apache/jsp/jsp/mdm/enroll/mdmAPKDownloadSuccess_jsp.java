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

public final class mdmAPKDownloadSuccess_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmAPKDownloadSuccess_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmAPKDownloadSuccess_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmAPKDownloadSuccess_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmAPKDownloadSuccess_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = mdmAPKDownloadSuccess_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css\" />\n        <script type=\"text/JavaScript\" language=\"JavaScript\">\n            // When ready...\n            window.addEventListener(\"load\",function() {\n                // Set a timeout...\n                setTimeout(function(){\n                    // Hide the address bar!\n                    window.scrollTo(0, 1);\n                }, 0);\n            });\n\t\t\t\n\t\t\tfunction SelectAll(containerid){                \n                    if (document.selection) {\n                        var range = document.body.createTextRange();\n                        range.moveToElementText(document.getElementById(containerid));\n                        range.select();\n                    } else if (window.getSelection()) {\n                        var range = document.createRange();\n                        range.selectNode(document.getElementById(containerid));\n                        window.getSelection().removeAllRanges();\n                        window.getSelection().addRange(range);\n                    }\n");
            out.write("                }\t\t\t\n        </script>\n    </head>\n    <body class=\"androidContent\">\n        <table width=\"100%\">\n            <tr class=\"androidHeader\">\n                <td>&nbsp;&nbsp;&nbsp;<img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</td>\n            </tr>\n        </table>\n        <div class=\"androidDivcontent\">\n            ");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_download_success", new Object[0]));
            out.write("<br />\n            <p>\n                ");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_enroll_information", new Object[0]));
            out.write("\n            </p>\n            <ol>\n                <li>\n                    <p>");
            out.print(I18N.getMsg("dc.common.SERVER_NAME", new Object[0]));
            out.write("&nbsp;:&nbsp;<span onclick=\"SelectAll('server_ip')\" id=\"server_ip\"><b>");
            out.write((String)PageContextImpl.proprietaryEvaluate("${serverIP}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("</b></span></p>");
            out.write("\n                </li>\n                <li>\n                    <p>");
            out.print(I18N.getMsg("dc.common.SERVER_PORT", new Object[0]));
            out.write("&nbsp;:&nbsp;<b><span onclick=\"SelectAll('server_port')\" id=\"server_port\">");
            out.write((String)PageContextImpl.proprietaryEvaluate("${serverPort}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("</b></span> </p>");
            out.write("\n                </li>\n                <li>\n                    <p>\n                        ");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Email_Address", new Object[0]));
            out.write("&nbsp;<b>< ");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Your_Email_Address", new Object[0]));
            out.write(" ></b>\n                    </p>\n                </li>\n                <li>\n                    <p>\n                        ");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_download_authdetails", new Object[0]));
            out.write("\n                    </p>\n                </li>\n            </ol>\n            <br/>\n            <br /> ");
            out.print(I18N.getMsg("dc.mdm.enroll.you_can_close_window", new Object[0]));
            out.write("\n        </div>\n    </body>\n</html>\n");
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
            mdmAPKDownloadSuccess_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmAPKDownloadSuccess_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmAPKDownloadSuccess_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
    }
}

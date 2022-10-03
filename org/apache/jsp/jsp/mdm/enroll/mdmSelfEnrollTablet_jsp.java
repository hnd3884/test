package org.apache.jsp.jsp.mdm.enroll;

import java.util.HashSet;
import java.util.HashMap;
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

public final class mdmSelfEnrollTablet_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmSelfEnrollTablet_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmSelfEnrollTablet_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmSelfEnrollTablet_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmSelfEnrollTablet_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = mdmSelfEnrollTablet_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    ");
            out.write("\n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css\" />\n        <script type=\"text/JavaScript\" language=\"JavaScript\">\n            // When ready...\n            window.addEventListener(\"load\",function() {\n                // Set a timeout...\n                setTimeout(function(){\n                    // Hide the address bar!\n                    window.scrollTo(0, 1);\n                }, 0);\n            });\n        </script>\n    </head>\n    <body class=\"androidContent\">\n        <table width=\"100%\">\n            <tr class=\"androidBlueHeader\" valign=\"middle\">\n                <td>&nbsp;&nbsp;&nbsp;<img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</td>\n            </tr>\n        </table>\n            <div class=\"androidDivcontent\">\n                ");
            out.print(I18N.getMsg("dc.mdm.email.android.self_enroll_steps", new Object[0]));
            out.write("\n            <ol>\n                <li>\n                    <p>\n                        ");
            out.print(I18N.getMsg("dc.mdm.email.android.enrollment_download", new Object[0]));
            out.write("\n                    </p>\n                </li>\n                <li>\n                    <p>\n                        ");
            out.print(I18N.getMsg("dc.mdm.email.android.enrollment_install", new Object[0]));
            out.write("\n                    </p>\n                </li>\n                <li>\n                    <p>\n                        ");
            out.print(I18N.getMsg("dc.mdm.email.android.enrollment_enroll", new Object[0]));
            out.write("\n                    </p>\n                </li>\n\n            </ol>\n            </div>\n\n        <table width=\"100%\" style=\"position: fixed; height: 30px; bottom:0; \">\n            <tr  class=\"androidbuttonBg\">\n                <td style=\"padding: 10px\" align=\"right\">\n                    <a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${androidEnrollmentUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"><input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.button.CONTINUE", new Object[0]));
            out.write("\" class=\"androidButtonBlue\"/></a>\n                </td>\n            </tr>\n        </table>\n    </body>\n</html>\n");
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
            mdmSelfEnrollTablet_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (mdmSelfEnrollTablet_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("jar:file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar!/META-INF/c.tld", 1425958870000L);
        mdmSelfEnrollTablet_jsp._jspx_dependants.put("file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar", 1663600380000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmSelfEnrollTablet_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmSelfEnrollTablet_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
    }
}

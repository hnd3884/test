package org.apache.jsp.jsp.common;

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
import com.adventnet.i18n.I18N;
import com.me.mdm.api.error.IAMExceptionHandler;
import java.util.logging.Level;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Logger;
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

public final class mdmErrorPage_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmErrorPage_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmErrorPage_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmErrorPage_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmErrorPage_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = mdmErrorPage_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n\n");
            final Logger logger = Logger.getLogger("MDMErrorPageLogger");
            final IAMSecurityException exception = (IAMSecurityException)request.getAttribute(IAMSecurityException.class.getName());
            if (exception != null) {
                final String uri = exception.getUri();
                logger.log(Level.INFO, "uri - " + uri);
                if (uri.contains("/api/")) {
                    final IAMExceptionHandler exceptionHandler = new IAMExceptionHandler();
                    exceptionHandler.writeAPIErrorResponse(exception, request, response);
                    return;
                }
                if (uri.contains("/emsapi/")) {
                    final com.me.devicemanagement.framework.webclient.filter.security.IAMExceptionHandler exceptionHandler2 = new com.me.devicemanagement.framework.webclient.filter.security.IAMExceptionHandler();
                    exceptionHandler2.writeAPIErrorResponse(exception, request, response);
                    return;
                }
                if (uri.contains("/emsapi/")) {
                    final com.me.devicemanagement.framework.webclient.filter.security.IAMExceptionHandler exceptionHandler2 = new com.me.devicemanagement.framework.webclient.filter.security.IAMExceptionHandler();
                    exceptionHandler2.writeAPIErrorResponse(exception, request, response);
                    return;
                }
            }
            out.write("\n<html>\n    <head>\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <style>\n        @font-face {\n            font-family: 'Lato';\n            src: url('../../../themes/styles/font/Lato.woff2') format('woff2');\n        }\n        @font-face {\n           font-family:\"Roboto\";\n           font-weight:400;\n           font-style:normal;\n           src: url(\"https://webfonts.zohowebstatic.com/robotoregular/font.woff\") format(\"woff\");\n        }\n        .paragraph {\n            font: 14px 'Lato', 'Roboto', sans-serif;\n            padding-top: 50px;\n            line-height: 24px;\n        }\n        .title {\n            font: 600 58px 'lato',sans-serif;\n            border: 0px !important;\n            padding: 17px;\n            color: #f44842;\n        }\n        .btn {\n            border: 1px solid #f44842;\n            border-radius: 30px;\n            padding: 5px 25px;\n            width: auto;\n            font:15px 'Lato', 'Roboto', sans-serif;\n");
            out.write("            color: #f44842;\n            background-color: #fff;\n        }\n\n        p {\n            margin: 0px !important;\n            padding: 0px !important;\n        }\n\n        input.btn:focus {\n            outline: none;\n        }\n\n        .btn:hover {\n            color: #fff;\n            background-color: #f44842;\n            cursor: pointer;\n        }\n        </style>\n    </head>\n\n\n<body>\n    <div width=\"100%\" align=\"center\" class=\"paragraph\">\n        <div class=\"title\"><p >");
            out.print(I18N.getMsg("dc.errorPage.sorry", new Object[0]));
            out.write("</p></div>\n    <p style=\"font: 600 17px 'lato',sans-serif;\">");
            out.print(I18N.getMsg("dc.errorPage.trouble_in_loading_page", new Object[0]));
            out.write("<br></p>\n    ");
            if (!Boolean.parseBoolean(request.getHeader("X-SGS"))) {
                out.write("\n        <p style=\"padding:10px !important;\">\n            ");
                out.print(I18N.getMsg("dc.errorPage.return_to_home_page_and_retry", new Object[0]));
                out.write("\n            <br>\n            ");
                out.print(I18N.getMsg("dc.errorPage.upload_logs_if_prob_persists", new Object[0]));
                out.write("\n        </p>\n    ");
            }
            out.write("\n    </body>\n</html>\n");
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
            mdmErrorPage_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (mdmErrorPage_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmErrorPage_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmErrorPage_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        mdmErrorPage_jsp._jspx_imports_classes.add("com.adventnet.iam.security.IAMSecurityException");
        mdmErrorPage_jsp._jspx_imports_classes.add("com.me.mdm.api.error.IAMExceptionHandler");
        mdmErrorPage_jsp._jspx_imports_classes.add("java.util.logging.Logger");
        mdmErrorPage_jsp._jspx_imports_classes.add("java.util.logging.Level");
    }
}

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
import com.adventnet.iam.xss.IAMEncoder;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
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

public final class NoPermToAccessMDM_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return NoPermToAccessMDM_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return NoPermToAccessMDM_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return NoPermToAccessMDM_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = NoPermToAccessMDM_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = NoPermToAccessMDM_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write("\n\n\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n    <style>\n    @font-face {\n        font-family: 'Lato';\n        src: url('../../../themes/styles/font/Lato.woff2') format('woff2');\n    }\n    @font-face {\n        font-family:\"Roboto\";\n        font-weight:400;\n        font-style:normal;\n        src: url(\"https://webfonts.zohowebstatic.com/robotoregular/font.woff\") format(\"woff\");\n    }\n    html, body {\n        padding: 0;\n        margin: 0;\n        font: 14px 'Lato', 'Roboto', sans-serif;\n    }      \n    .logoFreeEdition{\n        background: url(../../../images/dm-default/dc-logo.png) no-repeat top left;\n        background-size: 270px auto;\n        height: 100px;\n        background-position: 40px center\n    }\n    .licenseWrap {\n        width: 900px;\n        -webkit-border-radius: 5px;\n        -moz-border-radius: 5px;\n        border-radius: 5px;\n        background-color: #f9f9f9;\n        -webkit-box-shadow: 0 0 8px rgba(1,1,1,.14);\n");
            out.write("        -moz-box-shadow: 0 0 8px rgba(1,1,1,.14);\n        box-shadow: 0 0 8px rgba(1,1,1,.14);\n        border: solid 1px #d8dcdc;\n        padding: 30px 20px 20px 20px;\n        margin: 20px auto;\n        text-align:left\\9;\n    }            \n    .expireTitle {\n        color: #ff0000;\n        font-size: 17px;\n    }\n    .welcometext {\n        color:#000;\n        font-family: 'Lato', 'Roboto', sans-serif;\n    }\n\n    </style>\n</head>\n");
            final String pdtTitle = ProductUrlLoader.getInstance().getValue("productname");
            out.write("\n<body leftmargin=\"2\" topmargin=\"2\" marginwidth=\"2\" marginheight=\"2\">\n     <table width=\"100%\"  border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"hometablebg\">\n\n     <tr valign=\"top\">\n     <td width=\"28%\" height=\"61px\"  valign=\"top\" class=\"logoFreeEdition\" align=\"center\"><a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${prodUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/index.html\" target=\"_blank\" title=\"ManageEngine ");
            out.print(ProductUrlLoader.getInstance().getValue("displayname"));
            out.write("\" alt=\"ManageEngine ");
            out.print(ProductUrlLoader.getInstance().getValue("displayname"));
            out.write("\"><img src=\"../../images/1spacer.gif\" title=\"ManageEngine ");
            out.print(ProductUrlLoader.getInstance().getValue("displayname"));
            out.write("\" alt=\"ManageEngine ");
            out.print(ProductUrlLoader.getInstance().getValue("displayname"));
            out.write("\" border=\"0\" height=\"49\" width=\"181\"></a> </td>\n     <td width=\"72%\" valign=\"top\"  class=\"topmostlink\" nowrap> \n     <div  style=\"padding-top:3px; \">\n     <table border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n         <tr>\n         <td align=\"right\" class=\"dontwrap\">\n             <a href=\"./logout\" >");
            out.print(I18N.getMsg("dc.common.sign_out", new Object[0]));
            out.write("</a>\n             <span class=\"welcometext\">&nbsp;[<b>");
            out.print(IAMEncoder.encodeHTML(request.getUserPrincipal().getName()));
            out.write("</b>]&nbsp;&nbsp;</span>\n         </td>\n         </tr>\n     </table>\n     </div>\n     </td>\n </tr>\n                             </table>\n   <div class=\"licenseWrap\" >\n       <img src=\"images/alerts.png\" width=\"30\" height=\"30\" align=\"absmiddle\" /><b><span class=\"expireTitle\">");
            out.print(I18N.getMsg("dc.license.trial_or_license_expired", new Object[0]));
            out.write("</span></b>\n       <br/>\n       <br/>\n       <p style=\"position:relative; right:-30px;\">");
            out.print(I18N.getMsg("dc.license.free_edition.contact_admin", new Object[0]));
            out.write("</p>\n    </div>\n\n    </body>\n</html>");
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
            NoPermToAccessMDM_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (NoPermToAccessMDM_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        NoPermToAccessMDM_jsp._jspx_dependants.put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        NoPermToAccessMDM_jsp._jspx_imports_packages.add("javax.servlet.http");
        NoPermToAccessMDM_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        NoPermToAccessMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        NoPermToAccessMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
        NoPermToAccessMDM_jsp._jspx_imports_classes.add("com.adventnet.iam.xss.IAMEncoder");
    }
}

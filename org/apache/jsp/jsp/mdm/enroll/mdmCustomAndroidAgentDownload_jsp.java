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
import org.apache.jasper.runtime.JspRuntimeLibrary;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
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
import org.apache.jasper.runtime.TagHandlerPool;
import java.util.Set;
import java.util.Map;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class mdmCustomAndroidAgentDownload_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmCustomAndroidAgentDownload_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmCustomAndroidAgentDownload_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmCustomAndroidAgentDownload_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmCustomAndroidAgentDownload_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
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
            final PageContext pageContext = _jspx_page_context = mdmCustomAndroidAgentDownload_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    \n    \n    \n    ");
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.email.android.enrollment_enroll", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css\" />\n        <script type=\"text/JavaScript\" language=\"JavaScript\">\n            // When ready...\n            window.addEventListener(\"load\",function() {\n                // Set a timeout...\n                setTimeout(function(){\n                    // Hide the address bar!\n                    window.scrollTo(0, 1);\n                }, 0);\n            });\n        </script>\n    </head>\n    <body class=\"androidContent\">\n        <table width=\"100%\">\n            <tr class=\"androidHeader\">\n                <td>&nbsp;&nbsp;&nbsp;<img src=\"/images/ic_launcher.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</td>\n            </tr>\n        </table>\n        <div class=\"androidDivcontent\">\n            ");
            final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f0_reused = false;
            try {
                _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f0.setParent((Tag)null);
                _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSAFE == null || isSAFE == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
                if (_jspx_eval_c_005fif_005f0 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n                <p> ");
                        out.print(I18N.getMsg("dc.mdm.device_undetectable", new Object[0]));
                        out.write(" </p> <br />\n                <a href=\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${safeURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("\"> <div id=\"music\" class=\"nav\">");
                        out.print(I18N.getMsg("dc.mdm.download_safe", new Object[0]));
                        out.write("</div> </a> <br /><br />\n                <p> ");
                        out.print(I18N.getMsg("dc.mdm.device_undetectable_android_download", new Object[0]));
                        out.write(" </p> <br />\n                <a href=\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${androidURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("\"> <div id=\"music\" class=\"nav\">");
                        out.print(I18N.getMsg("dc.patch.apd.download", new Object[0]));
                        out.write("</div> </a>\n            ");
                        evalDoAfterBody = _jspx_th_c_005fif_005f0.doAfterBody();
                    } while (evalDoAfterBody == 2);
                }
                if (_jspx_th_c_005fif_005f0.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f0);
                _jspx_th_c_005fif_005f0_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f0_reused);
            }
            out.write("\n            ");
            final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f1_reused = false;
            try {
                _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f2.setParent((Tag)null);
                _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSAFE != null && isSAFE == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
                if (_jspx_eval_c_005fif_005f2 != 0) {
                    int evalDoAfterBody2;
                    do {
                        out.write("\n                <p> ");
                        out.print(I18N.getMsg("dc.common.DOWNLOAD_ME_MDM", new Object[0]));
                        out.write(" </p> <br />\n                <a href=\"https://play.google.com/store/apps/details?id=com.manageengine.mdm.samsung\"> <div id=\"music\" class=\"nav\">");
                        out.print(I18N.getMsg("dc.mdm.download_safe", new Object[0]));
                        out.write("</div> </a> <br /><br />\n            ");
                        evalDoAfterBody2 = _jspx_th_c_005fif_005f2.doAfterBody();
                    } while (evalDoAfterBody2 == 2);
                }
                if (_jspx_th_c_005fif_005f2.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f2);
                _jspx_th_c_005fif_005f1_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f1_reused);
            }
            out.write("\n\n            ");
            final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f2_reused = false;
            try {
                _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f3.setParent((Tag)null);
                _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnrollment !=null && isSelfEnrollment == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
                if (_jspx_eval_c_005fif_005f3 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n                <ol>\n                    <li>\n                        <p>\n                            ");
                        out.print(I18N.getMsg("dc.common.SERVER_NAME", new Object[0]));
                        out.write("\n\t\t\t\t\t\t\t&nbsp;:&nbsp;<b>");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${serverIP}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("</b>");
                        out.write("\n                        </p>\n                    </li>\n                    <li>\n                        <p>\n                            ");
                        out.print(I18N.getMsg("dc.common.SERVER_PORT", new Object[0]));
                        out.write("\n\t\t\t\t\t\t\t&nbsp;:&nbsp;<b>");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${serverPort}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("</b>");
                        out.write("\n                        </p>\n                    </li>\n                    <li>\n                        <p>\n                            ");
                        out.print(I18N.getMsg("dc.mdm.email.Corporate_Email_Address", new Object[0]));
                        out.write("&nbsp;<b>< ");
                        out.print(I18N.getMsg("dc.mdm.email.Corporate_Your_Email_Address", new Object[0]));
                        out.write(" ></b>\n                        </p>\n                    </li>\n                    <li>\n                        <p>\n                            ");
                        out.print(I18N.getMsg("dc.mdm.email.android.agent_download_authdetails", new Object[0]));
                        out.write("\n                        </p>\n                    </li>\n                </ol>\n            ");
                        evalDoAfterBody3 = _jspx_th_c_005fif_005f3.doAfterBody();
                    } while (evalDoAfterBody3 == 2);
                }
                if (_jspx_th_c_005fif_005f3.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f3);
                _jspx_th_c_005fif_005f2_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f2_reused);
            }
            out.write("\n        <br/>\n</div>\n\n    </body>\n\n</html>\n\n\n");
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
            mdmCustomAndroidAgentDownload_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (mdmCustomAndroidAgentDownload_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        mdmCustomAndroidAgentDownload_jsp._jspx_dependants.put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmCustomAndroidAgentDownload_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmCustomAndroidAgentDownload_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        mdmCustomAndroidAgentDownload_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

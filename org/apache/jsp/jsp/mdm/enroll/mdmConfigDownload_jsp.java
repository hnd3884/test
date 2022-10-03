package org.apache.jsp.jsp.mdm.enroll;

import java.util.HashSet;
import java.util.HashMap;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
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

public final class mdmConfigDownload_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmConfigDownload_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmConfigDownload_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmConfigDownload_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmConfigDownload_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fchoose = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
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
            final PageContext pageContext = _jspx_page_context = mdmConfigDownload_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n\n    \n        \n            ");
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write("\n\n                <head>\n                    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n                    <title>\n                        ");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("\n                    </title>\n                    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n                    <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\" />\n                    <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\" />\n                    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\n                    <meta name=\"HandheldFriendly\" content=\"true\" />\n                    <meta name=\"MobileOptimized\" content=\"width\" />\n                    <!-- The previous ulr of css will be cached in the device.So to load the changed css the url path is changed by appending 1 as the parameter -->\n                    <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css?2\" />\n                </head>\n                <script src=\"/framework/javascript/IncludeJS.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n\t\t\t\t<script src=\"/js/mdm/mdmDeviceEnrollmentPage.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n                <script>\n                    includeMainScripts(\"\",\"");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\");\n                </script>\n                <script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/AjaxAPI.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n                <script>\n\t\t\t\t\tfunction proceedEnroll(){\n\t\t\t\t\t\n\t\t\t\t\t var platformtype=  '");
            out.write((String)PageContextImpl.proprietaryEvaluate("${platform}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';\n\t\t\t\t\t \n\t\t\t\t\t switch(platformtype){\n\t\t\t\t\t case '1': showterms(); break;\n\t\t\t\t\t case '2': var responseJSON= JSON.parse('");
            out.write((String)PageContextImpl.proprietaryEvaluate("${responseJSON}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("');\n\t\t\t\t\t\t\t downloadAndroidonAuthJSON(responseJSON.MessageResponse); \n\t\t\t\t\t\t\t break;\n\t\t\t\t\t}\n\t\t\t\t\t}\n                    var mobileConfigDownloadStatusCalled = false;\n                    var downloadedProfile = false;\n                    var time = 0;\n                     oldStatus = 1;\n\n\n                        // When ready...\n                    window.addEventListener(\"load\", function() {\n                        // Set a timeout...\n                        setTimeout(function() {\n                            // Hide the address bar!\n                            window.scrollTo(0, 1);\n                        }, 0);\n                    });\n                </script>\n\n                <body onload=\"setThisPageSettings()\">\n                    <div class=\"displaywithPaddingIphone\">\n                       <table width=\"100%\"  ");
            if (this._jspx_meth_c_005fif_005f0(_jspx_page_context)) {
                return;
            }
            out.write(">\n                            <tr class=\"blueHeader\" >\n                                <td>\n                                    <div><img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\" />&nbsp;&nbsp;&nbsp;\n                                        ");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("\n                                    </div>\n                                </td>\n                            </tr>\n                        </table>\n                        <div id=\"headerDiv1\" ");
            if (this._jspx_meth_c_005fchoose_005f0(_jspx_page_context)) {
                return;
            }
            out.write(62);
            out.write("\n                            ");
            final ChooseTag _jspx_th_c_005fchoose_005f1 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f1_reused = false;
            try {
                _jspx_th_c_005fchoose_005f1.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f1.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f1 = _jspx_th_c_005fchoose_005f1.doStartTag();
                if (_jspx_eval_c_005fchoose_005f1 != 0) {
                    int evalDoAfterBody3;
                    do {
                        final WhenTag _jspx_th_c_005fwhen_005f1 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f1_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f1.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f1.setParent((Tag)_jspx_th_c_005fchoose_005f1);
                            _jspx_th_c_005fwhen_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${platform=='1'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f1 = _jspx_th_c_005fwhen_005f1.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f1 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.print(I18N.getMsg("dc.mdm.enroll.mdm_profile_installation", new Object[0]));
                                    evalDoAfterBody = _jspx_th_c_005fwhen_005f1.doAfterBody();
                                } while (evalDoAfterBody == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f1.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f1);
                            _jspx_th_c_005fwhen_005f1_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f1_reused);
                        }
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f1 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f1_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f1.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f1.setParent((Tag)_jspx_th_c_005fchoose_005f1);
                            final int _jspx_eval_c_005fotherwise_005f1 = _jspx_th_c_005fotherwise_005f1.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f1 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.print(I18N.getMsg("dc.mdm.viewparams.EnrollmentRequest.title", new Object[0]));
                                    evalDoAfterBody2 = _jspx_th_c_005fotherwise_005f1.doAfterBody();
                                } while (evalDoAfterBody2 == 2);
                            }
                            if (_jspx_th_c_005fotherwise_005f1.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f1);
                            _jspx_th_c_005fotherwise_005f1_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f1_reused);
                        }
                        evalDoAfterBody3 = _jspx_th_c_005fchoose_005f1.doAfterBody();
                    } while (evalDoAfterBody3 == 2);
                }
                if (_jspx_th_c_005fchoose_005f1.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f1);
                _jspx_th_c_005fchoose_005f1_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f1_reused);
            }
            out.write("\n                        </div>\n                        <div id=\"contentDiv\" ");
            if (this._jspx_meth_c_005fchoose_005f2(_jspx_page_context)) {
                return;
            }
            out.write(">\n                            <p id=\"installProfileP\">\n                              \t\t\t\t\t\t\t");
            final ChooseTag _jspx_th_c_005fchoose_005f2 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f3_reused = false;
            try {
                _jspx_th_c_005fchoose_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f2.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f2 = _jspx_th_c_005fchoose_005f2.doStartTag();
                if (_jspx_eval_c_005fchoose_005f2 != 0) {
                    int evalDoAfterBody6;
                    do {
                        final WhenTag _jspx_th_c_005fwhen_005f2 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f3_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f2.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            _jspx_th_c_005fwhen_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isQREnrollment=='true'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f2 = _jspx_th_c_005fwhen_005f2.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f2 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t <span>");
                                    out.print(I18N.getMsg("dc.mdm.email.Dear_User", new Object[0]));
                                    out.write("&nbsp;<b>");
                                    out.print(request.getAttribute("DISPLAY_NAME"));
                                    out.write("&nbsp;(");
                                    out.print(request.getAttribute("EMAIL_ADDRESS"));
                                    out.write(")&nbsp;</b>&nbsp;,&nbsp;<br> <br>");
                                    out.print(I18N.getMsg("mdm.enroll.qr_enroll_title", new Object[] { request.getAttribute("organisation_name") }));
                                    out.write("</span>\n                            \n\t\t\t\t\t\t\t\t\t");
                                    evalDoAfterBody2 = _jspx_th_c_005fwhen_005f2.doAfterBody();
                                } while (evalDoAfterBody2 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f2.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f2);
                            _jspx_th_c_005fwhen_005f3_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f3_reused);
                        }
                        out.write("\n\t\t\t\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f3 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f4_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f3.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            _jspx_th_c_005fwhen_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${platform=='2'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f3 = _jspx_th_c_005fwhen_005f3.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f3 != 0) {
                                int evalDoAfterBody4;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t\t\t<span>");
                                    out.print(I18N.getMsg("mdm.enroll.android_auth_desc", new Object[0]));
                                    out.write("</span>\n\t\t\t\t\t\t\t\t\t");
                                    evalDoAfterBody4 = _jspx_th_c_005fwhen_005f3.doAfterBody();
                                } while (evalDoAfterBody4 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f3.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f3);
                            _jspx_th_c_005fwhen_005f4_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f4_reused);
                        }
                        out.write("\n\t\t\t\t\t\t\t\t\t");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f2 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f3_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f2.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            final int _jspx_eval_c_005fotherwise_005f2 = _jspx_th_c_005fotherwise_005f2.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f2 != 0) {
                                int evalDoAfterBody5;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t  ");
                                    out.print(I18N.getMsg("dc.mdm.enroll.click_to_download_install_profile", new Object[0]));
                                    out.write(".\n                                    ");
                                    out.print(I18N.getMsg("dc.mdm.enroll.enroll_device_allow_access", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t\t\t");
                                    evalDoAfterBody5 = _jspx_th_c_005fotherwise_005f2.doAfterBody();
                                } while (evalDoAfterBody5 == 2);
                            }
                            if (_jspx_th_c_005fotherwise_005f2.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f2);
                            _jspx_th_c_005fotherwise_005f3_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f3_reused);
                        }
                        out.write("\n\t\t\t\t\t\t\t");
                        evalDoAfterBody6 = _jspx_th_c_005fchoose_005f2.doAfterBody();
                    } while (evalDoAfterBody6 == 2);
                }
                if (_jspx_th_c_005fchoose_005f2.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f2);
                _jspx_th_c_005fchoose_005f3_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f3_reused);
            }
            out.write("\n                            </p>\n                            <p id=\"successMsg\">\n                                ");
            out.print(I18N.getMsg("dc.mdm.enroll.device_enrolled_successfully", new Object[0]));
            out.write("\n                            </p>\n                            <div id=\"kb-list\" class=\"hide\">\n                                <p>\n                                    ");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_install_failed.title", new Object[0]));
            out.write("\n                                </p>\n                                <ol>\n                                    <li id=\"possibleReasons1\">");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_install_failed1", new Object[0]));
            out.write("\n                                 </li>\n                                 <li id=\"possibleReasons2\">\n                                        ");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_install_failed2", new Object[0]));
            out.write("\n                                    </li>\n                                </ol>\n                            </div>\n                        </div>\n\t\t\t\t\t\t<div id=\"termsdiv\" class=\"hide\">\n\t\t\t\t\t\t\t<div id=\"diV\" ");
            if (this._jspx_meth_c_005fchoose_005f4(_jspx_page_context)) {
                return;
            }
            out.write(62);
            out.write("\n\t\t\t\t\t\t\t<div style=\"padding-top:25%;\" id=\"loadingdiv\"><img src=\"/images/loader.gif\" height=\"150\" width=\"180\" align=\"center\" /></div>\n\t\t\t\t\t\t\t<iframe id=\"termsFrame\" class=\"hide\" frameborder=\"0\" onload=\"showframe();\" ></iframe>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div style=\"padding-bottom:0px;\">&nbsp;</div>\n\t\t\t\t\t\t\t<div style=\"padding-bottom:0px;padding-left: 25px;\"><input type=\"checkbox\" id=\"acceptterms\" onclick=\"showcontinue();\"/><span style=\"padding-left:5px;\" onclick=\"checkboxoperation();showcontinue();\">");
            out.print(I18N.getMsg("mdm.terms.agree_terms", new Object[0]));
            out.write("</span></div>\n\t\t\t\t\t\t\t<div id=\"errormsg\" style=\"padding-top:12px;\" class=\"hide\"></div>\n\t\t\t\t\t\t</div>\n                        <div id=\"continueBtn\" ");
            if (this._jspx_meth_c_005fchoose_005f5(_jspx_page_context)) {
                return;
            }
            out.write(">\n                            <input id=\"continueButton\" class=\"signinbtnIphone\" onclick=\"proceedEnroll();\" type=\"button\" \n\t\t\t\t\t\t\tvalue=");
            out.print(I18N.getMsg("dc.common.button.CONTINUE", new Object[0]));
            out.write(" />\n                        </div>\n                        <div id=\"closebutton\" class=\"hide\">\n                            <input class=\"signinbtnIphone\"  onclick=\"closeWindow();\" type=\"button\" value=");
            out.print(I18N.getMsg("dc.common.CLOSE", new Object[0]));
            out.write(" />\n                        </div>\n\n                    </div>\n                    <div id=\"footerGreen\" ");
            if (this._jspx_meth_c_005fchoose_005f6(_jspx_page_context)) {
                return;
            }
            out.write(">\n\t\t\t<p>&#169 ");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]));
            out.write("</p>\n\t\t</div>\n\t\t\n\t\t<input type=\"hidden\" value=\"");
            out.print(DMIAMEncoder.encodeJavaScript(String.valueOf(session.getAttribute("erid"))));
            out.write("\" id=\"erid\">\n\t\t<input type=\"hidden\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${downloadTime}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" id=\"downloadTime\">\n\t\t<input type=\"hidden\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${userAgentType}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" id=\"userAgentType\">\n\t\t<input type=\"hidden\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${path}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" id=\"terms_path\">\n\t\t<input type=\"hidden\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${showNewiOS12EnrollmentSteps}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" id=\"showNewiOS12EnrollmentSteps\">\n               </body>\n</html>\n");
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
            mdmConfigDownload_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_c_005fif_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f0_reused = false;
        try {
            _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f0.setParent((Tag)null);
            _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${platform!='1'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
            if (_jspx_eval_c_005fif_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"hide\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fif_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f0_reused = false;
        try {
            _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f0((JspTag)_jspx_th_c_005fchoose_005f0, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f0((JspTag)_jspx_th_c_005fchoose_005f0, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f0.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f0);
            _jspx_th_c_005fchoose_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f0(final JspTag _jspx_th_c_005fchoose_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f0_reused = false;
        try {
            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"headerIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f0);
            _jspx_th_c_005fwhen_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f0(final JspTag _jspx_th_c_005fchoose_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f0_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
            final int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"header\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f0);
            _jspx_th_c_005fotherwise_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f2(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f2 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f2_reused = false;
        try {
            _jspx_th_c_005fchoose_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f2.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f2 = _jspx_th_c_005fchoose_005f2.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f2 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f2((JspTag)_jspx_th_c_005fchoose_005f2, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f2((JspTag)_jspx_th_c_005fchoose_005f2, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f2.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f2);
            _jspx_th_c_005fchoose_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f2(final JspTag _jspx_th_c_005fchoose_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f2 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f2_reused = false;
        try {
            _jspx_th_c_005fwhen_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
            _jspx_th_c_005fwhen_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f2 = _jspx_th_c_005fwhen_005f2.doStartTag();
            if (_jspx_eval_c_005fwhen_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"displayTextIphone-image\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f2.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f2);
            _jspx_th_c_005fwhen_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f2(final JspTag _jspx_th_c_005fchoose_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f2 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f2_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
            final int _jspx_eval_c_005fotherwise_005f2 = _jspx_th_c_005fotherwise_005f2.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"displayText-image\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f2.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f2);
            _jspx_th_c_005fotherwise_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f4(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f4 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f4_reused = false;
        try {
            _jspx_th_c_005fchoose_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f4.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f4 = _jspx_th_c_005fchoose_005f4.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f4 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f5((JspTag)_jspx_th_c_005fchoose_005f4, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f4((JspTag)_jspx_th_c_005fchoose_005f4, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f4.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f4);
            _jspx_th_c_005fchoose_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f5(final JspTag _jspx_th_c_005fchoose_005f4, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f5 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f5_reused = false;
        try {
            _jspx_th_c_005fwhen_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f4);
            _jspx_th_c_005fwhen_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f5 = _jspx_th_c_005fwhen_005f5.doStartTag();
            if (_jspx_eval_c_005fwhen_005f5 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"displayContentIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f5.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f5.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f5);
            _jspx_th_c_005fwhen_005f5_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f5_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f4(final JspTag _jspx_th_c_005fchoose_005f4, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f4 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f4_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f4.setParent((Tag)_jspx_th_c_005fchoose_005f4);
            final int _jspx_eval_c_005fotherwise_005f4 = _jspx_th_c_005fotherwise_005f4.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"displayContent\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f4.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f4);
            _jspx_th_c_005fotherwise_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f5(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f5 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f5_reused = false;
        try {
            _jspx_th_c_005fchoose_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f5.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f5 = _jspx_th_c_005fchoose_005f5.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f5 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f6((JspTag)_jspx_th_c_005fchoose_005f5, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f5((JspTag)_jspx_th_c_005fchoose_005f5, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f5.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f5.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f5);
            _jspx_th_c_005fchoose_005f5_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f5_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f6(final JspTag _jspx_th_c_005fchoose_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f6 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f6_reused = false;
        try {
            _jspx_th_c_005fwhen_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f5);
            _jspx_th_c_005fwhen_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f6 = _jspx_th_c_005fwhen_005f6.doStartTag();
            if (_jspx_eval_c_005fwhen_005f6 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signinbtndivIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fwhen_005f6_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f6_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f5(final JspTag _jspx_th_c_005fchoose_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f5 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f5_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f5);
            final int _jspx_eval_c_005fotherwise_005f5 = _jspx_th_c_005fotherwise_005f5.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f5 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signinbtndiv\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f5.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f5.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f5);
            _jspx_th_c_005fotherwise_005f5_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f5_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f6(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f6 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f6_reused = false;
        try {
            _jspx_th_c_005fchoose_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f6.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f6 = _jspx_th_c_005fchoose_005f6.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f6 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f7((JspTag)_jspx_th_c_005fchoose_005f6, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f6((JspTag)_jspx_th_c_005fchoose_005f6, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f6.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f6);
            _jspx_th_c_005fchoose_005f6_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f6_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f7(final JspTag _jspx_th_c_005fchoose_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f7 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f7_reused = false;
        try {
            _jspx_th_c_005fwhen_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f7.setParent((Tag)_jspx_th_c_005fchoose_005f6);
            _jspx_th_c_005fwhen_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f7 = _jspx_th_c_005fwhen_005f7.doStartTag();
            if (_jspx_eval_c_005fwhen_005f7 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"fotterDivFixedIphone fotterDivFixedIphoneBlue\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f7.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f7.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f7);
            _jspx_th_c_005fwhen_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f6(final JspTag _jspx_th_c_005fchoose_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f6 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f6_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f6);
            final int _jspx_eval_c_005fotherwise_005f6 = _jspx_th_c_005fotherwise_005f6.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f6 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"fotterDiv fotterDivFixedIphoneBlue\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f6);
            _jspx_th_c_005fotherwise_005f6_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f6_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (mdmConfigDownload_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmConfigDownload_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmConfigDownload_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        mdmConfigDownload_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        mdmConfigDownload_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

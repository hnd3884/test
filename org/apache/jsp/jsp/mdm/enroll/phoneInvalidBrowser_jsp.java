package org.apache.jsp.jsp.mdm.enroll;

import java.util.HashSet;
import java.util.HashMap;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import com.me.devicemanagement.framework.webclient.taglib.DCMSPTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
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

public final class phoneInvalidBrowser_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return phoneInvalidBrowser_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return phoneInvalidBrowser_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return phoneInvalidBrowser_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = phoneInvalidBrowser_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fchoose = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.release();
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
            final PageContext pageContext = _jspx_page_context = phoneInvalidBrowser_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    ");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n    ");
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write("\n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css?2\" />\n        <script type=\"text/JavaScript\" language=\"JavaScript\">\n            // When ready...\n            window.addEventListener(\"load\",function() {\n            // Set a timeout...\n            setTimeout(function(){\n            // Hide the address bar!\n            window.scrollTo(0, 1);\n            }, 0);\n            });\n        </script>\n\t\t<style>\n\t\t    .errorFontSizeBig{\n\t\t\t\t");
            if (this._jspx_meth_c_005fchoose_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\n\t\t\t}\n\t\t\t\n\t\t\t.errorFontSizeMedium{\n\t\t\t  font-size:20px;\n\t\t\t  padding:40px 0px;\n\t\t\t}\n\t\t\t\n\t\t\t.errorOrangeText{\n\t\t\t\tcolor:#ff9933;\n\t\t\t\tpadding: 10px 10px 0px 10px;\n\t\t\t\t");
            if (this._jspx_meth_c_005fchoose_005f1(_jspx_page_context)) {
                return;
            }
            out.write("\n\t\t\t\t\n\t\t\t}\n\t\t\t\n\t\t\t\n\t\t\t.subErrorText{\n\t\t\t    font-size: 17px;\n\t\t\t\tcolor:#666;\n\t\t\t\tpadding:21px;\n\t\t\t\tline-height: 25px;\n\t\t\t}\n\t\t</style>\n    </head>\n    <body>\n      <div ");
            if (this._jspx_meth_c_005fchoose_005f2(_jspx_page_context)) {
                return;
            }
            out.write(62);
            out.write("\n      <table width=\"100%\">\n            <tr class=\"blueHeader\">\n                <td><div><img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</div></td>\n            </tr>\n      </table>        \n            <div ");
            if (this._jspx_meth_c_005fchoose_005f3(_jspx_page_context)) {
                return;
            }
            out.write(" align=\"center\" id=\"displayText\"> ");
            out.write("\n\t\t\t\t<div  class=\"displayTextIphone errorFontSizeBig\" align=\"center\" id=\"displayText\">\n\t\t\t\t\t<div align=\"center\"> <img src=\"../../images/orange-alert.png\" height=\"44\" width=\"48\"/></div>\n\t\t\t\t\t");
            final ChooseTag _jspx_th_c_005fchoose_005f4 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f4_reused = false;
            try {
                _jspx_th_c_005fchoose_005f4.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f4.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f4 = _jspx_th_c_005fchoose_005f4.doStartTag();
                if (_jspx_eval_c_005fchoose_005f4 != 0) {
                    int evalDoAfterBody28;
                    do {
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f4 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f4_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f4.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f4.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${browserValid == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f4 = _jspx_th_c_005fwhen_005f4.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f4 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div align=\"center\" class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("dc.mdm.enroll.use_safari_browser", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody = _jspx_th_c_005fwhen_005f4.doAfterBody();
                                } while (evalDoAfterBody == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f4.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f4);
                            _jspx_th_c_005fwhen_005f4_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f4_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f5 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f5_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f5.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${selfEnrollEnable == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f5 = _jspx_th_c_005fwhen_005f5.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f5 != 0) {
                                int evalDoAfterBody4;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t");
                                    final DCMSPTag _jspx_th_fw_005fmsp_005f0 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
                                    boolean _jspx_th_fw_005fmsp_005f0_reused = false;
                                    try {
                                        _jspx_th_fw_005fmsp_005f0.setPageContext(_jspx_page_context);
                                        _jspx_th_fw_005fmsp_005f0.setParent((Tag)_jspx_th_c_005fwhen_005f5);
                                        _jspx_th_fw_005fmsp_005f0.setIsMSP(Boolean.valueOf("false"));
                                        final int _jspx_eval_fw_005fmsp_005f0 = _jspx_th_fw_005fmsp_005f0.doStartTag();
                                        if (_jspx_eval_fw_005fmsp_005f0 != 0) {
                                            int evalDoAfterBody2;
                                            do {
                                                out.write("\n\t\t\t\t\t\t\t\t<div  align=\"center\" style=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t\t");
                                                out.print(I18N.getMsg("dc.mdm.enroll.self_enrollment_disable_error", new Object[0]));
                                                out.write("\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t");
                                                evalDoAfterBody2 = _jspx_th_fw_005fmsp_005f0.doAfterBody();
                                            } while (evalDoAfterBody2 == 2);
                                        }
                                        if (_jspx_th_fw_005fmsp_005f0.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f0);
                                        _jspx_th_fw_005fmsp_005f0_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f0_reused);
                                    }
                                    out.write("\n\t\t\t\t\t\t");
                                    final DCMSPTag _jspx_th_fw_005fmsp_005f2 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
                                    boolean _jspx_th_fw_005fmsp_005f1_reused = false;
                                    try {
                                        _jspx_th_fw_005fmsp_005f2.setPageContext(_jspx_page_context);
                                        _jspx_th_fw_005fmsp_005f2.setParent((Tag)_jspx_th_c_005fwhen_005f5);
                                        _jspx_th_fw_005fmsp_005f2.setIsMSP(Boolean.valueOf("true"));
                                        final int _jspx_eval_fw_005fmsp_005f2 = _jspx_th_fw_005fmsp_005f2.doStartTag();
                                        if (_jspx_eval_fw_005fmsp_005f2 != 0) {
                                            int evalDoAfterBody3;
                                            do {
                                                out.write("\n\t\t\t\t\t\t\t<div class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                                out.print(I18N.getMsg("dc.mdm.actionlog.enrollment.self_enroll_not_available", new Object[0]));
                                                out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t");
                                                evalDoAfterBody3 = _jspx_th_fw_005fmsp_005f2.doAfterBody();
                                            } while (evalDoAfterBody3 == 2);
                                        }
                                        if (_jspx_th_fw_005fmsp_005f2.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f2);
                                        _jspx_th_fw_005fmsp_005f1_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f2, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f1_reused);
                                    }
                                    out.write("\n\t\t\t\t\t\t\t<div class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("dc.mdm.enroll.contact_admin_to_enroll", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody4 = _jspx_th_c_005fwhen_005f5.doAfterBody();
                                } while (evalDoAfterBody4 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f5.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f5);
                            _jspx_th_c_005fwhen_005f5_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f5_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f6 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f6_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f6.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${sufficientLicense == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f6 = _jspx_th_c_005fwhen_005f6.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f6 != 0) {
                                int evalDoAfterBody5;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div align=\"center\" class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("dc.mdm.enroll.UNABLE_TO_ADD_SELF_ENROLLMENT_REQUEST_INSUFFICIENT_LICENSE", new Object[0]));
                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody5 = _jspx_th_c_005fwhen_005f6.doAfterBody();
                                } while (evalDoAfterBody5 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f6.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f6);
                            _jspx_th_c_005fwhen_005f6_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f6_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f7 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f7_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f7.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f7.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${apnsConfigure == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f7 = _jspx_th_c_005fwhen_005f7.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f7 != 0) {
                                int evalDoAfterBody4;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div align=\"center\" class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("dc.mdm.enroll.self_anps_not_uploaded", new Object[0]));
                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody4 = _jspx_th_c_005fwhen_005f7.doAfterBody();
                                } while (evalDoAfterBody4 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f7.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f7);
                            _jspx_th_c_005fwhen_005f7_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f7_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f8 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f8_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f8.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f8.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${apnsExpired == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f8 = _jspx_th_c_005fwhen_005f8.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f8 != 0) {
                                int evalDoAfterBody6;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div align=\"center\" class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("dc.mdm.enroll.self_anps_expired", new Object[0]));
                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody6 = _jspx_th_c_005fwhen_005f8.doAfterBody();
                                } while (evalDoAfterBody6 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f8.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f8);
                            _jspx_th_c_005fwhen_005f8_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f8_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f9 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f9_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f9.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f9.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${QRError == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f9 = _jspx_th_c_005fwhen_005f9.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f9 != 0) {
                                int evalDoAfterBody7;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div align=\"center\" class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("mdm.enroll.qr.error", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t\t\t<div class=\"subErrorText\">\n\t\t\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("mdm.enroll.qr.error_details", new Object[0]));
                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody7 = _jspx_th_c_005fwhen_005f9.doAfterBody();
                                } while (evalDoAfterBody7 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f9.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f9);
                            _jspx_th_c_005fwhen_005f9_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f9_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f10 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f10_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f10.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f10.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${androidNonAppQRSrc == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f10 = _jspx_th_c_005fwhen_005f10.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f10 != 0) {
                                int evalDoAfterBody8;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div align=\"center\" class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("mdm.enroll.qr.non_app_error", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t\t\t<div class=\"subErrorText\">\n\t\t\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("mdm.enroll.qr.non_app_error_desc", new Object[] { request.getAttribute("appDownloadURL") }));
                                    out.write("<br>\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody8 = _jspx_th_c_005fwhen_005f10.doAfterBody();
                                } while (evalDoAfterBody8 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f10.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f10);
                            _jspx_th_c_005fwhen_005f10_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f10_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f11 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f11_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f11.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f11.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${enrollidValid == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f11 = _jspx_th_c_005fwhen_005f11.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f11 != 0) {
                                int evalDoAfterBody23;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div  align=\"center\" class=\"errorOrangeText\">\n\t\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("mdm.enroll.request_cannot_be_initiated", new Object[0]));
                                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t<div class=\"subErrorText\">\n\t\t\t\t\t\t\t\t");
                                    final ChooseTag _jspx_th_c_005fchoose_005f5 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                    boolean _jspx_th_c_005fchoose_005f5_reused = false;
                                    try {
                                        _jspx_th_c_005fchoose_005f5.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fchoose_005f5.setParent((Tag)_jspx_th_c_005fwhen_005f11);
                                        final int _jspx_eval_c_005fchoose_005f5 = _jspx_th_c_005fchoose_005f5.doStartTag();
                                        if (_jspx_eval_c_005fchoose_005f5 != 0) {
                                            int evalDoAfterBody22;
                                            do {
                                                out.write("\n\t\t\t\t\t\t\t\t\t");
                                                final WhenTag _jspx_th_c_005fwhen_005f12 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                boolean _jspx_th_c_005fwhen_005f12_reused = false;
                                                try {
                                                    _jspx_th_c_005fwhen_005f12.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fwhen_005f12.setParent((Tag)_jspx_th_c_005fchoose_005f5);
                                                    _jspx_th_c_005fwhen_005f12.setTest((boolean)PageContextImpl.proprietaryEvaluate("${not empty platform && platform == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fwhen_005f12 = _jspx_th_c_005fwhen_005f12.doStartTag();
                                                    if (_jspx_eval_c_005fwhen_005f12 != 0) {
                                                        int evalDoAfterBody19;
                                                        do {
                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                            final ChooseTag _jspx_th_c_005fchoose_005f6 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                            boolean _jspx_th_c_005fchoose_005f6_reused = false;
                                                            try {
                                                                _jspx_th_c_005fchoose_005f6.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fchoose_005f6.setParent((Tag)_jspx_th_c_005fwhen_005f12);
                                                                final int _jspx_eval_c_005fchoose_005f6 = _jspx_th_c_005fchoose_005f6.doStartTag();
                                                                if (_jspx_eval_c_005fchoose_005f6 != 0) {
                                                                    int evalDoAfterBody18;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f13 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f13_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f13.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f13.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            _jspx_th_c_005fwhen_005f13.setTest((boolean)PageContextImpl.proprietaryEvaluate("${ERID_ERROR_CODE == 2}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f13 = _jspx_th_c_005fwhen_005f13.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f13 != 0) {
                                                                                int evalDoAfterBody9;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("mdm.enroll.request_other_platform", new Object[0]));
                                                                                    out.write(" &nbsp;");
                                                                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody9 = _jspx_th_c_005fwhen_005f13.doAfterBody();
                                                                                } while (evalDoAfterBody9 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f13.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f13);
                                                                            _jspx_th_c_005fwhen_005f13_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f13_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f14 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f14_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f14.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f14.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            _jspx_th_c_005fwhen_005f14.setTest((boolean)PageContextImpl.proprietaryEvaluate("${ERID_ERROR_CODE == 3}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f14 = _jspx_th_c_005fwhen_005f14.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f14 != 0) {
                                                                                int evalDoAfterBody10;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("mdm.enroll.request_used_already", new Object[0]));
                                                                                    out.write("&nbsp;");
                                                                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody10 = _jspx_th_c_005fwhen_005f14.doAfterBody();
                                                                                } while (evalDoAfterBody10 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f14.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f14);
                                                                            _jspx_th_c_005fwhen_005f14_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f14_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f15 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f15_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f15.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f15.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            _jspx_th_c_005fwhen_005f15.setTest((boolean)PageContextImpl.proprietaryEvaluate("${ERID_ERROR_CODE == 4}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f15 = _jspx_th_c_005fwhen_005f15.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f15 != 0) {
                                                                                int evalDoAfterBody11;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("mdm.enroll.request_expired", new Object[0]));
                                                                                    out.write("&nbsp;");
                                                                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody11 = _jspx_th_c_005fwhen_005f15.doAfterBody();
                                                                                } while (evalDoAfterBody11 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f15.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f15);
                                                                            _jspx_th_c_005fwhen_005f15_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f15_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f16 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f16_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f16.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f16.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            _jspx_th_c_005fwhen_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${ERID_ERROR_CODE == 5}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f16 = _jspx_th_c_005fwhen_005f16.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f16 != 0) {
                                                                                int evalDoAfterBody12;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("mdm.enroll.request_revoked", new Object[0]));
                                                                                    out.write("&nbsp;");
                                                                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody12 = _jspx_th_c_005fwhen_005f16.doAfterBody();
                                                                                } while (evalDoAfterBody12 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f16.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f16);
                                                                            _jspx_th_c_005fwhen_005f16_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f16_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f17 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f17_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f17.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f17.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            _jspx_th_c_005fwhen_005f17.setTest((boolean)PageContextImpl.proprietaryEvaluate("${ERID_ERROR_CODE == 6 || ERID_ERROR_CODE == 7 }", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f17 = _jspx_th_c_005fwhen_005f17.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f17 != 0) {
                                                                                int evalDoAfterBody13;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("mdm.enroll.request_notpresent", new Object[0]));
                                                                                    out.write("&nbsp;");
                                                                                    out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody13 = _jspx_th_c_005fwhen_005f17.doAfterBody();
                                                                                } while (evalDoAfterBody13 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f17.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f17);
                                                                            _jspx_th_c_005fwhen_005f17_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f17, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f17_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final OtherwiseTag _jspx_th_c_005fotherwise_005f4 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                        boolean _jspx_th_c_005fotherwise_005f4_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fotherwise_005f4.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fotherwise_005f4.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            final int _jspx_eval_c_005fotherwise_005f4 = _jspx_th_c_005fotherwise_005f4.doStartTag();
                                                                            if (_jspx_eval_c_005fotherwise_005f4 != 0) {
                                                                                int evalDoAfterBody17;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    final ChooseTag _jspx_th_c_005fchoose_005f7 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                                                    boolean _jspx_th_c_005fchoose_005f7_reused = false;
                                                                                    try {
                                                                                        _jspx_th_c_005fchoose_005f7.setPageContext(_jspx_page_context);
                                                                                        _jspx_th_c_005fchoose_005f7.setParent((Tag)_jspx_th_c_005fotherwise_005f4);
                                                                                        final int _jspx_eval_c_005fchoose_005f7 = _jspx_th_c_005fchoose_005f7.doStartTag();
                                                                                        if (_jspx_eval_c_005fchoose_005f7 != 0) {
                                                                                            int evalDoAfterBody16;
                                                                                            do {
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                                                final WhenTag _jspx_th_c_005fwhen_005f18 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                                                boolean _jspx_th_c_005fwhen_005f18_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fwhen_005f18.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fwhen_005f18.setParent((Tag)_jspx_th_c_005fchoose_005f7);
                                                                                                    _jspx_th_c_005fwhen_005f18.setTest((boolean)PageContextImpl.proprietaryEvaluate("${empty errorMessage}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                    final int _jspx_eval_c_005fwhen_005f18 = _jspx_th_c_005fwhen_005f18.doStartTag();
                                                                                                    if (_jspx_eval_c_005fwhen_005f18 != 0) {
                                                                                                        int evalDoAfterBody14;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.device_enrolled_failed", new Object[0]));
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody14 = _jspx_th_c_005fwhen_005f18.doAfterBody();
                                                                                                        } while (evalDoAfterBody14 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fwhen_005f18.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f18);
                                                                                                    _jspx_th_c_005fwhen_005f18_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f18, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f18_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f5 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                                                boolean _jspx_th_c_005fotherwise_005f5_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fotherwise_005f5.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fotherwise_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f7);
                                                                                                    final int _jspx_eval_c_005fotherwise_005f5 = _jspx_th_c_005fotherwise_005f5.doStartTag();
                                                                                                    if (_jspx_eval_c_005fotherwise_005f5 != 0) {
                                                                                                        int evalDoAfterBody15;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                                            out.print(I18N.getMsg((String)request.getAttribute("errorMessage"), new Object[0]));
                                                                                                            out.write(" \n\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody15 = _jspx_th_c_005fotherwise_005f5.doAfterBody();
                                                                                                        } while (evalDoAfterBody15 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fotherwise_005f5.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f5);
                                                                                                    _jspx_th_c_005fotherwise_005f5_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f5_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                                                evalDoAfterBody16 = _jspx_th_c_005fchoose_005f7.doAfterBody();
                                                                                            } while (evalDoAfterBody16 == 2);
                                                                                        }
                                                                                        if (_jspx_th_c_005fchoose_005f7.doEndTag() == 5) {
                                                                                            return;
                                                                                        }
                                                                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f7);
                                                                                        _jspx_th_c_005fchoose_005f7_reused = true;
                                                                                    }
                                                                                    finally {
                                                                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f7_reused);
                                                                                    }
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody17 = _jspx_th_c_005fotherwise_005f4.doAfterBody();
                                                                                } while (evalDoAfterBody17 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fotherwise_005f4.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f4);
                                                                            _jspx_th_c_005fotherwise_005f4_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f4_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody18 = _jspx_th_c_005fchoose_005f6.doAfterBody();
                                                                    } while (evalDoAfterBody18 == 2);
                                                                }
                                                                if (_jspx_th_c_005fchoose_005f6.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f6);
                                                                _jspx_th_c_005fchoose_005f6_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f6_reused);
                                                            }
                                                            out.write("\n\t\t\t\t\t\t\t\t\t");
                                                            evalDoAfterBody19 = _jspx_th_c_005fwhen_005f12.doAfterBody();
                                                        } while (evalDoAfterBody19 == 2);
                                                    }
                                                    if (_jspx_th_c_005fwhen_005f12.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f12);
                                                    _jspx_th_c_005fwhen_005f12_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f12_reused);
                                                }
                                                out.write("\n\t\t\t\t\t\t\t\t\t");
                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f6 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                boolean _jspx_th_c_005fotherwise_005f6_reused = false;
                                                try {
                                                    _jspx_th_c_005fotherwise_005f6.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fotherwise_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f5);
                                                    final int _jspx_eval_c_005fotherwise_005f6 = _jspx_th_c_005fotherwise_005f6.doStartTag();
                                                    if (_jspx_eval_c_005fotherwise_005f6 != 0) {
                                                        int evalDoAfterBody21;
                                                        do {
                                                            out.write("\n\t\t\t\t\t\t\t\t\t");
                                                            final ChooseTag _jspx_th_c_005fchoose_005f8 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                            boolean _jspx_th_c_005fchoose_005f8_reused = false;
                                                            try {
                                                                _jspx_th_c_005fchoose_005f8.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fchoose_005f8.setParent((Tag)_jspx_th_c_005fotherwise_005f6);
                                                                final int _jspx_eval_c_005fchoose_005f8 = _jspx_th_c_005fchoose_005f8.doStartTag();
                                                                if (_jspx_eval_c_005fchoose_005f8 != 0) {
                                                                    int evalDoAfterBody20;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f19 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f19_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f19.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f19.setParent((Tag)_jspx_th_c_005fchoose_005f8);
                                                                            _jspx_th_c_005fwhen_005f19.setTest((boolean)PageContextImpl.proprietaryEvaluate("${empty errorMessage}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f19 = _jspx_th_c_005fwhen_005f19.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f19 != 0) {
                                                                                int evalDoAfterBody10;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.device_enrolled_failed", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody10 = _jspx_th_c_005fwhen_005f19.doAfterBody();
                                                                                } while (evalDoAfterBody10 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f19.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f19);
                                                                            _jspx_th_c_005fwhen_005f19_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f19_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                        final OtherwiseTag _jspx_th_c_005fotherwise_005f7 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                        boolean _jspx_th_c_005fotherwise_005f7_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fotherwise_005f7.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fotherwise_005f7.setParent((Tag)_jspx_th_c_005fchoose_005f8);
                                                                            final int _jspx_eval_c_005fotherwise_005f7 = _jspx_th_c_005fotherwise_005f7.doStartTag();
                                                                            if (_jspx_eval_c_005fotherwise_005f7 != 0) {
                                                                                int evalDoAfterBody11;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg((String)request.getAttribute("errorMessage"), new Object[0]));
                                                                                    out.write(" \n\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody11 = _jspx_th_c_005fotherwise_005f7.doAfterBody();
                                                                                } while (evalDoAfterBody11 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fotherwise_005f7.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f7);
                                                                            _jspx_th_c_005fotherwise_005f7_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f7_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody20 = _jspx_th_c_005fchoose_005f8.doAfterBody();
                                                                    } while (evalDoAfterBody20 == 2);
                                                                }
                                                                if (_jspx_th_c_005fchoose_005f8.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f8);
                                                                _jspx_th_c_005fchoose_005f8_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f8_reused);
                                                            }
                                                            out.write("\n\t\t\t\t\t\t\t\t\t");
                                                            evalDoAfterBody21 = _jspx_th_c_005fotherwise_005f6.doAfterBody();
                                                        } while (evalDoAfterBody21 == 2);
                                                    }
                                                    if (_jspx_th_c_005fotherwise_005f6.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f6);
                                                    _jspx_th_c_005fotherwise_005f6_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f6_reused);
                                                }
                                                out.write("\n\t\t\t\t\t\t\t\t");
                                                evalDoAfterBody22 = _jspx_th_c_005fchoose_005f5.doAfterBody();
                                            } while (evalDoAfterBody22 == 2);
                                        }
                                        if (_jspx_th_c_005fchoose_005f5.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f5);
                                        _jspx_th_c_005fchoose_005f5_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f5_reused);
                                    }
                                    out.write("\n\t\t\t\t\t\t\t\t\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody23 = _jspx_th_c_005fwhen_005f11.doAfterBody();
                                } while (evalDoAfterBody23 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f11.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f11);
                            _jspx_th_c_005fwhen_005f11_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f11_reused);
                        }
                        out.write("\n\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f20 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f20_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f20.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f20.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fwhen_005f20.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSSLIssue == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f20 = _jspx_th_c_005fwhen_005f20.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f20 != 0) {
                                int evalDoAfterBody27;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t<div align=\"center\" class=\"subErrorText\">\n\t\t\t\t\t\t\t\t");
                                    final ChooseTag _jspx_th_c_005fchoose_005f9 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                    boolean _jspx_th_c_005fchoose_005f9_reused = false;
                                    try {
                                        _jspx_th_c_005fchoose_005f9.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fchoose_005f9.setParent((Tag)_jspx_th_c_005fwhen_005f20);
                                        final int _jspx_eval_c_005fchoose_005f9 = _jspx_th_c_005fchoose_005f9.doStartTag();
                                        if (_jspx_eval_c_005fchoose_005f9 != 0) {
                                            int evalDoAfterBody26;
                                            do {
                                                out.write("\n\t\t\t\t\t\t\t\t\t");
                                                final WhenTag _jspx_th_c_005fwhen_005f21 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                boolean _jspx_th_c_005fwhen_005f21_reused = false;
                                                try {
                                                    _jspx_th_c_005fwhen_005f21.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fwhen_005f21.setParent((Tag)_jspx_th_c_005fchoose_005f9);
                                                    _jspx_th_c_005fwhen_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${adminAppDownloadURL != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fwhen_005f21 = _jspx_th_c_005fwhen_005f21.doStartTag();
                                                    if (_jspx_eval_c_005fwhen_005f21 != 0) {
                                                        int evalDoAfterBody21;
                                                        do {
                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                            final ChooseTag _jspx_th_c_005fchoose_005f10 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                            boolean _jspx_th_c_005fchoose_005f10_reused = false;
                                                            try {
                                                                _jspx_th_c_005fchoose_005f10.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fchoose_005f10.setParent((Tag)_jspx_th_c_005fwhen_005f21);
                                                                final int _jspx_eval_c_005fchoose_005f10 = _jspx_th_c_005fchoose_005f10.doStartTag();
                                                                if (_jspx_eval_c_005fchoose_005f10 != 0) {
                                                                    int evalDoAfterBody24;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f22 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f22_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f22.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f22.setParent((Tag)_jspx_th_c_005fchoose_005f10);
                                                                            _jspx_th_c_005fwhen_005f22.setTest((boolean)PageContextImpl.proprietaryEvaluate("${hostNameIssue == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f22 = _jspx_th_c_005fwhen_005f22.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f22 != 0) {
                                                                                int evalDoAfterBody10;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("dc.common.certificate_issue.hostname_mismatch", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody10 = _jspx_th_c_005fwhen_005f22.doAfterBody();
                                                                                } while (evalDoAfterBody10 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f22.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f22);
                                                                            _jspx_th_c_005fwhen_005f22_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f22, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f22_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f23 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f23_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f23.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f23.setParent((Tag)_jspx_th_c_005fchoose_005f10);
                                                                            _jspx_th_c_005fwhen_005f23.setTest((boolean)PageContextImpl.proprietaryEvaluate("${certChainIssue == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f23 = _jspx_th_c_005fwhen_005f23.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f23 != 0) {
                                                                                int evalDoAfterBody11;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("dc.common.certificate_issue.certificateChain_invalid", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody11 = _jspx_th_c_005fwhen_005f23.doAfterBody();
                                                                                } while (evalDoAfterBody11 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f23.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f23);
                                                                            _jspx_th_c_005fwhen_005f23_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f23, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f23_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f24 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f24_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f24.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f24.setParent((Tag)_jspx_th_c_005fchoose_005f10);
                                                                            _jspx_th_c_005fwhen_005f24.setTest((boolean)PageContextImpl.proprietaryEvaluate("${certExpired == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f24 = _jspx_th_c_005fwhen_005f24.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f24 != 0) {
                                                                                int evalDoAfterBody12;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("dc.common.msg.cert_expired.remark", new Object[0]));
                                                                                    out.write(" \n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody12 = _jspx_th_c_005fwhen_005f24.doAfterBody();
                                                                                } while (evalDoAfterBody12 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f24.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f24);
                                                                            _jspx_th_c_005fwhen_005f24_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f24, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f24_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody24 = _jspx_th_c_005fchoose_005f10.doAfterBody();
                                                                    } while (evalDoAfterBody24 == 2);
                                                                }
                                                                if (_jspx_th_c_005fchoose_005f10.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f10);
                                                                _jspx_th_c_005fchoose_005f10_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f10_reused);
                                                            }
                                                            out.write("\n\t\t\t\t\t\t\t\t\t");
                                                            evalDoAfterBody21 = _jspx_th_c_005fwhen_005f21.doAfterBody();
                                                        } while (evalDoAfterBody21 == 2);
                                                    }
                                                    if (_jspx_th_c_005fwhen_005f21.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f21);
                                                    _jspx_th_c_005fwhen_005f21_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f21_reused);
                                                }
                                                out.write("\n\t\t\t\t\t\t\t\t\t");
                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f8 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                boolean _jspx_th_c_005fotherwise_005f8_reused = false;
                                                try {
                                                    _jspx_th_c_005fotherwise_005f8.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fotherwise_005f8.setParent((Tag)_jspx_th_c_005fchoose_005f9);
                                                    final int _jspx_eval_c_005fotherwise_005f8 = _jspx_th_c_005fotherwise_005f8.doStartTag();
                                                    if (_jspx_eval_c_005fotherwise_005f8 != 0) {
                                                        int evalDoAfterBody9;
                                                        do {
                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                            final ChooseTag _jspx_th_c_005fchoose_005f11 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                            boolean _jspx_th_c_005fchoose_005f11_reused = false;
                                                            try {
                                                                _jspx_th_c_005fchoose_005f11.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fchoose_005f11.setParent((Tag)_jspx_th_c_005fotherwise_005f8);
                                                                final int _jspx_eval_c_005fchoose_005f11 = _jspx_th_c_005fchoose_005f11.doStartTag();
                                                                if (_jspx_eval_c_005fchoose_005f11 != 0) {
                                                                    int evalDoAfterBody25;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f25 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f25_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f25.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f25.setParent((Tag)_jspx_th_c_005fchoose_005f11);
                                                                            _jspx_th_c_005fwhen_005f25.setTest((boolean)PageContextImpl.proprietaryEvaluate("${hostNameIssue == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f25 = _jspx_th_c_005fwhen_005f25.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f25 != 0) {
                                                                                int evalDoAfterBody11;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("dc.common.msg.certificate_name_mismatch.remark", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody11 = _jspx_th_c_005fwhen_005f25.doAfterBody();
                                                                                } while (evalDoAfterBody11 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f25.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f25);
                                                                            _jspx_th_c_005fwhen_005f25_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f25, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f25_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f26 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f26_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f26.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f26.setParent((Tag)_jspx_th_c_005fchoose_005f11);
                                                                            _jspx_th_c_005fwhen_005f26.setTest((boolean)PageContextImpl.proprietaryEvaluate("${certChainIssue == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f26 = _jspx_th_c_005fwhen_005f26.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f26 != 0) {
                                                                                int evalDoAfterBody12;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("dc.common.msg.cert_chain_not_verified.title", new Object[0]));
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody12 = _jspx_th_c_005fwhen_005f26.doAfterBody();
                                                                                } while (evalDoAfterBody12 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f26.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f26);
                                                                            _jspx_th_c_005fwhen_005f26_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f26, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f26_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f27 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f27_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f27.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f27.setParent((Tag)_jspx_th_c_005fchoose_005f11);
                                                                            _jspx_th_c_005fwhen_005f27.setTest((boolean)PageContextImpl.proprietaryEvaluate("${certExpired == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f27 = _jspx_th_c_005fwhen_005f27.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f27 != 0) {
                                                                                int evalDoAfterBody13;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    out.print(I18N.getMsg("dc.common.msg.cert_expired.remark", new Object[0]));
                                                                                    out.write(" \n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody13 = _jspx_th_c_005fwhen_005f27.doAfterBody();
                                                                                } while (evalDoAfterBody13 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f27.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f27);
                                                                            _jspx_th_c_005fwhen_005f27_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f27, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f27_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody25 = _jspx_th_c_005fchoose_005f11.doAfterBody();
                                                                    } while (evalDoAfterBody25 == 2);
                                                                }
                                                                if (_jspx_th_c_005fchoose_005f11.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f11);
                                                                _jspx_th_c_005fchoose_005f11_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f11_reused);
                                                            }
                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                            out.print(I18N.getMsg("dc.common.contact_your_admin", new Object[0]));
                                                            out.write("\n\t\t\t\t\t\t\t\t\t");
                                                            evalDoAfterBody9 = _jspx_th_c_005fotherwise_005f8.doAfterBody();
                                                        } while (evalDoAfterBody9 == 2);
                                                    }
                                                    if (_jspx_th_c_005fotherwise_005f8.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f8);
                                                    _jspx_th_c_005fotherwise_005f8_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f8_reused);
                                                }
                                                out.write("\n\t\t\t\t\t\t\t\t");
                                                evalDoAfterBody26 = _jspx_th_c_005fchoose_005f9.doAfterBody();
                                            } while (evalDoAfterBody26 == 2);
                                        }
                                        if (_jspx_th_c_005fchoose_005f9.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f9);
                                        _jspx_th_c_005fchoose_005f9_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f9_reused);
                                    }
                                    out.write("\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t");
                                    evalDoAfterBody27 = _jspx_th_c_005fwhen_005f20.doAfterBody();
                                } while (evalDoAfterBody27 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f20.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f20);
                            _jspx_th_c_005fwhen_005f20_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f20, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f20_reused);
                        }
                        out.write("\n\t\t\t\t\t");
                        evalDoAfterBody28 = _jspx_th_c_005fchoose_005f4.doAfterBody();
                    } while (evalDoAfterBody28 == 2);
                }
                if (_jspx_th_c_005fchoose_005f4.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f4);
                _jspx_th_c_005fchoose_005f4_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f4_reused);
            }
            out.write("\n\t\t\t\t</div>\n            </div>\n            <div class=\"spacing\" style=\"padding-bottom:175px;\">&nbsp; </div>\n        </div>\n        <div id=\"footerGreen\" ");
            if (this._jspx_meth_c_005fchoose_005f12(_jspx_page_context)) {
                return;
            }
            out.write(">\n\t\t\t<p>&#169 ");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]));
            out.write("</p>\n\t\t</div>\n    </body>\n    <script src=\"/js/mdm/cookieCheck.js?91042\"></script>\n    <script src=\"/js/mdm/enrollRestrict.js\"></script>\n    <script>\n    var ua = navigator.userAgent.toLowerCase();\n\tvar uacomparator =  ");
            if (this._jspx_meth_c_005fchoose_005f13(_jspx_page_context)) {
                return;
            }
            out.write(59);
            out.write("\n\tvar usergent =  ");
            if (this._jspx_meth_c_005fchoose_005f14(_jspx_page_context)) {
                return;
            }
            out.write(59);
            out.write("\n    if (ua.indexOf(uacomparator) > -1) {\n        if(areCookiesEnabled(usergent))\n        {\nisServerTimeSynced(usergent,");
            out.write((String)PageContextImpl.proprietaryEvaluate("${CERTIFICATE_CREATION_DATE}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(");\n        }\n    }\n\n    </script>\n</html>\n");
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
            phoneInvalidBrowser_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
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
            if (_jspx_eval_c_005fchoose_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n\t\t\t\t\t");
                    if (this._jspx_meth_c_005fwhen_005f0((JspTag)_jspx_th_c_005fchoose_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n\t\t\t\t\t");
                    if (this._jspx_meth_c_005fotherwise_005f0((JspTag)_jspx_th_c_005fchoose_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n\t\t\t\t");
                    evalDoAfterBody = _jspx_th_c_005fchoose_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
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
                    out.write("\n\t\t\t\t\t\tfont-size:20px;\n\t\t\t\t\t\tpadding:40px 0px;\n\t\t\t\t\t");
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
                    out.write("\n\t\t\t\t\t\tfont-size:24px;\n\t\t\t\t\t\tpadding:40px 0px;\n\t\t\t\t\t");
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
    
    private boolean _jspx_meth_c_005fchoose_005f1(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f1 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f1_reused = false;
        try {
            _jspx_th_c_005fchoose_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f1.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f1 = _jspx_th_c_005fchoose_005f1.doStartTag();
            if (_jspx_eval_c_005fchoose_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n\t\t\t\t\t");
                    if (this._jspx_meth_c_005fwhen_005f1((JspTag)_jspx_th_c_005fchoose_005f1, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n\t\t\t\t\t");
                    if (this._jspx_meth_c_005fotherwise_005f1((JspTag)_jspx_th_c_005fchoose_005f1, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n\t\t\t\t");
                    evalDoAfterBody = _jspx_th_c_005fchoose_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fchoose_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f1);
            _jspx_th_c_005fchoose_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f1(final JspTag _jspx_th_c_005fchoose_005f1, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f1 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f1_reused = false;
        try {
            _jspx_th_c_005fwhen_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f1.setParent((Tag)_jspx_th_c_005fchoose_005f1);
            _jspx_th_c_005fwhen_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f1 = _jspx_th_c_005fwhen_005f1.doStartTag();
            if (_jspx_eval_c_005fwhen_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n\t\t\t\t\t\tfont-size:18px;\n\t\t\t\t\t\tline-height: 32px;\n\t\t\t\t\t");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f1);
            _jspx_th_c_005fwhen_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f1(final JspTag _jspx_th_c_005fchoose_005f1, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f1 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f1_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f1.setParent((Tag)_jspx_th_c_005fchoose_005f1);
            final int _jspx_eval_c_005fotherwise_005f1 = _jspx_th_c_005fotherwise_005f1.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n\t\t\t\t\t\tfont-size:22px;\n\t\t\t\t\t\tline-height: 18px;\n\t\t\t\t\t");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f1);
            _jspx_th_c_005fotherwise_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f1_reused);
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
                    out.write("class=\"displayContentIphone\"");
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
                    out.write("class=\"displayContent\"");
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
    
    private boolean _jspx_meth_c_005fchoose_005f3(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f3 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f3_reused = false;
        try {
            _jspx_th_c_005fchoose_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f3.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f3 = _jspx_th_c_005fchoose_005f3.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f3 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f3((JspTag)_jspx_th_c_005fchoose_005f3, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f3((JspTag)_jspx_th_c_005fchoose_005f3, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f3.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f3.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f3);
            _jspx_th_c_005fchoose_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f3_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f3(final JspTag _jspx_th_c_005fchoose_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f3 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f3_reused = false;
        try {
            _jspx_th_c_005fwhen_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f3);
            _jspx_th_c_005fwhen_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f3 = _jspx_th_c_005fwhen_005f3.doStartTag();
            if (_jspx_eval_c_005fwhen_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" class=\"displayTextIphone errorFontSizeBig\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f3.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f3.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f3);
            _jspx_th_c_005fwhen_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f3_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f3(final JspTag _jspx_th_c_005fchoose_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f3 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f3_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f3);
            final int _jspx_eval_c_005fotherwise_005f3 = _jspx_th_c_005fotherwise_005f3.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"displayText errorFontSizeBig\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f3.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f3.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fotherwise_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f3_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f12(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f12 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f12_reused = false;
        try {
            _jspx_th_c_005fchoose_005f12.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f12.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f12 = _jspx_th_c_005fchoose_005f12.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f12 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f28((JspTag)_jspx_th_c_005fchoose_005f12, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f9((JspTag)_jspx_th_c_005fchoose_005f12, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f12.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f12.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f12);
            _jspx_th_c_005fchoose_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f28(final JspTag _jspx_th_c_005fchoose_005f12, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f28 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f28_reused = false;
        try {
            _jspx_th_c_005fwhen_005f28.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f28.setParent((Tag)_jspx_th_c_005fchoose_005f12);
            _jspx_th_c_005fwhen_005f28.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f28 = _jspx_th_c_005fwhen_005f28.doStartTag();
            if (_jspx_eval_c_005fwhen_005f28 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"fotterDivFixedIphone fotterDivFixedIphoneBlue\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f28.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f28.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f28);
            _jspx_th_c_005fwhen_005f28_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f28, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f28_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f9(final JspTag _jspx_th_c_005fchoose_005f12, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f9 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f9_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f9.setParent((Tag)_jspx_th_c_005fchoose_005f12);
            final int _jspx_eval_c_005fotherwise_005f9 = _jspx_th_c_005fotherwise_005f9.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f9 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"fotterDiv fotterDivFixedIphoneBlue\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f9.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f9.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f9);
            _jspx_th_c_005fotherwise_005f9_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f9_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f13(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f13 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f13_reused = false;
        try {
            _jspx_th_c_005fchoose_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f13.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f13 = _jspx_th_c_005fchoose_005f13.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f13 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f29((JspTag)_jspx_th_c_005fchoose_005f13, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f10((JspTag)_jspx_th_c_005fchoose_005f13, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f13.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f13.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f13);
            _jspx_th_c_005fchoose_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f29(final JspTag _jspx_th_c_005fchoose_005f13, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f29 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f29_reused = false;
        try {
            _jspx_th_c_005fwhen_005f29.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f29.setParent((Tag)_jspx_th_c_005fchoose_005f13);
            _jspx_th_c_005fwhen_005f29.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f29 = _jspx_th_c_005fwhen_005f29.doStartTag();
            if (_jspx_eval_c_005fwhen_005f29 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'iphone'");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f29.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f29.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f29);
            _jspx_th_c_005fwhen_005f29_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f29, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f29_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f10(final JspTag _jspx_th_c_005fchoose_005f13, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f10 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f10_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f10.setParent((Tag)_jspx_th_c_005fchoose_005f13);
            final int _jspx_eval_c_005fotherwise_005f10 = _jspx_th_c_005fotherwise_005f10.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f10 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'ipad'");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f10.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f10.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f10);
            _jspx_th_c_005fotherwise_005f10_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f10_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f14(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f14 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f14_reused = false;
        try {
            _jspx_th_c_005fchoose_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f14.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f14 = _jspx_th_c_005fchoose_005f14.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f14 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f30((JspTag)_jspx_th_c_005fchoose_005f14, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f11((JspTag)_jspx_th_c_005fchoose_005f14, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f14.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f14.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f14);
            _jspx_th_c_005fchoose_005f14_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f14_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f30(final JspTag _jspx_th_c_005fchoose_005f14, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f30 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f30_reused = false;
        try {
            _jspx_th_c_005fwhen_005f30.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f30.setParent((Tag)_jspx_th_c_005fchoose_005f14);
            _jspx_th_c_005fwhen_005f30.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f30 = _jspx_th_c_005fwhen_005f30.doStartTag();
            if (_jspx_eval_c_005fwhen_005f30 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'phone'");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f30.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f30.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f30);
            _jspx_th_c_005fwhen_005f30_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f30, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f30_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f11(final JspTag _jspx_th_c_005fchoose_005f14, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f11 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f11_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f11.setParent((Tag)_jspx_th_c_005fchoose_005f14);
            final int _jspx_eval_c_005fotherwise_005f11 = _jspx_th_c_005fotherwise_005f11.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f11 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'tablet'");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f11.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f11);
            _jspx_th_c_005fotherwise_005f11_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f11_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (phoneInvalidBrowser_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        phoneInvalidBrowser_jsp._jspx_dependants.put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        phoneInvalidBrowser_jsp._jspx_imports_packages.add("javax.servlet.http");
        phoneInvalidBrowser_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        phoneInvalidBrowser_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

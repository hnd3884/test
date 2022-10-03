package org.apache.jsp.jsp.mdm.enroll;

import java.util.HashSet;
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
import javax.servlet.jsp.tagext.JspTag;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
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

public final class mdmWindowsEnroll_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmWindowsEnroll_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmWindowsEnroll_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmWindowsEnroll_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmWindowsEnroll_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
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
            final PageContext pageContext = _jspx_page_context = mdmWindowsEnroll_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n  \n  ");
            out.write("\n  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n  <link href=\"https://fonts.googleapis.com/css?family=Open+Sans:300,400,500,600,600i,700\" rel=\"stylesheet\">\n  <link href=\"/images/favicon.ico\" rel=\"icon\">\n  <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.windows_title", new Object[0]));
            out.write("</title>\n  <style type=\"text/css\">\n\n        ol li{\n          padding: 20px;\n          border-bottom: 1px dashed #b1b1b1;\n          margin-right: 30px;\n        }\n\n        img{\n          padding:20px 0;\n        }\n\n    body {\n      font-family: 'Open Sans', sans-serif;\n      font-size: 14px;\n      color: #333;\n      padding: 0px;\n      margin: 0px;\n      background: #f4f4f4;\n    }\n\n    @media screen and (max-width: 1080px) and (orientation: portrait) {\n    body {\n        font-size: 24px;\n    }\n\n    .topBar {\n      height: 120px !important;\n    }\n\n    .logo {\n      height: 70% !important;\n      padding: 20px;\n    }\n\n    .subTitle {\n      font-size: 40px !important;\n    }\n}\n\n    a {\n      text-decoration: none !important;\n      color: #0669ac\n    }\n    .logo {\n      height:65%;\n      padding:10px;\n    }\n\n    .topBar {\n      height: 70px;\n      border-bottom: 1.5px solid #B7B7B7;\n      box-shadow: 0px -3px 10px #000;\n      background: #fff;\n    }\n\n    .content {\n    max-width: 1000px;\n    width: 100%;\n    margin: 0 auto;\n");
            out.write("    background: #fff;\n    border: 1px solid #e2e2e2;\n    box-shadow: 0px 0px 20px -8px #333;\n    }\n\n    .content img {\n      width: 100%;\n    }\n\n    .subTitle {\n      text-align:center;\n      font-size:22px;\n      font-weight:600;\n      color:#333;\n      padding: 15px;\n      border-bottom: 1px solid #d6d6d6;\n      background: #3194da;\n      color: #fff;\n    }\n\t\n\t.deviceTypeStyle {\n\t\twidth : 60% !important;\n\t}\n    </style>\n    </head>\n\n\n    <body>\n        <div style=\"width:100%\" class=\"bodyText\">\n            <div class=\"topBar\">\n              <img class=\"logo\" src=\"/images/dm-default/dc-logo.gif\" />\n            </div>\n\t\t\t<div style=\"height: 1px;\"></div>\n            <div class=\"content\">\n\n\t\t\t\t<div class=\"subTitle\">\n\t\t\t\t\t<span>");
            out.print(I18N.getMsg("dc.mdm.enroll.enroll_info", new Object[0]));
            out.write("</span>\n\t\t\t\t</div>\n\n\n                ");
            final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f0_reused = false;
            try {
                _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    int evalDoAfterBody19;
                    do {
                        out.write("\n\t\t\t\t\t\n                    ");
                        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f0_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isWindowsAbove80 == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                                int evalDoAfterBody5;
                                do {
                                    out.write("\n                        <ol  class=\"bodyText\">\n                            <li>");
                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_comp_apps", new Object[0]));
                                    out.write("<br/><img height=\"35%\" width=\"95%\" alt=\"\" src=\"/images/windows_enroll_1.png\"/></li>\n                            <li>");
                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_add_account", new Object[0]));
                                    out.write("<br/><img class='");
                                    if (this._jspx_meth_c_005fif_005f0((JspTag)_jspx_th_c_005fwhen_005f0, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_2.png\"/></li>\n                                ");
                                    final ChooseTag _jspx_th_c_005fchoose_005f2 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                    boolean _jspx_th_c_005fchoose_005f1_reused = false;
                                    try {
                                        _jspx_th_c_005fchoose_005f2.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fchoose_005f2.setParent((Tag)_jspx_th_c_005fwhen_005f0);
                                        final int _jspx_eval_c_005fchoose_005f2 = _jspx_th_c_005fchoose_005f2.doStartTag();
                                        if (_jspx_eval_c_005fchoose_005f2 != 0) {
                                            int evalDoAfterBody4;
                                            do {
                                                out.write("\n                                    ");
                                                final WhenTag _jspx_th_c_005fwhen_005f2 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                boolean _jspx_th_c_005fwhen_005f1_reused = false;
                                                try {
                                                    _jspx_th_c_005fwhen_005f2.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fwhen_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                                                    _jspx_th_c_005fwhen_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode != null && authMode != 'OTP'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fwhen_005f2 = _jspx_th_c_005fwhen_005f2.doStartTag();
                                                    if (_jspx_eval_c_005fwhen_005f2 != 0) {
                                                        int evalDoAfterBody;
                                                        do {
                                                            out.write("\n                                    <li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_pass_AD", new Object[0]));
                                                            out.write("<br/><img alt=\"\" src=\"/images/windows_enroll_self_1.png\"/></li>\n                                    <li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_details_AD", new Object[0]));
                                                            out.write("<br/><img class='");
                                                            if (this._jspx_meth_c_005fif_005f1((JspTag)_jspx_th_c_005fwhen_005f2, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_self_2.png\"/></li>\n                                    ");
                                                            evalDoAfterBody = _jspx_th_c_005fwhen_005f2.doAfterBody();
                                                        } while (evalDoAfterBody == 2);
                                                    }
                                                    if (_jspx_th_c_005fwhen_005f2.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f2);
                                                    _jspx_th_c_005fwhen_005f1_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f1_reused);
                                                }
                                                out.write("\n                                    ");
                                                final WhenTag _jspx_th_c_005fwhen_005f3 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                boolean _jspx_th_c_005fwhen_005f2_reused = false;
                                                try {
                                                    _jspx_th_c_005fwhen_005f3.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fwhen_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                                                    _jspx_th_c_005fwhen_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fwhen_005f3 = _jspx_th_c_005fwhen_005f3.doStartTag();
                                                    if (_jspx_eval_c_005fwhen_005f3 != 0) {
                                                        int evalDoAfterBody2;
                                                        do {
                                                            out.write("\n                                    <li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_pass_AD", new Object[0]));
                                                            out.write("<br/><img class='");
                                                            if (this._jspx_meth_c_005fif_005f2((JspTag)_jspx_th_c_005fwhen_005f3, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_self_1.png\"/></li>\n                                    <li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.self_enroll.windows_enter_details_AD", new Object[] { request.getAttribute("serverUrl") }));
                                                            out.write("<br/><img class='");
                                                            if (this._jspx_meth_c_005fif_005f3((JspTag)_jspx_th_c_005fwhen_005f3, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_self_2.png\"/></li>\n                                    ");
                                                            evalDoAfterBody2 = _jspx_th_c_005fwhen_005f3.doAfterBody();
                                                        } while (evalDoAfterBody2 == 2);
                                                    }
                                                    if (_jspx_th_c_005fwhen_005f3.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f3);
                                                    _jspx_th_c_005fwhen_005f2_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f2_reused);
                                                }
                                                out.write("\n                                    ");
                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                boolean _jspx_th_c_005fotherwise_005f0_reused = false;
                                                try {
                                                    _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fotherwise_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                                                    final int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
                                                    if (_jspx_eval_c_005fotherwise_005f0 != 0) {
                                                        int evalDoAfterBody3;
                                                        do {
                                                            out.write("\n                                    <li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_pass_OTP", new Object[0]));
                                                            out.write("<br/><img class='");
                                                            if (this._jspx_meth_c_005fif_005f4((JspTag)_jspx_th_c_005fotherwise_005f0, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_OT_1.png\"/></li>\n                                    <li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_details_OTP", new Object[0]));
                                                            out.write("<br/><img class='");
                                                            if (this._jspx_meth_c_005fif_005f5((JspTag)_jspx_th_c_005fotherwise_005f0, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_OT_2.png\"/></li>\n                                    ");
                                                            evalDoAfterBody3 = _jspx_th_c_005fotherwise_005f0.doAfterBody();
                                                        } while (evalDoAfterBody3 == 2);
                                                    }
                                                    if (_jspx_th_c_005fotherwise_005f0.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f0);
                                                    _jspx_th_c_005fotherwise_005f0_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f0_reused);
                                                }
                                                out.write("\n                                ");
                                                evalDoAfterBody4 = _jspx_th_c_005fchoose_005f2.doAfterBody();
                                            } while (evalDoAfterBody4 == 2);
                                        }
                                        if (_jspx_th_c_005fchoose_005f2.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f2);
                                        _jspx_th_c_005fchoose_005f1_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f1_reused);
                                    }
                                    out.write("\n                            <li>");
                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_successfully_enrolled", new Object[0]));
                                    out.write("<br><img class='");
                                    if (this._jspx_meth_c_005fif_005f6((JspTag)_jspx_th_c_005fwhen_005f0, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_5.png\"/></li>\n                        </ol>\n                    ");
                                    evalDoAfterBody5 = _jspx_th_c_005fwhen_005f0.doAfterBody();
                                } while (evalDoAfterBody5 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f0.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f0);
                            _jspx_th_c_005fwhen_005f0_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f0_reused);
                        }
                        out.write("\n                    ");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f2 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f1_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f2.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            final int _jspx_eval_c_005fotherwise_005f2 = _jspx_th_c_005fotherwise_005f2.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f2 != 0) {
                                int evalDoAfterBody18;
                                do {
                                    out.write("\n\t\t\t\t\t\t");
                                    final ChooseTag _jspx_th_c_005fchoose_005f3 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                    boolean _jspx_th_c_005fchoose_005f2_reused = false;
                                    try {
                                        _jspx_th_c_005fchoose_005f3.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fchoose_005f3.setParent((Tag)_jspx_th_c_005fotherwise_005f2);
                                        final int _jspx_eval_c_005fchoose_005f3 = _jspx_th_c_005fchoose_005f3.doStartTag();
                                        if (_jspx_eval_c_005fchoose_005f3 != 0) {
                                            int evalDoAfterBody4;
                                            do {
                                                out.write("\n\t\t\t\t\t\t\t");
                                                final WhenTag _jspx_th_c_005fwhen_005f4 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                boolean _jspx_th_c_005fwhen_005f3_reused = false;
                                                try {
                                                    _jspx_th_c_005fwhen_005f4.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fwhen_005f4.setParent((Tag)_jspx_th_c_005fchoose_005f3);
                                                    _jspx_th_c_005fwhen_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isAppBasedEnrollment == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fwhen_005f4 = _jspx_th_c_005fwhen_005f4.doStartTag();
                                                    if (_jspx_eval_c_005fwhen_005f4 != 0) {
                                                        int evalDoAfterBody10;
                                                        do {
                                                            out.write("\n\t\t\t\t\t\t\t\t<ol  class=\"bodyText\">\n\t\t\t\t\t\t\t\t\t<li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_download_app", new Object[0]));
                                                            out.write("<a href=\"");
                                                            out.write((String)PageContextImpl.proprietaryEvaluate("${storeAppUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                            out.write("\">Download Here</a></li>");
                                                            out.write(" \n\t\t\t\t\t\t\t\t\t<li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_memdmapp", new Object[0]));
                                                            out.write("<br><img class='");
                                                            if (this._jspx_meth_c_005fif_005f7((JspTag)_jspx_th_c_005fwhen_005f4, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_app_0.png\"/></li>\n\t\t\t\t\t\t\t\t\t<li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_serverurl_email_port", new Object[0]));
                                                            out.write("<br><img class='");
                                                            if (this._jspx_meth_c_005fif_005f8((JspTag)_jspx_th_c_005fwhen_005f4, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_app_1.png\"/></li>\n\t\t\t\t\t\t\t\t\t");
                                                            final ChooseTag _jspx_th_c_005fchoose_005f4 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                            boolean _jspx_th_c_005fchoose_005f3_reused = false;
                                                            try {
                                                                _jspx_th_c_005fchoose_005f4.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fchoose_005f4.setParent((Tag)_jspx_th_c_005fwhen_005f4);
                                                                final int _jspx_eval_c_005fchoose_005f4 = _jspx_th_c_005fchoose_005f4.doStartTag();
                                                                if (_jspx_eval_c_005fchoose_005f4 != 0) {
                                                                    int evalDoAfterBody9;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f5 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f4_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f5.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                                                                            _jspx_th_c_005fwhen_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode == 'OTP'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f5 = _jspx_th_c_005fwhen_005f5.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f5 != 0) {
                                                                                int evalDoAfterBody6;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_otp", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f9((JspTag)_jspx_th_c_005fwhen_005f5, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_2_OTP.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody6 = _jspx_th_c_005fwhen_005f5.doAfterBody();
                                                                                } while (evalDoAfterBody6 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f5.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f5);
                                                                            _jspx_th_c_005fwhen_005f4_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f4_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f6 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f5_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f6.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                                                                            _jspx_th_c_005fwhen_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode == 'ActiveDirectory' || isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f6 = _jspx_th_c_005fwhen_005f6.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f6 != 0) {
                                                                                int evalDoAfterBody7;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_AD", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f10((JspTag)_jspx_th_c_005fwhen_005f6, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_2_AD.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody7 = _jspx_th_c_005fwhen_005f6.doAfterBody();
                                                                                } while (evalDoAfterBody7 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f6.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f6);
                                                                            _jspx_th_c_005fwhen_005f5_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f5_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final OtherwiseTag _jspx_th_c_005fotherwise_005f3 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                        boolean _jspx_th_c_005fotherwise_005f2_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fotherwise_005f3.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fotherwise_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                                                                            final int _jspx_eval_c_005fotherwise_005f3 = _jspx_th_c_005fotherwise_005f3.doStartTag();
                                                                            if (_jspx_eval_c_005fotherwise_005f3 != 0) {
                                                                                int evalDoAfterBody8;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_OTP_AD", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f11((JspTag)_jspx_th_c_005fotherwise_005f3, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_2_TwoFac.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody8 = _jspx_th_c_005fotherwise_005f3.doAfterBody();
                                                                                } while (evalDoAfterBody8 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fotherwise_005f3.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f3);
                                                                            _jspx_th_c_005fotherwise_005f2_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f2_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody9 = _jspx_th_c_005fchoose_005f4.doAfterBody();
                                                                    } while (evalDoAfterBody9 == 2);
                                                                }
                                                                if (_jspx_th_c_005fchoose_005f4.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f4);
                                                                _jspx_th_c_005fchoose_005f3_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f3_reused);
                                                            }
                                                            out.write("\n\t\t\t\t\t\t\t\t\t<li>");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_copy_server_url", new Object[0]));
                                                            out.write("<br><img class='");
                                                            if (this._jspx_meth_c_005fif_005f12((JspTag)_jspx_th_c_005fwhen_005f4, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_app_3.png\"/></li>\n\t\t\t\t\t\t\t\t\t");
                                                            final ChooseTag _jspx_th_c_005fchoose_005f5 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                            boolean _jspx_th_c_005fchoose_005f4_reused = false;
                                                            try {
                                                                _jspx_th_c_005fchoose_005f5.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fchoose_005f5.setParent((Tag)_jspx_th_c_005fwhen_005f4);
                                                                final int _jspx_eval_c_005fchoose_005f5 = _jspx_th_c_005fchoose_005f5.doStartTag();
                                                                if (_jspx_eval_c_005fchoose_005f5 != 0) {
                                                                    int evalDoAfterBody9;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f7 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f6_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f7.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f7.setParent((Tag)_jspx_th_c_005fchoose_005f5);
                                                                            _jspx_th_c_005fwhen_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isWindows10OrAbove == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f7 = _jspx_th_c_005fwhen_005f7.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f7 != 0) {
                                                                                int evalDoAfterBody7;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows10_enrollDeviceManagement", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f13((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10_enroll_22.png\" /></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_email_only", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f14((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10_enroll_23.png\" /></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_paste_serverurl", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f15((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_enroll_client_token", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f16((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_4.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_paste_client_token", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f17((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10_enroll_OT_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_successfully_enrolled", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f18((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10_enroll_25.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_back_to_app", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f19((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_5.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_complete_enrollment", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f20((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_4.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody7 = _jspx_th_c_005fwhen_005f7.doAfterBody();
                                                                                } while (evalDoAfterBody7 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f7.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f7);
                                                                            _jspx_th_c_005fwhen_005f6_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f6_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t");
                                                                        final OtherwiseTag _jspx_th_c_005fotherwise_005f4 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                        boolean _jspx_th_c_005fotherwise_005f3_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fotherwise_005f4.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fotherwise_005f4.setParent((Tag)_jspx_th_c_005fchoose_005f5);
                                                                            final int _jspx_eval_c_005fotherwise_005f4 = _jspx_th_c_005fotherwise_005f4.doStartTag();
                                                                            if (_jspx_eval_c_005fotherwise_005f4 != 0) {
                                                                                int evalDoAfterBody8;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_email_only", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f21((JspTag)_jspx_th_c_005fotherwise_005f4, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_23.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_paste_serverurl", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f22((JspTag)_jspx_th_c_005fotherwise_005f4, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_enroll_client_token", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f23((JspTag)_jspx_th_c_005fotherwise_005f4, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_4.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_paste_client_token", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f24((JspTag)_jspx_th_c_005fotherwise_005f4, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_OT_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_successfully_enrolled", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f25((JspTag)_jspx_th_c_005fotherwise_005f4, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_25.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_back_to_app", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f26((JspTag)_jspx_th_c_005fotherwise_005f4, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_5.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_complete_enrollment", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f27((JspTag)_jspx_th_c_005fotherwise_005f4, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_app_4.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody8 = _jspx_th_c_005fotherwise_005f4.doAfterBody();
                                                                                } while (evalDoAfterBody8 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fotherwise_005f4.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f4);
                                                                            _jspx_th_c_005fotherwise_005f3_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f3_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody9 = _jspx_th_c_005fchoose_005f5.doAfterBody();
                                                                    } while (evalDoAfterBody9 == 2);
                                                                }
                                                                if (_jspx_th_c_005fchoose_005f5.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f5);
                                                                _jspx_th_c_005fchoose_005f4_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f4_reused);
                                                            }
                                                            out.write("\n\t\t\t\t\t\t\t\t</ol>\n\t\t\t\t\t\t\t");
                                                            evalDoAfterBody10 = _jspx_th_c_005fwhen_005f4.doAfterBody();
                                                        } while (evalDoAfterBody10 == 2);
                                                    }
                                                    if (_jspx_th_c_005fwhen_005f4.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f4);
                                                    _jspx_th_c_005fwhen_005f3_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f3_reused);
                                                }
                                                out.write("\n\t\t\t\t\t\t\t");
                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f5 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                boolean _jspx_th_c_005fotherwise_005f4_reused = false;
                                                try {
                                                    _jspx_th_c_005fotherwise_005f5.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fotherwise_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f3);
                                                    final int _jspx_eval_c_005fotherwise_005f5 = _jspx_th_c_005fotherwise_005f5.doStartTag();
                                                    if (_jspx_eval_c_005fotherwise_005f5 != 0) {
                                                        int evalDoAfterBody10;
                                                        do {
                                                            out.write("\n\t\t\t\t\t\t\t\t");
                                                            final ChooseTag _jspx_th_c_005fchoose_005f6 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                            boolean _jspx_th_c_005fchoose_005f5_reused = false;
                                                            try {
                                                                _jspx_th_c_005fchoose_005f6.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fchoose_005f6.setParent((Tag)_jspx_th_c_005fotherwise_005f5);
                                                                final int _jspx_eval_c_005fchoose_005f6 = _jspx_th_c_005fchoose_005f6.doStartTag();
                                                                if (_jspx_eval_c_005fchoose_005f6 != 0) {
                                                                    int evalDoAfterBody9;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                        final WhenTag _jspx_th_c_005fwhen_005f8 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                        boolean _jspx_th_c_005fwhen_005f7_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fwhen_005f8.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fwhen_005f8.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            _jspx_th_c_005fwhen_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isWindows10OrAbove == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                            final int _jspx_eval_c_005fwhen_005f8 = _jspx_th_c_005fwhen_005f8.doStartTag();
                                                                            if (_jspx_eval_c_005fwhen_005f8 != 0) {
                                                                                int evalDoAfterBody8;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t<ol  class=\"bodyText\">\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows10_accounts", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f28((JspTag)_jspx_th_c_005fwhen_005f8, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                    out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                    out.write("_enroll_21_1.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows10_workaccess", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f29((JspTag)_jspx_th_c_005fwhen_005f8, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                    out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                    out.write("_enroll_21_2.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows10_enrollDeviceManagement", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f30((JspTag)_jspx_th_c_005fwhen_005f8, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                    out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                    out.write("_enroll_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_email_only", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f31((JspTag)_jspx_th_c_005fwhen_005f8, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                    out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                    out.write("_enroll_23.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    final ChooseTag _jspx_th_c_005fchoose_005f7 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                                                    boolean _jspx_th_c_005fchoose_005f6_reused = false;
                                                                                    try {
                                                                                        _jspx_th_c_005fchoose_005f7.setPageContext(_jspx_page_context);
                                                                                        _jspx_th_c_005fchoose_005f7.setParent((Tag)_jspx_th_c_005fwhen_005f8);
                                                                                        final int _jspx_eval_c_005fchoose_005f7 = _jspx_th_c_005fchoose_005f7.doStartTag();
                                                                                        if (_jspx_eval_c_005fchoose_005f7 != 0) {
                                                                                            int evalDoAfterBody14;
                                                                                            do {
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                final WhenTag _jspx_th_c_005fwhen_005f9 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                                                boolean _jspx_th_c_005fwhen_005f8_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fwhen_005f9.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fwhen_005f9.setParent((Tag)_jspx_th_c_005fchoose_005f7);
                                                                                                    _jspx_th_c_005fwhen_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode != null && authMode != 'OTP'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                    final int _jspx_eval_c_005fwhen_005f9 = _jspx_th_c_005fwhen_005f9.doStartTag();
                                                                                                    if (_jspx_eval_c_005fwhen_005f9 != 0) {
                                                                                                        int evalDoAfterBody11;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_serverurl", new Object[] { request.getAttribute("serverUrl") }));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f32((JspTag)_jspx_th_c_005fwhen_005f9, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                                            out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                            out.write("_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_user_domain_AD", new Object[0]));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f33((JspTag)_jspx_th_c_005fwhen_005f9, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                                            out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                            out.write("_enroll_self_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody11 = _jspx_th_c_005fwhen_005f9.doAfterBody();
                                                                                                        } while (evalDoAfterBody11 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fwhen_005f9.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f9);
                                                                                                    _jspx_th_c_005fwhen_005f8_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f8_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                final WhenTag _jspx_th_c_005fwhen_005f10 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                                                boolean _jspx_th_c_005fwhen_005f9_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fwhen_005f10.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fwhen_005f10.setParent((Tag)_jspx_th_c_005fchoose_005f7);
                                                                                                    _jspx_th_c_005fwhen_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                    final int _jspx_eval_c_005fwhen_005f10 = _jspx_th_c_005fwhen_005f10.doStartTag();
                                                                                                    if (_jspx_eval_c_005fwhen_005f10 != 0) {
                                                                                                        int evalDoAfterBody12;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_serverurl", new Object[] { request.getAttribute("serverUrl") }));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f34((JspTag)_jspx_th_c_005fwhen_005f10, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                                            out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                            out.write("_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_user_domain_AD", new Object[0]));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f35((JspTag)_jspx_th_c_005fwhen_005f10, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                                            out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                            out.write("_enroll_self_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody12 = _jspx_th_c_005fwhen_005f10.doAfterBody();
                                                                                                        } while (evalDoAfterBody12 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fwhen_005f10.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f10);
                                                                                                    _jspx_th_c_005fwhen_005f9_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f9_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f6 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                                                boolean _jspx_th_c_005fotherwise_005f5_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fotherwise_005f6.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fotherwise_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f7);
                                                                                                    final int _jspx_eval_c_005fotherwise_005f6 = _jspx_th_c_005fotherwise_005f6.doStartTag();
                                                                                                    if (_jspx_eval_c_005fotherwise_005f6 != 0) {
                                                                                                        int evalDoAfterBody13;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_serverurl", new Object[] { request.getAttribute("serverUrl") }));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f36((JspTag)_jspx_th_c_005fotherwise_005f6, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                                            out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                            out.write("_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_details_OTP_8", new Object[0]));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f37((JspTag)_jspx_th_c_005fotherwise_005f6, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                                            out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                            out.write("_enroll_OT_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody13 = _jspx_th_c_005fotherwise_005f6.doAfterBody();
                                                                                                        } while (evalDoAfterBody13 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fotherwise_005f6.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f6);
                                                                                                    _jspx_th_c_005fotherwise_005f5_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f5_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                evalDoAfterBody14 = _jspx_th_c_005fchoose_005f7.doAfterBody();
                                                                                            } while (evalDoAfterBody14 == 2);
                                                                                        }
                                                                                        if (_jspx_th_c_005fchoose_005f7.doEndTag() == 5) {
                                                                                            return;
                                                                                        }
                                                                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f7);
                                                                                        _jspx_th_c_005fchoose_005f6_reused = true;
                                                                                    }
                                                                                    finally {
                                                                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f6_reused);
                                                                                    }
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_successfully_enrolled", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f38((JspTag)_jspx_th_c_005fwhen_005f8, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows10");
                                                                                    out.write((String)PageContextImpl.proprietaryEvaluate("${imageFileSuffix}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                    out.write("_enroll_25.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t</ol>\n\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody8 = _jspx_th_c_005fwhen_005f8.doAfterBody();
                                                                                } while (evalDoAfterBody8 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fwhen_005f8.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f8);
                                                                            _jspx_th_c_005fwhen_005f7_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f7_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t");
                                                                        final OtherwiseTag _jspx_th_c_005fotherwise_005f7 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                        boolean _jspx_th_c_005fotherwise_005f6_reused = false;
                                                                        try {
                                                                            _jspx_th_c_005fotherwise_005f7.setPageContext(_jspx_page_context);
                                                                            _jspx_th_c_005fotherwise_005f7.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                                            final int _jspx_eval_c_005fotherwise_005f7 = _jspx_th_c_005fotherwise_005f7.doStartTag();
                                                                            if (_jspx_eval_c_005fotherwise_005f7 != 0) {
                                                                                int evalDoAfterBody17;
                                                                                do {
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t<ol  class=\"bodyText\">\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_workplace", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f39((JspTag)_jspx_th_c_005fotherwise_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_21.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_add_account", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f40((JspTag)_jspx_th_c_005fotherwise_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_email_only", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f41((JspTag)_jspx_th_c_005fotherwise_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_23.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                    final ChooseTag _jspx_th_c_005fchoose_005f8 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                                                    boolean _jspx_th_c_005fchoose_005f7_reused = false;
                                                                                    try {
                                                                                        _jspx_th_c_005fchoose_005f8.setPageContext(_jspx_page_context);
                                                                                        _jspx_th_c_005fchoose_005f8.setParent((Tag)_jspx_th_c_005fotherwise_005f7);
                                                                                        final int _jspx_eval_c_005fchoose_005f8 = _jspx_th_c_005fchoose_005f8.doStartTag();
                                                                                        if (_jspx_eval_c_005fchoose_005f8 != 0) {
                                                                                            int evalDoAfterBody16;
                                                                                            do {
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                final WhenTag _jspx_th_c_005fwhen_005f11 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                                                boolean _jspx_th_c_005fwhen_005f10_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fwhen_005f11.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fwhen_005f11.setParent((Tag)_jspx_th_c_005fchoose_005f8);
                                                                                                    _jspx_th_c_005fwhen_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode != null && authMode != 'OTP'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                    final int _jspx_eval_c_005fwhen_005f11 = _jspx_th_c_005fwhen_005f11.doStartTag();
                                                                                                    if (_jspx_eval_c_005fwhen_005f11 != 0) {
                                                                                                        int evalDoAfterBody12;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_serverurl", new Object[] { request.getAttribute("serverUrl") }));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f42((JspTag)_jspx_th_c_005fwhen_005f11, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_user_domain_AD", new Object[0]));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f43((JspTag)_jspx_th_c_005fwhen_005f11, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_self_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody12 = _jspx_th_c_005fwhen_005f11.doAfterBody();
                                                                                                        } while (evalDoAfterBody12 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fwhen_005f11.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f11);
                                                                                                    _jspx_th_c_005fwhen_005f10_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f10_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                final WhenTag _jspx_th_c_005fwhen_005f12 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                                                                boolean _jspx_th_c_005fwhen_005f11_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fwhen_005f12.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fwhen_005f12.setParent((Tag)_jspx_th_c_005fchoose_005f8);
                                                                                                    _jspx_th_c_005fwhen_005f12.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                                                                    final int _jspx_eval_c_005fwhen_005f12 = _jspx_th_c_005fwhen_005f12.doStartTag();
                                                                                                    if (_jspx_eval_c_005fwhen_005f12 != 0) {
                                                                                                        int evalDoAfterBody13;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_serverurl", new Object[] { request.getAttribute("serverUrl") }));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f44((JspTag)_jspx_th_c_005fwhen_005f12, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_user_domain_AD", new Object[0]));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f45((JspTag)_jspx_th_c_005fwhen_005f12, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_self_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody13 = _jspx_th_c_005fwhen_005f12.doAfterBody();
                                                                                                        } while (evalDoAfterBody13 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fwhen_005f12.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f12);
                                                                                                    _jspx_th_c_005fwhen_005f11_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f11_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f8 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                                                                boolean _jspx_th_c_005fotherwise_005f7_reused = false;
                                                                                                try {
                                                                                                    _jspx_th_c_005fotherwise_005f8.setPageContext(_jspx_page_context);
                                                                                                    _jspx_th_c_005fotherwise_005f8.setParent((Tag)_jspx_th_c_005fchoose_005f8);
                                                                                                    final int _jspx_eval_c_005fotherwise_005f8 = _jspx_th_c_005fotherwise_005f8.doStartTag();
                                                                                                    if (_jspx_eval_c_005fotherwise_005f8 != 0) {
                                                                                                        int evalDoAfterBody15;
                                                                                                        do {
                                                                                                            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_email_serverurl", new Object[] { request.getAttribute("serverUrl") }));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f46((JspTag)_jspx_th_c_005fotherwise_005f8, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_24.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                                            out.print(I18N.getMsg("dc.mdm.enroll.windows_enter_details_OTP_8", new Object[0]));
                                                                                                            out.write("<br><img class='");
                                                                                                            if (this._jspx_meth_c_005fif_005f47((JspTag)_jspx_th_c_005fotherwise_005f8, _jspx_page_context)) {
                                                                                                                return;
                                                                                                            }
                                                                                                            out.write("'  alt=\"\" src=\"/images/windows_enroll_OT_22.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                            evalDoAfterBody15 = _jspx_th_c_005fotherwise_005f8.doAfterBody();
                                                                                                        } while (evalDoAfterBody15 == 2);
                                                                                                    }
                                                                                                    if (_jspx_th_c_005fotherwise_005f8.doEndTag() == 5) {
                                                                                                        return;
                                                                                                    }
                                                                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f8);
                                                                                                    _jspx_th_c_005fotherwise_005f7_reused = true;
                                                                                                }
                                                                                                finally {
                                                                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f7_reused);
                                                                                                }
                                                                                                out.write("\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                                                evalDoAfterBody16 = _jspx_th_c_005fchoose_005f8.doAfterBody();
                                                                                            } while (evalDoAfterBody16 == 2);
                                                                                        }
                                                                                        if (_jspx_th_c_005fchoose_005f8.doEndTag() == 5) {
                                                                                            return;
                                                                                        }
                                                                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f8);
                                                                                        _jspx_th_c_005fchoose_005f7_reused = true;
                                                                                    }
                                                                                    finally {
                                                                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f7_reused);
                                                                                    }
                                                                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t<li>");
                                                                                    out.print(I18N.getMsg("dc.mdm.enroll.windows_successfully_enrolled", new Object[0]));
                                                                                    out.write("<br><img class='");
                                                                                    if (this._jspx_meth_c_005fif_005f48((JspTag)_jspx_th_c_005fotherwise_005f7, _jspx_page_context)) {
                                                                                        return;
                                                                                    }
                                                                                    out.write("'  alt=\"\" src=\"/images/windows_enroll_25.png\"/></li>\n\t\t\t\t\t\t\t\t\t\t</ol>\n\t\t\t\t\t\t\t\t\t");
                                                                                    evalDoAfterBody17 = _jspx_th_c_005fotherwise_005f7.doAfterBody();
                                                                                } while (evalDoAfterBody17 == 2);
                                                                            }
                                                                            if (_jspx_th_c_005fotherwise_005f7.doEndTag() == 5) {
                                                                                return;
                                                                            }
                                                                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f7);
                                                                            _jspx_th_c_005fotherwise_005f6_reused = true;
                                                                        }
                                                                        finally {
                                                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f6_reused);
                                                                        }
                                                                        out.write("\n\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody9 = _jspx_th_c_005fchoose_005f6.doAfterBody();
                                                                    } while (evalDoAfterBody9 == 2);
                                                                }
                                                                if (_jspx_th_c_005fchoose_005f6.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f6);
                                                                _jspx_th_c_005fchoose_005f5_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f5_reused);
                                                            }
                                                            out.write("\n\t\t\t\t\t\t\t");
                                                            evalDoAfterBody10 = _jspx_th_c_005fotherwise_005f5.doAfterBody();
                                                        } while (evalDoAfterBody10 == 2);
                                                    }
                                                    if (_jspx_th_c_005fotherwise_005f5.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f5);
                                                    _jspx_th_c_005fotherwise_005f4_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f4_reused);
                                                }
                                                out.write("\n\t\t\t\t\t\t");
                                                evalDoAfterBody4 = _jspx_th_c_005fchoose_005f3.doAfterBody();
                                            } while (evalDoAfterBody4 == 2);
                                        }
                                        if (_jspx_th_c_005fchoose_005f3.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f3);
                                        _jspx_th_c_005fchoose_005f2_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f2_reused);
                                    }
                                    out.write("\n                    ");
                                    evalDoAfterBody18 = _jspx_th_c_005fotherwise_005f2.doAfterBody();
                                } while (evalDoAfterBody18 == 2);
                            }
                            if (_jspx_th_c_005fotherwise_005f2.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f2);
                            _jspx_th_c_005fotherwise_005f1_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f1_reused);
                        }
                        out.write("\n                ");
                        evalDoAfterBody19 = _jspx_th_c_005fchoose_005f0.doAfterBody();
                    } while (evalDoAfterBody19 == 2);
                }
                if (_jspx_th_c_005fchoose_005f0.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f0);
                _jspx_th_c_005fchoose_005f0_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f0_reused);
            }
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
            mdmWindowsEnroll_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_c_005fif_005f0(final JspTag _jspx_th_c_005fwhen_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f0_reused = false;
        try {
            _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_c_005fwhen_005f0);
            _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
            if (_jspx_eval_c_005fif_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
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
    
    private boolean _jspx_meth_c_005fif_005f1(final JspTag _jspx_th_c_005fwhen_005f1, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f1 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f1_reused = false;
        try {
            _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f1.setParent((Tag)_jspx_th_c_005fwhen_005f1);
            _jspx_th_c_005fif_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
            if (_jspx_eval_c_005fif_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f1);
            _jspx_th_c_005fif_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f2(final JspTag _jspx_th_c_005fwhen_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f2_reused = false;
        try {
            _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f2.setParent((Tag)_jspx_th_c_005fwhen_005f2);
            _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
            if (_jspx_eval_c_005fif_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f2.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f2);
            _jspx_th_c_005fif_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f3(final JspTag _jspx_th_c_005fwhen_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f3_reused = false;
        try {
            _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f3.setParent((Tag)_jspx_th_c_005fwhen_005f2);
            _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
            if (_jspx_eval_c_005fif_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f3.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f3.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f3);
            _jspx_th_c_005fif_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f3_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f4(final JspTag _jspx_th_c_005fotherwise_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f4 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f4_reused = false;
        try {
            _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f4.setParent((Tag)_jspx_th_c_005fotherwise_005f0);
            _jspx_th_c_005fif_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
            if (_jspx_eval_c_005fif_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f4.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f4);
            _jspx_th_c_005fif_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f5(final JspTag _jspx_th_c_005fotherwise_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f5 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f5_reused = false;
        try {
            _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f5.setParent((Tag)_jspx_th_c_005fotherwise_005f0);
            _jspx_th_c_005fif_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
            if (_jspx_eval_c_005fif_005f5 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f5.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f5.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fif_005f5_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f5_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f6(final JspTag _jspx_th_c_005fwhen_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f6_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_c_005fwhen_005f0);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
            if (_jspx_eval_c_005fif_005f6 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
            _jspx_th_c_005fif_005f6_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f6_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f7(final JspTag _jspx_th_c_005fwhen_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f7 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f7_reused = false;
        try {
            _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f7.setParent((Tag)_jspx_th_c_005fwhen_005f3);
            _jspx_th_c_005fif_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
            if (_jspx_eval_c_005fif_005f7 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f7.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f7.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f7);
            _jspx_th_c_005fif_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f8(final JspTag _jspx_th_c_005fwhen_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f8 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f8_reused = false;
        try {
            _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f8.setParent((Tag)_jspx_th_c_005fwhen_005f3);
            _jspx_th_c_005fif_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
            if (_jspx_eval_c_005fif_005f8 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f8.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f8.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f8);
            _jspx_th_c_005fif_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f9(final JspTag _jspx_th_c_005fwhen_005f4, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f9 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f9_reused = false;
        try {
            _jspx_th_c_005fif_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f9.setParent((Tag)_jspx_th_c_005fwhen_005f4);
            _jspx_th_c_005fif_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f9.doStartTag();
            if (_jspx_eval_c_005fif_005f9 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f9.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f9.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f9);
            _jspx_th_c_005fif_005f9_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f9_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f10(final JspTag _jspx_th_c_005fwhen_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f10 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f10_reused = false;
        try {
            _jspx_th_c_005fif_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f10.setParent((Tag)_jspx_th_c_005fwhen_005f5);
            _jspx_th_c_005fif_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f10 = _jspx_th_c_005fif_005f10.doStartTag();
            if (_jspx_eval_c_005fif_005f10 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f10.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f10.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f10_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f10_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f11(final JspTag _jspx_th_c_005fotherwise_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f11_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fotherwise_005f2);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f11 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f11.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
            _jspx_th_c_005fif_005f11_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f11_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f12(final JspTag _jspx_th_c_005fwhen_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f12 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f12_reused = false;
        try {
            _jspx_th_c_005fif_005f12.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f12.setParent((Tag)_jspx_th_c_005fwhen_005f3);
            _jspx_th_c_005fif_005f12.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f12 = _jspx_th_c_005fif_005f12.doStartTag();
            if (_jspx_eval_c_005fif_005f12 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f12.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f12.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f12);
            _jspx_th_c_005fif_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f13(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f13 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f13_reused = false;
        try {
            _jspx_th_c_005fif_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f13.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f13.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f13 = _jspx_th_c_005fif_005f13.doStartTag();
            if (_jspx_eval_c_005fif_005f13 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f13.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f13.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f13);
            _jspx_th_c_005fif_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f14(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f14 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f14_reused = false;
        try {
            _jspx_th_c_005fif_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f14.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f14.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f14 = _jspx_th_c_005fif_005f14.doStartTag();
            if (_jspx_eval_c_005fif_005f14 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f14.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f14.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f14);
            _jspx_th_c_005fif_005f14_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f14_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f15(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f15 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f15_reused = false;
        try {
            _jspx_th_c_005fif_005f15.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f15.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f15.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f15 = _jspx_th_c_005fif_005f15.doStartTag();
            if (_jspx_eval_c_005fif_005f15 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f15.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f15.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fif_005f15_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f15_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f16(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f16 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f16_reused = false;
        try {
            _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f16.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f16 = _jspx_th_c_005fif_005f16.doStartTag();
            if (_jspx_eval_c_005fif_005f16 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f16.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f16);
            _jspx_th_c_005fif_005f16_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f16_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f17(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f17 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f17_reused = false;
        try {
            _jspx_th_c_005fif_005f17.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f17.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f17.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f17 = _jspx_th_c_005fif_005f17.doStartTag();
            if (_jspx_eval_c_005fif_005f17 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f17.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f17.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f17);
            _jspx_th_c_005fif_005f17_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f17, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f17_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f18(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f18 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f18_reused = false;
        try {
            _jspx_th_c_005fif_005f18.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f18.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f18.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f18 = _jspx_th_c_005fif_005f18.doStartTag();
            if (_jspx_eval_c_005fif_005f18 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f18.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f18.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f18);
            _jspx_th_c_005fif_005f18_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f18, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f18_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f19(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f19 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f19_reused = false;
        try {
            _jspx_th_c_005fif_005f19.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f19.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f19.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f19 = _jspx_th_c_005fif_005f19.doStartTag();
            if (_jspx_eval_c_005fif_005f19 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f19.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f19.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f19);
            _jspx_th_c_005fif_005f19_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f19_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f20(final JspTag _jspx_th_c_005fwhen_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f20 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f20_reused = false;
        try {
            _jspx_th_c_005fif_005f20.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f20.setParent((Tag)_jspx_th_c_005fwhen_005f6);
            _jspx_th_c_005fif_005f20.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f20 = _jspx_th_c_005fif_005f20.doStartTag();
            if (_jspx_eval_c_005fif_005f20 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f20.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f20.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fif_005f20_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f20, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f20_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f21(final JspTag _jspx_th_c_005fotherwise_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f21 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f21_reused = false;
        try {
            _jspx_th_c_005fif_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f21.setParent((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fif_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f21 = _jspx_th_c_005fif_005f21.doStartTag();
            if (_jspx_eval_c_005fif_005f21 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f21.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f21.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f21);
            _jspx_th_c_005fif_005f21_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f21_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f22(final JspTag _jspx_th_c_005fotherwise_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f22 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f22_reused = false;
        try {
            _jspx_th_c_005fif_005f22.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f22.setParent((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fif_005f22.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f22 = _jspx_th_c_005fif_005f22.doStartTag();
            if (_jspx_eval_c_005fif_005f22 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f22.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f22.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f22);
            _jspx_th_c_005fif_005f22_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f22, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f22_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f23(final JspTag _jspx_th_c_005fotherwise_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f23 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f23_reused = false;
        try {
            _jspx_th_c_005fif_005f23.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f23.setParent((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fif_005f23.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f23 = _jspx_th_c_005fif_005f23.doStartTag();
            if (_jspx_eval_c_005fif_005f23 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f23.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f23.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f23);
            _jspx_th_c_005fif_005f23_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f23, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f23_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f24(final JspTag _jspx_th_c_005fotherwise_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f24 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f24_reused = false;
        try {
            _jspx_th_c_005fif_005f24.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f24.setParent((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fif_005f24.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f24 = _jspx_th_c_005fif_005f24.doStartTag();
            if (_jspx_eval_c_005fif_005f24 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f24.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f24.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f24);
            _jspx_th_c_005fif_005f24_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f24, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f24_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f25(final JspTag _jspx_th_c_005fotherwise_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f25 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f25_reused = false;
        try {
            _jspx_th_c_005fif_005f25.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f25.setParent((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fif_005f25.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f25 = _jspx_th_c_005fif_005f25.doStartTag();
            if (_jspx_eval_c_005fif_005f25 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f25.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f25.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fif_005f25_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f25, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f25_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f26(final JspTag _jspx_th_c_005fotherwise_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f26 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f26_reused = false;
        try {
            _jspx_th_c_005fif_005f26.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f26.setParent((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fif_005f26.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f26 = _jspx_th_c_005fif_005f26.doStartTag();
            if (_jspx_eval_c_005fif_005f26 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f26.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f26.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f26);
            _jspx_th_c_005fif_005f26_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f26, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f26_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f27(final JspTag _jspx_th_c_005fotherwise_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f27 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f27_reused = false;
        try {
            _jspx_th_c_005fif_005f27.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f27.setParent((Tag)_jspx_th_c_005fotherwise_005f3);
            _jspx_th_c_005fif_005f27.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f27 = _jspx_th_c_005fif_005f27.doStartTag();
            if (_jspx_eval_c_005fif_005f27 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f27.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f27.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f27);
            _jspx_th_c_005fif_005f27_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f27, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f27_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f28(final JspTag _jspx_th_c_005fwhen_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f28 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f28_reused = false;
        try {
            _jspx_th_c_005fif_005f28.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f28.setParent((Tag)_jspx_th_c_005fwhen_005f7);
            _jspx_th_c_005fif_005f28.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f28 = _jspx_th_c_005fif_005f28.doStartTag();
            if (_jspx_eval_c_005fif_005f28 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f28.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f28.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f28);
            _jspx_th_c_005fif_005f28_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f28, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f28_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f29(final JspTag _jspx_th_c_005fwhen_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f29 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f29_reused = false;
        try {
            _jspx_th_c_005fif_005f29.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f29.setParent((Tag)_jspx_th_c_005fwhen_005f7);
            _jspx_th_c_005fif_005f29.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f29 = _jspx_th_c_005fif_005f29.doStartTag();
            if (_jspx_eval_c_005fif_005f29 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f29.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f29.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f29);
            _jspx_th_c_005fif_005f29_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f29, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f29_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f30(final JspTag _jspx_th_c_005fwhen_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f30 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f30_reused = false;
        try {
            _jspx_th_c_005fif_005f30.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f30.setParent((Tag)_jspx_th_c_005fwhen_005f7);
            _jspx_th_c_005fif_005f30.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f30 = _jspx_th_c_005fif_005f30.doStartTag();
            if (_jspx_eval_c_005fif_005f30 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f30.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f30.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fif_005f30_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f30, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f30_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f31(final JspTag _jspx_th_c_005fwhen_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f31 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f31_reused = false;
        try {
            _jspx_th_c_005fif_005f31.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f31.setParent((Tag)_jspx_th_c_005fwhen_005f7);
            _jspx_th_c_005fif_005f31.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f31 = _jspx_th_c_005fif_005f31.doStartTag();
            if (_jspx_eval_c_005fif_005f31 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f31.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f31.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f31);
            _jspx_th_c_005fif_005f31_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f31, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f31_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f32(final JspTag _jspx_th_c_005fwhen_005f8, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f32 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f32_reused = false;
        try {
            _jspx_th_c_005fif_005f32.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f32.setParent((Tag)_jspx_th_c_005fwhen_005f8);
            _jspx_th_c_005fif_005f32.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f32 = _jspx_th_c_005fif_005f32.doStartTag();
            if (_jspx_eval_c_005fif_005f32 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f32.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f32.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f32);
            _jspx_th_c_005fif_005f32_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f32, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f32_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f33(final JspTag _jspx_th_c_005fwhen_005f8, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f33 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f33_reused = false;
        try {
            _jspx_th_c_005fif_005f33.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f33.setParent((Tag)_jspx_th_c_005fwhen_005f8);
            _jspx_th_c_005fif_005f33.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f33 = _jspx_th_c_005fif_005f33.doStartTag();
            if (_jspx_eval_c_005fif_005f33 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f33.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f33.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f33);
            _jspx_th_c_005fif_005f33_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f33, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f33_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f34(final JspTag _jspx_th_c_005fwhen_005f9, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f34 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f34_reused = false;
        try {
            _jspx_th_c_005fif_005f34.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f34.setParent((Tag)_jspx_th_c_005fwhen_005f9);
            _jspx_th_c_005fif_005f34.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f34 = _jspx_th_c_005fif_005f34.doStartTag();
            if (_jspx_eval_c_005fif_005f34 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f34.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f34.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f34);
            _jspx_th_c_005fif_005f34_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f34, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f34_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f35(final JspTag _jspx_th_c_005fwhen_005f9, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f35 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f35_reused = false;
        try {
            _jspx_th_c_005fif_005f35.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f35.setParent((Tag)_jspx_th_c_005fwhen_005f9);
            _jspx_th_c_005fif_005f35.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f35 = _jspx_th_c_005fif_005f35.doStartTag();
            if (_jspx_eval_c_005fif_005f35 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f35.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f35.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fif_005f35_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f35, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f35_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f36(final JspTag _jspx_th_c_005fotherwise_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f36 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f36_reused = false;
        try {
            _jspx_th_c_005fif_005f36.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f36.setParent((Tag)_jspx_th_c_005fotherwise_005f5);
            _jspx_th_c_005fif_005f36.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f36 = _jspx_th_c_005fif_005f36.doStartTag();
            if (_jspx_eval_c_005fif_005f36 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f36.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f36.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f36);
            _jspx_th_c_005fif_005f36_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f36, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f36_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f37(final JspTag _jspx_th_c_005fotherwise_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f37 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f37_reused = false;
        try {
            _jspx_th_c_005fif_005f37.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f37.setParent((Tag)_jspx_th_c_005fotherwise_005f5);
            _jspx_th_c_005fif_005f37.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f37 = _jspx_th_c_005fif_005f37.doStartTag();
            if (_jspx_eval_c_005fif_005f37 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f37.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f37.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f37);
            _jspx_th_c_005fif_005f37_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f37, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f37_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f38(final JspTag _jspx_th_c_005fwhen_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f38 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f38_reused = false;
        try {
            _jspx_th_c_005fif_005f38.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f38.setParent((Tag)_jspx_th_c_005fwhen_005f7);
            _jspx_th_c_005fif_005f38.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f38 = _jspx_th_c_005fif_005f38.doStartTag();
            if (_jspx_eval_c_005fif_005f38 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f38.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f38.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f38);
            _jspx_th_c_005fif_005f38_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f38, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f38_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f39(final JspTag _jspx_th_c_005fotherwise_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f39 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f39_reused = false;
        try {
            _jspx_th_c_005fif_005f39.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f39.setParent((Tag)_jspx_th_c_005fotherwise_005f6);
            _jspx_th_c_005fif_005f39.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f39 = _jspx_th_c_005fif_005f39.doStartTag();
            if (_jspx_eval_c_005fif_005f39 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f39.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f39.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f39);
            _jspx_th_c_005fif_005f39_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f39, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f39_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f40(final JspTag _jspx_th_c_005fotherwise_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f40 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f40_reused = false;
        try {
            _jspx_th_c_005fif_005f40.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f40.setParent((Tag)_jspx_th_c_005fotherwise_005f6);
            _jspx_th_c_005fif_005f40.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f40 = _jspx_th_c_005fif_005f40.doStartTag();
            if (_jspx_eval_c_005fif_005f40 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f40.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f40.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f40);
            _jspx_th_c_005fif_005f40_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f40, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f40_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f41(final JspTag _jspx_th_c_005fotherwise_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f41 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f41_reused = false;
        try {
            _jspx_th_c_005fif_005f41.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f41.setParent((Tag)_jspx_th_c_005fotherwise_005f6);
            _jspx_th_c_005fif_005f41.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f41 = _jspx_th_c_005fif_005f41.doStartTag();
            if (_jspx_eval_c_005fif_005f41 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f41.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f41.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f41);
            _jspx_th_c_005fif_005f41_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f41, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f41_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f42(final JspTag _jspx_th_c_005fwhen_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f42 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f42_reused = false;
        try {
            _jspx_th_c_005fif_005f42.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f42.setParent((Tag)_jspx_th_c_005fwhen_005f10);
            _jspx_th_c_005fif_005f42.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f42 = _jspx_th_c_005fif_005f42.doStartTag();
            if (_jspx_eval_c_005fif_005f42 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f42.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f42.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f42);
            _jspx_th_c_005fif_005f42_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f42, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f42_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f43(final JspTag _jspx_th_c_005fwhen_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f43 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f43_reused = false;
        try {
            _jspx_th_c_005fif_005f43.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f43.setParent((Tag)_jspx_th_c_005fwhen_005f10);
            _jspx_th_c_005fif_005f43.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f43 = _jspx_th_c_005fif_005f43.doStartTag();
            if (_jspx_eval_c_005fif_005f43 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f43.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f43.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f43);
            _jspx_th_c_005fif_005f43_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f43, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f43_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f44(final JspTag _jspx_th_c_005fwhen_005f11, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f44 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f44_reused = false;
        try {
            _jspx_th_c_005fif_005f44.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f44.setParent((Tag)_jspx_th_c_005fwhen_005f11);
            _jspx_th_c_005fif_005f44.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f44 = _jspx_th_c_005fif_005f44.doStartTag();
            if (_jspx_eval_c_005fif_005f44 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f44.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f44.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f44);
            _jspx_th_c_005fif_005f44_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f44, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f44_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f45(final JspTag _jspx_th_c_005fwhen_005f11, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f45 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f45_reused = false;
        try {
            _jspx_th_c_005fif_005f45.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f45.setParent((Tag)_jspx_th_c_005fwhen_005f11);
            _jspx_th_c_005fif_005f45.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f45 = _jspx_th_c_005fif_005f45.doStartTag();
            if (_jspx_eval_c_005fif_005f45 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f45.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f45.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fif_005f45_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f45, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f45_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f46(final JspTag _jspx_th_c_005fotherwise_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f46 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f46_reused = false;
        try {
            _jspx_th_c_005fif_005f46.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f46.setParent((Tag)_jspx_th_c_005fotherwise_005f7);
            _jspx_th_c_005fif_005f46.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f46 = _jspx_th_c_005fif_005f46.doStartTag();
            if (_jspx_eval_c_005fif_005f46 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f46.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f46.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f46);
            _jspx_th_c_005fif_005f46_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f46, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f46_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f47(final JspTag _jspx_th_c_005fotherwise_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f47 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f47_reused = false;
        try {
            _jspx_th_c_005fif_005f47.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f47.setParent((Tag)_jspx_th_c_005fotherwise_005f7);
            _jspx_th_c_005fif_005f47.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f47 = _jspx_th_c_005fif_005f47.doStartTag();
            if (_jspx_eval_c_005fif_005f47 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f47.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f47.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f47);
            _jspx_th_c_005fif_005f47_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f47, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f47_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f48(final JspTag _jspx_th_c_005fotherwise_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f48 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f48_reused = false;
        try {
            _jspx_th_c_005fif_005f48.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f48.setParent((Tag)_jspx_th_c_005fotherwise_005f6);
            _jspx_th_c_005fif_005f48.setTest((boolean)PageContextImpl.proprietaryEvaluate("${deviceType == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f48 = _jspx_th_c_005fif_005f48.doStartTag();
            if (_jspx_eval_c_005fif_005f48 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("deviceTypeStyle");
                    evalDoAfterBody = _jspx_th_c_005fif_005f48.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f48.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f48);
            _jspx_th_c_005fif_005f48_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f48, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f48_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmWindowsEnroll_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmWindowsEnroll_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
    }
}

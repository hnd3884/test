package org.apache.jsp.jsp.common;

import java.util.HashSet;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.taglib.DCProductTag;
import org.apache.taglibs.standard.tag.rt.core.OutTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import com.me.devicemanagement.framework.webclient.common.OnlineUrlLoader;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.common.MDMURLRedirection;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
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

public final class secondLoginMDM_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return secondLoginMDM_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return secondLoginMDM_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return secondLoginMDM_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = secondLoginMDM_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fchoose = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.release();
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.release();
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
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
            response.setContentType("text/html; charset=UTF-8");
            final PageContext pageContext = _jspx_page_context = secondLoginMDM_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n\n");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n\n\n\n\n\n\n       <link href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/common.css\" rel=\"stylesheet\" type=\"text/css\" />\n            <link href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/loginMDM.css?v2\" rel=\"stylesheet\" type=\"text/css\" />\n            <link href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/");
            if (this._jspx_meth_c_005fout_005f0(_jspx_page_context)) {
                return;
            }
            out.write("/style.css\" rel=\"stylesheet\" type=\"text/css\">\n                <link href=\"/images/favicon.ico\" rel=\"SHORTCUT ICON\"/>\n<form name=\"secondlogin\" action=\"");
            out.print(response.encodeURL("j_security_check"));
            out.write("\" style=\"height:100%;\"  method=\"post\" AUTOCOMPLETE=\"off\" >    \n    <table width=\"100%\" height=\"100%\" border=\"0\"  cellspacing=\"0\" cellpadding=\"0\">                                           \n        <tr>\n            <td style=\"vertical-align:middle;padding-top: 10px\">\n                <div style=\"width:100%;\">\n                    <div id=\"login_content\">\n                    <div class=\"login_top_container\" style=\"padding-bottom: 10px;\" >\n                                 <div id=\"login_top\">\n                                                    <p>&nbsp;</p>\n                                                    <a target=\"_blank\" href=\"");
            out.print(IAMEncoder.encodeHTMLAttribute(I18N.getMsg(MDMURLRedirection.getURL("get_quote"), new Object[0])));
            out.write(63);
            out.write(112);
            out.write(61);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" title=\"");
            out.print(I18N.getMsg("desktopcentral.common.get_qoute", new Object[0]));
            out.write("\"><div class=\"topLink7\"> &nbsp; </div></a>\n                                                    <a target=\"_blank\" href=\"");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("store_url"), new Object[0]));
            out.write(63);
            out.write(112);
            out.write(61);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"title=\"");
            out.print(I18N.getMsg("dc.common.ONLINE_STORE", new Object[0]));
            out.write("\"><div class=\"topLink8\"> &nbsp; </div></a>\n                                                    <a href=\"");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("forums_url"), new Object[0]));
            out.write(63);
            out.write(112);
            out.write(61);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\" title=\"");
            out.print(I18N.getMsg("dc.common.USER_COMMUNITY", new Object[0]));
            out.write("\"> <div class=\"topLink1\"> &nbsp; </div> </a>\n                                                    <a href=\"");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("blog"), new Object[0]));
            out.write(63);
            out.write(112);
            out.write(61);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\" title=\"");
            out.print(I18N.getMsg("dc.common.BLOGS", new Object[0]));
            out.write("\"><div class=\"topLink2\"> &nbsp; </div></a>\n                                                    <a href=\"http://twitter.com/ManageEngine\" target=\"_blank\"  title=\"");
            out.print(I18N.getMsg("dc.common.TWITTER", new Object[0]));
            out.write("\"><div class=\"topLink3\"> &nbsp; </div></a>\n                                                    <a href=\"https://www.facebook.com/ManageEngine\" target=\"_blank\"title=\"Facebook\"><div class=\"topLink9\"> &nbsp; </div></a>\n                                                    <a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/help/index.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\"title=\"");
            out.print(I18N.getMsg("dc.common.HELP", new Object[0]));
            out.write("\"><div class=\"topLink6\"> &nbsp; </div></a>\n\n                                                </div>\n\t\t\t\t\t\t\t\t\t</div>\n                        <div ");
            if (this._jspx_meth_fw_005fproductTag_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\n                                                  ");
            if (this._jspx_meth_fw_005fproductTag_005f1(_jspx_page_context)) {
                return;
            }
            out.write(" >\n                                                <div ");
            if (this._jspx_meth_fw_005fproductTag_005f2(_jspx_page_context)) {
                return;
            }
            out.write("\n                                                     ");
            if (this._jspx_meth_fw_005fproductTag_005f3(_jspx_page_context)) {
                return;
            }
            out.write(">\n                                <div style=\"text-align:center\" class=\"img_caption\"> \n                                 <span class=\"capt_normal\">");
            out.print(I18N.getMsg("dc.mdm.device_mgmt.mobile_device_management", new Object[0]));
            out.write(" \n                                    ");
            out.print(I18N.getMsg("dc.common.SOFTWARE", new Object[0]));
            out.write(" </span>      \n                                   </div>\n                                <div id=\"signin_form\">\n                                    <div class=\"signin_form_fill\">\n                                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-weight:bold\" class=\"formContentTble\">\n                                            <tr>\n                                                <td align=\"left\" class=\"bodytext\" style=\"padding-top:15px;\">\n                                                    <input name=\"2factor_password\" id=\"2factor_password\" autoFocus onFocus=\"if (this.value=='One Time Password') this.value = ''\" onKeyPress=\"return onlyNos(event,this);\" onpaste=\"return onlyNosPaste(event,this);\" class=\"passwordField\" placeholder=\"");
            out.print(I18N.getMsg("desktopcentral.common.login.one_time_password", new Object[0]));
            out.write("\" type=\"text\" autocomplete=\"off\" style=\"height:20px; padding-top:5px;\"/>\n                                                    ");
            final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f0_reused = false;
            try {
                _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f0_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userEmailForMailTFA != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<p style=\"padding-bottom:2px\">");
                                    out.print(I18N.getMsg("mdm.tfa.email_info_in_login_page", new Object[0]));
                                    out.write("&nbsp;<i>");
                                    out.write((String)PageContextImpl.proprietaryEvaluate("${userEmailForMailTFA}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                    out.write("</i></p>\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                                    evalDoAfterBody = _jspx_th_c_005fwhen_005f0.doAfterBody();
                                } while (evalDoAfterBody == 2);
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
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f0_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            final int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f0 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                                    out.print(I18N.getMsg("mdm.tfa.enter_authcode", new Object[0]));
                                    out.write("&nbsp;<br>\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                                    evalDoAfterBody2 = _jspx_th_c_005fotherwise_005f0.doAfterBody();
                                } while (evalDoAfterBody2 == 2);
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
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t");
                        evalDoAfterBody3 = _jspx_th_c_005fchoose_005f0.doAfterBody();
                    } while (evalDoAfterBody3 == 2);
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
            out.write("\n                                                    ");
            final ChooseTag _jspx_th_c_005fchoose_005f2 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f1_reused = false;
            try {
                _jspx_th_c_005fchoose_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f2.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f2 = _jspx_th_c_005fchoose_005f2.doStartTag();
                if (_jspx_eval_c_005fchoose_005f2 != 0) {
                    int evalDoAfterBody6;
                    do {
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t\n                                                        ");
                        final WhenTag _jspx_th_c_005fwhen_005f2 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f1_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f2.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            _jspx_th_c_005fwhen_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${!empty param.otpTimeout}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f2 = _jspx_th_c_005fwhen_005f2.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f2 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n                                                            <input type=\"hidden\" name=\"otpTimeout\" id=\"otpTimeout\" value='");
                                    out.print(IAMEncoder.encodeHTMLAttribute(request.getParameter("otpTimeout")));
                                    out.write("'/>\n                                                            <input type=\"checkbox\" id=\"rememberMe\" name=\"rememberMe\" value=\"rememberMe\"/>&nbsp;");
                                    out.print(I18N.getMsg("mdm.common.login.trust_this_browser", new Object[0]));
                                    out.write("&nbsp;");
                                    out.print(I18N.getMsg("dc.som.som_settings.for", new Object[0]));
                                    out.write("&nbsp;");
                                    out.print(IAMEncoder.encodeHTML(request.getParameter("otpTimeout")));
                                    out.write("&nbsp;");
                                    out.print(I18N.getMsg("dc.som.som_policy.Not_inactive_comp_2", new Object[0]));
                                    out.write(" \n                                                        ");
                                    evalDoAfterBody2 = _jspx_th_c_005fwhen_005f2.doAfterBody();
                                } while (evalDoAfterBody2 == 2);
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
                        out.write("\n                                                        ");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f2 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f1_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f2.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            final int _jspx_eval_c_005fotherwise_005f2 = _jspx_th_c_005fotherwise_005f2.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f2 != 0) {
                                int evalDoAfterBody5;
                                do {
                                    out.write("\n\t\n                                                            ");
                                    final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                                    boolean _jspx_th_c_005fif_005f0_reused = false;
                                    try {
                                        _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_c_005fotherwise_005f2);
                                        _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${!empty otpTimeout}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                        final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
                                        if (_jspx_eval_c_005fif_005f0 != 0) {
                                            int evalDoAfterBody4;
                                            do {
                                                out.write("\n                                                                <input type=\"hidden\" name=\"otpTimeout\" id=\"otpTimeout\" value=\"");
                                                out.write((String)PageContextImpl.proprietaryEvaluate("${otpTimeout}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                out.write("\"/>\n                                                                <input type=\"checkbox\" id=\"rememberMe\" name=\"rememberMe\" value=\"rememberMe\"/>&nbsp;");
                                                out.print(I18N.getMsg("mdm.common.login.trust_this_browser", new Object[0]));
                                                out.write("&nbsp;");
                                                out.print(I18N.getMsg("dc.som.som_settings.for", new Object[0]));
                                                out.write("&nbsp;");
                                                out.write((String)PageContextImpl.proprietaryEvaluate("${otpTimeout}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                out.write("&nbsp;");
                                                out.print(I18N.getMsg("dc.som.som_policy.Not_inactive_comp_2", new Object[0]));
                                                out.write(" \n                                                            ");
                                                evalDoAfterBody4 = _jspx_th_c_005fif_005f0.doAfterBody();
                                            } while (evalDoAfterBody4 == 2);
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
                                    out.write("\n                                                        ");
                                    evalDoAfterBody5 = _jspx_th_c_005fotherwise_005f2.doAfterBody();
                                } while (evalDoAfterBody5 == 2);
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
                        out.write("\n                                                    ");
                        evalDoAfterBody6 = _jspx_th_c_005fchoose_005f2.doAfterBody();
                    } while (evalDoAfterBody6 == 2);
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
            out.write("\n                                                    <div id=\"secondlogin_error\">\n                                                    ");
            final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f1_reused = false;
            try {
                _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f2.setParent((Tag)null);
                _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${param.accountLocked == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
                if (_jspx_eval_c_005fif_005f2 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n                                                        <span class=\"signin_error\">");
                        out.print(IAMEncoder.encodeHTML(I18N.getMsg("dc.common.secondlogin_failed_account_locked", new Object[] { request.getParameter("lockTime") })));
                        out.write("\n                                                        </span>\n                                                    ");
                        evalDoAfterBody3 = _jspx_th_c_005fif_005f2.doAfterBody();
                    } while (evalDoAfterBody3 == 2);
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
            out.write("\n                                                    ");
            final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f2_reused = false;
            try {
                _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f3.setParent((Tag)null);
                _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${param.loginStatus != null && param.accountLocked == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
                if (_jspx_eval_c_005fif_005f3 != 0) {
                    int evalDoAfterBody9;
                    do {
                        out.write("\n                                                         ");
                        final IfTag _jspx_th_c_005fif_005f4 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f3_reused = false;
                        try {
                            _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f4.setParent((Tag)_jspx_th_c_005fif_005f3);
                            _jspx_th_c_005fif_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${param.loginStatus != null && param.accountLocked == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
                            if (_jspx_eval_c_005fif_005f4 != 0) {
                                int evalDoAfterBody8;
                                do {
                                    out.write("\n                                                        <span class=\"signin_error\">");
                                    out.print(I18N.getMsg("desktopcentral.common.secondlogin_failed", new Object[0]));
                                    final IfTag _jspx_th_c_005fif_005f5 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                                    boolean _jspx_th_c_005fif_005f4_reused = false;
                                    try {
                                        _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fif_005f5.setParent((Tag)_jspx_th_c_005fif_005f4);
                                        _jspx_th_c_005fif_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userEmailForMailTFA == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                        final int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
                                        if (_jspx_eval_c_005fif_005f5 != 0) {
                                            int evalDoAfterBody7;
                                            do {
                                                out.write("\n <class=\"bodyText\" valign=\"absmiddle\" style=\"margin:17px 0px 0px 0px ;padding-left:8px\">");
                                                out.print(I18N.getMsg("mdm.admin.trobleshoot_tips", new Object[] { OnlineUrlLoader.getInstance().getValue("url.two_factor.troubleshoot_tips", true) }));
                                                out.write(10);
                                                evalDoAfterBody7 = _jspx_th_c_005fif_005f5.doAfterBody();
                                            } while (evalDoAfterBody7 == 2);
                                        }
                                        if (_jspx_th_c_005fif_005f5.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f5);
                                        _jspx_th_c_005fif_005f4_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f4_reused);
                                    }
                                    out.write("\n                                                        </span>\n\n                                                    ");
                                    evalDoAfterBody8 = _jspx_th_c_005fif_005f4.doAfterBody();
                                } while (evalDoAfterBody8 == 2);
                            }
                            if (_jspx_th_c_005fif_005f4.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f4);
                            _jspx_th_c_005fif_005f3_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f3_reused);
                        }
                        out.write("\n                                                    ");
                        evalDoAfterBody9 = _jspx_th_c_005fif_005f3.doAfterBody();
                    } while (evalDoAfterBody9 == 2);
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
            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<div>\n                                                     ");
            final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f5_reused = false;
            try {
                _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f6.setParent((Tag)null);
                _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userEmailForMailTFA == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
                if (_jspx_eval_c_005fif_005f6 != 0) {
                    int evalDoAfterBody4;
                    do {
                        out.write("\n                                                    ");
                        final ChooseTag _jspx_th_c_005fchoose_005f3 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                        boolean _jspx_th_c_005fchoose_005f2_reused = false;
                        try {
                            _jspx_th_c_005fchoose_005f3.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fchoose_005f3.setParent((Tag)_jspx_th_c_005fif_005f6);
                            final int _jspx_eval_c_005fchoose_005f3 = _jspx_th_c_005fchoose_005f3.doStartTag();
                            if (_jspx_eval_c_005fchoose_005f3 != 0) {
                                int evalDoAfterBody12;
                                do {
                                    out.write("\n                                                        ");
                                    final WhenTag _jspx_th_c_005fwhen_005f3 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                    boolean _jspx_th_c_005fwhen_005f2_reused = false;
                                    try {
                                        _jspx_th_c_005fwhen_005f3.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fwhen_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f3);
                                        _jspx_th_c_005fwhen_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${mailServerConfigured == true }", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                        final int _jspx_eval_c_005fwhen_005f3 = _jspx_th_c_005fwhen_005f3.doStartTag();
                                        if (_jspx_eval_c_005fwhen_005f3 != 0) {
                                            int evalDoAfterBody10;
                                            do {
                                                out.write("\n                                                           <div class=\"blueTxt\" style=\"cursor:pointer;padding-top: 8px;position:relative;\">");
                                                out.print(I18N.getMsg("mdm.admin.unable_to_access_qr_code", new Object[] { OnlineUrlLoader.getInstance().getValue("url.two_factor.how_to_disable_twofactor", true) }));
                                                out.write("\n                                                           </div>\n                                                            <div id=\"googleSuccessInfo\" style=\"display:none\" class=\"inlineResultSuccess\">\n                                                                \n\t\n                                                                    <img src=\"/images/approved.gif\" width=\"13px\" height=\"13px\" >");
                                                out.print(I18N.getMsg("mdm.tfa.send_qr__success", new Object[0]));
                                                out.write("</span>");
                                                out.write("\n                                        \n                                                            </div>\n                                                        ");
                                                evalDoAfterBody10 = _jspx_th_c_005fwhen_005f3.doAfterBody();
                                            } while (evalDoAfterBody10 == 2);
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
                                    out.write("\n                                                        ");
                                    final OtherwiseTag _jspx_th_c_005fotherwise_005f3 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                    boolean _jspx_th_c_005fotherwise_005f2_reused = false;
                                    try {
                                        _jspx_th_c_005fotherwise_005f3.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fotherwise_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f3);
                                        final int _jspx_eval_c_005fotherwise_005f3 = _jspx_th_c_005fotherwise_005f3.doStartTag();
                                        if (_jspx_eval_c_005fotherwise_005f3 != 0) {
                                            int evalDoAfterBody11;
                                            do {
                                                out.write("\n                                                            <span class=\"bodyText\" valign=\"absmiddle\" style=\"margin:17px 0px 0px 10px ;display: inline-block;\">");
                                                out.print(I18N.getMsg("mdm.admin.need_qr_code", new Object[] { OnlineUrlLoader.getInstance().getValue("url.two_factor.how_to_disable_twofactor", true) }));
                                                out.write("</span>");
                                                out.write("\n                                                        ");
                                                evalDoAfterBody11 = _jspx_th_c_005fotherwise_005f3.doAfterBody();
                                            } while (evalDoAfterBody11 == 2);
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
                                    out.write("\n                                                    ");
                                    evalDoAfterBody12 = _jspx_th_c_005fchoose_005f3.doAfterBody();
                                } while (evalDoAfterBody12 == 2);
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
                        out.write("\n                                                    ");
                        evalDoAfterBody4 = _jspx_th_c_005fif_005f6.doAfterBody();
                    } while (evalDoAfterBody4 == 2);
                }
                if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
                _jspx_th_c_005fif_005f5_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f5_reused);
            }
            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"Button\" id=\"Button\" onClick=\"return checkfornull(this.form)\" align=\"right\" class=\"signin_btn\" Value=\"Sign in\" tabindex=\"4\" />   ");
            out.write("\n                                                \t");
            final IfTag _jspx_th_c_005fif_005f7 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f6_reused = false;
            try {
                _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f7.setParent((Tag)null);
                _jspx_th_c_005fif_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userEmailForMailTFA != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
                if (_jspx_eval_c_005fif_005f7 != 0) {
                    int evalDoAfterBody4;
                    do {
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"blueTxt\" id=\"resendOTP\" onClick=\"javascript:resendOTP()\" style=\"cursor:pointer;padding-top:10px;margin-left:120px;position:absolute;\">");
                        out.print(I18N.getMsg("desktopcentral.common.login.resend_otp", new Object[0]));
                        out.write("\n                                                            <div id=\"successInfo\" style=\"display:none\">\n                                                                <div class=\"error-tip\" id=\"error-tip-phost\" style=\"color:#000;top:0; left:67px;min-width:155px;padding:10px;\">\n                                                                    ");
                        out.print(I18N.getMsg("mdm.tfa.resend_success", new Object[0]));
                        out.write("\n                                                                </div>\n                                                           </div>                                                                            \n\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
                        evalDoAfterBody4 = _jspx_th_c_005fif_005f7.doAfterBody();
                    } while (evalDoAfterBody4 == 2);
                }
                if (_jspx_th_c_005fif_005f7.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f7);
                _jspx_th_c_005fif_005f6_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f6_reused);
            }
            out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t</td>\n                                            </tr>\n                                        </table>\n                                    </div>\n                                </div>\n                            </div>\n                        </div>\n\n                       \n                                 \n                                      <div class=\"login_links_containter\">\n                                    <div id=\"login_links\">\n                                        <div class=\"login_links_left\"></div>\n                                        <div class=\"login_links_content\">\n                                            <div class=\"login_links_ql\">\n                                                <h4> ");
            out.print(I18N.getMsg("dc.common.QUICK_LINKS", new Object[0]));
            out.write("</h4>\n                                                <ul>\n                                                     <li> <img src=\"/images/login/quicktour.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/features.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("desktopcentral.common.login.quick_tour", new Object[0]));
            out.write("</a></li>\n                                                    <li  ><img src=\"/images/login/demoregistration.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/request-demo.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("desktopcentral.common.Register_Free_Demo", new Object[0]));
            out.write("</a></li>\n                                                    <li  ><img src=\"/images/login/kb.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/knowledge-base.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("dc.common.KNOWLEDGE_BASE", new Object[0]));
            out.write("</a></li>\n                                                    <li ><img src=\"/images/login/pricequote.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/get-quote.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("desktopcentral.webclient.common.contactus.get_price_quote", new Object[0]));
            out.write("</a></li>\n                                                </ul>\n                                            </div>\n\n                                            <div class=\"login_links_contact\" style=\"width:25%\">\n                                                <h4>");
            out.print(I18N.getMsg("desktopcentral.common.contact_us", new Object[0]));
            out.write("</h4>\n                                                <ul>\n                                                    <li>\n                                                        <img src=\"/images/login/website.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp; <a href=\"");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("desktopcentral_today"), new Object[0]));
            out.write(63);
            out.write(112);
            out.write(61);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("website"), new Object[0]));
            out.write("</a></li>");
            out.write("\n                                                        <li>\n                                                        <img src=\"/images/login/email.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp; <a href=\"mailto:");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
            out.write("</a></li>");
            out.write("\n                                                        <li><img src=\"/images/login/contact.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;   ");
            out.print(I18N.getMsg("dc.common.toll_free_number", new Object[0]));
            out.write("</li>\n                                                </ul>\n                                            </div>\n\t\t\t\t\t\t\t\t\t\t<div class=\"login_links_relatedprod\">\n                                                <h4>");
            out.print(I18N.getMsg("desktopcentral.common.login.related_products", new Object[0]));
            out.write("</h4>\n                                                \n                                               <a href=\"http://www.manageengine.com/products/desktop-central/index.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\" style=\"width: 300px;display: inline-block;padding: 5px 0px;\"><span style=\"display:inline-block;vertical-align: middle;width:150px;\"><img src=\"/images/login/ME-DC.svg\" width=\"112\" height=\"30\" border=\"0\" style=\"padding-top:5px;\"></span><span class=\"productHint\">");
            out.print(I18N.getMsg("mdm.relatedProducts.dc", new Object[0]));
            out.write("</span></a>                                                                                                                                      <br>\n                                                <a href=\"https://www.manageengine.com/secure-browser/?mdmplogin\" target=\"_blank\" style=\"width: 300px;display: inline-block;\"><span style=\"display:inline-block;vertical-align: middle;padding: 5px 0px;\"><img src=\"/images/login/bsp.svg\" width=\"150\" height=\"48\" border=\"0\" style=\"padding-top:5px;padding-left:2px;\"></span><span class=\"productHint\">");
            out.print(I18N.getMsg("mdm.relatedProducts.bsp", new Object[0]));
            out.write("</span></a>\n                                                \n                                            </div>\n                                        </div>\n                                    \n                                    </div>\n                                    </div>\n                                 \n\t\t\t\t\t\t\t\t\t <div id=\"browser_info\">");
            out.print(I18N.getMsg("dc.admin.rebranding.copyright", new Object[0]));
            out.write(" &copy; ");
            if (this._jspx_meth_c_005fout_005f1(_jspx_page_context)) {
                return;
            }
            out.write(" <a href=\"");
            if (this._jspx_meth_c_005fout_005f2(_jspx_page_context)) {
                return;
            }
            out.write("\" target=\"_blank\">");
            if (this._jspx_meth_c_005fout_005f3(_jspx_page_context)) {
                return;
            }
            out.write("</a>&nbsp;");
            out.print(I18N.getMsg("desktopcentral.common.login.all_rights_reserved", new Object[0]));
            out.write("</div>");
            out.write("\n             \n\t\t\t\t</div>\n                </div>\n            </td>\n        </tr>\n    </table>\n</form>\n\n\n");
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
            secondLoginMDM_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_c_005fout_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f0 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f0_reused = false;
        try {
            _jspx_th_c_005fout_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f0.setParent((Tag)null);
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${selectedskin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f0 = _jspx_th_c_005fout_005f0.doStartTag();
            if (_jspx_th_c_005fout_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f0);
            _jspx_th_c_005fout_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fproductTag_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f0 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f0_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f0.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f0.setParent((Tag)null);
            _jspx_th_fw_005fproductTag_005f0.setProductCode("MDMPMSP");
            _jspx_th_fw_005fproductTag_005f0.setShow(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fproductTag_005f0 = _jspx_th_fw_005fproductTag_005f0.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signin_band_container_msp\"");
                    evalDoAfterBody = _jspx_th_fw_005fproductTag_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fproductTag_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.reuse((Tag)_jspx_th_fw_005fproductTag_005f0);
            _jspx_th_fw_005fproductTag_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fproductTag_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fproductTag_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fproductTag_005f1(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f1 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f1_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f1.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f1.setParent((Tag)null);
            _jspx_th_fw_005fproductTag_005f1.setProductCode("MDMPMSP");
            _jspx_th_fw_005fproductTag_005f1.setShow(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fproductTag_005f1 = _jspx_th_fw_005fproductTag_005f1.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signin_band_container\"");
                    evalDoAfterBody = _jspx_th_fw_005fproductTag_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fproductTag_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.reuse((Tag)_jspx_th_fw_005fproductTag_005f1);
            _jspx_th_fw_005fproductTag_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fproductTag_005f1, this._jsp_getInstanceManager(), _jspx_th_fw_005fproductTag_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fproductTag_005f2(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f2 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f2_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f2.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f2.setParent((Tag)null);
            _jspx_th_fw_005fproductTag_005f2.setProductCode("MDMPMSP");
            _jspx_th_fw_005fproductTag_005f2.setShow(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fproductTag_005f2 = _jspx_th_fw_005fproductTag_005f2.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("id=\"signin_band_msp\"");
                    evalDoAfterBody = _jspx_th_fw_005fproductTag_005f2.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fproductTag_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.reuse((Tag)_jspx_th_fw_005fproductTag_005f2);
            _jspx_th_fw_005fproductTag_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fproductTag_005f2, this._jsp_getInstanceManager(), _jspx_th_fw_005fproductTag_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fproductTag_005f3(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f3 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f3_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f3.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f3.setParent((Tag)null);
            _jspx_th_fw_005fproductTag_005f3.setProductCode("MDMPMSP");
            _jspx_th_fw_005fproductTag_005f3.setShow(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fproductTag_005f3 = _jspx_th_fw_005fproductTag_005f3.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("id=\"signin_band\"");
                    evalDoAfterBody = _jspx_th_fw_005fproductTag_005f3.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fproductTag_005f3.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.reuse((Tag)_jspx_th_fw_005fproductTag_005f3);
            _jspx_th_fw_005fproductTag_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fproductTag_005f3, this._jsp_getInstanceManager(), _jspx_th_fw_005fproductTag_005f3_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f1(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f1 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f1_reused = false;
        try {
            _jspx_th_c_005fout_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f1.setParent((Tag)null);
            _jspx_th_c_005fout_005f1.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.copyright_year}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f1 = _jspx_th_c_005fout_005f1.doStartTag();
            if (_jspx_th_c_005fout_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f1);
            _jspx_th_c_005fout_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f2(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f2 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f2_reused = false;
        try {
            _jspx_th_c_005fout_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f2.setParent((Tag)null);
            _jspx_th_c_005fout_005f2.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_url}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f2 = _jspx_th_c_005fout_005f2.doStartTag();
            if (_jspx_th_c_005fout_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f2);
            _jspx_th_c_005fout_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f3(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f3 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f3_reused = false;
        try {
            _jspx_th_c_005fout_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f3.setParent((Tag)null);
            _jspx_th_c_005fout_005f3.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_name}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f3 = _jspx_th_c_005fout_005f3.doStartTag();
            if (_jspx_th_c_005fout_005f3.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f3);
            _jspx_th_c_005fout_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f3_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (secondLoginMDM_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        secondLoginMDM_jsp._jspx_imports_packages.add("java.util");
        secondLoginMDM_jsp._jspx_imports_packages.add("javax.servlet.http");
        secondLoginMDM_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        secondLoginMDM_jsp._jspx_imports_classes.add("com.me.mdm.server.common.MDMURLRedirection");
        secondLoginMDM_jsp._jspx_imports_classes.add("java.util.Locale");
        secondLoginMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.OnlineUrlLoader");
        secondLoginMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        secondLoginMDM_jsp._jspx_imports_classes.add("com.adventnet.iam.xss.IAMEncoder");
        secondLoginMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.I18NUtil");
    }
}

package org.apache.jsp.jsp.common;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.taglibs.standard.tag.rt.core.OutTag;
import com.me.devicemanagement.framework.webclient.taglib.DCProductTag;
import com.me.devicemanagement.framework.webclient.taglib.DCMSPTag;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import com.adventnet.iam.xss.IAMEncoder;
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
import org.apache.jasper.runtime.TagHandlerPool;
import java.util.Set;
import java.util.Map;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class googleAuth_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return googleAuth_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return googleAuth_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return googleAuth_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = googleAuth_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.release();
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.release();
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.release();
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
            final PageContext pageContext = _jspx_page_context = googleAuth_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n\n");
            out.write("\n\n\n\n\n");
            out.write(10);
            out.write(10);
            out.write("\n\n\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<style type=\"text/css\">\nhtml, body { padding:0; margin:0; font:13px Lato, sans-serif;}\ninput {outline:none;}\nh3 {color:#0b4c79; font:normal 18px Lato, sans-serif; margin-top:10px;}\n.passwordField {\n\tborder: 1px solid #d0d0d0;\n    border-radius: 3px;\n    padding:7px;\n\twidth:200px;\n}\nimg.platformSelected{\n\tborder: 1px solid #727f87;\n\tborder-radius:40px;\n\tpadding:5px;\n}\nimg.default {\n\topacity: 0.7;\n    filter: alpha(opacity=70);\n\tpadding:5px;\n}\nimg.default:hover { cursor:pointer;opacity: 1;\n    filter: alpha(opacity=100);}\nimg.iosImg, img.androidImg {\n\tdisplay:block;\n\tcursor:pointer;\n}\n.title { \n\t margin-top:0; \n\t padding:20px 0 20px 350px; \n\t font:normal 22px Lato, sans-serif; \n\t display:inline-block;\n}\n.topstrip {\n\tbackground: #f9f9f9;\n    width: 100%;\n    border-bottom: 1px solid #ebebeb;\n}\n.topstrip .header{\n\twidth:930px;\n\tmargin:auto;\n\tbackground:url(../../../images/login/login_logo.gif) no-repeat;\n\tbackground-size:150px 48px;\n}\n.topstrip .headerMSP{\n\twidth:930px;\n\tmargin:auto;\n");
            out.write("\tbackground:url(../../../images/login/login_logo.gif) no-repeat;\n\tbackground-size:175px 50px;\n}\n.topstrip .headerPMP{\n\twidth:930px;\n\tmargin:auto;\n\tbackground:url(../../../images/login/login_logo.gif) no-repeat;\n\tbackground-size:170px 45px;\n}\n.topstrip .headerRestricted{\n\twidth:930px;\n\tmargin:auto;\n\tbackground-size:170px 48px;\n}\n.container {\n\twidth:920px;\n\tborder-radius:5px;\n\t-moz-border-radius:5px;\n\tmargin:20px auto;\n\tborder:1px solid #e3e3e3;\n\tmin-height:200px;\n\t-webkit-box-shadow: 0 0 5px 1px #CCC;\nbox-shadow: 0 0 5px 1px #CCC;\n}\na, a:link, a:visited, a:active{\n\tcolor:#2973CF;\n\ttext-decoration:none;\n}\na:hover{ text-decoration:underline; cursor:pointer;}\n.greyText {\n\tcolor:#8f8f8f;\n\tfont-style: italic;\n}\n.content{ padding:20px 30px;}\n.navigate {\n\tpadding-bottom:10px;\n\tpadding-left:45%;\n}\n.primaryAction {\n\tborder:1px solid #417cc7;\n    background-color: #6a99d9;\n    color: #fff;\n    text-align: center;\n\tborder-radius:3px;\n\tfont:bold 12px 'Lato';\n\tmin-width: 60px;\n    width: auto;\n    height: 23px;\n    padding: 0 7px;\n");
            out.write("}\n\n.primaryActionSingle {\n\tborder:1px solid #417cc7;\n    background-color: #6a99d9;\n    color: #fff;\n    border-radius:3px;\n\tfont:bold 12px 'Lato';\n\tmin-width: 60px;\n    width: auto;\n    height: 23px;\n    padding: 0 7px;\n\tcursor:poiter;\n}\n\n.secondaryAction {\n\tborder: solid 1px #c6c7c8;\n    background-color: #dcdcdc;\n    border-radius:3px;\n\tfont:bold 12px 'Lato';\n\tmin-width: 60px;\n    width: auto;\n    height: 23px;\n    padding: 0 7px;\n}\n.primaryAction:hover {background:#5a86c5; cursor:pointer}\n.secondaryAction:hover {background:#CCC; cursor:pointer;}\nol li{line-height:2; padding-left:0}\nol {padding-left:20px;}\n.manual{display:none;}\n.footer {\n\ttext-align:center;\n\tcolor:#CCC;\n}\n.primaryActionBtn_loading{\n        background: url(\"/images/syncblue.gif\") no-repeat 15px center;\n        background-size: 27px 27px;\n        background-color: #46a1e1 !important;\n}\n</style>\n\n</head>\n\n<body>\n    <form name=\"googleAuth\" action=\"");
            out.print(response.encodeURL("j_security_check"));
            out.write("\" style=\"height:100%;\"  method=\"post\" AUTOCOMPLETE=\"off\"  >\n            <div class=\"topstrip\">\n                ");
            if (this._jspx_meth_c_005fchoose_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\n                <span class=\"title\"> ");
            out.print(I18N.getMsg("mdm.tfa.two_factor_authentication", new Object[0]));
            out.write("</span>\n                <span class=\"greyText\"> ");
            out.print(I18N.getMsg("mdm.tfa.one_time_setup", new Object[0]));
            out.write(" </span>\n            </div>\n        </div>\n        <div id=\"firstPageDiv\">\n            <div class=\"container\">\n                <div class=\"content\">\n                    <h3> <strong> ");
            out.print(I18N.getMsg("dc.admin.mssql.Step_1", new Object[0]));
            out.write(" : </strong>");
            out.print(I18N.getMsg("mdm.tfa.download_install", new Object[0]));
            out.write(" </h3>\n                    <p> ");
            out.print(I18N.getMsg("mdm.tfa.app_setup", new Object[0]));
            out.write("</p>\n                    \n                    <div id=\"downloadURL\">                        \n                       <br />\n                     <img src=\"/images/platform_ios.png\" id=\"iosImg\" align=\"absmiddle\" onClick=\"javascript:toggleCode('ios')\" width=\"40\" height=\"40\" class=\"platformSelected\">\n\t\t\t\t\t\t&nbsp; &nbsp;<img src=\"/images/platform_android.png\" id=\"androidImg\" align=\"absmiddle\" onClick=\"javascript:toggleCode('android')\" class=\"default\" width=\"40\" height=\"40\">\n                   <p></p>\n                        <div id=\"androidDiv\" style=\"display:none\">\n                             <img src=\"/images/android.png\" height=\"150\" align=\"absmiddle\" style=\"cursor:move;\">\n                        </div>\n                        <div id=\"iosDiv\" >\n                            <img src=\"/images/ios.png\" height=\"150\" align=\"absmiddle\" style=\"cursor:move;\">\n                        </div>\n                        <p>");
            out.print(I18N.getMsg("mdm.tfa.problem_scanning_code", new Object[0]));
            out.write("</p>\n                    </div>\n                    <div style=\"display:none;\" id=\"downloadManual\">\n                    <br />\n                        <p class=\"bodybold\">");
            out.print(I18N.getMsg("mdm.tfa.download_manually", new Object[0]));
            out.write("</p>\n                        <ol>\n                             <li>");
            out.print(I18N.getMsg("mdm.tfa.open_googleplay", new Object[0]));
            out.write("</li>\n                            <li>");
            out.print(I18N.getMsg("mdm.tfa.search_app", new Object[0]));
            out.write("</li>\n                            <li>");
            out.print(I18N.getMsg("mdm.tfa.download_and_install", new Object[0]));
            out.write("</li>\n\t\t\t\t\t\t\t<p>&nbsp;</p>\n                             </ol>\n                         <div class=\"blueTxt cursorPointer\" id=\"toggle1\" onclick=\"toggleDownloadDiv('show')\" style=\"position: absolute;\">");
            out.print(I18N.getMsg("mdm.tfa.want_download_scanqr", new Object[0]));
            out.write("</div>\n\n                    </div>\n                </div><p>&nbsp;</p>\n                <div class=\"navigate\"> \n                    <input name=\"next\" type=\"button\" id=\"nextSpan\" onmouseover=\"showNumOfSteps()\" class=\"primaryActionBtn\" onClick=\"javascript:swapPageOneToTwo()\" value=\"Next\"/>\n\t\t\t\t\t<span id=\"hoverdiv\" align=\"top\" style=\"display:none\" class=\"greyText\">&nbsp; &nbsp;");
            out.print(I18N.getMsg("mdm.tfa.complete_two_steps", new Object[0]));
            out.write("</span>\n                </div>\n        </div>\n\t\t</div>\n\t\t\n\t\t\n                                        <div id=\"secondPageDiv\" style=\"display:none\"> \n                                            <div class=\"container\">\n                                                <div class=\"content\">\n                                                    <h3> <strong> ");
            out.print(I18N.getMsg("dc.admin.mssql.Step_2", new Object[0]));
            out.write(" : </strong>");
            out.print(I18N.getMsg("mdm.tfa.configure_app", new Object[0]));
            out.write(" </h3>\n\t\t\t\t\t\t\t\t\t\t\t\t\t<p>");
            out.print(I18N.getMsg("mdm.tfa.scan_to_generate", new Object[0]));
            out.write("</p>\n                                                    <div id=\"barCode\">\n                                                        <img src=\"data:image/png;base64,");
            out.write((String)PageContextImpl.proprietaryEvaluate("${barUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"\n                                                             alt=\"Bar Code\" width=\"200\" height=\"200\" hspace=\"0\" vspace=\"0\"/>\n                                                        <div class=\"blueTxt cursorPointer\" id=\"toggle1\" onclick=\"toggleDiv('hide')\" style=\"position: absolute;\">");
            out.print(I18N.getMsg("mdm.tfa.problem_scanning2_code", new Object[0]));
            out.write("</div>\n                                                    </div>\n                                                    <div style=\"display:none;\" id=\"secretCode\"> <br />\n                                                        <p class=\"bodybold\">");
            out.print(I18N.getMsg("mdm.tfa.configure_manually", new Object[0]));
            out.write("</p>\n                                                            <ol>\n                                                                <li>");
            out.print(I18N.getMsg("mdm.tfa.account", new Object[0]));
            out.write("</li>\n                                                                <li>");
            out.print(I18N.getMsg("mdm.config.registry.addMore.Key", new Object[0]));
            out.write(" <strong> : ");
            out.write((String)PageContextImpl.proprietaryEvaluate("${secretKey}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(" </strong></li>\n                                                                <li>");
            out.print(I18N.getMsg("mdm.tfa.timebased", new Object[0]));
            out.write("</li>\n                                         \n                                                                 </ol>\n                                                            <div class=\"blueTxt cursorPointer\" id=\"toggle1\" onclick=\"toggleDiv('show')\" style=\"position: absolute;\"><br>");
            out.print(I18N.getMsg("mdm.tfa.want_configure_scanqr", new Object[0]));
            out.write("</div>\n                                                    </div>\n                                                </div>\n\t\t\t\t\t\t\t\t\t\t\t\t<p>&nbsp;</p>\n                                                    <div class=\"navigate\"> \n                                                        <input name=\"back\" type=\"button\" id=\"back\" class=\"secondaryActionBtn\" onClick=\"javascript:swapPageTwoToOne()\" value=\"Back\"/>\n                                                        <input name=\"next\" type=\"button\" id=\"secNextSpan\" onmouseover=\"showSecondNumOfSteps()\" class=\"primaryActionBtn\" onClick=\"javascript:swapPageTwoToThree()\" value=\"Next\"/>\n                                                        <span id=\"secHoverdiv\" align=\"top\" style=\"display:none\" class=\"greyText\">");
            out.print(I18N.getMsg("mdm.tfa.complete_one_step", new Object[0]));
            out.write("</span>\n                                                    </div>\n                                            </div>\n                                        </div>\n\t\t\t\t\t\t\n\t\t\t\t\t\t\n                                                        <div id=\"thirdPageDiv\" style=\"display:none\">\n                                                            <div class=\"container\">\n                                                                <div class=\"content\">\n                                                                    <h3> <strong> ");
            out.print(I18N.getMsg("dc.admin.mssql.Step_3", new Object[0]));
            out.write(" : </strong> ");
            out.print(I18N.getMsg("mdm.tfa.login_with_code", new Object[0]));
            out.write("</h3>\n                                                                    \n                                                                    <input name=\"2factor_password\" id=\"2factor_password\" onkeypress=\"return onlyNos(event, this);\" onpaste=\"return onlyNosPaste(event,this);\" class=\"smallbox\" placeholder=\"Enter OTP\" type=\"text\" autocomplete=\"off\"/>\n                                                                </div>\n                                                                <input name=\"userName\" type=\"hidden\" id=\"userName\" value='");
            out.print(IAMEncoder.encodeHTMLAttribute(request.getParameter("j_username")));
            out.write("'/>\n                                                                <input name=\"j_username\" id=\"j_username\" type=\"hidden\" value='");
            out.print(IAMEncoder.encodeHTMLAttribute(request.getParameter("j_username")));
            out.write("'/>\n                                                                <input type=\"hidden\" name=\"browserLocale\" id=\"browserLocale\" value='");
            out.print(IAMEncoder.encodeHTMLAttribute(request.getParameter("browserLocale")));
            out.write("'/>\n                                                               ");
            final ChooseTag _jspx_th_c_005fchoose_005f1 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f1_reused = false;
            try {
                _jspx_th_c_005fchoose_005f1.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f1.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f1 = _jspx_th_c_005fchoose_005f1.doStartTag();
                if (_jspx_eval_c_005fchoose_005f1 != 0) {
                    int evalDoAfterBody4;
                    do {
                        out.write("\n                                                                    ");
                        final WhenTag _jspx_th_c_005fwhen_005f1 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f1_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f1.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f1.setParent((Tag)_jspx_th_c_005fchoose_005f1);
                            _jspx_th_c_005fwhen_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${!empty param.otpTimeout}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f1 = _jspx_th_c_005fwhen_005f1.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f1 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n                                                                        <input type=\"hidden\" name=\"otpTimeout\" id=\"otpTimeout\" value='");
                                    out.print(IAMEncoder.encodeHTMLAttribute(request.getParameter("otpTimeout")));
                                    out.write("'/>\n                                                                        <span style=\"padding-left:25px;\"> <input type=\"checkbox\" id=\"rememberMe\" name=\"rememberMe\" value=\"rememberMe\"/>&nbsp;");
                                    out.print(I18N.getMsg("mdm.common.login.trust_this_browser", new Object[0]));
                                    out.write("&nbsp;");
                                    out.print(I18N.getMsg("dc.som.som_settings.for", new Object[0]));
                                    out.write("&nbsp;");
                                    out.print(IAMEncoder.encodeHTML(request.getParameter("otpTimeout")));
                                    out.write("&nbsp;");
                                    out.print(I18N.getMsg("dc.som.som_policy.Not_inactive_comp_2", new Object[0]));
                                    out.write(" </span> \n                                                                    ");
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
                        out.write("\n                                                                    ");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f1 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f1_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f1.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f1.setParent((Tag)_jspx_th_c_005fchoose_005f1);
                            final int _jspx_eval_c_005fotherwise_005f1 = _jspx_th_c_005fotherwise_005f1.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f1 != 0) {
                                int evalDoAfterBody3;
                                do {
                                    out.write("\n                                                                        ");
                                    final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                                    boolean _jspx_th_c_005fif_005f0_reused = false;
                                    try {
                                        _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_c_005fotherwise_005f1);
                                        _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${!empty otpTimeout}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                        final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
                                        if (_jspx_eval_c_005fif_005f0 != 0) {
                                            int evalDoAfterBody2;
                                            do {
                                                out.write("\n                                                                            <input type=\"hidden\" name=\"otpTimeout\" id=\"otpTimeout\" value=\"");
                                                out.write((String)PageContextImpl.proprietaryEvaluate("${otpTimeout}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                out.write("\"/>\n                                                                            &nbsp;<input type=\"checkbox\" id=\"rememberMe\" name=\"rememberMe\" value=\"rememberMe\"/>&nbsp;");
                                                out.print(I18N.getMsg("mdm.common.login.trust_this_browser", new Object[0]));
                                                out.write("&nbsp;");
                                                out.print(I18N.getMsg("dc.som.som_settings.for", new Object[0]));
                                                out.write("&nbsp;");
                                                out.write((String)PageContextImpl.proprietaryEvaluate("${otpTimeout}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                out.write("&nbsp;");
                                                out.print(I18N.getMsg("dc.som.som_policy.Not_inactive_comp_2", new Object[0]));
                                                out.write("\n                                                                        ");
                                                evalDoAfterBody2 = _jspx_th_c_005fif_005f0.doAfterBody();
                                            } while (evalDoAfterBody2 == 2);
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
                                    out.write("\n                                                                    ");
                                    evalDoAfterBody3 = _jspx_th_c_005fotherwise_005f1.doAfterBody();
                                } while (evalDoAfterBody3 == 2);
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
                        out.write("\n                                                                ");
                        evalDoAfterBody4 = _jspx_th_c_005fchoose_005f1.doAfterBody();
                    } while (evalDoAfterBody4 == 2);
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
            out.write("\n                                                                ");
            final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f1_reused = false;
            try {
                _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f2.setParent((Tag)null);
                _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${param.domainName != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
                if (_jspx_eval_c_005fif_005f2 != 0) {
                    int evalDoAfterBody5;
                    do {
                        out.write("\n                                                                    <input name=\"domainName\" id=\"domainName\" type=\"hidden\" value='");
                        out.print(IAMEncoder.encodeHTMLAttribute(request.getParameter("domainName")));
                        out.write("'/>\n                                                                ");
                        evalDoAfterBody5 = _jspx_th_c_005fif_005f2.doAfterBody();
                    } while (evalDoAfterBody5 == 2);
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
            out.write("\n                                                                <div style=\"height:75px\"> &nbsp;</div>\n                                                                <div class=\"navigate\"> \n                                                                \n                                                                    <input name=\"back\" type=\"button\" id=\"back\" class=\"secondaryActionBtn\" onClick=\"javascript:swapPageThreeToTwo()\" value=\"Back\"/>\n                                                                    <input name=\"Button\" type=\"submit\" id=\"Button\" style=\"background-color:#46a1e1 !important\" class=\"primaryActionBtn\" value=\"Proceed\"/>\n                                                                </div>\n                                                            </div>\n                                                        </div>\n\n        ");
            if (this._jspx_meth_c_005fif_005f2(_jspx_page_context)) {
                return;
            }
            out.write("\n    </form>\n</body>\n</html>\n<script>\njQuery('form').submit(function(e){ //No I18N\n    jQuery(':input[type=submit]').prop('disabled', true); //No I18N\n    jQuery('input[type=submit]').addClass( \"primaryActionBtn_loading\" );\n    jQuery(':input[type=submit]').prop('value', '  '); //No I18N\n});\nfunction showNumOfSteps()\n{\ndocument.getElementById('nextSpan').setAttribute(\"onmouseover\",'document.getElementById(\"hoverdiv\").style.display=\"inline\"');\n\t\t\tdocument.getElementById('nextSpan').setAttribute(\"onmouseout\",'document.getElementById(\"hoverdiv\").style.display=\"none\"');\n\t\n}\nfunction showSecondNumOfSteps()\n{\ndocument.getElementById('secNextSpan').setAttribute(\"onmouseover\",'document.getElementById(\"secHoverdiv\").style.display=\"inline\"');\n\t\t\tdocument.getElementById('secNextSpan').setAttribute(\"onmouseout\",'document.getElementById(\"secHoverdiv\").style.display=\"none\"');\n\t\n}\nfunction toggleDownloadDiv(value) {\n                   if (value==\"hide\") {\n\t\t\t\t\t document.getElementById('downloadURL').style.display = 'none';\n                         document.getElementById('downloadManual').style.display = 'block';\n");
            out.write("\t\t\t\t\t\t \n                             } else {\n                         document.getElementById('downloadURL').style.display = 'block';\n                         document.getElementById('downloadManual').style.display = 'none';\n                         }\n                 }\nfunction toggleCode(value) {\n                   if (value==\"android\") {\n\t\t\t\t\t document.getElementById('iosDiv').style.display = 'none';\n                         document.getElementById('androidDiv').style.display = 'block';\n\t\t\t\t\t\t document.getElementById('androidImg').className = \"platformSelected\";\n\t\t\t\t\t\t document.getElementById('iosImg').className = \"default\";\n\t\t\t\t\t\t \n                             } else {\n                         document.getElementById('iosDiv').style.display = 'block';\n                         document.getElementById('androidDiv').style.display = 'none';\n\t\t\t\t\t\t document.getElementById('androidImg').className = \"default\";\n\t\t\t\t\t\t document.getElementById('iosImg').className = \"platformSelected\";\n                         }\n                 }\n");
            out.write("function swapPageOneToTwo() {\n                    document.getElementById('secondPageDiv').style.display = 'block';\n\t\t\t\t\tdocument.getElementById('firstPageDiv').style.display = 'none';\n                 }\n\t\t\t\t function swapPageTwoToOne() {\n                    document.getElementById('firstPageDiv').style.display = 'block';\n\t\t\t\t\tdocument.getElementById('secondPageDiv').style.display = 'none';\n                 }\n\t\t\t\t function swapPageTwoToThree() {\n                    document.getElementById('secondPageDiv').style.display = 'none';\n\t\t\t\t\tdocument.getElementById('thirdPageDiv').style.display = 'block';\n                    document.getElementById(\"2factor_password\").focus();\n\t\t\t\t }\n\t\t\t\t function swapPageThreeToTwo() {\n                    document.getElementById('thirdPageDiv').style.display = 'none';\n\t\t\t\t\tdocument.getElementById('secondPageDiv').style.display = 'block';\n                 }\n\t\t\t\t \n                 function toggleDiv(value) {\n\t\t\t\t if (value==\"hide\") {\n\t\t\t\t\t document.getElementById('barCode').style.display = 'none';\n");
            out.write("                         document.getElementById('secretCode').style.display = 'block';\n\t\t\t\t\t\t \n                             } else {\n                         document.getElementById('barCode').style.display = 'block';\n                         document.getElementById('secretCode').style.display = 'none';\n                         }\n                     \n                 }\n               \n                 \n</script>\n");
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
            googleAuth_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
                    out.write("\n                    ");
                    if (this._jspx_meth_c_005fwhen_005f0((JspTag)_jspx_th_c_005fchoose_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                        ");
                    if (this._jspx_meth_c_005fotherwise_005f0((JspTag)_jspx_th_c_005fchoose_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                            ");
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
            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${restrictedLoginPage == 'true'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                        <div class=\"headerRestricted\">\n                        ");
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
                    out.write("\n                            ");
                    if (this._jspx_meth_fw_005fmsp_005f0((JspTag)_jspx_th_c_005fotherwise_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                                ");
                    if (this._jspx_meth_fw_005fmsp_005f1((JspTag)_jspx_th_c_005fotherwise_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                                ");
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
    
    private boolean _jspx_meth_fw_005fmsp_005f0(final JspTag _jspx_th_c_005fotherwise_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCMSPTag _jspx_th_fw_005fmsp_005f0 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
        boolean _jspx_th_fw_005fmsp_005f0_reused = false;
        try {
            _jspx_th_fw_005fmsp_005f0.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fmsp_005f0.setParent((Tag)_jspx_th_c_005fotherwise_005f0);
            _jspx_th_fw_005fmsp_005f0.setIsMSP(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fmsp_005f0 = _jspx_th_fw_005fmsp_005f0.doStartTag();
            if (_jspx_eval_fw_005fmsp_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                <div class=\"headerMSP\">\n                                ");
                    evalDoAfterBody = _jspx_th_fw_005fmsp_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fmsp_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f0);
            _jspx_th_fw_005fmsp_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fmsp_005f1(final JspTag _jspx_th_c_005fotherwise_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCMSPTag _jspx_th_fw_005fmsp_005f1 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
        boolean _jspx_th_fw_005fmsp_005f1_reused = false;
        try {
            _jspx_th_fw_005fmsp_005f1.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fmsp_005f1.setParent((Tag)_jspx_th_c_005fotherwise_005f0);
            _jspx_th_fw_005fmsp_005f1.setIsMSP(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fmsp_005f1 = _jspx_th_fw_005fmsp_005f1.doStartTag();
            if (_jspx_eval_fw_005fmsp_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                    ");
                    if (this._jspx_meth_fw_005fproductTag_005f0((JspTag)_jspx_th_fw_005fmsp_005f1, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                                    ");
                    if (this._jspx_meth_fw_005fproductTag_005f1((JspTag)_jspx_th_fw_005fmsp_005f1, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                                    ");
                    evalDoAfterBody = _jspx_th_fw_005fmsp_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fmsp_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f1);
            _jspx_th_fw_005fmsp_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f1, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fproductTag_005f0(final JspTag _jspx_th_fw_005fmsp_005f1, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f0 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f0_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f0.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f0.setParent((Tag)_jspx_th_fw_005fmsp_005f1);
            _jspx_th_fw_005fproductTag_005f0.setProductCode("PMP");
            _jspx_th_fw_005fproductTag_005f0.setShow(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fproductTag_005f0 = _jspx_th_fw_005fproductTag_005f0.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                        <div class=\"headerPMP\">\n                                    ");
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
    
    private boolean _jspx_meth_fw_005fproductTag_005f1(final JspTag _jspx_th_fw_005fmsp_005f1, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f1 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f1_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f1.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f1.setParent((Tag)_jspx_th_fw_005fmsp_005f1);
            _jspx_th_fw_005fproductTag_005f1.setProductCode("PMP");
            _jspx_th_fw_005fproductTag_005f1.setShow(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fproductTag_005f1 = _jspx_th_fw_005fproductTag_005f1.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                    <div class=\"header\">\n                                    ");
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
    
    private boolean _jspx_meth_c_005fif_005f2(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f2_reused = false;
        try {
            _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f2.setParent((Tag)null);
            _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${restrictedLoginPage != 'true'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
            if (_jspx_eval_c_005fif_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n            <div id=\"browser_info\"> &copy; ");
                    if (this._jspx_meth_c_005fout_005f0((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                        return true;
                    }
                    out.write(" <a class=\"bluetxt\" href=\"");
                    if (this._jspx_meth_c_005fout_005f1((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\" target=\"_blank\">");
                    if (this._jspx_meth_c_005fout_005f2((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                        return true;
                    }
                    out.write("</div>");
                    out.write("\n        ");
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
    
    private boolean _jspx_meth_c_005fout_005f0(final JspTag _jspx_th_c_005fif_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f0 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f0_reused = false;
        try {
            _jspx_th_c_005fout_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f0.setParent((Tag)_jspx_th_c_005fif_005f2);
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.copyright_year}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f1(final JspTag _jspx_th_c_005fif_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f1 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f1_reused = false;
        try {
            _jspx_th_c_005fout_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f1.setParent((Tag)_jspx_th_c_005fif_005f2);
            _jspx_th_c_005fout_005f1.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_url}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f2(final JspTag _jspx_th_c_005fif_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f2 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f2_reused = false;
        try {
            _jspx_th_c_005fout_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f2.setParent((Tag)_jspx_th_c_005fif_005f2);
            _jspx_th_c_005fout_005f2.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_name}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (googleAuth_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        googleAuth_jsp._jspx_imports_packages.add("java.util");
        googleAuth_jsp._jspx_imports_packages.add("javax.servlet.http");
        googleAuth_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        googleAuth_jsp._jspx_imports_classes.add("java.util.Locale");
        googleAuth_jsp._jspx_imports_classes.add("com.adventnet.iam.xss.IAMEncoder");
        googleAuth_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.I18NUtil");
    }
}

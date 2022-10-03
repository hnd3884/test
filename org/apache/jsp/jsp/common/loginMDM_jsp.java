package org.apache.jsp.jsp.common;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.jasper.el.JspValueExpression;
import org.apache.taglibs.standard.tag.rt.core.ForEachTag;
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
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.common.MDMURLRedirection;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.Locale;
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

public final class loginMDM_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return loginMDM_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return loginMDM_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return loginMDM_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = loginMDM_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fchoose = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.release();
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.release();
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
        this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.release();
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
            final PageContext pageContext = _jspx_page_context = loginMDM_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n\n");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n\n\n\n\n<html>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
            out.write("\n        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\"/>");
            out.write("\n        <head>\n            <script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/IncludeJS.js?");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" type=\"text/javascript\"></script>\n            <script>includeMainScripts(\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(34);
            out.write(44);
            out.write(34);
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\");</script>\n            <link href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/common.css\" rel=\"stylesheet\" type=\"text/css\" />\n            <link href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/loginMDM.css?v2\" rel=\"stylesheet\" type=\"text/css\" />\n            <link href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/");
            if (this._jspx_meth_c_005fout_005f0(_jspx_page_context)) {
                return;
            }
            out.write("/style.css\" rel=\"stylesheet\" type=\"text/css\">\n                <link href=\"/images/favicon.ico\" rel=\"SHORTCUT ICON\"/>\n                <title>");
            if (this._jspx_meth_c_005fout_005f1(_jspx_page_context)) {
                return;
            }
            out.write("</title>\n                <!-- this below fw:productTag-check code/content will be executed/shown only for MSPCL -->\n                ");
            if (this._jspx_meth_fw_005fproductTag_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\n                <style type=\"text/css\">\n                    html,body{padding:0;margin:0;height:100%;width:100%; background-color: #f8f8f8;}\n\n                </style>\n                <script language=\"JavaScript\" type=\"text/JavaScript\" >\n                    function alertLayer(alertmsg)\n                    {\n                    showDialog('<table class=\"bodytext\" width=\"100%\" height=\"100%\"><tr><td rowspan=\"3\">&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\"/></td><td></td><td></td></tr><tr ><td colspan=\"2\"><span class=\"bodytext\">'+alertmsg+'</span></td></tr> <tr><td></td><td></td></tr> <tr><td align=\"center\" colspan=\"3\" class=\"\"><input type=\"button\" value=\"&nbsp;&nbsp;&nbsp;&nbsp;OK&nbsp;&nbsp;&nbsp;&nbsp;\" class=\"primaryActionBtn\" onclick=\"javascript:closeDialog(null,this);\" style=\"width:80;\"></td></tr></table>','modal=yes,width=400,height=110,position=absolute,left=400,top=200,title=");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.ALERT", new Object[0]));
            out.write("');//No i18n\n\n\n                    }\n                    function checkBrowser()\n                    {\n                    var userAgent = navigator.userAgent.toLowerCase();\n                    if(userAgent.match(\"msie\") == \"msie\")\n                    {\n                    browser = \"internet explorer\";//No I18N\n                    if(browser==\"internet explorer\")\n                    {\n                    version = userAgent.substring(userAgent.indexOf(\"msie\")+4,userAgent.lastIndexOf(\";\"));\n                    var ver =parseFloat(version);\n                    if(browser == \"internet explorer\" && ver < 5.5)\n                    {\n                    return true;\n                    }\n                    }\n                    }\n\n                    else if( userAgent.match(\"netscape\") == \"netscape\")\n                    {\n                    browser=\"Netscape\";//No I18N\n                    if(browser==\"Netscape\")\n                    {\n                    version = userAgent.substring(userAgent.indexOf(\"netscape\")+4,userAgent.lastIndexOf(\";\"));\n");
            out.write("                    var ver =parseFloat(version);\n                    if(browser == \"Netscape\" && version < 7.0)\n                    {\n                    return true;\n                    }\n                    }\n                    }\n\n                    else if(userAgent.match(\"mozilla\") == \"mozilla\")\n                    {\n\n                    browser = \"mozilla\";//No I18N\n                    if(browser==\"mozilla\")\n                    {\n                    version = userAgent.substring(userAgent.indexOf(\"rv:\")+3,userAgent.indexOf(\")\"));\n                    var ver = parseFloat(version);\n                    if(browser == \"mozilla\" && ver < 1.5)\n                    {\n                    return true;\n                    }\n                    }\n                    }\n                    else\n                    {\n                    return false;\n                    }\n\n                    }\n\n                    function checkForNull(form)\n                    {\n\n                    document.login.Button.disabled = true;\n");
            out.write("                    document.login.Button.value = \"\";\n                    document.login.Button.className = \"signin_btn signin_loading\";\n\n                    var browser = checkBrowser();\n                    if(browser == true)\n                    {\n                    var browserInfo = \"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.login.browser_not_supported", new Object[0]));
            out.write("\";\n                    alertLayer(browserInfo);\n                    }\n                    //to set charecter type\n                    document.getElementById(\"userName\").value = document.getElementById(\"tempUserName\").value.toLowerCase();\n                    if(document.login.j_username.value == \"\" || document.login.j_password.value == \"\")\n                    {\n                    alertLayer(\"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.login.enter_username_password", new Object[0]));
            out.write("\");\n                    if(document.login.j_username.value==\"\")\n                    {\n                    document.login.tempUserName.focus();\n                    }\n                    else\n                    {\n                    document.login.j_password.focus();\n                    }\n\n                    document.login.Button.disabled = false;\n                    document.login.Button.className = \"signin_btn\";\n                    document.login.Button.value = \"Sign in\"; //No I18N\n\n                    return false;\n                    }\n                    changeType();\n                    if(");
            out.write((String)PageContextImpl.proprietaryEvaluate("${licenseType==\"T\"}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(")\n                    {            \n                    var RequestDemoSkippedTime = readCookie(\"RequestDemoSkippedTime\");//No i18N\n                    var currentTime=Date.now();\n                    if((RequestDemoSkippedTime == null) || (RequestDemoSkippedTime != null && Math.abs(currentTime-RequestDemoSkippedTime>10800000)))\n                    {\n                    document.cookie = generateDMCookies(\"UserLoginTime\",Date.now());\n                    }                               \n                    }\n\n                    document.login.Button.disabled = false;\n                    document.login.Button.className = \"signin_btn\";\n                    document.login.Button.value = \"Sign in\"; //No I18N\n                    \n                    //storing location hash in localstorage for ember pages to get loaded after login\n                    //this is done because hash is not retained after login\n                    if(window.location.hash.indexOf('#/dc') != -1 || window.location.hash.indexOf('#/uems') != -1 || window.location.hash.indexOf('#/uems/msm') != -1 || window.location.hash.indexOf('#/patch-mgmt') != -1){\n");
            out.write("                        window.localStorage.setItem(\"dcEmberURL\", window.location.hash); //No I18N\n                    }\n                    //for SpiceWorksApp\n                    if(window.location.hash.indexOf('#/uems/spiceworks') != -1){\n                        window.localStorage.setItem(\"integSpiceWorkHashURL\", window.location.hash); //No I18N\n                    }\n                    return true;\n                    }\n\n                    function getCookie()\n                    {\n                    var admin_password_changed = \"");
            if (this._jspx_meth_c_005fout_005f2(_jspx_page_context)) {
                return;
            }
            out.write("\";\n                    var licenseType = \"");
            if (this._jspx_meth_c_005fout_005f3(_jspx_page_context)) {
                return;
            }
            out.write("\";\n                    if (licenseType == 'T' && admin_password_changed == 'false')\n                    {\n                    document.login.j_username.value=\"admin\";//No I18N\n                    document.login.j_password.value=\"admin\";//No I18N\n                    if(document.getElementById(\"domainName\") != null)\n                    {\n                    document.login.domainName.options[0].selected=true;\n                    }\n                    document.getElementById(\"resetHelp\").style.display = \"none\";\n                    }\n                    else\n                    {\n                    document.getElementById(\"adminHelp\").style.display = \"none\";\n                    }\n                    var usernamecookie = \"dc_username\";//No I18N\n                    var passwordcookie = \"dc_password\";//No I18N\n                    var authvaluecookie= \"dc_auth\";//No I18N\n                    init = (document.cookie).indexOf(\"dc_username\");\n                    if(init == -1)\n                    {\n                    init = (document.cookie).indexOf(\"username\");\n");
            out.write("                    usernamecookie = \"username\";//No I18N\n                    passwordcookie = \"password\";//No I18N\n                    authvaluecookie= \"auth\";//No I18N\n                    }\n\n                    if(init != -1 )\n                    {\n                    userlen = usernamecookie.length;\n                    beginIndex = ((document.cookie).indexOf(usernamecookie)+userlen);\n                    endIndex = (document.cookie).indexOf(\";\",beginIndex);\n                    if(endIndex == -1)\n                    {\n                    endIndex = (document.cookie).length;\n                    }\n                    username=(document.cookie).substring(beginIndex+1,endIndex);\n                    if(beginIndex+1 < endIndex)\n                    {\n                    document.login.j_username.value=username;\n                    }\n                    startIndex = ((document.cookie).indexOf(passwordcookie)+passwordcookie.length);\n                    endInd = (document.cookie).indexOf(\";\",startIndex);\n                    if(endInd == -1)\n");
            out.write("                    {\n                    endInd=(document.cookie).length;\n                    }\n                    password=(document.cookie).substring(startIndex+1,endInd);\n                    if(startIndex+1 < endInd)\n                    {\n                    document.login.j_password.value=password;\n                    }\n\n                    startIndex = ((document.cookie).indexOf(authvaluecookie)+authvaluecookie.length);\n                    endInd = (document.cookie).indexOf(\";\",startIndex);\n                    if(endInd == -1)\n                    {\n                    endInd=(document.cookie).length;\n                    }\n                    authvalue=(document.cookie).substring(startIndex+1,endInd);\n                    if(startIndex+1 < endInd)\n                    {\n                    if(document.getElementById(\"domainName\") != null)\n                    {\n                    for (var i = 0; i < document.login.domainName.length; i++)\n                    {\n                    if (document.login.domainName.options[i].value == authvalue)\n");
            out.write("                    {\n                    document.login.domainName.options[i].selected = true;\n                    }\n                    }\n                    }\n                    }\n                    document.getElementById(\"Button\").focus();\n                    }\n                    else\n                    {\n                    document.login.tempUserName.focus();\n                    }\n                    }\n\n                    function initValues()\n                    {\n                    getCookie();\n                    var errorMessage = \"");
            if (this._jspx_meth_c_005fout_005f4(_jspx_page_context)) {
                return;
            }
            out.write("\";\n                    if(errorMessage != \"\")\n                    {\n                    document.login.Button.disabled = true;\n                    document.login.Button.className = \"buttongrey\";\n                    }\n                    document.getElementById(\"tempUserName\").value = document.getElementById(\"userName\").value;\n                    showUpdateMessage();\n                    ");
            String loginStatus = (String)request.getAttribute("login_status");
            if (loginStatus != null && !loginStatus.isEmpty()) {
                loginStatus = loginStatus.toLowerCase();
                if (loginStatus.startsWith("no rows found for the table") || loginStatus.contains("exception")) {
                    loginStatus = "Invalid loginName/password";
                }
                pageContext.setAttribute("login_status", (Object)loginStatus);
            }
            out.write("\n                    var loginStatus=\"");
            if (this._jspx_meth_c_005fout_005f5(_jspx_page_context)) {
                return;
            }
            out.write("\";\n                    if(loginStatus.toLowerCase().indexOf(\"badlogin\")>-1){\n                    document.getElementById(\"badLogin\").style.display = \"block\";\n                    }else if(loginStatus){\n                    document.getElementById(\"loginFailure\").style.display = \"block\";\n                    }else{}\n\n                    }\n\n                    function closeInfo()\n                    {\n                    document.getElementById(\"logoutMessage\").style.display=\"none\";\n                    document.login.Button.focus();\n                    }\n\n                    function showDiv( value )\n                    {\n                    if(value == 'show')\n                    {\n                    document.getElementById(\"errorNotify\").style.display=\"block\";\n                    document.getElementById(\"errorNotifyLink\").href=\"javascript:showDiv('hide')\";\n                    //document.chkbox.focus();\n                    }\n                    else if(value == 'hide')\n                    {\n                    document.getElementById(\"errorNotify\").style.display=\"none\";\n");
            out.write("                    document.getElementById(\"errorNotifyLink\").href=\"javascript:showDiv('show')\";\n                    //document.chkbox.focus();\n                    }\n                    }\n\n                    function closeDialogMessage(elementID)\n                    {\n                    if(document.getElementById(elementID))\n                    {\n                    document.getElementById(elementID).style.display = \"none\";\n                    }\n                    if (elementID == \"flashNewsMessage\")\n                    {\n                    if(document.getElementById(\"updatesMessage\"))\n                    {\n                    document.getElementById(\"updatesMessage\").style.display = \"\";\n                    }\n                    }\n                    }\n\n                    function showUpdateMessage()\n                    {\n                    if (document.getElementById(\"flashNewsMessage\"))\n                    {\n                    document.getElementById(\"flashNewsMessage\").style.display = \"\";\n                    }\n");
            out.write("                    else if(document.getElementById(\"updatesMessage\"))\n                    {\n                    document.getElementById(\"updatesMessage\").style.display = \"\";\n                    }\n                    }\n                    function setFlashNewsDisplayStatus(disableFlashNews)\n                    {\n                    closeDialogMessage(\"flashNewsMessage\"); //No i18n\n                    var url ='/notifyDCUpdates?FLASH_NEWS_DISABLE='+disableFlashNews//No I18N\n                    if(window.XMLHttpRequest)\n                    {\n                    req = new XMLHttpRequest();\n                    req.onreadystatechange = processRespText;\n                    req.open(\"GET\", url, true);\n                    req.send(null);\n                    }\n                    else if(window.ActiveXObject)\n                    {\n                    req = new ActiveXObject(\"Microsoft.XMLHTTP\");\n                    if(req)\n                    {\n                    req.onreadystatechange = processRespText;\n                    req.open(\"GET\", url, true);\n");
            out.write("                    req.send();\n                    }\n                    }\n                    }\n\n                    function processRespText()\n                    {\n                    }\n\n                    function changeType()\n                    {\n                    if(document.getElementById(\"domainName\") != null)\n                    {\n                    var value =document.getElementById(\"domainName\").value;\n                    if(value == 'dclocal')\n                    {\n                    document.getElementById(\"AUTHRULE_NAME\").value = \"Authenticator\";//No I18N\n                    document.getElementById(\"domainName\").name = \"dummy\";//No I18N\n                    }\n                    else\n                    {\n                    document.getElementById(\"AUTHRULE_NAME\").value = \"ADAuthenticator\";//No I18N\n                    document.getElementById(\"domainName\").name = \"domainName\";\n                    }\n                    }\n                    }\n\n                </script>\n\n        </head>\n        <body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" onLoad=\"initValues()\">\n");
            out.write("            ");
            if (this._jspx_meth_c_005fif_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\n            ");
            if (this._jspx_meth_c_005fif_005f1(_jspx_page_context)) {
                return;
            }
            out.write("\n            ");
            final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f0_reused = false;
            try {
                _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n                ");
                        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f0_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isTwoFactorEnabledGlobaly=='true'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write(" \n                    <form name=\"login\" action=\"");
                                    out.print(response.encodeURL("two_fact_auth"));
                                    out.write("\" style=\"height:100%;\"  method=\"post\" AUTOCOMPLETE=\"off\" >\n                    ");
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
                        out.write("\n                    ");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f0_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            final int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f0 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("\n                        <form name=\"login\" action=\"");
                                    out.print(response.encodeURL("j_security_check"));
                                    out.write("\" style=\"height:100%;\"  method=\"post\" AUTOCOMPLETE=\"off\" >\n                        ");
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
                        out.write("\n                    ");
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
            out.write("\n\n                    <div id=\"refMsg\" style=\"display:none\" class=\"top\">\n\n                        <center>");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.login.browser_ref_msg", new Object[0]));
            out.write("</center>\n\n                    </div>                     \n                    <table width=\"100%\" height=\"100%\" border=\"0\"  cellspacing=\"0\" cellpadding=\"0\">                                           \n                        <tr>\n                            <td style=\"vertical-align:middle;padding-top: 10px\">\n                                ");
            final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f2_reused = false;
            try {
                _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f2.setParent((Tag)null);
                _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${updateMsg != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
                if (_jspx_eval_c_005fif_005f2 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n                                    <div id=\"update_message\">\n                                        <table width=\"965\" cellspacing='0' cellpadding='0' border='0'align='center' class='grid' style=\"margin-bottom:15px;\">\n                                            <!-- <tr>\n                                                 <td width='6' height='6' class='topleft'> </td>\n                                                 <td class='topcenter'> </td>\n                                                 <td width='6' class='topright'> </td>\n                                             </tr>-->\n                                            <tr>\n                                                <!--<td class='leftcenter'> </td>-->\n                                                <td class=\"update_message_style\">\n                                                    <table width='100%' cellspacing='0' cellpadding='0' border='0'>\n                                                        <tr><td width=\"8%\" align=\"center\"><img src=\"/images/login/update_icon.gif\" align=\"absmiddle\"></td>\n");
                        out.write("                                                            <td width=\"92%\">\n                                                                <strong>");
                        if (this._jspx_meth_c_005fif_005f3((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("</strong>\n\n                                                                <div class=\"updatetxt\">\n                                                                    ");
                        if (this._jspx_meth_c_005fout_005f7((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n                                                                    ");
                        final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f4_reused = false;
                        try {
                            _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f3.setParent((Tag)_jspx_th_c_005fif_005f2);
                            _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${updateURL != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
                            if (_jspx_eval_c_005fif_005f3 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("\n                                                                        &nbsp;&nbsp;<a href='");
                                    if (this._jspx_meth_c_005fout_005f8((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("' target=\"_blank\">");
                                    out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.download_now", new Object[0]));
                                    out.write("</a>\n                                                                    ");
                                    evalDoAfterBody2 = _jspx_th_c_005fif_005f3.doAfterBody();
                                } while (evalDoAfterBody2 == 2);
                            }
                            if (_jspx_th_c_005fif_005f3.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f3);
                            _jspx_th_c_005fif_005f4_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f4_reused);
                        }
                        out.write("\n                                                                </div>\n                                                            </td>\n                                                            <!--td width=\"2%\"><img src=\"/images/dialogClose.gif\" align=\"absmiddle\" style=\"cursor:pointer\" title=\"Close\" onclick=\"javascript:closeDialog('flashNewsMessage')\"></td-->\n                                                        </tr></table>\n                                                </td>\n                                                <!--<td class='rightcenter'> </td>-->\n                                            </tr>\n                                            <!--<tr>\n                                                <td width='6' height='6' class='bottomleft'> </td>\n                                                <td class='bottomcenter'> </td>\n                                                <td width='6' height='6' class='bottomright'> </td>\n                                            </tr>-->\n                                        </table>\n");
                        out.write("                                    ");
                        evalDoAfterBody3 = _jspx_th_c_005fif_005f2.doAfterBody();
                    } while (evalDoAfterBody3 == 2);
                }
                if (_jspx_th_c_005fif_005f2.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f2);
                _jspx_th_c_005fif_005f2_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f2_reused);
            }
            out.write("\n                                    ");
            if (this._jspx_meth_c_005fif_005f5(_jspx_page_context)) {
                return;
            }
            out.write(" \n                                    ");
            if (this._jspx_meth_c_005fif_005f6(_jspx_page_context)) {
                return;
            }
            out.write("\n                                    <div style=\"width:100%;\">\n                                        <div id=\"login_content\">\n                                            <div class=\"login_top_container\" style=\"padding-bottom: 10px;\" >\n                                                <div id=\"login_top\">\n                                                    <p>&nbsp;</p>\n                                                    <a target=\"_blank\" href=\"");
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
            out.write("\"><div class=\"topLink6\"> &nbsp; </div></a>\n\n                                                </div>\n\n                                            </div>\n                                            <div ");
            if (this._jspx_meth_fw_005fproductTag_005f1(_jspx_page_context)) {
                return;
            }
            out.write("\n                                                                                                  ");
            if (this._jspx_meth_fw_005fproductTag_005f2(_jspx_page_context)) {
                return;
            }
            out.write(" >\n                                                <div ");
            if (this._jspx_meth_fw_005fproductTag_005f3(_jspx_page_context)) {
                return;
            }
            out.write("\n                                                                                                      ");
            if (this._jspx_meth_fw_005fproductTag_005f4(_jspx_page_context)) {
                return;
            }
            out.write(">\n                                                                                                          <div style=\"text-align:center\" class=\"img_caption\"> \n                                                                                                              <span class=\"capt_normal\">");
            out.print(I18N.getMsg("dc.mdm.device_mgmt.mobile_device_management", new Object[0]));
            out.write(" \n                                                                                                              ");
            out.print(I18N.getMsg("dc.common.SOFTWARE", new Object[0]));
            out.write(" </span>      \n                                                                                                      </div>\n                                                                                                      <div id=\"signin_form\">\n                                                                                                          <div class=\"signin_form_fill\">\n                                                                                                              <table width=\"100%\" height=\"260\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" style=\"font-weight:bold\">\n                                                                                                                  <tr>\n                                                                                                                      <td>\n\n                                                                                                                          <input id=\"userName\" name=\"j_username\" type=\"text\"   style=\"display:none;\"  class=\"userNameField\" />\n");
            out.write("                                                                                                                          <input id=\"tempUserName\" type=\"text\" size=\"22\" class=\"tempUserNameField\" tabindex=\"1\"  />\n                                                                                                                          <input name=\"j_password\" size=\"22\" type=\"password\" class=\"passwordField\" tabindex=\"2\" />\n                                                                                                                          <input type=\"hidden\" name=\"otpTimeout\" id=\"otpTimeout\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${otpTimeout}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\">\n                                                                                                                                     \n                                                                                                                                  ");
            if (this._jspx_meth_c_005fif_005f7(_jspx_page_context)) {
                return;
            }
            out.write("\n                                                                                                                                      <div id=\"firstTimeHelp\"><span id=\"adminHelp\"> ");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.login.first_time_users_suggestion", new Object[] { "admin", "admin" }));
            out.write("</span>\n                                                                                                                                      </div>\n\n                                                                                                                                      <div id=\"badLogin\" style=\"display:none;padding: 5px 0 5px 0;\">\n                                                                                                                                          <span class=\"signin_error\">");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "mdm.common.login.account_locked", new Object[0]));
            out.write("</span>\n                                                                                                                                      </div>\n                                                                                                                                      <div id=\"loginFailure\" style=\"display:none;padding: 5px 0 5px 0;\">\n                                                                                                                                          <span class=\"signin_error\">");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.login.invalid_username_password", new Object[0]));
            out.write("</span>\n                                                                                                                                      </div>\n\n                                                                                                                                      <input type=\"submit\" name=\"Button\" id=\"Button\" onClick=\"return checkForNull(this.form)\" align=\"right\" class=\"signin_btn\" Value=\"Sign in\" tabindex=\"4\" />  <span id=\"resetHelp\">  <a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/how-to/reset-password.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\" tabindex=\"5\" > Forgot Password?</a></span> ");
            out.write("\n                                                                                                                                      <div id=\"dropmenudiv\" style=\"position: absolute; z-index: 1; width: 280px; left: 200px; top: 23px; visibility: hidden;\" onMouseOver=\"clearhidemenu()\" onMouseOut=\"dynamichide(event)\"></div>\n\n\n                                                                                                                                      </td>\n\n                                                                                                                                      </tr>\n                                                                                                                                      </table>\n\n                                                                                                                                      </div>\n                                                                                                                                      </div>\n");
            out.write("                                                                                                                                      </div>\n                                                                                                                                      </div>\n\n                                                                                                                                    \n                                                                                                                                          <div class=\"login_links_containter\">\n                                                                                                                                              <div id=\"login_links\">\n                                                                                                                                                  <div class=\"login_links_left\"></div>\n                                                                                                                                                  <div class=\"login_links_content\">\n");
            out.write("                                                                                                                                                      <div class=\"login_links_ql\">\n                                                                                                                                                          <h4> ");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.QUICK_LINKS", new Object[0]));
            out.write("</h4>\n                                                                                                                                                          <ul>\n                                                                                                                                                              <li> <img src=\"/images/login/quicktour.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/features.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("desktopcentral.common.login.quick_tour", new Object[0]));
            out.write("</a></li>\n                                                                                                                                                              <li  ><img src=\"/images/login/demoregistration.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/request-demo.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("desktopcentral.common.Register_Free_Demo", new Object[0]));
            out.write("</a></li>\n                                                                                                                                                              <li  ><img src=\"/images/login/kb.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/knowledge-base.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("dc.common.KNOWLEDGE_BASE", new Object[0]));
            out.write("</a></li>\n                                                                                                                                                              <li ><img src=\"/images/login/pricequote.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;<a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/get-quote.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg("desktopcentral.webclient.common.contactus.get_price_quote", new Object[0]));
            out.write("</a></li>\n                                                                                                                                                          </ul>\n                                                                                                                                                      </div>\n\n                                                                                                                                                      <div class=\"login_links_contact\">\n                                                                                                                                                          <h4>");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.contact_us", new Object[0]));
            out.write("</h4>\n                                                                                                                                                          <ul>\n                                                                                                                                                              <li>\n                                                                                                                                                                  <img src=\"/images/login/website.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp; <a href=\"");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("desktopcentral_today"), new Object[0]));
            out.write(63);
            out.write(112);
            out.write(61);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("website"), new Object[0]));
            out.write("</a></li>");
            out.write("\n                                                                                                                                                              <li>\n                                                                                                                                                                  <img src=\"/images/login/email.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp; <a href=\"mailto:");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
            out.write("\" target=\"_blank\">");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
            out.write("</a></li>");
            out.write("\n                                                                                                                                                              <li><img src=\"/images/login/contact.png\" width=\"19\" height=\"20\" align=\"absmiddle\"/>&nbsp;   ");
            out.print(I18N.getMsg("dc.common.toll_free_number", new Object[0]));
            out.write("</li>\n                                                                                                                                                          </ul>\n                                                                                                                                                      </div>\n                                                                                                                                                      <div class=\"login_links_relatedprod\">\n                                                                                                                                                          <h4>");
            out.print(I18N.getMsg("desktopcentral.common.login.related_products", new Object[0]));
            out.write("</h4>\n\n                                                                                                                                                          <a href=\"http://www.manageengine.com/products/desktop-central/index.html?p=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" target=\"_blank\" style=\"width: 300px;display: inline-block;padding: 5px 0px;\"><span style=\"display:inline-block;vertical-align: middle;width:150px;\"><img src=\"/images/login/ME-DC.svg\" width=\"112\" height=\"30\" border=\"0\" style=\"padding-top:5px;\"></span><span class=\"productHint\">");
            out.print(I18N.getMsg("mdm.relatedProducts.dc", new Object[0]));
            out.write("</span></a>                                                                                                                                                                                                                                                                                                                               <br>\n                                                                                                                                                            <a href=\"https://www.manageengine.com/secure-browser/?mdmplogin\" target=\"_blank\" style=\"width: 300px;display: inline-block;\"><span style=\"display:inline-block;vertical-align: middle;padding: 5px 0px;\"><img src=\"/images/login/bsp.svg\" width=\"150\" height=\"48\" border=\"0\" style=\"padding-top:5px;padding-left:2px;\"></span><span class=\"productHint\">");
            out.print(I18N.getMsg("mdm.relatedProducts.bsp", new Object[0]));
            out.write("</span></a>\n\n                                                                                                                                                      </div>\n\n                                                                                                                                                  </div>\n\n                                                                                                                                              </div>\n                                                                                                                                          </div>\n                                                                                                                                   \n\n                                                                                                                                      <div id=\"browser_info\">");
            out.print(I18N.getMsg("dc.admin.rebranding.copyright", new Object[0]));
            out.write(" &copy; ");
            if (this._jspx_meth_c_005fout_005f12(_jspx_page_context)) {
                return;
            }
            out.write(" <a href=\"");
            if (this._jspx_meth_c_005fout_005f13(_jspx_page_context)) {
                return;
            }
            out.write("\" target=\"_blank\">");
            if (this._jspx_meth_c_005fout_005f14(_jspx_page_context)) {
                return;
            }
            out.write("</a>&nbsp;");
            out.print(I18N.getMsg("desktopcentral.common.login.all_rights_reserved", new Object[0]));
            out.write("</div>");
            out.write("\n\n                                                                                                                                      </div>\n                                                                                                                                      </div>\n                                                                                                                                      </td>\n                                                                                                                                      </tr>\n                                                                                                                                      </table>\n                                                                                                                                      <input type=\"hidden\" name=\"cacheNum\" id=\"cacheNum\" value=\"");
            if (this._jspx_meth_c_005fout_005f15(_jspx_page_context)) {
                return;
            }
            out.write("\"/>\n                                                                                                                                      <input type=\"hidden\" name=\"loginPageCsrfPreventionSalt\" value=\"");
            if (this._jspx_meth_c_005fout_005f16(_jspx_page_context)) {
                return;
            }
            out.write("\"/>\n                                                                                                                                      </form>\n\n                                                                                                                                      </body>\n                                                                                                                                      </html>\n\n                                                                                                                                      <script>\n                                                                                                                                          jQuery('form').submit(function (e) {                           //No I18N\n                                                                                                                                              jQuery(':input[type=submit]').prop('disabled', true);    //No I18N\n                                                                                                                                              jQuery(':input[type=submit]').prop('value', '');         //No I18N\n");
            out.write("                                                                                                                                              jQuery('input[type=submit]').removeClass(\"signin_btn\").addClass(\"signin_loading\");\n                                                                                                                                          });\n                                                                                                                                      </script>\n\n                                                                                                                                      <script language=\"JavaScript\" type=\"text/JavaScript\" >\n                                                                                                                                          function createRefMsgCookie() \n                                                                                                                                          { \n                                                                                                                                          //clearCookie(\"showRefMsg\");\n");
            out.write("                                                                                                                                          //clearCookie(\"cacheNum\");\n                                                                                                                                          var curentCacheNum = document.getElementById(\"cacheNum\").value;\n                                                                                                                                          var cookieCacheNum = readCookie(\"cacheNum\");//No I18N\n                                                                                                                                          var cookieStatus = readCookie(\"showRefMsg\");//No I18N \n                                                                                                                                          var cacheNum = \"cacheNum = \"+curentCacheNum;//No I18N\n                                                                                                                                          var refMsgStatus = \"showRefMsg = \"; //No I18N        \n");
            out.write("                                                                                                                                          var date = new Date();\n                                                                                                                                          date.setDate(date.getDate() + 365);\n                                                                                                                                          var expires = \"expires=\"+date.toGMTString(); //No I18N  \n                                                                                                                                          if(readCookie(\"buildNum\") != null)\n                                                                                                                                          {\n                                                                                                                                          clearCookie(\"buildNum\");//No I18N\n                                                                                                                                          document.cookie = generateDMCookies(\"cacheNum\",curentCacheNum)+expires+\"; path=/\";\n");
            out.write("                                                                                                                                          document.getElementById(\"refMsg\").style.display = \"\";//No I18N\n                                                                                                                                          document.cookie = generateDMCookies(\"showRefMsg\",\"true\")+expires+\"; path=/\";\n                                                                                                                                          }\n                                                                                                                                          else if(cookieCacheNum == null && cookieStatus == null)\n                                                                                                                                          {\n                                                                                                                                          var status =\"false\";//No I18N\n");
            out.write("                                                                                                                                          document.cookie = generateDMCookies(\"cacheNum\",curentCacheNum)+expires+\"; path=/\";\n                                                                                                                                          document.cookie = generateDMCookies(\"showRefMsg\",status)+expires+\"; path=/\";\n                                                                                                                                          }\n                                                                                                                                          else if(cookieCacheNum != null && cookieStatus != null)\n                                                                                                                                          {\n                                                                                                                                          document.cookie = generateDMCookies(\"cacheNum\",curentCacheNum)+expires+\"; path=/\";\n");
            out.write("                                                                                                                                          if(cookieCacheNum != curentCacheNum )\n                                                                                                                                          {\n                                                                                                                                          document.getElementById(\"refMsg\").style.display = \"\";//No I18N \n                                                                                                                                          document.cookie = generateDMCookies(\"showRefMsg\",\"true\")+expires+\"; path=/\";             \n                                                                                                                                          }       \n                                                                                                                                          else if(cookieCacheNum == curentCacheNum && cookieStatus == \"true\")\n");
            out.write("                                                                                                                                          {\n                                                                                                                                          document.getElementById(\"refMsg\").style.display = \"\";//No I18N \n                                                                                                                                          }           \n                                                                                                                                          }\n                                                                                                                                          }\n\n                                                                                                                                          function updateRefMsgCookie(cacheNum,refMsgStatus)\n                                                                                                                                          {\n");
            out.write("                                                                                                                                          var date = new Date();\n                                                                                                                                          date.setDate(date.getDate() + 365);\n                                                                                                                                          var expires = \"expires=\"+date.toGMTString(); //No I18N\n                                                                                                                                          document.cookie = generateDMCookies(\"cacheNum\",cacheNum)+expires+\"; path=/\";\n                                                                                                                                          document.cookie = generateDMCookies(\"showRefMsg\",status)+expires+\"; path=/\";\n                                                                                                                                          }\n");
            out.write("\n                                                                                                                                          function readCookie(name) {\n                                                                                                                                          var nameEQ = name + \"=\";\n                                                                                                                                          var ca = document.cookie.split(';');\n                                                                                                                                          for(var i=0;i < ca.length;i++) \n                                                                                                                                          {\n                                                                                                                                          var c = ca[i];\n                                                                                                                                          while (c.charAt(0)==' ') c = c.substring(1,c.length);\n");
            out.write("                                                                                                                                          if (c.indexOf(nameEQ) == 0) \n                                                                                                                                          return c.substring(nameEQ.length,c.length);\n                                                                                                                                          }\n                                                                                                                                          return null;\n                                                                                                                                          }\n\n\n                                                                                                                                          function clearCookie(name) {\n                                                                                                                                          var date=new Date();\n");
            out.write("                                                                                                                                          date.setDate(date.getDate()-1);\n                                                                                                                                          document.cookie = generateDMCookies(name,\"\")+\"expires=\" + date + \"; path=/\";\n                                                                                                                                          }\n                                                                                                                                          function closeRefMsg()\n                                                                                                                                          {\n                                                                                                                                          document.getElementById(\"refMsg\").style.display = \"none\";\n                                                                                                                                          updateRefMsgCookie(document.getElementById(\"cacheNum\").value,'false');\n");
            out.write("                                                                                                                                          }\n                                                                                                                                          createRefMsgCookie();\n                                                                                                                                      </script>\n");
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
            loginMDM_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
    
    private boolean _jspx_meth_c_005fout_005f1(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f1 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f1_reused = false;
        try {
            _jspx_th_c_005fout_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f1.setParent((Tag)null);
            _jspx_th_c_005fout_005f1.setValue(PageContextImpl.proprietaryEvaluate("${generalProperties.title}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_fw_005fproductTag_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f0 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f0_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f0.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f0.setParent((Tag)null);
            _jspx_th_fw_005fproductTag_005f0.setProductCode("MSPCL");
            _jspx_th_fw_005fproductTag_005f0.setShow(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fproductTag_005f0 = _jspx_th_fw_005fproductTag_005f0.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                    <script>window.location = \"/MspCenterHome.do\";</script>\n                ");
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
    
    private boolean _jspx_meth_c_005fout_005f2(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f2 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f2_reused = false;
        try {
            _jspx_th_c_005fout_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f2.setParent((Tag)null);
            _jspx_th_c_005fout_005f2.setValue(PageContextImpl.proprietaryEvaluate("${isPasswordChanged}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
            _jspx_th_c_005fout_005f3.setValue(PageContextImpl.proprietaryEvaluate("${licenseType}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f4(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f4 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f4_reused = false;
        try {
            _jspx_th_c_005fout_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f4.setParent((Tag)null);
            _jspx_th_c_005fout_005f4.setValue(PageContextImpl.proprietaryEvaluate("${errorMessage}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f4 = _jspx_th_c_005fout_005f4.doStartTag();
            if (_jspx_th_c_005fout_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f4);
            _jspx_th_c_005fout_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f5(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f5 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f5_reused = false;
        try {
            _jspx_th_c_005fout_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f5.setParent((Tag)null);
            _jspx_th_c_005fout_005f5.setValue(PageContextImpl.proprietaryEvaluate("${login_status}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f5 = _jspx_th_c_005fout_005f5.doStartTag();
            if (_jspx_th_c_005fout_005f5.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f5);
            _jspx_th_c_005fout_005f5_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f5_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f0_reused = false;
        try {
            _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f0.setParent((Tag)null);
            _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${licenseType == \"R\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
            if (_jspx_eval_c_005fif_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                <div class=\"dummyContainerCust\">&nbsp;</div>\n            ");
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
    
    private boolean _jspx_meth_c_005fif_005f1(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f1 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f1_reused = false;
        try {
            _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f1.setParent((Tag)null);
            _jspx_th_c_005fif_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${licenseType != \"R\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
            if (_jspx_eval_c_005fif_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                <div class=\"dummyContainerEval\">&nbsp;</div>\n            ");
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
    
    private boolean _jspx_meth_c_005fif_005f3(final JspTag _jspx_th_c_005fif_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f3_reused = false;
        try {
            _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f3.setParent((Tag)_jspx_th_c_005fif_005f2);
            _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${updateTitle != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
            if (_jspx_eval_c_005fif_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                                                        <span class=\"headingtext\">&nbsp;");
                    if (this._jspx_meth_c_005fout_005f6((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                        return true;
                    }
                    out.write("</span>\n                                                                    ");
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
    
    private boolean _jspx_meth_c_005fout_005f6(final JspTag _jspx_th_c_005fif_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f6 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f6_reused = false;
        try {
            _jspx_th_c_005fout_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f6.setParent((Tag)_jspx_th_c_005fif_005f3);
            _jspx_th_c_005fout_005f6.setValue(PageContextImpl.proprietaryEvaluate("${updateTitle}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f6 = _jspx_th_c_005fout_005f6.doStartTag();
            if (_jspx_th_c_005fout_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f6);
            _jspx_th_c_005fout_005f6_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f6_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f7(final JspTag _jspx_th_c_005fif_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f7 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f7_reused = false;
        try {
            _jspx_th_c_005fout_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f7.setParent((Tag)_jspx_th_c_005fif_005f2);
            _jspx_th_c_005fout_005f7.setValue(PageContextImpl.proprietaryEvaluate("${updateMsg}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f7 = _jspx_th_c_005fout_005f7.doStartTag();
            if (_jspx_th_c_005fout_005f7.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f7);
            _jspx_th_c_005fout_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f8(final JspTag _jspx_th_c_005fif_005f4, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f8 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f8_reused = false;
        try {
            _jspx_th_c_005fout_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f8.setParent((Tag)_jspx_th_c_005fif_005f4);
            _jspx_th_c_005fout_005f8.setValue(PageContextImpl.proprietaryEvaluate("${updateURL}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f8 = _jspx_th_c_005fout_005f8.doStartTag();
            if (_jspx_th_c_005fout_005f8.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f8);
            _jspx_th_c_005fout_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f5(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final HttpServletRequest request = (HttpServletRequest)_jspx_page_context.getRequest();
        final HttpServletResponse response = (HttpServletResponse)_jspx_page_context.getResponse();
        final IfTag _jspx_th_c_005fif_005f5 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f5_reused = false;
        try {
            _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f5.setParent((Tag)null);
            _jspx_th_c_005fif_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${flashShowStatus != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
            if (_jspx_eval_c_005fif_005f5 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                        <div id=\"flashNewsMessage\">\n                                            ");
                    JspRuntimeLibrary.include((ServletRequest)request, (ServletResponse)response, "/images/flashmsg/flashMsg.html", out, false);
                    out.write("\n                                        </div>\n                                    ");
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
    
    private boolean _jspx_meth_c_005fif_005f6(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f6_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)null);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${!empty errorMessage}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
            if (_jspx_eval_c_005fif_005f6 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                        <table width=\"950\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:15px;\">\n                                            <tr>\n                                                <td height=\"25\" align=\"center\"><span class=\"bodyboldred\">");
                    if (this._jspx_meth_c_005fout_005f9((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                        return true;
                    }
                    out.write("</span></td>\n                                            </tr>\n                                        </table>\n                                    ");
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
    
    private boolean _jspx_meth_c_005fout_005f9(final JspTag _jspx_th_c_005fif_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f9 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f9_reused = false;
        try {
            _jspx_th_c_005fout_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f9.setParent((Tag)_jspx_th_c_005fif_005f6);
            _jspx_th_c_005fout_005f9.setValue(PageContextImpl.proprietaryEvaluate("${errorMessage}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f9 = _jspx_th_c_005fout_005f9.doStartTag();
            if (_jspx_th_c_005fout_005f9.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f9);
            _jspx_th_c_005fout_005f9_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f9_reused);
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
            _jspx_th_fw_005fproductTag_005f1.setShow(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fproductTag_005f1 = _jspx_th_fw_005fproductTag_005f1.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signin_band_container_msp\"");
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
            _jspx_th_fw_005fproductTag_005f2.setShow(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fproductTag_005f2 = _jspx_th_fw_005fproductTag_005f2.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signin_band_container\"");
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
            _jspx_th_fw_005fproductTag_005f3.setShow(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fproductTag_005f3 = _jspx_th_fw_005fproductTag_005f3.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("id=\"signin_band_msp\"");
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
    
    private boolean _jspx_meth_fw_005fproductTag_005f4(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCProductTag _jspx_th_fw_005fproductTag_005f4 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
        boolean _jspx_th_fw_005fproductTag_005f4_reused = false;
        try {
            _jspx_th_fw_005fproductTag_005f4.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fproductTag_005f4.setParent((Tag)null);
            _jspx_th_fw_005fproductTag_005f4.setProductCode("MDMPMSP");
            _jspx_th_fw_005fproductTag_005f4.setShow(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fproductTag_005f4 = _jspx_th_fw_005fproductTag_005f4.doStartTag();
            if (_jspx_eval_fw_005fproductTag_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("id=\"signin_band\"");
                    evalDoAfterBody = _jspx_th_fw_005fproductTag_005f4.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fproductTag_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.reuse((Tag)_jspx_th_fw_005fproductTag_005f4);
            _jspx_th_fw_005fproductTag_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fproductTag_005f4, this._jsp_getInstanceManager(), _jspx_th_fw_005fproductTag_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f7(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f7 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f7_reused = false;
        try {
            _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f7.setParent((Tag)null);
            _jspx_th_c_005fif_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${loginDomainList != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
            if (_jspx_eval_c_005fif_005f7 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                                                                                                                      <div class=\"customizedSelect\">\n                                                                                                                                          <select id=\"domainName\" class=\"txtbox domainNameCombo\" size=\"1\" name=\"domainName\" tabindex=\"3\">\n                                                                                                                                              <option value=\"dclocal\" >Local Authentication</option>");
                    out.write("\n                                                                                                                                              ");
                    if (this._jspx_meth_c_005fforEach_005f0((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                                                                                                                                          </select>\n                                                                                                                                      </div>\n                                                                                                                                      <input type=\"hidden\" name=\"AUTHRULE_NAME\" id=\"AUTHRULE_NAME\" value=\"ADAuthenticator\">\n                                                                                                                                      ");
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
    
    private boolean _jspx_meth_c_005fforEach_005f0(final JspTag _jspx_th_c_005fif_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        JspWriter out = _jspx_page_context.getOut();
        final ForEachTag _jspx_th_c_005fforEach_005f0 = (ForEachTag)this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.get((Class)ForEachTag.class);
        boolean _jspx_th_c_005fforEach_005f0_reused = false;
        try {
            _jspx_th_c_005fforEach_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fforEach_005f0.setParent((Tag)_jspx_th_c_005fif_005f7);
            _jspx_th_c_005fforEach_005f0.setVar("domainVal");
            _jspx_th_c_005fforEach_005f0.setItems(new JspValueExpression("/jsp/common/loginMDM.jsp(474,142) '${loginDomainList}'", this._jsp_getExpressionFactory().createValueExpression(_jspx_page_context.getELContext(), "${loginDomainList}", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            _jspx_th_c_005fforEach_005f0.setVarStatus("status");
            final int[] _jspx_push_body_count_c_005fforEach_005f0 = { 0 };
            try {
                final int _jspx_eval_c_005fforEach_005f0 = _jspx_th_c_005fforEach_005f0.doStartTag();
                if (_jspx_eval_c_005fforEach_005f0 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n                                                                                                                                                  <option value=\"");
                        if (this._jspx_meth_c_005fout_005f10((JspTag)_jspx_th_c_005fforEach_005f0, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f0)) {
                            return true;
                        }
                        out.write("\"   ");
                        if (this._jspx_meth_c_005fif_005f8((JspTag)_jspx_th_c_005fforEach_005f0, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f0)) {
                            return true;
                        }
                        out.write(" >\n                                                                                                                                                      ");
                        if (this._jspx_meth_c_005fout_005f11((JspTag)_jspx_th_c_005fforEach_005f0, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f0)) {
                            return true;
                        }
                        out.write("\n                                                                                                                                                  </option>\n                                                                                                                                              ");
                        evalDoAfterBody = _jspx_th_c_005fforEach_005f0.doAfterBody();
                    } while (evalDoAfterBody == 2);
                }
                if (_jspx_th_c_005fforEach_005f0.doEndTag() == 5) {
                    return true;
                }
            }
            catch (final Throwable _jspx_exception) {
                while (_jspx_push_body_count_c_005fforEach_005f0[0]-- > 0) {
                    out = _jspx_page_context.popBody();
                }
                _jspx_th_c_005fforEach_005f0.doCatch(_jspx_exception);
            }
            finally {
                _jspx_th_c_005fforEach_005f0.doFinally();
            }
            this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.reuse((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fforEach_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fforEach_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fforEach_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f10(final JspTag _jspx_th_c_005fforEach_005f0, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f0) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f10 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f10_reused = false;
        try {
            _jspx_th_c_005fout_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f10.setParent((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fout_005f10.setValue(PageContextImpl.proprietaryEvaluate("${domainVal.value}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f10 = _jspx_th_c_005fout_005f10.doStartTag();
            if (_jspx_th_c_005fout_005f10.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f10);
            _jspx_th_c_005fout_005f10_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f10_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f8(final JspTag _jspx_th_c_005fforEach_005f0, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f0) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f8 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f8_reused = false;
        try {
            _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f8.setParent((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fif_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${defaultDomainSelect == domainVal.value}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
            if (_jspx_eval_c_005fif_005f8 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected=\"selected\" ");
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
    
    private boolean _jspx_meth_c_005fout_005f11(final JspTag _jspx_th_c_005fforEach_005f0, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f0) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f11 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f11_reused = false;
        try {
            _jspx_th_c_005fout_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f11.setParent((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fout_005f11.setValue(PageContextImpl.proprietaryEvaluate("${domainVal.key}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f11 = _jspx_th_c_005fout_005f11.doStartTag();
            if (_jspx_th_c_005fout_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f11);
            _jspx_th_c_005fout_005f11_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f11_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f12(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f12 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f12_reused = false;
        try {
            _jspx_th_c_005fout_005f12.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f12.setParent((Tag)null);
            _jspx_th_c_005fout_005f12.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.copyright_year}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f12 = _jspx_th_c_005fout_005f12.doStartTag();
            if (_jspx_th_c_005fout_005f12.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f12);
            _jspx_th_c_005fout_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f13(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f13 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f13_reused = false;
        try {
            _jspx_th_c_005fout_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f13.setParent((Tag)null);
            _jspx_th_c_005fout_005f13.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_url}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f13 = _jspx_th_c_005fout_005f13.doStartTag();
            if (_jspx_th_c_005fout_005f13.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f13);
            _jspx_th_c_005fout_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f14(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f14 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f14_reused = false;
        try {
            _jspx_th_c_005fout_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f14.setParent((Tag)null);
            _jspx_th_c_005fout_005f14.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_name}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f14 = _jspx_th_c_005fout_005f14.doStartTag();
            if (_jspx_th_c_005fout_005f14.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f14);
            _jspx_th_c_005fout_005f14_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f14_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f15(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f15 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f15_reused = false;
        try {
            _jspx_th_c_005fout_005f15.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f15.setParent((Tag)null);
            _jspx_th_c_005fout_005f15.setValue(PageContextImpl.proprietaryEvaluate("${cachenumber}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f15 = _jspx_th_c_005fout_005f15.doStartTag();
            if (_jspx_th_c_005fout_005f15.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f15);
            _jspx_th_c_005fout_005f15_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f15_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f16(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f16 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f16_reused = false;
        try {
            _jspx_th_c_005fout_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f16.setParent((Tag)null);
            _jspx_th_c_005fout_005f16.setValue(PageContextImpl.proprietaryEvaluate("${loginPageCsrfPreventionSalt}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f16 = _jspx_th_c_005fout_005f16.doStartTag();
            if (_jspx_th_c_005fout_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f16);
            _jspx_th_c_005fout_005f16_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f16_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (loginMDM_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        loginMDM_jsp._jspx_imports_packages.add("javax.servlet.http");
        loginMDM_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        loginMDM_jsp._jspx_imports_classes.add("com.me.mdm.server.common.MDMURLRedirection");
        loginMDM_jsp._jspx_imports_classes.add("java.util.Locale");
        loginMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        loginMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
        loginMDM_jsp._jspx_imports_classes.add("com.adventnet.iam.xss.IAMEncoder");
        loginMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.I18NUtil");
    }
}

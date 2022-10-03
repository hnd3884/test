package org.apache.jsp.jsp.common;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
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
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.mdm.server.common.MDMURLRedirection;
import com.adventnet.iam.xss.IAMEncoder;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.Locale;
import com.adventnet.i18n.I18N;
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

public final class loginAWS_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return loginAWS_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return loginAWS_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return loginAWS_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = loginAWS_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.release();
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.release();
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
            response.setContentType("text/html; charset=UTF-8");
            final PageContext pageContext = _jspx_page_context = loginAWS_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n\n");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n\n\n\n\n");
            out.write("<script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/common.js?cid=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"></script>\n<script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/overlib.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMAjaxAPI.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMSecurity.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/footer.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/jquery/jquery.min.js?");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/prototype.js\"></script>\n<script src=\"js/security.js\" type=\"text/javascript\"></script>\n\n<script>\n    csrfParamName='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${csrfParamName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    cookieName='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${cookieName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    isSAS='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isSAS}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    mdmUrl='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    CSRFParamName=csrfParamName;\n    CSRFParamValue=getCSRFCookie(cookieName);\n</script>\n<script>\n jQuery(function () {\n    attachDropDown();\n});\nfunction attachDropDown()\n{\njQuery('.showSingle:not(.bound)').addClass('bound').click(function () {//No I18N\n        var index = jQuery(this).index('.showSingle'),//No I18N\n            newTarget = jQuery('.targetDiv').eq(index);//No I18N\n        //console.log(index);\n        //console.log(newTarget);\n        jQuery('.targetDiv').not(newTarget).slideUp(0)//No I18N\n        newTarget.slideToggle(150);\n        //to hide on body click\n        show=index;\n        return false;\n    });\n}\nfunction openURLQuickLoad (url){\n    // check and remove this function\n   if(typeof useQuickLoad!= 'undefined' && useQuickLoad){\n      urlHistory(url);\n      sendAHref(url, null);\n   }\n   else{\n    window.location=url;\n   }\n}\nfunction popup(id, width)\n        {\n            var html=document.getElementById(id.toString());\n\t\t\t/*fix for issue where action menus are rendered away when clicked continuously for 10 times*/\n");
            out.write("\t\t\tif(!width) {\n\t\t\t\twidth = 230;\n\t\t\t}\n\t\t\tshowDialog(html.innerHTML,\"closeButton=no,closePrevious=false,draggable=no,closeOnBodyClick=yes,zAdjust=true,overflow=hidden,transitionInterval=0.0,transitionType=Effect.BlindDown,position=relative,left=-200,top=17,width=\"+width);//No I18N\n\n        }\n        function bodyClick(event) {\n                var source = event.target || event.srcElement;\n                if(source.className!=\"searchArrow\" && (typeof liClick == 'undefined' || !liClick ))\n                //hide_search();\n                if(typeof show != 'undefined' )\n                    newTarget = jQuery('.targetDiv').eq(show).slideUp(30);//No I18N\n\n                if(typeof clearFlashImg != 'undefined')\n                \tclearFlashImg();\n                /*var source = event.target || event.srcElement;\n                 if(!isDialog){\n                 isDialog=true;\n                 if(source.id=='imgDiv1')\n                 myquickShowMe('st-app');//No I18N\n                 else if(source.id=='helptop')\n                 quickShowMe('contact1');//No I18N\n");
            out.write("                 else if(source.id=='loginNamemultiLevelDropDownLable')\n                 quickShowMe('loginName');//No I18N\n                 }\n                 else\n                 {\n                 closeAll();\n                 isDialog=false;\n                 }*/\n            }\n\t\t\tdocument.body.addEventListener('click',bodyClick);\n</script>\n");
            out.write("\n<html>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
            out.write("\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\"/>");
            out.write("\n<head>\n<script src=\"/framework/javascript/IncludeJS.js?");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" type=\"text/javascript\"></script>\n<script>includeMainScripts(\"\",\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\");</script>\n<link href=\"/themes/styles/common.css\" rel=\"stylesheet\" type=\"text/css\" />\n<link href=\"../../themes/styles/login.css\" rel=\"stylesheet\" type=\"text/css\" />\n<link href=\"themes/styles/");
            if (this._jspx_meth_c_005fout_005f0(_jspx_page_context)) {
                return;
            }
            out.write("/style.css\" rel=\"stylesheet\" type=\"text/css\">\n<link href=\"/images/favicon.ico\" rel=\"SHORTCUT ICON\"/>\n<title>\n");
            if (this._jspx_meth_c_005fout_005f1(_jspx_page_context)) {
                return;
            }
            out.write("\n</title>\n<!-- this below dc:productTag-check code/content will be executed/shown only for MSPCL -->\n");
            if (this._jspx_meth_fw_005fproductTag_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\n<style type=\"text/css\">\n@font-face {\n    font-family: 'Lato';\n    src: url('../../../themes/styles/font/Lato.woff2') format('woff2');\n}\n@font-face {\n   font-family:\"Roboto\";\n   font-weight:400;\n   font-style:normal;\n   src: url(\"https://webfonts.zohowebstatic.com/robotoregular/font.woff\") format(\"woff\");\n}\nhtml, body {\n\tpadding: 0;\n\tmargin: 0;\n\tbackground-color: #ffffff;\n\tfont: 13px 'Lato', 'Roboto', Sans-Serif;\n}\n* {\n\tbox-sizing: border-box;\n\tmargin: 0;\n\tpadding: 0;\n}\n#login_top {\n\tmargin: 1px auto 2px auto;\n\tbackground: url(/images/dm-default/dc-logo.gif) no-repeat;\n\theight: 55px;\n\ttext-align: right;\n\tpadding-top: 7px;\n\tpadding-bottom: 85px;\n\twidth: 970px;\n}\n.enableJSDiv\n{\n    background-color:#f34444;\n    text-align: center;\n    color: #fff;\n    font-size: 125%;\n}\n.AwsLoginBody { margin-top:40px;}\n.aws-login-wrap {\n\tposition: relative;\n\tmargin: 0 auto;\n\tmargin-top: 10px;\n\tbackground: #f9f9f9;\n\twidth: 35%;\n\tborder-radius: 5px;\n\tborder:1px solid #efefef;\n\tpadding: 15px;\n}\n.aws-login-wrap h2 {\n\ttext-align: center;\n");
            out.write("\tfont-weight: 200;\n\tfont-size: 2em;\n\tmargin-top: 10px;\n\tcolor: #0097d8;\n}\n.aws-login-wrap .aws-form {\n\tpadding-top: 20px;\n}\n.aws-login-wrap .aws-form input[type=\"text\"] {\n\tbackground: url(/images/login/login_fields.png) 10px 13px no-repeat;\n}\n.aws-login-wrap .aws-form input[type=\"password\"] {\n\tbackground: url(/images/login/login_fields.png) 10px -20px no-repeat;\n}\n.aws-login-wrap .aws-form input[type=\"text\"], .aws-login-wrap .aws-form input[type=\"password\"], .aws-login-wrap .aws-form input[type=\"submit\"] {\n\twidth: 80%;\n\tmargin-left: 10%;\n\tmargin-bottom: 25px;\n\theight: 40px;\n\tborder-radius: 5px;\n\tbackground-color: #ffffff;\n\toutline: 0;\n\t-moz-outline-style: none;\n}\n.aws-login-wrap .aws-form input[type=\"text\"], .aws-login-wrap .aws-form input[type=\"password\"] {\n\tborder: 1px solid #d0d0d0;\n\tpadding: 0 0 0 45px;\n\tfont-size: 13px;\n}\n.aws-login-wrap .aws-form input[type=\"text\"]:focus, .aws-login-wrap .aws-form input[type=\"password\"]:focus {\n\tborder: 1px solid #3498db;\n}\ninput[type=\"text\"]:disabled{\n\tbackground-color:#e7e7e7 !important;\n");
            out.write("\tcolor:#909090 !important;\n}\n.aws-login-wrap .aws-form a {\n\ttext-align: center;\n\tcolor: #7fc1fe;\n\tcursor:pointer;\n}\n.aws-login-wrap .aws-form a p {\n\tpadding-bottom: 10px;\n}\n.aws-login-wrap .aws-form input[type=\"submit\"] {\n\tbackground: #0097d8;\n\tborder: none;\n\tcolor: white;\n\tfont-size: 18px;\n\tfont-weight: 200;\n\tcursor: pointer;\n\ttransition: box-shadow .4s ease;\n}\n.aws-login-wrap .aws-form input[type=\"submit\"]:hover {\n\tbox-shadow: 1px 1px 5px #555;\n}\n.aws-login-wrap .aws-form input[type=\"submit\"]:active {\n\tbox-shadow: 1px 1px 7px #222;\n}\n.aws-login-wrap:after {\n\tcontent: '';\n\tposition: absolute;\n\ttop: 0;\n\tleft: 0;\n\tright: 0;\n\tbackground: grey;\n\theight: 5px;\n\tborder-radius: 5px 5px 0 0;\n}\n.tooltips {\n\tposition: relative;\n\tdisplay: inline;\n}\n.tooltips span {\n\tposition: absolute;\n\tcolor:#fff;\n\tbackground: #4b4b4b;\n\ttext-align: center;\n\tvisibility: hidden;\n\tborder-radius: 9px;\n\twidth: 200px;\n\tbox-shadow: 0 0 5px #888888;\n}\n.tooltips span:after {\n\tcontent: '';\n\tposition: absolute;\n\ttop: 25%;\n\tright: 100%;\n\tmargin-top: 1px;\n");
            out.write("\twidth: 0;\n\theight: 0;\n\tborder-right: 8px solid #4b4b4b;\n\tborder-top: 8px solid transparent;\n\tborder-bottom: 8px solid transparent;\n}\n.tooltips:hover span {\n\tvisibility: visible;\n\topacity: 1;\n\tleft: 100%;\n\ttop: 50%;\n\tmargin-top: -25px;\n\tmargin-left: 7px;\n\tz-index: 999;\n\tpadding: 8px;\n}\n.AwsLoginFooter div {\n\twidth: 100%;\n\tbottom: 0;\n\tbackground: #f9f9f9;\n\tposition: absolute;\n\tbottom: 0;\n\ttext-align: center;\n}\n.signin_error {\n\tcolor:red !important;\n}\n.signin_loading{\n       background: #46a1e1 url(\"/images/syncblue.gif\") no-repeat 140px center !important;;\n\t   background-size: 27px 27px !important;;\n}\n</style>\n<script>\nfunction addCSRF(formObj)\n{\n\n   var input = document.createElement(\"input\");\n   input.type = \"text\";\n   input.name=csrfParamName;\n   input.value=getCSRFCookie(cookieName);\n   input.className=\"hide\";\n   formObj.appendChild(input);\n\n   return formObj;\n   }\n   function getCSRFCookie(cn) {\n                if (document.cookie.length > 0) {\n                        var beginIdx = document.cookie.indexOf(cn + \"=\");\n");
            out.write("                        if (beginIdx !== -1) {\n                                beginIdx = beginIdx + cn.length + 1;\n                                var endIdx = document.cookie.indexOf(\";\", beginIdx);\n                                if (endIdx === -1) {\n                                        endIdx = document.cookie.length;\n                                }\n                                return window.unescape(document.cookie.substring(beginIdx, endIdx));\n                        }\n                }\n                return \"\";\n        }\n\tfunction alertLayer(alertmsg)\n                    {\n                        showDialog('<table class=\"bodytext\" width=\"100%\" height=\"100%\"><tr><td rowspan=\"3\">&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\"/></td><td></td><td></td></tr><tr ><td colspan=\"2\"><span class=\"bodytext\">'+alertmsg+'</span></td></tr> <tr><td></td><td></td></tr> <tr><td align=\"center\" colspan=\"3\" class=\"formSubmitBg\"><input type=\"button\" value=\"OK\" class=\"primaryActionBtn\" onclick=\"javascript:closeDialog(null,this);\"></td></tr></table>','width=400,height=110,position=absolute,left=400,top=200,title=");
            out.print(I18N.getMsg("dc.common.ALERT", new Object[0]));
            out.write("');//No i18n\n                    }\n                    function checkBrowser()\n                    {\n                        var userAgent = navigator.userAgent.toLowerCase();\n                        if(userAgent.match(\"msie\") == \"msie\")\n                        {\n                            browser = \"internet explorer\";//No I18N\n                            if(browser==\"internet explorer\")\n                            {\n                                version = userAgent.substring(userAgent.indexOf(\"msie\")+4,userAgent.lastIndexOf(\";\"));\n                                var ver =parseFloat(version);\n                                if(browser == \"internet explorer\" && ver < 5.5)\n                                {\n                                    return true;\n                                }\n                            }\n                        }\n\n                        else if( userAgent.match(\"netscape\") == \"netscape\")\n                        {\n                            browser=\"Netscape\";//No I18N\n                            if(browser==\"Netscape\")\n");
            out.write("                            {\n                                version = userAgent.substring(userAgent.indexOf(\"netscape\")+4,userAgent.lastIndexOf(\";\"));\n                                var ver =parseFloat(version);\n                                if(browser == \"Netscape\" && version < 7.0)\n                                {\n                                    return true;\n                                }\n                            }\n                        }\n\n                        else if(userAgent.match(\"mozilla\") == \"mozilla\")\n                        {\n\n                            browser = \"mozilla\";//No I18N\n                            if(browser==\"mozilla\")\n                            {\n                                version = userAgent.substring(userAgent.indexOf(\"rv:\")+3,userAgent.indexOf(\")\"));\n                                var ver = parseFloat(version);\n                                if(browser == \"mozilla\" && ver < 1.5)\n                                {\n                                    return true;\n");
            out.write("                                }\n                            }\n                        }\n                        else\n                        {\n                            return false;\n                        }\n\n                    }\n                    function checkForNull(form)\n                    {\n\t\t\t\t\t\t\n\t\t\t\t\t\tdocument.login.Button.disabled = true;\n                        document.login.Button.value = \"\";\n                        document.login.Button.className = \"signin_loading\";\n\n                        var browser = checkBrowser();\n                        if(browser == true)\n                        {\n                            var browserInfo = \"");
            out.print(I18N.getMsg("desktopcentral.common.login.browser_not_supported", new Object[0]));
            out.write("\";\n                            alertLayer(browserInfo);\n                        }\n                        if(document.login.j_password.value == \"\")\n                        {\n\t\t\t\t            alertLayer(\"");
            out.print(I18N.getMsg("desktopcentral.common.login.enter_username_password", new Object[0]));
            out.write("\");\n                            document.login.j_password.focus();\n\t\t\t\t\t\t\t\n\t\t\t\t\t\t\tdocument.login.Button.disabled = false;\n\t\t\t\t\t\t    document.login.Button.className = \"signin_button\";\n                            document.login.Button.value = \"Sign in\";   //No I18N\n\t\t\t\t\t\t\t\n                            return false;\n                        }\n                        document.login.j_username.value=\"admin\";");
            out.write("\n\t\t\t\t\t\t\n\t\t\t\t\t\tdocument.login.Button.disabled = false;\n\t\t\t\t\t\tdocument.login.Button.className = \"signin_button\";\n                        document.login.Button.value = \"Sign in\";    //No I18N\n      \n                      //storing location hash in localstorage for ember pages to get loaded after login\n                      //this is done because hash is not retained after login\n                      window.localStorage.setItem(\"dcEmberURL\", window.location.hash); //No I18N\n      \n                       return true;\n                    }\n\tfunction validateChangePassInputs()\n\t{\n\t\tvar message=\"\";\n        if(message.length ==0  && document.changPasswordForm.newUserPassword.value.length == 0)\n        {\n           message =\"");
            out.print(I18N.getMsg("dc.admin.UserAdmin.enter_new_users_pwd", new Object[0]));
            out.write("\";\n           document.changPasswordForm.newUserPassword.focus();\n        }\n        if(message.length ==0  && document.changPasswordForm.confirmPassword.value.length == 0)\n        {\n           message = \"");
            out.print(I18N.getMsg("dc.admin.UserAdmin.enter_confirm_pwd", new Object[0]));
            out.write("\";\n           document.changPasswordForm.confirmPassword.focus();\n        }\n        if(message.length ==0  && document.changPasswordForm.newUserPassword.value.length < 5)\n        {\n           message = \"");
            out.print(I18N.getMsg("dc.admin.UserAdmin.Pwd_char_len", new Object[0]));
            out.write("\";\n           document.changPasswordForm.newUserPassword.focus();\n        }\n        var password = document.changPasswordForm.newUserPassword.value;\n        var confirmPassword = document.changPasswordForm.confirmPassword.value;\n        if(message.length ==0  && password != confirmPassword)\n        {\n           message = \"");
            out.print(I18N.getMsg("dc.admin.UserAdmin.Pwd_no_match", new Object[0]));
            out.write("\" ;\n           document.changPasswordForm.newUserPassword.focus();\n        }\n        if(message.length > 0)\n        {\n            document.getElementById(\"errorMsg\").innerHTML = message;\n            document.getElementById(\"infoBox\").style.display='block';\n            return false;\n        }\n\n\t\t var passwordform=document.forms[\"changPasswordForm\"];\n\t\taddCSRF(passwordform);\n        return true;\n    }\n</script>\n\n</head>\n<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" >\n<noscript>");
            out.write("\n  <div class=\"enableJSDiv\">\n     <table style=\"margin:auto;height:40px\">\n        <tr>\n           <td><img src=\"/images/alerts_small.png\"/></td>\n              <td>");
            out.print(IAMEncoder.encodeHTML(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.login.enable_javascript_msg", new Object[0])));
            out.write("\n              </td>\n           </tr>\n     </table>\n  </div>\n</noscript>");
            out.write("\n<div class=\"AwsLoginHeader\">\n  <div class=\"login_top_container\">\n    <div id=\"login_top\">\n      <p>&nbsp;</p>\n      <a target=\"_blank\" href=\"");
            out.print(IAMEncoder.encodeHTMLAttribute(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), MDMURLRedirection.getURL("get_quote"), new Object[0])));
            out.write(63);
            out.write(112);
            out.write(61);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("&did=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${DID}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" title=\"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.get_qoute", new Object[0]));
            out.write("\">\n      <div class=\"topLink7\"> &nbsp; </div>\n      </a> <a target=\"_blank\" href=\"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), ProductUrlLoader.getInstance().getValue("store_url"), new Object[0]));
            out.write(63);
            out.write((String)PageContextImpl.proprietaryEvaluate("${generalProperties.trackingcode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("&did=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${DID}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"title=\"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.ONLINE_STORE", new Object[0]));
            out.write("\">\n      <div class=\"topLink8\"> &nbsp; </div>\n      </a> <a href=\"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), ProductUrlLoader.getInstance().getValue("forums_url"), new Object[0]));
            out.write("\" target=\"_blank\" title=\"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.USER_COMMUNITY", new Object[0]));
            out.write("\">\n      <div class=\"topLink1\"> &nbsp; </div>\n      </a> <a href=\"https://www.manageengine.com/mobile-device-management/help/?aws\" target=\"_blank\"title=\"");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.HELP", new Object[0]));
            out.write("\">\n      <div class=\"topLink6\"> &nbsp; </div>\n      </a>\n\t</div>\n  </div>\n</div>\n<div class=\"AwsLoginBody\">\n  ");
            final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f0_reused = false;
            try {
                _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n    ");
                        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f0_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${AmazonUserLoginSuccess != 'true'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("\n      <div class=\"aws-login-wrap\">\n        <h2>Sign in</h2>");
                                    out.write("\n        </br>\n        <div class=\"aws-form\">\n          <form name=\"login\" action=\"");
                                    out.print(response.encodeURL("j_security_check"));
                                    out.write("\" style=\"height:100%;\"  method=\"post\" AUTOCOMPLETE=\"off\">\n            <input class=\"\" type=\"text\" placeholder=\"Username\" name=\"username\" value=\"admin\" disabled/>\n            <input id=\"userName\" name=\"j_username\" type=\"text\" placeholder=\"User Name\" value=\"admin\" text=\"admin\" style=\"display:none;\"/>\n            <input class=\"\" name=\"j_password\" type=\"password\" placeholder=\"Enter your amazon instance ID\" autofocus />\n            ");
                                    if (this._jspx_meth_c_005fif_005f0((JspTag)_jspx_th_c_005fwhen_005f0, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\n            <input type=\"hidden\" name=\"browserLocale\" id=\"browserLocale\" value=\"");
                                    out.write((String)PageContextImpl.proprietaryEvaluate("${browserLocale}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                    out.write("\">\n\n            <div class=\"tooltips\" href=\"#\"><img src=\"/images/login/infoMsg.png\" width=\"20px\" height=\"20px\" align=\"center\"/><span class=\"tooltipText\">Use your amazon <a target=\"_blank\" href=\"https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Instances:sort=Name\" >instance ID</a> as your password</span></div>");
                                    out.write("\n            ");
                                    final IfTag _jspx_th_c_005fif_005f1 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                                    boolean _jspx_th_c_005fif_005f1_reused = false;
                                    try {
                                        _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fif_005f1.setParent((Tag)_jspx_th_c_005fwhen_005f0);
                                        _jspx_th_c_005fif_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${login_status != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                        final int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
                                        if (_jspx_eval_c_005fif_005f1 != 0) {
                                            int evalDoAfterBody;
                                            do {
                                                out.write("\n              <div align=\"center\"> <span class=\"signin_error\" style=\"padding-bottom:13px;\">");
                                                out.print(I18N.getMsg("desktopcentral.common.login.invalid_username_password", new Object[0]));
                                                out.write("</span> </div>\n            ");
                                                evalDoAfterBody = _jspx_th_c_005fif_005f1.doAfterBody();
                                            } while (evalDoAfterBody == 2);
                                        }
                                        if (_jspx_th_c_005fif_005f1.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f1);
                                        _jspx_th_c_005fif_005f1_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f1_reused);
                                    }
                                    out.write("\n            <input type=\"submit\" value=\"Sign in\" name=\"Button\" id=\"Button\" class=\"signin_button\" onClick=\"return checkForNull(this.form)\"/>\n            <input type=\"hidden\" name=\"cacheNum\" id=\"cacheNum\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f2((JspTag)_jspx_th_c_005fwhen_005f0, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\"/>\n            <input type=\"hidden\" name=\"loginPageCsrfPreventionSalt\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f3((JspTag)_jspx_th_c_005fwhen_005f0, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\"/>\n          </form>\n        </div>\n      </div>\n    ");
                                    evalDoAfterBody2 = _jspx_th_c_005fwhen_005f0.doAfterBody();
                                } while (evalDoAfterBody2 == 2);
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
                        out.write("\n    ");
                        if (this._jspx_meth_c_005fotherwise_005f0((JspTag)_jspx_th_c_005fchoose_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(10);
                        out.write(32);
                        out.write(32);
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
            out.write("\n</div>\n<div class=\"AwsLoginFooter\" >\n  <div id=\"browser_info\">");
            out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.login.best_view_msg", new Object[0]));
            out.write(" &copy;\n    ");
            if (this._jspx_meth_c_005fout_005f4(_jspx_page_context)) {
                return;
            }
            out.write("\n    <a href=\"");
            if (this._jspx_meth_c_005fout_005f5(_jspx_page_context)) {
                return;
            }
            out.write("\" target=\"_blank\">\n    ");
            if (this._jspx_meth_c_005fout_005f6(_jspx_page_context)) {
                return;
            }
            out.write("\n  </div> ");
            out.write("\n</div>\n<script>\njQuery('form').submit(function(e){                                    //No I18N\n    jQuery(':input[type=submit]').prop('disabled', true);              //No I18N\n    jQuery(':input[type=submit]').prop('value', '');                    //No I18N\n    jQuery('input[type=submit]').removeClass(\"signin_button\").addClass( \"signin_loading\" );\n});\n</script>\n</body>\n</html>\n");
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
            loginAWS_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
                    out.write("\n  <script>window.location=\"/MspCenterHome.do\";</script>\n");
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
    
    private boolean _jspx_meth_c_005fif_005f0(final JspTag _jspx_th_c_005fwhen_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f0_reused = false;
        try {
            _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_c_005fwhen_005f0);
            _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
            if (_jspx_eval_c_005fif_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n              <input type=\"hidden\" name=\"otpTimeout\" id=\"otpTimeout\" value=\"");
                    out.write((String)PageContextImpl.proprietaryEvaluate("${otpTimeout}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                    out.write("\">\n            ");
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
    
    private boolean _jspx_meth_c_005fout_005f2(final JspTag _jspx_th_c_005fwhen_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f2 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f2_reused = false;
        try {
            _jspx_th_c_005fout_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f2.setParent((Tag)_jspx_th_c_005fwhen_005f0);
            _jspx_th_c_005fout_005f2.setValue(PageContextImpl.proprietaryEvaluate("${cachenumber}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f3(final JspTag _jspx_th_c_005fwhen_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f3 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f3_reused = false;
        try {
            _jspx_th_c_005fout_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f3.setParent((Tag)_jspx_th_c_005fwhen_005f0);
            _jspx_th_c_005fout_005f3.setValue(PageContextImpl.proprietaryEvaluate("${loginPageCsrfPreventionSalt}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
                    out.write("\n      <div class=\"aws-login-wrap\">\n        <h2>Change Password</h2>");
                    out.write("\n        </br>\n          <p style=\"color:#03648d; text-align: center;\">Set a new password before continuing.</p>");
                    out.write("\n        <div class=\"aws-form\">\n          <form method=\"post\" id=\"changPasswordForm\" name=\"changPasswordForm\"  action=\"/changeDefaultAmazonPassword\"  onSubmit=\"return validateChangePassInputs()\" >\n            <input class=\"\" type=\"text\" placeholder=\"Username\" name=\"username\" value=\"admin\" disabled />\n            <input type=\"hidden\" id=\"loginName\" name=\"loginName\" value=\"admin\"/>\n            <input class=\"\" type=\"password\" placeholder=\"New password\" id=\"newUserPassword\" name=\"newUserPassword\" autofocus />\n            <input class=\"\" type=\"password\" placeholder=\"Confirm password\" name=\"confirmPassword\" id=\"confirmPassword\" />\n            <div id=\"infoBox\" align=\"center\" style=\"display:none;padding-bottom:13px;\" class=\"signin_error\">\n              <p id=\"errorMsg\"  width=\"90%\" align=\"center\" ></p>\n            </div>\n            <input type=\"submit\" value=\"Change\"/>\n          </form>\n        </div>\n      </div>\n    ");
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
    
    private boolean _jspx_meth_c_005fout_005f4(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f4 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f4_reused = false;
        try {
            _jspx_th_c_005fout_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f4.setParent((Tag)null);
            _jspx_th_c_005fout_005f4.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.copyright_year}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
            _jspx_th_c_005fout_005f5.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_url}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f6(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f6 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f6_reused = false;
        try {
            _jspx_th_c_005fout_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f6.setParent((Tag)null);
            _jspx_th_c_005fout_005f6.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_name}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (loginAWS_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        loginAWS_jsp._jspx_dependants.put("/jsp/common/include.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        loginAWS_jsp._jspx_imports_packages.add("javax.servlet.http");
        loginAWS_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        loginAWS_jsp._jspx_imports_classes.add("com.me.mdm.server.common.MDMURLRedirection");
        loginAWS_jsp._jspx_imports_classes.add("java.util.Locale");
        loginAWS_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        loginAWS_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
        loginAWS_jsp._jspx_imports_classes.add("com.adventnet.iam.xss.IAMEncoder");
        loginAWS_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.I18NUtil");
    }
}

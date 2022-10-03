package org.apache.jsp.jsp.mdm.enroll;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.taglibs.standard.functions.Functions;
import org.apache.taglibs.standard.tag.rt.core.OutTag;
import org.apache.jasper.el.JspValueExpression;
import org.apache.taglibs.standard.tag.rt.core.ForEachTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
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
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class phoneLoginPage_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static ProtectedFunctionMapper _jspx_fnmap_0;
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return phoneLoginPage_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return phoneLoginPage_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return phoneLoginPage_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = phoneLoginPage_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
        this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.release();
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
            response.setContentType("text/html");
            final PageContext pageContext = _jspx_page_context = phoneLoginPage_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    \n    ");
            out.write("\n    \n\t");
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write("\n    \n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0,user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <!-- The previous ulr of css will be cached in the device.So to load the changed css the url path is changed by appending 1 as the parameter -->\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/sdp/style.css\"/>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css?2\" />\n    </head>\n\t\n    <body style=\"background-color: rgb(245, 244, 240); \">\n   <script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/IncludeJS.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n        <script>includeMainScripts(\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(34);
            out.write(44);
            out.write(34);
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\");</script>\n        <script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/validation.js\"></script>\n        <script language=\"Javascript\" type=\"text/javascript\">\n\t\t\n            function validateADInput(){\n\t\t        showInProgressMsg();\n               \n                var authMode= trimAll(document.getElementsByName(\"AuthMode\")[0].value);\n\t\t\t\t var platformStr= trimAll(document.getElementsByName(\"platformStr\")[0].value);\n                var includeBasicVal= trimAll(document.getElementsByName(\"IncludeBasicValidations\")[0].value);\n                var includeEmailVal= trimAll(document.getElementsByName(\"ValidateEmailAddress\")[0].value);\n                var erid= trimAll(document.getElementsByName(\"EnrollmentRequestID\")[0].value);\n                var json = {\"DevicePlatform\":platformStr,//No I18N\n                            \"AuthMode\":authMode,//No I18N\n                            \"IncludeBasicValidations\":includeBasicVal,//No I18N\n                            \"ValidateEmailAddress\":includeEmailVal//No I18N\n                            };                       \n                if(erid != ''){\n");
            out.write("                    json.EnrollmentRequestID=erid;\n                }\n                ");
            final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f0_reused = false;
            try {
                _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f0.setParent((Tag)null);
                _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode !='OTP'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
                if (_jspx_eval_c_005fif_005f0 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n                var userName = trimAll(document.getElementsByName(\"UserName\")[0].value);\n                var adpassword = document.getElementsByName(\"ADPassword\")[0].value.trim();\n                var domainName = trimAll(document.getElementsByName(\"DomainName\")[0].value);\n\t\t        if(userName == '')\n                {\n                    showErrorMsg('");
                        out.print(I18N.getMsg("dc.mdm.enroll.enter_user_name", new Object[0]));
                        out.write("');\n                    return false;\n                } else {\n                    json.UserName=userName;\n                }\n                if(adpassword == '')\n                {\n                    showErrorMsg('");
                        out.print(I18N.getMsg("dc.mdm.enroll.enter_ad_password", new Object[0]));
                        out.write("');\n                    return false;\n                } else {\n                    json.ADPassword=adpassword;\n                }\n                \n                if(domainName == '-1')\n                {\n                    showErrorMsg('");
                        out.print(I18N.getMsg("dc.mdm.enroll.Select_Domain_Name", new Object[0]));
                        out.write("');\n                    return false;\n                } else {\n                    json.DomainName=domainName;\n                }\n                ");
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
            out.write("\n                ");
            final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f1_reused = false;
            try {
                _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f2.setParent((Tag)null);
                _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
                if (_jspx_eval_c_005fif_005f2 != 0) {
                    int evalDoAfterBody2;
                    do {
                        out.write("\n                var ownedBy = document.getElementsByName(\"OwnedBy\")[0].value;\n                var email = document.getElementsByName(\"EmailAddress\")[0].value;\n                if(email == '')\n                {\n                    showErrorMsg('");
                        out.print(I18N.getMsg("dc.mdm.enroll.enter_email", new Object[0]));
                        out.write("');\n                    return false;\n                } else if(!dcIsValidEmail(email)) {\n                    showErrorMsg(I18N.getMsg('desktopcentral.som.addDomain.Enter_Valid_Mail_ID'));\n                    return false;\n                } else {\n                    json.EmailAddress=email;\n                }\n\t\t        if(ownedBy == '-1')\n                {\n                    showErrorMsg('");
                        out.print(I18N.getMsg("dc.mdm.enroll.Select_owned_by", new Object[0]));
                        out.write("')\n                    return false;\n                } else {\n                    json.OwnedBy=ownedBy;\n                }\n                ");
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
            out.write("\n                ");
            final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f2_reused = false;
            try {
                _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f3.setParent((Tag)null);
                _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode != 'ActiveDirectory'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
                if (_jspx_eval_c_005fif_005f3 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n                var otppassword = document.getElementsByName(\"OTPPassword\")[0].value.trim();\n                if(otppassword == '')\n                {\n                    showErrorMsg('");
                        out.print(I18N.getMsg("dc.mdm.enroll.enter_otp_password", new Object[0]));
                        out.write("');\n                    return false;\n                } else {\n                    json.OTPPassword=otppassword;\n                }\n\t\t        ");
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
            out.write("\n                var param={\"MsgRequestType\": \"Authenticate\",//No I18N\n                           \"RegistrationType\": \"MDMRegistration\", //No I18N\n                           \"DevicePlatform\":platformStr, //No I18N\n                           \"MsgRequest\":json//No I18N\n                            };\n                var requestHeaders = new Object();\n                requestHeaders['Content-type'] = 'application/json'; //No I18N\n                var url=\"/mdm/enroll?actionToCall=Authenticate\"; //No I18N\n                AjaxAPI.sendRequest({URL:url, METHOD: 'POST', ONSUCCESSFUNC:postAuthentication, REQUESTHEADERS: requestHeaders, PARAMETERS: JSON.stringify(param)}); //No I18N\n    \n            }\n            function submitForm(e){\n                if(e.keyCode === 13){\n                    validateADInput();\n                }\n            }\n            function showErrorMsg(errorMsg)\n            {\n                document.getElementById(\"errormsg\").className = \"errorMessage\";\n                document.getElementById(\"errormsg\").innerHTML = errorMsg;\n");
            out.write("\t\t\t\t\n            }\n            function showInProgressMsg()\n            {\n                document.getElementById(\"errormsg\").className = \"\";\n                document.getElementById(\"errormsg\").innerHTML = '<div class=\"AuthenticationMessage\"><img src=\"../../images/s_progressbar.gif\"><span> &nbsp;'+'");
            out.print(I18N.getMsg("dc.mdm.enroll.authenticating", new Object[0]));
            out.write("'+'</span></div>';\n\t\t\t\t\n            }\n            function postAuthentication(resp)\n\t\t\t{\n\t\t\t\tvar json = JSON.parse(resp.responseText);\n\t\t\t\tconsole.log(json);\n\t\t\t\t//alert(json.Status);\n\t\t\t\tif(json.Status == \"Acknowledged\"){\n\t\t\t\t\tif(json.MsgResponse.ManagedEmailAddress!=null && json.MsgResponse.ManagedEmailAddress!=''){\n\t\t\t\t\t\t\tshowDialog('<table id=\"iosmsgbox\" class=\"bodytext iosStylePopup\" width=\"100%\" height=\"100%\" cellpadding=\"10\" cellspacing=\"0\" style=\"padding:0px\"><tr><td colspan=\"2\" class=\"title\" style=\"padding:20px 20px 0px 20px\">'+I18N.getMsg(\"dc.mdm.enroll.managed_email_change_warning.title\")+'</td></tr><tr><td colspan=\"2\" style=\"padding:10px 15px 20px 15px\"><div style=\"word-wrap: break-word;width:220px;padding:0px\">'+I18N.getMsg(\"dc.mdm.enroll.managed_email_change_warning.message\",new Array(json.MsgResponse.ManagedEmailAddress))+'</div></td></tr><tr><td colspan=\"2\" align=\"center\" class=\"topLine\"><a onclick=\"closeDialog(null,this);download();\" style=\"color:rgb(86, 172, 79) !important;padding: 2px 0px 2px 0px;\"><b>'+I18N.getMsg(\"dc.common.ok\")+'</b></a></td></tr></table>','width=250,modal=yes,dialogBoxType=helpTip');//No i18n\n");
            out.write("\t\t\t\t\t} else{\n\t\t\t\t\t\t\tdownload();\n\t\t\t\t\t}\n\t\t\t\t} else{\n\t\t\t\t\tif(json.MsgResponse.ErrorCode == 51015){\n\t\t\t\t\t\tvar email = trimAll(document.getElementsByName(\"EmailAddress\")[0].value);\n\t\t\t\t\t\tshowDialog('<table id=\"iosmsgbox\" class=\"bodytext iosStylePopup\" width=\"100%\" height=\"100%\" cellpadding=\"10\" cellspacing=\"0\" style=\"padding:0px\"><tr><td colspan=\"2\" class=\"title\" style=\"padding:20px 20px 0px 20px\">'+I18N.getMsg(\"dc.mdm.enroll.ad_email_change_confirm.title\")+'</td></tr><tr><td colspan=\"2\" style=\"padding:10px 15px 20px 15px\"><div style=\"word-wrap: break-word;width:220px;padding:0px\">'+I18N.getMsg(\"dc.mdm.enroll.ad_email_change_confirm.message\",new Array(email,json.MsgResponse.ADEmailAddress))+'</div></td></tr>\t\t\t<tr><td colspan=\"2\" align=\"center\" class=\"topLine\"><a style=\"color:rgb(86, 172, 79) !important;padding: 2px 0px 2px 0px;\" onclick=\"closeDialog(null,this);changeEmail(\\''+json.MsgResponse.ADEmailAddress+'\\');\">'+I18N.getMsg(\"dc.js.common.continue_ad_email\")+'</a></td></tr>\t\t\t<tr><td colspan=\"2\" align=\"center\" class=\"topLine\"><a style=\"color:rgb(86, 172, 79) !important;padding: 2px 0px 2px 0px;\" onclick=\"closeDialog(null,this);proceed();\">'+I18N.getMsg(\"dc.js.common.continue_your_email\")+'</a></td></tr></table>','width=250,modal=yes,dialogBoxType=helpTip');//No i18n\t\n");
            out.write("\t\t\t\t\t} else {\n\t\t\t\t\t\tshowErrorMsg(I18N.getMsg(json.MsgResponse.ErrorKey));\n\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t}\n\t\t\tfunction download(){\n\t\t\t\twindow.location=\"/mdm/enroll?actionToCall=download\";\n\t\t\t}\n\t\t\tfunction proceed(){\n\t\t\t\tdocument.getElementsByName(\"ValidateEmailAddress\")[0].value=false;\n \t\t\t\tdocument.getElementsByName(\"EmailAddress\")[0].disabled=true;\n\t\t\t\tvalidateADInput();\n\t\t\t}\n\t\t\tfunction changeEmail(email){\n\t\t\t\tdocument.getElementsByName(\"EmailAddress\")[0].value=email;\n\t\t\t\tproceed();\n\t\t\t}\n                        function submitForm(e){\n                             if(e.keyCode === 13){\n                                 validateADInput();\n                             }\n                         }\n          // When ready...\n            window.addEventListener(\"load\",function() {\n              // Set a timeout...\n              setTimeout(function(){\n                // Hide the address bar!\n                window.scrollTo(0, 1);\n              }, 0);\n            });\n\n\t\t\twindow.onorientationchange = function() {\n\t\t\t\tif(document.getElementById(\"FreezeLayer\")){\n");
            out.write("\t\t\t\t\tdocument.getElementById(\"FreezeLayer\").style.width = window.innerWidth + \"px\"; \n\t\t\t\t\tdocument.getElementById(\"FreezeLayer\").style.height = getWindowHeight() + \"px\";\n\t\t\t\t}\n\t\t\t\tif(document.getElementById(\"_DIALOG_LAYER0\")){\n\t\t\t\t\tdocument.getElementById(\"_DIALOG_LAYER0\").style.left = (window.innerWidth/ 2 ) - (250 / 2) + \"px\";\n\t\t\t\t\tdocument.getElementById(\"_DIALOG_LAYER0\").style.top = (getWindowHeight()/ 2 ) - (document.getElementById(\"_DIALOG_LAYER0\").style.height/ 2) + \"px\";\n\t\t\t\t}\n\t\t\t}\n\t\t</script>\n\t <div ");
            if (this._jspx_meth_c_005fchoose_005f0(_jspx_page_context)) {
                return;
            }
            out.write(62);
            out.write("\n   \t \n          \n\t\n      <table width=\"100%\"  ");
            if (this._jspx_meth_c_005fif_005f3(_jspx_page_context)) {
                return;
            }
            out.write(">\n            <tr class=\"blueHeader\">\n                <td><div><img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</div></td>\n            </tr>\n      </table>            \n\t\t         <div ");
            if (this._jspx_meth_c_005fchoose_005f1(_jspx_page_context)) {
                return;
            }
            out.write(" id=\"header\" >");
            final ChooseTag _jspx_th_c_005fchoose_005f2 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f2_reused = false;
            try {
                _jspx_th_c_005fchoose_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f2.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f2 = _jspx_th_c_005fchoose_005f2.doStartTag();
                if (_jspx_eval_c_005fchoose_005f2 != 0) {
                    int evalDoAfterBody7;
                    do {
                        final WhenTag _jspx_th_c_005fwhen_005f2 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f2_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f2.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            _jspx_th_c_005fwhen_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode == 'OTP'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f2 = _jspx_th_c_005fwhen_005f2.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f2 != 0) {
                                int evalDoAfterBody4;
                                do {
                                    out.print(I18N.getMsg("dc.mdm.enroll.passcode_verification", new Object[0]));
                                    evalDoAfterBody4 = _jspx_th_c_005fwhen_005f2.doAfterBody();
                                } while (evalDoAfterBody4 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f2.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f2);
                            _jspx_th_c_005fwhen_005f2_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f2_reused);
                        }
                        final WhenTag _jspx_th_c_005fwhen_005f3 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f3_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f3.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            _jspx_th_c_005fwhen_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode == 'ActiveDirectory'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f3 = _jspx_th_c_005fwhen_005f3.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f3 != 0) {
                                int evalDoAfterBody5;
                                do {
                                    out.print(I18N.getMsg("dc.mdm.enroll.ad_title", new Object[0]));
                                    evalDoAfterBody5 = _jspx_th_c_005fwhen_005f3.doAfterBody();
                                } while (evalDoAfterBody5 == 2);
                            }
                            if (_jspx_th_c_005fwhen_005f3.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f3);
                            _jspx_th_c_005fwhen_005f3_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f3_reused);
                        }
                        final WhenTag _jspx_th_c_005fwhen_005f4 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f4_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f4.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f4.setParent((Tag)_jspx_th_c_005fchoose_005f2);
                            _jspx_th_c_005fwhen_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode == 'Combined'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f4 = _jspx_th_c_005fwhen_005f4.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f4 != 0) {
                                int evalDoAfterBody6;
                                do {
                                    out.print(I18N.getMsg("dc.mdm.enroll.twofactor_title", new Object[0]));
                                    evalDoAfterBody6 = _jspx_th_c_005fwhen_005f4.doAfterBody();
                                } while (evalDoAfterBody6 == 2);
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
                        evalDoAfterBody7 = _jspx_th_c_005fchoose_005f2.doAfterBody();
                    } while (evalDoAfterBody7 == 2);
                }
                if (_jspx_th_c_005fchoose_005f2.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f2);
                _jspx_th_c_005fchoose_005f2_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f2_reused);
            }
            out.write("</div>");
            out.write("\n\t\t\n          \t    <div ");
            if (this._jspx_meth_c_005fchoose_005f3(_jspx_page_context)) {
                return;
            }
            out.write("  id=\"displayText\">");
            out.write("\n\t\t\t\t    <table width=\"100%\" ");
            if (this._jspx_meth_c_005fif_005f4(_jspx_page_context)) {
                return;
            }
            out.write(" cellspacing=\"0\" cellpadding=\"0\" >");
            out.write("\n\t\t\t\t\t\t");
            final IfTag _jspx_th_c_005fif_005f4 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f5_reused = false;
            try {
                _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f4.setParent((Tag)null);
                _jspx_th_c_005fif_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode != 'OTP'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
                if (_jspx_eval_c_005fif_005f4 != 0) {
                    int evalDoAfterBody8;
                    do {
                        out.write("\n                            ");
                        final IfTag _jspx_th_c_005fif_005f5 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f6_reused = false;
                        try {
                            _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f5.setParent((Tag)_jspx_th_c_005fif_005f4);
                            _jspx_th_c_005fif_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
                            if (_jspx_eval_c_005fif_005f5 != 0) {
                                int evalDoAfterBody5;
                                do {
                                    out.write("\n                                <tr>\n\t\t\t\t\t\t\t\t  <td>\n\t\t\t\t\t\t\t\t  <input ");
                                    if (this._jspx_meth_c_005fchoose_005f4((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("  type=\"email\" name=\"EmailAddress\" placeholder=\"");
                                    out.print(I18N.getMsg("dc.mdm.device_mgmt.email", new Object[0]));
                                    out.write("&nbsp;*\"  value=\"");
                                    out.write((String)PageContextImpl.proprietaryEvaluate("${email}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                    out.write("\" autocapitalize=\"off\"/>");
                                    out.write("\n\t\t\t\t\t\t\t\t  </td>\n\n                                </tr>\n                            ");
                                    evalDoAfterBody5 = _jspx_th_c_005fif_005f5.doAfterBody();
                                } while (evalDoAfterBody5 == 2);
                            }
                            if (_jspx_th_c_005fif_005f5.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f5);
                            _jspx_th_c_005fif_005f6_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f6_reused);
                        }
                        out.write("\n                            <tr>\n                                ");
                        final ChooseTag _jspx_th_c_005fchoose_005f3 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                        boolean _jspx_th_c_005fchoose_005f5_reused = false;
                        try {
                            _jspx_th_c_005fchoose_005f3.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fchoose_005f3.setParent((Tag)_jspx_th_c_005fif_005f4);
                            final int _jspx_eval_c_005fchoose_005f3 = _jspx_th_c_005fchoose_005f3.doStartTag();
                            if (_jspx_eval_c_005fchoose_005f3 != 0) {
                                int evalDoAfterBody9;
                                do {
                                    out.write("\n                                ");
                                    final WhenTag _jspx_th_c_005fwhen_005f5 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                    boolean _jspx_th_c_005fwhen_005f7_reused = false;
                                    try {
                                        _jspx_th_c_005fwhen_005f5.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fwhen_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f3);
                                        _jspx_th_c_005fwhen_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                        final int _jspx_eval_c_005fwhen_005f5 = _jspx_th_c_005fwhen_005f5.doStartTag();
                                        if (_jspx_eval_c_005fwhen_005f5 != 0) {
                                            do {
                                                out.write("\n                                    <td><input ");
                                                if (this._jspx_meth_c_005fchoose_005f6((JspTag)_jspx_th_c_005fwhen_005f5, _jspx_page_context)) {
                                                    return;
                                                }
                                                out.write("  type=\"userName\" placeholder=\"");
                                                out.print(I18N.getMsg("dc.common.USER_NAME", new Object[0]));
                                                out.write("&nbsp;*\" name=\"UserName\" value=\"");
                                                out.write((String)PageContextImpl.proprietaryEvaluate("${userName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                out.write("\" autocapitalize=\"off\" /></td>");
                                                out.write("\n                                ");
                                                evalDoAfterBody8 = _jspx_th_c_005fwhen_005f5.doAfterBody();
                                            } while (evalDoAfterBody8 == 2);
                                        }
                                        if (_jspx_th_c_005fwhen_005f5.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f5);
                                        _jspx_th_c_005fwhen_005f7_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f7_reused);
                                    }
                                    out.write("\n                                ");
                                    if (this._jspx_meth_c_005fotherwise_005f5((JspTag)_jspx_th_c_005fchoose_005f3, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\n                                ");
                                    evalDoAfterBody9 = _jspx_th_c_005fchoose_005f3.doAfterBody();
                                } while (evalDoAfterBody9 == 2);
                            }
                            if (_jspx_th_c_005fchoose_005f3.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f3);
                            _jspx_th_c_005fchoose_005f5_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f5_reused);
                        }
                        out.write("\n                            </tr>\n                            <tr>\n                                <td><input ");
                        if (this._jspx_meth_c_005fchoose_005f8((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                            return;
                        }
                        out.write(32);
                        if (this._jspx_meth_c_005fif_005f7((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                            return;
                        }
                        out.write(" type=\"password\" name=\"ADPassword\" placeholder=\"");
                        out.print(I18N.getMsg("dc.mdm.enroll.adpassword", new Object[0]));
                        out.write("&nbsp;*\"  value=\"\" autocapitalize=\"off\" /></td>");
                        out.write("\n                            </tr>\n\t\t\t\t\t\t\t\n                            <tr>\n                                 ");
                        final ChooseTag _jspx_th_c_005fchoose_005f4 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                        boolean _jspx_th_c_005fchoose_005f9_reused = false;
                        try {
                            _jspx_th_c_005fchoose_005f4.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fchoose_005f4.setParent((Tag)_jspx_th_c_005fif_005f4);
                            final int _jspx_eval_c_005fchoose_005f4 = _jspx_th_c_005fchoose_005f4.doStartTag();
                            if (_jspx_eval_c_005fchoose_005f4 != 0) {
                                int evalDoAfterBody13;
                                do {
                                    out.write("\n                                    ");
                                    final WhenTag _jspx_th_c_005fwhen_005f6 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                    boolean _jspx_th_c_005fwhen_005f11_reused = false;
                                    try {
                                        _jspx_th_c_005fwhen_005f6.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fwhen_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                                        _jspx_th_c_005fwhen_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                        final int _jspx_eval_c_005fwhen_005f6 = _jspx_th_c_005fwhen_005f6.doStartTag();
                                        if (_jspx_eval_c_005fwhen_005f6 != 0) {
                                            int evalDoAfterBody12;
                                            do {
                                                out.write("\n                                        <td>\n                                            ");
                                                final ChooseTag _jspx_th_c_005fchoose_005f5 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                                boolean _jspx_th_c_005fchoose_005f10_reused = false;
                                                try {
                                                    _jspx_th_c_005fchoose_005f5.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fchoose_005f5.setParent((Tag)_jspx_th_c_005fwhen_005f6);
                                                    final int _jspx_eval_c_005fchoose_005f5 = _jspx_th_c_005fchoose_005f5.doStartTag();
                                                    if (_jspx_eval_c_005fchoose_005f5 != 0) {
                                                        int evalDoAfterBody11;
                                                        do {
                                                            out.write("\n                                            ");
                                                            final WhenTag _jspx_th_c_005fwhen_005f7 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                            boolean _jspx_th_c_005fwhen_005f12_reused = false;
                                                            try {
                                                                _jspx_th_c_005fwhen_005f7.setPageContext(_jspx_page_context);
                                                                _jspx_th_c_005fwhen_005f7.setParent((Tag)_jspx_th_c_005fchoose_005f5);
                                                                _jspx_th_c_005fwhen_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${fn:length(DomainNameList) gt 1}", (Class)Boolean.TYPE, _jspx_page_context, phoneLoginPage_jsp._jspx_fnmap_0));
                                                                final int _jspx_eval_c_005fwhen_005f7 = _jspx_th_c_005fwhen_005f7.doStartTag();
                                                                if (_jspx_eval_c_005fwhen_005f7 != 0) {
                                                                    int evalDoAfterBody10;
                                                                    do {
                                                                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t<div ");
                                                                        if (this._jspx_meth_c_005fchoose_005f11((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                            return;
                                                                        }
                                                                        out.write(62);
                                                                        out.write("\n                                            <select id=\"DomainName\" class=\"\" name=\"DomainName\" >\n                                                <option ");
                                                                        if (this._jspx_meth_c_005fif_005f8((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                            return;
                                                                        }
                                                                        out.write(" value=\"-1\" >-- ");
                                                                        out.print(I18N.getMsg("dc.common.SELECT_DOMAIN", new Object[0]));
                                                                        out.write(" --</option>\n                                               ");
                                                                        if (this._jspx_meth_c_005fforEach_005f0((JspTag)_jspx_th_c_005fwhen_005f7, _jspx_page_context)) {
                                                                            return;
                                                                        }
                                                                        out.write("\n                                            </select>\n\t\t\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t\t\t\t");
                                                                        evalDoAfterBody10 = _jspx_th_c_005fwhen_005f7.doAfterBody();
                                                                    } while (evalDoAfterBody10 == 2);
                                                                }
                                                                if (_jspx_th_c_005fwhen_005f7.doEndTag() == 5) {
                                                                    return;
                                                                }
                                                                this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f7);
                                                                _jspx_th_c_005fwhen_005f12_reused = true;
                                                            }
                                                            finally {
                                                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f12_reused);
                                                            }
                                                            out.write("\n                                            ");
                                                            if (this._jspx_meth_c_005fotherwise_005f9((JspTag)_jspx_th_c_005fchoose_005f5, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write("\n                                            ");
                                                            evalDoAfterBody11 = _jspx_th_c_005fchoose_005f5.doAfterBody();
                                                        } while (evalDoAfterBody11 == 2);
                                                    }
                                                    if (_jspx_th_c_005fchoose_005f5.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f5);
                                                    _jspx_th_c_005fchoose_005f10_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f10_reused);
                                                }
                                                out.write("\n                                        </td>\n                                    ");
                                                evalDoAfterBody12 = _jspx_th_c_005fwhen_005f6.doAfterBody();
                                            } while (evalDoAfterBody12 == 2);
                                        }
                                        if (_jspx_th_c_005fwhen_005f6.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f6);
                                        _jspx_th_c_005fwhen_005f11_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f11_reused);
                                    }
                                    out.write("\n                                    ");
                                    final OtherwiseTag _jspx_th_c_005fotherwise_005f11 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                    boolean _jspx_th_c_005fotherwise_005f11_reused = false;
                                    try {
                                        _jspx_th_c_005fotherwise_005f11.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fotherwise_005f11.setParent((Tag)_jspx_th_c_005fchoose_005f4);
                                        final int _jspx_eval_c_005fotherwise_005f11 = _jspx_th_c_005fotherwise_005f11.doStartTag();
                                        if (_jspx_eval_c_005fotherwise_005f11 != 0) {
                                            int evalDoAfterBody12;
                                            do {
                                                out.write("\n                                        <td>                                      \n \t\t\t\t\t\t\t\t\t\t<input  ");
                                                if (this._jspx_meth_c_005fchoose_005f13((JspTag)_jspx_th_c_005fotherwise_005f11, _jspx_page_context)) {
                                                    return;
                                                }
                                                out.write("  name=\"DomainName\" value=\"");
                                                out.print(DMIAMEncoder.encodeHTMLAttribute(request.getAttribute("DomainNameList").toString()));
                                                out.write("\" readonly /> ");
                                                out.write("\n\t\t\t\t\t\t\t\t\t\t</td>\n\n                                    ");
                                                evalDoAfterBody12 = _jspx_th_c_005fotherwise_005f11.doAfterBody();
                                            } while (evalDoAfterBody12 == 2);
                                        }
                                        if (_jspx_th_c_005fotherwise_005f11.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f11);
                                        _jspx_th_c_005fotherwise_005f11_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f11_reused);
                                    }
                                    out.write("\n                                ");
                                    evalDoAfterBody13 = _jspx_th_c_005fchoose_005f4.doAfterBody();
                                } while (evalDoAfterBody13 == 2);
                            }
                            if (_jspx_th_c_005fchoose_005f4.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f4);
                            _jspx_th_c_005fchoose_005f9_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f9_reused);
                        }
                        out.write("\n                            </tr>\n\t\t\t\t\t\t\t");
                        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f10_reused = false;
                        try {
                            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_c_005fif_005f4);
                            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
                            if (_jspx_eval_c_005fif_005f6 != 0) {
                                int evalDoAfterBody13;
                                do {
                                    out.write("\n                                <tr ");
                                    if (this._jspx_meth_c_005fif_005f11((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(">\n                                    <td>\n                                        <div ");
                                    if (this._jspx_meth_c_005fif_005f12((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(">\n\t\t\t\t\t\t\t\t\t<div ");
                                    if (this._jspx_meth_c_005fchoose_005f14((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(32);
                                    out.write(62);
                                    out.write(32);
                                    out.write("\n                                        <select id=\"OwnedBy\" name=\"OwnedBy\" class=\"combofieldActive\">\n                                            ");
                                    final ChooseTag _jspx_th_c_005fchoose_005f6 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                                    boolean _jspx_th_c_005fchoose_005f15_reused = false;
                                    try {
                                        _jspx_th_c_005fchoose_005f6.setPageContext(_jspx_page_context);
                                        _jspx_th_c_005fchoose_005f6.setParent((Tag)_jspx_th_c_005fif_005f6);
                                        final int _jspx_eval_c_005fchoose_005f6 = _jspx_th_c_005fchoose_005f6.doStartTag();
                                        if (_jspx_eval_c_005fchoose_005f6 != 0) {
                                            int evalDoAfterBody16;
                                            do {
                                                out.write("\n                                                ");
                                                final WhenTag _jspx_th_c_005fwhen_005f8 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                boolean _jspx_th_c_005fwhen_005f17_reused = false;
                                                try {
                                                    _jspx_th_c_005fwhen_005f8.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fwhen_005f8.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                    _jspx_th_c_005fwhen_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${ownedByOption != null && ownedByOption == 'corporate'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fwhen_005f8 = _jspx_th_c_005fwhen_005f8.doStartTag();
                                                    if (_jspx_eval_c_005fwhen_005f8 != 0) {
                                                        int evalDoAfterBody11;
                                                        do {
                                                            out.write("\n                                                    <option value=\"corporate\">");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.corporate", new Object[0]));
                                                            out.write("</option>\n                                                ");
                                                            evalDoAfterBody11 = _jspx_th_c_005fwhen_005f8.doAfterBody();
                                                        } while (evalDoAfterBody11 == 2);
                                                    }
                                                    if (_jspx_th_c_005fwhen_005f8.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f8);
                                                    _jspx_th_c_005fwhen_005f17_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f17_reused);
                                                }
                                                out.write("\n                                                ");
                                                final WhenTag _jspx_th_c_005fwhen_005f9 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                                                boolean _jspx_th_c_005fwhen_005f18_reused = false;
                                                try {
                                                    _jspx_th_c_005fwhen_005f9.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fwhen_005f9.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                    _jspx_th_c_005fwhen_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${ownedByOption != null && ownedByOption == 'personal'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fwhen_005f9 = _jspx_th_c_005fwhen_005f9.doStartTag();
                                                    if (_jspx_eval_c_005fwhen_005f9 != 0) {
                                                        int evalDoAfterBody14;
                                                        do {
                                                            out.write("\n                                                    <option value=\"personal\" ");
                                                            if (this._jspx_meth_c_005fif_005f13((JspTag)_jspx_th_c_005fwhen_005f9, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write(62);
                                                            out.print(I18N.getMsg("dc.mdm.enroll.personal", new Object[0]));
                                                            out.write("</option>\n                                                ");
                                                            evalDoAfterBody14 = _jspx_th_c_005fwhen_005f9.doAfterBody();
                                                        } while (evalDoAfterBody14 == 2);
                                                    }
                                                    if (_jspx_th_c_005fwhen_005f9.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f9);
                                                    _jspx_th_c_005fwhen_005f18_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f18_reused);
                                                }
                                                out.write("\n                                                ");
                                                final OtherwiseTag _jspx_th_c_005fotherwise_005f12 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                                                boolean _jspx_th_c_005fotherwise_005f14_reused = false;
                                                try {
                                                    _jspx_th_c_005fotherwise_005f12.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fotherwise_005f12.setParent((Tag)_jspx_th_c_005fchoose_005f6);
                                                    final int _jspx_eval_c_005fotherwise_005f12 = _jspx_th_c_005fotherwise_005f12.doStartTag();
                                                    if (_jspx_eval_c_005fotherwise_005f12 != 0) {
                                                        int evalDoAfterBody15;
                                                        do {
                                                            out.write("\n                                                    <option value=\"-1\">-- ");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.owned_by", new Object[0]));
                                                            out.write(" --</option>\n                                                    <option value=\"corporate\">");
                                                            out.print(I18N.getMsg("dc.mdm.enroll.corporate", new Object[0]));
                                                            out.write("</option>\n                                                    <option value=\"personal\" ");
                                                            if (this._jspx_meth_c_005fif_005f14((JspTag)_jspx_th_c_005fotherwise_005f12, _jspx_page_context)) {
                                                                return;
                                                            }
                                                            out.write(62);
                                                            out.print(I18N.getMsg("dc.mdm.enroll.personal", new Object[0]));
                                                            out.write("</option>\n                                                ");
                                                            evalDoAfterBody15 = _jspx_th_c_005fotherwise_005f12.doAfterBody();
                                                        } while (evalDoAfterBody15 == 2);
                                                    }
                                                    if (_jspx_th_c_005fotherwise_005f12.doEndTag() == 5) {
                                                        return;
                                                    }
                                                    this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f12);
                                                    _jspx_th_c_005fotherwise_005f14_reused = true;
                                                }
                                                finally {
                                                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f14_reused);
                                                }
                                                out.write("\n                                            ");
                                                evalDoAfterBody16 = _jspx_th_c_005fchoose_005f6.doAfterBody();
                                            } while (evalDoAfterBody16 == 2);
                                        }
                                        if (_jspx_th_c_005fchoose_005f6.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f6);
                                        _jspx_th_c_005fchoose_005f15_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f15_reused);
                                    }
                                    out.write("\n                                        </select>\n\t\t\t\t\t\t\t\t\t</div>\n                                        </div>\n                                    </td>\n                                </tr>\n                            ");
                                    evalDoAfterBody13 = _jspx_th_c_005fif_005f6.doAfterBody();
                                } while (evalDoAfterBody13 == 2);
                            }
                            if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
                            _jspx_th_c_005fif_005f10_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f10_reused);
                        }
                        out.write("\n                            ");
                        evalDoAfterBody8 = _jspx_th_c_005fif_005f4.doAfterBody();
                    } while (evalDoAfterBody8 == 2);
                }
                if (_jspx_th_c_005fif_005f4.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f4);
                _jspx_th_c_005fif_005f5_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f5_reused);
            }
            out.write("\n                            ");
            final IfTag _jspx_th_c_005fif_005f7 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f15_reused = false;
            try {
                _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f7.setParent((Tag)null);
                _jspx_th_c_005fif_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${authMode != 'ActiveDirectory'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
                if (_jspx_eval_c_005fif_005f7 != 0) {
                    int evalDoAfterBody17;
                    do {
                        out.write("\n\t\t\t\t\t\t\t<tr>\n                                <td><input ");
                        if (this._jspx_meth_c_005fchoose_005f16((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                            return;
                        }
                        out.write(" onkeypress=\"submitForm(event)\" type=\"password\" name=\"OTPPassword\" placeholder=\"");
                        out.print(I18N.getMsg("dc.mdm.enroll.otppasscode", new Object[0]));
                        out.write("&nbsp;*\"  value=\"\" autocapitalize=\"off\" autocomplete=\"off\" /></td>");
                        out.write("\n                            </tr>\n\t\t\t\t\t\t\t");
                        evalDoAfterBody17 = _jspx_th_c_005fif_005f7.doAfterBody();
                    } while (evalDoAfterBody17 == 2);
                }
                if (_jspx_th_c_005fif_005f7.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f7);
                _jspx_th_c_005fif_005f15_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f15_reused);
            }
            out.write("\n                        </table>\n                    </div>\n                    ");
            final IfTag _jspx_th_c_005fif_005f8 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f16_reused = false;
            try {
                _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f8.setParent((Tag)null);
                _jspx_th_c_005fif_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${showErrorMsg == \"true\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
                if (_jspx_eval_c_005fif_005f8 != 0) {
                    int evalDoAfterBody7;
                    do {
                        out.write("\n                        <div class=\"errorMessage\" id=\"errormsg\">");
                        out.print(I18N.getMsg("dc.mdm.enroll.ad_error_msg", new Object[0]));
                        out.write("</div>\n                    ");
                        evalDoAfterBody7 = _jspx_th_c_005fif_005f8.doAfterBody();
                    } while (evalDoAfterBody7 == 2);
                }
                if (_jspx_th_c_005fif_005f8.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f8);
                _jspx_th_c_005fif_005f16_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f16_reused);
            }
            out.write("\n                    ");
            if (this._jspx_meth_c_005fif_005f17(_jspx_page_context)) {
                return;
            }
            out.write("\n\t\t\t\t\t<div ");
            if (this._jspx_meth_c_005fchoose_005f17(_jspx_page_context)) {
                return;
            }
            out.write(" id=\"continueBtn\">");
            out.write("\n\t\t\t\t\t    ");
            final IfTag _jspx_th_c_005fif_005f9 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f18_reused = false;
            try {
                _jspx_th_c_005fif_005f9.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f9.setParent((Tag)null);
                _jspx_th_c_005fif_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isDemoMode != 'true'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f9.doStartTag();
                if (_jspx_eval_c_005fif_005f9 != 0) {
                    int evalDoAfterBody18;
                    do {
                        out.write("<input ");
                        if (this._jspx_meth_c_005fif_005f19((JspTag)_jspx_th_c_005fif_005f9, _jspx_page_context)) {
                            return;
                        }
                        out.write(" onclick=\"javascript:validateADInput();\" type=\"button\" value=");
                        out.print(I18N.getMsg("dc.common.button.CONTINUE", new Object[0]));
                        out.write(" />\n\t\t\t\t\t    ");
                        evalDoAfterBody18 = _jspx_th_c_005fif_005f9.doAfterBody();
                    } while (evalDoAfterBody18 == 2);
                }
                if (_jspx_th_c_005fif_005f9.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f9);
                _jspx_th_c_005fif_005f18_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f18_reused);
            }
            out.write("\n                        ");
            final IfTag _jspx_th_c_005fif_005f10 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f20_reused = false;
            try {
                _jspx_th_c_005fif_005f10.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f10.setParent((Tag)null);
                _jspx_th_c_005fif_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isDemoMode == 'true'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f10 = _jspx_th_c_005fif_005f10.doStartTag();
                if (_jspx_eval_c_005fif_005f10 != 0) {
                    int evalDoAfterBody8;
                    do {
                        out.write("<input class=\"");
                        if (this._jspx_meth_c_005fif_005f21((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                            return;
                        }
                        out.write(" buttongrey\" type=\"button\" value=");
                        out.print(I18N.getMsg("dc.common.button.CONTINUE", new Object[0]));
                        out.write(" /><div class=\"bodybold\" style=\"text-align: center;\">[");
                        if (this._jspx_meth_c_005fout_005f1((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                            return;
                        }
                        out.write("]</div>");
                        evalDoAfterBody8 = _jspx_th_c_005fif_005f10.doAfterBody();
                    } while (evalDoAfterBody8 == 2);
                }
                if (_jspx_th_c_005fif_005f10.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f10);
                _jspx_th_c_005fif_005f20_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f20_reused);
            }
            out.write("\n\t\t\t\t   </div>\n\t\t\t<input type=\"hidden\" name=\"EnrollmentRequestID\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${EnrollmentRequestID}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"/>\n\t\t\t<input type=\"hidden\" name=\"AuthMode\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${authMode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"/>\n\t\t\t<input type=\"hidden\" name=\"platformStr\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${platformStr}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"/>\n                        <input type=\"hidden\" name=\"IncludeBasicValidations\" value=\"true\"/>\n\t\t\t");
            if (this._jspx_meth_c_005fchoose_005f18(_jspx_page_context)) {
                return;
            }
            out.write("\n\t\t<div class=\"fotterDivFixedIphone fotterDivFixedIphoneBlue\">\n\t\t\t<p>&#169 ");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]));
            out.write("</p>\n\t\t</div>\n</div>\n\n\n        ");
            if (this._jspx_meth_c_005fif_005f22(_jspx_page_context)) {
                return;
            }
            out.write("\n\t<script src=\"/js/mdm/cookieCheck.js?91042\"></script>\n        <script src=\"/js/mdm/enrollRestrict.js\"></script>\n         <script>\n         if(areCookiesEnabled(");
            if (this._jspx_meth_c_005fchoose_005f19(_jspx_page_context)) {
                return;
            }
            out.write("))\n         {\n             isServerTimeSynced(");
            if (this._jspx_meth_c_005fchoose_005f20(_jspx_page_context)) {
                return;
            }
            out.write(44);
            out.write((String)PageContextImpl.proprietaryEvaluate("${CERTIFICATE_CREATION_DATE}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(");//No I18N\n         }\n \t</script>\n\t</body>\n</html>\n");
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
            phoneLoginPage_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
                    out.write("class=\"displayContentIphone\"");
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
                    out.write("class=\"displayContent\"");
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
    
    private boolean _jspx_meth_c_005fif_005f3(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f3_reused = false;
        try {
            _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f3.setParent((Tag)null);
            _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${platform!='1'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
            if (_jspx_eval_c_005fif_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"hide\"");
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
    
    private boolean _jspx_meth_c_005fchoose_005f1(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f1 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f1_reused = false;
        try {
            _jspx_th_c_005fchoose_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f1.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f1 = _jspx_th_c_005fchoose_005f1.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f1 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f1((JspTag)_jspx_th_c_005fchoose_005f1, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f1((JspTag)_jspx_th_c_005fchoose_005f1, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f1.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
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
                    out.write("class=\"headerIphone\"");
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
                    out.write("class=\"header\"");
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
                    while (!this._jspx_meth_c_005fwhen_005f5((JspTag)_jspx_th_c_005fchoose_005f3, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f2((JspTag)_jspx_th_c_005fchoose_005f3, _jspx_page_context)) {
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
    
    private boolean _jspx_meth_c_005fwhen_005f5(final JspTag _jspx_th_c_005fchoose_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f5 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f5_reused = false;
        try {
            _jspx_th_c_005fwhen_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f5.setParent((Tag)_jspx_th_c_005fchoose_005f3);
            _jspx_th_c_005fwhen_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f5 = _jspx_th_c_005fwhen_005f5.doStartTag();
            if (_jspx_eval_c_005fwhen_005f5 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"inputareaIphone\"");
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
    
    private boolean _jspx_meth_c_005fotherwise_005f2(final JspTag _jspx_th_c_005fchoose_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f2 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f2_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f2.setParent((Tag)_jspx_th_c_005fchoose_005f3);
            final int _jspx_eval_c_005fotherwise_005f2 = _jspx_th_c_005fotherwise_005f2.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"inputarea\" width=\"100%\" height=\"100%\"");
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
    
    private boolean _jspx_meth_c_005fif_005f4(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f4 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f4_reused = false;
        try {
            _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f4.setParent((Tag)null);
            _jspx_th_c_005fif_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType!='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
            if (_jspx_eval_c_005fif_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("height=\"100%\" order=\"0\"");
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
    
    private boolean _jspx_meth_c_005fchoose_005f4(final JspTag _jspx_th_c_005fif_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f4 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f4_reused = false;
        try {
            _jspx_th_c_005fchoose_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f4.setParent((Tag)_jspx_th_c_005fif_005f6);
            final int _jspx_eval_c_005fchoose_005f4 = _jspx_th_c_005fchoose_005f4.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f4 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f6((JspTag)_jspx_th_c_005fchoose_005f4, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f3((JspTag)_jspx_th_c_005fchoose_005f4, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f4.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
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
    
    private boolean _jspx_meth_c_005fwhen_005f6(final JspTag _jspx_th_c_005fchoose_005f4, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f6 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f6_reused = false;
        try {
            _jspx_th_c_005fwhen_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f4);
            _jspx_th_c_005fwhen_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f6 = _jspx_th_c_005fwhen_005f6.doStartTag();
            if (_jspx_eval_c_005fwhen_005f6 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActiveIphone\"");
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
    
    private boolean _jspx_meth_c_005fotherwise_005f3(final JspTag _jspx_th_c_005fchoose_005f4, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f3 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f3_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f3.setParent((Tag)_jspx_th_c_005fchoose_005f4);
            final int _jspx_eval_c_005fotherwise_005f3 = _jspx_th_c_005fotherwise_005f3.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActive\"");
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
    
    private boolean _jspx_meth_c_005fchoose_005f6(final JspTag _jspx_th_c_005fwhen_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f6 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f6_reused = false;
        try {
            _jspx_th_c_005fchoose_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f6.setParent((Tag)_jspx_th_c_005fwhen_005f7);
            final int _jspx_eval_c_005fchoose_005f6 = _jspx_th_c_005fchoose_005f6.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f6 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f8((JspTag)_jspx_th_c_005fchoose_005f6, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f4((JspTag)_jspx_th_c_005fchoose_005f6, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f6.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
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
    
    private boolean _jspx_meth_c_005fwhen_005f8(final JspTag _jspx_th_c_005fchoose_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f8 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f8_reused = false;
        try {
            _jspx_th_c_005fwhen_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f8.setParent((Tag)_jspx_th_c_005fchoose_005f6);
            _jspx_th_c_005fwhen_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f8 = _jspx_th_c_005fwhen_005f8.doStartTag();
            if (_jspx_eval_c_005fwhen_005f8 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActiveIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f8.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f8.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f8);
            _jspx_th_c_005fwhen_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f4(final JspTag _jspx_th_c_005fchoose_005f6, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f4 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f4_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f4.setParent((Tag)_jspx_th_c_005fchoose_005f6);
            final int _jspx_eval_c_005fotherwise_005f4 = _jspx_th_c_005fotherwise_005f4.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActive\"");
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
                    out.write("\n                                     <td><input ");
                    if (this._jspx_meth_c_005fchoose_005f7((JspTag)_jspx_th_c_005fotherwise_005f5, _jspx_page_context)) {
                        return true;
                    }
                    out.write(" type=\"userName\" name=\"UserName\" value=\"");
                    out.write((String)PageContextImpl.proprietaryEvaluate("${userName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                    out.write("\" autocapitalize=\"off\" readonly /></td>\n\t\t\t\t\t\t\t    ");
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
    
    private boolean _jspx_meth_c_005fchoose_005f7(final JspTag _jspx_th_c_005fotherwise_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f7 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f7_reused = false;
        try {
            _jspx_th_c_005fchoose_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f7.setParent((Tag)_jspx_th_c_005fotherwise_005f5);
            final int _jspx_eval_c_005fchoose_005f7 = _jspx_th_c_005fchoose_005f7.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f7 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f9((JspTag)_jspx_th_c_005fchoose_005f7, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f6((JspTag)_jspx_th_c_005fchoose_005f7, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f7.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f7.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f7);
            _jspx_th_c_005fchoose_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f9(final JspTag _jspx_th_c_005fchoose_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f9 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f9_reused = false;
        try {
            _jspx_th_c_005fwhen_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f9.setParent((Tag)_jspx_th_c_005fchoose_005f7);
            _jspx_th_c_005fwhen_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f9 = _jspx_th_c_005fwhen_005f9.doStartTag();
            if (_jspx_eval_c_005fwhen_005f9 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActiveIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f9.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f9.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f9);
            _jspx_th_c_005fwhen_005f9_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f9_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f6(final JspTag _jspx_th_c_005fchoose_005f7, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f6 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f6_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f6.setParent((Tag)_jspx_th_c_005fchoose_005f7);
            final int _jspx_eval_c_005fotherwise_005f6 = _jspx_th_c_005fotherwise_005f6.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f6 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActive\"");
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
    
    private boolean _jspx_meth_c_005fchoose_005f8(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f8 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f8_reused = false;
        try {
            _jspx_th_c_005fchoose_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f8.setParent((Tag)_jspx_th_c_005fif_005f5);
            final int _jspx_eval_c_005fchoose_005f8 = _jspx_th_c_005fchoose_005f8.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f8 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f10((JspTag)_jspx_th_c_005fchoose_005f8, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f7((JspTag)_jspx_th_c_005fchoose_005f8, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f8.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f8.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f8);
            _jspx_th_c_005fchoose_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f10(final JspTag _jspx_th_c_005fchoose_005f8, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f10 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f10_reused = false;
        try {
            _jspx_th_c_005fwhen_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f10.setParent((Tag)_jspx_th_c_005fchoose_005f8);
            _jspx_th_c_005fwhen_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f10 = _jspx_th_c_005fwhen_005f10.doStartTag();
            if (_jspx_eval_c_005fwhen_005f10 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActiveIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f10.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f10.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f10);
            _jspx_th_c_005fwhen_005f10_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f10_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f7(final JspTag _jspx_th_c_005fchoose_005f8, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f7 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f7_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f7.setParent((Tag)_jspx_th_c_005fchoose_005f8);
            final int _jspx_eval_c_005fotherwise_005f7 = _jspx_th_c_005fotherwise_005f7.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f7 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActive\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f7.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f7.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f7);
            _jspx_th_c_005fotherwise_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f7(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f7_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll != false && authMode == 'ActiveDirectory'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f6.doStartTag();
            if (_jspx_eval_c_005fif_005f7 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("onkeypress=\"submitForm(event)\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
            _jspx_th_c_005fif_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f11(final JspTag _jspx_th_c_005fwhen_005f12, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f11 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f11_reused = false;
        try {
            _jspx_th_c_005fchoose_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f11.setParent((Tag)_jspx_th_c_005fwhen_005f12);
            final int _jspx_eval_c_005fchoose_005f11 = _jspx_th_c_005fchoose_005f11.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f11 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f13((JspTag)_jspx_th_c_005fchoose_005f11, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f8((JspTag)_jspx_th_c_005fchoose_005f11, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f11.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f11);
            _jspx_th_c_005fchoose_005f11_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f11_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f13(final JspTag _jspx_th_c_005fchoose_005f11, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f13 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f13_reused = false;
        try {
            _jspx_th_c_005fwhen_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f13.setParent((Tag)_jspx_th_c_005fchoose_005f11);
            _jspx_th_c_005fwhen_005f13.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f13 = _jspx_th_c_005fwhen_005f13.doStartTag();
            if (_jspx_eval_c_005fwhen_005f13 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"combofieldIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f13.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f13.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f13);
            _jspx_th_c_005fwhen_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f8(final JspTag _jspx_th_c_005fchoose_005f11, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f8 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f8_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f8.setParent((Tag)_jspx_th_c_005fchoose_005f11);
            final int _jspx_eval_c_005fotherwise_005f8 = _jspx_th_c_005fotherwise_005f8.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f8 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"combofield\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f8.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f8.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f8);
            _jspx_th_c_005fotherwise_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f8(final JspTag _jspx_th_c_005fwhen_005f12, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f8 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f8_reused = false;
        try {
            _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f8.setParent((Tag)_jspx_th_c_005fwhen_005f12);
            _jspx_th_c_005fif_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
            if (_jspx_eval_c_005fif_005f8 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"defaultOption\"");
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
    
    private boolean _jspx_meth_c_005fforEach_005f0(final JspTag _jspx_th_c_005fwhen_005f12, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        JspWriter out = _jspx_page_context.getOut();
        final ForEachTag _jspx_th_c_005fforEach_005f0 = (ForEachTag)this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.get((Class)ForEachTag.class);
        boolean _jspx_th_c_005fforEach_005f0_reused = false;
        try {
            _jspx_th_c_005fforEach_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fforEach_005f0.setParent((Tag)_jspx_th_c_005fwhen_005f12);
            _jspx_th_c_005fforEach_005f0.setVar("domainVal");
            _jspx_th_c_005fforEach_005f0.setItems(new JspValueExpression("/jsp/mdm/enroll/phoneLoginPage.jsp(233,47) '${DomainNameList}'", this._jsp_getExpressionFactory().createValueExpression(_jspx_page_context.getELContext(), "${DomainNameList}", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            _jspx_th_c_005fforEach_005f0.setVarStatus("status");
            final int[] _jspx_push_body_count_c_005fforEach_005f0 = { 0 };
            try {
                final int _jspx_eval_c_005fforEach_005f0 = _jspx_th_c_005fforEach_005f0.doStartTag();
                if (_jspx_eval_c_005fforEach_005f0 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n                                                <option ");
                        if (this._jspx_meth_c_005fif_005f9((JspTag)_jspx_th_c_005fforEach_005f0, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f0)) {
                            return true;
                        }
                        out.write(" value=\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${domainVal}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write(34);
                        out.write(32);
                        out.write(62);
                        if (this._jspx_meth_c_005fout_005f0((JspTag)_jspx_th_c_005fforEach_005f0, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f0)) {
                            return true;
                        }
                        out.write("</option>");
                        out.write("\n                                                ");
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
    
    private boolean _jspx_meth_c_005fif_005f9(final JspTag _jspx_th_c_005fforEach_005f0, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f0) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f9 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f9_reused = false;
        try {
            _jspx_th_c_005fif_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f9.setParent((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fif_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f9.doStartTag();
            if (_jspx_eval_c_005fif_005f9 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"selectoptions\" ");
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
    
    private boolean _jspx_meth_c_005fout_005f0(final JspTag _jspx_th_c_005fforEach_005f0, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f0) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f0 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f0_reused = false;
        try {
            _jspx_th_c_005fout_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f0.setParent((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${domainVal}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fotherwise_005f9(final JspTag _jspx_th_c_005fchoose_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f9 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f9_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f9.setParent((Tag)_jspx_th_c_005fchoose_005f10);
            final int _jspx_eval_c_005fotherwise_005f9 = _jspx_th_c_005fotherwise_005f9.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f9 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                               ");
                    if (this._jspx_meth_c_005fforEach_005f1((JspTag)_jspx_th_c_005fotherwise_005f9, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n                                            ");
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
    
    private boolean _jspx_meth_c_005fforEach_005f1(final JspTag _jspx_th_c_005fotherwise_005f9, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        JspWriter out = _jspx_page_context.getOut();
        final ForEachTag _jspx_th_c_005fforEach_005f1 = (ForEachTag)this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.get((Class)ForEachTag.class);
        boolean _jspx_th_c_005fforEach_005f1_reused = false;
        try {
            _jspx_th_c_005fforEach_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fforEach_005f1.setParent((Tag)_jspx_th_c_005fotherwise_005f9);
            _jspx_th_c_005fforEach_005f1.setVar("domainVal");
            _jspx_th_c_005fforEach_005f1.setItems(new JspValueExpression("/jsp/mdm/enroll/phoneLoginPage.jsp(240,47) '${DomainNameList}'", this._jsp_getExpressionFactory().createValueExpression(_jspx_page_context.getELContext(), "${DomainNameList}", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            _jspx_th_c_005fforEach_005f1.setVarStatus("status");
            final int[] _jspx_push_body_count_c_005fforEach_005f1 = { 0 };
            try {
                final int _jspx_eval_c_005fforEach_005f1 = _jspx_th_c_005fforEach_005f1.doStartTag();
                if (_jspx_eval_c_005fforEach_005f1 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n\t\t\t\t\t\t\t\t\t\t\t\t\n                                                    <input ");
                        if (this._jspx_meth_c_005fchoose_005f12((JspTag)_jspx_th_c_005fforEach_005f1, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f1)) {
                            return true;
                        }
                        out.write(" name=\"DomainName\" value=\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${domainVal}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("\" readonly />\n                                                ");
                        evalDoAfterBody = _jspx_th_c_005fforEach_005f1.doAfterBody();
                    } while (evalDoAfterBody == 2);
                }
                if (_jspx_th_c_005fforEach_005f1.doEndTag() == 5) {
                    return true;
                }
            }
            catch (final Throwable _jspx_exception) {
                while (_jspx_push_body_count_c_005fforEach_005f1[0]-- > 0) {
                    out = _jspx_page_context.popBody();
                }
                _jspx_th_c_005fforEach_005f1.doCatch(_jspx_exception);
            }
            finally {
                _jspx_th_c_005fforEach_005f1.doFinally();
            }
            this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.reuse((Tag)_jspx_th_c_005fforEach_005f1);
            _jspx_th_c_005fforEach_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fforEach_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fforEach_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f12(final JspTag _jspx_th_c_005fforEach_005f1, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f1) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f12 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f12_reused = false;
        try {
            _jspx_th_c_005fchoose_005f12.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f12.setParent((Tag)_jspx_th_c_005fforEach_005f1);
            final int _jspx_eval_c_005fchoose_005f12 = _jspx_th_c_005fchoose_005f12.doStartTag();
            Label_0128: {
                if (_jspx_eval_c_005fchoose_005f12 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f14((JspTag)_jspx_th_c_005fchoose_005f12, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f1)) {
                        if (this._jspx_meth_c_005fotherwise_005f10((JspTag)_jspx_th_c_005fchoose_005f12, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f1)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f12.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0128;
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
    
    private boolean _jspx_meth_c_005fwhen_005f14(final JspTag _jspx_th_c_005fchoose_005f12, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f1) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f14 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f14_reused = false;
        try {
            _jspx_th_c_005fwhen_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f14.setParent((Tag)_jspx_th_c_005fchoose_005f12);
            _jspx_th_c_005fwhen_005f14.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f14 = _jspx_th_c_005fwhen_005f14.doStartTag();
            if (_jspx_eval_c_005fwhen_005f14 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldDisableIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f14.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f14.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f14);
            _jspx_th_c_005fwhen_005f14_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f14_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f10(final JspTag _jspx_th_c_005fchoose_005f12, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f1) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f10 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f10_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f10.setParent((Tag)_jspx_th_c_005fchoose_005f12);
            final int _jspx_eval_c_005fotherwise_005f10 = _jspx_th_c_005fotherwise_005f10.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f10 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldDisable\"");
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
    
    private boolean _jspx_meth_c_005fchoose_005f13(final JspTag _jspx_th_c_005fotherwise_005f11, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f13 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f13_reused = false;
        try {
            _jspx_th_c_005fchoose_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f13.setParent((Tag)_jspx_th_c_005fotherwise_005f11);
            final int _jspx_eval_c_005fchoose_005f13 = _jspx_th_c_005fchoose_005f13.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f13 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f15((JspTag)_jspx_th_c_005fchoose_005f13, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f12((JspTag)_jspx_th_c_005fchoose_005f13, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f13.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
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
    
    private boolean _jspx_meth_c_005fwhen_005f15(final JspTag _jspx_th_c_005fchoose_005f13, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f15 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f15_reused = false;
        try {
            _jspx_th_c_005fwhen_005f15.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f15.setParent((Tag)_jspx_th_c_005fchoose_005f13);
            _jspx_th_c_005fwhen_005f15.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f15 = _jspx_th_c_005fwhen_005f15.doStartTag();
            if (_jspx_eval_c_005fwhen_005f15 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldDisableIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f15.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f15.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f15);
            _jspx_th_c_005fwhen_005f15_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f15_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f12(final JspTag _jspx_th_c_005fchoose_005f13, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f12 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f12_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f12.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f12.setParent((Tag)_jspx_th_c_005fchoose_005f13);
            final int _jspx_eval_c_005fotherwise_005f12 = _jspx_th_c_005fotherwise_005f12.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f12 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldDisable\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f12.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f12.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f12);
            _jspx_th_c_005fotherwise_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f11(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f11_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType!='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f11 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"bodytext\"");
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
    
    private boolean _jspx_meth_c_005fif_005f12(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f12_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${showOwnedByFilter == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f12 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f12 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("style=\"display:none;\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f11.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
            _jspx_th_c_005fif_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f14(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f14 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f14_reused = false;
        try {
            _jspx_th_c_005fchoose_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f14.setParent((Tag)_jspx_th_c_005fif_005f10);
            final int _jspx_eval_c_005fchoose_005f14 = _jspx_th_c_005fchoose_005f14.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f14 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f16((JspTag)_jspx_th_c_005fchoose_005f14, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f13((JspTag)_jspx_th_c_005fchoose_005f14, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f14.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
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
    
    private boolean _jspx_meth_c_005fwhen_005f16(final JspTag _jspx_th_c_005fchoose_005f14, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f16 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f16_reused = false;
        try {
            _jspx_th_c_005fwhen_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f16.setParent((Tag)_jspx_th_c_005fchoose_005f14);
            _jspx_th_c_005fwhen_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType!='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f16 = _jspx_th_c_005fwhen_005f16.doStartTag();
            if (_jspx_eval_c_005fwhen_005f16 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"combofield\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f16.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f16);
            _jspx_th_c_005fwhen_005f16_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f16_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f13(final JspTag _jspx_th_c_005fchoose_005f14, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f13 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f13_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f13.setParent((Tag)_jspx_th_c_005fchoose_005f14);
            final int _jspx_eval_c_005fotherwise_005f13 = _jspx_th_c_005fotherwise_005f13.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f13 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"combofieldIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f13.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f13.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f13);
            _jspx_th_c_005fotherwise_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f13(final JspTag _jspx_th_c_005fwhen_005f18, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f13 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f13_reused = false;
        try {
            _jspx_th_c_005fif_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f13.setParent((Tag)_jspx_th_c_005fwhen_005f18);
            _jspx_th_c_005fif_005f13.setTest((boolean)PageContextImpl.proprietaryEvaluate("${showOwnedByFilter == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f13 = _jspx_th_c_005fif_005f13.doStartTag();
            if (_jspx_eval_c_005fif_005f13 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("selected");
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
    
    private boolean _jspx_meth_c_005fif_005f14(final JspTag _jspx_th_c_005fotherwise_005f14, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f14 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f14_reused = false;
        try {
            _jspx_th_c_005fif_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f14.setParent((Tag)_jspx_th_c_005fotherwise_005f14);
            _jspx_th_c_005fif_005f14.setTest((boolean)PageContextImpl.proprietaryEvaluate("${showOwnedByFilter == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f14 = _jspx_th_c_005fif_005f14.doStartTag();
            if (_jspx_eval_c_005fif_005f14 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("selected");
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
    
    private boolean _jspx_meth_c_005fchoose_005f16(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f16 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f16_reused = false;
        try {
            _jspx_th_c_005fchoose_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f16.setParent((Tag)_jspx_th_c_005fif_005f15);
            final int _jspx_eval_c_005fchoose_005f16 = _jspx_th_c_005fchoose_005f16.doStartTag();
            Label_0125: {
                if (_jspx_eval_c_005fchoose_005f16 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f19((JspTag)_jspx_th_c_005fchoose_005f16, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f15((JspTag)_jspx_th_c_005fchoose_005f16, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f16.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0125;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f16);
            _jspx_th_c_005fchoose_005f16_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f16_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f19(final JspTag _jspx_th_c_005fchoose_005f16, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f19 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f19_reused = false;
        try {
            _jspx_th_c_005fwhen_005f19.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f19.setParent((Tag)_jspx_th_c_005fchoose_005f16);
            _jspx_th_c_005fwhen_005f19.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f19 = _jspx_th_c_005fwhen_005f19.doStartTag();
            if (_jspx_eval_c_005fwhen_005f19 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActiveIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f19.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f19.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f19);
            _jspx_th_c_005fwhen_005f19_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f19_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f15(final JspTag _jspx_th_c_005fchoose_005f16, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f15 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f15_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f15.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f15.setParent((Tag)_jspx_th_c_005fchoose_005f16);
            final int _jspx_eval_c_005fotherwise_005f15 = _jspx_th_c_005fotherwise_005f15.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f15 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"textfieldActive\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f15.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f15.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f15);
            _jspx_th_c_005fotherwise_005f15_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f15_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f17(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f17 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f17_reused = false;
        try {
            _jspx_th_c_005fif_005f17.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f17.setParent((Tag)null);
            _jspx_th_c_005fif_005f17.setTest((boolean)PageContextImpl.proprietaryEvaluate("${showErrorMsg != \"true\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f17 = _jspx_th_c_005fif_005f17.doStartTag();
            if (_jspx_eval_c_005fif_005f17 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                        <div id=\"errormsg\" class=\"hide\" id=\"errormsg\"></div>\n                    ");
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
    
    private boolean _jspx_meth_c_005fchoose_005f17(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f17 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f17_reused = false;
        try {
            _jspx_th_c_005fchoose_005f17.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f17.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f17 = _jspx_th_c_005fchoose_005f17.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f17 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f20((JspTag)_jspx_th_c_005fchoose_005f17, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f16((JspTag)_jspx_th_c_005fchoose_005f17, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f17.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f17.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f17);
            _jspx_th_c_005fchoose_005f17_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f17, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f17_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f20(final JspTag _jspx_th_c_005fchoose_005f17, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f20 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f20_reused = false;
        try {
            _jspx_th_c_005fwhen_005f20.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f20.setParent((Tag)_jspx_th_c_005fchoose_005f17);
            _jspx_th_c_005fwhen_005f20.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f20 = _jspx_th_c_005fwhen_005f20.doStartTag();
            if (_jspx_eval_c_005fwhen_005f20 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signinbtndivIphone\"");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f20.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f20.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f20);
            _jspx_th_c_005fwhen_005f20_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f20, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f20_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f16(final JspTag _jspx_th_c_005fchoose_005f17, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f16 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f16_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f16.setParent((Tag)_jspx_th_c_005fchoose_005f17);
            final int _jspx_eval_c_005fotherwise_005f16 = _jspx_th_c_005fotherwise_005f16.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f16 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signinbtndiv\"");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f16.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f16);
            _jspx_th_c_005fotherwise_005f16_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f16_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f19(final JspTag _jspx_th_c_005fif_005f18, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f19 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f19_reused = false;
        try {
            _jspx_th_c_005fif_005f19.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f19.setParent((Tag)_jspx_th_c_005fif_005f18);
            _jspx_th_c_005fif_005f19.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f19 = _jspx_th_c_005fif_005f19.doStartTag();
            if (_jspx_eval_c_005fif_005f19 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"signinbtnIphone\"");
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
    
    private boolean _jspx_meth_c_005fif_005f21(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f21 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f21_reused = false;
        try {
            _jspx_th_c_005fif_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f21.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fif_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f21 = _jspx_th_c_005fif_005f21.doStartTag();
            if (_jspx_eval_c_005fif_005f21 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("signinbtndivIphone");
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
    
    private boolean _jspx_meth_c_005fout_005f1(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f1 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f1_reused = false;
        try {
            _jspx_th_c_005fout_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f1.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fout_005f1.setValue(PageContextImpl.proprietaryEvaluate("${demoModeMessage}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fchoose_005f18(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f18 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f18_reused = false;
        try {
            _jspx_th_c_005fchoose_005f18.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f18.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f18 = _jspx_th_c_005fchoose_005f18.doStartTag();
            if (_jspx_eval_c_005fchoose_005f18 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n\t\t\t");
                    if (this._jspx_meth_c_005fwhen_005f21((JspTag)_jspx_th_c_005fchoose_005f18, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n\t\t\t");
                    if (this._jspx_meth_c_005fotherwise_005f17((JspTag)_jspx_th_c_005fchoose_005f18, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n\t\t\t");
                    evalDoAfterBody = _jspx_th_c_005fchoose_005f18.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fchoose_005f18.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f18);
            _jspx_th_c_005fchoose_005f18_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f18, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f18_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f21(final JspTag _jspx_th_c_005fchoose_005f18, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f21 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f21_reused = false;
        try {
            _jspx_th_c_005fwhen_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f21.setParent((Tag)_jspx_th_c_005fchoose_005f18);
            _jspx_th_c_005fwhen_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f21 = _jspx_th_c_005fwhen_005f21.doStartTag();
            if (_jspx_eval_c_005fwhen_005f21 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                        <input type=\"hidden\" name=\"ValidateEmailAddress\" value=\"true\"/>\n\t\t\t");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f21.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f21.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f21);
            _jspx_th_c_005fwhen_005f21_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f21_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f17(final JspTag _jspx_th_c_005fchoose_005f18, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f17 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f17_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f17.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f17.setParent((Tag)_jspx_th_c_005fchoose_005f18);
            final int _jspx_eval_c_005fotherwise_005f17 = _jspx_th_c_005fotherwise_005f17.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f17 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                        <input type=\"hidden\" name=\"ValidateEmailAddress\" value=\"false\"/>\n\t\t\t");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f17.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f17.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f17);
            _jspx_th_c_005fotherwise_005f17_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f17, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f17_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f22(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f22 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f22_reused = false;
        try {
            _jspx_th_c_005fif_005f22.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f22.setParent((Tag)null);
            _jspx_th_c_005fif_005f22.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isSelfEnroll == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f22 = _jspx_th_c_005fif_005f22.doStartTag();
            if (_jspx_eval_c_005fif_005f22 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n            <script language=\"Javascript\" type=\"text/javascript\">\n                document.getElementById(\"ownedBy\").value = ");
                    out.write((String)PageContextImpl.proprietaryEvaluate("${ownedBy}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                    out.write(";\n            </script>\n        ");
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
    
    private boolean _jspx_meth_c_005fchoose_005f19(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f19 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f19_reused = false;
        try {
            _jspx_th_c_005fchoose_005f19.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f19.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f19 = _jspx_th_c_005fchoose_005f19.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f19 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f22((JspTag)_jspx_th_c_005fchoose_005f19, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f18((JspTag)_jspx_th_c_005fchoose_005f19, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f19.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f19.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f19);
            _jspx_th_c_005fchoose_005f19_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f19_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f22(final JspTag _jspx_th_c_005fchoose_005f19, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f22 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f22_reused = false;
        try {
            _jspx_th_c_005fwhen_005f22.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f22.setParent((Tag)_jspx_th_c_005fchoose_005f19);
            _jspx_th_c_005fwhen_005f22.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f22 = _jspx_th_c_005fwhen_005f22.doStartTag();
            if (_jspx_eval_c_005fwhen_005f22 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'phone'");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f22.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f22.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f22);
            _jspx_th_c_005fwhen_005f22_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f22, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f22_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f18(final JspTag _jspx_th_c_005fchoose_005f19, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f18 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f18_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f18.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f18.setParent((Tag)_jspx_th_c_005fchoose_005f19);
            final int _jspx_eval_c_005fotherwise_005f18 = _jspx_th_c_005fotherwise_005f18.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f18 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'tablet'");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f18.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f18.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f18);
            _jspx_th_c_005fotherwise_005f18_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f18, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f18_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fchoose_005f20(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final ChooseTag _jspx_th_c_005fchoose_005f20 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
        boolean _jspx_th_c_005fchoose_005f20_reused = false;
        try {
            _jspx_th_c_005fchoose_005f20.setPageContext(_jspx_page_context);
            _jspx_th_c_005fchoose_005f20.setParent((Tag)null);
            final int _jspx_eval_c_005fchoose_005f20 = _jspx_th_c_005fchoose_005f20.doStartTag();
            Label_0121: {
                if (_jspx_eval_c_005fchoose_005f20 != 0) {
                    while (!this._jspx_meth_c_005fwhen_005f23((JspTag)_jspx_th_c_005fchoose_005f20, _jspx_page_context)) {
                        if (this._jspx_meth_c_005fotherwise_005f19((JspTag)_jspx_th_c_005fchoose_005f20, _jspx_page_context)) {
                            return true;
                        }
                        final int evalDoAfterBody = _jspx_th_c_005fchoose_005f20.doAfterBody();
                        if (evalDoAfterBody != 2) {
                            break Label_0121;
                        }
                    }
                    return true;
                }
            }
            if (_jspx_th_c_005fchoose_005f20.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fchoose.reuse((Tag)_jspx_th_c_005fchoose_005f20);
            _jspx_th_c_005fchoose_005f20_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fchoose_005f20, this._jsp_getInstanceManager(), _jspx_th_c_005fchoose_005f20_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fwhen_005f23(final JspTag _jspx_th_c_005fchoose_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final WhenTag _jspx_th_c_005fwhen_005f23 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
        boolean _jspx_th_c_005fwhen_005f23_reused = false;
        try {
            _jspx_th_c_005fwhen_005f23.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f23.setParent((Tag)_jspx_th_c_005fchoose_005f20);
            _jspx_th_c_005fwhen_005f23.setTest((boolean)PageContextImpl.proprietaryEvaluate("${userAgentType=='phone'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fwhen_005f23 = _jspx_th_c_005fwhen_005f23.doStartTag();
            if (_jspx_eval_c_005fwhen_005f23 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'phone'");
                    evalDoAfterBody = _jspx_th_c_005fwhen_005f23.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fwhen_005f23.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse((Tag)_jspx_th_c_005fwhen_005f23);
            _jspx_th_c_005fwhen_005f23_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fwhen_005f23, this._jsp_getInstanceManager(), _jspx_th_c_005fwhen_005f23_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fotherwise_005f19(final JspTag _jspx_th_c_005fchoose_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OtherwiseTag _jspx_th_c_005fotherwise_005f19 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
        boolean _jspx_th_c_005fotherwise_005f19_reused = false;
        try {
            _jspx_th_c_005fotherwise_005f19.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f19.setParent((Tag)_jspx_th_c_005fchoose_005f20);
            final int _jspx_eval_c_005fotherwise_005f19 = _jspx_th_c_005fotherwise_005f19.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f19 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("'tablet'");
                    evalDoAfterBody = _jspx_th_c_005fotherwise_005f19.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fotherwise_005f19.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fotherwise.reuse((Tag)_jspx_th_c_005fotherwise_005f19);
            _jspx_th_c_005fotherwise_005f19_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fotherwise_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fotherwise_005f19_reused);
        }
        return false;
    }
    
    static {
        phoneLoginPage_jsp._jspx_fnmap_0 = ProtectedFunctionMapper.getMapForFunction("fn:length", (Class)Functions.class, "length", new Class[] { Object.class });
        _jspxFactory = JspFactory.getDefaultFactory();
        (phoneLoginPage_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        phoneLoginPage_jsp._jspx_imports_packages.add("javax.servlet.http");
        phoneLoginPage_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        phoneLoginPage_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        phoneLoginPage_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

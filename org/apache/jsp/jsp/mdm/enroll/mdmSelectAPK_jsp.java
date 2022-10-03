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

public final class mdmSelectAPK_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmSelectAPK_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmSelectAPK_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmSelectAPK_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmSelectAPK_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
    }
    
    public void _jspDestroy() {
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
            final PageContext pageContext = _jspx_page_context = mdmSelectAPK_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    ");
            out.write("\n    \n    \n    ");
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n    \n    \n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <!-- <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title> -->\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css\" />\n\t\t<script language=\"Javascript\" src=\"/js/mdm/agentDownload.js\" type=\"text/javascript\"></script>\n        <style type=\"text/css\">\n            .hide{display:none;}\n                        .Download-button\n                        {\n                            font-family: Roboto;\n                            font-size: 16px;\n                            font-weight: bold;\n                            color: white;\n                            background-color: #3a9dd6;\n                            border: solid 1px #ccc;\n                            text-decoration:none;\n                            padding:10px 20px;\n                            width: 180px;\n                            margin: 30px 0px;\n                        }\n                        a,a:active,a:visited\n                        {\n                            color: #229d01;\n                        }\n        </style>\n    </head>\n    <script  language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/AjaxAPI.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n    <script>\n\t\tdirectDownloadUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${downloadURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\t\thttpsDownloadUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${appHttpsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\t\tdownloadButtonId = \"serverDownloadButton\";\n\t\texternalDownloadUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${appExternalLink}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\t\tfilename = \"MDMAndroidAgent.apk\";\n\t\tmanageEnginePingUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${manageenginePingImgUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\n\t\tfunction enableDownloadButton(onClickFunction) {\n\t\t\tvar downloadButton = document.getElementById(downloadButtonId);\n\t\t\tdownloadButton.setAttribute(\"onClick\",onClickFunction);\n\t\t\tif(waitingForUrl == true) {\n\t\t\t\tdownloadButton.click();\n\t\t\t\twaitingForUrl = false;\n\t\t\t}\n\t\t}\n\t\tfunction downloadCertificate() {\n            callGetDownloadStatusRecursively();\n            location.href = \"/installCertificate.mob\";\n        }\n\n        // We are loosing JS Fn calling control, once we moved to other page. As a workaround\n        // same function will be called for every 1 second for 20 seconds using setTimeout method.\n        function callGetDownloadStatusRecursively() {\n\n            var callTimeInSeconds = 1;\n            while (callTimeInSeconds <= 20) {\n                setTimeout(\"getMobileConfigDownloadStatus()\", 2000 * callTimeInSeconds);\n                callTimeInSeconds++;\n            }\n            setTimeout(\"downloadAgent()\", 2000 * callTimeInSeconds);\n        }\n\n\n        function getMobileConfigDownloadStatus() {\n");
            out.write("\n            var url = '/getCertificateDownloadStatus.mob';  // No I18N\n            var req = AjaxAPI.getXMLHttpRequest();\n            req.open(\"POST\", url, true);\n            req.setRequestHeader(\"Connection\", \"close\");\n            req.onreadystatechange = function() {\n                checkDownloadStatusAndContinue(req);\n            };\n            req.send(null);\n        }\n\n        function checkDownloadStatusAndContinue(req) {\n            mobileConfigDownloadStatusCalled = true;\n            var downloadStatus = req.responseText;\n            if (downloadStatus != null && downloadStatus.trim() == \"true\") {\n                mobileConfigDownloadStatusCalled = false;\n                downloadAgent();\n            }\n        }\n        var isAgentDownload = false;\n        function downloadAgent() {\n            if(!isAgentDownload){\n                var isAndroidAgent = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isAndroidAgent}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n                var isThirdParty = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isThirdPartyEnabled}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n                var above4_2 = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${above4_2}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"// No I18N\n\t\t\t\tvar isForwardingServerEnable = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isEnableForwrdingServer}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n                if(isThirdParty == 'false' && isForwardingServerEnable == 'false'){\n                    location.href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${downloadURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n                }else{\n                    getAgentStatusRecursively();\n                    location.href = \"/DownloadAPK.mob?isAndroidAgent=\" + isAndroidAgent+\"&above4_2=\"+above4_2;\n                }\n                isAgentDownload = true;\n            }\n        }\n\n        function downloadFromServer(){\n            if(!isAgentDownload){\n                 var isAndroidAgent = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isAndroidAgent}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n                 var isThirdParty = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isThirdPartyEnabled}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n                 var above4_2 = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${above4_2}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"// No I18N\n            \t var isForwardingServerEnable = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isEnableForwrdingServer}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n                 if(isThirdParty == 'false' && isForwardingServerEnable == 'false'){\n                    location.href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${downloadURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n                }else{\n                    showCertificateDownloadConfig();\n                    getAgentStatusRecursively();\n                    location.href = \"/DownloadAPK.mob?isAndroidAgent=\" + isAndroidAgent+\"&above4_2=\"+above4_2;\n                }\n                isAgentDownload = true;\n            }\n        }\n        \n        function selectAndroidVersionInit()\n        {\n            var above4_2 = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${above4_2}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"// No I18N\n            if(above4_2 ==\"0\")\n            {\n                document.getElementById(\"safeVersionDiv\").style.display = \"\";\n            }\n            else\n            {\n                document.getElementById(\"safeVersionDiv\").style.display = \"none\";\n            }\n        }\n        \n        function getAgentStatusRecursively() {\n            var callTimeInSeconds = 1;\n            while (callTimeInSeconds <= 20) {\n                setTimeout(\"getAgentDownloadStatus()\", 1000 * callTimeInSeconds);\n                callTimeInSeconds++;\n            }\n            setTimeout(\"redirectSuccess()\", 1000 * callTimeInSeconds);\n        }\n\n        function getAgentDownloadStatus() {\n            var url = '/getCertificateDownloadStatus.mob';  // No I18N\n            var req = AjaxAPI.getXMLHttpRequest();\n            req.open(\"POST\", url, true);\n            req.setRequestHeader(\"Connection\", \"close\");\n            req.onreadystatechange = function() {\n                checkAgentStatusAndContinue(req);\n            };\n            req.send(null);\n");
            out.write("        }\n\n        function checkAgentStatusAndContinue(req) {\n            var downloadStatus = req.responseText;\n            if (downloadStatus != null && downloadStatus.trim() == \"true\") {\n                redirectSuccess();\n            }\n        }\n\n        function redirectSuccess() {\n            location.href = \"/mdm/enroll?actionToCall=agentDownloadSuccess\";\n        }\n\n        // When ready...\n        window.addEventListener(\"load\", function() {\n            // Set a timeout...\n            setTimeout(function() {\n                // Hide the address bar!\n                window.scrollTo(0, 1);\n            }, 0);\n        });\n        function showSAFEVersion(safeCheckbox)\n        {\n            if (safeCheckbox)\n            {\n                document.getElementById(\"safeVersionDiv\").className = \"\";\n            }\n            else\n            {\n                document.getElementById(\"safeVersionDiv\").className = \"hide\";\n            }\n        }\n\n         function showCertificateDownloadConfig(){\n            document.getElementById(\"agentDownloadInfo\").className = \"androidDivcontent\";\n");
            out.write("            //document.getElementById(\"agentDownloadConfig\").className = \"\";\n            document.getElementById(\"multiDownloadConfig\").className = \"hide\";\n         }\n\t\t \n\t\tfunction initDownloadConfig(){\n\t\t\tvar downloadMode = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${downloadMode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n\t\t\tif(downloadMode == '4'){\n\t\t\t\tdocument.getElementById(\"agentDownloadInfo\").className = \"hide\";\n\t\t\t\t//document.getElementById(\"agentDownloadConfig\").className = \"hide\";\n\t\t\t\tdocument.getElementById(\"multiDownloadConfig\").className = \"androidDivcontent\";\n\t\t\t\tdocument.getElementById(\"serverDownloadButton\").innerHTML = I18N.getMsg(\"dc.mdm.download.agent.from.corporate.network\");\n\t\t\t}else{\n\t\t\t\tdocument.getElementById(\"agentDownloadInfo\").className = \"androidDivcontent\";\n\t\t\t\t//document.getElementById(\"agentDownloadConfig\").className = \"\";\n\t\t\t\tdocument.getElementById(\"multiDownloadConfig\").className = \"hide\";\n\t\t\t\tdocument.getElementById(\"serverDownloadButton\").innerHTML = I18N.getMsg(\"dc.mdm.agent.default_name\");\n\t\t\t}\n\t\t}\n    </script>     \n    <body style=\"height: 100%\">\n\t\t<div id=\"downloadAppContent\">\n        <table width=\"100%\">\n            <tr class=\"androidBlueHeader\">\n                <td>&nbsp;&nbsp;&nbsp;<img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</td>\n            </tr>\n        </table>\n        <div class=\"androidDivcontent\" id=\"multiDownloadConfig\">\n            ");
            out.print(I18N.getMsg("dc.common.DOWNLOAD_ME_MDM", new Object[0]));
            out.write("\n            <p id=\"googlePlayDownloadOption\">\n                ");
            final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f0_reused = false;
            try {
                _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n                    ");
                        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f0_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isAndroidAgent != null && isAndroidAgent == false && above4_2 != null && above4_2 == 1}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n                        <center> <a href=\"");
                                    out.write((String)PageContextImpl.proprietaryEvaluate("${safeURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                    out.write("\" style=\"text-decoration: none;\"> <div class=\"nav Download-button\">");
                                    out.print(I18N.getMsg("dc.mdm.android.common.aent_download_mode.from_playstore", new Object[0]));
                                    out.write("</div> </a></center>\n                    ");
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
                                    out.write("\n                        <center> <a href=\"");
                                    out.write((String)PageContextImpl.proprietaryEvaluate("${androidURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                                    out.write("\" style=\"text-decoration: none;\"> <div class=\"nav Download-button\">");
                                    out.print(I18N.getMsg("dc.mdm.android.common.aent_download_mode.from_playstore", new Object[0]));
                                    out.write("</div> </a></center>\n                    ");
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
                        out.write("\n                ");
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
            out.write("\n            </p>\n            <p align=\"center\" style=\"margin:-15px\">");
            out.print(I18N.getMsg("dc.mdm.common.OR", new Object[0]));
            out.write("</p>\n        </div>\n\n        <div class=\"androidDivcontent hide\" id=\"agentDownloadInfo\">\n            <p>\n                ");
            out.print(I18N.getMsg("dc.mdm.email.android.download_apk", new Object[0]));
            out.write("\n            </p>\n        </div>\n        <center> \n\t\t\t<div onclick=\"showLoadingStatus(this)\" id=\"serverDownloadButton\" class=\"nav Download-button\"></div>\n\t\t\t<script>\n\t\t\t\tinitDownloadConfig();\n\t\t\t</script>\n\t\t</center>\n\t\t\n       <div class=\"androidDivcontent\" id=\"enrollInfo\">\n            <b>");
            out.print(I18N.getMsg("dc.mdm.enroll.enroll_info", new Object[0]));
            out.write("</b>\n\t\t\t<br />\n\t\t\t<p>");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_enroll_information", new Object[0]));
            out.write("</p>\n\t\t\t<ol>\n\t\t\t\t<li>\n\t\t\t\t\t<p>");
            out.print(I18N.getMsg("dc.common.SERVER_NAME", new Object[0]));
            out.write("&nbsp;:&nbsp;<span onclick=\"SelectAll('server_IP')\" id=\"server_IP\"><b>");
            out.write((String)PageContextImpl.proprietaryEvaluate("${serverIP}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("</b></span></p>");
            out.write("\n\t\t\t\t</li>\n\t\t\t\t<li>\n\t\t\t\t\t<p>");
            out.print(I18N.getMsg("dc.common.SERVER_PORT", new Object[0]));
            out.write("&nbsp;:&nbsp;<span onclick=\"SelectAll('server_port')\" id=\"server_port\"><b>");
            out.write((String)PageContextImpl.proprietaryEvaluate("${serverPort}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("</b></span></p>");
            out.write("\n\t\t\t\t</li>\n\t\t\t\t<li>\n\t\t\t\t\t<p>");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Email_Address", new Object[0]));
            out.write("&nbsp;<b>< ");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Your_Email_Address", new Object[0]));
            out.write(" ></b></p>\n\t\t\t\t</li>\n\t\t\t\t<li>\n\t\t\t\t\t<p>");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_download_authdetails", new Object[0]));
            out.write("</p>\n\t\t\t\t</li>\n\t\t\t</ol>\n\t\t\t<br/>\n\t\t</div>\n\t\t</div>\n\t\t<!-- Not needed for cloud. This page is presently redirected only for on-premise solutions. So, no check added-->\n\t\t<!-- Hidden img element to check if private IP is accessible -->\n\t\t<img class=\"hide\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${httpPingImgUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" onError=\"setHttpAccessible(false);\" onLoad=\"setHttpAccessible(true);\"/>\n\t\t<!-- Not needed for cloud block ends -->\n\t\t<div id =\"CannotDownload\" class=\"hide\">\n\t\t\t<div class=\"displayContentIphone\">\n\t\t\t\t<div class=\"dclogoDivIphone dclogoDivIphoneBlue\" > \n\t\t\t\t\t<img src=\"../../images/tablet/logo.png\"  alt=\"DC Logo\"/>\n\t\t\t\t</div>\n\t\t\t\t<div style=\"font-size:20px;padding:40px 0px;\" class=\"displayTextIphone\" align=\"center\" id=\"displayText\">\n\t\t\t\t\t<div align=\"center\">\n\t\t\t\t\t\t<img src=\"../../images/red-alert.png\" height=\"44\" width=\"48\"/>\n                    </div>\n\t\t\t\t\t<div align=\"center\" style=\"font-size: 25px; color: red;padding: 10px 10px 0px 10px;line-height: 32px;\">\n                        ");
            out.print(I18N.getMsg("dc.mdm.enrollment.cannot.download.memdm.app", new Object[0]));
            out.write("\n                    </div>\n\n\t\t\t\t\t<div style=\"font-size: 17px;color:#666;padding:21px;line-height: 25px;\">\t\n\t\t\t\t\t\t");
            out.print(I18N.getMsg("dc.mdm.actionlog.enrollment.cannot_download_desc", new Object[0]));
            out.write("\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t</div>\n\t\t</div>\n\t\t<div id=\"footerGreen\" class=\"fotterDivFixedIphone fotterDivFixedIphoneBlue\">\n\t\t\t<p>&#169 ");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]));
            out.write("</p>\n\t\t</div>\n    </body>\n</html>");
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
            mdmSelectAPK_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (mdmSelectAPK_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        mdmSelectAPK_jsp._jspx_dependants.put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmSelectAPK_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmSelectAPK_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        mdmSelectAPK_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        mdmSelectAPK_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

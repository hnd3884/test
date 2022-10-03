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

public final class mdmCertificateDownload_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
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
        return mdmCertificateDownload_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmCertificateDownload_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmCertificateDownload_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmCertificateDownload_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = mdmCertificateDownload_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    ");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n\n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css\" />\n    </head>\n    <style type=\"text/css\">\n        .Download-button{\n            font-family: Roboto;\n            font-size: 16px;\n            font-weight: bold;\n            color: #666;\n            background-color: #fff;\n            border: solid 1px #ccc;\n            text-decoration:none;\n            padding:10px 20px;\n            width: 180px;\n            margin: 30px 0px;\n        }\n        a,a:active,a:visited{\n            color: #229d01;\n        }\n    </style>\n    <script  language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/AjaxAPI.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n    <script>\n\n        function downloadCertificate(){\n\n            callGetDownloadStatusRecursively();\n            location.href=\"/installCertificate.mob\";\n        }\n\n        // We are loosing JS Fn calling control, once we moved to other page. As a workaround\n        // same function will be called for every 1 second for 20 seconds using setTimeout method.\n        function callGetDownloadStatusRecursively(){\n\n            var callTimeInSeconds = 1;\n            while(callTimeInSeconds <= 40){\n                setTimeout(\"getMobileConfigDownloadStatus()\", 1000 * callTimeInSeconds);\n                callTimeInSeconds ++;\n            }\n            setTimeout(\"downloadAgent()\", 500 * 20);\n        }\n\n\n        function getMobileConfigDownloadStatus(){\n            var url ='/getCertificateDownloadStatus.mob';  // No I18N\n            var req = AjaxAPI.getXMLHttpRequest();\n            req.open(\"POST\", url, true);\n            req.setRequestHeader(\"Connection\", \"close\");\n            req.onreadystatechange = function(){\n");
            out.write("                checkDownloadStatusAndContinue(req);\n            };\n            req.send(null);\n        }\n\n        function checkDownloadStatusAndContinue(req){\n            mobileConfigDownloadStatusCalled = true;\n            var downloadStatus = req.responseText;\n            if(downloadStatus != null && downloadStatus.trim() == \"true\"){\n                mobileConfigDownloadStatusCalled = false;\n                downloadAgent();\n            }\n        }\n        var isAgentDownload = false;\n        function downloadAgent(){\n            if(!isAgentDownload){\n                var isAndroidAgent = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isAndroidAgent}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"; // No I18N\n                var above4_2 = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${above4_2}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"; // No I18N\n                var isThirdParty = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isThirdPartyEnabled}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n\t\t\t\tvar isForwardingServerEnable = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isEnableForwrdingServer}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n                if(isThirdParty == 'false' && isForwardingServerEnable == 'false'){\n                   location.href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${downloadURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n                }else{\n                    getAgentStatusRecursively();\n                    location.href=\"/DownloadAPK.mob?isAndroidAgent=\"+isAndroidAgent+\"&above4_2=\"+above4_2;\n                }\n                isAgentDownload = true;\n            }\n        }\n\n        function getAgentStatusRecursively(){\n            var callTimeInSeconds = 1;\n            while(callTimeInSeconds <= 20){\n                setTimeout(\"getAgentDownloadStatus()\", 1000 * callTimeInSeconds);\n                callTimeInSeconds ++;\n            }\n            setTimeout(\"redirectSuccess()\", 1000 * callTimeInSeconds);\n        }\n\n        function getAgentDownloadStatus(){\n            var url ='/getAgentDownloadStatus.mob';  // No I18N\n            var req = AjaxAPI.getXMLHttpRequest();\n            req.open(\"POST\", url, true);\n            req.setRequestHeader(\"Connection\", \"close\");\n            req.onreadystatechange = function(){\n                checkAgentStatusAndContinue(req);\n            };\n            req.send(null);\n        }\n\n        function checkAgentStatusAndContinue(req){\n");
            out.write("            var downloadStatus = req.responseText;\n            if(downloadStatus != null && downloadStatus.trim() == \"true\"){\n                redirectSuccess();\n            }\n        }\n\n        function redirectSuccess(){\n            location.href=\"/mdm/enroll?actionToCall=agentDownloadSuccess\";\n        }\n\n        // When ready...\n        window.addEventListener(\"load\",function() {\n            // Set a timeout...\n            setTimeout(function(){\n                // Hide the address bar!\n                window.scrollTo(0, 1);\n            }, 0);\n        });\n\n\n        function initDownloadConfig(){\n            var downloadMode = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${downloadMode}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";// No I18N\n            if(downloadMode == '4'){\n                document.getElementById(\"certDownloadInfo\").className = \"hide\";\n                document.getElementById(\"certDownloadConfig\").className = \"hide\";\n                document.getElementById(\"downloadAppConfig\").className = \"hide\";\n                document.getElementById(\"multiDownloadConfig\").className = \"androidDivcontent\";\n            }else{\n                hideCertificateDownloadInfo();\n            }\n        }\n        function hideCertificateDownloadInfo(){\n            document.getElementById(\"certDownloadInfo\").className = \"hide\";\n            document.getElementById(\"certDownloadConfig\").className = \"hide\";\n            document.getElementById(\"multiDownloadConfig\").className = \"hide\";\n        }\n\n        function showHttpsDownloadSteps(){\n            document.getElementById(\"certDownloadInfo\").className=\"androidDivcontent\";\n            document.getElementById(\"certDownloadConfig\").className=\"\";\n            document.getElementById(\"downloadAppConfig\").className=\"hide\";\n");
            out.write("            document.getElementById(\"enrollInfo\").className=\"hide\";\n        }\n\n        function showAppDownloadSteps(){\n            document.getElementById(\"downloadAppConfig\").className=\"androidDivcontent\";\n            document.getElementById(\"enrollInfo\").className=\"hide\";\n            hideCertificateDownloadInfo()\n        }\n    </script>\n    <body class=\"androidContent\" style=\"height: 100%\">\n        <table width=\"100%\">\n            <tr class=\"androidHeader\">\n                <td>&nbsp;&nbsp;&nbsp;<img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</td>\n            </tr>\n        </table>\n        <div class=\"androidDivcontent\" id=\"downloadAppConfig\">\n            <p> ");
            out.print(I18N.getMsg("dc.mdm.email.android.download_apk", new Object[0]));
            out.write(" </p>\n            <center> <div onclick=\"showHttpsDownloadSteps();\" class=\"nav Download-button\">");
            out.print(I18N.getMsg("dc.mdm.ME_MDM_App", new Object[0]));
            out.write("</div> </center>\n        </div>\n        <div class=\"androidDivcontent\" id=\"multiDownloadConfig\">\n            ");
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
                                    out.write("\" style=\"text-decoration: none;\"> <div onclick=\"showEnrollInfo()\" class=\"nav Download-button\">");
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
                                    out.write("\" style=\"text-decoration: none;\"> <div onclick=\"showEnrollInfo()\" class=\"nav Download-button\">");
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
            out.write("\n            </p>\n            <p align=\"center\" style=\"margin:-15px\">\n                ");
            out.print(I18N.getMsg("dc.mdm.common.OR", new Object[0]));
            out.write("\n            </p>\n            <p id=\"serverDownloadOption\">\n                <center> <div onclick=\"showAppDownloadSteps()\" class=\"nav Download-button\">");
            out.print(I18N.getMsg("dc.mdm.download.agent.from.corporate.network", new Object[0]));
            out.write("</div> </center>\n            </p>\n        </div>\n        <div id=\"certDownloadInfo\" class=\"androidDivcontent\">\n                ");
            out.print(I18N.getMsg("dc.mdm.email.android.download_install_cert", new Object[0]));
            out.write("\n            <ol>\n                <li>\n                    <p>\n                        ");
            out.print(I18N.getMsg("dc.mdm.email.android.save_cert", new Object[0]));
            out.write("\n                    </p>\n                </li>\n                <li>\n                    <p>\n                        ");
            out.print(I18N.getMsg("dc.mdm.email.android.set_passcode", new Object[0]));
            out.write("\n                    </p>\n                </li>\n                <li>\n                    <p>                        \n                       ");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_download_automatic", new Object[0]));
            out.write("\n                    </p>\n                </li>\n            </ol>\n        </div>\n\n        <div class=\"androidDivcontent\" id=\"enrollInfo\">\n            <b>");
            out.print(I18N.getMsg("dc.mdm.enroll.enroll_info", new Object[0]));
            out.write("</b>\n        \t<br />\n        \t<p>");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_enroll_information", new Object[0]));
            out.write("</p>\n        \t<ol>\n        \t    <li>\n        \t\t    <p>");
            out.print(I18N.getMsg("dc.common.SERVER_NAME", new Object[0]));
            out.write("&nbsp;:&nbsp;<span onclick=\"SelectAll('server_IP')\" id=\"server_IP\"><b>");
            out.write((String)PageContextImpl.proprietaryEvaluate("${serverIP}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("</b></span></p>");
            out.write("\n        \t\t</li>\n        \t\t<li>\n        \t\t    <p>");
            out.print(I18N.getMsg("dc.common.SERVER_PORT", new Object[0]));
            out.write("&nbsp;:&nbsp;<span onclick=\"SelectAll('server_port')\" id=\"server_port\"><b>");
            out.write((String)PageContextImpl.proprietaryEvaluate("${serverPort}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("</b></span></p>");
            out.write("\n        \t\t</li>\n        \t\t<li>\n        \t\t    <p>");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Email_Address", new Object[0]));
            out.write("&nbsp;<b>< ");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Your_Email_Address", new Object[0]));
            out.write(" ></b></p>\n        \t\t</li>\n        \t\t<li>\n        \t\t    <p>");
            out.print(I18N.getMsg("dc.mdm.email.android.agent_download_authdetails", new Object[0]));
            out.write("</p>\n        \t\t</li>\n        \t    </ol>\n            <br/>\n        </div>\n\n        <table class=\"\" id=\"certDownloadConfig\" width=\"100%\" style=\"position: fixed; height: 30px; bottom:0; \">\n            <tr  class=\"androidbuttonBg\">\n                <td style=\"padding: 10px\" align=\"right\">\n                        <input type=\"button\" onclick=\"javascript:downloadCertificate();\" value=\"");
            out.print(I18N.getMsg("dc.common.button.CONTINUE", new Object[0]));
            out.write("\" class=\"androidButton\"/>\n                </td>\n            </tr>\n        </table>\n\n        <script>\n            initDownloadConfig();\n        </script>\n    </body>\n</html>\n\n\n");
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
            mdmCertificateDownload_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (mdmCertificateDownload_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        mdmCertificateDownload_jsp._jspx_dependants.put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmCertificateDownload_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmCertificateDownload_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        mdmCertificateDownload_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        mdmCertificateDownload_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

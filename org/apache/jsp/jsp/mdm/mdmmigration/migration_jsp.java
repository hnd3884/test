package org.apache.jsp.jsp.mdm.mdmmigration;

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
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
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
import java.util.Set;
import java.util.Map;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class migration_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return migration_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return migration_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return migration_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = migration_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
    }
    
    public void _jspDestroy() {
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
            final PageContext pageContext = _jspx_page_context = migration_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write(10);
            out.write(32);
            out.write("<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMAjaxAPI.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMSecurity.js\"></script>\n<script>\n        var isSAS = '");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isSAS}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n        var cookieName = '");
            out.write((String)PageContextImpl.proprietaryEvaluate("${cookieName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n        var csrfParamName = '");
            out.write((String)PageContextImpl.proprietaryEvaluate("${csrfParamName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n        CSRFParamName = csrfParamName;\n        //CSRFCookieName = cookieName;\n        CSRFParamValue = getCSRFCookie(cookieName);\n</script>\n");
            out.write("\n \n \n<html>\n<head>\n<meta charset=\"UTF-8\">\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/IncludeJS.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMAjaxAPI.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMSecurity.js\"></script>\n<script>\n\tincludeMainScripts(\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(34);
            out.write(44);
            out.write(34);
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\");// NO I18N\n</script>\n\n<style type=\"text/css\">\n    @font-face {\n        font-family: 'Lato';\n        src: url('../../../themes/styles/font/Lato.woff2') format('woff2');\n    }\n    @font-face {\n       font-family:\"Roboto\";\n       font-weight:400;\n       font-style:normal;\n       src: url(\"https://webfonts.zohowebstatic.com/robotoregular/font.woff\") format(\"woff\");\n    }\n\n\ta {\n\t\tfont-family: 'Lato', 'Roboto', san-serif;\n\t\ttext-decoration: none;\n\t\tcolor: #000\n\t}\n\thtml, body {\n\t\theight: 100%;\n\t}\n\n\tbody {\n\t\tbackground: #fff;\n\t\tmargin: 0;\n\t}\n\t.hide{\n\t\tdisplay:none !important;\n\t}\n\t.tabgroup, .header{\n\t\ttext-align: left;\n\t}\n\t\n\t.header{\n\t\tbackground: #3a9dd6;\n\t\tposition: fixed;\n\t\twidth: 98%;\n\t\theight: 160px;\n\t\tpadding: 20px 30px;\n\t\t-webkit-transition: all 0.4s ease-in-out;\n\t\t-moz-transition: all 0.4s ease-in-out ;\t\t\n\t}\n\t.header{\n\t\theight: 160px;\n\t}\t\n\t.main-title{\n\t\tfont-size: 45px;\n\t\tcolor: #fff;\n\t\tfont-weight: 400;\n\t\tpadding-top: 12px;\n\t\t-webkit-transition: all 0.4s ease-in-out;\n\t\t-moz-transition: all 0.4s ease-in-out ;\t\n");
            out.write("\t}\n\t.main-title img{\n\t\twidth: 54px;\n\t\tvertical-align: text-bottom;\n\t\tpadding-right: 30px;\n\t\tfloat: right;\n\t\tpadding-top: 40px;\n\t}\n\t.main-title{\n\t\tfont-size: 60px;\n\t\tcolor: #fff;\n\t\tfont-weight: 400;\n\t\tpadding-top: 40px;\n\t\ttext-align: center;\n\t}\n\t.li-img {\n\t\tvertical-align: middle;\n\t\twidth: 130px;\n\t\tpadding-right: 1em;\n\t\tpadding-bottom:1.3em;\n\t\tpadding-top: 1.3em;\n\t\ttext-align: center;\n\t\tmargin: auto;\n\t}\n\t.li-img img {\n\t\tdisplay: block;\n\t\twidth: 100%;\n\t\theight: auto;\n\t\tborder-radius: 30px;\n\t}\n\t#content {\n\t\tbackground: #fff;\n\t\tmin-height: 100%;\n\t\tz-index: 20;\n\t\t-webkit-backface-visibility: hidden;\n\t\t-webkit-perspective: 1000;\n\t\ttext-align: center;\n\t\tfont-family: 'Lato', 'Roboto', sans-serif;\n\t\tfont-size: 24px;\n\t\tfont-weight: 200;\n\t\tcolor: #555;\n\t\tbox-sizing: border-box;\n\t}\n\t#errormsg {\n\t\tpadding-right: 17% !important; \n\t\tpadding-left: 17% !important; \n\t}\n\t#errormsg.errorMessage {\n\t\ttext-align:center;\n\t\tpadding-top:0px;\n\t\tpadding-right: 5% !important; \n\t\tpadding-left: 5% !important; \n\t\tcolor: #FF0000;\n\t}\n</style>\n");
            final String deviceID = request.getParameter("deviceID");
            final String configID = request.getParameter("configID");
            final String requesturi = request.getRequestURI();
            out.write("\n<script>\nfunction startMigration(){\n\tdocument.getElementById(\"tab1\").className = \"hide\"; // NO I18N\n\tdocument.getElementById(\"tab2\").className = \"\";// NO I18N\n\tvar url=document.location.href;\n\t//var url=document.getElementById(\"requesturi\").value;\n\t//url = url.substring(0,url.indexOf('?'));\n\tvar deviceID = document.getElementById(\"deviceID\").value;// NO I18N\n\tvar configID = document.getElementById(\"configID\").value;// NO I18N\n\tvar xhttp = new DMAjaxAPI.getXMLHttpRequest();\n\txhttp.onreadystatechange = function() {\n\t\tif (this.readyState == 4 && this.status == 200) {\n\t\t\tgetCurrentState(this.responseText);\n\t\t}\n\t};\n    xhttp.open(\"POST\", url, true);// NO I18N\n    xhttp.setRequestHeader(\"Content-type\", \"application/json\");// NO I18N\n\tvar data = JSON.stringify({\"msgType\":\"UnmanageDevice\",\"msgContent\":{\"deviceID\":String(deviceID),\"CONFIG_ID\":String(configID)}});//NO I18N\n\txhttp.send(data);\n}\n\nfunction getCurrentState(responseText){\n\tvar responseTxt = JSON.parse(responseText);\n\tif(responseTxt.Error ==null){\n\t\tdocument.getElementById(\"enrollUrl\").value = responseTxt.NewEnrollmentURL;// NO I18N// NO I18N\n");
            out.write("\t\tdocument.getElementById(\"deviceUDID\").value = responseTxt.udid; // No I18N\n\t\tpollStatus(responseText);\n\t} else{\n\t\tdocument.getElementById(\"errormsg\").innerHTML=responseTxt.ErrorMsg;// NO I18N\n\t\tdocument.getElementById(\"errormsg\").className=\"errorMessage\";// NO I18N\n\t}\n}\nfunction getDeviceStatusBeforeMigration(){\n\tvar xhttp = new DMAjaxAPI.getXMLHttpRequest();\n\tvar url=document.location.href;\n\t//var url=document.getElementById(\"requesturi\").value;\n\t//url = url.substring(0,url.indexOf('?'));\n\tvar deviceID = document.getElementById(\"deviceID\").value;// NO I18N\n\tvar configID = document.getElementById(\"configID\").value;// NO I18N\n\txhttp.onreadystatechange = function() {\n\t\tif (this.readyState == 4 && this.status == 200) {\n\t\t\tvar statusresponseTxt = JSON.parse(this.responseText);\n\t\t\tif(statusresponseTxt.Error !=null){\n\t\t\t\tdocument.getElementById(\"errormsg\").innerHTML=statusresponseTxt.ErrorMsg;// NO I18N\n\t\t\t\tdocument.getElementById(\"errormsg\").className=\"errorMessage\";// NO I18N\n\t\t\t} else if(statusresponseTxt.Status!=null && statusresponseTxt.Status!='' && statusresponseTxt.Status=='Unmanaged'){// NO I18N\n");
            out.write("\t\t\t\tdocument.getElementById(\"enrollUrl\").value = statusresponseTxt.NewEnrollmentURL;// NO I18N\n\t\t\t\tdocument.getElementById(\"deviceUDID\").value = statusresponseTxt.udid; // No I18N\n\t\t\t\tpollStatus();\n\t\t\t} else if(statusresponseTxt.Status!=null && statusresponseTxt.Status!='' && statusresponseTxt.Status=='Managed'){// NO I18N\n\t\t\t\tdocument.getElementById(\"enrollUrl\").value = statusresponseTxt.NewEnrollmentURL;// NO I18N\n\t\t\t\tdocument.getElementById(\"deviceUDID\").value = statusresponseTxt.udid; // No I18N\n\t\t\t\tstartMigration();\n\t\t\t}\n\t\t}\n\t};\n\n\txhttp.open(\"POST\", url, true);// NO I18N\n\txhttp.setRequestHeader(\"Content-type\", \"application/json\");// NO I18N\n\tvar data = JSON.stringify({\"msgType\":\"QueryManagementStatus\",\"msgContent\":{\"deviceID\":String(deviceID),\"CONFIG_ID\":String(configID)}});//NO I18N\n\txhttp.send(data);\n}\nfunction pollStatus(){\n\tvar deviceUnmanaged = document.getElementById(\"deviceUnmanaged\").value;\n\tif(!(deviceUnmanaged == \"true\")){\n\tvar xhttp = new DMAjaxAPI.getXMLHttpRequest();\n\tvar url=document.location.href;\n");
            out.write("\t//var url=document.getElementById(\"requesturi\").value;\n\t//url = url.substring(0,url.indexOf('?'));\n\tvar deviceID = document.getElementById(\"deviceID\").value;// NO I18N\n\tvar configID = document.getElementById(\"configID\").value;// NO I18N\n\txhttp.onreadystatechange = function() {\n\t\tif (this.readyState == 4 && this.status == 200) {\n\t\t\tvar statusresponseTxt = JSON.parse(this.responseText);\n\t\t\tif(statusresponseTxt.Error !=null){\n\t\t\t\tdocument.getElementById(\"errormsg\").innerHTML=statusresponseTxt.ErrorMsg;// NO I18N\n\t\t\t\tdocument.getElementById(\"errormsg\").className=\"errorMessage\";// NO I18N\n\t\t\t} else if(statusresponseTxt.Status!=null && statusresponseTxt.Status!='' && statusresponseTxt.Status=='Unmanaged'){// NO I18N\n\t\t\t    document.getElementById(\"deviceUnmanaged\").value = \"true\";\n\t\t\t\tredirectOnSuccess();\n\t\t\t}\n\t\t}\n\t};\n\txhttp.open(\"POST\", url, true);// NO I18N\n\txhttp.setRequestHeader(\"Content-type\", \"application/json\");// NO I18N\n\tvar data = JSON.stringify({\"msgType\":\"QueryManagementStatus\",\"msgContent\":{\"deviceID\":String(deviceID),\"CONFIG_ID\":String(configID)}});//NO I18N\n");
            out.write("\txhttp.send(data);\n\tsetTimeout(function() {\n\t\tvar pollTempStatus = Number(document.getElementById(\"pollStatus\").value);\n\t\tif(pollTempStatus < 10){\n\t\t\tpollStatus();\n\t\t\tpollTempStatus=pollTempStatus + 1;\n\t\t\tdocument.getElementById(\"pollStatus\").value = pollTempStatus;\n\t\t} }, 30000);\n\t}\n}\n\n\nfunction redirectOnSuccess(){\n\tdocument.getElementById(\"tab1\").className = \"hide\"; // NO I18N\n\tdocument.getElementById(\"tab2\").className = \"hide\";// NO I18N\n\tdocument.getElementById(\"tab3\").className = \"\";// NO I18N\n\tvar enrollUrl = document.getElementById(\"enrollUrl\").value;// NO I18N\n\twindow.location.href = enrollUrl;\n\tpollMEStatus();\n}\n\nfunction pollMEStatus(){\n\n\tvar deviceManaged = document.getElementById(\"deviceManaged\").value;\n\tif(!(deviceManaged == \"true\")){\n\tvar xhttp = new DMAjaxAPI.getXMLHttpRequest();\n\tvar url=document.location.href;\n\t//var url=document.getElementById(\"requesturi\").value;\n\t//url = url.substring(0,url.indexOf('?'));\n\tvar deviceID = document.getElementById(\"deviceID\").value;// NO I18N\n\tvar configID = document.getElementById(\"configID\").value;// NO I18N\n");
            out.write("\tvar deviceUDID = document.getElementById(\"deviceUDID\").value;// NO I18N\n\txhttp.onreadystatechange = function() {\n\t\tif (this.readyState == 4 && this.status == 200) {\n\t\t\tvar statusresponseTxt = JSON.parse(this.responseText);\n\t\t\tif(statusresponseTxt.Error !=null){\n\t\t\t\tdocument.getElementById(\"errormsg\").innerHTML=statusresponseTxt.ErrorMsg;// NO I18N\n\t\t\t\tdocument.getElementById(\"errormsg\").className=\"errorMessage\";// NO I18N\n\t\t\t} else if(statusresponseTxt.Status!=null && statusresponseTxt.Status!='' && statusresponseTxt.Status=='Managed'){// NO I18N\n\t\t\t    document.getElementById(\"deviceManaged\").value = \"true\";\n\t\t\t\tcompleteOnSuccess();\n\t\t\t}\n\t\t}\n\t};\n\n\txhttp.open(\"POST\", url, true);// NO I18N\n\txhttp.setRequestHeader(\"Content-type\", \"application/json\");// NO I18N\n\tvar data = JSON.stringify({\"msgType\":\"EnrolledStatus\",\"msgContent\":{\"deviceID\":String(deviceID),\"CONFIG_ID\":String(configID),\"deviceUDID\":String(deviceUDID)}});//NO I18N\n\txhttp.send(data);\n\tsetTimeout(function() {\n\t\tvar pollTempStatus = Number(document.getElementById(\"pollMEStatus\").value);\n");
            out.write("\t\tif(pollTempStatus < 10){\n\t\t\tpollMEStatus();\n\t\t\tpollTempStatus= pollTempStatus + 1;\n\t\t\tdocument.getElementById(\"pollMEStatus\").value = pollTempStatus;\n\t\t}\n\t\t }, 15000);\n\t}\n}\n\nfunction completeOnSuccess(){\n\tdocument.getElementById(\"tab1\").className = \"hide\"; // NO I18N\n\tdocument.getElementById(\"tab2\").className = \"hide\"; // NO I18N\n\tdocument.getElementById(\"tab3\").className = \"hide\";// NO I18N\n\tdocument.getElementById(\"tab4\").className = \"\";// NO I18N\n}\n\nfunction closeWindow(){\n\twindow.open('about:blank', '_self').close();\n}\n\n</script>\n\n</head>\n\n<body>\n\n\n\n\n<!--Pattern HTML-->\n<input type=\"hidden\" name=\"deviceID\" id=\"deviceID\" value=\"");
            out.print(DMIAMEncoder.encodeHTMLAttribute(deviceID));
            out.write("\" />\n<input type=\"hidden\" name=\"configID\" id=\"configID\" value=\"");
            out.print(DMIAMEncoder.encodeHTMLAttribute(configID));
            out.write("\" />\n<input type=\"hidden\" name=\"enrollUrl\" id=\"enrollUrl\" />\n<input type=\"hidden\" name=\"deviceUnmanaged\" id=\"deviceUnmanaged\" value=\"false\"/>\n<input type=\"hidden\" name=\"deviceManaged\" id=\"deviceManaged\" value=\"false\"/>\n<input type=\"hidden\" name=\"deviceUDID\" id=\"deviceUDID\"/>\n<input type=\"hidden\" name=\"pollMEStatus\" id=\"pollMEStatus\" value=0></input>\n<input type=\"hidden\" name=\"pollStatus\" id=\"pollStatus\" value=0></input>\n<input type=\"hidden\" name=\"requesturi\" id=\"requesturi\" value=\"");
            out.print(DMIAMEncoder.encodeHTMLAttribute(requesturi));
            out.write("\" />\n <div id=\"content\">\n <header>");
            out.write("\n \t\n  <div class=\"header\">\n  \t\n  \t<div class=\"main-title\">");
            out.write((String)PageContextImpl.proprietaryEvaluate("${title}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("</div>");
            out.write("\n  \t\n  </div>\n \n  </header>");
            out.write("\n  \n  <section id=\"first-tab-group\"  style=\"padding-top: 230px;\" class=\"tabgroup\">");
            out.write("\n   <div id=\"tab1\">\n  <div id=\"pattern\" class=\"pattern\">\n \n  \t<div class=\"li-img\">\n\t\t\t\t\t\t<img src=\"../../jsp/mdm/mdmmigration/migrate.png\" alt=\"Migration\">");
            out.write("\n\t\t\t\t\t</div>\n\t\t\t\t\t<div class=\"\" style=\"\n    padding: 10px 50px 30px 50px;\n    line-height: 60px;\n    text-align: center;font-size: 45px;\n\"><b>Your administrator is migrating device management to a new service.<br>This process will complete in a few seconds.</b></div>");
            out.write("\n\n<div class=\"\" style=\"padding: 5px 50px 50px 80px; line-height: 60px; text-align: left;font-size: 40px;list-style-position: outside;\">\n<ul>\n\t<li>Make sure you have uninterrupted network connectivity</li>");
            out.write("\n\t<li>Do not exit the page until the process is complete</li>");
            out.write("\n</ul>\n</div>\n<div class=\"\" style=\"margin: auto; text-align: center;\" ><input style=\" background-color: #3499d1; padding: 25px 60px; text-align: center;font-size: 40px;margin: auto; color: #fff; border:0px; -webkit-appearance: none\" type=\"button\" onclick=\"getDeviceStatusBeforeMigration();\" value=\"Begin\"/></div>");
            out.write("\n\t</div>\n\t  </div>\n\t  \n\t  <div id=\"tab2\" class=\"hide\">\n  <div id=\"pattern\" class=\"pattern\">\n \n  \t<div class=\"li-img\">");
            out.write("\n\t\t\t\t\t\t<img src=\"../../jsp/mdm/mdmmigration/migrate.png\" alt=\"Migration\">\n\t\t\t\t\t</div>");
            out.write("\n\t\t\t\t\t<div class=\"\" style=\"\n    padding: 10px 50px 10px 50px;\n    line-height: 40px;\n    text-align: left;font-size: 25px;color: #2775a9;\n\">");
            out.write("<img src=\"../../jsp/mdm/mdmmigration/loader-1.gif\" style=\"width: 30px;\n    vertical-align: sub;\n    padding: 0px 20px;\">Unenrolling from old device management service</div>");
            out.write("\n    <div class=\"\" style=\"\n    padding: 10px 50px 50px 120px;\n    line-height: 40px;\n    text-align: left;font-size: 25px;\n\">Installing new device management profile</div>");
            out.write("\n\n\t</div>");
            out.write("\n\t  </div>\n\t  \n\t  <div id=\"tab3\" class=\"hide\">\n  <div id=\"pattern\" class=\"pattern\">\n \n  \t<div class=\"li-img\">\n\t\t<img src=\"/../../jsp/mdm/mdmmigration/migrate.png\" alt=\"Migration\">\n\t\t\t\t\t</div>\n\t\t\t\t\t<div class=\"\" style=\"padding: 10px 50px 10px 50px; line-height: 40px; text-align: left;font-size: 25px;color: #2da731; \"><img src=\"../../jsp/mdm/mdmmigration/success.png\" style=\"width: 30px; vertical-align: sub; padding: 0px 20px;\">Removing old device administration</div>");
            out.write("\n    <div class=\"\" style=\"padding: 10px 50px 50px 50px;line-height: 40px;text-align: left;font-size: 25px;color: #2775a9;\">\n    \t<img src=\"../../jsp/mdm/mdmmigration/loader-1.gif\" style=\"width: 30px;vertical-align: sub;padding: 0px 20px;\" />Enrolling ManageEngine MDM</div>");
            out.write("\n\t</div>\n\t  </div>\n\t  <div class=\"hide\" id=\"errormsg\"></div>\n\t  <div id=\"tab4\" class=\"hide\">\n\t\t<div id=\"pattern\" class=\"pattern\">\n\n\t\t\t<div class=\"li-img\">\n\t\t\t  <img src=\"/../../jsp/mdm/mdmmigration/migrate.png\" alt=\"Migration\">\n\t\t\t\t\t\t  </div>\n\t\t\t\t\t\t  <div class=\"\" style=\"padding: 10px 50px 10px 50px; line-height: 40px; text-align: left;font-size: 25px;color: #2da731; \"><img src=\"../../jsp/mdm/mdmmigration/success.png\" style=\"width: 30px; vertical-align: sub; padding: 0px 20px;\">Removing old device administration</div>");
            out.write("\n\t\t  <div class=\"\" style=\"padding: 10px 50px 50px 50px;line-height: 40px;text-align: left;font-size: 25px;color: #2da731;\">\n\t\t\t  <img src=\"../../jsp/mdm/mdmmigration/success.png\" style=\"width: 30px;vertical-align: sub;padding: 0px 20px;\" />Enrolled</div>");
            out.write("\n\t\t  </div>\n\t\t  <div class=\"\" style=\"margin: auto; text-align: center;\" ><input style=\"background-color: #3499d1;padding: 10px 20px;text-align: center;font-size: 28px;margin: auto;color: #fff;border:0px;border-radius: 10px;-webkit-appearance: none;\" type=\"button\" onclick=\"closeWindow();\" value=\"Close\"/></div>");
            out.write("\n\t\t\t</div>\n\t\t\t<div class=\"hide\" id=\"errormsg\"></div>\n\t\t  </div>\n\t</div>\n\n</body>\n</html>\n");
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
            migration_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (migration_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jspf/csrfIncludes.jspf", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        migration_jsp._jspx_imports_packages.add("javax.servlet.http");
        migration_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        migration_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

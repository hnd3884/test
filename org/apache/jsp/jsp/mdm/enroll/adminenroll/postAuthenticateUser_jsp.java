package org.apache.jsp.jsp.mdm.enroll.adminenroll;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
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

public final class postAuthenticateUser_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
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
        return postAuthenticateUser_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return postAuthenticateUser_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return postAuthenticateUser_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = postAuthenticateUser_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = postAuthenticateUser_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    \n    \n    \n    \n\t");
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write("\n    <head>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <title>");
            out.print(I18N.getMsg("dc.mdm.enroll.profile_installation", new Object[0]));
            out.write("</title>\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n        <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0,user-scalable=no;\"/>\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n        <!-- The previous ulr of css will be cached in the device.So to load the changed css the url path is changed by appending 1 as the parameter -->\n\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css?2\" />\n    </head>\n    <script src=\"/js/mdm/mdmDeviceEnrollmentPage.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n    <script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMSecurity.js\"></script>\n    <script language=\"Javascript\" type=\"text/javascript\">\n        csrfParamName='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${csrfParamName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//NO I18N\n        cookieName='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${cookieName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//NO I18N\n        isSAS='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isSAS}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//NO I18N\n        var responseJSON = JSON.parse('");
            out.write((String)PageContextImpl.proprietaryEvaluate("${responseJSON}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("'); //NO I18N\n\t\tvar device_username = responseJSON.UserName;\n        var device_useremail = responseJSON.EmailAddress;\n        var device_customerid = responseJSON.CustomerID;\n        var device_erid = responseJSON.EnrollmentReqID;\n\t\tvar serverName = responseJSON.serverName;\n        var portNumber = responseJSON.portNumber;\n\t\tvar device_ownedby = responseJSON.device_ownedby;\n\t\tvar IsOnPremise = responseJSON.IsOnPremise;\n\t\tvar platform = responseJSON.platform;\n\t\tif(platform==1) {\n\t\t    var forwardUrl = responseJSON.url;\n\t\t    if(isIpad()) {\n\t\t        forwardUrl+=\"true\";\n\t\t    }else{\n\t\t        forwardUrl+=\"false\";\n\t\t    }\n\t\t    if(responseJSON.httpMethod == \"POST\"){\n                postDownload(forwardUrl);\n\t\t    }else{\n\t\t        location.href = forwardUrl;\n            }\n\t\t} else {\n\t\t    var jsonForNative ={\"device_erid\":device_erid,\"device_username\":device_username,\"device_useremail\":device_useremail,\"device_customerid\":device_customerid,\"serverName\":serverName,\"portNumber\":portNumber,\"device_ownedby\":device_ownedby,\"IsOnPremise\":IsOnPremise};   // NO I18N\n");
            out.write("            var services=responseJSON.Services;\n            if(services!=null){\n                services.TokenValue=decodeURIComponent(services.TokenValue);\n                jsonForNative.Services=services;\n            }\n            Android.onAuthSuccess(JSON.stringify(jsonForNative));\n        }\n\n        function postDownload(url)\n        {\n        \tif(!document.getElementById('postDownloadForm')){\n        \t\tvar form=document.createElement('form');\n                form.method='POST';//No I18N\n                form.action=url;\n                form.setAttribute(\"id\", \"postDownloadForm\");\n                form=addCSRF(form);\n                document.getElementsByTagName(\"HEAD\")[0].appendChild(form);\n        \t}\n        \telse\n        \t{\n        \t\tdocument.getElementById('postDownloadForm').action=url;\n        \t}\n            document.getElementById('postDownloadForm').submit();\n        }\n\t</script>\n    <body style=\"background-color: rgb(245, 244, 240); \">\n   <script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/IncludeJS.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n   <script>includeMainScripts(\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(34);
            out.write(44);
            out.write(34);
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\");</script>\n\t <div ");
            if (this._jspx_meth_c_005fchoose_005f0(_jspx_page_context)) {
                return;
            }
            out.write(62);
            out.write("\n      <table width=\"100%\" style=\"border-spacing: 0px;\">\n            <tr class=\"blueHeader\">\n                <td colspan=\"4\"><div><img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</div></td>\n            </tr>\n            <tr><td colspan=\"3\">&nbsp;</td></tr><tr><td colspan=\"3\">&nbsp;</td></tr><tr><td colspan=\"3\">&nbsp;</td></tr><tr><td colspan=\"3\">&nbsp;</td></tr>\n            <tr><td colspan=\"3\">&nbsp;</td></tr><tr><td colspan=\"3\">&nbsp;</td></tr>\n            <tr><td></td><td style=\"padding-left:30%;\"><img src=\"/images/loading.gif\" align=\"absmiddle\"/> ");
            out.print(I18N.getMsg("mdm.enroll.assign_in_progess_remarks", new Object[0]));
            out.write("...</td><td></td></tr>\n      </table>\n\t  </div>\n      </body>\n</html>\n");
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
            postAuthenticateUser_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (postAuthenticateUser_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        postAuthenticateUser_jsp._jspx_imports_packages.add("javax.servlet.http");
        postAuthenticateUser_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        postAuthenticateUser_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        postAuthenticateUser_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

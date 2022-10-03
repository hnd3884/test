package org.apache.jsp.jsp.mdm.enroll.adminenroll;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.taglibs.standard.tag.rt.core.OutTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import javax.servlet.jsp.tagext.Tag;
import com.me.devicemanagement.framework.webclient.taglib.DCProductTag;
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

public final class downloadAgent_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return downloadAgent_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return downloadAgent_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return downloadAgent_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = downloadAgent_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.release();
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
            final PageContext pageContext = _jspx_page_context = downloadAgent_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("     \n    \n\t");
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(9);
            out.write("\n\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge,chrome=1\">\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n\n");
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<script>\n    CONTEXT_PATH=\"");
            out.print(DMIAMEncoder.encodeJavaScript(contextpath));
            out.write("\";\n</script>\n<script src=\"/components/javascript/Localize.js\" type=\"text/javascript\"></script>\n<script>\n\nvar JSRB = function()\n{\n}\n\nJSRB.val = function(key, valobj)\n{\n    var value = I18N.getMsg(key,new Array(valobj));\n    return value;  \n}\n\n</script>\n\n");
            out.write("\n    <html>\n        <head>\n            <title>\n                ");
            out.print(I18N.getMsg("dc.mdm.enroll.admin_enrollment.agent_download.title", new Object[0]));
            out.write("\n            </title>\n            <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n            <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;\"/>\n            <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n            <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n            <meta name=\"HandheldFriendly\" content=\"true\"/>\n            <meta name=\"MobileOptimized\" content=\"width\"/>\n            <script language=\"Javascript\" src=\"/js/mdm/agentDownload.js\" type=\"text/javascript\"></script>\n\t\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/../..");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmcssUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/themes/styles/tablet/profileinstallation.css\" />\n\t\t\t<script>\n\t\t\t\tdirectDownloadUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${adminAppDownloadURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\t\t\t\thttpsDownloadUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${adminAppHttpsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\t\t\t\tdownloadButtonId = \"downloadFromServer\";\n\t\t\t\texternalDownloadUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${adminAppExternalLink}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\t\t\t\tfilename = \"MDMAdminApp.apk\";  \n\t\t\t\tmanageEnginePingUrl = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${manageenginePingImgUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\";\n\t\t\t\tfunction enableDownloadButton(onClickFunction) {\n\t\t\t\t\tvar downloadButton = document.getElementById(downloadButtonId)\n\t\t\t\t\tdownloadButton.setAttribute(\"onClick\",onClickFunction);\n\t\t\t\t\tif(waitingForUrl == true) {\n\t\t\t\t\t\tdownloadButton.click();\n\t\t\t\t\t\twaitingForUrl = false;\n\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t\t\n\t\t\t\tfunction downloadAgentFromStore() {\n\t\t\t\t\twindow.location = \"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${adminAppPlayStoreURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"; //No I18N\n\t\t\t\t}\t\t\t\t\n            </script>\n            <style>\n\t\t\t\t.hide{display:none;}\n                input{\n                        background-color: #F2F2F2;\n                        font:12px 'Lato', 'Roboto', sans-serif;\n                        border: 0px;\n                        padding: 5px;\n                        padding-left:0px;\n                }\n                body{\n                    margin:0px;\n                }\n                .header{\n                    background-color:#3a9dd6;\n                    padding:10px 10px 10px 30px;\n                    height:70px;\n                    color:white;\n                    font:20px 'Lato', 'Roboto', sans-serif\n                }\n                .body,.footer {\n                    background-color:#f2f2f2;\n                }\n                .body {\n                    vertical-align:top;\n                    padding:20px 10px;\n                    font:12px 'Lato', 'Roboto', sans-serif\n                }\n                .downloadButton{\n\t\t\t\t\twidth:150px;\n");
            out.write("                    background-color: #3a9dd6;\n                    padding: 10px 25px;\n                    color: white;\n                    margin:10px 0px;\n\t\t\t\t\ttext-align:center;\n                }\n                .body table tr td.stepNo{\n                    white-space: nowrap;\n                    padding:10px 2px 2px 20px;\n                    text-align:right;\n                    vertical-align:top;\n                }\n                .body table tr td.stepContent{\n                    padding:10px 2px;\n                    text-align:left;\n                    vertical-align:top;\n                }\n                .body table tr td.stepContent table tr td table.serverInfo tr td{\n                    padding:0px 5px 5px 5px;\n                    white-space: nowrap;\n                }\n                .body table tr td.stepContent table tr td.secondRow{\n                    padding:20px 0px 5px 0px;\n                }\n                .footer{\n                    height:60px;\n                    text-align:center;\n                    padding:30px;\n");
            out.write("                }\n                ol{\n                    padding-left:0px;\n                }\n                ol li {\n                    list-style-type: none;\n                }\n            </style>\n\t\t\t</head>\n        <body>\n            <table id=\"downloadAppContent\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" height=\"100%\">\n                <tr>\n                    <td class=\"header\">\n                        ");
            out.print(I18N.getMsg("dc.mdm.enroll.admin_enrollment.step-1.title", new Object[0]));
            out.write("\n                    </td>\n                </tr>\n                <tr>\n                    <td class=\"body\">\n                        <table cellspacing=\"0\" cellpadding=\"0\">\n                            <tr>\n                                <td class=\"stepNo\">");
            out.print(I18N.getMsg("dc.admin.mssql.Step_1", new Object[0]));
            out.write(" : </td>\n                                <td class=\"stepContent\">\n                                    <table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" >\n                                        <tr><td>\n                                                ");
            out.print(I18N.getMsg("dc.mdm.enroll.admin_enrollment.agent_download.step-1", new Object[0]));
            out.write("\n                                            </td>\n                                        </tr>\n                                        <tr><td class=\"secondRow\" style=\"padding-left:0px;padding-bottom:10px;padding-top:10px;\">\n                                                <div class=\"downloadButton\" onclick=\"downloadAgentFromStore()\">\n                                                    ");
            out.print(I18N.getMsg("dc.mdm.android.common.aent_download_mode.from_playstore", new Object[0]));
            out.write("\n                                                </div>\n                                            </td></tr>\n                                    </table>\n                                </td>\n                            </tr>\n\t\t\t\t\t\t\t");
            final DCProductTag _jspx_th_fw_005fproductTag_005f0 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
            boolean _jspx_th_fw_005fproductTag_005f0_reused = false;
            try {
                _jspx_th_fw_005fproductTag_005f0.setPageContext(_jspx_page_context);
                _jspx_th_fw_005fproductTag_005f0.setParent((Tag)null);
                _jspx_th_fw_005fproductTag_005f0.setProductCode("MDMODEE,MDMODMSP");
                _jspx_th_fw_005fproductTag_005f0.setShow(Boolean.valueOf("true"));
                final int _jspx_eval_fw_005fproductTag_005f0 = _jspx_th_fw_005fproductTag_005f0.doStartTag();
                if (_jspx_eval_fw_005fproductTag_005f0 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n\t\t\t\t\t\t\t<script>\n\t\t\t\t\t\t\t\tisSas = true;\n                                setHttpAccessible(false);\n\t\t\t\t\t\t\t</script>\n                            <tr>\n                                <td class=\"stepNo\">");
                        out.print(I18N.getMsg("dc.admin.mssql.Step_2", new Object[0]));
                        out.write(" : </td>\n                                <td class=\"stepContent\">\n                                    ");
                        out.print(I18N.getMsg("dc.mdm.enroll.admin_enrollment.agent_download.step-3", new Object[0]));
                        out.write("\n                                </td>\n                            </tr>\n                            ");
                        evalDoAfterBody = _jspx_th_fw_005fproductTag_005f0.doAfterBody();
                    } while (evalDoAfterBody == 2);
                }
                if (_jspx_th_fw_005fproductTag_005f0.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.reuse((Tag)_jspx_th_fw_005fproductTag_005f0);
                _jspx_th_fw_005fproductTag_005f0_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fproductTag_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fproductTag_005f0_reused);
            }
            out.write("\n                            ");
            final DCProductTag _jspx_th_fw_005fproductTag_005f2 = (DCProductTag)this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.get((Class)DCProductTag.class);
            boolean _jspx_th_fw_005fproductTag_005f1_reused = false;
            try {
                _jspx_th_fw_005fproductTag_005f2.setPageContext(_jspx_page_context);
                _jspx_th_fw_005fproductTag_005f2.setParent((Tag)null);
                _jspx_th_fw_005fproductTag_005f2.setProductCode("MDMODEE,MDMODMSP");
                _jspx_th_fw_005fproductTag_005f2.setShow(Boolean.valueOf("false"));
                final int _jspx_eval_fw_005fproductTag_005f2 = _jspx_th_fw_005fproductTag_005f2.doStartTag();
                if (_jspx_eval_fw_005fproductTag_005f2 != 0) {
                    int evalDoAfterBody2;
                    do {
                        out.write("\n                            <tr>\n                                <td class=\"stepNo\">\n\t\t\t\t\t\t\t\t\t<!-- Hidden img element to check if private IP is accessible -->\n\t\t\t\t\t\t\t\t\t<img class=\"hide\" src=\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${httpPingImgUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("\" onError=\"setHttpAccessible(false);\" onLoad=\"setHttpAccessible(true);\"/>\n\t\t\t\t\t\t\t\t\t");
                        out.print(I18N.getMsg("dc.admin.mssql.Step_2", new Object[0]));
                        out.write(" : \n\t\t\t\t\t\t\t\t</td>\n                                <td class=\"stepContent\" style=\"padding-bottom:0px;\">\n                                    <table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" >\n                                        <tr><td>\n                                                ");
                        out.print(I18N.getMsg("dc.mdm.enroll.admin_enrollment.agent_download.step-2", new Object[0]));
                        out.write("\n                                            </td>\n                                        </tr>\n                                        <tr><td class=\"secondRow\" style=\"padding-left:0px;padding-bottom:0px;padding-top:10px;\">\n\n                                                <ol>\n                                                    <li style=\"padding: 10px 0px;\">\n                                                            <b>");
                        out.print(I18N.getMsg("dc.common.SERVER_NAME", new Object[0]));
                        out.write("</b> : \n                                                            <div style=\"font-size: inherit;display:inline-block;\" id=\"serverName\" type=\"text\" onclick=\"SelectAll('serverName')\">");
                        if (this._jspx_meth_c_005fout_005f0((JspTag)_jspx_th_fw_005fproductTag_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("</div>\n                                                    </li>\n                                                    <li style=\"padding: 10px 0px;\">\n                                                            <b>");
                        out.print(I18N.getMsg("dc.common.SERVER_PORT", new Object[0]));
                        out.write("</b> : \n                                                            <div style=\"font-size: inherit;display:inline-block;\" id=\"serverPort\" type=\"text\" onclick=\"SelectAll('serverPort')\" >");
                        if (this._jspx_meth_c_005fout_005f1((JspTag)_jspx_th_fw_005fproductTag_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("</div>\n                                                    </li>\n\n                                                </ol>\n                                            </td></tr>\n                                    </table>\n                                </td>\n                            </tr>\n                            <tr>\n                                <td class=\"stepNo\">");
                        out.print(I18N.getMsg("dc.admin.mssql.Step_3", new Object[0]));
                        out.write(" : </td>\n                                <td class=\"stepContent\">\n                                    ");
                        out.print(I18N.getMsg("dc.mdm.enroll.admin_enrollment.agent_download.step-3", new Object[0]));
                        out.write("\n                                </td>\n                            </tr>\n\t\t\t\t\t\t\t");
                        evalDoAfterBody2 = _jspx_th_fw_005fproductTag_005f2.doAfterBody();
                    } while (evalDoAfterBody2 == 2);
                }
                if (_jspx_th_fw_005fproductTag_005f2.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005ffw_005fproductTag_0026_005fshow_005fproductCode.reuse((Tag)_jspx_th_fw_005fproductTag_005f2);
                _jspx_th_fw_005fproductTag_005f1_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fproductTag_005f2, this._jsp_getInstanceManager(), _jspx_th_fw_005fproductTag_005f1_reused);
            }
            out.write("\n                        </table>\n                    </td>\n                </tr>\n            </table>\n\t\t\t<div id =\"CannotDownload\" class=\"hide\">\n\t\t\t\t<div class=\"displayContentIphone\">\n\t\t\t    <table width=\"100%\">\n                 <tr class=\"blueHeader\">\n                   <td><div><img src=\"/images/mdmp_server.png\" height=\"20\" width=\"20\" align=\"top\"/>&nbsp;&nbsp;&nbsp;");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_MDM", new Object[0]));
            out.write("</div></td>\n               </tr>\n               </table> \n\t\t\t\t\t<div style=\"font-size:20px;padding:40px 0px;\" class=\"displayTextIphone\" align=\"center\" id=\"displayText\">\n\t\t\t\t\t\t<div align=\"center\">\n\n\t\t\t\t\t\t\t<img src=\"../../images/red-alert.png\" height=\"44\" width=\"48\"/>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<div align=\"center\" style=\"font-size: 25px;color: red;padding: 10px 10px 0px 10px;line-height: 32px;\">\n\t\t\t\t\t\t\t");
            out.print(I18N.getMsg("dc.mdm.enrollment.cannot.download.admin.app", new Object[0]));
            out.write("\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<div style=\"font-size: 17px;color:#666;padding:21px;line-height: 25px;\">\n\t\t\t\t\t\t\t");
            out.print(I18N.getMsg("dc.mdm.actionlog.enrollment.cannot_download_desc", new Object[0]));
            out.write("\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</div>\n\t\t\t\t\t<div id=\"footerGreen\" class=\"fotterDivFixedIphone fotterDivFixedIphoneBlue\">\n\t\t\t\t\t\t<p>&#169 ");
            out.print(I18N.getMsg("dc.common.MANAGEENGINE_DESKTOP_CENTRAL", new Object[0]));
            out.write("</p>\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t</div>\n        </body>\n    </html>\n");
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
            downloadAgent_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_c_005fout_005f0(final JspTag _jspx_th_fw_005fproductTag_005f1, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f0 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f0_reused = false;
        try {
            _jspx_th_c_005fout_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f0.setParent((Tag)_jspx_th_fw_005fproductTag_005f1);
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${serverName}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f1(final JspTag _jspx_th_fw_005fproductTag_005f1, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f1 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f1_reused = false;
        try {
            _jspx_th_c_005fout_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f1.setParent((Tag)_jspx_th_fw_005fproductTag_005f1);
            _jspx_th_c_005fout_005f1.setValue(PageContextImpl.proprietaryEvaluate("${serverPort}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (downloadAgent_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        downloadAgent_jsp._jspx_dependants.put("/jsp/common/dcI18N.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        downloadAgent_jsp._jspx_imports_packages.add("javax.servlet.http");
        downloadAgent_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        downloadAgent_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

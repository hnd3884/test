package org.apache.jsp.jsp.common;

import java.util.HashSet;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.taglib.DCIframeFeaturesTag;
import javax.servlet.jsp.tagext.JspTag;
import com.me.devicemanagement.framework.webclient.taglib.DCIFrameLoginTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.rt.core.OutTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.adventnet.iam.security.SecurityUtil;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
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

public final class footer_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fiFrameLogin_0026_005fisIframeLogin;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fiFrameFeature_0026_005fshow_005fappName;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return footer_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return footer_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return footer_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = footer_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005ffw_005fiFrameLogin_0026_005fisIframeLogin = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005ffw_005fiFrameFeature_0026_005fshow_005fappName = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.release();
        this._005fjspx_005ftagPool_005ffw_005fiFrameLogin_0026_005fisIframeLogin.release();
        this._005fjspx_005ftagPool_005ffw_005fiFrameFeature_0026_005fshow_005fappName.release();
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
            final PageContext pageContext = _jspx_page_context = footer_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n\n    <script>\n        var footerparams = '';\n        ");
            if (MDMApiFactoryProvider.getMDMAnonymousTrackingImpl().isAnonymousTrackingEnbled()) {
                out.write("\n        footerparams = \"&bn=");
                out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("&id=");
                out.write((String)PageContextImpl.proprietaryEvaluate("${it}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("&lt1=");
                out.write((String)PageContextImpl.proprietaryEvaluate("${licenseType}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("&pt=");
                out.write((String)PageContextImpl.proprietaryEvaluate("${productType}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("&som=");
                out.write((String)PageContextImpl.proprietaryEvaluate("${som}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("|sdp-");
                out.write((String)PageContextImpl.proprietaryEvaluate("${sdp}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("&lang=");
                out.write((String)PageContextImpl.proprietaryEvaluate("${lang}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("&mdm=");
                out.write((String)PageContextImpl.proprietaryEvaluate("${mdm}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                out.write("\";//No i18n \n        ");
            }
            out.write("\n        function openpage( pageurl )\n        {\n           pageurl += footerparams;\n           window.open(pageurl);\n        }\n    </script>\n    <table border=\"0\" width=\"100%\" id=\"footer\" >\n        <tr> \n            <td width=\"50%\" align=\"center\">\n                <span class=\"footertext\">\n                    ");
            out.write("\n                        ");
            if (this._jspx_meth_c_005fout_005f0(_jspx_page_context)) {
                return;
            }
            out.write("&nbsp;");
            if (this._jspx_meth_c_005fout_005f1(_jspx_page_context)) {
                return;
            }
            out.write(",&nbsp;<a href=\"");
            if (this._jspx_meth_c_005fout_005f2(_jspx_page_context)) {
                return;
            }
            out.write("\" target=\"_blank\">");
            if (this._jspx_meth_c_005fout_005f3(_jspx_page_context)) {
                return;
            }
            out.write("</a>\n                    ");
            out.write("\n                </span>\n            </td>\n        </tr>\n    </table>\n\t<script>\n\t\n\t/*  */\nvar cookieName='");
            out.print(DMIAMEncoder.encodeJavaScript(SecurityUtil.getCSRFCookieName(request)));
            out.write("';\nvar csrfParamName='");
            out.print(DMIAMEncoder.encodeJavaScript(SecurityUtil.getCSRFParamName(request)));
            out.write("';\nif(  document.addEventListener  ){\n\n\tArray.prototype.forEach.call(document.querySelectorAll(\"form\"), function(form) {//No I18N\n    var input=document.createElement('input');\n\tinput.type='hidden';\n\tinput.id='csrfparameter';\n\tinput.name=csrfParamName;\n\tinput.value=getCSRFCookie(cookieName);\n\tform.appendChild(input);\n});\n}\nelse{\n\t\n(function(d, s) {\n\td=document, s=d.createStyleSheet();\n\td.querySelectorAll = function(r, c, i, j, a) {\n\t\ta=d.all, c=[], r = r.replace(/\\[for\\b/gi, '[htmlFor').split(',');\n\t\tfor (i=r.length; i--;) {\n\t\t\ts.addRule(r[i], 'k:v');\n\t\t\tfor (j=a.length; j--;) a[j].currentStyle.k && c.push(a[j]);\n\t\t\ts.removeRule(0);\n\t\t}\n\t\treturn c;\n\t}\n})()\nj.each(document.querySelectorAll(\"form\"), function(index, form){//No I18N\n  var realSubmit = form.submit;\n      form.submit = function() {\n          form=addCSRF(form);\n          realSubmit.call(form);\n      };\n});}\nfunction addCSRF(formObj)\n{\n\n   var input = document.createElement(\"input\");\n   input.type = \"text\";\n   input.name=csrfParamName;\n   input.value=getCSRFCookie(cookieName);\n");
            out.write("   input.className=\"hide\";\n   formObj.appendChild(input);\n   \n   return formObj;\n   }\nfunction addParam(txt)\n{\n\ntxt+=\"&\"+csrfParamName+\"=\"+getCSRFCookie(cookieName);\n\nreturn txt;\n}\nfunction getParam()\n{\nvar txt=\"\";\n\ntxt+=csrfParamName+\"=\"+getCSRFCookie(cookieName);\n\nreturn txt;\n}\nfunction getCSRFCookie(cn) {\n                if (document.cookie.length > 0) {\n                        var beginIdx = document.cookie.indexOf(cn + \"=\");\n                        if (beginIdx !== -1) {\n                                beginIdx = beginIdx + cn.length + 1;\n                                var endIdx = document.cookie.indexOf(\";\", beginIdx);\n                                if (endIdx === -1) {\n                                        endIdx = document.cookie.length;\n                                }\n                                return window.unescape(document.cookie.substring(beginIdx, endIdx));\n                        }\n                }\n                return \"\";\n        }\n</script>\t            \n    ");
            if (this._jspx_meth_fw_005fiFrameLogin_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\n              <iframe id=\"helperframe\" class=\"hide\"  height=\"0\" width=\"0\"></iframe>\n");
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
            footer_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.text}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_fw_005fiFrameLogin_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCIFrameLoginTag _jspx_th_fw_005fiFrameLogin_005f0 = (DCIFrameLoginTag)this._005fjspx_005ftagPool_005ffw_005fiFrameLogin_0026_005fisIframeLogin.get((Class)DCIFrameLoginTag.class);
        boolean _jspx_th_fw_005fiFrameLogin_005f0_reused = false;
        try {
            _jspx_th_fw_005fiFrameLogin_005f0.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fiFrameLogin_005f0.setParent((Tag)null);
            _jspx_th_fw_005fiFrameLogin_005f0.setisIframeLogin(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fiFrameLogin_005f0 = _jspx_th_fw_005fiFrameLogin_005f0.doStartTag();
            if (_jspx_eval_fw_005fiFrameLogin_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n    <script>\n        var oldURL = document.referrer;\n        var BaseServer = \"\";\n        var sdpUrl = \"\";\n        function callResize()\n        {\n            if(oldURL)\n            {\n                var fromIndex = oldURL.indexOf(\"://\")+3;\n                var domainName = oldURL.substring(0, oldURL.indexOf(\"/\",fromIndex));\n                if(oldURL.indexOf(\"DCHomePage.do\") != -1)\n                {\n                    createCookie(\"base_server_url\", domainName ,1);//No i18n\n                }\n            }\n        }   \n    </script>\n    ");
                    if (this._jspx_meth_fw_005fiFrameFeature_005f0((JspTag)_jspx_th_fw_005fiFrameLogin_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write(10);
                    evalDoAfterBody = _jspx_th_fw_005fiFrameLogin_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fiFrameLogin_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fiFrameLogin_0026_005fisIframeLogin.reuse((Tag)_jspx_th_fw_005fiFrameLogin_005f0);
            _jspx_th_fw_005fiFrameLogin_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fiFrameLogin_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fiFrameLogin_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fiFrameFeature_005f0(final JspTag _jspx_th_fw_005fiFrameLogin_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCIframeFeaturesTag _jspx_th_fw_005fiFrameFeature_005f0 = (DCIframeFeaturesTag)this._005fjspx_005ftagPool_005ffw_005fiFrameFeature_0026_005fshow_005fappName.get((Class)DCIframeFeaturesTag.class);
        boolean _jspx_th_fw_005fiFrameFeature_005f0_reused = false;
        try {
            _jspx_th_fw_005fiFrameFeature_005f0.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fiFrameFeature_005f0.setParent((Tag)_jspx_th_fw_005fiFrameLogin_005f0);
            _jspx_th_fw_005fiFrameFeature_005f0.setAppName("Helpdesk");
            _jspx_th_fw_005fiFrameFeature_005f0.setShow(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fiFrameFeature_005f0 = _jspx_th_fw_005fiFrameFeature_005f0.doStartTag();
            if (_jspx_eval_fw_005fiFrameFeature_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" \n        <script>                                                \n            window.onload = updateInfoToBaseServer;           \n            function updateInfoToBaseServer()\n            {\n                callResize();\n                var sdpURLfromCookies = readCookie(\"base_server_url\");//No I18N\n                var baseServerURL = sdpURLfromCookies+\"/DCIframeUpdate.do\";//No I18N \n                var helper =  document.getElementById('helperframe');//No I18N\n                var height = document.body.scrollHeight+50;\n                if(height < 925 )\n               {\n                   height = height+200;\n               }\n               var dcURl = document.location.href;\n               if(dcURl.indexOf(\"inventoryScript\") != -1)//No i18n\n               {\n                   /*\n                    * While update the url with strin script SDP server not accepting\n                    */\n                   dcURl = dcURl.replace(\"inventoryScript\", \"inventoryScrip\");//No i18n\n               }               \n                var customersegmentation = '");
                    if (this._jspx_meth_c_005fout_005f4((JspTag)_jspx_th_fw_005fiFrameFeature_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("';\n                var isAllowedAllCustomer = '");
                    if (this._jspx_meth_c_005fout_005f5((JspTag)_jspx_th_fw_005fiFrameFeature_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("';\n                var disablecustomerfilter = '");
                    if (this._jspx_meth_c_005fout_005f6((JspTag)_jspx_th_fw_005fiFrameFeature_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write("';\n                var dcFinalURL = baseServerURL+\"?r=\"+Math.random()+\"&height=\"+height+\"&isAllCus=\"+isAllowedAllCustomer+\"&isSegmentation=\"+customersegmentation+\"&disableFilter=\"+disablecustomerfilter+\"&dcURl=\"+dcURl;//No I18N\n                helper.src  = dcFinalURL ;\n            }\n                         \n        </script>\n    ");
                    evalDoAfterBody = _jspx_th_fw_005fiFrameFeature_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fiFrameFeature_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fiFrameFeature_0026_005fshow_005fappName.reuse((Tag)_jspx_th_fw_005fiFrameFeature_005f0);
            _jspx_th_fw_005fiFrameFeature_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fiFrameFeature_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fiFrameFeature_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f4(final JspTag _jspx_th_fw_005fiFrameFeature_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f4 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f4_reused = false;
        try {
            _jspx_th_c_005fout_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f4.setParent((Tag)_jspx_th_fw_005fiFrameFeature_005f0);
            _jspx_th_c_005fout_005f4.setValue(PageContextImpl.proprietaryEvaluate("${customersegmentation}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f5(final JspTag _jspx_th_fw_005fiFrameFeature_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f5 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f5_reused = false;
        try {
            _jspx_th_c_005fout_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f5.setParent((Tag)_jspx_th_fw_005fiFrameFeature_005f0);
            _jspx_th_c_005fout_005f5.setValue(PageContextImpl.proprietaryEvaluate("${summarypage}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f6(final JspTag _jspx_th_fw_005fiFrameFeature_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f6 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f6_reused = false;
        try {
            _jspx_th_c_005fout_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f6.setParent((Tag)_jspx_th_fw_005fiFrameFeature_005f0);
            _jspx_th_c_005fout_005f6.setValue(PageContextImpl.proprietaryEvaluate("${disablecustomerfilter}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
        (footer_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        footer_jsp._jspx_imports_packages.add("javax.servlet.http");
        footer_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        footer_jsp._jspx_imports_classes.add("com.me.mdm.server.factory.MDMApiFactoryProvider");
        footer_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        footer_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
    }
}

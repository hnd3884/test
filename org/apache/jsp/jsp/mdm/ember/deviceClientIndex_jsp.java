package org.apache.jsp.jsp.mdm.ember;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.ResourceBundle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import java.util.ArrayList;
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

public final class deviceClientIndex_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return deviceClientIndex_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return deviceClientIndex_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return deviceClientIndex_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = deviceClientIndex_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
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
            final PageContext pageContext = _jspx_page_context = deviceClientIndex_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n\n\n\n\n\n\n\n<!-- TODO: Workaround for I18N. Need to remove after API fixes -->\n");
            final String baseName = "ApplicationResources";
            final String newline = System.getProperty("line.separator");
            final StringBuffer output = new StringBuffer();
            ResourceBundle bundle = null;
            try {
                bundle = I18N.getBundle(baseName, I18N.getLocale());
            }
            catch (final Exception ex) {}
            if (bundle != null) {
                output.append("<script>var i18nAppJSON=({");
                boolean first = true;
                final ArrayList<String> keylist = new ArrayList<String>();
                final Enumeration e = bundle.getKeys();
                while (e.hasMoreElements()) {
                    keylist.add(e.nextElement().toString());
                }
                for (final String key : keylist) {
                    if (!first) {
                        output.append(",");
                    }
                    first = false;
                    String value = bundle.getString(key);
                    value = I18N.getEscapedString((Object)value);
                    output.append("'").append(I18N.getEscapedString((Object)key)).append("':'").append(value).append("'").append(newline);
                }
                output.append("});</script>");
                output.append(newline);
                out.println(output.toString());
            }
            final String contextpath = request.getContextPath();
            out.println(I18N.generateI18NScript());
            out.write("\n<!-- TODO: Workaround for I18N. Need to remove after API fixes -->\n<html>\n  <head>\n    ");
            final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f0_reused = false;
            try {
                _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n        ");
                        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f0_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${iosAppCatalog==true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n            <title>");
                                    out.print(I18N.getMsg("dc.mdm.device_mgmt.apps", new Object[0]));
                                    out.write("</title>\n        ");
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
                        out.write("\n        ");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f0_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            final int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f0 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("\n            <title>");
                                    out.print(ProductUrlLoader.getInstance().getValue("productname"));
                                    out.write("</title>\n        ");
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
                        out.write("\n    ");
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
            out.write("\n    <meta charset=\"utf-8\">\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n    <meta name=\"description\" content=\"\">\n    <meta name=\"viewport\" content=\"width=device-width, height=device-height, initial-scale=1 , user-scalable=0\">\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n    <!-- <meta name=\"viewport\" content=\"user-scalable=0\" /> -->\n        <meta forua=\"true\" content=\"no-cache\" http-equiv=\"Cache-Control\"/>\n        <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n        <meta name=\"HandheldFriendly\" content=\"true\"/>\n        <meta name=\"MobileOptimized\" content=\"width\"/>\n\n    <link rel=\"stylesheet\" href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${deviceClientStaticURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/ember/mdmdeviceapp/assets/vendor.css?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\">\n    <link rel=\"stylesheet\" href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${deviceClientStaticURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/ember/mdmdeviceapp/assets/mdm-device-client.css?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\">\n\n\n\n    <style>\n      @font-face {\n        font-family: \"LatoLight\";\n        src: url(\"/themes/styles/font/lato-light.woff2\") format(\"woff2\");\n      }\n      @font-face {\n        font-family: \"LatoSemiBold\";\n        src: url(\"/themes/styles/font/lato-semibold.woff2\") format(\"woff2\");\n      }\n      .loading-image {\n          display: flex;\n          height: 50%;\n          width: 100%;\n          position: absolute;\n          flex-direction: column;\n          justify-content: flex-end;\n          align-items: center;\n      }\n    </style>\n\n    <meta name=\"mdm-device-client/config/environment\" content=\"%7B%22modulePrefix%22%3A%22mdm-device-client%22%2C%22environment%22%3A%22production%22%2C%22rootURL%22%3A%22%2Fember%2Fmdmdeviceapp%2F%22%2C%22locationType%22%3A%22hash%22%2C%22EmberENV%22%3A%7B%22FEATURES%22%3A%7B%7D%2C%22EXTEND_PROTOTYPES%22%3A%7B%22Date%22%3Afalse%7D%7D%2C%22APP%22%3A%7B%7D%2C%22exportApplicationGlobal%22%3Afalse%7D\" />\n    <meta name=\"ios-app-catalog/config/environment\" content=\"%7B%22modulePrefix%22%3A%22ios-app-catalog%22%2C%22environment%22%3A%22production%22%7D\" />\n");
            out.write("\n    <meta name=\"mdm-device-client/config/asset-manifest\" content=\"%7B%22bundles%22%3A%7B%22ios-app-catalog%22%3A%7B%22assets%22%3A%5B%7B%22uri%22%3A%22");
            out.write((String)PageContextImpl.proprietaryEvaluate("${deviceClientStaticURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/ember/mdmdeviceapp/engines-dist/ios-app-catalog/assets/engine.js%22%2C%22type%22%3A%22js%22%7D%2C%7B%22uri%22%3A%22");
            out.write((String)PageContextImpl.proprietaryEvaluate("${deviceClientStaticURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/ember/mdmdeviceapp/engines-dist/ios-app-catalog/assets/engine.css%22%2C%22type%22%3A%22css%22%7D%2C%7B%22uri%22%3A%22");
            out.write((String)PageContextImpl.proprietaryEvaluate("${deviceClientStaticURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/ember/mdmdeviceapp/engines-dist/ios-app-catalog/config/environment.js%22%2C%22type%22%3A%22js%22%7D%5D%7D%7D%7D\" />\n\n\n    </head>\n  <body>\n\n    <input type=\"hidden\" name=\"udid\" id=\"udid\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${udid}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"/>\n    <input type=\"hidden\" name=\"encapiKey\" id=\"encapiKey\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${encapiKey}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"/>\n    <input type=\"hidden\" name=\"isNativeAgent\" id=\"isNativeAgent\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isNativeAgent}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"/>\n    <div id=\"MdmDeviceClient\"></div>\n    ");
            final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f0_reused = false;
            try {
                _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f0.setParent((Tag)null);
                _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${iosAppCatalog==true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
                if (_jspx_eval_c_005fif_005f0 != 0) {
                    int evalDoAfterBody4;
                    do {
                        out.write("\n        <div id=\"app-catalog-loader\">\n            <div class=\"loading-image\">\n                <img src=\"images/app-catalog/loader.gif\" align=\"absmiddle\" height=\"50px\" width=\"100px\">\n                ");
                        out.print(I18N.getMsg("mdm.appMgmt.loadingApps", new Object[0]));
                        out.write("\n            </div>\n        </div>\n    ");
                        evalDoAfterBody4 = _jspx_th_c_005fif_005f0.doAfterBody();
                    } while (evalDoAfterBody4 == 2);
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
            out.write("\n\n    <script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${deviceClientStaticURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/ember/mdmdeviceapp/assets/vendor.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\"></script>\n    <script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${deviceClientStaticURL}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/ember/mdmdeviceapp/assets/mdm-device-client.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\"></script>\n\n    ");
            String deviceClientStaticURL = "";
            if (request.getAttribute("deviceClientStaticURL") != null) {
                deviceClientStaticURL = (String)request.getAttribute("deviceClientStaticURL");
            }
            final Boolean iosAppCatalog = (Boolean)request.getAttribute("iosAppCatalog");
            out.write("\n    <script>\n        var iosAppCatalog = '");
            out.print((Object)iosAppCatalog);
            out.write("';\n        var staticServerUrl = '");
            out.print(deviceClientStaticURL);
            out.write("';\n        //load appcatalog ember route\n        if(iosAppCatalog=='true'){\n            window.location.href = window.location.href.split('#/')[0] + \"#/ios-app-catalog\";\n        }\n   </script>\n\n  </body>\n</html>");
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
                    catch (final IOException ex2) {}
                }
                if (_jspx_page_context == null) {
                    throw new ServletException(t);
                }
                _jspx_page_context.handlePageException(t);
            }
        }
        finally {
            deviceClientIndex_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (deviceClientIndex_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        deviceClientIndex_jsp._jspx_imports_packages.add("javax.servlet.http");
        deviceClientIndex_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        deviceClientIndex_jsp._jspx_imports_classes.add("java.util.ResourceBundle");
        deviceClientIndex_jsp._jspx_imports_classes.add("java.util.Enumeration");
        deviceClientIndex_jsp._jspx_imports_classes.add("java.util.Locale");
        deviceClientIndex_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        deviceClientIndex_jsp._jspx_imports_classes.add("java.util.MissingResourceException");
        deviceClientIndex_jsp._jspx_imports_classes.add("java.util.HashMap");
        deviceClientIndex_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
        deviceClientIndex_jsp._jspx_imports_classes.add("java.util.ArrayList");
    }
}

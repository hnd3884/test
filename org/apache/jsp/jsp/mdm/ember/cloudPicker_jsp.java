package org.apache.jsp.jsp.mdm.ember;

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

public final class cloudPicker_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return cloudPicker_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return cloudPicker_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return cloudPicker_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = cloudPicker_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = cloudPicker_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("\n\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/IncludeJS.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n<script>includeMainScripts(\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(34);
            out.write(44);
            out.write(34);
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\");</script>\n<script type=\"text/javascript\">\n    jQuery(function()\n    {\n        var url = window.location.href; //Current URL\n        var param = url.split(\"?\")[1];\n        if(param == 'frame=close'){\n            top.window.closeGadgets();\n        } else{\n            top.window.attachFileforcloud(param);\n            top.window.closeGadgets();\n        }\n    });\n</script>");
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
            cloudPicker_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (cloudPicker_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("jar:file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar!/META-INF/c.tld", 1425958870000L);
        cloudPicker_jsp._jspx_dependants.put("file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar", 1663600380000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        cloudPicker_jsp._jspx_imports_packages.add("javax.servlet.http");
        cloudPicker_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
    }
}

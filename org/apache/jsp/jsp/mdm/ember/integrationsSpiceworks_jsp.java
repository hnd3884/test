package org.apache.jsp.jsp.mdm.ember;

import java.util.HashSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
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
import java.util.Set;
import java.util.Map;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class integrationsSpiceworks_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return integrationsSpiceworks_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return integrationsSpiceworks_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return integrationsSpiceworks_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = integrationsSpiceworks_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = integrationsSpiceworks_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("\n<html> \n<head>\n  <meta charset=\"utf-8\">\n  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n  <title>");
            out.print(I18N.getMsg("Spiceworks App", new Object[0]));
            out.write("</title>\n  <meta name=\"description\" content=\"\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n\n  \n  <meta name=\"dummy/config/environment\" content=\"%7B%22modulePrefix%22%3A%22dummy%22%2C%22environment%22%3A%22production%22%2C%22rootURL%22%3A%22%2Fember%2Femsintegrations%2Fspiceworksapp%2F%22%2C%22locationType%22%3A%22hash%22%2C%22EmberENV%22%3A%7B%22FEATURES%22%3A%7B%7D%2C%22EXTEND_PROTOTYPES%22%3A%7B%22Date%22%3Afalse%7D%7D%2C%22APP%22%3A%7B%7D%2C%22exportApplicationGlobal%22%3Afalse%7D\" />\n  <meta name=\"spiceworks-app/config/environment\" content=\"%7B%22modulePrefix%22%3A%22spiceworks-app%22%2C%22environment%22%3A%22production%22%7D\" />\n\n  <link integrity=\"\" rel=\"stylesheet\" href=\"/ember/emsintegrations/spiceworksapp/assets/vendor-3ded40d410d4d7cfd340249744c4aeaa.css\">\n  <link integrity=\"\" rel=\"stylesheet\" href=\"/ember/emsintegrations/spiceworksapp/assets/dummy-be9a092fe9acf54a494c59eed518209e.css\">\n\n  <meta name=\"dummy/config/asset-manifest\" content=\"%7B%22bundles%22%3A%7B%22spiceworks-app%22%3A%7B%22assets%22%3A%5B%7B%22uri%22%3A%22/ember/emsintegrations/spiceworksapp/engines-dist/spiceworks-app/assets/engine-15ca67ce3c40b301d32401059c2fa601.js%22%2C%22type%22%3A%22js%22%7D%2C%7B%22uri%22%3A%22/ember/emsintegrations/spiceworksapp/engines-dist/spiceworks-app/assets/engine-535897585b7e77cd3284cc931358de91.css%22%2C%22type%22%3A%22css%22%7D%2C%7B%22uri%22%3A%22/ember/emsintegrations/spiceworksapp/engines-dist/spiceworks-app/config/environment-2b914ffbf045d84f0de4ab94f9552978.js%22%2C%22type%22%3A%22js%22%7D%5D%7D%7D%7D\" />>\n");
            out.write("\n</head>\n<script>\n  let csrfParamName ='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${csrfParamName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("', cookieName = '");
            out.write((String)PageContextImpl.proprietaryEvaluate("${cookieName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("'; //NO I18N\n  var hashURL = window.location.href;\n  var cachedURL = localStorage.getItem('integSpiceWorkHashURL'); //NO I18N\n  if (cachedURL && cachedURL.indexOf('/spiceworks') != -1) {\n    hashURL += cachedURL;\n    localStorage.removeItem('integSpiceWorkHashURL'); //NO I18N\n  }\n  window.location.href = hashURL;\n</script>\n<body>\n\n\n\n<script src=\"/ember/emsintegrations/spiceworksapp/assets/vendor-481627b603eaaf4dc906ad6f576f2b7e.js\"></script>\n<script src=\"/ember/emsintegrations/spiceworksapp/assets/dummy-5112aaa86fce7a5609a814558191c2b5.js\"></script>\n\n\n\n</body>\n</html>\n");
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
            integrationsSpiceworks_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        integrationsSpiceworks_jsp._jspx_imports_packages.add("javax.servlet.http");
        integrationsSpiceworks_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
    }
}

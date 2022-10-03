package org.apache.jsp.jsp.common;

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

public final class include_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return include_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return include_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return include_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = include_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = include_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/common.js?cid=");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"></script>\n<script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/overlib.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMAjaxAPI.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/DMSecurity.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/footer.js\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/jquery/jquery.min.js?");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/prototype.js\"></script>\n<script src=\"js/security.js\" type=\"text/javascript\"></script>\n\n<script>\n    csrfParamName='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${csrfParamName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    cookieName='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${cookieName}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    isSAS='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${isSAS}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    mdmUrl='");
            out.write((String)PageContextImpl.proprietaryEvaluate("${mdmUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("';//No I18N\n    CSRFParamName=csrfParamName;\n    CSRFParamValue=getCSRFCookie(cookieName);\n</script>\n<script>\n jQuery(function () {\n    attachDropDown();\n});\nfunction attachDropDown()\n{\njQuery('.showSingle:not(.bound)').addClass('bound').click(function () {//No I18N\n        var index = jQuery(this).index('.showSingle'),//No I18N\n            newTarget = jQuery('.targetDiv').eq(index);//No I18N\n        //console.log(index);\n        //console.log(newTarget);\n        jQuery('.targetDiv').not(newTarget).slideUp(0)//No I18N\n        newTarget.slideToggle(150);\n        //to hide on body click\n        show=index;\n        return false;\n    });\n}\nfunction openURLQuickLoad (url){\n    // check and remove this function\n   if(typeof useQuickLoad!= 'undefined' && useQuickLoad){\n      urlHistory(url);\n      sendAHref(url, null);\n   }\n   else{\n    window.location=url;\n   }\n}\nfunction popup(id, width)\n        {\n            var html=document.getElementById(id.toString());\n\t\t\t/*fix for issue where action menus are rendered away when clicked continuously for 10 times*/\n");
            out.write("\t\t\tif(!width) {\n\t\t\t\twidth = 230;\n\t\t\t}\n\t\t\tshowDialog(html.innerHTML,\"closeButton=no,closePrevious=false,draggable=no,closeOnBodyClick=yes,zAdjust=true,overflow=hidden,transitionInterval=0.0,transitionType=Effect.BlindDown,position=relative,left=-200,top=17,width=\"+width);//No I18N\n\n        }\n        function bodyClick(event) {\n                var source = event.target || event.srcElement;\n                if(source.className!=\"searchArrow\" && (typeof liClick == 'undefined' || !liClick ))\n                //hide_search();\n                if(typeof show != 'undefined' )\n                    newTarget = jQuery('.targetDiv').eq(show).slideUp(30);//No I18N\n\n                if(typeof clearFlashImg != 'undefined')\n                \tclearFlashImg();\n                /*var source = event.target || event.srcElement;\n                 if(!isDialog){\n                 isDialog=true;\n                 if(source.id=='imgDiv1')\n                 myquickShowMe('st-app');//No I18N\n                 else if(source.id=='helptop')\n                 quickShowMe('contact1');//No I18N\n");
            out.write("                 else if(source.id=='loginNamemultiLevelDropDownLable')\n                 quickShowMe('loginName');//No I18N\n                 }\n                 else\n                 {\n                 closeAll();\n                 isDialog=false;\n                 }*/\n            }\n\t\t\tdocument.body.addEventListener('click',bodyClick);\n</script>\n");
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
            include_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        include_jsp._jspx_imports_packages.add("javax.servlet.http");
        include_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        _jspx_imports_classes = null;
    }
}

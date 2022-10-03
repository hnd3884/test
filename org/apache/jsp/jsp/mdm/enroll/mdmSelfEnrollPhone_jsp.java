package org.apache.jsp.jsp.mdm.enroll;

import java.util.HashSet;
import java.util.HashMap;
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
import org.apache.jasper.runtime.TagHandlerPool;
import java.util.Set;
import java.util.Map;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class mdmSelfEnrollPhone_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmSelfEnrollPhone_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmSelfEnrollPhone_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmSelfEnrollPhone_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmSelfEnrollPhone_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
    }
    
    public void _jspDestroy() {
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
            final PageContext pageContext = _jspx_page_context = mdmSelfEnrollPhone_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n    \n    ");
            out.write("\n    <head>\n\n    </head>\n    <body>\n        <!--        <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n                    <tr>\n                        <td>-->\n        <p>\n            <b>");
            out.print(I18N.getMsg("dc.common.DC_MDM", new Object[0]));
            out.write("</b>\n        </p>\n        <p>\n            ");
            out.print(I18N.getMsg("dc.mdm.email.android.self_enroll_steps", new Object[0]));
            out.write("\n        </p>\n        <p>\n            ");
            out.print(I18N.getMsg("dc.mdm.email.android.enrollment_summary", new Object[0]));
            out.write("\n        </p>\n        <ol>\n            <li>\n                <p>\n                    ");
            out.print(I18N.getMsg("dc.mdm.email.android.enrollment_download_install", new Object[0]));
            out.write("\n                </p>\n                <ol>\n                    <li>\n                        <p>\n                            <a href=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${androidEnrollmentUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(34);
            out.write(62);
            out.print(I18N.getMsg("desktopcentral.common.click_here_cap", new Object[0]));
            out.write("</a> ");
            out.print(I18N.getMsg("dc.mdm.email.enrollment_guideline_android", new Object[0]));
            out.write("\n                        </p>\n                    </li>\n                </ol>\n            </li>\n            <li>\n                <p>\n                    ");
            out.print(I18N.getMsg("dc.mdm.email.enrollment_server_info_desc", new Object[0]));
            out.write("\n                </p>\n                <ol>\n                    <li>\n                        <p>\n                            ");
            out.print(I18N.getMsg("dc.common.SERVER_NAME", new Object[0]));
            out.write("&nbsp;:&nbsp;<b>");
            if (this._jspx_meth_c_005fout_005f0(_jspx_page_context)) {
                return;
            }
            out.write("</b>\n                        </p>\n                    </li>\n                    <li>\n                        <p>\n                            ");
            out.print(I18N.getMsg("dc.common.SERVER_PORT", new Object[0]));
            out.write("&nbsp;:&nbsp;<b>");
            if (this._jspx_meth_c_005fout_005f1(_jspx_page_context)) {
                return;
            }
            out.write("</b>\n                        </p>\n                    </li>\n                    <li>\n                        <p>\n                            ");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Email_Address", new Object[0]));
            out.write("&nbsp;<b>< ");
            out.print(I18N.getMsg("dc.mdm.email.Corporate_Your_Email_Address", new Object[0]));
            out.write(" ></b>\n                        </p>\n                    </li>\n                    <li>\n                        <p>\n                            ");
            out.print(I18N.getMsg("dc.mdm.email.enrollment_server_provide", new Object[0]));
            out.write("\n                        </p>\n                    </li>\n                    <li>\n                        <p>\n                            ");
            out.print(I18N.getMsg("dc.mdm.email.enrollment_owned_by_provide", new Object[0]));
            out.write("\n                        </p>\n                    </li>\n                </ol>\n            </li>\n        </ol>\n        <p>\n            ");
            out.print(I18N.getMsg("dc.mdm.email.enrollment_footer", new Object[0]));
            out.write("\n        </p>\n        <p>\n            ");
            out.print(I18N.getMsg("dc.mdm.email.Thanks", new Object[0]));
            out.write("\n        </p>\n        <p>\n            ");
            out.print(I18N.getMsg("dc.mdm.email.Admin_Team", new Object[0]));
            out.write("\n        </p>\n        <!--                </td>\n                    </tr>\n                </table>-->\n    </body>\n</html>\n");
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
            mdmSelfEnrollPhone_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${serverIP}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
        (mdmSelfEnrollPhone_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("jar:file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar!/META-INF/c.tld", 1425958870000L);
        mdmSelfEnrollPhone_jsp._jspx_dependants.put("file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar", 1663600380000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmSelfEnrollPhone_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmSelfEnrollPhone_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
    }
}

package org.apache.jsp.jsp.admin;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import org.apache.jasper.el.JspValueExpression;
import org.apache.taglibs.standard.tag.rt.core.SetTag;
import com.me.devicemanagement.framework.webclient.taglib.RoleTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import com.adventnet.i18n.I18N;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
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

public final class notAuthorizedPage_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return notAuthorizedPage_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return notAuthorizedPage_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return notAuthorizedPage_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = notAuthorizedPage_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fchoose = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.release();
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.release();
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
            final PageContext pageContext = _jspx_page_context = notAuthorizedPage_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n\n<style>\n    .paragraph {\n        font: 14px 'Lato', 'Roboto', sans-serif;\n        padding-top: 50px;\n        line-height: 24px;\n    }\n    .title {\n        font: 600 35px 'lato',sans-serif;\n        border: 0px !important;\n        padding: 17px;\n        height: 60px;\n        color: #f44842;\n    }\n\n    p {\n        margin: 0px !important;\n        padding: 0px !important;\n    }\n</style>\n");
            if (this._jspx_meth_RoleManagement_005frole_005f0(_jspx_page_context)) {
                return;
            }
            out.write(10);
            final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
            boolean _jspx_th_c_005fchoose_005f0_reused = false;
            try {
                _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                _jspx_th_c_005fchoose_005f0.setParent((Tag)null);
                final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                if (_jspx_eval_c_005fchoose_005f0 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n    ");
                        final WhenTag _jspx_th_c_005fwhen_005f0 = (WhenTag)this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get((Class)WhenTag.class);
                        boolean _jspx_th_c_005fwhen_005f0_reused = false;
                        try {
                            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fwhen_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            _jspx_th_c_005fwhen_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${showLicenseError == \"true\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
                            if (_jspx_eval_c_005fwhen_005f0 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n        <div width=\"100%\" align=\"center\" class=\"paragraph\">\n            <p class=\"title\" >");
                                    out.print(I18N.getMsg("desktopcentral.common.access_denied", new Object[0]));
                                    out.write("</p>\n            <p>");
                                    out.print(I18N.getMsg("mdm.admin.license_edition_error", new Object[0]));
                                    out.write("</p>\n\n        </div>\n    ");
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
                        out.write("\n    ");
                        final OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (OtherwiseTag)this._005fjspx_005ftagPool_005fc_005fotherwise.get((Class)OtherwiseTag.class);
                        boolean _jspx_th_c_005fotherwise_005f0_reused = false;
                        try {
                            _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fotherwise_005f0.setParent((Tag)_jspx_th_c_005fchoose_005f0);
                            final int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
                            if (_jspx_eval_c_005fotherwise_005f0 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write(" \n\n        <div width=\"100%\" align=\"center\" class=\"paragraph\">\n            <p class=\"title\" >");
                                    out.print(I18N.getMsg("desktopcentral.common.access_denied", new Object[0]));
                                    out.write("</p>\n            <p>");
                                    out.print(I18N.getMsg("dc.mainlayout.not_authorized_to_view_page", new Object[0]));
                                    out.write("</p>\n\n        </div>\n\n        <!-- ----------OLD THEME---------- -->\n\n        <!--<table width=\"32%\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n            <tr><td align=\"center\">\n                    <div class=\"shadow1\" >\n                        <div class=\"shadow2\">\n                            <div class=\"shadow3\">\n                                <div class=\"shadow4\">\n                                    <div class=\"shadow5\">\n                                        <div class=\"shadow6\">\n                                            <div class=\"error_page_bg\">\n                                                <table   height=\"100\"  border=\"0\" cellpadding=\"0\" cellspacing=\"3\"   >\n                                                    <tr>\n                                                        <td   style=\"color:#000000; font-family: 'Lato', 'Roboto', sans-serif; font-size:18px; font-weight:900; vertical-align:top; \"><div style=\"width:80%; padding:20px; \"><img align=\"absmiddle\" class=\"errorPNGFix\" src=\"images/error.png\" width=\"63\" height=\"63\"/> &nbsp;&nbsp;&nbsp;");
                                    out.print(I18N.getMsg("desktopcentral.common.access_denied", new Object[0]));
                                    out.write(" </div>\n                                                            <div style=\"width:80%; padding:20px; font-size:14px; color:#FF0000; white-space:nowrap \">");
                                    out.print(I18N.getMsg("dc.mainlayout.not_authorized_to_view_page", new Object[0]));
                                    out.write("</div></td>\n                                                    </tr>\n                                                </table>\n                                            </div>\n                                        </div>\n                                    </div>\n                                </div>\n                            </div>\n                        </div>\n                    </div>\n                </td>\n            </tr>\n        </table>-->\n        ");
                                    if (this._jspx_meth_c_005fif_005f0((JspTag)_jspx_th_c_005fotherwise_005f0, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\n    ");
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
                        out.write(10);
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
            out.write("\n\n\n\n");
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
            notAuthorizedPage_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_RoleManagement_005frole_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final RoleTag _jspx_th_RoleManagement_005frole_005f0 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
        boolean _jspx_th_RoleManagement_005frole_005f0_reused = false;
        try {
            _jspx_th_RoleManagement_005frole_005f0.setPageContext(_jspx_page_context);
            _jspx_th_RoleManagement_005frole_005f0.setParent((Tag)null);
            _jspx_th_RoleManagement_005frole_005f0.setroleName("Common_Write");
            final int _jspx_eval_RoleManagement_005frole_005f0 = _jspx_th_RoleManagement_005frole_005f0.doStartTag();
            if (_jspx_eval_RoleManagement_005frole_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n    ");
                    if (this._jspx_meth_c_005fset_005f0((JspTag)_jspx_th_RoleManagement_005frole_005f0, _jspx_page_context)) {
                        return true;
                    }
                    out.write(10);
                    evalDoAfterBody = _jspx_th_RoleManagement_005frole_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_RoleManagement_005frole_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f0);
            _jspx_th_RoleManagement_005frole_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f0, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fset_005f0(final JspTag _jspx_th_RoleManagement_005frole_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final SetTag _jspx_th_c_005fset_005f0 = (SetTag)this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.get((Class)SetTag.class);
        boolean _jspx_th_c_005fset_005f0_reused = false;
        try {
            _jspx_th_c_005fset_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fset_005f0.setParent((Tag)_jspx_th_RoleManagement_005frole_005f0);
            _jspx_th_c_005fset_005f0.setVar("isAdmin");
            _jspx_th_c_005fset_005f0.setValue(new JspValueExpression("/jsp/admin/notAuthorizedPage.jsp(26,4) 'true'", this._jsp_getExpressionFactory().createValueExpression((Object)"true", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            final int _jspx_eval_c_005fset_005f0 = _jspx_th_c_005fset_005f0.doStartTag();
            if (_jspx_th_c_005fset_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fset_005f0);
            _jspx_th_c_005fset_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fset_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fset_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f0(final JspTag _jspx_th_c_005fotherwise_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final HttpServletRequest request = (HttpServletRequest)_jspx_page_context.getRequest();
        final HttpServletResponse response = (HttpServletResponse)_jspx_page_context.getResponse();
        final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f0_reused = false;
        try {
            _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_c_005fotherwise_005f0);
            _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${setFooter != \"false\" }", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
            if (_jspx_eval_c_005fif_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n            <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n                <tr>\n                    <td  colspan=\"3\" bgcolor=\"#FFFFFF\" valign=\"top\"><div class=\"tableheaderbgcolor\"><img src=\"../images/spacer.png\" width=\"2\" height=\"1\"></div>\n                        ");
                    JspRuntimeLibrary.include((ServletRequest)request, (ServletResponse)response, "/jsp/common/footer.jsp", out, false);
                    out.write("</td>\n                </tr>\n            </table>\n        ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fif_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f0_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (notAuthorizedPage_jsp._jspx_dependants = new HashMap<String, Long>(3)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        notAuthorizedPage_jsp._jspx_dependants.put("jar:file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar!/META-INF/c.tld", 1425958870000L);
        notAuthorizedPage_jsp._jspx_dependants.put("file:/zoho/build/WH_DIR/Sep_19_2022/MDMP/MDMP_5439189/MDMP_DBUILD/build/DESKTOP/DMFramework/ManageEngine/DesktopCentral_Server/lib/taglibs-standard-impl-1.2.5.jar", 1663600380000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        notAuthorizedPage_jsp._jspx_imports_packages.add("javax.servlet.http");
        notAuthorizedPage_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
    }
}

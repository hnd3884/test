package org.apache.jsp.jsp.admin;

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
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.webclient.taglib.DMSASTag;
import com.me.devicemanagement.framework.webclient.taglib.DCMSPTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
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

public final class RoleForm_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fsas_0026_005fisSAS;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return RoleForm_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return RoleForm_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return RoleForm_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = RoleForm_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005ffw_005fsas_0026_005fisSAS = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.release();
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.release();
        this._005fjspx_005ftagPool_005ffw_005fsas_0026_005fisSAS.release();
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
            final PageContext pageContext = _jspx_page_context = RoleForm_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!--$Id: RoleForm.jsp,v 1.4.6.4.4.1.8.7.2.3 2014/08/06 06:49:38 veeraselvan Exp $-->\n\n\n\n\n\n");
            final boolean desktopModuleState = DMApplicationHandler.getInstance().getDesktopModuleState();
            out.write(10);
            out.write(10);
            out.write(10);
            out.write(10);
            out.write("\n<style type=\"text/css\">\n\n.outerSpacing {\n\tpadding-bottom: 15px;\n\tpadding-top: 4px;\n}\n\n.width40 {\n\twidth: 40%;\n}\n</style>\n\n<html>\n<form name=\"UMRolePage\" action=\"/roleMgmt.do\" method=\"POST\" class=\"marginPadding0\" onSubmit=\"return AjaxAPI.submit(this);\" onsuccessfunc=\"returnTo\" validatefunc=\"validateAddRoleInputs\" >\n  <table width='100%' border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"tablebg bodytext\" style=\"margin-top:13px\">\n    <tr>\n      <td class=\"headerStripText\" height=\"22\" colspan=\"2\">&nbsp;");
            out.print(I18N.getMsg("dc.admin.Role_form.Step_1_Define_Role", new Object[0]));
            out.write(" </td>\n    </tr>\n    <tr>\n        <td colspan=\"2\">\n            <table border=\"0\" width=\"100%\"  cellspacing=\"0\" cellpadding=\"3\"  class=\"form-padding\">\n                <tr>\n      <td height=\"38\" align=\"right\" class=\"bodytext\">");
            out.print(I18N.getMsg("dc.admin.Role_form.Role_Name", new Object[0]));
            out.write("<span class=\"bodyboldred\">*</span>&emsp; : &nbsp;</td>\n      <td><input type=\"text\" name=\"role_name\" value=\"");
            if (this._jspx_meth_c_005fout_005f0(_jspx_page_context)) {
                return;
            }
            out.write("\" size=\"30\" class=\"smallbox\" /></td>\n    </tr>\n    <tr>\n      <td height=\"85\" align=\"right\" valign=\"top\"  class=\"bodytext\">");
            out.print(I18N.getMsg("dc.common.DESCRIPTION", new Object[0]));
            out.write("&emsp; : &nbsp;</td>\n      <td valign=\"top\"><div align=\"top\">\n          <textarea name=\"role_desc\" rows=\"5\" cols=\"34\" class=\"smallbox\" style=\"font-family:'Lato', 'Roboto', sans-serif;font-size:12px;font-style:normal;font-weight:400\" >");
            if (this._jspx_meth_c_005fout_005f1(_jspx_page_context)) {
                return;
            }
            out.write("</textarea>\n        </div>\n        <input type=\"hidden\" name=\"role_id\" value=\"");
            if (this._jspx_meth_c_005fout_005f2(_jspx_page_context)) {
                return;
            }
            out.write("\" size=\"25\" /></td>\n    </tr>\n    </table>\n        </td>\n    </tr>\n    <tr><td> &nbsp;</td></tr>\n     ");
            if (desktopModuleState) {
                out.write("\n    <tr>\n      <td class=\"headerStripText\" height=\"22\" colspan=\"2\">&nbsp;");
                out.print(I18N.getMsg("dc.admin.Role_form.Step_2_Select_Control", new Object[0]));
                out.write(" </td>\n    </tr>\n    <tr>\n        <td colspan=\"2\">\n            <table border=\"0\" width=\"100%\"  cellspacing=\"0\" cellpadding=\"15%\" class=\"outerSpacing\">\n   \n    <tr class=\"bodytext\" align=\"center\">\n      <td colspan=\"2\"> <table border=\"0\" width=\"97%\"  cellspacing=\"0\" cellpadding=\"3\" class=\"tablebg bodytext\">\n          <thead>\n            <tr class=\"topstripbg1\">\n              <th class=\"tableHeader width40\" >");
                out.print(I18N.getMsg("dc.admin.Role_form.Module_Name", new Object[0]));
                out.write("</th>\n              <th class=\"tableHeader\" width=\"15%\">");
                out.print(I18N.getMsg("dc.common.FULL_CONTROL", new Object[0]));
                out.write("</th>\n              <th class=\"tableHeader\" width=\"15%\">");
                out.print(I18N.getMsg("dc.admin.Role_form.Write_Access", new Object[0]));
                out.write("</th>\n              <th class=\"tableHeader\" width=\"15%\">");
                out.print(I18N.getMsg("dc.admin.Role_form.Read_Only", new Object[0]));
                out.write("</th>\n              <th class=\"tableHeader\" width=\"15%\">");
                out.print(I18N.getMsg("dc.admin.Role_form.No_Access", new Object[0]));
                out.write(" </th>\n            </tr>\n          </thead>\n          <tbody>\n                            ");
                final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                boolean _jspx_th_c_005fif_005f0_reused = false;
                try {
                    _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
                    _jspx_th_c_005fif_005f0.setParent((Tag)null);
                    _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.Configurations != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                    final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
                    if (_jspx_eval_c_005fif_005f0 != 0) {
                        int evalDoAfterBody;
                        do {
                            out.write("\n            <tr class=\"bodytext evenRow\">\n              <td> &nbsp;");
                            out.print(I18N.getMsg("dc.config.common.CONFIGURATIONS", new Object[0]));
                            out.write("</td>\n              <!--td><input type=\"checkbox\" value=\"Configurations_Full\" name=\"roleList\" /></td>\n                                <td><input type=\"checkbox\" value=\"Configurations_Read\" name=\"roleList\" /></td>\n                                <td><input type=\"checkbox\" value=\"Configurations_Noaccess\" name=\"roleList\" /></td-->\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f3((JspTag)_jspx_th_c_005fif_005f0, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" id=\"Configurations_Admin\" name=\"roleList\" ");
                            if (this._jspx_meth_c_005fif_005f1((JspTag)_jspx_th_c_005fif_005f0, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Configurations')\" /></td>");
                            out.write("                            \n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f4((JspTag)_jspx_th_c_005fif_005f0, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" id=\"Configurations_Write\" name=\"roleList\" ");
                            if (this._jspx_meth_c_005fif_005f2((JspTag)_jspx_th_c_005fif_005f0, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Configurations')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f5((JspTag)_jspx_th_c_005fif_005f0, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" id=\"Configurations_Read\" name=\"roleList\" ");
                            if (this._jspx_meth_c_005fif_005f3((JspTag)_jspx_th_c_005fif_005f0, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckNoaccess('Configurations')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"Configurations_Noaccess\" ");
                            if (this._jspx_meth_c_005fif_005f4((JspTag)_jspx_th_c_005fif_005f0, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckReadWrite('Configurations')\" /></td>");
                            out.write("\n            </tr>\n                            ");
                            evalDoAfterBody = _jspx_th_c_005fif_005f0.doAfterBody();
                        } while (evalDoAfterBody == 2);
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
                out.write("\n                            ");
                final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                boolean _jspx_th_c_005fif_005f5_reused = false;
                try {
                    _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
                    _jspx_th_c_005fif_005f2.setParent((Tag)null);
                    _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.PatchManagement != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                    final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
                    if (_jspx_eval_c_005fif_005f2 != 0) {
                        int evalDoAfterBody2;
                        do {
                            out.write("\n            <tr class=\"bodytext oddRow\">\n              <td> &nbsp;");
                            out.print(I18N.getMsg("dc.common.PATCH_MANAGEMENT", new Object[0]));
                            out.write("</td>\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f6((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"PatchMgmt_Admin\" ");
                            if (this._jspx_meth_c_005fif_005f6((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('PatchMgmt')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f7((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"PatchMgmt_Write\" ");
                            if (this._jspx_meth_c_005fif_005f7((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('PatchMgmt')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f8((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"PatchMgmt_Read\" ");
                            if (this._jspx_meth_c_005fif_005f8((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckNoaccess('PatchMgmt')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"PatchMgmt_Noaccess\" ");
                            if (this._jspx_meth_c_005fif_005f9((JspTag)_jspx_th_c_005fif_005f2, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckReadWrite('PatchMgmt')\"/></td>");
                            out.write("\n            </tr>\n                            ");
                            evalDoAfterBody2 = _jspx_th_c_005fif_005f2.doAfterBody();
                        } while (evalDoAfterBody2 == 2);
                    }
                    if (_jspx_th_c_005fif_005f2.doEndTag() == 5) {
                        return;
                    }
                    this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f2);
                    _jspx_th_c_005fif_005f5_reused = true;
                }
                finally {
                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f5_reused);
                }
                out.write("\n                            ");
                final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                boolean _jspx_th_c_005fif_005f10_reused = false;
                try {
                    _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
                    _jspx_th_c_005fif_005f3.setParent((Tag)null);
                    _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.SoftwareDeployment != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                    final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
                    if (_jspx_eval_c_005fif_005f3 != 0) {
                        int evalDoAfterBody3;
                        do {
                            out.write("\n            <tr class=\"bodytext evenRow\">\n              <td> &nbsp;");
                            out.print(I18N.getMsg("dc.common.SOFTWARE_DEPLOYMENT", new Object[0]));
                            out.write("</td>\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f9((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"SWDeploy_Admin\" ");
                            if (this._jspx_meth_c_005fif_005f11((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('SWDeploy')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f10((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"SWDeploy_Write\" ");
                            if (this._jspx_meth_c_005fif_005f12((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('SWDeploy')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f11((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"SWDeploy_Read\" ");
                            if (this._jspx_meth_c_005fif_005f13((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckNoaccess('SWDeploy')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"SWDeploy_Noaccess\" ");
                            if (this._jspx_meth_c_005fif_005f14((JspTag)_jspx_th_c_005fif_005f3, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckReadWrite('SWDeploy')\"/></td>");
                            out.write("\n            </tr>\n                            ");
                            evalDoAfterBody3 = _jspx_th_c_005fif_005f3.doAfterBody();
                        } while (evalDoAfterBody3 == 2);
                    }
                    if (_jspx_th_c_005fif_005f3.doEndTag() == 5) {
                        return;
                    }
                    this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f3);
                    _jspx_th_c_005fif_005f10_reused = true;
                }
                finally {
                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f10_reused);
                }
                out.write("\n                            ");
                final IfTag _jspx_th_c_005fif_005f4 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                boolean _jspx_th_c_005fif_005f15_reused = false;
                try {
                    _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
                    _jspx_th_c_005fif_005f4.setParent((Tag)null);
                    _jspx_th_c_005fif_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.Inventory != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                    final int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
                    if (_jspx_eval_c_005fif_005f4 != 0) {
                        int evalDoAfterBody4;
                        do {
                            out.write("\n            <tr class=\"bodytext oddRow\">\n              <td> &nbsp;");
                            out.print(I18N.getMsg("dc.common.INVENTORY", new Object[0]));
                            out.write("</td>\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f12((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Inventory_Admin\" ");
                            if (this._jspx_meth_c_005fif_005f16((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Inventory')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f13((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Inventory_Write\" ");
                            if (this._jspx_meth_c_005fif_005f17((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Inventory')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f14((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Inventory_Read\" ");
                            if (this._jspx_meth_c_005fif_005f18((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckNoaccess('Inventory')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"Inventory_Noaccess\" ");
                            if (this._jspx_meth_c_005fif_005f19((JspTag)_jspx_th_c_005fif_005f4, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckReadWrite('Inventory')\"/></td>");
                            out.write("\n            </tr>\n                            ");
                            evalDoAfterBody4 = _jspx_th_c_005fif_005f4.doAfterBody();
                        } while (evalDoAfterBody4 == 2);
                    }
                    if (_jspx_th_c_005fif_005f4.doEndTag() == 5) {
                        return;
                    }
                    this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f4);
                    _jspx_th_c_005fif_005f15_reused = true;
                }
                finally {
                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f15_reused);
                }
                out.write("\n                            ");
                final IfTag _jspx_th_c_005fif_005f5 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                boolean _jspx_th_c_005fif_005f20_reused = false;
                try {
                    _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
                    _jspx_th_c_005fif_005f5.setParent((Tag)null);
                    _jspx_th_c_005fif_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.Tools != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                    final int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
                    if (_jspx_eval_c_005fif_005f5 != 0) {
                        int evalDoAfterBody5;
                        do {
                            out.write("\n            <tr class=\"bodytext evenRow\">\n              <td> &nbsp;");
                            out.print(I18N.getMsg("dc.common.TOOLS", new Object[0]));
                            out.write("</td>\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f15((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Tools_Admin\" ");
                            if (this._jspx_meth_c_005fif_005f21((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Tools')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f16((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Tools_Write\" ");
                            if (this._jspx_meth_c_005fif_005f22((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Tools')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f17((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Tools_Read\" ");
                            if (this._jspx_meth_c_005fif_005f23((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckNoaccess('Tools')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"Tools_Noaccess\" ");
                            if (this._jspx_meth_c_005fif_005f24((JspTag)_jspx_th_c_005fif_005f5, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckReadWrite('Tools')\" /></td>");
                            out.write("\n            </tr>\n                            ");
                            evalDoAfterBody5 = _jspx_th_c_005fif_005f5.doAfterBody();
                        } while (evalDoAfterBody5 == 2);
                    }
                    if (_jspx_th_c_005fif_005f5.doEndTag() == 5) {
                        return;
                    }
                    this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f5);
                    _jspx_th_c_005fif_005f20_reused = true;
                }
                finally {
                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f20_reused);
                }
                out.write("\n                            ");
                final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                boolean _jspx_th_c_005fif_005f25_reused = false;
                try {
                    _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
                    _jspx_th_c_005fif_005f6.setParent((Tag)null);
                    _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.RDS != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                    final int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
                    if (_jspx_eval_c_005fif_005f6 != 0) {
                        int evalDoAfterBody6;
                        do {
                            out.write("\n            <tr class=\"bodytext oddRow\">\n              <td nowrap> &nbsp;");
                            out.print(I18N.getMsg("dc.admin.Role_form.Remote_DesktopSharing", new Object[0]));
                            out.write("</td>\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f18((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Tool_RDS_Admin\"  ");
                            if (this._jspx_meth_c_005fif_005f26((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Tool_RDS')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f19((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Tool_RDS_Write\"  ");
                            if (this._jspx_meth_c_005fif_005f27((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Tool_RDS')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f20((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" name=\"roleList\" id=\"Tool_RDS_Read\"  ");
                            if (this._jspx_meth_c_005fif_005f28((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckNoaccess('Tool_RDS')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"Tool_RDS_Noaccess\"  ");
                            if (this._jspx_meth_c_005fif_005f29((JspTag)_jspx_th_c_005fif_005f6, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckReadWrite('Tool_RDS')\" /></td>");
                            out.write("\n            </tr>\n                            ");
                            evalDoAfterBody6 = _jspx_th_c_005fif_005f6.doAfterBody();
                        } while (evalDoAfterBody6 == 2);
                    }
                    if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                        return;
                    }
                    this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
                    _jspx_th_c_005fif_005f25_reused = true;
                }
                finally {
                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f25_reused);
                }
                out.write("\n                            ");
                final IfTag _jspx_th_c_005fif_005f7 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                boolean _jspx_th_c_005fif_005f30_reused = false;
                try {
                    _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
                    _jspx_th_c_005fif_005f7.setParent((Tag)null);
                    _jspx_th_c_005fif_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.Reports != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                    final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
                    if (_jspx_eval_c_005fif_005f7 != 0) {
                        int evalDoAfterBody7;
                        do {
                            out.write("\n            <tr class=\"bodytext evenRow\">\n              <td> &nbsp;");
                            out.print(I18N.getMsg("dc.admin.Role_form.Report", new Object[0]));
                            out.write("</td>\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f21((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" id=\"Report_Admin\" name=\"roleList\" ");
                            if (this._jspx_meth_c_005fif_005f31((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Report')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f22((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" id=\"Report_Write\" name=\"roleList\" ");
                            if (this._jspx_meth_c_005fif_005f32((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:checkWriteRead('Report')\" /></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"");
                            if (this._jspx_meth_c_005fout_005f23((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                                return;
                            }
                            out.write("\" id=\"Report_Read\" name=\"roleList\" ");
                            if (this._jspx_meth_c_005fif_005f33((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckNoaccess('Report')\"/></td>");
                            out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"Report_Noaccess\" ");
                            if (this._jspx_meth_c_005fif_005f34((JspTag)_jspx_th_c_005fif_005f7, _jspx_page_context)) {
                                return;
                            }
                            out.write(" onClick=\"javascript:uncheckReadWrite('Report')\"/></td>");
                            out.write("\n            </tr>\n                        ");
                            evalDoAfterBody7 = _jspx_th_c_005fif_005f7.doAfterBody();
                        } while (evalDoAfterBody7 == 2);
                    }
                    if (_jspx_th_c_005fif_005f7.doEndTag() == 5) {
                        return;
                    }
                    this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f7);
                    _jspx_th_c_005fif_005f30_reused = true;
                }
                finally {
                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f30_reused);
                }
                out.write("\n                        ");
                final DCMSPTag _jspx_th_fw_005fmsp_005f0 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
                boolean _jspx_th_fw_005fmsp_005f0_reused = false;
                try {
                    _jspx_th_fw_005fmsp_005f0.setPageContext(_jspx_page_context);
                    _jspx_th_fw_005fmsp_005f0.setParent((Tag)null);
                    _jspx_th_fw_005fmsp_005f0.setIsMSP(Boolean.valueOf("false"));
                    final int _jspx_eval_fw_005fmsp_005f0 = _jspx_th_fw_005fmsp_005f0.doStartTag();
                    if (_jspx_eval_fw_005fmsp_005f0 != 0) {
                        int evalDoAfterBody10;
                        do {
                            out.write("\n                        ");
                            final DMSASTag _jspx_th_fw_005fsas_005f0 = (DMSASTag)this._005fjspx_005ftagPool_005ffw_005fsas_0026_005fisSAS.get((Class)DMSASTag.class);
                            boolean _jspx_th_fw_005fsas_005f0_reused = false;
                            try {
                                _jspx_th_fw_005fsas_005f0.setPageContext(_jspx_page_context);
                                _jspx_th_fw_005fsas_005f0.setParent((Tag)_jspx_th_fw_005fmsp_005f0);
                                _jspx_th_fw_005fsas_005f0.setisSAS(Boolean.valueOf("false"));
                                final int _jspx_eval_fw_005fsas_005f0 = _jspx_th_fw_005fsas_005f0.doStartTag();
                                if (_jspx_eval_fw_005fsas_005f0 != 0) {
                                    int evalDoAfterBody9;
                                    do {
                                        out.write("\n                            ");
                                        final IfTag _jspx_th_c_005fif_005f8 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                                        boolean _jspx_th_c_005fif_005f35_reused = false;
                                        try {
                                            _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
                                            _jspx_th_c_005fif_005f8.setParent((Tag)_jspx_th_fw_005fsas_005f0);
                                            _jspx_th_c_005fif_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.SOM != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                            final int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
                                            if (_jspx_eval_c_005fif_005f8 != 0) {
                                                int evalDoAfterBody8;
                                                do {
                                                    out.write("\n            <tr class=\"bodytext oddRow\">\n              <td> &nbsp;");
                                                    out.print(I18N.getMsg("dc.common.SOM", new Object[0]));
                                                    out.write("</td>\n              <td><input type=\"checkbox\" value=\"");
                                                    if (this._jspx_meth_c_005fout_005f24((JspTag)_jspx_th_c_005fif_005f8, _jspx_page_context)) {
                                                        return;
                                                    }
                                                    out.write("\" id=\"SOM_Admin\" name=\"roleList\" ");
                                                    if (this._jspx_meth_c_005fif_005f36((JspTag)_jspx_th_c_005fif_005f8, _jspx_page_context)) {
                                                        return;
                                                    }
                                                    out.write(" onClick=\"javascript:checkWriteRead('SOM')\" /></td>");
                                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                                    if (this._jspx_meth_c_005fout_005f25((JspTag)_jspx_th_c_005fif_005f8, _jspx_page_context)) {
                                                        return;
                                                    }
                                                    out.write("\" id=\"SOM_Write\" name=\"roleList\" ");
                                                    if (this._jspx_meth_c_005fif_005f37((JspTag)_jspx_th_c_005fif_005f8, _jspx_page_context)) {
                                                        return;
                                                    }
                                                    out.write(" onClick=\"javascript:checkWriteRead('SOM')\" /></td>");
                                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                                    if (this._jspx_meth_c_005fout_005f26((JspTag)_jspx_th_c_005fif_005f8, _jspx_page_context)) {
                                                        return;
                                                    }
                                                    out.write("\" id=\"SOM_Read\" name=\"roleList\" ");
                                                    if (this._jspx_meth_c_005fif_005f38((JspTag)_jspx_th_c_005fif_005f8, _jspx_page_context)) {
                                                        return;
                                                    }
                                                    out.write(" onClick=\"javascript:uncheckNoaccess('SOM')\"/></td>");
                                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"SOM_Noaccess\" ");
                                                    if (this._jspx_meth_c_005fif_005f39((JspTag)_jspx_th_c_005fif_005f8, _jspx_page_context)) {
                                                        return;
                                                    }
                                                    out.write(" onClick=\"javascript:uncheckReadWrite('SOM')\"/></td>");
                                                    out.write("\n            </tr>\n                        ");
                                                    evalDoAfterBody8 = _jspx_th_c_005fif_005f8.doAfterBody();
                                                } while (evalDoAfterBody8 == 2);
                                            }
                                            if (_jspx_th_c_005fif_005f8.doEndTag() == 5) {
                                                return;
                                            }
                                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f8);
                                            _jspx_th_c_005fif_005f35_reused = true;
                                        }
                                        finally {
                                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f35_reused);
                                        }
                                        out.write("\n           ");
                                        evalDoAfterBody9 = _jspx_th_fw_005fsas_005f0.doAfterBody();
                                    } while (evalDoAfterBody9 == 2);
                                }
                                if (_jspx_th_fw_005fsas_005f0.doEndTag() == 5) {
                                    return;
                                }
                                this._005fjspx_005ftagPool_005ffw_005fsas_0026_005fisSAS.reuse((Tag)_jspx_th_fw_005fsas_005f0);
                                _jspx_th_fw_005fsas_005f0_reused = true;
                            }
                            finally {
                                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fsas_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fsas_005f0_reused);
                            }
                            out.write(32);
                            out.write("\n           ");
                            evalDoAfterBody10 = _jspx_th_fw_005fmsp_005f0.doAfterBody();
                        } while (evalDoAfterBody10 == 2);
                    }
                    if (_jspx_th_fw_005fmsp_005f0.doEndTag() == 5) {
                        return;
                    }
                    this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f0);
                    _jspx_th_fw_005fmsp_005f0_reused = true;
                }
                finally {
                    JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f0_reused);
                }
                out.write("\n            <tr class=\"bodytext evenrow\" style=\"display: none;\">\n                <td></td>\n                <td><input type=\"checkbox\" value=\"");
                if (this._jspx_meth_c_005fout_005f27(_jspx_page_context)) {
                    return;
                }
                out.write("\" id=\"Tools_PM_Admin\" name=\"roleList\" ");
                if (this._jspx_meth_c_005fif_005f40(_jspx_page_context)) {
                    return;
                }
                out.write(" /></td>\n                <td><input type=\"checkbox\" value=\"");
                if (this._jspx_meth_c_005fout_005f28(_jspx_page_context)) {
                    return;
                }
                out.write("\" id=\"Tools_PM_Write\" name=\"roleList\" ");
                if (this._jspx_meth_c_005fif_005f41(_jspx_page_context)) {
                    return;
                }
                out.write(" /></td>\n                <td><input type=\"checkbox\" value=\"");
                if (this._jspx_meth_c_005fout_005f29(_jspx_page_context)) {
                    return;
                }
                out.write("\" id=\"Tools_PM_Read\" name=\"roleList\" ");
                if (this._jspx_meth_c_005fif_005f42(_jspx_page_context)) {
                    return;
                }
                out.write(" /></td>          \n                <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"Tools_PM_Noaccess\" ");
                if (this._jspx_meth_c_005fif_005f43(_jspx_page_context)) {
                    return;
                }
                out.write(" /></td>\n            </tr>    \n          </tbody>\n        </table></td>\n    </tr>\n\n    </table>\n        </td>\n    </tr>\n  ");
            }
            out.write("\n \n     ");
            final IfTag _jspx_th_c_005fif_005f9 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f44_reused = false;
            try {
                _jspx_th_c_005fif_005f9.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f9.setParent((Tag)null);
                _jspx_th_c_005fif_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMInventory != null ||MODULES.MDMAppMgmt  != null || MODULES.MDMContentMgmt  != null || MODULES.MDMConfiguration != null ||MODULES.MDMSettings  != null  ||MODULES.MDMReport   != null || MODULES.MDMRemoteControl != null || MODULES.MDMAnnouncement != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f9.doStartTag();
                if (_jspx_eval_c_005fif_005f9 != 0) {
                    int evalDoAfterBody19;
                    do {
                        out.write("\n    <tr>\n      <td class=\"headerStripText\" height=\"22\" colspan=\"2\">&nbsp;\n      ");
                        if (desktopModuleState) {
                            out.write("\n            ");
                            out.print(I18N.getMsg("dc.admin.Role_form.Step_3_Select_MDM_Control", new Object[] { "3" }));
                        }
                        else if (!desktopModuleState) {
                            out.write("\n      \t");
                            out.print(I18N.getMsg("dc.admin.Role_form.Step_3_Select_MDM_Control", new Object[] { "2" }));
                            out.write("\n      \t");
                        }
                        out.write("\n      \t</td>\n    </tr>\n    <tr>\n        <td colspan=\"2\">\n            <table border=\"0\" width=\"100%\"  cellspacing=\"0\" cellpadding=\"15%\" class=\"outerSpacing\">\n    <tr class=\"bodytext\" align=\"center\">\n        <td colspan=\"2\"> <table border=\"0\" width=\"97%\"  cellspacing=\"0\" cellpadding=\"3\" class=\"tablebg bodytext\">\n          <thead>\n            <tr class=\"topstripbg1\">\n               <th class=\"tableHeader width40\" >");
                        out.print(I18N.getMsg("dc.admin.Role_form.Module_Name", new Object[0]));
                        out.write("</th>\n               <th class=\"tableHeader\" width=\"15%\"><span>");
                        out.print(I18N.getMsg("dc.common.FULL_CONTROL", new Object[0]));
                        out.write("</span></th>\n               <th class=\"tableHeader\" width=\"15%\">");
                        out.print(I18N.getMsg("dc.admin.Role_form.Write_Access", new Object[0]));
                        out.write("</th>             \n               <th class=\"tableHeader\" width=\"15%\">");
                        out.print(I18N.getMsg("dc.admin.Role_form.Read_Only", new Object[0]));
                        out.write("</th>\n               <th class=\"tableHeader\" width=\"15%\">");
                        out.print(I18N.getMsg("dc.admin.Role_form.No_Access", new Object[0]));
                        out.write(" </th>\n            </tr>\n          </thead>\n          <tbody>\n              ");
                        final IfTag _jspx_th_c_005fif_005f10 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f45_reused = false;
                        try {
                            _jspx_th_c_005fif_005f10.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f10.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMConfiguration != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f10 = _jspx_th_c_005fif_005f10.doStartTag();
                            if (_jspx_eval_c_005fif_005f10 != 0) {
                                int evalDoAfterBody11;
                                do {
                                    out.write("\n            <tr class=\"bodytext evenRow\">\n              <td> &nbsp;");
                                    out.print(I18N.getMsg("dc.mdm.device_mgmt.profile_mgmt", new Object[0]));
                                    out.write("</td>\n              <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f30((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Configurations_Admin\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f46((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Configurations')\" /></span></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f31((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Configurations_Write\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f47((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Configurations')\" /></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f32((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Configurations_Read\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f48((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_Configurations')\" /></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_Configurations_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f49((JspTag)_jspx_th_c_005fif_005f10, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_Configurations')\" /></td> ");
                                    out.write("\n            </tr>\n               ");
                                    evalDoAfterBody11 = _jspx_th_c_005fif_005f10.doAfterBody();
                                } while (evalDoAfterBody11 == 2);
                            }
                            if (_jspx_th_c_005fif_005f10.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f10);
                            _jspx_th_c_005fif_005f45_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f45_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f50_reused = false;
                        try {
                            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMAppMgmt != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
                            if (_jspx_eval_c_005fif_005f11 != 0) {
                                int evalDoAfterBody12;
                                do {
                                    out.write("\n            <tr class=\"bodytext oddRow\">\n              <td> &nbsp;");
                                    out.print(I18N.getMsg("dc.admin.Role_form.APP_MGMT", new Object[0]));
                                    out.write("</td>\n              <td><span ><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f33((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" name=\"roleList\" id=\"MDM_AppMgmt_Admin\" ");
                                    if (this._jspx_meth_c_005fif_005f51((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_AppMgmt')\"/></span></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f34((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" name=\"roleList\" id=\"MDM_AppMgmt_Write\" ");
                                    if (this._jspx_meth_c_005fif_005f52((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_AppMgmt')\"/></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f35((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" name=\"roleList\" id=\"MDM_AppMgmt_Read\" ");
                                    if (this._jspx_meth_c_005fif_005f53((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_AppMgmt')\"/></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_AppMgmt_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f54((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_AppMgmt')\"/></td> ");
                                    out.write("\n            </tr>\n            ");
                                    evalDoAfterBody12 = _jspx_th_c_005fif_005f11.doAfterBody();
                                } while (evalDoAfterBody12 == 2);
                            }
                            if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
                            _jspx_th_c_005fif_005f50_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f50_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f12 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f55_reused = false;
                        try {
                            _jspx_th_c_005fif_005f12.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f12.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f12.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMInventory != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f12 = _jspx_th_c_005fif_005f12.doStartTag();
                            if (_jspx_eval_c_005fif_005f12 != 0) {
                                int evalDoAfterBody13;
                                do {
                                    out.write("\n            <tr class=\"bodytext evenRow\">\n              <td> &nbsp;");
                                    out.print(I18N.getMsg("dc.common.INVENTORY", new Object[0]));
                                    out.write("</td>\n              <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f36((JspTag)_jspx_th_c_005fif_005f12, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" name=\"roleList\" id=\"MDM_Inventory_Admin\" ");
                                    if (this._jspx_meth_c_005fif_005f56((JspTag)_jspx_th_c_005fif_005f12, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Inventory')\" /></span></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f37((JspTag)_jspx_th_c_005fif_005f12, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" name=\"roleList\" id=\"MDM_Inventory_Write\" ");
                                    if (this._jspx_meth_c_005fif_005f57((JspTag)_jspx_th_c_005fif_005f12, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Inventory')\" /></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f38((JspTag)_jspx_th_c_005fif_005f12, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" name=\"roleList\" id=\"MDM_Inventory_Read\" ");
                                    if (this._jspx_meth_c_005fif_005f58((JspTag)_jspx_th_c_005fif_005f12, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_Inventory')\"/></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_Inventory_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f59((JspTag)_jspx_th_c_005fif_005f12, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_Inventory')\"/></td> ");
                                    out.write("\n            </tr>\n            ");
                                    evalDoAfterBody13 = _jspx_th_c_005fif_005f12.doAfterBody();
                                } while (evalDoAfterBody13 == 2);
                            }
                            if (_jspx_th_c_005fif_005f12.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f12);
                            _jspx_th_c_005fif_005f55_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f55_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f13 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f60_reused = false;
                        try {
                            _jspx_th_c_005fif_005f13.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f13.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f13.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMReport != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f13 = _jspx_th_c_005fif_005f13.doStartTag();
                            if (_jspx_eval_c_005fif_005f13 != 0) {
                                int evalDoAfterBody14;
                                do {
                                    out.write("\n            <tr class=\"bodytext oddRow\">\n              <td> &nbsp;");
                                    out.print(I18N.getMsg("dc.admin.Role_form.Report", new Object[0]));
                                    out.write("</td>\n              <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f39((JspTag)_jspx_th_c_005fif_005f13, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Report_Admin\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f61((JspTag)_jspx_th_c_005fif_005f13, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Report')\" /></span></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f40((JspTag)_jspx_th_c_005fif_005f13, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Report_Write\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f62((JspTag)_jspx_th_c_005fif_005f13, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Report')\" /></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f41((JspTag)_jspx_th_c_005fif_005f13, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Report_Read\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f63((JspTag)_jspx_th_c_005fif_005f13, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_Report')\"/></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_Report_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f64((JspTag)_jspx_th_c_005fif_005f13, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_Report')\"/></td> ");
                                    out.write("\n            </tr>\n            ");
                                    evalDoAfterBody14 = _jspx_th_c_005fif_005f13.doAfterBody();
                                } while (evalDoAfterBody14 == 2);
                            }
                            if (_jspx_th_c_005fif_005f13.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f13);
                            _jspx_th_c_005fif_005f60_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f60_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f14 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f65_reused = false;
                        try {
                            _jspx_th_c_005fif_005f14.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f14.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f14.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMEnrollment != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f14 = _jspx_th_c_005fif_005f14.doStartTag();
                            if (_jspx_eval_c_005fif_005f14 != 0) {
                                int evalDoAfterBody15;
                                do {
                                    out.write("\n            <tr class=\"bodytext evenrow\">\n              <td> &nbsp;");
                                    out.print(I18N.getMsg("dc.mdm.general.enrollment", new Object[0]));
                                    out.write("</td>\n              <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f42((JspTag)_jspx_th_c_005fif_005f14, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Enrollment_Admin\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f66((JspTag)_jspx_th_c_005fif_005f14, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Enrollment')\" /></span></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f43((JspTag)_jspx_th_c_005fif_005f14, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Enrollment_Write\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f67((JspTag)_jspx_th_c_005fif_005f14, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Enrollment')\" /></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f44((JspTag)_jspx_th_c_005fif_005f14, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Enrollment_Read\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f68((JspTag)_jspx_th_c_005fif_005f14, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_Enrollment')\"/></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_Enrollment_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f69((JspTag)_jspx_th_c_005fif_005f14, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_Enrollment')\"/></td> ");
                                    out.write("\n            </tr>\n            ");
                                    evalDoAfterBody15 = _jspx_th_c_005fif_005f14.doAfterBody();
                                } while (evalDoAfterBody15 == 2);
                            }
                            if (_jspx_th_c_005fif_005f14.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f14);
                            _jspx_th_c_005fif_005f65_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f65_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f15 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f70_reused = false;
                        try {
                            _jspx_th_c_005fif_005f15.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f15.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f15.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMContentMgmt  != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f15 = _jspx_th_c_005fif_005f15.doStartTag();
                            if (_jspx_eval_c_005fif_005f15 != 0) {
                                int evalDoAfterBody16;
                                do {
                                    out.write("\n            <tr class=\"bodytext oddRow\">\n              <td> &nbsp;");
                                    out.print(I18N.getMsg("mdm.content.mgmt", new Object[0]));
                                    out.write("</td>\n              <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f45((JspTag)_jspx_th_c_005fif_005f15, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_ContentMgmt_Admin\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f71((JspTag)_jspx_th_c_005fif_005f15, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_ContentMgmt')\" /></span></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f46((JspTag)_jspx_th_c_005fif_005f15, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_ContentMgmt_Write\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f72((JspTag)_jspx_th_c_005fif_005f15, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_ContentMgmt')\" /></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f47((JspTag)_jspx_th_c_005fif_005f15, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_ContentMgmt_Read\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f73((JspTag)_jspx_th_c_005fif_005f15, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_ContentMgmt')\"/></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_ContentMgmt_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f74((JspTag)_jspx_th_c_005fif_005f15, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_ContentMgmt')\"/></td> ");
                                    out.write("\n            </tr>\n            ");
                                    evalDoAfterBody16 = _jspx_th_c_005fif_005f15.doAfterBody();
                                } while (evalDoAfterBody16 == 2);
                            }
                            if (_jspx_th_c_005fif_005f15.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f15);
                            _jspx_th_c_005fif_005f70_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f70_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f16 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f75_reused = false;
                        try {
                            _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f16.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMOSUpdateMgmt  != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f16 = _jspx_th_c_005fif_005f16.doStartTag();
                            if (_jspx_eval_c_005fif_005f16 != 0) {
                                int evalDoAfterBody17;
                                do {
                                    out.write("\n                <tr class=\"bodytext evenrow\">\n                  <td> &nbsp;");
                                    out.print(I18N.getMsg("mdm.osupdate.mgmt", new Object[0]));
                                    out.write("</td>\n                  <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f48((JspTag)_jspx_th_c_005fif_005f16, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_OSUpdateMgmt_Admin\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f76((JspTag)_jspx_th_c_005fif_005f16, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_OSUpdateMgmt')\" /></span></td> ");
                                    out.write("\n                  <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f49((JspTag)_jspx_th_c_005fif_005f16, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_OSUpdateMgmt_Write\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f77((JspTag)_jspx_th_c_005fif_005f16, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_OSUpdateMgmt')\" /></td> ");
                                    out.write("\n                  <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f50((JspTag)_jspx_th_c_005fif_005f16, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_OSUpdateMgmt_Read\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f78((JspTag)_jspx_th_c_005fif_005f16, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_OSUpdateMgmt')\"/></td> ");
                                    out.write("\n                  <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_OSUpdateMgmt_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f79((JspTag)_jspx_th_c_005fif_005f16, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_OSUpdateMgmt')\"/></td> ");
                                    out.write("\n                </tr>\n                ");
                                    evalDoAfterBody17 = _jspx_th_c_005fif_005f16.doAfterBody();
                                } while (evalDoAfterBody17 == 2);
                            }
                            if (_jspx_th_c_005fif_005f16.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f16);
                            _jspx_th_c_005fif_005f75_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f75_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f17 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f80_reused = false;
                        try {
                            _jspx_th_c_005fif_005f17.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f17.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f17.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMRemoteControl  != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f17 = _jspx_th_c_005fif_005f17.doStartTag();
                            if (_jspx_eval_c_005fif_005f17 != 0) {
                                int evalDoAfterBody18;
                                do {
                                    out.write("\n            <tr class=\"bodytext oddRow\">\n              <td> &nbsp;");
                                    out.print(I18N.getMsg("dc.mdm.inv.remote_troubleshoot", new Object[0]));
                                    out.write("</td>\n              <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f51((JspTag)_jspx_th_c_005fif_005f17, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_RemoteControl_Admin\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f81((JspTag)_jspx_th_c_005fif_005f17, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_RemoteControl')\" /></span></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f52((JspTag)_jspx_th_c_005fif_005f17, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_RemoteControl_Write\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f82((JspTag)_jspx_th_c_005fif_005f17, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_RemoteControl')\" /></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f53((JspTag)_jspx_th_c_005fif_005f17, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_RemoteControl_Read\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f83((JspTag)_jspx_th_c_005fif_005f17, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_RemoteControl')\"/></td> ");
                                    out.write("\n              <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_RemoteControl_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f84((JspTag)_jspx_th_c_005fif_005f17, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_RemoteControl')\"/></td> ");
                                    out.write("\n            </tr>\n            ");
                                    evalDoAfterBody18 = _jspx_th_c_005fif_005f17.doAfterBody();
                                } while (evalDoAfterBody18 == 2);
                            }
                            if (_jspx_th_c_005fif_005f17.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f17);
                            _jspx_th_c_005fif_005f80_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f17, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f80_reused);
                        }
                        out.write("\n            ");
                        final IfTag _jspx_th_c_005fif_005f18 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f85_reused = false;
                        try {
                            _jspx_th_c_005fif_005f18.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f18.setParent((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f18.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MODULES.MDMAnnouncement  != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f18 = _jspx_th_c_005fif_005f18.doStartTag();
                            if (_jspx_eval_c_005fif_005f18 != 0) {
                                int evalDoAfterBody9;
                                do {
                                    out.write("\n              <tr class=\"bodytext evenrow\">\n                <td> &nbsp;");
                                    out.print(I18N.getMsg("mdm.announcement", new Object[0]));
                                    out.write("</td>\n                <td><span><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f54((JspTag)_jspx_th_c_005fif_005f18, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Announcement_Admin\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f86((JspTag)_jspx_th_c_005fif_005f18, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Announcement')\" /></span></td> ");
                                    out.write("\n                <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f55((JspTag)_jspx_th_c_005fif_005f18, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Announcement_Write\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f87((JspTag)_jspx_th_c_005fif_005f18, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:checkWriteRead('MDM_Announcement')\" /></td> ");
                                    out.write("\n                <td><input type=\"checkbox\" value=\"");
                                    if (this._jspx_meth_c_005fout_005f56((JspTag)_jspx_th_c_005fif_005f18, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write("\" id=\"MDM_Announcement_Read\" name=\"roleList\" ");
                                    if (this._jspx_meth_c_005fif_005f88((JspTag)_jspx_th_c_005fif_005f18, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckNoaccess('MDM_Announcement')\"/></td> ");
                                    out.write("\n                <td><input type=\"checkbox\" value=\"-1\" name=\"roleList\" id=\"MDM_Announcement_Noaccess\" ");
                                    if (this._jspx_meth_c_005fif_005f89((JspTag)_jspx_th_c_005fif_005f18, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(" onClick=\"javascript:uncheckReadWrite('MDM_Announcement')\"/></td> ");
                                    out.write("\n              </tr>\n              ");
                                    evalDoAfterBody9 = _jspx_th_c_005fif_005f18.doAfterBody();
                                } while (evalDoAfterBody9 == 2);
                            }
                            if (_jspx_th_c_005fif_005f18.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f18);
                            _jspx_th_c_005fif_005f85_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f18, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f85_reused);
                        }
                        out.write("\n          </tbody>\n        </table></td>\n    </tr>\n            </table>\n        </td></tr>\n     ");
                        evalDoAfterBody19 = _jspx_th_c_005fif_005f9.doAfterBody();
                    } while (evalDoAfterBody19 == 2);
                }
                if (_jspx_th_c_005fif_005f9.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f9);
                _jspx_th_c_005fif_005f44_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f44_reused);
            }
            out.write("\n    <tr>\n    <tr>\n      <td colspan=\"3\">\n         <div style=\"display:none; text-align:center;\" id=\"infoBox\">\n            <span class=\"bodyboldred\" id=\"errorMsg\"></span>\n         </div>\n       </td>\n     </tr>\n    <tr>\n        <td height=\"28\" colspan=\"2\" align=\"center\" class=\"formSubmitBg\">");
            if (this._jspx_meth_c_005fif_005f90(_jspx_page_context)) {
                return;
            }
            final IfTag _jspx_th_c_005fif_005f19 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f91_reused = false;
            try {
                _jspx_th_c_005fif_005f19.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f19.setParent((Tag)null);
                _jspx_th_c_005fif_005f19.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isDemoMode == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f19 = _jspx_th_c_005fif_005f19.doStartTag();
                if (_jspx_eval_c_005fif_005f19 != 0) {
                    int evalDoAfterBody2;
                    do {
                        out.write("<span class=\"bodyboldred\">");
                        out.print(I18N.getMsg("dc.common.RUNNING_IN RESTRICTED_MODE", new Object[0]));
                        out.write("</span>");
                        evalDoAfterBody2 = _jspx_th_c_005fif_005f19.doAfterBody();
                    } while (evalDoAfterBody2 == 2);
                }
                if (_jspx_th_c_005fif_005f19.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f19);
                _jspx_th_c_005fif_005f91_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f91_reused);
            }
            out.write("\n        <input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.CANCEL", new Object[0]));
            out.write("\" name=\"Cancel\"  class=\"secondaryActionBtn\"  onclick=\"javascript:renderCurrentView('RoleDetailView.cc','tt');\"/></td>\n    </tr>\n  </table>\n  <input type=\"hidden\" id=\"actionCall\" name=\"actionToCall\" value=\"");
            if (this._jspx_meth_c_005fout_005f58(_jspx_page_context)) {
                return;
            }
            out.write("\" />\n  <input name=\"rolePageCsrfPreventionSalt\" type=\"hidden\" value=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${rolePageCsrfPreventionSalt}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" />\n</form>\n\n</html>\n<script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/validation.js\" type=\"text/javascript\"></script>\n<script language=\"Javascript\" src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/js/common.js\" type=\"text/javascript\"></script>\n<script src=\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("/framework/javascript/IncludeJS.js?");
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\" type=\"text/javascript\"></script>\n<script>includeMainScripts(\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${dmjsUrl}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(34);
            out.write(44);
            out.write(34);
            out.print(ProductUrlLoader.getInstance().getValue("buildnumber"));
            out.write("\");</script>\n<script language=\"Javascript\">\nfunction enableAddRole() {\n        if(typeof allowAddRole=='undefined'){\n            allowAddRole = ");
            out.write((String)PageContextImpl.proprietaryEvaluate("${allowAddRole}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write(";\n        }\n        if(allowAddRole) {\n                if(document.getElementById(\"creatte_role\") != null){\n                document.getElementById(\"creatte_role\").disabled=false;\n                document.getElementById(\"creatte_role\").className=\"primaryActionBtn\";\n                }\n        } else {\n        if(document.getElementById(\"creatte_role\") != null){\n        document.getElementById(\"creatte_role\").disabled= true;\n        document.getElementById(\"creatte_role\").className=\"buttongrey\";\n        }\n        document.getElementById(\"infoBox\").style.display = 'block' ;\n        document.getElementById(\"errorMsg\").innerHTML = I18N.getMsg('dm.iamerror.superadmin_confirm_account');\n        }\n    }\n        enableAddRole();\n\n    function returnTo(response,reqOptions)\n    {        \n        var message=response.getOnlyHtml();\n        if(message.indexOf(\"Error\")!=-1)\n        {\n            showAlertIcon();\n            message=message.split(\":\")[1];\n            var htmlText='<table class=\"bodytext\" colspan=\"10\" width=\"100%\" height=\"100%\"><tr><td>&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\" align=\"absmiddle\"/></td><td colspan=\"2\" nowrap height=\"50\"><span class=\"bodytext\">'+message+' &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td></tr><tr><td align=\"center\" colspan=\"2\" class=\"formSubmitBg\"><input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.OK", new Object[0]));
            out.write("\" class=\"primaryActionBtn\" onclick=\"closeDialog();\"></td></tr></table>';//No I18N\n            showDialog(htmlText,'modal=yes,title=");
            out.print(I18N.getMsg("dc.common.ALERT", new Object[0]));
            out.write("');//No I18N\n        }\n        else\n        {\n          showSuccessIcon();\n          hideAfterTimeOut();\n          document.getElementById(\"MessageInfoDiv\").style.display='block';\n          document.getElementById(\"MessageInfoSpan\").innerHTML = message;\n\t\t  changeTabStyle(\"Role\");//NO I18N\t\t  \n        }\n        //refreshSubView('UserDetailView');        \n    }\n\n    function uncheckReadWrite(module)\n    {\n        var mAdmin = module+\"_Admin\";//No I18N        \n        var mWrite = module+\"_Write\";//NO I18N\n        var mRead = module+\"_Read\";//NO I18N\n        var mNoaccess = module+\"_Noaccess\";//NO I18N\n        if(document.getElementById(mNoaccess).checked == true)\n        {\n            document.getElementById(mAdmin).checked=false;\n            document.getElementById(mWrite).checked=false;\n            document.getElementById(mRead).checked=false;\n        }\n        else\n        {\n            document.getElementById(mRead).checked=true;\n        }\n        \n        //Below code used for update the hidden role changes\n");
            out.write("        if((module == 'PatchMgmt') || (module == 'Tools'))\n        {\n            updatePMToolsValues()\n        }\n\n    }\n    function checkWriteRead(module)\n    {\n        var mAdmin = module+\"_Admin\";//No I18N\n        var mWrite = module+\"_Write\";//NO I18N\n        var mRead = module+\"_Read\";//NO I18N\n        var mNoaccess = module+\"_Noaccess\";//NO I18N\t\t\n        if(document.getElementById(mAdmin).checked == true)\n        {\n            document.getElementById(mWrite).checked=true;\n            document.getElementById(mRead).checked=true;\n            document.getElementById(mNoaccess).checked=false;\n        }\n        if(document.getElementById(mWrite).checked == true)\n        {\n            document.getElementById(mRead).checked=true;\n            document.getElementById(mNoaccess).checked=false;\n        }\n\n        //Below code used for update the hidden role changes\n        if((module == 'PatchMgmt'))\n        {\n            updatePMToolsValues()\n        }\n    }\n    function uncheckNoaccess(module)\n    {\n        var mAdmin = module+\"_Admin\";//No I18N\n");
            out.write("        var mWrite = module+\"_Write\";//NO I18N\n        var mRead = module+\"_Read\";//NO I18N\n        var mNoaccess = module+\"_Noaccess\";//NO I18N\n        if(document.getElementById(mRead).checked == true)\n        {\n            document.getElementById(mNoaccess).checked=false;\n        }\n        if(document.getElementById(mRead).checked == false)\n        {\n            document.getElementById(mAdmin).checked=false;\n            document.getElementById(mWrite).checked=false;\n            document.getElementById(mNoaccess).checked=true;\n        }\n\n        //Below code used for update the hidden role changes\n        if((module == 'PatchMgmt') || (module == 'Tools'))\n        {\n            updatePMToolsValues()\n        }\n    }\n    \n    function updatePMToolsValues()\n    {\n        if((document.getElementById(\"PatchMgmt_Admin\").checked == true) )\n        {    \n            document.getElementById(\"Tools_PM_Admin\").checked=true;\n        }\n        else\n        {\n            document.getElementById(\"Tools_PM_Admin\").checked=false;\n");
            out.write("        }\n        \n        if((document.getElementById(\"PatchMgmt_Write\").checked == true) )\n        {    \n            document.getElementById(\"Tools_PM_Write\").checked=true;\n        }\n        else\n        {\n            document.getElementById(\"Tools_PM_Write\").checked=false;\n        }\n        \n        if((document.getElementById(\"PatchMgmt_Read\").checked == true) )\n        {    \n            document.getElementById(\"Tools_PM_Read\").checked=true;\n        }\n        else\n        {\n            document.getElementById(\"Tools_PM_Read\").checked=false;\n        }\n        \n        if((document.getElementById(\"PatchMgmt_Noaccess\").checked == true))\n        {    \n            document.getElementById(\"Tools_PM_Noaccess\").checked=true;\n        }\n        else\n        {\n            document.getElementById(\"Tools_PM_Noaccess\").checked=false;\n        }\n    }\n function checkNoAccessUnChecked(){\n        var moduleArray = [ 'Configurations', 'PatchMgmt', 'SWDeploy', 'Inventory', 'Tools', 'Tool_RDS', 'Report', 'MDM_Inventory', 'MDM_AppMgmt', 'MDM_Configurations', 'MDM_Report', 'MDM_Enrollment', 'SOM','MDM_ContentMgmt','MDM_RemoteControl','MDM_OSUpdateMgmt','MDM_Announcement' ]; // No I18N\n");
            out.write("        for(var moduleIndex = 0; moduleIndex < moduleArray.length ; moduleIndex++){\n            var mNoaccess = moduleArray[moduleIndex]+\"_Noaccess\";\n            var noAccessElement = document.getElementById(mNoaccess);\n            if(noAccessElement && noAccessElement.checked == false){\n                return true;\n            }\n        }\n        return false;\n    }\n    function validateAddRoleInputs()\n    {\n     \n        var roleName = trimAll(document.UMRolePage.role_name.value);\n        if(document.getElementById(\"actionCall\").value==\"UpdateRole\")\n        {\n            var confirmMsg = \"");
            out.print(I18N.getMsg("dc.admin.Role_form.modify_confirm", new Object[0]));
            out.write("\";\n            if(!confirm(confirmMsg))\n            {\n                return false;\n            }        \n        }\n        if(roleName.length == 0)\n        {\n                var htmlText='<table class=\"bodytext\" colspan=\"10\" width=\"100%\" height=\"100%\"><tr><td>&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\" align=\"absmiddle\"/></td><td colspan=\"2\" nowrap height=\"50\"><span class=\"bodytext\">");
            out.print(I18N.getMsg("dc.admin.Role_form.Role_Name_cannot_be_empty", new Object[0]));
            out.write(" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td></tr><tr><td align=\"center\" colspan=\"2\" class=\"formSubmitBg\"><input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.OK", new Object[0]));
            out.write("\" class=\"primaryActionBtn\" onclick=\"closeDialog();document.UMRolePage.role_name.focus();\"></td></tr></table>';//No I18N\n                showDialog(htmlText,'modal=yes,title=");
            out.print(I18N.getMsg("dc.common.ALERT", new Object[0]));
            out.write("');//No I18N\n                return false;\n        }\n        else if(roleName.length < 5 || roleName.length > 30 )\n        {\n                var htmlText='<table class=\"bodytext\" colspan=\"10\" width=\"100%\" height=\"100%\"><tr><td rowspan=\"1\">&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\" align=\"absmiddle\"/></td><td colspan=\"2\" nowrap height=\"50\"><span class=\"bodytext\">");
            out.print(I18N.getMsg("dc.admin.role_name_criteria", new Object[0]));
            out.write(" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td></tr><tr><td align=\"center\" colspan=\"2\" align=\"center\" class=\"formSubmitBg\"><input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.OK", new Object[0]));
            out.write("\" class=\"primaryActionBtn\" onclick=\"closeDialog();document.UMRolePage.role_name.focus();\"></td></tr></table>';//No I18N\n                showDialog(htmlText,'modal=yes,title=");
            out.print(I18N.getMsg("dc.common.ALERT", new Object[0]));
            out.write("');//No I18N\n                return false;\n        }\n        else if(roleName.length > 0)\n        {\n                    for (i=0; i<roleName.length; i++)\n                    {\n                        var ipChar = roleName.charCodeAt(i);\n                        if ((ipChar < 48 || (ipChar > 57 && ipChar < 64) ||\n                            (ipChar > 90 && ipChar != 95 && ipChar < 97 ) || (ipChar > 122 && ipChar <= 125)||\n                            ipChar == 126)&& ipChar != 46 && ipChar != 32 )\n                        {\n                            var htmlText='<table class=\"bodytext\" colspan=\"10\" width=\"100%\" height=\"100%\"><tr><td rowspan=\"1\">&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\" align=\"absmiddle\"/></td><td colspan=\"2\" nowrap height=\"50\"><span class=\"bodytext\">");
            out.print(I18N.getMsg("dc.admin.Role_form.Rolename_char_criteria", new Object[0]));
            out.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td></tr><tr><td align=\"center\" colspan=\"2\" align=\"center\" class=\"formSubmitBg\"><input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.OK", new Object[0]));
            out.write("\" class=\"primaryActionBtn\" onclick=\"closeDialog();\"></td></tr></table>';\n                            showDialog(htmlText,'title=");
            out.print(I18N.getMsg("dc.common.ALERT", new Object[0]));
            out.write(",modal=yes');//No I18N\n                            return false;\n                        }\n                    }\n        }        \n        else if(document.UMRolePage.role_desc.value.length > 250 )\n        {\n                var htmlText='<table class=\"bodytext\" colspan=\"10\" width=\"100%\" height=\"100%\"><tr><td rowspan=\"1\">&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\" align=\"absmiddle\"/></td><td colspan=\"2\" nowrap height=\"50\"><span class=\"bodytext\">");
            out.print(I18N.getMsg("desktopcentral.configurations.common.Description_cannot_exceed_250_characters", new Object[0]));
            out.write(" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td></tr><tr><td align=\"center\" colspan=\"2\" align=\"center\" class=\"formSubmitBg\"><input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.OK", new Object[0]));
            out.write("\" class=\"primaryActionBtn\" onclick=\"closeDialog();document.UMRolePage.role_name.focus();\"></td></tr></table>';//No I18N\n                showDialog(htmlText,'modal=yes,title=");
            out.print(I18N.getMsg("dc.common.ALERT", new Object[0]));
            out.write("');//No I18N\n                return false;\n        }\n            if((! checkNoAccessUnChecked()))\n            {\n                var htmlText='<table class=\"bodytext\" colspan=\"10\" width=\"100%\" height=\"100%\"><tr><td>&nbsp;&nbsp;&nbsp;<img src=\"images/alerts.png\" align=\"absmiddle\"/></td><td colspan=\"2\" nowrap height=\"50\"><span class=\"bodytext\">");
            out.print(I18N.getMsg("dc.admin.Role_form.Role_Save_module_alert", new Object[0]));
            out.write("&nbsp;&nbsp;&nbsp;</span></td></tr><tr><td colspan=\"2\" class=\"formSubmitBg\"><input type=\"button\" value=\"");
            out.print(I18N.getMsg("dc.common.OK", new Object[0]));
            out.write("\" class=\"primaryActionBtn\" onclick=\"closeDialog();\"></td></tr></table>';\n                showDialog(htmlText,'modal=yes,title=");
            out.print(I18N.getMsg("dc.common.ALERT", new Object[0]));
            out.write("');//No I18N\n                return false;\n            }\n\n        \n        return true;\n    }\n</script>        ");
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
            RoleForm_jsp._jspxFactory.releasePageContext(_jspx_page_context);
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
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${ROLENAME}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
            _jspx_th_c_005fout_005f1.setValue(PageContextImpl.proprietaryEvaluate("${ROLEDESC}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
            _jspx_th_c_005fout_005f2.setValue(PageContextImpl.proprietaryEvaluate("${ROLEID}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f3(final JspTag _jspx_th_c_005fif_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f3 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f3_reused = false;
        try {
            _jspx_th_c_005fout_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f3.setParent((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fout_005f3.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Configurations_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fif_005f1(final JspTag _jspx_th_c_005fif_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f1_reused = false;
        try {
            _jspx_th_c_005fif_005f.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f.setParent((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fif_005f.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Configurations_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f.doStartTag();
            if (_jspx_eval_c_005fif_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f);
            _jspx_th_c_005fif_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f4(final JspTag _jspx_th_c_005fif_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f4 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f4_reused = false;
        try {
            _jspx_th_c_005fout_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f4.setParent((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fout_005f4.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Configurations_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fif_005f2(final JspTag _jspx_th_c_005fif_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f2_reused = false;
        try {
            _jspx_th_c_005fif_005f.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f.setParent((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fif_005f.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Configurations_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f.doStartTag();
            if (_jspx_eval_c_005fif_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f);
            _jspx_th_c_005fif_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f5(final JspTag _jspx_th_c_005fif_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f5 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f5_reused = false;
        try {
            _jspx_th_c_005fout_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f5.setParent((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fout_005f5.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Configurations_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fif_005f3(final JspTag _jspx_th_c_005fif_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f3_reused = false;
        try {
            _jspx_th_c_005fif_005f.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f.setParent((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fif_005f.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Configurations_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f.doStartTag();
            if (_jspx_eval_c_005fif_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f);
            _jspx_th_c_005fif_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f3_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f4(final JspTag _jspx_th_c_005fif_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f4_reused = false;
        try {
            _jspx_th_c_005fif_005f.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f.setParent((Tag)_jspx_th_c_005fif_005f0);
            _jspx_th_c_005fif_005f.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Configurations_Write == null && SELECTEDROLELIST.Configurations_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f.doStartTag();
            if (_jspx_eval_c_005fif_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f);
            _jspx_th_c_005fif_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f6(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f6 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f6_reused = false;
        try {
            _jspx_th_c_005fout_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f6.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fout_005f6.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.PatchMgmt_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fif_005f6(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f6_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.PatchMgmt_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
            if (_jspx_eval_c_005fif_005f6 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
            _jspx_th_c_005fif_005f6_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f6_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f7(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f7 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f7_reused = false;
        try {
            _jspx_th_c_005fout_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f7.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fout_005f7.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.PatchMgmt_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f7 = _jspx_th_c_005fout_005f7.doStartTag();
            if (_jspx_th_c_005fout_005f7.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f7);
            _jspx_th_c_005fout_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f7(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f7_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.PatchMgmt_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f6.doStartTag();
            if (_jspx_eval_c_005fif_005f7 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
            _jspx_th_c_005fif_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f8(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f8 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f8_reused = false;
        try {
            _jspx_th_c_005fout_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f8.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fout_005f8.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.PatchMgmt_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f8 = _jspx_th_c_005fout_005f8.doStartTag();
            if (_jspx_th_c_005fout_005f8.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f8);
            _jspx_th_c_005fout_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f8(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f8_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.PatchMgmt_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f6.doStartTag();
            if (_jspx_eval_c_005fif_005f8 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
            _jspx_th_c_005fif_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f9(final JspTag _jspx_th_c_005fif_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f9_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.PatchMgmt_Write == null && SELECTEDROLELIST.PatchMgmt_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f6.doStartTag();
            if (_jspx_eval_c_005fif_005f9 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f6.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f6.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f6);
            _jspx_th_c_005fif_005f9_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f6, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f9_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f9(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f9 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f9_reused = false;
        try {
            _jspx_th_c_005fout_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f9.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fout_005f9.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.SWDeploy_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f9 = _jspx_th_c_005fout_005f9.doStartTag();
            if (_jspx_th_c_005fout_005f9.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f9);
            _jspx_th_c_005fout_005f9_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f9_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f11(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f11_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SWDeploy_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f11 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f11.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
            _jspx_th_c_005fif_005f11_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f11_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f10(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f10 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f10_reused = false;
        try {
            _jspx_th_c_005fout_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f10.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fout_005f10.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.SWDeploy_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f10 = _jspx_th_c_005fout_005f10.doStartTag();
            if (_jspx_th_c_005fout_005f10.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f10);
            _jspx_th_c_005fout_005f10_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f10_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f12(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f12_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SWDeploy_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f12 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f12 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f11.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
            _jspx_th_c_005fif_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f11(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f11 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f11_reused = false;
        try {
            _jspx_th_c_005fout_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f11.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fout_005f11.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.SWDeploy_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f11 = _jspx_th_c_005fout_005f11.doStartTag();
            if (_jspx_th_c_005fout_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f11);
            _jspx_th_c_005fout_005f11_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f11_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f13(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f13_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SWDeploy_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f13 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f13 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f11.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
            _jspx_th_c_005fif_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f14(final JspTag _jspx_th_c_005fif_005f10, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f14_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SWDeploy_Write == null && SELECTEDROLELIST.SWDeploy_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f14 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f14 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f11.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
            _jspx_th_c_005fif_005f14_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f14_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f12(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f12 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f12_reused = false;
        try {
            _jspx_th_c_005fout_005f12.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f12.setParent((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fout_005f12.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Inventory_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f12 = _jspx_th_c_005fout_005f12.doStartTag();
            if (_jspx_th_c_005fout_005f12.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f12);
            _jspx_th_c_005fout_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f16(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f16 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f16_reused = false;
        try {
            _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f16.setParent((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fif_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Inventory_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f16 = _jspx_th_c_005fif_005f16.doStartTag();
            if (_jspx_eval_c_005fif_005f16 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f16.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f16);
            _jspx_th_c_005fif_005f16_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f16_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f13(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f13 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f13_reused = false;
        try {
            _jspx_th_c_005fout_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f13.setParent((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fout_005f13.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Inventory_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f13 = _jspx_th_c_005fout_005f13.doStartTag();
            if (_jspx_th_c_005fout_005f13.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f13);
            _jspx_th_c_005fout_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f17(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f16 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f17_reused = false;
        try {
            _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f16.setParent((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fif_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Inventory_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f17 = _jspx_th_c_005fif_005f16.doStartTag();
            if (_jspx_eval_c_005fif_005f17 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f16.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f16);
            _jspx_th_c_005fif_005f17_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f17_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f14(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f14 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f14_reused = false;
        try {
            _jspx_th_c_005fout_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f14.setParent((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fout_005f14.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Inventory_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f14 = _jspx_th_c_005fout_005f14.doStartTag();
            if (_jspx_th_c_005fout_005f14.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f14);
            _jspx_th_c_005fout_005f14_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f14_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f18(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f16 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f18_reused = false;
        try {
            _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f16.setParent((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fif_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Inventory_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f18 = _jspx_th_c_005fif_005f16.doStartTag();
            if (_jspx_eval_c_005fif_005f18 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f16.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f16);
            _jspx_th_c_005fif_005f18_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f18_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f19(final JspTag _jspx_th_c_005fif_005f15, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f16 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f19_reused = false;
        try {
            _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f16.setParent((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fif_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Inventory_Write == null && SELECTEDROLELIST.Inventory_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f19 = _jspx_th_c_005fif_005f16.doStartTag();
            if (_jspx_eval_c_005fif_005f19 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f16.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f16);
            _jspx_th_c_005fif_005f19_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f19_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f15(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f15 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f15_reused = false;
        try {
            _jspx_th_c_005fout_005f15.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f15.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fout_005f15.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tools_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f15 = _jspx_th_c_005fout_005f15.doStartTag();
            if (_jspx_th_c_005fout_005f15.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f15);
            _jspx_th_c_005fout_005f15_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f15_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f21(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f21 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f21_reused = false;
        try {
            _jspx_th_c_005fif_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f21.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fif_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f21 = _jspx_th_c_005fif_005f21.doStartTag();
            if (_jspx_eval_c_005fif_005f21 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f21.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f21.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f21);
            _jspx_th_c_005fif_005f21_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f21_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f16(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f16 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f16_reused = false;
        try {
            _jspx_th_c_005fout_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f16.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fout_005f16.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tools_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f16 = _jspx_th_c_005fout_005f16.doStartTag();
            if (_jspx_th_c_005fout_005f16.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f16);
            _jspx_th_c_005fout_005f16_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f16, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f16_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f22(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f21 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f22_reused = false;
        try {
            _jspx_th_c_005fif_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f21.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fif_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f22 = _jspx_th_c_005fif_005f21.doStartTag();
            if (_jspx_eval_c_005fif_005f22 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f21.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f21.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f21);
            _jspx_th_c_005fif_005f22_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f22_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f17(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f17 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f17_reused = false;
        try {
            _jspx_th_c_005fout_005f17.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f17.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fout_005f17.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tools_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f17 = _jspx_th_c_005fout_005f17.doStartTag();
            if (_jspx_th_c_005fout_005f17.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f17);
            _jspx_th_c_005fout_005f17_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f17, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f17_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f23(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f21 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f23_reused = false;
        try {
            _jspx_th_c_005fif_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f21.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fif_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f23 = _jspx_th_c_005fif_005f21.doStartTag();
            if (_jspx_eval_c_005fif_005f23 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f21.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f21.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f21);
            _jspx_th_c_005fif_005f23_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f23_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f24(final JspTag _jspx_th_c_005fif_005f20, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f21 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f24_reused = false;
        try {
            _jspx_th_c_005fif_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f21.setParent((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fif_005f21.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_Write == null && SELECTEDROLELIST.Tools_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f24 = _jspx_th_c_005fif_005f21.doStartTag();
            if (_jspx_eval_c_005fif_005f24 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f21.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f21.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f21);
            _jspx_th_c_005fif_005f24_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f24_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f18(final JspTag _jspx_th_c_005fif_005f25, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f18 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f18_reused = false;
        try {
            _jspx_th_c_005fout_005f18.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f18.setParent((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fout_005f18.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tool_RDS_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f18 = _jspx_th_c_005fout_005f18.doStartTag();
            if (_jspx_th_c_005fout_005f18.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f18);
            _jspx_th_c_005fout_005f18_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f18, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f18_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f26(final JspTag _jspx_th_c_005fif_005f25, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f26 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f26_reused = false;
        try {
            _jspx_th_c_005fif_005f26.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f26.setParent((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fif_005f26.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tool_RDS_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f26 = _jspx_th_c_005fif_005f26.doStartTag();
            if (_jspx_eval_c_005fif_005f26 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f26.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f26.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f26);
            _jspx_th_c_005fif_005f26_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f26, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f26_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f19(final JspTag _jspx_th_c_005fif_005f25, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f19 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f19_reused = false;
        try {
            _jspx_th_c_005fout_005f19.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f19.setParent((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fout_005f19.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tool_RDS_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f19 = _jspx_th_c_005fout_005f19.doStartTag();
            if (_jspx_th_c_005fout_005f19.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f19);
            _jspx_th_c_005fout_005f19_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f19_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f27(final JspTag _jspx_th_c_005fif_005f25, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f26 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f27_reused = false;
        try {
            _jspx_th_c_005fif_005f26.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f26.setParent((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fif_005f26.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tool_RDS_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f27 = _jspx_th_c_005fif_005f26.doStartTag();
            if (_jspx_eval_c_005fif_005f27 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f26.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f26.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f26);
            _jspx_th_c_005fif_005f27_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f26, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f27_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f20(final JspTag _jspx_th_c_005fif_005f25, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f20 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f20_reused = false;
        try {
            _jspx_th_c_005fout_005f20.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f20.setParent((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fout_005f20.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tool_RDS_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f20 = _jspx_th_c_005fout_005f20.doStartTag();
            if (_jspx_th_c_005fout_005f20.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f20);
            _jspx_th_c_005fout_005f20_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f20, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f20_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f28(final JspTag _jspx_th_c_005fif_005f25, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f26 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f28_reused = false;
        try {
            _jspx_th_c_005fif_005f26.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f26.setParent((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fif_005f26.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tool_RDS_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f28 = _jspx_th_c_005fif_005f26.doStartTag();
            if (_jspx_eval_c_005fif_005f28 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f26.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f26.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f26);
            _jspx_th_c_005fif_005f28_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f26, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f28_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f29(final JspTag _jspx_th_c_005fif_005f25, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f26 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f29_reused = false;
        try {
            _jspx_th_c_005fif_005f26.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f26.setParent((Tag)_jspx_th_c_005fif_005f25);
            _jspx_th_c_005fif_005f26.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tool_RDS_Write == null && SELECTEDROLELIST.Tool_RDS_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f29 = _jspx_th_c_005fif_005f26.doStartTag();
            if (_jspx_eval_c_005fif_005f29 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f26.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f26.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f26);
            _jspx_th_c_005fif_005f29_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f26, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f29_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f21(final JspTag _jspx_th_c_005fif_005f30, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f21 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f21_reused = false;
        try {
            _jspx_th_c_005fout_005f21.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f21.setParent((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fout_005f21.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Report_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f21 = _jspx_th_c_005fout_005f21.doStartTag();
            if (_jspx_th_c_005fout_005f21.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f21);
            _jspx_th_c_005fout_005f21_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f21, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f21_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f31(final JspTag _jspx_th_c_005fif_005f30, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f31 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f31_reused = false;
        try {
            _jspx_th_c_005fif_005f31.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f31.setParent((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fif_005f31.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Report_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f31 = _jspx_th_c_005fif_005f31.doStartTag();
            if (_jspx_eval_c_005fif_005f31 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f31.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f31.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f31);
            _jspx_th_c_005fif_005f31_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f31, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f31_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f22(final JspTag _jspx_th_c_005fif_005f30, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f22 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f22_reused = false;
        try {
            _jspx_th_c_005fout_005f22.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f22.setParent((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fout_005f22.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Report_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f22 = _jspx_th_c_005fout_005f22.doStartTag();
            if (_jspx_th_c_005fout_005f22.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f22);
            _jspx_th_c_005fout_005f22_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f22, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f22_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f32(final JspTag _jspx_th_c_005fif_005f30, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f31 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f32_reused = false;
        try {
            _jspx_th_c_005fif_005f31.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f31.setParent((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fif_005f31.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Report_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f32 = _jspx_th_c_005fif_005f31.doStartTag();
            if (_jspx_eval_c_005fif_005f32 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f31.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f31.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f31);
            _jspx_th_c_005fif_005f32_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f31, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f32_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f23(final JspTag _jspx_th_c_005fif_005f30, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f23 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f23_reused = false;
        try {
            _jspx_th_c_005fout_005f23.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f23.setParent((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fout_005f23.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Report_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f23 = _jspx_th_c_005fout_005f23.doStartTag();
            if (_jspx_th_c_005fout_005f23.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f23);
            _jspx_th_c_005fout_005f23_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f23, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f23_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f33(final JspTag _jspx_th_c_005fif_005f30, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f31 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f33_reused = false;
        try {
            _jspx_th_c_005fif_005f31.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f31.setParent((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fif_005f31.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Report_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f33 = _jspx_th_c_005fif_005f31.doStartTag();
            if (_jspx_eval_c_005fif_005f33 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f31.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f31.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f31);
            _jspx_th_c_005fif_005f33_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f31, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f33_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f34(final JspTag _jspx_th_c_005fif_005f30, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f31 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f34_reused = false;
        try {
            _jspx_th_c_005fif_005f31.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f31.setParent((Tag)_jspx_th_c_005fif_005f30);
            _jspx_th_c_005fif_005f31.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Report_Write == null && SELECTEDROLELIST.Report_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f34 = _jspx_th_c_005fif_005f31.doStartTag();
            if (_jspx_eval_c_005fif_005f34 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f31.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f31.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f31);
            _jspx_th_c_005fif_005f34_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f31, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f34_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f24(final JspTag _jspx_th_c_005fif_005f35, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f24 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f24_reused = false;
        try {
            _jspx_th_c_005fout_005f24.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f24.setParent((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fout_005f24.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.SOM_Admin }", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f24 = _jspx_th_c_005fout_005f24.doStartTag();
            if (_jspx_th_c_005fout_005f24.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f24);
            _jspx_th_c_005fout_005f24_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f24, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f24_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f36(final JspTag _jspx_th_c_005fif_005f35, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f36 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f36_reused = false;
        try {
            _jspx_th_c_005fif_005f36.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f36.setParent((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fif_005f36.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SOM_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f36 = _jspx_th_c_005fif_005f36.doStartTag();
            if (_jspx_eval_c_005fif_005f36 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f36.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f36.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f36);
            _jspx_th_c_005fif_005f36_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f36, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f36_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f25(final JspTag _jspx_th_c_005fif_005f35, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f25 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f25_reused = false;
        try {
            _jspx_th_c_005fout_005f25.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f25.setParent((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fout_005f25.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.SOM_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f25 = _jspx_th_c_005fout_005f25.doStartTag();
            if (_jspx_th_c_005fout_005f25.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f25);
            _jspx_th_c_005fout_005f25_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f25, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f25_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f37(final JspTag _jspx_th_c_005fif_005f35, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f36 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f37_reused = false;
        try {
            _jspx_th_c_005fif_005f36.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f36.setParent((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fif_005f36.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SOM_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f37 = _jspx_th_c_005fif_005f36.doStartTag();
            if (_jspx_eval_c_005fif_005f37 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f36.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f36.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f36);
            _jspx_th_c_005fif_005f37_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f36, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f37_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f26(final JspTag _jspx_th_c_005fif_005f35, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f26 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f26_reused = false;
        try {
            _jspx_th_c_005fout_005f26.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f26.setParent((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fout_005f26.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.SOM_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f26 = _jspx_th_c_005fout_005f26.doStartTag();
            if (_jspx_th_c_005fout_005f26.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f26);
            _jspx_th_c_005fout_005f26_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f26, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f26_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f38(final JspTag _jspx_th_c_005fif_005f35, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f36 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f38_reused = false;
        try {
            _jspx_th_c_005fif_005f36.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f36.setParent((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fif_005f36.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SOM_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f38 = _jspx_th_c_005fif_005f36.doStartTag();
            if (_jspx_eval_c_005fif_005f38 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f36.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f36.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f36);
            _jspx_th_c_005fif_005f38_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f36, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f38_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f39(final JspTag _jspx_th_c_005fif_005f35, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f36 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f39_reused = false;
        try {
            _jspx_th_c_005fif_005f36.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f36.setParent((Tag)_jspx_th_c_005fif_005f35);
            _jspx_th_c_005fif_005f36.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.SOM_Write == null && SELECTEDROLELIST.SOM_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f39 = _jspx_th_c_005fif_005f36.doStartTag();
            if (_jspx_eval_c_005fif_005f39 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f36.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f36.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f36);
            _jspx_th_c_005fif_005f39_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f36, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f39_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f27(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f27 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f27_reused = false;
        try {
            _jspx_th_c_005fout_005f27.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f27.setParent((Tag)null);
            _jspx_th_c_005fout_005f27.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tools_PM_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f27 = _jspx_th_c_005fout_005f27.doStartTag();
            if (_jspx_th_c_005fout_005f27.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f27);
            _jspx_th_c_005fout_005f27_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f27, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f27_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f40(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f40 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f40_reused = false;
        try {
            _jspx_th_c_005fif_005f40.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f40.setParent((Tag)null);
            _jspx_th_c_005fif_005f40.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_PM_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f40 = _jspx_th_c_005fif_005f40.doStartTag();
            if (_jspx_eval_c_005fif_005f40 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f40.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f40.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f40);
            _jspx_th_c_005fif_005f40_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f40, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f40_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f28(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f28 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f28_reused = false;
        try {
            _jspx_th_c_005fout_005f28.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f28.setParent((Tag)null);
            _jspx_th_c_005fout_005f28.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tools_PM_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f28 = _jspx_th_c_005fout_005f28.doStartTag();
            if (_jspx_th_c_005fout_005f28.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f28);
            _jspx_th_c_005fout_005f28_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f28, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f28_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f41(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f41 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f41_reused = false;
        try {
            _jspx_th_c_005fif_005f41.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f41.setParent((Tag)null);
            _jspx_th_c_005fif_005f41.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_PM_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f41 = _jspx_th_c_005fif_005f41.doStartTag();
            if (_jspx_eval_c_005fif_005f41 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f41.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f41.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f41);
            _jspx_th_c_005fif_005f41_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f41, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f41_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f29(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f29 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f29_reused = false;
        try {
            _jspx_th_c_005fout_005f29.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f29.setParent((Tag)null);
            _jspx_th_c_005fout_005f29.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.Tools_PM_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f29 = _jspx_th_c_005fout_005f29.doStartTag();
            if (_jspx_th_c_005fout_005f29.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f29);
            _jspx_th_c_005fout_005f29_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f29, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f29_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f42(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f42 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f42_reused = false;
        try {
            _jspx_th_c_005fif_005f42.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f42.setParent((Tag)null);
            _jspx_th_c_005fif_005f42.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_PM_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f42 = _jspx_th_c_005fif_005f42.doStartTag();
            if (_jspx_eval_c_005fif_005f42 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f42.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f42.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f42);
            _jspx_th_c_005fif_005f42_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f42, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f42_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f43(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f43 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f43_reused = false;
        try {
            _jspx_th_c_005fif_005f43.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f43.setParent((Tag)null);
            _jspx_th_c_005fif_005f43.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.Tools_PM_Write == null && SELECTEDROLELIST.Tools_PM_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f43 = _jspx_th_c_005fif_005f43.doStartTag();
            if (_jspx_eval_c_005fif_005f43 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f43.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f43.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f43);
            _jspx_th_c_005fif_005f43_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f43, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f43_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f30(final JspTag _jspx_th_c_005fif_005f45, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f30 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f30_reused = false;
        try {
            _jspx_th_c_005fout_005f30.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f30.setParent((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fout_005f30.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Configurations_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f30 = _jspx_th_c_005fout_005f30.doStartTag();
            if (_jspx_th_c_005fout_005f30.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f30);
            _jspx_th_c_005fout_005f30_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f30, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f30_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f46(final JspTag _jspx_th_c_005fif_005f45, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f46 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f46_reused = false;
        try {
            _jspx_th_c_005fif_005f46.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f46.setParent((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fif_005f46.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Configurations_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f46 = _jspx_th_c_005fif_005f46.doStartTag();
            if (_jspx_eval_c_005fif_005f46 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f46.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f46.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f46);
            _jspx_th_c_005fif_005f46_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f46, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f46_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f31(final JspTag _jspx_th_c_005fif_005f45, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f31 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f31_reused = false;
        try {
            _jspx_th_c_005fout_005f31.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f31.setParent((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fout_005f31.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Configurations_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f31 = _jspx_th_c_005fout_005f31.doStartTag();
            if (_jspx_th_c_005fout_005f31.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f31);
            _jspx_th_c_005fout_005f31_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f31, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f31_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f47(final JspTag _jspx_th_c_005fif_005f45, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f46 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f47_reused = false;
        try {
            _jspx_th_c_005fif_005f46.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f46.setParent((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fif_005f46.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Configurations_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f47 = _jspx_th_c_005fif_005f46.doStartTag();
            if (_jspx_eval_c_005fif_005f47 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f46.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f46.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f46);
            _jspx_th_c_005fif_005f47_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f46, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f47_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f32(final JspTag _jspx_th_c_005fif_005f45, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f32 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f32_reused = false;
        try {
            _jspx_th_c_005fout_005f32.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f32.setParent((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fout_005f32.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Configurations_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f32 = _jspx_th_c_005fout_005f32.doStartTag();
            if (_jspx_th_c_005fout_005f32.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f32);
            _jspx_th_c_005fout_005f32_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f32, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f32_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f48(final JspTag _jspx_th_c_005fif_005f45, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f46 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f48_reused = false;
        try {
            _jspx_th_c_005fif_005f46.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f46.setParent((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fif_005f46.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Configurations_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f48 = _jspx_th_c_005fif_005f46.doStartTag();
            if (_jspx_eval_c_005fif_005f48 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f46.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f46.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f46);
            _jspx_th_c_005fif_005f48_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f46, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f48_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f49(final JspTag _jspx_th_c_005fif_005f45, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f46 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f49_reused = false;
        try {
            _jspx_th_c_005fif_005f46.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f46.setParent((Tag)_jspx_th_c_005fif_005f45);
            _jspx_th_c_005fif_005f46.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Configurations_Write == null && SELECTEDROLELIST.MDM_Configurations_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f49 = _jspx_th_c_005fif_005f46.doStartTag();
            if (_jspx_eval_c_005fif_005f49 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f46.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f46.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f46);
            _jspx_th_c_005fif_005f49_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f46, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f49_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f33(final JspTag _jspx_th_c_005fif_005f50, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f33 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f33_reused = false;
        try {
            _jspx_th_c_005fout_005f33.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f33.setParent((Tag)_jspx_th_c_005fif_005f50);
            _jspx_th_c_005fout_005f33.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_AppMgmt_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f33 = _jspx_th_c_005fout_005f33.doStartTag();
            if (_jspx_th_c_005fout_005f33.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f33);
            _jspx_th_c_005fout_005f33_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f33, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f33_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f51(final JspTag _jspx_th_c_005fif_005f50, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f51 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f51_reused = false;
        try {
            _jspx_th_c_005fif_005f51.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f51.setParent((Tag)_jspx_th_c_005fif_005f50);
            _jspx_th_c_005fif_005f51.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_AppMgmt_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f51 = _jspx_th_c_005fif_005f51.doStartTag();
            if (_jspx_eval_c_005fif_005f51 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f51.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f51.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f51);
            _jspx_th_c_005fif_005f51_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f51, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f51_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f34(final JspTag _jspx_th_c_005fif_005f50, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f34 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f34_reused = false;
        try {
            _jspx_th_c_005fout_005f34.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f34.setParent((Tag)_jspx_th_c_005fif_005f50);
            _jspx_th_c_005fout_005f34.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_AppMgmt_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f34 = _jspx_th_c_005fout_005f34.doStartTag();
            if (_jspx_th_c_005fout_005f34.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f34);
            _jspx_th_c_005fout_005f34_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f34, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f34_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f52(final JspTag _jspx_th_c_005fif_005f50, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f51 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f52_reused = false;
        try {
            _jspx_th_c_005fif_005f51.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f51.setParent((Tag)_jspx_th_c_005fif_005f50);
            _jspx_th_c_005fif_005f51.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_AppMgmt_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f52 = _jspx_th_c_005fif_005f51.doStartTag();
            if (_jspx_eval_c_005fif_005f52 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f51.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f51.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f51);
            _jspx_th_c_005fif_005f52_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f51, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f52_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f35(final JspTag _jspx_th_c_005fif_005f50, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f35 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f35_reused = false;
        try {
            _jspx_th_c_005fout_005f35.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f35.setParent((Tag)_jspx_th_c_005fif_005f50);
            _jspx_th_c_005fout_005f35.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_AppMgmt_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f35 = _jspx_th_c_005fout_005f35.doStartTag();
            if (_jspx_th_c_005fout_005f35.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f35);
            _jspx_th_c_005fout_005f35_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f35, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f35_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f53(final JspTag _jspx_th_c_005fif_005f50, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f51 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f53_reused = false;
        try {
            _jspx_th_c_005fif_005f51.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f51.setParent((Tag)_jspx_th_c_005fif_005f50);
            _jspx_th_c_005fif_005f51.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_AppMgmt_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f53 = _jspx_th_c_005fif_005f51.doStartTag();
            if (_jspx_eval_c_005fif_005f53 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f51.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f51.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f51);
            _jspx_th_c_005fif_005f53_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f51, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f53_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f54(final JspTag _jspx_th_c_005fif_005f50, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f51 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f54_reused = false;
        try {
            _jspx_th_c_005fif_005f51.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f51.setParent((Tag)_jspx_th_c_005fif_005f50);
            _jspx_th_c_005fif_005f51.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_AppMgmt_Write == null && SELECTEDROLELIST.MDM_AppMgmt_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f54 = _jspx_th_c_005fif_005f51.doStartTag();
            if (_jspx_eval_c_005fif_005f54 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f51.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f51.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f51);
            _jspx_th_c_005fif_005f54_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f51, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f54_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f36(final JspTag _jspx_th_c_005fif_005f55, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f36 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f36_reused = false;
        try {
            _jspx_th_c_005fout_005f36.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f36.setParent((Tag)_jspx_th_c_005fif_005f55);
            _jspx_th_c_005fout_005f36.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Inventory_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f36 = _jspx_th_c_005fout_005f36.doStartTag();
            if (_jspx_th_c_005fout_005f36.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f36);
            _jspx_th_c_005fout_005f36_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f36, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f36_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f56(final JspTag _jspx_th_c_005fif_005f55, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f56 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f56_reused = false;
        try {
            _jspx_th_c_005fif_005f56.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f56.setParent((Tag)_jspx_th_c_005fif_005f55);
            _jspx_th_c_005fif_005f56.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Inventory_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f56 = _jspx_th_c_005fif_005f56.doStartTag();
            if (_jspx_eval_c_005fif_005f56 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f56.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f56.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f56);
            _jspx_th_c_005fif_005f56_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f56, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f56_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f37(final JspTag _jspx_th_c_005fif_005f55, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f37 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f37_reused = false;
        try {
            _jspx_th_c_005fout_005f37.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f37.setParent((Tag)_jspx_th_c_005fif_005f55);
            _jspx_th_c_005fout_005f37.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Inventory_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f37 = _jspx_th_c_005fout_005f37.doStartTag();
            if (_jspx_th_c_005fout_005f37.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f37);
            _jspx_th_c_005fout_005f37_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f37, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f37_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f57(final JspTag _jspx_th_c_005fif_005f55, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f56 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f57_reused = false;
        try {
            _jspx_th_c_005fif_005f56.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f56.setParent((Tag)_jspx_th_c_005fif_005f55);
            _jspx_th_c_005fif_005f56.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Inventory_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f57 = _jspx_th_c_005fif_005f56.doStartTag();
            if (_jspx_eval_c_005fif_005f57 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f56.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f56.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f56);
            _jspx_th_c_005fif_005f57_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f56, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f57_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f38(final JspTag _jspx_th_c_005fif_005f55, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f38 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f38_reused = false;
        try {
            _jspx_th_c_005fout_005f38.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f38.setParent((Tag)_jspx_th_c_005fif_005f55);
            _jspx_th_c_005fout_005f38.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Inventory_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f38 = _jspx_th_c_005fout_005f38.doStartTag();
            if (_jspx_th_c_005fout_005f38.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f38);
            _jspx_th_c_005fout_005f38_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f38, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f38_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f58(final JspTag _jspx_th_c_005fif_005f55, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f56 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f58_reused = false;
        try {
            _jspx_th_c_005fif_005f56.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f56.setParent((Tag)_jspx_th_c_005fif_005f55);
            _jspx_th_c_005fif_005f56.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Inventory_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f58 = _jspx_th_c_005fif_005f56.doStartTag();
            if (_jspx_eval_c_005fif_005f58 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f56.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f56.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f56);
            _jspx_th_c_005fif_005f58_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f56, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f58_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f59(final JspTag _jspx_th_c_005fif_005f55, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f56 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f59_reused = false;
        try {
            _jspx_th_c_005fif_005f56.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f56.setParent((Tag)_jspx_th_c_005fif_005f55);
            _jspx_th_c_005fif_005f56.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Inventory_Write == null && SELECTEDROLELIST.MDM_Inventory_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f59 = _jspx_th_c_005fif_005f56.doStartTag();
            if (_jspx_eval_c_005fif_005f59 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f56.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f56.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f56);
            _jspx_th_c_005fif_005f59_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f56, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f59_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f39(final JspTag _jspx_th_c_005fif_005f60, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f39 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f39_reused = false;
        try {
            _jspx_th_c_005fout_005f39.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f39.setParent((Tag)_jspx_th_c_005fif_005f60);
            _jspx_th_c_005fout_005f39.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Report_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f39 = _jspx_th_c_005fout_005f39.doStartTag();
            if (_jspx_th_c_005fout_005f39.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f39);
            _jspx_th_c_005fout_005f39_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f39, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f39_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f61(final JspTag _jspx_th_c_005fif_005f60, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f61 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f61_reused = false;
        try {
            _jspx_th_c_005fif_005f61.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f61.setParent((Tag)_jspx_th_c_005fif_005f60);
            _jspx_th_c_005fif_005f61.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Report_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f61 = _jspx_th_c_005fif_005f61.doStartTag();
            if (_jspx_eval_c_005fif_005f61 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f61.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f61.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f61);
            _jspx_th_c_005fif_005f61_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f61, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f61_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f40(final JspTag _jspx_th_c_005fif_005f60, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f40 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f40_reused = false;
        try {
            _jspx_th_c_005fout_005f40.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f40.setParent((Tag)_jspx_th_c_005fif_005f60);
            _jspx_th_c_005fout_005f40.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Report_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f40 = _jspx_th_c_005fout_005f40.doStartTag();
            if (_jspx_th_c_005fout_005f40.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f40);
            _jspx_th_c_005fout_005f40_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f40, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f40_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f62(final JspTag _jspx_th_c_005fif_005f60, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f61 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f62_reused = false;
        try {
            _jspx_th_c_005fif_005f61.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f61.setParent((Tag)_jspx_th_c_005fif_005f60);
            _jspx_th_c_005fif_005f61.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Report_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f62 = _jspx_th_c_005fif_005f61.doStartTag();
            if (_jspx_eval_c_005fif_005f62 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f61.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f61.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f61);
            _jspx_th_c_005fif_005f62_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f61, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f62_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f41(final JspTag _jspx_th_c_005fif_005f60, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f41 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f41_reused = false;
        try {
            _jspx_th_c_005fout_005f41.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f41.setParent((Tag)_jspx_th_c_005fif_005f60);
            _jspx_th_c_005fout_005f41.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Report_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f41 = _jspx_th_c_005fout_005f41.doStartTag();
            if (_jspx_th_c_005fout_005f41.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f41);
            _jspx_th_c_005fout_005f41_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f41, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f41_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f63(final JspTag _jspx_th_c_005fif_005f60, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f61 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f63_reused = false;
        try {
            _jspx_th_c_005fif_005f61.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f61.setParent((Tag)_jspx_th_c_005fif_005f60);
            _jspx_th_c_005fif_005f61.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Report_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f63 = _jspx_th_c_005fif_005f61.doStartTag();
            if (_jspx_eval_c_005fif_005f63 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f61.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f61.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f61);
            _jspx_th_c_005fif_005f63_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f61, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f63_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f64(final JspTag _jspx_th_c_005fif_005f60, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f61 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f64_reused = false;
        try {
            _jspx_th_c_005fif_005f61.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f61.setParent((Tag)_jspx_th_c_005fif_005f60);
            _jspx_th_c_005fif_005f61.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Report_Write == null && SELECTEDROLELIST.MDM_Report_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f64 = _jspx_th_c_005fif_005f61.doStartTag();
            if (_jspx_eval_c_005fif_005f64 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f61.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f61.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f61);
            _jspx_th_c_005fif_005f64_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f61, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f64_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f42(final JspTag _jspx_th_c_005fif_005f65, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f42 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f42_reused = false;
        try {
            _jspx_th_c_005fout_005f42.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f42.setParent((Tag)_jspx_th_c_005fif_005f65);
            _jspx_th_c_005fout_005f42.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Enrollment_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f42 = _jspx_th_c_005fout_005f42.doStartTag();
            if (_jspx_th_c_005fout_005f42.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f42);
            _jspx_th_c_005fout_005f42_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f42, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f42_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f66(final JspTag _jspx_th_c_005fif_005f65, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f66 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f66_reused = false;
        try {
            _jspx_th_c_005fif_005f66.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f66.setParent((Tag)_jspx_th_c_005fif_005f65);
            _jspx_th_c_005fif_005f66.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Enrollment_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f66 = _jspx_th_c_005fif_005f66.doStartTag();
            if (_jspx_eval_c_005fif_005f66 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f66.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f66.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f66);
            _jspx_th_c_005fif_005f66_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f66, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f66_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f43(final JspTag _jspx_th_c_005fif_005f65, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f43 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f43_reused = false;
        try {
            _jspx_th_c_005fout_005f43.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f43.setParent((Tag)_jspx_th_c_005fif_005f65);
            _jspx_th_c_005fout_005f43.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Enrollment_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f43 = _jspx_th_c_005fout_005f43.doStartTag();
            if (_jspx_th_c_005fout_005f43.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f43);
            _jspx_th_c_005fout_005f43_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f43, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f43_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f67(final JspTag _jspx_th_c_005fif_005f65, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f66 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f67_reused = false;
        try {
            _jspx_th_c_005fif_005f66.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f66.setParent((Tag)_jspx_th_c_005fif_005f65);
            _jspx_th_c_005fif_005f66.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Enrollment_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f67 = _jspx_th_c_005fif_005f66.doStartTag();
            if (_jspx_eval_c_005fif_005f67 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f66.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f66.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f66);
            _jspx_th_c_005fif_005f67_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f66, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f67_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f44(final JspTag _jspx_th_c_005fif_005f65, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f44 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f44_reused = false;
        try {
            _jspx_th_c_005fout_005f44.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f44.setParent((Tag)_jspx_th_c_005fif_005f65);
            _jspx_th_c_005fout_005f44.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Enrollment_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f44 = _jspx_th_c_005fout_005f44.doStartTag();
            if (_jspx_th_c_005fout_005f44.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f44);
            _jspx_th_c_005fout_005f44_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f44, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f44_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f68(final JspTag _jspx_th_c_005fif_005f65, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f66 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f68_reused = false;
        try {
            _jspx_th_c_005fif_005f66.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f66.setParent((Tag)_jspx_th_c_005fif_005f65);
            _jspx_th_c_005fif_005f66.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Enrollment_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f68 = _jspx_th_c_005fif_005f66.doStartTag();
            if (_jspx_eval_c_005fif_005f68 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f66.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f66.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f66);
            _jspx_th_c_005fif_005f68_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f66, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f68_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f69(final JspTag _jspx_th_c_005fif_005f65, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f66 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f69_reused = false;
        try {
            _jspx_th_c_005fif_005f66.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f66.setParent((Tag)_jspx_th_c_005fif_005f65);
            _jspx_th_c_005fif_005f66.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Enrollment_Write == null && SELECTEDROLELIST.MDM_Enrollment_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f69 = _jspx_th_c_005fif_005f66.doStartTag();
            if (_jspx_eval_c_005fif_005f69 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f66.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f66.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f66);
            _jspx_th_c_005fif_005f69_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f66, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f69_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f45(final JspTag _jspx_th_c_005fif_005f70, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f45 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f45_reused = false;
        try {
            _jspx_th_c_005fout_005f45.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f45.setParent((Tag)_jspx_th_c_005fif_005f70);
            _jspx_th_c_005fout_005f45.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_ContentMgmt_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f45 = _jspx_th_c_005fout_005f45.doStartTag();
            if (_jspx_th_c_005fout_005f45.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f45);
            _jspx_th_c_005fout_005f45_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f45, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f45_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f71(final JspTag _jspx_th_c_005fif_005f70, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f71 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f71_reused = false;
        try {
            _jspx_th_c_005fif_005f71.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f71.setParent((Tag)_jspx_th_c_005fif_005f70);
            _jspx_th_c_005fif_005f71.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_ContentMgmt_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f71 = _jspx_th_c_005fif_005f71.doStartTag();
            if (_jspx_eval_c_005fif_005f71 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f71.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f71.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f71);
            _jspx_th_c_005fif_005f71_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f71, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f71_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f46(final JspTag _jspx_th_c_005fif_005f70, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f46 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f46_reused = false;
        try {
            _jspx_th_c_005fout_005f46.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f46.setParent((Tag)_jspx_th_c_005fif_005f70);
            _jspx_th_c_005fout_005f46.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_ContentMgmt_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f46 = _jspx_th_c_005fout_005f46.doStartTag();
            if (_jspx_th_c_005fout_005f46.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f46);
            _jspx_th_c_005fout_005f46_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f46, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f46_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f72(final JspTag _jspx_th_c_005fif_005f70, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f71 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f72_reused = false;
        try {
            _jspx_th_c_005fif_005f71.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f71.setParent((Tag)_jspx_th_c_005fif_005f70);
            _jspx_th_c_005fif_005f71.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_ContentMgmt_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f72 = _jspx_th_c_005fif_005f71.doStartTag();
            if (_jspx_eval_c_005fif_005f72 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f71.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f71.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f71);
            _jspx_th_c_005fif_005f72_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f71, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f72_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f47(final JspTag _jspx_th_c_005fif_005f70, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f47 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f47_reused = false;
        try {
            _jspx_th_c_005fout_005f47.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f47.setParent((Tag)_jspx_th_c_005fif_005f70);
            _jspx_th_c_005fout_005f47.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_ContentMgmt_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f47 = _jspx_th_c_005fout_005f47.doStartTag();
            if (_jspx_th_c_005fout_005f47.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f47);
            _jspx_th_c_005fout_005f47_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f47, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f47_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f73(final JspTag _jspx_th_c_005fif_005f70, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f71 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f73_reused = false;
        try {
            _jspx_th_c_005fif_005f71.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f71.setParent((Tag)_jspx_th_c_005fif_005f70);
            _jspx_th_c_005fif_005f71.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_ContentMgmt_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f73 = _jspx_th_c_005fif_005f71.doStartTag();
            if (_jspx_eval_c_005fif_005f73 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f71.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f71.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f71);
            _jspx_th_c_005fif_005f73_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f71, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f73_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f74(final JspTag _jspx_th_c_005fif_005f70, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f71 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f74_reused = false;
        try {
            _jspx_th_c_005fif_005f71.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f71.setParent((Tag)_jspx_th_c_005fif_005f70);
            _jspx_th_c_005fif_005f71.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_ContentMgmt_Write == null && SELECTEDROLELIST.MDM_ContentMgmt_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f74 = _jspx_th_c_005fif_005f71.doStartTag();
            if (_jspx_eval_c_005fif_005f74 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f71.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f71.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f71);
            _jspx_th_c_005fif_005f74_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f71, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f74_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f48(final JspTag _jspx_th_c_005fif_005f75, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f48 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f48_reused = false;
        try {
            _jspx_th_c_005fout_005f48.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f48.setParent((Tag)_jspx_th_c_005fif_005f75);
            _jspx_th_c_005fout_005f48.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_OSUpdateMgmt_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f48 = _jspx_th_c_005fout_005f48.doStartTag();
            if (_jspx_th_c_005fout_005f48.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f48);
            _jspx_th_c_005fout_005f48_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f48, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f48_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f76(final JspTag _jspx_th_c_005fif_005f75, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f76 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f76_reused = false;
        try {
            _jspx_th_c_005fif_005f76.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f76.setParent((Tag)_jspx_th_c_005fif_005f75);
            _jspx_th_c_005fif_005f76.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_OSUpdateMgmt_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f76 = _jspx_th_c_005fif_005f76.doStartTag();
            if (_jspx_eval_c_005fif_005f76 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f76.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f76.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f76);
            _jspx_th_c_005fif_005f76_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f76, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f76_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f49(final JspTag _jspx_th_c_005fif_005f75, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f49 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f49_reused = false;
        try {
            _jspx_th_c_005fout_005f49.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f49.setParent((Tag)_jspx_th_c_005fif_005f75);
            _jspx_th_c_005fout_005f49.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_OSUpdateMgmt_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f49 = _jspx_th_c_005fout_005f49.doStartTag();
            if (_jspx_th_c_005fout_005f49.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f49);
            _jspx_th_c_005fout_005f49_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f49, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f49_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f77(final JspTag _jspx_th_c_005fif_005f75, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f76 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f77_reused = false;
        try {
            _jspx_th_c_005fif_005f76.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f76.setParent((Tag)_jspx_th_c_005fif_005f75);
            _jspx_th_c_005fif_005f76.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_OSUpdateMgmt_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f77 = _jspx_th_c_005fif_005f76.doStartTag();
            if (_jspx_eval_c_005fif_005f77 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f76.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f76.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f76);
            _jspx_th_c_005fif_005f77_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f76, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f77_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f50(final JspTag _jspx_th_c_005fif_005f75, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f50 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f50_reused = false;
        try {
            _jspx_th_c_005fout_005f50.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f50.setParent((Tag)_jspx_th_c_005fif_005f75);
            _jspx_th_c_005fout_005f50.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_OSUpdateMgmt_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f50 = _jspx_th_c_005fout_005f50.doStartTag();
            if (_jspx_th_c_005fout_005f50.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f50);
            _jspx_th_c_005fout_005f50_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f50, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f50_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f78(final JspTag _jspx_th_c_005fif_005f75, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f76 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f78_reused = false;
        try {
            _jspx_th_c_005fif_005f76.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f76.setParent((Tag)_jspx_th_c_005fif_005f75);
            _jspx_th_c_005fif_005f76.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_OSUpdateMgmt_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f78 = _jspx_th_c_005fif_005f76.doStartTag();
            if (_jspx_eval_c_005fif_005f78 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f76.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f76.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f76);
            _jspx_th_c_005fif_005f78_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f76, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f78_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f79(final JspTag _jspx_th_c_005fif_005f75, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f76 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f79_reused = false;
        try {
            _jspx_th_c_005fif_005f76.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f76.setParent((Tag)_jspx_th_c_005fif_005f75);
            _jspx_th_c_005fif_005f76.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_OSUpdateMgmt_Write == null && SELECTEDROLELIST.MDM_OSUpdateMgmt_Read == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f79 = _jspx_th_c_005fif_005f76.doStartTag();
            if (_jspx_eval_c_005fif_005f79 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f76.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f76.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f76);
            _jspx_th_c_005fif_005f79_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f76, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f79_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f51(final JspTag _jspx_th_c_005fif_005f80, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f51 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f51_reused = false;
        try {
            _jspx_th_c_005fout_005f51.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f51.setParent((Tag)_jspx_th_c_005fif_005f80);
            _jspx_th_c_005fout_005f51.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_RemoteControl_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f51 = _jspx_th_c_005fout_005f51.doStartTag();
            if (_jspx_th_c_005fout_005f51.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f51);
            _jspx_th_c_005fout_005f51_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f51, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f51_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f81(final JspTag _jspx_th_c_005fif_005f80, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f81 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f81_reused = false;
        try {
            _jspx_th_c_005fif_005f81.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f81.setParent((Tag)_jspx_th_c_005fif_005f80);
            _jspx_th_c_005fif_005f81.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_RemoteControl_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f81 = _jspx_th_c_005fif_005f81.doStartTag();
            if (_jspx_eval_c_005fif_005f81 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f81.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f81.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f81);
            _jspx_th_c_005fif_005f81_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f81, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f81_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f52(final JspTag _jspx_th_c_005fif_005f80, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f52 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f52_reused = false;
        try {
            _jspx_th_c_005fout_005f52.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f52.setParent((Tag)_jspx_th_c_005fif_005f80);
            _jspx_th_c_005fout_005f52.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_RemoteControl_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f52 = _jspx_th_c_005fout_005f52.doStartTag();
            if (_jspx_th_c_005fout_005f52.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f52);
            _jspx_th_c_005fout_005f52_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f52, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f52_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f82(final JspTag _jspx_th_c_005fif_005f80, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f81 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f82_reused = false;
        try {
            _jspx_th_c_005fif_005f81.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f81.setParent((Tag)_jspx_th_c_005fif_005f80);
            _jspx_th_c_005fif_005f81.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_RemoteControl_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f82 = _jspx_th_c_005fif_005f81.doStartTag();
            if (_jspx_eval_c_005fif_005f82 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f81.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f81.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f81);
            _jspx_th_c_005fif_005f82_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f81, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f82_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f53(final JspTag _jspx_th_c_005fif_005f80, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f53 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f53_reused = false;
        try {
            _jspx_th_c_005fout_005f53.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f53.setParent((Tag)_jspx_th_c_005fif_005f80);
            _jspx_th_c_005fout_005f53.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_RemoteControl_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f53 = _jspx_th_c_005fout_005f53.doStartTag();
            if (_jspx_th_c_005fout_005f53.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f53);
            _jspx_th_c_005fout_005f53_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f53, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f53_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f83(final JspTag _jspx_th_c_005fif_005f80, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f81 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f83_reused = false;
        try {
            _jspx_th_c_005fif_005f81.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f81.setParent((Tag)_jspx_th_c_005fif_005f80);
            _jspx_th_c_005fif_005f81.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_RemoteControl_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f83 = _jspx_th_c_005fif_005f81.doStartTag();
            if (_jspx_eval_c_005fif_005f83 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f81.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f81.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f81);
            _jspx_th_c_005fif_005f83_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f81, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f83_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f84(final JspTag _jspx_th_c_005fif_005f80, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f81 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f84_reused = false;
        try {
            _jspx_th_c_005fif_005f81.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f81.setParent((Tag)_jspx_th_c_005fif_005f80);
            _jspx_th_c_005fif_005f81.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_RemoteControl_Read == null && SELECTEDROLELIST.MDM_RemoteControl_Write == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f84 = _jspx_th_c_005fif_005f81.doStartTag();
            if (_jspx_eval_c_005fif_005f84 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f81.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f81.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f81);
            _jspx_th_c_005fif_005f84_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f81, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f84_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f54(final JspTag _jspx_th_c_005fif_005f85, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f54 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f54_reused = false;
        try {
            _jspx_th_c_005fout_005f54.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f54.setParent((Tag)_jspx_th_c_005fif_005f85);
            _jspx_th_c_005fout_005f54.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Announcement_Admin}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f54 = _jspx_th_c_005fout_005f54.doStartTag();
            if (_jspx_th_c_005fout_005f54.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f54);
            _jspx_th_c_005fout_005f54_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f54, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f54_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f86(final JspTag _jspx_th_c_005fif_005f85, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f86 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f86_reused = false;
        try {
            _jspx_th_c_005fif_005f86.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f86.setParent((Tag)_jspx_th_c_005fif_005f85);
            _jspx_th_c_005fif_005f86.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Announcement_Admin != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f86 = _jspx_th_c_005fif_005f86.doStartTag();
            if (_jspx_eval_c_005fif_005f86 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f86.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f86.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f86);
            _jspx_th_c_005fif_005f86_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f86, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f86_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f55(final JspTag _jspx_th_c_005fif_005f85, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f55 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f55_reused = false;
        try {
            _jspx_th_c_005fout_005f55.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f55.setParent((Tag)_jspx_th_c_005fif_005f85);
            _jspx_th_c_005fout_005f55.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Announcement_Write}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f55 = _jspx_th_c_005fout_005f55.doStartTag();
            if (_jspx_th_c_005fout_005f55.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f55);
            _jspx_th_c_005fout_005f55_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f55, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f55_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f87(final JspTag _jspx_th_c_005fif_005f85, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f86 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f87_reused = false;
        try {
            _jspx_th_c_005fif_005f86.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f86.setParent((Tag)_jspx_th_c_005fif_005f85);
            _jspx_th_c_005fif_005f86.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Announcement_Write != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f87 = _jspx_th_c_005fif_005f86.doStartTag();
            if (_jspx_eval_c_005fif_005f87 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f86.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f86.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f86);
            _jspx_th_c_005fif_005f87_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f86, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f87_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f56(final JspTag _jspx_th_c_005fif_005f85, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f56 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f56_reused = false;
        try {
            _jspx_th_c_005fout_005f56.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f56.setParent((Tag)_jspx_th_c_005fif_005f85);
            _jspx_th_c_005fout_005f56.setValue(PageContextImpl.proprietaryEvaluate("${ROLELIST.MDM_Announcement_Read}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f56 = _jspx_th_c_005fout_005f56.doStartTag();
            if (_jspx_th_c_005fout_005f56.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f56);
            _jspx_th_c_005fout_005f56_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f56, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f56_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f88(final JspTag _jspx_th_c_005fif_005f85, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f86 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f88_reused = false;
        try {
            _jspx_th_c_005fif_005f86.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f86.setParent((Tag)_jspx_th_c_005fif_005f85);
            _jspx_th_c_005fif_005f86.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Announcement_Read != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f88 = _jspx_th_c_005fif_005f86.doStartTag();
            if (_jspx_eval_c_005fif_005f88 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f86.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f86.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f86);
            _jspx_th_c_005fif_005f88_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f86, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f88_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f89(final JspTag _jspx_th_c_005fif_005f85, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f86 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f89_reused = false;
        try {
            _jspx_th_c_005fif_005f86.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f86.setParent((Tag)_jspx_th_c_005fif_005f85);
            _jspx_th_c_005fif_005f86.setTest((boolean)PageContextImpl.proprietaryEvaluate("${SELECTEDROLELIST.MDM_Announcement_Read == null && SELECTEDROLELIST.MDM_Announcement_Write == null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f89 = _jspx_th_c_005fif_005f86.doStartTag();
            if (_jspx_eval_c_005fif_005f89 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" checked ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f86.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f86.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f86);
            _jspx_th_c_005fif_005f89_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f86, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f89_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f90(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f90 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f90_reused = false;
        try {
            _jspx_th_c_005fif_005f90.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f90.setParent((Tag)null);
            _jspx_th_c_005fif_005f90.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isDemoMode == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f90 = _jspx_th_c_005fif_005f90.doStartTag();
            if (_jspx_eval_c_005fif_005f90 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("<input type=\"submit\" value=\"");
                    if (this._jspx_meth_c_005fout_005f57((JspTag)_jspx_th_c_005fif_005f90, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\" id=\"creatte_role\" name=\"creatte_role\" class=\"primaryActionBtn\" />");
                    evalDoAfterBody = _jspx_th_c_005fif_005f90.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f90.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f90);
            _jspx_th_c_005fif_005f90_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f90, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f90_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f57(final JspTag _jspx_th_c_005fif_005f90, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f57 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f57_reused = false;
        try {
            _jspx_th_c_005fout_005f57.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f57.setParent((Tag)_jspx_th_c_005fif_005f90);
            _jspx_th_c_005fout_005f57.setValue(PageContextImpl.proprietaryEvaluate("${DISPLAY}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f57 = _jspx_th_c_005fout_005f57.doStartTag();
            if (_jspx_th_c_005fout_005f57.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f57);
            _jspx_th_c_005fout_005f57_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f57, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f57_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f58(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f58 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f58_reused = false;
        try {
            _jspx_th_c_005fout_005f58.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f58.setParent((Tag)null);
            _jspx_th_c_005fout_005f58.setValue(PageContextImpl.proprietaryEvaluate("${ACTIONFOR}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fout_005f58 = _jspx_th_c_005fout_005f58.doStartTag();
            if (_jspx_th_c_005fout_005f58.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fout_005f58);
            _jspx_th_c_005fout_005f58_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fout_005f58, this._jsp_getInstanceManager(), _jspx_th_c_005fout_005f58_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (RoleForm_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        RoleForm_jsp._jspx_imports_packages.add("javax.servlet.http");
        RoleForm_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        RoleForm_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        RoleForm_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
        RoleForm_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.common.DMApplicationHandler");
    }
}

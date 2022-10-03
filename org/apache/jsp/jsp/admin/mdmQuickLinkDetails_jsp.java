package org.apache.jsp.jsp.admin;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.jasper.el.JspValueExpression;
import org.apache.taglibs.standard.tag.rt.core.SetTag;
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
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import javax.servlet.jsp.tagext.JspTag;
import com.me.mdm.webclient.taglib.MDMProfessionalEditionTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import com.adventnet.i18n.I18N;
import javax.servlet.jsp.tagext.Tag;
import com.me.devicemanagement.framework.webclient.taglib.RoleTag;
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

public final class mdmQuickLinkDetails_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName;
    private TagHandlerPool _005fjspx_005ftagPool_005fMDMEdition_005fProfessional;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return mdmQuickLinkDetails_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return mdmQuickLinkDetails_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return mdmQuickLinkDetails_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = mdmQuickLinkDetails_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fMDMEdition_005fProfessional = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.release();
        this._005fjspx_005ftagPool_005fMDMEdition_005fProfessional.release();
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.release();
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
            final PageContext pageContext = _jspx_page_context = mdmQuickLinkDetails_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("\n\n\n\n<script>\nfunction quickLinkClick(url) {\n    jQuery(document.getElementById('flashQuickLinks')).hide();\n    clearFlashImg();\n    window.location.href = url;\n}\n</script>\n ");
            final RoleTag _jspx_th_RoleManagement_005frole_005f0 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
            boolean _jspx_th_RoleManagement_005frole_005f0_reused = false;
            try {
                _jspx_th_RoleManagement_005frole_005f0.setPageContext(_jspx_page_context);
                _jspx_th_RoleManagement_005frole_005f0.setParent((Tag)null);
                _jspx_th_RoleManagement_005frole_005f0.setroleName("MDM_Settings_Write||MDM_Enrollment_Write||ModernMgmt_Settings_Write||ModernMgmt_Enrollment_Write");
                final int _jspx_eval_RoleManagement_005frole_005f0 = _jspx_th_RoleManagement_005frole_005f0.doStartTag();
                if (_jspx_eval_RoleManagement_005frole_005f0 != 0) {
                    int evalDoAfterBody2;
                    do {
                        out.write("\n <!--No I18N-->\n                                                                                    <h1>");
                        out.print(I18N.getMsg("dc.mdm.msg.device_enroll_link", new Object[0]));
                        out.write("</h1>\n\n\n                                                                                    <ul>\n                                                                                    ");
                        final RoleTag _jspx_th_RoleManagement_005frole_005f2 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
                        boolean _jspx_th_RoleManagement_005frole_005f1_reused = false;
                        try {
                            _jspx_th_RoleManagement_005frole_005f2.setPageContext(_jspx_page_context);
                            _jspx_th_RoleManagement_005frole_005f2.setParent((Tag)_jspx_th_RoleManagement_005frole_005f0);
                            _jspx_th_RoleManagement_005frole_005f2.setroleName("MDM_Settings_Write||MDM_Enrollment_Write");
                            final int _jspx_eval_RoleManagement_005frole_005f2 = _jspx_th_RoleManagement_005frole_005f2.doStartTag();
                            if (_jspx_eval_RoleManagement_005frole_005f2 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n                                                                                        <li>\n                                                                                            <a loadhash=\"true\" sdpkey=\"alt+s\" displaytxt=\"New Solution\" p_tab=\"sdp.header.solutions\" href=javascript:quickLinkClick(\"#/uems/mdm/enrollment/devices/invite/ios/request?fromPage=mdmql\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.mdm.ios", new Object[0]));
                                    out.write(" </a>\n                                                                                        </li><li>\n                                                                                            <a loadhash=\"true\" sdpkey=\"alt+u\" displaytxt=\"New Requester\" p_tab=\"sdp.header.admin\" href=javascript:quickLinkClick(\"#/uems/mdm/enrollment/devices/invite/android/request?type=non-samsung&fromPage=mdmql\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.mdm.android", new Object[0]));
                                    out.write(" </a>\n                                                                                        </li>\n                                                                                    ");
                                    evalDoAfterBody = _jspx_th_RoleManagement_005frole_005f2.doAfterBody();
                                } while (evalDoAfterBody == 2);
                            }
                            if (_jspx_th_RoleManagement_005frole_005f2.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f2);
                            _jspx_th_RoleManagement_005frole_005f1_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f2, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f1_reused);
                        }
                        out.write("\n                                                                                        <li>\n                                                                                            <a loadhash=\"true\" sdpkey=\"alt+j\" displaytxt=\"New Technician\" p_tab=\"sdp.header.admin\" href=javascript:quickLinkClick(\"#/uems/mdm/enrollment/devices/invite/windows/request?fromPage=mdmql\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                        out.print(I18N.getMsg("dc.common.WINDOWS", new Object[0]));
                        out.write(" </a>\n                                                                                        </li>\n                                                                                    </ul>\n                                                                                    <div class=\"clearboth\" style=\"margin-bottom: 8px;\"></div>\n                                                                                ");
                        evalDoAfterBody2 = _jspx_th_RoleManagement_005frole_005f0.doAfterBody();
                    } while (evalDoAfterBody2 == 2);
                }
                if (_jspx_th_RoleManagement_005frole_005f0.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f0);
                _jspx_th_RoleManagement_005frole_005f0_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f0, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f0_reused);
            }
            out.write("\n                                                                                ");
            final RoleTag _jspx_th_RoleManagement_005frole_005f3 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
            boolean _jspx_th_RoleManagement_005frole_005f2_reused = false;
            try {
                _jspx_th_RoleManagement_005frole_005f3.setPageContext(_jspx_page_context);
                _jspx_th_RoleManagement_005frole_005f3.setParent((Tag)null);
                _jspx_th_RoleManagement_005frole_005f3.setroleName("MDM_AppMgmt_Admin||ModernMgmt_AppMgmt_Admin");
                final int _jspx_eval_RoleManagement_005frole_005f3 = _jspx_th_RoleManagement_005frole_005f3.doStartTag();
                if (_jspx_eval_RoleManagement_005frole_005f3 != 0) {
                    int evalDoAfterBody4;
                    do {
                        out.write("\n                                                                                    <h1>");
                        out.print(I18N.getMsg("dc.mdm.group.Distribute_App", new Object[0]));
                        out.write("</h1>\n                                                                                    <ul>\n                                                                                    ");
                        final RoleTag _jspx_th_RoleManagement_005frole_005f4 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
                        boolean _jspx_th_RoleManagement_005frole_005f3_reused = false;
                        try {
                            _jspx_th_RoleManagement_005frole_005f4.setPageContext(_jspx_page_context);
                            _jspx_th_RoleManagement_005frole_005f4.setParent((Tag)_jspx_th_RoleManagement_005frole_005f3);
                            _jspx_th_RoleManagement_005frole_005f4.setroleName("MDM_AppMgmt_Admin");
                            final int _jspx_eval_RoleManagement_005frole_005f4 = _jspx_th_RoleManagement_005frole_005f4.doStartTag();
                            if (_jspx_eval_RoleManagement_005frole_005f4 != 0) {
                                int evalDoAfterBody3;
                                do {
                                    out.write("\n                                                                                        <li>\n                                                                                            <a ignoreQuickLoad=\"true\" loadhash=\"true\" sdpkey=\"alt+s\" displaytxt=\"New Solution\" p_tab=\"sdp.header.solutions\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/appRepo/apps/ios/storeApp?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.conf.syspref.app_Store", new Object[0]));
                                    out.write(" </a>\n                                                                                        </li><li>\n                                                                                            <a ignoreQuickLoad=\"true\" loadhash=\"true\" sdpkey=\"alt+j\" displaytxt=\"New Technician\" p_tab=\"sdp.header.admin\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/appRepo/apps/android/storeApp?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.mdm.inv.allow_android_market", new Object[0]));
                                    out.write(" </a>\n                                                                                        </li>\n                                                                                    </ul>\n                                                                                    <div class=\"clearboth\" style=\"margin-bottom: 8px;\"></div>\n                                                                                    <ul>\n                                                                                        <li>\n                                                                                            <a ignoreQuickLoad=\"true\" loadhash=\"true\" sdpkey=\"alt+u\" displaytxt=\"New Requester\" p_tab=\"sdp.header.admin\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/appRepo/apps/ios/enterprise?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\"> ");
                                    out.print(I18N.getMsg("dm.mdm.ios_enterprise", new Object[0]));
                                    out.write(" </a>\n                                                                                        </li>\n                                                                                        <li>\n                                                                                            <a ignoreQuickLoad=\"true\" sdpkey=\"ctrl+alt+a\" displaytxt=\"New Announcement\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/appRepo/apps/android/enterprise?sourcePage=QL\")>\n                                                                                                <img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dm.mdm.android_enterprise", new Object[0]));
                                    out.write("\n                                                                                            </a>\n                                                                                        </li>\n                                                                                        ");
                                    evalDoAfterBody3 = _jspx_th_RoleManagement_005frole_005f4.doAfterBody();
                                } while (evalDoAfterBody3 == 2);
                            }
                            if (_jspx_th_RoleManagement_005frole_005f4.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f4);
                            _jspx_th_RoleManagement_005frole_005f3_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f4, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f3_reused);
                        }
                        out.write("\n                                                                                        <li>\n                                                                                            <a ignoreQuickLoad=\"true\" loadhash=\"true\" sdpkey=\"alt+j\" displaytxt=\"New Technician\" p_tab=\"sdp.header.admin\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/appRepo/apps/windows/enterprise?sourcePage=QL\")> <img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                        out.print(I18N.getMsg("dm.mdm.windows_enterprise", new Object[0]));
                        out.write(" </a>\n                                                                                        </li>\n                                                                                    </ul>\n                                                                                    <div class=\"clearboth\" style=\"margin-bottom: 8px;\"></div>\n                                                                                ");
                        evalDoAfterBody4 = _jspx_th_RoleManagement_005frole_005f3.doAfterBody();
                    } while (evalDoAfterBody4 == 2);
                }
                if (_jspx_th_RoleManagement_005frole_005f3.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f3);
                _jspx_th_RoleManagement_005frole_005f2_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f3, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f2_reused);
            }
            out.write("\n                                                                                ");
            final RoleTag _jspx_th_RoleManagement_005frole_005f5 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
            boolean _jspx_th_RoleManagement_005frole_005f4_reused = false;
            try {
                _jspx_th_RoleManagement_005frole_005f5.setPageContext(_jspx_page_context);
                _jspx_th_RoleManagement_005frole_005f5.setParent((Tag)null);
                _jspx_th_RoleManagement_005frole_005f5.setroleName("MDM_ContentMgmt_Admin");
                final int _jspx_eval_RoleManagement_005frole_005f5 = _jspx_th_RoleManagement_005frole_005f5.doStartTag();
                if (_jspx_eval_RoleManagement_005frole_005f5 != 0) {
                    int evalDoAfterBody4;
                    do {
                        out.write("\n                                                                                    <h1>");
                        out.print(I18N.getMsg("mdm.content.mgmt", new Object[0]));
                        out.write("</h1>\n                                                                                    <ul>\n                                                                                        <li>\n                                                                                            <a loadhash=\"true\" sdpkey=\"alt+s\" displaytxt=\"New Solution\" p_tab=\"sdp.header.solutions\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/content?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                        out.print(I18N.getMsg("mdm.dist.content", new Object[0]));
                        out.write(" </a>\n                                                                                        </li>\n                                                                                    </ul>\n                                                                                    <div class=\"clearboth\" style=\"margin-bottom: 8px;\"></div>\n                                                                                ");
                        evalDoAfterBody4 = _jspx_th_RoleManagement_005frole_005f5.doAfterBody();
                    } while (evalDoAfterBody4 == 2);
                }
                if (_jspx_th_RoleManagement_005frole_005f5.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f5);
                _jspx_th_RoleManagement_005frole_005f4_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f5, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f4_reused);
            }
            out.write("\n                                                                                ");
            final RoleTag _jspx_th_RoleManagement_005frole_005f6 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
            boolean _jspx_th_RoleManagement_005frole_005f5_reused = false;
            try {
                _jspx_th_RoleManagement_005frole_005f6.setPageContext(_jspx_page_context);
                _jspx_th_RoleManagement_005frole_005f6.setParent((Tag)null);
                _jspx_th_RoleManagement_005frole_005f6.setroleName("MDM_Configurations_Admin||MDM_Settings_Write||ModernMgmt_Configurations_Admin||ModernMgmt_Settings_Write");
                final int _jspx_eval_RoleManagement_005frole_005f6 = _jspx_th_RoleManagement_005frole_005f6.doStartTag();
                if (_jspx_eval_RoleManagement_005frole_005f6 != 0) {
                    int evalDoAfterBody7;
                    do {
                        out.write("\n                                                                                    <div valign=\"top\" class=\"\" id=\"schedulerTD\"></div>\n\n                                                                                    <h1>");
                        out.print(I18N.getMsg("dm.general.create_new", new Object[0]));
                        out.write("</h1>\n                                                                                    ");
                        final RoleTag _jspx_th_RoleManagement_005frole_005f7 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
                        boolean _jspx_th_RoleManagement_005frole_005f6_reused = false;
                        try {
                            _jspx_th_RoleManagement_005frole_005f7.setPageContext(_jspx_page_context);
                            _jspx_th_RoleManagement_005frole_005f7.setParent((Tag)_jspx_th_RoleManagement_005frole_005f6);
                            _jspx_th_RoleManagement_005frole_005f7.setroleName("MDM_Configurations_Admin||ModernMgmt_Configurations_Admin");
                            final int _jspx_eval_RoleManagement_005frole_005f7 = _jspx_th_RoleManagement_005frole_005f7.doStartTag();
                            if (_jspx_eval_RoleManagement_005frole_005f7 != 0) {
                                int evalDoAfterBody6;
                                do {
                                    out.write("\n                                                                                    <ul>\n                                                                                    ");
                                    final RoleTag _jspx_th_RoleManagement_005frole_005f8 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
                                    boolean _jspx_th_RoleManagement_005frole_005f7_reused = false;
                                    try {
                                        _jspx_th_RoleManagement_005frole_005f8.setPageContext(_jspx_page_context);
                                        _jspx_th_RoleManagement_005frole_005f8.setParent((Tag)_jspx_th_RoleManagement_005frole_005f7);
                                        _jspx_th_RoleManagement_005frole_005f8.setroleName("MDM_Configurations_Admin");
                                        final int _jspx_eval_RoleManagement_005frole_005f8 = _jspx_th_RoleManagement_005frole_005f8.doStartTag();
                                        if (_jspx_eval_RoleManagement_005frole_005f8 != 0) {
                                            int evalDoAfterBody5;
                                            do {
                                                out.write("\n                                                                                        <li><a ignoreQuickLoad=\"true\" viewonly=\"true\" loadhash=\"true\" p_tab=\"sdp.header.home\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/profile/ios?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                                out.print(I18N.getMsg("dc.mdm.profile.ios_profile", new Object[0]));
                                                out.write("</a></li>\n                                                                                        <li><a ignoreQuickLoad=\"true\" viewonly=\"true\" loadhash=\"true\" p_tab=\"sdp.header.home\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/profile/android?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                                out.print(I18N.getMsg("dc.mdm.profile.android_profile", new Object[0]));
                                                out.write("</a></li>\n                                                                                       \n                                                                                    ");
                                                evalDoAfterBody5 = _jspx_th_RoleManagement_005frole_005f8.doAfterBody();
                                            } while (evalDoAfterBody5 == 2);
                                        }
                                        if (_jspx_th_RoleManagement_005frole_005f8.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f8);
                                        _jspx_th_RoleManagement_005frole_005f7_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f8, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f7_reused);
                                    }
                                    out.write("\n                                            \n                                                                                        <li><a ignoreQuickLoad=\"true\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/profile/windows?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.mdm.profile.windows_profile", new Object[0]));
                                    out.write("</a></li>\n                                                                                        <li><a ignoreQuickLoad=\"true\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/profile/chrome?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.mdm.profile.chrome_profile", new Object[0]));
                                    out.write("</a></li>\n                                                                                        <li><a ignoreQuickLoad=\"true\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/profile/mac?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.mdm.profile.mac_profile", new Object[0]));
                                    out.write("</a></li>\n                                                                                        <li><a ignoreQuickLoad=\"true\" href=javascript:quickLinkClick(\"#/uems/mdm/manage/profile/tvos?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                    out.print(I18N.getMsg("dc.mdm.profile.tvos_profile", new Object[0]));
                                    out.write("</a></li>\n                            \n                                                                                    </ul><div class=\"clearboth\" style=\"margin-bottom: 8px;\"></div>\n                                                                                    ");
                                    evalDoAfterBody6 = _jspx_th_RoleManagement_005frole_005f7.doAfterBody();
                                } while (evalDoAfterBody6 == 2);
                            }
                            if (_jspx_th_RoleManagement_005frole_005f7.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f7);
                            _jspx_th_RoleManagement_005frole_005f6_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f7, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f6_reused);
                        }
                        out.write("\n                                                                                 ");
                        evalDoAfterBody7 = _jspx_th_RoleManagement_005frole_005f6.doAfterBody();
                    } while (evalDoAfterBody7 == 2);
                }
                if (_jspx_th_RoleManagement_005frole_005f6.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f6);
                _jspx_th_RoleManagement_005frole_005f5_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f6, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f5_reused);
            }
            out.write("\n                                                                                 ");
            final RoleTag _jspx_th_RoleManagement_005frole_005f9 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
            boolean _jspx_th_RoleManagement_005frole_005f8_reused = false;
            try {
                _jspx_th_RoleManagement_005frole_005f9.setPageContext(_jspx_page_context);
                _jspx_th_RoleManagement_005frole_005f9.setParent((Tag)null);
                _jspx_th_RoleManagement_005frole_005f9.setroleName("MDM_Inventory_Admin||MDM_Settings_Write||ModernMgmt_Inventory_Admin||ModernMgmt_Settings_Write");
                final int _jspx_eval_RoleManagement_005frole_005f9 = _jspx_th_RoleManagement_005frole_005f9.doStartTag();
                if (_jspx_eval_RoleManagement_005frole_005f9 != 0) {
                    int evalDoAfterBody11;
                    do {
                        out.write("\n                                                                                    <h1>");
                        out.print(I18N.getMsg("dc.common.CONFIGURE", new Object[0]));
                        out.write(" </h1>\n                                                                                    <ul>\n                                                                                        ");
                        final RoleTag _jspx_th_RoleManagement_005frole_005f10 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
                        boolean _jspx_th_RoleManagement_005frole_005f9_reused = false;
                        try {
                            _jspx_th_RoleManagement_005frole_005f10.setPageContext(_jspx_page_context);
                            _jspx_th_RoleManagement_005frole_005f10.setParent((Tag)_jspx_th_RoleManagement_005frole_005f9);
                            _jspx_th_RoleManagement_005frole_005f10.setroleName("MDM_Inventory_Admin||ModernMgmt_Inventory_Admin");
                            final int _jspx_eval_RoleManagement_005frole_005f10 = _jspx_th_RoleManagement_005frole_005f10.doStartTag();
                            if (_jspx_eval_RoleManagement_005frole_005f10 != 0) {
                                int evalDoAfterBody10;
                                do {
                                    out.write("\n                                                                                        ");
                                    final MDMProfessionalEditionTag _jspx_th_MDMEdition_005fProfessional_005f0 = (MDMProfessionalEditionTag)this._005fjspx_005ftagPool_005fMDMEdition_005fProfessional.get((Class)MDMProfessionalEditionTag.class);
                                    boolean _jspx_th_MDMEdition_005fProfessional_005f0_reused = false;
                                    try {
                                        _jspx_th_MDMEdition_005fProfessional_005f0.setPageContext(_jspx_page_context);
                                        _jspx_th_MDMEdition_005fProfessional_005f0.setParent((Tag)_jspx_th_RoleManagement_005frole_005f10);
                                        final int _jspx_eval_MDMEdition_005fProfessional_005f0 = _jspx_th_MDMEdition_005fProfessional_005f0.doStartTag();
                                        if (_jspx_eval_MDMEdition_005fProfessional_005f0 != 0) {
                                            int evalDoAfterBody9;
                                            do {
                                                out.write("\n                                                                                        <li><a ignorequickload=\"true\" viewonly=\"true\" loadhash=\"true\" p_tab=\"sdp.header.home\" href=javascript:quickLinkClick(\"#/uems/mdm/inventory/scheduleScan\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                                out.print(I18N.getMsg("dc.mdm.inv.schedule_device_scan", new Object[0]));
                                                out.write("</a></li>\n                                                                                        ");
                                                if (this._jspx_meth_c_005fset_005f0((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                                                    return;
                                                }
                                                out.write("\n                                                                                        ");
                                                final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                                                boolean _jspx_th_c_005fif_005f0_reused = false;
                                                try {
                                                    _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
                                                    _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
                                                    _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isGeoTrackingEnabled}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                                                    final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
                                                    if (_jspx_eval_c_005fif_005f0 != 0) {
                                                        int evalDoAfterBody8;
                                                        do {
                                                            out.write("\n                                                                                        <li><a ignoreQuickLoad=\"true\" viewonly=\"true\" loadhash=\"true\" p_tab=\"sdp.header.home\" href=javascript:quickLinkClick(\"#/uems/mdm/inventory/geoTracking\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                                                            out.print(I18N.getMsg("dc.mdm.geoLoc_location_tracking", new Object[0]));
                                                            out.write("</a></li>\n                                                                                        ");
                                                            evalDoAfterBody8 = _jspx_th_c_005fif_005f0.doAfterBody();
                                                        } while (evalDoAfterBody8 == 2);
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
                                                out.write("\n                                                                                        ");
                                                evalDoAfterBody9 = _jspx_th_MDMEdition_005fProfessional_005f0.doAfterBody();
                                            } while (evalDoAfterBody9 == 2);
                                        }
                                        if (_jspx_th_MDMEdition_005fProfessional_005f0.doEndTag() == 5) {
                                            return;
                                        }
                                        this._005fjspx_005ftagPool_005fMDMEdition_005fProfessional.reuse((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
                                        _jspx_th_MDMEdition_005fProfessional_005f0_reused = true;
                                    }
                                    finally {
                                        JspRuntimeLibrary.releaseTag((Tag)_jspx_th_MDMEdition_005fProfessional_005f0, this._jsp_getInstanceManager(), _jspx_th_MDMEdition_005fProfessional_005f0_reused);
                                    }
                                    out.write("\n                                                                                        ");
                                    evalDoAfterBody10 = _jspx_th_RoleManagement_005frole_005f10.doAfterBody();
                                } while (evalDoAfterBody10 == 2);
                            }
                            if (_jspx_th_RoleManagement_005frole_005f10.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f10);
                            _jspx_th_RoleManagement_005frole_005f9_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f10, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f9_reused);
                        }
                        out.write("\n                                                                                    </ul>\n                                                                                    <div class=\"clearboth\" style=\"margin-bottom: 8px;\"></div>\n                                                                                 ");
                        evalDoAfterBody11 = _jspx_th_RoleManagement_005frole_005f9.doAfterBody();
                    } while (evalDoAfterBody11 == 2);
                }
                if (_jspx_th_RoleManagement_005frole_005f9.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f9);
                _jspx_th_RoleManagement_005frole_005f8_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f9, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f8_reused);
            }
            out.write("\n\n\n                                                                                    <h1>");
            out.print(I18N.getMsg("dc.common.VIEW", new Object[0]));
            out.write("</h1>\n                                                                                    <ul>\n                                                                                        ");
            final RoleTag _jspx_th_RoleManagement_005frole_005f11 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
            boolean _jspx_th_RoleManagement_005frole_005f10_reused = false;
            try {
                _jspx_th_RoleManagement_005frole_005f11.setPageContext(_jspx_page_context);
                _jspx_th_RoleManagement_005frole_005f11.setParent((Tag)null);
                _jspx_th_RoleManagement_005frole_005f11.setroleName("MDM_Settings_Read||ModernMgmt_Settings_Read");
                final int _jspx_eval_RoleManagement_005frole_005f11 = _jspx_th_RoleManagement_005frole_005f11.doStartTag();
                if (_jspx_eval_RoleManagement_005frole_005f11 != 0) {
                    int evalDoAfterBody11;
                    do {
                        out.write("\n                                                                                        <li><a viewonly=\"true\" loadhash=\"true\" p_tab=\"sdp.header.home\" href=javascript:quickLinkClick(\"#/uems/mdm/enrollment/devices?fromPage=mdmql\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                        out.print(I18N.getMsg("dm.mdm.enrolled_devices", new Object[0]));
                        out.write("</a></li>\n                                                                                        ");
                        evalDoAfterBody11 = _jspx_th_RoleManagement_005frole_005f11.doAfterBody();
                    } while (evalDoAfterBody11 == 2);
                }
                if (_jspx_th_RoleManagement_005frole_005f11.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f11);
                _jspx_th_RoleManagement_005frole_005f10_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f11, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f10_reused);
            }
            out.write("\n                                                                                        ");
            final RoleTag _jspx_th_RoleManagement_005frole_005f12 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
            boolean _jspx_th_RoleManagement_005frole_005f11_reused = false;
            try {
                _jspx_th_RoleManagement_005frole_005f12.setPageContext(_jspx_page_context);
                _jspx_th_RoleManagement_005frole_005f12.setParent((Tag)null);
                _jspx_th_RoleManagement_005frole_005f12.setroleName("MDM_Report_Read||ModernMgmt_Report_Read");
                final int _jspx_eval_RoleManagement_005frole_005f12 = _jspx_th_RoleManagement_005frole_005f12.doStartTag();
                if (_jspx_eval_RoleManagement_005frole_005f12 != 0) {
                    int evalDoAfterBody5;
                    do {
                        out.write("\n                                                                                        <li><a ignorequickload=\"true\" href=javascript:quickLinkClick(\"#/uems/mdm/reports/predefined?sourcePage=QL\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
                        out.print(I18N.getMsg("dc.common.REPORTS", new Object[0]));
                        out.write("</a></li>\n                                                                                        ");
                        evalDoAfterBody5 = _jspx_th_RoleManagement_005frole_005f12.doAfterBody();
                    } while (evalDoAfterBody5 == 2);
                }
                if (_jspx_th_RoleManagement_005frole_005f12.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.reuse((Tag)_jspx_th_RoleManagement_005frole_005f12);
                _jspx_th_RoleManagement_005frole_005f11_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_RoleManagement_005frole_005f12, this._jsp_getInstanceManager(), _jspx_th_RoleManagement_005frole_005f11_reused);
            }
            out.write("\n                                                                                        <li><a href=javascript:quickLinkClick(\"#/uems/mdm/gettingStarted?source=MDM\")><img src=\"images/arrow-right.png\" align=\"\" width=\"\" height=\"\" style=\"margin: 0px;width: 16px;vertical-align: bottom;padding-right: 2px;\">");
            out.print(I18N.getMsg("desktopcentral.common.Getting_Started", new Object[0]));
            out.write("</a></li>\n\n                                                                                    </ul><div class=\"clearboth\" style=\"margin-bottom: 12px;\"></div>\n\n");
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
            mdmQuickLinkDetails_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_c_005fset_005f0(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final SetTag _jspx_th_c_005fset_005f0 = (SetTag)this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.get((Class)SetTag.class);
        boolean _jspx_th_c_005fset_005f0_reused = false;
        try {
            _jspx_th_c_005fset_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fset_005f0.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fset_005f0.setVar("isGeoTrackingEnabled");
            _jspx_th_c_005fset_005f0.setValue(new JspValueExpression("/jsp/admin/mdmQuickLinkDetails.jsp(93,88) '${MDMUtil.getInstance().isGeoTrackingEnabled()}'", this._jsp_getExpressionFactory().createValueExpression(_jspx_page_context.getELContext(), "${MDMUtil.getInstance().isGeoTrackingEnabled()}", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
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
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (mdmQuickLinkDetails_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/WEB-INF/tlds/dcrole.tld", 1663600462000L);
        mdmQuickLinkDetails_jsp._jspx_dependants.put("/WEB-INF/tlds/mdmLicenseEdition.tld", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        mdmQuickLinkDetails_jsp._jspx_imports_packages.add("javax.servlet.http");
        mdmQuickLinkDetails_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        mdmQuickLinkDetails_jsp._jspx_imports_classes.add("com.adventnet.sym.server.mdm.util.MDMUtil");
    }
}

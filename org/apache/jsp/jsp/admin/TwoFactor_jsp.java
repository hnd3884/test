package org.apache.jsp.jsp.admin;

import java.util.HashSet;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.taglib.RoleTag;
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
import org.apache.taglibs.standard.tag.common.core.OtherwiseTag;
import org.apache.taglibs.standard.tag.rt.core.WhenTag;
import org.apache.taglibs.standard.tag.common.core.ChooseTag;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import javax.servlet.jsp.tagext.JspTag;
import com.adventnet.i18n.I18N;
import javax.servlet.jsp.tagext.Tag;
import com.me.mdm.webclient.taglib.MDMProfessionalEditionTag;
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
import org.apache.jasper.runtime.TagHandlerPool;
import java.util.Set;
import java.util.Map;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.HttpJspBase;

public final class TwoFactor_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005fMDMEdition_005fProfessional;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return TwoFactor_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return TwoFactor_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return TwoFactor_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = TwoFactor_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005fMDMEdition_005fProfessional = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fchoose = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fotherwise = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005fMDMEdition_005fProfessional.release();
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.release();
        this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.release();
        this._005fjspx_005ftagPool_005fc_005fchoose.release();
        this._005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fotherwise.release();
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
            response.setContentType("text/html;charset=UTF-8");
            final PageContext pageContext = _jspx_page_context = TwoFactor_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("<!--$Id$-->\n\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n   \n\n\n\n<script language=\"Javascript\" src=\"js/ajaxRequest.js\" type=\"text/javascript\"></script>\n<script src=\"/framework/javascript/IncludeJS.js?");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\" type=\"text/javascript\"></script>\n<script>includeMainScripts(\"\",\"");
            out.write((String)PageContextImpl.proprietaryEvaluate("${buildnumber}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
            out.write("\");</script>\n");
            final MDMProfessionalEditionTag _jspx_th_MDMEdition_005fProfessional_005f0 = (MDMProfessionalEditionTag)this._005fjspx_005ftagPool_005fMDMEdition_005fProfessional.get((Class)MDMProfessionalEditionTag.class);
            boolean _jspx_th_MDMEdition_005fProfessional_005f0_reused = false;
            try {
                _jspx_th_MDMEdition_005fProfessional_005f0.setPageContext(_jspx_page_context);
                _jspx_th_MDMEdition_005fProfessional_005f0.setParent((Tag)null);
                final int _jspx_eval_MDMEdition_005fProfessional_005f0 = _jspx_th_MDMEdition_005fProfessional_005f0.doStartTag();
                if (_jspx_eval_MDMEdition_005fProfessional_005f0 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n<div id=\"helpDiv\" style=\"position:absolute;\" class=\"hide\">\n    <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\">\n        <tr>\n            <td style=\"background-color:#f9f6c8; border-left:1px solid #e2ae00; border-top:1px solid #e2ae00;\" class=\"bodybold\" width=\"98%\" height=\"10\">\n                <div id=\"draghold\" width=\"100%\"  style=\"cursor: move;\">&nbsp;<span id=\"helpTitle\">");
                        out.print(I18N.getMsg("dc.common.HELP", new Object[0]));
                        out.write(" <span>\n                            </div>\n                            </td>\n                            <td align=\"right\" style=\"background-color:#f9f6c8; border-right:1px solid #e2ae00; border-top:1px solid #e2ae00;\"><a href=\"javascript:closeHelp()\"><img src=\"images/dialogClose.gif\" class=\"padding5\"></a>&nbsp;</td>\n                            <td valign=\"top\" style=\"background-image:url(images/shadow_rightcenter.gif); background-repeat:repeat-y;\"><img src=\"images/shadow_righttop.gif\" width=\"6\"></td>\n                            </tr>\n                            <tr>\n                                <td colspan=\"2\" rowspan=\"2\" style=\"background-color:#f9f6c8; border-left:1px solid #e2ae00; padding:5px; border-bottom:1px solid #e2ae00;border-right:1px solid #e2ae00;\" id=\"helpContent\" class=\"bodytext\" valign=\"top\">\n                                </td>\n                                <td style=\"background-image:url(images/shadow_rightcenter.gif); background-repeat:repeat-y;\">\n                                </td>\n");
                        out.write("                            </tr>\n                            <tr>\n                                <td style=\"background-image:url(images/shadow_rightcenter.gif); background-repeat:repeat-y;\">&nbsp;</td>\n                            </tr>\n                            <tr>\n                                <td width=\"11\" height=\"8\" valign=\"top\" style=\"background-image:url(images/shadow_bottomcenter.gif); background-repeat:repeat-x; \"><img src=\"images/shadow_bottomleft.gif\" width=\"11\" height=\"8\"></td>\n                                <td width=\"605\"  height=\"8\" style=\"background-image:url(images/shadow_bottomcenter.gif); background-repeat:repeat-x; \"></td>\n                                <td height=\"8\" valign=\"top\" ><img src=\"images/shadow_rightbottom.gif\" width=\"6\" height=\"7\"></td>\n                            </tr>\n    </table>\n</div>  \n<div id=\"two_fact\" style=\"width=750px\">\n<form method=\"post\" name=\"twoFactorForm\" action=\"/twoFactorSettings.do\">\n    <table width=\"750px\" style=\"padding:25px 0 5px 0;\" class=\"bodytext\">\n");
                        out.write("        <tr>\n            <td width=\"30%\" align=\"right\" class=\"bodytext\"> ");
                        out.print(I18N.getMsg("dc.mdm.authentication", new Object[0]));
                        out.write(" </td>");
                        out.write("\n\t\t\t<td width=\"10%\">:</td>\n            <td align=\"left\">\n                <label class=\"bodytext\" id=\"enableLabel\"><input type=\"radio\" id = \"enable\" name = \"TWO_FACTOR_ENABLED\" value=\"enable\"");
                        if (this._jspx_meth_c_005fif_005f0((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(" onclick=\"showAuthInfo(this.value);\"/>&nbsp;<span id=\"enable_label\" >");
                        out.print(I18N.getMsg("dc.common.ENABLE", new Object[0]));
                        out.write("</span></label>&nbsp;&nbsp;&nbsp;");
                        out.write("\n                <label class=\"bodytext\" id=\"disableLabel\"><input type=\"radio\" id = \"disable\" name = \"TWO_FACTOR_ENABLED\" value=\"disable\"");
                        if (this._jspx_meth_c_005fif_005f1((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(" onclick=\"showAuthInfo(this.value);\"/>&nbsp;<span id=\"disable_label\">");
                        out.print(I18N.getMsg("dc.common.DISABLE", new Object[0]));
                        out.write("</span></label>");
                        out.write("\n                </td>\n            </tr>\n            <tr><td>&nbsp;</td></tr>\n            <tr id=\"authTypesRow\" ");
                        if (this._jspx_meth_c_005fif_005f2((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">\n            <td width=\"30%\" align=\"right\" class=\"bodytext\">");
                        out.print(I18N.getMsg("mdm.tfa.mode_of_auth", new Object[0]));
                        out.write(" </td>\n\t\t\t<td width=\"10%\">:</td>\n            <td>\n                <label ");
                        if (this._jspx_meth_c_005fif_005f3((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(" id=\"emailLabel\"><input type=\"radio\" name=\"authenticationType\" id=\"email\" value=\"email\" ");
                        if (this._jspx_meth_c_005fif_005f4((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(" onClick=\"enableAuthenticationType(this.value);\" />&nbsp;");
                        out.print(I18N.getMsg("dc.mdm.device_mgmt.email", new Object[0]));
                        out.write("&nbsp;&nbsp;&nbsp;</label>&nbsp;");
                        out.write("\n                <label ");
                        if (this._jspx_meth_c_005fif_005f5((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(" id=\"googleLabel\"><input type=\"radio\" name=\"authenticationType\" id=\"googleApp\" value=\"googleApp\" ");
                        if (this._jspx_meth_c_005fif_005f6((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(" onClick=\"enableAuthenticationType(this.value);\" />&nbsp;");
                        out.print(I18N.getMsg("mdm.tfa.googleauth", new Object[0]));
                        out.write("</label>");
                        out.write("\n                </td>\n                </tr>");
                        out.write("\n\t\t\t\t   <tr id=\"emailInfo\" ");
                        if (this._jspx_meth_c_005fif_005f7((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">\n                <td></td><td></td>\n                <td class=\"infoText\" style=\"white-space:normal;padding-left:5px;padding-top:10px;padding-bottom:10px;\" valign=\"top\">");
                        out.print(I18N.getMsg("mdm.tfa.mail_info", new Object[0]));
                        out.write("</td>\n            <tr/>");
                        out.write("\n            <tr id=\"googleInfo\" ");
                        if (this._jspx_meth_c_005fif_005f8((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">\n                <td></td><td></td>\n                <td valign=\"top\" style=\"white-space:normal;padding-left:5px;padding-top:10px;padding-bottom:10px;\"class=\"infoText\">");
                        out.print(I18N.getMsg("mdm.tfa.google_auth_note", new Object[0]));
                        out.write("\n                </td>\n            <tr/>");
                        out.write("\n   \n        ");
                        final IfTag _jspx_th_c_005fif_005f9 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f9_reused = false;
                        try {
                            _jspx_th_c_005fif_005f9.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f9.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
                            _jspx_th_c_005fif_005f9.setTest((boolean)PageContextImpl.proprietaryEvaluate("${MAIL_SERVER_NOT_CONFIGURED == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f9.doStartTag();
                            if (_jspx_eval_c_005fif_005f9 != 0) {
                                int evalDoAfterBody;
                                do {
                                    out.write("\n            <tr id=\"msg\" ");
                                    if (this._jspx_meth_c_005fif_005f10((JspTag)_jspx_th_c_005fif_005f9, _jspx_page_context)) {
                                        return;
                                    }
                                    out.write(">\n                    <td height=\"40\">&nbsp;</td><td></td>\n                    <td class=\"infoText\" valign=\"top\">&nbsp;");
                                    out.print(I18N.getMsg("dc.pm.msg.mail_server_not_configured.title", new Object[0]));
                                    out.write("</font>\n                    <a href=\"javascript:editmailsettings()\" target=\"_self\">");
                                    out.print(I18N.getMsg("desktopcentral.common.CONFIGURE_NOW", new Object[0]));
                                    out.write("</a></td>\n            <tr/>");
                                    out.write("\t\n        ");
                                    evalDoAfterBody = _jspx_th_c_005fif_005f9.doAfterBody();
                                } while (evalDoAfterBody == 2);
                            }
                            if (_jspx_th_c_005fif_005f9.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f9);
                            _jspx_th_c_005fif_005f9_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f9, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f9_reused);
                        }
                        out.write("\n\n\n<tr id=\"otpInfo\" ");
                        if (this._jspx_meth_c_005fif_005f11((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write("><td></td><td></td><td valign=\"top\" class=\"bodytext\">&nbsp; ");
                        out.print(I18N.getMsg("mdm.tfa.otp_info", new Object[0]));
                        out.write("&nbsp;");
                        out.print(I18N.getMsg("dc.som.som_settings.for", new Object[0]));
                        out.write("&nbsp;\n            <select name=\"otp\" id=\"otp\" class=\"inputNumber\">\n                <option value=\"1\" ");
                        if (this._jspx_meth_c_005fif_005f12((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">1</option>\n                <option value=\"7\" ");
                        if (this._jspx_meth_c_005fif_005f13((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">7</option>\n                <option value=\"15\" ");
                        if (this._jspx_meth_c_005fif_005f14((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">15</option>\n                <option value=\"30\" ");
                        if (this._jspx_meth_c_005fif_005f15((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">30</option>\n                <option value=\"60\" ");
                        if (this._jspx_meth_c_005fif_005f16((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">60</option>\n                <option value=\"120\" ");
                        if (this._jspx_meth_c_005fif_005f17((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">120</option>\n                <option value=\"180\" ");
                        if (this._jspx_meth_c_005fif_005f18((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">180</option>\n                <option value=\"0\" ");
                        if (this._jspx_meth_c_005fif_005f19((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(">0</option></select> &nbsp;\n            ");
                        out.print(I18N.getMsg("dc.som.som_policy.Not_inactive_comp_2", new Object[0]));
                        out.write("\n            &nbsp;\t\t\t<a style='text-decoration: none;' class='tool-tip' id='disable_mail_recent_syncing' href=\"#\" >\n                    <span align=\"left\" style=\"padding:10px;text-align:left;line-height: 20px;width:230px;white-space:normal\">\n                        ");
                        out.print(I18N.getMsg("mdm.tfa.help_text", new Object[0]));
                        out.write("</span>\n                <img align=\"absmiddle\" src=\"/images/help_small.gif\" style=\"height: 17px;width: 17px;padding:0px;\"></a>\n\n        </td>\n            <tr/>\n\n     \n        <tr>\n            <td colspan=\"3\" align=\"center\" class=\"formSubmitBg\" nowrap=\"nowrap\">\n                ");
                        if (this._jspx_meth_c_005fif_005f20((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        final IfTag _jspx_th_c_005fif_005f10 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
                        boolean _jspx_th_c_005fif_005f21_reused = false;
                        try {
                            _jspx_th_c_005fif_005f10.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fif_005f10.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
                            _jspx_th_c_005fif_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isDemoMode == true}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                            final int _jspx_eval_c_005fif_005f10 = _jspx_th_c_005fif_005f10.doStartTag();
                            if (_jspx_eval_c_005fif_005f10 != 0) {
                                int evalDoAfterBody2;
                                do {
                                    out.write("<span class=\"bodyboldred\">");
                                    out.print(I18N.getMsg("dc.common.RUNNING_IN_RESTRICTED_MODE", new Object[0]));
                                    out.write("</span>");
                                    evalDoAfterBody2 = _jspx_th_c_005fif_005f10.doAfterBody();
                                } while (evalDoAfterBody2 == 2);
                            }
                            if (_jspx_th_c_005fif_005f10.doEndTag() == 5) {
                                return;
                            }
                            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f10);
                            _jspx_th_c_005fif_005f21_reused = true;
                        }
                        finally {
                            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f21_reused);
                        }
                        out.write("\n                    <input name=\"Cancel\" type=\"button\" id=\"cancelReport\" class=\"secondaryActionBtn\" onclick=\"renderCurrentView('TwoFactorDetailsView.cc?actionToCall=TwoFactor','tt');\" value=\"Cancel\">\n                </td>\n            </tr>\n        </table>\n    </form>\n\t\n\n    <script>\n\n    function enableAuthenticationType(checkedValue)\n    {\n\n        if (checkedValue == \"email\")\n        {\n            document.getElementById(\"emailLabel\").className = 'bodytext';\n            document.getElementById(\"googleLabel\").className = 'bodytext';\n            document.getElementById(\"googleInfo\").className = \"hide\";\n            document.getElementById(\"emailInfo\").className = \"\";\n            setMsgClass(\"\");\n        } else {\n            document.getElementById(\"googleLabel\").className = 'bodytext';\n            document.getElementById(\"emailLabel\").className = 'bodytext';\n            document.getElementById(\"googleInfo\").className = \"\";\n            document.getElementById(\"emailInfo\").className = \"hide\";\n            setMsgClass(\"hide\");//NO I18N\n");
                        out.write("        }\n    }\n\n    function showAuthInfo(type1)\n    {\n        if (type1 == \"enable\")\n        {\n            document.getElementById(\"authTypesRow\").className = \"\";\n            document.getElementById(\"otpInfo\").className = \"\";\n            if (\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("\" == \"googleApp\")\n            {\n                document.getElementById(\"googleApp\").checked = true;\n                document.getElementById(\"googleInfo\").className = \"\";\n                document.getElementById(\"emailLabel\").className = 'bodytext';\n                document.getElementById(\"googleLabel\").className = 'bodytext';\n            }\n            else\n            {\n                document.getElementById(\"emailLabel\").className = 'bodytext';\n                document.getElementById(\"googleLabel\").className = 'bodytext';\n                document.getElementById(\"email\").checked = true;\n                document.getElementById(\"emailInfo\").className = \"\";\n                setMsgClass(\"\");\n            }\n        } else {\n            document.getElementById(\"authTypesRow\").className = \"hide\";\n            document.getElementById(\"otpInfo\").className = \"hide\";\n            document.getElementById(\"googleInfo\").className = \"hide\";\n            document.getElementById(\"emailInfo\").className = \"hide\";\n            setMsgClass(\"hide\");//NO I18N\n");
                        out.write("        }\n\n    }\n    function setMsgClass(msgClassName)\n    {\n        var mailServerNotConfigured =");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${MAIL_SERVER_NOT_CONFIGURED}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write(";\n        if (mailServerNotConfigured == true)\n        {\n            document.getElementById(\"msg\").className = msgClassName;\n        }\n    }\n\n    function saveTwoFactor()\n    {\n        var isMailNotProvided = \"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${MAIL_NOT_PROVIDED}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write("\"; //No I18N\n        var otp = document.getElementById(\"otp\").value;\n        var authType = \"disabled\";//NO I18N\n        if (document.getElementById(\"enable\").checked)\n            if (document.getElementById(\"enable\").checked)\n            {\n                if (document.getElementById(\"email\").checked)\n                {\n                    if (((document.getElementById(\"msg\")) != null) && ((document.getElementById(\"msg\").style.display) != \"none\"))\n                    {\n                        alertLayer(\"");
                        out.print(I18N.getMsg("desktopcentral.inventory.prohibitedSW.MAIL_SERVER_NOT_CONFIGURED", new Object[0]));
                        out.write("\");\n                        return false;\n                    }\n                    if (isMailNotProvided == \"true\")\n                    {\n                        alertLayer(\"");
                        out.print(I18N.getMsg("mdm.tfa.specify_email", new Object[0]));
                        out.write("\");\n                        return false;\n                    }\n                    var authType = \"mail\";//No I18N\n                }\n                if (document.getElementById(\"googleApp\").checked)\n                {\n                    var authType = \"googleApp\";//No I18N\n                }\n            }\n        var params = \"otp=\" + otp + \"&authType=\" + authType;//No I18N\n        var url = \"/twoFactorSettings.do?actionToCall=modifyTwoFactor\";//No I18N\n        AjaxAPI.sendRequest({URL: url, PARAMETERS: params, ONSUCCESSFUNC: returnTo});\n    }\n    function returnTo(response)\n    {\n        document.getElementById(\"MessageIcon\").src=\"/images/success.gif\";\n        document.getElementById(\"MessageInfoTable\").className=\"sucessboard extrapadding\";//No I18N\n        document.getElementById(\"MessageInfoDiv\").style.display = 'block';\n        hideAfterTimeOut();\n        document.getElementById(\"MessageInfoSpan\").innerHTML = response.getOnlyHtml();\n        changeTabStyle('securitySettings');//No I18N\n    }\n\n    function editmailsettings()\n");
                        out.write("    {\n        // var url1 = 'javascript:getMailServerUrl()';//No i18n\n        // var title = '");
                        out.print(I18N.getMsg("dc.admin.Mail_ServerSettings", new Object[0]));
                        out.write("';\n        // var winParams = 'position=absmiddle,modal=yes,width=575,top=100,left=200,scrollbars=yes,title=' + title;//No i18n\n        // showURLInDialog(url1, winParams);\n        window.location.href = 'javascript:getMailServerUrl();';\n    }\n\n    function showHelp(inElem, from, pos, title)\n    {\n        var helpContent = document.getElementById(inElem).innerHTML;\n        var helpTitle = document.getElementById(\"helpTitle\");\n        msgLeng = helpContent.length\n        var helpDiv = document.getElementById(\"helpDiv\");\n        helpDiv.style.verticalAlign = \"bottom\";//NO I18N\n        document.getElementById(\"helpContent\").innerHTML = helpContent;\n        posX = findPosX(from);\n        posY = findPosY(from);\n        var divWidth = 340;\n        var divHeight = 70;\n        if (msgLeng > 800)\n        {\n            divHeight = 300;\n        } else if (msgLeng > 500)\n        {\n            divHeight = 230;\n        } else if (msgLeng > 150)\n        {\n            divHeight = 140;\n        }\n        if (pos)\n        {\n            helpDiv.style.left = posX + 25\n");
                        out.write("            helpDiv.style.top = posY - parseInt(divHeight + 5);\n        }\n        else\n        {\n            helpDiv.style.left = posX;\n            helpDiv.style.top = posY - parseInt(divHeight + 5);\n        }\n        var posTop = parseInt(helpDiv.style.top)\n        if (posTop < 0)\n        {\n            helpDiv.style.top = 10;\n        }\n        helpDiv.style.height = divHeight;\n        helpDiv.style.width = divWidth;\n        helpDiv.className = \"\";\n        if (typeof title != \"undefined\")\n        {\n            helpTitle.innerHTML = title;\n        }\n        else\n        {\n            helpTitle.innerHTML = \"");
                        out.print(I18N.getMsg("dc.common.HELP", new Object[0]));
                        out.write("\";\n        }\n    }\n</script>\n");
                        if (this._jspx_meth_c_005fset_005f0((JspTag)_jspx_th_MDMEdition_005fProfessional_005f0, _jspx_page_context)) {
                            return;
                        }
                        out.write(10);
                        out.write(32);
                        evalDoAfterBody3 = _jspx_th_MDMEdition_005fProfessional_005f0.doAfterBody();
                    } while (evalDoAfterBody3 == 2);
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
            out.write(10);
            final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
            boolean _jspx_th_c_005fif_005f22_reused = false;
            try {
                _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
                _jspx_th_c_005fif_005f11.setParent((Tag)null);
                _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${showErrorPage != \"false\" }", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
                final int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
                if (_jspx_eval_c_005fif_005f11 != 0) {
                    int evalDoAfterBody3;
                    do {
                        out.write("\n    ");
                        if (this._jspx_meth_c_005fset_005f1((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n    ");
                        out.write(10);
                        out.write(10);
                        out.write(10);
                        out.write(10);
                        out.write(10);
                        out.write("\n\n<style>\n    .paragraph {\n        font: 14px 'Lato', 'Roboto', sans-serif;\n        padding-top: 50px;\n        line-height: 24px;\n    }\n    .title {\n        font: 600 35px 'lato',sans-serif;\n        border: 0px !important;\n        padding: 17px;\n        height: 60px;\n        color: #f44842;\n    }\n\n    p {\n        margin: 0px !important;\n        padding: 0px !important;\n    }\n</style>\n");
                        if (this._jspx_meth_RoleManagement_005frole_005f0((JspTag)_jspx_th_c_005fif_005f11, _jspx_page_context)) {
                            return;
                        }
                        out.write(10);
                        final ChooseTag _jspx_th_c_005fchoose_005f0 = (ChooseTag)this._005fjspx_005ftagPool_005fc_005fchoose.get((Class)ChooseTag.class);
                        boolean _jspx_th_c_005fchoose_005f0_reused = false;
                        try {
                            _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
                            _jspx_th_c_005fchoose_005f0.setParent((Tag)_jspx_th_c_005fif_005f11);
                            final int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
                            if (_jspx_eval_c_005fchoose_005f0 != 0) {
                                int evalDoAfterBody6;
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
                                            int evalDoAfterBody4;
                                            do {
                                                out.write("\n        <div width=\"100%\" align=\"center\" class=\"paragraph\">\n            <p class=\"title\" >");
                                                out.print(I18N.getMsg("desktopcentral.common.access_denied", new Object[0]));
                                                out.write("</p>\n            <p>");
                                                out.print(I18N.getMsg("mdm.admin.license_edition_error", new Object[0]));
                                                out.write("</p>\n\n        </div>\n    ");
                                                evalDoAfterBody4 = _jspx_th_c_005fwhen_005f0.doAfterBody();
                                            } while (evalDoAfterBody4 == 2);
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
                                            int evalDoAfterBody5;
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
                                                if (this._jspx_meth_c_005fif_005f23((JspTag)_jspx_th_c_005fotherwise_005f0, _jspx_page_context)) {
                                                    return;
                                                }
                                                out.write("\n    ");
                                                evalDoAfterBody5 = _jspx_th_c_005fotherwise_005f0.doAfterBody();
                                            } while (evalDoAfterBody5 == 2);
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
                                    evalDoAfterBody6 = _jspx_th_c_005fchoose_005f0.doAfterBody();
                                } while (evalDoAfterBody6 == 2);
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
                        out.write(10);
                        evalDoAfterBody3 = _jspx_th_c_005fif_005f11.doAfterBody();
                    } while (evalDoAfterBody3 == 2);
                }
                if (_jspx_th_c_005fif_005f11.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f11);
                _jspx_th_c_005fif_005f22_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f11, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f22_reused);
            }
            out.write(10);
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
            TwoFactor_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_c_005fif_005f0(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f0_reused = false;
        try {
            _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE != 'disabled'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
            if (_jspx_eval_c_005fif_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
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
    
    private boolean _jspx_meth_c_005fif_005f1(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f1 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f1_reused = false;
        try {
            _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f1.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE == 'disabled'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
            if (_jspx_eval_c_005fif_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f1);
            _jspx_th_c_005fif_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f2(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f2_reused = false;
        try {
            _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f2.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE == 'disabled'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
            if (_jspx_eval_c_005fif_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"hide\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f2.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f2);
            _jspx_th_c_005fif_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f3(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f3 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f3_reused = false;
        try {
            _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f3.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f3.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE == 'mail'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
            if (_jspx_eval_c_005fif_005f3 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"bodytext\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f3.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f3.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f3);
            _jspx_th_c_005fif_005f3_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f3, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f3_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f4(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f4 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f4_reused = false;
        try {
            _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f4.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f4.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE == 'mail'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
            if (_jspx_eval_c_005fif_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("checked");
                    evalDoAfterBody = _jspx_th_c_005fif_005f4.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f4);
            _jspx_th_c_005fif_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f4, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f5(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f5 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f5_reused = false;
        try {
            _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f5.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f5.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE == 'googleApp'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
            if (_jspx_eval_c_005fif_005f5 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"bodytext\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f5.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f5.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f5);
            _jspx_th_c_005fif_005f5_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f5, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f5_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f6(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f6 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f6_reused = false;
        try {
            _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f6.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f6.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE == 'googleApp'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fif_005f7(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f7 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f7_reused = false;
        try {
            _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f7.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f7.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE != 'mail'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
            if (_jspx_eval_c_005fif_005f7 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"hide\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f7.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f7.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f7);
            _jspx_th_c_005fif_005f7_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f7, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f7_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f8(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f8 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f8_reused = false;
        try {
            _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f8.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f8.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE != 'googleApp'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
            if (_jspx_eval_c_005fif_005f8 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"hide\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f8.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f8.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f8);
            _jspx_th_c_005fif_005f8_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f8, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f8_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f10(final JspTag _jspx_th_c_005fif_005f9, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f10 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f10_reused = false;
        try {
            _jspx_th_c_005fif_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f10.setParent((Tag)_jspx_th_c_005fif_005f9);
            _jspx_th_c_005fif_005f10.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE != 'mail'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f10 = _jspx_th_c_005fif_005f10.doStartTag();
            if (_jspx_eval_c_005fif_005f10 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"hide\"");
                    evalDoAfterBody = _jspx_th_c_005fif_005f10.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f10.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f10);
            _jspx_th_c_005fif_005f10_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f10, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f10_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f11(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f11 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f11_reused = false;
        try {
            _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f11.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f11.setTest((boolean)PageContextImpl.proprietaryEvaluate("${TWOFACTORAUTH_TYPE == 'disabled'}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
            if (_jspx_eval_c_005fif_005f11 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("class=\"hide\"\"");
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
    
    private boolean _jspx_meth_c_005fif_005f12(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f12 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f12_reused = false;
        try {
            _jspx_th_c_005fif_005f12.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f12.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f12.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"1\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f12 = _jspx_th_c_005fif_005f12.doStartTag();
            if (_jspx_eval_c_005fif_005f12 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f12.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f12.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f12);
            _jspx_th_c_005fif_005f12_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f12, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f12_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f13(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f13 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f13_reused = false;
        try {
            _jspx_th_c_005fif_005f13.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f13.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f13.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"7\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f13 = _jspx_th_c_005fif_005f13.doStartTag();
            if (_jspx_eval_c_005fif_005f13 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f13.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f13.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f13);
            _jspx_th_c_005fif_005f13_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f13, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f13_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f14(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f14 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f14_reused = false;
        try {
            _jspx_th_c_005fif_005f14.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f14.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f14.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"15\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f14 = _jspx_th_c_005fif_005f14.doStartTag();
            if (_jspx_eval_c_005fif_005f14 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f14.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f14.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f14);
            _jspx_th_c_005fif_005f14_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f14, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f14_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f15(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f15 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f15_reused = false;
        try {
            _jspx_th_c_005fif_005f15.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f15.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f15.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"30\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f15 = _jspx_th_c_005fif_005f15.doStartTag();
            if (_jspx_eval_c_005fif_005f15 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f15.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f15.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f15);
            _jspx_th_c_005fif_005f15_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f15, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f15_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f16(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f16 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f16_reused = false;
        try {
            _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f16.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f16.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"60\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f16 = _jspx_th_c_005fif_005f16.doStartTag();
            if (_jspx_eval_c_005fif_005f16 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
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
    
    private boolean _jspx_meth_c_005fif_005f17(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f17 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f17_reused = false;
        try {
            _jspx_th_c_005fif_005f17.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f17.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f17.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"120\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f17 = _jspx_th_c_005fif_005f17.doStartTag();
            if (_jspx_eval_c_005fif_005f17 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f17.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f17.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f17);
            _jspx_th_c_005fif_005f17_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f17, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f17_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f18(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f18 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f18_reused = false;
        try {
            _jspx_th_c_005fif_005f18.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f18.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f18.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"180\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f18 = _jspx_th_c_005fif_005f18.doStartTag();
            if (_jspx_eval_c_005fif_005f18 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f18.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f18.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f18);
            _jspx_th_c_005fif_005f18_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f18, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f18_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f19(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f19 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f19_reused = false;
        try {
            _jspx_th_c_005fif_005f19.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f19.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f19.setTest((boolean)PageContextImpl.proprietaryEvaluate("${otpTimeout == \"0\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f19 = _jspx_th_c_005fif_005f19.doStartTag();
            if (_jspx_eval_c_005fif_005f19 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f19.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f19.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f19);
            _jspx_th_c_005fif_005f19_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f19, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f19_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f20(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f20 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f20_reused = false;
        try {
            _jspx_th_c_005fif_005f20.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f20.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fif_005f20.setTest((boolean)PageContextImpl.proprietaryEvaluate("${isDemoMode == false}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f20 = _jspx_th_c_005fif_005f20.doStartTag();
            if (_jspx_eval_c_005fif_005f20 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("<input name=\"Save\" type=\"button\" class=\"primaryActionBtn\" id=\"modTwoFactor\" onClick=\"javascript:saveTwoFactor()\" value=\"Save\">");
                    evalDoAfterBody = _jspx_th_c_005fif_005f20.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f20.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f20);
            _jspx_th_c_005fif_005f20_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f20, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f20_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fset_005f0(final JspTag _jspx_th_MDMEdition_005fProfessional_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final SetTag _jspx_th_c_005fset_005f0 = (SetTag)this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.get((Class)SetTag.class);
        boolean _jspx_th_c_005fset_005f0_reused = false;
        try {
            _jspx_th_c_005fset_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fset_005f0.setParent((Tag)_jspx_th_MDMEdition_005fProfessional_005f0);
            _jspx_th_c_005fset_005f0.setVar("showErrorPage");
            _jspx_th_c_005fset_005f0.setValue(new JspValueExpression("/jsp/admin/TwoFactor.jsp(271,0) 'false'", this._jsp_getExpressionFactory().createValueExpression((Object)"false", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
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
    
    private boolean _jspx_meth_c_005fset_005f1(final JspTag _jspx_th_c_005fif_005f22, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final SetTag _jspx_th_c_005fset_005f1 = (SetTag)this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.get((Class)SetTag.class);
        boolean _jspx_th_c_005fset_005f1_reused = false;
        try {
            _jspx_th_c_005fset_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fset_005f1.setParent((Tag)_jspx_th_c_005fif_005f22);
            _jspx_th_c_005fset_005f1.setVar("setFooter");
            _jspx_th_c_005fset_005f1.setValue(new JspValueExpression("/jsp/admin/TwoFactor.jsp(274,4) 'false'", this._jsp_getExpressionFactory().createValueExpression((Object)"false", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            final int _jspx_eval_c_005fset_005f1 = _jspx_th_c_005fset_005f1.doStartTag();
            if (_jspx_th_c_005fset_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fset_005f1);
            _jspx_th_c_005fset_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fset_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fset_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_RoleManagement_005frole_005f0(final JspTag _jspx_th_c_005fif_005f22, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final RoleTag _jspx_th_RoleManagement_005frole_005f0 = (RoleTag)this._005fjspx_005ftagPool_005fRoleManagement_005frole_0026_005froleName.get((Class)RoleTag.class);
        boolean _jspx_th_RoleManagement_005frole_005f0_reused = false;
        try {
            _jspx_th_RoleManagement_005frole_005f0.setPageContext(_jspx_page_context);
            _jspx_th_RoleManagement_005frole_005f0.setParent((Tag)_jspx_th_c_005fif_005f22);
            _jspx_th_RoleManagement_005frole_005f0.setroleName("Common_Write");
            final int _jspx_eval_RoleManagement_005frole_005f0 = _jspx_th_RoleManagement_005frole_005f0.doStartTag();
            if (_jspx_eval_RoleManagement_005frole_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n    ");
                    if (this._jspx_meth_c_005fset_005f2((JspTag)_jspx_th_RoleManagement_005frole_005f0, _jspx_page_context)) {
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
    
    private boolean _jspx_meth_c_005fset_005f2(final JspTag _jspx_th_RoleManagement_005frole_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final SetTag _jspx_th_c_005fset_005f2 = (SetTag)this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.get((Class)SetTag.class);
        boolean _jspx_th_c_005fset_005f2_reused = false;
        try {
            _jspx_th_c_005fset_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fset_005f2.setParent((Tag)_jspx_th_RoleManagement_005frole_005f0);
            _jspx_th_c_005fset_005f2.setVar("isAdmin");
            _jspx_th_c_005fset_005f2.setValue(new JspValueExpression("/jsp/admin/notAuthorizedPage.jsp(26,4) 'true'", this._jsp_getExpressionFactory().createValueExpression((Object)"true", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            final int _jspx_eval_c_005fset_005f2 = _jspx_th_c_005fset_005f2.doStartTag();
            if (_jspx_th_c_005fset_005f2.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.reuse((Tag)_jspx_th_c_005fset_005f2);
            _jspx_th_c_005fset_005f2_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fset_005f2, this._jsp_getInstanceManager(), _jspx_th_c_005fset_005f2_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f23(final JspTag _jspx_th_c_005fotherwise_005f0, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final HttpServletRequest request = (HttpServletRequest)_jspx_page_context.getRequest();
        final HttpServletResponse response = (HttpServletResponse)_jspx_page_context.getResponse();
        final IfTag _jspx_th_c_005fif_005f23 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f23_reused = false;
        try {
            _jspx_th_c_005fif_005f23.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f23.setParent((Tag)_jspx_th_c_005fotherwise_005f0);
            _jspx_th_c_005fif_005f23.setTest((boolean)PageContextImpl.proprietaryEvaluate("${setFooter != \"false\" }", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f23 = _jspx_th_c_005fif_005f23.doStartTag();
            if (_jspx_eval_c_005fif_005f23 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n            <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n                <tr>\n                    <td  colspan=\"3\" bgcolor=\"#FFFFFF\" valign=\"top\"><div class=\"tableheaderbgcolor\"><img src=\"../images/spacer.png\" width=\"2\" height=\"1\"></div>\n                        ");
                    JspRuntimeLibrary.include((ServletRequest)request, (ServletResponse)response, "/jsp/common/footer.jsp", out, false);
                    out.write("</td>\n                </tr>\n            </table>\n        ");
                    evalDoAfterBody = _jspx_th_c_005fif_005f23.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_c_005fif_005f23.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse((Tag)_jspx_th_c_005fif_005f23);
            _jspx_th_c_005fif_005f23_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fif_005f23, this._jsp_getInstanceManager(), _jspx_th_c_005fif_005f23_reused);
        }
        return false;
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (TwoFactor_jsp._jspx_dependants = new HashMap<String, Long>(2)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        TwoFactor_jsp._jspx_dependants.put("/jsp/admin/notAuthorizedPage.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        TwoFactor_jsp._jspx_imports_packages.add("javax.servlet.http");
        TwoFactor_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        TwoFactor_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        TwoFactor_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.common.DMApplicationHandler");
    }
}

package org.apache.jsp.jsp.common;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.taglibs.standard.tag.rt.core.SetTag;
import org.apache.jasper.el.JspValueExpression;
import org.apache.taglibs.standard.tag.rt.core.ForEachTag;
import org.apache.taglibs.standard.tag.rt.core.IfTag;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.PageContextImpl;
import org.apache.taglibs.standard.tag.rt.core.OutTag;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.jsp.SkipPageException;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.Locale;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.common.MDMURLRedirection;
import javax.servlet.jsp.tagext.JspTag;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import javax.servlet.jsp.tagext.Tag;
import com.me.devicemanagement.framework.webclient.taglib.DCMSPTag;
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

public final class onlineDemoMDM_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private TagHandlerPool _005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems;
    private TagHandlerPool _005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return onlineDemoMDM_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return onlineDemoMDM_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return onlineDemoMDM_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = onlineDemoMDM_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody = TagHandlerPool.getTagHandlerPool(this.getServletConfig());
    }
    
    public void _jspDestroy() {
        this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.release();
        this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.release();
        this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
        this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.release();
        this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.release();
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
            final PageContext pageContext = _jspx_page_context = onlineDemoMDM_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write("\n\n\n\n\n\n\n\n");
            out.write(10);
            out.write(10);
            out.write("\n\n\n\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" style=\"height:100%;\">\n<head>\n");
            if (this._jspx_meth_fw_005fmsp_005f0(_jspx_page_context)) {
                return;
            }
            out.write(10);
            if (this._jspx_meth_fw_005fmsp_005f1(_jspx_page_context)) {
                return;
            }
            out.write("\n\n  <!-- Google Tag Manager -->\n  <script>\n    (function(w,d,s,l,i)\n    {\n      w[l]=w[l]||[];\n      w[l].push({'gtm.start':new Date().getTime(),event:'gtm.js'});//No I18N\n      var f=d.getElementsByTagName(s)[0],j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src='//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);\n    })(window,document,'script','dataLayer','GTM-2HQZ');//No I18N\n  </script>\n  <!-- End Google Tag Manager -->\n<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n<link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'>\n<title>Mobile Device Manager Plus - Online Demo</title>");
            out.write("\n<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>\n<script>\n$(function() {\n$('.call-back').click(function() {\n$('.call-back').removeClass(\"active\");\n$(this).addClass(\"active\");\n        $('body').css('cursor','progress');//No I18N\n        $(this).children('a').css('cursor','progress');//No I18N\n    });    \n});\n</script>\n<style type=\"text/css\" media=\"screen\">\n@font-face {\n    font-family: 'Lato';\n    src: url('../../../themes/styles/font/Lato.woff2') format('woff2');\n}\n@font-face {\n   font-family:\"Roboto\";\n   font-weight:400;\n   font-style:normal;\n   src: url(\"https://webfonts.zohowebstatic.com/robotoregular/font.woff\") format(\"woff\");\n}\n.hide{\n\tdisplay:none !important;\n}\n@media screen and (max-width: 1990px) {\n.lang-algn {\n\tpadding-left: 41% !important;\n\tpadding-top: 2% !important;\n}\n.bottom-fixed {\n\tposition: fixed ;\n\twidth: 100%;\n\tbottom: 2% ;\n\tpadding-top:0px ;\n\ttext-align: center;\n}\n}\n@media screen and (max-width: 1440px) {\n\t.bottom-fixed {\n\tposition: relative;\n\twidth: 100%;\n");
            out.write("\tpadding-top:4%;\n\tbottom: 0%;\n\ttext-align: center;\n}\n.title {\n\tpadding-top: 1% !important;\n}\n.top {\n\tpadding-top: 0 !important;\n}\n.call-to-action {\n\tpadding-top: 1% !important;\n}\n.lang-algn {\n\tpadding-left: 38% !important;\n\tpadding-top: 2% !important;\n}\n}\n@media screen and (max-width: 1380px) {\n\t.bottom-fixed {\n\tposition: relative;\n\twidth: 100%;\n\tpadding-top:4%;\n\tbottom: 0%;\n\ttext-align: center;\n}\n.title {\n\tpadding-top: 1% !important;\n}\n.top {\n\tpadding-top: 0 !important;\n}\n.call-to-action {\n\tpadding-top: 0 !important;\n}\n.lang-algn {\n\tpadding-left: 38% !important;\n\tpadding-top: 1% !important;\n}\n}\n@media screen and (max-width: 1100px) {\n\t.bottom-fixed {\n\tposition: relative;\n\twidth: 100%;\n\tpadding-top:4%;\n\tbottom: 0%;\n\ttext-align: center;\n}\n.title {\n\tpadding-top: 1% !important;\n}\n.lang-algn {\n\tpadding-left: 38% !important;\n\tpadding-top: 1% !important;\n}\n.top {\n\tpadding-top: 0 !important;\n}\n.call-to-action {\n\tpadding-top: 0 !important;\n}\n.menu {\n\tpadding: 0px 8px 0px 8px !important;\n\tbackground-position: 9px 230px!important;\n");
            out.write("}\n}\nbody, html {\n\tbackground: #eee url(../../images/demo/white_carbon.png) repeat top left;\n}\n\n/* center */\n.container {\n\tposition: relative;\n}\na {\n\tcolor: #fff;\n\ttext-decoration: none;\n}\nimg, img a, a, a:active, a:visited {\n\tborder: none;\n\toutline: none;\n}\n.clr {\n\tclear: both;\n}\nh1 {\n\tmargin: 0px;\n\tpadding: 20px;\n\tfont-size: 32px;\n\tcolor: #000;\n\ttext-shadow: 1px 1px 1px rgba(255,255,255,0.9);\n\ttext-align: center;\n\tfont-weight: 400;\n}\nh1 span {\n\tdisplay: block;\n\tfont-size: 14px;\n\tcolor: #666;\n\tfont-style: italic;\n\tfont-family: 'Droid Sans', sans-serif;\n\tpadding-top: 5px;\n}\n/* Box Style */\n\n.menu {\n\tpadding: 0px 20px 0px 20px;\n\tmargin: 20px auto;\n\tbackground: url(../images/demo/bottom_shadow.png) no-repeat 21px 220px;\n}\n.img-position {\n\tdisplay: none;\n\tposition: absolute;\n\ttop: 0;\n\tleft: 52px;\n\ttext-align: center;\n\tmargin-top: -22px;\n}\n.menu li:hover .img-position {\n\tdisplay: block;\n}\n.menu li {\n\twidth: 150px;\n\theight: 163px;\n\t-webkit-border-radius: 2px;\n\t-moz-border-radius: 2px;\n\tborder-radius: 2px;\n\tbackground-color: #f9f9f9;\n");
            out.write("\t-webkit-box-shadow: inset 0 0 3px rgba(255,255,255,.75);\n\t-moz-box-shadow: inset 0 0 3px rgba(255,255,255,.75);\n\tbox-shadow: inset 0 0 3px rgba(255,255,255,.75);\n\tborder: solid 1px #eee;\n\toverflow: hidden;\n\tposition: relative;\n\tfloat: left;\n\tmargin-right: 4px;\n\t-webkit-transition: all 300ms linear;\n\t-moz-transition: all 300ms linear;\n\t-o-transition: all 300ms linear;\n\t-ms-transition: all 300ms linear;\n\ttransition: all 300ms linear;\n}\n.menu li:last-child {\n\tmargin-right: 0px;\n}\n.menu li a {\n\ttext-align: left;\n\twidth: 100%;\n\theight: 100%;\n\tdisplay: block;\n\tcolor: #2e9bcd;\n\tposition: relative;\n\t-webkit-transition: height 0.7s; /* For Safari 3.1 to 6.0 */\n\ttransition: height 0.7s;\n}\n.icon {\n\tline-height: 150px;\n\tposition: absolute;\n\twidth: 100%;\n\theight: 60%;\n\tleft: 0px;\n\ttop: -15px;\n\ttext-align: center;\n\t-webkit-transition: all 400ms linear;\n\t-moz-transition: all 400ms linear;\n\t-o-transition: all 400ms linear;\n\t-ms-transition: all 400ms linear;\n\ttransition: all 400ms linear;\n}\n.one, .mspOne {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat 54px 45px;\n");
            out.write("}\n.two {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -58px 42px;\n}\n.three {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -652px 45px;\n}\n.four {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -288px 45px;\n}\n.five {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -1334px 45px;\n}\n.six {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -1167px 45px;\n}\n.mspTwo {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -652px 46px;\n}\n.mspThree {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -767px 46px;\n}\n.main {\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 15px;\n\tfont-weight: 400;\n\tpadding-top: 90px;\n\ttext-align: center;\n\t-webkit-transition: all 200ms linear;\n\t-moz-transition: all 200ms linear;\n\t-o-transition: all 200ms linear;\n\t-ms-transition: all 200ms linear;\n\ttransition: all 200ms linear;\n}\n.sub {\n\ttext-align: center;\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 12px;\n\tfont-weight: 400;\n\tcolor: #fff;\n\tposition: absolute;\n");
            out.write("\tbottom: 0px;\n\twidth: 100%;\n\tleft: 0px;\n\topacity: 0;\n\tfilter: alpha(opacity=0);\n\tletter-spacing: .4px;\n\tline-height: 19px;\n\t-webkit-transition: all 200ms linear;\n\t-moz-transition: all 200ms linear;\n\t-o-transition: all 200ms linear;\n\t-ms-transition: all 200ms linear;\n\ttransition: all 200ms linear;\n}\n.safari .sub{font-size: 13px;}\n.fixed {\n\theight: 47px;\n\tvertical-align: middle;\n\tpadding-top: 24px;\n}\nh3 {\n\tpadding: 10% 0 10% 0;\n}\n.menu li:hover {\n\tbackground-color: #f9f9f9;\n\tz-index: 999;\n\theight: 240px;\n}\n.menu li:hover .icon {\n\tcolor: #2e9bcd;\n\tfont-size: 55px;\n\topacity: 0.2;\n\ttop: -20px;\n}\n.menu li:hover .main {\n\tcolor: #2e9bcd;\n\t-webkit-animation: smallToBig 300ms ease;\n\t-moz-animation: smallToBig 300ms ease;\n\t-ms-animation: smallToBig 300ms ease;\n\tpadding-top: 85px;\n}\n.menu li:hover .sub {\n\topacity: 1;\n\tfilter: alpha(opacity=100);\n\tbackground-color: #2e9bcd;\n\tcolor: #fff;\n\t-webkit-animation: moveFromBottom 500ms ease;\n\t-moz-animation: moveFromBottom 500ms ease;\n\t-ms-animation: moveFromBottom 500ms ease;\n}\n/* other styles */\n");
            out.write(".top {\n\tpadding-top: 0%;\n}\nli {\n\tdisplay: inline-block;\n\tlist-style-type: none;\n}\n.title {\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 25px;\n\tfont-weight: 700;\n\tcolor: #5e6c76;\n\ttext-shadow: 1px 2px 1px rgba(255,255,255,.75);\n\ttext-align: center;\n\tpadding-top: 1%;\n}\n.header {\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 12px;\n\tcolor: #999;\n\tpadding: 0.5% 1% 0.5% 1%;\n\tmin-width: 750px;\n}\n.header a {\n\tcolor: #999;\n}\n.header .left {\n\tfloat: left;\n}\na li:hover {\n\ttext-decoration: underline;\n}\n.shedule-demo-btn .right a:hover, .download-btn .right a:hover {\n\ttext-decoration: underline;\n}\nul a:hover {\n\ttext-decoration: underline;\n}\n.header .right {\n\tfloat: right;\n\tpadding-top: 1.7%;\n}\n.download-text {\n\tpadding: 9px 0px 0px 51px;\n\ttext-align: left;\n}\n.download-text .small {\n\tfont-size: 11px;\n\tpadding-bottom: 4px;\n\tcolor: #fff;\n\tfont-weight: 100;\n}\n.font-change {\n\tfont: 700 15px 'Lato', 'Roboto', sans-serif;\n\tpadding-bottom: 4px\n}\n.call-to-action {\n\tpadding-top: 1%;\n\tletter-spacing: .4px;\n\ttext-align: center;\n");
            out.write("}\n\n.footer-text {\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 14px;\n\tcolor: #888;\n\ttext-align: center;\n}\n.footer-text a {\n\tcolor: #888;\n}\n.footer-text a:hover {\n\ttext-decoration: underline;\n}\n.help-text {\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 12px;\n\tcolor: #b1b1b1;\n\tpadding-top: .5%;\n\ttext-align: center;\n}\n.help-text a {\n\tcolor: #b1b1b1;\n\ttext-decoration: underline;\n}\n.help-text a:hover {\n\tcolor: #b1b1b1;\n\ttext-decoration: none;\n}\n.download-btn {\n\twidth: 220px;\n\theight: 53px;\n\t-webkit-border-radius: 6px;\n\t-moz-border-radius: 6px;\n\tborder-radius: 6px;\n\tbackground-color: #91c547;\n\tdisplay: inline-block;\n\tmargin-right: 30px;\n}\n.download-btn .left {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -953px 4px;\n\twidth: 40px;\n\theight: 40px;\n\tfloat: left;\n}\n.download-btn .right {\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 11px;\n\tcolor: #fff;\n}\n.download-btn .right a {\n\ttext-decoration: none;\n\tcolor: #fff;\n}\n.shedule-demo-btn {\n\twidth: 220px;\n\theight: 53px;\n\t-webkit-border-radius: 6px;\n");
            out.write("\t-moz-border-radius: 6px;\n\tborder-radius: 6px;\n\tbackground-color: #91c547;\n\tdisplay: inline-block;\n}\n.shedule-demo-btn .left {\n\tbackground: url(../../images/demo/demo_icons.png) no-repeat -1066px 5px;\n\twidth: 40px;\n\theight: 40px;\n\tfloat: left;\n}\n.shedule-demo-btn .right {\n\tfont-family: 'Droid Sans', sans-serif;\n\tfont-size: 15px;\n\tfont-weight: 600;\n}\n.shedule-demo-btn .right a {\n\ttext-decoration: none;\n\tcolor: #fff;\n}\n.textfield {\n\tfloat: left;\n\tfont-size: 14px;\n\tline-height: 26px;\n\tfont-family: Droid Sans, 'Lato', sans-serif;\n\tcolor: #5e6c75;\n}\noption {\n\tborder: none;\n}\nselect {\n\tbackground: url(../../images/demo/arrow.png) no-repeat 145px -3px;\n\tpadding: 0px 0 0 12px;\n\tfont-size: 12px;\n\tcolor: #666;\n\tborder: none;\n\t-webkit-appearance: none;\n\twidth: 200px !important;\n\tbackground-color: #f7f7f7;\n\t-moz-appearance: none;\n\tmargin-bottom: 30px;\n\theight: 25px;\n\tborder-radius: 0;\n\toutline:none;\n}\nselect:-moz-focusring {\n    color: transparent;\n    text-shadow: 0 0 0 #000;\n}\n@media screen\\9 {\nselect {\n\tpadding: 2px 0 0 0px;\n");
            out.write("}\n}\n@media \\0screen {\nselect {\n\tpadding: 2px 0 4px 0px;\n}\n}\n@media screen and (min-width:0\\0) {\n select {\npadding: 2px 0 0 0px;\n}\n}\n:root select {\n\tpadding: 2px 0 0 0px \\0/IE9; /* IE9 */\n}\n@-moz-document url-prefix() {\n select {\n padding: 3px 0 0 12px;\n}\n}\n.arrow {\n\twidth: 177px !important;\n\tfloat: left;\n\tborder: 1px solid #ddd;\n\t-webkit-border-radius: 2px;\n\t-moz-border-radius: 2px;\n\tborder-radius: 2px;\n\tpadding: 0px !important;\n\tmargin-bottom: 30px;\n\toverflow: hidden;\n\tbackground-repeat: no-repeat;\n\tbackground-position: 30px 0px;\n\theight: 25px;\n}\n.lang-algn {\n\tpadding-left: 38%;\n\tpadding-top: 2%;\n}\n</style>\n</head>\n<body>\n<!-- Google Tag Manager -->\n<noscript>");
            out.write("\n  <iframe src=\"//www.googletagmanager.com/ns.html?id=GTM-2HQZ\" height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe>\n</noscript>");
            out.write("\n<!-- End Google Tag Manager -->\n");
            final DCMSPTag _jspx_th_fw_005fmsp_005f2 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
            boolean _jspx_th_fw_005fmsp_005f2_reused = false;
            try {
                _jspx_th_fw_005fmsp_005f2.setPageContext(_jspx_page_context);
                _jspx_th_fw_005fmsp_005f2.setParent((Tag)null);
                _jspx_th_fw_005fmsp_005f2.setIsMSP(Boolean.valueOf("false"));
                final int _jspx_eval_fw_005fmsp_005f2 = _jspx_th_fw_005fmsp_005f2.doStartTag();
                if (_jspx_eval_fw_005fmsp_005f2 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n  <form target=\"_parent\" name=\"login\" action=\"");
                        out.print(DMIAMEncoder.encodeHTMLAttribute(response.encodeURL("j_security_check")));
                        out.write("\"  method=\"post\" >\n    <input id=\"j_username\" name=\"j_username\" type=\"hidden\" value=\"admin\" >\n    <input id=\"j_password\" name=\"j_password\" type=\"hidden\" value=\"admin\" >\n\t<input type=\"hidden\" name=\"loginPageCsrfPreventionSalt\" value=\"");
                        if (this._jspx_meth_c_005fout_005f0((JspTag)_jspx_th_fw_005fmsp_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("\"/>\n  </form>\n  <div class=\"header\">\n    <div class=\"left\"><img src=\"../../images/demo/logo_enterpriseMDM.png\" style=\"\n    \t    width: 275px;\"/></div>\n    <div class=\"right\">\n      <ul>\n        <a href=\"");
                        out.print(IAMEncoder.encodeHTMLAttribute(I18N.getMsg(MDMURLRedirection.getURL("get_quote"), new Object[0])));
                        out.write("?demo\" target=\"_blank\">\n        <li>");
                        out.print(I18N.getMsg("desktopcentral.common.get_qoute", new Object[0]));
                        out.write("</li></a> | \n        <a href=\"");
                        out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("forums_url"), new Object[0]));
                        out.write("\" target=\"_blank\">\n        <li>");
                        out.print(I18N.getMsg("desktopcentral.common.forums", new Object[0]));
                        out.write("</li></a> | \n        <a href=\"");
                        out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("blog"), new Object[0]));
                        out.write("\" target=\"_blank\">\n        <li>");
                        out.print(I18N.getMsg("dc.common.BLOGS", new Object[0]));
                        out.write("</li></a> | \n        <a href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/index.html?demo\" target=\"_blank\">\n        <li> ");
                        out.print(I18N.getMsg("desktopcentral.common.Website", new Object[0]));
                        out.write("</li></a> | \n        <a href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/help.html?demo\" target=\"_blank\">\n        <li>");
                        out.print(I18N.getMsg("dc.common.USER_GUIDE", new Object[0]));
                        out.write("</li></a>\n      </ul>\n    </div>\n    <div class=\"clr\"></div>\n  </div>\n   <div style=\"width: 980px;margin: auto;\">\n\n    ");
                        if (this._jspx_meth_c_005fif_005f0((JspTag)_jspx_th_fw_005fmsp_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write(" \n\t\t\t\t\t\t\t\t</div>\n  <div class=\"lang-algn\"><span class=\"textfield\" style=\"padding-right:20px;\">Select Language &nbsp; :</span>");
                        out.write("\n      <div class=\"arrow\">\n          <select id=\"selectedLocale\">\n              ");
                        if (this._jspx_meth_c_005fforEach_005f0((JspTag)_jspx_th_fw_005fmsp_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n          </select>\n      </div>\n  </div>\n  <div class=\"clr\"></div>\n  <div class=\"title\">");
                        out.print(I18N.getMsg("dc.mdm.device_mgmt.mobile_device_management", new Object[0]));
                        out.write("&nbsp;");
                        out.print(I18N.getMsg("dc.common.SOFTWARE", new Object[0]));
                        out.write("</div>\n  <div class=\"top\">\n    <table height=\"280px\" align=\"center\">\n      <tr>\n        <td class=\"menu\"><div>\n            <li class=\"call-back\"><a href=\"javascript:loginCall('admin');\"><span class=\"icon one\"></span>\n              <div class=\"content\">\n                <h2 class=\"main\">");
                        out.print(I18N.getMsg("desktopcentral.admin.usermgmt.role.administrator", new Object[0]));
                        out.write("</h2>");
                        out.write("\n                \n                <h3 class=\"sub\">");
                        out.write("\n                  <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                  ");
                        out.print(I18N.getMsg("mdm.msp.demo.admin_desc", new Object[0]));
                        out.write("</h3>\n                \n              </div>\n              </a></li>\n          </div></td>\n        \n        <td class=\"menu\"><div class=\"\">\n            <li class=\"call-back\"><a href=\"javascript:loginCall('technician');\"> <span class=\"icon three\"></span>\n              <div class=\"content\">\n                <h2 class=\"main\">");
                        out.print(I18N.getMsg("desktopcentral.admin.usermgmt.role.technician", new Object[0]));
                        out.write(" </h2>");
                        out.write("\n                \n                <h3 class=\"sub fixed\">");
                        out.write("\n                  <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                  ");
                        out.print(I18N.getMsg("desktopcentral.common.onlineDemo.roledesc.technician", new Object[0]));
                        out.write("</h3>\n               \n              </div>\n              </a></li>\n          </div></td>\n        <td class=\"menu\"><div class=\"\">\n            <li class=\"call-back\"><a href=\"javascript:loginCall('assetmanager');\"> <span class=\"icon four\"></span>\n              <div class=\"content\">\n                <h2 class=\"main\">");
                        out.print(I18N.getMsg("desktopcentral.admin.usermgmt.role.asset_manager", new Object[0]));
                        out.write("</h2>");
                        out.write("\n                \n                <h3 class=\"sub fixed\">");
                        out.write("\n                  <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                  ");
                        out.print(I18N.getMsg("desktopcentral.common.onlineDemo.roledesc.assetmgr", new Object[0]));
                        out.write("                  \n                </h3>\n              </div>\n              </a></li>\n          </div></td>\n\t\t<td class=\"menu\"><div class=\"\">\n            <li class=\"call-back\"><a href=\"javascript:loginCall('profilemanager');\"> <span class=\"icon five\"></span>\n              <div class=\"content\">\n                <h2 class=\"main\">");
                        out.print(I18N.getMsg("desktopcentral.admin.usermgmt.role.profile_manager", new Object[0]));
                        out.write("</h2>");
                        out.write("\n                \n                <h3 class=\"sub fixed\">");
                        out.write("\n                  <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                  ");
                        out.print(I18N.getMsg("desktopcentral.common.onlineDemo.roledesc.profilemgr", new Object[0]));
                        out.write("                  \n                </h3>\n              </div>\n              </a></li>\n          </div></td>\n\t\t<td class=\"menu\"><div class=\"\">\n            <li class=\"call-back\"><a href=\"javascript:loginCall('appmanager');\"> <span class=\"icon six\"></span>\n              <div class=\"content\">\n                <h2 class=\"main\">");
                        out.print(I18N.getMsg("desktopcentral.admin.usermgmt.role.app_manager", new Object[0]));
                        out.write("</h2>");
                        out.write("\n                \n                <h3 class=\"sub fixed\">");
                        out.write("\n                  <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                  ");
                        out.print(I18N.getMsg("desktopcentral.common.onlineDemo.roledesc.appmgr", new Object[0]));
                        out.write("                  \n                </h3>\n              </div>\n              </a></li>\n          </div></td>\n      </tr>\n    </table>\n  </div>\n  <div class=\"call-to-action\" style=\"\">\n    <div class=\"download-btn\">\n      <div class=\"left\"></div>\n      <div class=\"right\">\n        <div class=\"download-text\">\n          <div class=\"font-change\">");
                        out.print(I18N.getMsg("desktopcentral.common.download_now", new Object[0]));
                        out.write("</div>\n          <div><a target=\"_blank\" href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/download-free.html?p=demo\">");
                        out.print(I18N.getMsg("dc.license.edtion.free_edition", new Object[0]));
                        out.write("</a> | <a target=\"_blank\" href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/download.html?demo\">");
                        out.print(I18N.getMsg("desktopcentral.common.onlineDemo.30daytrial", new Object[0]));
                        out.write("</a> </div>\n        </div>\n      </div>\n      <div class=\"clr\"></div>\n    </div>\n    <a target=\"_blank\" href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/request-demo.html?p=demo\">\n    <div class=\"shedule-demo-btn\">\n      <div class=\"left\"></div>\n      <div class=\"right\">\n        <div class=\"download-text\">\n          <div class=\"small\">");
                        out.print(I18N.getMsg("desktopcentral.common.onlineDemo.scheduledemo1", new Object[0]));
                        out.write("</div>\n          <div class=\"font-change\">");
                        out.print(I18N.getMsg("desktopcentral.common.onlineDemo.scheduledemo2", new Object[0]));
                        out.write(" </div>\n        </div>\n      </div>\n      <div class=\"clr\"></div>\n    </div>\n    </a> </div>\n  <div class=\"bottom-fixed\">\n    <div class=\"footer-text\"> <a href=\"mailto:");
                        out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
                        out.write(34);
                        out.write(62);
                        out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
                        out.write("</a> |  US: +1 888 720 9500 / +1 800 443 6694 | Intl: +1 925 924 9500 | Australia: +1 800 631 268 | UK: 0800 028 6590</div>");
                        out.write("\n    <div class=\"help-text\">");
                        out.print(I18N.getMsg("dc.admin.rebranding.copyright", new Object[0]));
                        out.write(" &copy; ");
                        if (this._jspx_meth_c_005fout_005f2((JspTag)_jspx_th_fw_005fmsp_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write(" <a href=\"");
                        if (this._jspx_meth_c_005fout_005f3((JspTag)_jspx_th_fw_005fmsp_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("\" target=\"_blank\">");
                        if (this._jspx_meth_c_005fout_005f4((JspTag)_jspx_th_fw_005fmsp_005f2, _jspx_page_context)) {
                            return;
                        }
                        out.write("</a>&nbsp;");
                        out.print(I18N.getMsg("desktopcentral.common.login.all_rights_reserved", new Object[0]));
                        out.write("</div>");
                        out.write(10);
                        evalDoAfterBody = _jspx_th_fw_005fmsp_005f2.doAfterBody();
                    } while (evalDoAfterBody == 2);
                }
                if (_jspx_th_fw_005fmsp_005f2.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f2);
                _jspx_th_fw_005fmsp_005f2_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f2, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f2_reused);
            }
            out.write("\n    ");
            final DCMSPTag _jspx_th_fw_005fmsp_005f3 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
            boolean _jspx_th_fw_005fmsp_005f3_reused = false;
            try {
                _jspx_th_fw_005fmsp_005f3.setPageContext(_jspx_page_context);
                _jspx_th_fw_005fmsp_005f3.setParent((Tag)null);
                _jspx_th_fw_005fmsp_005f3.setIsMSP(Boolean.valueOf("true"));
                final int _jspx_eval_fw_005fmsp_005f3 = _jspx_th_fw_005fmsp_005f3.doStartTag();
                if (_jspx_eval_fw_005fmsp_005f3 != 0) {
                    int evalDoAfterBody2;
                    do {
                        out.write("\n  <form target=\"_parent\" name=\"login\" action=\"");
                        out.print(response.encodeURL("j_security_check"));
                        out.write("\"  method=\"post\" >\n    <input id=\"j_username\" name=\"j_username\" type=\"hidden\" value=\"admin\" >\n    <input id=\"j_password\" name=\"j_password\" type=\"hidden\" value=\"admin\" >\n\t<input type=\"hidden\" name=\"loginPageCsrfPreventionSalt\" value=\"");
                        if (this._jspx_meth_c_005fout_005f5((JspTag)_jspx_th_fw_005fmsp_005f3, _jspx_page_context)) {
                            return;
                        }
                        out.write("\"/>\n  </form>\n  <div class=\"header\">\n    <div class=\"left\"><img src=\"../../images/dm-default/dc-logo.gif\" style=\"width: 330px;\"/></div>\n    <div class=\"right\">\n      <ul>\n        <a href=\"");
                        out.print(IAMEncoder.encodeHTMLAttribute(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), MDMURLRedirection.getURL("get_quote"), new Object[0])));
                        out.write("?demo\" target=\"_blank\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.get_qoute", new Object[0]));
                        out.write("</a> | <a href=\"");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), ProductUrlLoader.getInstance().getValue("forums_url"), new Object[0]));
                        out.write("\" target=\"_blank\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.forums", new Object[0]));
                        out.write("</a> | <a href=\"http://blogs.manageengine.com/desktopcentral/\" target=\"_blank\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.BLOGS", new Object[0]));
                        out.write("</a> | <a href=\"");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), ProductUrlLoader.getInstance().getValue("desktopcentral_today"), new Object[0]));
                        out.write("?demo\" target=\"_blank\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.Website", new Object[0]));
                        out.write("</a> | <a href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/demo/desktop-management-videos.html?demo\" target=\"_blank\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.VIDEOS", new Object[0]));
                        out.write(" </a> | <a href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/help.html?demo\" target=\"_blank\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.common.USER_GUIDE", new Object[0]));
                        out.write("</a>\n      </ul>\n    </div>\n    <div class=\"clr\"></div>\n  </div>\n  <div class=\"lang-algn\"><span class=\"textfield\" style=\"padding-right:20px;\">Select Language &nbsp; :</span>");
                        out.write("\n      <div class=\"arrow\">\n          ");
                        if (this._jspx_meth_c_005fset_005f0((JspTag)_jspx_th_fw_005fmsp_005f3, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n          <select id=\"selectedLocale\">\n              ");
                        if (this._jspx_meth_c_005fforEach_005f1((JspTag)_jspx_th_fw_005fmsp_005f3, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n          </select>\n      </div>\n  </div>\n  <div class=\"clr\"></div>\n  <div class=\"title\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "mdm.onlineDemo.mspcaption", new Object[0]));
                        out.write("</div>\n  <div class=\"top\">\n      <table height=\"280px\" align=\"center\">\n          <tr>\n              <td class=\"menu\"><div>\n                      <li class=\"call-back\"><a href=\"javascript:loginCall('admin');\"><span class=\"icon mspOne\"></span>\n                              <div class=\"content\">\n                                  <h2 class=\"main\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "mdm.onlineDemo.MSPAdministrator", new Object[0]));
                        out.write(" </h2>");
                        out.write("\n\n                                  <h3 class=\"sub\">");
                        out.write("\n                                      <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                                      ");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "mdm.msp.demo.admin_desc", new Object[0]));
                        out.write("</h3>\n                                </div>\n                          </a></li>\n                  </div></td>\n\n              <td class=\"menu\"><div class=\"\">\n                      <li class=\"call-back\"><a href=\"javascript:loginCall('technician');\"> <span class=\"icon mspTwo\"></span>\n                              <div class=\"content\">\n                                  <h2 class=\"main\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "mdm.onlineDemo.MSPTechnician", new Object[0]));
                        out.write(" </h2>");
                        out.write("\n\n                                  <h3 class=\"sub fixed\">");
                        out.write("\n                                      <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                                      ");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "mdm.onlineDemo.roledesc.msptech", new Object[0]));
                        out.write("</h3>\n\n                              </div>\n                          </a></li>\n                  </div></td>\n              <td class=\"menu\"><div class=\"\">\n                      <li class=\"call-back\"><a href=\"javascript:loginCall('customer');\"> <span class=\"icon mspThree\"></span>\n                              <div class=\"content\">\n                                  <h2 class=\"main\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.customer", new Object[0]));
                        out.write("</h2>");
                        out.write("\n\n                                  <h3 class=\"sub fixed\">");
                        out.write("\n                                      <div class=\"img-position\"><img src=\"../../images/demo/hover.png\" /></div>\n                                      ");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "mdm.onlineDemo.roledesc.mspcustomer", new Object[0]));
                        out.write(" </h3>\n\n                              </div>\n                          </a></li>\n                  </div></td>\n          </tr>\n      </table>\n  </div>\n  <div class=\"call-to-action\" style=\"\">\n    <div class=\"download-btn\">\n      <div class=\"left\"></div>\n      <div class=\"right\">\n        <div class=\"download-text\">\n          <div class=\"font-change\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.download_now", new Object[0]));
                        out.write("</div>\n          <div><a target=\"_blank\" href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/download-free.html?demo\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "dc.license.edtion.free_edition", new Object[0]));
                        out.write("</a> | <a target=\"_blank\" href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/download.html?demo\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.onlineDemo.30daytrial", new Object[0]));
                        out.write("</a> </div>\n        </div>\n      </div>\n      <div class=\"clr\"></div>");
                        out.write("\n    </div>\n    <a target=\"_blank\" href=\"");
                        out.print(ProductUrlLoader.getInstance().getValue("mdmUrl"));
                        out.write("/request-demo.html?demo\">\n    <div class=\"shedule-demo-btn\">");
                        out.write("\n      <div class=\"left\"></div>");
                        out.write("\n      <div class=\"right\">");
                        out.write("\n        <div class=\"download-text\"> ");
                        out.write("\n          <div class=\"small\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.onlineDemo.scheduledemo1", new Object[0]));
                        out.write("</div>\n          <div class=\"font-change\">");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), "desktopcentral.common.onlineDemo.scheduledemo2", new Object[0]));
                        out.write("</div>\n        </div>\n      </div>\n      <div class=\"clr\"></div>");
                        out.write("\n    </div>\n    </a> </div>\n  <div class=\"bottom-fixed\">");
                        out.write("\n    <div class=\"footer-text\"> <a href=\"mailto:");
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
                        out.write(34);
                        out.write(62);
                        out.print(I18NUtil.getMsgFromLocale((Locale)pageContext.findAttribute("browserLocale"), ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]));
                        out.write("</a>");
                        out.write("\n      \n      |  US: +1 888 720 9500 / +1 800 443 6694 | Intl: +1 925 924 9500 | Australia: +1 800 631 268 | UK: 0800 028 6590</div>");
                        out.write("\n    \n    <div class=\"help-text\">Best viewed in IE 7.0 & above, Mozilla Firefox 3.6 & above, at a Screen Resolution of 1024 X 768 pixels. &copy; ");
                        out.write("\n      ");
                        if (this._jspx_meth_fw_005fmsp_005f4((JspTag)_jspx_th_fw_005fmsp_005f3, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n      ");
                        if (this._jspx_meth_fw_005fmsp_005f5((JspTag)_jspx_th_fw_005fmsp_005f3, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n      ,&nbsp;<a href=\"");
                        if (this._jspx_meth_c_005fout_005f9((JspTag)_jspx_th_fw_005fmsp_005f3, _jspx_page_context)) {
                            return;
                        }
                        out.write("\" target=\"_blank\">\n      ");
                        if (this._jspx_meth_c_005fout_005f10((JspTag)_jspx_th_fw_005fmsp_005f3, _jspx_page_context)) {
                            return;
                        }
                        out.write("\n      </a></div>\n  </div>");
                        out.write(10);
                        evalDoAfterBody2 = _jspx_th_fw_005fmsp_005f3.doAfterBody();
                    } while (evalDoAfterBody2 == 2);
                }
                if (_jspx_th_fw_005fmsp_005f3.doEndTag() == 5) {
                    return;
                }
                this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f3);
                _jspx_th_fw_005fmsp_005f3_reused = true;
            }
            finally {
                JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f3, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f3_reused);
            }
            out.write("\n</body>\n</html>\n<script language=\"JavaScript\" type=\"text/JavaScript\" >\n\nfunction checkForNull()\n{\n\tvar browser = checkBrowser();\n\tif(browser == true)\n\t{\n\t\tvar browserInfo = \"");
            out.print(I18N.getMsg("dc.onlinedemo.broswer_not_supported", new Object[0]));
            out.write("\";\n\t\talert(browserInfo);\n\t}\n\t//storing location hash in localstorage for ember pages to get loaded after login\n\t//this is done because hash is not retained after login\n\twindow.localStorage.setItem(\"dcEmberURL\", window.location.hash); //No I18N\n\t\n\tdocument.login.submit();\n}\nfunction loginCall(userName)\n{\n\n    userName = userName+\"_\"+document.getElementById(\"selectedLocale\").value;\n    userName = userName.toLowerCase();\n   \n    allUserLogin(userName);\n}\nfunction allUserLogin(userName)\n{\n\tdocument.getElementById(\"j_username\").value = userName;\n\tdocument.getElementById(\"j_password\").value = userName;\n\tcheckForNull();\n}\nfunction checkBrowser()\n{\n\tvar userAgent = navigator.userAgent.toLowerCase();\n\tif(userAgent.match(\"msie\") == \"msie\")\n\t{\n\t\tbrowser = \"internet explorer\";//No I18N\n\t\tif(browser==\"internet explorer\")\n\t\t{\n\t\t\tversion = userAgent.substring(userAgent.indexOf(\"msie\")+4,userAgent.lastIndexOf(\";\"));\n\t\t\tvar ver =parseFloat(version);\n\t\t\tif(browser == \"internet explorer\" && ver < 5.5)\n\t\t\t{\n\t\t\t\treturn true;\n\t\t\t}\n");
            out.write("\t\t}\n\t}\n\telse if( userAgent.match(\"netscape\") == \"netscape\")\n\t{\n\t\tbrowser=\"Netscape\";//No I18N\n\t\tif(browser==\"Netscape\")\n\t\t{\n\t\t\tversion = userAgent.substring(userAgent.indexOf(\"netscape\")+4,userAgent.lastIndexOf(\";\"));\n\t\t\tvar ver =parseFloat(version);\n\t\t\tif(browser == \"Netscape\" && version < 7.0)\n\t\t\t{\n\t\t\t\treturn true;\n\t\t\t}\n\t\t}\n\t}\n\telse if(userAgent.match(\"mozilla\") == \"mozilla\")\n\t{\n\n\t\tbrowser = \"mozilla\";//No I18N\n\t\tif(browser==\"mozilla\")\n\t\t{\n\t\t\tversion = userAgent.substring(userAgent.indexOf(\"rv:\")+3,userAgent.indexOf(\")\"));\n\t\t\tvar ver = parseFloat(version);\n\t\t\tif(browser == \"mozilla\" && ver < 1.5)\n\t\t\t{\n\t\t\t\treturn true;\n\t\t\t}\n\t\t}\n\t}\n\telse\n\t{\n\t\treturn false;\n\t}\n}\n\nfunction open_download_page(){\n\n        window.open(\"");
            out.print(I18N.getMsg(ProductUrlLoader.getInstance().getValue("download"), new Object[0]));
            out.write("\");\n}\nif (navigator.userAgent.indexOf('Safari') != -1 && \n    navigator.userAgent.indexOf('Chrome') == -1) {\n        document.body.className += \" safari\";//No I18N\n    }\n</script>\n");
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
            onlineDemoMDM_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    private boolean _jspx_meth_fw_005fmsp_005f0(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCMSPTag _jspx_th_fw_005fmsp_005f0 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
        boolean _jspx_th_fw_005fmsp_005f0_reused = false;
        try {
            _jspx_th_fw_005fmsp_005f0.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fmsp_005f0.setParent((Tag)null);
            _jspx_th_fw_005fmsp_005f0.setIsMSP(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fmsp_005f0 = _jspx_th_fw_005fmsp_005f0.doStartTag();
            if (_jspx_eval_fw_005fmsp_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n\t<meta name=\"google-site-verification\" content=\"Hp12PF1JN8ZfI9W9mJx4OmoAWggvsRjLYS77seUDZ5U\" />\n");
                    evalDoAfterBody = _jspx_th_fw_005fmsp_005f0.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fmsp_005f0.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f0);
            _jspx_th_fw_005fmsp_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f0, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_fw_005fmsp_005f1(final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCMSPTag _jspx_th_fw_005fmsp_005f1 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
        boolean _jspx_th_fw_005fmsp_005f1_reused = false;
        try {
            _jspx_th_fw_005fmsp_005f1.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fmsp_005f1.setParent((Tag)null);
            _jspx_th_fw_005fmsp_005f1.setIsMSP(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fmsp_005f1 = _jspx_th_fw_005fmsp_005f1.doStartTag();
            if (_jspx_eval_fw_005fmsp_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n  <meta name=\"google-site-verification\" content=\"OJVgr-B4_lYxW0ls41TxItMm3bXHxxcMWZ3-IE7GE8I\" />\n");
                    evalDoAfterBody = _jspx_th_fw_005fmsp_005f1.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fmsp_005f1.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f1);
            _jspx_th_fw_005fmsp_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f1, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f0(final JspTag _jspx_th_fw_005fmsp_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f0 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f0_reused = false;
        try {
            _jspx_th_c_005fout_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f0.setParent((Tag)_jspx_th_fw_005fmsp_005f2);
            _jspx_th_c_005fout_005f0.setValue(PageContextImpl.proprietaryEvaluate("${loginPageCsrfPreventionSalt}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fif_005f0(final JspTag _jspx_th_fw_005fmsp_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final HttpServletRequest request = (HttpServletRequest)_jspx_page_context.getRequest();
        final HttpServletResponse response = (HttpServletResponse)_jspx_page_context.getResponse();
        final IfTag _jspx_th_c_005fif_005f0 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f0_reused = false;
        try {
            _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f0.setParent((Tag)_jspx_th_fw_005fmsp_005f2);
            _jspx_th_c_005fif_005f0.setTest((boolean)PageContextImpl.proprietaryEvaluate("${flashShowStatus != null}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
            if (_jspx_eval_c_005fif_005f0 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n                                    <div id=\"flashNewsMessage\">\n                                         ");
                    JspRuntimeLibrary.include((ServletRequest)request, (ServletResponse)response, "/images/flashmsg/flashMsg.html", out, false);
                    out.write("\n                                    </div>\n                                ");
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
    
    private boolean _jspx_meth_c_005fforEach_005f0(final JspTag _jspx_th_fw_005fmsp_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        JspWriter out = _jspx_page_context.getOut();
        final ForEachTag _jspx_th_c_005fforEach_005f0 = (ForEachTag)this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.get((Class)ForEachTag.class);
        boolean _jspx_th_c_005fforEach_005f0_reused = false;
        try {
            _jspx_th_c_005fforEach_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fforEach_005f0.setParent((Tag)_jspx_th_fw_005fmsp_005f2);
            _jspx_th_c_005fforEach_005f0.setVar("val");
            _jspx_th_c_005fforEach_005f0.setItems(new JspValueExpression("/jsp/common/onlineDemoMDM.jsp(592,14) '${LOCALES}'", this._jsp_getExpressionFactory().createValueExpression(_jspx_page_context.getELContext(), "${LOCALES}", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            _jspx_th_c_005fforEach_005f0.setVarStatus("status");
            final int[] _jspx_push_body_count_c_005fforEach_005f0 = { 0 };
            try {
                final int _jspx_eval_c_005fforEach_005f0 = _jspx_th_c_005fforEach_005f0.doStartTag();
                if (_jspx_eval_c_005fforEach_005f0 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n                  <option value=\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${val.key}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write(34);
                        out.write(32);
                        if (this._jspx_meth_c_005fif_005f1((JspTag)_jspx_th_c_005fforEach_005f0, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f0)) {
                            return true;
                        }
                        out.write(32);
                        out.write(62);
                        out.write(32);
                        if (this._jspx_meth_c_005fout_005f1((JspTag)_jspx_th_c_005fforEach_005f0, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f0)) {
                            return true;
                        }
                        out.write("</option>                                                 \t\n              ");
                        evalDoAfterBody = _jspx_th_c_005fforEach_005f0.doAfterBody();
                    } while (evalDoAfterBody == 2);
                }
                if (_jspx_th_c_005fforEach_005f0.doEndTag() == 5) {
                    return true;
                }
            }
            catch (final Throwable _jspx_exception) {
                while (_jspx_push_body_count_c_005fforEach_005f0[0]-- > 0) {
                    out = _jspx_page_context.popBody();
                }
                _jspx_th_c_005fforEach_005f0.doCatch(_jspx_exception);
            }
            finally {
                _jspx_th_c_005fforEach_005f0.doFinally();
            }
            this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.reuse((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fforEach_005f0_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fforEach_005f0, this._jsp_getInstanceManager(), _jspx_th_c_005fforEach_005f0_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f1(final JspTag _jspx_th_c_005fforEach_005f0, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f0) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f1 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f1_reused = false;
        try {
            _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f1.setParent((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fif_005f1.setTest((boolean)PageContextImpl.proprietaryEvaluate("${val.key == \"en_US\"}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
            if (_jspx_eval_c_005fif_005f1 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
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
    
    private boolean _jspx_meth_c_005fout_005f1(final JspTag _jspx_th_c_005fforEach_005f0, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f0) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f1 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f1_reused = false;
        try {
            _jspx_th_c_005fout_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f1.setParent((Tag)_jspx_th_c_005fforEach_005f0);
            _jspx_th_c_005fout_005f1.setValue(PageContextImpl.proprietaryEvaluate("${val.value}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f2(final JspTag _jspx_th_fw_005fmsp_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f2 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f2_reused = false;
        try {
            _jspx_th_c_005fout_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f2.setParent((Tag)_jspx_th_fw_005fmsp_005f2);
            _jspx_th_c_005fout_005f2.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.copyright_year}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f3(final JspTag _jspx_th_fw_005fmsp_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f3 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f3_reused = false;
        try {
            _jspx_th_c_005fout_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f3.setParent((Tag)_jspx_th_fw_005fmsp_005f2);
            _jspx_th_c_005fout_005f3.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_url}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f4(final JspTag _jspx_th_fw_005fmsp_005f2, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f4 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f4_reused = false;
        try {
            _jspx_th_c_005fout_005f4.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f4.setParent((Tag)_jspx_th_fw_005fmsp_005f2);
            _jspx_th_c_005fout_005f4.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_name}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f5(final JspTag _jspx_th_fw_005fmsp_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f5 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f5_reused = false;
        try {
            _jspx_th_c_005fout_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f5.setParent((Tag)_jspx_th_fw_005fmsp_005f3);
            _jspx_th_c_005fout_005f5.setValue(PageContextImpl.proprietaryEvaluate("${loginPageCsrfPreventionSalt}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fset_005f0(final JspTag _jspx_th_fw_005fmsp_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final SetTag _jspx_th_c_005fset_005f0 = (SetTag)this._005fjspx_005ftagPool_005fc_005fset_0026_005fvar_005fvalue_005fnobody.get((Class)SetTag.class);
        boolean _jspx_th_c_005fset_005f0_reused = false;
        try {
            _jspx_th_c_005fset_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fset_005f0.setParent((Tag)_jspx_th_fw_005fmsp_005f3);
            _jspx_th_c_005fset_005f0.setVar("locale");
            _jspx_th_c_005fset_005f0.setValue(new JspValueExpression("/jsp/common/onlineDemoMDM.jsp(711,10) '${displayLocale}'", this._jsp_getExpressionFactory().createValueExpression(_jspx_page_context.getELContext(), "${displayLocale}", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
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
    
    private boolean _jspx_meth_c_005fforEach_005f1(final JspTag _jspx_th_fw_005fmsp_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        JspWriter out = _jspx_page_context.getOut();
        final ForEachTag _jspx_th_c_005fforEach_005f1 = (ForEachTag)this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.get((Class)ForEachTag.class);
        boolean _jspx_th_c_005fforEach_005f1_reused = false;
        try {
            _jspx_th_c_005fforEach_005f1.setPageContext(_jspx_page_context);
            _jspx_th_c_005fforEach_005f1.setParent((Tag)_jspx_th_fw_005fmsp_005f3);
            _jspx_th_c_005fforEach_005f1.setVar("val");
            _jspx_th_c_005fforEach_005f1.setItems(new JspValueExpression("/jsp/common/onlineDemoMDM.jsp(713,14) '${LOCALES}'", this._jsp_getExpressionFactory().createValueExpression(_jspx_page_context.getELContext(), "${LOCALES}", (Class)Object.class)).getValue(_jspx_page_context.getELContext()));
            _jspx_th_c_005fforEach_005f1.setVarStatus("status");
            final int[] _jspx_push_body_count_c_005fforEach_005f1 = { 0 };
            try {
                final int _jspx_eval_c_005fforEach_005f1 = _jspx_th_c_005fforEach_005f1.doStartTag();
                if (_jspx_eval_c_005fforEach_005f1 != 0) {
                    int evalDoAfterBody;
                    do {
                        out.write("\n                  <option value=\"");
                        out.write((String)PageContextImpl.proprietaryEvaluate("${val.key}", (Class)String.class, _jspx_page_context, (ProtectedFunctionMapper)null));
                        out.write(34);
                        out.write(32);
                        if (this._jspx_meth_c_005fif_005f2((JspTag)_jspx_th_c_005fforEach_005f1, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f1)) {
                            return true;
                        }
                        out.write(32);
                        out.write(62);
                        out.write(32);
                        if (this._jspx_meth_c_005fout_005f6((JspTag)_jspx_th_c_005fforEach_005f1, _jspx_page_context, _jspx_push_body_count_c_005fforEach_005f1)) {
                            return true;
                        }
                        out.write("</option>                                                 \t\n              ");
                        evalDoAfterBody = _jspx_th_c_005fforEach_005f1.doAfterBody();
                    } while (evalDoAfterBody == 2);
                }
                if (_jspx_th_c_005fforEach_005f1.doEndTag() == 5) {
                    return true;
                }
            }
            catch (final Throwable _jspx_exception) {
                while (_jspx_push_body_count_c_005fforEach_005f1[0]-- > 0) {
                    out = _jspx_page_context.popBody();
                }
                _jspx_th_c_005fforEach_005f1.doCatch(_jspx_exception);
            }
            finally {
                _jspx_th_c_005fforEach_005f1.doFinally();
            }
            this._005fjspx_005ftagPool_005fc_005fforEach_0026_005fvarStatus_005fvar_005fitems.reuse((Tag)_jspx_th_c_005fforEach_005f1);
            _jspx_th_c_005fforEach_005f1_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_c_005fforEach_005f1, this._jsp_getInstanceManager(), _jspx_th_c_005fforEach_005f1_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fif_005f2(final JspTag _jspx_th_c_005fforEach_005f1, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f1) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final IfTag _jspx_th_c_005fif_005f2 = (IfTag)this._005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get((Class)IfTag.class);
        boolean _jspx_th_c_005fif_005f2_reused = false;
        try {
            _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f2.setParent((Tag)_jspx_th_c_005fforEach_005f1);
            _jspx_th_c_005fif_005f2.setTest((boolean)PageContextImpl.proprietaryEvaluate("${val.key == locale}", (Class)Boolean.TYPE, _jspx_page_context, (ProtectedFunctionMapper)null));
            final int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
            if (_jspx_eval_c_005fif_005f2 != 0) {
                int evalDoAfterBody;
                do {
                    out.write(" selected ");
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
    
    private boolean _jspx_meth_c_005fout_005f6(final JspTag _jspx_th_c_005fforEach_005f1, final PageContext _jspx_page_context, final int[] _jspx_push_body_count_c_005fforEach_005f1) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f6 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f6_reused = false;
        try {
            _jspx_th_c_005fout_005f6.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f6.setParent((Tag)_jspx_th_c_005fforEach_005f1);
            _jspx_th_c_005fout_005f6.setValue(PageContextImpl.proprietaryEvaluate("${val.value}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_fw_005fmsp_005f4(final JspTag _jspx_th_fw_005fmsp_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCMSPTag _jspx_th_fw_005fmsp_005f4 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
        boolean _jspx_th_fw_005fmsp_005f4_reused = false;
        try {
            _jspx_th_fw_005fmsp_005f4.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fmsp_005f4.setParent((Tag)_jspx_th_fw_005fmsp_005f3);
            _jspx_th_fw_005fmsp_005f4.setIsMSP(Boolean.valueOf("false"));
            final int _jspx_eval_fw_005fmsp_005f4 = _jspx_th_fw_005fmsp_005f4.doStartTag();
            if (_jspx_eval_fw_005fmsp_005f4 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n        ");
                    if (this._jspx_meth_c_005fout_005f7((JspTag)_jspx_th_fw_005fmsp_005f4, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n      ");
                    evalDoAfterBody = _jspx_th_fw_005fmsp_005f4.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fmsp_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f4);
            _jspx_th_fw_005fmsp_005f4_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f4, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f4_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f7(final JspTag _jspx_th_fw_005fmsp_005f4, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f7 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f7_reused = false;
        try {
            _jspx_th_c_005fout_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f7.setParent((Tag)_jspx_th_fw_005fmsp_005f4);
            _jspx_th_c_005fout_005f7.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.copyright_year}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_fw_005fmsp_005f5(final JspTag _jspx_th_fw_005fmsp_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final DCMSPTag _jspx_th_fw_005fmsp_005f4 = (DCMSPTag)this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.get((Class)DCMSPTag.class);
        boolean _jspx_th_fw_005fmsp_005f5_reused = false;
        try {
            _jspx_th_fw_005fmsp_005f4.setPageContext(_jspx_page_context);
            _jspx_th_fw_005fmsp_005f4.setParent((Tag)_jspx_th_fw_005fmsp_005f3);
            _jspx_th_fw_005fmsp_005f4.setIsMSP(Boolean.valueOf("true"));
            final int _jspx_eval_fw_005fmsp_005f5 = _jspx_th_fw_005fmsp_005f4.doStartTag();
            if (_jspx_eval_fw_005fmsp_005f5 != 0) {
                int evalDoAfterBody;
                do {
                    out.write("\n        ");
                    if (this._jspx_meth_c_005fout_005f8((JspTag)_jspx_th_fw_005fmsp_005f4, _jspx_page_context)) {
                        return true;
                    }
                    out.write("\n      ");
                    evalDoAfterBody = _jspx_th_fw_005fmsp_005f4.doAfterBody();
                } while (evalDoAfterBody == 2);
            }
            if (_jspx_th_fw_005fmsp_005f4.doEndTag() == 5) {
                return true;
            }
            this._005fjspx_005ftagPool_005ffw_005fmsp_0026_005fisMSP.reuse((Tag)_jspx_th_fw_005fmsp_005f4);
            _jspx_th_fw_005fmsp_005f5_reused = true;
        }
        finally {
            JspRuntimeLibrary.releaseTag((Tag)_jspx_th_fw_005fmsp_005f4, this._jsp_getInstanceManager(), _jspx_th_fw_005fmsp_005f5_reused);
        }
        return false;
    }
    
    private boolean _jspx_meth_c_005fout_005f8(final JspTag _jspx_th_fw_005fmsp_005f5, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f8 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f8_reused = false;
        try {
            _jspx_th_c_005fout_005f8.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f8.setParent((Tag)_jspx_th_fw_005fmsp_005f5);
            _jspx_th_c_005fout_005f8.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.copyright_year}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f9(final JspTag _jspx_th_fw_005fmsp_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f9 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f9_reused = false;
        try {
            _jspx_th_c_005fout_005f9.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f9.setParent((Tag)_jspx_th_fw_005fmsp_005f3);
            _jspx_th_c_005fout_005f9.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_url}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    private boolean _jspx_meth_c_005fout_005f10(final JspTag _jspx_th_fw_005fmsp_005f3, final PageContext _jspx_page_context) throws Throwable {
        final PageContext pageContext = _jspx_page_context;
        final JspWriter out = _jspx_page_context.getOut();
        final OutTag _jspx_th_c_005fout_005f10 = (OutTag)this._005fjspx_005ftagPool_005fc_005fout_0026_005fvalue_005fnobody.get((Class)OutTag.class);
        boolean _jspx_th_c_005fout_005f10_reused = false;
        try {
            _jspx_th_c_005fout_005f10.setPageContext(_jspx_page_context);
            _jspx_th_c_005fout_005f10.setParent((Tag)_jspx_th_fw_005fmsp_005f3);
            _jspx_th_c_005fout_005f10.setValue(PageContextImpl.proprietaryEvaluate("${COPYRIGHT_PROPS.company_name}", (Class)Object.class, _jspx_page_context, (ProtectedFunctionMapper)null));
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
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (onlineDemoMDM_jsp._jspx_dependants = new HashMap<String, Long>(1)).put("/jsp/common/TagLibImport.jsp", 1663600462000L);
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        onlineDemoMDM_jsp._jspx_imports_packages.add("javax.servlet.http");
        onlineDemoMDM_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        onlineDemoMDM_jsp._jspx_imports_classes.add("com.me.mdm.server.common.MDMURLRedirection");
        onlineDemoMDM_jsp._jspx_imports_classes.add("java.util.Locale");
        onlineDemoMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.webclient.common.ProductUrlLoader");
        onlineDemoMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.DMIAMEncoder");
        onlineDemoMDM_jsp._jspx_imports_classes.add("com.adventnet.iam.xss.IAMEncoder");
        onlineDemoMDM_jsp._jspx_imports_classes.add("com.me.devicemanagement.framework.server.util.I18NUtil");
    }
}

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
import com.adventnet.iam.xss.IAMEncoder;
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

public final class dcHelpTip_jsp extends HttpJspBase implements JspSourceDependent, JspSourceImports
{
    private static final JspFactory _jspxFactory;
    private static Map<String, Long> _jspx_dependants;
    private static final Set<String> _jspx_imports_packages;
    private static final Set<String> _jspx_imports_classes;
    private volatile ExpressionFactory _el_expressionfactory;
    private volatile InstanceManager _jsp_instancemanager;
    
    public Map<String, Long> getDependants() {
        return dcHelpTip_jsp._jspx_dependants;
    }
    
    public Set<String> getPackageImports() {
        return dcHelpTip_jsp._jspx_imports_packages;
    }
    
    public Set<String> getClassImports() {
        return dcHelpTip_jsp._jspx_imports_classes;
    }
    
    public ExpressionFactory _jsp_getExpressionFactory() {
        if (this._el_expressionfactory == null) {
            synchronized (this) {
                if (this._el_expressionfactory == null) {
                    this._el_expressionfactory = dcHelpTip_jsp._jspxFactory.getJspApplicationContext(this.getServletConfig().getServletContext()).getExpressionFactory();
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
            final PageContext pageContext = _jspx_page_context = dcHelpTip_jsp._jspxFactory.getPageContext((Servlet)this, (ServletRequest)request, (ServletResponse)response, (String)null, true, 8192, true);
            final ServletContext application = pageContext.getServletContext();
            final ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = (_jspx_out = pageContext.getOut());
            out.write(10);
            out.write("\n\n\n<style>\n#__DIALOG_CONTENT0{\n-webkit-box-shadow: 1px 2px 10px rgba(80,81,61,.31);\n    -moz-box-shadow: 1px 2px 10px rgba(80,81,61,.31);\n    box-shadow: 1px 2px 10px rgba(80,81,61,.31);}\n</style>\n\n<div id=\"helpDiv\" style=\"position:relative;vertical-align: bottom\" class=\"\">\n  <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\">\n    <tbody><tr height=\"50\">\n      <td nowrap=\"\" class=\"bodybold\" width=\"98%\" height=\"10\"><div width=\"100%\" onmousedown=\"captureDialog(event,this)\" style=\"cursor: move;\">&nbsp;<span id=\"helpTitle\">");
            out.print(IAMEncoder.encodeHTML(I18N.getMsg(request.getParameter("helpTitle"), new Object[0])));
            out.write("</span></div></td>\n      <td align=\"right\"><a href=\"javascript:closeDialog()\"><img src=\"images/dialogClose.gif\" style=\"padding:5px;\"></a>&nbsp;</td>\n     \n    </tr>\n    <tr> \n        <td colspan=\"2\" rowspan=\"2\" id=\"helpContent\" class=\"bodytext\" style=\"padding:3px;\" valign=\"top\">");
            out.print(IAMEncoder.encodeHTML(I18N.getMsg(request.getParameter("helpContent"), new Object[0])));
            out.write("</td>\n      \n    </tr>\n \n \n  </tbody></table>\n</div>\n\n");
            out.write("\n<script>\n   /**\n    * Function to arrange the alignment of helpdiv based on the content length.\n    * Sets the top and left style attributes of the div (_DIALOG_LAYER0) that holds the help tip.\n    */ \n   function arrangeDiv(){\n        var helpContent = document.getElementById(\"helpContent\").innerHTML;\n        msgLeng = helpContent.length\n        var helpDiv = document.getElementById(\"_DIALOG_LAYER0\");\n        var srcElement = document.getElementById('");
            out.print(IAMEncoder.encodeJavaScript(request.getParameter("fromElement")));
            out.write("');\n        if(!srcElement){\n            /*\n             * fix for mainPage(<tiles:insert attribute=\"mainPage\"/>) is loaded in iframe \n             */\n            inIframe=true\n            srcElement=document.getElementById(\"iframeRef\").contentWindow.document.getElementById('");
            out.print(IAMEncoder.encodeJavaScript(request.getParameter("fromElement")));
            out.write("');\n         }\n   }\n /*\n\thelpDiv.style.verticalAlign = \"bottom\"; //No I18N\n\tposX = findPosX(srcElement);\n\tposY = findPosY(srcElement);\n        \n        \n             if(inIframe){\n              var iframe=   document.getElementById(\"iframeRef\")\n            posX  +=findPosX(iframe);\n            posY  +=findPosX(iframe);\n            }\n        \n        \n\tvar divWidth = 340;\n\tvar divHeight = 70;\n\tif(msgLeng > 800 )\n\t{\n\t\tdivHeight = 300;\n\n\t}else if(msgLeng > 500 )\n\t{\n\t\tdivHeight = 230;\n\n\t}else if(msgLeng > 150)\n\t{\n\t\tdivHeight = 140;\n\t}\n\t//changed JS\n        helpDiv.style.left = posX+25\n\thelpDiv.style.top = posY - divHeight - 5;\n       \n\tvar posTop = parseInt(helpDiv.style.top)\n\tif(posTop < 0)\n\t{\n\t\thelpDiv.style.top = 10;\n\t}\n\thelpDiv.style.height = divHeight;\n\thelpDiv.style.width = divWidth;\n   }\n    arrangeDiv();\n    //New*/\n\n</script>\n");
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
            dcHelpTip_jsp._jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
    
    static {
        _jspxFactory = JspFactory.getDefaultFactory();
        (_jspx_imports_packages = new HashSet<String>()).add("javax.servlet");
        dcHelpTip_jsp._jspx_imports_packages.add("javax.servlet.http");
        dcHelpTip_jsp._jspx_imports_packages.add("javax.servlet.jsp");
        (_jspx_imports_classes = new HashSet<String>()).add("com.adventnet.i18n.I18N");
        dcHelpTip_jsp._jspx_imports_classes.add("com.adventnet.iam.xss.IAMEncoder");
    }
}

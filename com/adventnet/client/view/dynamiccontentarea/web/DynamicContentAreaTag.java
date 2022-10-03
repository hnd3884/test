package com.adventnet.client.view.dynamiccontentarea.web;

import java.util.List;
import com.adventnet.client.themes.web.ThemesAPI;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.jsp.JspException;
import javax.servlet.ServletRequest;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.box.web.BoxAPI;
import com.adventnet.client.util.web.ViewRequest;
import com.adventnet.client.view.web.WebViewAPI;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.jsp.tagext.TagSupport;

public class DynamicContentAreaTag extends TagSupport implements WebConstants, JavaScriptConstants
{
    private String name;
    private boolean flush;
    private boolean generateBreadCrumb;
    private boolean dynamicTitle;
    private String popContentAreaJSFunc;
    private String boxType;
    
    public DynamicContentAreaTag() {
        this.dynamicTitle = false;
        this.popContentAreaJSFunc = "popContentArea";
        this.boxType = null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean isFlush() {
        return this.flush;
    }
    
    public void setFlush(final boolean flush) {
        this.flush = flush;
    }
    
    public boolean isGenerateBreadCrumb() {
        return this.generateBreadCrumb;
    }
    
    public void setGenerateBreadCrumb(final boolean newGenerateBreadCrumb) {
        this.generateBreadCrumb = newGenerateBreadCrumb;
    }
    
    public boolean isDynamicTitle() {
        return this.dynamicTitle;
    }
    
    public void setDynamicTitle(final boolean newDynamicTitle) {
        this.dynamicTitle = newDynamicTitle;
    }
    
    public void setShowInBox(final String showInBox) {
        this.boxType = showInBox;
    }
    
    public String getShowInBox() {
        return this.boxType;
    }
    
    public void setPopContentAreaJSFunc(final String funcName) {
        this.popContentAreaJSFunc = funcName;
    }
    
    public int doStartTag() throws JspException {
        try {
            final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
            final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, this.name);
            final ViewContext curCtx = model.getCurrentItem();
            String encodedName = null;
            if (curCtx != null) {
                if (this.dynamicTitle) {
                    request.setAttribute("WINDOWTITLE", (Object)curCtx.getTitle());
                }
                curCtx.setStateParameter("PDCA", model.getContentAreaName());
                final String params = (String)curCtx.getStateOrURLStateParameter("_D_RP");
                final String forwardUrl = WebViewAPI.getViewForwardURL(curCtx.getModel().getViewName(), curCtx.getUniqueId(), params);
                HttpServletRequest forwardReq = request;
                if (params != null) {
                    forwardReq = (HttpServletRequest)new ViewRequest(forwardReq, params);
                }
                final Object typeOfBox = curCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 11);
                if (typeOfBox != null) {
                    this.boxType = (String)typeOfBox;
                }
                if (this.boxType != null) {
                    BoxAPI.setBoxForView(curCtx, this.boxType, true);
                }
                if (this.name != null) {
                    encodedName = IAMEncoder.encodeHTMLAttribute(this.name);
                    this.pageContext.getOut().println("<div id=\"" + encodedName + "\" contentareaname=\"" + encodedName + "\" unique_id=\"" + IAMEncoder.encodeHTMLAttribute(curCtx.getUniqueId()) + "\" closefunc=\"closeSrcView\">");
                }
                else {
                    final String caname = IAMEncoder.encodeHTMLAttribute(DynamicContentAreaUtils.getDynamicContentAreaName(request, null));
                    this.pageContext.getOut().println("<div id=\"" + caname + "\" contentareaname=\"" + caname + "\" unique_id=\"" + IAMEncoder.encodeHTMLAttribute(curCtx.getUniqueId()) + "\" closefunc=\"closeSrcView\">");
                }
                JspRuntimeLibrary.include((ServletRequest)forwardReq, this.pageContext.getResponse(), forwardUrl, this.pageContext.getOut(), this.flush);
                this.pageContext.getOut().println("</div>");
                this.pageContext.getOut().println(DynamicContentAreaAPI.generateOldStateForViews(model, request));
                this.generateBreadCrumb(model);
                this.pageContext.getOut().println(DynamicContentAreaAPI.genContentAreaStateScript(request, model));
            }
            else {
                if (this.name != null) {
                    encodedName = IAMEncoder.encodeHTMLAttribute(this.name);
                    this.pageContext.getOut().println("<div id=\"" + encodedName + "\" contentareaname=\"" + encodedName + "\" closefunc=\"closeSrcView\">");
                }
                else {
                    final String caname2 = IAMEncoder.encodeHTMLAttribute(DynamicContentAreaUtils.getDynamicContentAreaName(request, null));
                    this.pageContext.getOut().println("<div id=\"" + caname2 + "\" name=\"" + caname2 + "\" closefunc=\"closeSrcView\">");
                }
                this.pageContext.getOut().println("</div>");
            }
            return 0;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new JspException((Throwable)ex);
        }
    }
    
    protected void generateBreadCrumb(final DynamicContentAreaModel model) throws Exception {
        final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        if (this.generateBreadCrumb) {
            final List caiList = model.getContentList();
            if (caiList.size() < 2) {
                return;
            }
            final StringBuffer div = new StringBuffer("<div id='");
            div.append(IAMEncoder.encodeHTMLAttribute(model.getContentAreaName())).append("_BC' ").append("style='display:none'>");
            for (int i = 0, j = caiList.size() - 1; i < j; ++i) {
                final ViewContext ctx = caiList.get(i);
                final String title = ctx.getTitle();
                div.append("<a href='javascript:").append(IAMEncoder.encodeJavaScript(this.popContentAreaJSFunc)).append('(').append(i).append(",\"").append(IAMEncoder.encodeJavaScript(model.getContentAreaName())).append("\")'>");
                if (System.getProperty("breadcrumb") != null && System.getProperty("breadcrumb").equals("fromcss")) {
                    div.append(IAMEncoder.encodeHTML(title)).append("</a><span class='breadCrumbcss'>&nbsp;</span>");
                }
                else {
                    div.append(IAMEncoder.encodeHTML(title)).append("</a><img src='").append(IAMEncoder.encodeHTMLAttribute(ThemesAPI.getThemeDirForRequest(request))).append("/images/arrow_right.gif'/>");
                }
            }
            final ViewContext ctx2 = caiList.get(caiList.size() - 1);
            final String title2 = ctx2.getTitle();
            div.append(IAMEncoder.encodeHTML(title2));
            div.append("</div>");
            div.append("<script>").append("createBreadCrumbs(\"" + IAMEncoder.encodeJavaScript(model.getContentAreaName()) + "\",window);").append("</script>");
            this.pageContext.getOut().println(div.toString());
        }
    }
}

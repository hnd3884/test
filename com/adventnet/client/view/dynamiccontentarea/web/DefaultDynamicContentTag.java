package com.adventnet.client.view.dynamiccontentarea.web;

import com.adventnet.client.view.web.ViewContext;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.jsp.tagext.TagSupport;

public class DefaultDynamicContentTag extends TagSupport implements WebConstants
{
    private String contentAreaName;
    private String viewName;
    private String viewParams;
    private String viewUniqueId;
    private String viewTitle;
    private String tileName;
    
    public String getContentAreaName() {
        return this.contentAreaName;
    }
    
    public void setContentAreaName(final String contentAreaName) {
        this.contentAreaName = contentAreaName;
    }
    
    public String getViewName() {
        return this.viewName;
    }
    
    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }
    
    public String getViewParams() {
        return this.viewParams;
    }
    
    public void setViewParams(final String viewParams) {
        this.viewParams = viewParams;
    }
    
    public String getTileName() {
        return this.tileName;
    }
    
    public void setTileName(final String newTileName) {
        this.tileName = newTileName;
    }
    
    public String getViewUniqueId() {
        return this.viewUniqueId;
    }
    
    public void setViewUniqueId(final String viewUniqueId) {
        this.viewUniqueId = viewUniqueId;
    }
    
    public String getViewTitle() {
        return this.viewTitle;
    }
    
    public void setViewTitle(final String viewTitle) {
        this.viewTitle = viewTitle;
    }
    
    public int doStartTag() throws JspException {
        try {
            final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
            if (this.viewName == null && this.tileName != null) {
                this.viewName = (String)request.getAttribute("TILE:" + this.tileName);
            }
            if (this.viewName == null) {
                return 0;
            }
            final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, this.contentAreaName);
            final ViewContext viewCtx = model.getCurrentItem();
            if (viewCtx != null) {
                return 0;
            }
            if (this.viewTitle == null) {
                this.viewTitle = this.viewName;
            }
            DynamicContentAreaAPI.updateDynamicContentArea(request, this.viewName, this.viewUniqueId, model.getContentAreaName(), this.viewParams, false, true);
            return 0;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new JspException((Throwable)ex);
        }
    }
}

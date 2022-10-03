package com.adventnet.client.view.web;

import javax.servlet.jsp.JspException;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.client.box.web.BoxAPI;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.jsp.tagext.TagSupport;

public class ShowViewTag extends TagSupport implements WebConstants
{
    protected String viewName;
    protected String viewUniqueId;
    protected String viewParams;
    protected boolean flush;
    protected String boxConfig;
    protected boolean boxDefaultOpen;
    private String stateParams;
    private static final Logger LOG;
    
    public ShowViewTag() {
        this.boxDefaultOpen = true;
    }
    
    public String getBoxConfig() {
        return this.boxConfig;
    }
    
    public void setBoxConfig(final String newBoxConfig) {
        this.boxConfig = newBoxConfig;
    }
    
    public String getViewName() {
        return this.viewName;
    }
    
    public void setViewName(final String newViewName) {
        this.viewName = newViewName;
    }
    
    public String getViewUniqueId() {
        return this.viewUniqueId;
    }
    
    public void setViewUniqueId(final String newViewUniqueId) {
        this.viewUniqueId = newViewUniqueId;
    }
    
    public String getViewParams() {
        return this.viewParams;
    }
    
    public void setViewParams(final String newViewParams) {
        this.viewParams = newViewParams;
    }
    
    public boolean isFlush() {
        return this.flush;
    }
    
    public void setFlush(final boolean newFlush) {
        this.flush = newFlush;
    }
    
    public boolean isBoxDefaultOpen() {
        return this.boxDefaultOpen;
    }
    
    public void setBoxDefaultOpen(final boolean newBoxDefaultOpen) {
        this.boxDefaultOpen = newBoxDefaultOpen;
    }
    
    public String getStateParams() {
        return this.stateParams;
    }
    
    public void setStateParams(final String param) {
        this.stateParams = param;
    }
    
    public int doStartTag() throws JspException {
        final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        try {
            final String vName = this.getViewName();
            if (vName == null) {
                return 0;
            }
            final Long vNameNo = WebViewAPI.getViewNameNo(vName);
            String uniqueId = this.getViewUniqueId();
            if (uniqueId == null) {
                uniqueId = vName;
            }
            try {
                final ViewContext vc = ViewContext.getViewContext(uniqueId, vNameNo, request);
                if (this.boxConfig != null) {
                    BoxAPI.setBoxForView(vc, this.boxConfig, this.boxDefaultOpen);
                }
                if (this.stateParams != null) {
                    this.updateState(this.stateParams, vc);
                }
                final String url = WebViewAPI.getViewForwardURL(vName, uniqueId, this.viewParams);
                this.pageContext.include(url, this.flush);
                return 0;
            }
            catch (final AuthorizationException ae) {
                ShowViewTag.LOG.finer("User is not allowed to view " + uniqueId);
                return 0;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new JspException((Throwable)ex);
        }
    }
    
    protected void updateState(final String stateParams, final ViewContext vc) throws Exception {
        final String[] stateArray = stateParams.split(",");
        for (int i = 0; i < stateArray.length; ++i) {
            final String state = stateArray[i];
            final String[] stateAndValue = state.split("\\=");
            vc.setStateOrURLStateParam(stateAndValue[0], stateAndValue[1]);
        }
    }
    
    static {
        LOG = Logger.getLogger(ShowViewTag.class.getName());
    }
}

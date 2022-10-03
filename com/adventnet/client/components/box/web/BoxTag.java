package com.adventnet.client.components.box.web;

import javax.servlet.jsp.JspTagException;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;

public class BoxTag extends com.adventnet.client.box.web.BoxTag implements WebConstants, JavaScriptConstants
{
    String displayName;
    String initialState;
    
    public BoxTag() {
        this.displayName = null;
        this.initialState = null;
    }
    
    public void setInitialState(final String initialState) {
        this.initialState = initialState;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public int doStartTag() throws JspTagException {
        super.setBoxType("PrimaryBox");
        super.setTitle(this.displayName);
        return super.doStartTag();
    }
}

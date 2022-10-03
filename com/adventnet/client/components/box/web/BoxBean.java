package com.adventnet.client.components.box.web;

import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;

public class BoxBean implements WebConstants
{
    public static String MINIMIZED;
    public static String MAXIMIZED;
    ViewContext viewContext;
    String boxId;
    String initialState;
    String displayName;
    String idPrefix;
    String action;
    boolean currentlyMaximized;
    
    public void init() {
        final String referenceId = this.viewContext.getReferenceId();
        String curState;
        final String modifiedState = curState = (String)this.viewContext.getStateParameter(this.boxId);
        if (curState == null) {
            curState = this.initialState;
        }
        if (curState == null) {
            curState = BoxBean.MAXIMIZED;
        }
        this.currentlyMaximized = curState.equals(BoxBean.MAXIMIZED);
        this.action = "changeBoxState('" + referenceId + "','" + this.boxId + "')";
        this.idPrefix = this.boxId + "_";
        if (modifiedState != null && !modifiedState.equals(this.initialState)) {
            this.viewContext.setStateParameter(this.boxId, (Object)modifiedState);
        }
    }
    
    public String getBoxPrefix() {
        final StringBuffer boxPrefix = new StringBuffer();
        boxPrefix.append("<table class='boxLayout' id=\"").append(this.boxId).append("\" cellspacing=0 cellpadding=0>");
        boxPrefix.append("<tr>");
        boxPrefix.append("<td class=\"boxTL\">&nbsp;</td>");
        boxPrefix.append("<td class=\"boxHeader\">");
        boxPrefix.append("<table width='100%' border=0 cellspacing=0 cellpadding=0>");
        boxPrefix.append("<tr><td nowrap class='boxHeaderText'>");
        boxPrefix.append(this.displayName);
        boxPrefix.append("</td>");
        boxPrefix.append("<td class='ctrlButtonPane'>");
        boxPrefix.append("<input type='button' class='");
        boxPrefix.append(this.getMinimizeBtnClass());
        boxPrefix.append("' id = '");
        boxPrefix.append(this.getMinimizeImgId());
        boxPrefix.append("' onClick = \"");
        boxPrefix.append(this.getActionString());
        boxPrefix.append("\" />");
        boxPrefix.append("<input type='button' class='");
        boxPrefix.append(this.getMaximizeBtnClass());
        boxPrefix.append("' id = '");
        boxPrefix.append(this.getMaximizeImgId());
        boxPrefix.append("' onClick = \"");
        boxPrefix.append(this.getActionString());
        boxPrefix.append("\"/>");
        boxPrefix.append("</tr></table></td>");
        boxPrefix.append("<td class='boxTR'>&nbsp;</td>");
        boxPrefix.append("</tr>");
        boxPrefix.append("<tr><td class='boxLB'>&nbsp;</td>");
        boxPrefix.append("<td id='" + this.getBoxMaxContentId() + "' class='" + this.getBoxMaxContentClass() + "'>");
        return boxPrefix.toString();
    }
    
    public String getBoxSuffix() {
        final StringBuffer boxSuffix = new StringBuffer();
        boxSuffix.append("</td><td class='boxRB' colspan='2'>&nbsp;</td>");
        boxSuffix.append("</tr>");
        boxSuffix.append("<tr><td class='boxBL'>&nbsp;</td>");
        boxSuffix.append("<td class='boxBC'>&nbsp;</td>");
        boxSuffix.append("<td class='boxBR'>&nbsp;</td></tr></table>");
        return boxSuffix.toString();
    }
    
    private String getMaximizeImgId() {
        return this.idPrefix + "maxImg";
    }
    
    private String getMinimizeImgId() {
        return this.idPrefix + "minImg";
    }
    
    private String getMinimizeBtnClass() {
        return this.currentlyMaximized ? "minimizeButton" : "box_hide";
    }
    
    private String getMaximizeBtnClass() {
        return this.currentlyMaximized ? "box_hide" : "maximizeButton";
    }
    
    private String getBoxMaxContentClass() {
        return this.currentlyMaximized ? "boxContent" : "box_hide";
    }
    
    private String getActionString() {
        return this.action;
    }
    
    private String getBoxMaxContentId() {
        return this.idPrefix + "maxContent";
    }
    
    private String getBoxMinContentId() {
        return this.idPrefix + "minContent";
    }
    
    static {
        BoxBean.MINIMIZED = "0";
        BoxBean.MAXIMIZED = "1";
    }
}

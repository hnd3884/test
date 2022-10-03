package com.adventnet.client.box.web;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.view.web.ViewContext;

public class DefaultBoxCreator implements BoxCreator
{
    protected String boxId;
    protected String title;
    protected String action;
    protected boolean isMaximized;
    protected String boxConfig;
    protected String toolBarSnippet;
    protected ViewContext viewCtx;
    
    public DefaultBoxCreator() {
        this.boxId = null;
        this.title = null;
        this.action = null;
        this.isMaximized = true;
        this.boxConfig = null;
        this.toolBarSnippet = "";
        this.viewCtx = null;
    }
    
    @Override
    public void initBox(final String boxConfig, final String boxId, final boolean isOpen) {
        try {
            this.boxId = boxId;
            this.boxConfig = boxConfig;
            this.isMaximized = isOpen;
            this.action = "changeBoxState('" + IAMEncoder.encodeJavaScript(boxId) + "')";
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }
    
    @Override
    public void setToolBarSnippet(final String toolBarSnippet) {
        this.toolBarSnippet = toolBarSnippet;
    }
    
    @Override
    public void setViewContext(final ViewContext vc) {
        this.viewCtx = vc;
    }
    
    @Override
    public String getHtmlForBoxPrefix() {
        final StringBuffer boxPrefix = new StringBuffer();
        boxPrefix.append("<TABLE cellspacing='0' class='").append((this.boxConfig != null) ? IAMEncoder.encodeHTMLAttribute(this.boxConfig) : this.boxConfig).append("' id='").append(IAMEncoder.encodeHTMLAttribute(this.boxId)).append("'>");
        boxPrefix.append("<TR>");
        boxPrefix.append("<TD class='boxHeader'>");
        try {
            final Criteria criteria = new Criteria(new Column("ACClientProps", "PARAMNAME"), (Object)"BOX_HEADER_LINK", 0);
            final DataObject dataObject = DataAccess.get("ACClientProps", criteria);
            final Row row = dataObject.getRow("ACClientProps");
            if (!dataObject.isEmpty() && row != null && row.get("PARAMVALUE").toString().equals("false")) {
                boxPrefix.append(IAMEncoder.encodeHTML(this.title));
            }
            else {
                boxPrefix.append("<div class='boxTitleDiv' onClick=\"").append(this.getActionString()).append("\">").append(IAMEncoder.encodeHTML(this.title)).append("</div></TD>");
            }
            boxPrefix.append("</TD><TD class='boxCtrlButtonPane' nowrap> &nbsp;");
            boxPrefix.append(IAMEncoder.encodeHTML(this.toolBarSnippet));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        boxPrefix.append("<input type='button' class='").append(this.getMinimizeBtnClass()).append("' id = '").append(this.getMinimizeImgId()).append("' onClick = \"").append(this.getActionString()).append("\" />");
        boxPrefix.append("<input type='button' class='").append(this.getMaximizeBtnClass()).append("' id = '").append(this.getMaximizeImgId()).append("' onClick = \"").append(this.getActionString()).append("\"/>");
        boxPrefix.append("</TD></TR>");
        boxPrefix.append("<TR>");
        boxPrefix.append("<TD colspan='3' id='").append(IAMEncoder.encodeHTMLAttribute(this.getBoxMaxContentId())).append("' class='").append(this.getBoxMaxContentClass()).append("'>");
        return boxPrefix.toString();
    }
    
    @Override
    public String getHtmlForBoxSuffix() {
        final StringBuffer boxSuffix = new StringBuffer();
        boxSuffix.append("</TD></TR></TABLE>");
        return boxSuffix.toString();
    }
    
    public String getMaximizeImgId() {
        return IAMEncoder.encodeHTMLAttribute(this.boxId) + "_MaI";
    }
    
    public String getMinimizeImgId() {
        return IAMEncoder.encodeHTMLAttribute(this.boxId) + "_MiI";
    }
    
    public String getMinimizeBtnClass() {
        return this.isMaximized ? "minButton" : "hide";
    }
    
    public String getMaximizeBtnClass() {
        return this.isMaximized ? "hide" : "maxButton";
    }
    
    public String getBoxMaxContentClass() {
        return this.isMaximized ? "boxContent" : "hideBoxContent";
    }
    
    public String getActionString() {
        return this.action;
    }
    
    public String getBoxMaxContentId() {
        return this.boxId + "_C";
    }
    
    @Override
    public String getBoxConfigName() {
        return this.boxConfig;
    }
}

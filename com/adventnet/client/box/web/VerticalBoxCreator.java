package com.adventnet.client.box.web;

import com.adventnet.iam.xss.IAMEncoder;

public class VerticalBoxCreator extends DefaultBoxCreator
{
    @Override
    public String getHtmlForBoxPrefix() {
        final StringBuffer boxPrefix = new StringBuffer();
        boxPrefix.append("<TABLE cellspacing='0' class='").append(IAMEncoder.encodeHTMLAttribute(this.boxConfig)).append("' id='").append(IAMEncoder.encodeHTMLAttribute(this.boxId)).append("'>");
        boxPrefix.append("<TR><TD class='boxHeader'><br>");
        for (int i = 0; i < this.title.length(); ++i) {
            boxPrefix.append(this.title.charAt(i) + "<br>");
        }
        boxPrefix.append("<br></TD>");
        boxPrefix.append("<TD id='").append(IAMEncoder.encodeHTMLAttribute(this.getBoxMaxContentId())).append("' class='").append(this.getBoxMaxContentClass()).append("'>");
        return boxPrefix.toString();
    }
    
    @Override
    public String getHtmlForBoxSuffix() {
        final StringBuffer boxSuffix = new StringBuffer();
        boxSuffix.append("</TD><TD class='boxCtrlButtonPane'>");
        boxSuffix.append("<input type='button' class='").append(this.getMinimizeBtnClass()).append("' id = '").append(this.getMinimizeImgId()).append("' onClick = \"").append(this.getActionString()).append("\" />");
        boxSuffix.append("<input type='button' class='").append(this.getMaximizeBtnClass()).append("' id = '").append(this.getMaximizeImgId()).append("' onClick = \"").append(this.getActionString()).append("\"/>");
        boxSuffix.append("</TD></TR></TABLE>");
        return boxSuffix.toString();
    }
}
